package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;

/* JADX INFO: loaded from: classes3.dex */
public class IsSet extends ProjectComponent implements Condition {
    private String property;

    public void setProperty(String str) {
        this.property = str;
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() throws BuildException {
        if (this.property != null) {
            return getProject().getProperty(this.property) != null;
        }
        throw new BuildException("No property specified for isset condition");
    }
}
