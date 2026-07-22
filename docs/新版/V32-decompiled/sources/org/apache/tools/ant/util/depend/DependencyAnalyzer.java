package org.apache.tools.ant.util.depend;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import org.apache.tools.ant.types.Path;

/* JADX INFO: loaded from: classes3.dex */
public interface DependencyAnalyzer {
    void addClassPath(Path path);

    void addRootClass(String str);

    void addSourcePath(Path path);

    void config(String str, Object obj);

    File getClassContainer(String str) throws IOException;

    Enumeration<String> getClassDependencies();

    Enumeration<File> getFileDependencies();

    File getSourceContainer(String str) throws IOException;

    void reset();

    void setClosure(boolean z);
}
