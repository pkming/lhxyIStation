package org.apache.tools.ant.types;

import java.util.Stack;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/* JADX INFO: loaded from: classes3.dex */
public final class AntFilterReader extends DataType implements Cloneable {
    private String className;
    private Path classpath;
    private final Vector<Parameter> parameters = new Vector<>();

    public void setClassName(String str) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.className = str;
    }

    public String getClassName() {
        if (isReference()) {
            return ((AntFilterReader) getCheckedRef()).getClassName();
        }
        dieOnCircularReference();
        return this.className;
    }

    public void addParam(Parameter parameter) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        this.parameters.addElement(parameter);
    }

    public void setClasspath(Path path) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        Path path2 = this.classpath;
        if (path2 == null) {
            this.classpath = path;
        } else {
            path2.append(path);
        }
        setChecked(false);
    }

    public Path createClasspath() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        if (this.classpath == null) {
            this.classpath = new Path(getProject());
        }
        setChecked(false);
        return this.classpath.createPath();
    }

    public Path getClasspath() {
        if (isReference()) {
            ((AntFilterReader) getCheckedRef()).getClasspath();
        }
        dieOnCircularReference();
        return this.classpath;
    }

    public void setClasspathRef(Reference reference) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        createClasspath().setRefid(reference);
    }

    public Parameter[] getParams() {
        if (isReference()) {
            ((AntFilterReader) getCheckedRef()).getParams();
        }
        dieOnCircularReference();
        Parameter[] parameterArr = new Parameter[this.parameters.size()];
        this.parameters.copyInto(parameterArr);
        return parameterArr;
    }

    @Override // org.apache.tools.ant.types.DataType
    public void setRefid(Reference reference) throws BuildException {
        if (!this.parameters.isEmpty() || this.className != null || this.classpath != null) {
            throw tooManyAttributes();
        }
        super.setRefid(reference);
    }

    @Override // org.apache.tools.ant.types.DataType
    protected synchronized void dieOnCircularReference(Stack<Object> stack, Project project) throws BuildException {
        if (isChecked()) {
            return;
        }
        if (isReference()) {
            super.dieOnCircularReference(stack, project);
        } else {
            Path path = this.classpath;
            if (path != null) {
                pushAndInvokeCircularReferenceCheck(path, stack, project);
            }
            setChecked(true);
        }
    }
}
