package com.unisound.common;

import android.text.TextUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* JADX INFO: loaded from: classes2.dex */
public class t {
    public static String a(File file) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] bArr = new byte[8192];
                while (true) {
                    try {
                        try {
                            int i = fileInputStream.read(bArr);
                            if (i <= 0) {
                                break;
                            }
                            messageDigest.update(bArr, 0, i);
                        } catch (IOException e) {
                            throw new RuntimeException("Unable to process file for MD5", e);
                        }
                    } finally {
                        try {
                            fileInputStream.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                            r.e("MD5 checkMD5: Exception on closing MD5 input stream: " + e2);
                        }
                    }
                }
                return String.format("%32s", new BigInteger(1, messageDigest.digest()).toString(16)).replace(' ', '0');
            } catch (FileNotFoundException e3) {
                r.e("MD5 checkMD5: Exception while getting FileInputStream: " + e3);
                e3.printStackTrace();
                return null;
            }
        } catch (NoSuchAlgorithmException e4) {
            r.e("MD5 checkMD5: Exception while getting Digest: " + e4);
            e4.printStackTrace();
            return null;
        }
    }

    public static String a(String str) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            messageDigest = null;
        }
        byte[] bArrDigest = messageDigest.digest(str.getBytes());
        StringBuilder sb = new StringBuilder(40);
        for (byte b : bArrDigest) {
            int i = b & 255;
            if ((i >> 4) == 0) {
                sb.append("0").append(Integer.toHexString(i));
            } else {
                sb.append(Integer.toHexString(i));
            }
        }
        return sb.toString();
    }

    public static boolean a(String str, File file) {
        String str2;
        if (TextUtils.isEmpty(str) || file == null) {
            str2 = "MD5 checkMD5: md5 String NULL or File NULL";
        } else {
            String strA = a(file);
            if (strA != null) {
                r.b("MD5 checkMD5: Calculated digest: " + strA);
                r.b("MD5 checkMD5: Provided digest: " + str);
                return strA.equalsIgnoreCase(str);
            }
            str2 = "MD5 checkMD5: calculatedDigest NULL";
        }
        r.e(str2);
        return false;
    }
}
