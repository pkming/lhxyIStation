package org.apache.tools.ant.taskdefs.rmic;

import com.unisound.common.r;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import org.apache.tools.ant.taskdefs.Definer;
import org.apache.tools.ant.taskdefs.Rmic;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.StringUtils;

/* JADX INFO: loaded from: classes3.dex */
public abstract class DefaultRmicAdapter implements RmicAdapter {
    private static final Random RAND = new Random();
    public static final String RMI_SKEL_SUFFIX = "_Skel";
    public static final String RMI_STUB_SUFFIX = "_Stub";
    public static final String RMI_TIE_SUFFIX = "_Tie";
    public static final String STUB_1_1 = "-v1.1";
    public static final String STUB_1_2 = "-v1.2";
    public static final String STUB_COMPAT = "-vcompat";
    public static final String STUB_OPTION_1_1 = "1.1";
    public static final String STUB_OPTION_1_2 = "1.2";
    public static final String STUB_OPTION_COMPAT = "compat";
    private Rmic attributes;
    private FileNameMapper mapper;

    protected String getSkelClassSuffix() {
        return RMI_SKEL_SUFFIX;
    }

    protected String getStubClassSuffix() {
        return RMI_STUB_SUFFIX;
    }

    protected String getTieClassSuffix() {
        return RMI_TIE_SUFFIX;
    }

    protected String[] preprocessCompilerArgs(String[] strArr) {
        return strArr;
    }

    @Override // org.apache.tools.ant.taskdefs.rmic.RmicAdapter
    public void setRmic(Rmic rmic) {
        this.attributes = rmic;
        this.mapper = new RmicFileNameMapper();
    }

    public Rmic getRmic() {
        return this.attributes;
    }

    @Override // org.apache.tools.ant.taskdefs.rmic.RmicAdapter
    public FileNameMapper getMapper() {
        return this.mapper;
    }

    @Override // org.apache.tools.ant.taskdefs.rmic.RmicAdapter
    public Path getClasspath() {
        return getCompileClasspath();
    }

    protected Path getCompileClasspath() {
        Path path = new Path(this.attributes.getProject());
        path.setLocation(this.attributes.getBase());
        Path classpath = this.attributes.getClasspath();
        if (classpath == null) {
            classpath = new Path(this.attributes.getProject());
        }
        if (this.attributes.getIncludeantruntime()) {
            path.addExisting(classpath.concatSystemClasspath(r.E));
        } else {
            path.addExisting(classpath.concatSystemClasspath(Definer.OnError.POLICY_IGNORE));
        }
        if (this.attributes.getIncludejavaruntime()) {
            path.addJavaRuntime();
        }
        return path;
    }

    protected Commandline setupRmicCommand() {
        return setupRmicCommand(null);
    }

    protected Commandline setupRmicCommand(String[] strArr) {
        Commandline commandline = new Commandline();
        if (strArr != null) {
            for (String str : strArr) {
                commandline.createArgument().setValue(str);
            }
        }
        Path compileClasspath = getCompileClasspath();
        commandline.createArgument().setValue("-d");
        commandline.createArgument().setFile(this.attributes.getOutputDir());
        if (this.attributes.getExtdirs() != null) {
            commandline.createArgument().setValue("-extdirs");
            commandline.createArgument().setPath(this.attributes.getExtdirs());
        }
        commandline.createArgument().setValue("-classpath");
        commandline.createArgument().setPath(compileClasspath);
        String strAddStubVersionOptions = addStubVersionOptions();
        if (strAddStubVersionOptions != null) {
            commandline.createArgument().setValue(strAddStubVersionOptions);
        }
        if (this.attributes.getSourceBase() != null) {
            commandline.createArgument().setValue("-keepgenerated");
        }
        if (this.attributes.getIiop()) {
            this.attributes.log("IIOP has been turned on.", 2);
            commandline.createArgument().setValue("-iiop");
            if (this.attributes.getIiopopts() != null) {
                this.attributes.log("IIOP Options: " + this.attributes.getIiopopts(), 2);
                commandline.createArgument().setValue(this.attributes.getIiopopts());
            }
        }
        if (this.attributes.getIdl()) {
            commandline.createArgument().setValue("-idl");
            this.attributes.log("IDL has been turned on.", 2);
            if (this.attributes.getIdlopts() != null) {
                commandline.createArgument().setValue(this.attributes.getIdlopts());
                this.attributes.log("IDL Options: " + this.attributes.getIdlopts(), 2);
            }
        }
        if (this.attributes.getDebug()) {
            commandline.createArgument().setValue("-g");
        }
        commandline.addArguments(preprocessCompilerArgs(this.attributes.getCurrentCompilerArgs()));
        logAndAddFilesToCompile(commandline);
        return commandline;
    }

    protected String addStubVersionOptions() {
        String str;
        String stubVersion = this.attributes.getStubVersion();
        if (stubVersion == null) {
            str = null;
        } else if ("1.1".equals(stubVersion)) {
            str = STUB_1_1;
        } else if ("1.2".equals(stubVersion)) {
            str = STUB_1_2;
        } else if (STUB_OPTION_COMPAT.equals(stubVersion)) {
            str = STUB_COMPAT;
        } else {
            this.attributes.log("Unknown stub option " + stubVersion);
            str = null;
        }
        return (str != null || this.attributes.getIiop() || this.attributes.getIdl()) ? str : STUB_COMPAT;
    }

    protected String[] filterJvmCompilerArgs(String[] strArr) {
        ArrayList arrayList = new ArrayList(strArr.length);
        for (String str : strArr) {
            if (!str.startsWith("-J")) {
                arrayList.add(str);
            } else {
                this.attributes.log("Dropping " + str + " from compiler arguments");
            }
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    protected void logAndAddFilesToCompile(Commandline commandline) {
        Vector compileList = this.attributes.getCompileList();
        this.attributes.log("Compilation " + commandline.describeArguments(), 3);
        StringBuffer stringBuffer = new StringBuffer("File");
        int size = compileList.size();
        if (size != 1) {
            stringBuffer.append("s");
        }
        stringBuffer.append(" to be compiled:");
        for (int i = 0; i < size; i++) {
            String str = (String) compileList.elementAt(i);
            commandline.createArgument().setValue(str);
            stringBuffer.append("    ");
            stringBuffer.append(str);
        }
        this.attributes.log(stringBuffer.toString(), 3);
    }

    private class RmicFileNameMapper implements FileNameMapper {
        @Override // org.apache.tools.ant.util.FileNameMapper
        public void setFrom(String str) {
        }

        @Override // org.apache.tools.ant.util.FileNameMapper
        public void setTo(String str) {
        }

        RmicFileNameMapper() {
        }

        @Override // org.apache.tools.ant.util.FileNameMapper
        public String[] mapFileName(String str) {
            int i;
            String strSubstring;
            int i2;
            if (str == null || !str.endsWith(".class") || str.endsWith(DefaultRmicAdapter.this.getStubClassSuffix() + ".class") || str.endsWith(DefaultRmicAdapter.this.getSkelClassSuffix() + ".class") || str.endsWith(DefaultRmicAdapter.this.getTieClassSuffix() + ".class")) {
                return null;
            }
            String strRemoveSuffix = StringUtils.removeSuffix(str, ".class");
            String strReplace = strRemoveSuffix.replace(File.separatorChar, '.');
            if (DefaultRmicAdapter.this.attributes.getVerify() && !DefaultRmicAdapter.this.attributes.isValidRmiRemote(strReplace)) {
                return null;
            }
            String[] strArr = {str + ".tmp." + DefaultRmicAdapter.RAND.nextLong()};
            if (!DefaultRmicAdapter.this.attributes.getIiop() && !DefaultRmicAdapter.this.attributes.getIdl()) {
                return "1.2".equals(DefaultRmicAdapter.this.attributes.getStubVersion()) ? new String[]{strRemoveSuffix + DefaultRmicAdapter.this.getStubClassSuffix() + ".class"} : new String[]{strRemoveSuffix + DefaultRmicAdapter.this.getStubClassSuffix() + ".class", strRemoveSuffix + DefaultRmicAdapter.this.getSkelClassSuffix() + ".class"};
            }
            if (DefaultRmicAdapter.this.attributes.getIdl()) {
                return strArr;
            }
            int iLastIndexOf = strRemoveSuffix.lastIndexOf(File.separatorChar);
            String strReplace2 = "";
            if (iLastIndexOf == -1) {
                i = 0;
                strSubstring = "";
            } else {
                i = iLastIndexOf + 1;
                strSubstring = strRemoveSuffix.substring(0, i);
            }
            String strSubstring2 = strRemoveSuffix.substring(i);
            try {
                Class<?> clsLoadClass = DefaultRmicAdapter.this.attributes.getLoader().loadClass(strReplace);
                if (clsLoadClass.isInterface()) {
                    return new String[]{strSubstring + "_" + strSubstring2 + DefaultRmicAdapter.this.getStubClassSuffix() + ".class"};
                }
                String name = DefaultRmicAdapter.this.attributes.getRemoteInterface(clsLoadClass).getName();
                int iLastIndexOf2 = name.lastIndexOf(".");
                if (iLastIndexOf2 == -1) {
                    i2 = 0;
                } else {
                    i2 = iLastIndexOf2 + 1;
                    strReplace2 = name.substring(0, i2).replace('.', File.separatorChar);
                }
                return new String[]{strSubstring + "_" + strSubstring2 + DefaultRmicAdapter.this.getTieClassSuffix() + ".class", strReplace2 + "_" + name.substring(i2) + DefaultRmicAdapter.this.getStubClassSuffix() + ".class"};
            } catch (ClassNotFoundException unused) {
                DefaultRmicAdapter.this.attributes.log(Rmic.ERROR_UNABLE_TO_VERIFY_CLASS + strReplace + Rmic.ERROR_NOT_FOUND, 1);
                return strArr;
            } catch (NoClassDefFoundError unused2) {
                DefaultRmicAdapter.this.attributes.log(Rmic.ERROR_UNABLE_TO_VERIFY_CLASS + strReplace + Rmic.ERROR_NOT_DEFINED, 1);
                return strArr;
            } catch (Throwable th) {
                DefaultRmicAdapter.this.attributes.log(Rmic.ERROR_UNABLE_TO_VERIFY_CLASS + strReplace + Rmic.ERROR_LOADING_CAUSED_EXCEPTION + th.getMessage(), 1);
                return strArr;
            }
        }
    }
}
