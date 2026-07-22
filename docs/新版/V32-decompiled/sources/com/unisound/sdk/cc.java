package com.unisound.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import cn.yunzhisheng.casr.EncodeContent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class cc {
    public static String a = "http://rtc.hivoice.cn";
    private static final int d = 10000;
    private String b = "/data-process-service/oneshot";
    private EncodeContent c = new EncodeContent();
    private Context e;
    private String f;

    public cc(Context context, String str) {
        this.e = context;
        this.f = str;
    }

    private Set<String> a(Context context) {
        return context.getSharedPreferences("onlinewakeup", 0).getStringSet("onlineWakeupWord", null);
    }

    public static boolean a(String str) {
        return Pattern.compile("[`~!@#$%^&*()_\\+\\-\\={}|\\\\\\[\\]\\:\";'<>?,./~·！@#￥%……&*（）——\\+\\-\\={}|【】、：“”；‘’《》？，。、｀～！＠＃＄％＾＆＊（）＿＋－＝｛｝｜［］＼：＂＂；＇＇＜＞？，．／·～！＠＃￥％……＆×（）——＋－＝｛｝｜【】＼：“”；‘’《》？，。、]").matcher(str).find();
    }

    public String a(int i) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("status", i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jSONObject.toString();
    }

    public String a(String str, String str2) {
        String strA;
        com.unisound.common.r.b("UploadOneShotOnlineWakeupData onlineWakeupWord => " + str2);
        a(0);
        try {
            byte[] bytes = str.getBytes();
            byte[] bytes2 = str2.getBytes();
            byte[] bArr = new byte[bytes.length + bytes2.length + 10];
            if (this.c.EncodeTotalContent(bytes, bytes2, bArr) != 0) {
                strA = a(-63605);
            } else {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(a + this.b).openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setConnectTimeout(10000);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                com.unisound.common.r.b("UploadOneShotOnlineWakeupData conn param => " + httpURLConnection.toString());
                outputStream.write(bArr);
                outputStream.flush();
                outputStream.close();
                if (httpURLConnection.getResponseCode() == 200) {
                    strA = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream())).readLine();
                    com.unisound.common.r.c("upload response codeStr=" + strA);
                } else {
                    strA = a(-63603);
                }
            }
            return strA;
        } catch (Exception e) {
            com.unisound.common.r.e("UploadOneShotOnlineWakeupData exception =>" + e.getMessage());
            return a(-63603);
        }
    }

    public String a(List<String> list) {
        com.unisound.common.y yVar = new com.unisound.common.y();
        yVar.a("1.0");
        yVar.b(this.f);
        yVar.c(com.unisound.common.k.x);
        yVar.a(a(this.e));
        HashSet hashSet = new HashSet();
        hashSet.addAll(list);
        yVar.b(hashSet);
        return yVar.g();
    }

    public void a(Set<String> set) {
        SharedPreferences.Editor editorEdit = this.e.getSharedPreferences("onlinewakeup", 0).edit();
        editorEdit.putStringSet("onlineWakeupWord", set);
        editorEdit.commit();
    }
}
