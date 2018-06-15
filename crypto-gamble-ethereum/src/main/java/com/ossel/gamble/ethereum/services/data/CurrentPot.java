package com.ossel.gamble.ethereum.services.data;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlock;
import com.ossel.gamble.core.data.Block;
import com.ossel.gamble.core.data.Participant;
import com.ossel.gamble.core.data.Pot;
import com.ossel.gamble.core.data.enums.CryptoCurrency;
import com.ossel.gamble.core.utils.CoreUtil;
import com.ossel.gamble.ethereum.UserConfiguration;
import com.ossel.gamble.ethereum.generated.TrustlessGambling;

public class CurrentPot extends ExpiryCacheValue {

    private static final Logger log = Logger.getLogger(CurrentPot.class);

    private static final String NA = "not accessible";

    private GasPrice gasPrice;
    private CurrentBlockHeight currentBlockHeight;
    private String contractAddress;

    private List<Pot> closedPots;

    public CurrentPot(Web3j web3j, Credentials credentials, GasPrice gasPrice,
            CurrentBlockHeight currentBlockHeight, String contractAddress) {
        super(web3j, credentials, 10 * SECOND);
        this.gasPrice = gasPrice;
        this.currentBlockHeight = currentBlockHeight;
        this.contractAddress = contractAddress;
        Pot currentPot = new Pot(CryptoCurrency.ETHEREUM,
                UserConfiguration.CONTRACT_EXPECTED_NBR_OF_PARTICIPANTS,
                UserConfiguration.CONTRACT_EXPECTED_BETTING_AMOUNT);
        this.value = currentPot;
        closedPots = new ArrayList<Pot>();
    }

    @Override
    protected void refresh() {
        log.info("### Refresh current pot[" + contractAddress + "] ###");
        TrustlessGambling contract = TrustlessGambling.load(contractAddress, getWeb3jService(),
                getCredentials(), gasPrice.getValue(),
                EMPTY_CONTRACT_CREATION_GAS.multiply(BigInteger.valueOf(100)));
        try {
            if (contract.isValid()) {
                Pot pot = (Pot) this.value;
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
                        log.info("Pot has been reopened.\nPayout blockhash of last pot = "
                                + payoutBlockhash + "\nwinner =" + winner);
                        BigInteger bi = BigInteger.ONE;
                        try {
                            bi = new BigInteger(payoutBlockhash);
                        } catch (NumberFormatException e) {

                        }
                        if (bi.intValue() == 0) {
                            pot.setState(
                                    "Pot closed. Payout() too late. The amount has been added to the next pot.");
                        } else {
                            Block block = new Block("0x" + payoutBlockhash.toLowerCase(), winner);
                            pot.setPayoutBlock(block);
                            pot.setWinner(winner);
                            pot.setState("Pot closed. Winner is " + pot.getWinner().getDisplayName()
                                    + ". Payout done.");
                        }

                        closedPots.add(pot);
                        // new pot opened
                        Pot newPot = new Pot(CryptoCurrency.ETHEREUM,
                                UserConfiguration.CONTRACT_EXPECTED_NBR_OF_PARTICIPANTS,
                                UserConfiguration.CONTRACT_EXPECTED_BETTING_AMOUNT);
                        newPot.setState(CoreUtil.getPotState(newPot));
                        this.value = newPot;

                    } else {
                        for (int i =
                                applicationNbrOfParticipants; i < contractNbrOfParticipants; i++) {
                            String depositAddress = getDepositAddress(contract, i);
                            String payoutAddress = getPayoutAddress(contract, i);
                            if (payoutAddress != null && depositAddress != null) {
                                Participant p = new Participant(depositAddress, payoutAddress);
                                p.setPotIndex(i);
                                p.setReceivedAmount(
                                        UserConfiguration.CONTRACT_EXPECTED_BETTING_AMOUNT);
                                pot.addParticipant(p);

                            } else {
                                log.info("Couldn't fetch participant " + i
                                        + " depositAddress or payoutAddress data.");
                            }
                        }
                        pot.setState(CoreUtil.getPotState(pot));
                        if (pot.isFull()) {
                            log.info("Pot full. Fetch payoutblocknumber.");
                            int closingBlockHeigth = getClosingBlocknumber(contract);
                            CoreUtil.closePot(pot, new Date(), fetchBlockHash(closingBlockHeigth),
                                    closingBlockHeigth);
                            pot.setState(CoreUtil.getPotState(pot) + " Call payout()");
                        }
                    }
                } else {
                    log.info("no new participants");
                    if (NA.equals(pot.getClosingBlockHash())) {
                        pot.setClosingBlockHash(fetchBlockHash(pot.getClosingBlockHeight()));
                    }
                    if (pot.getPayoutBlockHeight() != 0 && pot.getPayoutBlock() == null
                            && currentBlockHeight.getValue().intValue() >= pot
                                    .getPayoutBlockHeight()) {
                        // payout block has been mined
                        log.info("fetchPayoutBlockhash(" + pot.getPayoutBlockHeight() + ")");
                        String payoutBlockhash = fetchBlockHash(pot.getPayoutBlockHeight());
                        if (!NA.equals(payoutBlockhash)) {
                            log.info("Payout blockhash = " + payoutBlockhash);
                            pot.setPayoutBlock(new Block(payoutBlockhash.toLowerCase(), -1));
                        }
                    }
                    if (pot.getPayoutBlock() != null && pot.getPayoutBlock().getWinner() == -1) {
                        // payout block found but no one has called the payout() function yet
                        int diff = currentBlockHeight.getValue().intValue()
                                - pot.getPayoutBlockHeight();
                        int blocksLeft = (256 - diff); // solidity restriction
                        if (blocksLeft > 0) {
                            pot.setState("Pot closed. Call payout() during the next " + blocksLeft
                                    + " blocks. Otherwise the whole amount will be added to the next pot.");
                        } else {
                            pot.setState(
                                    "Pot closed. Payout() too late. The amount has been added to the next pot. Call payout() to open a new pot.");
                        }

                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String fetchBlockHash(int closingBlockHeigth) {
        EthBlock ethBlock = null;
        try {
            ethBlock = getWeb3jService().ethGetBlockByNumber(
                    new DefaultBlockParameterNumber(BigInteger.valueOf(closingBlockHeigth)), false)
                    .send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = NA;
        if (ethBlock != null && ethBlock.getBlock() != null) {
            result = ethBlock.getBlock().getHash();
        }
        return result;
    }


    private String fetchPayoutBlockhash(TrustlessGambling contract) {
        try {
            byte[] x = contract.payoutBlockHash().send();
            return javax.xml.bind.DatatypeConverter.printHexBinary(x);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "-1";
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
