package org.apache.tools.ant.taskdefs.launcher;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class CommandLauncher {
    protected static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static CommandLauncher shellLauncher;
    private static CommandLauncher vmLauncher;

    static {
        vmLauncher = null;
        shellLauncher = null;
        if (!Os.isFamily(Os.FAMILY_OS2)) {
            vmLauncher = new Java13CommandLauncher();
        }
        if (Os.isFamily(Os.FAMILY_MAC) && !Os.isFamily(Os.FAMILY_UNIX)) {
            shellLauncher = new MacCommandLauncher(new CommandLauncher());
            return;
        }
        if (Os.isFamily(Os.FAMILY_OS2)) {
            shellLauncher = new OS2CommandLauncher(new CommandLauncher());
            return;
        }
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            CommandLauncher commandLauncher = new CommandLauncher();
            if (!Os.isFamily(Os.FAMILY_9X)) {
                shellLauncher = new WinNTCommandLauncher(commandLauncher);
                return;
            } else {
                shellLauncher = new ScriptCommandLauncher("bin/antRun.bat", commandLauncher);
                return;
            }
        }
        if (Os.isFamily(Os.FAMILY_NETWARE)) {
            shellLauncher = new PerlScriptCommandLauncher("bin/antRun.pl", new CommandLauncher());
        } else if (Os.isFamily(Os.FAMILY_VMS)) {
            shellLauncher = new VmsCommandLauncher();
        } else {
            shellLauncher = new ScriptCommandLauncher("bin/antRun", new CommandLauncher());
        }
    }

    public Process exec(Project project, String[] strArr, String[] strArr2) throws IOException {
        if (project != null) {
            project.log("Execute:CommandLauncher: " + Commandline.describeCommand(strArr), 4);
        }
        return Runtime.getRuntime().exec(strArr, strArr2);
    }

    public Process exec(Project project, String[] strArr, String[] strArr2, File file) throws IOException {
        if (file == null) {
            return exec(project, strArr, strArr2);
        }
        throw new IOException("Cannot execute a process in different directory under this JVM");
    }

    public static CommandLauncher getShellLauncher(Project project) {
        CommandLauncher commandLauncherExtractLauncher = extractLauncher(MagicNames.ANT_SHELL_LAUNCHER_REF_ID, project);
        return commandLauncherExtractLauncher == null ? shellLauncher : commandLauncherExtractLauncher;
    }

    public static CommandLauncher getVMLauncher(Project project) {
        CommandLauncher commandLauncherExtractLauncher = extractLauncher(MagicNames.ANT_VM_LAUNCHER_REF_ID, project);
        return commandLauncherExtractLauncher == null ? vmLauncher : commandLauncherExtractLauncher;
    }

    private static CommandLauncher extractLauncher(String str, Project project) {
        CommandLauncher commandLauncher = project != null ? (CommandLauncher) project.getReference(str) : null;
        return commandLauncher == null ? getSystemLauncher(str) : commandLauncher;
    }

    private static CommandLauncher getSystemLauncher(String str) {
        String property = System.getProperty(str);
        if (property != null) {
            try {
                return (CommandLauncher) Class.forName(property).newInstance();
            } catch (ClassNotFoundException e) {
                System.err.println("Could not instantiate launcher class " + property + ": " + e.getMessage());
            } catch (IllegalAccessException e2) {
                System.err.println("Could not instantiate launcher class " + property + ": " + e2.getMessage());
            } catch (InstantiationException e3) {
                System.err.println("Could not instantiate launcher class " + property + ": " + e3.getMessage());
            }
        }
        return null;
    }

    public static void setVMLauncher(Project project, CommandLauncher commandLauncher) {
        if (project != null) {
            project.addReference(MagicNames.ANT_VM_LAUNCHER_REF_ID, commandLauncher);
        }
    }

    public static void setShellLauncher(Project project, CommandLauncher commandLauncher) {
        if (project != null) {
            project.addReference(MagicNames.ANT_SHELL_LAUNCHER_REF_ID, commandLauncher);
        }
    }
}
