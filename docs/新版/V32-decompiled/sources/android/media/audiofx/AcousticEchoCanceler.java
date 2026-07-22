package android.media.audiofx;

import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class AcousticEchoCanceler extends AudioEffect {
    private static final String TAG = "AcousticEchoCanceler";

    public static boolean isAvailable() {
        return AudioEffect.isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_AEC);
    }

    public static AcousticEchoCanceler create(int i) {
        try {
            try {
                try {
                    return new AcousticEchoCanceler(i);
                } catch (RuntimeException unused) {
                    Log.w(TAG, "not enough memory");
                    return null;
                }
            } catch (IllegalArgumentException unused2) {
                Log.w(TAG, "not implemented on this device" + ((Object) null));
                return null;
            } catch (UnsupportedOperationException unused3) {
                Log.w(TAG, "not enough resources");
                return null;
            }
        } catch (Throwable unused4) {
            return null;
        }
    }

    private AcousticEchoCanceler(int i) throws RuntimeException {
        super(EFFECT_TYPE_AEC, EFFECT_TYPE_NULL, 0, i);
    }
}
