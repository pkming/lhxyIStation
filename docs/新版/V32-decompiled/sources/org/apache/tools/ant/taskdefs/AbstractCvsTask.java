package org.apache.tools.ant.taskdefs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSSConstants;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.StringUtils;

/* JADX INFO: loaded from: classes3.dex */
public abstract class AbstractCvsTask extends Task {
    private static final String DEFAULT_COMMAND = "checkout";
    public static final int DEFAULT_COMPRESSION_LEVEL = 3;
    private static final int MAXIMUM_COMRESSION_LEVEL = 9;
    private String cvsPackage;
    private String cvsRoot;
    private String cvsRsh;
    private File dest;
    private File error;
    private OutputStream errorStream;
    private ExecuteStreamHandler executeStreamHandler;
    private File output;
    private OutputStream outputStream;
    private String tag;
    private Commandline cmd = new Commandline();
    private ArrayList<Module> modules = new ArrayList<>();
    private Vector<Commandline> vecCommandlines = new Vector<>();
    private String command = null;
    private boolean quiet = false;
    private boolean reallyquiet = false;
    private int compression = 0;
    private boolean noexec = false;
    private int port = 0;
    private File passFile = null;
    private boolean append = false;
    private boolean failOnError = false;

    public void setExecuteStreamHandler(ExecuteStreamHandler executeStreamHandler) {
        this.executeStreamHandler = executeStreamHandler;
    }

    protected ExecuteStreamHandler getExecuteStreamHandler() {
        if (this.executeStreamHandler == null) {
            setExecuteStreamHandler(new PumpStreamHandler(getOutputStream(), getErrorStream()));
        }
        return this.executeStreamHandler;
    }

    protected void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    protected OutputStream getOutputStream() {
        if (this.outputStream == null) {
            if (this.output != null) {
                try {
                    setOutputStream(new PrintStream(new BufferedOutputStream(new FileOutputStream(this.output.getPath(), this.append))));
                } catch (IOException e) {
                    throw new BuildException(e, getLocation());
                }
            } else {
                setOutputStream(new LogOutputStream((Task) this, 2));
            }
        }
        return this.outputStream;
    }

    protected void setErrorStream(OutputStream outputStream) {
        this.errorStream = outputStream;
    }

    protected OutputStream getErrorStream() {
        if (this.errorStream == null) {
            if (this.error != null) {
                try {
                    setErrorStream(new PrintStream(new BufferedOutputStream(new FileOutputStream(this.error.getPath(), this.append))));
                } catch (IOException e) {
                    throw new BuildException(e, getLocation());
                }
            } else {
                setErrorStream(new LogOutputStream((Task) this, 1));
            }
        }
        return this.errorStream;
    }

    protected void runCommand(Commandline commandline) throws BuildException {
        Environment environment = new Environment();
        if (this.port > 0) {
            Environment.Variable variable = new Environment.Variable();
            variable.setKey("CVS_CLIENT_PORT");
            variable.setValue(String.valueOf(this.port));
            environment.addVariable(variable);
            Environment.Variable variable2 = new Environment.Variable();
            variable2.setKey("CVS_PSERVER_PORT");
            variable2.setValue(String.valueOf(this.port));
            environment.addVariable(variable2);
        }
        if (this.passFile == null) {
            File file = new File(System.getProperty("cygwin.user.home", System.getProperty("user.home")) + File.separatorChar + ".cvspass");
            if (file.exists()) {
                setPassfile(file);
            }
        }
        File file2 = this.passFile;
        if (file2 != null) {
            if (file2.isFile() && this.passFile.canRead()) {
                Environment.Variable variable3 = new Environment.Variable();
                variable3.setKey("CVS_PASSFILE");
                variable3.setValue(String.valueOf(this.passFile));
                environment.addVariable(variable3);
                log("Using cvs passfile: " + String.valueOf(this.passFile), 3);
            } else if (!this.passFile.canRead()) {
                log("cvs passfile: " + String.valueOf(this.passFile) + " ignored as it is not readable", 1);
            } else {
                log("cvs passfile: " + String.valueOf(this.passFile) + " ignored as it is not a file", 1);
            }
        }
        if (this.cvsRsh != null) {
            Environment.Variable variable4 = new Environment.Variable();
            variable4.setKey("CVS_RSH");
            variable4.setValue(String.valueOf(this.cvsRsh));
            environment.addVariable(variable4);
        }
        Execute execute = new Execute(getExecuteStreamHandler(), null);
        execute.setAntRun(getProject());
        if (this.dest == null) {
            this.dest = getProject().getBaseDir();
        }
        if (!this.dest.exists()) {
            this.dest.mkdirs();
        }
        execute.setWorkingDirectory(this.dest);
        execute.setCommandline(commandline.getCommandline());
        execute.setEnvironment(environment.getVariables());
        try {
            String strExecuteToString = executeToString(execute);
            log(strExecuteToString, 3);
            int iExecute = execute.execute();
            log("retCode=" + iExecute, 4);
            if (this.failOnError && Execute.isFailure(iExecute)) {
                throw new BuildException("cvs exited with error code " + iExecute + StringUtils.LINE_SEP + "Command line was [" + strExecuteToString + "]", getLocation());
            }
        } catch (IOException e) {
            if (this.failOnError) {
                throw new BuildException(e, getLocation());
            }
            log("Caught exception: " + e.getMessage(), 1);
        } catch (BuildException e2) {
            e = e2;
            if (this.failOnError) {
                throw e;
            }
            Throwable cause = e.getCause();
            if (cause != null) {
                e = cause;
            }
            log("Caught exception: " + e.getMessage(), 1);
        } catch (Exception e3) {
            if (this.failOnError) {
                throw new BuildException(e3, getLocation());
            }
            log("Caught exception: " + e3.getMessage(), 1);
        }
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        String command = getCommand();
        if (getCommand() == null && this.vecCommandlines.size() == 0) {
            setCommand("checkout");
        }
        String command2 = getCommand();
        Commandline commandline = null;
        if (command2 != null) {
            commandline = (Commandline) this.cmd.clone();
            commandline.createArgument(true).setLine(command2);
            addConfiguredCommandline(commandline, true);
        }
        try {
            int size = this.vecCommandlines.size();
            for (int i = 0; i < size; i++) {
                runCommand(this.vecCommandlines.elementAt(i));
            }
        } finally {
            if (commandline != null) {
                removeCommandline(commandline);
            }
            setCommand(command);
            FileUtils.close(this.outputStream);
            FileUtils.close(this.errorStream);
        }
    }

    private String executeToString(Execute execute) {
        StringBuffer stringBufferRemoveCvsPassword = removeCvsPassword(Commandline.describeCommand(execute.getCommandline()));
        String str = StringUtils.LINE_SEP;
        String[] environment = execute.getEnvironment();
        if (environment != null) {
            stringBufferRemoveCvsPassword.append(str);
            stringBufferRemoveCvsPassword.append(str);
            stringBufferRemoveCvsPassword.append("environment:");
            stringBufferRemoveCvsPassword.append(str);
            for (String str2 : environment) {
                stringBufferRemoveCvsPassword.append(str);
                stringBufferRemoveCvsPassword.append("\t");
                stringBufferRemoveCvsPassword.append(str2);
            }
        }
        return stringBufferRemoveCvsPassword.toString();
    }

    private StringBuffer removeCvsPassword(String str) {
        StringBuffer stringBuffer = new StringBuffer(str);
        int iIndexOf = str.indexOf("-d:");
        if (iIndexOf >= 0) {
            str.indexOf("@", iIndexOf);
            int iIndexOf2 = str.indexOf(":", iIndexOf);
            int iIndexOf3 = str.indexOf(":", str.indexOf(":", iIndexOf2 + 1) + 1);
            int iIndexOf4 = str.indexOf("@", iIndexOf);
            if (iIndexOf4 >= 0 && iIndexOf3 > iIndexOf2 && iIndexOf3 < iIndexOf4) {
                int i = iIndexOf3 + 1;
                while (i < iIndexOf4) {
                    int i2 = i + 1;
                    stringBuffer.replace(i, i2, "*");
                    i = i2;
                }
            }
        }
        return stringBuffer;
    }

    public void setCvsRoot(String str) {
        if (str != null && str.trim().equals("")) {
            str = null;
        }
        this.cvsRoot = str;
    }

    public String getCvsRoot() {
        return this.cvsRoot;
    }

    public void setCvsRsh(String str) {
        if (str != null && str.trim().equals("")) {
            str = null;
        }
        this.cvsRsh = str;
    }

    public String getCvsRsh() {
        return this.cvsRsh;
    }

    public void setPort(int i) {
        this.port = i;
    }

    public int getPort() {
        return this.port;
    }

    public void setPassfile(File file) {
        this.passFile = file;
    }

    public File getPassFile() {
        return this.passFile;
    }

    public void setDest(File file) {
        this.dest = file;
    }

    public File getDest() {
        return this.dest;
    }

    public void setPackage(String str) {
        this.cvsPackage = str;
    }

    public String getPackage() {
        return this.cvsPackage;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String str) {
        if (str == null || str.trim().length() <= 0) {
            return;
        }
        this.tag = str;
        addCommandArgument("-r" + str);
    }

    public void addCommandArgument(String str) {
        addCommandArgument(this.cmd, str);
    }

    public void addCommandArgument(Commandline commandline, String str) {
        commandline.createArgument().setValue(str);
    }

    public void setDate(String str) {
        if (str == null || str.trim().length() <= 0) {
            return;
        }
        addCommandArgument(MSVSSConstants.FLAG_CODEDIFF);
        addCommandArgument(str);
    }

    public void setCommand(String str) {
        this.command = str;
    }

    public String getCommand() {
        return this.command;
    }

    public void setQuiet(boolean z) {
        this.quiet = z;
    }

    public void setReallyquiet(boolean z) {
        this.reallyquiet = z;
    }

    public void setNoexec(boolean z) {
        this.noexec = z;
    }

    public void setOutput(File file) {
        this.output = file;
    }

    public void setError(File file) {
        this.error = file;
    }

    public void setAppend(boolean z) {
        this.append = z;
    }

    public void setFailOnError(boolean z) {
        this.failOnError = z;
    }

    protected void configureCommandline(Commandline commandline) {
        if (commandline == null) {
            return;
        }
        commandline.setExecutable("cvs");
        if (this.cvsPackage != null) {
            commandline.createArgument().setLine(this.cvsPackage);
        }
        Iterator<Module> it = this.modules.iterator();
        while (it.hasNext()) {
            commandline.createArgument().setValue(it.next().getName());
        }
        int i = this.compression;
        if (i > 0 && i <= 9) {
            commandline.createArgument(true).setValue("-z" + this.compression);
        }
        if (this.quiet && !this.reallyquiet) {
            commandline.createArgument(true).setValue("-q");
        }
        if (this.reallyquiet) {
            commandline.createArgument(true).setValue("-Q");
        }
        if (this.noexec) {
            commandline.createArgument(true).setValue("-n");
        }
        if (this.cvsRoot != null) {
            commandline.createArgument(true).setLine("-d" + this.cvsRoot);
        }
    }

    protected void removeCommandline(Commandline commandline) {
        this.vecCommandlines.removeElement(commandline);
    }

    public void addConfiguredCommandline(Commandline commandline) {
        addConfiguredCommandline(commandline, false);
    }

    public void addConfiguredCommandline(Commandline commandline, boolean z) {
        if (commandline == null) {
            return;
        }
        configureCommandline(commandline);
        if (z) {
            this.vecCommandlines.insertElementAt(commandline, 0);
        } else {
            this.vecCommandlines.addElement(commandline);
        }
    }

    public void setCompressionLevel(int i) {
        this.compression = i;
    }

    public void setCompression(boolean z) {
        setCompressionLevel(z ? 3 : 0);
    }

    public void addModule(Module module) {
        this.modules.add(module);
    }

    protected List<Module> getModules() {
        return (List) this.modules.clone();
    }

    public static final class Module {
        private String name;

        public void setName(String str) {
            this.name = str;
        }

        public String getName() {
            return this.name;
        }
    }
}
