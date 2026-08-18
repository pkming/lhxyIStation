package com.unisound.sdk;

import cn.yunzhisheng.nlu.OfflineNlu;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes2.dex */
public class j {
    public String d;
    public float e;
    public OfflineNlu h;
    private String i;
    public List<String> a = new ArrayList();
    public boolean b = true;
    public boolean c = false;
    public boolean f = false;
    public String g = "";

    public static String a(float f, String str) {
        String[] strArrSplit = str.split("\n");
        int length = strArrSplit.length - 1;
        return length > 0 ? f > b(strArrSplit[length]) ? "" : str.replace(strArrSplit[length], "").trim() : str;
    }

    public static float b(String str) {
        try {
            return Float.parseFloat(str);
        } catch (Exception e) {
            e.printStackTrace();
            return -100.0f;
        }
    }

    public static float d(String str) {
        String[] strArrSplit = str.split("\n");
        int length = strArrSplit.length - 1;
        if (length > 0) {
            return b(strArrSplit[length]);
        }
        return -25.0f;
    }

    public static String e(String str) {
        Pattern patternCompile = Pattern.compile("<s>|</s>");
        return Pattern.compile("<[\\w]*>").matcher(Pattern.compile("<[\\w]*>").matcher(patternCompile.matcher(str).replaceAll("")).replaceAll("").replaceAll("/", "")).replaceAll("").split("\n")[0].trim();
    }

    public String a() {
        Iterator<String> it = this.a.iterator();
        return it.hasNext() ? it.next() : "";
    }

    public boolean a(String str) {
        this.d = "";
        if (str == null || str.length() == 0) {
            return false;
        }
        String[] strArrSplit = Pattern.compile("<[\\w]*>").matcher(Pattern.compile("<[\\w]*>").matcher(Pattern.compile("<s>|</s>").matcher(str).replaceAll("")).replaceAll("\n").replaceAll("/", "").replaceAll("\n\n", "\n")).replaceAll("").split("\n");
        this.e = -25.0f;
        int length = strArrSplit.length - 1;
        if (length <= 0) {
            return false;
        }
        this.e = b(strArrSplit[length]);
        this.d = length == 1 ? strArrSplit[0] : strArrSplit[0] + strArrSplit[length - 1];
        return true;
    }

    public boolean a(String str, boolean z) {
        this.a.clear();
        int i = 0;
        if (str == null || str.length() == 0) {
            return false;
        }
        if (str.split("\n").length < 2) {
            com.unisound.common.r.e("FixrecognizeResult -> setResultList: RecognitionResult error for lessing than two lines!");
            return false;
        }
        if (this.f && !z) {
            if (this.g.equals("") || this.h == null) {
                com.unisound.common.r.d("RecognizeResult", "setResultList : nluConfigFile didn't exists or OfflineNlu is null");
                return false;
            }
            String[] strArrSplit = str.split("\n");
            this.e = -25.0f;
            int length = strArrSplit.length - 1;
            if (length <= 0) {
                return false;
            }
            this.e = b(strArrSplit[length]);
            while (i < length) {
                this.a.add(this.h.a("[" + strArrSplit[i] + "]", ""));
                i++;
            }
            return true;
        }
        if (this.c) {
            String[] strArrSplit2 = str.split("\n");
            this.e = -25.0f;
            int length2 = strArrSplit2.length - 1;
            com.unisound.common.r.c("RecognizeResult", "setResultList : arrayOfstring.length =" + strArrSplit2.length);
            if (length2 <= 0) {
                return false;
            }
            this.e = b(strArrSplit2[length2]);
            while (i < length2) {
                try {
                    this.a.add(com.unisound.common.aa.b(strArrSplit2[i], this.e, this.i + "result.xml").toString().replace(" ", "").replace("_", ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                i++;
            }
            return true;
        }
        if (!this.b) {
            String[] strArrSplit3 = str.split("\n");
            this.e = -25.0f;
            int length3 = strArrSplit3.length - 1;
            if (length3 <= 0) {
                return false;
            }
            this.e = b(strArrSplit3[length3]);
            while (i < length3) {
                this.a.add(strArrSplit3[i]);
                i++;
            }
            return true;
        }
        String[] strArrSplit4 = Pattern.compile("<[\\w]*>").matcher(Pattern.compile("<[\\w]*>").matcher(Pattern.compile("<s>|</s>").matcher(str).replaceAll("")).replaceAll("").replaceAll("/", "")).replaceAll("").split("\n");
        this.e = -25.0f;
        int length4 = strArrSplit4.length - 1;
        if (length4 <= 0) {
            return false;
        }
        this.e = b(strArrSplit4[length4]);
        while (i < length4) {
            this.a.add(strArrSplit4[i]);
            i++;
        }
        return true;
    }

    public void c(String str) {
        this.i = str;
    }

    public String toString() {
        String str = this.d;
        return (str == null || str.length() <= 0) ? "" : this.d + "\n" + this.e;
    }
}
