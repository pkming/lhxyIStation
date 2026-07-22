package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Rename extends Task {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private File dest;
    private boolean replace = true;
    private File src;

    public void setSrc(File file) {
        this.src = file;
    }

    public void setDest(File file) {
        this.dest = file;
    }

    public void setReplace(String str) {
        this.replace = Project.toBoolean(str);
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        log("DEPRECATED - The rename task is deprecated.  Use move instead.");
        File file = this.dest;
        if (file == null) {
            throw new BuildException("dest attribute is required", getLocation());
        }
        if (this.src == null) {
            throw new BuildException("src attribute is required", getLocation());
        }
        if (!this.replace && file.exists()) {
            throw new BuildException(this.dest + " already exists.");
        }
        try {
            FILE_UTILS.rename(this.src, this.dest);
        } catch (IOException e) {
            throw new BuildException("Unable to rename " + this.src + " to " + this.dest, e, getLocation());
        }
    }
}
