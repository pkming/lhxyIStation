package org.apache.tools.ant.taskdefs.optional.sos;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public abstract class SOS extends Task implements SOSCmd {
    private static final int ERROR_EXIT_STATUS = 255;
    protected Commandline commandLine;
    private String sosCmdDir = null;
    private String sosUsername = null;
    private String sosPassword = "";
    private String projectPath = null;
    private String vssServerPath = null;
    private String sosServerPath = null;
    private String sosHome = null;
    private String localPath = null;
    private String version = null;
    private String label = null;
    private String comment = null;
    private String filename = null;
    private boolean noCompress = false;
    private boolean noCache = false;
    private boolean recursive = false;
    private boolean verbose = false;

    abstract Commandline buildCmdLine();

    public final void setNoCache(boolean z) {
        this.noCache = z;
    }

    public final void setNoCompress(boolean z) {
        this.noCompress = z;
    }

    public final void setSosCmd(String str) {
        this.sosCmdDir = FileUtils.translatePath(str);
    }

    public final void setUsername(String str) {
        this.sosUsername = str;
    }

    public final void setPassword(String str) {
        this.sosPassword = str;
    }

    public final void setProjectPath(String str) {
        if (str.startsWith("$")) {
            this.projectPath = str;
        } else {
            this.projectPath = "$" + str;
        }
    }

    public final void setVssServerPath(String str) {
        this.vssServerPath = str;
    }

    public final void setSosHome(String str) {
        this.sosHome = str;
    }

    public final void setSosServerPath(String str) {
        this.sosServerPath = str;
    }

    public final void setLocalPath(Path path) {
        this.localPath = path.toString();
    }

    public void setVerbose(boolean z) {
        this.verbose = z;
    }

    protected void setInternalFilename(String str) {
        this.filename = str;
    }

    protected void setInternalRecursive(boolean z) {
        this.recursive = z;
    }

    protected void setInternalComment(String str) {
        this.comment = str;
    }

    protected void setInternalLabel(String str) {
        this.label = str;
    }

    protected void setInternalVersion(String str) {
        this.version = str;
    }

    protected String getSosCommand() {
        return this.sosCmdDir == null ? SOSCmd.COMMAND_SOS_EXE : this.sosCmdDir + File.separator + SOSCmd.COMMAND_SOS_EXE;
    }

    protected String getComment() {
        return this.comment;
    }

    protected String getVersion() {
        return this.version;
    }

    protected String getLabel() {
        return this.label;
    }

    protected String getUsername() {
        return this.sosUsername;
    }

    protected String getPassword() {
        return this.sosPassword;
    }

    protected String getProjectPath() {
        return this.projectPath;
    }

    protected String getVssServerPath() {
        return this.vssServerPath;
    }

    protected String getSosHome() {
        return this.sosHome;
    }

    protected String getSosServerPath() {
        return this.sosServerPath;
    }

    protected String getFilename() {
        return this.filename;
    }

    protected String getNoCompress() {
        return this.noCompress ? SOSCmd.FLAG_NO_COMPRESSION : "";
    }

    protected String getNoCache() {
        return this.noCache ? SOSCmd.FLAG_NO_CACHE : "";
    }

    protected String getVerbose() {
        return this.verbose ? SOSCmd.FLAG_VERBOSE : "";
    }

    protected String getRecursive() {
        return this.recursive ? SOSCmd.FLAG_RECURSION : "";
    }

    protected String getLocalPath() {
        if (this.localPath == null) {
            return getProject().getBaseDir().getAbsolutePath();
        }
        File fileResolveFile = getProject().resolveFile(this.localPath);
        if (!fileResolveFile.exists()) {
            if (!(fileResolveFile.mkdirs() || fileResolveFile.isDirectory())) {
                throw new BuildException("Directory " + this.localPath + " creation was not successful for an unknown reason", getLocation());
            }
            getProject().log("Created dir: " + fileResolveFile.getAbsolutePath());
        }
        return fileResolveFile.getAbsolutePath();
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        buildCmdLine();
        if (run(this.commandLine) == 255) {
            throw new BuildException("Failed executing: " + this.commandLine.toString(), getLocation());
        }
    }

    protected int run(Commandline commandline) {
        try {
            Execute execute = new Execute(new LogStreamHandler((Task) this, 2, 1));
            execute.setAntRun(getProject());
            execute.setWorkingDirectory(getProject().getBaseDir());
            execute.setCommandline(commandline.getCommandline());
            execute.setVMLauncher(false);
            return execute.execute();
        } catch (IOException e) {
            throw new BuildException(e, getLocation());
        }
    }

    protected void getRequiredAttributes() {
        this.commandLine.setExecutable(getSosCommand());
        if (getSosServerPath() == null) {
            throw new BuildException("sosserverpath attribute must be set!", getLocation());
        }
        this.commandLine.createArgument().setValue(SOSCmd.FLAG_SOS_SERVER);
        this.commandLine.createArgument().setValue(getSosServerPath());
        if (getUsername() == null) {
            throw new BuildException("username attribute must be set!", getLocation());
        }
        this.commandLine.createArgument().setValue(SOSCmd.FLAG_USERNAME);
        this.commandLine.createArgument().setValue(getUsername());
        this.commandLine.createArgument().setValue(SOSCmd.FLAG_PASSWORD);
        this.commandLine.createArgument().setValue(getPassword());
        if (getVssServerPath() == null) {
            throw new BuildException("vssserverpath attribute must be set!", getLocation());
        }
        this.commandLine.createArgument().setValue(SOSCmd.FLAG_VSS_SERVER);
        this.commandLine.createArgument().setValue(getVssServerPath());
        if (getProjectPath() == null) {
            throw new BuildException("projectpath attribute must be set!", getLocation());
        }
        this.commandLine.createArgument().setValue(SOSCmd.FLAG_PROJECT);
        this.commandLine.createArgument().setValue(getProjectPath());
    }

    protected void getOptionalAttributes() {
        this.commandLine.createArgument().setValue(getVerbose());
        this.commandLine.createArgument().setValue(getNoCompress());
        if (getSosHome() == null) {
            this.commandLine.createArgument().setValue(getNoCache());
        } else {
            this.commandLine.createArgument().setValue(SOSCmd.FLAG_SOS_HOME);
            this.commandLine.createArgument().setValue(getSosHome());
        }
        if (getLocalPath() != null) {
            this.commandLine.createArgument().setValue(SOSCmd.FLAG_WORKING_DIR);
            this.commandLine.createArgument().setValue(getLocalPath());
        }
    }
}
