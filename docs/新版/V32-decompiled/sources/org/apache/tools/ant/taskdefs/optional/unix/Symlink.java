package org.apache.tools.ant.taskdefs.optional.unix;

import android.app.backup.FullBackup;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.dispatch.DispatchTask;
import org.apache.tools.ant.dispatch.DispatchUtils;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.SymbolicLinkUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Symlink extends DispatchTask {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final SymbolicLinkUtils SYMLINK_UTILS = SymbolicLinkUtils.getSymbolicLinkUtils();
    private boolean failonerror;
    private String link;
    private String linkFileName;
    private boolean overwrite;
    private String resource;
    private Vector fileSets = new Vector();
    private boolean executing = false;

    @Override // org.apache.tools.ant.Task
    public void init() throws BuildException {
        super.init();
        setDefaults();
    }

    @Override // org.apache.tools.ant.Task
    public synchronized void execute() throws BuildException {
        if (this.executing) {
            throw new BuildException("Infinite recursion detected in Symlink.execute()");
        }
        try {
            this.executing = true;
            DispatchUtils.execute(this);
        } finally {
            this.executing = false;
        }
    }

    public void single() throws BuildException {
        try {
            String str = this.resource;
            if (str == null) {
                handleError("Must define the resource to symlink to!");
                return;
            }
            String str2 = this.link;
            if (str2 == null) {
                handleError("Must define the link name for symlink!");
            } else {
                doLink(str, str2);
            }
        } finally {
            setDefaults();
        }
    }

    public void delete() throws BuildException {
        try {
            try {
            } catch (FileNotFoundException e) {
                handleError(e.toString());
            } catch (IOException e2) {
                handleError(e2.toString());
            }
            if (this.link == null) {
                handleError("Must define the link name for symlink!");
            } else {
                log("Removing symlink: " + this.link);
                SYMLINK_UTILS.deleteSymbolicLink(FILE_UTILS.resolveFile(new File("."), this.link), this);
            }
        } finally {
            setDefaults();
        }
    }

    public void recreate() throws BuildException {
        try {
            if (this.fileSets.isEmpty()) {
                handleError("File set identifying link file(s) required for action recreate");
                return;
            }
            Properties propertiesLoadLinks = loadLinks(this.fileSets);
            for (String str : propertiesLoadLinks.keySet()) {
                String property = propertiesLoadLinks.getProperty(str);
                try {
                    File file = new File(str);
                    SymbolicLinkUtils symbolicLinkUtils = SYMLINK_UTILS;
                    if (!symbolicLinkUtils.isSymbolicLink(str)) {
                        doLink(property, str);
                    } else if (!file.getCanonicalPath().equals(new File(property).getCanonicalPath())) {
                        symbolicLinkUtils.deleteSymbolicLink(file, this);
                        doLink(property, str);
                    }
                } catch (IOException unused) {
                    handleError("IO exception while creating link");
                }
            }
        } finally {
            setDefaults();
        }
    }

    public void record() throws BuildException {
        try {
            if (this.fileSets.isEmpty()) {
                handleError("Fileset identifying links to record required");
                return;
            }
            if (this.linkFileName == null) {
                handleError("Name of file to record links in required");
                return;
            }
            Hashtable hashtable = new Hashtable();
            for (File file : findLinks(this.fileSets)) {
                File parentFile = file.getParentFile();
                Vector vector = (Vector) hashtable.get(parentFile);
                if (vector == null) {
                    vector = new Vector();
                    hashtable.put(parentFile, vector);
                }
                vector.addElement(file);
            }
            for (File file2 : hashtable.keySet()) {
                Vector<File> vector2 = (Vector) hashtable.get(file2);
                Properties properties = new Properties();
                for (File file3 : vector2) {
                    try {
                        properties.put(file3.getName(), file3.getCanonicalPath());
                    } catch (IOException unused) {
                        handleError("Couldn't get canonical name of parent link");
                    }
                }
                writePropertyFile(properties, file2);
            }
        } finally {
            setDefaults();
        }
    }

    private void setDefaults() {
        this.resource = null;
        this.link = null;
        this.linkFileName = null;
        this.failonerror = true;
        this.overwrite = false;
        setAction("single");
        this.fileSets.clear();
    }

    public void setOverwrite(boolean z) {
        this.overwrite = z;
    }

    public void setFailOnError(boolean z) {
        this.failonerror = z;
    }

    @Override // org.apache.tools.ant.dispatch.DispatchTask
    public void setAction(String str) {
        super.setAction(str);
    }

    public void setLink(String str) {
        this.link = str;
    }

    public void setResource(String str) {
        this.resource = str;
    }

    public void setLinkfilename(String str) {
        this.linkFileName = str;
    }

    public void addFileset(FileSet fileSet) {
        this.fileSets.addElement(fileSet);
    }

    public static void deleteSymlink(String str) throws Throwable {
        SYMLINK_UTILS.deleteSymbolicLink(new File(str), null);
    }

    public static void deleteSymlink(File file) throws Throwable {
        SYMLINK_UTILS.deleteSymbolicLink(file, null);
    }

    private void writePropertyFile(Properties properties, File file) throws Throwable {
        BufferedOutputStream bufferedOutputStream;
        BufferedOutputStream bufferedOutputStream2 = null;
        try {
            try {
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(file, this.linkFileName)));
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException e) {
            e = e;
        }
        try {
            properties.store(bufferedOutputStream, "Symlinks from " + file);
            FileUtils.close(bufferedOutputStream);
        } catch (IOException e2) {
            e = e2;
            throw new BuildException(e, getLocation());
        } catch (Throwable th2) {
            th = th2;
            bufferedOutputStream2 = bufferedOutputStream;
            FileUtils.close(bufferedOutputStream2);
            throw th;
        }
    }

    private void handleError(String str) {
        if (this.failonerror) {
            throw new BuildException(str);
        }
        log(str);
    }

    private void doLink(String str, String str2) throws Throwable {
        File file = new File(str2);
        String str3 = "-s";
        if (this.overwrite) {
            str3 = "-s" + FullBackup.DATA_TREE_TOKEN;
            if (file.exists()) {
                try {
                    SYMLINK_UTILS.deleteSymbolicLink(file, this);
                } catch (FileNotFoundException unused) {
                    log("Symlink disappeared before it was deleted: " + str2);
                } catch (IOException e) {
                    log("Unable to overwrite preexisting link or file: " + str2, e, 2);
                }
            }
        }
        try {
            Execute.runCommand(this, new String[]{"ln", str3, str, str2});
        } catch (BuildException e2) {
            if (this.failonerror) {
                throw e2;
            }
            log(e2.getMessage(), e2, 2);
        }
    }

    private HashSet findLinks(Vector vector) {
        HashSet hashSet = new HashSet();
        int size = vector.size();
        for (int i = 0; i < size; i++) {
            FileSet fileSet = (FileSet) vector.get(i);
            DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(getProject());
            String[][] strArr = {directoryScanner.getIncludedFiles(), directoryScanner.getIncludedDirectories()};
            File dir = fileSet.getDir(getProject());
            for (int i2 = 0; i2 < 2; i2++) {
                for (int i3 = 0; i3 < strArr[i2].length; i3++) {
                    try {
                        File file = new File(dir, strArr[i2][i3]);
                        File parentFile = file.getParentFile();
                        String name = file.getName();
                        if (SYMLINK_UTILS.isSymbolicLink(parentFile, name)) {
                            hashSet.add(new File(parentFile.getCanonicalFile(), name));
                        }
                    } catch (IOException unused) {
                        handleError("IOException: " + strArr[i2][i3] + " omitted");
                    }
                }
            }
        }
        return hashSet;
    }

    private Properties loadLinks(Vector vector) throws Throwable {
        BufferedInputStream bufferedInputStream;
        Properties properties = new Properties();
        int size = vector.size();
        for (int i = 0; i < size; i++) {
            FileSet fileSet = (FileSet) vector.elementAt(i);
            DirectoryScanner directoryScanner = new DirectoryScanner();
            fileSet.setupDirectoryScanner(directoryScanner, getProject());
            directoryScanner.setFollowSymlinks(false);
            directoryScanner.scan();
            String[] includedFiles = directoryScanner.getIncludedFiles();
            File dir = fileSet.getDir(getProject());
            for (int i2 = 0; i2 < includedFiles.length; i2++) {
                File file = new File(dir, includedFiles[i2]);
                File parentFile = file.getParentFile();
                Properties properties2 = new Properties();
                BufferedInputStream bufferedInputStream2 = null;
                try {
                    try {
                        bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                    } catch (Throwable th) {
                        th = th;
                    }
                } catch (FileNotFoundException unused) {
                } catch (IOException unused2) {
                }
                try {
                    properties2.load(bufferedInputStream);
                    File canonicalFile = parentFile.getCanonicalFile();
                    FileUtils.close(bufferedInputStream);
                    properties2.list(new PrintStream(new LogOutputStream((Task) this, 2)));
                    for (String str : properties2.keySet()) {
                        properties.put(new File(canonicalFile, str).getAbsolutePath(), properties2.getProperty(str));
                    }
                } catch (FileNotFoundException unused3) {
                    bufferedInputStream2 = bufferedInputStream;
                    handleError("Unable to find " + includedFiles[i2] + "; skipping it.");
                    FileUtils.close(bufferedInputStream2);
                } catch (IOException unused4) {
                    bufferedInputStream2 = bufferedInputStream;
                    handleError("Unable to open " + includedFiles[i2] + " or its parent dir; skipping it.");
                    FileUtils.close(bufferedInputStream2);
                } catch (Throwable th2) {
                    th = th2;
                    bufferedInputStream2 = bufferedInputStream;
                    FileUtils.close(bufferedInputStream2);
                    throw th;
                }
            }
        }
        return properties;
    }
}
