package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Resource;

/* JADX INFO: loaded from: classes3.dex */
public class Type implements ResourceSelector {
    private static final String DIR_ATTR = "dir";
    private static final String FILE_ATTR = "file";
    private FileDir type = null;
    public static final Type FILE = new Type(new FileDir("file"));
    public static final Type DIR = new Type(new FileDir("dir"));
    private static final String ANY_ATTR = "any";
    public static final Type ANY = new Type(new FileDir(ANY_ATTR));

    public static class FileDir extends EnumeratedAttribute {
        private static final String[] VALUES = {"file", "dir", Type.ANY_ATTR};

        public FileDir() {
        }

        public FileDir(String str) {
            setValue(str);
        }

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return VALUES;
        }
    }

    public Type() {
    }

    public Type(FileDir fileDir) {
        setType(fileDir);
    }

    public void setType(FileDir fileDir) {
        this.type = fileDir;
    }

    @Override // org.apache.tools.ant.types.resources.selectors.ResourceSelector
    public boolean isSelected(Resource resource) {
        FileDir fileDir = this.type;
        if (fileDir == null) {
            throw new BuildException("The type attribute is required.");
        }
        int index = fileDir.getIndex();
        if (index == 2) {
            return true;
        }
        if (resource.isDirectory()) {
            if (index == 1) {
                return true;
            }
        } else if (index == 0) {
            return true;
        }
        return false;
    }
}
