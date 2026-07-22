package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.ClasspathUtils;

/* JADX INFO: loaded from: classes3.dex */
public abstract class DefBase extends AntlibDefinition {
    private ClasspathUtils.Delegate cpDelegate;
    private ClassLoader createdLoader;

    protected boolean hasCpDelegate() {
        return this.cpDelegate != null;
    }

    public void setReverseLoader(boolean z) {
        getDelegate().setReverseLoader(z);
        log("The reverseloader attribute is DEPRECATED. It will be removed", 1);
    }

    public Path getClasspath() {
        return getDelegate().getClasspath();
    }

    public boolean isReverseLoader() {
        return getDelegate().isReverseLoader();
    }

    public String getLoaderId() {
        return getDelegate().getClassLoadId();
    }

    public String getClasspathId() {
        return getDelegate().getClassLoadId();
    }

    public void setClasspath(Path path) {
        getDelegate().setClasspath(path);
    }

    public Path createClasspath() {
        return getDelegate().createClasspath();
    }

    public void setClasspathRef(Reference reference) {
        getDelegate().setClasspathref(reference);
    }

    public void setLoaderRef(Reference reference) {
        getDelegate().setLoaderRef(reference);
    }

    protected ClassLoader createLoader() {
        if (getAntlibClassLoader() != null && this.cpDelegate == null) {
            return getAntlibClassLoader();
        }
        if (this.createdLoader == null) {
            ClassLoader classLoader = getDelegate().getClassLoader();
            this.createdLoader = classLoader;
            ((AntClassLoader) classLoader).addSystemPackageRoot("org.apache.tools.ant");
        }
        return this.createdLoader;
    }

    @Override // org.apache.tools.ant.Task
    public void init() throws BuildException {
        super.init();
    }

    private ClasspathUtils.Delegate getDelegate() {
        if (this.cpDelegate == null) {
            this.cpDelegate = ClasspathUtils.getDelegate(this);
        }
        return this.cpDelegate;
    }
}
