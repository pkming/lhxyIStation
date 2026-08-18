package org.apache.tools.ant.taskdefs.optional.javah;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.launch.Locator;
import org.apache.tools.ant.taskdefs.Definer;
import org.apache.tools.ant.taskdefs.ExecuteJava;
import org.apache.tools.ant.taskdefs.optional.Javah;
import org.apache.tools.ant.taskdefs.optional.clearcase.CCRmtype;
import org.apache.tools.ant.taskdefs.optional.sos.SOSCmd;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;

/* JADX INFO: loaded from: classes3.dex */
public class SunJavah implements JavahAdapter {
    public static final String IMPLEMENTATION_NAME = "sun";

    @Override // org.apache.tools.ant.taskdefs.optional.javah.JavahAdapter
    public boolean compile(Javah javah) throws BuildException {
        Class<?> cls;
        Commandline commandline = setupJavahCommand(javah);
        ExecuteJava executeJava = new ExecuteJava();
        try {
            try {
                cls = Class.forName("com.sun.tools.javah.oldjavah.Main");
            } catch (ClassNotFoundException e) {
                throw new BuildException("Can't load javah", e, javah.getLocation());
            }
        } catch (ClassNotFoundException unused) {
            cls = Class.forName("com.sun.tools.javah.Main");
        }
        commandline.setExecutable(cls.getName());
        executeJava.setJavaCommand(commandline);
        File classSource = Locator.getClassSource(cls);
        if (classSource != null) {
            executeJava.setClasspath(new Path(javah.getProject(), classSource.getPath()));
        }
        return executeJava.fork(javah) == 0;
    }

    private Commandline setupJavahCommand(Javah javah) {
        Commandline commandline = new Commandline();
        if (javah.getDestdir() != null) {
            commandline.createArgument().setValue("-d");
            commandline.createArgument().setFile(javah.getDestdir());
        }
        if (javah.getOutputfile() != null) {
            commandline.createArgument().setValue("-o");
            commandline.createArgument().setFile(javah.getOutputfile());
        }
        if (javah.getClasspath() != null) {
            commandline.createArgument().setValue("-classpath");
            commandline.createArgument().setPath(javah.getClasspath());
        }
        if (javah.getVerbose()) {
            commandline.createArgument().setValue(SOSCmd.FLAG_VERBOSE);
        }
        if (javah.getOld()) {
            commandline.createArgument().setValue("-old");
        }
        if (javah.getForce()) {
            commandline.createArgument().setValue(CCRmtype.FLAG_FORCE);
        }
        if (javah.getStubs() && !javah.getOld()) {
            throw new BuildException("stubs only available in old mode.", javah.getLocation());
        }
        if (javah.getStubs()) {
            commandline.createArgument().setValue("-stubs");
        }
        Path path = new Path(javah.getProject());
        if (javah.getBootclasspath() != null) {
            path.append(javah.getBootclasspath());
        }
        Path pathConcatSystemBootClasspath = path.concatSystemBootClasspath(Definer.OnError.POLICY_IGNORE);
        if (pathConcatSystemBootClasspath.size() > 0) {
            commandline.createArgument().setValue("-bootclasspath");
            commandline.createArgument().setPath(pathConcatSystemBootClasspath);
        }
        commandline.addArguments(javah.getCurrentArgs());
        javah.logAndAddFiles(commandline);
        return commandline;
    }
}
