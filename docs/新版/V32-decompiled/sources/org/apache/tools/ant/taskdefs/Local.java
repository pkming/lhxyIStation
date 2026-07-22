package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.property.LocalProperties;

/* JADX INFO: loaded from: classes3.dex */
public class Local extends Task {
    private String name;

    public void setName(String str) {
        this.name = str;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() {
        if (this.name == null) {
            throw new BuildException("Missing attribute name");
        }
        LocalProperties.get(getProject()).addLocal(this.name);
    }
}
