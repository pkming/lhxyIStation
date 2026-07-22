package android.debug;

/* JADX INFO: loaded from: classes.dex */
public class JNITest {
    private native int part1(int i, double d, String str, int[] iArr);

    private static native int part3(String str);

    public int test(int i, double d, String str) {
        return part1(i, d, str, new int[]{42, 53, 65, 127});
    }

    private int part2(double d, int i, String str) {
        System.out.println(str + " : " + ((float) d) + " : " + i);
        return part3(str) + 6;
    }
}
