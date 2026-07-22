package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpUtil;

/* JADX INFO: loaded from: classes3.dex */
public class Name implements ResourceSelector {
    private Regexp expression;
    private String pattern;
    private Project project;
    private RegularExpression reg;
    private String regex = null;
    private boolean cs = true;
    private boolean handleDirSep = false;

    public void setProject(Project project) {
        this.project = project;
    }

    public void setName(String str) {
        this.pattern = str;
    }

    public String getName() {
        return this.pattern;
    }

    public void setRegex(String str) {
        this.regex = str;
        this.reg = null;
    }

    public String getRegex() {
        return this.regex;
    }

    public void setCaseSensitive(boolean z) {
        this.cs = z;
    }

    public boolean isCaseSensitive() {
        return this.cs;
    }

    public void setHandleDirSep(boolean z) {
        this.handleDirSep = z;
    }

    public boolean doesHandledirSep() {
        return this.handleDirSep;
    }

    @Override // org.apache.tools.ant.types.resources.selectors.ResourceSelector
    public boolean isSelected(Resource resource) {
        String name = resource.getName();
        if (matches(name)) {
            return true;
        }
        String string = resource.toString();
        if (string.equals(name)) {
            return false;
        }
        return matches(string);
    }

    private boolean matches(String str) {
        String str2 = this.pattern;
        if (str2 != null) {
            return SelectorUtils.match(modify(str2), modify(str), this.cs);
        }
        if (this.reg == null) {
            RegularExpression regularExpression = new RegularExpression();
            this.reg = regularExpression;
            regularExpression.setPattern(this.regex);
            this.expression = this.reg.getRegexp(this.project);
        }
        return this.expression.matches(modify(str), RegexpUtil.asOptions(this.cs));
    }

    private String modify(String str) {
        return (str == null || !this.handleDirSep || str.indexOf("\\") == -1) ? str : str.replace('\\', '/');
    }
}
