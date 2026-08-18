package org.apache.tools.ant.taskdefs.compilers;

import java.io.OutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JavaEnvUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Javac12 extends DefaultCompilerAdapter {
    protected static final String CLASSIC_COMPILER_CLASSNAME = "sun.tools.javac.Main";

    @Override // org.apache.tools.ant.taskdefs.compilers.CompilerAdapter
    public boolean execute() throws BuildException {
        this.attributes.log("Using classic compiler", 3);
        Commandline commandline = setupJavacCommand(true);
        LogOutputStream logOutputStream = new LogOutputStream((Task) this.attributes, 1);
        try {
            try {
                Class<?> cls = Class.forName(CLASSIC_COMPILER_CLASSNAME);
                return ((Boolean) cls.getMethod("compile", String[].class).invoke(cls.getConstructor(OutputStream.class, String.class).newInstance(logOutputStream, "javac"), commandline.getArguments())).booleanValue();
            } catch (ClassNotFoundException unused) {
                throw new BuildException("Cannot use classic compiler , as it is not available. \n A common solution is to set the environment variable JAVA_HOME to your jdk directory.\nIt is currently set to \"" + JavaEnvUtils.getJavaHome() + "\"", this.location);
            } catch (Exception e) {
                if (e instanceof BuildException) {
                    throw ((BuildException) e);
                }
                throw new BuildException("Error starting classic compiler: ", e, this.location);
            }
        } finally {
            FileUtils.close(logOutputStream);
        }
    }
}
