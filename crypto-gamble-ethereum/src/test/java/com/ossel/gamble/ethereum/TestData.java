package com.ossel.gamble.ethereum;
import java.math.BigInteger;
import org.web3j.protocol.core.methods.request.Transaction;

public class TestData {

    public String validAccount() {
        // https://testnet.etherscan.io/address/0xCB10FBad79F5e602699fFf2Bb4919Fbd87AbC8CC
        return "0xcb10fbad79f5e602699fff2bb4919fbd87abc8cc";
    }

    public String validContractCode() {
        return "0x";
    }

    public Transaction buildTransaction() {
        return Transaction.createContractTransaction(validAccount(), BigInteger.ZERO, // nonce
                Transaction.DEFAULT_GAS, validContractCode());
    }

}
