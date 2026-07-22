package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.IdentityMapper;

/* JADX INFO: loaded from: classes3.dex */
public class PresentSelector extends BaseSelector {
    private File targetdir = null;
    private Mapper mapperElement = null;
    private FileNameMapper map = null;
    private boolean destmustexist = true;

    @Override // org.apache.tools.ant.types.DataType
    public String toString() {
        StringBuilder sb = new StringBuilder("{presentselector targetdir: ");
        File file = this.targetdir;
        if (file == null) {
            sb.append("NOT YET SET");
        } else {
            sb.append(file.getName());
        }
        sb.append(" present: ");
        if (this.destmustexist) {
            sb.append("both");
        } else {
            sb.append("srconly");
        }
        FileNameMapper fileNameMapper = this.map;
        if (fileNameMapper != null) {
            sb.append(fileNameMapper.toString());
        } else {
            Mapper mapper = this.mapperElement;
            if (mapper != null) {
                sb.append(mapper.toString());
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public void setTargetdir(File file) {
        this.targetdir = file;
    }

    public Mapper createMapper() throws BuildException {
        if (this.map != null || this.mapperElement != null) {
            throw new BuildException(Expand.ERROR_MULTIPLE_MAPPERS);
        }
        Mapper mapper = new Mapper(getProject());
        this.mapperElement = mapper;
        return mapper;
    }

    public void addConfigured(FileNameMapper fileNameMapper) {
        if (this.map != null || this.mapperElement != null) {
            throw new BuildException(Expand.ERROR_MULTIPLE_MAPPERS);
        }
        this.map = fileNameMapper;
    }

    public void setPresent(FilePresence filePresence) {
        if (filePresence.getIndex() == 0) {
            this.destmustexist = false;
        }
    }

    @Override // org.apache.tools.ant.types.selectors.BaseSelector
    public void verifySettings() {
        if (this.targetdir == null) {
            setError("The targetdir attribute is required.");
        }
        if (this.map == null) {
            Mapper mapper = this.mapperElement;
            if (mapper == null) {
                this.map = new IdentityMapper();
                return;
            }
            FileNameMapper implementation = mapper.getImplementation();
            this.map = implementation;
            if (implementation == null) {
                setError("Could not set <mapper> element.");
            }
        }
    }

    @Override // org.apache.tools.ant.types.selectors.BaseSelector, org.apache.tools.ant.types.selectors.FileSelector
    public boolean isSelected(File file, String str, File file2) {
        validate();
        String[] strArrMapFileName = this.map.mapFileName(str);
        if (strArrMapFileName == null) {
            return false;
        }
        if (strArrMapFileName.length != 1 || strArrMapFileName[0] == null) {
            throw new BuildException("Invalid destination file results for " + this.targetdir + " with filename " + str);
        }
        return FileUtils.getFileUtils().resolveFile(this.targetdir, strArrMapFileName[0]).exists() == this.destmustexist;
    }

    public static class FilePresence extends EnumeratedAttribute {
        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"srconly", "both"};
        }
    }
}
