package org.apache.tools.ant.taskdefs.rmic;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.types.Commandline;

/* JADX INFO: loaded from: classes3.dex */
public class SunRmic extends DefaultRmicAdapter {
    public static final String COMPILER_NAME = "sun";
    public static final String ERROR_NO_RMIC_ON_CLASSPATH = "Cannot use SUN rmic, as it is not available.  A common solution is to set the environment variable JAVA_HOME";
    public static final String ERROR_RMIC_FAILED = "Error starting SUN rmic: ";
    public static final String RMIC_CLASSNAME = "sun.rmi.rmic.Main";
    public static final String RMIC_EXECUTABLE = "rmic";

    @Override // org.apache.tools.ant.taskdefs.rmic.RmicAdapter
    public boolean execute() throws BuildException {
        getRmic().log("Using SUN rmic compiler", 3);
        Commandline commandline = setupRmicCommand();
        LogOutputStream logOutputStream = new LogOutputStream((Task) getRmic(), 1);
        try {
            try {
                try {
                    Class<?> cls = Class.forName(RMIC_CLASSNAME);
                    boolean zBooleanValue = ((Boolean) cls.getMethod("compile", String[].class).invoke(cls.getConstructor(OutputStream.class, String.class).newInstance(logOutputStream, RMIC_EXECUTABLE), commandline.getArguments())).booleanValue();
                    try {
                        logOutputStream.close();
                        return zBooleanValue;
                    } catch (IOException e) {
                        throw new BuildException(e);
                    }
                } catch (Throwable th) {
                    try {
                        logOutputStream.close();
                        throw th;
                    } catch (IOException e2) {
                        throw new BuildException(e2);
                    }
                }
            } catch (Exception e3) {
                if (e3 instanceof BuildException) {
                    throw ((BuildException) e3);
                }
                throw new BuildException(ERROR_RMIC_FAILED, e3, getRmic().getLocation());
            }
        } catch (ClassNotFoundException unused) {
            throw new BuildException(ERROR_NO_RMIC_ON_CLASSPATH, getRmic().getLocation());
        }
    }

    @Override // org.apache.tools.ant.taskdefs.rmic.DefaultRmicAdapter
    protected String[] preprocessCompilerArgs(String[] strArr) {
        return filterJvmCompilerArgs(strArr);
    }
}
