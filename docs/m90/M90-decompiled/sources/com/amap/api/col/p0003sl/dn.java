package com.amap.api.col.p0003sl;

import android.content.Context;
import android.content.SharedPreferences;

/* JADX INFO: compiled from: PreferenceTools.java */
/* JADX INFO: loaded from: classes2.dex */
public final class dn {
    public static void a(Context context, String str, String str2, Object obj) {
        if (context == null) {
            return;
        }
        SharedPreferences.Editor editorEdit = context.getSharedPreferences(str, 0).edit();
        if (obj instanceof String) {
            editorEdit.putString(str2, (String) obj);
        } else if (obj instanceof Integer) {
            editorEdit.putInt(str2, ((Integer) obj).intValue());
        } else if (obj instanceof Boolean) {
            editorEdit.putBoolean(str2, ((Boolean) obj).booleanValue());
        } else if (obj instanceof Float) {
            editorEdit.putFloat(str2, ((Float) obj).floatValue());
        } else if (obj instanceof Long) {
            editorEdit.putLong(str2, ((Long) obj).longValue());
        } else {
            editorEdit.putString(str2, obj.toString());
        }
        editorEdit.apply();
    }

    public static String a(Context context, String str, String str2, String str3) {
        Object objB = b(context, str, str2, str3);
        return objB != null ? (String) objB : str3;
    }

    public static Long a(Context context, String str, String str2, Long l) {
        Object objB = b(context, str, str2, l);
        return objB != null ? (Long) objB : l;
    }

    private static Object b(Context context, String str, String str2, Object obj) {
        if (context == null) {
            return null;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(str, 0);
        if (obj instanceof String) {
            return sharedPreferences.getString(str2, (String) obj);
        }
        if (obj instanceof Integer) {
            return Integer.valueOf(sharedPreferences.getInt(str2, ((Integer) obj).intValue()));
        }
        if (obj instanceof Boolean) {
            return Boolean.valueOf(sharedPreferences.getBoolean(str2, ((Boolean) obj).booleanValue()));
        }
        if (obj instanceof Float) {
            return Float.valueOf(sharedPreferences.getFloat(str2, ((Float) obj).floatValue()));
        }
        if (obj instanceof Long) {
            return Long.valueOf(sharedPreferences.getLong(str2, ((Long) obj).longValue()));
        }
        return null;
    }
}
