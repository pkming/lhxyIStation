package org.apache.tools.ant.taskdefs.optional.extension;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/* JADX INFO: loaded from: classes3.dex */
public class JarLibDisplayTask extends Task {
    private File libraryFile;
    private final Vector libraryFileSets = new Vector();

    public void setFile(File file) {
        this.libraryFile = file;
    }

    public void addFileset(FileSet fileSet) {
        this.libraryFileSets.addElement(fileSet);
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        validate();
        LibraryDisplayer libraryDisplayer = new LibraryDisplayer();
        if (!this.libraryFileSets.isEmpty()) {
            Iterator it = this.libraryFileSets.iterator();
            while (it.hasNext()) {
                DirectoryScanner directoryScanner = ((FileSet) it.next()).getDirectoryScanner(getProject());
                File basedir = directoryScanner.getBasedir();
                for (String str : directoryScanner.getIncludedFiles()) {
                    libraryDisplayer.displayLibrary(new File(basedir, str));
                }
            }
            return;
        }
        libraryDisplayer.displayLibrary(this.libraryFile);
    }

    private void validate() throws BuildException {
        if (this.libraryFile == null && this.libraryFileSets.isEmpty()) {
            throw new BuildException("File attribute not specified.");
        }
        File file = this.libraryFile;
        if (file != null && !file.exists()) {
            throw new BuildException("File '" + this.libraryFile + "' does not exist.");
        }
        File file2 = this.libraryFile;
        if (file2 != null && !file2.isFile()) {
            throw new BuildException("'" + this.libraryFile + "' is not a file.");
        }
    }
}
