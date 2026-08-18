package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ExitStatusException;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;

/* JADX INFO: loaded from: classes3.dex */
public class Exit extends Task {
    private Object ifCondition;
    private String message;
    private NestedCondition nestedCondition;
    private Integer status;
    private Object unlessCondition;

    private static class NestedCondition extends ConditionBase implements Condition {
        private NestedCondition() {
        }

        @Override // org.apache.tools.ant.taskdefs.condition.Condition
        public boolean eval() {
            if (countConditions() != 1) {
                throw new BuildException("A single nested condition is required.");
            }
            return ((Condition) getConditions().nextElement()).eval();
        }
    }

    public void setMessage(String str) {
        this.message = str;
    }

    public void setIf(Object obj) {
        this.ifCondition = obj;
    }

    public void setIf(String str) {
        setIf((Object) str);
    }

    public void setUnless(Object obj) {
        this.unlessCondition = obj;
    }

    public void setUnless(String str) {
        setUnless((Object) str);
    }

    public void setStatus(int i) {
        this.status = new Integer(i);
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (nestedConditionPresent() ? testNestedCondition() : testIfCondition() && testUnlessCondition()) {
            String strTrim = null;
            String str = this.message;
            if (str != null && str.trim().length() > 0) {
                strTrim = this.message.trim();
            } else {
                Object obj = this.ifCondition;
                if (obj != null && !"".equals(obj) && testIfCondition()) {
                    strTrim = "if=" + this.ifCondition;
                }
                Object obj2 = this.unlessCondition;
                if (obj2 != null && !"".equals(obj2) && testUnlessCondition()) {
                    strTrim = (strTrim != null ? strTrim + " and " : "") + "unless=" + this.unlessCondition;
                }
                if (nestedConditionPresent()) {
                    strTrim = "condition satisfied";
                } else if (strTrim == null) {
                    strTrim = "No message";
                }
            }
            log("failing due to " + strTrim, 4);
            if (this.status != null) {
                throw new ExitStatusException(strTrim, this.status.intValue());
            }
        }
    }

    public void addText(String str) {
        if (this.message == null) {
            this.message = "";
        }
        this.message += getProject().replaceProperties(str);
    }

    public ConditionBase createCondition() {
        if (this.nestedCondition != null) {
            throw new BuildException("Only one nested condition is allowed.");
        }
        NestedCondition nestedCondition = new NestedCondition();
        this.nestedCondition = nestedCondition;
        return nestedCondition;
    }

    private boolean testIfCondition() {
        return PropertyHelper.getPropertyHelper(getProject()).testIfCondition(this.ifCondition);
    }

    private boolean testUnlessCondition() {
        return PropertyHelper.getPropertyHelper(getProject()).testUnlessCondition(this.unlessCondition);
    }

    private boolean testNestedCondition() {
        boolean zNestedConditionPresent = nestedConditionPresent();
        if ((!zNestedConditionPresent || this.ifCondition == null) && this.unlessCondition == null) {
            return zNestedConditionPresent && this.nestedCondition.eval();
        }
        throw new BuildException("Nested conditions not permitted in conjunction with if/unless attributes");
    }

    private boolean nestedConditionPresent() {
        return this.nestedCondition != null;
    }
}
