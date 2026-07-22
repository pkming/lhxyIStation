package org.apache.commons.net.nntp;

import android.telephony.PhoneNumberUtils;
import java.util.Calendar;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes3.dex */
public final class NewGroupsOrNewsQuery {
    private final String __date;
    private final boolean __isGMT;
    private final String __time;
    private StringBuffer __distributions = null;
    private StringBuffer __newsgroups = null;

    public NewGroupsOrNewsQuery(Calendar calendar, boolean z) {
        this.__isGMT = z;
        StringBuilder sb = new StringBuilder();
        String string = Integer.toString(calendar.get(1));
        int length = string.length();
        if (length >= 2) {
            sb.append(string.substring(length - 2));
        } else {
            sb.append(TarConstants.VERSION_POSIX);
        }
        String string2 = Integer.toString(calendar.get(2) + 1);
        int length2 = string2.length();
        if (length2 == 1) {
            sb.append('0');
            sb.append(string2);
        } else if (length2 == 2) {
            sb.append(string2);
        } else {
            sb.append("01");
        }
        String string3 = Integer.toString(calendar.get(5));
        int length3 = string3.length();
        if (length3 == 1) {
            sb.append('0');
            sb.append(string3);
        } else if (length3 == 2) {
            sb.append(string3);
        } else {
            sb.append("01");
        }
        this.__date = sb.toString();
        sb.setLength(0);
        String string4 = Integer.toString(calendar.get(11));
        int length4 = string4.length();
        if (length4 == 1) {
            sb.append('0');
            sb.append(string4);
        } else if (length4 == 2) {
            sb.append(string4);
        } else {
            sb.append(TarConstants.VERSION_POSIX);
        }
        String string5 = Integer.toString(calendar.get(12));
        int length5 = string5.length();
        if (length5 == 1) {
            sb.append('0');
            sb.append(string5);
        } else if (length5 == 2) {
            sb.append(string5);
        } else {
            sb.append(TarConstants.VERSION_POSIX);
        }
        String string6 = Integer.toString(calendar.get(13));
        int length6 = string6.length();
        if (length6 == 1) {
            sb.append('0');
            sb.append(string6);
        } else if (length6 == 2) {
            sb.append(string6);
        } else {
            sb.append(TarConstants.VERSION_POSIX);
        }
        this.__time = sb.toString();
    }

    public void addNewsgroup(String str) {
        StringBuffer stringBuffer = this.__newsgroups;
        if (stringBuffer != null) {
            stringBuffer.append(PhoneNumberUtils.PAUSE);
        } else {
            this.__newsgroups = new StringBuffer();
        }
        this.__newsgroups.append(str);
    }

    public void omitNewsgroup(String str) {
        addNewsgroup("!" + str);
    }

    public void addDistribution(String str) {
        StringBuffer stringBuffer = this.__distributions;
        if (stringBuffer != null) {
            stringBuffer.append(PhoneNumberUtils.PAUSE);
        } else {
            this.__distributions = new StringBuffer();
        }
        this.__distributions.append(str);
    }

    public String getDate() {
        return this.__date;
    }

    public String getTime() {
        return this.__time;
    }

    public boolean isGMT() {
        return this.__isGMT;
    }

    public String getDistributions() {
        StringBuffer stringBuffer = this.__distributions;
        if (stringBuffer == null) {
            return null;
        }
        return stringBuffer.toString();
    }

    public String getNewsgroups() {
        StringBuffer stringBuffer = this.__newsgroups;
        if (stringBuffer == null) {
            return null;
        }
        return stringBuffer.toString();
    }
}
