package android.content.pm;

import android.Manifest;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Slog;
import android.util.TypedValue;
import com.android.internal.R;
import com.android.internal.util.XmlUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.apache.tools.ant.taskdefs.compilers.AptCompilerAdapter;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class PackageParser {
    private static final String ANDROID_MANIFEST_FILENAME = "AndroidManifest.xml";
    private static final String ANDROID_RESOURCES = "http://schemas.android.com/apk/res/android";
    private static final boolean DEBUG_BACKUP = false;
    private static final boolean DEBUG_JAR = false;
    private static final boolean DEBUG_PARSER = false;
    public static final int PARSE_CHATTY = 2;
    private static final int PARSE_DEFAULT_INSTALL_LOCATION = -1;
    public static final int PARSE_FORWARD_LOCK = 16;
    public static final int PARSE_IGNORE_PROCESSES = 8;
    public static final int PARSE_IS_PRIVILEGED = 128;
    public static final int PARSE_IS_SYSTEM = 1;
    public static final int PARSE_IS_SYSTEM_DIR = 64;
    public static final int PARSE_MUST_BE_APK = 4;
    public static final int PARSE_ON_SDCARD = 32;
    private static final boolean RIGID_PARSER = false;
    private static final String SDK_CODENAME;
    private static final String TAG = "PackageParser";
    private static WeakReference<byte[]> mReadBuffer;
    private static final Object mSync;
    private static boolean sCompatibilityModeEnabled;
    private String mArchiveSourcePath;
    private boolean mOnlyCoreApps;
    private ParseComponentArgs mParseActivityAliasArgs;
    private ParseComponentArgs mParseActivityArgs;
    private int mParseError = 1;
    private ParsePackageItemArgs mParseInstrumentationArgs;
    private ParseComponentArgs mParseProviderArgs;
    private ParseComponentArgs mParseServiceArgs;
    private String[] mSeparateProcesses;
    public static final NewPermissionInfo[] NEW_PERMISSIONS = {new NewPermissionInfo(Manifest.permission.WRITE_EXTERNAL_STORAGE, 4, 0), new NewPermissionInfo(Manifest.permission.READ_PHONE_STATE, 4, 0)};
    public static final SplitPermissionInfo[] SPLIT_PERMISSIONS = {new SplitPermissionInfo(Manifest.permission.WRITE_EXTERNAL_STORAGE, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10001), new SplitPermissionInfo(Manifest.permission.READ_CONTACTS, new String[]{Manifest.permission.READ_CALL_LOG}, 16), new SplitPermissionInfo(Manifest.permission.WRITE_CONTACTS, new String[]{Manifest.permission.WRITE_CALL_LOG}, 16)};
    private static final int SDK_VERSION = Build.VERSION.SDK_INT;

    public static class IntentInfo extends IntentFilter {
        public boolean hasDefault;
        public int icon;
        public int labelRes;
        public int logo;
        public CharSequence nonLocalizedLabel;
        public int preferred;
    }

    public static class NewPermissionInfo {
        public final int fileVersion;
        public final String name;
        public final int sdkVersion;

        public NewPermissionInfo(String str, int i, int i2) {
            this.name = str;
            this.sdkVersion = i;
            this.fileVersion = i2;
        }
    }

    public static class SplitPermissionInfo {
        public final String[] newPerms;
        public final String rootPerm;
        public final int targetSdk;

        public SplitPermissionInfo(String str, String[] strArr, int i) {
            this.rootPerm = str;
            this.newPerms = strArr;
            this.targetSdk = i;
        }
    }

    static {
        SDK_CODENAME = "REL".equals(Build.VERSION.CODENAME) ? null : Build.VERSION.CODENAME;
        mSync = new Object();
        sCompatibilityModeEnabled = true;
    }

    static class ParsePackageItemArgs {
        final int iconRes;
        final int labelRes;
        final int logoRes;
        final int nameRes;
        final String[] outError;
        final Package owner;
        TypedArray sa;
        String tag;

        ParsePackageItemArgs(Package r1, String[] strArr, int i, int i2, int i3, int i4) {
            this.owner = r1;
            this.outError = strArr;
            this.nameRes = i;
            this.labelRes = i2;
            this.iconRes = i3;
            this.logoRes = i4;
        }
    }

    static class ParseComponentArgs extends ParsePackageItemArgs {
        final int descriptionRes;
        final int enabledRes;
        int flags;
        final int processRes;
        final String[] sepProcesses;

        ParseComponentArgs(Package r1, String[] strArr, int i, int i2, int i3, int i4, String[] strArr2, int i5, int i6, int i7) {
            super(r1, strArr, i, i2, i3, i4);
            this.sepProcesses = strArr2;
            this.processRes = i5;
            this.descriptionRes = i6;
            this.enabledRes = i7;
        }
    }

    public static class PackageLite {
        public final int installLocation;
        public final String packageName;
        public final VerifierInfo[] verifiers;
        public final int versionCode;

        public PackageLite(String str, int i, int i2, List<VerifierInfo> list) {
            this.packageName = str;
            this.versionCode = i;
            this.installLocation = i2;
            this.verifiers = (VerifierInfo[]) list.toArray(new VerifierInfo[list.size()]);
        }
    }

    public PackageParser(String str) {
        this.mArchiveSourcePath = str;
    }

    public void setSeparateProcesses(String[] strArr) {
        this.mSeparateProcesses = strArr;
    }

    public void setOnlyCoreApps(boolean z) {
        this.mOnlyCoreApps = z;
    }

    private static final boolean isPackageFilename(String str) {
        return str.endsWith(".apk");
    }

    public static PackageInfo generatePackageInfo(Package r10, int[] iArr, int i, long j, long j2, HashSet<String> hashSet, PackageUserState packageUserState) {
        return generatePackageInfo(r10, iArr, i, j, j2, hashSet, packageUserState, UserHandle.getCallingUserId());
    }

    private static boolean checkUseInstalledOrBlocked(int i, PackageUserState packageUserState) {
        return (packageUserState.installed && !packageUserState.blocked) || (i & 8192) != 0;
    }

    public static boolean isAvailable(PackageUserState packageUserState) {
        return checkUseInstalledOrBlocked(0, packageUserState);
    }

    public static PackageInfo generatePackageInfo(Package r4, int[] iArr, int i, long j, long j2, HashSet<String> hashSet, PackageUserState packageUserState, int i2) {
        int size;
        int size2;
        int size3;
        int size4;
        int size5;
        if (!checkUseInstalledOrBlocked(i, packageUserState)) {
            return null;
        }
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = r4.packageName;
        packageInfo.versionCode = r4.mVersionCode;
        packageInfo.versionName = r4.mVersionName;
        packageInfo.sharedUserId = r4.mSharedUserId;
        packageInfo.sharedUserLabel = r4.mSharedUserLabel;
        packageInfo.applicationInfo = generateApplicationInfo(r4, i, packageUserState, i2);
        packageInfo.installLocation = r4.installLocation;
        if ((packageInfo.applicationInfo.flags & 1) != 0 || (packageInfo.applicationInfo.flags & 128) != 0) {
            packageInfo.requiredForAllUsers = r4.mRequiredForAllUsers;
        }
        packageInfo.restrictedAccountType = r4.mRestrictedAccountType;
        packageInfo.requiredAccountType = r4.mRequiredAccountType;
        packageInfo.firstInstallTime = j;
        packageInfo.lastUpdateTime = j2;
        if ((i & 256) != 0) {
            packageInfo.gids = iArr;
        }
        if ((i & 16384) != 0) {
            int size6 = r4.configPreferences.size();
            if (size6 > 0) {
                packageInfo.configPreferences = new ConfigurationInfo[size6];
                r4.configPreferences.toArray(packageInfo.configPreferences);
            }
            int size7 = r4.reqFeatures != null ? r4.reqFeatures.size() : 0;
            if (size7 > 0) {
                packageInfo.reqFeatures = new FeatureInfo[size7];
                r4.reqFeatures.toArray(packageInfo.reqFeatures);
            }
        }
        if ((i & 1) != 0 && (size5 = r4.activities.size()) > 0) {
            int i3 = i & 512;
            if (i3 != 0) {
                packageInfo.activities = new ActivityInfo[size5];
            } else {
                int i4 = 0;
                for (int i5 = 0; i5 < size5; i5++) {
                    if (r4.activities.get(i5).info.enabled) {
                        i4++;
                    }
                }
                packageInfo.activities = new ActivityInfo[i4];
            }
            int i6 = 0;
            for (int i7 = 0; i7 < size5; i7++) {
                if (r4.activities.get(i7).info.enabled || i3 != 0) {
                    packageInfo.activities[i6] = generateActivityInfo(r4.activities.get(i7), i, packageUserState, i2);
                    i6++;
                }
            }
        }
        if ((i & 2) != 0 && (size4 = r4.receivers.size()) > 0) {
            int i8 = i & 512;
            if (i8 != 0) {
                packageInfo.receivers = new ActivityInfo[size4];
            } else {
                int i9 = 0;
                for (int i10 = 0; i10 < size4; i10++) {
                    if (r4.receivers.get(i10).info.enabled) {
                        i9++;
                    }
                }
                packageInfo.receivers = new ActivityInfo[i9];
            }
            int i11 = 0;
            for (int i12 = 0; i12 < size4; i12++) {
                if (r4.receivers.get(i12).info.enabled || i8 != 0) {
                    packageInfo.receivers[i11] = generateActivityInfo(r4.receivers.get(i12), i, packageUserState, i2);
                    i11++;
                }
            }
        }
        if ((i & 4) != 0 && (size3 = r4.services.size()) > 0) {
            int i13 = i & 512;
            if (i13 != 0) {
                packageInfo.services = new ServiceInfo[size3];
            } else {
                int i14 = 0;
                for (int i15 = 0; i15 < size3; i15++) {
                    if (r4.services.get(i15).info.enabled) {
                        i14++;
                    }
                }
                packageInfo.services = new ServiceInfo[i14];
            }
            int i16 = 0;
            for (int i17 = 0; i17 < size3; i17++) {
                if (r4.services.get(i17).info.enabled || i13 != 0) {
                    packageInfo.services[i16] = generateServiceInfo(r4.services.get(i17), i, packageUserState, i2);
                    i16++;
                }
            }
        }
        if ((i & 8) != 0 && (size2 = r4.providers.size()) > 0) {
            int i18 = i & 512;
            if (i18 != 0) {
                packageInfo.providers = new ProviderInfo[size2];
            } else {
                int i19 = 0;
                for (int i20 = 0; i20 < size2; i20++) {
                    if (r4.providers.get(i20).info.enabled) {
                        i19++;
                    }
                }
                packageInfo.providers = new ProviderInfo[i19];
            }
            int i21 = 0;
            for (int i22 = 0; i22 < size2; i22++) {
                if (r4.providers.get(i22).info.enabled || i18 != 0) {
                    packageInfo.providers[i21] = generateProviderInfo(r4.providers.get(i22), i, packageUserState, i2);
                    i21++;
                }
            }
        }
        if ((i & 16) != 0 && (size = r4.instrumentation.size()) > 0) {
            packageInfo.instrumentation = new InstrumentationInfo[size];
            for (int i23 = 0; i23 < size; i23++) {
                packageInfo.instrumentation[i23] = generateInstrumentationInfo(r4.instrumentation.get(i23), i);
            }
        }
        if ((i & 4096) != 0) {
            int size8 = r4.permissions.size();
            if (size8 > 0) {
                packageInfo.permissions = new PermissionInfo[size8];
                for (int i24 = 0; i24 < size8; i24++) {
                    packageInfo.permissions[i24] = generatePermissionInfo(r4.permissions.get(i24), i);
                }
            }
            int size9 = r4.requestedPermissions.size();
            if (size9 > 0) {
                packageInfo.requestedPermissions = new String[size9];
                packageInfo.requestedPermissionsFlags = new int[size9];
                for (int i25 = 0; i25 < size9; i25++) {
                    String str = r4.requestedPermissions.get(i25);
                    packageInfo.requestedPermissions[i25] = str;
                    if (r4.requestedPermissionsRequired.get(i25).booleanValue()) {
                        int[] iArr2 = packageInfo.requestedPermissionsFlags;
                        iArr2[i25] = iArr2[i25] | 1;
                    }
                    if (hashSet != null && hashSet.contains(str)) {
                        int[] iArr3 = packageInfo.requestedPermissionsFlags;
                        iArr3[i25] = iArr3[i25] | 2;
                    }
                }
            }
        }
        if ((i & 64) != 0) {
            int length = r4.mSignatures != null ? r4.mSignatures.length : 0;
            if (length > 0) {
                packageInfo.signatures = new Signature[length];
                System.arraycopy(r4.mSignatures, 0, packageInfo.signatures, 0, length);
            }
        }
        return packageInfo;
    }

    private Certificate[] loadCertificates(JarFile jarFile, JarEntry jarEntry, byte[] bArr) {
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(jarFile.getInputStream(jarEntry));
            while (bufferedInputStream.read(bArr, 0, bArr.length) != -1) {
            }
            bufferedInputStream.close();
            if (jarEntry != null) {
                return jarEntry.getCertificates();
            }
            return null;
        } catch (IOException e) {
            Slog.w(TAG, "Exception reading " + jarEntry.getName() + " in " + jarFile.getName(), e);
            return null;
        } catch (RuntimeException e2) {
            Slog.w(TAG, "Exception reading " + jarEntry.getName() + " in " + jarFile.getName(), e2);
            return null;
        }
    }

    public int getParseError() {
        return this.mParseError;
    }

    public Package parsePackage(File file, String str, DisplayMetrics displayMetrics, int i) {
        Resources resources;
        AssetManager assetManager;
        boolean z;
        XmlResourceParser xmlResourceParser;
        Exception exc;
        Package r0;
        AssetManager assetManager2;
        XmlResourceParser xmlResourceParserOpenXmlResourceParser;
        this.mParseError = 1;
        this.mArchiveSourcePath = file.getPath();
        if (!file.isFile()) {
            Slog.w(TAG, "Skipping dir: " + this.mArchiveSourcePath);
            this.mParseError = -100;
            return null;
        }
        if (!isPackageFilename(file.getName()) && (i & 4) != 0) {
            if ((i & 1) == 0) {
                Slog.w(TAG, "Skipping non-package file: " + this.mArchiveSourcePath);
            }
            this.mParseError = -100;
            return null;
        }
        try {
            assetManager = new AssetManager();
            try {
                int iAddAssetPath = assetManager.addAssetPath(this.mArchiveSourcePath);
                if (iAddAssetPath != 0) {
                    resources = new Resources(assetManager, displayMetrics, null);
                    try {
                        assetManager2 = assetManager;
                    } catch (Exception e) {
                        e = e;
                    }
                    try {
                        assetManager.setConfiguration(0, 0, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Build.VERSION.RESOURCES_SDK_INT);
                        xmlResourceParserOpenXmlResourceParser = assetManager2.openXmlResourceParser(iAddAssetPath, ANDROID_MANIFEST_FILENAME);
                        resources = resources;
                        z = false;
                    } catch (Exception e2) {
                        e = e2;
                        assetManager = assetManager2;
                        resources = resources;
                        Slog.w(TAG, "Unable to read AndroidManifest.xml of " + this.mArchiveSourcePath, e);
                        z = true;
                        xmlResourceParser = null;
                    }
                } else {
                    assetManager2 = assetManager;
                    try {
                        Slog.w(TAG, "Failed adding asset path:" + this.mArchiveSourcePath);
                        z = true;
                        xmlResourceParserOpenXmlResourceParser = null;
                        resources = null;
                    } catch (Exception e3) {
                        e = e3;
                        assetManager = assetManager2;
                        resources = null;
                        Slog.w(TAG, "Unable to read AndroidManifest.xml of " + this.mArchiveSourcePath, e);
                        z = true;
                        xmlResourceParser = null;
                    }
                }
                assetManager = assetManager2;
                xmlResourceParser = xmlResourceParserOpenXmlResourceParser;
            } catch (Exception e4) {
                e = e4;
            }
        } catch (Exception e5) {
            e = e5;
            resources = null;
            assetManager = null;
        }
        if (z) {
            if (assetManager != null) {
                assetManager.close();
            }
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_BAD_MANIFEST;
            return null;
        }
        String[] strArr = new String[1];
        try {
            r0 = parsePackage(resources, xmlResourceParser, i, strArr);
            exc = null;
        } catch (Exception e6) {
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION;
            exc = e6;
            r0 = null;
        }
        if (r0 == null) {
            if (!this.mOnlyCoreApps || this.mParseError != 1) {
                if (exc != null) {
                    Slog.w(TAG, this.mArchiveSourcePath, exc);
                } else {
                    Slog.w(TAG, this.mArchiveSourcePath + " (at " + xmlResourceParser.getPositionDescription() + "): " + strArr[0]);
                }
                if (this.mParseError == 1) {
                    this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
                }
            }
            xmlResourceParser.close();
            assetManager.close();
            return null;
        }
        xmlResourceParser.close();
        assetManager.close();
        r0.mPath = str;
        r0.mScanPath = this.mArchiveSourcePath;
        r0.mSignatures = null;
        return r0;
    }

    public boolean collectManifestDigest(Package r3) {
        try {
            JarFile jarFile = new JarFile(this.mArchiveSourcePath);
            try {
                ZipEntry entry = jarFile.getEntry(ANDROID_MANIFEST_FILENAME);
                if (entry != null) {
                    r3.manifestDigest = ManifestDigest.fromInputStream(jarFile.getInputStream(entry));
                }
                jarFile.close();
                return true;
            } catch (Throwable th) {
                jarFile.close();
                throw th;
            }
        } catch (IOException unused) {
            return false;
        }
    }

    public boolean collectCertificates(Package r17, int i) {
        WeakReference<byte[]> weakReference;
        byte[] bArr;
        Certificate[] certificateArrLoadCertificates;
        int i2;
        boolean z;
        Certificate[] certificateArr = null;
        r17.mSignatures = null;
        synchronized (mSync) {
            weakReference = mReadBuffer;
            if (weakReference != null) {
                mReadBuffer = null;
                bArr = weakReference.get();
            } else {
                bArr = null;
            }
            if (bArr == null) {
                bArr = new byte[8192];
                weakReference = new WeakReference<>(bArr);
            }
        }
        try {
            JarFile jarFile = new JarFile(this.mArchiveSourcePath);
            if ((i & 1) != 0) {
                JarEntry jarEntry = jarFile.getJarEntry(ANDROID_MANIFEST_FILENAME);
                certificateArrLoadCertificates = loadCertificates(jarFile, jarEntry, bArr);
                if (certificateArrLoadCertificates == null) {
                    Slog.e(TAG, "Package " + r17.packageName + " has no certificates at entry " + jarEntry.getName() + "; ignoring!");
                    jarFile.close();
                    this.mParseError = PackageManager.INSTALL_PARSE_FAILED_NO_CERTIFICATES;
                    return false;
                }
            } else {
                Enumeration<JarEntry> enumerationEntries = jarFile.entries();
                while (enumerationEntries.hasMoreElements()) {
                    JarEntry jarEntryNextElement = enumerationEntries.nextElement();
                    if (!jarEntryNextElement.isDirectory()) {
                        String name = jarEntryNextElement.getName();
                        if (!name.startsWith("META-INF/")) {
                            if (ANDROID_MANIFEST_FILENAME.equals(name)) {
                                r17.manifestDigest = ManifestDigest.fromInputStream(jarFile.getInputStream(jarEntryNextElement));
                            }
                            Object[] objArrLoadCertificates = loadCertificates(jarFile, jarEntryNextElement, bArr);
                            if (objArrLoadCertificates == null) {
                                Slog.e(TAG, "Package " + r17.packageName + " has no certificates at entry " + jarEntryNextElement.getName() + "; ignoring!");
                                jarFile.close();
                                this.mParseError = PackageManager.INSTALL_PARSE_FAILED_NO_CERTIFICATES;
                                return false;
                            }
                            if (certificateArr != null) {
                                for (0; i2 < certificateArr.length; i2 + 1) {
                                    int i3 = 0;
                                    while (true) {
                                        if (i3 >= objArrLoadCertificates.length) {
                                            z = false;
                                            break;
                                        }
                                        if (certificateArr[i2] != null && certificateArr[i2].equals(objArrLoadCertificates[i3])) {
                                            z = true;
                                            break;
                                        }
                                        i3++;
                                    }
                                    i2 = (z && certificateArr.length == objArrLoadCertificates.length) ? i2 + 1 : 0;
                                    Slog.e(TAG, "Package " + r17.packageName + " has mismatched certificates at entry " + jarEntryNextElement.getName() + "; ignoring!");
                                    jarFile.close();
                                    this.mParseError = PackageManager.INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES;
                                    return false;
                                }
                            }
                            certificateArr = objArrLoadCertificates;
                        }
                    }
                }
                certificateArrLoadCertificates = certificateArr;
            }
            jarFile.close();
            synchronized (mSync) {
                mReadBuffer = weakReference;
            }
            if (certificateArrLoadCertificates != null && certificateArrLoadCertificates.length > 0) {
                int length = certificateArrLoadCertificates.length;
                r17.mSignatures = new Signature[certificateArrLoadCertificates.length];
                for (int i4 = 0; i4 < length; i4++) {
                    r17.mSignatures[i4] = new Signature(certificateArrLoadCertificates[i4].getEncoded());
                }
                r17.mSigningKeys = new HashSet();
                for (Certificate certificate : certificateArrLoadCertificates) {
                    r17.mSigningKeys.add(certificate.getPublicKey());
                }
                return true;
            }
            Slog.e(TAG, "Package " + r17.packageName + " has no certificates; ignoring!");
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_NO_CERTIFICATES;
            return false;
        } catch (IOException e) {
            Slog.w(TAG, "Exception reading " + this.mArchiveSourcePath, e);
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING;
            return false;
        } catch (RuntimeException e2) {
            Slog.w(TAG, "Exception reading " + this.mArchiveSourcePath, e2);
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION;
            return false;
        } catch (CertificateEncodingException e3) {
            Slog.w(TAG, "Exception reading " + this.mArchiveSourcePath, e3);
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING;
            return false;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x006e  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x008a A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static android.content.pm.PackageParser.PackageLite parsePackageLite(java.lang.String r22, int r23) {
        /*
            r1 = r22
            java.lang.String r2 = "PackageParser"
            android.content.res.AssetManager r15 = new android.content.res.AssetManager     // Catch: java.lang.Exception -> L9a
            r15.<init>()     // Catch: java.lang.Exception -> L9a
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            r0 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            int r21 = android.os.Build.VERSION.RESOURCES_SDK_INT     // Catch: java.lang.Exception -> L97
            r4 = r15
            r3 = r15
            r15 = r0
            r4.setConfiguration(r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21)     // Catch: java.lang.Exception -> L94
            int r0 = r3.addAssetPath(r1)     // Catch: java.lang.Exception -> L94
            if (r0 != 0) goto L2e
            r4 = 0
            return r4
        L2e:
            r4 = 0
            android.util.DisplayMetrics r5 = new android.util.DisplayMetrics     // Catch: java.lang.Exception -> L94
            r5.<init>()     // Catch: java.lang.Exception -> L94
            r5.setToDefaults()     // Catch: java.lang.Exception -> L94
            android.content.res.Resources r6 = new android.content.res.Resources     // Catch: java.lang.Exception -> L94
            r6.<init>(r3, r5, r4)     // Catch: java.lang.Exception -> L94
            java.lang.String r4 = "AndroidManifest.xml"
            android.content.res.XmlResourceParser r4 = r3.openXmlResourceParser(r0, r4)     // Catch: java.lang.Exception -> L94
            r0 = 1
            java.lang.String[] r5 = new java.lang.String[r0]
            r0 = r23
            android.content.pm.PackageParser$PackageLite r0 = parsePackageLite(r6, r4, r4, r0, r5)     // Catch: java.lang.Throwable -> L54 org.xmlpull.v1.XmlPullParserException -> L56 java.io.IOException -> L5e
            if (r4 == 0) goto L50
            r4.close()
        L50:
            r3.close()
            goto L6c
        L54:
            r0 = move-exception
            goto L8b
        L56:
            r0 = move-exception
            r6 = r0
            android.util.Slog.w(r2, r1, r6)     // Catch: java.lang.Throwable -> L54
            if (r4 == 0) goto L68
            goto L65
        L5e:
            r0 = move-exception
            r6 = r0
            android.util.Slog.w(r2, r1, r6)     // Catch: java.lang.Throwable -> L54
            if (r4 == 0) goto L68
        L65:
            r4.close()
        L68:
            r3.close()
            r0 = 0
        L6c:
            if (r0 != 0) goto L8a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "parsePackageLite error: "
            java.lang.StringBuilder r0 = r0.append(r1)
            r1 = 0
            r1 = r5[r1]
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.String r0 = r0.toString()
            android.util.Slog.e(r2, r0)
        L88:
            r1 = 0
            return r1
        L8a:
            return r0
        L8b:
            if (r4 == 0) goto L90
            r4.close()
        L90:
            r3.close()
            throw r0
        L94:
            r0 = move-exception
            r15 = r3
            goto L9c
        L97:
            r0 = move-exception
            r3 = r15
            goto L9c
        L9a:
            r0 = move-exception
            r15 = 0
        L9c:
            if (r15 == 0) goto La1
            r15.close()
        La1:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Unable to read AndroidManifest.xml of "
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.StringBuilder r1 = r3.append(r1)
            java.lang.String r1 = r1.toString()
            android.util.Slog.w(r2, r1, r0)
            goto L88
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parsePackageLite(java.lang.String, int):android.content.pm.PackageParser$PackageLite");
    }

    private static String validateName(String str, boolean z) {
        int length = str.length();
        boolean z2 = false;
        boolean z3 = true;
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            if ((cCharAt >= 'a' && cCharAt <= 'z') || (cCharAt >= 'A' && cCharAt <= 'Z')) {
                z3 = false;
            } else if (z3 || ((cCharAt < '0' || cCharAt > '9') && cCharAt != '_')) {
                if (cCharAt != '.') {
                    return "bad character '" + cCharAt + "'";
                }
                z2 = true;
                z3 = true;
            }
        }
        if (z2 || !z) {
            return null;
        }
        return "must have at least one '.' separator";
    }

    private static String parsePackageName(XmlPullParser xmlPullParser, AttributeSet attributeSet, int i, String[] strArr) throws XmlPullParserException, IOException {
        int next;
        do {
            next = xmlPullParser.next();
            if (next == 2) {
                break;
            }
        } while (next != 1);
        if (next != 2) {
            strArr[0] = "No start tag found";
            return null;
        }
        if (!xmlPullParser.getName().equals("manifest")) {
            strArr[0] = "No <manifest> tag";
            return null;
        }
        String attributeValue = attributeSet.getAttributeValue(null, "package");
        if (attributeValue == null || attributeValue.length() == 0) {
            strArr[0] = "<manifest> does not specify package";
            return null;
        }
        String strValidateName = validateName(attributeValue, true);
        if (strValidateName != null && !"android".equals(attributeValue)) {
            strArr[0] = "<manifest> specifies bad package name \"" + attributeValue + "\": " + strValidateName;
            return null;
        }
        return attributeValue.intern();
    }

    /* JADX WARN: Removed duplicated region for block: B:37:0x0093 A[LOOP:1: B:26:0x0069->B:37:0x0093, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0096 A[EDGE_INSN: B:63:0x0096->B:38:0x0096 BREAK  A[LOOP:1: B:26:0x0069->B:37:0x0093], SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static android.content.pm.PackageParser.PackageLite parsePackageLite(android.content.res.Resources r11, org.xmlpull.v1.XmlPullParser r12, android.util.AttributeSet r13, int r14, java.lang.String[] r15) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 225
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parsePackageLite(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, int, java.lang.String[]):android.content.pm.PackageParser$PackageLite");
    }

    public static Signature stringToSignature(String str) {
        int length = str.length();
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            bArr[i] = (byte) str.charAt(i);
        }
        return new Signature(bArr);
    }

    /* JADX WARN: Code restructure failed: missing block: B:208:0x05c5, code lost:
    
        r13 = r0;
        r23 = r1;
        r10 = r2;
        r1 = r4;
        r0 = r12;
        r25 = r19;
        r19 = r17;
        r17 = r18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:209:0x05d3, code lost:
    
        if (r16 != false) goto L213;
     */
    /* JADX WARN: Code restructure failed: missing block: B:211:0x05db, code lost:
    
        if (r15.instrumentation.size() != 0) goto L213;
     */
    /* JADX WARN: Code restructure failed: missing block: B:212:0x05dd, code lost:
    
        r3 = 0;
        r31[0] = "<manifest> does not contain an <application> or <instrumentation>";
        r27.mParseError = android.content.pm.PackageManager.INSTALL_PARSE_FAILED_MANIFEST_EMPTY;
     */
    /* JADX WARN: Code restructure failed: missing block: B:213:0x05e7, code lost:
    
        r3 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:214:0x05e8, code lost:
    
        r2 = android.content.pm.PackageParser.NEW_PERMISSIONS.length;
        r12 = r0;
        r0 = r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:215:0x05ed, code lost:
    
        if (r0 >= r2) goto L310;
     */
    /* JADX WARN: Code restructure failed: missing block: B:216:0x05ef, code lost:
    
        r4 = android.content.pm.PackageParser.NEW_PERMISSIONS[r0];
     */
    /* JADX WARN: Code restructure failed: missing block: B:217:0x05f9, code lost:
    
        if (r15.applicationInfo.targetSdkVersion < r4.sdkVersion) goto L219;
     */
    /* JADX WARN: Code restructure failed: missing block: B:220:0x0604, code lost:
    
        if (r15.requestedPermissions.contains(r4.name) != false) goto L313;
     */
    /* JADX WARN: Code restructure failed: missing block: B:221:0x0606, code lost:
    
        if (r12 != 0) goto L223;
     */
    /* JADX WARN: Code restructure failed: missing block: B:222:0x0608, code lost:
    
        r12 = new java.lang.StringBuilder(128);
        r12.append(r15.packageName);
        r12.append(": compat added ");
        r12 = r12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:223:0x061a, code lost:
    
        r12.append(' ');
        r12 = r12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:224:0x061f, code lost:
    
        r12.append(r4.name);
        r15.requestedPermissions.add(r4.name);
        r15.requestedPermissionsRequired.add(java.lang.Boolean.TRUE);
     */
    /* JADX WARN: Code restructure failed: missing block: B:225:0x0632, code lost:
    
        r0 = r0 + 1;
        r12 = r12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:226:0x0635, code lost:
    
        if (r12 == 0) goto L228;
     */
    /* JADX WARN: Code restructure failed: missing block: B:227:0x0637, code lost:
    
        android.util.Slog.i(android.content.pm.PackageParser.TAG, r12.toString());
     */
    /* JADX WARN: Code restructure failed: missing block: B:228:0x063e, code lost:
    
        r0 = android.content.pm.PackageParser.SPLIT_PERMISSIONS.length;
        r2 = r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:229:0x0642, code lost:
    
        if (r2 >= r0) goto L314;
     */
    /* JADX WARN: Code restructure failed: missing block: B:230:0x0644, code lost:
    
        r4 = android.content.pm.PackageParser.SPLIT_PERMISSIONS[r2];
     */
    /* JADX WARN: Code restructure failed: missing block: B:231:0x064e, code lost:
    
        if (r15.applicationInfo.targetSdkVersion >= r4.targetSdk) goto L315;
     */
    /* JADX WARN: Code restructure failed: missing block: B:233:0x0658, code lost:
    
        if (r15.requestedPermissions.contains(r4.rootPerm) != false) goto L235;
     */
    /* JADX WARN: Code restructure failed: missing block: B:235:0x065b, code lost:
    
        r5 = r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:237:0x065f, code lost:
    
        if (r5 >= r4.newPerms.length) goto L316;
     */
    /* JADX WARN: Code restructure failed: missing block: B:238:0x0661, code lost:
    
        r6 = r4.newPerms[r5];
     */
    /* JADX WARN: Code restructure failed: missing block: B:239:0x066b, code lost:
    
        if (r15.requestedPermissions.contains(r6) != false) goto L319;
     */
    /* JADX WARN: Code restructure failed: missing block: B:240:0x066d, code lost:
    
        r15.requestedPermissions.add(r6);
        r15.requestedPermissionsRequired.add(java.lang.Boolean.TRUE);
     */
    /* JADX WARN: Code restructure failed: missing block: B:241:0x0679, code lost:
    
        r5 = r5 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:242:0x067c, code lost:
    
        r2 = r2 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:243:0x067f, code lost:
    
        if (r10 < 0) goto L247;
     */
    /* JADX WARN: Code restructure failed: missing block: B:244:0x0681, code lost:
    
        if (r10 <= 0) goto L248;
     */
    /* JADX WARN: Code restructure failed: missing block: B:246:0x0687, code lost:
    
        if (r15.applicationInfo.targetSdkVersion < r1) goto L248;
     */
    /* JADX WARN: Code restructure failed: missing block: B:247:0x0689, code lost:
    
        r15.applicationInfo.flags |= 512;
     */
    /* JADX WARN: Code restructure failed: missing block: B:248:0x0691, code lost:
    
        if (r23 == 0) goto L250;
     */
    /* JADX WARN: Code restructure failed: missing block: B:249:0x0693, code lost:
    
        r15.applicationInfo.flags |= 1024;
     */
    /* JADX WARN: Code restructure failed: missing block: B:250:0x069b, code lost:
    
        if (r13 < 0) goto L254;
     */
    /* JADX WARN: Code restructure failed: missing block: B:251:0x069d, code lost:
    
        if (r13 <= 0) goto L255;
     */
    /* JADX WARN: Code restructure failed: missing block: B:253:0x06a3, code lost:
    
        if (r15.applicationInfo.targetSdkVersion < r1) goto L255;
     */
    /* JADX WARN: Code restructure failed: missing block: B:254:0x06a5, code lost:
    
        r15.applicationInfo.flags |= 2048;
     */
    /* JADX WARN: Code restructure failed: missing block: B:255:0x06ad, code lost:
    
        if (r19 < 0) goto L259;
     */
    /* JADX WARN: Code restructure failed: missing block: B:256:0x06af, code lost:
    
        if (r19 <= 0) goto L260;
     */
    /* JADX WARN: Code restructure failed: missing block: B:258:0x06b7, code lost:
    
        if (r15.applicationInfo.targetSdkVersion < 9) goto L260;
     */
    /* JADX WARN: Code restructure failed: missing block: B:259:0x06b9, code lost:
    
        r15.applicationInfo.flags |= 524288;
     */
    /* JADX WARN: Code restructure failed: missing block: B:260:0x06c2, code lost:
    
        if (r17 < 0) goto L264;
     */
    /* JADX WARN: Code restructure failed: missing block: B:261:0x06c4, code lost:
    
        if (r17 <= 0) goto L265;
     */
    /* JADX WARN: Code restructure failed: missing block: B:263:0x06ca, code lost:
    
        if (r15.applicationInfo.targetSdkVersion < r1) goto L265;
     */
    /* JADX WARN: Code restructure failed: missing block: B:264:0x06cc, code lost:
    
        r15.applicationInfo.flags |= 4096;
     */
    /* JADX WARN: Code restructure failed: missing block: B:265:0x06d4, code lost:
    
        if (r25 < 0) goto L269;
     */
    /* JADX WARN: Code restructure failed: missing block: B:266:0x06d6, code lost:
    
        if (r25 <= 0) goto L270;
     */
    /* JADX WARN: Code restructure failed: missing block: B:268:0x06dc, code lost:
    
        if (r15.applicationInfo.targetSdkVersion < r1) goto L270;
     */
    /* JADX WARN: Code restructure failed: missing block: B:269:0x06de, code lost:
    
        r15.applicationInfo.flags |= 8192;
     */
    /* JADX WARN: Code restructure failed: missing block: B:271:0x06ec, code lost:
    
        if (r15.applicationInfo.targetSdkVersion >= 18) goto L276;
     */
    /* JADX WARN: Code restructure failed: missing block: B:272:0x06ee, code lost:
    
        r14 = r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:274:0x06f5, code lost:
    
        if (r14 >= r15.requestedPermissionsRequired.size()) goto L320;
     */
    /* JADX WARN: Code restructure failed: missing block: B:275:0x06f7, code lost:
    
        r15.requestedPermissionsRequired.set(r14, java.lang.Boolean.TRUE);
        r14 = r14 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:276:0x0701, code lost:
    
        return r15;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:121:0x02da  */
    /* JADX WARN: Removed duplicated region for block: B:127:0x02ed A[PHI: r2 r6
      0x02ed: PHI (r2v45 java.lang.Object) = (r2v111 java.lang.Object), (r2v112 java.lang.Object) binds: [B:120:0x02d8, B:125:0x02e2] A[DONT_GENERATE, DONT_INLINE]
      0x02ed: PHI (r6v26 java.lang.Object) = (r6v36 java.lang.Object), (r6v28 java.lang.Object) binds: [B:120:0x02d8, B:125:0x02e2] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:130:0x02fc  */
    /* JADX WARN: Removed duplicated region for block: B:138:0x0345  */
    /* JADX WARN: Removed duplicated region for block: B:141:0x0372  */
    /* JADX WARN: Removed duplicated region for block: B:148:0x03c1  */
    /* JADX WARN: Type inference failed for: r12v4 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private android.content.pm.PackageParser.Package parsePackage(android.content.res.Resources r28, android.content.res.XmlResourceParser r29, int r30, java.lang.String[] r31) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 1794
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parsePackage(android.content.res.Resources, android.content.res.XmlResourceParser, int, java.lang.String[]):android.content.pm.PackageParser$Package");
    }

    private boolean parseUsesPermission(Package r6, Resources resources, XmlResourceParser xmlResourceParser, AttributeSet attributeSet, String[] strArr) throws XmlPullParserException, IOException {
        TypedArray typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.AndroidManifestUsesPermission);
        String nonResourceString = typedArrayObtainAttributes.getNonResourceString(0);
        TypedValue typedValuePeekValue = typedArrayObtainAttributes.peekValue(1);
        int i = (typedValuePeekValue == null || typedValuePeekValue.type < 16 || typedValuePeekValue.type > 31) ? 0 : typedValuePeekValue.data;
        typedArrayObtainAttributes.recycle();
        if ((i == 0 || i >= Build.VERSION.RESOURCES_SDK_INT) && nonResourceString != null) {
            int iIndexOf = r6.requestedPermissions.indexOf(nonResourceString);
            if (iIndexOf == -1) {
                r6.requestedPermissions.add(nonResourceString.intern());
                r6.requestedPermissionsRequired.add(Boolean.TRUE);
            } else if (!r6.requestedPermissionsRequired.get(iIndexOf).booleanValue()) {
                strArr[0] = "conflicting <uses-permission> entries";
                this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
                return false;
            }
        }
        XmlUtils.skipCurrentTag(xmlResourceParser);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String buildClassName(String str, CharSequence charSequence, String[] strArr) {
        if (charSequence == null || charSequence.length() <= 0) {
            strArr[0] = "Empty class name in package " + str;
            return null;
        }
        String string = charSequence.toString();
        char cCharAt = string.charAt(0);
        if (cCharAt == '.') {
            return (str + string).intern();
        }
        if (string.indexOf(46) < 0) {
            return (str + '.' + string).intern();
        }
        if (cCharAt >= 'a' && cCharAt <= 'z') {
            return string.intern();
        }
        strArr[0] = "Bad class name " + string + " in package " + str;
        return null;
    }

    private static String buildCompoundName(String str, CharSequence charSequence, String str2, String[] strArr) {
        String string = charSequence.toString();
        char cCharAt = string.charAt(0);
        if (str != null && cCharAt == ':') {
            if (string.length() < 2) {
                strArr[0] = "Bad " + str2 + " name " + string + " in package " + str + ": must be at least two characters";
                return null;
            }
            String strValidateName = validateName(string.substring(1), false);
            if (strValidateName != null) {
                strArr[0] = "Invalid " + str2 + " name " + string + " in package " + str + ": " + strValidateName;
                return null;
            }
            return (str + string).intern();
        }
        String strValidateName2 = validateName(string, true);
        if (strValidateName2 != null && !"system".equals(string)) {
            strArr[0] = "Invalid " + str2 + " name " + string + " in package " + str + ": " + strValidateName2;
            return null;
        }
        return string.intern();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String buildProcessName(String str, String str2, CharSequence charSequence, int i, String[] strArr, String[] strArr2) {
        if ((i & 8) != 0 && !"system".equals(charSequence)) {
            return str2 != null ? str2 : str;
        }
        if (strArr != null) {
            for (int length = strArr.length - 1; length >= 0; length--) {
                String str3 = strArr[length];
                if (str3.equals(str) || str3.equals(str2) || str3.equals(charSequence)) {
                    return str;
                }
            }
        }
        return (charSequence == null || charSequence.length() <= 0) ? str2 : buildCompoundName(str, charSequence, AptCompilerAdapter.APT_METHOD_NAME, strArr2);
    }

    private static String buildTaskAffinityName(String str, String str2, CharSequence charSequence, String[] strArr) {
        if (charSequence == null) {
            return str2;
        }
        if (charSequence.length() <= 0) {
            return null;
        }
        return buildCompoundName(str, charSequence, "taskAffinity", strArr);
    }

    private boolean parseKeys(Package r10, Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, String[] strArr) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth();
        HashMap map = new HashMap();
        loop0: while (true) {
            int depth2 = -1;
            PublicKey publicKey = null;
            while (true) {
                int next = xmlPullParser.next();
                if (next == 1 || (next == 3 && xmlPullParser.getDepth() <= depth)) {
                    break loop0;
                }
                if (next == 3) {
                    if (xmlPullParser.getDepth() == depth2) {
                        break;
                    }
                } else {
                    String name = xmlPullParser.getName();
                    if (name.equals("publicKey")) {
                        TypedArray typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.PublicKey);
                        PublicKey publicKey2 = parsePublicKey(typedArrayObtainAttributes.getNonResourceString(0));
                        if (publicKey2 == null) {
                            Slog.w(TAG, "No valid key in 'publicKey' tag at " + xmlPullParser.getPositionDescription());
                            typedArrayObtainAttributes.recycle();
                        } else {
                            depth2 = xmlPullParser.getDepth();
                            map.put(publicKey2, new HashSet());
                            typedArrayObtainAttributes.recycle();
                        }
                        publicKey = publicKey2;
                    } else if (!name.equals("keyset")) {
                        Slog.w(TAG, "Unknown element under <keys>: " + xmlPullParser.getName() + " at " + this.mArchiveSourcePath + " " + xmlPullParser.getPositionDescription());
                        XmlUtils.skipCurrentTag(xmlPullParser);
                    } else if (publicKey == null) {
                        Slog.i(TAG, "'keyset' not in 'publicKey' tag at " + xmlPullParser.getPositionDescription());
                    } else {
                        TypedArray typedArrayObtainAttributes2 = resources.obtainAttributes(attributeSet, R.styleable.KeySet);
                        ((Set) map.get(publicKey)).add(typedArrayObtainAttributes2.getNonResourceString(0));
                        typedArrayObtainAttributes2.recycle();
                    }
                }
            }
        }
        r10.mKeySetMapping = new HashMap();
        for (Map.Entry entry : map.entrySet()) {
            PublicKey publicKey3 = (PublicKey) entry.getKey();
            for (String str : (Set) entry.getValue()) {
                if (r10.mKeySetMapping.containsKey(str)) {
                    r10.mKeySetMapping.get(str).add(publicKey3);
                } else {
                    HashSet hashSet = new HashSet();
                    hashSet.add(publicKey3);
                    r10.mKeySetMapping.put(str, hashSet);
                }
            }
        }
        return true;
    }

    private PermissionGroup parsePermissionGroup(Package r17, int i, Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, String[] strArr) throws XmlPullParserException, IOException {
        PermissionGroup permissionGroup = new PermissionGroup(r17);
        TypedArray typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.AndroidManifestPermissionGroup);
        if (!parsePackageItemInfo(r17, permissionGroup.info, strArr, "<permission-group>", typedArrayObtainAttributes, 2, 0, 1, 5)) {
            typedArrayObtainAttributes.recycle();
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        permissionGroup.info.descriptionRes = typedArrayObtainAttributes.getResourceId(4, 0);
        permissionGroup.info.flags = typedArrayObtainAttributes.getInt(6, 0);
        permissionGroup.info.priority = typedArrayObtainAttributes.getInt(3, 0);
        if (permissionGroup.info.priority > 0 && (i & 1) == 0) {
            permissionGroup.info.priority = 0;
        }
        typedArrayObtainAttributes.recycle();
        if (!parseAllMetaData(resources, xmlPullParser, attributeSet, "<permission-group>", permissionGroup, strArr)) {
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        r17.permissionGroups.add(permissionGroup);
        return permissionGroup;
    }

    private Permission parsePermission(Package r17, Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, String[] strArr) throws XmlPullParserException, IOException {
        Permission permission = new Permission(r17);
        TypedArray typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.AndroidManifestPermission);
        if (!parsePackageItemInfo(r17, permission.info, strArr, "<permission>", typedArrayObtainAttributes, 2, 0, 1, 6)) {
            typedArrayObtainAttributes.recycle();
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        permission.info.group = typedArrayObtainAttributes.getNonResourceString(4);
        if (permission.info.group != null) {
            permission.info.group = permission.info.group.intern();
        }
        permission.info.descriptionRes = typedArrayObtainAttributes.getResourceId(5, 0);
        permission.info.protectionLevel = typedArrayObtainAttributes.getInt(3, 0);
        permission.info.flags = typedArrayObtainAttributes.getInt(7, 0);
        typedArrayObtainAttributes.recycle();
        if (permission.info.protectionLevel == -1) {
            strArr[0] = "<permission> does not specify protectionLevel";
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        permission.info.protectionLevel = PermissionInfo.fixProtectionLevel(permission.info.protectionLevel);
        if ((permission.info.protectionLevel & 240) != 0 && (permission.info.protectionLevel & 15) != 2) {
            strArr[0] = "<permission>  protectionLevel specifies a flag but is not based on signature type";
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        if (!parseAllMetaData(resources, xmlPullParser, attributeSet, "<permission>", permission, strArr)) {
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        r17.permissions.add(permission);
        return permission;
    }

    private Permission parsePermissionTree(Package r17, Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, String[] strArr) throws XmlPullParserException, IOException {
        Permission permission = new Permission(r17);
        TypedArray typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.AndroidManifestPermissionTree);
        if (!parsePackageItemInfo(r17, permission.info, strArr, "<permission-tree>", typedArrayObtainAttributes, 2, 0, 1, 3)) {
            typedArrayObtainAttributes.recycle();
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        typedArrayObtainAttributes.recycle();
        int iIndexOf = permission.info.name.indexOf(46);
        if (iIndexOf > 0) {
            iIndexOf = permission.info.name.indexOf(46, iIndexOf + 1);
        }
        if (iIndexOf < 0) {
            strArr[0] = "<permission-tree> name has less than three segments: " + permission.info.name;
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        permission.info.descriptionRes = 0;
        permission.info.protectionLevel = 0;
        permission.tree = true;
        if (!parseAllMetaData(resources, xmlPullParser, attributeSet, "<permission-tree>", permission, strArr)) {
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        r17.permissions.add(permission);
        return permission;
    }

    private Instrumentation parseInstrumentation(Package r15, Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, String[] strArr) throws XmlPullParserException, IOException {
        TypedArray typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.AndroidManifestInstrumentation);
        if (this.mParseInstrumentationArgs == null) {
            ParsePackageItemArgs parsePackageItemArgs = new ParsePackageItemArgs(r15, strArr, 2, 0, 1, 6);
            this.mParseInstrumentationArgs = parsePackageItemArgs;
            parsePackageItemArgs.tag = "<instrumentation>";
        }
        this.mParseInstrumentationArgs.sa = typedArrayObtainAttributes;
        Instrumentation instrumentation = new Instrumentation(this.mParseInstrumentationArgs, new InstrumentationInfo());
        if (strArr[0] != null) {
            typedArrayObtainAttributes.recycle();
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        String nonResourceString = typedArrayObtainAttributes.getNonResourceString(3);
        instrumentation.info.targetPackage = nonResourceString != null ? nonResourceString.intern() : null;
        instrumentation.info.handleProfiling = typedArrayObtainAttributes.getBoolean(4, false);
        instrumentation.info.functionalTest = typedArrayObtainAttributes.getBoolean(5, false);
        typedArrayObtainAttributes.recycle();
        if (instrumentation.info.targetPackage == null) {
            strArr[0] = "<instrumentation> does not specify targetPackage";
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        if (!parseAllMetaData(resources, xmlPullParser, attributeSet, "<instrumentation>", instrumentation, strArr)) {
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        r15.instrumentation.add(instrumentation);
        return instrumentation;
    }

    /* JADX WARN: Code restructure failed: missing block: B:181:0x03a3, code lost:
    
        return r8;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean parseApplication(android.content.pm.PackageParser.Package r22, android.content.res.Resources r23, org.xmlpull.v1.XmlPullParser r24, android.util.AttributeSet r25, int r26, java.lang.String[] r27) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 932
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseApplication(android.content.pm.PackageParser$Package, android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, int, java.lang.String[]):boolean");
    }

    private boolean parsePackageItemInfo(Package r2, PackageItemInfo packageItemInfo, String[] strArr, String str, TypedArray typedArray, int i, int i2, int i3, int i4) {
        String nonConfigurationString = typedArray.getNonConfigurationString(i, 0);
        if (nonConfigurationString == null) {
            strArr[0] = str + " does not specify android:name";
            return false;
        }
        packageItemInfo.name = buildClassName(r2.applicationInfo.packageName, nonConfigurationString, strArr);
        if (packageItemInfo.name == null) {
            return false;
        }
        int resourceId = typedArray.getResourceId(i3, 0);
        if (resourceId != 0) {
            packageItemInfo.icon = resourceId;
            packageItemInfo.nonLocalizedLabel = null;
        }
        int resourceId2 = typedArray.getResourceId(i4, 0);
        if (resourceId2 != 0) {
            packageItemInfo.logo = resourceId2;
        }
        TypedValue typedValuePeekValue = typedArray.peekValue(i2);
        if (typedValuePeekValue != null) {
            int i5 = typedValuePeekValue.resourceId;
            packageItemInfo.labelRes = i5;
            if (i5 == 0) {
                packageItemInfo.nonLocalizedLabel = typedValuePeekValue.coerceToString();
            }
        }
        packageItemInfo.packageName = r2.packageName;
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:144:0x041e, code lost:
    
        if (r15 != false) goto L150;
     */
    /* JADX WARN: Code restructure failed: missing block: B:145:0x0420, code lost:
    
        r0 = r8.info;
     */
    /* JADX WARN: Code restructure failed: missing block: B:146:0x0428, code lost:
    
        if (r8.intents.size() <= 0) goto L148;
     */
    /* JADX WARN: Code restructure failed: missing block: B:147:0x042a, code lost:
    
        r9 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:148:0x042c, code lost:
    
        r9 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:149:0x042d, code lost:
    
        r0.exported = r9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:150:0x042f, code lost:
    
        return r8;
     */
    /* JADX WARN: Type inference incomplete: some casts might be missing */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private android.content.pm.PackageParser.Activity parseActivity(android.content.pm.PackageParser.Package r20, android.content.res.Resources r21, org.xmlpull.v1.XmlPullParser r22, android.util.AttributeSet r23, int r24, java.lang.String[] r25, boolean r26, boolean r27) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 1072
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseActivity(android.content.pm.PackageParser$Package, android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, int, java.lang.String[], boolean, boolean):android.content.pm.PackageParser$Activity");
    }

    /* JADX WARN: Code restructure failed: missing block: B:77:0x0272, code lost:
    
        r16 = r4 == true ? 1 : 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x0274, code lost:
    
        if (r9 != false) goto L84;
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x0276, code lost:
    
        r0 = r8.info;
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x027e, code lost:
    
        if (r8.intents.size() <= 0) goto L82;
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x0281, code lost:
    
        r15 = r16 ? 1 : 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:83:0x0283, code lost:
    
        r0.exported = r15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x0285, code lost:
    
        return r8;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r15v1 */
    /* JADX WARN: Type inference failed for: r15v2, types: [int] */
    /* JADX WARN: Type inference failed for: r15v8 */
    /* JADX WARN: Type inference failed for: r1v1, types: [android.content.res.TypedArray] */
    /* JADX WARN: Type inference failed for: r4v1 */
    /* JADX WARN: Type inference failed for: r4v2, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r4v3 */
    /* JADX WARN: Type inference failed for: r4v5 */
    /* JADX WARN: Type inference failed for: r4v7 */
    /* JADX WARN: Type inference failed for: r4v8 */
    /* JADX WARN: Type inference incomplete: some casts might be missing */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private android.content.pm.PackageParser.Activity parseActivityAlias(android.content.pm.PackageParser.Package r23, android.content.res.Resources r24, org.xmlpull.v1.XmlPullParser r25, android.util.AttributeSet r26, int r27, java.lang.String[] r28) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 646
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseActivityAlias(android.content.pm.PackageParser$Package, android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, int, java.lang.String[]):android.content.pm.PackageParser$Activity");
    }

    private Provider parseProvider(Package r19, Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, int i, String[] strArr) throws XmlPullParserException, IOException {
        TypedArray typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.AndroidManifestProvider);
        if (this.mParseProviderArgs == null) {
            ParseComponentArgs parseComponentArgs = new ParseComponentArgs(r19, strArr, 2, 0, 1, 15, this.mSeparateProcesses, 8, 14, 6);
            this.mParseProviderArgs = parseComponentArgs;
            parseComponentArgs.tag = "<provider>";
        }
        this.mParseProviderArgs.sa = typedArrayObtainAttributes;
        this.mParseProviderArgs.flags = i;
        Provider provider = new Provider(this.mParseProviderArgs, new ProviderInfo());
        if (strArr[0] != null) {
            typedArrayObtainAttributes.recycle();
            return null;
        }
        provider.info.exported = typedArrayObtainAttributes.getBoolean(7, r19.applicationInfo.targetSdkVersion < 17);
        String nonConfigurationString = typedArrayObtainAttributes.getNonConfigurationString(10, 0);
        provider.info.isSyncable = typedArrayObtainAttributes.getBoolean(11, false);
        String nonConfigurationString2 = typedArrayObtainAttributes.getNonConfigurationString(3, 0);
        String nonConfigurationString3 = typedArrayObtainAttributes.getNonConfigurationString(4, 0);
        if (nonConfigurationString3 == null) {
            nonConfigurationString3 = nonConfigurationString2;
        }
        if (nonConfigurationString3 == null) {
            provider.info.readPermission = r19.applicationInfo.permission;
        } else {
            provider.info.readPermission = nonConfigurationString3.length() > 0 ? nonConfigurationString3.toString().intern() : null;
        }
        String nonConfigurationString4 = typedArrayObtainAttributes.getNonConfigurationString(5, 0);
        if (nonConfigurationString4 != null) {
            nonConfigurationString2 = nonConfigurationString4;
        }
        if (nonConfigurationString2 == null) {
            provider.info.writePermission = r19.applicationInfo.permission;
        } else {
            provider.info.writePermission = nonConfigurationString2.length() > 0 ? nonConfigurationString2.toString().intern() : null;
        }
        provider.info.grantUriPermissions = typedArrayObtainAttributes.getBoolean(13, false);
        provider.info.multiprocess = typedArrayObtainAttributes.getBoolean(9, false);
        provider.info.initOrder = typedArrayObtainAttributes.getInt(12, 0);
        provider.info.flags = 0;
        if (typedArrayObtainAttributes.getBoolean(16, false)) {
            provider.info.flags |= 1073741824;
            if (provider.info.exported) {
                Slog.w(TAG, "Provider exported request ignored due to singleUser: " + provider.className + " at " + this.mArchiveSourcePath + " " + xmlPullParser.getPositionDescription());
                provider.info.exported = false;
            }
        }
        typedArrayObtainAttributes.recycle();
        if ((r19.applicationInfo.flags & 268435456) != 0 && provider.info.processName == r19.packageName) {
            strArr[0] = "Heavy-weight applications can not have providers in main process";
            return null;
        }
        if (nonConfigurationString == null) {
            strArr[0] = "<provider> does not include authorities attribute";
            return null;
        }
        provider.info.authority = nonConfigurationString.intern();
        if (parseProviderTags(resources, xmlPullParser, attributeSet, provider, strArr)) {
            return provider;
        }
        return null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:79:0x0255, code lost:
    
        return true;
     */
    /* JADX WARN: Type inference incomplete: some casts might be missing */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean parseProviderTags(android.content.res.Resources r19, org.xmlpull.v1.XmlPullParser r20, android.util.AttributeSet r21, android.content.pm.PackageParser.Provider r22, java.lang.String[] r23) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 598
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseProviderTags(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.content.pm.PackageParser$Provider, java.lang.String[]):boolean");
    }

    /* JADX WARN: Code restructure failed: missing block: B:63:0x01c3, code lost:
    
        if (r16 != false) goto L68;
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x01c5, code lost:
    
        r0 = r8.info;
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x01cd, code lost:
    
        if (r8.intents.size() <= 0) goto L67;
     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x01cf, code lost:
    
        r9 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x01d0, code lost:
    
        r0.exported = r9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x01d2, code lost:
    
        return r8;
     */
    /* JADX WARN: Type inference incomplete: some casts might be missing */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private android.content.pm.PackageParser.Service parseService(android.content.pm.PackageParser.Package r20, android.content.res.Resources r21, org.xmlpull.v1.XmlPullParser r22, android.util.AttributeSet r23, int r24, java.lang.String[] r25) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 467
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseService(android.content.pm.PackageParser$Package, android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, int, java.lang.String[]):android.content.pm.PackageParser$Service");
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x007b, code lost:
    
        return true;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean parseAllMetaData(android.content.res.Resources r9, org.xmlpull.v1.XmlPullParser r10, android.util.AttributeSet r11, java.lang.String r12, android.content.pm.PackageParser.Component r13, java.lang.String[] r14) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r8 = this;
            int r0 = r10.getDepth()
        L4:
            int r1 = r10.next()
            r2 = 1
            if (r1 == r2) goto L7b
            r3 = 3
            if (r1 != r3) goto L14
            int r4 = r10.getDepth()
            if (r4 <= r0) goto L7b
        L14:
            if (r1 == r3) goto L4
            r2 = 4
            if (r1 != r2) goto L1a
            goto L4
        L1a:
            java.lang.String r1 = r10.getName()
            java.lang.String r2 = "meta-data"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L37
            android.os.Bundle r6 = r13.metaData
            r2 = r8
            r3 = r9
            r4 = r10
            r5 = r11
            r7 = r14
            android.os.Bundle r1 = r2.parseMetaData(r3, r4, r5, r6, r7)
            r13.metaData = r1
            if (r1 != 0) goto L4
            r9 = 0
            return r9
        L37:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Unknown element under "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.StringBuilder r1 = r1.append(r12)
            java.lang.String r2 = ": "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r2 = r10.getName()
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r2 = " at "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r2 = r8.mArchiveSourcePath
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r2 = " "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r2 = r10.getPositionDescription()
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "PackageParser"
            android.util.Slog.w(r2, r1)
            com.android.internal.util.XmlUtils.skipCurrentTag(r10)
            goto L4
        L7b:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseAllMetaData(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, java.lang.String, android.content.pm.PackageParser$Component, java.lang.String[]):boolean");
    }

    private Bundle parseMetaData(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Bundle bundle, String[] strArr) throws XmlPullParserException, IOException {
        TypedArray typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.AndroidManifestMetaData);
        if (bundle == null) {
            bundle = new Bundle();
        }
        String nonConfigurationString = typedArrayObtainAttributes.getNonConfigurationString(0, 0);
        if (nonConfigurationString == null) {
            strArr[0] = "<meta-data> requires an android:name attribute";
            typedArrayObtainAttributes.recycle();
            return null;
        }
        String strIntern = nonConfigurationString.intern();
        TypedValue typedValuePeekValue = typedArrayObtainAttributes.peekValue(2);
        if (typedValuePeekValue != null && typedValuePeekValue.resourceId != 0) {
            bundle.putInt(strIntern, typedValuePeekValue.resourceId);
        } else {
            TypedValue typedValuePeekValue2 = typedArrayObtainAttributes.peekValue(1);
            if (typedValuePeekValue2 != null) {
                if (typedValuePeekValue2.type == 3) {
                    CharSequence charSequenceCoerceToString = typedValuePeekValue2.coerceToString();
                    bundle.putString(strIntern, charSequenceCoerceToString != null ? charSequenceCoerceToString.toString().intern() : null);
                } else if (typedValuePeekValue2.type == 18) {
                    bundle.putBoolean(strIntern, typedValuePeekValue2.data != 0);
                } else if (typedValuePeekValue2.type >= 16 && typedValuePeekValue2.type <= 31) {
                    bundle.putInt(strIntern, typedValuePeekValue2.data);
                } else if (typedValuePeekValue2.type == 4) {
                    bundle.putFloat(strIntern, typedValuePeekValue2.getFloat());
                } else {
                    Slog.w(TAG, "<meta-data> only supports string, integer, float, color, boolean, and resource reference types: " + xmlPullParser.getName() + " at " + this.mArchiveSourcePath + " " + xmlPullParser.getPositionDescription());
                }
            } else {
                strArr[0] = "<meta-data> requires an android:value or android:resource attribute";
                bundle = null;
            }
        }
        typedArrayObtainAttributes.recycle();
        XmlUtils.skipCurrentTag(xmlPullParser);
        return bundle;
    }

    private static VerifierInfo parseVerifier(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, int i, String[] strArr) throws XmlPullParserException, IOException {
        TypedArray typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.AndroidManifestPackageVerifier);
        String nonResourceString = typedArrayObtainAttributes.getNonResourceString(0);
        String nonResourceString2 = typedArrayObtainAttributes.getNonResourceString(1);
        typedArrayObtainAttributes.recycle();
        if (nonResourceString == null || nonResourceString.length() == 0) {
            Slog.i(TAG, "verifier package name was null; skipping");
            return null;
        }
        if (nonResourceString2 == null) {
            Slog.i(TAG, "verifier " + nonResourceString + " public key was null; skipping");
        }
        PublicKey publicKey = parsePublicKey(nonResourceString2);
        if (publicKey != null) {
            return new VerifierInfo(nonResourceString, publicKey);
        }
        return null;
    }

    public static final PublicKey parsePublicKey(String str) {
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decode(str, 0));
            try {
                try {
                    return KeyFactory.getInstance("RSA").generatePublic(x509EncodedKeySpec);
                } catch (NoSuchAlgorithmException unused) {
                    Log.wtf(TAG, "Could not parse public key because DSA isn't included in build");
                    return null;
                } catch (InvalidKeySpecException unused2) {
                    return null;
                }
            } catch (NoSuchAlgorithmException unused3) {
                Log.wtf(TAG, "Could not parse public key because RSA isn't included in build");
                return null;
            } catch (InvalidKeySpecException unused4) {
                return KeyFactory.getInstance("DSA").generatePublic(x509EncodedKeySpec);
            }
        } catch (IllegalArgumentException unused5) {
            Slog.i(TAG, "Could not parse verifier public key; invalid Base64");
            return null;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x0075, code lost:
    
        r21[0] = "No value supplied for <android:name>";
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0077, code lost:
    
        return false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x0091, code lost:
    
        r21[0] = "No value supplied for <android:name>";
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0093, code lost:
    
        return false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x0161, code lost:
    
        r20.hasDefault = r20.hasCategory(android.content.Intent.CATEGORY_DEFAULT);
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x016a, code lost:
    
        return true;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean parseIntent(android.content.res.Resources r16, org.xmlpull.v1.XmlPullParser r17, android.util.AttributeSet r18, boolean r19, android.content.pm.PackageParser.IntentInfo r20, java.lang.String[] r21) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 363
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseIntent(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, boolean, android.content.pm.PackageParser$IntentInfo, java.lang.String[]):boolean");
    }

    public static final class Package {
        public final ArrayList<Activity> activities;
        public final ApplicationInfo applicationInfo;
        public final ArrayList<ConfigurationInfo> configPreferences;
        public int installLocation;
        public final ArrayList<Instrumentation> instrumentation;
        public ArrayList<String> libraryNames;
        public ArrayList<String> mAdoptPermissions;
        public Bundle mAppMetaData;
        public boolean mDidDexOpt;
        public Object mExtras;
        public Map<String, Set<PublicKey>> mKeySetMapping;
        public boolean mOperationPending;
        public ArrayList<String> mOriginalPackages;
        public String mPath;
        public int mPreferredOrder;
        public String mRealPackage;
        public String mRequiredAccountType;
        public boolean mRequiredForAllUsers;
        public String mRestrictedAccountType;
        public String mScanPath;
        public String mSharedUserId;
        public int mSharedUserLabel;
        public Signature[] mSignatures;
        public Set<PublicKey> mSigningKeys;
        public int mVersionCode;
        public String mVersionName;
        public ManifestDigest manifestDigest;
        public String packageName;
        public final ArrayList<PermissionGroup> permissionGroups;
        public final ArrayList<Permission> permissions;
        public ArrayList<ActivityIntentInfo> preferredActivityFilters;
        public ArrayList<String> protectedBroadcasts;
        public final ArrayList<Provider> providers;
        public final ArrayList<Activity> receivers;
        public ArrayList<FeatureInfo> reqFeatures;
        public final ArrayList<String> requestedPermissions;
        public final ArrayList<Boolean> requestedPermissionsRequired;
        public final ArrayList<Service> services;
        public ArrayList<String> usesLibraries;
        public String[] usesLibraryFiles;
        public ArrayList<String> usesOptionalLibraries;

        public Package(String str) {
            ApplicationInfo applicationInfo = new ApplicationInfo();
            this.applicationInfo = applicationInfo;
            this.permissions = new ArrayList<>(0);
            this.permissionGroups = new ArrayList<>(0);
            this.activities = new ArrayList<>(0);
            this.receivers = new ArrayList<>(0);
            this.providers = new ArrayList<>(0);
            this.services = new ArrayList<>(0);
            this.instrumentation = new ArrayList<>(0);
            this.requestedPermissions = new ArrayList<>();
            this.requestedPermissionsRequired = new ArrayList<>();
            this.libraryNames = null;
            this.usesLibraries = null;
            this.usesOptionalLibraries = null;
            this.usesLibraryFiles = null;
            this.preferredActivityFilters = null;
            this.mOriginalPackages = null;
            this.mRealPackage = null;
            this.mAdoptPermissions = null;
            this.mAppMetaData = null;
            this.mPreferredOrder = 0;
            this.configPreferences = new ArrayList<>();
            this.reqFeatures = null;
            this.packageName = str;
            applicationInfo.packageName = str;
            applicationInfo.uid = -1;
        }

        public void setPackageName(String str) {
            this.packageName = str;
            this.applicationInfo.packageName = str;
            for (int size = this.permissions.size() - 1; size >= 0; size--) {
                this.permissions.get(size).setPackageName(str);
            }
            for (int size2 = this.permissionGroups.size() - 1; size2 >= 0; size2--) {
                this.permissionGroups.get(size2).setPackageName(str);
            }
            for (int size3 = this.activities.size() - 1; size3 >= 0; size3--) {
                this.activities.get(size3).setPackageName(str);
            }
            for (int size4 = this.receivers.size() - 1; size4 >= 0; size4--) {
                this.receivers.get(size4).setPackageName(str);
            }
            for (int size5 = this.providers.size() - 1; size5 >= 0; size5--) {
                this.providers.get(size5).setPackageName(str);
            }
            for (int size6 = this.services.size() - 1; size6 >= 0; size6--) {
                this.services.get(size6).setPackageName(str);
            }
            for (int size7 = this.instrumentation.size() - 1; size7 >= 0; size7--) {
                this.instrumentation.get(size7).setPackageName(str);
            }
        }

        public boolean hasComponentClassName(String str) {
            for (int size = this.activities.size() - 1; size >= 0; size--) {
                if (str.equals(this.activities.get(size).className)) {
                    return true;
                }
            }
            for (int size2 = this.receivers.size() - 1; size2 >= 0; size2--) {
                if (str.equals(this.receivers.get(size2).className)) {
                    return true;
                }
            }
            for (int size3 = this.providers.size() - 1; size3 >= 0; size3--) {
                if (str.equals(this.providers.get(size3).className)) {
                    return true;
                }
            }
            for (int size4 = this.services.size() - 1; size4 >= 0; size4--) {
                if (str.equals(this.services.get(size4).className)) {
                    return true;
                }
            }
            for (int size5 = this.instrumentation.size() - 1; size5 >= 0; size5--) {
                if (str.equals(this.instrumentation.get(size5).className)) {
                    return true;
                }
            }
            return false;
        }

        public String toString() {
            return "Package{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.packageName + "}";
        }
    }

    public static class Component<II extends IntentInfo> {
        public final String className;
        ComponentName componentName;
        String componentShortName;
        public final ArrayList<II> intents;
        public Bundle metaData;
        public final Package owner;

        public Component(Package r1) {
            this.owner = r1;
            this.intents = null;
            this.className = null;
        }

        public Component(ParsePackageItemArgs parsePackageItemArgs, PackageItemInfo packageItemInfo) {
            Package r0 = parsePackageItemArgs.owner;
            this.owner = r0;
            this.intents = new ArrayList<>(0);
            String nonConfigurationString = parsePackageItemArgs.sa.getNonConfigurationString(parsePackageItemArgs.nameRes, 0);
            if (nonConfigurationString != null) {
                packageItemInfo.name = PackageParser.buildClassName(r0.applicationInfo.packageName, nonConfigurationString, parsePackageItemArgs.outError);
                if (packageItemInfo.name == null) {
                    this.className = null;
                    parsePackageItemArgs.outError[0] = parsePackageItemArgs.tag + " does not have valid android:name";
                    return;
                }
                this.className = packageItemInfo.name;
                int resourceId = parsePackageItemArgs.sa.getResourceId(parsePackageItemArgs.iconRes, 0);
                if (resourceId != 0) {
                    packageItemInfo.icon = resourceId;
                    packageItemInfo.nonLocalizedLabel = null;
                }
                int resourceId2 = parsePackageItemArgs.sa.getResourceId(parsePackageItemArgs.logoRes, 0);
                if (resourceId2 != 0) {
                    packageItemInfo.logo = resourceId2;
                }
                TypedValue typedValuePeekValue = parsePackageItemArgs.sa.peekValue(parsePackageItemArgs.labelRes);
                if (typedValuePeekValue != null) {
                    int i = typedValuePeekValue.resourceId;
                    packageItemInfo.labelRes = i;
                    if (i == 0) {
                        packageItemInfo.nonLocalizedLabel = typedValuePeekValue.coerceToString();
                    }
                }
                packageItemInfo.packageName = r0.packageName;
                return;
            }
            this.className = null;
            parsePackageItemArgs.outError[0] = parsePackageItemArgs.tag + " does not specify android:name";
        }

        public Component(ParseComponentArgs parseComponentArgs, ComponentInfo componentInfo) {
            String nonResourceString;
            this((ParsePackageItemArgs) parseComponentArgs, (PackageItemInfo) componentInfo);
            if (parseComponentArgs.outError[0] != null) {
                return;
            }
            if (parseComponentArgs.processRes != 0) {
                if (this.owner.applicationInfo.targetSdkVersion >= 8) {
                    nonResourceString = parseComponentArgs.sa.getNonConfigurationString(parseComponentArgs.processRes, 1024);
                } else {
                    nonResourceString = parseComponentArgs.sa.getNonResourceString(parseComponentArgs.processRes);
                }
                componentInfo.processName = PackageParser.buildProcessName(this.owner.applicationInfo.packageName, this.owner.applicationInfo.processName, nonResourceString, parseComponentArgs.flags, parseComponentArgs.sepProcesses, parseComponentArgs.outError);
            }
            if (parseComponentArgs.descriptionRes != 0) {
                componentInfo.descriptionRes = parseComponentArgs.sa.getResourceId(parseComponentArgs.descriptionRes, 0);
            }
            componentInfo.enabled = parseComponentArgs.sa.getBoolean(parseComponentArgs.enabledRes, true);
        }

        public Component(Component<II> component) {
            this.owner = component.owner;
            this.intents = component.intents;
            this.className = component.className;
            this.componentName = component.componentName;
            this.componentShortName = component.componentShortName;
        }

        public ComponentName getComponentName() {
            ComponentName componentName = this.componentName;
            if (componentName != null) {
                return componentName;
            }
            if (this.className != null) {
                this.componentName = new ComponentName(this.owner.applicationInfo.packageName, this.className);
            }
            return this.componentName;
        }

        public void appendComponentShortName(StringBuilder sb) {
            ComponentName.appendShortString(sb, this.owner.applicationInfo.packageName, this.className);
        }

        public void printComponentShortName(PrintWriter printWriter) {
            ComponentName.printShortString(printWriter, this.owner.applicationInfo.packageName, this.className);
        }

        public void setPackageName(String str) {
            this.componentName = null;
            this.componentShortName = null;
        }
    }

    public static final class Permission extends Component<IntentInfo> {
        public PermissionGroup group;
        public final PermissionInfo info;
        public boolean tree;

        public Permission(Package r1) {
            super(r1);
            this.info = new PermissionInfo();
        }

        public Permission(Package r1, PermissionInfo permissionInfo) {
            super(r1);
            this.info = permissionInfo;
        }

        @Override // android.content.pm.PackageParser.Component
        public void setPackageName(String str) {
            super.setPackageName(str);
            this.info.packageName = str;
        }

        public String toString() {
            return "Permission{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.info.name + "}";
        }
    }

    public static final class PermissionGroup extends Component<IntentInfo> {
        public final PermissionGroupInfo info;

        public PermissionGroup(Package r1) {
            super(r1);
            this.info = new PermissionGroupInfo();
        }

        public PermissionGroup(Package r1, PermissionGroupInfo permissionGroupInfo) {
            super(r1);
            this.info = permissionGroupInfo;
        }

        @Override // android.content.pm.PackageParser.Component
        public void setPackageName(String str) {
            super.setPackageName(str);
            this.info.packageName = str;
        }

        public String toString() {
            return "PermissionGroup{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.info.name + "}";
        }
    }

    private static boolean copyNeeded(int i, Package r4, PackageUserState packageUserState, Bundle bundle, int i2) {
        if (i2 != 0) {
            return true;
        }
        if (packageUserState.enabled != 0) {
            if (r4.applicationInfo.enabled != (packageUserState.enabled == 1)) {
                return true;
            }
        }
        if (!packageUserState.installed || packageUserState.blocked || packageUserState.stopped) {
            return true;
        }
        if ((i & 128) == 0 || (bundle == null && r4.mAppMetaData == null)) {
            return ((i & 1024) == 0 || r4.usesLibraryFiles == null) ? false : true;
        }
        return true;
    }

    public static ApplicationInfo generateApplicationInfo(Package r1, int i, PackageUserState packageUserState) {
        return generateApplicationInfo(r1, i, packageUserState, UserHandle.getCallingUserId());
    }

    private static void updateApplicationInfo(ApplicationInfo applicationInfo, int i, PackageUserState packageUserState) {
        if (!sCompatibilityModeEnabled) {
            applicationInfo.disableCompatibilityMode();
        }
        if (packageUserState.installed) {
            applicationInfo.flags |= 8388608;
        } else {
            applicationInfo.flags &= -8388609;
        }
        if (packageUserState.blocked) {
            applicationInfo.flags |= 134217728;
        } else {
            applicationInfo.flags &= -134217729;
        }
        if (packageUserState.enabled == 1) {
            applicationInfo.enabled = true;
        } else if (packageUserState.enabled == 4) {
            applicationInfo.enabled = (i & 32768) != 0;
        } else if (packageUserState.enabled == 2 || packageUserState.enabled == 3) {
            applicationInfo.enabled = false;
        }
        applicationInfo.enabledSetting = packageUserState.enabled;
    }

    public static ApplicationInfo generateApplicationInfo(Package r2, int i, PackageUserState packageUserState, int i2) {
        if (r2 == null || !checkUseInstalledOrBlocked(i, packageUserState)) {
            return null;
        }
        if (!copyNeeded(i, r2, packageUserState, null, i2) && ((32768 & i) == 0 || packageUserState.enabled != 4)) {
            updateApplicationInfo(r2.applicationInfo, i, packageUserState);
            return r2.applicationInfo;
        }
        ApplicationInfo applicationInfo = new ApplicationInfo(r2.applicationInfo);
        if (i2 != 0) {
            applicationInfo.uid = UserHandle.getUid(i2, applicationInfo.uid);
            applicationInfo.dataDir = PackageManager.getDataDirForUser(i2, applicationInfo.packageName);
        }
        if ((i & 128) != 0) {
            applicationInfo.metaData = r2.mAppMetaData;
        }
        if ((i & 1024) != 0) {
            applicationInfo.sharedLibraryFiles = r2.usesLibraryFiles;
        }
        if (packageUserState.stopped) {
            applicationInfo.flags |= 2097152;
        } else {
            applicationInfo.flags &= -2097153;
        }
        updateApplicationInfo(applicationInfo, i, packageUserState);
        return applicationInfo;
    }

    public static final PermissionInfo generatePermissionInfo(Permission permission, int i) {
        if (permission == null) {
            return null;
        }
        if ((i & 128) == 0) {
            return permission.info;
        }
        PermissionInfo permissionInfo = new PermissionInfo(permission.info);
        permissionInfo.metaData = permission.metaData;
        return permissionInfo;
    }

    public static final PermissionGroupInfo generatePermissionGroupInfo(PermissionGroup permissionGroup, int i) {
        if (permissionGroup == null) {
            return null;
        }
        if ((i & 128) == 0) {
            return permissionGroup.info;
        }
        PermissionGroupInfo permissionGroupInfo = new PermissionGroupInfo(permissionGroup.info);
        permissionGroupInfo.metaData = permissionGroup.metaData;
        return permissionGroupInfo;
    }

    public static final class Activity extends Component<ActivityIntentInfo> {
        public final ActivityInfo info;

        public Activity(ParseComponentArgs parseComponentArgs, ActivityInfo activityInfo) {
            super(parseComponentArgs, (ComponentInfo) activityInfo);
            this.info = activityInfo;
            activityInfo.applicationInfo = parseComponentArgs.owner.applicationInfo;
        }

        @Override // android.content.pm.PackageParser.Component
        public void setPackageName(String str) {
            super.setPackageName(str);
            this.info.packageName = str;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("Activity{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            appendComponentShortName(sb);
            sb.append('}');
            return sb.toString();
        }
    }

    public static final ActivityInfo generateActivityInfo(Activity activity, int i, PackageUserState packageUserState, int i2) {
        if (activity == null || !checkUseInstalledOrBlocked(i, packageUserState)) {
            return null;
        }
        if (!copyNeeded(i, activity.owner, packageUserState, activity.metaData, i2)) {
            return activity.info;
        }
        ActivityInfo activityInfo = new ActivityInfo(activity.info);
        activityInfo.metaData = activity.metaData;
        activityInfo.applicationInfo = generateApplicationInfo(activity.owner, i, packageUserState, i2);
        return activityInfo;
    }

    public static final class Service extends Component<ServiceIntentInfo> {
        public final ServiceInfo info;

        public Service(ParseComponentArgs parseComponentArgs, ServiceInfo serviceInfo) {
            super(parseComponentArgs, (ComponentInfo) serviceInfo);
            this.info = serviceInfo;
            serviceInfo.applicationInfo = parseComponentArgs.owner.applicationInfo;
        }

        @Override // android.content.pm.PackageParser.Component
        public void setPackageName(String str) {
            super.setPackageName(str);
            this.info.packageName = str;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("Service{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            appendComponentShortName(sb);
            sb.append('}');
            return sb.toString();
        }
    }

    public static final ServiceInfo generateServiceInfo(Service service, int i, PackageUserState packageUserState, int i2) {
        if (service == null || !checkUseInstalledOrBlocked(i, packageUserState)) {
            return null;
        }
        if (!copyNeeded(i, service.owner, packageUserState, service.metaData, i2)) {
            return service.info;
        }
        ServiceInfo serviceInfo = new ServiceInfo(service.info);
        serviceInfo.metaData = service.metaData;
        serviceInfo.applicationInfo = generateApplicationInfo(service.owner, i, packageUserState, i2);
        return serviceInfo;
    }

    public static final class Provider extends Component<ProviderIntentInfo> {
        public final ProviderInfo info;
        public boolean syncable;

        public Provider(ParseComponentArgs parseComponentArgs, ProviderInfo providerInfo) {
            super(parseComponentArgs, (ComponentInfo) providerInfo);
            this.info = providerInfo;
            providerInfo.applicationInfo = parseComponentArgs.owner.applicationInfo;
            this.syncable = false;
        }

        public Provider(Provider provider) {
            super(provider);
            this.info = provider.info;
            this.syncable = provider.syncable;
        }

        @Override // android.content.pm.PackageParser.Component
        public void setPackageName(String str) {
            super.setPackageName(str);
            this.info.packageName = str;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("Provider{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            appendComponentShortName(sb);
            sb.append('}');
            return sb.toString();
        }
    }

    public static final ProviderInfo generateProviderInfo(Provider provider, int i, PackageUserState packageUserState, int i2) {
        if (provider == null || !checkUseInstalledOrBlocked(i, packageUserState)) {
            return null;
        }
        if (!copyNeeded(i, provider.owner, packageUserState, provider.metaData, i2) && ((i & 2048) != 0 || provider.info.uriPermissionPatterns == null)) {
            return provider.info;
        }
        ProviderInfo providerInfo = new ProviderInfo(provider.info);
        providerInfo.metaData = provider.metaData;
        if ((i & 2048) == 0) {
            providerInfo.uriPermissionPatterns = null;
        }
        providerInfo.applicationInfo = generateApplicationInfo(provider.owner, i, packageUserState, i2);
        return providerInfo;
    }

    public static final class Instrumentation extends Component {
        public final InstrumentationInfo info;

        public Instrumentation(ParsePackageItemArgs parsePackageItemArgs, InstrumentationInfo instrumentationInfo) {
            super(parsePackageItemArgs, instrumentationInfo);
            this.info = instrumentationInfo;
        }

        @Override // android.content.pm.PackageParser.Component
        public void setPackageName(String str) {
            super.setPackageName(str);
            this.info.packageName = str;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("Instrumentation{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            appendComponentShortName(sb);
            sb.append('}');
            return sb.toString();
        }
    }

    public static final InstrumentationInfo generateInstrumentationInfo(Instrumentation instrumentation, int i) {
        if (instrumentation == null) {
            return null;
        }
        if ((i & 128) == 0) {
            return instrumentation.info;
        }
        InstrumentationInfo instrumentationInfo = new InstrumentationInfo(instrumentation.info);
        instrumentationInfo.metaData = instrumentation.metaData;
        return instrumentationInfo;
    }

    public static final class ActivityIntentInfo extends IntentInfo {
        public final Activity activity;

        public ActivityIntentInfo(Activity activity) {
            this.activity = activity;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("ActivityIntentInfo{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            this.activity.appendComponentShortName(sb);
            sb.append('}');
            return sb.toString();
        }
    }

    public static final class ServiceIntentInfo extends IntentInfo {
        public final Service service;

        public ServiceIntentInfo(Service service) {
            this.service = service;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("ServiceIntentInfo{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            this.service.appendComponentShortName(sb);
            sb.append('}');
            return sb.toString();
        }
    }

    public static final class ProviderIntentInfo extends IntentInfo {
        public final Provider provider;

        public ProviderIntentInfo(Provider provider) {
            this.provider = provider;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("ProviderIntentInfo{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            this.provider.appendComponentShortName(sb);
            sb.append('}');
            return sb.toString();
        }
    }

    public static void setCompatibilityModeEnabled(boolean z) {
        sCompatibilityModeEnabled = z;
    }
}
