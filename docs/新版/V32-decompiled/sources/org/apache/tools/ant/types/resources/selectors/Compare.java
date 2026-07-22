package org.apache.tools.ant.types.resources.selectors;

import java.util.Iterator;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Comparison;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Quantifier;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.types.resources.comparators.DelegatedResourceComparator;
import org.apache.tools.ant.types.resources.comparators.ResourceComparator;

/* JADX INFO: loaded from: classes3.dex */
public class Compare extends DataType implements ResourceSelector {
    private static final String ONE_CONTROL_MESSAGE = " the <control> element should be specified exactly once.";
    private Union control;
    private DelegatedResourceComparator comp = new DelegatedResourceComparator();
    private Quantifier against = Quantifier.ALL;
    private Comparison when = Comparison.EQUAL;

    public synchronized void add(ResourceComparator resourceComparator) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        this.comp.add(resourceComparator);
        setChecked(false);
    }

    public synchronized void setAgainst(Quantifier quantifier) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.against = quantifier;
    }

    public synchronized void setWhen(Comparison comparison) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.when = comparison;
    }

    public synchronized ResourceCollection createControl() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        if (this.control != null) {
            throw oneControl();
        }
        this.control = new Union();
        setChecked(false);
        return this.control;
    }

    @Override // org.apache.tools.ant.types.resources.selectors.ResourceSelector
    public synchronized boolean isSelected(Resource resource) {
        if (isReference()) {
            return ((ResourceSelector) getCheckedRef()).isSelected(resource);
        }
        if (this.control == null) {
            throw oneControl();
        }
        dieOnCircularReference();
        Iterator<Resource> it = this.control.iterator();
        int i = 0;
        int i2 = 0;
        while (it.hasNext()) {
            if (this.when.evaluate(this.comp.compare(resource, it.next()))) {
                i++;
            } else {
                i2++;
            }
        }
        return this.against.evaluate(i, i2);
    }

    @Override // org.apache.tools.ant.types.DataType
    protected synchronized void dieOnCircularReference(Stack<Object> stack, Project project) throws BuildException {
        if (isChecked()) {
            return;
        }
        if (isReference()) {
            super.dieOnCircularReference(stack, project);
        } else {
            Union union = this.control;
            if (union != null) {
                DataType.pushAndInvokeCircularReferenceCheck(union, stack, project);
            }
            DataType.pushAndInvokeCircularReferenceCheck(this.comp, stack, project);
            setChecked(true);
        }
    }

    private BuildException oneControl() {
        return new BuildException(super.toString() + ONE_CONTROL_MESSAGE);
    }
}
