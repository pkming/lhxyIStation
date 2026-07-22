package org.apache.tools.ant.util.depend;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipFile;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.VectorSet;

/* JADX INFO: loaded from: classes3.dex */
public abstract class AbstractAnalyzer implements DependencyAnalyzer {
    public static final int MAX_LOOPS = 1000;
    private Vector<String> classDependencies;
    private Vector<File> fileDependencies;
    private Path sourcePath = new Path(null);
    private Path classPath = new Path(null);
    private final Vector<String> rootClasses = new VectorSet();
    private boolean determined = false;
    private boolean closure = true;

    @Override // org.apache.tools.ant.util.depend.DependencyAnalyzer
    public void config(String str, Object obj) {
    }

    protected abstract void determineDependencies(Vector<File> vector, Vector<String> vector2);

    protected abstract boolean supportsFileDependencies();

    protected AbstractAnalyzer() {
        reset();
    }

    @Override // org.apache.tools.ant.util.depend.DependencyAnalyzer
    public void setClosure(boolean z) {
        this.closure = z;
    }

    @Override // org.apache.tools.ant.util.depend.DependencyAnalyzer
    public Enumeration<File> getFileDependencies() {
        if (!supportsFileDependencies()) {
            throw new RuntimeException("File dependencies are not supported by this analyzer");
        }
        if (!this.determined) {
            determineDependencies(this.fileDependencies, this.classDependencies);
        }
        return this.fileDependencies.elements();
    }

    @Override // org.apache.tools.ant.util.depend.DependencyAnalyzer
    public Enumeration<String> getClassDependencies() {
        if (!this.determined) {
            determineDependencies(this.fileDependencies, this.classDependencies);
        }
        return this.classDependencies.elements();
    }

    @Override // org.apache.tools.ant.util.depend.DependencyAnalyzer
    public File getClassContainer(String str) throws IOException {
        return getResourceContainer(str.replace('.', '/') + ".class", this.classPath.list());
    }

    @Override // org.apache.tools.ant.util.depend.DependencyAnalyzer
    public File getSourceContainer(String str) throws IOException {
        return getResourceContainer(str.replace('.', '/') + ".java", this.sourcePath.list());
    }

    @Override // org.apache.tools.ant.util.depend.DependencyAnalyzer
    public void addSourcePath(Path path) {
        if (path == null) {
            return;
        }
        this.sourcePath.append(path);
        this.sourcePath.setProject(path.getProject());
    }

    @Override // org.apache.tools.ant.util.depend.DependencyAnalyzer
    public void addClassPath(Path path) {
        if (path == null) {
            return;
        }
        this.classPath.append(path);
        this.classPath.setProject(path.getProject());
    }

    @Override // org.apache.tools.ant.util.depend.DependencyAnalyzer
    public void addRootClass(String str) {
        if (str == null || this.rootClasses.contains(str)) {
            return;
        }
        this.rootClasses.addElement(str);
    }

    @Override // org.apache.tools.ant.util.depend.DependencyAnalyzer
    public void reset() {
        this.rootClasses.removeAllElements();
        this.determined = false;
        this.fileDependencies = new Vector<>();
        this.classDependencies = new Vector<>();
    }

    protected Enumeration<String> getRootClasses() {
        return this.rootClasses.elements();
    }

    protected boolean isClosureRequired() {
        return this.closure;
    }

    private File getResourceContainer(String str, String[] strArr) throws Throwable {
        int i = 0;
        while (true) {
            ZipFile zipFile = null;
            if (i >= strArr.length) {
                return null;
            }
            File file = new File(strArr[i]);
            if (file.exists()) {
                if (file.isDirectory()) {
                    File file2 = new File(file, str);
                    if (file2.exists()) {
                        return file2;
                    }
                } else {
                    try {
                        ZipFile zipFile2 = new ZipFile(file);
                        try {
                            if (zipFile2.getEntry(str) != null) {
                                zipFile2.close();
                                return file;
                            }
                            zipFile2.close();
                        } catch (Throwable th) {
                            th = th;
                            zipFile = zipFile2;
                            if (zipFile != null) {
                                zipFile.close();
                            }
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                }
            }
            i++;
        }
    }
}
