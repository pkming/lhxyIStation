package com.unisound.common;

import android.view.Window;
import com.unisound.client.ErrorCode;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class ap extends Thread {
    private ae b;
    private String c;
    public String a = "";
    private ac d = null;
    private ErrorCode e = new ErrorCode();

    private void a(int i) {
        ae aeVar = this.b;
        if (aeVar != null) {
            aeVar.a(this, this.e.createProfessionError(i));
        }
    }

    private void c(String str) {
        this.c = str;
        start();
    }

    public ac a() {
        return this.d;
    }

    void a(ae aeVar) {
        this.b = aeVar;
    }

    public void a(String str) {
        this.a = str;
    }

    void a(String str, ac acVar, List<String> list) {
        this.d = acVar;
        acVar.a(false);
        this.a += "?ak=" + str + "&imei=" + k.q + "&an=wechar&si=" + acVar.c() + "&av=1.0&sn=abcdefg&trace=1";
        StringBuilder sb = new StringBuilder();
        sb.append("data=<SCENE>\n");
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            sb.append(it.next()).append("\n");
        }
        sb.append("</SCENE>");
        c(sb.toString());
    }

    public void b() {
        this.b = null;
    }

    protected void b(String str) {
        r.e("UploadSceneTask:" + str);
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        int i;
        int i2 = ErrorCode.UPLOAD_SCENE_DATA_NETWORK_ERROR;
        try {
            byte[] bytes = this.c.getBytes();
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(this.a).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setConnectTimeout(Window.PROGRESS_SECONDARY_END);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
            if (httpURLConnection.getResponseCode() == 200) {
                JSONObject jSONObject = new JSONObject(new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream())).readLine());
                int i3 = Integer.parseInt(jSONObject.has("errorCode") ? jSONObject.getString("errorCode") : "");
                r.c("upload userdata code=" + i3);
                if (i3 == 0) {
                    this.d.a(true);
                    i = 0;
                } else {
                    i = i3 == -1 ? ErrorCode.UPLOAD_SCENE_GENERAL_ERROR : i3 == -5 ? ErrorCode.UPLOAD_SCENE_INVALID_KEY : i3 == -8 ? ErrorCode.UPLOAD_SCENE_STREAM_IO_ERR : i3 == -11 ? ErrorCode.UPLOAD_SCENE_UNKNOWN_ERR : i3 == -12 ? ErrorCode.UPLOAD_SCENE_DATA_SIZE_IS_FORBIDDEN : i3 == -13 ? ErrorCode.UPLOAD_SCENE_INVALID_VER : i3 == -6 ? ErrorCode.UPLOAD_SCENE_DATA_TOO_FAST : ErrorCode.UPLOAD_SCENE_DATA_SERVER_REFUSED;
                }
                i2 = i;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        a(i2);
    }
}
