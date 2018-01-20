package com.ossel.gamble.dash.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.KeyChain.KeyPurpose;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.Service.State;
import com.ossel.gamble.core.data.Participant;
import com.ossel.gamble.core.data.Pot;
import com.ossel.gamble.core.data.ServiceInformation;
import com.ossel.gamble.core.service.AbstractCryptoNetworkService;
import com.ossel.gamble.core.utils.CoreUtil;
import com.ossel.gamble.core.utils.TestUtil;
import com.ossel.gamble.dash.ejb.DashMainEJB;
import com.ossel.gamble.dash.listeners.CoinReceivedListener;
import com.ossel.gamble.dash.listeners.NewBlockListener;
import com.ossel.gamble.dash.threads.BankThread;
import com.ossel.gamble.dash.threads.PayoutThread;

public abstract class DashService extends AbstractCryptoNetworkService {

    /**
     * switch to true to add test data
     */
    public static final boolean DEVELOPMENT_MODE = false;

    /**
     * Each participant has to pay a least this amount of duffs to participate in the pot
     */
    public static final long EXPECTED_BETTING_AMOUNT = 100000L;

    /**
     * serves as wallet prefix
     */
    public static final String APP_NAME = "dash";

    private static final Logger log = Logger.getLogger(DashMainEJB.class);

    private static final String WALLET_FILE_NAME = APP_NAME.replaceAll("[^a-zA-Z0-9.-]", "_") + "-"
            + MainNetParams.get().getPaymentProtocolId();

    private WalletAppKit appKit;

    private Pot currentPot;

    private List<Pot> closedPots = new ArrayList<Pot>();

    private List<Pot> potsToPayout = new ArrayList<Pot>();

    private List<Participant> possibleParticipants = new ArrayList<Participant>();


    private CoinReceivedListener coinReceivedListener;

    private NewBlockListener newBlockListener;


    private BankThread bankThread;

    private Date lastParticipantJoin = new Date();

    @PostConstruct
    private void start() {
        log.info("#### " + DashMainEJB.class.getSimpleName() + " ####");
        currentPot = new Pot(getCryptoNetwork().getCryptoCurrency(), 2, EXPECTED_BETTING_AMOUNT);
        if (DEVELOPMENT_MODE)
            TestUtil.addTestData1(getCryptoNetwork(), EXPECTED_BETTING_AMOUNT, possibleParticipants,
                    currentPot, closedPots);
        try {
            if (appKit != null && appKit.isChainFileLocked()) {
                log.error("This application is already running and cannot be started twice.");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        if (appKit == null) {


            log.info("Dash wallet directory = " + CoreUtil.getWalletDirectory().getAbsolutePath());
            appKit = new WalletAppKit(MainNetParams.get(), CoreUtil.getWalletDirectory(),
                    WALLET_FILE_NAME) {
                @Override
                protected void onSetupCompleted() {
                    appKit.wallet().allowSpendingUnconfirmedTransactions();
                    State state = appKit.state();
                    System.out.println("onSetupCompleted state: " + state);
                }
            };
            DownloadProgressTracker tracker = new DownloadProgressTracker();
            appKit.setDownloadListener(tracker).setBlockingStartup(false).setUserAgent(APP_NAME,
                    "1.0");

            appKit.addListener(new Service.Listener() {
                @Override
                public void failed(Service.State from, Throwable failure) {
                    log.error(failure.getMessage(), failure);
                }
            }, new Executor() {

                @Override
                public void execute(Runnable command) {
                    log.info("Service Listener executor" + command);

                }
            });

            appKit.startAsync();
            log.info("Dash appkit started.");
            boolean stopWaiting = false;
            while (stopWaiting || !appKit.state().equals(State.RUNNING)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                    stopWaiting = true;
                }
            }
            if (appKit.state().equals(State.RUNNING)) {
                newBlockListener = new NewBlockListener(this);

                appKit.chain().addNewBestBlockListener(newBlockListener);
                log.info("NewBestBlockListener added to appKit");
            } else {
                log.error("Faild to add NewBestBlockListener to appKit");
            }
            coinReceivedListener = new CoinReceivedListener(this);
            appKit.wallet().addCoinsReceivedEventListener(coinReceivedListener);

            BankThread bankThread = new BankThread(this);
            bankThread.start();

        }

    }

    public WalletAppKit getAppKit() {
        return appKit;
    }

    public Pot getCurrentPot() {
        return currentPot;
    }

    /**
     * second step, triggered by the user
     * 
     * @param depositAddress
     * @param pseudonym
     * @param payoutAddress
     */
    public void updatePossibleParticipants(String depositAddress, String pseudonym,
            String payoutAddress) {
        for (Participant p : possibleParticipants) {
            if (p.getDepositAddress().equals(depositAddress)) {
                p.setPayoutAddress(payoutAddress);
                p.setPseudonym(pseudonym);
            }
        }

    }

    /**
     * first step, called by loading a new session page
     * 
     * @param depositAddress
     */
    public Participant addPossibleParticipant(String depositAddress) {
        Participant p = new Participant(depositAddress, null);
        possibleParticipants.add(p);
        return p;
    }

    public String getFreshDepositAddress() {
        return appKit.wallet().freshAddress(KeyPurpose.RECEIVE_FUNDS).toString();
    }

    public List<Participant> getPossibleParticipants() {
        return possibleParticipants;
    }

    public String getCurrentBlockHash() {
        return appKit.chain().getChainHead().getHeader().getHash().toString();
    }

    public int getCurrentBlockHeight() {
        return appKit.chain().getChainHead().getHeight();
    }

    public void closeCurrentPot(Date receiveTime) {
        CoreUtil.closePot(currentPot, receiveTime, getCurrentBlockHash(), getCurrentBlockHeight());
        closedPots.add(currentPot);
        currentPot = createNewPot();
    }

    private Pot createNewPot() {
        if (Math.random() > 0.3) {
            return new Pot(getCryptoNetwork().getCryptoCurrency(), 5, EXPECTED_BETTING_AMOUNT);
        } else {
            if (Math.random() > 0.7) {
                return new Pot(getCryptoNetwork().getCryptoCurrency(), 10, EXPECTED_BETTING_AMOUNT);
            } else {
                return new Pot(getCryptoNetwork().getCryptoCurrency(), 2, EXPECTED_BETTING_AMOUNT);
            }
        }
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

    public void scheduledPayout(Pot pot) {
        potsToPayout.add(pot);
    }

    public void startPayoutBatch() {
        log.info("start payout threads");
        int c = 0;
        for (Pot pot : potsToPayout) {
            if (!pot.isPayoutStarted()) {
                PayoutThread p = new PayoutThread(this, pot);
                p.start();
                c++;
            }
        }
        log.info(c + " payout threads started.");
    }

    public List<Pot> getPotsToPayout() {
        return potsToPayout;
    }

    /**
     * to be fired by a coin received listener
     */
    public void paymentReceived() {
        lastParticipantJoin = new Date();
    }

    public void addBankParticipant() {
        Participant bankParticipant =
                new Participant("No deposit needed.", "No payout address needed.");
        bankParticipant.setPseudonym(getBankPseudonym());
        bankParticipant.setReceivedAmount(currentPot.getExpectedBettingamount());
        bankParticipant.setBankParticipant(true);
        log.info("Add bank participant " + bankParticipant + " to the current pot.");
        paymentReceived();
        possibleParticipants.add(bankParticipant);
        bankParticipant.setPotIndex(currentPot.getNbrOfParticipants());
        currentPot.addParticipant(bankParticipant);
        if (currentPot.isFull()) {
            this.closeCurrentPot(new Date());
            log.info("Pot" + currentPot.getCreateTime().getTime()
                    + " is full because a bank participant has been joined.");
        }

    }

    private String getBankPseudonym() {
        switch (currentPot.getNbrOfOpenSlots()) {
            case 1:
                return "David Rockefeller";
            case 2:
                return "Mario Draghi";
            case 3:
                return "Janet Yellen";
            case 4:
                return "Helicopter Ben";
            default:
                return "Helicopter Ben";
        }
    }

    public Date getLastParticipantJoinTime() {
        return lastParticipantJoin;
    }

    public void setLastParticipantJoin(Date lastParticipantJoin) {
        this.lastParticipantJoin = lastParticipantJoin;
    }

    @Override
    public String getDisplayableAmount(long receivedAmount) {
        return Coin.SATOSHI.multiply(receivedAmount).toFriendlyString();
    }

    @Override
    public String getQrCodeLink(String depositAddress) {
        return "https://chart.googleapis.com/chart?chs=250x250&cht=qr&chl=dash:" + depositAddress
                + "?amount=" + getDisplayableAmount(getCurrentPot().getExpectedBettingamount())
                        .replace(" DASH", "")
                + "&message=trustless-gambling";
    }


    @Override
    public List<ServiceInformation> getServiceInformations() {
        List<ServiceInformation> result = new ArrayList<>();
        result.add(ServiceInformation.walletVersion(String.valueOf(appKit.wallet().getVersion())));
        result.add(ServiceInformation.serviceState(appKit.state().toString()));
        return result;
    }

    @Override
    public String getSmartContractABI() {
        return "";// not applicable for dash
    }
}
