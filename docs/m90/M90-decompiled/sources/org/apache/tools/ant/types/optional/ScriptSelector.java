package org.apache.tools.ant.types.optional;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.selectors.BaseSelector;
import org.apache.tools.ant.util.ScriptRunnerBase;
import org.apache.tools.ant.util.ScriptRunnerHelper;

/* JADX INFO: loaded from: classes3.dex */
public class ScriptSelector extends BaseSelector {
    private File basedir;
    private File file;
    private String filename;
    private ScriptRunnerHelper helper = new ScriptRunnerHelper();
    private ScriptRunnerBase runner;
    private boolean selected;

    @Override // org.apache.tools.ant.ProjectComponent
    public void setProject(Project project) {
        super.setProject(project);
        this.helper.setProjectComponent(this);
    }

    public void setManager(String str) {
        this.helper.setManager(str);
    }

    public void setLanguage(String str) {
        this.helper.setLanguage(str);
    }

    private void init() throws BuildException {
        if (this.runner != null) {
            return;
        }
        this.runner = this.helper.getScriptRunner();
    }

    public void setSrc(File file) {
        this.helper.setSrc(file);
    }

    public void addText(String str) {
        this.helper.addText(str);
    }

    public void setClasspath(Path path) {
        this.helper.setClasspath(path);
    }

    public Path createClasspath() {
        return this.helper.createClasspath();
    }

    public void setClasspathRef(Reference reference) {
        this.helper.setClasspathRef(reference);
    }

    public void setSetBeans(boolean z) {
        this.helper.setSetBeans(z);
    }

    @Override // org.apache.tools.ant.types.selectors.BaseSelector, org.apache.tools.ant.types.selectors.FileSelector
    public boolean isSelected(File file, String str, File file2) {
        init();
        setSelected(true);
        this.file = file2;
        this.basedir = file;
        this.filename = str;
        this.runner.addBean(MagicNames.PROJECT_BASEDIR, file);
        this.runner.addBean("filename", str);
        this.runner.addBean("file", file2);
        this.runner.executeScript("ant_selector");
        return isSelected();
    }

    public File getBasedir() {
        return this.basedir;
    }

    public String getFilename() {
        return this.filename;
    }

    public File getFile() {
        return this.file;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean z) {
        this.selected = z;
    }
}
