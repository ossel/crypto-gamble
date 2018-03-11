package com.ossel.gamble.bitcoin.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.AbstractBitcoinNetParams;
import org.bitcoinj.wallet.KeyChain.KeyPurpose;
import com.google.common.util.concurrent.Service.State;
import com.ossel.gamble.bitcoin.listeners.CoinsReceivedListener;
import com.ossel.gamble.bitcoin.listeners.NewBlockListener;
import com.ossel.gamble.core.data.Participant;
import com.ossel.gamble.core.data.Pot;
import com.ossel.gamble.core.data.ServiceInformation;
import com.ossel.gamble.core.service.AbstractCryptoNetworkService;
import com.ossel.gamble.core.utils.CoreUtil;

public abstract class AbstractBitcoinService extends AbstractCryptoNetworkService {

    private static final Logger log = Logger.getLogger(AbstractBitcoinService.class);

    public final String APP_NAME = "bitcoin";

    private final String WALLET_FILE_NAME = APP_NAME.replaceAll("[^a-zA-Z0-9.-]", "_") + "-"
            + getNetworkParams().getPaymentProtocolId();

    private WalletAppKit bitcoinAppKit;

    private Pot currentPot;
    private List<Pot> closedPots = new ArrayList<Pot>();

    private List<Pot> potsToPayout = new ArrayList<Pot>();

    private List<Participant> possibleParticipants = new ArrayList<Participant>();

    private static final long EXPECTED_BETTING_AMOUNT = 100000L;

    private CoinsReceivedListener coinReceivedListener;
    private NewBlockListener newBlockListener;

    public abstract AbstractBitcoinNetParams getNetworkParams();

    @PostConstruct
    private void startSvpNode() {
        log.info("#### Start Bitcoin SPV Node  ####");
        currentPot = new Pot(getCryptoNetwork().getCryptoCurrency(), 2, EXPECTED_BETTING_AMOUNT);
        currentPot.setState(CoreUtil.getPotState(currentPot));
        log.info("Bitcoin wallet directory = " + CoreUtil.getWalletDirectory().getAbsolutePath());
        bitcoinAppKit = new WalletAppKit(getNetworkParams(), CoreUtil.getWalletDirectory(),
                WALLET_FILE_NAME) {
            @Override
            protected void onSetupCompleted() {
                log.info("#### Bitcoin SPV Node [" + bitcoinAppKit.state() + "] ####");
            }
        };

        bitcoinAppKit.startAsync();
        waitUntilStarted(bitcoinAppKit);
        log.info("Bitcoin appkit started.");
        newBlockListener = new NewBlockListener(this);
        bitcoinAppKit.chain().addNewBestBlockListener(newBlockListener);
        coinReceivedListener = new CoinsReceivedListener(this);
        bitcoinAppKit.wallet().addCoinsReceivedEventListener(coinReceivedListener);
        log.info("#### startup finished  ####");
    }

    private void waitUntilStarted(WalletAppKit appkit) {
        while (!State.RUNNING.equals(appkit.state())) {
            log.info("Wait for 1 more second...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
                break;
            }
        }
    }

    public WalletAppKit getAppKit() {
        return bitcoinAppKit;
    }

    public Pot getCurrentPot() {
        return currentPot;
    }

    public String getDepositAddress() {
        String depositAddress =
                bitcoinAppKit.wallet().freshAddress(KeyPurpose.RECEIVE_FUNDS).toString();
        possibleParticipants.add(new Participant(depositAddress, null));
        return depositAddress;
    }

    public List<Participant> getPossibleParticipants() {
        return possibleParticipants;
    }

    public String getCurrentBlockHash() {
        return bitcoinAppKit.chain().getChainHead().getHeader().getHash().toString();
    }

    public int getCurrentBlockHeight() {
        return bitcoinAppKit.chain().getChainHead().getHeight();
    }

    public void closeCurrentPot(Date receiveTime) {
        log.info("Pot " + currentPot.getCreateTime().getTime() + " was full and has been closed.");
        CoreUtil.closePot(currentPot, receiveTime, getCurrentBlockHash(), getCurrentBlockHeight());
        closedPots.add(currentPot);
        currentPot = createNewPot();
    }

    public List<Pot> getClosedPots() {
        return closedPots;
    }

    public List<Pot> getClosedUnfinishedPots() {
        List<Pot> unfinishedPots = new ArrayList<Pot>();
        for (Pot pot : closedPots) {
            if (!pot.payoutFinished()) {
                unfinishedPots.add(pot);
            }
        }
        return unfinishedPots;
    }

    /**
     * 
     * @param potId
     * @return null if not found
     */
    public Pot getPotById(long potId) {
        if (currentPot.getId() == potId) {
            return currentPot;
        }
        for (Pot pot : closedPots) {
            if (pot.getId() == potId) {
                return pot;
            }
        }
        return null;
    }

    public boolean isValidAddress(String depositAddress) {
        try {
            return depositAddress != null && depositAddress.startsWith("X")
                    && depositAddress.length() > 20 && depositAddress.length() < 40;
        } catch (AddressFormatException e) {
            log.debug(e.getMessage());
            return false;
        }

    }

    public List<Pot> getPotsToPayout() {
        return potsToPayout;
    }


    public void addBankParticipant() {
        Participant bankParticipant =
                new Participant("No deposit needed.", "No payout address needed.");
        bankParticipant.setPseudonym(getBankPseudonym());
        bankParticipant.setReceivedAmount(currentPot.getExpectedBettingamount());
        bankParticipant.setBankParticipant(true);
        log.info("Add bank participant " + bankParticipant + " to the current pot.");
        // TODO check paymentReceived();
        possibleParticipants.add(bankParticipant);
        bankParticipant.setPotIndex(currentPot.getNbrOfParticipants());
        currentPot.addParticipant(bankParticipant);
        if (currentPot.isFull()) {
            this.closeCurrentPot(new Date());
            log.info("Pot" + currentPot.getCreateTime().getTime()
                    + " is full because a bank participant has been joined.");
        }
        currentPot.setState(CoreUtil.getPotState(currentPot));

    }

    @Override
    public String getDisplayableAmount(long receivedAmount) {
        return Coin.SATOSHI.multiply(receivedAmount).toFriendlyString();
    }

    @Override
    public String getQrCodeLink(String depositAddress) {
        return "https://chart.googleapis.com/chart?chs=250x250&cht=qr&chl=bitcoin:" + depositAddress
                + "?amount=" + getDisplayableAmount(getCurrentPot().getExpectedBettingamount())
                        .replace(" BTC", "")
                + "&message=gambling";
    }

    @Override
    public List<ServiceInformation> getServiceInformations() {
        List<ServiceInformation> result = new ArrayList<>();
        result.add(ServiceInformation
                .walletVersion(String.valueOf(bitcoinAppKit.wallet().getVersion())));
        result.add(ServiceInformation.serviceState(bitcoinAppKit.state().toString()));
        return result;
    }

    @Override
    public String getSmartContractABI() {
        return "";// not applicable for bitcoin
    }

    @Override
    public long getExpectedBettingAmount() {
        return EXPECTED_BETTING_AMOUNT;
    }

}
