package com.wuxiaolong.androidutils.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

/* JADX INFO: loaded from: classes2.dex */
public class AppUtils {
    public static void hideSoftInput(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 2);
        }
    }

    public static String decimalFormat(double d, String str) {
        return new DecimalFormat(str).format(d);
    }

    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static String md5(String str) {
        String lowerCase = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str.getBytes());
            byte[] bArrDigest = messageDigest.digest();
            StringBuffer stringBuffer = new StringBuffer("");
            for (int i = 0; i < bArrDigest.length; i++) {
                int i2 = bArrDigest[i];
                if (i2 < 0) {
                    i2 += 256;
                }
                if (i2 < 16) {
                    stringBuffer.append("0");
                }
                stringBuffer.append(Integer.toHexString(i2));
            }
            lowerCase = stringBuffer.toString().toLowerCase();
            stringBuffer.toString().substring(8, 24);
            return lowerCase;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return lowerCase;
        }
    }

    public static void installAPK(Context context, String str) {
        Intent intent = new Intent();
        intent.addFlags(268435456);
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(new File(str)), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void actionCall(Context context, String str) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(WebView.SCHEME_TEL + str));
        intent.setAction(Intent.ACTION_CALL);
        context.startActivity(intent);
    }

    public static void actionDial(Context context, String str) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(WebView.SCHEME_TEL + str));
        intent.setAction(Intent.ACTION_DIAL);
        context.startActivity(intent);
    }
}
