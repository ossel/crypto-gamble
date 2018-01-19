package com.ossel.gamble.ethereum.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.math.BigInteger;
import org.junit.Test;
import org.web3j.crypto.Credentials;
import com.ossel.gamble.ethereum.RinkebyTestcase;
import com.ossel.gamble.ethereum.generated.Fibonacci;

public class FibonacciContractTest extends RinkebyTestcase {


    private String createFibonacciContract() throws Exception {
        Credentials credentials = getCredentials();
        assertNotNull(credentials);
        assertEquals("0x2201f3919589b519135ce977cc0906c9481069b2", getWalletAddress());
        Fibonacci contract = Fibonacci
                .deploy(web3j, credentials, getGasPrice(), getEmptyContractCreationGasAmount()
                        .multiply(BigInteger.TEN).multiply(BigInteger.TEN))
                .send();
        String address = contract.getContractAddress();
        // System.out.println("contract address"+address);
        return address;
    }

    // @Test
    public void testContractCreation() throws Exception {
        assertNotNull(createFibonacciContract());
    }

    @Test
    public void testContractInteraction() throws Exception {
        String fibonacciContractAddress = "0xde524f00ac62cea5a7da64be89758255d84529e8";
        Fibonacci contract = Fibonacci.load(fibonacciContractAddress, web3j, getCredentials(),
                getGasPrice(), getEmptyContractCreationGasAmount().multiply(BigInteger.TEN)
                        .multiply(BigInteger.TEN));
        assertTrue(contract.isValid());
        BigInteger x = contract.fibonacci(BigInteger.TEN).send();
        assertEquals(55, x.longValueExact());
    }

}
