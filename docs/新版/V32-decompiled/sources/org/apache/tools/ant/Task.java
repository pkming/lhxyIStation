package org.apache.tools.ant;

import java.io.IOException;
import java.util.Enumeration;
import org.apache.tools.ant.dispatch.DispatchUtils;

/* JADX INFO: loaded from: classes3.dex */
public abstract class Task extends ProjectComponent {
    private boolean invalid;
    private UnknownElement replacement;
    protected Target target;
    protected String taskName;
    protected String taskType;
    protected RuntimeConfigurable wrapper;

    public void execute() throws BuildException {
    }

    public void init() throws BuildException {
    }

    public void setOwningTarget(Target target) {
        this.target = target;
    }

    public Target getOwningTarget() {
        return this.target;
    }

    public void setTaskName(String str) {
        this.taskName = str;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void setTaskType(String str) {
        this.taskType = str;
    }

    public RuntimeConfigurable getRuntimeConfigurableWrapper() {
        if (this.wrapper == null) {
            this.wrapper = new RuntimeConfigurable(this, getTaskName());
        }
        return this.wrapper;
    }

    public void setRuntimeConfigurableWrapper(RuntimeConfigurable runtimeConfigurable) {
        this.wrapper = runtimeConfigurable;
    }

    public void maybeConfigure() throws BuildException {
        if (!this.invalid) {
            RuntimeConfigurable runtimeConfigurable = this.wrapper;
            if (runtimeConfigurable != null) {
                runtimeConfigurable.maybeConfigure(getProject());
                return;
            }
            return;
        }
        getReplacement();
    }

    public void reconfigure() {
        RuntimeConfigurable runtimeConfigurable = this.wrapper;
        if (runtimeConfigurable != null) {
            runtimeConfigurable.reconfigure(getProject());
        }
    }

    protected void handleOutput(String str) {
        log(str, 2);
    }

    protected void handleFlush(String str) {
        handleOutput(str);
    }

    protected int handleInput(byte[] bArr, int i, int i2) throws IOException {
        return getProject().defaultInput(bArr, i, i2);
    }

    protected void handleErrorOutput(String str) {
        log(str, 1);
    }

    protected void handleErrorFlush(String str) {
        handleErrorOutput(str);
    }

    @Override // org.apache.tools.ant.ProjectComponent
    public void log(String str) {
        log(str, 2);
    }

    @Override // org.apache.tools.ant.ProjectComponent
    public void log(String str, int i) {
        if (getProject() != null) {
            getProject().log(this, str, i);
        } else {
            super.log(str, i);
        }
    }

    public void log(Throwable th, int i) {
        if (th != null) {
            log(th.getMessage(), th, i);
        }
    }

    public void log(String str, Throwable th, int i) {
        if (getProject() != null) {
            getProject().log(this, str, th, i);
        } else {
            super.log(str, i);
        }
    }

    public final void perform() throws Throwable {
        Throwable th;
        if (!this.invalid) {
            getProject().fireTaskStarted(this);
            BuildException buildException = null;
            try {
                try {
                    try {
                        maybeConfigure();
                        DispatchUtils.execute(this);
                        getProject().fireTaskFinished(this, null);
                        return;
                    } catch (Throwable th2) {
                        th = th2;
                        getProject().fireTaskFinished(this, buildException);
                        throw th;
                    }
                } catch (BuildException e) {
                    if (e.getLocation() == Location.UNKNOWN_LOCATION) {
                        e.setLocation(getLocation());
                    }
                    try {
                        throw e;
                    } catch (Throwable th3) {
                        th = th3;
                        buildException = e;
                        getProject().fireTaskFinished(this, buildException);
                        throw th;
                    }
                }
            } catch (Error e2) {
                throw e2;
            } catch (Exception e3) {
                BuildException buildException2 = new BuildException(e3);
                buildException2.setLocation(getLocation());
                throw buildException2;
            }
        }
        getReplacement().getTask().perform();
    }

    final void markInvalid() {
        this.invalid = true;
    }

    protected final boolean isInvalid() {
        return this.invalid;
    }

    private UnknownElement getReplacement() {
        if (this.replacement == null) {
            UnknownElement unknownElement = new UnknownElement(this.taskType);
            this.replacement = unknownElement;
            unknownElement.setProject(getProject());
            this.replacement.setTaskType(this.taskType);
            this.replacement.setTaskName(this.taskName);
            this.replacement.setLocation(getLocation());
            this.replacement.setOwningTarget(this.target);
            this.replacement.setRuntimeConfigurableWrapper(this.wrapper);
            this.wrapper.setProxy(this.replacement);
            replaceChildren(this.wrapper, this.replacement);
            this.target.replaceChild(this, this.replacement);
            this.replacement.maybeConfigure();
        }
        return this.replacement;
    }

    private void replaceChildren(RuntimeConfigurable runtimeConfigurable, UnknownElement unknownElement) {
        Enumeration<RuntimeConfigurable> children = runtimeConfigurable.getChildren();
        while (children.hasMoreElements()) {
            RuntimeConfigurable runtimeConfigurableNextElement = children.nextElement();
            UnknownElement unknownElement2 = new UnknownElement(runtimeConfigurableNextElement.getElementTag());
            unknownElement.addChild(unknownElement2);
            unknownElement2.setProject(getProject());
            unknownElement2.setRuntimeConfigurableWrapper(runtimeConfigurableNextElement);
            runtimeConfigurableNextElement.setProxy(unknownElement2);
            replaceChildren(runtimeConfigurableNextElement, unknownElement2);
        }
    }

    public String getTaskType() {
        return this.taskType;
    }

    protected RuntimeConfigurable getWrapper() {
        return this.wrapper;
    }

    public final void bindToOwner(Task task) {
        setProject(task.getProject());
        setOwningTarget(task.getOwningTarget());
        setTaskName(task.getTaskName());
        setDescription(task.getDescription());
        setLocation(task.getLocation());
        setTaskType(task.getTaskType());
    }
}
