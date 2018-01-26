package com.ossel.gamble.ethereum.misc;

import static org.junit.Assert.assertEquals;
import java.math.BigInteger;
import org.junit.Test;
import com.ossel.gamble.ethereum.utility.EthereumUtil;

public class Misc {

    @Test
    public void testNullBlockhash() throws Exception {
        assertEquals(0, new BigInteger("0000000000000000000000").intValue());
    }

    @Test
    public void testHexHashToUInt() throws Exception {
        assertEquals(
                "43010280667384004654441207986232896993269182950646931792897788114357618285314",
                EthereumUtil
                        .uint("5F16F4C7F149AC4F9510D9CF8CF384038AD348B3BCDC01915F95DE12DF9D1B02")
                        .toString());
        assertEquals(4,
                EthereumUtil
                        .uint("5F16F4C7F149AC4F9510D9CF8CF384038AD348B3BCDC01915F95DE12DF9D1B02")
                        .mod(BigInteger.TEN).intValue());
    }


}
