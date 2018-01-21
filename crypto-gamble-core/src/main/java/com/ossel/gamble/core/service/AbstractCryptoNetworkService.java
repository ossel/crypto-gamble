package com.ossel.gamble.core.service;

import com.ossel.gamble.core.data.Pot;
import com.ossel.gamble.core.data.enums.CryptoNetwork;
import com.ossel.gamble.core.utils.CoreUtil;

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

    public Pot createNewPot() {
        Pot pot = null;
        if (Math.random() > 0.3) {
            pot = new Pot(getCryptoNetwork().getCryptoCurrency(), 5, getExpectedBettingAmount());
        } else {
            pot = new Pot(getCryptoNetwork().getCryptoCurrency(), 2, getExpectedBettingAmount());
        }
        pot.setState(CoreUtil.getPotState(pot));
        return pot;
    }


    protected String getBankPseudonym() {
        switch (getCurrentPot().getNbrOfOpenSlots()) {
            case 1:
                return "David Rockefeller";
            case 2:
                return "Mario Draghi";
            case 3:
                return "Janet Yellen";
            case 4:
                return "Helicopter Ben";
            default:
                return "Helicopter Ben";
        }
    }

    public abstract long getExpectedBettingAmount();
}
