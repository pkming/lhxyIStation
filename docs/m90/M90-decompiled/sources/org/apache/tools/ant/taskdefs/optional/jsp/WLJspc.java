package org.apache.tools.ant.taskdefs.optional.jsp;

import java.io.File;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.Path;

/* JADX INFO: loaded from: classes3.dex */
public class WLJspc extends MatchingTask {
    private Path compileClasspath;
    private File destinationDirectory;
    private String destinationPackage;
    private File sourceDirectory;
    private String pathToPackage = "";
    private Vector filesToDo = new Vector();

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (!this.destinationDirectory.isDirectory()) {
            throw new BuildException("destination directory " + this.destinationDirectory.getPath() + " is not valid");
        }
        if (!this.sourceDirectory.isDirectory()) {
            throw new BuildException("src directory " + this.sourceDirectory.getPath() + " is not valid");
        }
        String str = this.destinationPackage;
        if (str == null) {
            throw new BuildException("package attribute must be present.", getLocation());
        }
        this.pathToPackage = str.replace('.', File.separatorChar);
        DirectoryScanner directoryScanner = super.getDirectoryScanner(this.sourceDirectory);
        if (this.compileClasspath == null) {
            this.compileClasspath = new Path(getProject());
        }
        this.compileClasspath = this.compileClasspath.concatSystemClasspath();
        String[] includedFiles = directoryScanner.getIncludedFiles();
        Java java = new Java(this);
        java.setFork(true);
        java.setClassname("weblogic.jspc");
        java.setTaskName(getTaskName());
        String[] strArr = new String[12];
        strArr[0] = "-d";
        strArr[1] = this.destinationDirectory.getAbsolutePath().trim();
        strArr[2] = "-docroot";
        strArr[3] = this.sourceDirectory.getAbsolutePath().trim();
        strArr[4] = "-keepgenerated";
        strArr[5] = "-compilerclass";
        strArr[6] = "sun.tools.javac.Main";
        strArr[7] = "-classpath";
        strArr[8] = this.compileClasspath.toString();
        scanDir(includedFiles);
        log("Compiling " + this.filesToDo.size() + " JSP files");
        int size = this.filesToDo.size();
        for (int i = 0; i < size; i++) {
            String str2 = (String) this.filesToDo.elementAt(i);
            File file = new File(str2);
            strArr[9] = "-package";
            String parent = file.getParent();
            if (parent != null && !"".equals(parent)) {
                strArr[10] = this.destinationPackage + "._" + replaceString(parent, File.separator, "_.");
            } else {
                strArr[10] = this.destinationPackage;
            }
            strArr[11] = this.sourceDirectory + File.separator + str2;
            java.clearArgs();
            for (int i2 = 0; i2 < 12; i2++) {
                java.createArg().setValue(strArr[i2]);
            }
            java.setClasspath(this.compileClasspath);
            if (java.executeJava() != 0) {
                log(str2 + " failed to compile", 1);
            }
        }
    }

    public void setClasspath(Path path) {
        Path path2 = this.compileClasspath;
        if (path2 == null) {
            this.compileClasspath = path;
        } else {
            path2.append(path);
        }
    }

    public Path createClasspath() {
        if (this.compileClasspath == null) {
            this.compileClasspath = new Path(getProject());
        }
        return this.compileClasspath;
    }

    public void setSrc(File file) {
        this.sourceDirectory = file;
    }

    public void setDest(File file) {
        this.destinationDirectory = file;
    }

    public void setPackage(String str) {
        this.destinationPackage = str;
    }

    protected void scanDir(String[] strArr) {
        String str;
        long time = new Date().getTime();
        for (int i = 0; i < strArr.length; i++) {
            File file = new File(this.sourceDirectory, strArr[i]);
            String parent = new File(strArr[i]).getParent();
            if (parent != null && !"".equals(parent)) {
                str = this.pathToPackage + File.separator + "_" + replaceString(parent, File.separator, "_/");
            } else {
                str = this.pathToPackage;
            }
            String str2 = str + File.separator + "_";
            int iLastIndexOf = strArr[i].lastIndexOf(File.separator) != -1 ? strArr[i].lastIndexOf(File.separator) + 1 : 0;
            int iIndexOf = strArr[i].indexOf(".jsp");
            if (iIndexOf == -1) {
                log("Skipping " + strArr[i] + ". Not a JSP", 3);
            } else {
                File file2 = new File(this.destinationDirectory, (str2 + strArr[i].substring(iLastIndexOf, iIndexOf)) + ".class");
                if (file.lastModified() > time) {
                    log("Warning: file modified in the future: " + strArr[i], 1);
                }
                if (file.lastModified() > file2.lastModified()) {
                    this.filesToDo.addElement(strArr[i]);
                    log("Recompiling File " + strArr[i], 3);
                }
            }
        }
    }

    protected String replaceString(String str, String str2, String str3) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, str2, true);
        int iCountTokens = stringTokenizer.countTokens();
        String str4 = "";
        for (int i = 0; i < iCountTokens; i++) {
            String strNextToken = stringTokenizer.nextToken();
            if (strNextToken.equals(str2)) {
                strNextToken = str3;
            }
            str4 = str4 + strNextToken;
        }
        return str4;
    }
}
