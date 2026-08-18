package org.apache.tools.ant.types.selectors;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpUtil;

/* JADX INFO: loaded from: classes3.dex */
public class ContainsRegexpSelector extends BaseExtendSelector implements ResourceSelector {
    private static final String CS_KEY = "casesensitive";
    public static final String EXPRESSION_KEY = "expression";
    private static final String ML_KEY = "multiline";
    private static final String SL_KEY = "singleline";
    private String userProvidedExpression = null;
    private RegularExpression myRegExp = null;
    private Regexp myExpression = null;
    private boolean caseSensitive = true;
    private boolean multiLine = false;
    private boolean singleLine = false;

    @Override // org.apache.tools.ant.types.DataType
    public String toString() {
        return "{containsregexpselector expression: " + this.userProvidedExpression + "}";
    }

    public void setExpression(String str) {
        this.userProvidedExpression = str;
    }

    public void setCaseSensitive(boolean z) {
        this.caseSensitive = z;
    }

    public void setMultiLine(boolean z) {
        this.multiLine = z;
    }

    public void setSingleLine(boolean z) {
        this.singleLine = z;
    }

    @Override // org.apache.tools.ant.types.selectors.BaseExtendSelector, org.apache.tools.ant.types.Parameterizable
    public void setParameters(Parameter[] parameterArr) {
        super.setParameters(parameterArr);
        if (parameterArr != null) {
            for (int i = 0; i < parameterArr.length; i++) {
                String name = parameterArr[i].getName();
                if ("expression".equalsIgnoreCase(name)) {
                    setExpression(parameterArr[i].getValue());
                } else if ("casesensitive".equalsIgnoreCase(name)) {
                    setCaseSensitive(Project.toBoolean(parameterArr[i].getValue()));
                } else if (ML_KEY.equalsIgnoreCase(name)) {
                    setMultiLine(Project.toBoolean(parameterArr[i].getValue()));
                } else if (SL_KEY.equalsIgnoreCase(name)) {
                    setSingleLine(Project.toBoolean(parameterArr[i].getValue()));
                } else {
                    setError("Invalid parameter " + name);
                }
            }
        }
    }

    @Override // org.apache.tools.ant.types.selectors.BaseSelector
    public void verifySettings() {
        if (this.userProvidedExpression == null) {
            setError("The expression attribute is required");
        }
    }

    @Override // org.apache.tools.ant.types.selectors.BaseExtendSelector, org.apache.tools.ant.types.selectors.BaseSelector, org.apache.tools.ant.types.selectors.FileSelector
    public boolean isSelected(File file, String str, File file2) {
        return isSelected(new FileResource(file2));
    }

    @Override // org.apache.tools.ant.types.resources.selectors.ResourceSelector
    public boolean isSelected(Resource resource) {
        validate();
        if (resource.isDirectory()) {
            return true;
        }
        if (this.myRegExp == null) {
            RegularExpression regularExpression = new RegularExpression();
            this.myRegExp = regularExpression;
            regularExpression.setPattern(this.userProvidedExpression);
            this.myExpression = this.myRegExp.getRegexp(getProject());
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            try {
                try {
                    for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                        if (this.myExpression.matches(line, RegexpUtil.asOptions(this.caseSensitive, this.multiLine, this.singleLine))) {
                            try {
                                bufferedReader.close();
                                return true;
                            } catch (Exception unused) {
                                throw new BuildException("Could not close " + resource.toLongString());
                            }
                        }
                    }
                    try {
                        bufferedReader.close();
                        return false;
                    } catch (Exception unused2) {
                        throw new BuildException("Could not close " + resource.toLongString());
                    }
                } catch (IOException unused3) {
                    throw new BuildException("Could not read " + resource.toLongString());
                }
            } catch (Throwable th) {
                try {
                    bufferedReader.close();
                    throw th;
                } catch (Exception unused4) {
                    throw new BuildException("Could not close " + resource.toLongString());
                }
            }
        } catch (Exception e) {
            throw new BuildException("Could not get InputStream from " + resource.toLongString(), e);
        }
    }
}
