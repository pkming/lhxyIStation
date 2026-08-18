package org.apache.tools.ant.taskdefs.compilers;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Javac;

/* JADX INFO: loaded from: classes3.dex */
public interface CompilerAdapter {
    boolean execute() throws BuildException;

    void setJavac(Javac javac);
}
