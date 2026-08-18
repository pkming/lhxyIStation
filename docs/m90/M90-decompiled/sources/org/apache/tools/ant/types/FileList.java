package org.apache.tools.ant.types;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.resources.FileResourceIterator;

/* JADX INFO: loaded from: classes3.dex */
public class FileList extends DataType implements ResourceCollection {
    private File dir;
    private List<String> filenames;

    @Override // org.apache.tools.ant.types.ResourceCollection
    public boolean isFilesystemOnly() {
        return true;
    }

    public FileList() {
        this.filenames = new ArrayList();
    }

    protected FileList(FileList fileList) {
        this.filenames = new ArrayList();
        this.dir = fileList.dir;
        this.filenames = fileList.filenames;
        setProject(fileList.getProject());
    }

    @Override // org.apache.tools.ant.types.DataType
    public void setRefid(Reference reference) throws BuildException {
        if (this.dir != null || this.filenames.size() != 0) {
            throw tooManyAttributes();
        }
        super.setRefid(reference);
    }

    public void setDir(File file) throws BuildException {
        checkAttributesAllowed();
        this.dir = file;
    }

    public File getDir(Project project) {
        if (isReference()) {
            return getRef(project).getDir(project);
        }
        return this.dir;
    }

    public void setFiles(String str) {
        checkAttributesAllowed();
        if (str == null || str.length() <= 0) {
            return;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(str, ", \t\n\r\f", false);
        while (stringTokenizer.hasMoreTokens()) {
            this.filenames.add(stringTokenizer.nextToken());
        }
    }

    public String[] getFiles(Project project) {
        if (isReference()) {
            return getRef(project).getFiles(project);
        }
        if (this.dir == null) {
            throw new BuildException("No directory specified for filelist.");
        }
        if (this.filenames.size() == 0) {
            throw new BuildException("No files specified for filelist.");
        }
        List<String> list = this.filenames;
        return (String[]) list.toArray(new String[list.size()]);
    }

    protected FileList getRef(Project project) {
        return (FileList) getCheckedRef(project);
    }

    public static class FileName {
        private String name;

        public void setName(String str) {
            this.name = str;
        }

        public String getName() {
            return this.name;
        }
    }

    public void addConfiguredFile(FileName fileName) {
        if (fileName.getName() == null) {
            throw new BuildException("No name specified in nested file element");
        }
        this.filenames.add(fileName.getName());
    }

    @Override // org.apache.tools.ant.types.ResourceCollection, java.lang.Iterable
    public Iterator<Resource> iterator() {
        if (isReference()) {
            return getRef(getProject()).iterator();
        }
        Project project = getProject();
        File file = this.dir;
        List<String> list = this.filenames;
        return new FileResourceIterator(project, file, (String[]) list.toArray(new String[list.size()]));
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public int size() {
        if (isReference()) {
            return getRef(getProject()).size();
        }
        return this.filenames.size();
    }
}
