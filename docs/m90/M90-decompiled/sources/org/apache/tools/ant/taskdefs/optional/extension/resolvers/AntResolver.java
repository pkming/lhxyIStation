package org.apache.tools.ant.taskdefs.optional.extension.resolvers;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.optional.extension.Extension;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionResolver;

/* JADX INFO: loaded from: classes3.dex */
public class AntResolver implements ExtensionResolver {
    private File antfile;
    private File destfile;
    private String target;

    public void setAntfile(File file) {
        this.antfile = file;
    }

    public void setDestfile(File file) {
        this.destfile = file;
    }

    public void setTarget(String str) {
        this.target = str;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.extension.ExtensionResolver
    public File resolve(Extension extension, Project project) throws BuildException {
        validate();
        Ant ant = new Ant();
        ant.setProject(project);
        ant.setInheritAll(false);
        ant.setAntfile(this.antfile.getName());
        try {
            ant.setDir(this.antfile.getParentFile().getCanonicalFile());
            String str = this.target;
            if (str != null) {
                ant.setTarget(str);
            }
            ant.execute();
            return this.destfile;
        } catch (IOException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    private void validate() {
        if (this.antfile == null) {
            throw new BuildException("Must specify Buildfile");
        }
        if (this.destfile == null) {
            throw new BuildException("Must specify destination file");
        }
    }

    public String toString() {
        return "Ant[" + this.antfile + "==>" + this.destfile + "]";
    }
}
