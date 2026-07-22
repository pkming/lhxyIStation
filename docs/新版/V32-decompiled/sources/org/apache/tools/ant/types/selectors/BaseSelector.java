package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DataType;

/* JADX INFO: loaded from: classes3.dex */
public abstract class BaseSelector extends DataType implements FileSelector {
    private String errmsg = null;

    public abstract boolean isSelected(File file, String str, File file2);

    public void setError(String str) {
        if (this.errmsg == null) {
            this.errmsg = str;
        }
    }

    public String getError() {
        return this.errmsg;
    }

    public void verifySettings() {
        if (isReference()) {
            ((BaseSelector) getCheckedRef()).verifySettings();
        }
    }

    public void validate() {
        if (getError() == null) {
            verifySettings();
        }
        if (getError() != null) {
            throw new BuildException(this.errmsg);
        }
        if (isReference()) {
            return;
        }
        dieOnCircularReference();
    }
}
