package org.apache.tools.ant.taskdefs.condition;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.util.ReflectWrapper;
import org.apache.tools.ant.util.StringUtils;

/* JADX INFO: loaded from: classes3.dex */
public class HasFreeSpace implements Condition {
    private String needed;
    private String partition;

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() throws BuildException {
        validate();
        try {
            if (JavaEnvUtils.isAtLeastJavaVersion(JavaEnvUtils.JAVA_1_6)) {
                return ((Long) new ReflectWrapper(new File(this.partition)).invoke("getFreeSpace")).longValue() >= StringUtils.parseHumanSizes(this.needed);
            }
            throw new BuildException("HasFreeSpace condition not supported on Java5 or less.");
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    private void validate() throws BuildException {
        if (this.partition == null) {
            throw new BuildException("Please set the partition attribute.");
        }
        if (this.needed == null) {
            throw new BuildException("Please set the needed attribute.");
        }
    }

    public String getPartition() {
        return this.partition;
    }

    public void setPartition(String str) {
        this.partition = str;
    }

    public String getNeeded() {
        return this.needed;
    }

    public void setNeeded(String str) {
        this.needed = str;
    }
}
