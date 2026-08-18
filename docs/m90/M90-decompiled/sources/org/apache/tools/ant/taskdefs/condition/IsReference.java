package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Reference;

/* JADX INFO: loaded from: classes3.dex */
public class IsReference extends ProjectComponent implements Condition {
    private Reference ref;
    private String type;

    public void setRefid(Reference reference) {
        this.ref = reference;
    }

    public void setType(String str) {
        this.type = str;
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() throws BuildException {
        Reference reference = this.ref;
        if (reference == null) {
            throw new BuildException("No reference specified for isreference condition");
        }
        String refId = reference.getRefId();
        if (!getProject().hasReference(refId)) {
            return false;
        }
        if (this.type == null) {
            return true;
        }
        Object reference2 = getProject().getReference(refId);
        Class<?> cls = getProject().getDataTypeDefinitions().get(this.type);
        if (cls == null) {
            cls = getProject().getTaskDefinitions().get(this.type);
        }
        if (cls == null) {
            return false;
        }
        return cls.isAssignableFrom(reference2.getClass());
    }
}
