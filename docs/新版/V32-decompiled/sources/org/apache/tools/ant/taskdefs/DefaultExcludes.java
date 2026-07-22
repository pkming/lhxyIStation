package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.StringUtils;

/* JADX INFO: loaded from: classes3.dex */
public class DefaultExcludes extends Task {
    private String add = "";
    private String remove = "";
    private boolean defaultrequested = false;
    private boolean echo = false;
    private int logLevel = 1;

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (!this.defaultrequested && this.add.equals("") && this.remove.equals("") && !this.echo) {
            throw new BuildException("<defaultexcludes> task must set at least one attribute (echo=\"false\" doesn't count since that is the default");
        }
        if (this.defaultrequested) {
            DirectoryScanner.resetDefaultExcludes();
        }
        if (!this.add.equals("")) {
            DirectoryScanner.addDefaultExclude(this.add);
        }
        if (!this.remove.equals("")) {
            DirectoryScanner.removeDefaultExclude(this.remove);
        }
        if (this.echo) {
            StringBuffer stringBuffer = new StringBuffer("Current Default Excludes:");
            stringBuffer.append(StringUtils.LINE_SEP);
            for (String str : DirectoryScanner.getDefaultExcludes()) {
                stringBuffer.append("  ");
                stringBuffer.append(str);
                stringBuffer.append(StringUtils.LINE_SEP);
            }
            log(stringBuffer.toString(), this.logLevel);
        }
    }

    public void setDefault(boolean z) {
        this.defaultrequested = z;
    }

    public void setAdd(String str) {
        this.add = str;
    }

    public void setRemove(String str) {
        this.remove = str;
    }

    public void setEcho(boolean z) {
        this.echo = z;
    }
}
