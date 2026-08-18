package org.apache.tools.ant.attribute;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.UnknownElement;

/* JADX INFO: loaded from: classes3.dex */
public class IfTrueAttribute extends BaseIfAttribute {

    public static class Unless extends IfTrueAttribute {
        public Unless() {
            setPositive(false);
        }
    }

    @Override // org.apache.tools.ant.attribute.EnableAttribute
    public boolean isEnabled(UnknownElement unknownElement, String str) {
        return convertResult(Project.toBoolean(str));
    }
}
