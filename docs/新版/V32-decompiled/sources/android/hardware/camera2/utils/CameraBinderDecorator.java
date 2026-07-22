package android.hardware.camera2.utils;

import android.hardware.camera2.utils.Decorator;
import android.os.DeadObjectException;
import android.os.RemoteException;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class CameraBinderDecorator {
    public static final int ALREADY_EXISTS = -17;
    public static final int BAD_VALUE = -22;
    public static final int DEAD_OBJECT = -32;
    public static final int EACCES = -13;
    public static final int EBUSY = -16;
    public static final int ENODEV = -19;
    public static final int EOPNOTSUPP = -95;
    public static final int EUSERS = -87;
    public static final int NO_ERROR = 0;
    public static final int PERMISSION_DENIED = -1;

    private static class CameraBinderDecoratorListener implements Decorator.DecoratorListener {
        @Override // android.hardware.camera2.utils.Decorator.DecoratorListener
        public void onBeforeInvocation(Method method, Object[] objArr) {
        }

        @Override // android.hardware.camera2.utils.Decorator.DecoratorListener
        public void onFinally(Method method, Object[] objArr) {
        }

        private CameraBinderDecoratorListener() {
        }

        @Override // android.hardware.camera2.utils.Decorator.DecoratorListener
        public void onAfterInvocation(Method method, Object[] objArr, Object obj) throws Exception {
            if (method.getReturnType() == Integer.TYPE) {
                int iIntValue = ((Integer) obj).intValue();
                if (iIntValue == -95) {
                    UncheckedThrow.throwAnyException(new CameraRuntimeException(1000));
                } else if (iIntValue != -87) {
                    if (iIntValue == -32) {
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(2));
                    } else {
                        if (iIntValue == -22) {
                            throw new IllegalArgumentException("Bad argument passed to camera service");
                        }
                        if (iIntValue != -19) {
                            if (iIntValue != -13) {
                                if (iIntValue == -17) {
                                    return;
                                }
                                if (iIntValue != -16) {
                                    if (iIntValue == -1) {
                                        throw new SecurityException("Lacking privileges to access camera service");
                                    }
                                    if (iIntValue == 0) {
                                        return;
                                    }
                                }
                            }
                            UncheckedThrow.throwAnyException(new CameraRuntimeException(4));
                            UncheckedThrow.throwAnyException(new CameraRuntimeException(5));
                        }
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(2));
                        UncheckedThrow.throwAnyException(new CameraRuntimeException(1000));
                    }
                    UncheckedThrow.throwAnyException(new CameraRuntimeException(1));
                    UncheckedThrow.throwAnyException(new CameraRuntimeException(4));
                    UncheckedThrow.throwAnyException(new CameraRuntimeException(5));
                    UncheckedThrow.throwAnyException(new CameraRuntimeException(2));
                    UncheckedThrow.throwAnyException(new CameraRuntimeException(1000));
                } else {
                    UncheckedThrow.throwAnyException(new CameraRuntimeException(5));
                    UncheckedThrow.throwAnyException(new CameraRuntimeException(2));
                    UncheckedThrow.throwAnyException(new CameraRuntimeException(1000));
                }
                if (iIntValue < 0) {
                    throw new UnsupportedOperationException(String.format("Unknown error %d", Integer.valueOf(iIntValue)));
                }
            }
        }

        @Override // android.hardware.camera2.utils.Decorator.DecoratorListener
        public boolean onCatchException(Method method, Object[] objArr, Throwable th) throws Exception {
            if (th instanceof DeadObjectException) {
                UncheckedThrow.throwAnyException(new CameraRuntimeException(2, "Process hosting the camera service has died unexpectedly", th));
                return false;
            }
            if (th instanceof RemoteException) {
                throw new UnsupportedOperationException("An unknown RemoteException was thrown which should never happen.", th);
            }
            return false;
        }
    }

    public static <T> T newInstance(T t) {
        return (T) Decorator.newInstance(t, new CameraBinderDecoratorListener());
    }
}
