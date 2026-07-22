package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/* JADX INFO: loaded from: classes3.dex */
public class ExtendSelector extends BaseSelector {
    private String classname = null;
    private FileSelector dynselector = null;
    private Vector<Parameter> paramVec = new Vector<>();
    private Path classpath = null;

    public void setClassname(String str) {
        this.classname = str;
    }

    public void selectorCreate() {
        Class<?> cls;
        String str = this.classname;
        if (str != null && str.length() > 0) {
            try {
                if (this.classpath == null) {
                    cls = Class.forName(this.classname);
                } else {
                    cls = Class.forName(this.classname, true, getProject().createClassLoader(this.classpath));
                }
                this.dynselector = (FileSelector) cls.asSubclass(FileSelector.class).newInstance();
                Project project = getProject();
                if (project != null) {
                    project.setProjectReference(this.dynselector);
                    return;
                }
                return;
            } catch (ClassNotFoundException unused) {
                setError("Selector " + this.classname + " not initialized, no such class");
                return;
            } catch (IllegalAccessException unused2) {
                setError("Selector " + this.classname + " not initialized, class not accessible");
                return;
            } catch (InstantiationException unused3) {
                setError("Selector " + this.classname + " not initialized, could not create class");
                return;
            }
        }
        setError("There is no classname specified");
    }

    public void addParam(Parameter parameter) {
        this.paramVec.addElement(parameter);
    }

    public final void setClasspath(Path path) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        Path path2 = this.classpath;
        if (path2 == null) {
            this.classpath = path;
        } else {
            path2.append(path);
        }
    }

    public final Path createClasspath() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        if (this.classpath == null) {
            this.classpath = new Path(getProject());
        }
        return this.classpath.createPath();
    }

    public final Path getClasspath() {
        return this.classpath;
    }

    public void setClasspathref(Reference reference) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        createClasspath().setRefid(reference);
    }

    @Override // org.apache.tools.ant.types.selectors.BaseSelector
    public void verifySettings() {
        if (this.dynselector == null) {
            selectorCreate();
        }
        String str = this.classname;
        if (str == null || str.length() < 1) {
            setError("The classname attribute is required");
            return;
        }
        FileSelector fileSelector = this.dynselector;
        if (fileSelector == null) {
            setError("Internal Error: The custom selector was not created");
        } else {
            if ((fileSelector instanceof ExtendFileSelector) || this.paramVec.size() <= 0) {
                return;
            }
            setError("Cannot set parameters on custom selector that does not implement ExtendFileSelector");
        }
    }

    @Override // org.apache.tools.ant.types.selectors.BaseSelector, org.apache.tools.ant.types.selectors.FileSelector
    public boolean isSelected(File file, String str, File file2) throws BuildException {
        validate();
        if (this.paramVec.size() > 0 && (this.dynselector instanceof ExtendFileSelector)) {
            Parameter[] parameterArr = new Parameter[this.paramVec.size()];
            this.paramVec.copyInto(parameterArr);
            ((ExtendFileSelector) this.dynselector).setParameters(parameterArr);
        }
        return this.dynselector.isSelected(file, str, file2);
    }
}
