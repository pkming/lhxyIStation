package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.util.Enumeration;

/* JADX INFO: loaded from: classes3.dex */
public class AndSelector extends BaseSelectorContainer {
    @Override // org.apache.tools.ant.types.selectors.BaseSelectorContainer, org.apache.tools.ant.types.DataType
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (hasSelectors()) {
            sb.append("{andselect: ");
            sb.append(super.toString());
            sb.append("}");
        }
        return sb.toString();
    }

    @Override // org.apache.tools.ant.types.selectors.BaseSelectorContainer, org.apache.tools.ant.types.selectors.BaseSelector, org.apache.tools.ant.types.selectors.FileSelector
    public boolean isSelected(File file, String str, File file2) {
        validate();
        Enumeration<FileSelector> enumerationSelectorElements = selectorElements();
        while (enumerationSelectorElements.hasMoreElements()) {
            if (!enumerationSelectorElements.nextElement().isSelected(file, str, file2)) {
                return false;
            }
        }
        return true;
    }
}
