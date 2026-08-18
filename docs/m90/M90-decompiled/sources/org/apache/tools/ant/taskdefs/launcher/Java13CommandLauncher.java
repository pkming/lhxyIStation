package org.apache.tools.ant.taskdefs.launcher;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline;

/* JADX INFO: loaded from: classes3.dex */
public class Java13CommandLauncher extends CommandLauncher {
    @Override // org.apache.tools.ant.taskdefs.launcher.CommandLauncher
    public Process exec(Project project, String[] strArr, String[] strArr2, File file) throws IOException {
        if (project != null) {
            try {
                project.log("Execute:Java13CommandLauncher: " + Commandline.describeCommand(strArr), 4);
            } catch (IOException e) {
                throw e;
            } catch (Exception e2) {
                throw new BuildException("Unable to execute command", e2);
            }
        }
        return Runtime.getRuntime().exec(strArr, strArr2, file);
    }
}
