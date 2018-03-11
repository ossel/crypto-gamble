package com.ossel.gamble.bitcoin.listeners;

import java.util.List;
import org.apache.log4j.Logger;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import com.ossel.gamble.bitcoin.services.AbstractBitcoinService;
import com.ossel.gamble.core.data.Participant;
import com.ossel.gamble.core.data.Pot;
import com.ossel.gamble.core.utils.CoreUtil;

public class CoinsReceivedListener implements WalletCoinsReceivedEventListener {

    private static final Logger log = Logger.getLogger(CoinsReceivedListener.class);

    private AbstractBitcoinService service;

    public CoinsReceivedListener(AbstractBitcoinService dashService) {
        this.service = dashService;
    }

    @Override
    public void onCoinsReceived(Wallet wallet, Transaction txn, Coin prevBalance, Coin newBalance) {
        Coin value = txn.getValueSentToMe(wallet);
        log.info("Received " + value.toFriendlyString() + " from transaction: " + txn.getHash());
        log.debug("Transaction details: " + txn.toString());
        List<Participant> possibleParticipants = service.getPossibleParticipants();
        WalletAppKit appkit = service.getAppKit();
        for (TransactionOutput txnOutput : txn.getOutputs()) {
            for (Participant participant : possibleParticipants) {
                String depositAddress = participant.getDepositAddress();
                Address address = txnOutput.getAddressFromP2PKHScript(appkit.params());
                if (address == null) {
                    address = txnOutput.getAddressFromP2SH(appkit.params());
                }
                if (depositAddress != null && address != null
                        && depositAddress.equals(address.toString())) {
                    log.info("Received " + value.toFriendlyString() + " coins from "
                            + participant.toString());
                    if (participant.hasPayed()) {
                        log.warn(participant
                                + " had already payed. Maybe he used an address twice.");
                    }
                    participant.setReceivedAmount(value.getValue());
                    String fromAddress = txn.getInput(0).getFromAddress().toString();
                    participant.setPayoutAddress(fromAddress);
                    wallet.addTransactionConfidenceEventListener(
                            new TxnConfidenceListener(service, txn, participant));
                }
            }

        }
        Pot currentPot = service.getCurrentPot();
        currentPot.setState(CoreUtil.getPotState(currentPot));
    }

}
