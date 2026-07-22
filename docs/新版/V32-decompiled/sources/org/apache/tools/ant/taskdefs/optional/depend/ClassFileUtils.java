package org.apache.tools.ant.taskdefs.optional.depend;

/* JADX INFO: loaded from: classes3.dex */
public class ClassFileUtils {
    public static String convertSlashName(String str) {
        return str.replace('\\', '.').replace('/', '.');
    }

    public static String convertDotName(String str) {
        return str.replace('.', '/');
    }
}
