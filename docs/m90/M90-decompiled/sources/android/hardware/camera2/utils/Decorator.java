package android.hardware.camera2.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/* JADX INFO: loaded from: classes.dex */
public class Decorator<T> implements InvocationHandler {
    private final DecoratorListener mListener;
    private final T mObject;

    public interface DecoratorListener {
        void onAfterInvocation(Method method, Object[] objArr, Object obj);

        void onBeforeInvocation(Method method, Object[] objArr);

        boolean onCatchException(Method method, Object[] objArr, Throwable th);

        void onFinally(Method method, Object[] objArr);
    }

    public static <T> T newInstance(T t, DecoratorListener decoratorListener) {
        return (T) Proxy.newProxyInstance(t.getClass().getClassLoader(), t.getClass().getInterfaces(), new Decorator(t, decoratorListener));
    }

    private Decorator(T t, DecoratorListener decoratorListener) {
        this.mObject = t;
        this.mListener = decoratorListener;
    }

    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
        Object objInvoke = null;
        try {
            try {
                this.mListener.onBeforeInvocation(method, objArr);
                objInvoke = method.invoke(this.mObject, objArr);
                this.mListener.onAfterInvocation(method, objArr, objInvoke);
            } catch (InvocationTargetException e) {
                Throwable targetException = e.getTargetException();
                if (!this.mListener.onCatchException(method, objArr, targetException)) {
                    throw targetException;
                }
            }
            return objInvoke;
        } finally {
            this.mListener.onFinally(method, objArr);
        }
    }
}
