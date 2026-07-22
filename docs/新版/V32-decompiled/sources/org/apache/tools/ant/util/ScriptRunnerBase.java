package org.apache.tools.ant.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

/* JADX INFO: loaded from: classes3.dex */
public abstract class ScriptRunnerBase {
    private String language;
    private Project project;
    private ClassLoader scriptLoader;
    private boolean keepEngine = false;
    private String script = "";
    private Map beans = new HashMap();

    public abstract Object evaluateScript(String str);

    public abstract void executeScript(String str);

    public abstract String getManagerName();

    public abstract boolean supportsLanguage();

    public void addBeans(Map map) {
        for (String str : map.keySet()) {
            try {
                addBean(str, map.get(str));
            } catch (BuildException unused) {
            }
        }
    }

    public void addBean(String str, Object obj) {
        boolean zIsJavaIdentifierPart = false;
        if (str.length() > 0 && Character.isJavaIdentifierStart(str.charAt(0))) {
            zIsJavaIdentifierPart = true;
        }
        for (int i = 1; zIsJavaIdentifierPart && i < str.length(); i++) {
            zIsJavaIdentifierPart = Character.isJavaIdentifierPart(str.charAt(i));
        }
        if (zIsJavaIdentifierPart) {
            this.beans.put(str, obj);
        }
    }

    protected Map getBeans() {
        return this.beans;
    }

    public void setLanguage(String str) {
        this.language = str;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setScriptClassLoader(ClassLoader classLoader) {
        this.scriptLoader = classLoader;
    }

    protected ClassLoader getScriptClassLoader() {
        return this.scriptLoader;
    }

    public void setKeepEngine(boolean z) {
        this.keepEngine = z;
    }

    public boolean getKeepEngine() {
        return this.keepEngine;
    }

    public void setSrc(File file) throws Throwable {
        String path = file.getPath();
        if (!file.exists()) {
            throw new BuildException("file " + path + " not found.");
        }
        try {
            readSource(new FileReader(file), path);
        } catch (FileNotFoundException unused) {
            throw new BuildException("file " + path + " not found.");
        }
    }

    private void readSource(Reader reader, String str) throws Throwable {
        BufferedReader bufferedReader;
        BufferedReader bufferedReader2 = null;
        try {
            try {
                bufferedReader = new BufferedReader(reader);
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException e) {
            e = e;
        }
        try {
            this.script += FileUtils.safeReadFully(bufferedReader);
            FileUtils.close(bufferedReader);
        } catch (IOException e2) {
            e = e2;
            throw new BuildException("Failed to read " + str, e);
        } catch (Throwable th2) {
            th = th2;
            bufferedReader2 = bufferedReader;
            FileUtils.close(bufferedReader2);
            throw th;
        }
    }

    public void loadResource(Resource resource) throws Throwable {
        String longString = resource.toLongString();
        try {
            readSource(new InputStreamReader(resource.getInputStream()), longString);
        } catch (IOException e) {
            throw new BuildException("Failed to open " + longString, e);
        } catch (UnsupportedOperationException e2) {
            throw new BuildException("Failed to open " + longString + " -it is not readable", e2);
        }
    }

    public void loadResources(ResourceCollection resourceCollection) throws Throwable {
        Iterator<Resource> it = resourceCollection.iterator();
        while (it.hasNext()) {
            loadResource(it.next());
        }
    }

    public void addText(String str) {
        this.script += str;
    }

    public String getScript() {
        return this.script;
    }

    public void clearScript() {
        this.script = "";
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return this.project;
    }

    public void bindToComponent(ProjectComponent projectComponent) {
        Project project = projectComponent.getProject();
        this.project = project;
        addBeans(project.getProperties());
        addBeans(this.project.getUserProperties());
        addBeans(this.project.getCopyOfTargets());
        addBeans(this.project.getCopyOfReferences());
        addBean("project", this.project);
        addBean("self", projectComponent);
    }

    public void bindToComponentMinimum(ProjectComponent projectComponent) {
        Project project = projectComponent.getProject();
        this.project = project;
        addBean("project", project);
        addBean("self", projectComponent);
    }

    protected void checkLanguage() {
        if (this.language == null) {
            throw new BuildException("script language must be specified");
        }
    }

    protected ClassLoader replaceContextLoader() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (getScriptClassLoader() == null) {
            setScriptClassLoader(getClass().getClassLoader());
        }
        Thread.currentThread().setContextClassLoader(getScriptClassLoader());
        return contextClassLoader;
    }

    protected void restoreContextLoader(ClassLoader classLoader) {
        Thread.currentThread().setContextClassLoader(classLoader);
    }
}
