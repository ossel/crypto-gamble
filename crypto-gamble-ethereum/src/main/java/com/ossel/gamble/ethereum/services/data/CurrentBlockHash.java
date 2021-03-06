package com.ossel.gamble.ethereum.services.data;

import java.io.IOException;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlock;

public class CurrentBlockHash extends ExpiryCacheValue {

    CurrentBlockHeight blockHeight;

    public CurrentBlockHash(Web3j web3j, Credentials credentials, CurrentBlockHeight blockHeight) {
        super(web3j, credentials, 5 * SECOND);
        this.blockHeight = blockHeight;
    }

    /**
     * refresh if cache value expired. Called automatically by the ExpiryCacheValue.getValue()
     * method.
     */
    @Override
    protected void refresh() {
        String blockHash = fetchCurrentBlockHash();
        if (blockHash != null)
            setValue(blockHash);
    }

    private String fetchCurrentBlockHash() {
        EthBlock ethBlock = null;
        DefaultBlockParameterNumber number =
                new DefaultBlockParameterNumber(blockHeight.getValue());;
        try {
            ethBlock = getWeb3jService().ethGetBlockByNumber(number, false).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = "error";
        if (ethBlock != null && ethBlock.getBlock() != null) {
            result = ethBlock.getBlock().getHash();
        }
        return result;
    }

    public String getValue() {
        return (String) super.getValue();
    }

}
