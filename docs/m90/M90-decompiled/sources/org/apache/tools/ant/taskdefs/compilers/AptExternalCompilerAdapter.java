package org.apache.tools.ant.taskdefs.compilers;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Apt;
import org.apache.tools.ant.types.Commandline;

/* JADX INFO: loaded from: classes3.dex */
public class AptExternalCompilerAdapter extends DefaultCompilerAdapter {
    protected Apt getApt() {
        return (Apt) getJavac();
    }

    @Override // org.apache.tools.ant.taskdefs.compilers.CompilerAdapter
    public boolean execute() throws BuildException {
        this.attributes.log("Using external apt compiler", 3);
        Apt apt = getApt();
        Commandline commandline = new Commandline();
        commandline.setExecutable(apt.getAptExecutable());
        setupModernJavacCommandlineSwitches(commandline);
        AptCompilerAdapter.setAptCommandlineSwitches(apt, commandline);
        int size = commandline.size();
        logAndAddFilesToCompile(commandline);
        return executeExternalCompile(commandline.getCommandline(), size, true) == 0;
    }
}
