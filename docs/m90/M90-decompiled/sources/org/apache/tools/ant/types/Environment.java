package org.apache.tools.ant.types;

import java.io.File;
import java.util.Vector;
import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public class Environment {
    protected Vector<Variable> variables = new Vector<>();

    public static class Variable {
        private String key;
        private String value;

        public void setKey(String str) {
            this.key = str;
        }

        public void setValue(String str) {
            this.value = str;
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }

        public void setPath(Path path) {
            this.value = path.toString();
        }

        public void setFile(File file) {
            this.value = file.getAbsolutePath();
        }

        public String getContent() throws BuildException {
            validate();
            StringBuffer stringBuffer = new StringBuffer(this.key.trim());
            stringBuffer.append("=").append(this.value.trim());
            return stringBuffer.toString();
        }

        public void validate() {
            if (this.key == null || this.value == null) {
                throw new BuildException("key and value must be specified for environment variables.");
            }
        }
    }

    public void addVariable(Variable variable) {
        this.variables.addElement(variable);
    }

    public String[] getVariables() throws BuildException {
        if (this.variables.size() == 0) {
            return null;
        }
        int size = this.variables.size();
        String[] strArr = new String[size];
        for (int i = 0; i < size; i++) {
            strArr[i] = this.variables.elementAt(i).getContent();
        }
        return strArr;
    }

    public Vector<Variable> getVariablesVector() {
        return this.variables;
    }
}
