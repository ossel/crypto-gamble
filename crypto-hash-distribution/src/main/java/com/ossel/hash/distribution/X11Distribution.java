package com.ossel.hash.distribution;


import org.bitcoinj.core.Sha256Hash;
import com.hashengineering.crypto.Sha512Hash;
import com.ossel.gamble.core.data.ExtendedBlock;
import fr.cryptohash.BLAKE512;
import fr.cryptohash.BMW512;
import fr.cryptohash.CubeHash512;
import fr.cryptohash.ECHO512;
import fr.cryptohash.Groestl512;
import fr.cryptohash.JH512;
import fr.cryptohash.Keccak512;
import fr.cryptohash.Luffa512;
import fr.cryptohash.SHAvite512;
import fr.cryptohash.SIMD512;
import fr.cryptohash.Skein512;

public class X11Distribution {

    static Sha256Hash x11(byte header[]) {
        // Initialize
        Sha512Hash[] hash = new Sha512Hash[11];

        // Run the chain of algorithms
        BLAKE512 blake512 = new BLAKE512();
        hash[0] = new Sha512Hash(blake512.digest(header));

        BMW512 bmw = new BMW512();
        hash[1] = new Sha512Hash(bmw.digest(hash[0].getBytes()));

        Groestl512 groestl = new Groestl512();
        hash[2] = new Sha512Hash(groestl.digest(hash[1].getBytes()));

        Skein512 skein = new Skein512();
        hash[3] = new Sha512Hash(skein.digest(hash[2].getBytes()));

        JH512 jh = new JH512();
        hash[4] = new Sha512Hash(jh.digest(hash[3].getBytes()));

        Keccak512 keccak = new Keccak512();
        hash[5] = new Sha512Hash(keccak.digest(hash[4].getBytes()));

        Luffa512 luffa = new Luffa512();
        hash[6] = new Sha512Hash(luffa.digest(hash[5].getBytes()));

        CubeHash512 cubehash = new CubeHash512();
        hash[7] = new Sha512Hash(cubehash.digest(hash[6].getBytes()));

        SHAvite512 shavite = new SHAvite512();
        hash[8] = new Sha512Hash(shavite.digest(hash[7].getBytes()));

        SIMD512 simd = new SIMD512();
        hash[9] = new Sha512Hash(simd.digest(hash[8].getBytes()));

        ECHO512 echo = new ECHO512();
        hash[10] = new Sha512Hash(echo.digest(hash[9].getBytes()));

        return hash[10].trim256();
    }

    public void test() {
        // ECHO512(SIMD512(SHAvite512(CubeHash512(Luffa512(Keccak512(JH512(Skein512(Groestl512(BMW512(BLAKE512(block_header)))))))))))
        int n = 1000000;
        System.out.println("x11 monte carlo: n=" + n);
        int[] result = new int[10];
        for (int i = 0; i < n; i++) {
            String input = Math.random() + "" + i;
            Sha256Hash hash = x11(input.getBytes());
            int winner = new ExtendedBlock(hash.toString()).getWinner();
            // System.out.println(input + " -> " + hash.toString() + " : " + winner);
            result[winner]++;
        }
        System.out.println("#############################");

        for (int i = 0; i < result.length; i++) {
            System.out.println(i + ":" + result[i]);
        }
    }

}
