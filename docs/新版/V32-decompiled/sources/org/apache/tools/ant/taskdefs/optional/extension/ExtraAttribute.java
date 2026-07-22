package org.apache.tools.ant.taskdefs.optional.extension;

import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public class ExtraAttribute {
    private String name;
    private String value;

    public void setName(String str) {
        this.name = str;
    }

    public void setValue(String str) {
        this.value = str;
    }

    String getName() {
        return this.name;
    }

    String getValue() {
        return this.value;
    }

    public void validate() throws BuildException {
        if (this.name == null) {
            throw new BuildException("Missing name from parameter.");
        }
        if (this.value == null) {
            throw new BuildException("Missing value from parameter " + this.name + ".");
        }
    }
}
