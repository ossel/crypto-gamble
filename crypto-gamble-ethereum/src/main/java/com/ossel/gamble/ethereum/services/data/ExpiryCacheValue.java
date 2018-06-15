package com.ossel.gamble.ethereum.services.data;

import java.math.BigInteger;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

public abstract class ExpiryCacheValue {

    public static final long SECOND = 1000;
    public static final BigInteger EMPTY_CONTRACT_CREATION_GAS = BigInteger.valueOf(53000);

    private Web3j web3j;
    private Credentials credentials;
    protected Object value;
    private long lastRefreshTime;
    private long threshold;

    public ExpiryCacheValue(Web3j web3j, Credentials credentials, long threshold) {
        super();
        this.web3j = web3j;
        this.credentials = credentials;
        this.threshold = threshold;
    }


    public Web3j getWeb3jService() {
        return web3j;
    }



    public Credentials getCredentials() {
        return credentials;
    }

    public Object getValue() {
        if (System.currentTimeMillis() - threshold > lastRefreshTime)
            refresh();
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
        lastRefreshTime = System.currentTimeMillis();
    }


    protected abstract void refresh();

}
