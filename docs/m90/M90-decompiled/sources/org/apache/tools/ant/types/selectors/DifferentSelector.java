package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class DifferentSelector extends MappingSelector {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private boolean ignoreFileTimes = true;
    private boolean ignoreContents = false;

    public void setIgnoreFileTimes(boolean z) {
        this.ignoreFileTimes = z;
    }

    public void setIgnoreContents(boolean z) {
        this.ignoreContents = z;
    }

    @Override // org.apache.tools.ant.types.selectors.MappingSelector
    protected boolean selectionTest(File file, File file2) {
        if (file.exists() != file2.exists() || file.length() != file2.length()) {
            return true;
        }
        if (!this.ignoreFileTimes) {
            if (!(file2.lastModified() >= file.lastModified() - ((long) this.granularity) && file2.lastModified() <= file.lastModified() + ((long) this.granularity))) {
                return true;
            }
        }
        if (this.ignoreContents) {
            return false;
        }
        try {
            return !FILE_UTILS.contentEquals(file, file2);
        } catch (IOException e) {
            throw new BuildException("while comparing " + file + " and " + file2, e);
        }
    }
}
