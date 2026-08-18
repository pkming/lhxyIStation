package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.Project;

/* JADX INFO: loaded from: classes3.dex */
public class Jikes {
    private static final int MAX_FILES_ON_COMMAND_LINE = 250;
    protected String command;
    protected JikesOutputParser jop;
    protected Project project;

    protected Jikes(JikesOutputParser jikesOutputParser, String str, Project project) {
        System.err.println("As of Ant 1.2 released in October 2000, the Jikes class");
        System.err.println("is considered to be dead code by the Ant developers and is unmaintained.");
        System.err.println("Don't use it!");
        this.jop = jikesOutputParser;
        this.command = str;
        this.project = project;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0089 A[Catch: all -> 0x00c6, TRY_ENTER, TRY_LEAVE, TryCatch #4 {all -> 0x00c6, blocks: (B:3:0x0001, B:5:0x0017, B:36:0x0096, B:43:0x00be, B:44:0x00c5, B:34:0x0089), top: B:52:0x0001, inners: #7 }] */
    /* JADX WARN: Type inference failed for: r1v10, types: [int] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void compile(java.lang.String[] r12) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 211
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.taskdefs.Jikes.compile(java.lang.String[]):void");
    }
}
