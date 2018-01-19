package com.ossel.gamble.core.service;

import com.ossel.gamble.core.data.enums.CryptoNetwork;

public abstract class AbstractCryptoNetworkService implements CryptoNetworkService {


    /**
     * Checks if the EJB class that implements this service has the correct name. There is a naming
     * convention because the GUI application needs to lookup this EJB class.
     */
    public AbstractCryptoNetworkService() {
        String className = this.getClass().getSimpleName();
        if (className.contains("$")) {
            className = className.split("\\$")[0];
        }
        if (!className.equals(getCryptoNetwork().getNetworkServiceEjbName()))
            throw new RuntimeException(this.getClass().getSimpleName() + " class must be named "
                    + getCryptoNetwork().getNetworkServiceEjbName()
                    + " to meet the naming convention. See " + CryptoNetwork.class.getSimpleName()
                    + ".getNetworkServiceEjbName()");

    }
}
