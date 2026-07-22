package org.apache.tools.ant.taskdefs.compilers;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSSConstants;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JavaEnvUtils;

/* JADX INFO: loaded from: classes3.dex */
public class JavacExternal extends DefaultCompilerAdapter {
    @Override // org.apache.tools.ant.taskdefs.compilers.CompilerAdapter
    public boolean execute() throws BuildException {
        this.attributes.log("Using external javac compiler", 3);
        Commandline commandline = new Commandline();
        commandline.setExecutable(getJavac().getJavacExecutable());
        if (!assumeJava11() && !assumeJava12()) {
            setupModernJavacCommandlineSwitches(commandline);
        } else {
            setupJavacCommandlineSwitches(commandline, true);
        }
        int size = assumeJava11() ? -1 : commandline.size();
        logAndAddFilesToCompile(commandline);
        if (Os.isFamily(Os.FAMILY_VMS)) {
            return execOnVMS(commandline, size);
        }
        return executeExternalCompile(commandline.getCommandline(), size, true) == 0;
    }

    private boolean execOnVMS(Commandline commandline, int i) {
        File fileCreateVmsJavaOptionFile = null;
        try {
            try {
                fileCreateVmsJavaOptionFile = JavaEnvUtils.createVmsJavaOptionFile(commandline.getArguments());
                return executeExternalCompile(new String[]{commandline.getExecutable(), MSVSSConstants.FLAG_VERSION, fileCreateVmsJavaOptionFile.getPath()}, i, true) == 0;
            } catch (IOException unused) {
                throw new BuildException("Failed to create a temporary file for \"-V\" switch");
            }
        } finally {
            FileUtils.delete(fileCreateVmsJavaOptionFile);
        }
    }
}
