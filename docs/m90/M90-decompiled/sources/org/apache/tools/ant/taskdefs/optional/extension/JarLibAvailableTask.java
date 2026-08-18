package org.apache.tools.ant.taskdefs.optional.extension;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/* JADX INFO: loaded from: classes3.dex */
public class JarLibAvailableTask extends Task {
    private final Vector extensionFileSets = new Vector();
    private File libraryFile;
    private String propertyName;
    private ExtensionAdapter requiredExtension;

    public void setProperty(String str) {
        this.propertyName = str;
    }

    public void setFile(File file) {
        this.libraryFile = file;
    }

    public void addConfiguredExtension(ExtensionAdapter extensionAdapter) {
        if (this.requiredExtension != null) {
            throw new BuildException("Can not specify extension to search for multiple times.");
        }
        this.requiredExtension = extensionAdapter;
    }

    public void addConfiguredExtensionSet(ExtensionSet extensionSet) {
        this.extensionFileSets.addElement(extensionSet);
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        validate();
        Extension extension = this.requiredExtension.toExtension();
        if (!this.extensionFileSets.isEmpty()) {
            Iterator it = this.extensionFileSets.iterator();
            while (it.hasNext()) {
                for (Extension extension2 : ((ExtensionSet) it.next()).toExtensions(getProject())) {
                    if (extension2.isCompatibleWith(extension)) {
                        getProject().setNewProperty(this.propertyName, "true");
                    }
                }
            }
            return;
        }
        for (Extension extension3 : Extension.getAvailable(ExtensionUtil.getManifest(this.libraryFile))) {
            if (extension3.isCompatibleWith(extension)) {
                getProject().setNewProperty(this.propertyName, "true");
            }
        }
    }

    private void validate() throws BuildException {
        if (this.requiredExtension == null) {
            throw new BuildException("Extension element must be specified.");
        }
        if (this.libraryFile == null && this.extensionFileSets.isEmpty()) {
            throw new BuildException("File attribute not specified.");
        }
        File file = this.libraryFile;
        if (file != null && !file.exists()) {
            throw new BuildException("File '" + this.libraryFile + "' does not exist.");
        }
        File file2 = this.libraryFile;
        if (file2 != null && !file2.isFile()) {
            throw new BuildException("'" + this.libraryFile + "' is not a file.");
        }
    }
}
