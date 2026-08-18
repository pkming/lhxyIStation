package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.ProjectHelperRepository;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.URLProvider;
import org.apache.tools.ant.types.resources.URLResource;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ImportTask extends Task {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private String file;
    private boolean optional;
    private final Union resources;
    private String targetPrefix = ProjectHelper.USE_PROJECT_NAME_AS_TARGET_PREFIX;
    private String prefixSeparator = ".";

    public ImportTask() {
        Union union = new Union();
        this.resources = union;
        union.setCache(true);
    }

    public void setOptional(boolean z) {
        this.optional = z;
    }

    public void setFile(String str) {
        this.file = str;
    }

    public void setAs(String str) {
        this.targetPrefix = str;
    }

    public void setPrefixSeparator(String str) {
        this.prefixSeparator = str;
    }

    public void add(ResourceCollection resourceCollection) {
        this.resources.add(resourceCollection);
    }

    @Override // org.apache.tools.ant.Task
    public void execute() {
        if (this.file == null && this.resources.size() == 0) {
            throw new BuildException("import requires file attribute or at least one nested resource");
        }
        if (getOwningTarget() == null || !"".equals(getOwningTarget().getName())) {
            throw new BuildException("import only allowed as a top-level task");
        }
        ProjectHelper projectHelper = (ProjectHelper) getProject().getReference("ant.projectHelper");
        if (projectHelper == null) {
            throw new BuildException("import requires support in ProjectHelper");
        }
        if (projectHelper.getImportStack().size() == 0) {
            throw new BuildException("import requires support in ProjectHelper");
        }
        if (getLocation() == null || getLocation().getFileName() == null) {
            throw new BuildException("Unable to get location of import task");
        }
        Union union = new Union(getProject(), this.resources);
        Resource fileAttributeResource = getFileAttributeResource();
        if (fileAttributeResource != null) {
            this.resources.add(fileAttributeResource);
        }
        Iterator<Resource> it = union.iterator();
        while (it.hasNext()) {
            importResource(projectHelper, it.next());
        }
    }

    private void importResource(ProjectHelper projectHelper, Resource resource) {
        String str;
        Vector<Object> importStack = projectHelper.getImportStack();
        getProject().log("Importing file " + resource + " from " + getLocation().getFileName(), 3);
        if (!resource.isExists()) {
            String str2 = "Cannot find " + resource + " imported from " + getLocation().getFileName();
            if (this.optional) {
                getProject().log(str2, 3);
                return;
            }
            throw new BuildException(str2);
        }
        if (!isInIncludeMode() && hasAlreadyBeenImported(resource, importStack)) {
            getProject().log("Skipped already imported file:\n   " + resource + "\n", 3);
            return;
        }
        String currentTargetPrefix = ProjectHelper.getCurrentTargetPrefix();
        boolean zIsInIncludeMode = ProjectHelper.isInIncludeMode();
        String currentPrefixSeparator = ProjectHelper.getCurrentPrefixSeparator();
        try {
            try {
                if (isInIncludeMode() && currentTargetPrefix != null && this.targetPrefix != null) {
                    str = currentTargetPrefix + currentPrefixSeparator + this.targetPrefix;
                } else {
                    str = (!isInIncludeMode() && ProjectHelper.USE_PROJECT_NAME_AS_TARGET_PREFIX.equals(this.targetPrefix)) ? currentTargetPrefix : this.targetPrefix;
                }
                setProjectHelperProps(str, this.prefixSeparator, isInIncludeMode());
                ProjectHelper projectHelperForBuildFile = ProjectHelperRepository.getInstance().getProjectHelperForBuildFile(resource);
                projectHelperForBuildFile.getImportStack().addAll(projectHelper.getImportStack());
                projectHelperForBuildFile.getExtensionStack().addAll(projectHelper.getExtensionStack());
                getProject().addReference("ant.projectHelper", projectHelperForBuildFile);
                projectHelperForBuildFile.parse(getProject(), resource);
                getProject().addReference("ant.projectHelper", projectHelper);
                projectHelper.getImportStack().clear();
                projectHelper.getImportStack().addAll(projectHelperForBuildFile.getImportStack());
                projectHelper.getExtensionStack().clear();
                projectHelper.getExtensionStack().addAll(projectHelperForBuildFile.getExtensionStack());
            } catch (BuildException e) {
                throw ProjectHelper.addLocationToBuildException(e, getLocation());
            }
        } finally {
            setProjectHelperProps(currentTargetPrefix, currentPrefixSeparator, zIsInIncludeMode);
        }
    }

    private Resource getFileAttributeResource() {
        String str = this.file;
        if (str == null) {
            return null;
        }
        if (isExistingAbsoluteFile(str)) {
            return new FileResource(new File(this.file));
        }
        File absoluteFile = new File(getLocation().getFileName()).getAbsoluteFile();
        if (absoluteFile.exists()) {
            return new FileResource(FILE_UTILS.resolveFile(new File(absoluteFile.getParent()), this.file));
        }
        try {
            return new URLResource(new URL(new URL(getLocation().getFileName()), this.file));
        } catch (MalformedURLException e) {
            log(e.toString(), 3);
            throw new BuildException("failed to resolve " + this.file + " relative to " + getLocation().getFileName());
        }
    }

    private boolean isExistingAbsoluteFile(String str) {
        File file = new File(str);
        return file.isAbsolute() && file.exists();
    }

    private boolean hasAlreadyBeenImported(Resource resource, Vector<Object> vector) {
        FileProvider fileProvider = (FileProvider) resource.as(FileProvider.class);
        File file = fileProvider != null ? fileProvider.getFile() : null;
        URLProvider uRLProvider = (URLProvider) resource.as(URLProvider.class);
        URL url = uRLProvider != null ? uRLProvider.getURL() : null;
        Iterator<Object> it = vector.iterator();
        while (it.hasNext()) {
            if (isOneOf(it.next(), resource, file, url)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOneOf(Object obj, Resource resource, File file, URL url) {
        URLProvider uRLProvider;
        FileProvider fileProvider;
        if (obj.equals(resource) || obj.equals(file) || obj.equals(url)) {
            return true;
        }
        if (!(obj instanceof Resource)) {
            return false;
        }
        if (file == null || (fileProvider = (FileProvider) ((Resource) obj).as(FileProvider.class)) == null || !fileProvider.getFile().equals(file)) {
            return (url == null || (uRLProvider = (URLProvider) ((Resource) obj).as(URLProvider.class)) == null || !uRLProvider.getURL().equals(url)) ? false : true;
        }
        return true;
    }

    protected final boolean isInIncludeMode() {
        return "include".equals(getTaskType());
    }

    private static void setProjectHelperProps(String str, String str2, boolean z) {
        ProjectHelper.setCurrentTargetPrefix(str);
        ProjectHelper.setCurrentPrefixSeparator(str2);
        ProjectHelper.setInIncludeMode(z);
    }
}
