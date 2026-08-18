package org.apache.tools.ant.taskdefs.optional.ccm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.types.Commandline;

/* JADX INFO: loaded from: classes3.dex */
public class CCMCreateTask extends Continuus implements ExecuteStreamHandler {
    public static final String FLAG_COMMENT = "/synopsis";
    public static final String FLAG_PLATFORM = "/plat";
    public static final String FLAG_RELEASE = "/release";
    public static final String FLAG_RESOLVER = "/resolver";
    public static final String FLAG_SUBSYSTEM = "/subsystem";
    public static final String FLAG_TASK = "/task";
    private String comment = null;
    private String platform = null;
    private String resolver = null;
    private String release = null;
    private String subSystem = null;
    private String task = null;

    @Override // org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void setProcessInputStream(OutputStream outputStream) throws IOException {
    }

    @Override // org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void start() throws IOException {
    }

    @Override // org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void stop() {
    }

    public CCMCreateTask() {
        setCcmAction(Continuus.COMMAND_CREATE_TASK);
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        Commandline commandline = new Commandline();
        commandline.setExecutable(getCcmCommand());
        commandline.createArgument().setValue(getCcmAction());
        checkOptions(commandline);
        if (Execute.isFailure(run(commandline, this))) {
            throw new BuildException("Failed executing: " + commandline.toString(), getLocation());
        }
        Commandline commandline2 = new Commandline();
        commandline2.setExecutable(getCcmCommand());
        commandline2.createArgument().setValue(Continuus.COMMAND_DEFAULT_TASK);
        commandline2.createArgument().setValue(getTask());
        log(commandline.describeCommand(), 4);
        if (run(commandline2) != 0) {
            throw new BuildException("Failed executing: " + commandline2.toString(), getLocation());
        }
    }

    private void checkOptions(Commandline commandline) {
        if (getComment() != null) {
            commandline.createArgument().setValue(FLAG_COMMENT);
            commandline.createArgument().setValue("\"" + getComment() + "\"");
        }
        if (getPlatform() != null) {
            commandline.createArgument().setValue(FLAG_PLATFORM);
            commandline.createArgument().setValue(getPlatform());
        }
        if (getResolver() != null) {
            commandline.createArgument().setValue(FLAG_RESOLVER);
            commandline.createArgument().setValue(getResolver());
        }
        if (getSubSystem() != null) {
            commandline.createArgument().setValue(FLAG_SUBSYSTEM);
            commandline.createArgument().setValue("\"" + getSubSystem() + "\"");
        }
        if (getRelease() != null) {
            commandline.createArgument().setValue(FLAG_RELEASE);
            commandline.createArgument().setValue(getRelease());
        }
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String str) {
        this.comment = str;
    }

    public String getPlatform() {
        return this.platform;
    }

    public void setPlatform(String str) {
        this.platform = str;
    }

    public String getResolver() {
        return this.resolver;
    }

    public void setResolver(String str) {
        this.resolver = str;
    }

    public String getRelease() {
        return this.release;
    }

    public void setRelease(String str) {
        this.release = str;
    }

    public String getSubSystem() {
        return this.subSystem;
    }

    public void setSubSystem(String str) {
        this.subSystem = str;
    }

    public String getTask() {
        return this.task;
    }

    public void setTask(String str) {
        this.task = str;
    }

    @Override // org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void setProcessErrorStream(InputStream inputStream) throws IOException {
        String line = new BufferedReader(new InputStreamReader(inputStream)).readLine();
        if (line != null) {
            log("err " + line, 4);
        }
    }

    @Override // org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void setProcessOutputStream(InputStream inputStream) throws IOException {
        try {
            String line = new BufferedReader(new InputStreamReader(inputStream)).readLine();
            if (line != null) {
                log("buffer:" + line, 4);
                String strTrim = line.substring(line.indexOf(32)).trim();
                setTask(strTrim.substring(0, strTrim.lastIndexOf(32)).trim());
                log("task is " + getTask(), 4);
            }
        } catch (NullPointerException e) {
            log("error procession stream , null pointer exception", 0);
            e.printStackTrace();
            throw new BuildException(e.getClass().getName());
        } catch (Exception e2) {
            log("error procession stream " + e2.getMessage(), 0);
            throw new BuildException(e2.getMessage());
        }
    }
}
