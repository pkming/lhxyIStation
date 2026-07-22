package org.apache.tools.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/* JADX INFO: loaded from: classes3.dex */
public class Mkdir extends Task {
    private static final int MKDIR_RETRY_SLEEP_MILLIS = 10;
    private File dir;

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        File file = this.dir;
        if (file == null) {
            throw new BuildException("dir attribute is required", getLocation());
        }
        if (file.isFile()) {
            throw new BuildException("Unable to create directory as a file already exists with that name: " + this.dir.getAbsolutePath());
        }
        if (!this.dir.exists()) {
            if (!mkdirs(this.dir)) {
                if (this.dir.exists()) {
                    log("A different process or task has already created dir " + this.dir.getAbsolutePath(), 3);
                    return;
                }
                throw new BuildException("Directory " + this.dir.getAbsolutePath() + " creation was not successful for an unknown reason", getLocation());
            }
            log("Created dir: " + this.dir.getAbsolutePath());
            return;
        }
        log("Skipping " + this.dir.getAbsolutePath() + " because it already exists.", 3);
    }

    public void setDir(File file) {
        this.dir = file;
    }

    public File getDir() {
        return this.dir;
    }

    private boolean mkdirs(File file) {
        if (file.mkdirs()) {
            return true;
        }
        try {
            Thread.sleep(10L);
            return file.mkdirs();
        } catch (InterruptedException unused) {
            return file.mkdirs();
        }
    }
}
