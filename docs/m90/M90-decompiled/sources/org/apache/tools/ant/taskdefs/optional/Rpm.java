package org.apache.tools.ant.taskdefs.optional;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Rpm extends Task {
    private static final String PATH1 = "PATH";
    private static final String PATH2 = "Path";
    private static final String PATH3 = "path";
    private File error;
    private File output;
    private String specFile;
    private File topDir;
    private String command = "-bb";
    private String rpmBuildCommand = null;
    private boolean cleanBuildDir = false;
    private boolean removeSpec = false;
    private boolean removeSource = false;
    private boolean failOnError = false;
    private boolean quiet = false;

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        OutputStream printStream;
        OutputStream outputStream;
        PumpStreamHandler pumpStreamHandler;
        LogOutputStream logOutputStream;
        Commandline commandline = new Commandline();
        String strGuessRpmBuildCommand = this.rpmBuildCommand;
        if (strGuessRpmBuildCommand == null) {
            strGuessRpmBuildCommand = guessRpmBuildCommand();
        }
        commandline.setExecutable(strGuessRpmBuildCommand);
        if (this.topDir != null) {
            commandline.createArgument().setValue("--define");
            commandline.createArgument().setValue("_topdir " + this.topDir);
        }
        commandline.createArgument().setLine(this.command);
        if (this.cleanBuildDir) {
            commandline.createArgument().setValue("--clean");
        }
        if (this.removeSpec) {
            commandline.createArgument().setValue("--rmspec");
        }
        if (this.removeSource) {
            commandline.createArgument().setValue("--rmsource");
        }
        commandline.createArgument().setValue("SPECS/" + this.specFile);
        OutputStream printStream2 = null;
        if (this.error == null && this.output == null) {
            if (!this.quiet) {
                pumpStreamHandler = new LogStreamHandler((Task) this, 2, 1);
            } else {
                pumpStreamHandler = new LogStreamHandler((Task) this, 4, 4);
            }
            outputStream = null;
        } else {
            if (this.output != null) {
                try {
                    printStream2 = new PrintStream(new BufferedOutputStream(new FileOutputStream(this.output)));
                } catch (IOException e) {
                    throw new BuildException(e, getLocation());
                }
            } else {
                if (!this.quiet) {
                    logOutputStream = new LogOutputStream((Task) this, 2);
                } else {
                    logOutputStream = new LogOutputStream((Task) this, 4);
                }
                printStream2 = logOutputStream;
            }
            if (this.error != null) {
                try {
                    printStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(this.error)));
                } catch (IOException e2) {
                    throw new BuildException(e2, getLocation());
                }
            } else if (!this.quiet) {
                printStream = new LogOutputStream((Task) this, 1);
            } else {
                printStream = new LogOutputStream((Task) this, 4);
            }
            outputStream = printStream;
            pumpStreamHandler = new PumpStreamHandler(printStream2, printStream);
        }
        Execute execute = getExecute(commandline, pumpStreamHandler);
        try {
            try {
                log("Building the RPM based on the " + this.specFile + " file");
                int iExecute = execute.execute();
                if (Execute.isFailure(iExecute)) {
                    String str = "'" + commandline.getExecutable() + "' failed with exit code " + iExecute;
                    if (this.failOnError) {
                        throw new BuildException(str);
                    }
                    log(str, 0);
                }
            } catch (IOException e3) {
                throw new BuildException(e3, getLocation());
            }
        } finally {
            FileUtils.close(printStream2);
            FileUtils.close(outputStream);
        }
    }

    public void setTopDir(File file) {
        this.topDir = file;
    }

    public void setCommand(String str) {
        this.command = str;
    }

    public void setSpecFile(String str) {
        if (str == null || str.trim().length() == 0) {
            throw new BuildException("You must specify a spec file", getLocation());
        }
        this.specFile = str;
    }

    public void setCleanBuildDir(boolean z) {
        this.cleanBuildDir = z;
    }

    public void setRemoveSpec(boolean z) {
        this.removeSpec = z;
    }

    public void setRemoveSource(boolean z) {
        this.removeSource = z;
    }

    public void setOutput(File file) {
        this.output = file;
    }

    public void setError(File file) {
        this.error = file;
    }

    public void setRpmBuildCommand(String str) {
        this.rpmBuildCommand = str;
    }

    public void setFailOnError(boolean z) {
        this.failOnError = z;
    }

    public void setQuiet(boolean z) {
        this.quiet = z;
    }

    protected String guessRpmBuildCommand() {
        Map<String, String> environmentVariables = Execute.getEnvironmentVariables();
        String str = environmentVariables.get(PATH1);
        if (str == null && (str = environmentVariables.get(PATH2)) == null) {
            str = environmentVariables.get(PATH3);
        }
        if (str == null) {
            return "rpm";
        }
        String[] list = new Path(getProject(), str).list();
        for (String str2 : list) {
            File file = new File(str2, "rpmbuild" + (Os.isFamily(Os.FAMILY_DOS) ? ".exe" : ""));
            if (file.canRead()) {
                return file.getAbsolutePath();
            }
        }
        return "rpm";
    }

    protected Execute getExecute(Commandline commandline, ExecuteStreamHandler executeStreamHandler) {
        Execute execute = new Execute(executeStreamHandler, null);
        execute.setAntRun(getProject());
        if (this.topDir == null) {
            this.topDir = getProject().getBaseDir();
        }
        execute.setWorkingDirectory(this.topDir);
        execute.setCommandline(commandline.getCommandline());
        return execute;
    }
}
