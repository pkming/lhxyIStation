package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.util.Enumeration;

/* JADX INFO: loaded from: classes3.dex */
public class MajoritySelector extends BaseSelectorContainer {
    private boolean allowtie = true;

    @Override // org.apache.tools.ant.types.selectors.BaseSelectorContainer, org.apache.tools.ant.types.DataType
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (hasSelectors()) {
            sb.append("{majorityselect: ");
            sb.append(super.toString());
            sb.append("}");
        }
        return sb.toString();
    }

    public void setAllowtie(boolean z) {
        this.allowtie = z;
    }

    @Override // org.apache.tools.ant.types.selectors.BaseSelectorContainer, org.apache.tools.ant.types.selectors.BaseSelector, org.apache.tools.ant.types.selectors.FileSelector
    public boolean isSelected(File file, String str, File file2) {
        validate();
        Enumeration<FileSelector> enumerationSelectorElements = selectorElements();
        int i = 0;
        int i2 = 0;
        while (enumerationSelectorElements.hasMoreElements()) {
            if (enumerationSelectorElements.nextElement().isSelected(file, str, file2)) {
                i++;
            } else {
                i2++;
            }
        }
        if (i > i2) {
            return true;
        }
        if (i2 > i) {
            return false;
        }
        return this.allowtie;
    }
}
