package org.apache.tools.ant.helper;

import android.app.Instrumentation;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.util.FileUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: classes3.dex */
public class AntXMLContext {
    private File buildFile;
    private File buildFileParent;
    private URL buildFileParentURL;
    private URL buildFileURL;
    private String currentProjectName;
    private Locator locator;
    private Project project;
    private Vector<Target> targetVector = new Vector<>();
    private Target implicitTarget = new Target();
    private Target currentTarget = null;
    private Vector<RuntimeConfigurable> wStack = new Vector<>();
    private boolean ignoreProjectTag = false;
    private Map<String, List<String>> prefixMapping = new HashMap();
    private Map<String, Target> currentTargets = null;

    public AntXMLContext(Project project) {
        this.project = project;
        this.implicitTarget.setProject(project);
        this.implicitTarget.setName("");
        this.targetVector.addElement(this.implicitTarget);
    }

    public void setBuildFile(File file) {
        this.buildFile = file;
        if (file != null) {
            this.buildFileParent = new File(file.getParent());
            this.implicitTarget.setLocation(new Location(file.getAbsolutePath()));
            try {
                setBuildFile(FileUtils.getFileUtils().getFileURL(file));
                return;
            } catch (MalformedURLException e) {
                throw new BuildException(e);
            }
        }
        this.buildFileParent = null;
    }

    public void setBuildFile(URL url) throws MalformedURLException {
        this.buildFileURL = url;
        this.buildFileParentURL = new URL(url, ".");
        if (this.implicitTarget.getLocation() == null) {
            this.implicitTarget.setLocation(new Location(url.toString()));
        }
    }

    public File getBuildFile() {
        return this.buildFile;
    }

    public File getBuildFileParent() {
        return this.buildFileParent;
    }

    public URL getBuildFileURL() {
        return this.buildFileURL;
    }

    public URL getBuildFileParentURL() {
        return this.buildFileParentURL;
    }

    public Project getProject() {
        return this.project;
    }

    public String getCurrentProjectName() {
        return this.currentProjectName;
    }

    public void setCurrentProjectName(String str) {
        this.currentProjectName = str;
    }

    public RuntimeConfigurable currentWrapper() {
        if (this.wStack.size() < 1) {
            return null;
        }
        Vector<RuntimeConfigurable> vector = this.wStack;
        return vector.elementAt(vector.size() - 1);
    }

    public RuntimeConfigurable parentWrapper() {
        if (this.wStack.size() < 2) {
            return null;
        }
        Vector<RuntimeConfigurable> vector = this.wStack;
        return vector.elementAt(vector.size() - 2);
    }

    public void pushWrapper(RuntimeConfigurable runtimeConfigurable) {
        this.wStack.addElement(runtimeConfigurable);
    }

    public void popWrapper() {
        if (this.wStack.size() > 0) {
            this.wStack.removeElementAt(r0.size() - 1);
        }
    }

    public Vector<RuntimeConfigurable> getWrapperStack() {
        return this.wStack;
    }

    public void addTarget(Target target) {
        this.targetVector.addElement(target);
        this.currentTarget = target;
    }

    public Target getCurrentTarget() {
        return this.currentTarget;
    }

    public Target getImplicitTarget() {
        return this.implicitTarget;
    }

    public void setCurrentTarget(Target target) {
        this.currentTarget = target;
    }

    public void setImplicitTarget(Target target) {
        this.implicitTarget = target;
    }

    public Vector<Target> getTargets() {
        return this.targetVector;
    }

    public void configureId(Object obj, Attributes attributes) {
        String value = attributes.getValue(Instrumentation.REPORT_KEY_IDENTIFIER);
        if (value != null) {
            this.project.addIdReference(value, obj);
        }
    }

    public Locator getLocator() {
        return this.locator;
    }

    public void setLocator(Locator locator) {
        this.locator = locator;
    }

    public boolean isIgnoringProjectTag() {
        return this.ignoreProjectTag;
    }

    public void setIgnoreProjectTag(boolean z) {
        this.ignoreProjectTag = z;
    }

    public void startPrefixMapping(String str, String str2) {
        List<String> arrayList = this.prefixMapping.get(str);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.prefixMapping.put(str, arrayList);
        }
        arrayList.add(str2);
    }

    public void endPrefixMapping(String str) {
        List<String> list = this.prefixMapping.get(str);
        if (list == null || list.size() == 0) {
            return;
        }
        list.remove(list.size() - 1);
    }

    public String getPrefixMapping(String str) {
        List<String> list = this.prefixMapping.get(str);
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    public Map<String, Target> getCurrentTargets() {
        return this.currentTargets;
    }

    public void setCurrentTargets(Map<String, Target> map) {
        this.currentTargets = map;
    }
}
