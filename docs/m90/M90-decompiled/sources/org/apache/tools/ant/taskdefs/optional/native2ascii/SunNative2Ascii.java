package org.apache.tools.ant.taskdefs.optional.native2ascii;

import java.lang.reflect.Method;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.optional.Native2Ascii;
import org.apache.tools.ant.types.Commandline;

/* JADX INFO: loaded from: classes3.dex */
public final class SunNative2Ascii extends DefaultNative2Ascii {
    public static final String IMPLEMENTATION_NAME = "sun";

    @Override // org.apache.tools.ant.taskdefs.optional.native2ascii.DefaultNative2Ascii
    protected void setup(Commandline commandline, Native2Ascii native2Ascii) throws BuildException {
        if (native2Ascii.getReverse()) {
            commandline.createArgument().setValue("-reverse");
        }
        super.setup(commandline, native2Ascii);
    }

    @Override // org.apache.tools.ant.taskdefs.optional.native2ascii.DefaultNative2Ascii
    protected boolean run(Commandline commandline, ProjectComponent projectComponent) throws BuildException {
        try {
            Class<?> cls = Class.forName("sun.tools.native2ascii.Main");
            Method method = cls.getMethod("convert", String[].class);
            if (method != null) {
                return ((Boolean) method.invoke(cls.newInstance(), commandline.getArguments())).booleanValue();
            }
            throw new BuildException("Could not find convert() method in sun.tools.native2ascii.Main");
        } catch (BuildException e) {
            throw e;
        } catch (Exception e2) {
            throw new BuildException("Error starting Sun's native2ascii: ", e2);
        }
    }
}
