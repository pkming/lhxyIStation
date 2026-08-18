package org.apache.tools.ant.loader;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

/* JADX INFO: loaded from: classes3.dex */
public class AntClassLoader5 extends AntClassLoader implements Closeable {
    public AntClassLoader5(ClassLoader classLoader, Project project, Path path, boolean z) {
        super(classLoader, project, path, z);
    }

    @Override // java.lang.ClassLoader
    public Enumeration<URL> getResources(String str) throws IOException {
        return getNamedResources(str);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        cleanup();
    }
}
