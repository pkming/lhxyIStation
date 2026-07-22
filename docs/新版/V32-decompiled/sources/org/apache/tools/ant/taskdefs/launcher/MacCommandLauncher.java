package org.apache.tools.ant.taskdefs.launcher;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.Project;

/* JADX INFO: loaded from: classes3.dex */
public class MacCommandLauncher extends CommandLauncherProxy {
    public MacCommandLauncher(CommandLauncher commandLauncher) {
        super(commandLauncher);
    }

    @Override // org.apache.tools.ant.taskdefs.launcher.CommandLauncher
    public Process exec(Project project, String[] strArr, String[] strArr2, File file) throws IOException {
        if (file == null) {
            return exec(project, strArr, strArr2);
        }
        System.getProperties().put("user.dir", file.getAbsolutePath());
        try {
            return exec(project, strArr, strArr2);
        } finally {
            System.getProperties().put("user.dir", System.getProperty("user.dir"));
        }
    }
}
