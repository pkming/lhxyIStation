package org.apache.tools.ant.types;

import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.util.IdentityStack;

/* JADX INFO: loaded from: classes3.dex */
public abstract class DataType extends ProjectComponent implements Cloneable {
    protected boolean checked = true;
    protected Reference ref;

    public boolean isReference() {
        return this.ref != null;
    }

    public void setRefid(Reference reference) {
        this.ref = reference;
        this.checked = false;
    }

    protected String getDataTypeName() {
        return ComponentHelper.getElementName(getProject(), this, true);
    }

    protected void dieOnCircularReference() {
        dieOnCircularReference(getProject());
    }

    protected void dieOnCircularReference(Project project) {
        if (this.checked || !isReference()) {
            return;
        }
        dieOnCircularReference(new IdentityStack(this), project);
    }

    protected void dieOnCircularReference(Stack<Object> stack, Project project) throws BuildException {
        if (this.checked || !isReference()) {
            return;
        }
        Object referencedObject = this.ref.getReferencedObject(project);
        if (referencedObject instanceof DataType) {
            IdentityStack identityStack = IdentityStack.getInstance(stack);
            if (identityStack.contains(referencedObject)) {
                throw circularReference();
            }
            identityStack.push(referencedObject);
            ((DataType) referencedObject).dieOnCircularReference(identityStack, project);
            identityStack.pop();
        }
        this.checked = true;
    }

    public static void invokeCircularReferenceCheck(DataType dataType, Stack<Object> stack, Project project) {
        dataType.dieOnCircularReference(stack, project);
    }

    public static void pushAndInvokeCircularReferenceCheck(DataType dataType, Stack<Object> stack, Project project) {
        stack.push(dataType);
        dataType.dieOnCircularReference(stack, project);
        stack.pop();
    }

    protected Object getCheckedRef() {
        return getCheckedRef(getProject());
    }

    protected Object getCheckedRef(Project project) {
        return getCheckedRef(getClass(), getDataTypeName(), project);
    }

    protected <T> T getCheckedRef(Class<T> cls, String str) {
        return (T) getCheckedRef(cls, str, getProject());
    }

    protected <T> T getCheckedRef(Class<T> cls, String str, Project project) {
        if (project == null) {
            throw new BuildException("No Project specified");
        }
        dieOnCircularReference(project);
        T t = (T) this.ref.getReferencedObject(project);
        if (cls.isAssignableFrom(t.getClass())) {
            return t;
        }
        log("Class " + t.getClass() + " is not a subclass of " + cls, 3);
        throw new BuildException(this.ref.getRefId() + " doesn't denote a " + str);
    }

    protected BuildException tooManyAttributes() {
        return new BuildException("You must not specify more than one attribute when using refid");
    }

    protected BuildException noChildrenAllowed() {
        return new BuildException("You must not specify nested elements when using refid");
    }

    protected BuildException circularReference() {
        return new BuildException("This data type contains a circular reference.");
    }

    protected boolean isChecked() {
        return this.checked;
    }

    protected void setChecked(boolean z) {
        this.checked = z;
    }

    public Reference getRefid() {
        return this.ref;
    }

    protected void checkAttributesAllowed() {
        if (isReference()) {
            throw tooManyAttributes();
        }
    }

    protected void checkChildrenAllowed() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
    }

    public String toString() {
        String description = getDescription();
        return description == null ? getDataTypeName() : getDataTypeName() + " " + description;
    }

    @Override // org.apache.tools.ant.ProjectComponent
    public Object clone() throws CloneNotSupportedException {
        DataType dataType = (DataType) super.clone();
        dataType.setDescription(getDescription());
        if (getRefid() != null) {
            dataType.setRefid(getRefid());
        }
        dataType.setChecked(isChecked());
        return dataType;
    }
}
