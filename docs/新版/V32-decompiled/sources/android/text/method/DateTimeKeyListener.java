package android.text.method;

import android.text.format.DateFormat;

/* JADX INFO: loaded from: classes.dex */
public class DateTimeKeyListener extends NumberKeyListener {
    public static final char[] CHARACTERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', DateFormat.AM_PM, DateFormat.MINUTE, 'p', ':', '/', '-', ' '};
    private static DateTimeKeyListener sInstance;

    @Override // android.text.method.KeyListener
    public int getInputType() {
        return 4;
    }

    @Override // android.text.method.NumberKeyListener
    protected char[] getAcceptedChars() {
        return CHARACTERS;
    }

    public static DateTimeKeyListener getInstance() {
        DateTimeKeyListener dateTimeKeyListener = sInstance;
        if (dateTimeKeyListener != null) {
            return dateTimeKeyListener;
        }
        DateTimeKeyListener dateTimeKeyListener2 = new DateTimeKeyListener();
        sInstance = dateTimeKeyListener2;
        return dateTimeKeyListener2;
    }
}
