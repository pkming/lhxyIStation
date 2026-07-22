package org.apache.tools.ant.taskdefs.optional.javacc;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.JavaEnvUtils;

/* JADX INFO: loaded from: classes3.dex */
public class JavaCC extends Task {
    protected static final String[] ARCHIVE_LOCATIONS = {"JavaCC.zip", "bin/lib/JavaCC.zip", "bin/lib/javacc.jar", "javacc.jar"};
    protected static final int[] ARCHIVE_LOCATIONS_VS_MAJOR_VERSION = {1, 2, 3, 3};
    private static final String BUILD_PARSER = "BUILD_PARSER";
    private static final String BUILD_TOKEN_MANAGER = "BUILD_TOKEN_MANAGER";
    private static final String CACHE_TOKENS = "CACHE_TOKENS";
    private static final String CHOICE_AMBIGUITY_CHECK = "CHOICE_AMBIGUITY_CHECK";
    private static final String COMMON_TOKEN_ACTION = "COMMON_TOKEN_ACTION";
    protected static final String COM_JAVACC_CLASS = "javacc.Main";
    protected static final String COM_JJDOC_CLASS = "jjdoc.JJDocMain";
    protected static final String COM_JJTREE_CLASS = "jjtree.Main";
    protected static final String COM_PACKAGE = "COM.sun.labs.";
    private static final String DEBUG_LOOKAHEAD = "DEBUG_LOOKAHEAD";
    private static final String DEBUG_PARSER = "DEBUG_PARSER";
    private static final String DEBUG_TOKEN_MANAGER = "DEBUG_TOKEN_MANAGER";
    private static final String ERROR_REPORTING = "ERROR_REPORTING";
    private static final String FORCE_LA_CHECK = "FORCE_LA_CHECK";
    private static final String IGNORE_CASE = "IGNORE_CASE";
    private static final String JAVA_UNICODE_ESCAPE = "JAVA_UNICODE_ESCAPE";
    private static final String JDK_VERSION = "JDK_VERSION";
    private static final String KEEP_LINE_COLUMN = "KEEP_LINE_COLUMN";
    private static final String LOOKAHEAD = "LOOKAHEAD";
    private static final String OPTIMIZE_TOKEN_MANAGER = "OPTIMIZE_TOKEN_MANAGER";
    protected static final String ORG_JAVACC_CLASS = "parser.Main";
    protected static final String ORG_JJDOC_CLASS = "jjdoc.JJDocMain";
    protected static final String ORG_JJTREE_CLASS = "jjtree.Main";
    protected static final String ORG_PACKAGE_3_0 = "org.netbeans.javacc.";
    protected static final String ORG_PACKAGE_3_1 = "org.javacc.";
    private static final String OTHER_AMBIGUITY_CHECK = "OTHER_AMBIGUITY_CHECK";
    private static final String SANITY_CHECK = "SANITY_CHECK";
    private static final String STATIC = "STATIC";
    protected static final int TASKDEF_TYPE_JAVACC = 1;
    protected static final int TASKDEF_TYPE_JJDOC = 3;
    protected static final int TASKDEF_TYPE_JJTREE = 2;
    private static final String UNICODE_INPUT = "UNICODE_INPUT";
    private static final String USER_CHAR_STREAM = "USER_CHAR_STREAM";
    private static final String USER_TOKEN_MANAGER = "USER_TOKEN_MANAGER";
    private CommandlineJava cmdl;
    private String maxMemory;
    private final Hashtable optionalAttrs = new Hashtable();
    private File outputDirectory = null;
    private File targetFile = null;
    private File javaccHome = null;

    public void setLookahead(int i) {
        this.optionalAttrs.put(LOOKAHEAD, new Integer(i));
    }

    public void setChoiceambiguitycheck(int i) {
        this.optionalAttrs.put(CHOICE_AMBIGUITY_CHECK, new Integer(i));
    }

    public void setOtherambiguityCheck(int i) {
        this.optionalAttrs.put(OTHER_AMBIGUITY_CHECK, new Integer(i));
    }

    public void setStatic(boolean z) {
        this.optionalAttrs.put(STATIC, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setDebugparser(boolean z) {
        this.optionalAttrs.put(DEBUG_PARSER, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setDebuglookahead(boolean z) {
        this.optionalAttrs.put(DEBUG_LOOKAHEAD, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setDebugtokenmanager(boolean z) {
        this.optionalAttrs.put(DEBUG_TOKEN_MANAGER, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setOptimizetokenmanager(boolean z) {
        this.optionalAttrs.put(OPTIMIZE_TOKEN_MANAGER, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setErrorreporting(boolean z) {
        this.optionalAttrs.put(ERROR_REPORTING, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setJavaunicodeescape(boolean z) {
        this.optionalAttrs.put(JAVA_UNICODE_ESCAPE, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setUnicodeinput(boolean z) {
        this.optionalAttrs.put(UNICODE_INPUT, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setIgnorecase(boolean z) {
        this.optionalAttrs.put(IGNORE_CASE, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setCommontokenaction(boolean z) {
        this.optionalAttrs.put(COMMON_TOKEN_ACTION, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setUsertokenmanager(boolean z) {
        this.optionalAttrs.put(USER_TOKEN_MANAGER, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setUsercharstream(boolean z) {
        this.optionalAttrs.put(USER_CHAR_STREAM, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setBuildparser(boolean z) {
        this.optionalAttrs.put(BUILD_PARSER, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setBuildtokenmanager(boolean z) {
        this.optionalAttrs.put(BUILD_TOKEN_MANAGER, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setSanitycheck(boolean z) {
        this.optionalAttrs.put(SANITY_CHECK, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setForcelacheck(boolean z) {
        this.optionalAttrs.put(FORCE_LA_CHECK, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setCachetokens(boolean z) {
        this.optionalAttrs.put(CACHE_TOKENS, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setKeeplinecolumn(boolean z) {
        this.optionalAttrs.put(KEEP_LINE_COLUMN, z ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setJDKversion(String str) {
        this.optionalAttrs.put(JDK_VERSION, str);
    }

    public void setOutputdirectory(File file) {
        this.outputDirectory = file;
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

    public JavaCC() {
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
        File file2 = this.outputDirectory;
        if (file2 == null) {
            this.outputDirectory = new File(this.targetFile.getParent());
        } else if (!file2.isDirectory()) {
            throw new BuildException("Outputdir not a directory.");
        }
        this.cmdl.createArgument().setValue("-OUTPUT_DIRECTORY:" + this.outputDirectory.getAbsolutePath());
        File outputJavaFile = getOutputJavaFile(this.outputDirectory, this.targetFile);
        if (outputJavaFile.exists() && this.targetFile.lastModified() < outputJavaFile.lastModified()) {
            log("Target is already built - skipping (" + this.targetFile + ")", 3);
            return;
        }
        this.cmdl.createArgument().setValue(this.targetFile.getAbsolutePath());
        Path pathCreateClasspath = this.cmdl.createClasspath(getProject());
        pathCreateClasspath.createPathElement().setPath(getArchiveFile(this.javaccHome).getAbsolutePath());
        pathCreateClasspath.addJavaRuntime();
        this.cmdl.setClassname(getMainClass(pathCreateClasspath, 1));
        this.cmdl.setMaxmemory(this.maxMemory);
        this.cmdl.createVmArgument().setValue("-Dinstall.root=" + this.javaccHome.getAbsolutePath());
        Execute.runCommand(this, this.cmdl.getCommandline());
    }

    protected static File getArchiveFile(File file) throws BuildException {
        return new File(file, ARCHIVE_LOCATIONS[getArchiveLocationIndex(file)]);
    }

    protected static String getMainClass(File file, int i) throws BuildException {
        Path path = new Path(null);
        path.createPathElement().setLocation(getArchiveFile(file));
        path.addJavaRuntime();
        return getMainClass(path, i);
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x003e A[PHI: r0
      0x003e: PHI (r0v8 java.lang.String) = (r0v5 java.lang.String), (r0v12 java.lang.String) binds: [B:25:0x008c, B:10:0x003b] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:13:0x0040 A[PHI: r0
      0x0040: PHI (r0v7 java.lang.String) = (r0v5 java.lang.String), (r0v12 java.lang.String) binds: [B:24:0x008a, B:9:0x0039] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected static java.lang.String getMainClass(org.apache.tools.ant.types.Path r11, int r12) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 215
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.taskdefs.optional.javacc.JavaCC.getMainClass(org.apache.tools.ant.types.Path, int):java.lang.String");
    }

    private static int getArchiveLocationIndex(File file) throws BuildException {
        if (file == null || !file.isDirectory()) {
            throw new BuildException("JavaCC home must be a valid directory.");
        }
        int i = 0;
        while (true) {
            String[] strArr = ARCHIVE_LOCATIONS;
            if (i < strArr.length) {
                if (new File(file, strArr[i]).exists()) {
                    return i;
                }
                i++;
            } else {
                throw new BuildException("Could not find a path to JavaCC.zip or javacc.jar from '" + file + "'.");
            }
        }
    }

    protected static int getMajorVersionNumber(File file) throws BuildException {
        return ARCHIVE_LOCATIONS_VS_MAJOR_VERSION[getArchiveLocationIndex(file)];
    }

    private File getOutputJavaFile(File file, File file2) {
        String str;
        String path = file2.getPath();
        int iLastIndexOf = path.lastIndexOf(File.separator);
        if (iLastIndexOf != -1) {
            path = path.substring(iLastIndexOf + 1);
        }
        int iLastIndexOf2 = path.lastIndexOf(46);
        if (iLastIndexOf2 != -1) {
            str = path.substring(0, iLastIndexOf2) + ".java";
        } else {
            str = path + ".java";
        }
        if (file != null) {
            str = file + File.separator + str;
        }
        return new File(str);
    }
}
