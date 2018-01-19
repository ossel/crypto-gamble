package com.ossel.gamble.gui.services;

import com.ossel.gamble.core.data.enums.CryptoCurrency;

public interface PriceService {

    public String getPrice(CryptoCurrency currency);
}
