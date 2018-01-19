package com.ossel.gamble.ethereum.data;

import com.ossel.gamble.core.data.Pot;
import com.ossel.gamble.core.data.enums.CryptoCurrency;

public class EthPot extends Pot {

    private String message;

    public EthPot(CryptoCurrency currency, int expectedParticipants, long expectedBettingamount) {
        super(currency, expectedParticipants, expectedBettingamount);
    }

    @Override
    public String getState() {
        if (message != null && !message.isEmpty())
            return super.getState() + "\n" + message;
        return super.getState();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
