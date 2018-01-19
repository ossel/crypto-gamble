package com.ossel.gamble.core.utils;

import java.io.File;
import java.util.Date;
import java.util.List;
import com.ossel.gamble.core.data.Block;
import com.ossel.gamble.core.data.Participant;
import com.ossel.gamble.core.data.Pot;
import com.ossel.gamble.core.service.CryptoNetworkService;

public class CoreUtil {


    public static File getWalletDirectory() {
        String JBOSS_HOME = System.getProperty("jboss.home.dir");
        return new File(JBOSS_HOME + File.separator + CryptoNetworkService.WALLET_DIRECTORY_NAME);
    }

    public static String getPotState(Pot pot) {
        if (pot.getPayoutTxnId() != null && pot.getPayoutError() == null
                && pot.getWinner() != null) {
            return "Pot closed. Winner is " + pot.getWinner().getDisplayName() + ".";
        }
        if (pot.getPayoutError() != null) {
            return "Pot closed. Winner is " + pot.getWinner().getDisplayName() + ". "
                    + pot.getPayoutError().getMessage();
        }

        if (pot.getWinner() != null && pot.getWinner().isBankParticipant()) {
            return "Pot closed. Winner is the bank: " + pot.getWinner().getPseudonym();
        }

        if (pot.getFinalPayoutBlock() != null && pot.getWinner() != null) {
            return "Pot closed. Winner is " + pot.getWinner().getDisplayName()
                    + ". Triggering payout!";
        }

        if (pot.getClosingTime() != null) {
            return "Pot closed. Waiting for block " + pot.getPayoutBlockHeight()
                    + " to select the winner.";
        }
        return "Pot open. Waiting for " + pot.getNbrOfOpenSlots() + " more participant"
                + (pot.getNbrOfOpenSlots() == 1 ? "." : "s.");
    }

    /**
     * closes pot and sets payoutBlockHeight = closingBlockHeight + 1
     * 
     * @param pot
     * @param closingTime
     * @param closingBlockHash
     * @param closingBlockHeight
     */
    public static void closePot(Pot pot, Date closingTime, String closingBlockHash,
            int closingBlockHeight) {
        pot.setclosingTime(closingTime);
        pot.setClosingBlockHash(closingBlockHash);
        pot.setClosingBlockHeight(closingBlockHeight);
        pot.setPayoutBlockHeight(pot.getClosingBlockHeight() + 1);
    }


    public static Participant selectWinner(Pot pot, Block finalPayoutBlock) {
        List<Participant> actualParticipants = pot.getParticipants();
        actualParticipants.sort(new ReceiveTimeComparator());
        pot.setWinnerIndex(finalPayoutBlock.getWinner() % actualParticipants.size());
        pot.setWinner(actualParticipants.get(pot.getWinnerIndex()));
        return pot.getWinner();
    }

}
