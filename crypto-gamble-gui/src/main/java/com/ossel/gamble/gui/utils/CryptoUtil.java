package com.ossel.gamble.gui.utils;

import org.apache.log4j.Logger;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import com.ossel.gamble.core.data.Participant;
import com.ossel.gamble.core.data.Pot;

public class CryptoUtil {

    private static final Logger log = Logger.getLogger(CryptoUtil.class);


    public static PieDataset getPieDataset(Pot pot) {
        // "aa", "39", "bb", "12", "cc", "12", "dd", "4"
        DefaultKeyedValues values = new DefaultKeyedValues();
        int i = 0;
        for (Participant participant : pot.getParticipants()) {
            values.addValue(" " + i + " ", pot.getExpectedBettingamount());
            // log.debug("Adding " + "(" + i + ") " + participant.getDisplayName() + " to pie
            // chart");
            i++;
        }

        int nbrOfOpenSlots = pot.getExpectedParticipants() - pot.getNbrOfParticipants();
        // log.debug("adding " + nbrOfOpenSlots + " open slot to the pie chart");
        String label = Pot.LABEL_OPEN_SLOT;
        for (int j = 1; j <= nbrOfOpenSlots; j++) {
            values.addValue(label + j, pot.getExpectedBettingamount());
        }

        return new DefaultPieDataset(values);
    }

}
