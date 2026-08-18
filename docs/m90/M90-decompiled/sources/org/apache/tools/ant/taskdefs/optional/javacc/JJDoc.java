package org.apache.tools.ant.taskdefs.optional.javacc;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.JavaEnvUtils;

/* JADX INFO: loaded from: classes3.dex */
public class JJDoc extends Task {
    private static final String DEFAULT_SUFFIX_HTML = ".html";
    private static final String DEFAULT_SUFFIX_TEXT = ".txt";
    private static final String ONE_TABLE = "ONE_TABLE";
    private static final String OUTPUT_FILE = "OUTPUT_FILE";
    private static final String TEXT = "TEXT";
    private CommandlineJava cmdl;
    private String maxMemory;
    private final Hashtable optionalAttrs = new Hashtable();
    private String outputFile = null;
    private boolean plainText = false;
    private File targetFile = null;
    private File javaccHome = null;

    public void setText(boolean z) {
        this.optionalAttrs.put(TEXT, z ? Boolean.TRUE : Boolean.FALSE);
        this.plainText = z;
    }

    public void setOnetable(boolean z) {
        this.optionalAttrs.put(ONE_TABLE, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setOutputfile(String str) {
        this.outputFile = str;
    }

    public void setTarget(File file) {
        this.targetFile = file;
    }

    public void setJavacchome(File file) {
        this.javaccHome = file;
    }

    public void setMaxmemory(String str) {
        this.maxMemory = str;
    }

    public JJDoc() {
        CommandlineJava commandlineJava = new CommandlineJava();
        this.cmdl = commandlineJava;
        this.maxMemory = null;
        commandlineJava.setVm(JavaEnvUtils.getJreExecutable("java"));
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        Enumeration enumerationKeys = this.optionalAttrs.keys();
        while (enumerationKeys.hasMoreElements()) {
            String str = (String) enumerationKeys.nextElement();
            this.cmdl.createArgument().setValue("-" + str + ":" + this.optionalAttrs.get(str).toString());
        }
        File file = this.targetFile;
        if (file == null || !file.isFile()) {
            throw new BuildException("Invalid target: " + this.targetFile);
        }
        if (this.outputFile != null) {
            this.cmdl.createArgument().setValue("-OUTPUT_FILE:" + this.outputFile.replace('\\', '/'));
        }
        File file2 = new File(createOutputFileName(this.targetFile, this.outputFile, this.plainText));
        if (file2.exists() && this.targetFile.lastModified() < file2.lastModified()) {
            log("Target is already built - skipping (" + this.targetFile + ")", 3);
            return;
        }
        this.cmdl.createArgument().setValue(this.targetFile.getAbsolutePath());
        Path pathCreateClasspath = this.cmdl.createClasspath(getProject());
        pathCreateClasspath.createPathElement().setPath(JavaCC.getArchiveFile(this.javaccHome).getAbsolutePath());
        pathCreateClasspath.addJavaRuntime();
        this.cmdl.setClassname(JavaCC.getMainClass(pathCreateClasspath, 3));
        this.cmdl.setMaxmemory(this.maxMemory);
        this.cmdl.createVmArgument().setValue("-Dinstall.root=" + this.javaccHome.getAbsolutePath());
        Execute execute = new Execute(new LogStreamHandler((Task) this, 2, 2), null);
        log(this.cmdl.describeCommand(), 3);
        execute.setCommandline(this.cmdl.getCommandline());
        try {
            if (execute.execute() == 0) {
            } else {
                throw new BuildException("JJDoc failed.");
            }
        } catch (IOException e) {
            throw new BuildException("Failed to launch JJDoc", e);
        }
    }

    private String createOutputFileName(File file, String str, boolean z) {
        String strReplace;
        String strReplace2 = file.getAbsolutePath().replace('\\', '/');
        String str2 = z ? DEFAULT_SUFFIX_TEXT : DEFAULT_SUFFIX_HTML;
        if (str == null || str.equals("")) {
            int iLastIndexOf = strReplace2.lastIndexOf("/");
            if (iLastIndexOf >= 0) {
                strReplace2 = strReplace2.substring(iLastIndexOf + 1);
            }
            int iLastIndexOf2 = strReplace2.lastIndexOf(46);
            if (iLastIndexOf2 == -1 || strReplace2.substring(iLastIndexOf2).equals(str2)) {
                strReplace = strReplace2 + str2;
            } else {
                strReplace = strReplace2.substring(0, iLastIndexOf2) + str2;
            }
        } else {
            strReplace = str.replace('\\', '/');
        }
        return (getProject().getBaseDir() + "/" + strReplace).replace('\\', '/');
    }
}
