package com.ossel.gamble.dash.listeners;

import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import com.ossel.gamble.core.data.Participant;
import com.ossel.gamble.core.data.Pot;
import com.ossel.gamble.dash.services.DashService;

public class CoinReceivedListener extends AbstractListener
        implements WalletCoinsReceivedEventListener {

    private static final Logger log = Logger.getLogger(CoinReceivedListener.class);

    private DashService dashService;

    public CoinReceivedListener(DashService dashService2) {
        this.dashService = dashService2;
    }

    @Override
    public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
        listenerTriggered();
        this.dashService.paymentReceived();
        Coin value = tx.getValueSentToMe(wallet);
        log.info("Received " + value.toFriendlyString() + " from transaction: " + tx.getHash());
        log.debug("Transaction details: " + tx.toString());
        Pot currentPot = dashService.getCurrentPot();
        List<Participant> possibleParticipants = dashService.getPossibleParticipants();
        WalletAppKit appkit = dashService.getAppKit();
        for (TransactionOutput transactionOutput : tx.getOutputs()) {
            for (Participant p : possibleParticipants) {
                String depositAddress = p.getDepositAddress();
                Address a = transactionOutput.getAddressFromP2PKHScript(appkit.params());
                if (a == null) {
                    a = transactionOutput.getAddressFromP2SH(appkit.params());
                }
                if (depositAddress != null && a != null && depositAddress.equals(a.toString())) {
                    if (p.hasPayed()) {
                        log.warn(p + " had already payed. Maybe he used an address twice.");
                    }
                    Date receiveTime = p.setReceivedAmount(value.getValue());
                    if (p.getPayoutAddress() == null || !p.getPayoutAddress().isEmpty()) {
                        log.warn("No special payout address was defined.");
                        String fromAddress = tx.getInput(0).getFromAddress().toString();
                        log.info("Setting payout address to input address: " + fromAddress);
                        p.setPayoutAddress(fromAddress);
                    }

                    currentPot.addParticipant(p);
                    log.info(
                            "Received " + value.toFriendlyString() + " coins from " + p.toString());
                    if (currentPot.isFull()) {
                        dashService.closeCurrentPot(receiveTime);
                        log.info("Pot" + currentPot.getCreateTime().getTime()
                                + " was full and has been closed.");
                    }
                }
            }

        }

    }

}
