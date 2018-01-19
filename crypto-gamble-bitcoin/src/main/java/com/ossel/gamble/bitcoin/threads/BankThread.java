package com.ossel.gamble.bitcoin.threads;

import java.util.Calendar;
import com.ossel.gamble.bitcoin.services.BitcoinService;

public class BankThread extends Thread {

    BitcoinService service;

    public BankThread(BitcoinService abstractBitcoinService) {
        this.service = abstractBitcoinService;
    }

    @Override
    public void run() {
        while (true) {
            try {
                sleep(1000); // TODO: we can calculate exactly when the thread
                             // needs to weak up
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, -150); // every 2.5 minutes
            if (service.getLastParticipantJoinTime().before(cal.getTime())) {
                // no one joined for 2.5 minutes => add
                // bank participant
                service.addBankParticipant();
            }

            // if (dashService.getCurrentPot().isEmpty()) {
            // // this thread can wait for a little longer
            // try {
            // sleep(10000);
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }
            // }

        }
    }
}
