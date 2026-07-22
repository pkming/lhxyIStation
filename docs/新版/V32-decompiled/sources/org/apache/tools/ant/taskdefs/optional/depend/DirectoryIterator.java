package org.apache.tools.ant.taskdefs.optional.depend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

/* JADX INFO: loaded from: classes3.dex */
public class DirectoryIterator implements ClassFileIterator {
    private Enumeration currentEnum;
    private Stack enumStack = new Stack();

    public DirectoryIterator(File file, boolean z) throws IOException {
        this.currentEnum = getDirectoryEntries(file).elements();
    }

    private Vector getDirectoryEntries(File file) {
        Vector vector = new Vector();
        String[] list = file.list();
        if (list != null) {
            for (String str : list) {
                vector.addElement(new File(file, str));
            }
        }
        return vector;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.depend.ClassFileIterator
    public ClassFile getNextClassFile() {
        ClassFile classFile = null;
        while (classFile == null) {
            try {
                if (this.currentEnum.hasMoreElements()) {
                    File file = (File) this.currentEnum.nextElement();
                    if (file.isDirectory()) {
                        this.enumStack.push(this.currentEnum);
                        this.currentEnum = getDirectoryEntries(file).elements();
                    } else {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        if (file.getName().endsWith(".class")) {
                            classFile = new ClassFile();
                            classFile.read(fileInputStream);
                        }
                    }
                } else {
                    if (this.enumStack.empty()) {
                        break;
                    }
                    this.currentEnum = (Enumeration) this.enumStack.pop();
                }
            } catch (IOException unused) {
                return null;
            }
        }
        return classFile;
    }
}
