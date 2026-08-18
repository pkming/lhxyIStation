package org.apache.tools.ant.types.optional;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;

/* JADX INFO: loaded from: classes3.dex */
public class ScriptCondition extends AbstractScriptComponent implements Condition {
    private boolean value = false;

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() throws BuildException {
        initScriptRunner();
        executeScript("ant_condition");
        return getValue();
    }

    public boolean getValue() {
        return this.value;
    }

    public void setValue(boolean z) {
        this.value = z;
    }
}
