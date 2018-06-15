package com.ossel.gamble.ethereum.services.data;

import java.io.IOException;
import java.math.BigInteger;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGasPrice;

public class GasPrice extends ExpiryCacheValue {


    public GasPrice(Web3j web3j, Credentials credentials) {
        super(web3j, credentials, 60 * SECOND);
    }

    @Override
    protected void refresh() {
        BigInteger gasPrice = fetchGasPrice();
        if (gasPrice != null)
            setValue(gasPrice);
    }

    private BigInteger fetchGasPrice() {
        EthGasPrice ethGasPrice;
        try {
            ethGasPrice = getWeb3jService().ethGasPrice().send();
            return ethGasPrice.getGasPrice();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public BigInteger getValue() {
        return (BigInteger) super.getValue();
    }

}
