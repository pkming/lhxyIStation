package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/* JADX INFO: loaded from: classes3.dex */
public class Deltree extends Task {
    private File dir;

    public void setDir(File file) {
        this.dir = file;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        log("DEPRECATED - The deltree task is deprecated.  Use delete instead.");
        File file = this.dir;
        if (file == null) {
            throw new BuildException("dir attribute must be set!", getLocation());
        }
        if (file.exists()) {
            if (!this.dir.isDirectory()) {
                if (!this.dir.delete()) {
                    throw new BuildException("Unable to delete directory " + this.dir.getAbsolutePath(), getLocation());
                }
            } else {
                log("Deleting: " + this.dir.getAbsolutePath());
                try {
                    removeDir(this.dir);
                } catch (IOException unused) {
                    throw new BuildException("Unable to delete " + this.dir.getAbsolutePath(), getLocation());
                }
            }
        }
    }

    private void removeDir(File file) throws IOException {
        for (String str : file.list()) {
            File file2 = new File(file, str);
            if (file2.isDirectory()) {
                removeDir(file2);
            } else if (!file2.delete()) {
                throw new BuildException("Unable to delete file " + file2.getAbsolutePath());
            }
        }
        if (!file.delete()) {
            throw new BuildException("Unable to delete directory " + file.getAbsolutePath());
        }
    }
}
