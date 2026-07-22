package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.Comparison;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.ResourceCollection;

/* JADX INFO: loaded from: classes3.dex */
public class ResourceCount extends Task implements Condition {
    private static final String COUNT_REQUIRED = "Use of the ResourceCount condition requires that the count attribute be set.";
    private static final String ONE_NESTED_MESSAGE = "ResourceCount can count resources from exactly one nested ResourceCollection.";
    private Integer count;
    private String property;
    private ResourceCollection rc;
    private Comparison when = Comparison.EQUAL;

    public void add(ResourceCollection resourceCollection) {
        if (this.rc != null) {
            throw new BuildException(ONE_NESTED_MESSAGE);
        }
        this.rc = resourceCollection;
    }

    public void setRefid(Reference reference) {
        Object referencedObject = reference.getReferencedObject();
        if (!(referencedObject instanceof ResourceCollection)) {
            throw new BuildException(reference.getRefId() + " doesn't denote a ResourceCollection");
        }
        add((ResourceCollection) referencedObject);
    }

    @Override // org.apache.tools.ant.Task
    public void execute() {
        if (this.rc == null) {
            throw new BuildException(ONE_NESTED_MESSAGE);
        }
        if (this.property == null) {
            log("resource count = " + this.rc.size());
        } else {
            getProject().setNewProperty(this.property, Integer.toString(this.rc.size()));
        }
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() {
        if (this.rc == null) {
            throw new BuildException(ONE_NESTED_MESSAGE);
        }
        if (this.count == null) {
            throw new BuildException(COUNT_REQUIRED);
        }
        return this.when.evaluate(new Integer(this.rc.size()).compareTo(this.count));
    }

    public void setCount(int i) {
        this.count = new Integer(i);
    }

    public void setWhen(Comparison comparison) {
        this.when = comparison;
    }

    public void setProperty(String str) {
        this.property = str;
    }
}
