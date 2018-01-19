package com.ossel.gamble.ethereum;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import org.junit.Before;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.infura.InfuraHttpService;
import com.ossel.gamble.ethereum.UserConfiguration;

public abstract class RinkebyTestcase {


    public static final String WALLET_FILENAME = "ethereum.json";
    public static final String WALLET_PASSWORD = "changeit";

    protected Web3j web3j;
    protected TestData testData;

    @Before
    public void setUp() {
        this.web3j = Web3j.build(
                new InfuraHttpService("https://rinkeby.infura.io/" + UserConfiguration.API_KEY));
        testData = new TestData();
    }



    protected String getWalletBalance() {
        try {
            EthGetBalance ethGetBalance =
                    web3j.ethGetBalance(getWalletAddress(), DefaultBlockParameter.valueOf("latest"))
                            .send();
            return ethGetBalance.getBalance().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "? ETH";
    }

    protected String getWalletAddress() {
        return getCredentials().getAddress();
    }


    protected Credentials getCredentials() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(WALLET_FILENAME).getFile());
        // System.out.println(file.getAbsolutePath());
        try {
            return WalletUtils.loadCredentials(WALLET_PASSWORD, file.getAbsolutePath());
        } catch (IOException | CipherException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected BigInteger getGasPrice() {
        EthGasPrice ethGasPrice;
        try {
            ethGasPrice = web3j.ethGasPrice().send();
            return ethGasPrice.getGasPrice();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    protected BigInteger getEmptyContractCreationGasAmount() throws Exception {
        EthEstimateGas ethEstimateGas = web3j.ethEstimateGas(testData.buildTransaction()).send();
        return ethEstimateGas.getAmountUsed();
    }

}
