package org.cj5x.chain;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

public class EncryptIt {
    public static byte[] encrypt(byte[] data, PublicKey pubKey) throws NoSuchPaddingException,
            NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher c = Cipher.getInstance("AES", "BC");
        c.init(Cipher.ENCRYPT_MODE, pubKey);

        return c.doFinal(data);
    }

    public static byte[] decrypt(byte[] encData, PrivateKey privKey) throws NoSuchPaddingException,
            NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher c = Cipher.getInstance("AES", "BC");
        c.init(Cipher.DECRYPT_MODE, privKey);

        return c.doFinal(encData);
    }
}
