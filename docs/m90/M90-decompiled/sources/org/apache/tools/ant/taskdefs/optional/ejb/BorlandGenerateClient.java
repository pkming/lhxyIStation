package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/* JADX INFO: loaded from: classes3.dex */
public class BorlandGenerateClient extends Task {
    static final String FORK_MODE = "fork";
    static final String JAVA_MODE = "java";
    Path classpath;
    boolean debug = false;
    File ejbjarfile = null;
    File clientjarfile = null;
    String mode = FORK_MODE;
    int version = 4;

    public void setVersion(int i) {
        this.version = i;
    }

    public void setMode(String str) {
        this.mode = str;
    }

    public void setDebug(boolean z) {
        this.debug = z;
    }

    public void setEjbjar(File file) {
        this.ejbjarfile = file;
    }

    public void setClientjar(File file) {
        this.clientjarfile = file;
    }

    public void setClasspath(Path path) {
        Path path2 = this.classpath;
        if (path2 == null) {
            this.classpath = path;
        } else {
            path2.append(path);
        }
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(getProject());
        }
        return this.classpath.createPath();
    }

    public void setClasspathRef(Reference reference) {
        createClasspath().setRefid(reference);
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        File file = this.ejbjarfile;
        if (file == null || file.isDirectory()) {
            throw new BuildException("invalid ejb jar file.");
        }
        File file2 = this.clientjarfile;
        if (file2 == null || file2.isDirectory()) {
            log("invalid or missing client jar file.", 3);
            String absolutePath = this.ejbjarfile.getAbsolutePath();
            this.clientjarfile = new File(absolutePath.substring(0, absolutePath.lastIndexOf(".")) + "client.jar");
        }
        if (this.mode == null) {
            log("mode is null default mode  is java");
            setMode(JAVA_MODE);
        }
        int i = this.version;
        if (i != 5 && i != 4) {
            throw new BuildException("version " + this.version + " is not supported");
        }
        log("client jar file is " + this.clientjarfile);
        if (this.mode.equalsIgnoreCase(FORK_MODE)) {
            executeFork();
        } else {
            executeJava();
        }
    }

    protected void executeJava() throws BuildException {
        try {
            if (this.version == 5) {
                throw new BuildException("java mode is supported only for previous version <=4");
            }
            log("mode : java");
            Java java = new Java(this);
            java.setDir(new File("."));
            java.setClassname("com.inprise.server.commandline.EJBUtilities");
            java.setClasspath(this.classpath.concatSystemClasspath());
            java.setFork(true);
            java.createArg().setValue("generateclient");
            if (this.debug) {
                java.createArg().setValue("-trace");
            }
            java.createArg().setValue("-short");
            java.createArg().setValue("-jarfile");
            java.createArg().setValue(this.ejbjarfile.getAbsolutePath());
            java.createArg().setValue("-single");
            java.createArg().setValue("-clientjarfile");
            java.createArg().setValue(this.clientjarfile.getAbsolutePath());
            log("Calling EJBUtilities", 3);
            java.execute();
        } catch (Exception e) {
            throw new BuildException("Exception while calling generateclient Details: " + e.toString(), e);
        }
    }

    protected void executeFork() throws BuildException {
        if (this.version == 4) {
            executeForkV4();
        }
        if (this.version == 5) {
            executeForkV5();
        }
    }

    protected void executeForkV4() throws BuildException {
        try {
            log("mode : fork 4", 4);
            ExecTask execTask = new ExecTask(this);
            execTask.setDir(new File("."));
            execTask.setExecutable("iastool");
            execTask.createArg().setValue("generateclient");
            if (this.debug) {
                execTask.createArg().setValue("-trace");
            }
            execTask.createArg().setValue("-short");
            execTask.createArg().setValue("-jarfile");
            execTask.createArg().setValue(this.ejbjarfile.getAbsolutePath());
            execTask.createArg().setValue("-single");
            execTask.createArg().setValue("-clientjarfile");
            execTask.createArg().setValue(this.clientjarfile.getAbsolutePath());
            log("Calling iastool", 3);
            execTask.execute();
        } catch (Exception e) {
            throw new BuildException("Exception while calling generateclient Details: " + e.toString(), e);
        }
    }

    protected void executeForkV5() throws BuildException {
        try {
            log("mode : fork 5", 4);
            ExecTask execTask = new ExecTask(this);
            execTask.setDir(new File("."));
            execTask.setExecutable("iastool");
            if (this.debug) {
                execTask.createArg().setValue("-debug");
            }
            execTask.createArg().setValue("-genclient");
            execTask.createArg().setValue("-jars");
            execTask.createArg().setValue(this.ejbjarfile.getAbsolutePath());
            execTask.createArg().setValue("-target");
            execTask.createArg().setValue(this.clientjarfile.getAbsolutePath());
            execTask.createArg().setValue("-cp");
            execTask.createArg().setValue(this.classpath.toString());
            log("Calling iastool", 3);
            execTask.execute();
        } catch (Exception e) {
            throw new BuildException("Exception while calling generateclient Details: " + e.toString(), e);
        }
    }
}
