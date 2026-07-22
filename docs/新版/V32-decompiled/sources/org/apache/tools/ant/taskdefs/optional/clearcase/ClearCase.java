package org.apache.tools.ant.taskdefs.optional.clearcase;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public abstract class ClearCase extends Task {
    private static final String CLEARTOOL_EXE = "cleartool";
    public static final String COMMAND_CHECKIN = "checkin";
    public static final String COMMAND_CHECKOUT = "checkout";
    public static final String COMMAND_LOCK = "lock";
    public static final String COMMAND_LSCO = "lsco";
    public static final String COMMAND_MKATTR = "mkattr";
    public static final String COMMAND_MKBL = "mkbl";
    public static final String COMMAND_MKDIR = "mkdir";
    public static final String COMMAND_MKELEM = "mkelem";
    public static final String COMMAND_MKLABEL = "mklabel";
    public static final String COMMAND_MKLBTYPE = "mklbtype";
    public static final String COMMAND_RMTYPE = "rmtype";
    public static final String COMMAND_UNCHECKOUT = "uncheckout";
    public static final String COMMAND_UNLOCK = "unlock";
    public static final String COMMAND_UPDATE = "update";
    private static int pcnt;
    private String mClearToolDir = "";
    private String mviewPath = null;
    private String mobjSelect = null;
    private boolean mFailonerr = true;

    public final void setClearToolDir(String str) {
        this.mClearToolDir = FileUtils.translatePath(str);
    }

    protected final String getClearToolCommand() {
        String str = this.mClearToolDir;
        if (!str.equals("") && !str.endsWith("/")) {
            str = str + "/";
        }
        return str + CLEARTOOL_EXE;
    }

    public final void setViewPath(String str) {
        this.mviewPath = str;
    }

    public String getViewPath() {
        return this.mviewPath;
    }

    public String getViewPathBasename() {
        return new File(this.mviewPath).getName();
    }

    public final void setObjSelect(String str) {
        this.mobjSelect = str;
    }

    public String getObjSelect() {
        return this.mobjSelect;
    }

    protected int run(Commandline commandline) {
        try {
            Project project = getProject();
            Execute execute = new Execute(new LogStreamHandler((Task) this, 2, 1));
            execute.setAntRun(project);
            execute.setWorkingDirectory(project.getBaseDir());
            execute.setCommandline(commandline.getCommandline());
            return execute.execute();
        } catch (IOException e) {
            throw new BuildException(e, getLocation());
        }
    }

    protected String runS(Commandline commandline) {
        StringBuilder sbAppend = new StringBuilder().append("opts.cc.runS.output");
        int i = pcnt;
        pcnt = i + 1;
        String string = sbAppend.append(i).toString();
        ExecTask execTask = new ExecTask(this);
        Commandline.Argument argumentCreateArg = execTask.createArg();
        execTask.setExecutable(commandline.getExecutable());
        argumentCreateArg.setLine(Commandline.toString(commandline.getArguments()));
        execTask.setOutputproperty(string);
        execTask.execute();
        return getProject().getProperty(string);
    }

    public void setFailOnErr(boolean z) {
        this.mFailonerr = z;
    }

    public boolean getFailOnErr() {
        return this.mFailonerr;
    }
}
