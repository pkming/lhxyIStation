package org.apache.tools.ant.taskdefs.launcher;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.Project;

/* JADX INFO: loaded from: classes3.dex */
public class WinNTCommandLauncher extends CommandLauncherProxy {
    public WinNTCommandLauncher(CommandLauncher commandLauncher) {
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
        String[] strArr3 = new String[strArr.length + 6];
        strArr3[0] = "cmd";
        strArr3[1] = "/c";
        strArr3[2] = "cd";
        strArr3[3] = "/d";
        strArr3[4] = file.getAbsolutePath();
        strArr3[5] = "&&";
        System.arraycopy(strArr, 0, strArr3, 6, strArr.length);
        return exec(project, strArr3, strArr2);
    }
}
