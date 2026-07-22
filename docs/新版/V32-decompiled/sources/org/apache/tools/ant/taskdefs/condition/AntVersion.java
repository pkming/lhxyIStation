package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.DeweyDecimal;

/* JADX INFO: loaded from: classes3.dex */
public class AntVersion extends Task implements Condition {
    private String atLeast = null;
    private String exactly = null;
    private String propertyname = null;

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (this.propertyname == null) {
            throw new BuildException("'property' must be set.");
        }
        if (this.atLeast != null || this.exactly != null) {
            if (eval()) {
                getProject().setNewProperty(this.propertyname, getVersion().toString());
                return;
            }
            return;
        }
        getProject().setNewProperty(this.propertyname, getVersion().toString());
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() throws BuildException {
        validate();
        DeweyDecimal version = getVersion();
        if (this.atLeast != null) {
            return version.isGreaterThanOrEqual(new DeweyDecimal(this.atLeast));
        }
        if (this.exactly != null) {
            return version.isEqual(new DeweyDecimal(this.exactly));
        }
        return false;
    }

    private void validate() throws BuildException {
        String str = this.atLeast;
        if (str != null && this.exactly != null) {
            throw new BuildException("Only one of atleast or exactly may be set.");
        }
        if (str == null && this.exactly == null) {
            throw new BuildException("One of atleast or exactly must be set.");
        }
        if (str != null) {
            try {
                new DeweyDecimal(this.atLeast);
            } catch (NumberFormatException unused) {
                throw new BuildException("The 'atleast' attribute is not a Dewey Decimal eg 1.1.0 : " + this.atLeast);
            }
        } else {
            try {
                new DeweyDecimal(this.exactly);
            } catch (NumberFormatException unused2) {
                throw new BuildException("The 'exactly' attribute is not a Dewey Decimal eg 1.1.0 : " + this.exactly);
            }
        }
    }

    private DeweyDecimal getVersion() {
        Project project = new Project();
        project.init();
        char[] charArray = project.getProperty(MagicNames.ANT_VERSION).toCharArray();
        StringBuffer stringBuffer = new StringBuffer();
        boolean z = false;
        for (int i = 0; i < charArray.length; i++) {
            if (Character.isDigit(charArray[i])) {
                stringBuffer.append(charArray[i]);
                z = true;
            }
            if (charArray[i] == '.' && z) {
                stringBuffer.append(charArray[i]);
            }
            if (Character.isLetter(charArray[i]) && z) {
                break;
            }
        }
        return new DeweyDecimal(stringBuffer.toString());
    }

    public String getAtLeast() {
        return this.atLeast;
    }

    public void setAtLeast(String str) {
        this.atLeast = str;
    }

    public String getExactly() {
        return this.exactly;
    }

    public void setExactly(String str) {
        this.exactly = str;
    }

    public String getProperty() {
        return this.propertyname;
    }

    public void setProperty(String str) {
        this.propertyname = str;
    }
}
