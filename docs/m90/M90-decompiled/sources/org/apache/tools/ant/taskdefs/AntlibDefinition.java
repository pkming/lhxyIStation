package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Task;

/* JADX INFO: loaded from: classes3.dex */
public class AntlibDefinition extends Task {
    private ClassLoader antlibClassLoader;
    private String uri = "";

    public void setURI(String str) throws BuildException {
        if (str.equals(ProjectHelper.ANT_CORE_URI)) {
            str = "";
        }
        if (str.startsWith("ant:")) {
            throw new BuildException("Attempt to use a reserved URI " + str);
        }
        this.uri = str;
    }

    public String getURI() {
        return this.uri;
    }

    public void setAntlibClassLoader(ClassLoader classLoader) {
        this.antlibClassLoader = classLoader;
    }

    public ClassLoader getAntlibClassLoader() {
        return this.antlibClassLoader;
    }
}
