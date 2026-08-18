package android.os;

/* JADX INFO: loaded from: classes.dex */
public final class FactoryTest {
    public static boolean isLongPressOnPowerOffEnabled() {
        return SystemProperties.getInt("factory.long_press_power_off", 0) != 0;
    }
}
