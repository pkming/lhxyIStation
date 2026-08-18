package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.util.Hashtable;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.ejb.EjbJar;

/* JADX INFO: loaded from: classes3.dex */
public class JbossDeploymentTool extends GenericDeploymentTool {
    protected static final String JBOSS_CMP10D = "jaws.xml";
    protected static final String JBOSS_CMP20D = "jbosscmp-jdbc.xml";
    protected static final String JBOSS_DD = "jboss.xml";
    private String jarSuffix = ".jar";

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool, org.apache.tools.ant.taskdefs.optional.ejb.EJBDeploymentTool
    public void validateConfigured() throws BuildException {
    }

    public void setSuffix(String str) {
        this.jarSuffix = str;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool
    protected void addVendorFiles(Hashtable hashtable, String str) {
        File file = new File(getConfig().descriptorDir, str + JBOSS_DD);
        if (file.exists()) {
            hashtable.put("META-INF/jboss.xml", file);
            String str2 = EjbJar.CMPVersion.CMP2_0.equals(getParent().getCmpversion()) ? JBOSS_CMP20D : JBOSS_CMP10D;
            File file2 = new File(getConfig().descriptorDir, str + str2);
            if (file2.exists()) {
                hashtable.put("META-INF/" + str2, file2);
                return;
            } else {
                log("Unable to locate jboss cmp descriptor. It was expected to be in " + file2.getPath(), 3);
                return;
            }
        }
        log("Unable to locate jboss deployment descriptor. It was expected to be in " + file.getPath(), 1);
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool
    File getVendorOutputJarFile(String str) {
        if (getDestDir() == null && getParent().getDestdir() == null) {
            throw new BuildException("DestDir not specified");
        }
        if (getDestDir() == null) {
            return new File(getParent().getDestdir(), str + this.jarSuffix);
        }
        return new File(getDestDir(), str + this.jarSuffix);
    }

    private EjbJar getParent() {
        return (EjbJar) getTask();
    }
}
