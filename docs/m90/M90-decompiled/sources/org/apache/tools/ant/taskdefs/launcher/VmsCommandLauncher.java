package org.apache.tools.ant.taskdefs.launcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class VmsCommandLauncher extends Java13CommandLauncher {
    @Override // org.apache.tools.ant.taskdefs.launcher.CommandLauncher
    public Process exec(Project project, String[] strArr, String[] strArr2) throws Throwable {
        File fileCreateCommandFile = createCommandFile(strArr, strArr2);
        Process processExec = super.exec(project, new String[]{fileCreateCommandFile.getPath()}, strArr2);
        deleteAfter(fileCreateCommandFile, processExec);
        return processExec;
    }

    @Override // org.apache.tools.ant.taskdefs.launcher.Java13CommandLauncher, org.apache.tools.ant.taskdefs.launcher.CommandLauncher
    public Process exec(Project project, String[] strArr, String[] strArr2, File file) throws Throwable {
        File fileCreateCommandFile = createCommandFile(strArr, strArr2);
        Process processExec = super.exec(project, new String[]{fileCreateCommandFile.getPath()}, strArr2, file);
        deleteAfter(fileCreateCommandFile, processExec);
        return processExec;
    }

    private File createCommandFile(String[] strArr, String[] strArr2) throws Throwable {
        File fileCreateTempFile = FILE_UTILS.createTempFile("ANT", ".COM", null, true, true);
        BufferedWriter bufferedWriter = null;
        try {
            BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(fileCreateTempFile));
            if (strArr2 != null) {
                for (int i = 0; i < strArr2.length; i++) {
                    try {
                        int iIndexOf = strArr2[i].indexOf(61);
                        if (iIndexOf != -1) {
                            bufferedWriter2.write("$ DEFINE/NOLOG ");
                            bufferedWriter2.write(strArr2[i].substring(0, iIndexOf));
                            bufferedWriter2.write(" \"");
                            bufferedWriter2.write(strArr2[i].substring(iIndexOf + 1));
                            bufferedWriter2.write(34);
                            bufferedWriter2.newLine();
                        }
                    } catch (Throwable th) {
                        th = th;
                        bufferedWriter = bufferedWriter2;
                        FileUtils.close(bufferedWriter);
                        throw th;
                    }
                }
            }
            bufferedWriter2.write("$ " + strArr[0]);
            for (int i2 = 1; i2 < strArr.length; i2++) {
                bufferedWriter2.write(" -");
                bufferedWriter2.newLine();
                bufferedWriter2.write(strArr[i2]);
            }
            FileUtils.close(bufferedWriter2);
            return fileCreateTempFile;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [org.apache.tools.ant.taskdefs.launcher.VmsCommandLauncher$1] */
    private void deleteAfter(final File file, final Process process) {
        new Thread() { // from class: org.apache.tools.ant.taskdefs.launcher.VmsCommandLauncher.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    process.waitFor();
                } catch (InterruptedException unused) {
                }
                FileUtils.delete(file);
            }
        }.start();
    }
}
