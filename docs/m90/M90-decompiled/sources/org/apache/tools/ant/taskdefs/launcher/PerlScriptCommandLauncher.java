package org.apache.tools.ant.taskdefs.launcher;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Project;

/* JADX INFO: loaded from: classes3.dex */
public class PerlScriptCommandLauncher extends CommandLauncherProxy {
    private final String myScript;

    public PerlScriptCommandLauncher(String str, CommandLauncher commandLauncher) {
        super(commandLauncher);
        this.myScript = str;
    }

    @Override // org.apache.tools.ant.taskdefs.launcher.CommandLauncher
    public Process exec(Project project, String[] strArr, String[] strArr2, File file) throws IOException {
        if (project == null) {
            if (file == null) {
                return exec(project, strArr, strArr2);
            }
            throw new IOException("Cannot locate antRun script: No project provided");
        }
        String property = project.getProperty(MagicNames.ANT_HOME);
        if (property == null) {
            throw new IOException("Cannot locate antRun script: Property 'ant.home' not found");
        }
        String string = FILE_UTILS.resolveFile(project.getBaseDir(), property + File.separator + this.myScript).toString();
        if (file == null) {
            file = project.getBaseDir();
        }
        String[] strArr3 = new String[strArr.length + 3];
        strArr3[0] = "perl";
        strArr3[1] = string;
        strArr3[2] = file.getAbsolutePath();
        System.arraycopy(strArr, 0, strArr3, 3, strArr.length);
        return exec(project, strArr3, strArr2);
    }
}
