package com.ossel.gamble.bitcoin.listeners;

import java.util.List;
import org.apache.log4j.Logger;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
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
        log.debug("Transaction details: " + txn.toString());
        Coin value = txn.getValueSentToMe(wallet);
        Pot currentPot = service.getCurrentPot();
        if (currentPot.getExpectedBettingAmount() > value.getValue()) {
            log.warn("Player did not pay enough.");
            return;
        }
        List<Participant> participants = service.getPossibleParticipants();
        NetworkParameters params = service.getAppKit().params();
        for (TransactionOutput txnOutput : txn.getOutputs()) {
            Address a = txnOutput.getAddressFromP2PKHScript(params);
            if (a == null) {
                a = txnOutput.getAddressFromP2SH(params);
            }
            String address = a.toString();
            log.info("Coins received on address: " + address);
            for (Participant participant : participants) {
                String depositAddress = participant.getDepositAddress();
                if (depositAddress != null && a != null && depositAddress.equals(address)) {

                    log.info("Received " + value.toFriendlyString() + " coins from "
                            + participant.toString());
                    if (participant.hasPayed()) {
                        log.warn(participant
                                + " had already payed. Maybe he used an address twice.");
                    }

                    participant.setReceivedAmount(value.getValue());
                    String fromAddress = txn.getInput(0).getFromAddress().toString();
                    log.info("Payout address: " + fromAddress);
                    participant.setPayoutAddress(fromAddress);
                    wallet.addTransactionConfidenceEventListener(
                            new TxnConfidenceListener(service, txn, participant));
                }
            }
        }
        currentPot.setState(CoreUtil.getPotState(currentPot));
    }


}
