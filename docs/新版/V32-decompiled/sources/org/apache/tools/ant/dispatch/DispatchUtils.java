package org.apache.tools.ant.dispatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.UnknownElement;

/* JADX INFO: loaded from: classes3.dex */
public class DispatchUtils {
    public static final void execute(Object obj) throws BuildException {
        Dispatchable dispatchable;
        Object realThing;
        try {
            try {
                String str = null;
                if (obj instanceof Dispatchable) {
                    dispatchable = (Dispatchable) obj;
                } else {
                    dispatchable = ((obj instanceof UnknownElement) && (realThing = ((UnknownElement) obj).getRealThing()) != null && (realThing instanceof Dispatchable) && (realThing instanceof Task)) ? (Dispatchable) realThing : null;
                }
                if (dispatchable != null) {
                    try {
                        String actionParameterName = dispatchable.getActionParameterName();
                        if (actionParameterName != null && actionParameterName.trim().length() > 0) {
                            String str2 = "get" + actionParameterName.trim().substring(0, 1).toUpperCase();
                            try {
                                if (actionParameterName.length() > 1) {
                                    str2 = str2 + actionParameterName.substring(1);
                                }
                                Method method = dispatchable.getClass().getMethod(str2, new Class[0]);
                                if (method != null) {
                                    Object objInvoke = method.invoke(dispatchable, (Object[]) null);
                                    if (objInvoke != null) {
                                        String string = objInvoke.toString();
                                        if (string != null && string.trim().length() > 0) {
                                            String strTrim = string.trim();
                                            Method method2 = dispatchable.getClass().getMethod(strTrim, new Class[0]);
                                            if (method2 == null) {
                                                throw new BuildException("No public " + strTrim + "() in " + dispatchable.getClass());
                                            }
                                            method2.invoke(dispatchable, (Object[]) null);
                                            if (obj instanceof UnknownElement) {
                                                ((UnknownElement) obj).setRealThing(null);
                                                return;
                                            }
                                            return;
                                        }
                                        throw new BuildException("Dispatchable Task attribute '" + actionParameterName.trim() + "' not set or value is empty.");
                                    }
                                    throw new BuildException("Dispatchable Task attribute '" + actionParameterName.trim() + "' not set or value is empty.");
                                }
                                return;
                            } catch (NoSuchMethodException unused) {
                                str = str2;
                                throw new BuildException("No public " + str + "() in " + obj.getClass());
                            }
                        }
                        throw new BuildException("Action Parameter Name must not be empty for Dispatchable Task.");
                    } catch (NoSuchMethodException unused2) {
                    }
                } else {
                    Method method3 = obj.getClass().getMethod("execute", new Class[0]);
                    if (method3 == null) {
                        throw new BuildException("No public execute() in " + obj.getClass());
                    }
                    method3.invoke(obj, (Object[]) null);
                    if (obj instanceof UnknownElement) {
                        ((UnknownElement) obj).setRealThing(null);
                    }
                }
            } catch (NoSuchMethodException e) {
                throw new BuildException(e);
            }
        } catch (IllegalAccessException e2) {
            throw new BuildException(e2);
        } catch (InvocationTargetException e3) {
            Throwable targetException = e3.getTargetException();
            if (targetException instanceof BuildException) {
                throw ((BuildException) targetException);
            }
            throw new BuildException(targetException);
        }
    }
}
