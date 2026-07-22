package org.apache.tools.ant.taskdefs.rmic;

import org.apache.tools.ant.types.Commandline;

/* JADX INFO: loaded from: classes3.dex */
public class XNewRmic extends ForkingSunRmic {
    public static final String COMPILER_NAME = "xnew";

    @Override // org.apache.tools.ant.taskdefs.rmic.DefaultRmicAdapter
    protected Commandline setupRmicCommand() {
        return super.setupRmicCommand(new String[]{"-Xnew"});
    }
}
