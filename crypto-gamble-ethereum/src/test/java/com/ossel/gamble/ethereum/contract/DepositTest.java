package com.ossel.gamble.ethereum.contract;

import java.math.BigInteger;
import org.junit.Test;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import com.ossel.gamble.ethereum.RinkebyTestcase;
import com.ossel.gamble.ethereum.generated.TrustlessGambling;

public class DepositTest extends RinkebyTestcase {

    private void deposit(TrustlessGambling contract, String contractAddress, String payoutAddress)
            throws Exception {

        System.out.println("getEmptyContractCreationGasAmount: "
                + getEmptyContractCreationGasAmount().intValue());
        TransactionReceipt txnReceipt;
        // assertTrue(contract.isValid());
        String myAddress = getWalletAddress();
        txnReceipt = contract.deposit(payoutAddress, BigInteger.valueOf(1000)).send();
        if ("0x0".equals(txnReceipt.getStatus())) {
            System.out.println("Txn " + txnReceipt.getTransactionHash() + " failed :(");
        }
        if ("0x1".equals(txnReceipt.getStatus())) {
            System.out.println("Txn " + txnReceipt.getTransactionHash() + " successful :)");
        }
        System.out.println(myAddress + "called: deposit(" + payoutAddress + ") in block "
                + txnReceipt.getBlockHash() + " status " + txnReceipt.getStatus());

    }

    @Test
    public void testDeposit() throws Exception {
        String contractAddress = "0x9b04da87b9bedafaee8f02c53bb5e290dce0cf2d";
        String payoutAddress = "0x83ddd477852f6f9493d8cfa0f895e44ef2eac2ce"; // payout-wallet.json
        TrustlessGambling contract =
                TrustlessGambling.load(contractAddress, web3j, getCredentials(), getGasPrice(),
                        getEmptyContractCreationGasAmount().multiply(BigInteger.valueOf(100)));
        deposit(contract, contractAddress, payoutAddress);
        deposit(contract, contractAddress, payoutAddress);
        deposit(contract, contractAddress, payoutAddress);
    }
}
