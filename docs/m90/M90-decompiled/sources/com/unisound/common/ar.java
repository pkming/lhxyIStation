package com.unisound.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes2.dex */
public class ar {
    private static File a(String str) {
        File file = new File(str);
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            r.c("VprHttpClient : ", "create filedir failure!");
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /* JADX WARN: Removed duplicated region for block: B:57:0x0093  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x00a0 A[Catch: IOException -> 0x009c, TRY_LEAVE, TryCatch #10 {IOException -> 0x009c, blocks: (B:59:0x0098, B:63:0x00a0), top: B:73:0x0098 }] */
    /* JADX WARN: Removed duplicated region for block: B:73:0x0098 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean a(java.lang.String r4, java.lang.String r5) throws java.lang.Throwable {
        /*
            r0 = 0
            java.io.File r5 = a(r5)     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L6b
            java.net.URL r1 = new java.net.URL     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L6b
            r1.<init>(r4)     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L6b
            java.net.URLConnection r4 = r1.openConnection()     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L6b
            java.net.HttpURLConnection r4 = (java.net.HttpURLConnection) r4     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L6b
            r1 = 30000(0x7530, float:4.2039E-41)
            r4.setConnectTimeout(r1)     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L62
            java.lang.String r1 = "GET"
            r4.setRequestMethod(r1)     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L62
            r4.connect()     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L62
            int r1 = r4.getResponseCode()     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L62
            r2 = 200(0xc8, float:2.8E-43)
            if (r1 != r2) goto L44
            java.io.InputStream r1 = r4.getInputStream()     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L62
            byte[] r2 = a(r1)     // Catch: java.lang.Throwable -> L3e java.lang.Exception -> L41
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch: java.lang.Throwable -> L3e java.lang.Exception -> L41
            r3.<init>(r5)     // Catch: java.lang.Throwable -> L3e java.lang.Exception -> L41
            r3.write(r2)     // Catch: java.lang.Throwable -> L3a java.lang.Exception -> L3c
            r3.flush()     // Catch: java.lang.Throwable -> L3a java.lang.Exception -> L3c
            r0 = r3
            goto L45
        L3a:
            r5 = move-exception
            goto L60
        L3c:
            r5 = move-exception
            goto L65
        L3e:
            r5 = move-exception
            r3 = r0
            goto L60
        L41:
            r5 = move-exception
            r3 = r0
            goto L65
        L44:
            r1 = r0
        L45:
            if (r4 == 0) goto L4a
            r4.disconnect()
        L4a:
            if (r0 == 0) goto L52
            r0.close()     // Catch: java.io.IOException -> L50
            goto L52
        L50:
            r4 = move-exception
            goto L58
        L52:
            if (r1 == 0) goto L5b
            r1.close()     // Catch: java.io.IOException -> L50
            goto L5b
        L58:
            r4.printStackTrace()
        L5b:
            r4 = 1
            return r4
        L5d:
            r5 = move-exception
            r1 = r0
            r3 = r1
        L60:
            r0 = r4
            goto L91
        L62:
            r5 = move-exception
            r1 = r0
            r3 = r1
        L65:
            r0 = r4
            goto L6e
        L67:
            r5 = move-exception
            r1 = r0
            r3 = r1
            goto L91
        L6b:
            r5 = move-exception
            r1 = r0
            r3 = r1
        L6e:
            java.lang.String r4 = "VprHttpClient : "
            java.lang.String r2 = "getVPRAudio wrong!"
            com.unisound.common.r.c(r4, r2)     // Catch: java.lang.Throwable -> L90
            r5.printStackTrace()     // Catch: java.lang.Throwable -> L90
            r4 = 0
            if (r0 == 0) goto L7e
            r0.disconnect()
        L7e:
            if (r3 == 0) goto L86
            r3.close()     // Catch: java.io.IOException -> L84
            goto L86
        L84:
            r5 = move-exception
            goto L8c
        L86:
            if (r1 == 0) goto L8f
            r1.close()     // Catch: java.io.IOException -> L84
            goto L8f
        L8c:
            r5.printStackTrace()
        L8f:
            return r4
        L90:
            r5 = move-exception
        L91:
            if (r0 == 0) goto L96
            r0.disconnect()
        L96:
            if (r3 == 0) goto L9e
            r3.close()     // Catch: java.io.IOException -> L9c
            goto L9e
        L9c:
            r4 = move-exception
            goto La4
        L9e:
            if (r1 == 0) goto La7
            r1.close()     // Catch: java.io.IOException -> L9c
            goto La7
        La4:
            r4.printStackTrace()
        La7:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.unisound.common.ar.a(java.lang.String, java.lang.String):boolean");
    }

    public static byte[] a(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[1024];
        while (true) {
            int i = inputStream.read(bArr);
            if (i == -1) {
                inputStream.close();
                return byteArrayOutputStream.toByteArray();
            }
            byteArrayOutputStream.write(bArr, 0, i);
        }
    }
}
