package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSSConstants;
import org.apache.tools.ant.types.Commandline;

/* JADX INFO: loaded from: classes3.dex */
public class Patch extends Task {
    private static final String PATCH = "patch";
    private File directory;
    private File originalFile;
    private boolean havePatchfile = false;
    private Commandline cmd = new Commandline();
    private boolean failOnError = false;

    public void setOriginalfile(File file) {
        this.originalFile = file;
    }

    public void setDestfile(File file) {
        if (file != null) {
            this.cmd.createArgument().setValue("-o");
            this.cmd.createArgument().setFile(file);
        }
    }

    public void setPatchfile(File file) {
        if (!file.exists()) {
            throw new BuildException("patchfile " + file + " doesn't exist", getLocation());
        }
        this.cmd.createArgument().setValue("-i");
        this.cmd.createArgument().setFile(file);
        this.havePatchfile = true;
    }

    public void setBackups(boolean z) {
        if (z) {
            this.cmd.createArgument().setValue("-b");
        }
    }

    public void setIgnorewhitespace(boolean z) {
        if (z) {
            this.cmd.createArgument().setValue("-l");
        }
    }

    public void setStrip(int i) throws BuildException {
        if (i < 0) {
            throw new BuildException("strip has to be >= 0", getLocation());
        }
        this.cmd.createArgument().setValue("-p" + i);
    }

    public void setQuiet(boolean z) {
        if (z) {
            this.cmd.createArgument().setValue("-s");
        }
    }

    public void setReverse(boolean z) {
        if (z) {
            this.cmd.createArgument().setValue(MSVSSConstants.FLAG_RECURSION);
        }
    }

    public void setDir(File file) {
        this.directory = file;
    }

    public void setFailOnError(boolean z) {
        this.failOnError = z;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (!this.havePatchfile) {
            throw new BuildException("patchfile argument is required", getLocation());
        }
        Commandline commandline = (Commandline) this.cmd.clone();
        commandline.setExecutable(PATCH);
        if (this.originalFile != null) {
            commandline.createArgument().setFile(this.originalFile);
        }
        Execute execute = new Execute(new LogStreamHandler((Task) this, 2, 1), null);
        execute.setCommandline(commandline.getCommandline());
        File file = this.directory;
        if (file != null) {
            if (file.exists() && this.directory.isDirectory()) {
                execute.setWorkingDirectory(this.directory);
            } else {
                if (!this.directory.isDirectory()) {
                    throw new BuildException(this.directory + " is not a directory.", getLocation());
                }
                throw new BuildException("directory " + this.directory + " doesn't exist", getLocation());
            }
        } else {
            execute.setWorkingDirectory(getProject().getBaseDir());
        }
        log(commandline.describeCommand(), 3);
        try {
            int iExecute = execute.execute();
            if (Execute.isFailure(iExecute)) {
                String str = "'patch' failed with exit code " + iExecute;
                if (this.failOnError) {
                    throw new BuildException(str);
                }
                log(str, 0);
            }
        } catch (IOException e) {
            throw new BuildException(e, getLocation());
        }
    }
}
