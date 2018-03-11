package com.ossel.gamble.ethereum.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.NetPeerCount;
import org.web3j.protocol.core.methods.response.NetVersion;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import com.ossel.gamble.core.data.Participant;
import com.ossel.gamble.core.data.Pot;
import com.ossel.gamble.core.data.ServiceInformation;
import com.ossel.gamble.core.data.enums.CryptoNetwork;
import com.ossel.gamble.core.service.AbstractCryptoNetworkService;
import com.ossel.gamble.core.utils.CoreUtil;
import com.ossel.gamble.ethereum.UserConfiguration;
import com.ossel.gamble.ethereum.services.data.CurrentBlockHash;
import com.ossel.gamble.ethereum.services.data.CurrentBlockHeight;
import com.ossel.gamble.ethereum.services.data.GasPrice;
import com.ossel.gamble.ethereum.services.data.PotService;

public abstract class EthereumService extends AbstractCryptoNetworkService {

    private static final Logger log = Logger.getLogger(EthereumService.class);


    private Date lastParticipantJoin = new Date();

    private String walletAddress;

    private Credentials credentials;

    // #######################
    // # Expiry cache values #
    // #######################

    private PotService potService;

    private GasPrice gasPrice;

    private CurrentBlockHash blockHash;

    private CurrentBlockHeight blockHight;

    @PostConstruct
    private void start() {
        log.info("#### start " + getClass().getSimpleName() + " network service ####");
        blockHight = new CurrentBlockHeight(getWeb3jService(), getCredentials());
        log.info("blockHight=" + blockHight.getValue().intValue());
        blockHash = new CurrentBlockHash(getWeb3jService(), getCredentials(), blockHight);
        log.info("blockHash=" + blockHash.getValue());
        gasPrice = new GasPrice(getWeb3jService(), getCredentials());
        log.info("gasPrice=" + gasPrice.getValue().intValue());
        potService = new PotService(getWeb3jService(), getCredentials(), gasPrice, blockHight,
                UserConfiguration.CONTRACT_ADDRESS);
        log.info("currentPot=" + potService.getValue().toString());
    }

    protected abstract Web3j getWeb3jService();


    public Pot getCurrentPot() {
        return potService.getCurrentPot();
    }

    public String getCurrentBlockHash() {
        return blockHash.getValue();
    }

    public int getCurrentBlockHeight() {
        return blockHight.getValue().intValue();
    }


    public List<Pot> getClosedPots() {
        return potService.getClosedPots();
    }

    /**
     * 
     * @param potId
     * @return null if not found
     */
    public Pot getPotById(long potId) {
        if (potService.getCurrentPot().getCreateTime().getTime() == potId) {
            return potService.getCurrentPot();
        } else {
            for (Pot p : potService.getClosedPots()) {
                if (p.getCreateTime().getTime() == potId) {
                    return p;
                }
            }
        }
        log.warn("Pot couldn't be found. Return current pot.");
        return potService.getCurrentPot();
    }

    public boolean isValidAddress(String depositAddress) {
        return true;
    }

    public Date getLastParticipantJoinTime() {
        return lastParticipantJoin;
    }

    @Override
    public String getDisplayableAmount(long amount) {
        return amount + " WEI";
    }

    @Override
    public String getQrCodeLink(String depositAddress) {
        return "https://chart.googleapis.com/chart?chs=250x250&cht=qr&chl=ethereum:"
                + depositAddress + "?amount="
                + getDisplayableAmount(getCurrentPot().getExpectedBettingAmount()).replace(" ETH",
                        "")
                + "&message=gambling";
    }

    @Override
    public abstract CryptoNetwork getCryptoNetwork();


    /**
     * In case of ethereum this method returns always the smart contract address.
     */
    @Override
    public String getDepositAddress() {
        return UserConfiguration.CONTRACT_ADDRESS;
    }

    public String getWalletAddress() {
        if (walletAddress == null) {
            walletAddress = getCredentials().getAddress();
        }
        return walletAddress;
    }

    private Credentials getCredentials() {
        if (credentials == null) {
            String walletFilename =
                    "ethereum-" + (getCryptoNetwork().isTestnet() ? "main" : "test") + ".json";
            String pathToWallet = CoreUtil.getWalletDirectory().getAbsolutePath() + File.separator
                    + walletFilename;
            log.debug("pathToWallet=" + pathToWallet);
            try {
                credentials = WalletUtils.loadCredentials(UserConfiguration.WALLET_CREDENTIAL,
                        pathToWallet);
            } catch (IOException | CipherException e) {
                log.error(e.getMessage(), e);
            }
        }
        return credentials;
    }

    @Override
    public List<ServiceInformation> getServiceInformations() {

        Web3j web3j = getWeb3jService();
        Web3ClientVersion web3ClientVersion;
        String clientVersion = null;
        String serviceState = "full node ";
        String netVersionString = null;
        int peerCount = 0;
        try {
            web3ClientVersion = web3j.web3ClientVersion().send();

            clientVersion = web3ClientVersion.getWeb3ClientVersion();
            NetPeerCount netPeerCount = web3j.netPeerCount().send();
            peerCount = netPeerCount.getQuantity().intValue();
            NetVersion netVersion = web3j.netVersion().send();
            netVersionString = netVersion.getNetVersion();
            serviceState += "online";
        } catch (IOException e) {
            e.printStackTrace();
            serviceState += "down";
        }
        List<ServiceInformation> result = new ArrayList<>();
        result.add(ServiceInformation.serviceState(serviceState));
        result.add(new ServiceInformation("clientVersion", clientVersion));
        result.add(new ServiceInformation("netVersion", netVersionString));
        result.add(new ServiceInformation("netPeerCount", String.valueOf(peerCount)));
        result.add(new ServiceInformation("walletAddress", getWalletAddress()));
        result.add(new ServiceInformation("walletBalance", getWalletBalance()));
        return result;
    }

    public String getWalletBalance() {
        try {
            EthGetBalance ethGetBalance = getWeb3jService()
                    .ethGetBalance(getWalletAddress(), DefaultBlockParameter.valueOf("latest"))
                    .send();
            return ethGetBalance.getBalance().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "? ETH";
    }

    @Override
    public String getSmartContractABI() {
        return "[{\"constant\":true,\"inputs\":[],\"name\":\"_message\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"depositAddresses\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"NBR_OF_SLOTS\",\"outputs\":[{\"name\":\"\",\"type\":\"uint8\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"closingBlockNumber\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_blockNumberOnPayout\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"PAYOUT_BLOCK_OFFSET\",\"outputs\":[{\"name\":\"\",\"type\":\"uint8\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"payout\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"payoutBlockNumber\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"EXPECTED_POT_AMOUNT\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_blockHashAsInteger\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"deposit\",\"outputs\":[],\"payable\":true,\"stateMutability\":\"payable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"potClosed\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"payoutBlockHash\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes32\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"winner\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"nbrOfMissedPayouts\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_payout\",\"type\":\"address\"}],\"name\":\"deposit\",\"outputs\":[],\"payable\":true,\"stateMutability\":\"payable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"payoutAddresses\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"nbrOfParticipants\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"from\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"to\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"Deposit\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"to\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"Payout\",\"type\":\"event\"}]";
    }

    @Override
    public long getExpectedBettingAmount() {
        return UserConfiguration.CONTRACT_EXPECTED_BETTING_AMOUNT;
    }

    public List<Participant> getPossibleParticipants() {
        // not used
        return new ArrayList<>();
    }

}

