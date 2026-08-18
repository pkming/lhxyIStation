package org.apache.tools.ant.taskdefs.condition;

import java.util.Enumeration;
import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public class Xor extends ConditionBase implements Condition {
    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() throws BuildException {
        Enumeration conditions = getConditions();
        boolean zEval = false;
        while (conditions.hasMoreElements()) {
            zEval ^= ((Condition) conditions.nextElement()).eval();
        }
        return zEval;
    }
}
