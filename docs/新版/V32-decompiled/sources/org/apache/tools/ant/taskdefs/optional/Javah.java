package org.apache.tools.ant.taskdefs.optional;

import com.unisound.common.r;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Definer;
import org.apache.tools.ant.taskdefs.optional.javah.JavahAdapter;
import org.apache.tools.ant.taskdefs.optional.javah.JavahAdapterFactory;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.util.facade.FacadeTaskHelper;
import org.apache.tools.ant.util.facade.ImplementationSpecificArgument;

/* JADX INFO: loaded from: classes3.dex */
public class Javah extends Task {
    private Path bootclasspath;
    private String cls;
    private File destDir;
    private FacadeTaskHelper facade;
    private Vector classes = new Vector(2);
    private Path classpath = null;
    private File outputFile = null;
    private boolean verbose = false;
    private boolean force = false;
    private boolean old = false;
    private boolean stubs = false;
    private Vector files = new Vector();
    private JavahAdapter nestedAdapter = null;

    public Javah() {
        this.facade = null;
        this.facade = new FacadeTaskHelper(JavahAdapterFactory.getDefault());
    }

    public void setClass(String str) {
        this.cls = str;
    }

    public ClassArgument createClass() {
        ClassArgument classArgument = new ClassArgument();
        this.classes.addElement(classArgument);
        return classArgument;
    }

    public class ClassArgument {
        private String name;

        public ClassArgument() {
        }

        public void setName(String str) {
            this.name = str;
        }

        public String getName() {
            return this.name;
        }
    }

    public void addFileSet(FileSet fileSet) {
        this.files.add(fileSet);
    }

    public String[] getClasses() {
        ArrayList arrayList = new ArrayList();
        if (this.cls != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(this.cls, ",", false);
            while (stringTokenizer.hasMoreTokens()) {
                arrayList.add(stringTokenizer.nextToken().trim());
            }
        }
        if (this.files.size() > 0) {
            Enumeration enumerationElements = this.files.elements();
            while (enumerationElements.hasMoreElements()) {
                String[] includedFiles = ((FileSet) enumerationElements.nextElement()).getDirectoryScanner(getProject()).getIncludedFiles();
                for (int i = 0; i < includedFiles.length; i++) {
                    arrayList.add(includedFiles[i].replace('\\', '.').replace('/', '.').substring(0, includedFiles[i].length() - 6));
                }
            }
        }
        Enumeration enumerationElements2 = this.classes.elements();
        while (enumerationElements2.hasMoreElements()) {
            arrayList.add(((ClassArgument) enumerationElements2.nextElement()).getName());
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    public void setDestdir(File file) {
        this.destDir = file;
    }

    public File getDestdir() {
        return this.destDir;
    }

    public void setClasspath(Path path) {
        Path path2 = this.classpath;
        if (path2 == null) {
            this.classpath = path;
        } else {
            path2.append(path);
        }
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

    public Path getClasspath() {
        return this.classpath;
    }

    public void setBootclasspath(Path path) {
        Path path2 = this.bootclasspath;
        if (path2 == null) {
            this.bootclasspath = path;
        } else {
            path2.append(path);
        }
    }

    public Path createBootclasspath() {
        if (this.bootclasspath == null) {
            this.bootclasspath = new Path(getProject());
        }
        return this.bootclasspath.createPath();
    }

    public void setBootClasspathRef(Reference reference) {
        createBootclasspath().setRefid(reference);
    }

    public Path getBootclasspath() {
        return this.bootclasspath;
    }

    public void setOutputFile(File file) {
        this.outputFile = file;
    }

    public File getOutputfile() {
        return this.outputFile;
    }

    public void setForce(boolean z) {
        this.force = z;
    }

    public boolean getForce() {
        return this.force;
    }

    public void setOld(boolean z) {
        this.old = z;
    }

    public boolean getOld() {
        return this.old;
    }

    public void setStubs(boolean z) {
        this.stubs = z;
    }

    public boolean getStubs() {
        return this.stubs;
    }

    public void setVerbose(boolean z) {
        this.verbose = z;
    }

    public boolean getVerbose() {
        return this.verbose;
    }

    public void setImplementation(String str) {
        if ("default".equals(str)) {
            this.facade.setImplementation(JavahAdapterFactory.getDefault());
        } else {
            this.facade.setImplementation(str);
        }
    }

    public ImplementationSpecificArgument createArg() {
        ImplementationSpecificArgument implementationSpecificArgument = new ImplementationSpecificArgument();
        this.facade.addImplementationArgument(implementationSpecificArgument);
        return implementationSpecificArgument;
    }

    public String[] getCurrentArgs() {
        return this.facade.getArgs();
    }

    public Path createImplementationClasspath() {
        return this.facade.getImplementationClasspath(getProject());
    }

    public void add(JavahAdapter javahAdapter) {
        if (this.nestedAdapter != null) {
            throw new BuildException("Can't have more than one javah adapter");
        }
        this.nestedAdapter = javahAdapter;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (this.cls == null && this.classes.size() == 0 && this.files.size() == 0) {
            throw new BuildException("class attribute must be set!", getLocation());
        }
        if (this.cls != null && this.classes.size() > 0 && this.files.size() > 0) {
            throw new BuildException("set class attribute OR class element OR fileset, not 2 or more of them.", getLocation());
        }
        File file = this.destDir;
        if (file != null) {
            if (!file.isDirectory()) {
                throw new BuildException("destination directory \"" + this.destDir + "\" does not exist or is not a directory", getLocation());
            }
            if (this.outputFile != null) {
                throw new BuildException("destdir and outputFile are mutually exclusive", getLocation());
            }
        }
        Path path = this.classpath;
        if (path == null) {
            this.classpath = new Path(getProject()).concatSystemClasspath(r.E);
        } else {
            this.classpath = path.concatSystemClasspath(Definer.OnError.POLICY_IGNORE);
        }
        JavahAdapter adapter = this.nestedAdapter;
        if (adapter == null) {
            adapter = JavahAdapterFactory.getAdapter(this.facade.getImplementation(), this, createImplementationClasspath());
        }
        if (!adapter.compile(this)) {
            throw new BuildException("compilation failed");
        }
    }

    public void logAndAddFiles(Commandline commandline) {
        logAndAddFilesToCompile(commandline);
    }

    protected void logAndAddFilesToCompile(Commandline commandline) {
        log("Compilation " + commandline.describeArguments(), 3);
        StringBuffer stringBuffer = new StringBuffer();
        String[] classes = getClasses();
        for (int i = 0; i < classes.length; i++) {
            commandline.createArgument().setValue(classes[i]);
            stringBuffer.append("    ");
            stringBuffer.append(classes[i]);
            stringBuffer.append(StringUtils.LINE_SEP);
        }
        StringBuffer stringBuffer2 = new StringBuffer("Class");
        if (classes.length > 1) {
            stringBuffer2.append("es");
        }
        stringBuffer2.append(" to be compiled:");
        stringBuffer2.append(StringUtils.LINE_SEP);
        log(stringBuffer2.toString() + stringBuffer.toString(), 3);
    }
}
