package org.apache.tools.ant.taskdefs.optional.jsp.compilers;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.jsp.Jasper41Mangler;
import org.apache.tools.ant.taskdefs.optional.jsp.JspNameMangler;

/* JADX INFO: loaded from: classes3.dex */
public final class JspCompilerAdapterFactory {
    private JspCompilerAdapterFactory() {
    }

    public static JspCompilerAdapter getCompiler(String str, Task task) throws BuildException {
        return getCompiler(str, task, task.getProject().createClassLoader(null));
    }

    public static JspCompilerAdapter getCompiler(String str, Task task, AntClassLoader antClassLoader) throws BuildException {
        if (str.equalsIgnoreCase("jasper")) {
            return new JasperC(new JspNameMangler());
        }
        if (str.equalsIgnoreCase("jasper41")) {
            return new JasperC(new Jasper41Mangler());
        }
        return resolveClassName(str, antClassLoader);
    }

    private static JspCompilerAdapter resolveClassName(String str, AntClassLoader antClassLoader) throws BuildException {
        try {
            return (JspCompilerAdapter) antClassLoader.findClass(str).newInstance();
        } catch (ClassCastException e) {
            throw new BuildException(str + " isn't the classname of a compiler adapter.", e);
        } catch (ClassNotFoundException e2) {
            throw new BuildException(str + " can't be found.", e2);
        } catch (Throwable th) {
            throw new BuildException(str + " caused an interesting exception.", th);
        }
    }
}
