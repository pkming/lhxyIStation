package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSSConstants;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Permissions;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.util.TimeoutObserver;
import org.apache.tools.ant.util.Watchdog;

/* JADX INFO: loaded from: classes3.dex */
public class ExecuteJava implements Runnable, TimeoutObserver {
    private Commandline javaCommand = null;
    private Path classpath = null;
    private CommandlineJava.SysProperties sysProperties = null;
    private Permissions perm = null;
    private Method main = null;
    private Long timeout = null;
    private volatile Throwable caught = null;
    private volatile boolean timedOut = false;
    private Thread thread = null;

    public void setOutput(PrintStream printStream) {
    }

    public void setJavaCommand(Commandline commandline) {
        this.javaCommand = commandline;
    }

    public void setClasspath(Path path) {
        this.classpath = path;
    }

    public void setSystemProperties(CommandlineJava.SysProperties sysProperties) {
        this.sysProperties = sysProperties;
    }

    public void setPermissions(Permissions permissions) {
        this.perm = permissions;
    }

    public void setTimeout(Long l) {
        this.timeout = l;
    }

    public void execute(Project project) throws BuildException {
        AntClassLoader antClassLoader;
        Class<?> cls;
        String executable = this.javaCommand.getExecutable();
        AntClassLoader antClassLoader2 = null;
        try {
            try {
                CommandlineJava.SysProperties sysProperties = this.sysProperties;
                if (sysProperties != null) {
                    sysProperties.setSystem();
                }
                try {
                    Path path = this.classpath;
                    if (path == null) {
                        cls = Class.forName(executable);
                        antClassLoader = null;
                    } else {
                        AntClassLoader antClassLoaderCreateClassLoader = project.createClassLoader(path);
                        try {
                            antClassLoaderCreateClassLoader.setParent(project.getCoreLoader());
                            antClassLoaderCreateClassLoader.setParentFirst(false);
                            antClassLoaderCreateClassLoader.addJavaLibraries();
                            antClassLoaderCreateClassLoader.setIsolated(true);
                            antClassLoaderCreateClassLoader.setThreadContextLoader();
                            antClassLoaderCreateClassLoader.forceLoadClass(executable);
                            antClassLoader = antClassLoaderCreateClassLoader;
                            cls = Class.forName(executable, true, antClassLoaderCreateClassLoader);
                        } catch (ClassNotFoundException unused) {
                            throw new BuildException("Could not find " + executable + ". Make sure you have it in your classpath");
                        } catch (SecurityException e) {
                            throw e;
                        } catch (ThreadDeath e2) {
                            throw e2;
                        } catch (BuildException e3) {
                            throw e3;
                        } catch (Throwable th) {
                            th = th;
                            throw new BuildException(th);
                        }
                    }
                } catch (ClassNotFoundException unused2) {
                }
            } catch (Throwable th2) {
                if (0 != 0) {
                    antClassLoader2.resetThreadContextLoader();
                    antClassLoader2.cleanup();
                }
                CommandlineJava.SysProperties sysProperties2 = this.sysProperties;
                if (sysProperties2 != null) {
                    sysProperties2.restoreSystem();
                }
                throw th2;
            }
        } catch (SecurityException e4) {
            throw e4;
        } catch (ThreadDeath e5) {
            throw e5;
        } catch (BuildException e6) {
            throw e6;
        } catch (Throwable th3) {
            th = th3;
        }
        try {
            Method method = cls.getMethod("main", String[].class);
            this.main = method;
            if (method == null) {
                throw new BuildException("Could not find main() method in " + executable);
            }
            if ((method.getModifiers() & 8) == 0) {
                throw new BuildException("main() method in " + executable + " is not declared static");
            }
            if (this.timeout == null) {
                run();
            } else {
                this.thread = new Thread(this, "ExecuteJava");
                project.registerThreadTask(this.thread, project.getThreadTask(Thread.currentThread()));
                this.thread.setDaemon(true);
                Watchdog watchdog = new Watchdog(this.timeout.longValue());
                watchdog.addTimeoutObserver(this);
                synchronized (this) {
                    this.thread.start();
                    watchdog.start();
                    try {
                        wait();
                    } catch (InterruptedException unused3) {
                    }
                    if (this.timedOut) {
                        project.log("Timeout: sub-process interrupted", 1);
                    } else {
                        this.thread = null;
                        watchdog.stop();
                    }
                }
            }
            if (this.caught != null) {
                throw this.caught;
            }
            if (antClassLoader != null) {
                antClassLoader.resetThreadContextLoader();
                antClassLoader.cleanup();
            }
            CommandlineJava.SysProperties sysProperties3 = this.sysProperties;
            if (sysProperties3 != null) {
                sysProperties3.restoreSystem();
            }
        } catch (SecurityException e7) {
        } catch (ThreadDeath e8) {
        } catch (BuildException e9) {
        } catch (Throwable th4) {
            th = th4;
            throw new BuildException(th);
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        Object[] objArr = {this.javaCommand.getArguments()};
        try {
            try {
                try {
                    Permissions permissions = this.perm;
                    if (permissions != null) {
                        permissions.setSecurityManager();
                    }
                    this.main.invoke(null, objArr);
                    Permissions permissions2 = this.perm;
                    if (permissions2 != null) {
                        permissions2.restoreSecurityManager();
                    }
                    synchronized (this) {
                        notifyAll();
                    }
                } catch (InvocationTargetException e) {
                    Throwable targetException = e.getTargetException();
                    if (!(targetException instanceof InterruptedException)) {
                        this.caught = targetException;
                    }
                    Permissions permissions3 = this.perm;
                    if (permissions3 != null) {
                        permissions3.restoreSecurityManager();
                    }
                    synchronized (this) {
                        notifyAll();
                    }
                }
            } catch (Throwable th) {
                this.caught = th;
                Permissions permissions4 = this.perm;
                if (permissions4 != null) {
                    permissions4.restoreSecurityManager();
                }
                synchronized (this) {
                    notifyAll();
                }
            }
        } catch (Throwable th2) {
            Permissions permissions5 = this.perm;
            if (permissions5 != null) {
                permissions5.restoreSecurityManager();
            }
            synchronized (this) {
                notifyAll();
                throw th2;
            }
        }
    }

    @Override // org.apache.tools.ant.util.TimeoutObserver
    public synchronized void timeoutOccured(Watchdog watchdog) {
        if (this.thread != null) {
            this.timedOut = true;
            this.thread.interrupt();
        }
        notifyAll();
    }

    public synchronized boolean killedProcess() {
        return this.timedOut;
    }

    public int fork(ProjectComponent projectComponent) throws BuildException {
        CommandlineJava commandlineJava = new CommandlineJava();
        commandlineJava.setClassname(this.javaCommand.getExecutable());
        for (String str : this.javaCommand.getArguments()) {
            commandlineJava.createArgument().setValue(str);
        }
        if (this.classpath != null) {
            commandlineJava.createClasspath(projectComponent.getProject()).append(this.classpath);
        }
        CommandlineJava.SysProperties sysProperties = this.sysProperties;
        if (sysProperties != null) {
            commandlineJava.addSysproperties(sysProperties);
        }
        Redirector redirector = new Redirector(projectComponent);
        Execute execute = new Execute(redirector.createHandler(), this.timeout == null ? null : new ExecuteWatchdog(this.timeout.longValue()));
        execute.setAntRun(projectComponent.getProject());
        if (Os.isFamily(Os.FAMILY_VMS)) {
            setupCommandLineForVMS(execute, commandlineJava.getCommandline());
        } else {
            execute.setCommandline(commandlineJava.getCommandline());
        }
        try {
            try {
                int iExecute = execute.execute();
                redirector.complete();
                return iExecute;
            } catch (IOException e) {
                throw new BuildException(e);
            }
        } finally {
            this.timedOut = execute.killedProcess();
        }
    }

    public static void setupCommandLineForVMS(Execute execute, String[] strArr) {
        execute.setVMLauncher(true);
        try {
            String[] strArr2 = new String[strArr.length - 1];
            System.arraycopy(strArr, 1, strArr2, 0, strArr.length - 1);
            File fileCreateVmsJavaOptionFile = JavaEnvUtils.createVmsJavaOptionFile(strArr2);
            fileCreateVmsJavaOptionFile.deleteOnExit();
            execute.setCommandline(new String[]{strArr[0], MSVSSConstants.FLAG_VERSION, fileCreateVmsJavaOptionFile.getPath()});
        } catch (IOException unused) {
            throw new BuildException("Failed to create a temporary file for \"-V\" switch");
        }
    }
}
