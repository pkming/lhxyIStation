package com.amap.api.col.p0003sl;

import android.content.Context;
import com.amap.api.services.poisearch.PoiSearchV2;

/* JADX INFO: compiled from: PoiHandler.java */
/* JADX INFO: loaded from: classes2.dex */
abstract class ge<T, V> extends fh<T, V> {
    public ge(Context context, T t) {
        super(context, t);
    }

    protected static boolean c(String str) {
        return str == null || str.equals("") || str.equals("[]");
    }

    protected static String a(PoiSearchV2.ShowFields showFields) {
        if (showFields == null || showFields.getValue() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if ((showFields.getValue() & 1) != 0) {
            sb.append("children,");
        }
        if ((showFields.getValue() & 2) != 0) {
            sb.append("business,");
        }
        if ((showFields.getValue() & 4) != 0) {
            sb.append("indoor,");
        }
        if ((showFields.getValue() & 8) != 0) {
            sb.append("navi,");
        }
        if ((showFields.getValue() & 16) != 0) {
            sb.append("photos,");
        }
        if (sb.length() <= 0) {
            return null;
        }
        sb.replace(sb.length() - 1, sb.length(), "");
        return sb.toString();
    }
}
