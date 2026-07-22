package org.apache.tools.ant.taskdefs.optional.jsp.compilers;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Definer;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.optional.jsp.JspC;
import org.apache.tools.ant.taskdefs.optional.jsp.JspMangler;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Path;

/* JADX INFO: loaded from: classes3.dex */
public class JasperC extends DefaultJspCompilerAdapter {
    JspMangler mangler;

    public JasperC(JspMangler jspMangler) {
        this.mangler = jspMangler;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.jsp.compilers.JspCompilerAdapter
    public boolean execute() throws BuildException {
        getJspc().log("Using jasper compiler", 3);
        CommandlineJava commandlineJava = setupJasperCommand();
        try {
            try {
                Java java = new Java(this.owner);
                Path classpath = getClasspath();
                if (getJspc().getClasspath() != null) {
                    getProject().log("using user supplied classpath: " + classpath, 4);
                } else {
                    getProject().log("using system classpath: " + classpath, 4);
                }
                java.setClasspath(classpath);
                java.setDir(getProject().getBaseDir());
                java.setClassname("org.apache.jasper.JspC");
                for (String str : commandlineJava.getJavaCommand().getArguments()) {
                    java.createArg().setValue(str);
                }
                java.setFailonerror(getJspc().getFailonerror());
                java.setFork(true);
                java.setTaskName("jasperc");
                java.execute();
                return true;
            } catch (Exception e) {
                if (e instanceof BuildException) {
                    throw ((BuildException) e);
                }
                throw new BuildException("Error running jsp compiler: ", e, getJspc().getLocation());
            }
        } finally {
            getJspc().deleteEmptyJavaFiles();
        }
    }

    private CommandlineJava setupJasperCommand() {
        CommandlineJava commandlineJava = new CommandlineJava();
        JspC jspc = getJspc();
        addArg(commandlineJava, "-d", jspc.getDestdir());
        addArg(commandlineJava, "-p", jspc.getPackage());
        if (!isTomcat5x()) {
            addArg(commandlineJava, "-v" + jspc.getVerbose());
        } else {
            getProject().log("this task doesn't support Tomcat 5.x properly, please use the Tomcat provided jspc task instead");
        }
        addArg(commandlineJava, "-uriroot", jspc.getUriroot());
        addArg(commandlineJava, "-uribase", jspc.getUribase());
        addArg(commandlineJava, "-ieplugin", jspc.getIeplugin());
        addArg(commandlineJava, "-webinc", jspc.getWebinc());
        addArg(commandlineJava, "-webxml", jspc.getWebxml());
        addArg(commandlineJava, "-die9");
        if (jspc.isMapped()) {
            addArg(commandlineJava, "-mapped");
        }
        if (jspc.getWebApp() != null) {
            addArg(commandlineJava, "-webapp", jspc.getWebApp().getDirectory());
        }
        logAndAddFilesToCompile(getJspc(), getJspc().getCompileList(), commandlineJava);
        return commandlineJava;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.jsp.compilers.JspCompilerAdapter
    public JspMangler createMangler() {
        return this.mangler;
    }

    private Path getClasspath() {
        Path classpath = getJspc().getClasspath();
        if (classpath == null) {
            return new Path(getProject()).concatSystemClasspath("only");
        }
        return classpath.concatSystemClasspath(Definer.OnError.POLICY_IGNORE);
    }

    private boolean isTomcat5x() {
        AntClassLoader antClassLoaderCreateClassLoader = null;
        try {
            antClassLoaderCreateClassLoader = getProject().createClassLoader(getClasspath());
            antClassLoaderCreateClassLoader.loadClass("org.apache.jasper.tagplugins.jstl.If");
            if (antClassLoaderCreateClassLoader != null) {
                antClassLoaderCreateClassLoader.cleanup();
            }
            return true;
        } catch (ClassNotFoundException unused) {
            if (antClassLoaderCreateClassLoader != null) {
                antClassLoaderCreateClassLoader.cleanup();
            }
            return false;
        } catch (Throwable th) {
            if (antClassLoaderCreateClassLoader != null) {
                antClassLoaderCreateClassLoader.cleanup();
            }
            throw th;
        }
    }
}
