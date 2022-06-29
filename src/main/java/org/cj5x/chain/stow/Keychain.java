package org.cj5x.chain.stow;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.PEMUtil;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfoBuilder;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcaPKCS8EncryptedPrivateKeyInfoBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEOutputEncryptorBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class Keychain {
    private Path path;
    private String password;

    public Keychain(Path p, String passwd) {
        Security.addProvider(new BouncyCastleProvider());

        this.path = p;
        this.password = passwd.strip();

        if(!Files.exists(this.path)) {
            try {
                make();
            } catch (IOException | NoSuchAlgorithmException | NoSuchProviderException
                     | OperatorCreationException e) {
                e.printStackTrace();
            }
        }


    }

    private void make() throws IOException, NoSuchAlgorithmException, NoSuchProviderException,
            OperatorCreationException {
        Files.createDirectories(this.path.getParent());
        Files.createFile(this.path);

        generate(2048);
    }

    private void generate(int keySize) throws NoSuchAlgorithmException,
            NoSuchProviderException, OperatorCreationException, IOException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA", "BC");
        gen.initialize(keySize, new SecureRandom());

        KeyPair pair = gen.generateKeyPair();
        PrivateKey privKey = pair.getPrivate();

        // encrypt key with password
        PKCS8EncryptedPrivateKeyInfoBuilder builder = new JcaPKCS8EncryptedPrivateKeyInfoBuilder(privKey);
        ASN1ObjectIdentifier m = PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC;
        JcePKCSPBEOutputEncryptorBuilder encBuilder = new JcePKCSPBEOutputEncryptorBuilder(m);
        OutputEncryptor outBuilder = encBuilder.build(this.password.toCharArray());
        PKCS8EncryptedPrivateKeyInfo privKeyInfo = builder.build(outBuilder);

        // send key to file
        BufferedWriter writer = Files.newBufferedWriter(this.path, StandardCharsets.UTF_8);
        PemWriter pem = new PemWriter(writer);
        pem.writeObject(new PemObject("RSA PRIVATE KEY", privKeyInfo.getEncoded()));
        pem.flush();
        pem.close();
    }

    public PrivateKey fromFile() throws IOException, PKCSException {
        BufferedReader reader = Files.newBufferedReader(this.path, StandardCharsets.UTF_8);
        PemReader pemRead = new PemReader(reader);

        PKCS8EncryptedPrivateKeyInfo privKeyInfo = new PKCS8EncryptedPrivateKeyInfo(pemRead.readPemObject().getContent());
        JcaPEMKeyConverter cp = new JcaPEMKeyConverter();
        cp.setProvider("BC");

        JcePKCSPBEInputDecryptorProviderBuilder decryptBuilder = new JcePKCSPBEInputDecryptorProviderBuilder();
        InputDecryptorProvider inBuilder = decryptBuilder.build(this.password.toCharArray());
        PrivateKeyInfo info = privKeyInfo.decryptPrivateKeyInfo(inBuilder);

        PrivateKey decodeKey = cp.getPrivateKey(info);

        return decodeKey;
    }
}
