package org.apache.tools.ant.taskdefs.rmic;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.ExecuteJava;
import org.apache.tools.ant.taskdefs.optional.sos.SOSCmd;
import org.apache.tools.ant.types.Commandline;

/* JADX INFO: loaded from: classes3.dex */
public class KaffeRmic extends DefaultRmicAdapter {
    public static final String COMPILER_NAME = "kaffe";
    private static final String[] RMIC_CLASSNAMES = {"gnu.classpath.tools.rmi.rmic.RMIC", "gnu.java.rmi.rmic.RMIC", "kaffe.rmi.rmic.RMIC"};

    @Override // org.apache.tools.ant.taskdefs.rmic.RmicAdapter
    public boolean execute() throws BuildException {
        getRmic().log("Using Kaffe rmic", 3);
        Commandline commandline = setupRmicCommand();
        Class rmicClass = getRmicClass();
        int i = 0;
        if (rmicClass == null) {
            StringBuffer stringBuffer = new StringBuffer("Cannot use Kaffe rmic, as it is not available.  None of ");
            while (true) {
                String[] strArr = RMIC_CLASSNAMES;
                if (i < strArr.length) {
                    if (i != 0) {
                        stringBuffer.append(", ");
                    }
                    stringBuffer.append(strArr[i]);
                    i++;
                } else {
                    stringBuffer.append(" have been found. A common solution is to set the environment variable JAVA_HOME or CLASSPATH.");
                    throw new BuildException(stringBuffer.toString(), getRmic().getLocation());
                }
            }
        } else {
            commandline.setExecutable(rmicClass.getName());
            String name = rmicClass.getName();
            String[] strArr2 = RMIC_CLASSNAMES;
            if (!name.equals(strArr2[strArr2.length - 1])) {
                commandline.createArgument().setValue(SOSCmd.FLAG_VERBOSE);
                getRmic().log(Commandline.describeCommand(commandline));
            }
            ExecuteJava executeJava = new ExecuteJava();
            executeJava.setJavaCommand(commandline);
            return executeJava.fork(getRmic()) == 0;
        }
    }

    public static boolean isAvailable() {
        return getRmicClass() != null;
    }

    private static Class getRmicClass() {
        int i = 0;
        while (true) {
            String[] strArr = RMIC_CLASSNAMES;
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
