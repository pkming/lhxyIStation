package android.security;

import android.security.KeyStore;
import com.android.org.bouncycastle.x509.X509V3CertificateGenerator;
import com.android.org.conscrypt.OpenSSLEngine;
import com.unisound.common.k;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGeneratorSpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;

/* JADX INFO: loaded from: classes.dex */
public class AndroidKeyPairGenerator extends KeyPairGeneratorSpi {
    private KeyStore mKeyStore;
    private KeyPairGeneratorSpec mSpec;

    @Override // java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        KeyPairGeneratorSpec keyPairGeneratorSpec;
        if (this.mKeyStore == null || (keyPairGeneratorSpec = this.mSpec) == null) {
            throw new IllegalStateException("Must call initialize with an android.security.KeyPairGeneratorSpec first");
        }
        if ((keyPairGeneratorSpec.getFlags() & 1) != 0 && this.mKeyStore.state() != KeyStore.State.UNLOCKED) {
            throw new IllegalStateException("Android keystore must be in initialized and unlocked state if encryption is required");
        }
        String keystoreAlias = this.mSpec.getKeystoreAlias();
        Credentials.deleteAllTypesForAlias(this.mKeyStore, keystoreAlias);
        int keyTypeForAlgorithm = KeyStore.getKeyTypeForAlgorithm(this.mSpec.getKeyType());
        byte[][] argsForKeyType = getArgsForKeyType(keyTypeForAlgorithm, this.mSpec.getAlgorithmParameterSpec());
        String str = Credentials.USER_PRIVATE_KEY + keystoreAlias;
        if (!this.mKeyStore.generate(str, -1, keyTypeForAlgorithm, this.mSpec.getKeySize(), this.mSpec.getFlags(), argsForKeyType)) {
            throw new IllegalStateException("could not generate key in keystore");
        }
        try {
            PrivateKey privateKeyById = OpenSSLEngine.getInstance("keystore").getPrivateKeyById(str);
            try {
                PublicKey publicKeyGeneratePublic = KeyFactory.getInstance(this.mSpec.getKeyType()).generatePublic(new X509EncodedKeySpec(this.mKeyStore.getPubkey(str)));
                X509V3CertificateGenerator x509V3CertificateGenerator = new X509V3CertificateGenerator();
                x509V3CertificateGenerator.setPublicKey(publicKeyGeneratePublic);
                x509V3CertificateGenerator.setSerialNumber(this.mSpec.getSerialNumber());
                x509V3CertificateGenerator.setSubjectDN(this.mSpec.getSubjectDN());
                x509V3CertificateGenerator.setIssuerDN(this.mSpec.getSubjectDN());
                x509V3CertificateGenerator.setNotBefore(this.mSpec.getStartDate());
                x509V3CertificateGenerator.setNotAfter(this.mSpec.getEndDate());
                x509V3CertificateGenerator.setSignatureAlgorithm(getDefaultSignatureAlgorithmForKeyType(this.mSpec.getKeyType()));
                try {
                    try {
                        if (!this.mKeyStore.put(Credentials.USER_CERTIFICATE + keystoreAlias, x509V3CertificateGenerator.generate(privateKeyById).getEncoded(), -1, this.mSpec.getFlags())) {
                            Credentials.deleteAllTypesForAlias(this.mKeyStore, keystoreAlias);
                            throw new IllegalStateException("Can't store certificate in AndroidKeyStore");
                        }
                        return new KeyPair(publicKeyGeneratePublic, privateKeyById);
                    } catch (CertificateEncodingException e) {
                        Credentials.deleteAllTypesForAlias(this.mKeyStore, keystoreAlias);
                        throw new IllegalStateException("Can't get encoding of certificate", e);
                    }
                } catch (Exception e2) {
                    Credentials.deleteAllTypesForAlias(this.mKeyStore, keystoreAlias);
                    throw new IllegalStateException("Can't generate certificate", e2);
                }
            } catch (NoSuchAlgorithmException e3) {
                throw new IllegalStateException("Can't instantiate key generator", e3);
            } catch (InvalidKeySpecException e4) {
                throw new IllegalStateException("keystore returned invalid key encoding", e4);
            }
        } catch (InvalidKeyException e5) {
            throw new RuntimeException("Can't get key", e5);
        }
    }

    private static String getDefaultSignatureAlgorithmForKeyType(String str) {
        if ("RSA".equalsIgnoreCase(str)) {
            return "sha256WithRSA";
        }
        if ("DSA".equalsIgnoreCase(str)) {
            return "sha1WithDSA";
        }
        if (k.i.equalsIgnoreCase(str)) {
            return "sha256WithECDSA";
        }
        throw new IllegalArgumentException("Unsupported key type " + str);
    }

    private static byte[][] getArgsForKeyType(int i, AlgorithmParameterSpec algorithmParameterSpec) {
        if (i == 6) {
            if (algorithmParameterSpec instanceof RSAKeyGenParameterSpec) {
                return new byte[][]{((RSAKeyGenParameterSpec) algorithmParameterSpec).getPublicExponent().toByteArray()};
            }
        } else if (i == 116 && (algorithmParameterSpec instanceof DSAParameterSpec)) {
            DSAParameterSpec dSAParameterSpec = (DSAParameterSpec) algorithmParameterSpec;
            return new byte[][]{dSAParameterSpec.getG().toByteArray(), dSAParameterSpec.getP().toByteArray(), dSAParameterSpec.getQ().toByteArray()};
        }
        return (byte[][]) null;
    }

    @Override // java.security.KeyPairGeneratorSpi
    public void initialize(int i, SecureRandom secureRandom) {
        throw new IllegalArgumentException("cannot specify keysize with AndroidKeyPairGenerator");
    }

    @Override // java.security.KeyPairGeneratorSpi
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (algorithmParameterSpec == null) {
            throw new InvalidAlgorithmParameterException("must supply params of type android.security.KeyPairGeneratorSpec");
        }
        if (!(algorithmParameterSpec instanceof KeyPairGeneratorSpec)) {
            throw new InvalidAlgorithmParameterException("params must be of type android.security.KeyPairGeneratorSpec");
        }
        this.mSpec = (KeyPairGeneratorSpec) algorithmParameterSpec;
        this.mKeyStore = KeyStore.getInstance();
    }
}
