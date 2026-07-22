package org.apache.tools.ant.taskdefs.optional.j2ee;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;

/* JADX INFO: loaded from: classes3.dex */
public class JonasHotDeploymentTool extends GenericHotDeploymentTool implements HotDeploymentTool {
    protected static final String DEFAULT_ORB = "RMI";
    private static final String JONAS_DEPLOY_CLASS_NAME = "org.objectweb.jonas.adm.JonasAdmin";
    private static final String[] VALID_ACTIONS = {HotDeploymentTool.ACTION_DELETE, HotDeploymentTool.ACTION_DEPLOY, HotDeploymentTool.ACTION_LIST, HotDeploymentTool.ACTION_UNDEPLOY, "update"};
    private String davidHost;
    private int davidPort;
    private File jonasroot;
    private String orb = null;

    public void setDavidhost(String str) {
        this.davidHost = str;
    }

    public void setDavidport(int i) {
        this.davidPort = i;
    }

    public void setJonasroot(File file) {
        this.jonasroot = file;
    }

    public void setOrb(String str) {
        this.orb = str;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.j2ee.AbstractHotDeploymentTool
    public Path getClasspath() {
        Path classpath = super.getClasspath();
        if (classpath == null) {
            classpath = new Path(getTask().getProject());
        }
        if (this.orb != null) {
            classpath.append(new Path(classpath.getProject(), new File(this.jonasroot, "lib/" + this.orb + "_jonas.jar").toString() + File.pathSeparator + new File(this.jonasroot, "config/").toString()));
        }
        return classpath;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.j2ee.GenericHotDeploymentTool, org.apache.tools.ant.taskdefs.optional.j2ee.AbstractHotDeploymentTool, org.apache.tools.ant.taskdefs.optional.j2ee.HotDeploymentTool
    public void validateAttributes() throws BuildException {
        Java java = getJava();
        String action = getTask().getAction();
        if (action == null) {
            throw new BuildException("The \"action\" attribute must be set");
        }
        if (!isActionValid()) {
            throw new BuildException("Invalid action \"" + action + "\" passed");
        }
        if (getClassName() == null) {
            setClassName(JONAS_DEPLOY_CLASS_NAME);
        }
        File file = this.jonasroot;
        if (file == null || file.isDirectory()) {
            java.createJvmarg().setValue("-Dinstall.root=" + this.jonasroot);
            java.createJvmarg().setValue("-Djava.security.policy=" + this.jonasroot + "/config/java.policy");
            if ("DAVID".equals(this.orb)) {
                java.createJvmarg().setValue("-Dorg.omg.CORBA.ORBClass=org.objectweb.david.libs.binding.orbs.iiop.IIOPORB");
                java.createJvmarg().setValue("-Dorg.omg.CORBA.ORBSingletonClass=org.objectweb.david.libs.binding.orbs.ORBSingletonClass");
                java.createJvmarg().setValue("-Djavax.rmi.CORBA.StubClass=org.objectweb.david.libs.stub_factories.rmi.StubDelegate");
                java.createJvmarg().setValue("-Djavax.rmi.CORBA.PortableRemoteObjectClass=org.objectweb.david.libs.binding.rmi.ORBPortableRemoteObjectDelegate");
                java.createJvmarg().setValue("-Djavax.rmi.CORBA.UtilClass=org.objectweb.david.libs.helpers.RMIUtilDelegate");
                java.createJvmarg().setValue("-Ddavid.CosNaming.default_method=0");
                java.createJvmarg().setValue("-Ddavid.rmi.ValueHandlerClass=com.sun.corba.se.internal.io.ValueHandlerImpl");
                if (this.davidHost != null) {
                    java.createJvmarg().setValue("-Ddavid.CosNaming.default_host=" + this.davidHost);
                }
                if (this.davidPort != 0) {
                    java.createJvmarg().setValue("-Ddavid.CosNaming.default_port=" + this.davidPort);
                }
            }
        }
        if (getServer() != null) {
            java.createArg().setLine("-n " + getServer());
        }
        if (action.equals(HotDeploymentTool.ACTION_DEPLOY) || action.equals("update") || action.equals("redeploy")) {
            java.createArg().setLine("-a " + getTask().getSource());
            return;
        }
        if (action.equals(HotDeploymentTool.ACTION_DELETE) || action.equals(HotDeploymentTool.ACTION_UNDEPLOY)) {
            java.createArg().setLine("-r " + getTask().getSource());
        } else if (action.equals(HotDeploymentTool.ACTION_LIST)) {
            java.createArg().setValue("-l");
        }
    }

    @Override // org.apache.tools.ant.taskdefs.optional.j2ee.GenericHotDeploymentTool, org.apache.tools.ant.taskdefs.optional.j2ee.AbstractHotDeploymentTool
    protected boolean isActionValid() {
        String action = getTask().getAction();
        int i = 0;
        while (true) {
            String[] strArr = VALID_ACTIONS;
            if (i >= strArr.length) {
                return false;
            }
            if (action.equals(strArr[i])) {
                return true;
            }
            i++;
        }
    }
}
