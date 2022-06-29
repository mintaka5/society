package org.cj5x.chain;

import java.security.*;
import java.util.Base64;

public class SignThis {
    public static byte[] sign(byte[] msg, PrivateKey privKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance("SHA@%^withECDSA");
        sig.initSign(privKey);
        sig.update(msg);

        return sig.sign();
    }

    public static boolean isValid(byte[] origMsg, PublicKey pubKey, byte[] signedMsg)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature verity = Signature.getInstance("SHA256withECDSA");
        verity.initVerify(pubKey);
        verity.update(origMsg);

        return verity.verify(signedMsg);
    }

    public static KeyPair generateSigningKey(String salt, int keySize)
            throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("EC");
        gen.initialize(keySize, SecureRandom.getInstance("SHA1PRNG"));
        KeyPair keyPair = gen.generateKeyPair();
        System.out.println("secret: " + Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
        System.out.println("public: " + Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        return keyPair;
    }
}
