package org.apache.tools.ant.taskdefs.optional.unix;

import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public class Chgrp extends AbstractAccessTask {
    private boolean haveGroup = false;

    public Chgrp() {
        super.setExecutable("chgrp");
    }

    public void setGroup(String str) {
        createArg().setValue(str);
        this.haveGroup = true;
    }

    @Override // org.apache.tools.ant.taskdefs.ExecuteOn, org.apache.tools.ant.taskdefs.ExecTask
    protected void checkConfiguration() {
        if (!this.haveGroup) {
            throw new BuildException("Required attribute group not set in chgrp", getLocation());
        }
        super.checkConfiguration();
    }

    @Override // org.apache.tools.ant.taskdefs.ExecTask
    public void setExecutable(String str) {
        throw new BuildException(getTaskType() + " doesn't support the executable attribute", getLocation());
    }
}
