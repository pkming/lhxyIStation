package org.apache.tools.ant.taskdefs.launcher;

import java.io.IOException;
import org.apache.tools.ant.Project;

/* JADX INFO: loaded from: classes3.dex */
public class CommandLauncherProxy extends CommandLauncher {
    private final CommandLauncher myLauncher;

    protected CommandLauncherProxy(CommandLauncher commandLauncher) {
        this.myLauncher = commandLauncher;
    }

    @Override // org.apache.tools.ant.taskdefs.launcher.CommandLauncher
    public Process exec(Project project, String[] strArr, String[] strArr2) throws IOException {
        return this.myLauncher.exec(project, strArr, strArr2);
    }
}
