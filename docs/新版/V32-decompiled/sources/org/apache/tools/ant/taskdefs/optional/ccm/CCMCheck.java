package org.apache.tools.ant.taskdefs.optional.ccm;

import java.io.File;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.FileSet;

/* JADX INFO: loaded from: classes3.dex */
public class CCMCheck extends Continuus {
    public static final String FLAG_COMMENT = "/comment";
    public static final String FLAG_TASK = "/task";
    private File file = null;
    private String comment = null;
    private String task = null;
    protected Vector filesets = new Vector();

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        log("working file " + file, 3);
        this.file = file;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String str) {
        this.comment = str;
    }

    public String getTask() {
        return this.task;
    }

    public void setTask(String str) {
        this.task = str;
    }

    public void addFileset(FileSet fileSet) {
        this.filesets.addElement(fileSet);
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (this.file == null && this.filesets.size() == 0) {
            throw new BuildException("Specify at least one source - a file or a fileset.");
        }
        File file = this.file;
        if (file != null && file.exists() && this.file.isDirectory()) {
            throw new BuildException("CCMCheck cannot be generated for directories");
        }
        if (this.file != null && this.filesets.size() > 0) {
            throw new BuildException("Choose between file and fileset !");
        }
        if (getFile() != null) {
            doit();
            return;
        }
        int size = this.filesets.size();
        for (int i = 0; i < size; i++) {
            FileSet fileSet = (FileSet) this.filesets.elementAt(i);
            for (String str : fileSet.getDirectoryScanner(getProject()).getIncludedFiles()) {
                setFile(new File(fileSet.getDir(getProject()), str));
                doit();
            }
        }
    }

    private void doit() {
        Commandline commandline = new Commandline();
        commandline.setExecutable(getCcmCommand());
        commandline.createArgument().setValue(getCcmAction());
        checkOptions(commandline);
        if (Execute.isFailure(run(commandline))) {
            throw new BuildException("Failed executing: " + commandline.toString(), getLocation());
        }
    }

    private void checkOptions(Commandline commandline) {
        if (getComment() != null) {
            commandline.createArgument().setValue(FLAG_COMMENT);
            commandline.createArgument().setValue(getComment());
        }
        if (getTask() != null) {
            commandline.createArgument().setValue("/task");
            commandline.createArgument().setValue(getTask());
        }
        if (getFile() != null) {
            commandline.createArgument().setValue(this.file.getAbsolutePath());
        }
    }
}
