package android.telephony;

import android.net.ethernet.EthernetManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.CalendarContract;

/* JADX INFO: loaded from: classes.dex */
public class ServiceState implements Parcelable {
    public static final Parcelable.Creator<ServiceState> CREATOR = new Parcelable.Creator<ServiceState>() { // from class: android.telephony.ServiceState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ServiceState createFromParcel(Parcel parcel) {
            return new ServiceState(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ServiceState[] newArray(int i) {
            return new ServiceState[i];
        }
    };
    static final boolean DBG = true;
    static final String LOG_TAG = "PHONE";
    public static final int REGISTRATION_STATE_HOME_NETWORK = 1;
    public static final int REGISTRATION_STATE_NOT_REGISTERED_AND_NOT_SEARCHING = 0;
    public static final int REGISTRATION_STATE_NOT_REGISTERED_AND_SEARCHING = 2;
    public static final int REGISTRATION_STATE_REGISTRATION_DENIED = 3;
    public static final int REGISTRATION_STATE_ROAMING = 5;
    public static final int REGISTRATION_STATE_UNKNOWN = 4;
    public static final int RIL_RADIO_TECHNOLOGY_1xRTT = 6;
    public static final int RIL_RADIO_TECHNOLOGY_EDGE = 2;
    public static final int RIL_RADIO_TECHNOLOGY_EHRPD = 13;
    public static final int RIL_RADIO_TECHNOLOGY_EVDO_0 = 7;
    public static final int RIL_RADIO_TECHNOLOGY_EVDO_A = 8;
    public static final int RIL_RADIO_TECHNOLOGY_EVDO_B = 12;
    public static final int RIL_RADIO_TECHNOLOGY_GPRS = 1;
    public static final int RIL_RADIO_TECHNOLOGY_GSM = 16;
    public static final int RIL_RADIO_TECHNOLOGY_HSDPA = 9;
    public static final int RIL_RADIO_TECHNOLOGY_HSPA = 11;
    public static final int RIL_RADIO_TECHNOLOGY_HSPAP = 15;
    public static final int RIL_RADIO_TECHNOLOGY_HSUPA = 10;
    public static final int RIL_RADIO_TECHNOLOGY_IS95A = 4;
    public static final int RIL_RADIO_TECHNOLOGY_IS95B = 5;
    public static final int RIL_RADIO_TECHNOLOGY_LTE = 14;
    public static final int RIL_RADIO_TECHNOLOGY_UMTS = 3;
    public static final int RIL_RADIO_TECHNOLOGY_UNKNOWN = 0;
    public static final int RIL_REG_STATE_DENIED = 3;
    public static final int RIL_REG_STATE_DENIED_EMERGENCY_CALL_ENABLED = 13;
    public static final int RIL_REG_STATE_HOME = 1;
    public static final int RIL_REG_STATE_NOT_REG = 0;
    public static final int RIL_REG_STATE_NOT_REG_EMERGENCY_CALL_ENABLED = 10;
    public static final int RIL_REG_STATE_ROAMING = 5;
    public static final int RIL_REG_STATE_SEARCHING = 2;
    public static final int RIL_REG_STATE_SEARCHING_EMERGENCY_CALL_ENABLED = 12;
    public static final int RIL_REG_STATE_UNKNOWN = 4;
    public static final int RIL_REG_STATE_UNKNOWN_EMERGENCY_CALL_ENABLED = 14;
    public static final int STATE_EMERGENCY_ONLY = 2;
    public static final int STATE_IN_SERVICE = 0;
    public static final int STATE_OUT_OF_SERVICE = 1;
    public static final int STATE_POWER_OFF = 3;
    private int mCdmaDefaultRoamingIndicator;
    private int mCdmaEriIconIndex;
    private int mCdmaEriIconMode;
    private int mCdmaRoamingIndicator;
    private boolean mCssIndicator;
    private int mDataRegState;
    private boolean mIsEmergencyOnly;
    private boolean mIsManualNetworkSelection;
    private int mNetworkId;
    private String mOperatorAlphaLong;
    private String mOperatorAlphaShort;
    private String mOperatorNumeric;
    private int mRilDataRadioTechnology;
    private int mRilVoiceRadioTechnology;
    private boolean mRoaming;
    private int mSystemId;
    private int mVoiceRegState;

    public static boolean isCdma(int i) {
        return i == 4 || i == 5 || i == 6 || i == 7 || i == 8 || i == 12 || i == 13;
    }

    public static boolean isGsm(int i) {
        return i == 1 || i == 2 || i == 3 || i == 9 || i == 10 || i == 11 || i == 14 || i == 15 || i == 16;
    }

    private int rilRadioTechnologyToNetworkType(int i) {
        switch (i) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
            case 5:
                return 4;
            case 6:
                return 7;
            case 7:
                return 5;
            case 8:
                return 6;
            case 9:
                return 8;
            case 10:
                return 9;
            case 11:
                return 10;
            case 12:
                return 12;
            case 13:
                return 14;
            case 14:
                return 13;
            case 15:
                return 15;
            default:
                return 0;
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static ServiceState newFromBundle(Bundle bundle) {
        ServiceState serviceState = new ServiceState();
        serviceState.setFromNotifierBundle(bundle);
        return serviceState;
    }

    public ServiceState() {
        this.mVoiceRegState = 1;
        this.mDataRegState = 1;
    }

    public ServiceState(ServiceState serviceState) {
        this.mVoiceRegState = 1;
        this.mDataRegState = 1;
        copyFrom(serviceState);
    }

    protected void copyFrom(ServiceState serviceState) {
        this.mVoiceRegState = serviceState.mVoiceRegState;
        this.mDataRegState = serviceState.mDataRegState;
        this.mRoaming = serviceState.mRoaming;
        this.mOperatorAlphaLong = serviceState.mOperatorAlphaLong;
        this.mOperatorAlphaShort = serviceState.mOperatorAlphaShort;
        this.mOperatorNumeric = serviceState.mOperatorNumeric;
        this.mIsManualNetworkSelection = serviceState.mIsManualNetworkSelection;
        this.mRilVoiceRadioTechnology = serviceState.mRilVoiceRadioTechnology;
        this.mRilDataRadioTechnology = serviceState.mRilDataRadioTechnology;
        this.mCssIndicator = serviceState.mCssIndicator;
        this.mNetworkId = serviceState.mNetworkId;
        this.mSystemId = serviceState.mSystemId;
        this.mCdmaRoamingIndicator = serviceState.mCdmaRoamingIndicator;
        this.mCdmaDefaultRoamingIndicator = serviceState.mCdmaDefaultRoamingIndicator;
        this.mCdmaEriIconIndex = serviceState.mCdmaEriIconIndex;
        this.mCdmaEriIconMode = serviceState.mCdmaEriIconMode;
        this.mIsEmergencyOnly = serviceState.mIsEmergencyOnly;
    }

    public ServiceState(Parcel parcel) {
        this.mVoiceRegState = 1;
        this.mDataRegState = 1;
        this.mVoiceRegState = parcel.readInt();
        this.mDataRegState = parcel.readInt();
        this.mRoaming = parcel.readInt() != 0;
        this.mOperatorAlphaLong = parcel.readString();
        this.mOperatorAlphaShort = parcel.readString();
        this.mOperatorNumeric = parcel.readString();
        this.mIsManualNetworkSelection = parcel.readInt() != 0;
        this.mRilVoiceRadioTechnology = parcel.readInt();
        this.mRilDataRadioTechnology = parcel.readInt();
        this.mCssIndicator = parcel.readInt() != 0;
        this.mNetworkId = parcel.readInt();
        this.mSystemId = parcel.readInt();
        this.mCdmaRoamingIndicator = parcel.readInt();
        this.mCdmaDefaultRoamingIndicator = parcel.readInt();
        this.mCdmaEriIconIndex = parcel.readInt();
        this.mCdmaEriIconMode = parcel.readInt();
        this.mIsEmergencyOnly = parcel.readInt() != 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mVoiceRegState);
        parcel.writeInt(this.mDataRegState);
        parcel.writeInt(this.mRoaming ? 1 : 0);
        parcel.writeString(this.mOperatorAlphaLong);
        parcel.writeString(this.mOperatorAlphaShort);
        parcel.writeString(this.mOperatorNumeric);
        parcel.writeInt(this.mIsManualNetworkSelection ? 1 : 0);
        parcel.writeInt(this.mRilVoiceRadioTechnology);
        parcel.writeInt(this.mRilDataRadioTechnology);
        parcel.writeInt(this.mCssIndicator ? 1 : 0);
        parcel.writeInt(this.mNetworkId);
        parcel.writeInt(this.mSystemId);
        parcel.writeInt(this.mCdmaRoamingIndicator);
        parcel.writeInt(this.mCdmaDefaultRoamingIndicator);
        parcel.writeInt(this.mCdmaEriIconIndex);
        parcel.writeInt(this.mCdmaEriIconMode);
        parcel.writeInt(this.mIsEmergencyOnly ? 1 : 0);
    }

    public int getState() {
        return getVoiceRegState();
    }

    public int getVoiceRegState() {
        return this.mVoiceRegState;
    }

    public int getDataRegState() {
        return this.mDataRegState;
    }

    public boolean getRoaming() {
        return this.mRoaming;
    }

    public boolean isEmergencyOnly() {
        return this.mIsEmergencyOnly;
    }

    public int getCdmaRoamingIndicator() {
        return this.mCdmaRoamingIndicator;
    }

    public int getCdmaDefaultRoamingIndicator() {
        return this.mCdmaDefaultRoamingIndicator;
    }

    public int getCdmaEriIconIndex() {
        return this.mCdmaEriIconIndex;
    }

    public int getCdmaEriIconMode() {
        return this.mCdmaEriIconMode;
    }

    public String getOperatorAlphaLong() {
        return this.mOperatorAlphaLong;
    }

    public String getOperatorAlphaShort() {
        return this.mOperatorAlphaShort;
    }

    public String getOperatorNumeric() {
        return this.mOperatorNumeric;
    }

    public boolean getIsManualSelection() {
        return this.mIsManualNetworkSelection;
    }

    public int hashCode() {
        int i = (this.mVoiceRegState * 31) + (this.mDataRegState * 37) + (this.mRoaming ? 1 : 0) + (this.mIsManualNetworkSelection ? 1 : 0);
        String str = this.mOperatorAlphaLong;
        int iHashCode = i + (str == null ? 0 : str.hashCode());
        String str2 = this.mOperatorAlphaShort;
        int iHashCode2 = iHashCode + (str2 == null ? 0 : str2.hashCode());
        String str3 = this.mOperatorNumeric;
        return iHashCode2 + (str3 != null ? str3.hashCode() : 0) + this.mCdmaRoamingIndicator + this.mCdmaDefaultRoamingIndicator + (this.mIsEmergencyOnly ? 1 : 0);
    }

    public boolean equals(Object obj) {
        try {
            ServiceState serviceState = (ServiceState) obj;
            return obj != null && this.mVoiceRegState == serviceState.mVoiceRegState && this.mDataRegState == serviceState.mDataRegState && this.mRoaming == serviceState.mRoaming && this.mIsManualNetworkSelection == serviceState.mIsManualNetworkSelection && equalsHandlesNulls(this.mOperatorAlphaLong, serviceState.mOperatorAlphaLong) && equalsHandlesNulls(this.mOperatorAlphaShort, serviceState.mOperatorAlphaShort) && equalsHandlesNulls(this.mOperatorNumeric, serviceState.mOperatorNumeric) && equalsHandlesNulls(Integer.valueOf(this.mRilVoiceRadioTechnology), Integer.valueOf(serviceState.mRilVoiceRadioTechnology)) && equalsHandlesNulls(Integer.valueOf(this.mRilDataRadioTechnology), Integer.valueOf(serviceState.mRilDataRadioTechnology)) && equalsHandlesNulls(Boolean.valueOf(this.mCssIndicator), Boolean.valueOf(serviceState.mCssIndicator)) && equalsHandlesNulls(Integer.valueOf(this.mNetworkId), Integer.valueOf(serviceState.mNetworkId)) && equalsHandlesNulls(Integer.valueOf(this.mSystemId), Integer.valueOf(serviceState.mSystemId)) && equalsHandlesNulls(Integer.valueOf(this.mCdmaRoamingIndicator), Integer.valueOf(serviceState.mCdmaRoamingIndicator)) && equalsHandlesNulls(Integer.valueOf(this.mCdmaDefaultRoamingIndicator), Integer.valueOf(serviceState.mCdmaDefaultRoamingIndicator)) && this.mIsEmergencyOnly == serviceState.mIsEmergencyOnly;
        } catch (ClassCastException unused) {
            return false;
        }
    }

    public static String rilRadioTechnologyToString(int i) {
        switch (i) {
            case 0:
                return "Unknown";
            case 1:
                return "GPRS";
            case 2:
                return "EDGE";
            case 3:
                return "UMTS";
            case 4:
                return "CDMA-IS95A";
            case 5:
                return "CDMA-IS95B";
            case 6:
                return "1xRTT";
            case 7:
                return "EvDo-rev.0";
            case 8:
                return "EvDo-rev.A";
            case 9:
                return "HSDPA";
            case 10:
                return "HSUPA";
            case 11:
                return "HSPA";
            case 12:
                return "EvDo-rev.B";
            case 13:
                return "eHRPD";
            case 14:
                return "LTE";
            case 15:
                return "HSPAP";
            case 16:
                return "GSM";
            default:
                Rlog.w(LOG_TAG, "Unexpected radioTechnology=" + i);
                return "Unexpected";
        }
    }

    public String toString() {
        return this.mVoiceRegState + " " + this.mDataRegState + " " + (this.mRoaming ? "roaming" : CalendarContract.CalendarCache.TIMEZONE_TYPE_HOME) + " " + this.mOperatorAlphaLong + " " + this.mOperatorAlphaShort + " " + this.mOperatorNumeric + " " + (this.mIsManualNetworkSelection ? "(manual)" : "") + " " + rilRadioTechnologyToString(this.mRilVoiceRadioTechnology) + " " + rilRadioTechnologyToString(this.mRilDataRadioTechnology) + " " + (this.mCssIndicator ? "CSS supported" : "CSS not supported") + " " + this.mNetworkId + " " + this.mSystemId + " RoamInd=" + this.mCdmaRoamingIndicator + " DefRoamInd=" + this.mCdmaDefaultRoamingIndicator + " EmergOnly=" + this.mIsEmergencyOnly;
    }

    private void setNullState(int i) {
        Rlog.d(LOG_TAG, "[ServiceState] setNullState=" + i);
        this.mVoiceRegState = i;
        this.mDataRegState = i;
        this.mRoaming = false;
        this.mOperatorAlphaLong = null;
        this.mOperatorAlphaShort = null;
        this.mOperatorNumeric = null;
        this.mIsManualNetworkSelection = false;
        this.mRilVoiceRadioTechnology = 0;
        this.mRilDataRadioTechnology = 0;
        this.mCssIndicator = false;
        this.mNetworkId = -1;
        this.mSystemId = -1;
        this.mCdmaRoamingIndicator = -1;
        this.mCdmaDefaultRoamingIndicator = -1;
        this.mCdmaEriIconIndex = -1;
        this.mCdmaEriIconMode = -1;
        this.mIsEmergencyOnly = false;
    }

    public void setStateOutOfService() {
        setNullState(1);
    }

    public void setStateOff() {
        setNullState(3);
    }

    public void setState(int i) {
        setVoiceRegState(i);
        Rlog.e(LOG_TAG, "[ServiceState] setState deprecated use setVoiceRegState()");
    }

    public void setVoiceRegState(int i) {
        this.mVoiceRegState = i;
        Rlog.d(LOG_TAG, "[ServiceState] setVoiceRegState=" + this.mVoiceRegState);
    }

    public void setDataRegState(int i) {
        this.mDataRegState = i;
        Rlog.d(LOG_TAG, "[ServiceState] setDataRegState=" + this.mDataRegState);
    }

    public void setRoaming(boolean z) {
        this.mRoaming = z;
    }

    public void setEmergencyOnly(boolean z) {
        this.mIsEmergencyOnly = z;
    }

    public void setCdmaRoamingIndicator(int i) {
        this.mCdmaRoamingIndicator = i;
    }

    public void setCdmaDefaultRoamingIndicator(int i) {
        this.mCdmaDefaultRoamingIndicator = i;
    }

    public void setCdmaEriIconIndex(int i) {
        this.mCdmaEriIconIndex = i;
    }

    public void setCdmaEriIconMode(int i) {
        this.mCdmaEriIconMode = i;
    }

    public void setOperatorName(String str, String str2, String str3) {
        this.mOperatorAlphaLong = str;
        this.mOperatorAlphaShort = str2;
        this.mOperatorNumeric = str3;
    }

    public void setOperatorAlphaLong(String str) {
        this.mOperatorAlphaLong = str;
    }

    public void setIsManualSelection(boolean z) {
        this.mIsManualNetworkSelection = z;
    }

    private static boolean equalsHandlesNulls(Object obj, Object obj2) {
        if (obj == null) {
            return obj2 == null;
        }
        return obj.equals(obj2);
    }

    private void setFromNotifierBundle(Bundle bundle) {
        this.mVoiceRegState = bundle.getInt("voiceRegState");
        this.mDataRegState = bundle.getInt("dataRegState");
        this.mRoaming = bundle.getBoolean("roaming");
        this.mOperatorAlphaLong = bundle.getString("operator-alpha-long");
        this.mOperatorAlphaShort = bundle.getString("operator-alpha-short");
        this.mOperatorNumeric = bundle.getString("operator-numeric");
        this.mIsManualNetworkSelection = bundle.getBoolean(EthernetManager.ETHERNET_CONNECT_MODE_MANUAL);
        this.mRilVoiceRadioTechnology = bundle.getInt("radioTechnology");
        this.mRilDataRadioTechnology = bundle.getInt("dataRadioTechnology");
        this.mCssIndicator = bundle.getBoolean("cssIndicator");
        this.mNetworkId = bundle.getInt("networkId");
        this.mSystemId = bundle.getInt("systemId");
        this.mCdmaRoamingIndicator = bundle.getInt("cdmaRoamingIndicator");
        this.mCdmaDefaultRoamingIndicator = bundle.getInt("cdmaDefaultRoamingIndicator");
        this.mIsEmergencyOnly = bundle.getBoolean("emergencyOnly");
    }

    public void fillInNotifierBundle(Bundle bundle) {
        bundle.putInt("voiceRegState", this.mVoiceRegState);
        bundle.putInt("dataRegState", this.mDataRegState);
        bundle.putBoolean("roaming", Boolean.valueOf(this.mRoaming).booleanValue());
        bundle.putString("operator-alpha-long", this.mOperatorAlphaLong);
        bundle.putString("operator-alpha-short", this.mOperatorAlphaShort);
        bundle.putString("operator-numeric", this.mOperatorNumeric);
        bundle.putBoolean(EthernetManager.ETHERNET_CONNECT_MODE_MANUAL, Boolean.valueOf(this.mIsManualNetworkSelection).booleanValue());
        bundle.putInt("radioTechnology", this.mRilVoiceRadioTechnology);
        bundle.putInt("dataRadioTechnology", this.mRilDataRadioTechnology);
        bundle.putBoolean("cssIndicator", this.mCssIndicator);
        bundle.putInt("networkId", this.mNetworkId);
        bundle.putInt("systemId", this.mSystemId);
        bundle.putInt("cdmaRoamingIndicator", this.mCdmaRoamingIndicator);
        bundle.putInt("cdmaDefaultRoamingIndicator", this.mCdmaDefaultRoamingIndicator);
        bundle.putBoolean("emergencyOnly", Boolean.valueOf(this.mIsEmergencyOnly).booleanValue());
    }

    public void setRilVoiceRadioTechnology(int i) {
        this.mRilVoiceRadioTechnology = i;
    }

    public void setRilDataRadioTechnology(int i) {
        this.mRilDataRadioTechnology = i;
        Rlog.d(LOG_TAG, "[ServiceState] setDataRadioTechnology=" + this.mRilDataRadioTechnology);
    }

    public void setCssIndicator(int i) {
        this.mCssIndicator = i != 0;
    }

    public void setSystemAndNetworkId(int i, int i2) {
        this.mSystemId = i;
        this.mNetworkId = i2;
    }

    public int getRilVoiceRadioTechnology() {
        return this.mRilVoiceRadioTechnology;
    }

    public int getRilDataRadioTechnology() {
        return this.mRilDataRadioTechnology;
    }

    public int getRadioTechnology() {
        Rlog.e(LOG_TAG, "ServiceState.getRadioTechnology() DEPRECATED will be removed *******");
        return getRilDataRadioTechnology();
    }

    public int getNetworkType() {
        Rlog.e(LOG_TAG, "ServiceState.getNetworkType() DEPRECATED will be removed *******");
        return rilRadioTechnologyToNetworkType(this.mRilVoiceRadioTechnology);
    }

    public int getDataNetworkType() {
        return rilRadioTechnologyToNetworkType(this.mRilDataRadioTechnology);
    }

    public int getVoiceNetworkType() {
        return rilRadioTechnologyToNetworkType(this.mRilVoiceRadioTechnology);
    }

    public int getCssIndicator() {
        return this.mCssIndicator ? 1 : 0;
    }

    public int getNetworkId() {
        return this.mNetworkId;
    }

    public int getSystemId() {
        return this.mSystemId;
    }
}
