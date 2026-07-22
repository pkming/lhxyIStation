package org.apache.tools.ant.taskdefs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Task;

/* JADX INFO: loaded from: classes3.dex */
public class BindTargets extends Task {
    private String extensionPoint;
    private ProjectHelper.OnMissingExtensionPoint onMissingExtensionPoint;
    private List<String> targets = new ArrayList();

    public void setExtensionPoint(String str) {
        this.extensionPoint = str;
    }

    public void setOnMissingExtensionPoint(String str) {
        try {
            this.onMissingExtensionPoint = ProjectHelper.OnMissingExtensionPoint.valueOf(str);
        } catch (IllegalArgumentException unused) {
            throw new BuildException("Invalid onMissingExtensionPoint: " + str);
        }
    }

    public void setOnMissingExtensionPoint(ProjectHelper.OnMissingExtensionPoint onMissingExtensionPoint) {
        this.onMissingExtensionPoint = onMissingExtensionPoint;
    }

    public void setTargets(String str) {
        for (String str2 : str.split(",")) {
            String strTrim = str2.trim();
            if (strTrim.length() > 0) {
                this.targets.add(strTrim);
            }
        }
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (this.extensionPoint == null) {
            throw new BuildException("extensionPoint required", getLocation());
        }
        if (getOwningTarget() == null || !"".equals(getOwningTarget().getName())) {
            throw new BuildException("bindtargets only allowed as a top-level task");
        }
        if (this.onMissingExtensionPoint == null) {
            this.onMissingExtensionPoint = ProjectHelper.OnMissingExtensionPoint.FAIL;
        }
        ProjectHelper projectHelper = (ProjectHelper) getProject().getReference("ant.projectHelper");
        Iterator<String> it = this.targets.iterator();
        while (it.hasNext()) {
            projectHelper.getExtensionStack().add(new String[]{this.extensionPoint, it.next(), this.onMissingExtensionPoint.name()});
        }
    }
}
