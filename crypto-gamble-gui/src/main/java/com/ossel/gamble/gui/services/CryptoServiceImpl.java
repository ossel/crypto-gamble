package com.ossel.gamble.gui.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import com.ossel.gamble.core.data.Participant;
import com.ossel.gamble.core.data.Pot;
import com.ossel.gamble.core.data.ServiceInformation;
import com.ossel.gamble.core.data.enums.CryptoNetwork;
import com.ossel.gamble.core.service.CryptoNetworkService;

public class CryptoServiceImpl implements CryptoNetworkService {

    private static final Logger log = Logger.getLogger(CryptoServiceImpl.class);

    private CryptoNetwork cryptoNetwork;

    /**
     * Determines which CryptoService implementation should be used based on the
     * crypto_gamble_config properties.
     * 
     * @return the CryptoService implementation
     */
    public CryptoNetworkService lookupService() {
        if (cryptoNetwork == null) {
            cryptoNetwork = loadCryptoCurrencyProperie();
        }
        CryptoNetworkService service = null;
        try {
            service = (CryptoNetworkService) new InitialContext()
                    .lookup("java:module/" + cryptoNetwork.getNetworkServiceEjbName());

        } catch (NamingException e) {
            log.error("Coulnt lookup Service for " + cryptoNetwork.toString(), e);
        }
        if (service == null) {
            log.error(
                    "No CryptoNetworkService implementation found in /WEB-INF/lib. Please add dependency.");
        } else {
            log.debug(service.getCryptoNetwork().getCryptoCurrency().getShortName()
                    + " service lookup successfull");
        }
        return service;
    }

    private static CryptoNetwork loadCryptoCurrencyProperie() {
        Properties prop = new Properties();
        InputStream input = null;

        try {

            String filename = "crypto_gamble_config.properties";
            input = CryptoServiceImpl.class.getClassLoader().getResourceAsStream(filename);
            if (input == null) {
                log.error("Sorry, unable to find " + filename);
                return null;
            }
            prop.load(input);
            String networkString = prop.getProperty("app.cryptoNetwork");
            log.info("app.cryptoNetwork=" + networkString);
            return CryptoNetwork.valueOf(networkString);

        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public CryptoNetwork getCryptoNetwork() {
        return lookupService().getCryptoNetwork();
    }

    @Override
    public String getCurrentBlockHash() {
        return lookupService().getCurrentBlockHash();
    }

    @Override
    public int getCurrentBlockHeight() {
        return lookupService().getCurrentBlockHeight();
    }

    @Override
    public String getFreshDepositAddress() {
        return lookupService().getFreshDepositAddress();
    }

    @Override
    public boolean isValidAddress(String depositAddress) {
        return lookupService().isValidAddress(depositAddress);
    }

    @Override
    public Pot getCurrentPot() {
        return lookupService().getCurrentPot();
    }

    @Override
    public Pot getPotById(long potId) {
        return lookupService().getPotById(potId);
    }

    @Override
    public List<Pot> getClosedPots() {
        return lookupService().getClosedPots();
    }

    @Override
    public Participant addPossibleParticipant(String depositAddress) {
        return lookupService().addPossibleParticipant(depositAddress);
    }

    @Override
    public void updatePossibleParticipants(String depositAddress, String pseudonym,
            String payoutAddress) {
        lookupService().updatePossibleParticipants(depositAddress, pseudonym, payoutAddress);
    }

    @Override
    public String getDisplayableAmount(long receivedAmount) {
        return lookupService().getDisplayableAmount(receivedAmount);
    }

    @Override
    public String getQrCodeLink(String depositAddress) {
        return lookupService().getQrCodeLink(depositAddress);
    }

    @Override
    public List<ServiceInformation> getServiceInformations() {
        return lookupService().getServiceInformations();
    }

    @Override
    public String getSmartContractABI() {
        return lookupService().getSmartContractABI();
    }

}
