package com.ossel.gamble.ethereum.misc;

import static org.junit.Assert.assertTrue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.junit.Test;
import com.ossel.gamble.ethereum.RinkebyTestcase;
import com.ossel.gamble.ethereum.generated.TrustlessGambling;
import rx.Subscription;

public class Web3jServiceTest extends RinkebyTestcase {


    // @Test
    public void testEthEstimateGas() throws Exception {
        assertTrue(getEmptyContractCreationGasAmount().signum() == 1);
    }

    // @Test
    public void testEthGasPrice() throws Exception {
        assertTrue(getGasPrice().signum() == 1);
    }


    // @Test
    public void testSubscription() {
        try {
            Subscription subscription = web3j.blockObservable(false).subscribe(block -> {
                System.out.println(block.getBlock().getHash());
            }, error -> {
                System.out.println("error: " + error.getMessage());
                error.printStackTrace();
            });
            System.out.println("subscribed");
            int c = 0;
            while (c != 10) {
                Thread.sleep(5000);
                System.out.println("waiting");
                c++;
            }
            subscription.unsubscribe();
            System.out.println("unsubscribed");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // @Test
    public void testTest() {
        try {
            Subscription subscription = web3j.blockObservable(false).subscribe(block -> {
                System.out.println(block.getBlock().getHash());
            }, error -> {
                System.out.println("error: " + error.getMessage());
                error.printStackTrace();
            });
            System.out.println("subscribed");
            int c = 0;
            while (c != 10) {
                Thread.sleep(5000);
                System.out.println("waiting");
                c++;
            }
            subscription.unsubscribe();
            System.out.println("unsubscribed");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * To get it to a java compatible sting goto text editor replace " with \" and remove spaces by
     * searching for the regular expression \s+ and replacing it with nothing.
     */
    @Test
    public void readContractABI() {
        String abiFileName = TrustlessGambling.class.getSimpleName() + ".abi";
        System.out.println("load " + abiFileName);
        ClassLoader classLoader = getClass().getClassLoader();
        StringBuilder b = new StringBuilder();
        BufferedReader br = null;
        try {
            File file = new File(classLoader.getResource(abiFileName).getFile());
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                b.append(line.replaceAll(" ", ""));
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(b.toString());
    }

}
