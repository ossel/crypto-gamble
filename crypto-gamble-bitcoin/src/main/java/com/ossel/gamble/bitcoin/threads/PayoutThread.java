package com.ossel.gamble.bitcoin.threads;

import java.util.concurrent.ExecutionException;
import org.apache.log4j.Logger;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.Wallet;
import com.ossel.gamble.bitcoin.services.AbstractBitcoinService;
import com.ossel.gamble.core.data.Pot;

public class PayoutThread extends Thread {

    private static final Logger log = Logger.getLogger(PayoutThread.class);

    private Pot pot;
    private AbstractBitcoinService bitcoinService;

    public PayoutThread(AbstractBitcoinService abstractBitcoinService, Pot pot) {
        this.pot = pot;
        this.bitcoinService = abstractBitcoinService;
    }

    private void payout(Pot pot)
            throws InsufficientMoneyException, InterruptedException, ExecutionException {
        if (pot.isPayoutStarted()) {
            log.error("Payout already started: " + pot.getPayoutTxnId() + " - "
                    + pot.getPayoutError());
        } else {
            pot.setPayoutStarted(true);
            Address winnerAddress = new Address(bitcoinService.getNetworkParams(),
                    pot.getWinner().getPayoutAddress());
            Coin potValue = Coin.SATOSHI
                    .multiply(pot.getParticipants().size() * pot.getExpectedBettingAmount());
            Wallet.SendResult result = bitcoinService.getAppKit().wallet()
                    .sendCoins(bitcoinService.getAppKit().peerGroup(), winnerAddress, potValue);
            String txnId = result.tx.getHash().toString();
            pot.setPayoutTxnId(txnId);
            log.info("Payout TXN ID = " + txnId);
            Transaction transaction = result.broadcastComplete.get();
            log(transaction);
        }
    }

    private void log(Transaction transaction) {
        log.info("payout txnid " + transaction.getHashAsString() + " added to pot " + pot.getId());
        log.debug(transaction.toString());
    }

    @Override
    public void run() {
        log.info("#########################");
        log.info("####Payout pot " + pot.getId() + "###");
        log.info("#########################");
        if (pot.getWinner().isBankParticipant()) {
            pot.setPayoutError(new Exception("No payout. The bank is the winner."));
        } else {
            try {
                payout(pot);
            } catch (InsufficientMoneyException e) {
                log.error(e);
                pot.setPayoutError(e);
            } catch (InterruptedException e) {
                log.error(e);
                pot.setPayoutError(e);
            } catch (ExecutionException e) {
                log.error(e);
                pot.setPayoutError(e);
            } catch (RuntimeException e) {
                log.error(e);
                pot.setPayoutError(e);
            }
        }
    }
}
