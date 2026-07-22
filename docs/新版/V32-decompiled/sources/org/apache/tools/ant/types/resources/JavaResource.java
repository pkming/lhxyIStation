package org.apache.tools.ant.types.resources;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.AbstractClasspathResource;

/* JADX INFO: loaded from: classes3.dex */
public class JavaResource extends AbstractClasspathResource implements URLProvider {
    public JavaResource() {
    }

    public JavaResource(String str, Path path) {
        setName(str);
        setClasspath(path);
    }

    @Override // org.apache.tools.ant.types.resources.AbstractClasspathResource
    protected InputStream openInputStream(ClassLoader classLoader) throws IOException {
        if (classLoader == null) {
            InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream(getName());
            if (systemResourceAsStream != null) {
                return systemResourceAsStream;
            }
            throw new FileNotFoundException("No resource " + getName() + " on Ant's classpath");
        }
        InputStream resourceAsStream = classLoader.getResourceAsStream(getName());
        if (resourceAsStream != null) {
            return resourceAsStream;
        }
        throw new FileNotFoundException("No resource " + getName() + " on the classpath " + classLoader);
    }

    @Override // org.apache.tools.ant.types.resources.URLProvider
    public URL getURL() {
        if (isReference()) {
            return ((JavaResource) getCheckedRef()).getURL();
        }
        AbstractClasspathResource.ClassLoaderWithFlag classLoader = getClassLoader();
        if (classLoader.getLoader() == null) {
            return ClassLoader.getSystemResource(getName());
        }
        try {
            return classLoader.getLoader().getResource(getName());
        } finally {
            classLoader.cleanup();
        }
    }

    @Override // org.apache.tools.ant.types.Resource, java.lang.Comparable
    public int compareTo(Resource resource) {
        if (isReference()) {
            return ((Resource) getCheckedRef()).compareTo(resource);
        }
        if (resource.getClass().equals(getClass())) {
            JavaResource javaResource = (JavaResource) resource;
            if (!getName().equals(javaResource.getName())) {
                return getName().compareTo(javaResource.getName());
            }
            if (getLoader() != javaResource.getLoader()) {
                if (getLoader() == null) {
                    return -1;
                }
                if (javaResource.getLoader() == null) {
                    return 1;
                }
                return getLoader().getRefId().compareTo(javaResource.getLoader().getRefId());
            }
            Path classpath = getClasspath();
            Path classpath2 = javaResource.getClasspath();
            if (classpath == classpath2) {
                return 0;
            }
            if (classpath == null) {
                return -1;
            }
            if (classpath2 == null) {
                return 1;
            }
            return classpath.toString().compareTo(classpath2.toString());
        }
        return super.compareTo(resource);
    }
}
