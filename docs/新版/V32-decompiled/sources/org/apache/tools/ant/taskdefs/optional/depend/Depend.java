package org.apache.tools.ant.taskdefs.optional.depend;

import android.net.LinkQualityInfo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.taskdefs.rmic.DefaultRmicAdapter;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Depend extends MatchingTask {
    private static final String CACHE_FILE_NAME = "dependencies.txt";
    private static final String CLASSNAME_PREPEND = "||:";
    private static final int ONE_SECOND = 1000;
    private Hashtable affectedClassMap;
    private File cache;
    private Hashtable classFileInfoMap;
    private Hashtable classpathDependencies;
    private Path dependClasspath;
    private Path destPath;
    private Hashtable outOfDateClasses;
    private Path srcPath;
    private String[] srcPathList;
    private boolean closure = false;
    private boolean warnOnRmiStubs = true;
    private boolean dump = false;

    private static class ClassFileInfo {
        private File absoluteFile;
        private String className;
        private boolean isUserWarned;
        private File sourceFile;

        private ClassFileInfo() {
            this.isUserWarned = false;
        }
    }

    public void setClasspath(Path path) {
        Path path2 = this.dependClasspath;
        if (path2 == null) {
            this.dependClasspath = path;
        } else {
            path2.append(path);
        }
    }

    public Path getClasspath() {
        return this.dependClasspath;
    }

    public Path createClasspath() {
        if (this.dependClasspath == null) {
            this.dependClasspath = new Path(getProject());
        }
        return this.dependClasspath.createPath();
    }

    public void setClasspathRef(Reference reference) {
        createClasspath().setRefid(reference);
    }

    public void setWarnOnRmiStubs(boolean z) {
        this.warnOnRmiStubs = z;
    }

    private Hashtable readCachedDependencies(File file) throws Throwable {
        Hashtable hashtable = new Hashtable();
        BufferedReader bufferedReader = null;
        Vector vector = null;
        try {
            BufferedReader bufferedReader2 = new BufferedReader(new FileReader(file));
            while (true) {
                try {
                    String line = bufferedReader2.readLine();
                    if (line != null) {
                        if (line.startsWith(CLASSNAME_PREPEND)) {
                            vector = new Vector();
                            hashtable.put(line.substring(3), vector);
                        } else {
                            vector.addElement(line);
                        }
                    } else {
                        FileUtils.close(bufferedReader2);
                        return hashtable;
                    }
                } catch (Throwable th) {
                    th = th;
                    bufferedReader = bufferedReader2;
                    FileUtils.close(bufferedReader);
                    throw th;
                }
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private void writeCachedDependencies(Hashtable hashtable) throws Throwable {
        File file = this.cache;
        if (file == null) {
            return;
        }
        BufferedWriter bufferedWriter = null;
        try {
            file.mkdirs();
            BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(new File(this.cache, CACHE_FILE_NAME)));
            try {
                Enumeration enumerationKeys = hashtable.keys();
                while (enumerationKeys.hasMoreElements()) {
                    String str = (String) enumerationKeys.nextElement();
                    bufferedWriter2.write(CLASSNAME_PREPEND + str);
                    bufferedWriter2.newLine();
                    Vector vector = (Vector) hashtable.get(str);
                    int size = vector.size();
                    for (int i = 0; i < size; i++) {
                        bufferedWriter2.write(String.valueOf(vector.elementAt(i)));
                        bufferedWriter2.newLine();
                    }
                }
                FileUtils.close(bufferedWriter2);
            } catch (Throwable th) {
                th = th;
                bufferedWriter = bufferedWriter2;
                FileUtils.close(bufferedWriter);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private Path getCheckClassPath() {
        if (this.dependClasspath == null) {
            return null;
        }
        String[] list = this.destPath.list();
        String str = "";
        for (String str2 : this.dependClasspath.list()) {
            boolean zEquals = false;
            for (int i = 0; i < list.length && !zEquals; i++) {
                zEquals = list[i].equals(str2);
            }
            if (!zEquals) {
                str = str.length() == 0 ? str2 : str + ":" + str2;
            }
        }
        Path path = str.length() > 0 ? new Path(getProject(), str) : null;
        log("Classpath without dest dir is " + path, 4);
        return path;
    }

    private void determineDependencies() throws Throwable {
        long jLastModified;
        boolean zExists;
        AntClassLoader antClassLoaderCreateClassLoader;
        int i;
        Object file;
        this.affectedClassMap = new Hashtable();
        this.classFileInfoMap = new Hashtable();
        Hashtable hashtable = new Hashtable();
        if (this.cache != null) {
            File file2 = new File(this.cache, CACHE_FILE_NAME);
            zExists = file2.exists();
            jLastModified = file2.lastModified();
            if (zExists) {
                hashtable = readCachedDependencies(file2);
            }
        } else {
            jLastModified = LinkQualityInfo.UNKNOWN_LONG;
            zExists = true;
        }
        Enumeration enumerationElements = getClassFiles(this.destPath).elements();
        boolean z = false;
        while (true) {
            antClassLoaderCreateClassLoader = null;
            vector = null;
            vector = null;
            Vector vector = null;
            i = 4;
            if (!enumerationElements.hasMoreElements()) {
                break;
            }
            ClassFileInfo classFileInfo = (ClassFileInfo) enumerationElements.nextElement();
            log("Adding class info for " + classFileInfo.className, 4);
            this.classFileInfoMap.put(classFileInfo.className, classFileInfo);
            if (this.cache != null && zExists && jLastModified > classFileInfo.absoluteFile.lastModified()) {
                vector = (Vector) hashtable.get(classFileInfo.className);
            }
            if (vector == null) {
                AntAnalyzer antAnalyzer = new AntAnalyzer();
                antAnalyzer.addRootClass(classFileInfo.className);
                antAnalyzer.addClassPath(this.destPath);
                antAnalyzer.setClosure(false);
                vector = new Vector();
                Enumeration<String> classDependencies = antAnalyzer.getClassDependencies();
                while (classDependencies.hasMoreElements()) {
                    String strNextElement = classDependencies.nextElement();
                    vector.addElement(strNextElement);
                    log("Class " + classFileInfo.className + " depends on " + ((Object) strNextElement), 4);
                }
                hashtable.put(classFileInfo.className, vector);
                z = true;
            }
            Enumeration enumerationElements2 = vector.elements();
            while (enumerationElements2.hasMoreElements()) {
                String str = (String) enumerationElements2.nextElement();
                Hashtable hashtable2 = (Hashtable) this.affectedClassMap.get(str);
                if (hashtable2 == null) {
                    hashtable2 = new Hashtable();
                    this.affectedClassMap.put(str, hashtable2);
                }
                hashtable2.put(classFileInfo.className, classFileInfo);
                log(str + " affects " + classFileInfo.className, 4);
            }
        }
        this.classpathDependencies = null;
        Path checkClassPath = getCheckClassPath();
        if (checkClassPath != null) {
            this.classpathDependencies = new Hashtable();
            try {
                antClassLoaderCreateClassLoader = getProject().createClassLoader(checkClassPath);
                Hashtable hashtable3 = new Hashtable();
                Object obj = new Object();
                Enumeration enumerationKeys = hashtable.keys();
                while (enumerationKeys.hasMoreElements()) {
                    String str2 = (String) enumerationKeys.nextElement();
                    log("Determining classpath dependencies for " + str2, i);
                    Vector vector2 = (Vector) hashtable.get(str2);
                    Hashtable hashtable4 = new Hashtable();
                    this.classpathDependencies.put(str2, hashtable4);
                    Enumeration enumerationElements3 = vector2.elements();
                    while (enumerationElements3.hasMoreElements()) {
                        String str3 = (String) enumerationElements3.nextElement();
                        log("Looking for " + str3, i);
                        Object obj2 = hashtable3.get(str3);
                        if (obj2 == null) {
                            if (!str3.startsWith("java.") && !str3.startsWith("javax.")) {
                                URL resource = antClassLoaderCreateClassLoader.getResource(str3.replace('.', '/') + ".class");
                                log("URL is " + resource, 4);
                                if (resource != null) {
                                    if (resource.getProtocol().equals("jar")) {
                                        String file3 = resource.getFile();
                                        String strSubstring = file3.substring(0, file3.indexOf(33));
                                        if (strSubstring.startsWith("file:")) {
                                            file = new File(FileUtils.getFileUtils().fromURI(strSubstring));
                                        } else {
                                            throw new IOException("Bizarre nested path in jar: protocol: " + strSubstring);
                                        }
                                    } else {
                                        file = resource.getProtocol().equals("file") ? new File(FileUtils.getFileUtils().fromURI(resource.toExternalForm())) : obj;
                                    }
                                    log("Class " + str2 + " depends on " + file + " due to " + str3, 4);
                                } else {
                                    file = obj;
                                }
                                obj2 = file;
                            } else {
                                log("Ignoring base classlib dependency " + str3, 4);
                                obj2 = obj;
                            }
                            hashtable3.put(str3, obj2);
                        }
                        if (obj2 != obj) {
                            File file4 = (File) obj2;
                            log("Adding a classpath dependency on " + file4, 4);
                            hashtable4.put(file4, file4);
                        }
                        i = 4;
                    }
                }
            } finally {
                if (antClassLoaderCreateClassLoader != null) {
                    antClassLoaderCreateClassLoader.cleanup();
                }
            }
        } else {
            log("No classpath to check", 4);
        }
        if (this.cache == null || !z) {
            return;
        }
        writeCachedDependencies(hashtable);
    }

    private int deleteAllAffectedFiles() {
        Enumeration enumerationElements = this.outOfDateClasses.elements();
        int iDeleteAffectedFiles = 0;
        while (enumerationElements.hasMoreElements()) {
            String str = (String) enumerationElements.nextElement();
            iDeleteAffectedFiles += deleteAffectedFiles(str);
            ClassFileInfo classFileInfo = (ClassFileInfo) this.classFileInfoMap.get(str);
            if (classFileInfo != null && classFileInfo.absoluteFile.exists()) {
                if (classFileInfo.sourceFile != null) {
                    classFileInfo.absoluteFile.delete();
                    iDeleteAffectedFiles++;
                } else {
                    warnOutOfDateButNotDeleted(classFileInfo, str, str);
                }
            }
        }
        return iDeleteAffectedFiles;
    }

    private int deleteAffectedFiles(String str) {
        int iDeleteAffectedFiles;
        Hashtable hashtable = (Hashtable) this.affectedClassMap.get(str);
        if (hashtable == null) {
            return 0;
        }
        Enumeration enumerationKeys = hashtable.keys();
        int i = 0;
        while (enumerationKeys.hasMoreElements()) {
            String str2 = (String) enumerationKeys.nextElement();
            ClassFileInfo classFileInfo = (ClassFileInfo) hashtable.get(str2);
            if (classFileInfo.absoluteFile.exists()) {
                if (classFileInfo.sourceFile != null) {
                    log("Deleting file " + classFileInfo.absoluteFile.getPath() + " since " + str + " out of date", 3);
                    classFileInfo.absoluteFile.delete();
                    i++;
                    if (this.closure) {
                        iDeleteAffectedFiles = deleteAffectedFiles(str2);
                    } else if (str2.indexOf("$") != -1) {
                        String strSubstring = str2.substring(0, str2.indexOf("$"));
                        log("Top level class = " + strSubstring, 3);
                        ClassFileInfo classFileInfo2 = (ClassFileInfo) this.classFileInfoMap.get(strSubstring);
                        if (classFileInfo2 != null && classFileInfo2.absoluteFile.exists()) {
                            log("Deleting file " + classFileInfo2.absoluteFile.getPath() + " since one of its inner classes was removed", 3);
                            classFileInfo2.absoluteFile.delete();
                            i++;
                            if (this.closure) {
                                iDeleteAffectedFiles = deleteAffectedFiles(strSubstring);
                            }
                        }
                    }
                    i += iDeleteAffectedFiles;
                } else {
                    warnOutOfDateButNotDeleted(classFileInfo, str2, str);
                }
            }
        }
        return i;
    }

    private void warnOutOfDateButNotDeleted(ClassFileInfo classFileInfo, String str, String str2) {
        if (classFileInfo.isUserWarned) {
            return;
        }
        log("The class " + str + " in file " + classFileInfo.absoluteFile.getPath() + " is out of date due to " + str2 + " but has not been deleted because its source file could not be determined", (this.warnOnRmiStubs || !isRmiStub(str, str2)) ? 1 : 3);
        classFileInfo.isUserWarned = true;
    }

    private boolean isRmiStub(String str, String str2) {
        return isStub(str, str2, DefaultRmicAdapter.RMI_STUB_SUFFIX) || isStub(str, str2, DefaultRmicAdapter.RMI_SKEL_SUFFIX) || isStub(str, str2, DefaultRmicAdapter.RMI_STUB_SUFFIX) || isStub(str, str2, DefaultRmicAdapter.RMI_SKEL_SUFFIX);
    }

    private boolean isStub(String str, String str2, String str3) {
        return (str2 + str3).equals(str);
    }

    private void dumpDependencies() {
        log("Reverse Dependency Dump for " + this.affectedClassMap.size() + " classes:", 4);
        Enumeration enumerationKeys = this.affectedClassMap.keys();
        while (enumerationKeys.hasMoreElements()) {
            String str = (String) enumerationKeys.nextElement();
            log(" Class " + str + " affects:", 4);
            Hashtable hashtable = (Hashtable) this.affectedClassMap.get(str);
            Enumeration enumerationKeys2 = hashtable.keys();
            while (enumerationKeys2.hasMoreElements()) {
                String str2 = (String) enumerationKeys2.nextElement();
                log("    " + str2 + " in " + ((ClassFileInfo) hashtable.get(str2)).absoluteFile.getPath(), 4);
            }
        }
        if (this.classpathDependencies != null) {
            log("Classpath file dependencies (Forward):", 4);
            Enumeration enumerationKeys3 = this.classpathDependencies.keys();
            while (enumerationKeys3.hasMoreElements()) {
                String str3 = (String) enumerationKeys3.nextElement();
                log(" Class " + str3 + " depends on:", 4);
                Enumeration enumerationElements = ((Hashtable) this.classpathDependencies.get(str3)).elements();
                while (enumerationElements.hasMoreElements()) {
                    log("    " + ((File) enumerationElements.nextElement()).getPath(), 4);
                }
            }
        }
    }

    private void determineOutOfDateClasses() {
        ClassFileInfo classFileInfo;
        this.outOfDateClasses = new Hashtable();
        for (int i = 0; i < this.srcPathList.length; i++) {
            File fileResolveFile = getProject().resolveFile(this.srcPathList[i]);
            if (fileResolveFile.exists()) {
                scanDir(fileResolveFile, getDirectoryScanner(fileResolveFile).getIncludedFiles());
            }
        }
        Hashtable hashtable = this.classpathDependencies;
        if (hashtable == null) {
            return;
        }
        Enumeration enumerationKeys = hashtable.keys();
        while (enumerationKeys.hasMoreElements()) {
            String str = (String) enumerationKeys.nextElement();
            if (!this.outOfDateClasses.containsKey(str) && (classFileInfo = (ClassFileInfo) this.classFileInfoMap.get(str)) != null) {
                Enumeration enumerationElements = ((Hashtable) this.classpathDependencies.get(str)).elements();
                while (true) {
                    if (enumerationElements.hasMoreElements()) {
                        File file = (File) enumerationElements.nextElement();
                        if (file.lastModified() > classFileInfo.absoluteFile.lastModified()) {
                            log("Class " + str + " is out of date with respect to " + file, 4);
                            this.outOfDateClasses.put(str, str);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws Throwable {
        try {
            long jCurrentTimeMillis = System.currentTimeMillis();
            Path path = this.srcPath;
            if (path == null) {
                throw new BuildException("srcdir attribute must be set", getLocation());
            }
            String[] list = path.list();
            this.srcPathList = list;
            if (list.length == 0) {
                throw new BuildException("srcdir attribute must be non-empty", getLocation());
            }
            if (this.destPath == null) {
                this.destPath = this.srcPath;
            }
            File file = this.cache;
            if (file != null && file.exists() && !this.cache.isDirectory()) {
                throw new BuildException("The cache, if specified, must point to a directory");
            }
            File file2 = this.cache;
            if (file2 != null && !file2.exists()) {
                this.cache.mkdirs();
            }
            determineDependencies();
            if (this.dump) {
                dumpDependencies();
            }
            determineOutOfDateClasses();
            int iDeleteAllAffectedFiles = deleteAllAffectedFiles();
            log("Deleted " + iDeleteAllAffectedFiles + " out of date files in " + ((System.currentTimeMillis() - jCurrentTimeMillis) / 1000) + " seconds", iDeleteAllAffectedFiles > 0 ? 2 : 4);
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    protected void scanDir(File file, String[] strArr) {
        for (int i = 0; i < strArr.length; i++) {
            File file2 = new File(file, strArr[i]);
            if (strArr[i].endsWith(".java")) {
                String strConvertSlashName = ClassFileUtils.convertSlashName(file2.getPath().substring(file.getPath().length() + 1, r2.length() - 5));
                ClassFileInfo classFileInfo = (ClassFileInfo) this.classFileInfoMap.get(strConvertSlashName);
                if (classFileInfo == null) {
                    this.outOfDateClasses.put(strConvertSlashName, strConvertSlashName);
                } else if (file2.lastModified() > classFileInfo.absoluteFile.lastModified()) {
                    this.outOfDateClasses.put(strConvertSlashName, strConvertSlashName);
                }
            }
        }
    }

    private Vector getClassFiles(Path path) {
        String[] list = path.list();
        Vector vector = new Vector();
        for (String str : list) {
            File file = new File(str);
            if (file.isDirectory()) {
                addClassFiles(vector, file, file);
            }
        }
        return vector;
    }

    private File findSourceFile(String str, File file) {
        String str2;
        int iIndexOf = str.indexOf("$");
        if (iIndexOf != -1) {
            str2 = str.substring(0, iIndexOf) + ".java";
        } else {
            str2 = str + ".java";
        }
        for (int i = 0; i < this.srcPathList.length; i++) {
            File file2 = new File(this.srcPathList[i], str2);
            if (file2.equals(file) || file2.exists()) {
                return file2;
            }
        }
        return null;
    }

    private void addClassFiles(Vector vector, File file, File file2) {
        String[] list = file.list();
        if (list == null) {
            return;
        }
        int length = list.length;
        int length2 = file2.getPath().length();
        File fileFindSourceFile = null;
        for (int i = 0; i < length; i++) {
            File file3 = new File(file, list[i]);
            if (list[i].endsWith(".class")) {
                ClassFileInfo classFileInfo = new ClassFileInfo();
                classFileInfo.absoluteFile = file3;
                String strSubstring = file3.getPath().substring(length2 + 1, file3.getPath().length() - 6);
                classFileInfo.className = ClassFileUtils.convertSlashName(strSubstring);
                fileFindSourceFile = findSourceFile(strSubstring, fileFindSourceFile);
                classFileInfo.sourceFile = fileFindSourceFile;
                vector.addElement(classFileInfo);
            } else {
                addClassFiles(vector, file3, file2);
            }
        }
    }

    public void setSrcdir(Path path) {
        this.srcPath = path;
    }

    public void setDestDir(Path path) {
        this.destPath = path;
    }

    public void setCache(File file) {
        this.cache = file;
    }

    public void setClosure(boolean z) {
        this.closure = z;
    }

    public void setDump(boolean z) {
        this.dump = z;
    }
}
