package android.telephony;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.MediaStore;
import com.android.internal.telephony.IPhoneSubInfo;
import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.ITelephonyRegistry;
import com.android.internal.telephony.PhoneConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class TelephonyManager {
    public static final String ACTION_PHONE_STATE_CHANGED = "android.intent.action.PHONE_STATE";
    public static final String ACTION_RESPOND_VIA_MESSAGE = "android.intent.action.RESPOND_VIA_MESSAGE";
    public static final int CALL_STATE_IDLE = 0;
    public static final int CALL_STATE_OFFHOOK = 2;
    public static final int CALL_STATE_RINGING = 1;
    public static final int DATA_ACTIVITY_DORMANT = 4;
    public static final int DATA_ACTIVITY_IN = 1;
    public static final int DATA_ACTIVITY_INOUT = 3;
    public static final int DATA_ACTIVITY_NONE = 0;
    public static final int DATA_ACTIVITY_OUT = 2;
    public static final int DATA_CONNECTED = 2;
    public static final int DATA_CONNECTING = 1;
    public static final int DATA_DISCONNECTED = 0;
    public static final int DATA_SUSPENDED = 3;
    public static final int DATA_UNKNOWN = -1;
    public static final String EXTRA_INCOMING_NUMBER = "incoming_number";
    public static final String EXTRA_STATE = "state";
    public static final int NETWORK_CLASS_2_G = 1;
    public static final int NETWORK_CLASS_3_G = 2;
    public static final int NETWORK_CLASS_4_G = 3;
    public static final int NETWORK_CLASS_UNKNOWN = 0;
    public static final int NETWORK_TYPE_1xRTT = 7;
    public static final int NETWORK_TYPE_CDMA = 4;
    public static final int NETWORK_TYPE_EDGE = 2;
    public static final int NETWORK_TYPE_EHRPD = 14;
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    public static final int NETWORK_TYPE_EVDO_A = 6;
    public static final int NETWORK_TYPE_EVDO_B = 12;
    public static final int NETWORK_TYPE_GPRS = 1;
    public static final int NETWORK_TYPE_HSDPA = 8;
    public static final int NETWORK_TYPE_HSPA = 10;
    public static final int NETWORK_TYPE_HSPAP = 15;
    public static final int NETWORK_TYPE_HSUPA = 9;
    public static final int NETWORK_TYPE_IDEN = 11;
    public static final int NETWORK_TYPE_LTE = 13;
    public static final int NETWORK_TYPE_UMTS = 3;
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    public static final int PHONE_TYPE_CDMA = 2;
    public static final int PHONE_TYPE_GSM = 1;
    public static final int PHONE_TYPE_NONE = 0;
    public static final int PHONE_TYPE_SIP = 3;
    public static final int SIM_STATE_ABSENT = 1;
    public static final int SIM_STATE_NETWORK_LOCKED = 4;
    public static final int SIM_STATE_PIN_REQUIRED = 2;
    public static final int SIM_STATE_PUK_REQUIRED = 3;
    public static final int SIM_STATE_READY = 5;
    public static final int SIM_STATE_UNKNOWN = 0;
    private static final String TAG = "TelephonyManager";
    private static ITelephonyRegistry sRegistry;
    private final Context mContext;
    private static TelephonyManager sInstance = new TelephonyManager();
    public static final String EXTRA_STATE_IDLE = PhoneConstants.State.IDLE.toString();
    public static final String EXTRA_STATE_RINGING = PhoneConstants.State.RINGING.toString();
    public static final String EXTRA_STATE_OFFHOOK = PhoneConstants.State.OFFHOOK.toString();
    private static final String sKernelCmdLine = getProcCmdLine();
    private static final Pattern sProductTypePattern = Pattern.compile("\\sproduct_type\\s*=\\s*(\\w+)");
    private static final String sLteOnCdmaProductType = SystemProperties.get("telephony.lteOnCdmaProductType", "");

    public static int getNetworkClass(int i) {
        switch (i) {
            case 1:
            case 2:
            case 4:
            case 7:
            case 11:
                return 1;
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 14:
            case 15:
                return 2;
            case 13:
                return 3;
            default:
                return 0;
        }
    }

    public static String getNetworkTypeName(int i) {
        switch (i) {
            case 1:
                return "GPRS";
            case 2:
                return "EDGE";
            case 3:
                return "UMTS";
            case 4:
                return "CDMA";
            case 5:
                return "CDMA - EvDo rev. 0";
            case 6:
                return "CDMA - EvDo rev. A";
            case 7:
                return "CDMA - 1xRTT";
            case 8:
                return "HSDPA";
            case 9:
                return "HSUPA";
            case 10:
                return "HSPA";
            case 11:
                return "iDEN";
            case 12:
                return "CDMA - EvDo rev. B";
            case 13:
                return "LTE";
            case 14:
                return "CDMA - eHRPD";
            case 15:
                return "HSPA+";
            default:
                return MediaPlayer.CHARSET_UNKNOWN;
        }
    }

    public TelephonyManager(Context context) {
        Context applicationContext = context.getApplicationContext();
        if (applicationContext != null) {
            this.mContext = applicationContext;
        } else {
            this.mContext = context;
        }
        if (sRegistry == null) {
            sRegistry = ITelephonyRegistry.Stub.asInterface(ServiceManager.getService("telephony.registry"));
        }
    }

    private TelephonyManager() {
        this.mContext = null;
    }

    public static TelephonyManager getDefault() {
        return sInstance;
    }

    public static TelephonyManager from(Context context) {
        return (TelephonyManager) context.getSystemService("phone");
    }

    public String getDeviceSoftwareVersion() {
        try {
            return getSubscriberInfo().getDeviceSvn();
        } catch (RemoteException | NullPointerException unused) {
            return null;
        }
    }

    public String getDeviceId() {
        if (SystemProperties.get("ro.sw.embeded.telephony").equals("true") || SystemProperties.get("ro.imei.real").equals("true")) {
            try {
                return getSubscriberInfo().getDeviceId();
            } catch (RemoteException | NullPointerException unused) {
                return null;
            }
        }
        try {
            String deviceId = getSubscriberInfo().getDeviceId();
            if (deviceId == null) {
                return deviceId.equals("") ? "352005048247251" : deviceId;
            }
            if (!SystemProperties.get("gsm.operator.iso-country").equals("")) {
                return deviceId;
            }
            Properties properties = new Properties();
            try {
                FileInputStream fileInputStream = new FileInputStream("/data/misc/radio/imei.conf");
                properties.load(fileInputStream);
                fileInputStream.close();
                return properties.getProperty("IMEI", null);
            } catch (IOException unused2) {
                return "352005048247251";
            }
        } catch (RemoteException | IOException unused3) {
            return "352005048247251";
        } catch (NullPointerException unused4) {
            Properties properties2 = new Properties();
            try {
                FileInputStream fileInputStream2 = new FileInputStream("/data/misc/radio/imei.conf");
                properties2.load(fileInputStream2);
                fileInputStream2.close();
                return properties2.getProperty("IMEI", null);
            } catch (IOException unused5) {
                FileOutputStream fileOutputStream = new FileOutputStream(new File("/data/misc/radio/imei.conf"));
                String[] strArr = {"35793200", "35795200", "35881700", "35936500", "35227201", "35707000", "35973200", "35227301", "35227401", "35200504"};
                String strValueOf = String.valueOf((int) ((Math.random() * 899999.0d) + 100000.0d));
                int iRandom = (int) (Math.random() * 10.0d);
                int i = 0;
                if (iRandom >= 10 || iRandom < 0) {
                    iRandom = 0;
                }
                String str = strArr[iRandom] + strValueOf;
                int i2 = 1;
                int[][] iArr = {new int[]{0, 2, 4, 6, 8, 1, 3, 5, 7, 9}, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}};
                int length = str.length() - 1;
                int i3 = 0;
                while (length >= 0) {
                    i += iArr[i3 & i2][Character.digit(str.charAt(length), 10)];
                    length--;
                    i3++;
                    i2 = 1;
                }
                int i4 = i % 10;
                if (i4 != 0) {
                    i4 = 10 - i4;
                }
                String str2 = strArr[iRandom] + strValueOf + String.valueOf(i4);
                properties2.setProperty("IMEI", str2);
                properties2.store(fileOutputStream, "");
                fileOutputStream.close();
                Runtime.getRuntime().exec("chmod 666 /data/misc/radio/imei.conf");
                return str2;
            }
        }
    }

    public CellLocation getCellLocation() {
        try {
            Bundle cellLocation = getITelephony().getCellLocation();
            if (cellLocation.isEmpty()) {
                return null;
            }
            CellLocation cellLocationNewFromBundle = CellLocation.newFromBundle(cellLocation);
            if (cellLocationNewFromBundle.isEmpty()) {
                return null;
            }
            return cellLocationNewFromBundle;
        } catch (RemoteException | NullPointerException unused) {
            return null;
        }
    }

    public void enableLocationUpdates() {
        try {
            getITelephony().enableLocationUpdates();
        } catch (RemoteException | NullPointerException unused) {
        }
    }

    public void disableLocationUpdates() {
        try {
            getITelephony().disableLocationUpdates();
        } catch (RemoteException | NullPointerException unused) {
        }
    }

    public List<NeighboringCellInfo> getNeighboringCellInfo() {
        try {
            return getITelephony().getNeighboringCellInfo(this.mContext.getOpPackageName());
        } catch (RemoteException | NullPointerException unused) {
            return null;
        }
    }

    public int getCurrentPhoneType() {
        try {
            ITelephony iTelephony = getITelephony();
            if (iTelephony != null) {
                return iTelephony.getActivePhoneType();
            }
            return getPhoneTypeFromProperty();
        } catch (RemoteException unused) {
            return getPhoneTypeFromProperty();
        } catch (NullPointerException unused2) {
            return getPhoneTypeFromProperty();
        }
    }

    public int getPhoneType() {
        if (isVoiceCapable()) {
            return getCurrentPhoneType();
        }
        return 0;
    }

    private int getPhoneTypeFromProperty() {
        return SystemProperties.getInt("gsm.current.phone-type", getPhoneTypeFromNetworkType());
    }

    private int getPhoneTypeFromNetworkType() {
        int i = SystemProperties.getInt("ro.telephony.default_network", -1);
        if (i == -1) {
            return 0;
        }
        return getPhoneType(i);
    }

    public static int getPhoneType(int i) {
        if (i == 11) {
            return getLteOnCdmaModeStatic() == 1 ? 2 : 1;
        }
        switch (i) {
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                return 2;
            default:
                return 1;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x005d A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.lang.String getProcCmdLine() throws java.lang.Throwable {
        /*
            java.lang.String r0 = "TelephonyManager"
            java.lang.String r1 = ""
            r2 = 0
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L23 java.io.IOException -> L25
            java.lang.String r4 = "/proc/cmdline"
            r3.<init>(r4)     // Catch: java.lang.Throwable -> L23 java.io.IOException -> L25
            r2 = 2048(0x800, float:2.87E-42)
            byte[] r2 = new byte[r2]     // Catch: java.io.IOException -> L21 java.lang.Throwable -> L59
            int r4 = r3.read(r2)     // Catch: java.io.IOException -> L21 java.lang.Throwable -> L59
            if (r4 <= 0) goto L1d
            java.lang.String r5 = new java.lang.String     // Catch: java.io.IOException -> L21 java.lang.Throwable -> L59
            r6 = 0
            r5.<init>(r2, r6, r4)     // Catch: java.io.IOException -> L21 java.lang.Throwable -> L59
            r1 = r5
        L1d:
            r3.close()     // Catch: java.io.IOException -> L42
            goto L42
        L21:
            r2 = move-exception
            goto L29
        L23:
            r0 = move-exception
            goto L5b
        L25:
            r3 = move-exception
            r7 = r3
            r3 = r2
            r2 = r7
        L29:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L59
            r4.<init>()     // Catch: java.lang.Throwable -> L59
            java.lang.String r5 = "No /proc/cmdline exception="
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch: java.lang.Throwable -> L59
            java.lang.StringBuilder r2 = r4.append(r2)     // Catch: java.lang.Throwable -> L59
            java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> L59
            android.telephony.Rlog.d(r0, r2)     // Catch: java.lang.Throwable -> L59
            if (r3 == 0) goto L42
            goto L1d
        L42:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "/proc/cmdline="
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.StringBuilder r2 = r2.append(r1)
            java.lang.String r2 = r2.toString()
            android.telephony.Rlog.d(r0, r2)
            return r1
        L59:
            r0 = move-exception
            r2 = r3
        L5b:
            if (r2 == 0) goto L60
            r2.close()     // Catch: java.io.IOException -> L60
        L60:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.telephony.TelephonyManager.getProcCmdLine():java.lang.String");
    }

    /* JADX WARN: Removed duplicated region for block: B:9:0x0029 A[PHI: r4
      0x0029: PHI (r4v2 java.lang.String) = (r4v0 java.lang.String), (r4v3 java.lang.String) binds: [B:5:0x001a, B:7:0x0026] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static int getLteOnCdmaModeStatic() {
        /*
            java.lang.String r0 = "telephony.lteOnCdmaDevice"
            r1 = -1
            int r0 = android.os.SystemProperties.getInt(r0, r1)
            r2 = 1
            r3 = 0
            java.lang.String r4 = ""
            if (r0 != r1) goto L2b
            java.util.regex.Pattern r1 = android.telephony.TelephonyManager.sProductTypePattern
            java.lang.String r5 = android.telephony.TelephonyManager.sKernelCmdLine
            java.util.regex.Matcher r1 = r1.matcher(r5)
            boolean r5 = r1.find()
            if (r5 == 0) goto L29
            java.lang.String r4 = r1.group(r2)
            java.lang.String r1 = android.telephony.TelephonyManager.sLteOnCdmaProductType
            boolean r1 = r1.equals(r4)
            if (r1 == 0) goto L29
            goto L2c
        L29:
            r2 = r3
            goto L2c
        L2b:
            r2 = r0
        L2c:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "getLteOnCdmaMode="
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r3 = " curVal="
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.StringBuilder r0 = r1.append(r0)
            java.lang.String r1 = " product_type='"
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.StringBuilder r0 = r0.append(r4)
            java.lang.String r1 = "' lteOnCdmaProductType='"
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.String r1 = android.telephony.TelephonyManager.sLteOnCdmaProductType
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.String r1 = "'"
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "TelephonyManager"
            android.telephony.Rlog.d(r1, r0)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: android.telephony.TelephonyManager.getLteOnCdmaModeStatic():int");
    }

    public String getNetworkOperatorName() {
        return SystemProperties.get("gsm.operator.alpha");
    }

    public String getNetworkOperator() {
        return SystemProperties.get("gsm.operator.numeric");
    }

    public boolean isNetworkRoaming() {
        return "true".equals(SystemProperties.get("gsm.operator.isroaming"));
    }

    public String getNetworkCountryIso() {
        return SystemProperties.get("gsm.operator.iso-country");
    }

    public int getNetworkType() {
        return getDataNetworkType();
    }

    public int getDataNetworkType() {
        try {
            ITelephony iTelephony = getITelephony();
            if (iTelephony != null) {
                return iTelephony.getDataNetworkType();
            }
            return 0;
        } catch (RemoteException | NullPointerException unused) {
            return 0;
        }
    }

    public int getVoiceNetworkType() {
        try {
            ITelephony iTelephony = getITelephony();
            if (iTelephony != null) {
                return iTelephony.getVoiceNetworkType();
            }
            return 0;
        } catch (RemoteException | NullPointerException unused) {
            return 0;
        }
    }

    public String getNetworkTypeName() {
        return getNetworkTypeName(getNetworkType());
    }

    public boolean hasIccCard() {
        try {
            return getITelephony().hasIccCard();
        } catch (RemoteException | NullPointerException unused) {
            return false;
        }
    }

    public int getSimState() {
        String str = SystemProperties.get("gsm.sim.state");
        if ("ABSENT".equals(str)) {
            return 1;
        }
        if ("PIN_REQUIRED".equals(str)) {
            return 2;
        }
        if ("PUK_REQUIRED".equals(str)) {
            return 3;
        }
        if ("NETWORK_LOCKED".equals(str)) {
            return 4;
        }
        return "READY".equals(str) ? 5 : 0;
    }

    public String getSimOperator() {
        return SystemProperties.get("gsm.sim.operator.numeric");
    }

    public String getSimOperatorName() {
        return SystemProperties.get("gsm.sim.operator.alpha");
    }

    public String getSimCountryIso() {
        return SystemProperties.get("gsm.sim.operator.iso-country");
    }

    public String getSimSerialNumber() {
        try {
            return getSubscriberInfo().getIccSerialNumber();
        } catch (RemoteException | NullPointerException unused) {
            return null;
        }
    }

    public int getLteOnCdmaMode() {
        try {
            return getITelephony().getLteOnCdmaMode();
        } catch (RemoteException | NullPointerException unused) {
            return -1;
        }
    }

    public String getSubscriberId() {
        try {
            return getSubscriberInfo().getSubscriberId();
        } catch (RemoteException | NullPointerException unused) {
            return null;
        }
    }

    public String getGroupIdLevel1() {
        try {
            return getSubscriberInfo().getGroupIdLevel1();
        } catch (RemoteException | NullPointerException unused) {
            return null;
        }
    }

    public String getLine1Number() {
        try {
            return getSubscriberInfo().getLine1Number();
        } catch (RemoteException | NullPointerException unused) {
            return null;
        }
    }

    public String getLine1AlphaTag() {
        try {
            return getSubscriberInfo().getLine1AlphaTag();
        } catch (RemoteException | NullPointerException unused) {
            return null;
        }
    }

    public String getMsisdn() {
        try {
            return getSubscriberInfo().getMsisdn();
        } catch (RemoteException | NullPointerException unused) {
            return null;
        }
    }

    public String getVoiceMailNumber() {
        try {
            return getSubscriberInfo().getVoiceMailNumber();
        } catch (RemoteException | NullPointerException unused) {
            return null;
        }
    }

    public String getCompleteVoiceMailNumber() {
        try {
            return getSubscriberInfo().getCompleteVoiceMailNumber();
        } catch (RemoteException | NullPointerException unused) {
            return null;
        }
    }

    public int getVoiceMessageCount() {
        try {
            return getITelephony().getVoiceMessageCount();
        } catch (RemoteException | NullPointerException unused) {
            return 0;
        }
    }

    public String getVoiceMailAlphaTag() {
        try {
            return getSubscriberInfo().getVoiceMailAlphaTag();
        } catch (RemoteException | NullPointerException unused) {
            return null;
        }
    }

    public String getIsimImpi() {
        try {
            return getSubscriberInfo().getIsimImpi();
        } catch (RemoteException | NullPointerException unused) {
            return null;
        }
    }

    public String getIsimDomain() {
        try {
            return getSubscriberInfo().getIsimDomain();
        } catch (RemoteException | NullPointerException unused) {
            return null;
        }
    }

    public String[] getIsimImpu() {
        try {
            return getSubscriberInfo().getIsimImpu();
        } catch (RemoteException | NullPointerException unused) {
            return null;
        }
    }

    private IPhoneSubInfo getSubscriberInfo() {
        return IPhoneSubInfo.Stub.asInterface(ServiceManager.getService("iphonesubinfo"));
    }

    public int getCallState() {
        try {
            return getITelephony().getCallState();
        } catch (RemoteException | NullPointerException unused) {
            return 0;
        }
    }

    public int getDataActivity() {
        try {
            return getITelephony().getDataActivity();
        } catch (RemoteException | NullPointerException unused) {
            return 0;
        }
    }

    public int getDataState() {
        try {
            return getITelephony().getDataState();
        } catch (RemoteException | NullPointerException unused) {
            return 0;
        }
    }

    private ITelephony getITelephony() {
        return ITelephony.Stub.asInterface(ServiceManager.getService("phone"));
    }

    public void listen(PhoneStateListener phoneStateListener, int i) {
        Context context = this.mContext;
        try {
            Boolean bool = true;
            sRegistry.listen(context != null ? context.getPackageName() : MediaStore.UNKNOWN_STRING, phoneStateListener.callback, i, bool.booleanValue());
        } catch (RemoteException | NullPointerException unused) {
        }
    }

    public int getCdmaEriIconIndex() {
        try {
            return getITelephony().getCdmaEriIconIndex();
        } catch (RemoteException | NullPointerException unused) {
            return -1;
        }
    }

    public int getCdmaEriIconMode() {
        try {
            return getITelephony().getCdmaEriIconMode();
        } catch (RemoteException | NullPointerException unused) {
            return -1;
        }
    }

    public String getCdmaEriText() {
        try {
            return getITelephony().getCdmaEriText();
        } catch (RemoteException | NullPointerException unused) {
            return null;
        }
    }

    public boolean isVoiceCapable() {
        Context context = this.mContext;
        if (context == null) {
            return true;
        }
        return context.getResources().getBoolean(17891385);
    }

    public boolean isSmsCapable() {
        Context context = this.mContext;
        if (context == null) {
            return true;
        }
        return context.getResources().getBoolean(17891386);
    }

    public List<CellInfo> getAllCellInfo() {
        try {
            return getITelephony().getAllCellInfo();
        } catch (RemoteException | NullPointerException unused) {
            return null;
        }
    }

    public void setCellInfoListRate(int i) {
        try {
            getITelephony().setCellInfoListRate(i);
        } catch (RemoteException | NullPointerException unused) {
        }
    }

    public String getMmsUserAgent() {
        Context context = this.mContext;
        if (context == null) {
            return null;
        }
        return context.getResources().getString(17039405);
    }

    public String getMmsUAProfUrl() {
        Context context = this.mContext;
        if (context == null) {
            return null;
        }
        return context.getResources().getString(17039406);
    }
}
