package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/* JADX INFO: loaded from: classes3.dex */
public class Copyfile extends Task {
    private File destFile;
    private boolean filtering = false;
    private boolean forceOverwrite = false;
    private File srcFile;

    public void setSrc(File file) {
        this.srcFile = file;
    }

    public void setForceoverwrite(boolean z) {
        this.forceOverwrite = z;
    }

    public void setDest(File file) {
        this.destFile = file;
    }

    public void setFiltering(String str) {
        this.filtering = Project.toBoolean(str);
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        log("DEPRECATED - The copyfile task is deprecated.  Use copy instead.");
        File file = this.srcFile;
        if (file == null) {
            throw new BuildException("The src attribute must be present.", getLocation());
        }
        if (!file.exists()) {
            throw new BuildException("src " + this.srcFile.toString() + DirectoryScanner.DOES_NOT_EXIST_POSTFIX, getLocation());
        }
        File file2 = this.destFile;
        if (file2 == null) {
            throw new BuildException("The dest attribute must be present.", getLocation());
        }
        if (this.srcFile.equals(file2)) {
            log("Warning: src == dest", 1);
        }
        if (this.forceOverwrite || this.srcFile.lastModified() > this.destFile.lastModified()) {
            try {
                getProject().copyFile(this.srcFile, this.destFile, this.filtering, this.forceOverwrite);
            } catch (IOException e) {
                throw new BuildException("Error copying file: " + this.srcFile.getAbsolutePath() + " due to " + e.getMessage());
            }
        }
    }
}
