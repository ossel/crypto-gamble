package com.ossel.gamble.core.data.enums;

public enum CryptoNetwork {

    BTC_MAIN("Bitcoin Mainnet", CryptoCurrency.BITCOIN, false), //
    BTC_TEST("Bitcoin Testnet", CryptoCurrency.BITCOIN, true), //
    DASH_MAIN("DASH Mainnet", CryptoCurrency.DASH, false), //
    ETH_MAIN("Ethereum Mainnet", CryptoCurrency.ETHEREUM, false), //
    ETH_RINKEBY("Ethereum Rinkeby Testnet", CryptoCurrency.ETHEREUM, true), //
    ETH_ROPSTEN("Ethereum Ropsten Testnet", CryptoCurrency.ETHEREUM, true); //

    private String networkName;
    private CryptoCurrency cryptoCurrency;
    private boolean testnet;


    private CryptoNetwork(String name, CryptoCurrency currency, boolean testnetwork) {
        this.networkName = name;
        this.cryptoCurrency = currency;
        this.testnet = testnetwork;
    }


    public String getNetworkName() {
        return networkName;
    }


    public CryptoCurrency getCryptoCurrency() {
        return cryptoCurrency;
    }


    public boolean isTestnet() {
        return testnet;
    }

    public String getExplorerLink() {
        switch (this) {
            case BTC_MAIN:
                return "https://blockchain.info/en";
            case BTC_TEST:
                return "https://chain.so/testnet/btc";
            case DASH_MAIN:
                return "https://explorer.dash.org";
            case ETH_MAIN:
                return "https://etherscan.io/blocks";
            case ETH_ROPSTEN:
                return "https://ropsten.etherscan.io/blocks";
            case ETH_RINKEBY:
                return "https://rinkeby.etherscan.io/blocks";
            default:
                return "https://en.wikipedia.org/wiki/Wikipedia:To-do_list";
        }
    }

    public String getExplorerLinkToBlock(String blockHash) {
        switch (this) {
            case BTC_MAIN:
                return "https://blockchain.info/en";
            case BTC_TEST:
                return "https://chain.so/block/BTCTEST/" + blockHash;
            case DASH_MAIN:
                return "https://explorer.dash.org/block/" + blockHash;
            case ETH_MAIN:
                return "https://etherscan.io//block/" + blockHash;
            case ETH_RINKEBY:
                return "https://rinkeby.etherscan.io/block/" + blockHash;
            case ETH_ROPSTEN:
                return "https://ropsten.etherscan.io/block/" + blockHash;
            default:
                return "https://en.wikipedia.org/wiki/Wikipedia:To-do_list";
        }
    }

    public String getExplorerLinkToAddress(String address) {
        switch (this) {
            case BTC_MAIN:
                return "https://blockchain.info/en/address/" + address;
            case DASH_MAIN:
                return "https://explorer.dash.org/address/" + address;
            case ETH_MAIN:
                return "https://etherscan.io//address/" + address;
            case ETH_RINKEBY:
                return "https://rinkeby.etherscan.io/address/" + address;
            case ETH_ROPSTEN:
                return "https://ropsten.etherscan.io/address/" + address;
            case BTC_TEST:
                return "https://chain.so/address/BTCTEST/" + address;
            default:
                return "https://en.wikipedia.org/wiki/Wikipedia:To-do_list";
        }
    }

    public String getExplorerLinkToTxn(String txnId) {
        String link = getExplorerLink();
        switch (this) {
            case BTC_MAIN:
            case DASH_MAIN:
            case ETH_MAIN:
            case ETH_RINKEBY:
            case ETH_ROPSTEN:
                link += "/tx/" + txnId;
                break;
            case BTC_TEST:
                link = "https://chain.so/tx/BTCTEST/" + txnId;
                break;
            default:
                return "https://en.wikipedia.org/wiki/Wikipedia:To-do_list";
        }
        return link;
    }

    /**
     * Converts the enum name to the EJB java class name according to the convention.<br>
     * Example:<br>
     * ETH_ROPSTEN -> EthRopstenEJB <br>
     * DASH_MAIN -> DashMainEJB
     * 
     * @return the java ejb class name
     */
    public String getNetworkServiceEjbName() {
        String name = this.name().toLowerCase();
        StringBuilder result = new StringBuilder(name.length());
        result.append(Character.toUpperCase(name.charAt(0)));
        for (int i = 1; i < name.length(); i++) {
            if (name.charAt(i) == '_') {
                result.append(Character.toUpperCase(name.charAt(i + 1)));
                i++;
            } else {
                result.append(name.charAt(i));
            }
        }
        result.append("EJB");
        return result.toString();
    }
}
