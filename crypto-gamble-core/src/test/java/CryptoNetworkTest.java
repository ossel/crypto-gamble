import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.ossel.gamble.core.data.enums.CryptoNetwork;


public class CryptoNetworkTest {

    @Test
    public void testEjbNamingConvention() {
        assertEquals("BtcMainEJB", CryptoNetwork.BTC_MAIN.getNetworkServiceEjbName());
        assertEquals("BtcTestEJB", CryptoNetwork.BTC_TEST.getNetworkServiceEjbName());
        assertEquals("DashMainEJB", CryptoNetwork.DASH_MAIN.getNetworkServiceEjbName());
        assertEquals("EthMainEJB", CryptoNetwork.ETH_MAIN.getNetworkServiceEjbName());
        assertEquals("EthRinkebyEJB", CryptoNetwork.ETH_RINKEBY.getNetworkServiceEjbName());
        assertEquals("EthRopstenEJB", CryptoNetwork.ETH_ROPSTEN.getNetworkServiceEjbName());
    }

    @Test
    public void testClassNameDollarRemoval() {
        assertTrue("BtcMainEJB$$$vie2".contains("$"));
        assertEquals("BtcMainEJB", "BtcMainEJB$$$vie2".split("\\$")[0]);
    }

}
