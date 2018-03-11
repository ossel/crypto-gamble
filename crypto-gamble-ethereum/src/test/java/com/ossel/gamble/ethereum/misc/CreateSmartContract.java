package com.ossel.gamble.ethereum.misc;

import java.io.File;
import java.math.BigInteger;
import org.junit.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.infura.InfuraHttpService;
import com.ossel.gamble.ethereum.UserConfiguration;
import com.ossel.gamble.ethereum.generated.TrustlessGambling;

public class CreateSmartContract {

    @Test
    public void createContract() throws Exception {
        String WALLET_FILENAME = "ethereum.json";
        String WALLET_PASSWORD = "changeit";
        long GAS_LIMIT = 1000000;
        ClassLoader classLoader = getClass().getClassLoader();
        File walletFile = new File(classLoader.getResource(WALLET_FILENAME).getFile());
        Credentials credentials =
                WalletUtils.loadCredentials(WALLET_PASSWORD, walletFile.getAbsolutePath());
        System.out.println("Account address = " + credentials.getAddress());
        Web3j web3j = Web3j.build(
                new InfuraHttpService("https://rinkeby.infura.io/" + UserConfiguration.API_KEY));
        BigInteger currentGasPrice = web3j.ethGasPrice().send().getGasPrice();
        TrustlessGambling contract = TrustlessGambling
                .deploy(web3j, credentials, currentGasPrice, BigInteger.valueOf(GAS_LIMIT)).send();
        if ("0x1".equals(contract.getTransactionReceipt().get().getStatus())) {
            String address = contract.getContractAddress();
            System.out.println("Contract address = " + address);
            System.out.println(
                    "TXN hash = " + contract.getTransactionReceipt().get().getTransactionHash());
            System.out.println("Gas used = " + contract.getTransactionReceipt().get().getGasUsed());
        } else {
            System.out.println("Smart contract could not be deployed.");
        }
    }

}
