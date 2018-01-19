package com.ossel.gamble.core.data;

public class ServiceInformation {

    public static final String WALLET_VERSION = "wallet-version";
    public static final String SERVICE_VERSION = "version";
    public static final String SERVICE_STATE = "state";
    private String key;
    private String value;

    public ServiceInformation(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "[key=" + key + ", value=" + value + "]";
    }

    public static ServiceInformation walletVersion(String version) {
        return new ServiceInformation(WALLET_VERSION, version);
    }

    public static ServiceInformation serviceVersion(String version) {
        return new ServiceInformation(SERVICE_VERSION, version);
    }

    public static ServiceInformation serviceState(String state) {
        return new ServiceInformation(SERVICE_STATE, state);
    }

}
