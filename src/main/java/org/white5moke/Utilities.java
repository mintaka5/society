package org.white5moke;

import org.apache.commons.codec.binary.Hex;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Utilities {
    public static String randomID(int bytLength) {
        SecureRandom r = null;
        try {
            r = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] rBs = new byte[bytLength];

        return Hex.encodeHexString(rBs);
    }
}
