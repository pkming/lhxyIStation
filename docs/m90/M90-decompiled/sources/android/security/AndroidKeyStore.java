package android.security;

import android.util.Log;
import com.android.org.conscrypt.OpenSSLEngine;
import com.android.org.conscrypt.OpenSSLKeyHolder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class AndroidKeyStore extends KeyStoreSpi {
    public static final String NAME = "AndroidKeyStore";
    private KeyStore mKeyStore;

    @Override // java.security.KeyStoreSpi
    public Key engineGetKey(String str, char[] cArr) throws UnrecoverableKeyException, NoSuchAlgorithmException {
        if (!isKeyEntry(str)) {
            return null;
        }
        try {
            return OpenSSLEngine.getInstance("keystore").getPrivateKeyById(Credentials.USER_PRIVATE_KEY + str);
        } catch (InvalidKeyException e) {
            UnrecoverableKeyException unrecoverableKeyException = new UnrecoverableKeyException("Can't get key");
            unrecoverableKeyException.initCause(e);
            throw unrecoverableKeyException;
        }
    }

    @Override // java.security.KeyStoreSpi
    public Certificate[] engineGetCertificateChain(String str) {
        Certificate[] certificateArr;
        Objects.requireNonNull(str, "alias == null");
        X509Certificate x509Certificate = (X509Certificate) engineGetCertificate(str);
        if (x509Certificate == null) {
            return null;
        }
        byte[] bArr = this.mKeyStore.get(Credentials.CA_CERTIFICATE + str);
        int i = 1;
        if (bArr != null) {
            Collection<X509Certificate> certificates = toCertificates(bArr);
            certificateArr = new Certificate[certificates.size() + 1];
            Iterator<X509Certificate> it = certificates.iterator();
            while (it.hasNext()) {
                certificateArr[i] = it.next();
                i++;
            }
        } else {
            certificateArr = new Certificate[1];
        }
        certificateArr[0] = x509Certificate;
        return certificateArr;
    }

    @Override // java.security.KeyStoreSpi
    public Certificate engineGetCertificate(String str) {
        Objects.requireNonNull(str, "alias == null");
        byte[] bArr = this.mKeyStore.get(Credentials.USER_CERTIFICATE + str);
        if (bArr != null) {
            return toCertificate(bArr);
        }
        byte[] bArr2 = this.mKeyStore.get(Credentials.CA_CERTIFICATE + str);
        if (bArr2 != null) {
            return toCertificate(bArr2);
        }
        return null;
    }

    private static X509Certificate toCertificate(byte[] bArr) {
        try {
            return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(bArr));
        } catch (CertificateException e) {
            Log.w("AndroidKeyStore", "Couldn't parse certificate in keystore", e);
            return null;
        }
    }

    private static Collection<X509Certificate> toCertificates(byte[] bArr) {
        try {
            return CertificateFactory.getInstance("X.509").generateCertificates(new ByteArrayInputStream(bArr));
        } catch (CertificateException e) {
            Log.w("AndroidKeyStore", "Couldn't parse certificates in keystore", e);
            return new ArrayList();
        }
    }

    private Date getModificationDate(String str) {
        long j = this.mKeyStore.getmtime(str);
        if (j == -1) {
            return null;
        }
        return new Date(j);
    }

    @Override // java.security.KeyStoreSpi
    public Date engineGetCreationDate(String str) {
        Objects.requireNonNull(str, "alias == null");
        Date modificationDate = getModificationDate(Credentials.USER_PRIVATE_KEY + str);
        if (modificationDate != null) {
            return modificationDate;
        }
        Date modificationDate2 = getModificationDate(Credentials.USER_CERTIFICATE + str);
        return modificationDate2 != null ? modificationDate2 : getModificationDate(Credentials.CA_CERTIFICATE + str);
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws java.security.KeyStoreException {
        if (cArr != null && cArr.length > 0) {
            throw new java.security.KeyStoreException("entries cannot be protected with passwords");
        }
        if (key instanceof PrivateKey) {
            setPrivateKeyEntry(str, (PrivateKey) key, certificateArr, null);
            return;
        }
        throw new java.security.KeyStoreException("Only PrivateKeys are supported");
    }

    private void setPrivateKeyEntry(String str, PrivateKey privateKey, Certificate[] certificateArr, KeyStoreParameter keyStoreParameter) throws java.security.KeyStoreException {
        byte[] encoded;
        boolean z;
        byte[] bArr = null;
        String alias = privateKey instanceof OpenSSLKeyHolder ? ((OpenSSLKeyHolder) privateKey).getOpenSSLKey().getAlias() : null;
        if (alias != null && alias.startsWith(Credentials.USER_PRIVATE_KEY)) {
            String strSubstring = alias.substring(8);
            if (!str.equals(strSubstring)) {
                throw new java.security.KeyStoreException("Can only replace keys with same alias: " + str + " != " + strSubstring);
            }
            encoded = null;
            z = false;
        } else {
            String format = privateKey.getFormat();
            if (format == null || !"PKCS#8".equals(format)) {
                throw new java.security.KeyStoreException("Only PrivateKeys that can be encoded into PKCS#8 are supported");
            }
            encoded = privateKey.getEncoded();
            if (encoded == null) {
                throw new java.security.KeyStoreException("PrivateKey has no encoding");
            }
            z = true;
        }
        if (certificateArr == null || certificateArr.length == 0) {
            throw new java.security.KeyStoreException("Must supply at least one Certificate with PrivateKey");
        }
        int length = certificateArr.length;
        X509Certificate[] x509CertificateArr = new X509Certificate[length];
        for (int i = 0; i < certificateArr.length; i++) {
            if (!"X.509".equals(certificateArr[i].getType())) {
                throw new java.security.KeyStoreException("Certificates must be in X.509 format: invalid cert #" + i);
            }
            if (!(certificateArr[i] instanceof X509Certificate)) {
                throw new java.security.KeyStoreException("Certificates must be in X.509 format: invalid cert #" + i);
            }
            x509CertificateArr[i] = (X509Certificate) certificateArr[i];
        }
        try {
            byte[] encoded2 = x509CertificateArr[0].getEncoded();
            if (certificateArr.length > 1) {
                int i2 = length - 1;
                byte[][] bArr2 = new byte[i2][];
                int i3 = 0;
                int length2 = 0;
                while (i3 < i2) {
                    int i4 = i3 + 1;
                    try {
                        bArr2[i3] = x509CertificateArr[i4].getEncoded();
                        length2 += bArr2[i3].length;
                        i3 = i4;
                    } catch (CertificateEncodingException e) {
                        throw new java.security.KeyStoreException("Can't encode Certificate #" + i3, e);
                    }
                }
                byte[] bArr3 = new byte[length2];
                int i5 = 0;
                for (int i6 = 0; i6 < i2; i6++) {
                    int length3 = bArr2[i6].length;
                    System.arraycopy(bArr2[i6], 0, bArr3, i5, length3);
                    i5 += length3;
                    bArr2[i6] = null;
                }
                bArr = bArr3;
            }
            if (z) {
                Credentials.deleteAllTypesForAlias(this.mKeyStore, str);
            } else {
                Credentials.deleteCertificateTypesForAlias(this.mKeyStore, str);
            }
            int flags = keyStoreParameter != null ? keyStoreParameter.getFlags() : 0;
            if (z && !this.mKeyStore.importKey(Credentials.USER_PRIVATE_KEY + str, encoded, -1, flags)) {
                Credentials.deleteAllTypesForAlias(this.mKeyStore, str);
                throw new java.security.KeyStoreException("Couldn't put private key in keystore");
            }
            if (!this.mKeyStore.put(Credentials.USER_CERTIFICATE + str, encoded2, -1, flags)) {
                Credentials.deleteAllTypesForAlias(this.mKeyStore, str);
                throw new java.security.KeyStoreException("Couldn't put certificate #1 in keystore");
            }
            if (bArr == null || this.mKeyStore.put(Credentials.CA_CERTIFICATE + str, bArr, -1, flags)) {
                return;
            }
            Credentials.deleteAllTypesForAlias(this.mKeyStore, str);
            throw new java.security.KeyStoreException("Couldn't put certificate chain in keystore");
        } catch (CertificateEncodingException e2) {
            throw new java.security.KeyStoreException("Couldn't encode certificate #1", e2);
        }
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws java.security.KeyStoreException {
        throw new java.security.KeyStoreException("Operation not supported because key encoding is unknown");
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetCertificateEntry(String str, Certificate certificate) throws java.security.KeyStoreException {
        if (isKeyEntry(str)) {
            throw new java.security.KeyStoreException("Entry exists and is not a trusted certificate");
        }
        Objects.requireNonNull(certificate, "cert == null");
        try {
            if (!this.mKeyStore.put(Credentials.CA_CERTIFICATE + str, certificate.getEncoded(), -1, 0)) {
                throw new java.security.KeyStoreException("Couldn't insert certificate; is KeyStore initialized?");
            }
        } catch (CertificateEncodingException e) {
            throw new java.security.KeyStoreException(e);
        }
    }

    @Override // java.security.KeyStoreSpi
    public void engineDeleteEntry(String str) throws java.security.KeyStoreException {
        if ((isKeyEntry(str) || isCertificateEntry(str)) && !Credentials.deleteAllTypesForAlias(this.mKeyStore, str)) {
            throw new java.security.KeyStoreException("No such entry " + str);
        }
    }

    private Set<String> getUniqueAliases() {
        String[] strArrSaw = this.mKeyStore.saw("");
        if (strArrSaw == null) {
            return new HashSet();
        }
        HashSet hashSet = new HashSet(strArrSaw.length);
        for (String str : strArrSaw) {
            int iIndexOf = str.indexOf(95);
            if (iIndexOf == -1 || str.length() <= iIndexOf) {
                Log.e("AndroidKeyStore", "invalid alias: " + str);
            } else {
                hashSet.add(new String(str.substring(iIndexOf + 1)));
            }
        }
        return hashSet;
    }

    @Override // java.security.KeyStoreSpi
    public Enumeration<String> engineAliases() {
        return Collections.enumeration(getUniqueAliases());
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineContainsAlias(String str) {
        Objects.requireNonNull(str, "alias == null");
        return this.mKeyStore.contains(new StringBuilder().append(Credentials.USER_PRIVATE_KEY).append(str).toString()) || this.mKeyStore.contains(new StringBuilder().append(Credentials.USER_CERTIFICATE).append(str).toString()) || this.mKeyStore.contains(new StringBuilder().append(Credentials.CA_CERTIFICATE).append(str).toString());
    }

    @Override // java.security.KeyStoreSpi
    public int engineSize() {
        return getUniqueAliases().size();
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsKeyEntry(String str) {
        return isKeyEntry(str);
    }

    private boolean isKeyEntry(String str) {
        Objects.requireNonNull(str, "alias == null");
        return this.mKeyStore.contains(Credentials.USER_PRIVATE_KEY + str);
    }

    private boolean isCertificateEntry(String str) {
        Objects.requireNonNull(str, "alias == null");
        return this.mKeyStore.contains(Credentials.CA_CERTIFICATE + str);
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsCertificateEntry(String str) {
        return !isKeyEntry(str) && isCertificateEntry(str);
    }

    @Override // java.security.KeyStoreSpi
    public String engineGetCertificateAlias(Certificate certificate) {
        if (certificate == null) {
            return null;
        }
        HashSet hashSet = new HashSet();
        String[] strArrSaw = this.mKeyStore.saw(Credentials.USER_CERTIFICATE);
        if (strArrSaw != null) {
            for (String str : strArrSaw) {
                byte[] bArr = this.mKeyStore.get(Credentials.USER_CERTIFICATE + str);
                if (bArr != null) {
                    X509Certificate certificate2 = toCertificate(bArr);
                    hashSet.add(str);
                    if (certificate.equals(certificate2)) {
                        return str;
                    }
                }
            }
        }
        String[] strArrSaw2 = this.mKeyStore.saw(Credentials.CA_CERTIFICATE);
        if (strArrSaw != null) {
            for (String str2 : strArrSaw2) {
                if (!hashSet.contains(str2) && this.mKeyStore.get(Credentials.CA_CERTIFICATE + str2) != null && certificate.equals(toCertificate(this.mKeyStore.get(Credentials.CA_CERTIFICATE + str2)))) {
                    return str2;
                }
            }
        }
        return null;
    }

    @Override // java.security.KeyStoreSpi
    public void engineStore(OutputStream outputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
        throw new UnsupportedOperationException("Can not serialize AndroidKeyStore to OutputStream");
    }

    @Override // java.security.KeyStoreSpi
    public void engineLoad(InputStream inputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
        if (inputStream != null) {
            throw new IllegalArgumentException("InputStream not supported");
        }
        if (cArr != null) {
            throw new IllegalArgumentException("password not supported");
        }
        this.mKeyStore = KeyStore.getInstance();
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetEntry(String str, KeyStore.Entry entry, KeyStore.ProtectionParameter protectionParameter) throws java.security.KeyStoreException {
        if (entry == null) {
            throw new java.security.KeyStoreException("entry == null");
        }
        if (engineContainsAlias(str)) {
            engineDeleteEntry(str);
        }
        if (entry instanceof KeyStore.TrustedCertificateEntry) {
            engineSetCertificateEntry(str, ((KeyStore.TrustedCertificateEntry) entry).getTrustedCertificate());
            return;
        }
        if (protectionParameter != null && !(protectionParameter instanceof KeyStoreParameter)) {
            throw new java.security.KeyStoreException("protParam should be android.security.KeyStoreParameter; was: " + protectionParameter.getClass().getName());
        }
        if (entry instanceof KeyStore.PrivateKeyEntry) {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) entry;
            setPrivateKeyEntry(str, privateKeyEntry.getPrivateKey(), privateKeyEntry.getCertificateChain(), (KeyStoreParameter) protectionParameter);
            return;
        }
        throw new java.security.KeyStoreException("Entry must be a PrivateKeyEntry or TrustedCertificateEntry; was " + entry);
    }
}
