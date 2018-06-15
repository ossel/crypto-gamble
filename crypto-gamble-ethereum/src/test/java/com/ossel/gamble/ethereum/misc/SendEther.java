package com.ossel.gamble.ethereum.misc;

import java.math.BigInteger;
import org.junit.Test;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import com.ossel.gamble.ethereum.RinkebyTestcase;

public class SendEther extends RinkebyTestcase {


    static final BigInteger GAS_PRICE = BigInteger.valueOf(20_000_000_000L);
    static final BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000);

    @Test
    public void senEther() throws Exception {
        // send1Eth("0x57602eeedCC0b268F94Ac6d3b6fA0D5cf7cba09D");//
        // 52f6c9f3642eac2c801e18c0764121ea4c7e85d345381d8628daa757232c6088
        // send1Eth("0x2ED371F3608076E3a89debA2F6c93674CA049830");//
        // 10905288cc12cc7a17aed7dbe2121f279df26a18b9bfde5389393e73cacda811
        // send1Eth("0x325F487B785A389049AE7fd58252f29A8353AA9c");//
        // 45980b489d0a1b0816ae607847c009efdcb3ffeeaa11fbbfe25954b02b2f3cd9
    }

    private void send1Eth(String to) throws Exception {
        String from = getWalletAddress();
        EthGetTransactionCount ethGetTransactionCount = web3j
                .ethGetTransactionCount(from, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, GAS_PRICE,
                GAS_LIMIT, to, Convert.toWei("1", Convert.Unit.ETHER).toBigInteger());
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, getCredentials());
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction ethSendTransaction =
                web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        String transactionHash = ethSendTransaction.getTransactionHash();
        System.out.println("Txn hash: " + transactionHash);
    }

}
