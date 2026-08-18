package org.apache.tools.ant.util;

import java.io.File;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.ClasspathUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ScriptRunnerHelper {
    private String language;
    private ProjectComponent projectComponent;
    private File srcFile;
    private String text;
    private ClasspathUtils.Delegate cpDelegate = null;
    private String manager = "auto";
    private boolean setBeans = true;
    private ClassLoader scriptLoader = null;
    private Union resources = new Union();

    public void setProjectComponent(ProjectComponent projectComponent) {
        this.projectComponent = projectComponent;
    }

    public ScriptRunnerBase getScriptRunner() throws Throwable {
        ScriptRunnerBase runner = getRunner();
        File file = this.srcFile;
        if (file != null) {
            runner.setSrc(file);
        }
        String str = this.text;
        if (str != null) {
            runner.addText(str);
        }
        Union union = this.resources;
        if (union != null) {
            runner.loadResources(union);
        }
        if (this.setBeans) {
            runner.bindToComponent(this.projectComponent);
        } else {
            runner.bindToComponentMinimum(this.projectComponent);
        }
        return runner;
    }

    public Path createClasspath() {
        return getClassPathDelegate().createClasspath();
    }

    public void setClasspath(Path path) {
        getClassPathDelegate().setClasspath(path);
    }

    public void setClasspathRef(Reference reference) {
        getClassPathDelegate().setClasspathref(reference);
    }

    public void setSrc(File file) {
        this.srcFile = file;
    }

    public void addText(String str) {
        this.text = str;
    }

    public void setManager(String str) {
        this.manager = str;
    }

    public void setLanguage(String str) {
        this.language = str;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setSetBeans(boolean z) {
        this.setBeans = z;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.scriptLoader = classLoader;
    }

    private synchronized ClassLoader generateClassLoader() {
        ClassLoader classLoader = this.scriptLoader;
        if (classLoader != null) {
            return classLoader;
        }
        ClasspathUtils.Delegate delegate = this.cpDelegate;
        if (delegate == null) {
            ClassLoader classLoader2 = getClass().getClassLoader();
            this.scriptLoader = classLoader2;
            return classLoader2;
        }
        ClassLoader classLoader3 = delegate.getClassLoader();
        this.scriptLoader = classLoader3;
        return classLoader3;
    }

    private ClasspathUtils.Delegate getClassPathDelegate() {
        if (this.cpDelegate == null) {
            this.cpDelegate = ClasspathUtils.getDelegate(this.projectComponent);
        }
        return this.cpDelegate;
    }

    private ScriptRunnerBase getRunner() {
        return new ScriptRunnerCreator(this.projectComponent.getProject()).createRunner(this.manager, this.language, generateClassLoader());
    }

    public void add(ResourceCollection resourceCollection) {
        this.resources.add(resourceCollection);
    }
}
