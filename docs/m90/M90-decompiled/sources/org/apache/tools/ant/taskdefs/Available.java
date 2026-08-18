package org.apache.tools.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.StringUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Available extends Task implements Condition {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private String classname;
    private Path classpath;
    private File file;
    private String filename;
    private Path filepath;
    private AntClassLoader loader;
    private String property;
    private String resource;
    private FileDir type;
    private Object value = "true";
    private boolean isTask = false;
    private boolean ignoreSystemclasses = false;
    private boolean searchParents = false;

    public void setSearchParents(boolean z) {
        this.searchParents = z;
    }

    public void setClasspath(Path path) {
        createClasspath().append(path);
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(getProject());
        }
        return this.classpath.createPath();
    }

    public void setClasspathRef(Reference reference) {
        createClasspath().setRefid(reference);
    }

    public void setFilepath(Path path) {
        createFilepath().append(path);
    }

    public Path createFilepath() {
        if (this.filepath == null) {
            this.filepath = new Path(getProject());
        }
        return this.filepath.createPath();
    }

    public void setProperty(String str) {
        this.property = str;
    }

    public void setValue(Object obj) {
        this.value = obj;
    }

    public void setValue(String str) {
        setValue((Object) str);
    }

    public void setClassname(String str) {
        if ("".equals(str)) {
            return;
        }
        this.classname = str;
    }

    public void setFile(File file) {
        this.file = file;
        this.filename = FILE_UTILS.removeLeadingPath(getProject().getBaseDir(), file);
    }

    public void setResource(String str) {
        this.resource = str;
    }

    public void setType(String str) {
        log("DEPRECATED - The setType(String) method has been deprecated. Use setType(Available.FileDir) instead.", 1);
        FileDir fileDir = new FileDir();
        this.type = fileDir;
        fileDir.setValue(str);
    }

    public void setType(FileDir fileDir) {
        this.type = fileDir;
    }

    public void setIgnoresystemclasses(boolean z) {
        this.ignoreSystemclasses = z;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (this.property == null) {
            throw new BuildException("property attribute is required", getLocation());
        }
        this.isTask = true;
        try {
            if (eval()) {
                PropertyHelper propertyHelper = PropertyHelper.getPropertyHelper(getProject());
                Object property = propertyHelper.getProperty(this.property);
                if (property != null && !property.equals(this.value)) {
                    log("DEPRECATED - <available> used to override an existing property." + StringUtils.LINE_SEP + "  Build file should not reuse the same property name for different values.", 1);
                }
                propertyHelper.setProperty(this.property, this.value, true);
            }
        } finally {
            this.isTask = false;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() throws BuildException {
        try {
            if (this.classname == null && this.file == null && this.resource == null) {
                throw new BuildException("At least one of (classname|file|resource) is required", getLocation());
            }
            if (this.type != null && this.file == null) {
                throw new BuildException("The type attribute is only valid when specifying the file attribute.", getLocation());
            }
            Path path = this.classpath;
            if (path != null) {
                path.setProject(getProject());
                this.loader = getProject().createClassLoader(this.classpath);
            }
            String str = "";
            if (this.isTask) {
                str = " to set property " + this.property;
            } else {
                setTaskName("available");
            }
            String str2 = this.classname;
            if (str2 != null && !checkClass(str2)) {
                log("Unable to load class " + this.classname + str, 3);
                return false;
            }
            if (this.file != null && !checkFile()) {
                StringBuffer stringBuffer = new StringBuffer("Unable to find ");
                FileDir fileDir = this.type;
                if (fileDir != null) {
                    stringBuffer.append(fileDir).append(' ');
                }
                stringBuffer.append(this.filename).append(str);
                log(stringBuffer.toString(), 3);
                AntClassLoader antClassLoader = this.loader;
                if (antClassLoader != null) {
                    antClassLoader.cleanup();
                    this.loader = null;
                }
                if (!this.isTask) {
                    setTaskName(null);
                }
                return false;
            }
            String str3 = this.resource;
            if (str3 == null || checkResource(str3)) {
                AntClassLoader antClassLoader2 = this.loader;
                if (antClassLoader2 != null) {
                    antClassLoader2.cleanup();
                    this.loader = null;
                }
                if (this.isTask) {
                    return true;
                }
                setTaskName(null);
                return true;
            }
            log("Unable to load resource " + this.resource + str, 3);
            AntClassLoader antClassLoader3 = this.loader;
            if (antClassLoader3 != null) {
                antClassLoader3.cleanup();
                this.loader = null;
            }
            if (!this.isTask) {
                setTaskName(null);
            }
            return false;
        } finally {
            AntClassLoader antClassLoader4 = this.loader;
            if (antClassLoader4 != null) {
                antClassLoader4.cleanup();
                this.loader = null;
            }
            if (!this.isTask) {
                setTaskName(null);
            }
        }
    }

    private boolean checkFile() {
        Path path = this.filepath;
        if (path == null) {
            return checkFile(this.file, this.filename);
        }
        String[] list = path.list();
        for (int i = 0; i < list.length; i++) {
            log("Searching " + list[i], 3);
            File file = new File(list[i]);
            if (file.exists() && (this.filename.equals(list[i]) || this.filename.equals(file.getName()))) {
                FileDir fileDir = this.type;
                if (fileDir == null) {
                    log("Found: " + file, 3);
                    return true;
                }
                if (fileDir.isDir() && file.isDirectory()) {
                    log("Found directory: " + file, 3);
                    return true;
                }
                if (!this.type.isFile() || !file.isFile()) {
                    return false;
                }
                log("Found file: " + file, 3);
                return true;
            }
            File parentFile = file.getParentFile();
            if (parentFile != null && parentFile.exists() && this.filename.equals(parentFile.getAbsolutePath())) {
                FileDir fileDir2 = this.type;
                if (fileDir2 == null) {
                    log("Found: " + parentFile, 3);
                    return true;
                }
                if (!fileDir2.isDir()) {
                    return false;
                }
                log("Found directory: " + parentFile, 3);
                return true;
            }
            if (file.exists() && file.isDirectory() && checkFile(new File(file, this.filename), this.filename + " in " + file)) {
                return true;
            }
            while (this.searchParents && parentFile != null && parentFile.exists()) {
                if (checkFile(new File(parentFile, this.filename), this.filename + " in " + parentFile)) {
                    return true;
                }
                parentFile = parentFile.getParentFile();
            }
        }
        return false;
    }

    private boolean checkFile(File file, String str) {
        FileDir fileDir = this.type;
        if (fileDir != null) {
            if (fileDir.isDir()) {
                if (file.isDirectory()) {
                    log("Found directory: " + str, 3);
                }
                return file.isDirectory();
            }
            if (this.type.isFile()) {
                if (file.isFile()) {
                    log("Found file: " + str, 3);
                }
                return file.isFile();
            }
        }
        if (file.exists()) {
            log("Found: " + str, 3);
        }
        return file.exists();
    }

    private boolean checkResource(String str) {
        AntClassLoader antClassLoader = this.loader;
        if (antClassLoader != null) {
            return antClassLoader.getResourceAsStream(str) != null;
        }
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader != null ? classLoader.getResourceAsStream(str) != null : ClassLoader.getSystemResourceAsStream(str) != null;
    }

    private boolean checkClass(String str) {
        try {
            if (this.ignoreSystemclasses) {
                AntClassLoader antClassLoaderCreateClassLoader = getProject().createClassLoader(this.classpath);
                this.loader = antClassLoaderCreateClassLoader;
                antClassLoaderCreateClassLoader.setParentFirst(false);
                this.loader.addJavaLibraries();
                try {
                    this.loader.findClass(str);
                } catch (SecurityException unused) {
                    return true;
                }
            } else {
                AntClassLoader antClassLoader = this.loader;
                if (antClassLoader != null) {
                    antClassLoader.loadClass(str);
                } else {
                    ClassLoader classLoader = getClass().getClassLoader();
                    if (classLoader != null) {
                        Class.forName(str, true, classLoader);
                    } else {
                        Class.forName(str);
                    }
                }
            }
            return true;
        } catch (ClassNotFoundException unused2) {
            log("class \"" + str + "\" was not found", 4);
            return false;
        } catch (NoClassDefFoundError e) {
            log("Could not load dependent class \"" + e.getMessage() + "\" for class \"" + str + "\"", 4);
            return false;
        }
    }

    public static class FileDir extends EnumeratedAttribute {
        private static final String[] VALUES = {"file", "dir"};

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return VALUES;
        }

        public boolean isDir() {
            return "dir".equalsIgnoreCase(getValue());
        }

        public boolean isFile() {
            return "file".equalsIgnoreCase(getValue());
        }
    }
}
