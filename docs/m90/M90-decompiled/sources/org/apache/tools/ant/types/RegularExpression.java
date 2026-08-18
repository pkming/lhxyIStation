package org.apache.tools.ant.types;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpFactory;

/* JADX INFO: loaded from: classes3.dex */
public class RegularExpression extends DataType {
    public static final String DATA_TYPE_NAME = "regexp";
    private static final RegexpFactory FACTORY = new RegexpFactory();
    private String myPattern;
    private boolean alreadyInit = false;
    private Regexp regexp = null;
    private boolean setPatternPending = false;

    private void init(Project project) {
        if (this.alreadyInit) {
            return;
        }
        this.regexp = FACTORY.newRegexp(project);
        this.alreadyInit = true;
    }

    private void setPattern() {
        if (this.setPatternPending) {
            this.regexp.setPattern(this.myPattern);
            this.setPatternPending = false;
        }
    }

    public void setPattern(String str) {
        Regexp regexp = this.regexp;
        if (regexp == null) {
            this.myPattern = str;
            this.setPatternPending = true;
        } else {
            regexp.setPattern(str);
        }
    }

    public String getPattern(Project project) {
        init(project);
        if (isReference()) {
            return getRef(project).getPattern(project);
        }
        setPattern();
        return this.regexp.getPattern();
    }

    public Regexp getRegexp(Project project) {
        init(project);
        if (isReference()) {
            return getRef(project).getRegexp(project);
        }
        setPattern();
        return this.regexp;
    }

    public RegularExpression getRef(Project project) {
        return (RegularExpression) getCheckedRef(project);
    }
}
