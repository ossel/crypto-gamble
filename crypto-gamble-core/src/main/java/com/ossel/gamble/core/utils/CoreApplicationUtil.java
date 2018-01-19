package com.ossel.gamble.core.utils;

import java.io.File;
import com.ossel.gamble.core.service.CryptoNetworkService;

public class CoreApplicationUtil {



    public static File getWalletDirectory() {
        String JBOSS_HOME = System.getProperty("jboss.home.dir");
        return new File(JBOSS_HOME + File.separator + CryptoNetworkService.WALLET_DIRECTORY_NAME);
    }

}
