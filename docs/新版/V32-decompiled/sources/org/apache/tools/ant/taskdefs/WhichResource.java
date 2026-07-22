package org.apache.tools.ant.taskdefs;

import java.net.URL;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Definer;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/* JADX INFO: loaded from: classes3.dex */
public class WhichResource extends Task {
    private String classname;
    private Path classpath;
    private String property;
    private String resource;

    public void setClasspath(Path path) {
        Path path2 = this.classpath;
        if (path2 == null) {
            this.classpath = path;
        } else {
            path2.append(path);
        }
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(getProject());
        }
        return this.classpath.createPath();
    }

    public void setClasspathRef(Reference reference) {
        createClasspath().setRefid(reference);
    }

    private void validate() {
        int i = this.classname != null ? 1 : 0;
        if (this.resource != null) {
            i++;
        }
        if (i == 0) {
            throw new BuildException("One of classname or resource must be specified");
        }
        if (i > 1) {
            throw new BuildException("Only one of classname or resource can be specified");
        }
        if (this.property == null) {
            throw new BuildException(MakeUrl.ERROR_NO_PROPERTY);
        }
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        validate();
        Path path = this.classpath;
        if (path != null) {
            this.classpath = path.concatSystemClasspath(Definer.OnError.POLICY_IGNORE);
            getProject().log("using user supplied classpath: " + this.classpath, 4);
        } else {
            Path path2 = new Path(getProject());
            this.classpath = path2;
            this.classpath = path2.concatSystemClasspath("only");
            getProject().log("using system classpath: " + this.classpath, 4);
        }
        AntClassLoader antClassLoader = null;
        try {
            AntClassLoader antClassLoaderNewAntClassLoader = AntClassLoader.newAntClassLoader(getProject().getCoreLoader(), getProject(), this.classpath, false);
            if (this.classname != null) {
                this.resource = this.classname.replace('.', '/') + ".class";
            }
            String str = this.resource;
            if (str == null) {
                throw new BuildException("One of class or resource is required");
            }
            if (str.startsWith("/")) {
                this.resource = this.resource.substring(1);
            }
            log("Searching for " + this.resource, 3);
            URL resource = antClassLoaderNewAntClassLoader.getResource(this.resource);
            if (resource != null) {
                getProject().setNewProperty(this.property, resource.toExternalForm());
            }
            if (antClassLoaderNewAntClassLoader != null) {
                antClassLoaderNewAntClassLoader.cleanup();
            }
        } catch (Throwable th) {
            if (0 != 0) {
                antClassLoader.cleanup();
            }
            throw th;
        }
    }

    public void setResource(String str) {
        this.resource = str;
    }

    public void setClass(String str) {
        this.classname = str;
    }

    public void setProperty(String str) {
        this.property = str;
    }
}
