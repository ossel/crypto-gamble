package com.ossel.gamble.bitcoin.listeners;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.core.listeners.NewBestBlockListener;
import org.bitcoinj.store.BlockStoreException;
import com.ossel.gamble.bitcoin.services.AbstractBitcoinService;
import com.ossel.gamble.bitcoin.threads.PayoutThread;
import com.ossel.gamble.core.data.Block;
import com.ossel.gamble.core.data.ExtendedBlock;
import com.ossel.gamble.core.data.Participant;
import com.ossel.gamble.core.data.Pot;
import com.ossel.gamble.core.utils.CoreUtil;
import com.ossel.gamble.core.utils.ReceiveTimeComparator;

public class NewBlockListener implements NewBestBlockListener {

    private static final Logger log = Logger.getLogger(NewBlockListener.class);

    AbstractBitcoinService service;

    public NewBlockListener(AbstractBitcoinService abstractBitcoinService) {
        this.service = abstractBitcoinService;
    }

    @Override
    public void notifyNewBestBlock(StoredBlock block) throws VerificationException {
        log.info("New Block height = " + block.getHeight() + " hash = "
                + block.getHeader().getHash().toString());
        List<Pot> unfinishedPots = getUnfinishedPots(service.getClosedPots());
        for (Pot pot : unfinishedPots) {
            long potId = pot.getCreateTime().getTime();
            if (pot.getPayoutBlockHeight() > block.getHeight()) {
                log.info("Pot[" + potId + "] can not be finished yet. Waiting for "
                        + (pot.getPayoutBlockHeight() - block.getHeight()) + " more blocks.");
            } else if (block.getHeight() == pot.getPayoutBlockHeight()) {
                log.info("Pot[" + potId + "] select temorary Winner.");
                Block tmpPayoutBlock = new ExtendedBlock(block.getHeader().getHash().toString());
                pot.setPayoutBlock(tmpPayoutBlock);
                selectWinner(pot, tmpPayoutBlock);
            } else {
                log.info("Pot[" + potId + "] select final winner.");
                try {
                    StoredBlock correctBlock = getPastBlock(pot.getPayoutBlockHeight(), block);
                    Block finalPayoutBlock =
                            new ExtendedBlock(correctBlock.getHeader().getHash().toString());
                    pot.setPayoutBlock(finalPayoutBlock);
                    Participant winner = selectWinner(pot, finalPayoutBlock);
                    log.info(winner.getDepositAddress() + " wins pot[" + potId + "].");
                    if (!pot.isPayoutStarted())
                        new PayoutThread(service, pot).start();
                } catch (BlockStoreException e) {
                    log.info("Couldn't select final winner of Pot[" + potId + "]:" + e.getMessage(),
                            e);
                }
            }
            pot.setState(CoreUtil.getPotState(pot));
        }
    }

    public static Participant selectWinner(Pot pot, Block finalPayoutBlock) {
        List<Participant> participants = pot.getParticipants();
        participants.sort(new ReceiveTimeComparator());
        pot.setWinner(finalPayoutBlock.getWinner() % participants.size());
        pot.setWinner(participants.get(pot.getWinnerIndex()));
        return pot.getWinner();
    }

    public List<Pot> getUnfinishedPots(List<Pot> closedPots) {
        List<Pot> unfinishedPots = new ArrayList<Pot>();
        for (Pot pot : closedPots) {
            if (!pot.payoutFinished()) {
                unfinishedPots.add(pot);
            }
        }
        return unfinishedPots;
    }

    /**
     * goes back in the chain to find a specific block.
     * 
     * @param height the block height of the
     * @param newBlock the of the longest block header chain
     * @return the block with the correct block height
     */
    public StoredBlock getPastBlock(int height, StoredBlock newBlock) throws BlockStoreException {
        StoredBlock prev = newBlock;
        while (prev != null && prev.getHeight() != height) {
            prev = prev.getPrev(service.getAppKit().store());
        }
        return prev;
    }


}
