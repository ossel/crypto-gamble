package com.ossel.gamble.bitcoin.ejb;


import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.bitcoinj.params.AbstractBitcoinNetParams;
import org.bitcoinj.params.MainNetParams;
import com.ossel.gamble.bitcoin.services.AbstractBitcoinService;
import com.ossel.gamble.core.data.enums.CryptoNetwork;

/**
 * Class will be excluded for the testnet jar via the maven jar plugin.
 *
 */
@Startup
@Singleton
public class BtcMainEJB extends AbstractBitcoinService {

    @Override
    public CryptoNetwork getCryptoNetwork() {
        return CryptoNetwork.BTC_MAIN;
    }

    @Override
    public AbstractBitcoinNetParams getNetworkParams() {
        return MainNetParams.get();
    }


}
