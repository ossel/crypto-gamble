package com.ossel.gamble.gui.utils;

import java.util.Comparator;
import com.ossel.gamble.core.data.Participant;

public class ReceiveTimeComparator implements Comparator<Participant> {
    @Override
    public int compare(Participant a, Participant b) {
        if ((a.getReceiveTime().getTime() < b.getReceiveTime().getTime())) {
            return -1;
        }
        return 1;
    }
}
