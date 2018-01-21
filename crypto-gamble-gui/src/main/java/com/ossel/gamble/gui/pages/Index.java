package com.ossel.gamble.gui.pages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import com.ossel.gamble.core.data.Participant;
import com.ossel.gamble.core.data.Pot;
import com.ossel.gamble.core.data.enums.CryptoCurrency;
import com.ossel.gamble.core.data.enums.CryptoNetwork;
import com.ossel.gamble.core.service.CryptoNetworkService;
import com.ossel.gamble.gui.services.PriceService;

/**
 * Start page of application.
 */
public class Index {

    private static final Logger log = Logger.getLogger(Index.class);

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    @Inject
    private AlertManager alertManager;

    @Inject
    private PriceService priceService;

    @Inject
    private CryptoNetworkService service;

    @Property
    @Persist
    private String depositAddress;

    @Property
    @Persist
    private String smartContractAbi;

    @Property
    @Persist
    private Participant deposit;

    @Property
    @Persist
    private List<Participant> myDeposits;

    @Property
    @Persist
    private Integer maxSeenBlockHeight;

    @Property
    @Persist
    private boolean showPaymentForm;

    @Persist
    private CryptoNetwork network;

    public CryptoNetwork getCryptoNetwork() {
        if (network == null) {
            network = service.getCryptoNetwork();
        }
        return network;
    }


    @SetupRender
    public void setupPage() {

        updateNewBlockAlerts();
        if (myDeposits == null) {
            myDeposits = new ArrayList<Participant>();
        }
        if (smartContractAbi == null) {
            smartContractAbi = service.getSmartContractABI();
        }

    }

    public String getCurrentBockHash() {
        return service.getCurrentBlockHash();
    }

    public int getCurrentBockHeight() {
        return service.getCurrentBlockHeight();
    }

    public String getLinkToDepositAddress() {
        return network.getExplorerLinkToAddress(depositAddress);
    }

    public String getCurrentBockHashExplorerLink() {
        return getCryptoNetwork().getExplorerLinkToBlock(getCurrentBockHash());
    }

    private void updateNewBlockAlerts() {
        if (maxSeenBlockHeight == null) {
            maxSeenBlockHeight = service.getCurrentBlockHeight();
        } else {
            int currentBlockHeigth = service.getCurrentBlockHeight();
            int diff = currentBlockHeigth - maxSeenBlockHeight;
            if (diff > 5) {
                diff = 5; // never show more than 5 alerts
            }
            int statValue = currentBlockHeigth - diff;
            for (int i = 1; i <= diff; i++) {
                alertManager.info("Block <" + (statValue + i) + "> has been found!");
            }
            maxSeenBlockHeight = currentBlockHeigth;
        }
    }

    public String getCurrentReceivingAddressLink() {
        return service.getQrCodeLink(depositAddress);
    }


    @Component
    private Zone dataZone;

    public Block onUpdateDataZone() {
        log.debug("onUpdateDataZone");
        // updateNewBlockAlerts();
        return dataZone.getBody();
    }

    @Component
    private Zone headerZone;

    public Block onUpdateHeaderZone() {
        log.debug("onUpdateHeaderZone");
        updateNewBlockAlerts();
        return headerZone.getBody();
    }

    public String getTime() {
        return DATE_FORMAT.format(new Date());
    }

    @InjectComponent
    private Form addPayoutAddress;

    @Property
    private String payoutAddress;

    @Property
    private String pseudonym;

    void onValidateFromAddPayoutAddress() {
        log.info(">onValidateFromAddPayoutAddressForm");
        if (payoutAddress != null && payoutAddress != ""
                && !service.isValidAddress(payoutAddress)) {
            alertManager.error("Not a valid " + getCryptoNetwork().getCryptoCurrency().getCode()
                    + " address!");
            addPayoutAddress.recordError("Not a valid "
                    + getCryptoNetwork().getCryptoCurrency().getCode() + " address!");
        }
        log.info("<onValidateFromAddPayoutAddressForm");
    }

    Object onSuccessFromAddPayoutAddress() {
        log.info("onSuccessFromAddPayoutAddressForm!");
        showPaymentForm = false;
        service.updatePossibleParticipants(depositAddress, pseudonym, payoutAddress);
        alertManager.success(
                "You get added to the current pot soon as your transaction gets included into the blockchain.");
        depositAddress = null; // generate new one
        return null;
    }

    public Pot getCurrentPot() {
        return service.getCurrentPot();
    }

    @Property
    private Pot closedPot;

    public List<Pot> getClosedPots() {
        List<Pot> pots = service.getClosedPots();
        Collections.sort(pots, new Comparator<Pot>() {
            @Override
            public int compare(Pot o1, Pot o2) {
                return (int) (o2.getId() - o1.getId());
            }
        });
        return pots.subList(0, Math.min(pots.size(), 10));// display max 10 pots
    }

    public String getPrice() {
        return priceService.getPrice(getCryptoNetwork().getCryptoCurrency());

    }

    void onActionFromJoinPot() {
        showPaymentForm = true;
        if (depositAddress == null) {
            depositAddress = service.getFreshDepositAddress();
            Participant p = service.addPossibleParticipant(depositAddress);
            myDeposits.add(p);
        }
    }

    // void onActionFromRefresh() {
    // log.debug("onActionFromRefresh");
    // }

    public boolean isShowDeposits() {
        return myDeposits != null && myDeposits.size() > 0 && !isEthereum();
    }

    public boolean isEthereum() {
        return CryptoCurrency.ETHEREUM.equals(network.getCryptoCurrency());
    }

    public String getSubmitLabel() {
        return isEthereum() ? "Close" : "Sent!";
    }


    public boolean isShowClosedPots() {
        return getClosedPots().size() > 0;
    }
    //
    // public String getPossibleBlockHashValues() {
    // final String values = "0123456789ABCDEF??????????";
    // return isEthereum() ? values.toLowerCase() : values;
    // }
    //
    // public String getPossibleBlockHashPrefix() {
    // return isEthereum() ? "0x0?0?0?0?0?0?0?" : "0000000000000";
    // }



}
