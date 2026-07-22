package org.apache.tools.ant.taskdefs;

import android.app.Instrumentation;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TypeAdapter;

/* JADX INFO: loaded from: classes3.dex */
public class AugmentReference extends Task implements TypeAdapter {
    private String id;

    @Override // org.apache.tools.ant.TypeAdapter
    public void checkProxyClass(Class<?> cls) {
    }

    @Override // org.apache.tools.ant.TypeAdapter
    public synchronized Object getProxy() {
        Object reference;
        if (getProject() == null) {
            throw new IllegalStateException(getTaskName() + "Project owner unset");
        }
        hijackId();
        if (getProject().hasReference(this.id)) {
            reference = getProject().getReference(this.id);
            log("project reference " + this.id + "=" + String.valueOf(reference), 4);
        } else {
            throw new IllegalStateException("Unknown reference \"" + this.id + "\"");
        }
        return reference;
    }

    @Override // org.apache.tools.ant.TypeAdapter
    public void setProxy(Object obj) {
        throw new UnsupportedOperationException();
    }

    private synchronized void hijackId() {
        if (this.id == null) {
            RuntimeConfigurable wrapper = getWrapper();
            String id = wrapper.getId();
            this.id = id;
            if (id == null) {
                throw new IllegalStateException(getTaskName() + " attribute 'id' unset");
            }
            wrapper.setAttribute(Instrumentation.REPORT_KEY_IDENTIFIER, (String) null);
            wrapper.removeAttribute(Instrumentation.REPORT_KEY_IDENTIFIER);
            wrapper.setElementTag("augmented reference \"" + this.id + "\"");
        }
    }

    @Override // org.apache.tools.ant.Task
    public void execute() {
        restoreWrapperId();
    }

    private synchronized void restoreWrapperId() {
        if (this.id != null) {
            log("restoring augment wrapper " + this.id, 4);
            RuntimeConfigurable wrapper = getWrapper();
            wrapper.setAttribute(Instrumentation.REPORT_KEY_IDENTIFIER, this.id);
            wrapper.setElementTag(getTaskName());
            this.id = null;
        }
    }
}
