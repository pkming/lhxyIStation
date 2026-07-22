package org.apache.tools.ant.taskdefs;

import android.view.HardwareRenderer;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.taskdefs.launcher.CommandLauncher;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Execute {
    public static final int INVALID = Integer.MAX_VALUE;
    private static final int ONE_SECOND = 1000;
    private static boolean environmentCaseInSensitive;
    private String[] cmdl;
    private String[] env;
    private int exitValue;
    private boolean newEnvironment;
    private Project project;
    private ExecuteStreamHandler streamHandler;
    private boolean useVMLauncher;
    private final ExecuteWatchdog watchdog;
    private File workingDirectory;
    private static String antWorkingDirectory = System.getProperty("user.dir");
    private static Map<String, String> procEnvironment = null;
    private static ProcessDestroyer processDestroyer = new ProcessDestroyer();

    @Deprecated
    public void setSpawn(boolean z) {
    }

    static {
        environmentCaseInSensitive = false;
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            environmentCaseInSensitive = true;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0057 A[Catch: IOException -> 0x00cc, all -> 0x00d4, TRY_LEAVE, TryCatch #2 {IOException -> 0x00cc, blocks: (B:16:0x0024, B:18:0x0057, B:22:0x0060, B:23:0x0062, B:25:0x0069, B:28:0x0074, B:29:0x0086, B:31:0x009e, B:34:0x00b7), top: B:47:0x0024, outer: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:21:0x005f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static synchronized java.util.Map<java.lang.String, java.lang.String> getEnvironmentVariables() {
        /*
            Method dump skipped, instruction units count: 215
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.taskdefs.Execute.getEnvironmentVariables():java.util.Map");
    }

    @Deprecated
    public static synchronized Vector<String> getProcEnvironment() {
        Vector<String> vector;
        vector = new Vector<>();
        for (Map.Entry<String, String> entry : getEnvironmentVariables().entrySet()) {
            vector.add(entry.getKey() + "=" + entry.getValue());
        }
        return vector;
    }

    private static String[] getProcEnvCommand() {
        if (Os.isFamily(Os.FAMILY_OS2)) {
            return new String[]{"cmd", "/c", "set"};
        }
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            if (Os.isFamily(Os.FAMILY_9X)) {
                return new String[]{"command.com", "/c", "set"};
            }
            return new String[]{"cmd", "/c", "set"};
        }
        if (Os.isFamily(Os.FAMILY_ZOS) || Os.isFamily(Os.FAMILY_UNIX)) {
            String[] strArr = new String[1];
            if (new File("/bin/env").canRead()) {
                strArr[0] = "/bin/env";
            } else if (new File("/usr/bin/env").canRead()) {
                strArr[0] = "/usr/bin/env";
            } else {
                strArr[0] = "env";
            }
            return strArr;
        }
        if (Os.isFamily(Os.FAMILY_NETWARE) || Os.isFamily(Os.FAMILY_OS400)) {
            return new String[]{"env"};
        }
        if (Os.isFamily(Os.FAMILY_VMS)) {
            return new String[]{HardwareRenderer.OVERDRAW_PROPERTY_SHOW, "logical"};
        }
        return null;
    }

    public static String toString(ByteArrayOutputStream byteArrayOutputStream) {
        if (Os.isFamily(Os.FAMILY_ZOS)) {
            return byteArrayOutputStream.toString("Cp1047");
        }
        if (Os.isFamily(Os.FAMILY_OS400)) {
            return byteArrayOutputStream.toString("Cp500");
        }
        return byteArrayOutputStream.toString();
    }

    public Execute() {
        this(new PumpStreamHandler(), null);
    }

    public Execute(ExecuteStreamHandler executeStreamHandler) {
        this(executeStreamHandler, null);
    }

    public Execute(ExecuteStreamHandler executeStreamHandler, ExecuteWatchdog executeWatchdog) {
        this.cmdl = null;
        this.env = null;
        this.exitValue = Integer.MAX_VALUE;
        this.workingDirectory = null;
        this.project = null;
        this.newEnvironment = false;
        this.useVMLauncher = true;
        setStreamHandler(executeStreamHandler);
        this.watchdog = executeWatchdog;
        if (Os.isFamily(Os.FAMILY_VMS)) {
            this.useVMLauncher = false;
        }
    }

    public void setStreamHandler(ExecuteStreamHandler executeStreamHandler) {
        this.streamHandler = executeStreamHandler;
    }

    public String[] getCommandline() {
        return this.cmdl;
    }

    public void setCommandline(String[] strArr) {
        this.cmdl = strArr;
    }

    public void setNewenvironment(boolean z) {
        this.newEnvironment = z;
    }

    public String[] getEnvironment() {
        String[] strArr = this.env;
        return (strArr == null || this.newEnvironment) ? strArr : patchEnvironment();
    }

    public void setEnvironment(String[] strArr) {
        this.env = strArr;
    }

    public void setWorkingDirectory(File file) {
        if (file == null || file.getAbsolutePath().equals(antWorkingDirectory)) {
            file = null;
        }
        this.workingDirectory = file;
    }

    public File getWorkingDirectory() {
        File file = this.workingDirectory;
        return file == null ? new File(antWorkingDirectory) : file;
    }

    public void setAntRun(Project project) throws BuildException {
        this.project = project;
    }

    public void setVMLauncher(boolean z) {
        this.useVMLauncher = z;
    }

    public static Process launch(Project project, String[] strArr, String[] strArr2, File file, boolean z) throws IOException {
        if (file != null && !file.exists()) {
            throw new BuildException(file + " doesn't exist.");
        }
        CommandLauncher vMLauncher = CommandLauncher.getVMLauncher(project);
        if (!z || vMLauncher == null) {
            vMLauncher = CommandLauncher.getShellLauncher(project);
        }
        return vMLauncher.exec(project, strArr, strArr2, file);
    }

    public int execute() throws IOException {
        File file = this.workingDirectory;
        if (file != null && !file.exists()) {
            throw new BuildException(this.workingDirectory + " doesn't exist.");
        }
        Process processLaunch = launch(this.project, getCommandline(), getEnvironment(), this.workingDirectory, this.useVMLauncher);
        try {
            this.streamHandler.setProcessInputStream(processLaunch.getOutputStream());
            this.streamHandler.setProcessOutputStream(processLaunch.getInputStream());
            this.streamHandler.setProcessErrorStream(processLaunch.getErrorStream());
            this.streamHandler.start();
            try {
                try {
                    processDestroyer.add(processLaunch);
                    ExecuteWatchdog executeWatchdog = this.watchdog;
                    if (executeWatchdog != null) {
                        executeWatchdog.start(processLaunch);
                    }
                    waitFor(processLaunch);
                    ExecuteWatchdog executeWatchdog2 = this.watchdog;
                    if (executeWatchdog2 != null) {
                        executeWatchdog2.stop();
                    }
                    this.streamHandler.stop();
                    closeStreams(processLaunch);
                    ExecuteWatchdog executeWatchdog3 = this.watchdog;
                    if (executeWatchdog3 != null) {
                        executeWatchdog3.checkException();
                    }
                    return getExitValue();
                } finally {
                    processDestroyer.remove(processLaunch);
                }
            } catch (ThreadDeath e) {
                processLaunch.destroy();
                throw e;
            }
        } catch (IOException e2) {
            processLaunch.destroy();
            throw e2;
        }
    }

    public void spawn() throws IOException {
        File file = this.workingDirectory;
        if (file != null && !file.exists()) {
            throw new BuildException(this.workingDirectory + " doesn't exist.");
        }
        Process processLaunch = launch(this.project, getCommandline(), getEnvironment(), this.workingDirectory, this.useVMLauncher);
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException unused) {
                this.project.log("interruption in the sleep after having spawned a process", 3);
            }
        }
        PumpStreamHandler pumpStreamHandler = new PumpStreamHandler(new OutputStream() { // from class: org.apache.tools.ant.taskdefs.Execute.1
            @Override // java.io.OutputStream
            public void write(int i) throws IOException {
            }
        });
        pumpStreamHandler.setProcessErrorStream(processLaunch.getErrorStream());
        pumpStreamHandler.setProcessOutputStream(processLaunch.getInputStream());
        pumpStreamHandler.start();
        processLaunch.getOutputStream().close();
        this.project.log("spawned process " + processLaunch.toString(), 3);
    }

    protected void waitFor(Process process) {
        try {
            process.waitFor();
            setExitValue(process.exitValue());
        } catch (InterruptedException unused) {
            process.destroy();
        }
    }

    protected void setExitValue(int i) {
        this.exitValue = i;
    }

    public int getExitValue() {
        return this.exitValue;
    }

    public static boolean isFailure(int i) {
        if (Os.isFamily(Os.FAMILY_VMS)) {
            if (i % 2 == 0) {
                return true;
            }
        } else if (i != 0) {
            return true;
        }
        return false;
    }

    public boolean isFailure() {
        return isFailure(getExitValue());
    }

    public boolean killedProcess() {
        ExecuteWatchdog executeWatchdog = this.watchdog;
        return executeWatchdog != null && executeWatchdog.killedProcess();
    }

    private String[] patchEnvironment() {
        if (Os.isFamily(Os.FAMILY_VMS)) {
            return this.env;
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap(getEnvironmentVariables());
        int i = 0;
        while (true) {
            String[] strArr = this.env;
            if (i >= strArr.length) {
                break;
            }
            String str = strArr[i];
            String strSubstring = str.substring(0, str.indexOf(61));
            if (linkedHashMap.remove(strSubstring) == null && environmentCaseInSensitive) {
                Iterator it = linkedHashMap.keySet().iterator();
                while (true) {
                    if (it.hasNext()) {
                        String str2 = (String) it.next();
                        if (str2.toLowerCase().equals(strSubstring.toLowerCase())) {
                            strSubstring = str2;
                            break;
                        }
                    }
                }
            }
            linkedHashMap.put(strSubstring, str.substring(strSubstring.length() + 1));
            i++;
        }
        ArrayList arrayList = new ArrayList();
        for (Map.Entry entry : linkedHashMap.entrySet()) {
            arrayList.add(((String) entry.getKey()) + "=" + ((String) entry.getValue()));
        }
        return (String[]) arrayList.toArray(new String[linkedHashMap.size()]);
    }

    public static void runCommand(Task task, String[] strArr) throws BuildException {
        try {
            task.log(Commandline.describeCommand(strArr), 3);
            Execute execute = new Execute(new LogStreamHandler(task, 2, 0));
            execute.setAntRun(task.getProject());
            execute.setCommandline(strArr);
            int iExecute = execute.execute();
            if (isFailure(iExecute)) {
                throw new BuildException(strArr[0] + " failed with return code " + iExecute, task.getLocation());
            }
        } catch (IOException e) {
            throw new BuildException("Could not launch " + strArr[0] + ": " + e, task.getLocation());
        }
    }

    public static void closeStreams(Process process) {
        FileUtils.close(process.getInputStream());
        FileUtils.close(process.getOutputStream());
        FileUtils.close(process.getErrorStream());
    }

    private static Map<String, String> getVMSLogicals(BufferedReader bufferedReader) throws IOException {
        HashMap map = new HashMap();
        String str = null;
        String strSubstring = null;
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            if (line.startsWith("\t=")) {
                if (str != null) {
                    strSubstring = strSubstring + "," + line.substring(4, line.length() - 1);
                }
            } else if (line.startsWith("  \"")) {
                if (str != null) {
                    map.put(str, strSubstring);
                }
                int iIndexOf = line.indexOf(61);
                String strSubstring2 = line.substring(3, iIndexOf - 2);
                if (map.containsKey(strSubstring2)) {
                    str = null;
                } else {
                    strSubstring = line.substring(iIndexOf + 3, line.length() - 1);
                    str = strSubstring2;
                }
            }
        }
        if (str != null) {
            map.put(str, strSubstring);
        }
        return map;
    }
}
