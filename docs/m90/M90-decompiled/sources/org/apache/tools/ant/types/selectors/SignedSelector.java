package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.taskdefs.condition.IsSigned;
import org.apache.tools.ant.types.DataType;

/* JADX INFO: loaded from: classes3.dex */
public class SignedSelector extends DataType implements FileSelector {
    private IsSigned isSigned = new IsSigned();

    public void setName(String str) {
        this.isSigned.setName(str);
    }

    @Override // org.apache.tools.ant.types.selectors.FileSelector
    public boolean isSelected(File file, String str, File file2) {
        if (file2.isDirectory()) {
            return false;
        }
        this.isSigned.setProject(getProject());
        this.isSigned.setFile(file2);
        return this.isSigned.eval();
    }
}
