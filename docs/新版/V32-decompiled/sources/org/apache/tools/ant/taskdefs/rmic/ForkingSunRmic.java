package org.apache.tools.ant.taskdefs.rmic;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.taskdefs.Rmic;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.JavaEnvUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ForkingSunRmic extends DefaultRmicAdapter {
    public static final String COMPILER_NAME = "forking";

    protected String getExecutableName() {
        return SunRmic.RMIC_EXECUTABLE;
    }

    @Override // org.apache.tools.ant.taskdefs.rmic.RmicAdapter
    public boolean execute() throws BuildException {
        Rmic rmic = getRmic();
        Commandline commandline = setupRmicCommand();
        Project project = rmic.getProject();
        String executable = rmic.getExecutable();
        if (executable == null) {
            executable = JavaEnvUtils.getJdkExecutable(getExecutableName());
        }
        commandline.setExecutable(executable);
        String[] commandline2 = commandline.getCommandline();
        try {
            Execute execute = new Execute(new LogStreamHandler((Task) rmic, 2, 1));
            execute.setAntRun(project);
            execute.setWorkingDirectory(project.getBaseDir());
            execute.setCommandline(commandline2);
            execute.execute();
            return !execute.isFailure();
        } catch (IOException e) {
            throw new BuildException("Error running " + getExecutableName() + " -maybe it is not on the path", e);
        }
    }
}
