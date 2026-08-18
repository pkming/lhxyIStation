package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.RedirectorElement;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ExecTask extends Task {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private File dir;
    private File error;
    private String executable;
    private File input;
    private String inputString;
    private String os;
    private String osFamily;
    private File output;
    protected RedirectorElement redirectorElement;
    private String resultProperty;
    protected boolean failOnError = false;
    protected boolean newEnvironment = false;
    private Long timeout = null;
    private Environment env = new Environment();
    protected Commandline cmdl = new Commandline();
    private boolean failIfExecFails = true;
    private boolean resolveExecutable = false;
    private boolean searchPath = false;
    private boolean spawn = false;
    private boolean incompatibleWithSpawn = false;
    protected Redirector redirector = new Redirector((Task) this);
    private boolean vmLauncher = true;

    protected void logFlush() {
    }

    public ExecTask() {
    }

    public ExecTask(Task task) {
        bindToOwner(task);
    }

    public void setSpawn(boolean z) {
        this.spawn = z;
    }

    public void setTimeout(Long l) {
        this.timeout = l;
        this.incompatibleWithSpawn = (l != null) | this.incompatibleWithSpawn;
    }

    public void setTimeout(Integer num) {
        setTimeout(num == null ? null : new Long(num.intValue()));
    }

    public void setExecutable(String str) {
        this.executable = str;
        this.cmdl.setExecutable(str);
    }

    public void setDir(File file) {
        this.dir = file;
    }

    public void setOs(String str) {
        this.os = str;
    }

    public final String getOs() {
        return this.os;
    }

    public void setCommand(Commandline commandline) {
        log("The command attribute is deprecated.\nPlease use the executable attribute and nested arg elements.", 1);
        this.cmdl = commandline;
    }

    public void setOutput(File file) {
        this.output = file;
        this.incompatibleWithSpawn = true;
    }

    public void setInput(File file) {
        if (this.inputString != null) {
            throw new BuildException("The \"input\" and \"inputstring\" attributes cannot both be specified");
        }
        this.input = file;
        this.incompatibleWithSpawn = true;
    }

    public void setInputString(String str) {
        if (this.input != null) {
            throw new BuildException("The \"input\" and \"inputstring\" attributes cannot both be specified");
        }
        this.inputString = str;
        this.incompatibleWithSpawn = true;
    }

    public void setLogError(boolean z) {
        this.redirector.setLogError(z);
        this.incompatibleWithSpawn = z | this.incompatibleWithSpawn;
    }

    public void setError(File file) {
        this.error = file;
        this.incompatibleWithSpawn = true;
    }

    public void setOutputproperty(String str) {
        this.redirector.setOutputProperty(str);
        this.incompatibleWithSpawn = true;
    }

    public void setErrorProperty(String str) {
        this.redirector.setErrorProperty(str);
        this.incompatibleWithSpawn = true;
    }

    public void setFailonerror(boolean z) {
        this.failOnError = z;
        this.incompatibleWithSpawn = z | this.incompatibleWithSpawn;
    }

    public void setNewenvironment(boolean z) {
        this.newEnvironment = z;
    }

    public void setResolveExecutable(boolean z) {
        this.resolveExecutable = z;
    }

    public void setSearchPath(boolean z) {
        this.searchPath = z;
    }

    public boolean getResolveExecutable() {
        return this.resolveExecutable;
    }

    public void addEnv(Environment.Variable variable) {
        this.env.addVariable(variable);
    }

    public Commandline.Argument createArg() {
        return this.cmdl.createArgument();
    }

    public void setResultProperty(String str) {
        this.resultProperty = str;
        this.incompatibleWithSpawn = true;
    }

    protected void maybeSetResultPropertyValue(int i) {
        if (this.resultProperty != null) {
            getProject().setNewProperty(this.resultProperty, Integer.toString(i));
        }
    }

    public void setFailIfExecutionFails(boolean z) {
        this.failIfExecFails = z;
        this.incompatibleWithSpawn = z | this.incompatibleWithSpawn;
    }

    public void setAppend(boolean z) {
        this.redirector.setAppend(z);
        this.incompatibleWithSpawn = z | this.incompatibleWithSpawn;
    }

    public void addConfiguredRedirector(RedirectorElement redirectorElement) {
        if (this.redirectorElement != null) {
            throw new BuildException("cannot have > 1 nested <redirector>s");
        }
        this.redirectorElement = redirectorElement;
        this.incompatibleWithSpawn = true;
    }

    public void setOsFamily(String str) {
        this.osFamily = str.toLowerCase(Locale.ENGLISH);
    }

    public final String getOsFamily() {
        return this.osFamily;
    }

    protected String resolveExecutable(String str, boolean z) {
        String path;
        if (!this.resolveExecutable) {
            return str;
        }
        File fileResolveFile = getProject().resolveFile(str);
        if (fileResolveFile.exists()) {
            return fileResolveFile.getAbsolutePath();
        }
        File file = this.dir;
        if (file != null) {
            File fileResolveFile2 = FILE_UTILS.resolveFile(file, str);
            if (fileResolveFile2.exists()) {
                return fileResolveFile2.getAbsolutePath();
            }
        }
        if (z) {
            Path path2 = null;
            String[] variables = this.env.getVariables();
            if (variables != null) {
                int i = 0;
                while (true) {
                    if (i >= variables.length) {
                        break;
                    }
                    if (isPath(variables[i])) {
                        path2 = new Path(getProject(), getPath(variables[i]));
                        break;
                    }
                    i++;
                }
            }
            if (path2 == null && (path = getPath(Execute.getEnvironmentVariables())) != null) {
                path2 = new Path(getProject(), path);
            }
            if (path2 != null) {
                for (String str2 : path2.list()) {
                    File fileResolveFile3 = FILE_UTILS.resolveFile(new File(str2), str);
                    if (fileResolveFile3.exists()) {
                        return fileResolveFile3.getAbsolutePath();
                    }
                }
            }
        }
        return str;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (isValidOs()) {
            File file = this.dir;
            this.cmdl.setExecutable(resolveExecutable(this.executable, this.searchPath));
            checkConfiguration();
            try {
                runExec(prepareExec());
            } finally {
                this.dir = file;
            }
        }
    }

    protected void checkConfiguration() throws BuildException {
        if (this.cmdl.getExecutable() == null) {
            throw new BuildException("no executable specified", getLocation());
        }
        File file = this.dir;
        if (file != null && !file.exists()) {
            throw new BuildException("The directory " + this.dir + " does not exist");
        }
        File file2 = this.dir;
        if (file2 != null && !file2.isDirectory()) {
            throw new BuildException(this.dir + " is not a directory");
        }
        if (this.spawn && this.incompatibleWithSpawn) {
            getProject().log("spawn does not allow attributes related to input, output, error, result", 0);
            getProject().log("spawn also does not allow timeout", 0);
            getProject().log("finally, spawn is not compatible with a nested I/O <redirector>", 0);
            throw new BuildException("You have used an attribute or nested element which is not compatible with spawn");
        }
        setupRedirector();
    }

    protected void setupRedirector() {
        this.redirector.setInput(this.input);
        this.redirector.setInputString(this.inputString);
        this.redirector.setOutput(this.output);
        this.redirector.setError(this.error);
    }

    protected boolean isValidOs() {
        String str = this.osFamily;
        if (str != null && !Os.isFamily(str)) {
            return false;
        }
        String property = System.getProperty("os.name");
        log("Current OS is " + property, 3);
        String str2 = this.os;
        if (str2 == null || str2.indexOf(property) >= 0) {
            return true;
        }
        log("This OS, " + property + " was not found in the specified list of valid OSes: " + this.os, 3);
        return false;
    }

    public void setVMLauncher(boolean z) {
        this.vmLauncher = z;
    }

    protected Execute prepareExec() throws BuildException {
        if (this.dir == null) {
            this.dir = getProject().getBaseDir();
        }
        RedirectorElement redirectorElement = this.redirectorElement;
        if (redirectorElement != null) {
            redirectorElement.configure(this.redirector);
        }
        Execute execute = new Execute(createHandler(), createWatchdog());
        execute.setAntRun(getProject());
        execute.setWorkingDirectory(this.dir);
        execute.setVMLauncher(this.vmLauncher);
        String[] variables = this.env.getVariables();
        if (variables != null) {
            for (String str : variables) {
                log("Setting environment variable: " + str, 3);
            }
        }
        execute.setNewenvironment(this.newEnvironment);
        execute.setEnvironment(variables);
        return execute;
    }

    protected final void runExecute(Execute execute) throws IOException {
        if (!this.spawn) {
            int iExecute = execute.execute();
            if (execute.killedProcess()) {
                if (this.failOnError) {
                    throw new BuildException("Timeout: killed the sub-process");
                }
                log("Timeout: killed the sub-process", 1);
            }
            maybeSetResultPropertyValue(iExecute);
            this.redirector.complete();
            if (Execute.isFailure(iExecute)) {
                if (this.failOnError) {
                    throw new BuildException(getTaskType() + " returned: " + iExecute, getLocation());
                }
                log("Result: " + iExecute, 0);
                return;
            }
            return;
        }
        execute.spawn();
    }

    protected void runExec(Execute execute) throws BuildException {
        log(this.cmdl.describeCommand(), 3);
        execute.setCommandline(this.cmdl.getCommandline());
        try {
            try {
                runExecute(execute);
            } catch (IOException e) {
                if (this.failIfExecFails) {
                    throw new BuildException("Execute failed: " + e.toString(), e, getLocation());
                }
                log("Execute failed: " + e.toString(), 0);
            }
        } finally {
            logFlush();
        }
    }

    protected ExecuteStreamHandler createHandler() throws BuildException {
        return this.redirector.createHandler();
    }

    protected ExecuteWatchdog createWatchdog() throws BuildException {
        if (this.timeout == null) {
            return null;
        }
        return new ExecuteWatchdog(this.timeout.longValue());
    }

    private boolean isPath(String str) {
        return str.startsWith("PATH=") || str.startsWith("Path=");
    }

    private String getPath(String str) {
        return str.substring(5);
    }

    private String getPath(Map<String, String> map) {
        String str = map.get("PATH");
        return str != null ? str : map.get("Path");
    }
}
