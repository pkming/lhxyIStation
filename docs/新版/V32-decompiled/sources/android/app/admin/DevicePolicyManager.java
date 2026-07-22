package android.app.admin;

import android.app.admin.IDevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import com.android.org.conscrypt.TrustedCertificateStore;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class DevicePolicyManager {
    public static final String ACTION_ADD_DEVICE_ADMIN = "android.app.action.ADD_DEVICE_ADMIN";
    public static final String ACTION_DEVICE_POLICY_MANAGER_STATE_CHANGED = "android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED";
    public static final String ACTION_SET_NEW_PASSWORD = "android.app.action.SET_NEW_PASSWORD";
    public static final String ACTION_START_ENCRYPTION = "android.app.action.START_ENCRYPTION";
    public static final int ENCRYPTION_STATUS_ACTIVATING = 2;
    public static final int ENCRYPTION_STATUS_ACTIVE = 3;
    public static final int ENCRYPTION_STATUS_INACTIVE = 1;
    public static final int ENCRYPTION_STATUS_UNSUPPORTED = 0;
    public static final String EXTRA_ADD_EXPLANATION = "android.app.extra.ADD_EXPLANATION";
    public static final String EXTRA_DEVICE_ADMIN = "android.app.extra.DEVICE_ADMIN";
    public static final int KEYGUARD_DISABLE_FEATURES_ALL = Integer.MAX_VALUE;
    public static final int KEYGUARD_DISABLE_FEATURES_NONE = 0;
    public static final int KEYGUARD_DISABLE_SECURE_CAMERA = 2;
    public static final int KEYGUARD_DISABLE_WIDGETS_ALL = 1;
    public static final int PASSWORD_QUALITY_ALPHABETIC = 262144;
    public static final int PASSWORD_QUALITY_ALPHANUMERIC = 327680;
    public static final int PASSWORD_QUALITY_BIOMETRIC_WEAK = 32768;
    public static final int PASSWORD_QUALITY_COMPLEX = 393216;
    public static final int PASSWORD_QUALITY_NUMERIC = 131072;
    public static final int PASSWORD_QUALITY_SOMETHING = 65536;
    public static final int PASSWORD_QUALITY_UNSPECIFIED = 0;
    public static final int RESET_PASSWORD_REQUIRE_ENTRY = 1;
    private static String TAG = "DevicePolicyManager";
    public static final int WIPE_EXTERNAL_STORAGE = 1;
    private final Context mContext;
    private final IDevicePolicyManager mService = IDevicePolicyManager.Stub.asInterface(ServiceManager.getService(Context.DEVICE_POLICY_SERVICE));

    public int getPasswordMaximumLength(int i) {
        return 16;
    }

    private DevicePolicyManager(Context context, Handler handler) {
        this.mContext = context;
    }

    public static DevicePolicyManager create(Context context, Handler handler) {
        DevicePolicyManager devicePolicyManager = new DevicePolicyManager(context, handler);
        if (devicePolicyManager.mService != null) {
            return devicePolicyManager;
        }
        return null;
    }

    public boolean isAdminActive(ComponentName componentName) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return false;
        }
        try {
            return iDevicePolicyManager.isAdminActive(componentName, UserHandle.myUserId());
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return false;
        }
    }

    public List<ComponentName> getActiveAdmins() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return null;
        }
        try {
            return iDevicePolicyManager.getActiveAdmins(UserHandle.myUserId());
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return null;
        }
    }

    public boolean packageHasActiveAdmins(String str) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return false;
        }
        try {
            return iDevicePolicyManager.packageHasActiveAdmins(str, UserHandle.myUserId());
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return false;
        }
    }

    public void removeActiveAdmin(ComponentName componentName) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.removeActiveAdmin(componentName, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public boolean hasGrantedPolicy(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return false;
        }
        try {
            return iDevicePolicyManager.hasGrantedPolicy(componentName, i, UserHandle.myUserId());
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return false;
        }
    }

    public void setPasswordQuality(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPasswordQuality(componentName, i, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordQuality(ComponentName componentName) {
        return getPasswordQuality(componentName, UserHandle.myUserId());
    }

    public int getPasswordQuality(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0;
        }
        try {
            return iDevicePolicyManager.getPasswordQuality(componentName, i);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return 0;
        }
    }

    public void setPasswordMinimumLength(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPasswordMinimumLength(componentName, i, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumLength(ComponentName componentName) {
        return getPasswordMinimumLength(componentName, UserHandle.myUserId());
    }

    public int getPasswordMinimumLength(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0;
        }
        try {
            return iDevicePolicyManager.getPasswordMinimumLength(componentName, i);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return 0;
        }
    }

    public void setPasswordMinimumUpperCase(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPasswordMinimumUpperCase(componentName, i, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumUpperCase(ComponentName componentName) {
        return getPasswordMinimumUpperCase(componentName, UserHandle.myUserId());
    }

    public int getPasswordMinimumUpperCase(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0;
        }
        try {
            return iDevicePolicyManager.getPasswordMinimumUpperCase(componentName, i);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return 0;
        }
    }

    public void setPasswordMinimumLowerCase(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPasswordMinimumLowerCase(componentName, i, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumLowerCase(ComponentName componentName) {
        return getPasswordMinimumLowerCase(componentName, UserHandle.myUserId());
    }

    public int getPasswordMinimumLowerCase(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0;
        }
        try {
            return iDevicePolicyManager.getPasswordMinimumLowerCase(componentName, i);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return 0;
        }
    }

    public void setPasswordMinimumLetters(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPasswordMinimumLetters(componentName, i, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumLetters(ComponentName componentName) {
        return getPasswordMinimumLetters(componentName, UserHandle.myUserId());
    }

    public int getPasswordMinimumLetters(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0;
        }
        try {
            return iDevicePolicyManager.getPasswordMinimumLetters(componentName, i);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return 0;
        }
    }

    public void setPasswordMinimumNumeric(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPasswordMinimumNumeric(componentName, i, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumNumeric(ComponentName componentName) {
        return getPasswordMinimumNumeric(componentName, UserHandle.myUserId());
    }

    public int getPasswordMinimumNumeric(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0;
        }
        try {
            return iDevicePolicyManager.getPasswordMinimumNumeric(componentName, i);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return 0;
        }
    }

    public void setPasswordMinimumSymbols(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPasswordMinimumSymbols(componentName, i, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumSymbols(ComponentName componentName) {
        return getPasswordMinimumSymbols(componentName, UserHandle.myUserId());
    }

    public int getPasswordMinimumSymbols(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0;
        }
        try {
            return iDevicePolicyManager.getPasswordMinimumSymbols(componentName, i);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return 0;
        }
    }

    public void setPasswordMinimumNonLetter(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPasswordMinimumNonLetter(componentName, i, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumNonLetter(ComponentName componentName) {
        return getPasswordMinimumNonLetter(componentName, UserHandle.myUserId());
    }

    public int getPasswordMinimumNonLetter(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0;
        }
        try {
            return iDevicePolicyManager.getPasswordMinimumNonLetter(componentName, i);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return 0;
        }
    }

    public void setPasswordHistoryLength(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPasswordHistoryLength(componentName, i, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void setPasswordExpirationTimeout(ComponentName componentName, long j) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPasswordExpirationTimeout(componentName, j, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public long getPasswordExpirationTimeout(ComponentName componentName) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0L;
        }
        try {
            return iDevicePolicyManager.getPasswordExpirationTimeout(componentName, UserHandle.myUserId());
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return 0L;
        }
    }

    public long getPasswordExpiration(ComponentName componentName) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0L;
        }
        try {
            return iDevicePolicyManager.getPasswordExpiration(componentName, UserHandle.myUserId());
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return 0L;
        }
    }

    public int getPasswordHistoryLength(ComponentName componentName) {
        return getPasswordHistoryLength(componentName, UserHandle.myUserId());
    }

    public int getPasswordHistoryLength(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0;
        }
        try {
            return iDevicePolicyManager.getPasswordHistoryLength(componentName, i);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return 0;
        }
    }

    public boolean isActivePasswordSufficient() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return false;
        }
        try {
            return iDevicePolicyManager.isActivePasswordSufficient(UserHandle.myUserId());
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return false;
        }
    }

    public int getCurrentFailedPasswordAttempts() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return -1;
        }
        try {
            return iDevicePolicyManager.getCurrentFailedPasswordAttempts(UserHandle.myUserId());
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return -1;
        }
    }

    public void setMaximumFailedPasswordsForWipe(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setMaximumFailedPasswordsForWipe(componentName, i, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getMaximumFailedPasswordsForWipe(ComponentName componentName) {
        return getMaximumFailedPasswordsForWipe(componentName, UserHandle.myUserId());
    }

    public int getMaximumFailedPasswordsForWipe(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0;
        }
        try {
            return iDevicePolicyManager.getMaximumFailedPasswordsForWipe(componentName, i);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return 0;
        }
    }

    public boolean resetPassword(String str, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return false;
        }
        try {
            return iDevicePolicyManager.resetPassword(str, i, UserHandle.myUserId());
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return false;
        }
    }

    public void setMaximumTimeToLock(ComponentName componentName, long j) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setMaximumTimeToLock(componentName, j, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public long getMaximumTimeToLock(ComponentName componentName) {
        return getMaximumTimeToLock(componentName, UserHandle.myUserId());
    }

    public long getMaximumTimeToLock(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0L;
        }
        try {
            return iDevicePolicyManager.getMaximumTimeToLock(componentName, i);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return 0L;
        }
    }

    public void lockNow() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.lockNow();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void wipeData(int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.wipeData(i, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public ComponentName setGlobalProxy(ComponentName componentName, Proxy proxy, List<String> list) {
        String str;
        String string;
        Objects.requireNonNull(proxy);
        if (this.mService != null) {
            try {
                if (proxy.equals(Proxy.NO_PROXY)) {
                    string = null;
                    str = null;
                } else {
                    if (!proxy.type().equals(Proxy.Type.HTTP)) {
                        throw new IllegalArgumentException();
                    }
                    InetSocketAddress inetSocketAddress = (InetSocketAddress) proxy.address();
                    String hostName = inetSocketAddress.getHostName();
                    int port = inetSocketAddress.getPort();
                    str = hostName + ":" + Integer.toString(port);
                    if (list == null) {
                        string = "";
                    } else {
                        StringBuilder sb = new StringBuilder();
                        boolean z = true;
                        for (String str2 : list) {
                            if (z) {
                                z = false;
                            } else {
                                sb = sb.append(",");
                            }
                            sb = sb.append(str2.trim());
                        }
                        string = sb.toString();
                    }
                    android.net.Proxy.validate(hostName, Integer.toString(port), string);
                }
                return this.mService.setGlobalProxy(componentName, str, string, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return null;
    }

    public ComponentName getGlobalProxyAdmin() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return null;
        }
        try {
            return iDevicePolicyManager.getGlobalProxyAdmin(UserHandle.myUserId());
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return null;
        }
    }

    public int setStorageEncryption(ComponentName componentName, boolean z) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0;
        }
        try {
            return iDevicePolicyManager.setStorageEncryption(componentName, z, UserHandle.myUserId());
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return 0;
        }
    }

    public boolean getStorageEncryption(ComponentName componentName) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return false;
        }
        try {
            return iDevicePolicyManager.getStorageEncryption(componentName, UserHandle.myUserId());
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return false;
        }
    }

    public int getStorageEncryptionStatus() {
        return getStorageEncryptionStatus(UserHandle.myUserId());
    }

    public int getStorageEncryptionStatus(int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0;
        }
        try {
            return iDevicePolicyManager.getStorageEncryptionStatus(i);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return 0;
        }
    }

    public boolean installCaCert(byte[] bArr) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return false;
        }
        try {
            return iDevicePolicyManager.installCaCert(bArr);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return false;
        }
    }

    public void uninstallCaCert(byte[] bArr) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.uninstallCaCert(bArr);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public static boolean hasAnyCaCertsInstalled() {
        Set setUserAliases = new TrustedCertificateStore().userAliases();
        return (setUserAliases == null || setUserAliases.isEmpty()) ? false : true;
    }

    public boolean hasCaCertInstalled(byte[] bArr) {
        try {
            return new TrustedCertificateStore().getCertificateAlias((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(bArr))) != null;
        } catch (CertificateException e) {
            Log.w(TAG, "Could not parse certificate", e);
            return false;
        }
    }

    public void setCameraDisabled(ComponentName componentName, boolean z) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setCameraDisabled(componentName, z, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public boolean getCameraDisabled(ComponentName componentName) {
        return getCameraDisabled(componentName, UserHandle.myUserId());
    }

    public boolean getCameraDisabled(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return false;
        }
        try {
            return iDevicePolicyManager.getCameraDisabled(componentName, i);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return false;
        }
    }

    public void setKeyguardDisabledFeatures(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setKeyguardDisabledFeatures(componentName, i, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getKeyguardDisabledFeatures(ComponentName componentName) {
        return getKeyguardDisabledFeatures(componentName, UserHandle.myUserId());
    }

    public int getKeyguardDisabledFeatures(ComponentName componentName, int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0;
        }
        try {
            return iDevicePolicyManager.getKeyguardDisabledFeatures(componentName, i);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed talking with device policy service", e);
            return 0;
        }
    }

    public void setActiveAdmin(ComponentName componentName, boolean z) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setActiveAdmin(componentName, z, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public DeviceAdminInfo getAdminInfo(ComponentName componentName) {
        try {
            ActivityInfo receiverInfo = this.mContext.getPackageManager().getReceiverInfo(componentName, 128);
            ResolveInfo resolveInfo = new ResolveInfo();
            resolveInfo.activityInfo = receiverInfo;
            try {
                return new DeviceAdminInfo(this.mContext, resolveInfo);
            } catch (IOException e) {
                Log.w(TAG, "Unable to parse device policy " + componentName, e);
                return null;
            } catch (XmlPullParserException e2) {
                Log.w(TAG, "Unable to parse device policy " + componentName, e2);
                return null;
            }
        } catch (PackageManager.NameNotFoundException e3) {
            Log.w(TAG, "Unable to retrieve device policy " + componentName, e3);
            return null;
        }
    }

    public void getRemoveWarning(ComponentName componentName, RemoteCallback remoteCallback) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.getRemoveWarning(componentName, remoteCallback, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void setActivePasswordState(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setActivePasswordState(i, i2, i3, i4, i5, i6, i7, i8, i9);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void reportFailedPasswordAttempt(int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.reportFailedPasswordAttempt(i);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void reportSuccessfulPasswordAttempt(int i) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.reportSuccessfulPasswordAttempt(i);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public boolean setDeviceOwner(String str) throws IllegalStateException, IllegalArgumentException {
        return setDeviceOwner(str, null);
    }

    public boolean setDeviceOwner(String str, String str2) throws IllegalStateException, IllegalArgumentException {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return false;
        }
        try {
            return iDevicePolicyManager.setDeviceOwner(str, str2);
        } catch (RemoteException unused) {
            Log.w(TAG, "Failed to set device owner");
            return false;
        }
    }

    public boolean isDeviceOwnerApp(String str) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return false;
        }
        try {
            return iDevicePolicyManager.isDeviceOwner(str);
        } catch (RemoteException unused) {
            Log.w(TAG, "Failed to check device owner");
            return false;
        }
    }

    public boolean isDeviceOwner(String str) {
        return isDeviceOwnerApp(str);
    }

    public String getDeviceOwner() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return null;
        }
        try {
            return iDevicePolicyManager.getDeviceOwner();
        } catch (RemoteException unused) {
            Log.w(TAG, "Failed to get device owner");
            return null;
        }
    }

    public String getDeviceOwnerName() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return null;
        }
        try {
            return iDevicePolicyManager.getDeviceOwnerName();
        } catch (RemoteException unused) {
            Log.w(TAG, "Failed to get device owner");
            return null;
        }
    }
}
