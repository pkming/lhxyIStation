package org.apache.tools.ant.types;

import org.apache.tools.ant.Project;

/* JADX INFO: loaded from: classes3.dex */
public class Substitution extends DataType {
    public static final String DATA_TYPE_NAME = "substitution";
    private String expression = null;

    public void setExpression(String str) {
        this.expression = str;
    }

    public String getExpression(Project project) {
        if (isReference()) {
            return getRef(project).getExpression(project);
        }
        return this.expression;
    }

    public Substitution getRef(Project project) {
        return (Substitution) getCheckedRef(project);
    }
}
