package org.white5moke.castlegate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;

public class RSA implements Encryption {
    public static final int KEYSIZE = 2048;

    @Override
    public String decrypt(byte[] enc, PublicKey pub) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher c = Cipher.getInstance(RSA);
        c.init(Cipher.DECRYPT_MODE, pub);

        byte[] r = c.doFinal(enc);

        return new String(r, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] encrypt(String msg, PrivateKey priv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher c = Cipher.getInstance(RSA);
        c.init(Cipher.ENCRYPT_MODE, priv);


        return c.doFinal(msg.getBytes(StandardCharsets.UTF_8));
    }

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        SecureRandom sr = new SecureRandom();

        KeyPairGenerator gen = KeyPairGenerator.getInstance(RSA);
        gen.initialize(KEYSIZE, sr);

        return gen.generateKeyPair();
    }
}
