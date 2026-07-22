package org.apache.tools.ant.taskdefs.optional.extension.resolvers;

import java.io.File;
import java.net.URL;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Get;
import org.apache.tools.ant.taskdefs.optional.extension.Extension;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionResolver;

/* JADX INFO: loaded from: classes3.dex */
public class URLResolver implements ExtensionResolver {
    private File destdir;
    private File destfile;
    private URL url;

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setDestfile(File file) {
        this.destfile = file;
    }

    public void setDestdir(File file) {
        this.destdir = file;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.extension.ExtensionResolver
    public File resolve(Extension extension, Project project) throws BuildException {
        validate();
        File dest = getDest();
        Get get = new Get();
        get.setProject(project);
        get.setDest(dest);
        get.setSrc(this.url);
        get.execute();
        return dest;
    }

    private File getDest() {
        String strSubstring;
        File file = this.destfile;
        if (file != null) {
            return file;
        }
        String file2 = this.url.getFile();
        if (file2 == null || file2.length() <= 1) {
            strSubstring = "default.file";
        } else {
            int iLastIndexOf = file2.lastIndexOf(47);
            if (-1 == iLastIndexOf) {
                iLastIndexOf = 0;
            }
            strSubstring = file2.substring(iLastIndexOf);
        }
        return new File(this.destdir, strSubstring);
    }

    private void validate() {
        if (this.url == null) {
            throw new BuildException("Must specify URL");
        }
        File file = this.destdir;
        if (file == null && this.destfile == null) {
            throw new BuildException("Must specify destination file or directory");
        }
        if (file != null && this.destfile != null) {
            throw new BuildException("Must not specify both destination file or directory");
        }
    }

    public String toString() {
        return "URL[" + this.url + "]";
    }
}
