package org.apache.tools.ant.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public class ReflectUtil {
    private ReflectUtil() {
    }

    public static <T> T newInstance(Class<T> cls, Class<?>[] clsArr, Object[] objArr) {
        try {
            return cls.getConstructor(clsArr).newInstance(objArr);
        } catch (Exception e) {
            throwBuildException(e);
            return null;
        }
    }

    public static Object invoke(Object obj, String str) {
        try {
            return obj.getClass().getMethod(str, (Class[]) null).invoke(obj, (Object[]) null);
        } catch (Exception e) {
            throwBuildException(e);
            return null;
        }
    }

    public static Object invokeStatic(Object obj, String str) {
        try {
            return ((Class) obj).getMethod(str, (Class[]) null).invoke(obj, (Object[]) null);
        } catch (Exception e) {
            throwBuildException(e);
            return null;
        }
    }

    public static Object invoke(Object obj, String str, Class<?> cls, Object obj2) {
        try {
            return obj.getClass().getMethod(str, cls).invoke(obj, obj2);
        } catch (Exception e) {
            throwBuildException(e);
            return null;
        }
    }

    public static Object invoke(Object obj, String str, Class<?> cls, Object obj2, Class<?> cls2, Object obj3) {
        try {
            return obj.getClass().getMethod(str, cls, cls2).invoke(obj, obj2, obj3);
        } catch (Exception e) {
            throwBuildException(e);
            return null;
        }
    }

    public static Object getField(Object obj, String str) throws BuildException {
        try {
            Field declaredField = obj.getClass().getDeclaredField(str);
            declaredField.setAccessible(true);
            return declaredField.get(obj);
        } catch (Exception e) {
            throwBuildException(e);
            return null;
        }
    }

    public static void throwBuildException(Exception exc) throws BuildException {
        throw toBuildException(exc);
    }

    public static BuildException toBuildException(Exception exc) {
        if (exc instanceof InvocationTargetException) {
            Throwable targetException = ((InvocationTargetException) exc).getTargetException();
            if (targetException instanceof BuildException) {
                return (BuildException) targetException;
            }
            return new BuildException(targetException);
        }
        return new BuildException(exc);
    }

    public static boolean respondsTo(Object obj, String str) throws BuildException {
        try {
            for (Method method : obj.getClass().getMethods()) {
                if (method.getName().equals(str)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw toBuildException(e);
        }
    }
}
