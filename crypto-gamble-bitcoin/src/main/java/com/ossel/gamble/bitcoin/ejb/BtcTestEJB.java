package com.ossel.gamble.bitcoin.ejb;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.bitcoinj.params.AbstractBitcoinNetParams;
import org.bitcoinj.params.TestNet3Params;
import com.ossel.gamble.bitcoin.services.AbstractBitcoinService;
import com.ossel.gamble.core.data.enums.CryptoNetwork;

/**
 * Class will be excluded for the default jar via the maven jar plugin.
 *
 */
@Startup
@Singleton
public class BtcTestEJB extends AbstractBitcoinService {

    @Override
    public AbstractBitcoinNetParams getNetworkParams() {
        return TestNet3Params.get();
    }

    @Override
    public CryptoNetwork getCryptoNetwork() {
        return CryptoNetwork.BTC_TEST;
    }
}
