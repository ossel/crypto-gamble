package com.ossel.gamble.gui.services;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.ossel.gamble.core.data.enums.CryptoCurrency;
import com.ossel.gamble.core.ejbs.CryptoPriceServiceEjb;

public class PriceServiceImpl implements PriceService {

    private CryptoPriceServiceEjb priceServiceEjb;

    @Override
    public String getPrice(CryptoCurrency currency) {
        String price = lookupService().getPrice(currency);
        return price;
    }

    private CryptoPriceServiceEjb lookupService() {
        if (priceServiceEjb == null) {
            try {
                priceServiceEjb = (CryptoPriceServiceEjb) new InitialContext()
                        .lookup("java:module/" + CryptoPriceServiceEjb.class.getSimpleName());
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }
        return priceServiceEjb;
    }

}
