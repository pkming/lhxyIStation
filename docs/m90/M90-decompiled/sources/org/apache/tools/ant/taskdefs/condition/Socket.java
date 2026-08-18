package org.apache.tools.ant.taskdefs.condition;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;

/* JADX INFO: loaded from: classes3.dex */
public class Socket extends ProjectComponent implements Condition {
    private String server = null;
    private int port = 0;

    public void setServer(String str) {
        this.server = str;
    }

    public void setPort(int i) {
        this.port = i;
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() throws BuildException {
        if (this.server == null) {
            throw new BuildException("No server specified in socket condition");
        }
        if (this.port == 0) {
            throw new BuildException("No port specified in socket condition");
        }
        log("Checking for listener at " + this.server + ":" + this.port, 3);
        try {
            try {
                new java.net.Socket(this.server, this.port).close();
                return true;
            } catch (IOException unused) {
                return true;
            }
        } catch (IOException unused2) {
            return false;
        }
    }
}
