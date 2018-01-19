package com.ossel.gamble.ethereum.services.data;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import com.ossel.gamble.core.data.Block;
import com.ossel.gamble.core.data.Participant;
import com.ossel.gamble.core.data.Pot;
import com.ossel.gamble.core.data.enums.CryptoCurrency;
import com.ossel.gamble.core.utils.CoreUtil;
import com.ossel.gamble.ethereum.UserConfiguration;
import com.ossel.gamble.ethereum.data.EthPot;
import com.ossel.gamble.ethereum.generated.TrustlessGambling;

public class PotService extends ExpiryCacheValue {

    private static final Logger log = Logger.getLogger(PotService.class);

    private GasPrice gasPrice;
    private String contractAddress;

    private List<Pot> closedPots;

    public PotService(Web3j web3j, Credentials credentials, GasPrice gasPrice,
            String contractAddress) {
        super(web3j, credentials, 10 * SECOND);
        this.gasPrice = gasPrice;
        this.contractAddress = contractAddress;
        EthPot currentPot = new EthPot(CryptoCurrency.ETHEREUM,
                UserConfiguration.CONTRACT_EXPECTED_NBR_OF_PARTICIPANTS,
                UserConfiguration.CONTRACT_EXPECTED_BETTING_AMOUNT);
        currentPot.setMessage("");
        this.value = currentPot;
        closedPots = new ArrayList<Pot>();
    }

    @Override
    protected void refresh() {
        log.info("### Refresh current pot[" + contractAddress + "] ###");
        TrustlessGambling contract = TrustlessGambling.load(contractAddress, getService(),
                getCredentials(), gasPrice.getValue(),
                EMPTY_CONTRACT_CREATION_GAS.multiply(BigInteger.valueOf(100)));
        try {
            if (contract.isValid()) {
                EthPot pot = (EthPot) this.value;
                int contractNbrOfParticipants = contract.nbrOfParticipants().send().intValue();
                int applicationNbrOfParticipants = pot.getNbrOfParticipants();
                log.info("contract nbrOfParticipants=" + contractNbrOfParticipants
                        + " application pot nbrOfParticipants=" + applicationNbrOfParticipants);
                if (applicationNbrOfParticipants != contractNbrOfParticipants) {
                    log.info("lets get the new values");
                    if (contractNbrOfParticipants < applicationNbrOfParticipants) {
                        // pot has been reopened
                        int winner = fetchWinner(contract);
                        String payoutBlockhash = fetchPayoutBlockhash(contract);
                        Block block = new Block(payoutBlockhash, winner);
                        pot.setFinalPayoutBlock(block);
                        pot.setMessage("Payout done.");
                        closedPots.add(pot);
                        // new pot opened
                        this.value = new EthPot(CryptoCurrency.ETHEREUM,
                                UserConfiguration.CONTRACT_EXPECTED_NBR_OF_PARTICIPANTS,
                                UserConfiguration.CONTRACT_EXPECTED_BETTING_AMOUNT);
                    } else {
                        for (int i =
                                applicationNbrOfParticipants; i < contractNbrOfParticipants; i++) {
                            String depositAddress = getDepositAddress(contract, i);
                            String payoutAddress = getPayoutAddress(contract, i);
                            if (payoutAddress != null && depositAddress != null) {
                                Participant p = new Participant(depositAddress, payoutAddress);
                                p.setReceivedAmount(
                                        UserConfiguration.CONTRACT_EXPECTED_BETTING_AMOUNT);
                                pot.addParticipant(p);

                            } else {
                                log.info("Couldn't fetch participant " + i
                                        + " depositAddress or payoutAddress data.");
                            }
                        }
                        if (pot.isFull()) {
                            log.info("Pot full. Fetch payoutblocknumber.");
                            pot.setMessage("call payout()");
                            CoreUtil.closePot(pot, new Date(), "not accessible",
                                    getClosingBlocknumber(contract));
                        }
                    }
                } else {
                    log.info("no new participants");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String fetchPayoutBlockhash(TrustlessGambling contract) {
        try {
            byte[] x = contract.payoutBlockHash().send();
            return javax.xml.bind.DatatypeConverter.printHexBinary(x);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "error";
    }

    private String getDepositAddress(TrustlessGambling contract, int number) {
        try {
            return contract.depositAddresses(BigInteger.valueOf(number)).send();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private String getPayoutAddress(TrustlessGambling contract, int number) {
        try {
            return contract.payoutAddresses(BigInteger.valueOf(number)).send();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private int getClosingBlocknumber(TrustlessGambling contract) {
        try {
            BigInteger x = contract.closingBlockNumber().send();
            return x.intValue();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return -1;
    }

    private int fetchWinner(TrustlessGambling contract) {
        try {
            BigInteger x = contract.winner().send();
            return x.intValue();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return -1;
    }

    public Pot getCurrentPot() {
        return (Pot) super.getValue();
    }

    public List<Pot> getClosedPots() {
        return closedPots;
    }

}
