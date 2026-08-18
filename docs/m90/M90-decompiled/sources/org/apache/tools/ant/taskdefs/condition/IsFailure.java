package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.taskdefs.Execute;

/* JADX INFO: loaded from: classes3.dex */
public class IsFailure implements Condition {
    private int code;

    public void setCode(int i) {
        this.code = i;
    }

    public int getCode() {
        return this.code;
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() {
        return Execute.isFailure(this.code);
    }
}
