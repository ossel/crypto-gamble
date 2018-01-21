package com.ossel.gamble.ethereum.misc;

import static org.junit.Assert.assertEquals;
import java.math.BigInteger;
import org.junit.Test;

public class Misc {

    @Test
    public void testNullBlockhash() throws Exception {
        assertEquals(0, new BigInteger("0000000000000000000000").intValue());
    }


}
