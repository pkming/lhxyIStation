package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class CopyPath extends Task {
    public static final String ERROR_NO_DESTDIR = "No destDir specified";
    public static final String ERROR_NO_MAPPER = "No mapper specified";
    public static final String ERROR_NO_PATH = "No path specified";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private File destDir;
    private FileNameMapper mapper;
    private Path path;
    private long granularity = FILE_UTILS.getFileTimestampGranularity();
    private boolean preserveLastModified = false;

    public void setDestDir(File file) {
        this.destDir = file;
    }

    public void add(FileNameMapper fileNameMapper) {
        if (this.mapper != null) {
            throw new BuildException("Only one mapper allowed");
        }
        this.mapper = fileNameMapper;
    }

    public void setPath(Path path) {
        createPath().append(path);
    }

    public void setPathRef(Reference reference) {
        createPath().setRefid(reference);
    }

    public Path createPath() {
        if (this.path == null) {
            this.path = new Path(getProject());
        }
        return this.path;
    }

    public void setGranularity(long j) {
        this.granularity = j;
    }

    public void setPreserveLastModified(boolean z) {
        this.preserveLastModified = z;
    }

    protected void validateAttributes() throws BuildException {
        if (this.destDir == null) {
            throw new BuildException(ERROR_NO_DESTDIR);
        }
        if (this.mapper == null) {
            throw new BuildException(ERROR_NO_MAPPER);
        }
        if (this.path == null) {
            throw new BuildException(ERROR_NO_PATH);
        }
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        File file;
        File file2;
        int i;
        String[] strArr;
        int i2 = 0;
        log("This task should have never been released and was obsoleted by ResourceCollection support in <copy> available since Ant 1.7.0.  Don't use it.", 0);
        validateAttributes();
        String[] list = this.path.list();
        int i3 = 3;
        if (list.length == 0) {
            log("Path is empty", 3);
            return;
        }
        int i4 = 0;
        while (i4 < list.length) {
            String str = list[i4];
            File file3 = new File(str);
            String[] strArrMapFileName = this.mapper.mapFileName(str);
            int i5 = i2;
            while (i5 < strArrMapFileName.length) {
                File file4 = new File(this.destDir, strArrMapFileName[i5]);
                if (file3.equals(file4)) {
                    log("Skipping self-copy of " + str, i3);
                } else if (file3.isDirectory()) {
                    log("Skipping directory " + str);
                } else {
                    try {
                        log("Copying " + file3 + " to " + file4, i3);
                        file = file4;
                        i = i5;
                        strArr = strArrMapFileName;
                        file2 = file3;
                    } catch (IOException e) {
                        e = e;
                        file = file4;
                        file2 = file3;
                    }
                    try {
                        FILE_UTILS.copyFile(file3, file4, (FilterSetCollection) null, (Vector) null, false, this.preserveLastModified, (String) null, (String) null, getProject());
                        i5 = i + 1;
                        file3 = file2;
                        strArrMapFileName = strArr;
                        i3 = 3;
                    } catch (IOException e2) {
                        e = e2;
                        String str2 = "Failed to copy " + file2 + " to " + file + " due to " + e.getMessage();
                        if (file.exists() && !file.delete()) {
                            str2 = str2 + " and I couldn't delete the corrupt " + file;
                        }
                        throw new BuildException(str2, e, getLocation());
                    }
                }
                i = i5;
                strArr = strArrMapFileName;
                file2 = file3;
                i5 = i + 1;
                file3 = file2;
                strArrMapFileName = strArr;
                i3 = 3;
            }
            i4++;
            i2 = 0;
            i3 = 3;
        }
    }
}
