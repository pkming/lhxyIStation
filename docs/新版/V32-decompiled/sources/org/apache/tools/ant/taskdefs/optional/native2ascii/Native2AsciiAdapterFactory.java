package org.apache.tools.ant.taskdefs.optional.native2ascii;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.JavaEnvUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Native2AsciiAdapterFactory {
    public static String getDefault() {
        return (JavaEnvUtils.isKaffe() || JavaEnvUtils.isClasspathBased()) ? "kaffe" : "sun";
    }

    public static Native2AsciiAdapter getAdapter(String str, ProjectComponent projectComponent) throws BuildException {
        return getAdapter(str, projectComponent, null);
    }

    public static Native2AsciiAdapter getAdapter(String str, ProjectComponent projectComponent, Path path) throws BuildException {
        if (((JavaEnvUtils.isKaffe() || JavaEnvUtils.isClasspathBased()) && str == null) || "kaffe".equals(str)) {
            return new KaffeNative2Ascii();
        }
        if ("sun".equals(str)) {
            return new SunNative2Ascii();
        }
        if (str != null) {
            return resolveClassName(str, projectComponent.getProject().createClassLoader(path));
        }
        return new SunNative2Ascii();
    }

    private static Native2AsciiAdapter resolveClassName(String str, ClassLoader classLoader) throws BuildException {
        if (classLoader == null) {
            classLoader = Native2AsciiAdapterFactory.class.getClassLoader();
        }
        return (Native2AsciiAdapter) ClasspathUtils.newInstance(str, classLoader, Native2AsciiAdapter.class);
    }
}
