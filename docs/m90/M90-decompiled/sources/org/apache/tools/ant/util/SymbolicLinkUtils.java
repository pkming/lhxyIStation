package org.apache.tools.ant.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;

/* JADX INFO: loaded from: classes3.dex */
public class SymbolicLinkUtils {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final SymbolicLinkUtils PRIMARY_INSTANCE = new SymbolicLinkUtils();

    public static SymbolicLinkUtils getSymbolicLinkUtils() {
        return PRIMARY_INSTANCE;
    }

    protected SymbolicLinkUtils() {
    }

    public boolean isSymbolicLink(File file) throws IOException {
        return isSymbolicLink(file.getParentFile(), file.getName());
    }

    public boolean isSymbolicLink(String str) throws IOException {
        return isSymbolicLink(new File(str));
    }

    public boolean isSymbolicLink(File file, String str) throws IOException {
        File file2 = file != null ? new File(file.getCanonicalPath(), str) : new File(str);
        return !file2.getAbsolutePath().equals(file2.getCanonicalPath());
    }

    public boolean isDanglingSymbolicLink(String str) throws IOException {
        return isDanglingSymbolicLink(new File(str));
    }

    public boolean isDanglingSymbolicLink(File file) throws IOException {
        return isDanglingSymbolicLink(file.getParentFile(), file.getName());
    }

    public boolean isDanglingSymbolicLink(File file, String str) throws IOException {
        File file2 = new File(file, str);
        if (file2.exists()) {
            return false;
        }
        final String name = file2.getName();
        String[] list = file.list(new FilenameFilter() { // from class: org.apache.tools.ant.util.SymbolicLinkUtils.1
            @Override // java.io.FilenameFilter
            public boolean accept(File file3, String str2) {
                return name.equals(str2);
            }
        });
        return list != null && list.length > 0;
    }

    public void deleteSymbolicLink(File file, Task task) throws Throwable {
        if (isDanglingSymbolicLink(file)) {
            if (!file.delete()) {
                throw new IOException("failed to remove dangling symbolic link " + file);
            }
            return;
        }
        if (!isSymbolicLink(file)) {
            return;
        }
        if (!file.exists()) {
            throw new FileNotFoundException("No such symbolic link: " + file);
        }
        File canonicalFile = file.getCanonicalFile();
        boolean z = true;
        if (task != null && !canonicalFile.getParentFile().canWrite()) {
            Execute.runCommand(task, new String[]{"rm", file.getAbsolutePath()});
            return;
        }
        FileUtils fileUtils = FILE_UTILS;
        File fileCreateTempFile = fileUtils.createTempFile("symlink", ".tmp", canonicalFile.getParentFile(), false, false);
        File file2 = fileUtils.isLeadingPath(canonicalFile, file) ? new File(fileCreateTempFile, fileUtils.removeLeadingPath(canonicalFile, file)) : file;
        try {
            try {
                fileUtils.rename(canonicalFile, fileCreateTempFile);
                try {
                    if (!file2.delete()) {
                        throw new IOException("Couldn't delete symlink: " + file2 + " (was it a real file? is this not a UNIX system?)");
                    }
                    try {
                        fileUtils.rename(fileCreateTempFile, canonicalFile);
                    } catch (IOException e) {
                        throw new IOException("Couldn't return resource " + fileCreateTempFile + " to its original name: " + canonicalFile.getAbsolutePath() + ". Reason: " + e.getMessage() + "\n THE RESOURCE'S NAME ON DISK HAS BEEN CHANGED BY THIS ERROR!\n");
                    }
                } catch (Throwable th) {
                    th = th;
                    if (z) {
                        try {
                            FILE_UTILS.rename(fileCreateTempFile, canonicalFile);
                        } catch (IOException e2) {
                            throw new IOException("Couldn't return resource " + fileCreateTempFile + " to its original name: " + canonicalFile.getAbsolutePath() + ". Reason: " + e2.getMessage() + "\n THE RESOURCE'S NAME ON DISK HAS BEEN CHANGED BY THIS ERROR!\n");
                        }
                    }
                    throw th;
                }
            } catch (IOException e3) {
                throw new IOException("Couldn't rename resource when attempting to delete '" + file2 + "'.  Reason: " + e3.getMessage());
            }
        } catch (Throwable th2) {
            th = th2;
            z = false;
        }
    }
}
