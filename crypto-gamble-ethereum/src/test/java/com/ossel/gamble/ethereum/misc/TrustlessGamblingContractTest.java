package com.ossel.gamble.ethereum.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.math.BigInteger;
import org.junit.Test;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import com.ossel.gamble.ethereum.RinkebyTestcase;
import com.ossel.gamble.ethereum.generated.TrustlessGambling;

public class TrustlessGamblingContractTest extends RinkebyTestcase {

    private String createGamblingContract() throws Exception {
        Credentials credentials = getCredentials();
        assertNotNull(credentials);
        assertEquals("0x2201f3919589b519135ce977cc0906c9481069b2", getWalletAddress());
        BigInteger gasLimit = getEmptyContractCreationGasAmount().multiply(BigInteger.valueOf(100));
        TrustlessGambling contract =
                TrustlessGambling.deploy(web3j, credentials, getGasPrice(), gasLimit).send();
        String address = contract.getContractAddress();
        System.out.println("contract address = " + address);
        System.out
                .println("contract status = " + contract.getTransactionReceipt().get().getStatus());
        System.out.println("contract gaslimit = " + gasLimit + " gas used = "
                + contract.getTransactionReceipt().get().getGasUsed());
        return address;

    }

    @Test
    public void testContractCreation() throws Exception {
        assertNotNull(createGamblingContract());
    }

    @Test
    public void testContractInteraction() throws Exception {
        String contractAddress = "0xde3bce96479ebeac719039ecdd93eecb06c5247a";// createGamblingContract();
        System.out.println("getEmptyContractCreationGasAmount: "
                + getEmptyContractCreationGasAmount().intValue());
        TrustlessGambling contract =
                TrustlessGambling.load(contractAddress, web3j, getCredentials(), getGasPrice(),
                        getEmptyContractCreationGasAmount().multiply(BigInteger.valueOf(100)));
        System.out.println("Connected to contract " + contract.getContractAddress());
        System.out.println("contract binary:" + contract.getContractBinary());
        TransactionReceipt txnReceipt = contract.payout().send();
        System.out.println("checkPayout block:" + txnReceipt.getBlockHash());
        // assertTrue(contract.isValid());
        String myAddress = getWalletAddress();
        txnReceipt = contract.deposit(myAddress, BigInteger.valueOf(1000)).send();
        System.out
                .println("participated as:" + myAddress + " in block " + txnReceipt.getBlockHash());
        txnReceipt = contract.payout().send();
        System.out.println("checkPayout call status" + txnReceipt.getStatus() + " in block "
                + txnReceipt.getBlockHash());
        System.out.println("start loop");
        while (true) {
            BigInteger x = contract.nbrOfParticipants().send();
            System.out.println("nbrOfParticipants=" + x);
            String message = contract._message().send();
            System.out.println("message=" + message);
            Thread.sleep(3000);
        }
    }

}
