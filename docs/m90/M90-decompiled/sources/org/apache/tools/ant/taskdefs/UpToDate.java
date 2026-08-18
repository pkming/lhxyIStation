package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.MergingMapper;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.util.SourceFileScanner;

/* JADX INFO: loaded from: classes3.dex */
public class UpToDate extends Task implements Condition {
    private String property;
    private File sourceFile;
    private File targetFile;
    private String value;
    private Vector sourceFileSets = new Vector();
    private Union sourceResources = new Union();
    protected Mapper mapperElement = null;

    public void setProperty(String str) {
        this.property = str;
    }

    public void setValue(String str) {
        this.value = str;
    }

    private String getValue() {
        String str = this.value;
        return str != null ? str : "true";
    }

    public void setTargetFile(File file) {
        this.targetFile = file;
    }

    public void setSrcfile(File file) {
        this.sourceFile = file;
    }

    public void addSrcfiles(FileSet fileSet) {
        this.sourceFileSets.addElement(fileSet);
    }

    public Union createSrcResources() {
        return this.sourceResources;
    }

    public Mapper createMapper() throws BuildException {
        if (this.mapperElement != null) {
            throw new BuildException(Expand.ERROR_MULTIPLE_MAPPERS, getLocation());
        }
        Mapper mapper = new Mapper(getProject());
        this.mapperElement = mapper;
        return mapper;
    }

    public void add(FileNameMapper fileNameMapper) {
        createMapper().add(fileNameMapper);
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() {
        boolean zScanDir;
        if (this.sourceFileSets.size() == 0 && this.sourceResources.size() == 0 && this.sourceFile == null) {
            throw new BuildException("At least one srcfile or a nested <srcfiles> or <srcresources> element must be set.");
        }
        if ((this.sourceFileSets.size() > 0 || this.sourceResources.size() > 0) && this.sourceFile != null) {
            throw new BuildException("Cannot specify both the srcfile attribute and a nested <srcfiles> or <srcresources> element.");
        }
        File file = this.targetFile;
        if (file == null && this.mapperElement == null) {
            throw new BuildException("The targetfile attribute or a nested mapper element must be set.");
        }
        if (file != null && !file.exists()) {
            log("The targetfile \"" + this.targetFile.getAbsolutePath() + "\" does not exist.", 3);
            return false;
        }
        File file2 = this.sourceFile;
        if (file2 != null && !file2.exists()) {
            throw new BuildException(this.sourceFile.getAbsolutePath() + " not found.");
        }
        if (this.sourceFile != null) {
            zScanDir = this.mapperElement != null ? new SourceFileScanner(this).restrict(new String[]{this.sourceFile.getAbsolutePath()}, null, null, this.mapperElement.getImplementation()).length == 0 : this.targetFile.lastModified() >= this.sourceFile.lastModified();
            if (!zScanDir) {
                log(this.sourceFile.getAbsolutePath() + " is newer than (one of) its target(s).", 3);
            }
        } else {
            zScanDir = true;
        }
        Enumeration enumerationElements = this.sourceFileSets.elements();
        while (zScanDir && enumerationElements.hasMoreElements()) {
            FileSet fileSet = (FileSet) enumerationElements.nextElement();
            zScanDir = scanDir(fileSet.getDir(getProject()), fileSet.getDirectoryScanner(getProject()).getIncludedFiles());
        }
        if (!zScanDir) {
            return zScanDir;
        }
        Resource[] resourceArrListResources = this.sourceResources.listResources();
        if (resourceArrListResources.length > 0) {
            return ResourceUtils.selectOutOfDateSources(this, resourceArrListResources, getMapper(), getProject()).length == 0;
        }
        return zScanDir;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (this.property == null) {
            throw new BuildException("property attribute is required.", getLocation());
        }
        if (eval()) {
            getProject().setNewProperty(this.property, getValue());
            if (this.mapperElement == null) {
                log("File \"" + this.targetFile.getAbsolutePath() + "\" is up-to-date.", 3);
            } else {
                log("All target files are up-to-date.", 3);
            }
        }
    }

    protected boolean scanDir(File file, String[] strArr) {
        return new SourceFileScanner(this).restrict(strArr, file, this.mapperElement == null ? null : file, getMapper()).length == 0;
    }

    private FileNameMapper getMapper() {
        Mapper mapper = this.mapperElement;
        if (mapper == null) {
            MergingMapper mergingMapper = new MergingMapper();
            mergingMapper.setTo(this.targetFile.getAbsolutePath());
            return mergingMapper;
        }
        return mapper.getImplementation();
    }
}
