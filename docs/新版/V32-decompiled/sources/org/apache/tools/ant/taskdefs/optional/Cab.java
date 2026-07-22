package org.apache.tools.ant.taskdefs.optional;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.taskdefs.StreamPumper;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Cab extends MatchingTask {
    private static final int DEFAULT_RESULT = -99;
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private File baseDir;
    private File cabFile;
    private String cmdOptions;
    private Vector filesets = new Vector();
    private boolean doCompress = true;
    private boolean doVerbose = false;
    protected String archiveType = "cab";

    public void setCabfile(File file) {
        this.cabFile = file;
    }

    public void setBasedir(File file) {
        this.baseDir = file;
    }

    public void setCompress(boolean z) {
        this.doCompress = z;
    }

    public void setVerbose(boolean z) {
        this.doVerbose = z;
    }

    public void setOptions(String str) {
        this.cmdOptions = str;
    }

    public void addFileset(FileSet fileSet) {
        if (this.filesets.size() > 0) {
            throw new BuildException("Only one nested fileset allowed");
        }
        this.filesets.addElement(fileSet);
    }

    protected void checkConfiguration() throws BuildException {
        if (this.baseDir == null && this.filesets.size() == 0) {
            throw new BuildException("basedir attribute or one nested fileset is required!", getLocation());
        }
        File file = this.baseDir;
        if (file != null && !file.exists()) {
            throw new BuildException("basedir does not exist!", getLocation());
        }
        if (this.baseDir != null && this.filesets.size() > 0) {
            throw new BuildException("Both basedir attribute and a nested fileset is not allowed");
        }
        if (this.cabFile == null) {
            throw new BuildException("cabfile attribute must be set!", getLocation());
        }
    }

    protected ExecTask createExec() throws BuildException {
        return new ExecTask(this);
    }

    protected boolean isUpToDate(Vector vector) {
        int size = vector.size();
        boolean z = true;
        for (int i = 0; i < size && z; i++) {
            if (FILE_UTILS.resolveFile(this.baseDir, vector.elementAt(i).toString()).lastModified() > this.cabFile.lastModified()) {
                z = false;
            }
        }
        return z;
    }

    protected File createListFile(Vector vector) throws Throwable {
        File fileCreateTempFile = FILE_UTILS.createTempFile("ant", "", null, true, true);
        BufferedWriter bufferedWriter = null;
        try {
            BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(fileCreateTempFile));
            try {
                int size = vector.size();
                for (int i = 0; i < size; i++) {
                    bufferedWriter2.write('\"' + vector.elementAt(i).toString() + '\"');
                    bufferedWriter2.newLine();
                }
                FileUtils.close(bufferedWriter2);
                return fileCreateTempFile;
            } catch (Throwable th) {
                th = th;
                bufferedWriter = bufferedWriter2;
                FileUtils.close(bufferedWriter);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    protected void appendFiles(Vector vector, DirectoryScanner directoryScanner) {
        for (String str : directoryScanner.getIncludedFiles()) {
            vector.addElement(str);
        }
    }

    protected Vector getFileList() throws BuildException {
        Vector vector = new Vector();
        File file = this.baseDir;
        if (file != null) {
            appendFiles(vector, super.getDirectoryScanner(file));
        } else {
            FileSet fileSet = (FileSet) this.filesets.elementAt(0);
            this.baseDir = fileSet.getDir();
            appendFiles(vector, fileSet.getDirectoryScanner(getProject()));
        }
        return vector;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws Throwable {
        checkConfiguration();
        Vector fileList = getFileList();
        if (isUpToDate(fileList)) {
            return;
        }
        log("Building " + this.archiveType + ": " + this.cabFile.getAbsolutePath());
        File fileCreateTempFile = null;
        if (!Os.isFamily(Os.FAMILY_WINDOWS)) {
            log("Using listcab/libcabinet", 3);
            StringBuffer stringBuffer = new StringBuffer();
            Enumeration enumerationElements = fileList.elements();
            while (enumerationElements.hasMoreElements()) {
                stringBuffer.append(enumerationElements.nextElement()).append("\n");
            }
            stringBuffer.append("\n").append(this.cabFile.getAbsolutePath()).append("\n");
            try {
                Project project = getProject();
                String[] strArr = {"listcab"};
                File baseDir = this.baseDir;
                if (baseDir == null) {
                    baseDir = getProject().getBaseDir();
                }
                Process processLaunch = Execute.launch(project, strArr, null, baseDir, true);
                OutputStream outputStream = processLaunch.getOutputStream();
                LogOutputStream logOutputStream = new LogOutputStream((Task) this, 3);
                LogOutputStream logOutputStream2 = new LogOutputStream((Task) this, 0);
                StreamPumper streamPumper = new StreamPumper(processLaunch.getInputStream(), logOutputStream);
                StreamPumper streamPumper2 = new StreamPumper(processLaunch.getErrorStream(), logOutputStream2);
                new Thread(streamPumper).start();
                new Thread(streamPumper2).start();
                outputStream.write(stringBuffer.toString().getBytes());
                outputStream.flush();
                outputStream.close();
                int iWaitFor = DEFAULT_RESULT;
                try {
                    iWaitFor = processLaunch.waitFor();
                    streamPumper.waitFor();
                    logOutputStream.close();
                    streamPumper2.waitFor();
                    logOutputStream2.close();
                } catch (InterruptedException e) {
                    log("Thread interrupted: " + e);
                }
                if (Execute.isFailure(iWaitFor)) {
                    log("Error executing listcab; error code: " + iWaitFor);
                    return;
                }
                return;
            } catch (IOException e2) {
                throw new BuildException("Problem creating " + this.cabFile + " " + e2.getMessage(), getLocation());
            }
        }
        try {
            File fileCreateListFile = createListFile(fileList);
            ExecTask execTaskCreateExec = createExec();
            execTaskCreateExec.setFailonerror(true);
            execTaskCreateExec.setDir(this.baseDir);
            if (!this.doVerbose) {
                fileCreateTempFile = FILE_UTILS.createTempFile("ant", "", null, true, true);
                execTaskCreateExec.setOutput(fileCreateTempFile);
            }
            execTaskCreateExec.setExecutable("cabarc");
            execTaskCreateExec.createArg().setValue("-r");
            execTaskCreateExec.createArg().setValue("-p");
            if (!this.doCompress) {
                execTaskCreateExec.createArg().setValue("-m");
                execTaskCreateExec.createArg().setValue("none");
            }
            if (this.cmdOptions != null) {
                execTaskCreateExec.createArg().setLine(this.cmdOptions);
            }
            execTaskCreateExec.createArg().setValue("n");
            execTaskCreateExec.createArg().setFile(this.cabFile);
            execTaskCreateExec.createArg().setValue("@" + fileCreateListFile.getAbsolutePath());
            execTaskCreateExec.execute();
            if (fileCreateTempFile != null) {
                fileCreateTempFile.delete();
            }
            fileCreateListFile.delete();
        } catch (IOException e3) {
            throw new BuildException("Problem creating " + this.cabFile + " " + e3.getMessage(), getLocation());
        }
    }
}
