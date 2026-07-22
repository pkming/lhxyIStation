package org.apache.tools.ant.taskdefs.launcher;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.Project;

/* JADX INFO: loaded from: classes3.dex */
public class OS2CommandLauncher extends CommandLauncherProxy {
    public OS2CommandLauncher(CommandLauncher commandLauncher) {
        super(commandLauncher);
    }

    @Override // org.apache.tools.ant.taskdefs.launcher.CommandLauncher
    public Process exec(Project project, String[] strArr, String[] strArr2, File file) throws IOException {
        if (file == null) {
            if (project != null) {
                file = project.getBaseDir();
            } else {
                return exec(project, strArr, strArr2);
            }
        }
        String absolutePath = file.getAbsolutePath();
        String[] strArr3 = new String[strArr.length + 7];
        strArr3[0] = "cmd";
        strArr3[1] = "/c";
        strArr3[2] = absolutePath.substring(0, 2);
        strArr3[3] = "&&";
        strArr3[4] = "cd";
        strArr3[5] = absolutePath.substring(2);
        strArr3[6] = "&&";
        System.arraycopy(strArr, 0, strArr3, 7, strArr.length);
        return exec(project, strArr3, strArr2);
    }
}
