package org.apache.tools.ant.util;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.launch.Locator;

/* JADX INFO: loaded from: classes3.dex */
public class LoaderUtils {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();

    public static boolean isContextLoaderAvailable() {
        return true;
    }

    public static void setContextClassLoader(ClassLoader classLoader) {
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    public static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private static File normalizeSource(File file) {
        if (file == null) {
            return file;
        }
        try {
            return FILE_UTILS.normalize(file.getAbsolutePath());
        } catch (BuildException unused) {
            return file;
        }
    }

    public static File getClassSource(Class cls) {
        return normalizeSource(Locator.getClassSource(cls));
    }

    public static File getResourceSource(ClassLoader classLoader, String str) {
        if (classLoader == null) {
            classLoader = LoaderUtils.class.getClassLoader();
        }
        return normalizeSource(Locator.getResourceSource(classLoader, str));
    }

    public static String classNameToResource(String str) {
        return str.replace('.', '/') + ".class";
    }

    public static boolean classExists(ClassLoader classLoader, String str) {
        return classLoader.getResource(classNameToResource(str)) != null;
    }
}
