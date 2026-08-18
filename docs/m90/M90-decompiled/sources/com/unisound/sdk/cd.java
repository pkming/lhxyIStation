package com.unisound.sdk;

import android.util.SparseArray;
import android.view.Window;
import cn.yunzhisheng.casr.EncodeContent;
import com.unisound.client.ErrorCode;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class cd extends Thread {
    public static String a = "http://v2.hivoice.cn:8081/casr/upload";
    public static final int b = 10000;
    public static final int c = 3000;
    public static final int d = 3000;
    public static final int e = 3000;
    public static final int f = 3000;
    public static final int g = 3000;
    public static final int h = 3000;
    private ce i;
    private EncodeContent j = new EncodeContent();
    private String k;

    private Map<Integer, List<String>> a(Map<Integer, List<String>> map) {
        Iterator<Integer> it = map.keySet().iterator();
        while (it.hasNext()) {
            List<String> list = map.get(it.next());
            if (list != null) {
                a(list);
                list.size();
            }
        }
        return map;
    }

    private void a(String str) {
        com.unisound.common.r.e("UploadUserData:" + str);
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x0042  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void a(java.util.List<java.lang.String> r9) {
        /*
            r8 = this;
            int r0 = r9.size()
            r1 = 1
            int r0 = r0 - r1
        L6:
            r2 = -1
            if (r0 <= r2) goto L48
            java.lang.Object r3 = r9.get(r0)
            java.lang.String r3 = (java.lang.String) r3
            if (r3 == 0) goto L42
            int r4 = r3.length()
            if (r4 != 0) goto L18
            goto L42
        L18:
            r4 = 0
            java.lang.String r5 = ">"
            int r6 = r3.indexOf(r5)
            java.lang.String r7 = ""
            if (r6 <= r2) goto L28
            java.lang.String r3 = r3.replaceAll(r5, r7)
            r4 = r1
        L28:
            java.lang.String r5 = "<"
            int r6 = r3.indexOf(r5)
            if (r6 <= r2) goto L35
            java.lang.String r3 = r3.replaceAll(r5, r7)
            r4 = r1
        L35:
            int r2 = r3.length()
            if (r2 != 0) goto L3c
            goto L42
        L3c:
            if (r4 == 0) goto L45
            r9.set(r0, r3)
            goto L45
        L42:
            r9.remove(r0)
        L45:
            int r0 = r0 + (-1)
            goto L6
        L48:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.unisound.sdk.cd.a(java.util.List):void");
    }

    private boolean a(int i, List<String> list, StringBuilder sb) {
        String strC = c(i);
        if (strC == null) {
            a(" not find tag id = " + i);
            return true;
        }
        if (list == null) {
            a(strC + " = NULL");
            return true;
        }
        int iB = b(i);
        if (list.size() > iB) {
            a(strC + " Number of over count > " + iB);
            a(ErrorCode.UPLOAD_USER_TOO_LARGE);
            return false;
        }
        a(list);
        sb.append("<" + strC + ">\n");
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            sb.append(it.next()).append("\n");
        }
        sb.append("</" + strC + ">\n");
        return true;
    }

    private int b(int i) {
        switch (i) {
            case 1:
                return 10000;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                return 3000;
            default:
                return -1;
        }
    }

    private void b(String str) {
        this.k = str;
        start();
    }

    private String c(int i) {
        switch (i) {
            case 1:
                return "NAME";
            case 2:
                return "APP";
            case 3:
                return "SONG";
            case 4:
                return "SINGER";
            case 5:
                return "ALBUM";
            case 6:
                return "COMMAND";
            case 7:
                return "wechat_contact";
            default:
                return null;
        }
    }

    public void a(int i) {
        ce ceVar = this.i;
        if (ceVar != null) {
            ceVar.e(i);
        }
    }

    public void a(ce ceVar) {
        this.i = ceVar;
    }

    public void a(String str, SparseArray<List<String>> sparseArray) {
        StringBuilder sb = new StringBuilder();
        sb.append(str).append(";").append(com.unisound.common.k.x).append("\n");
        int size = sparseArray.size();
        for (int i = 0; i < size; i++) {
            int iKeyAt = sparseArray.keyAt(i);
            if (!a(iKeyAt, sparseArray.get(iKeyAt), sb)) {
                return;
            }
        }
        b(sb.toString());
    }

    public void a(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append(str).append(";").append(com.unisound.common.k.x).append("\n");
        b(sb.toString());
    }

    public void a(String str, Map<Integer, List<String>> map) {
        if (this.i == null || map == null) {
            a(ErrorCode.UPLOAD_USER_DATA_EMPTY);
            return;
        }
        if (map.isEmpty()) {
            a(ErrorCode.UPLOAD_USER_DATA_EMPTY);
            return;
        }
        Map<Integer, List<String>> mapA = a(map);
        StringBuilder sb = new StringBuilder();
        sb.append(str).append(";").append(com.unisound.common.k.x).append("\n");
        for (Integer num : mapA.keySet()) {
            if (!a(num.intValue(), mapA.get(num), sb)) {
                return;
            }
        }
        b(sb.toString());
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        int i = ErrorCode.UPLOAD_USER_DATA_NETWORK_ERROR;
        try {
            byte[] bytes = com.unisound.common.k.x.getBytes();
            com.unisound.common.r.c("uploadUserData server is :\n" + a + "\n Data is \n" + this.k);
            byte[] bytes2 = this.k.getBytes();
            byte[] bArr = new byte[bytes.length + bytes2.length + 10];
            if (this.j.EncodeTotalContent(bytes, bytes2, bArr) != 0) {
                i = ErrorCode.UPLOAD_USER_ENCODE_ERROR;
            } else {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(a).openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setConnectTimeout(Window.PROGRESS_SECONDARY_END);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(bArr);
                outputStream.flush();
                outputStream.close();
                if (httpURLConnection.getResponseCode() == 200) {
                    int i2 = Integer.parseInt(new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream())).readLine());
                    com.unisound.common.r.c("upload userdata code=" + i2);
                    i = i2 == 0 ? 0 : i2 == -6 ? ErrorCode.UPLOAD_USER_DATA_TOO_FAST : ErrorCode.UPLOAD_USER_DATA_SERVER_REFUSED;
                }
            }
        } catch (Exception unused) {
        }
        a(i);
    }
}
