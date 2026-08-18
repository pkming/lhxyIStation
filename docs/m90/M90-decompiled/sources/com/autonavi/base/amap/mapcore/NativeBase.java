package com.autonavi.base.amap.mapcore;

import com.amap.api.col.p0003sl.dy;
import com.amap.api.col.p0003sl.dz;
import com.autonavi.base.amap.mapcore.annotations.ParameterIsClass;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public abstract class NativeBase {
    private static final String CREATE_OVERLAY = "createOverlay";
    private static final int LOG_SIZE = 10;
    private static final int STACK_NUMBER = 3;
    private static final String UPDATE_OPTIONS = "updateOptions";
    private boolean mCalledFuntion = false;
    protected boolean useRunnable = true;
    protected volatile boolean destroy = false;
    private int logSizeCount = 1;
    ArrayList<Method> methodMap = new ArrayList<>();
    ArrayList<NativeFunctionRunnable> runnableArrayList = new ArrayList<>();

    protected abstract void createNative();

    protected abstract void finalizeNative();

    protected abstract long getNative();

    protected void finalize() throws Throwable {
        super.finalize();
        try {
            finalizeNative();
        } catch (Throwable th) {
            Object[] objArr = new Object[3];
            getClass().getSimpleName();
            th.toString();
        }
    }

    protected boolean isReady() {
        return getNative() != 0;
    }

    public void validate() {
        ParameterIsClass parameterIsClass;
        Class<?>[] parameterTypes;
        java.lang.reflect.Method[] methods = getClass().getMethods();
        if (methods == null) {
            return;
        }
        for (java.lang.reflect.Method method : methods) {
            if (method.isAnnotationPresent(ParameterIsClass.class) && (parameterIsClass = (ParameterIsClass) method.getAnnotation(ParameterIsClass.class)) != null && parameterIsClass.required() && (parameterTypes = method.getParameterTypes()) != null) {
                for (Class<?> cls : parameterTypes) {
                    if (!(cls instanceof Object)) {
                        throw new RuntimeException("函数：" + method + " 参数不是对象类型");
                    }
                }
            }
        }
    }

    public synchronized void callAllFunction() {
        Class<?> cls;
        java.lang.reflect.Method declaredMethod;
        Runnable runnable;
        if (isReady() && !this.destroy) {
            if (this.mCalledFuntion) {
                return;
            }
            try {
                this.mCalledFuntion = true;
                if (this.useRunnable) {
                    while (this.runnableArrayList.size() > 0 && !this.destroy) {
                        NativeFunctionRunnable nativeFunctionRunnable = this.runnableArrayList.get(0);
                        if (nativeFunctionRunnable != null && (runnable = nativeFunctionRunnable.getRunnable()) != null) {
                            runnable.run();
                        }
                        this.runnableArrayList.remove(0);
                    }
                    this.logSizeCount = 1;
                    return;
                }
                for (Method method : this.methodMap) {
                    if (this.destroy) {
                        break;
                    }
                    if (method.object != null && (cls = method.object.getClass()) != null && (declaredMethod = cls.getDeclaredMethod(method.methodName, method.clazz)) != null) {
                        declaredMethod.setAccessible(true);
                        declaredMethod.invoke(method.object, method.param);
                    }
                }
                this.methodMap.clear();
            } catch (Throwable unused) {
            }
        }
    }

    public void destroy() {
        this.destroy = true;
        synchronized (this) {
            this.runnableArrayList.clear();
            this.methodMap.clear();
        }
    }

    public int findRunnableIndex(NativeFunctionRunnable nativeFunctionRunnable) {
        if (this.runnableArrayList == null || nativeFunctionRunnable == null) {
            return -1;
        }
        for (int i = 0; i < this.runnableArrayList.size(); i++) {
            NativeFunctionRunnable nativeFunctionRunnable2 = this.runnableArrayList.get(i);
            if (nativeFunctionRunnable2 != null && nativeFunctionRunnable2.getKey().equals(nativeFunctionRunnable.getKey())) {
                return i;
            }
        }
        return -1;
    }

    public synchronized void storeUncallFunction(Object obj, Object obj2, String str, String str2, Object... objArr) {
        try {
            if (this.useRunnable && obj2 != null) {
                NativeFunctionRunnable nativeFunctionRunnable = new NativeFunctionRunnable(str2, str, (Runnable) obj2);
                synchronized (this.runnableArrayList) {
                    int size = this.runnableArrayList.size();
                    if (size > this.logSizeCount * 10) {
                        dz.c(dy.d, "storeUncallFunction size = ".concat(String.valueOf(size)));
                        this.logSizeCount++;
                    }
                    int iFindRunnableIndex = findRunnableIndex(nativeFunctionRunnable);
                    if (iFindRunnableIndex >= 0) {
                        this.runnableArrayList.remove(iFindRunnableIndex);
                    }
                    this.runnableArrayList.add(nativeFunctionRunnable);
                }
            } else {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                if (stackTrace != null && stackTrace.length >= 3) {
                    this.methodMap.add(new Method(obj, stackTrace[3].getMethodName(), false, objArr));
                }
            }
            this.mCalledFuntion = false;
        } catch (Throwable unused) {
        }
    }

    public synchronized void storeUncallFunctionArray(Object obj, Object obj2, String[] strArr, String str, Object[] objArr) {
        try {
            if (this.useRunnable && obj2 != null) {
                StringBuilder sb = new StringBuilder("");
                for (String str2 : strArr) {
                    sb.append(str2).append(",");
                }
                NativeFunctionRunnable nativeFunctionRunnable = new NativeFunctionRunnable(str, sb.toString(), (Runnable) obj2);
                synchronized (this.runnableArrayList) {
                    int size = this.runnableArrayList.size();
                    if (size > this.logSizeCount * 10) {
                        dz.c(dy.d, "storeUncallFunctionArray size = ".concat(String.valueOf(size)));
                        this.logSizeCount++;
                    }
                    int iFindRunnableIndex = findRunnableIndex(nativeFunctionRunnable);
                    if (iFindRunnableIndex >= 0) {
                        this.runnableArrayList.remove(iFindRunnableIndex);
                    }
                    this.runnableArrayList.add(nativeFunctionRunnable);
                }
            } else {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                if (stackTrace != null && stackTrace.length >= 3) {
                    this.methodMap.add(new Method(obj, stackTrace[3].getMethodName(), false, objArr));
                }
            }
            this.mCalledFuntion = false;
        } catch (Throwable unused) {
        }
    }

    public static class Method {
        private boolean baseClass;
        private Class<?>[] clazz;
        private String methodName;
        private Object object;
        private Object[] param;

        public Method(Object obj, String str, boolean z, Object... objArr) {
            this.object = obj;
            this.methodName = str;
            this.baseClass = z;
            if (objArr != null) {
                try {
                    if (objArr.length > 0) {
                        this.clazz = new Class[objArr.length];
                        if (("createOverlay".equals(str) || "updateOptions".equals(str)) ? true : z) {
                            for (int i = 0; i < objArr.length; i++) {
                                if (i == 1) {
                                    this.clazz[i] = objArr[i].getClass().getSuperclass();
                                } else {
                                    this.clazz[i] = objArr[i].getClass();
                                }
                            }
                        } else {
                            for (int i2 = 0; i2 < objArr.length; i2++) {
                                this.clazz[i2] = objArr[i2].getClass();
                            }
                        }
                        this.param = new Object[objArr.length];
                        for (int i3 = 0; i3 < objArr.length; i3++) {
                            this.param[i3] = objArr[i3];
                        }
                    }
                } catch (Throwable unused) {
                }
            }
        }
    }
}
