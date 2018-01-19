package com.ossel.gamble.ethereum.contract;

import java.math.BigInteger;
import org.junit.Test;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import com.ossel.gamble.ethereum.RinkebyTestcase;
import com.ossel.gamble.ethereum.generated.TrustlessGambling;

public class PayoutTest extends RinkebyTestcase {

    @Test
    public void testPayout() throws Exception {
        String contractAddress = "0x9b04da87b9bedafaee8f02c53bb5e290dce0cf2d";
        System.out.println("getEmptyContractCreationGasAmount: "
                + getEmptyContractCreationGasAmount().intValue());
        TrustlessGambling contract =
                TrustlessGambling.load(contractAddress, web3j, getCredentials(), getGasPrice(),
                        getEmptyContractCreationGasAmount().multiply(BigInteger.valueOf(100)));
        System.out.println("Connected to contract " + contract.getContractAddress());
        System.out.println("contract binary:" + contract.getContractBinary());
        TransactionReceipt txnReceipt;
        String myAddress = getWalletAddress();
        System.out.println("myAddress " + myAddress);
        txnReceipt = contract.payout().send();
        System.out.println(myAddress + "called: payout() in block " + txnReceipt.getBlockHash()
                + " status " + txnReceipt.getStatus());
    }
}
