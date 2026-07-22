package org.apache.tools.ant.types.spi;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;

/* JADX INFO: loaded from: classes3.dex */
public class Provider extends ProjectComponent {
    private String type;

    public String getClassName() {
        return this.type;
    }

    public void setClassName(String str) {
        this.type = str;
    }

    public void check() {
        String str = this.type;
        if (str == null) {
            throw new BuildException("classname attribute must be set for provider element", getLocation());
        }
        if (str.length() == 0) {
            throw new BuildException("Invalid empty classname", getLocation());
        }
    }
}
