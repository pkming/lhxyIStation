package org.apache.tools.ant.types.selectors;

/* JADX INFO: loaded from: classes3.dex */
public class NotSelector extends NoneSelector {
    public NotSelector() {
    }

    public NotSelector(FileSelector fileSelector) {
        this();
        appendSelector(fileSelector);
    }

    @Override // org.apache.tools.ant.types.selectors.NoneSelector, org.apache.tools.ant.types.selectors.BaseSelectorContainer, org.apache.tools.ant.types.DataType
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (hasSelectors()) {
            sb.append("{notselect: ");
            sb.append(super.toString());
            sb.append("}");
        }
        return sb.toString();
    }

    @Override // org.apache.tools.ant.types.selectors.BaseSelector
    public void verifySettings() {
        if (selectorCount() != 1) {
            setError("One and only one selector is allowed within the <not> tag");
        }
    }
}
