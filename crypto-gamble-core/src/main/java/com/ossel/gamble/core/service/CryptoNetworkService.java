package com.ossel.gamble.core.service;

import java.util.List;
import com.ossel.gamble.core.data.Participant;
import com.ossel.gamble.core.data.Pot;
import com.ossel.gamble.core.data.ServiceInformation;
import com.ossel.gamble.core.data.enums.CryptoNetwork;

public interface CryptoNetworkService {

    public final static String WALLET_DIRECTORY_NAME = "wallets";


    public List<ServiceInformation> getServiceInformations();

    public CryptoNetwork getCryptoNetwork();

    public String getCurrentBlockHash();

    public int getCurrentBlockHeight();

    public String getFreshDepositAddress();

    public boolean isValidAddress(String depositAddress);

    public Pot getCurrentPot();

    public Pot getPotById(long potId);

    public List<Pot> getClosedPots();

    public Participant addPossibleParticipant(String depositAddress);

    public void updatePossibleParticipants(String depositAddress, String pseudonym,
            String payoutAddress);

    public String getDisplayableAmount(long receivedAmount);

    public String getQrCodeLink(String depositAddress);

    public String getSmartContractABI();

}
