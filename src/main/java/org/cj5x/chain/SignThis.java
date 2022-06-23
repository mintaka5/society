package org.cj5x.chain;

import java.security.*;

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
}
