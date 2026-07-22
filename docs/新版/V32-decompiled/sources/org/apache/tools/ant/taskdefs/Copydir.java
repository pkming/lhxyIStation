package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public class Copydir extends MatchingTask {
    private File destDir;
    private File srcDir;
    private boolean filtering = false;
    private boolean flatten = false;
    private boolean forceOverwrite = false;
    private Hashtable<String, String> filecopyList = new Hashtable<>();

    public void setSrc(File file) {
        this.srcDir = file;
    }

    public void setDest(File file) {
        this.destDir = file;
    }

    public void setFiltering(boolean z) {
        this.filtering = z;
    }

    public void setFlatten(boolean z) {
        this.flatten = z;
    }

    public void setForceoverwrite(boolean z) {
        this.forceOverwrite = z;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        log("DEPRECATED - The copydir task is deprecated.  Use copy instead.");
        File file = this.srcDir;
        if (file == null) {
            throw new BuildException("src attribute must be set!", getLocation());
        }
        if (!file.exists()) {
            throw new BuildException("srcdir " + this.srcDir.toString() + " does not exist!", getLocation());
        }
        File file2 = this.destDir;
        if (file2 == null) {
            throw new BuildException("The dest attribute must be set.", getLocation());
        }
        if (this.srcDir.equals(file2)) {
            log("Warning: src == dest", 1);
        }
        try {
            scanDir(this.srcDir, this.destDir, super.getDirectoryScanner(this.srcDir).getIncludedFiles());
            if (this.filecopyList.size() > 0) {
                log("Copying " + this.filecopyList.size() + " file" + (this.filecopyList.size() == 1 ? "" : "s") + " to " + this.destDir.getAbsolutePath());
                for (Map.Entry<String, String> entry : this.filecopyList.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    try {
                        getProject().copyFile(key, value, this.filtering, this.forceOverwrite);
                    } catch (IOException e) {
                        throw new BuildException("Failed to copy " + key + " to " + value + " due to " + e.getMessage(), e, getLocation());
                    }
                }
            }
        } finally {
            this.filecopyList.clear();
        }
    }

    private void scanDir(File file, File file2, String[] strArr) {
        File file3;
        for (String str : strArr) {
            File file4 = new File(file, str);
            if (this.flatten) {
                file3 = new File(file2, new File(str).getName());
            } else {
                file3 = new File(file2, str);
            }
            if (this.forceOverwrite || file4.lastModified() > file3.lastModified()) {
                this.filecopyList.put(file4.getAbsolutePath(), file3.getAbsolutePath());
            }
        }
    }
}
