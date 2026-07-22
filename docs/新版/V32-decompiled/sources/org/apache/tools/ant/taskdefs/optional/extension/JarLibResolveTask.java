package org.apache.tools.ant.taskdefs.optional.extension;

import java.io.File;
import java.util.ArrayList;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.extension.resolvers.AntResolver;
import org.apache.tools.ant.taskdefs.optional.extension.resolvers.LocationResolver;
import org.apache.tools.ant.taskdefs.optional.extension.resolvers.URLResolver;

/* JADX INFO: loaded from: classes3.dex */
public class JarLibResolveTask extends Task {
    private String propertyName;
    private Extension requiredExtension;
    private final ArrayList resolvers = new ArrayList();
    private boolean checkExtension = true;
    private boolean failOnError = true;

    public void setProperty(String str) {
        this.propertyName = str;
    }

    public void setCheckExtension(boolean z) {
        this.checkExtension = z;
    }

    public void setFailOnError(boolean z) {
        this.failOnError = z;
    }

    public void addConfiguredLocation(LocationResolver locationResolver) {
        this.resolvers.add(locationResolver);
    }

    public void addConfiguredUrl(URLResolver uRLResolver) {
        this.resolvers.add(uRLResolver);
    }

    public void addConfiguredAnt(AntResolver antResolver) {
        this.resolvers.add(antResolver);
    }

    public void addConfiguredExtension(ExtensionAdapter extensionAdapter) {
        if (this.requiredExtension != null) {
            throw new BuildException("Can not specify extension to resolve multiple times.");
        }
        this.requiredExtension = extensionAdapter.toExtension();
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        validate();
        getProject().log("Resolving extension: " + this.requiredExtension, 3);
        String property = getProject().getProperty(this.propertyName);
        if (property != null) {
            String str = "Property Already set to: " + property;
            if (this.failOnError) {
                throw new BuildException(str);
            }
            getProject().log(str, 0);
            return;
        }
        int size = this.resolvers.size();
        for (int i = 0; i < size; i++) {
            ExtensionResolver extensionResolver = (ExtensionResolver) this.resolvers.get(i);
            getProject().log("Searching for extension using Resolver:" + extensionResolver, 3);
            try {
                File fileResolve = extensionResolver.resolve(this.requiredExtension, getProject());
                try {
                    checkExtension(fileResolve);
                    return;
                } catch (BuildException e) {
                    getProject().log("File " + fileResolve + " returned by resolver failed to satisfy extension due to: " + e.getMessage(), 1);
                }
            } catch (BuildException e2) {
                getProject().log("Failed to resolve extension to file using resolver " + extensionResolver + " due to: " + e2, 1);
            }
        }
        missingExtension();
    }

    private void missingExtension() {
        if (this.failOnError) {
            throw new BuildException("Unable to resolve extension to a file");
        }
        getProject().log("Unable to resolve extension to a file", 0);
    }

    private void checkExtension(File file) {
        if (!file.exists()) {
            throw new BuildException("File " + file + " does not exist");
        }
        if (!file.isFile()) {
            throw new BuildException("File " + file + " is not a file");
        }
        if (!this.checkExtension) {
            getProject().log("Setting property to " + file + " without verifying library satisfies extension", 3);
            setLibraryProperty(file);
            return;
        }
        getProject().log("Checking file " + file + " to see if it satisfies extension", 3);
        for (Extension extension : Extension.getAvailable(ExtensionUtil.getManifest(file))) {
            if (extension.isCompatibleWith(this.requiredExtension)) {
                setLibraryProperty(file);
                return;
            }
        }
        String str = "File " + file + " skipped as it does not satisfy extension";
        getProject().log(str, 3);
        throw new BuildException(str);
    }

    private void setLibraryProperty(File file) {
        getProject().setNewProperty(this.propertyName, file.getAbsolutePath());
    }

    private void validate() throws BuildException {
        if (this.propertyName == null) {
            throw new BuildException("Property attribute must be specified.");
        }
        if (this.requiredExtension == null) {
            throw new BuildException("Extension element must be specified.");
        }
    }
}
