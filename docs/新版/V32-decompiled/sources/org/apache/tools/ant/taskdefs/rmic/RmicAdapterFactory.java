package org.apache.tools.ant.taskdefs.rmic;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.ClasspathUtils;

/* JADX INFO: loaded from: classes3.dex */
public final class RmicAdapterFactory {
    public static final String DEFAULT_COMPILER = "default";
    public static final String ERROR_NOT_RMIC_ADAPTER = "Class of unexpected Type: ";
    public static final String ERROR_UNKNOWN_COMPILER = "Class not found: ";

    private RmicAdapterFactory() {
    }

    public static RmicAdapter getRmic(String str, Task task) throws BuildException {
        return getRmic(str, task, null);
    }

    public static RmicAdapter getRmic(String str, Task task, Path path) throws BuildException {
        if ("default".equalsIgnoreCase(str) || str.length() == 0) {
            str = KaffeRmic.isAvailable() ? "kaffe" : "sun";
        }
        if ("sun".equalsIgnoreCase(str)) {
            return new SunRmic();
        }
        if ("kaffe".equalsIgnoreCase(str)) {
            return new KaffeRmic();
        }
        if (WLRmic.COMPILER_NAME.equalsIgnoreCase(str)) {
            return new WLRmic();
        }
        if (ForkingSunRmic.COMPILER_NAME.equalsIgnoreCase(str)) {
            return new ForkingSunRmic();
        }
        if (XNewRmic.COMPILER_NAME.equalsIgnoreCase(str)) {
            return new XNewRmic();
        }
        return resolveClassName(str, task.getProject().createClassLoader(path));
    }

    private static RmicAdapter resolveClassName(String str, ClassLoader classLoader) throws BuildException {
        if (classLoader == null) {
            classLoader = RmicAdapterFactory.class.getClassLoader();
        }
        return (RmicAdapter) ClasspathUtils.newInstance(str, classLoader, RmicAdapter.class);
    }
}
