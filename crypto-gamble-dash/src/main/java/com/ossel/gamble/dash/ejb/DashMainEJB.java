package com.ossel.gamble.dash.ejb;


import javax.ejb.Singleton;
import javax.ejb.Startup;
import com.ossel.gamble.core.data.enums.CryptoNetwork;
import com.ossel.gamble.dash.services.DashService;

@Startup
@Singleton
public class DashMainEJB extends DashService {


    @Override
    public CryptoNetwork getCryptoNetwork() {
        return CryptoNetwork.DASH_MAIN;
    }

}
