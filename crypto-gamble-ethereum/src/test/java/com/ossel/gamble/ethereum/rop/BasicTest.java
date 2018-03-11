package com.ossel.gamble.ethereum.rop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.web3j.crypto.Credentials;
import com.ossel.gamble.ethereum.RopstenTestcase;

public class BasicTest extends RopstenTestcase {


    @Test
    public void testWalletBallence() throws Exception {
        Credentials credentials = getCredentials();
        assertNotNull(credentials);
        assertEquals("0x2201f3919589b519135ce977cc0906c9481069b2", getWalletAddress());
        System.out.println(getWalletBalance());
    }

}
