package org.apache.tools.ant.taskdefs.condition;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class FilesMatch implements Condition {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private File file1;
    private File file2;
    private boolean textfile = false;

    public void setFile1(File file) {
        this.file1 = file;
    }

    public void setFile2(File file) {
        this.file2 = file;
    }

    public void setTextfile(boolean z) {
        this.textfile = z;
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() throws BuildException {
        File file;
        File file2 = this.file1;
        if (file2 == null || (file = this.file2) == null) {
            throw new BuildException("both file1 and file2 are required in filesmatch");
        }
        try {
            return FILE_UTILS.contentEquals(file2, file, this.textfile);
        } catch (IOException e) {
            throw new BuildException("when comparing files: " + e.getMessage(), e);
        }
    }
}
