package com.ossel.gamble.bitcoin.listeners;

import java.util.Date;
import org.apache.log4j.Logger;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.listeners.TransactionConfidenceEventListener;
import org.bitcoinj.wallet.Wallet;
import com.ossel.gamble.bitcoin.services.AbstractBitcoinService;
import com.ossel.gamble.core.data.Participant;
import com.ossel.gamble.core.data.Pot;
import com.ossel.gamble.core.utils.CoreUtil;

public class TxnConfidenceListener implements TransactionConfidenceEventListener {

    private static final Logger log = Logger.getLogger(TxnConfidenceListener.class);

    private AbstractBitcoinService service;
    private Transaction targetTxn;
    private Participant participant;

    public TxnConfidenceListener(AbstractBitcoinService service, Transaction targetTxn,
            Participant participant) {
        this.service = service;
        this.targetTxn = targetTxn;
        this.participant = participant;
    }

    @Override
    public void onTransactionConfidenceChanged(Wallet wallet, Transaction txn) {
        if (txn.equals(targetTxn)) {
            log.debug("onTransactionConfidenceChanged Tx: " + txn.getHash());
            switch (txn.getConfidence().getConfidenceType()) {
                case PENDING:
                    // unconfirmed but should be included shortly
                    break;
                case BUILDING:
                    // transaction is included in the best chain
                    Pot currentPot = service.getCurrentPot();
                    participant.setPotIndex(currentPot.getNbrOfParticipants());
                    currentPot.addParticipant(participant);
                    if (currentPot.isFull()) {
                        service.closeCurrentPot(new Date());
                    }
                    currentPot.setState(CoreUtil.getPotState(currentPot));
                    wallet.removeTransactionConfidenceEventListener(this);
                    break;
                case IN_CONFLICT:
                    log.warn("possible double spend of txn " + txn.getHashAsString());
                    break;
                case DEAD:
                    log.warn("txn " + txn.getHashAsString()
                            + " won't confirm unless there is another re-org");
                    wallet.removeTransactionConfidenceEventListener(this);
                    break;
                default:
                    log.warn("Unhandled txn confidence case["
                            + txn.getConfidence().getConfidenceType() + "] for txn "
                            + txn.getHashAsString());
            }
        }
    }

}
