package com.lianhexinye.m90.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.core.content.SharedPreferencesCompat;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class SPUserInfoUtils {
    public static final String ACCOUNT = "account";
    public static final String ARRIVALTIME = "arrivalTime";
    public static final String BUSDIRECTIONNAME = "busDirectionName";
    public static final String BUSLINENAME = "busLineName";
    public static final String BUYGOTO = "buygoto";
    public static final String CARDSTATUSINFO = "cardStatusInfo";
    public static final String CARNUMBER = "carNumber";
    public static final String DEPARTURETIME = "departureTime";
    public static final String DISPATCHEND = "dispatchEnd";
    public static final String DISPATCHSTATE = "dispatchState";
    public static final String DRIVERCARDID = "driverCardId";
    public static final String DRIVERID = "driverID";
    public static final String DRIVERNAME = "driverName";
    public static final String DRIVERSTATE = "driverState";
    private static String FILLNAME = "M90Info";
    public static final String GUIDEBOARDNAME = "guideboardName";
    public static final String ISAUTHORIZATION = "isAuthorization";
    public static final String ISBROADCASTINITIALSTATION = "isBroadcastInitialStation";
    public static final String ISCHARTEREDBUS = "isCharteredBus";
    public static final String ISCONFIGVALID = "isConfigValid";
    public static final String ISETHERNETENABLED = "ethernetEnabled";
    public static final String ISFRIST = "isfrist";
    public static final String ISLOGIN = "isLogin";
    public static final String ISSWITCHBUSLINE = "isSwitchBusLine";
    public static final String JOBNUMBER = "jobNumber";
    public static final String LASTTIMELATITUDE = "lastTimeLatitude";
    public static final String LASTTIMELONGITUDE = "lastTimeLongitude";
    public static final String LINEATTRIBUTE = "lineAttribute";
    public static final String LINEGUID = "lineGuid";
    public static final String LINENUMBER = "lineNumber";
    public static final String LOGIP = "logIP";
    public static final String LOGPASSWORD = "logPassword";
    public static final String LOGPATH = "logPath";
    public static final String LOGPORT = "logPort";
    public static final String LOGSENDSTATUS = "logSendStatus";
    public static final String LOGTASK = "logTask";
    public static final String LOGTIME = "logTime";
    public static final String LOGUSERNAME = "logUsername";
    public static final String MESSAGENO = "messageNo";
    public static final String NEXTTRIP = "nextTrip";
    public static final String OT = "ot";
    public static final String OTS = "ots";
    public static final String PRIMARYARRIVALTIME = "primaryArrivalTime";
    public static final String PRIMARYDEPARTURETIME = "primaryDepartureTime";
    public static final String SCHEDULENO = "scheduleNo";
    public static final String SELLGOTO = "sellgoto";
    public static final String SESSIONID = "sessionId";
    public static final String SIGNINOUTSTATUS = "signInOutStatus";
    public static final String SOURCEFILELASTMODIFYTIME = "sourceFileLastModifyTime";
    public static final String THISTRIP = "thisTrip";
    public static final String TIMESNO = "timesNo";
    public static final String TOMORROW = "tomorrow";
    public static final String TOTALMILEAGE = "totalMileage";
    public static final String TS = "ts";
    public static final String TTSNOTICECONTENT = "ttsNoticeContent";
    public static final String TTSNOTICECONTENTTWO = "ttsNoticeContentTwo";
    public static final String UPLINK = "uplink";
    public static final String USERID = "userId";
    public static final String VEHICLESTATUSID = "vehicleStatusId";
    public static final String VEHICLESTATUSNAME = "vehicleStatusName";
    public static final String WEATHERCITYNAME = "weatherCityName";

    public static void put(Context context, String str, Object obj) {
        SharedPreferences.Editor editorEdit = context.getSharedPreferences(FILLNAME, 0).edit();
        if (obj instanceof String) {
            editorEdit.putString(str, (String) obj);
        } else if (obj instanceof Integer) {
            editorEdit.putInt(str, ((Integer) obj).intValue());
        } else if (obj instanceof Boolean) {
            editorEdit.putBoolean(str, ((Boolean) obj).booleanValue());
        } else if (obj instanceof Float) {
            editorEdit.putFloat(str, ((Float) obj).floatValue());
        } else if (obj instanceof Long) {
            editorEdit.putLong(str, ((Long) obj).longValue());
        }
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editorEdit);
    }

    public static Object get(Context context, String str, Object obj) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILLNAME, 0);
        if (obj instanceof String) {
            return sharedPreferences.getString(str, (String) obj);
        }
        if (obj instanceof Integer) {
            return Integer.valueOf(sharedPreferences.getInt(str, ((Integer) obj).intValue()));
        }
        if (obj instanceof Boolean) {
            return Boolean.valueOf(sharedPreferences.getBoolean(str, ((Boolean) obj).booleanValue()));
        }
        if (obj instanceof Float) {
            return Float.valueOf(sharedPreferences.getFloat(str, ((Float) obj).floatValue()));
        }
        if (obj instanceof Long) {
            return Long.valueOf(sharedPreferences.getLong(str, ((Long) obj).longValue()));
        }
        return null;
    }

    public static Map<String, ?> getAll(Context context) {
        return context.getSharedPreferences(FILLNAME, 0).getAll();
    }

    public static void remove(Context context, String str) {
        SharedPreferences.Editor editorEdit = context.getSharedPreferences(FILLNAME, 0).edit();
        editorEdit.remove(str);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editorEdit);
    }

    public static void clear(Context context) {
        SharedPreferences.Editor editorEdit = context.getSharedPreferences(FILLNAME, 0).edit();
        editorEdit.clear();
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editorEdit);
    }
}
