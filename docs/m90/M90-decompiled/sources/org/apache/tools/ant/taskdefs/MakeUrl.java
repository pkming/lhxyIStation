package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class MakeUrl extends Task {
    public static final String ERROR_MISSING_FILE = "A source file is missing: ";
    public static final String ERROR_NO_FILES = "No files defined";
    public static final String ERROR_NO_PROPERTY = "No property defined";
    private File file;
    private String property;
    private String separator = " ";
    private List<FileSet> filesets = new LinkedList();
    private List<Path> paths = new LinkedList();
    private boolean validate = true;

    public void setProperty(String str) {
        this.property = str;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void addFileSet(FileSet fileSet) {
        this.filesets.add(fileSet);
    }

    public void setSeparator(String str) {
        this.separator = str;
    }

    public void setValidate(boolean z) {
        this.validate = z;
    }

    public void addPath(Path path) {
        this.paths.add(path);
    }

    private String filesetsToURL() {
        if (this.filesets.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        ListIterator<FileSet> listIterator = this.filesets.listIterator();
        int i = 0;
        while (listIterator.hasNext()) {
            DirectoryScanner directoryScanner = listIterator.next().getDirectoryScanner(getProject());
            for (String str : directoryScanner.getIncludedFiles()) {
                File file = new File(directoryScanner.getBasedir(), str);
                validateFile(file);
                String url = toURL(file);
                sb.append(url);
                log(url, 4);
                sb.append(this.separator);
                i++;
            }
        }
        return stripTrailingSeparator(sb, i);
    }

    private String stripTrailingSeparator(StringBuilder sb, int i) {
        if (i <= 0) {
            return "";
        }
        sb.delete(sb.length() - this.separator.length(), sb.length());
        return new String(sb);
    }

    private String pathsToURL() {
        if (this.paths.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        ListIterator<Path> listIterator = this.paths.listIterator();
        int i = 0;
        while (listIterator.hasNext()) {
            for (String str : listIterator.next().list()) {
                File file = new File(str);
                validateFile(file);
                String url = toURL(file);
                sb.append(url);
                log(url, 4);
                sb.append(this.separator);
                i++;
            }
        }
        return stripTrailingSeparator(sb, i);
    }

    private void validateFile(File file) {
        if (this.validate && !file.exists()) {
            throw new BuildException(ERROR_MISSING_FILE + file.toString());
        }
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        validate();
        if (getProject().getProperty(this.property) != null) {
            return;
        }
        String strFilesetsToURL = filesetsToURL();
        File file = this.file;
        if (file != null) {
            validateFile(file);
            String url = toURL(this.file);
            strFilesetsToURL = strFilesetsToURL.length() > 0 ? url + this.separator + strFilesetsToURL : url;
        }
        String strPathsToURL = pathsToURL();
        if (strPathsToURL.length() > 0) {
            strFilesetsToURL = strFilesetsToURL.length() > 0 ? strFilesetsToURL + this.separator + strPathsToURL : strPathsToURL;
        }
        log("Setting " + this.property + " to URL " + strFilesetsToURL, 3);
        getProject().setNewProperty(this.property, strFilesetsToURL);
    }

    private void validate() {
        if (this.property == null) {
            throw new BuildException(ERROR_NO_PROPERTY);
        }
        if (this.file == null && this.filesets.isEmpty() && this.paths.isEmpty()) {
            throw new BuildException(ERROR_NO_FILES);
        }
    }

    private String toURL(File file) {
        return FileUtils.getFileUtils().toURI(file.getAbsolutePath());
    }
}
