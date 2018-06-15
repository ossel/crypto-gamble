package com.ossel.gamble.ethereum.contract;

import java.math.BigInteger;
import org.junit.Test;
import com.ossel.gamble.ethereum.RinkebyTestcase;
import com.ossel.gamble.ethereum.generated.TrustlessGambling;

public class WinnerTest extends RinkebyTestcase {


    @Test
    public void testWinner() throws Exception {
        String contractAddress = "0x9b04da87b9bedafaee8f02c53bb5e290dce0cf2d";
        System.out.println("getEmptyContractCreationGasAmount: "
                + getEmptyContractCreationGasAmount().intValue());
        TrustlessGambling contract =
                TrustlessGambling.load(contractAddress, web3j, getCredentials(), getGasPrice(),
                        getEmptyContractCreationGasAmount().multiply(BigInteger.valueOf(100)));
        BigInteger x;
        String myAddress = getWalletAddress();
        x = contract.winner().send();
        System.out.println("Winner = " + x.intValue());

    }

}
