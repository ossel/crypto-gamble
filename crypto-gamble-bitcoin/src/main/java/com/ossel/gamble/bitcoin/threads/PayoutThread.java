package com.ossel.gamble.bitcoin.threads;

import java.util.concurrent.ExecutionException;
import org.apache.log4j.Logger;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.wallet.Wallet;
import com.ossel.gamble.bitcoin.services.BitcoinService;
import com.ossel.gamble.core.data.Pot;

public class PayoutThread extends Thread {

    private static final Logger log = Logger.getLogger(PayoutThread.class);

    Pot pot;
    BitcoinService service;

    public PayoutThread(BitcoinService abstractBitcoinService, Pot pot) {
        this.pot = pot;
        this.service = abstractBitcoinService;
    }

    private void payout(Pot pot)
            throws InsufficientMoneyException, InterruptedException, ExecutionException {
        // Get the address 1RbxbA1yP2Lebauuef3cBiBho853f7jxs in object
        // form.
        Address targetAddress =
                new Address(service.getNetworkParams(), pot.getWinner().getPayoutAddress());
        // TODO find out what hex is. new Address(MainNetParams.get(),
        // Hex.decode("4a22c3c4cbb31e4d03b15550636762bda0baf85a"));
        // Do the send of 1 BTC in the background. This could throw
        // InsufficientMoneyException.
        Wallet.SendResult result =
                service.getAppKit().wallet().sendCoins(service.getAppKit().peerGroup(),
                        targetAddress, Coin.SATOSHI.multiply(pot.getPotLimit()));
        String txnId = result.tx.getHash().toString();
        pot.setPayoutTxnId(txnId);
        log.info("payout txn has the id " + txnId);
        // Save the wallet to disk, optional if using auto saving (see
        // below).
        // TODO wallet.saveToFile(....);
        // Wait for the transaction to propagate across the P2P network,
        // indicating acceptance.
        result.broadcastComplete.get();
        log.info("payout txnid " + txnId + " added to pot " + pot.getId());
    }

    @Override
    public void run() {
        log.info("#########################");
        log.info("####Payout pot " + pot.getId() + "###");
        log.info("#########################");
        pot.setPayoutStarted(true);
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
