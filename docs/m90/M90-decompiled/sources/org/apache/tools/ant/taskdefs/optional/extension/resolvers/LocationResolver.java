package org.apache.tools.ant.taskdefs.optional.extension.resolvers;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.extension.Extension;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionResolver;

/* JADX INFO: loaded from: classes3.dex */
public class LocationResolver implements ExtensionResolver {
    private String location;

    public void setLocation(String str) {
        this.location = str;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.extension.ExtensionResolver
    public File resolve(Extension extension, Project project) throws BuildException {
        String str = this.location;
        if (str == null) {
            throw new BuildException("No location specified for resolver");
        }
        return project.resolveFile(str);
    }

    public String toString() {
        return "Location[" + this.location + "]";
    }
}
