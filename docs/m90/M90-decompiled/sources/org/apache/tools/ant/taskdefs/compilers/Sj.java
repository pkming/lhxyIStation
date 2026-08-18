package org.apache.tools.ant.taskdefs.compilers;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;

/* JADX INFO: loaded from: classes3.dex */
public class Sj extends DefaultCompilerAdapter {
    @Override // org.apache.tools.ant.taskdefs.compilers.DefaultCompilerAdapter
    protected String getNoDebugArgument() {
        return null;
    }

    @Override // org.apache.tools.ant.taskdefs.compilers.CompilerAdapter
    public boolean execute() throws BuildException {
        this.attributes.log("Using symantec java compiler", 3);
        Commandline commandline = setupJavacCommand();
        String executable = getJavac().getExecutable();
        if (executable == null) {
            executable = "sj";
        }
        commandline.setExecutable(executable);
        return executeExternalCompile(commandline.getCommandline(), commandline.size() - this.compileList.length) == 0;
    }
}
