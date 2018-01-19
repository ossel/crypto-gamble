package com.ossel.gamble.ethereum.ejb;


import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.infura.InfuraHttpService;
import com.ossel.gamble.core.data.enums.CryptoNetwork;
import com.ossel.gamble.ethereum.UserConfiguration;
import com.ossel.gamble.ethereum.services.EthereumService;

@Startup
@Singleton
public class EthMainEJB extends EthereumService {

    @Override
    protected Web3j getWeb3jService() {
        return Web3j.build(
                new InfuraHttpService("https://mainnet.infura.io/" + UserConfiguration.API_KEY));
    }

    @Override
    public CryptoNetwork getCryptoNetwork() {
        return CryptoNetwork.ETH_MAIN;
    }

}
