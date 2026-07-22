package android.text.method;

/* JADX INFO: loaded from: classes.dex */
public class DateKeyListener extends NumberKeyListener {
    public static final char[] CHARACTERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '/', '-', '.'};
    private static DateKeyListener sInstance;

    @Override // android.text.method.KeyListener
    public int getInputType() {
        return 20;
    }

    @Override // android.text.method.NumberKeyListener
    protected char[] getAcceptedChars() {
        return CHARACTERS;
    }

    public static DateKeyListener getInstance() {
        DateKeyListener dateKeyListener = sInstance;
        if (dateKeyListener != null) {
            return dateKeyListener;
        }
        DateKeyListener dateKeyListener2 = new DateKeyListener();
        sInstance = dateKeyListener2;
        return dateKeyListener2;
    }
}
