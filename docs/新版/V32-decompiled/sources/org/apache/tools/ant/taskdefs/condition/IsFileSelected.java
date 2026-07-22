package org.apache.tools.ant.taskdefs.condition;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.selectors.AbstractSelectorContainer;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class IsFileSelected extends AbstractSelectorContainer implements Condition {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private File baseDir;
    private File file;

    public void setFile(File file) {
        this.file = file;
    }

    public void setBaseDir(File file) {
        this.baseDir = file;
    }

    @Override // org.apache.tools.ant.types.selectors.AbstractSelectorContainer
    public void validate() {
        if (selectorCount() != 1) {
            throw new BuildException("Only one selector allowed");
        }
        super.validate();
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() {
        if (this.file == null) {
            throw new BuildException("file attribute not set");
        }
        validate();
        File baseDir = this.baseDir;
        if (baseDir == null) {
            baseDir = getProject().getBaseDir();
        }
        return getSelectors(getProject())[0].isSelected(baseDir, FILE_UTILS.removeLeadingPath(baseDir, this.file), this.file);
    }
}
