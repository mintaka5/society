package org.white5moke.castlegate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

public interface Encryption {
    final String RSA = "RSA";
    final String ECDSA = "ECDSA";

    String decrypt(byte[] enc, PublicKey pub) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException;
    byte[] encrypt(String msg, PrivateKey priv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException;

    KeyPair generateKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
}
