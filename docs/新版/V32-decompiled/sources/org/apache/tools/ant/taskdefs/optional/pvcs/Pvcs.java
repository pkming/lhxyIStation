package org.apache.tools.ant.taskdefs.optional.pvcs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSSConstants;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Pvcs extends Task {
    private static final String GET_EXE = "get";
    private static final String PCLI_EXE = "pcli";
    private static final int POS_1 = 1;
    private static final int POS_2 = 2;
    private static final int POS_3 = 3;
    private String config;
    private String revision;
    private String userId;
    private String pvcsProject = null;
    private Vector pvcsProjects = new Vector();
    private String workspace = null;
    private String repository = null;
    private String pvcsbin = null;
    private String force = null;
    private String promotiongroup = null;
    private String label = null;
    private boolean ignorerc = false;
    private boolean updateOnly = false;
    private String lineStart = "\"P:";
    private String filenameFormat = "{0}-arc({1})";

    protected int runCmd(Commandline commandline, ExecuteStreamHandler executeStreamHandler) {
        try {
            Project project = getProject();
            Execute execute = new Execute(executeStreamHandler);
            execute.setAntRun(project);
            execute.setWorkingDirectory(project.getBaseDir());
            execute.setCommandline(commandline.getCommandline());
            return execute.execute();
        } catch (IOException e) {
            throw new BuildException("Failed executing: " + commandline.toString() + ". Exception: " + e.getMessage(), getLocation());
        }
    }

    private String getExecutable(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        if (getPvcsbin() != null) {
            if (this.pvcsbin.endsWith(File.separator)) {
                stringBuffer.append(this.pvcsbin);
            } else {
                stringBuffer.append(this.pvcsbin).append(File.separator);
            }
        }
        return stringBuffer.append(str).toString();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r9v1 */
    /* JADX WARN: Type inference failed for: r9v11 */
    /* JADX WARN: Type inference failed for: r9v12, types: [java.io.File] */
    /* JADX WARN: Type inference failed for: r9v13, types: [java.lang.String] */
    @Override // org.apache.tools.ant.Task
    public void execute() throws Throwable {
        File file;
        File file2;
        FileOutputStream fileOutputStream;
        String str = this.repository;
        if (str == null || str.trim().equals("")) {
            throw new BuildException("Required argument repository not specified");
        }
        Commandline commandline = new Commandline();
        commandline.setExecutable(getExecutable(PCLI_EXE));
        commandline.createArgument().setValue("lvf");
        commandline.createArgument().setValue("-z");
        commandline.createArgument().setValue("-aw");
        if (getWorkspace() != null) {
            commandline.createArgument().setValue("-sp" + getWorkspace());
        }
        commandline.createArgument().setValue("-pr" + getRepository());
        String userId = getUserId();
        if (userId != null) {
            file = "-id";
            commandline.createArgument().setValue("-id" + userId);
        }
        if (getPvcsproject() == null && getPvcsprojects().isEmpty()) {
            this.pvcsProject = "/";
        }
        if (getPvcsproject() != null) {
            commandline.createArgument().setValue(getPvcsproject());
        }
        if (!getPvcsprojects().isEmpty()) {
            Enumeration enumerationElements = getPvcsprojects().elements();
            while (enumerationElements.hasMoreElements()) {
                String name = ((PvcsProject) enumerationElements.nextElement()).getName();
                if (name == null || name.trim().equals("")) {
                    throw new BuildException("name is a required attribute of pvcsproject");
                }
                commandline.createArgument().setValue(name);
            }
        }
        File file3 = null;
        try {
            try {
                Random random = new Random(System.currentTimeMillis());
                file2 = new File("pvcs_ant_" + random.nextLong() + ".log");
                try {
                    fileOutputStream = new FileOutputStream(file2);
                    file = new File("pvcs_ant_" + random.nextLong() + ".log");
                } catch (FileNotFoundException e) {
                    e = e;
                } catch (IOException e2) {
                    e = e2;
                } catch (ParseException e3) {
                    e = e3;
                } catch (Throwable th) {
                    th = th;
                    file = 0;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (FileNotFoundException e4) {
            e = e4;
        } catch (IOException e5) {
            e = e5;
        } catch (ParseException e6) {
            e = e6;
        } catch (Throwable th3) {
            th = th3;
            file = 0;
        }
        try {
            log(commandline.describeCommand(), 3);
            try {
                int iRunCmd = runCmd(commandline, new PumpStreamHandler(fileOutputStream, new LogOutputStream((Task) this, 1)));
                FileUtils.close(fileOutputStream);
                if (Execute.isFailure(iRunCmd) && !this.ignorerc) {
                    throw new BuildException("Failed executing: " + commandline.toString(), getLocation());
                }
                if (!file2.exists()) {
                    throw new BuildException("Communication between ant and pvcs failed. No output generated from executing PVCS commandline interface \"pcli\" and \"get\"");
                }
                log("Creating folders", 2);
                createFolders(file2);
                massagePCLI(file2, file);
                commandline.clearArgs();
                commandline.setExecutable(getExecutable(GET_EXE));
                if (getConfig() != null && getConfig().length() > 0) {
                    commandline.createArgument().setValue("-c" + getConfig());
                }
                if (getForce() != null && getForce().equals("yes")) {
                    commandline.createArgument().setValue("-Y");
                } else {
                    commandline.createArgument().setValue(MSVSSConstants.VALUE_NO);
                }
                if (getPromotiongroup() != null) {
                    commandline.createArgument().setValue("-G" + getPromotiongroup());
                } else if (getLabel() != null) {
                    commandline.createArgument().setValue("-v" + getLabel());
                } else if (getRevision() != null) {
                    commandline.createArgument().setValue("-r" + getRevision());
                }
                if (this.updateOnly) {
                    commandline.createArgument().setValue(MSVSSConstants.FLAG_USER);
                }
                commandline.createArgument().setValue("@" + file.getAbsolutePath());
                log("Getting files", 2);
                log("Executing " + commandline.toString(), 3);
                int iRunCmd2 = runCmd(commandline, new LogStreamHandler((Task) this, 2, 1));
                if (iRunCmd2 != 0 && !this.ignorerc) {
                    throw new BuildException("Failed executing: " + commandline.toString() + ". Return code was " + iRunCmd2, getLocation());
                }
                file2.delete();
                file.delete();
            } catch (Throwable th4) {
                FileUtils.close(fileOutputStream);
                throw th4;
            }
        } catch (FileNotFoundException e7) {
            e = e7;
            throw new BuildException("Failed executing: " + commandline.toString() + ". Exception: " + e.getMessage(), getLocation());
        } catch (IOException e8) {
            e = e8;
            throw new BuildException("Failed executing: " + commandline.toString() + ". Exception: " + e.getMessage(), getLocation());
        } catch (ParseException e9) {
            e = e9;
            throw new BuildException("Failed executing: " + commandline.toString() + ". Exception: " + e.getMessage(), getLocation());
        } catch (Throwable th5) {
            th = th5;
            file3 = file2;
            if (file3 != null) {
                file3.delete();
            }
            if (file != 0) {
                file.delete();
            }
            throw th;
        }
    }

    private void createFolders(File file) throws Throwable {
        BufferedReader bufferedReader = null;
        try {
            BufferedReader bufferedReader2 = new BufferedReader(new FileReader(file));
            try {
                MessageFormat messageFormat = new MessageFormat(getFilenameFormat());
                for (String line = bufferedReader2.readLine(); line != null; line = bufferedReader2.readLine()) {
                    log("Considering \"" + line + "\"", 3);
                    if (line.startsWith("\"\\") || line.startsWith("\"/") || (line.length() > 3 && line.startsWith("\"") && Character.isLetter(line.charAt(1)) && String.valueOf(line.charAt(2)).equals(":") && String.valueOf(line.charAt(3)).equals("\\"))) {
                        String str = (String) messageFormat.parse(line)[1];
                        int iLastIndexOf = str.lastIndexOf(File.separator);
                        if (iLastIndexOf > -1) {
                            File file2 = new File(str.substring(0, iLastIndexOf));
                            if (!file2.exists()) {
                                log("Creating " + file2.getAbsolutePath(), 3);
                                if (file2.mkdirs() || file2.isDirectory()) {
                                    log("Created " + file2.getAbsolutePath(), 2);
                                } else {
                                    log("Failed to create " + file2.getAbsolutePath(), 2);
                                }
                            } else {
                                log(file2.getAbsolutePath() + " exists. Skipping", 3);
                            }
                        } else {
                            log("File separator problem with " + line, 1);
                        }
                    } else {
                        log("Skipped \"" + line + "\"", 3);
                    }
                }
                FileUtils.close(bufferedReader2);
            } catch (Throwable th) {
                th = th;
                bufferedReader = bufferedReader2;
                FileUtils.close(bufferedReader);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private void massagePCLI(File file, File file2) throws Throwable {
        BufferedWriter bufferedWriter;
        BufferedReader bufferedReader = null;
        try {
            BufferedReader bufferedReader2 = new BufferedReader(new FileReader(file));
            try {
                bufferedWriter = new BufferedWriter(new FileWriter(file2));
                while (true) {
                    try {
                        String line = bufferedReader2.readLine();
                        if (line != null) {
                            bufferedWriter.write(line.replace('\\', '/'));
                            bufferedWriter.newLine();
                        } else {
                            FileUtils.close(bufferedReader2);
                            FileUtils.close(bufferedWriter);
                            return;
                        }
                    } catch (Throwable th) {
                        th = th;
                        bufferedReader = bufferedReader2;
                        FileUtils.close(bufferedReader);
                        FileUtils.close(bufferedWriter);
                        throw th;
                    }
                }
            } catch (Throwable th2) {
                th = th2;
                bufferedWriter = null;
            }
        } catch (Throwable th3) {
            th = th3;
            bufferedWriter = null;
        }
    }

    public String getRepository() {
        return this.repository;
    }

    public String getFilenameFormat() {
        return this.filenameFormat;
    }

    public void setFilenameFormat(String str) {
        this.filenameFormat = str;
    }

    public String getLineStart() {
        return this.lineStart;
    }

    public void setLineStart(String str) {
        this.lineStart = str;
    }

    public void setRepository(String str) {
        this.repository = str;
    }

    public String getPvcsproject() {
        return this.pvcsProject;
    }

    public void setPvcsproject(String str) {
        this.pvcsProject = str;
    }

    public Vector getPvcsprojects() {
        return this.pvcsProjects;
    }

    public String getWorkspace() {
        return this.workspace;
    }

    public void setWorkspace(String str) {
        this.workspace = str;
    }

    public String getPvcsbin() {
        return this.pvcsbin;
    }

    public void setPvcsbin(String str) {
        this.pvcsbin = str;
    }

    public String getForce() {
        return this.force;
    }

    public void setForce(String str) {
        if (str != null && str.equalsIgnoreCase("yes")) {
            this.force = "yes";
        } else {
            this.force = "no";
        }
    }

    public String getPromotiongroup() {
        return this.promotiongroup;
    }

    public void setPromotiongroup(String str) {
        this.promotiongroup = str;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String str) {
        this.label = str;
    }

    public String getRevision() {
        return this.revision;
    }

    public void setRevision(String str) {
        this.revision = str;
    }

    public boolean getIgnoreReturnCode() {
        return this.ignorerc;
    }

    public void setIgnoreReturnCode(boolean z) {
        this.ignorerc = z;
    }

    public void addPvcsproject(PvcsProject pvcsProject) {
        this.pvcsProjects.addElement(pvcsProject);
    }

    public boolean getUpdateOnly() {
        return this.updateOnly;
    }

    public void setUpdateOnly(boolean z) {
        this.updateOnly = z;
    }

    public String getConfig() {
        return this.config;
    }

    public void setConfig(File file) {
        this.config = file.toString();
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String str) {
        this.userId = str;
    }
}
