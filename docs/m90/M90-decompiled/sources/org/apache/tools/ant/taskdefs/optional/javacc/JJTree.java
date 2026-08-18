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
public class JJTree extends Task {
    private static final String BUILD_NODE_FILES = "BUILD_NODE_FILES";
    private static final String DEFAULT_SUFFIX = ".jj";
    private static final String MULTI = "MULTI";
    private static final String NODE_DEFAULT_VOID = "NODE_DEFAULT_VOID";
    private static final String NODE_FACTORY = "NODE_FACTORY";
    private static final String NODE_PACKAGE = "NODE_PACKAGE";
    private static final String NODE_PREFIX = "NODE_PREFIX";
    private static final String NODE_SCOPE_HOOK = "NODE_SCOPE_HOOK";
    private static final String NODE_USES_PARSER = "NODE_USES_PARSER";
    private static final String OUTPUT_FILE = "OUTPUT_FILE";
    private static final String STATIC = "STATIC";
    private static final String VISITOR = "VISITOR";
    private static final String VISITOR_EXCEPTION = "VISITOR_EXCEPTION";
    private CommandlineJava cmdl;
    private String maxMemory;
    private final Hashtable optionalAttrs = new Hashtable();
    private String outputFile = null;
    private File outputDirectory = null;
    private File targetFile = null;
    private File javaccHome = null;

    public void setBuildnodefiles(boolean z) {
        this.optionalAttrs.put(BUILD_NODE_FILES, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setMulti(boolean z) {
        this.optionalAttrs.put(MULTI, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setNodedefaultvoid(boolean z) {
        this.optionalAttrs.put(NODE_DEFAULT_VOID, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setNodefactory(boolean z) {
        this.optionalAttrs.put(NODE_FACTORY, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setNodescopehook(boolean z) {
        this.optionalAttrs.put(NODE_SCOPE_HOOK, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setNodeusesparser(boolean z) {
        this.optionalAttrs.put(NODE_USES_PARSER, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setStatic(boolean z) {
        this.optionalAttrs.put(STATIC, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setVisitor(boolean z) {
        this.optionalAttrs.put(VISITOR, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setNodepackage(String str) {
        this.optionalAttrs.put(NODE_PACKAGE, str);
    }

    public void setVisitorException(String str) {
        this.optionalAttrs.put(VISITOR_EXCEPTION, str);
    }

    public void setNodeprefix(String str) {
        this.optionalAttrs.put(NODE_PREFIX, str);
    }

    public void setOutputdirectory(File file) {
        this.outputDirectory = file;
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

    public JJTree() {
        CommandlineJava commandlineJava = new CommandlineJava();
        this.cmdl = commandlineJava;
        this.maxMemory = null;
        commandlineJava.setVm(JavaEnvUtils.getJreExecutable("java"));
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        File file;
        Enumeration enumerationKeys = this.optionalAttrs.keys();
        while (enumerationKeys.hasMoreElements()) {
            String str = (String) enumerationKeys.nextElement();
            this.cmdl.createArgument().setValue("-" + str + ":" + this.optionalAttrs.get(str).toString());
        }
        File file2 = this.targetFile;
        if (file2 == null || !file2.isFile()) {
            throw new BuildException("Invalid target: " + this.targetFile);
        }
        File file3 = this.outputDirectory;
        if (file3 == null) {
            this.cmdl.createArgument().setValue("-OUTPUT_DIRECTORY:" + getDefaultOutputDirectory());
            file = new File(createOutputFileName(this.targetFile, this.outputFile, null));
        } else {
            if (!file3.isDirectory()) {
                throw new BuildException("'outputdirectory' " + this.outputDirectory + " is not a directory.");
            }
            this.cmdl.createArgument().setValue("-OUTPUT_DIRECTORY:" + this.outputDirectory.getAbsolutePath().replace('\\', '/'));
            file = new File(createOutputFileName(this.targetFile, this.outputFile, this.outputDirectory.getPath()));
        }
        if (file.exists() && this.targetFile.lastModified() < file.lastModified()) {
            log("Target is already built - skipping (" + this.targetFile + ")", 3);
            return;
        }
        if (this.outputFile != null) {
            this.cmdl.createArgument().setValue("-OUTPUT_FILE:" + this.outputFile.replace('\\', '/'));
        }
        this.cmdl.createArgument().setValue(this.targetFile.getAbsolutePath());
        Path pathCreateClasspath = this.cmdl.createClasspath(getProject());
        pathCreateClasspath.createPathElement().setPath(JavaCC.getArchiveFile(this.javaccHome).getAbsolutePath());
        pathCreateClasspath.addJavaRuntime();
        this.cmdl.setClassname(JavaCC.getMainClass(pathCreateClasspath, 2));
        this.cmdl.setMaxmemory(this.maxMemory);
        this.cmdl.createVmArgument().setValue("-Dinstall.root=" + this.javaccHome.getAbsolutePath());
        Execute execute = new Execute(new LogStreamHandler((Task) this, 2, 2), null);
        log(this.cmdl.describeCommand(), 3);
        execute.setCommandline(this.cmdl.getCommandline());
        try {
            if (execute.execute() == 0) {
            } else {
                throw new BuildException("JJTree failed.");
            }
        } catch (IOException e) {
            throw new BuildException("Failed to launch JJTree", e);
        }
    }

    private String createOutputFileName(File file, String str, String str2) {
        String strValidateOutputFile = validateOutputFile(str, str2);
        String strReplace = file.getAbsolutePath().replace('\\', '/');
        if (strValidateOutputFile == null || strValidateOutputFile.equals("")) {
            int iLastIndexOf = strReplace.lastIndexOf("/");
            if (iLastIndexOf >= 0) {
                strReplace = strReplace.substring(iLastIndexOf + 1);
            }
            int iLastIndexOf2 = strReplace.lastIndexOf(46);
            if (iLastIndexOf2 == -1 || strReplace.substring(iLastIndexOf2).equals(DEFAULT_SUFFIX)) {
                strValidateOutputFile = strReplace + DEFAULT_SUFFIX;
            } else {
                strValidateOutputFile = strReplace.substring(0, iLastIndexOf2) + DEFAULT_SUFFIX;
            }
        }
        if (str2 == null || str2.equals("")) {
            str2 = getDefaultOutputDirectory();
        }
        return (str2 + "/" + strValidateOutputFile).replace('\\', '/');
    }

    private String validateOutputFile(String str, String str2) throws BuildException {
        if (str == null) {
            return null;
        }
        if (str2 == null && (str.startsWith("/") || str.startsWith("\\"))) {
            String strMakeOutputFileRelative = makeOutputFileRelative(str);
            setOutputfile(strMakeOutputFileRelative);
            return strMakeOutputFileRelative;
        }
        String absolutePath = getRoot(new File(str)).getAbsolutePath();
        if (absolutePath.length() <= 1 || !str.startsWith(absolutePath.substring(0, absolutePath.length() - 1))) {
            return str;
        }
        throw new BuildException("Drive letter in 'outputfile' not supported: " + str);
    }

    private String makeOutputFileRelative(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        String defaultOutputDirectory = getDefaultOutputDirectory();
        int iIndexOf = defaultOutputDirectory.indexOf(47);
        loop0: while (true) {
            iIndexOf++;
            while (iIndexOf > -1 && iIndexOf < defaultOutputDirectory.length()) {
                stringBuffer.append("/..");
                iIndexOf = defaultOutputDirectory.indexOf(47, iIndexOf);
                if (iIndexOf == -1) {
                }
            }
        }
        stringBuffer.append(str);
        return stringBuffer.toString();
    }

    private String getDefaultOutputDirectory() {
        return getProject().getBaseDir().getAbsolutePath().replace('\\', '/');
    }

    private File getRoot(File file) {
        File absoluteFile = file.getAbsoluteFile();
        while (absoluteFile.getParent() != null) {
            absoluteFile = absoluteFile.getParentFile();
        }
        return absoluteFile;
    }
}
