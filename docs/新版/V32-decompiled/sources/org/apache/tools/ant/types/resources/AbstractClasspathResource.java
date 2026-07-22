package org.apache.tools.ant.types.resources;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Definer;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public abstract class AbstractClasspathResource extends Resource {
    private Path classpath;
    private Reference loader;
    private boolean parentFirst = true;

    protected abstract InputStream openInputStream(ClassLoader classLoader) throws IOException;

    public void setClasspath(Path path) {
        checkAttributesAllowed();
        Path path2 = this.classpath;
        if (path2 == null) {
            this.classpath = path;
        } else {
            path2.append(path);
        }
        setChecked(false);
    }

    public Path createClasspath() {
        checkChildrenAllowed();
        if (this.classpath == null) {
            this.classpath = new Path(getProject());
        }
        setChecked(false);
        return this.classpath.createPath();
    }

    public void setClasspathRef(Reference reference) {
        checkAttributesAllowed();
        createClasspath().setRefid(reference);
    }

    public Path getClasspath() {
        if (isReference()) {
            return ((AbstractClasspathResource) getCheckedRef()).getClasspath();
        }
        dieOnCircularReference();
        return this.classpath;
    }

    public Reference getLoader() {
        if (isReference()) {
            return ((AbstractClasspathResource) getCheckedRef()).getLoader();
        }
        dieOnCircularReference();
        return this.loader;
    }

    public void setLoaderRef(Reference reference) {
        checkAttributesAllowed();
        this.loader = reference;
    }

    public void setParentFirst(boolean z) {
        this.parentFirst = z;
    }

    @Override // org.apache.tools.ant.types.Resource, org.apache.tools.ant.types.DataType
    public void setRefid(Reference reference) {
        if (this.loader != null || this.classpath != null) {
            throw tooManyAttributes();
        }
        super.setRefid(reference);
    }

    @Override // org.apache.tools.ant.types.Resource
    public boolean isExists() {
        if (isReference()) {
            return ((Resource) getCheckedRef()).isExists();
        }
        dieOnCircularReference();
        try {
            InputStream inputStream = getInputStream();
            boolean z = inputStream != null;
            FileUtils.close(inputStream);
            return z;
        } catch (IOException unused) {
            FileUtils.close((InputStream) null);
            return false;
        } catch (Throwable th) {
            FileUtils.close((InputStream) null);
            throw th;
        }
    }

    @Override // org.apache.tools.ant.types.Resource
    public InputStream getInputStream() throws IOException {
        if (isReference()) {
            return ((Resource) getCheckedRef()).getInputStream();
        }
        dieOnCircularReference();
        final ClassLoaderWithFlag classLoader = getClassLoader();
        return !classLoader.needsCleanup() ? openInputStream(classLoader.getLoader()) : new FilterInputStream(openInputStream(classLoader.getLoader())) { // from class: org.apache.tools.ant.types.resources.AbstractClasspathResource.1
            @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
            public void close() throws IOException {
                FileUtils.close(this.in);
                classLoader.cleanup();
            }

            protected void finalize() throws Throwable {
                try {
                    close();
                } finally {
                    super.finalize();
                }
            }
        };
    }

    protected ClassLoaderWithFlag getClassLoader() {
        Reference reference = this.loader;
        ClassLoader classLoader = reference != null ? (ClassLoader) reference.getReferencedObject() : null;
        boolean z = false;
        if (classLoader == null) {
            if (getClasspath() != null) {
                Path pathConcatSystemClasspath = getClasspath().concatSystemClasspath(Definer.OnError.POLICY_IGNORE);
                if (this.parentFirst) {
                    classLoader = getProject().createClassLoader(pathConcatSystemClasspath);
                } else {
                    classLoader = AntClassLoader.newAntClassLoader(getProject().getCoreLoader(), getProject(), pathConcatSystemClasspath, false);
                }
                if (this.loader == null) {
                    z = true;
                }
            } else {
                classLoader = JavaResource.class.getClassLoader();
            }
            if (this.loader != null && classLoader != null) {
                getProject().addReference(this.loader.getRefId(), classLoader);
            }
        }
        return new ClassLoaderWithFlag(classLoader, z);
    }

    @Override // org.apache.tools.ant.types.DataType
    protected synchronized void dieOnCircularReference(Stack<Object> stack, Project project) {
        if (isChecked()) {
            return;
        }
        if (isReference()) {
            super.dieOnCircularReference(stack, project);
        } else {
            Path path = this.classpath;
            if (path != null) {
                pushAndInvokeCircularReferenceCheck(path, stack, project);
            }
            setChecked(true);
        }
    }

    public static class ClassLoaderWithFlag {
        private final boolean cleanup;
        private final ClassLoader loader;

        ClassLoaderWithFlag(ClassLoader classLoader, boolean z) {
            this.loader = classLoader;
            this.cleanup = z && (classLoader instanceof AntClassLoader);
        }

        public ClassLoader getLoader() {
            return this.loader;
        }

        public boolean needsCleanup() {
            return this.cleanup;
        }

        public void cleanup() {
            if (this.cleanup) {
                ((AntClassLoader) this.loader).cleanup();
            }
        }
    }
}
