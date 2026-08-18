package org.apache.tools.ant.taskdefs.optional.j2ee;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Java;

/* JADX INFO: loaded from: classes3.dex */
public class WebLogicHotDeploymentTool extends AbstractHotDeploymentTool implements HotDeploymentTool {
    private static final int STRING_BUFFER_SIZE = 1024;
    private static final String[] VALID_ACTIONS = {HotDeploymentTool.ACTION_DELETE, HotDeploymentTool.ACTION_DEPLOY, HotDeploymentTool.ACTION_LIST, HotDeploymentTool.ACTION_UNDEPLOY, "update"};
    private static final String WEBLOGIC_DEPLOY_CLASS_NAME = "weblogic.deploy";
    private String application;
    private String component;
    private boolean debug;

    @Override // org.apache.tools.ant.taskdefs.optional.j2ee.AbstractHotDeploymentTool, org.apache.tools.ant.taskdefs.optional.j2ee.HotDeploymentTool
    public void deploy() {
        Java java = new Java(getTask());
        java.setFork(true);
        java.setFailonerror(true);
        java.setClasspath(getClasspath());
        java.setClassname(WEBLOGIC_DEPLOY_CLASS_NAME);
        java.createArg().setLine(getArguments());
        java.execute();
    }

    @Override // org.apache.tools.ant.taskdefs.optional.j2ee.AbstractHotDeploymentTool, org.apache.tools.ant.taskdefs.optional.j2ee.HotDeploymentTool
    public void validateAttributes() throws BuildException {
        super.validateAttributes();
        String action = getTask().getAction();
        if (getPassword() == null) {
            throw new BuildException("The password attribute must be set.");
        }
        if ((action.equals(HotDeploymentTool.ACTION_DEPLOY) || action.equals("update")) && this.application == null) {
            throw new BuildException("The application attribute must be set if action = " + action);
        }
        if ((action.equals(HotDeploymentTool.ACTION_DEPLOY) || action.equals("update")) && getTask().getSource() == null) {
            throw new BuildException("The source attribute must be set if action = " + action);
        }
        if ((action.equals(HotDeploymentTool.ACTION_DELETE) || action.equals(HotDeploymentTool.ACTION_UNDEPLOY)) && this.application == null) {
            throw new BuildException("The application attribute must be set if action = " + action);
        }
    }

    public String getArguments() throws BuildException {
        String action = getTask().getAction();
        if (action.equals(HotDeploymentTool.ACTION_DEPLOY) || action.equals("update")) {
            return buildDeployArgs();
        }
        if (action.equals(HotDeploymentTool.ACTION_DELETE) || action.equals(HotDeploymentTool.ACTION_UNDEPLOY)) {
            return buildUndeployArgs();
        }
        if (action.equals(HotDeploymentTool.ACTION_LIST)) {
            return buildListArgs();
        }
        return null;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.j2ee.AbstractHotDeploymentTool
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

    protected StringBuffer buildArgsPrefix() {
        return new StringBuffer(1024).append(getServer() != null ? "-url " + getServer() : "").append(" ").append(this.debug ? "-debug " : "").append(getUserName() != null ? "-username " + getUserName() : "").append(" ").append(getTask().getAction()).append(" ").append(getPassword()).append(" ");
    }

    protected String buildDeployArgs() {
        String string = buildArgsPrefix().append(this.application).append(" ").append(getTask().getSource()).toString();
        return this.component != null ? "-component " + this.component + " " + string : string;
    }

    protected String buildUndeployArgs() {
        return buildArgsPrefix().append(this.application).append(" ").toString();
    }

    protected String buildListArgs() {
        return buildArgsPrefix().toString();
    }

    public void setDebug(boolean z) {
        this.debug = z;
    }

    public void setApplication(String str) {
        this.application = str;
    }

    public void setComponent(String str) {
        this.component = str;
    }
}
