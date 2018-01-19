package com.ossel.gamble.dash.listeners;

import java.util.List;
import org.apache.log4j.Logger;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.core.listeners.NewBestBlockListener;
import org.bitcoinj.store.BlockStoreException;
import com.google.common.util.concurrent.Service.State;
import com.ossel.gamble.core.data.ExtendedBlock;
import com.ossel.gamble.core.data.Participant;
import com.ossel.gamble.core.data.Pot;
import com.ossel.gamble.dash.services.DashService;

public class NewBlockListener extends AbstractListener implements NewBestBlockListener {
    private static final Logger log = Logger.getLogger(NewBlockListener.class);

    DashService dashService;

    public NewBlockListener(DashService dashService2) {
        this.dashService = dashService2;
    }

    public boolean ready() {
        return dashService.getAppKit().state().equals(State.RUNNING);
    }

    @Override
    public void notifyNewBestBlock(StoredBlock block) throws VerificationException {
        listenerTriggered();
        log.info("notifyNewBestBlock getHeight()=" + block.getHeight() + " hash="
                + block.getHeader().getHash().toString());

        if (!ready()) {
            log.info("appkit not ready yet!");
            return;
        }

        List<Pot> pots = dashService.getClosedUnfinishedPots();
        if (pots.size() == 0) {
            log.info("Apparently no pot needs to be handled.");
        }
        for (Pot pot : pots) {
            if (pot.getPayoutBlockHeight() > block.getHeight()) {
                log.info("Pot " + pot.getCreateTime().getTime()
                        + " can not be finished yet. Waiting for "
                        + (pot.getPayoutBlockHeight() - block.getHeight()) + " more blocks");
            } else {
                if (block.getHeight() == pot.getPayoutBlockHeight()) {
                    log.info("Pot " + pot.getCreateTime().getTime() + " select tmp Winner.");
                    pot.setTmpPayoutBlock(new ExtendedBlock(block.getHeader().getHash().toString()));
                    pot.selectWinner();
                } else {
                    log.info("Pot " + pot.getCreateTime().getTime() + " select final Winner.");
                    log.info("back in time to payout block " + pot.getPayoutBlockHeight());
                    StoredBlock prev = block;
                    while (prev.getHeight() != pot.getPayoutBlockHeight()) {
                        try {
                            prev = prev.getPrev(dashService.getAppKit().store());
                            if (prev == null) {
                                log.warn("Couldn't go back in time previous block null.");
                                break;
                            }
                            log.info("back in time " + prev.getHeight());
                        } catch (BlockStoreException e) {
                            log.error(e.getMessage(), e);
                            prev = null;
                            break;
                        }
                    }
                    if (prev == null) {
                        log.error("Something went wrong going back in time.");
                    } else {
                        log.info("Found correct block to select winner:"
                                + (prev.getHeight() == pot.getPayoutBlockHeight()));
                        if (prev.getHeight() == pot.getPayoutBlockHeight()) {
                            pot.setFinalPayoutBlock(
                                    new ExtendedBlock(prev.getHeader().getHash().toString()));
                            Participant winner = pot.selectWinner();
                            log.info(winner.getDisplayName()
                                    + " has been selected as a winner of pot " + pot.getId()
                                    + " by the blockchain.");
                            dashService.scheduledPayout(pot);
                        }

                    }
                    log.info("found correct closing block");
                }

            }

            dashService.startPayoutBatch();
        }

    }

}
