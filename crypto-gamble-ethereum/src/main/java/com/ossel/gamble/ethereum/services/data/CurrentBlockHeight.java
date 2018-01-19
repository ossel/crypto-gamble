package com.ossel.gamble.ethereum.services.data;

import java.io.IOException;
import java.math.BigInteger;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlockNumber;

public class CurrentBlockHeight extends ExpiryCacheValue {

    public CurrentBlockHeight(Web3j web3j, Credentials credentials) {
        super(web3j, credentials, 5 * SECOND);
    }

    @Override
    protected void refresh() {
        BigInteger height = fetchCurrentBlockHeight();
        if (height != null)
            setValue(height);
    }

    private BigInteger fetchCurrentBlockHeight() {
        try {
            EthBlockNumber ethBlockNumber = getService().ethBlockNumber().send();
            return ethBlockNumber.getBlockNumber();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new BigInteger("0");
    }

    public BigInteger getValue() {
        return (BigInteger) super.getValue();
    }


}
