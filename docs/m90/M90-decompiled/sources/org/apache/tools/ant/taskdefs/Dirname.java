package org.apache.tools.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/* JADX INFO: loaded from: classes3.dex */
public class Dirname extends Task {
    private File file;
    private String property;

    public void setFile(File file) {
        this.file = file;
    }

    public void setProperty(String str) {
        this.property = str;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (this.property == null) {
            throw new BuildException("property attribute required", getLocation());
        }
        File file = this.file;
        if (file == null) {
            throw new BuildException("file attribute required", getLocation());
        }
        getProject().setNewProperty(this.property, file.getParent());
    }
}
