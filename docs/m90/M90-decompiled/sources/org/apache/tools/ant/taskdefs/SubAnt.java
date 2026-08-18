package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Main;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.ResourceCollection;

/* JADX INFO: loaded from: classes3.dex */
public class SubAnt extends Task {
    private Path buildpath;
    private Ant ant = null;
    private String subTarget = null;
    private String antfile = getDefaultBuildFile();
    private File genericantfile = null;
    private boolean verbose = false;
    private boolean inheritAll = false;
    private boolean inheritRefs = false;
    private boolean failOnError = true;
    private String output = null;
    private Vector properties = new Vector();
    private Vector references = new Vector();
    private Vector propertySets = new Vector();
    private Vector targets = new Vector();

    protected String getDefaultBuildFile() {
        return Main.DEFAULT_BUILD_FILENAME;
    }

    @Override // org.apache.tools.ant.Task
    public void handleOutput(String str) {
        Ant ant = this.ant;
        if (ant != null) {
            ant.handleOutput(str);
        } else {
            super.handleOutput(str);
        }
    }

    @Override // org.apache.tools.ant.Task
    public int handleInput(byte[] bArr, int i, int i2) throws IOException {
        Ant ant = this.ant;
        if (ant != null) {
            return ant.handleInput(bArr, i, i2);
        }
        return super.handleInput(bArr, i, i2);
    }

    @Override // org.apache.tools.ant.Task
    public void handleFlush(String str) {
        Ant ant = this.ant;
        if (ant != null) {
            ant.handleFlush(str);
        } else {
            super.handleFlush(str);
        }
    }

    @Override // org.apache.tools.ant.Task
    public void handleErrorOutput(String str) {
        Ant ant = this.ant;
        if (ant != null) {
            ant.handleErrorOutput(str);
        } else {
            super.handleErrorOutput(str);
        }
    }

    @Override // org.apache.tools.ant.Task
    public void handleErrorFlush(String str) {
        Ant ant = this.ant;
        if (ant != null) {
            ant.handleErrorFlush(str);
        } else {
            super.handleErrorFlush(str);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:59:0x00ef  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x009d A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:85:0x00ce A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:87:0x0173 A[SYNTHETIC] */
    @Override // org.apache.tools.ant.Task
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void execute() {
        /*
            Method dump skipped, instruction units count: 387
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.taskdefs.SubAnt.execute():void");
    }

    private void execute(File file, File file2) throws BuildException {
        if (!file.exists() || file.isDirectory() || !file.canRead()) {
            String str = "Invalid file: " + file;
            if (this.failOnError) {
                throw new BuildException(str);
            }
            log(str, 1);
            return;
        }
        this.ant = createAntTask(file2);
        String absolutePath = file.getAbsolutePath();
        this.ant.setAntfile(absolutePath);
        int size = this.targets.size();
        for (int i = 0; i < size; i++) {
            this.ant.addConfiguredTarget((Ant.TargetElement) this.targets.get(i));
        }
        try {
            try {
                if (this.verbose) {
                    log("Executing: " + absolutePath, 2);
                }
                this.ant.execute();
            } finally {
                this.ant = null;
            }
        } catch (BuildException e) {
            if (this.failOnError || isHardError(e)) {
                throw e;
            }
            log("Failure for target '" + this.subTarget + "' of: " + absolutePath + "\n" + e.getMessage(), 1);
        } catch (Throwable th) {
            if (this.failOnError || isHardError(th)) {
                throw new BuildException(th);
            }
            log("Failure for target '" + this.subTarget + "' of: " + absolutePath + "\n" + th.toString(), 1);
        }
    }

    private boolean isHardError(Throwable th) {
        if (th instanceof BuildException) {
            return isHardError(th.getCause());
        }
        return (th instanceof OutOfMemoryError) || (th instanceof ThreadDeath);
    }

    public void setAntfile(String str) {
        this.antfile = str;
    }

    public void setGenericAntfile(File file) {
        this.genericantfile = file;
    }

    public void setFailonerror(boolean z) {
        this.failOnError = z;
    }

    public void setTarget(String str) {
        this.subTarget = str;
    }

    public void addConfiguredTarget(Ant.TargetElement targetElement) {
        if ("".equals(targetElement.getName())) {
            throw new BuildException("target name must not be empty");
        }
        this.targets.add(targetElement);
    }

    public void setVerbose(boolean z) {
        this.verbose = z;
    }

    public void setOutput(String str) {
        this.output = str;
    }

    public void setInheritall(boolean z) {
        this.inheritAll = z;
    }

    public void setInheritrefs(boolean z) {
        this.inheritRefs = z;
    }

    public void addProperty(Property property) {
        this.properties.addElement(property);
    }

    public void addReference(Ant.Reference reference) {
        this.references.addElement(reference);
    }

    public void addPropertyset(PropertySet propertySet) {
        this.propertySets.addElement(propertySet);
    }

    public void addDirset(DirSet dirSet) {
        add(dirSet);
    }

    public void addFileset(FileSet fileSet) {
        add(fileSet);
    }

    public void addFilelist(FileList fileList) {
        add(fileList);
    }

    public void add(ResourceCollection resourceCollection) {
        getBuildpath().add(resourceCollection);
    }

    public void setBuildpath(Path path) {
        getBuildpath().append(path);
    }

    public Path createBuildpath() {
        return getBuildpath().createPath();
    }

    public Path.PathElement createBuildpathElement() {
        return getBuildpath().createPathElement();
    }

    private Path getBuildpath() {
        if (this.buildpath == null) {
            this.buildpath = new Path(getProject());
        }
        return this.buildpath;
    }

    public void setBuildpathRef(Reference reference) {
        createBuildpath().setRefid(reference);
    }

    private Ant createAntTask(File file) {
        Ant ant = new Ant(this);
        ant.init();
        String str = this.subTarget;
        if (str != null && str.length() > 0) {
            ant.setTarget(this.subTarget);
        }
        String str2 = this.output;
        if (str2 != null) {
            ant.setOutput(str2);
        }
        if (file != null) {
            ant.setDir(file);
        } else {
            ant.setUseNativeBasedir(true);
        }
        ant.setInheritAll(this.inheritAll);
        Enumeration enumerationElements = this.properties.elements();
        while (enumerationElements.hasMoreElements()) {
            copyProperty(ant.createProperty(), (Property) enumerationElements.nextElement());
        }
        Enumeration enumerationElements2 = this.propertySets.elements();
        while (enumerationElements2.hasMoreElements()) {
            ant.addPropertyset((PropertySet) enumerationElements2.nextElement());
        }
        ant.setInheritRefs(this.inheritRefs);
        Enumeration enumerationElements3 = this.references.elements();
        while (enumerationElements3.hasMoreElements()) {
            ant.addReference((Ant.Reference) enumerationElements3.nextElement());
        }
        return ant;
    }

    private static void copyProperty(Property property, Property property2) {
        property.setName(property2.getName());
        if (property2.getValue() != null) {
            property.setValue(property2.getValue());
        }
        if (property2.getFile() != null) {
            property.setFile(property2.getFile());
        }
        if (property2.getResource() != null) {
            property.setResource(property2.getResource());
        }
        if (property2.getPrefix() != null) {
            property.setPrefix(property2.getPrefix());
        }
        if (property2.getRefid() != null) {
            property.setRefid(property2.getRefid());
        }
        if (property2.getEnvironment() != null) {
            property.setEnvironment(property2.getEnvironment());
        }
        if (property2.getClasspath() != null) {
            property.setClasspath(property2.getClasspath());
        }
    }
}
