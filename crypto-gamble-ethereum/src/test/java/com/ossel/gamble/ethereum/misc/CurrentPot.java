package com.ossel.gamble.ethereum.misc;

import java.io.IOException;
import java.math.BigInteger;
import org.apache.log4j.Logger;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlock;
import com.ossel.gamble.core.data.Block;
import com.ossel.gamble.core.data.Participant;
import com.ossel.gamble.core.data.Pot;
import com.ossel.gamble.core.data.enums.CryptoCurrency;
import com.ossel.gamble.ethereum.generated.TrustlessGambling;
import com.ossel.gamble.ethereum.services.data.CurrentBlockHeight;
import com.ossel.gamble.ethereum.services.data.ExpiryCacheValue;
import com.ossel.gamble.ethereum.services.data.GasPrice;

public class CurrentPot extends ExpiryCacheValue {

    private static final Logger log = Logger.getLogger(CurrentPot.class);

    private static final String NA = "not accessible";

    private GasPrice gasPrice;
    private CurrentBlockHeight currentBlockHeight;
    private String contractAddress;


    public CurrentPot(Web3j web3j, Credentials credentials, GasPrice gasPrice,
            CurrentBlockHeight currentBlockHeight, String contractAddress) {
        super(web3j, credentials, 10 * SECOND);
        this.gasPrice = gasPrice;
        this.currentBlockHeight = currentBlockHeight;
        this.contractAddress = contractAddress;
        try {
            this.value = createEmptyPot();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private Pot createEmptyPot() throws Exception {
        TrustlessGambling contract = TrustlessGambling.load(contractAddress, getWeb3jService(),
                getCredentials(), gasPrice.getValue(), BigInteger.valueOf(5300000));
        int nbrOfSlots = contract.NBR_OF_SLOTS().send().intValue();
        long amount = contract.EXPECTED_POT_AMOUNT().send().longValue();
        return new Pot(CryptoCurrency.ETHEREUM, nbrOfSlots, amount);
    }

    @Override
    protected void refresh() {
        try {
            log.info("### Refresh current pot[" + contractAddress + "] ###");
            TrustlessGambling contract = TrustlessGambling.load(contractAddress, getWeb3jService(),
                    getCredentials(), gasPrice.getValue(),
                    EMPTY_CONTRACT_CREATION_GAS.multiply(BigInteger.valueOf(100)));
            Pot pot = (Pot) this.value;
            int actualParaticipants = contract.nbrOfParticipants().send().intValue();
            int potParticipants = pot.getNbrOfParticipants();
            if (actualParaticipants < potParticipants) {
                // pot has been reopened
                int winner = contract.winner().send().intValue();
                byte[] hashBytes = contract.payoutBlockHash().send();
                String payoutBlockhash = javax.xml.bind.DatatypeConverter.printHexBinary(hashBytes);
                if (new BigInteger(payoutBlockhash).intValue() == 0) {
                    pot.setState(
                            "Pot closed. Payout() too late. The amount has been added to the next pot.");
                } else {
                    Block block = new Block("0x" + payoutBlockhash.toLowerCase(), winner);
                    pot.setPayoutBlock(block);
                    pot.setWinner(winner);
                    pot.setState("Pot closed. Winner is " + pot.getWinner().getPayoutAddress());
                }
                this.value = createEmptyPot();

            } else {
                for (int i = potParticipants; i < actualParaticipants; i++) {
                    String depositAddress = getDepositAddress(contract, i);
                    String payoutAddress = getPayoutAddress(contract, i);
                    pot.addParticipant(new Participant(depositAddress, payoutAddress));
                }
            }

            boolean potClosed = contract.potClosed().send();
            if (potClosed) {
                int closingBlockNumber = contract.closingBlockNumber().send().intValue();
                int payoutOffset = contract.PAYOUT_BLOCK_OFFSET().send().intValue();
                DefaultBlockParameterNumber param =
                        new DefaultBlockParameterNumber(BigInteger.valueOf(closingBlockNumber));
                String closingBlockHash = getWeb3jService().ethGetBlockByNumber(param, false).send()
                        .getBlock().getHash();
                pot.setClosingBlockHash(closingBlockHash);
                pot.setClosingBlockHeight(closingBlockNumber);
                pot.setPayoutBlockHeight(closingBlockNumber + payoutOffset);
                int currentBlockHeightValue = currentBlockHeight.getValue().intValue();
                if (pot.getPayoutBlockHeight() > currentBlockHeightValue) {
                    pot.setState("Pot closed. Waiting for payout block.");
                } else {
                    int diff = currentBlockHeightValue - pot.getPayoutBlockHeight();
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



    public Pot getCurrentPot() {
        return (Pot) super.getValue();
    }



    private void collectData() throws Exception {
        TrustlessGambling contract = TrustlessGambling.load(contractAddress, getWeb3jService(),
                getCredentials(), gasPrice.getValue(),
                EMPTY_CONTRACT_CREATION_GAS.multiply(BigInteger.valueOf(100)));
        long amount = contract.EXPECTED_POT_AMOUNT().send().longValue();
        int nbrOfSlots = contract.NBR_OF_SLOTS().send().intValue();
        Pot pot = new Pot(CryptoCurrency.ETHEREUM, nbrOfSlots, amount);
        int participants = contract.nbrOfParticipants().send().intValue();
        for (int i = 0; i < participants; i++) {
            String depositAddress = contract.depositAddresses(BigInteger.valueOf(i)).send();
            String payoutAddress = contract.depositAddresses(BigInteger.valueOf(i)).send();
            pot.addParticipant(new Participant(depositAddress, payoutAddress));
        }
        boolean potClosed = contract.potClosed().send();
        if (potClosed) {
            // payout needs to be called
            int closingBlockNumber = contract.closingBlockNumber().send().intValue();
            int payoutBlockNumber = contract.payoutBlockNumber().send().intValue();
        } else {

        }

    }

}
