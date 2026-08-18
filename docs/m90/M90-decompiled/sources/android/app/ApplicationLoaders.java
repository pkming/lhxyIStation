package android.app;

import android.os.Trace;
import android.util.ArrayMap;
import dalvik.system.PathClassLoader;

/* JADX INFO: loaded from: classes.dex */
class ApplicationLoaders {
    private static final ApplicationLoaders gApplicationLoaders = new ApplicationLoaders();
    private final ArrayMap<String, ClassLoader> mLoaders = new ArrayMap<>();

    ApplicationLoaders() {
    }

    public static ApplicationLoaders getDefault() {
        return gApplicationLoaders;
    }

    public ClassLoader getClassLoader(String str, String str2, ClassLoader classLoader) {
        ClassLoader parent = ClassLoader.getSystemClassLoader().getParent();
        synchronized (this.mLoaders) {
            if (classLoader == null) {
                classLoader = parent;
            }
            if (classLoader == parent) {
                ClassLoader classLoader2 = this.mLoaders.get(str);
                if (classLoader2 != null) {
                    return classLoader2;
                }
                Trace.traceBegin(64L, str);
                PathClassLoader pathClassLoader = new PathClassLoader(str, str2, classLoader);
                Trace.traceEnd(64L);
                this.mLoaders.put(str, pathClassLoader);
                return pathClassLoader;
            }
            Trace.traceBegin(64L, str);
            PathClassLoader pathClassLoader2 = new PathClassLoader(str, classLoader);
            Trace.traceEnd(64L);
            return pathClassLoader2;
        }
    }
}
