package org.apache.tools.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/* JADX INFO: loaded from: classes3.dex */
public class Classloader extends Task {
    public static final String SYSTEM_LOADER_REF = "ant.coreLoader";
    private Path classpath;
    private String name = null;
    private boolean reset = false;
    private boolean parentFirst = true;
    private String parentName = null;

    public void setName(String str) {
        this.name = str;
    }

    public void setReset(boolean z) {
        this.reset = z;
    }

    public void setReverse(boolean z) {
        this.parentFirst = !z;
    }

    public void setParentFirst(boolean z) {
        this.parentFirst = z;
    }

    public void setParentName(String str) {
        this.parentName = str;
    }

    public void setClasspathRef(Reference reference) throws BuildException {
        this.classpath = (Path) reference.getReferencedObject(getProject());
    }

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
            this.classpath = new Path(null);
        }
        return this.classpath.createPath();
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws Throwable {
        Path path;
        String str;
        try {
            String str2 = "ant.coreLoader";
            if ("only".equals(getProject().getProperty(MagicNames.BUILD_SYSCLASSPATH)) && ((str = this.name) == null || "ant.coreLoader".equals(str))) {
                log("Changing the system loader is disabled by build.sysclasspath=only", 1);
                return;
            }
            String str3 = this.name;
            if (str3 != null) {
                str2 = str3;
            }
            Object reference = getProject().getReference(str2);
            Object classLoader = null;
            if (this.reset) {
                reference = null;
            }
            if (reference != null && !(reference instanceof AntClassLoader)) {
                log("Referenced object is not an AntClassLoader", 0);
                return;
            }
            AntClassLoader antClassLoaderNewAntClassLoader = (AntClassLoader) reference;
            boolean z = antClassLoaderNewAntClassLoader != null;
            if (antClassLoaderNewAntClassLoader == null) {
                if (this.parentName != null) {
                    Object reference2 = getProject().getReference(this.parentName);
                    if (reference2 instanceof ClassLoader) {
                        classLoader = reference2;
                    }
                }
                if (classLoader == null) {
                    classLoader = getClass().getClassLoader();
                }
                getProject().log("Setting parent loader " + this.name + " " + classLoader + " " + this.parentFirst, 4);
                antClassLoaderNewAntClassLoader = AntClassLoader.newAntClassLoader((ClassLoader) classLoader, getProject(), this.classpath, this.parentFirst);
                getProject().addReference(str2, antClassLoaderNewAntClassLoader);
                if (this.name == null) {
                    antClassLoaderNewAntClassLoader.addLoaderPackageRoot("org.apache.tools.ant.taskdefs.optional");
                    getProject().setCoreLoader(antClassLoaderNewAntClassLoader);
                }
            }
            if (!z || (path = this.classpath) == null) {
                return;
            }
            for (String str4 : path.list()) {
                File file = new File(str4);
                if (file.exists()) {
                    log("Adding to class loader " + antClassLoaderNewAntClassLoader + " " + file.getAbsolutePath(), 4);
                    antClassLoaderNewAntClassLoader.addPathElement(file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
