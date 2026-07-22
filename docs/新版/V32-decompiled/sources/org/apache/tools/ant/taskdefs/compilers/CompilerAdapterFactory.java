package org.apache.tools.ant.taskdefs.compilers;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.JavaEnvUtils;

/* JADX INFO: loaded from: classes3.dex */
public final class CompilerAdapterFactory {
    private static final String MODERN_COMPILER = "com.sun.tools.javac.Main";

    private CompilerAdapterFactory() {
    }

    public static CompilerAdapter getCompiler(String str, Task task) throws BuildException {
        return getCompiler(str, task, null);
    }

    public static CompilerAdapter getCompiler(String str, Task task, Path path) throws BuildException {
        if (str.equalsIgnoreCase("jikes")) {
            return new Jikes();
        }
        if (str.equalsIgnoreCase("extjavac")) {
            return new JavacExternal();
        }
        if (str.equalsIgnoreCase("classic") || str.equalsIgnoreCase("javac1.1") || str.equalsIgnoreCase("javac1.2")) {
            task.log("This version of java does not support the classic compiler; upgrading to modern", 1);
            str = "modern";
        }
        if (str.equalsIgnoreCase("modern") || str.equalsIgnoreCase("javac1.3") || str.equalsIgnoreCase("javac1.4") || str.equalsIgnoreCase("javac1.5") || str.equalsIgnoreCase("javac1.6") || str.equalsIgnoreCase("javac1.7") || str.equalsIgnoreCase("javac1.8") || str.equalsIgnoreCase("javac1.9")) {
            if (doesModernCompilerExist()) {
                return new Javac13();
            }
            throw new BuildException("Unable to find a javac compiler;\ncom.sun.tools.javac.Main is not on the classpath.\nPerhaps JAVA_HOME does not point to the JDK.\nIt is currently set to \"" + JavaEnvUtils.getJavaHome() + "\"");
        }
        if (str.equalsIgnoreCase("jvc") || str.equalsIgnoreCase("microsoft")) {
            return new Jvc();
        }
        if (str.equalsIgnoreCase("kjc")) {
            return new Kjc();
        }
        if (str.equalsIgnoreCase("gcj")) {
            return new Gcj();
        }
        if (str.equalsIgnoreCase("sj") || str.equalsIgnoreCase("symantec")) {
            return new Sj();
        }
        return resolveClassName(str, task.getProject().createClassLoader(path));
    }

    private static boolean doesModernCompilerExist() {
        try {
            try {
                Class.forName(MODERN_COMPILER);
                return true;
            } catch (ClassNotFoundException unused) {
                ClassLoader classLoader = CompilerAdapterFactory.class.getClassLoader();
                if (classLoader == null) {
                    return false;
                }
                classLoader.loadClass(MODERN_COMPILER);
                return true;
            }
        } catch (ClassNotFoundException unused2) {
            return false;
        }
    }

    private static CompilerAdapter resolveClassName(String str, ClassLoader classLoader) throws BuildException {
        if (classLoader == null) {
            classLoader = CompilerAdapterFactory.class.getClassLoader();
        }
        return (CompilerAdapter) ClasspathUtils.newInstance(str, classLoader, CompilerAdapter.class);
    }
}
