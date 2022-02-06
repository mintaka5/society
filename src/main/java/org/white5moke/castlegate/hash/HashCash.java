package org.white5moke.castlegate.hash;

import io.leonard.Base58;
import org.apache.commons.lang3.RandomStringUtils;

import java.nio.charset.StandardCharsets;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HashCash {
    private static final String ALGO = "SHA1";
    private static final byte[] hashBuffer = new byte[20];
    private static MessageDigest messageDigest = null;
    private static int MAX_BITS = hashBuffer.length * Byte.SIZE;

    public HashCash() {}

    public static String generateRandomString() {
        return RandomStringUtils.randomAscii(10);
    }

    public static boolean isValid(String stamp) throws DigestException, NoSuchAlgorithmException {
        return validate(Integer.parseInt(stamp.split(":")[1]), stamp);
    }

    public static boolean validate(int numBits, String stamp) throws NoSuchAlgorithmException, DigestException {
        if(numBits > MAX_BITS) {
            throw new IllegalArgumentException(String.format("parameter numBits has a maximum size of %d", MAX_BITS));
        }

        if(messageDigest == null) {
            messageDigest = MessageDigest.getInstance(ALGO);
        }

        messageDigest.reset();
        messageDigest.update(stamp.getBytes(StandardCharsets.UTF_8));
        messageDigest.digest(hashBuffer, 0, hashBuffer.length);

        int i = 0;
        int total = 0;
        while(i < Math.floor(numBits / 8)) {
            total |= hashBuffer[i];
            i++;
        }

        if(numBits % 8 != 0) {
            total |= (hashBuffer[i] >> (Byte.SIZE - (numBits % Byte.SIZE)));
        }

        return total == 0;
    }

    public static String generate(int numBits, String resource) throws DigestException, NoSuchAlgorithmException {
        if(numBits > MAX_BITS) {
            throw new IllegalArgumentException(String.format("parameter numBits has a maximum size of %d", MAX_BITS));
        }

        String result = null;

        Date currentDate = new Date(System.currentTimeMillis());

        int version = 1;
        String date = null;
        {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddhhmmss");
            fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = fmt.format(currentDate);
        }

        String ext = "";
        String rando = generateRandomString();
        int counter = 1;

        while(result == null) {
            String stamp = String.format(
                    "%s:%s:%s:%s:%s:%s:%s", version, numBits, date, resource, ext,
                    Base58.encode(rando.getBytes(StandardCharsets.UTF_8)),
                    counter
            );

            if(validate(numBits, stamp)) {
                result = stamp;
                break;
            }

            counter++;
        }

        return result;
    }

    @Override
    public String toString() {
        return null;
    }
}
