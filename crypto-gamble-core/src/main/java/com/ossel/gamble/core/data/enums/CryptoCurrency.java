package com.ossel.gamble.core.data.enums;

public enum CryptoCurrency {

    BITCOIN("BTC", "Bitcoin", "Bitcoin", "Satoshi"), // bitcoin
    DASH("DASH", "DASH", "DASH - Digital Cash", "duff"), // dash
    ETHEREUM("ETH", "Ether", "Ether", "WEI"); // ethereum

    /**
     * The currency code used by coinmarketcap
     */
    private String code;

    /**
     * The currency name
     */
    private String shortName;
    /**
     * The currency name - slogan if existent
     */
    private String fullName;

    private String smallestDenomination;


    private CryptoCurrency(String code, String shortName, String fullName,
            String smallestDenomination) {
        this.code = code;
        this.shortName = shortName;
        this.fullName = fullName;
        this.smallestDenomination = smallestDenomination;
    }


    public String getCode() {
        return code;
    }


    public String getShortName() {
        return shortName;
    }


    public String getFullName() {
        return fullName;
    }



    public String getSmallestDenomination() {
        return smallestDenomination;
    }


    public String getPriceUrl() {
        if (this.equals(ETHEREUM)) {
            return "https://api.coinmarketcap.com/v1/ticker/ethereum/?convert=EUR";
        }
        return "https://api.coinmarketcap.com/v1/ticker/" + this.getShortName().toLowerCase()
                + "/?convert=EUR";
    }

}
