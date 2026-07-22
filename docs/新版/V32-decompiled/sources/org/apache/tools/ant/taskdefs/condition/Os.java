package org.apache.tools.ant.taskdefs.condition;

import java.util.Locale;
import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public class Os implements Condition {
    private static final String DARWIN = "darwin";
    public static final String FAMILY_9X = "win9x";
    public static final String FAMILY_DOS = "dos";
    public static final String FAMILY_MAC = "mac";
    public static final String FAMILY_NETWARE = "netware";
    public static final String FAMILY_NT = "winnt";
    public static final String FAMILY_OS2 = "os/2";
    public static final String FAMILY_OS400 = "os/400";
    public static final String FAMILY_TANDEM = "tandem";
    public static final String FAMILY_UNIX = "unix";
    public static final String FAMILY_VMS = "openvms";
    public static final String FAMILY_WINDOWS = "windows";
    public static final String FAMILY_ZOS = "z/os";
    private String arch;
    private String family;
    private String name;
    private String version;
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
    private static final String OS_ARCH = System.getProperty("os.arch").toLowerCase(Locale.ENGLISH);
    private static final String OS_VERSION = System.getProperty("os.version").toLowerCase(Locale.ENGLISH);
    private static final String PATH_SEP = System.getProperty("path.separator");

    public Os() {
    }

    public Os(String str) {
        setFamily(str);
    }

    public void setFamily(String str) {
        this.family = str.toLowerCase(Locale.ENGLISH);
    }

    public void setName(String str) {
        this.name = str.toLowerCase(Locale.ENGLISH);
    }

    public void setArch(String str) {
        this.arch = str.toLowerCase(Locale.ENGLISH);
    }

    public void setVersion(String str) {
        this.version = str.toLowerCase(Locale.ENGLISH);
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() throws BuildException {
        return isOs(this.family, this.name, this.arch, this.version);
    }

    public static boolean isFamily(String str) {
        return isOs(str, null, null, null);
    }

    public static boolean isName(String str) {
        return isOs(null, str, null, null);
    }

    public static boolean isArch(String str) {
        return isOs(null, null, str, null);
    }

    public static boolean isVersion(String str) {
        return isOs(null, null, null, str);
    }

    /* JADX WARN: Removed duplicated region for block: B:102:0x0151  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x005b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean isOs(java.lang.String r8, java.lang.String r9, java.lang.String r10, java.lang.String r11) {
        /*
            Method dump skipped, instruction units count: 378
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.taskdefs.condition.Os.isOs(java.lang.String, java.lang.String, java.lang.String, java.lang.String):boolean");
    }
}
