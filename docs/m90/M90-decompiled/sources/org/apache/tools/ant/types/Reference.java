package org.apache.tools.ant.types;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/* JADX INFO: loaded from: classes3.dex */
public class Reference {
    private Project project;
    private String refid;

    public Reference() {
    }

    public Reference(String str) {
        setRefId(str);
    }

    public Reference(Project project, String str) {
        setRefId(str);
        setProject(project);
    }

    public void setRefId(String str) {
        this.refid = str;
    }

    public String getRefId() {
        return this.refid;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return this.project;
    }

    public Object getReferencedObject(Project project) throws BuildException {
        String str = this.refid;
        if (str == null) {
            throw new BuildException("No reference specified");
        }
        Project project2 = this.project;
        Object reference = project2 == null ? project.getReference(str) : project2.getReference(str);
        if (reference != null) {
            return reference;
        }
        throw new BuildException("Reference " + this.refid + " not found.");
    }

    public Object getReferencedObject() throws BuildException {
        Project project = this.project;
        if (project == null) {
            throw new BuildException("No project set on reference to " + this.refid);
        }
        return getReferencedObject(project);
    }
}
