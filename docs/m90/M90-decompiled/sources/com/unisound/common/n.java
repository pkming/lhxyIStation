package com.unisound.common;

import android.view.Window;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/* JADX INFO: loaded from: classes2.dex */
public class n {
    public static String a(String str) {
        return a(str, Window.PROGRESS_SECONDARY_END);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:70:0x00c7 A[Catch: IOException -> 0x00c3, TryCatch #9 {IOException -> 0x00c3, blocks: (B:66:0x00bf, B:70:0x00c7, B:72:0x00cc), top: B:77:0x00bf }] */
    /* JADX WARN: Removed duplicated region for block: B:72:0x00cc A[Catch: IOException -> 0x00c3, TRY_LEAVE, TryCatch #9 {IOException -> 0x00c3, blocks: (B:66:0x00bf, B:70:0x00c7, B:72:0x00cc), top: B:77:0x00bf }] */
    /* JADX WARN: Removed duplicated region for block: B:77:0x00bf A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r1v0 */
    /* JADX WARN: Type inference failed for: r1v1, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r1v15 */
    /* JADX WARN: Type inference failed for: r1v17 */
    /* JADX WARN: Type inference failed for: r1v18 */
    /* JADX WARN: Type inference failed for: r1v2 */
    /* JADX WARN: Type inference failed for: r1v9, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r6v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r6v1, types: [java.net.HttpURLConnection] */
    /* JADX WARN: Type inference failed for: r6v10, types: [java.net.HttpURLConnection] */
    /* JADX WARN: Type inference failed for: r6v11, types: [java.net.HttpURLConnection] */
    /* JADX WARN: Type inference failed for: r6v13, types: [java.net.HttpURLConnection] */
    /* JADX WARN: Type inference failed for: r6v14 */
    /* JADX WARN: Type inference failed for: r6v15 */
    /* JADX WARN: Type inference failed for: r6v3 */
    /* JADX WARN: Type inference failed for: r6v4 */
    /* JADX WARN: Type inference failed for: r6v6 */
    /* JADX WARN: Type inference failed for: r6v8 */
    /* JADX WARN: Type inference failed for: r7v0, types: [int] */
    /* JADX WARN: Type inference failed for: r7v1 */
    /* JADX WARN: Type inference failed for: r7v12, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r7v2 */
    /* JADX WARN: Type inference failed for: r7v3 */
    /* JADX WARN: Type inference failed for: r7v4, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r7v5, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r7v7 */
    /* JADX WARN: Type inference failed for: r7v9 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String a(java.lang.String r6, int r7) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 212
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.unisound.common.n.a(java.lang.String, int):java.lang.String");
    }

    public static String a(String str, String str2) {
        return a(str, str2.getBytes());
    }

    public static String a(String str, byte[] bArr) {
        return a(str, bArr, Window.PROGRESS_SECONDARY_END);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v0 */
    /* JADX WARN: Type inference failed for: r1v1 */
    /* JADX WARN: Type inference failed for: r1v10 */
    /* JADX WARN: Type inference failed for: r1v15 */
    /* JADX WARN: Type inference failed for: r1v18 */
    /* JADX WARN: Type inference failed for: r1v2, types: [java.net.HttpURLConnection] */
    /* JADX WARN: Type inference failed for: r1v3, types: [java.net.HttpURLConnection] */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r1v5 */
    /* JADX WARN: Type inference failed for: r1v6 */
    /* JADX WARN: Type inference failed for: r1v9 */
    public static String a(String str, byte[] bArr, int i) throws Throwable {
        HttpURLConnection httpURLConnection;
        String string = "";
        ?? r1 = 0;
        r1 = 0;
        try {
            try {
                httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
            httpURLConnection.setRequestMethod("POST");
            r1 = 1;
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setConnectTimeout(i);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(bArr);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e2) {
            e = e2;
            r1 = httpURLConnection;
            e.printStackTrace();
            if (r1 != 0) {
                r1.disconnect();
            }
        } catch (Throwable th2) {
            th = th2;
            r1 = httpURLConnection;
            if (r1 != 0) {
                r1.disconnect();
            }
            throw th;
        }
        if (httpURLConnection.getResponseCode() == 200) {
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            Object obj = inputStream;
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                StringBuilder sbAppend = new StringBuilder().append(string);
                string = sbAppend.append(line).toString();
                obj = sbAppend;
                return string;
            }
            bufferedReader.close();
            r1 = obj;
        }
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }
        return string;
    }

    public static int b(String str, String str2) {
        return b(str, str2.getBytes(), Window.PROGRESS_SECONDARY_END);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0 */
    /* JADX WARN: Type inference failed for: r0v1 */
    /* JADX WARN: Type inference failed for: r0v10 */
    /* JADX WARN: Type inference failed for: r0v11 */
    /* JADX WARN: Type inference failed for: r0v2, types: [java.net.HttpURLConnection] */
    /* JADX WARN: Type inference failed for: r0v3, types: [java.net.HttpURLConnection] */
    /* JADX WARN: Type inference failed for: r0v4 */
    /* JADX WARN: Type inference failed for: r0v5 */
    /* JADX WARN: Type inference failed for: r0v6 */
    /* JADX WARN: Type inference failed for: r0v9 */
    public static int b(String str, byte[] bArr, int i) throws Throwable {
        int responseCode;
        HttpURLConnection httpURLConnection;
        ?? r0 = 0;
        r0 = 0;
        try {
            try {
                httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
            httpURLConnection.setRequestMethod("POST");
            r0 = 1;
            r0 = 1;
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setConnectTimeout(i);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(bArr);
            outputStream.flush();
            outputStream.close();
            responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {
                responseCode = 0;
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        } catch (Exception e2) {
            e = e2;
            r0 = httpURLConnection;
            e.printStackTrace();
            if (r0 != 0) {
                r0.disconnect();
            }
            responseCode = -1;
        } catch (Throwable th2) {
            th = th2;
            r0 = httpURLConnection;
            if (r0 != 0) {
                r0.disconnect();
            }
            throw th;
        }
        return responseCode;
    }
}
