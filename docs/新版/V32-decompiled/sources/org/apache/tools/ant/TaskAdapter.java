package org.apache.tools.ant;

import java.lang.reflect.Method;
import org.apache.tools.ant.dispatch.DispatchUtils;
import org.apache.tools.ant.dispatch.Dispatchable;

/* JADX INFO: loaded from: classes3.dex */
public class TaskAdapter extends Task implements TypeAdapter {
    private Object proxy;

    public TaskAdapter() {
    }

    public TaskAdapter(Object obj) {
        this();
        setProxy(obj);
    }

    public static void checkTaskClass(Class<?> cls, Project project) {
        if (Dispatchable.class.isAssignableFrom(cls)) {
            return;
        }
        try {
            Method method = cls.getMethod("execute", (Class[]) null);
            if (Void.TYPE.equals(method.getReturnType())) {
                return;
            }
            project.log("return type of execute() should be void but was \"" + method.getReturnType() + "\" in " + cls, 1);
        } catch (LinkageError e) {
            String str = "Could not load " + cls + ": " + e;
            project.log(str, 0);
            throw new BuildException(str, e);
        } catch (NoSuchMethodException unused) {
            String str2 = "No public execute() in " + cls;
            project.log(str2, 0);
            throw new BuildException(str2);
        }
    }

    @Override // org.apache.tools.ant.TypeAdapter
    public void checkProxyClass(Class<?> cls) {
        checkTaskClass(cls, getProject());
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        try {
            Method method = this.proxy.getClass().getMethod("setLocation", Location.class);
            if (method != null) {
                method.invoke(this.proxy, getLocation());
            }
        } catch (NoSuchMethodException unused) {
        } catch (Exception e) {
            log("Error setting location in " + this.proxy.getClass(), 0);
            throw new BuildException(e);
        }
        try {
            Method method2 = this.proxy.getClass().getMethod("setProject", Project.class);
            if (method2 != null) {
                method2.invoke(this.proxy, getProject());
            }
        } catch (NoSuchMethodException unused2) {
        } catch (Exception e2) {
            log("Error setting project in " + this.proxy.getClass(), 0);
            throw new BuildException(e2);
        }
        try {
            DispatchUtils.execute(this.proxy);
        } catch (BuildException e3) {
            throw e3;
        } catch (Exception e4) {
            log("Error in " + this.proxy.getClass(), 3);
            throw new BuildException(e4);
        }
    }

    @Override // org.apache.tools.ant.TypeAdapter
    public void setProxy(Object obj) {
        this.proxy = obj;
    }

    @Override // org.apache.tools.ant.TypeAdapter
    public Object getProxy() {
        return this.proxy;
    }
}
