package com.ossel.gamble.core.utils;

import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.ossel.gamble.core.data.Participant;
import com.ossel.gamble.core.data.Pot;
import com.ossel.gamble.core.data.enums.CryptoCurrency;
import com.ossel.gamble.core.data.enums.CryptoNetwork;

public class TestUtil {

    private static final Logger log = Logger.getLogger(CryptoCurrency.class);

    private static String getSampleAddress(CryptoNetwork network) {
        switch (network) {
            case BTC_MAIN:
                return "1NiNja1bUmhSoTXozBRBEtR8LeF9TGbZBN";
            case BTC_TEST:
                return "mwcfydwiFZjtKsEonPkZwGz79YSChgMd8R";
            case DASH_MAIN:
                return "XepYSiYuDmUthxfF8uBRckL1qCiphx798N";
            case ETH_MAIN:
            case ETH_RINKEBY:
            case ETH_ROPSTEN:
                return "0x1b26b0E2a7BbB0F2AAC5e6BbA8c94972F601655b";
            default:
                return "1TODOTODOTODOTODOTODOTODO";
        }
    }

    private static String getSampleBlockhash(CryptoNetwork network) {
        switch (network) {
            case BTC_MAIN:
                return "0000000000000000000ab3f94c6287e21862a5065d3fca04101fe37c9ae466c9";
            case BTC_TEST:
                return "0000000013ccbbf02a69afee5e13eaf72c41cffd6ed9c39de7e080a255089a8a";
            case DASH_MAIN:
                return "00000000000000347413b4d3050772f66dd7c54c121d0d7125f99e0df731592b";
            case ETH_MAIN:
            case ETH_RINKEBY:
            case ETH_ROPSTEN:
                return "0x77eaea8c088c631c2331a74b59d20c43757af861a4fb1925687423f5c7841150";
            default:
                return "0000000000000000000000000000000000000TODO";
        }
    }

    public static void addTestData1(final CryptoNetwork network,
            final long EXPECTED_BETTING_AMOUNT, final List<Participant> possibleParticipants,
            final Pot currentPot, final List<Pot> closedPots) {
        String address = getSampleAddress(network);
        Participant p = new Participant(address.substring(0, address.length() - 7) + "D3pos1t",
                address.substring(0, address.length() - 6) + "P4yout");
        p.setPseudonym(network.getCryptoCurrency().getShortName() + "TestUser");
        p.setReceivedAmount(EXPECTED_BETTING_AMOUNT);
        possibleParticipants.add(p);
        currentPot.addParticipant(p);
        try {
            Thread.sleep(100); // creation time will be the id of the pot. Prevents id collisions.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Pot pot = new Pot(network.getCryptoCurrency(), 5, EXPECTED_BETTING_AMOUNT);
        for (int i = 0; i < 5; i++) {
            Participant cp = new Participant(address.substring(0, address.length() - 2) + "D" + i,
                    address.substring(0, address.length() - 2) + "P" + +i);
            cp.setPseudonym(network.getCryptoCurrency().getShortName() + "Tester" + i);
            cp.setReceivedAmount(EXPECTED_BETTING_AMOUNT);
            possibleParticipants.add(cp);
            pot.addParticipant(cp);
        }
        pot.close(new Date(), getSampleBlockhash(network), 755174);
        closedPots.add(pot);
        try {
            Thread.sleep(100);// because creation time will be the id of the pot. Prevents id
                              // collisions.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Pot pot2 = new Pot(network.getCryptoCurrency(), 10, EXPECTED_BETTING_AMOUNT);
        for (int i = 0; i < 10; i++) {
            Participant cp = new Participant(address.substring(0, address.length() - 2) + "D" + i,
                    address.substring(0, address.length() - 2) + "P" + +i);
            cp.setPseudonym(network.getCryptoCurrency().getShortName() + "Tester" + i);
            cp.setReceivedAmount(EXPECTED_BETTING_AMOUNT);
            possibleParticipants.add(cp);
            pot2.addParticipant(cp);
        }
        pot2.close(new Date(), getSampleBlockhash(network), 755170);
        closedPots.add(pot2);
    }


}
