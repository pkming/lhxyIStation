package org.apache.tools.ant.taskdefs.optional.native2ascii;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.ExecuteJava;
import org.apache.tools.ant.taskdefs.optional.Native2Ascii;
import org.apache.tools.ant.types.Commandline;

/* JADX INFO: loaded from: classes3.dex */
public final class KaffeNative2Ascii extends DefaultNative2Ascii {
    public static final String IMPLEMENTATION_NAME = "kaffe";
    private static final String[] N2A_CLASSNAMES = {"gnu.classpath.tools.native2ascii.Native2ASCII", "kaffe.tools.native2ascii.Native2Ascii"};

    @Override // org.apache.tools.ant.taskdefs.optional.native2ascii.DefaultNative2Ascii
    protected void setup(Commandline commandline, Native2Ascii native2Ascii) throws BuildException {
        if (native2Ascii.getReverse()) {
            throw new BuildException("-reverse is not supported by Kaffe");
        }
        super.setup(commandline, native2Ascii);
    }

    @Override // org.apache.tools.ant.taskdefs.optional.native2ascii.DefaultNative2Ascii
    protected boolean run(Commandline commandline, ProjectComponent projectComponent) throws BuildException {
        ExecuteJava executeJava = new ExecuteJava();
        Class n2aClass = getN2aClass();
        if (n2aClass == null) {
            throw new BuildException("Couldn't load Kaffe's Native2Ascii class");
        }
        commandline.setExecutable(n2aClass.getName());
        executeJava.setJavaCommand(commandline);
        executeJava.execute(projectComponent.getProject());
        return true;
    }

    private static Class getN2aClass() {
        int i = 0;
        while (true) {
            String[] strArr = N2A_CLASSNAMES;
            if (i >= strArr.length) {
                return null;
            }
            try {
                return Class.forName(strArr[i]);
            } catch (ClassNotFoundException unused) {
                i++;
            }
        }
    }
}
