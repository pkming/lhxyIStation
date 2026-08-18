package org.apache.tools.ant.taskdefs.optional.jsp.compilers;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.jsp.JspC;
import org.apache.tools.ant.taskdefs.optional.jsp.JspMangler;

/* JADX INFO: loaded from: classes3.dex */
public interface JspCompilerAdapter {
    JspMangler createMangler();

    boolean execute() throws BuildException;

    boolean implementsOwnDependencyChecking();

    void setJspc(JspC jspC);
}
