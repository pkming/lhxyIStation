package android.media.audiofx;

import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class AutomaticGainControl extends AudioEffect {
    private static final String TAG = "AutomaticGainControl";

    public static boolean isAvailable() {
        return AudioEffect.isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_AGC);
    }

    public static AutomaticGainControl create(int i) {
        try {
            try {
                try {
                    return new AutomaticGainControl(i);
                } catch (RuntimeException unused) {
                    Log.w(TAG, "not enough memory");
                    return null;
                }
            } catch (IllegalArgumentException unused2) {
                Log.w(TAG, "not implemented on this device " + ((Object) null));
                return null;
            } catch (UnsupportedOperationException unused3) {
                Log.w(TAG, "not enough resources");
                return null;
            }
        } catch (Throwable unused4) {
            return null;
        }
    }

    private AutomaticGainControl(int i) throws RuntimeException {
        super(EFFECT_TYPE_AGC, EFFECT_TYPE_NULL, 0, i);
    }
}
