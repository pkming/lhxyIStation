package org.apache.tools.ant.util;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.rmic.RmicAdapterFactory;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/* JADX INFO: loaded from: classes3.dex */
public class ClasspathUtils {
    public static final String REUSE_LOADER_REF = "ant.reuse.loader";

    public static ClassLoader getClassLoaderForPath(Project project, Reference reference) {
        return getClassLoaderForPath(project, reference, false);
    }

    public static ClassLoader getClassLoaderForPath(Project project, Reference reference, boolean z) {
        String refId = reference.getRefId();
        Object reference2 = project.getReference(refId);
        if (!(reference2 instanceof Path)) {
            throw new BuildException("The specified classpathref " + refId + " does not reference a Path.");
        }
        return getClassLoaderForPath(project, (Path) reference2, MagicNames.REFID_CLASSPATH_LOADER_PREFIX + refId, z);
    }

    public static ClassLoader getClassLoaderForPath(Project project, Path path, String str) {
        return getClassLoaderForPath(project, path, str, false);
    }

    public static ClassLoader getClassLoaderForPath(Project project, Path path, String str, boolean z) {
        return getClassLoaderForPath(project, path, str, z, isMagicPropertySet(project));
    }

    public static ClassLoader getClassLoaderForPath(Project project, Path path, String str, boolean z, boolean z2) {
        ClassLoader uniqueClassLoaderForPath;
        if (str == null || !z2) {
            uniqueClassLoaderForPath = null;
        } else {
            Object reference = project.getReference(str);
            if (reference != null && !(reference instanceof ClassLoader)) {
                throw new BuildException("The specified loader id " + str + " does not reference a class loader");
            }
            uniqueClassLoaderForPath = (ClassLoader) reference;
        }
        if (uniqueClassLoaderForPath == null) {
            uniqueClassLoaderForPath = getUniqueClassLoaderForPath(project, path, z);
            if (str != null && z2) {
                project.addReference(str, uniqueClassLoaderForPath);
            }
        }
        return uniqueClassLoaderForPath;
    }

    public static ClassLoader getUniqueClassLoaderForPath(Project project, Path path, boolean z) {
        AntClassLoader antClassLoaderCreateClassLoader = project.createClassLoader(path);
        if (z) {
            antClassLoaderCreateClassLoader.setParentFirst(false);
            antClassLoaderCreateClassLoader.addJavaLibraries();
        }
        return antClassLoaderCreateClassLoader;
    }

    public static Object newInstance(String str, ClassLoader classLoader) {
        return newInstance(str, classLoader, Object.class);
    }

    public static Object newInstance(String str, ClassLoader classLoader, Class cls) {
        try {
            Object objNewInstance = Class.forName(str, true, classLoader).newInstance();
            if (cls.isInstance(objNewInstance)) {
                return objNewInstance;
            }
            throw new BuildException(RmicAdapterFactory.ERROR_NOT_RMIC_ADAPTER + str + " expected :" + cls);
        } catch (ClassNotFoundException e) {
            throw new BuildException(RmicAdapterFactory.ERROR_UNKNOWN_COMPILER + str, e);
        } catch (IllegalAccessException e2) {
            throw new BuildException("Could not instantiate " + str + ". Specified class should have a public constructor.", e2);
        } catch (InstantiationException e3) {
            throw new BuildException("Could not instantiate " + str + ". Specified class should have a no argument constructor.", e3);
        } catch (LinkageError e4) {
            throw new BuildException("Class " + str + " could not be loaded because of an invalid dependency.", e4);
        }
    }

    public static Delegate getDelegate(ProjectComponent projectComponent) {
        return new Delegate(projectComponent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isMagicPropertySet(Project project) {
        return project.getProperty("ant.reuse.loader") != null;
    }

    public static class Delegate {
        private String className;
        private Path classpath;
        private String classpathId;
        private final ProjectComponent component;
        private String loaderId;
        private boolean reverseLoader = false;

        Delegate(ProjectComponent projectComponent) {
            this.component = projectComponent;
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
                this.classpath = new Path(this.component.getProject());
            }
            return this.classpath.createPath();
        }

        public void setClassname(String str) {
            this.className = str;
        }

        public void setClasspathref(Reference reference) {
            this.classpathId = reference.getRefId();
            createClasspath().setRefid(reference);
        }

        public void setReverseLoader(boolean z) {
            this.reverseLoader = z;
        }

        public void setLoaderRef(Reference reference) {
            this.loaderId = reference.getRefId();
        }

        public ClassLoader getClassLoader() {
            return ClasspathUtils.getClassLoaderForPath(getContextProject(), this.classpath, getClassLoadId(), this.reverseLoader, this.loaderId != null || ClasspathUtils.isMagicPropertySet(getContextProject()));
        }

        private Project getContextProject() {
            return this.component.getProject();
        }

        public String getClassLoadId() {
            String str = this.loaderId;
            return (str != null || this.classpathId == null) ? str : MagicNames.REFID_CLASSPATH_LOADER_PREFIX + this.classpathId;
        }

        public Object newInstance() {
            return ClasspathUtils.newInstance(this.className, getClassLoader());
        }

        public Path getClasspath() {
            return this.classpath;
        }

        public boolean isReverseLoader() {
            return this.reverseLoader;
        }
    }
}
