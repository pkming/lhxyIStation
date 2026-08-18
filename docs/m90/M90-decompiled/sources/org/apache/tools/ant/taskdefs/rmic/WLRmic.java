package org.apache.tools.ant.taskdefs.rmic;

/* JADX INFO: loaded from: classes3.dex */
public class WLRmic extends DefaultRmicAdapter {
    public static final String COMPILER_NAME = "weblogic";
    public static final String ERROR_NO_WLRMIC_ON_CLASSPATH = "Cannot use WebLogic rmic, as it is not available. Add it to Ant's classpath with the -lib option";
    public static final String ERROR_WLRMIC_FAILED = "Error starting WebLogic rmic: ";
    public static final String UNSUPPORTED_STUB_OPTION = "Unsupported stub option: ";
    public static final String WLRMIC_CLASSNAME = "weblogic.rmic";
    public static final String WL_RMI_SKEL_SUFFIX = "_WLSkel";
    public static final String WL_RMI_STUB_SUFFIX = "_WLStub";

    @Override // org.apache.tools.ant.taskdefs.rmic.DefaultRmicAdapter
    public String getSkelClassSuffix() {
        return WL_RMI_SKEL_SUFFIX;
    }

    @Override // org.apache.tools.ant.taskdefs.rmic.DefaultRmicAdapter
    public String getStubClassSuffix() {
        return WL_RMI_STUB_SUFFIX;
    }

    /* JADX WARN: Removed duplicated region for block: B:29:0x0078 A[Catch: all -> 0x0071, TryCatch #2 {all -> 0x0071, blocks: (B:3:0x0015, B:6:0x0022, B:27:0x0074, B:29:0x0078, B:30:0x007a, B:31:0x007b, B:32:0x008a, B:7:0x0028, B:33:0x008b, B:34:0x009a), top: B:39:0x0015 }] */
    /* JADX WARN: Removed duplicated region for block: B:31:0x007b A[Catch: all -> 0x0071, TryCatch #2 {all -> 0x0071, blocks: (B:3:0x0015, B:6:0x0022, B:27:0x0074, B:29:0x0078, B:30:0x007a, B:31:0x007b, B:32:0x008a, B:7:0x0028, B:33:0x008b, B:34:0x009a), top: B:39:0x0015 }] */
    /* JADX WARN: Removed duplicated region for block: B:36:0x009d  */
    @Override // org.apache.tools.ant.taskdefs.rmic.RmicAdapter
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean execute() throws java.lang.Throwable {
        /*
            r10 = this;
            org.apache.tools.ant.taskdefs.Rmic r0 = r10.getRmic()
            java.lang.String r1 = "Using WebLogic rmic"
            r2 = 3
            r0.log(r1, r2)
            java.lang.String r0 = "-noexit"
            java.lang.String[] r0 = new java.lang.String[]{r0}
            org.apache.tools.ant.types.Commandline r0 = r10.setupRmicCommand(r0)
            r1 = 0
            org.apache.tools.ant.taskdefs.Rmic r2 = r10.getRmic()     // Catch: java.lang.Throwable -> L71 java.lang.Exception -> L73 java.lang.ClassNotFoundException -> L8b
            org.apache.tools.ant.types.Path r2 = r2.getClasspath()     // Catch: java.lang.Throwable -> L71 java.lang.Exception -> L73 java.lang.ClassNotFoundException -> L8b
            java.lang.String r3 = "weblogic.rmic"
            r4 = 1
            if (r2 != 0) goto L28
            java.lang.Class r2 = java.lang.Class.forName(r3)     // Catch: java.lang.Throwable -> L71 java.lang.Exception -> L73 java.lang.ClassNotFoundException -> L8b
            r3 = r1
            goto L43
        L28:
            org.apache.tools.ant.taskdefs.Rmic r2 = r10.getRmic()     // Catch: java.lang.Throwable -> L71 java.lang.Exception -> L73 java.lang.ClassNotFoundException -> L8b
            org.apache.tools.ant.Project r2 = r2.getProject()     // Catch: java.lang.Throwable -> L71 java.lang.Exception -> L73 java.lang.ClassNotFoundException -> L8b
            org.apache.tools.ant.taskdefs.Rmic r5 = r10.getRmic()     // Catch: java.lang.Throwable -> L71 java.lang.Exception -> L73 java.lang.ClassNotFoundException -> L8b
            org.apache.tools.ant.types.Path r5 = r5.getClasspath()     // Catch: java.lang.Throwable -> L71 java.lang.Exception -> L73 java.lang.ClassNotFoundException -> L8b
            org.apache.tools.ant.AntClassLoader r2 = r2.createClassLoader(r5)     // Catch: java.lang.Throwable -> L71 java.lang.Exception -> L73 java.lang.ClassNotFoundException -> L8b
            java.lang.Class r3 = java.lang.Class.forName(r3, r4, r2)     // Catch: java.lang.Throwable -> L69 java.lang.Exception -> L6c java.lang.ClassNotFoundException -> L6f
            r9 = r3
            r3 = r2
            r2 = r9
        L43:
            java.lang.String r5 = "main"
            java.lang.Class[] r6 = new java.lang.Class[r4]     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64 java.lang.ClassNotFoundException -> L67
            java.lang.Class<java.lang.String[]> r7 = java.lang.String[].class
            r8 = 0
            r6[r8] = r7     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64 java.lang.ClassNotFoundException -> L67
            java.lang.reflect.Method r2 = r2.getMethod(r5, r6)     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64 java.lang.ClassNotFoundException -> L67
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64 java.lang.ClassNotFoundException -> L67
            java.lang.String[] r0 = r0.getArguments()     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64 java.lang.ClassNotFoundException -> L67
            r5[r8] = r0     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64 java.lang.ClassNotFoundException -> L67
            r2.invoke(r1, r5)     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64 java.lang.ClassNotFoundException -> L67
            if (r3 == 0) goto L60
            r3.cleanup()
        L60:
            return r4
        L61:
            r0 = move-exception
            r1 = r3
            goto L9b
        L64:
            r0 = move-exception
            r1 = r3
            goto L74
        L67:
            r1 = r3
            goto L8b
        L69:
            r0 = move-exception
            r1 = r2
            goto L9b
        L6c:
            r0 = move-exception
            r1 = r2
            goto L74
        L6f:
            r1 = r2
            goto L8b
        L71:
            r0 = move-exception
            goto L9b
        L73:
            r0 = move-exception
        L74:
            boolean r2 = r0 instanceof org.apache.tools.ant.BuildException     // Catch: java.lang.Throwable -> L71
            if (r2 == 0) goto L7b
            org.apache.tools.ant.BuildException r0 = (org.apache.tools.ant.BuildException) r0     // Catch: java.lang.Throwable -> L71
            throw r0     // Catch: java.lang.Throwable -> L71
        L7b:
            org.apache.tools.ant.BuildException r2 = new org.apache.tools.ant.BuildException     // Catch: java.lang.Throwable -> L71
            java.lang.String r3 = "Error starting WebLogic rmic: "
            org.apache.tools.ant.taskdefs.Rmic r4 = r10.getRmic()     // Catch: java.lang.Throwable -> L71
            org.apache.tools.ant.Location r4 = r4.getLocation()     // Catch: java.lang.Throwable -> L71
            r2.<init>(r3, r0, r4)     // Catch: java.lang.Throwable -> L71
            throw r2     // Catch: java.lang.Throwable -> L71
        L8b:
            org.apache.tools.ant.BuildException r0 = new org.apache.tools.ant.BuildException     // Catch: java.lang.Throwable -> L71
            java.lang.String r2 = "Cannot use WebLogic rmic, as it is not available. Add it to Ant's classpath with the -lib option"
            org.apache.tools.ant.taskdefs.Rmic r3 = r10.getRmic()     // Catch: java.lang.Throwable -> L71
            org.apache.tools.ant.Location r3 = r3.getLocation()     // Catch: java.lang.Throwable -> L71
            r0.<init>(r2, r3)     // Catch: java.lang.Throwable -> L71
            throw r0     // Catch: java.lang.Throwable -> L71
        L9b:
            if (r1 == 0) goto La0
            r1.cleanup()
        La0:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.taskdefs.rmic.WLRmic.execute():boolean");
    }

    @Override // org.apache.tools.ant.taskdefs.rmic.DefaultRmicAdapter
    protected String[] preprocessCompilerArgs(String[] strArr) {
        return filterJvmCompilerArgs(strArr);
    }

    @Override // org.apache.tools.ant.taskdefs.rmic.DefaultRmicAdapter
    protected String addStubVersionOptions() {
        String stubVersion = getRmic().getStubVersion();
        if (stubVersion == null) {
            return null;
        }
        getRmic().log(UNSUPPORTED_STUB_OPTION + stubVersion, 1);
        return null;
    }
}
