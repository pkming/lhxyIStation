package org.apache.tools.ant.taskdefs.optional.javah;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.JavaEnvUtils;

/* JADX INFO: loaded from: classes3.dex */
public class JavahAdapterFactory {
    public static String getDefault() {
        return JavaEnvUtils.isKaffe() ? Kaffeh.IMPLEMENTATION_NAME : JavaEnvUtils.isGij() ? Gcjh.IMPLEMENTATION_NAME : "sun";
    }

    public static JavahAdapter getAdapter(String str, ProjectComponent projectComponent) throws BuildException {
        return getAdapter(str, projectComponent, null);
    }

    public static JavahAdapter getAdapter(String str, ProjectComponent projectComponent, Path path) throws BuildException {
        if ((JavaEnvUtils.isKaffe() && str == null) || Kaffeh.IMPLEMENTATION_NAME.equals(str)) {
            return new Kaffeh();
        }
        if ((JavaEnvUtils.isGij() && str == null) || Gcjh.IMPLEMENTATION_NAME.equals(str)) {
            return new Gcjh();
        }
        if ("sun".equals(str)) {
            return new SunJavah();
        }
        if (str != null) {
            return resolveClassName(str, projectComponent.getProject().createClassLoader(path));
        }
        return new SunJavah();
    }

    private static JavahAdapter resolveClassName(String str, ClassLoader classLoader) throws BuildException {
        if (classLoader == null) {
            classLoader = JavahAdapterFactory.class.getClassLoader();
        }
        return (JavahAdapter) ClasspathUtils.newInstance(str, classLoader, JavahAdapter.class);
    }
}
