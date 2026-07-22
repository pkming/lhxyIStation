package android.os;

/* JADX INFO: loaded from: classes.dex */
public class NullVibrator extends Vibrator {
    private static final NullVibrator sInstance = new NullVibrator();

    @Override // android.os.Vibrator
    public void cancel() {
    }

    @Override // android.os.Vibrator
    public boolean hasVibrator() {
        return false;
    }

    @Override // android.os.Vibrator
    public void vibrate(long j) {
    }

    private NullVibrator() {
    }

    public static NullVibrator getInstance() {
        return sInstance;
    }

    @Override // android.os.Vibrator
    public void vibrate(long[] jArr, int i) {
        if (i >= jArr.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    @Override // android.os.Vibrator
    public void vibrate(int i, String str, long j) {
        vibrate(j);
    }

    @Override // android.os.Vibrator
    public void vibrate(int i, String str, long[] jArr, int i2) {
        vibrate(jArr, i2);
    }
}
