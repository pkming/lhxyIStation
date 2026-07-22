package org.apache.tools.ant.taskdefs.optional.javah;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Definer;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.optional.Javah;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.JavaEnvUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Kaffeh implements JavahAdapter {
    public static final String IMPLEMENTATION_NAME = "kaffeh";

    @Override // org.apache.tools.ant.taskdefs.optional.javah.JavahAdapter
    public boolean compile(Javah javah) throws BuildException {
        try {
            Execute.runCommand(javah, setupKaffehCommand(javah).getCommandline());
            return true;
        } catch (BuildException e) {
            if (e.getMessage().indexOf("failed with return code") != -1) {
                return false;
            }
            throw e;
        }
    }

    private Commandline setupKaffehCommand(Javah javah) {
        Commandline commandline = new Commandline();
        commandline.setExecutable(JavaEnvUtils.getJdkExecutable(IMPLEMENTATION_NAME));
        if (javah.getDestdir() != null) {
            commandline.createArgument().setValue("-d");
            commandline.createArgument().setFile(javah.getDestdir());
        }
        if (javah.getOutputfile() != null) {
            commandline.createArgument().setValue("-o");
            commandline.createArgument().setFile(javah.getOutputfile());
        }
        Path path = new Path(javah.getProject());
        if (javah.getBootclasspath() != null) {
            path.append(javah.getBootclasspath());
        }
        Path pathConcatSystemBootClasspath = path.concatSystemBootClasspath(Definer.OnError.POLICY_IGNORE);
        if (javah.getClasspath() != null) {
            pathConcatSystemBootClasspath.append(javah.getClasspath());
        }
        if (pathConcatSystemBootClasspath.size() > 0) {
            commandline.createArgument().setValue("-classpath");
            commandline.createArgument().setPath(pathConcatSystemBootClasspath);
        }
        if (!javah.getOld()) {
            commandline.createArgument().setValue("-jni");
        }
        commandline.addArguments(javah.getCurrentArgs());
        javah.logAndAddFiles(commandline);
        return commandline;
    }
}
