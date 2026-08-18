package org.apache.tools.ant.taskdefs.condition;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ResourceContains implements Condition {
    private boolean casesensitive = true;
    private Project project;
    private String refid;
    private Resource resource;
    private String substring;

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return this.project;
    }

    public void setResource(String str) {
        this.resource = new FileResource(new File(str));
    }

    public void setRefid(String str) {
        this.refid = str;
    }

    private void resolveRefid() {
        try {
            if (getProject() == null) {
                throw new BuildException("Cannot retrieve refid; project unset");
            }
            Object reference = getProject().getReference(this.refid);
            if (!(reference instanceof Resource)) {
                if (reference instanceof ResourceCollection) {
                    ResourceCollection resourceCollection = (ResourceCollection) reference;
                    if (resourceCollection.size() == 1) {
                        reference = resourceCollection.iterator().next();
                    }
                } else {
                    throw new BuildException("Illegal value at '" + this.refid + "': " + String.valueOf(reference));
                }
            }
            this.resource = (Resource) reference;
        } finally {
            this.refid = null;
        }
    }

    public void setSubstring(String str) {
        this.substring = str;
    }

    public void setCasesensitive(boolean z) {
        this.casesensitive = z;
    }

    private void validate() {
        Resource resource = this.resource;
        if (resource != null && this.refid != null) {
            throw new BuildException("Cannot set both resource and refid");
        }
        if (resource == null && this.refid != null) {
            resolveRefid();
        }
        if (this.resource == null || this.substring == null) {
            throw new BuildException("both resource and substring are required in <resourcecontains>");
        }
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public synchronized boolean eval() throws BuildException {
        BufferedReader bufferedReader;
        Throwable th;
        validate();
        if (this.substring.length() == 0) {
            if (getProject() != null) {
                getProject().log("Substring is empty; returning true", 3);
            }
            return true;
        }
        if (this.resource.getSize() == 0) {
            return false;
        }
        try {
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(this.resource.getInputStream()));
            } catch (Throwable th2) {
                bufferedReader = null;
                th = th2;
            }
        } catch (IOException unused) {
        }
        try {
            String strSafeReadFully = FileUtils.safeReadFully(bufferedReader);
            String lowerCase = this.substring;
            if (!this.casesensitive) {
                strSafeReadFully = strSafeReadFully.toLowerCase();
                lowerCase = lowerCase.toLowerCase();
            }
            boolean z = strSafeReadFully.indexOf(lowerCase) >= 0;
            FileUtils.close(bufferedReader);
            return z;
        } catch (IOException unused2) {
            throw new BuildException("There was a problem accessing resource : " + this.resource);
        } catch (Throwable th3) {
            th = th3;
            FileUtils.close(bufferedReader);
            throw th;
        }
    }
}
