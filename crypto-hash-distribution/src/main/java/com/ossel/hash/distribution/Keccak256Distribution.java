package com.ossel.hash.distribution;


import java.math.BigInteger;
import com.ossel.gamble.ethereum.utility.EthereumUtil;
import fr.cryptohash.Keccak256;

public class Keccak256Distribution {


    public static void main(String[] args) {
        test();
    }

    public static void test() {
        int n = 1000000;
        System.out.println("Keccak256 monte carlo: n=" + n);
        int[] result = new int[10];
        for (int i = 0; i < n; i++) {
            String input = String.valueOf(i);
            Keccak256 h = new Keccak256();
            String hexHash = EthereumUtil.bytesToHex(h.digest(input.getBytes()));
            result[EthereumUtil.uint(hexHash).mod(BigInteger.TEN).intValue()]++;
        }
        System.out.println("#############################");

        for (int i = 0; i < result.length; i++) {
            System.out.println(i + ":" + result[i]);
        }
    }

}
