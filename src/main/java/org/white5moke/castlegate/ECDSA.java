package org.white5moke.castlegate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;

public class ECDSA implements Encryption {
    @Override
    public String decrypt(byte[] enc, PublicKey pub) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return null;
    }

    @Override
    public byte[] encrypt(String msg, PrivateKey priv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return new byte[0];
    }

    @Override
    public KeyPair generateKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec curve = new ECGenParameterSpec("sepc256r1");
        gen.initialize(curve);

        KeyPair pair = generateKeyPair();

        return pair;
    }
}
