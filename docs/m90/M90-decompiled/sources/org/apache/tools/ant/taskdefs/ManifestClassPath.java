package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.UnsupportedEncodingException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.launch.Locator;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ManifestClassPath extends Task {
    private File dir;
    private int maxParentLevels = 2;
    private String name;
    private Path path;

    @Override // org.apache.tools.ant.Task
    public void execute() {
        if (this.name == null) {
            throw new BuildException("Missing 'property' attribute!");
        }
        if (this.dir == null) {
            throw new BuildException("Missing 'jarfile' attribute!");
        }
        if (getProject().getProperty(this.name) != null) {
            throw new BuildException("Property '" + this.name + "' already set!");
        }
        if (this.path == null) {
            throw new BuildException("Missing nested <classpath>!");
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < this.maxParentLevels + 1; i++) {
            stringBuffer.append("../");
        }
        String string = stringBuffer.toString();
        FileUtils fileUtils = FileUtils.getFileUtils();
        this.dir = fileUtils.normalize(this.dir.getAbsolutePath());
        String[] list = this.path.list();
        StringBuffer stringBuffer2 = new StringBuffer();
        for (String str : list) {
            String absolutePath = new File(str).getAbsolutePath();
            File fileNormalize = fileUtils.normalize(absolutePath);
            try {
                String relativePath = this.dir.equals(fileNormalize) ? "." : FileUtils.getRelativePath(this.dir, fileNormalize);
                String canonicalPath = fileNormalize.getCanonicalPath();
                if (File.separatorChar != '/') {
                    canonicalPath = canonicalPath.replace(File.separatorChar, '/');
                }
                if (relativePath.equals(canonicalPath) || relativePath.startsWith(string)) {
                    throw new BuildException("No suitable relative path from " + this.dir + " to " + absolutePath);
                }
                if (fileNormalize.isDirectory() && !relativePath.endsWith("/")) {
                    relativePath = relativePath + '/';
                }
                try {
                    stringBuffer2.append(Locator.encodeURI(relativePath));
                    stringBuffer2.append(' ');
                } catch (UnsupportedEncodingException e) {
                    throw new BuildException(e);
                }
            } catch (Exception e2) {
                throw new BuildException("error trying to get the relative path from " + this.dir + " to " + absolutePath, e2);
            }
        }
        getProject().setNewProperty(this.name, stringBuffer2.toString().trim());
    }

    public void setProperty(String str) {
        this.name = str;
    }

    public void setJarFile(File file) {
        File parentFile = file.getParentFile();
        if (!parentFile.isDirectory()) {
            throw new BuildException("Jar's directory not found: " + parentFile);
        }
        this.dir = parentFile;
    }

    public void setMaxParentLevels(int i) {
        if (i < 0) {
            throw new BuildException("maxParentLevels must not be a negative number");
        }
        this.maxParentLevels = i;
    }

    public void addClassPath(Path path) {
        this.path = path;
    }
}
