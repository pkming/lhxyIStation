package org.apache.tools.ant.taskdefs.optional.unix;

import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public class Chown extends AbstractAccessTask {
    private boolean haveOwner = false;

    public Chown() {
        super.setExecutable("chown");
    }

    public void setOwner(String str) {
        createArg().setValue(str);
        this.haveOwner = true;
    }

    @Override // org.apache.tools.ant.taskdefs.ExecuteOn, org.apache.tools.ant.taskdefs.ExecTask
    protected void checkConfiguration() {
        if (!this.haveOwner) {
            throw new BuildException("Required attribute owner not set in chown", getLocation());
        }
        super.checkConfiguration();
    }

    @Override // org.apache.tools.ant.taskdefs.ExecTask
    public void setExecutable(String str) {
        throw new BuildException(getTaskType() + " doesn't support the executable attribute", getLocation());
    }
}
