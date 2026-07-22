package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;

/* JADX INFO: loaded from: classes3.dex */
public class IsTrue extends ProjectComponent implements Condition {
    private Boolean value = null;

    public void setValue(boolean z) {
        this.value = z ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() throws BuildException {
        Boolean bool = this.value;
        if (bool == null) {
            throw new BuildException("Nothing to test for truth");
        }
        return bool.booleanValue();
    }
}
