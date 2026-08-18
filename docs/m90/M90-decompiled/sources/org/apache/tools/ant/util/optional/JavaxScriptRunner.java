package org.apache.tools.ant.util.optional;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.ReflectWrapper;
import org.apache.tools.ant.util.ScriptRunnerBase;

/* JADX INFO: loaded from: classes3.dex */
public class JavaxScriptRunner extends ScriptRunnerBase {
    private ReflectWrapper engine;

    @Override // org.apache.tools.ant.util.ScriptRunnerBase
    public String getManagerName() {
        return "javax";
    }

    @Override // org.apache.tools.ant.util.ScriptRunnerBase
    public boolean supportsLanguage() {
        if (this.engine != null) {
            return true;
        }
        checkLanguage();
        ClassLoader classLoaderReplaceContextLoader = replaceContextLoader();
        try {
            return createEngine() != null;
        } catch (Exception unused) {
            return false;
        } finally {
            restoreContextLoader(classLoaderReplaceContextLoader);
        }
    }

    @Override // org.apache.tools.ant.util.ScriptRunnerBase
    public void executeScript(String str) throws BuildException {
        evaluateScript(str);
    }

    @Override // org.apache.tools.ant.util.ScriptRunnerBase
    public Object evaluateScript(String str) throws BuildException {
        checkLanguage();
        ClassLoader classLoaderReplaceContextLoader = replaceContextLoader();
        try {
            try {
                ReflectWrapper reflectWrapperCreateEngine = createEngine();
                if (reflectWrapperCreateEngine == null) {
                    throw new BuildException("Unable to create javax script engine for " + getLanguage());
                }
                for (String str2 : getBeans().keySet()) {
                    Object obj = getBeans().get(str2);
                    if ("FX".equalsIgnoreCase(getLanguage())) {
                        reflectWrapperCreateEngine.invoke("put", String.class, str2 + ":" + obj.getClass().getName(), Object.class, obj);
                    } else {
                        reflectWrapperCreateEngine.invoke("put", String.class, str2, Object.class, obj);
                    }
                }
                return reflectWrapperCreateEngine.invoke("eval", String.class, getScript());
            } catch (BuildException e) {
                throw unwrap(e);
            } catch (Exception e2) {
                e = e2;
                Throwable cause = e.getCause();
                if (cause != null) {
                    if (cause instanceof BuildException) {
                        throw ((BuildException) cause);
                    }
                    e = cause;
                }
                throw new BuildException(e);
            }
        } finally {
            restoreContextLoader(classLoaderReplaceContextLoader);
        }
    }

    private ReflectWrapper createEngine() throws Exception {
        ReflectWrapper reflectWrapper = this.engine;
        if (reflectWrapper != null) {
            return reflectWrapper;
        }
        Object objInvoke = new ReflectWrapper(getClass().getClassLoader(), "javax.script.ScriptEngineManager").invoke("getEngineByName", String.class, getLanguage());
        if (objInvoke == null) {
            return null;
        }
        ReflectWrapper reflectWrapper2 = new ReflectWrapper(objInvoke);
        if (getKeepEngine()) {
            this.engine = reflectWrapper2;
        }
        return reflectWrapper2;
    }

    private static BuildException unwrap(Throwable th) {
        BuildException buildException = th instanceof BuildException ? (BuildException) th : null;
        while (th.getCause() != null) {
            th = th.getCause();
            if (th instanceof BuildException) {
                buildException = (BuildException) th;
            }
        }
        return buildException;
    }
}
