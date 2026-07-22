package org.apache.tools.ant.taskdefs.optional.jlink;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.Path;

/* JADX INFO: loaded from: classes3.dex */
public class JlinkTask extends MatchingTask {
    private File outfile = null;
    private Path mergefiles = null;
    private Path addfiles = null;
    private boolean compress = false;

    public void setOutfile(File file) {
        this.outfile = file;
    }

    public Path createMergefiles() {
        if (this.mergefiles == null) {
            this.mergefiles = new Path(getProject());
        }
        return this.mergefiles.createPath();
    }

    public void setMergefiles(Path path) {
        Path path2 = this.mergefiles;
        if (path2 == null) {
            this.mergefiles = path;
        } else {
            path2.append(path);
        }
    }

    public Path createAddfiles() {
        if (this.addfiles == null) {
            this.addfiles = new Path(getProject());
        }
        return this.addfiles.createPath();
    }

    public void setAddfiles(Path path) {
        Path path2 = this.addfiles;
        if (path2 == null) {
            this.addfiles = path;
        } else {
            path2.append(path);
        }
    }

    public void setCompress(boolean z) {
        this.compress = z;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (this.outfile == null) {
            throw new BuildException("outfile attribute is required! Please set.");
        }
        if (!haveAddFiles() && !haveMergeFiles()) {
            throw new BuildException("addfiles or mergefiles required! Please set.");
        }
        log("linking:     " + this.outfile.getPath());
        log("compression: " + this.compress, 3);
        jlink jlinkVar = new jlink();
        jlinkVar.setOutfile(this.outfile.getPath());
        jlinkVar.setCompression(this.compress);
        if (haveMergeFiles()) {
            log("merge files: " + this.mergefiles.toString(), 3);
            jlinkVar.addMergeFiles(this.mergefiles.list());
        }
        if (haveAddFiles()) {
            log("add files: " + this.addfiles.toString(), 3);
            jlinkVar.addAddFiles(this.addfiles.list());
        }
        try {
            jlinkVar.link();
        } catch (Exception e) {
            throw new BuildException(e, getLocation());
        }
    }

    private boolean haveAddFiles() {
        return haveEntries(this.addfiles);
    }

    private boolean haveMergeFiles() {
        return haveEntries(this.mergefiles);
    }

    private boolean haveEntries(Path path) {
        return path != null && path.size() > 0;
    }
}
