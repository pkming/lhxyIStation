package android.hardware.camera2.utils;

/* JADX INFO: loaded from: classes.dex */
public class UncheckedThrow {
    public static void throwAnyException(Exception exc) throws Exception {
        throwAnyImpl(exc);
    }

    private static <T extends Exception> void throwAnyImpl(Exception exc) throws Exception {
        throw exc;
    }
}
