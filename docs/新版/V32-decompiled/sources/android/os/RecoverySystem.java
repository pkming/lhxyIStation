package android.os;

import android.Manifest;
import android.app.backup.FullBackup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.pkcs7.ContentInfo;
import org.apache.harmony.security.pkcs7.SignedData;
import org.apache.harmony.security.pkcs7.SignerInfo;
import org.apache.harmony.security.provider.cert.X509CertImpl;

/* JADX INFO: loaded from: classes.dex */
public class RecoverySystem {
    private static final long PUBLISH_PROGRESS_INTERVAL_MS = 500;
    private static final String TAG = "RecoverySystem";
    private static final File DEFAULT_KEYSTORE = new File("/system/etc/security/otacerts.zip");
    private static File RECOVERY_DIR = new File("/cache/recovery");
    private static File COMMAND_FILE = new File(RECOVERY_DIR, "command");
    private static File LOG_FILE = new File(RECOVERY_DIR, "log");
    private static String LAST_PREFIX = "last_";
    private static int LOG_FILE_MAX_LENGTH = 65536;

    public interface ProgressListener {
        void onProgress(int i);
    }

    private void RecoverySystem() {
    }

    private static HashSet<Certificate> getTrustedCerts(File file) throws GeneralSecurityException, IOException {
        HashSet<Certificate> hashSet = new HashSet<>();
        if (file == null) {
            file = DEFAULT_KEYSTORE;
        }
        ZipFile zipFile = new ZipFile(file);
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Enumeration<? extends ZipEntry> enumerationEntries = zipFile.entries();
            while (enumerationEntries.hasMoreElements()) {
                InputStream inputStream = zipFile.getInputStream(enumerationEntries.nextElement());
                try {
                    hashSet.add(certificateFactory.generateCertificate(inputStream));
                    inputStream.close();
                } finally {
                }
            }
            return hashSet;
        } finally {
            zipFile.close();
        }
    }

    public static void verifyPackage(File file, ProgressListener progressListener, File file2) throws GeneralSecurityException, IOException {
        boolean z;
        String sigAlgName;
        Signature signature;
        long length = file.length();
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, FullBackup.ROOT_TREE_TOKEN);
        try {
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (progressListener != null) {
                progressListener.onProgress(0);
            }
            randomAccessFile.seek(length - 6);
            byte[] bArr = new byte[6];
            randomAccessFile.readFully(bArr);
            if (bArr[2] != -1 || bArr[3] != -1) {
                throw new SignatureException("no signature in file (no footer)");
            }
            int i = (bArr[4] & 255) | ((bArr[5] & 255) << 8);
            int i2 = ((bArr[1] & 255) << 8) | (bArr[0] & 255);
            int i3 = i + 22;
            byte[] bArr2 = new byte[i3];
            randomAccessFile.seek(length - ((long) i3));
            randomAccessFile.readFully(bArr2);
            if (bArr2[0] != 80 || bArr2[1] != 75 || bArr2[2] != 5 || bArr2[3] != 6) {
                throw new SignatureException("no signature in file (bad footer)");
            }
            for (int i4 = 4; i4 < i3 - 3; i4++) {
                if (bArr2[i4] == 80 && bArr2[i4 + 1] == 75 && bArr2[i4 + 2] == 5) {
                    if (bArr2[i4 + 3] == 6) {
                        throw new SignatureException("EOCD marker found after start of EOCD");
                    }
                }
            }
            SignedData signedData = ((ContentInfo) ContentInfo.ASN1.decode(new BerInputStream(new ByteArrayInputStream(bArr2, i3 - i2, i2)))).getSignedData();
            if (signedData == null) {
                throw new IOException("signedData is null");
            }
            List certificates = signedData.getCertificates();
            if (certificates.isEmpty()) {
                throw new IOException("encCerts is empty");
            }
            Iterator it = certificates.iterator();
            if (it.hasNext()) {
                X509CertImpl x509CertImpl = new X509CertImpl((org.apache.harmony.security.x509.Certificate) it.next());
                List signerInfos = signedData.getSignerInfos();
                if (!signerInfos.isEmpty()) {
                    SignerInfo signerInfo = (SignerInfo) signerInfos.get(0);
                    HashSet<Certificate> trustedCerts = getTrustedCerts(file2 == null ? DEFAULT_KEYSTORE : file2);
                    PublicKey publicKey = x509CertImpl.getPublicKey();
                    Iterator<Certificate> it2 = trustedCerts.iterator();
                    while (true) {
                        if (it2.hasNext()) {
                            if (it2.next().getPublicKey().equals(publicKey)) {
                                z = true;
                                break;
                            }
                        } else {
                            z = false;
                            break;
                        }
                    }
                    if (!z) {
                        throw new SignatureException("signature doesn't match any trusted key");
                    }
                    String digestAlgorithm = signerInfo.getDigestAlgorithm();
                    String digestEncryptionAlgorithm = signerInfo.getDigestEncryptionAlgorithm();
                    if (digestAlgorithm == null || digestEncryptionAlgorithm == null) {
                        sigAlgName = x509CertImpl.getSigAlgName();
                    } else {
                        sigAlgName = digestAlgorithm + "with" + digestEncryptionAlgorithm;
                    }
                    Signature signature2 = Signature.getInstance(sigAlgName);
                    signature2.initVerify((Certificate) x509CertImpl);
                    long j = (length - ((long) i)) - 2;
                    long j2 = 0;
                    randomAccessFile.seek(0L);
                    int i5 = 4096;
                    byte[] bArr3 = new byte[4096];
                    boolean zInterrupted = false;
                    int i6 = 0;
                    while (j2 < j && !(zInterrupted = Thread.interrupted())) {
                        SignerInfo signerInfo2 = signerInfo;
                        int i7 = randomAccessFile.read(bArr3, 0, ((long) i5) + j2 > j ? (int) (j - j2) : i5);
                        signature2.update(bArr3, 0, i7);
                        byte[] bArr4 = bArr3;
                        j2 += (long) i7;
                        if (progressListener != null) {
                            long jCurrentTimeMillis2 = System.currentTimeMillis();
                            signature = signature2;
                            int i8 = (int) ((100 * j2) / j);
                            if (i8 > i6 && jCurrentTimeMillis2 - jCurrentTimeMillis > 500) {
                                progressListener.onProgress(i8);
                                i6 = i8;
                                jCurrentTimeMillis = jCurrentTimeMillis2;
                            }
                        } else {
                            signature = signature2;
                        }
                        signature2 = signature;
                        signerInfo = signerInfo2;
                        bArr3 = bArr4;
                        i5 = 4096;
                    }
                    SignerInfo signerInfo3 = signerInfo;
                    Signature signature3 = signature2;
                    if (progressListener != null) {
                        progressListener.onProgress(100);
                    }
                    if (zInterrupted) {
                        throw new SignatureException("verification was interrupted");
                    }
                    if (!signature3.verify(signerInfo3.getEncryptedDigest())) {
                        throw new SignatureException("signature digest verification failed");
                    }
                    return;
                }
                throw new IOException("no signer infos!");
            }
            throw new SignatureException("signature contains no certificates");
        } finally {
            randomAccessFile.close();
        }
    }

    public static void installPackage(Context context, File file) throws IOException {
        String canonicalPath = file.getCanonicalPath();
        if (canonicalPath.startsWith("/storage/emulated/")) {
            canonicalPath = "/data/media/" + canonicalPath.substring(18);
        }
        Log.w(TAG, "!!! REBOOTING TO INSTALL " + canonicalPath + " !!!");
        bootCommand(context, "--update_package=" + canonicalPath + "\n--locale=" + Locale.getDefault().toString());
    }

    public static void rebootWipeUserData(Context context) throws IOException {
        final ConditionVariable conditionVariable = new ConditionVariable();
        context.sendOrderedBroadcastAsUser(new Intent("android.intent.action.MASTER_CLEAR_NOTIFICATION"), UserHandle.OWNER, Manifest.permission.MASTER_CLEAR, new BroadcastReceiver() { // from class: android.os.RecoverySystem.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                conditionVariable.open();
            }
        }, null, 0, null, null);
        conditionVariable.block();
        bootCommand(context, "--wipe_data\n--locale=" + Locale.getDefault().toString());
    }

    public static void rebootWipeCache(Context context) throws IOException {
        bootCommand(context, "--wipe_cache\n--locale=" + Locale.getDefault().toString());
    }

    private static void bootCommand(Context context, String str) throws IOException {
        RECOVERY_DIR.mkdirs();
        COMMAND_FILE.delete();
        LOG_FILE.delete();
        FileWriter fileWriter = new FileWriter(COMMAND_FILE);
        try {
            fileWriter.write(str);
            fileWriter.write("\n");
            fileWriter.close();
            ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).reboot("recovery");
            throw new IOException("Reboot failed (no permissions?)");
        } catch (Throwable th) {
            fileWriter.close();
            throw th;
        }
    }

    public static String handleAftermath() {
        String textFile;
        try {
            textFile = FileUtils.readTextFile(LOG_FILE, -LOG_FILE_MAX_LENGTH, "...\n");
        } catch (FileNotFoundException unused) {
            Log.i(TAG, "No recovery log file");
            textFile = null;
        } catch (IOException e) {
            Log.e(TAG, "Error reading recovery log", e);
            textFile = null;
        }
        String[] list = RECOVERY_DIR.list();
        for (int i = 0; list != null && i < list.length; i++) {
            if (!list[i].startsWith(LAST_PREFIX)) {
                File file = new File(RECOVERY_DIR, list[i]);
                if (!file.delete()) {
                    Log.e(TAG, "Can't delete: " + file);
                } else {
                    Log.i(TAG, "Deleted: " + file);
                }
            }
        }
        return textFile;
    }
}
