package org.apache.tools.ant.taskdefs.condition;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/* JADX INFO: loaded from: classes3.dex */
public class HasMethod extends ProjectComponent implements Condition {
    private String classname;
    private Path classpath;
    private String field;
    private boolean ignoreSystemClasses = false;
    private AntClassLoader loader;
    private String method;

    public void setClasspath(Path path) {
        createClasspath().append(path);
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

    public void setClassname(String str) {
        this.classname = str;
    }

    public void setMethod(String str) {
        this.method = str;
    }

    public void setField(String str) {
        this.field = str;
    }

    public void setIgnoreSystemClasses(boolean z) {
        this.ignoreSystemClasses = z;
    }

    private Class loadClass(String str) {
        try {
            if (this.ignoreSystemClasses) {
                AntClassLoader antClassLoaderCreateClassLoader = getProject().createClassLoader(this.classpath);
                this.loader = antClassLoaderCreateClassLoader;
                antClassLoaderCreateClassLoader.setParentFirst(false);
                this.loader.addJavaLibraries();
                try {
                    return this.loader.findClass(str);
                } catch (SecurityException e) {
                    throw new BuildException("class \"" + str + "\" was found but a SecurityException has been raised while loading it", e);
                }
            }
            AntClassLoader antClassLoader = this.loader;
            if (antClassLoader != null) {
                return antClassLoader.loadClass(str);
            }
            ClassLoader classLoader = getClass().getClassLoader();
            if (classLoader != null) {
                return Class.forName(str, true, classLoader);
            }
            return Class.forName(str);
        } catch (ClassNotFoundException unused) {
            throw new BuildException("class \"" + str + "\" was not found");
        } catch (NoClassDefFoundError e2) {
            throw new BuildException("Could not load dependent class \"" + e2.getMessage() + "\" for class \"" + str + "\"");
        }
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() throws BuildException {
        String str = this.classname;
        if (str == null) {
            throw new BuildException("No classname defined");
        }
        AntClassLoader antClassLoader = this.loader;
        try {
            Class clsLoadClass = loadClass(str);
            if (this.method != null) {
                return isMethodFound(clsLoadClass);
            }
            if (this.field != null) {
                boolean zIsFieldFound = isFieldFound(clsLoadClass);
                AntClassLoader antClassLoader2 = this.loader;
                if (antClassLoader != antClassLoader2 && antClassLoader2 != null) {
                    antClassLoader2.cleanup();
                    this.loader = null;
                }
                return zIsFieldFound;
            }
            throw new BuildException("Neither method nor field defined");
        } finally {
            AntClassLoader antClassLoader3 = this.loader;
            if (antClassLoader != antClassLoader3 && antClassLoader3 != null) {
                antClassLoader3.cleanup();
                this.loader = null;
            }
        }
    }

    private boolean isFieldFound(Class cls) {
        for (Field field : cls.getDeclaredFields()) {
            if (field.getName().equals(this.field)) {
                return true;
            }
        }
        return false;
    }

    private boolean isMethodFound(Class cls) {
        for (Method method : cls.getDeclaredMethods()) {
            if (method.getName().equals(this.method)) {
                return true;
            }
        }
        return false;
    }
}
