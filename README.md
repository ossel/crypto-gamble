# crypto-gamble: Trustless Gambling Platform

## general
This prototype was created during my master thesis.

## getting started

### prerequisites
- java 8
- maven 3 
- wildfly 11.0.0.Final <https://wildfly.org/downloads/>

### configuration
- maven: overwrite the <JBOSS_HOME>C:\path\to\your\server\wildfly-11.0.0.Final</JBOSS_HOME> property in the parent pom.xml. Maven will automatically copy the .war into the wildfly deployments directory.
- Ethereum: Configure your API KEY in /crypto-gamble-ethereum/src/main/java/com/ossel/gamble/ethereum/UserConfiguration.java


### build
The GUI application can be build for differnt crypto networks by using the corresponding maven build profile.  

| maven build profile | Crypto network           |
| :-----------------: | ------------------------ |
| BTC (default)       | Bitcoin Mainnet          |
| BTC_TEST            | Bitcoin Testnet          |
| DASH                | DASH Mainnet             |
| ETH                 | Ethereum Mainnet         |
| ETH_ROP             | Ethereum Ropsten Testnet |
| ETH_RIN             | Ethereum Rinkeby Testnet |
