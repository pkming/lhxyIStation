package org.apache.tools.ant.taskdefs;

import java.util.Enumeration;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.JavaEnvUtils;

/* JADX INFO: loaded from: classes3.dex */
public class GenerateKey extends Task {
    protected String alias;
    protected String dname;
    protected DistinguishedName expandedDname;
    protected String keyalg;
    protected String keypass;
    protected int keysize;
    protected String keystore;
    protected String sigalg;
    protected String storepass;
    protected String storetype;
    protected int validity;
    protected boolean verbose;

    public static class DnameParam {
        private String name;
        private String value;

        public void setName(String str) {
            this.name = str;
        }

        public String getName() {
            return this.name;
        }

        public void setValue(String str) {
            this.value = str;
        }

        public String getValue() {
            return this.value;
        }
    }

    public static class DistinguishedName {
        private Vector<DnameParam> params = new Vector<>();

        public Object createParam() {
            DnameParam dnameParam = new DnameParam();
            this.params.addElement(dnameParam);
            return dnameParam;
        }

        public Enumeration<DnameParam> getParams() {
            return this.params.elements();
        }

        public String toString() {
            int size = this.params.size();
            StringBuffer stringBuffer = new StringBuffer();
            boolean z = true;
            int i = 0;
            while (i < size) {
                if (!z) {
                    stringBuffer.append(" ,");
                }
                DnameParam dnameParamElementAt = this.params.elementAt(i);
                stringBuffer.append(encode(dnameParamElementAt.getName()));
                stringBuffer.append('=');
                stringBuffer.append(encode(dnameParamElementAt.getValue()));
                i++;
                z = false;
            }
            return stringBuffer.toString();
        }

        public String encode(String str) {
            int iIndexOf = str.indexOf(44);
            if (-1 == iIndexOf) {
                return str;
            }
            StringBuffer stringBuffer = new StringBuffer();
            int i = 0;
            while (-1 != iIndexOf) {
                stringBuffer.append(str.substring(i, iIndexOf));
                stringBuffer.append("\\,");
                i = iIndexOf + 1;
                iIndexOf = str.indexOf(44, i);
            }
            stringBuffer.append(str.substring(i));
            return stringBuffer.toString();
        }
    }

    public DistinguishedName createDname() throws BuildException {
        if (this.expandedDname != null) {
            throw new BuildException("DName sub-element can only be specified once.");
        }
        if (this.dname != null) {
            throw new BuildException("It is not possible to specify dname  both as attribute and element.");
        }
        DistinguishedName distinguishedName = new DistinguishedName();
        this.expandedDname = distinguishedName;
        return distinguishedName;
    }

    public void setDname(String str) {
        if (this.expandedDname != null) {
            throw new BuildException("It is not possible to specify dname  both as attribute and element.");
        }
        this.dname = str;
    }

    public void setAlias(String str) {
        this.alias = str;
    }

    public void setKeystore(String str) {
        this.keystore = str;
    }

    public void setStorepass(String str) {
        this.storepass = str;
    }

    public void setStoretype(String str) {
        this.storetype = str;
    }

    public void setKeypass(String str) {
        this.keypass = str;
    }

    public void setSigalg(String str) {
        this.sigalg = str;
    }

    public void setKeyalg(String str) {
        this.keyalg = str;
    }

    public void setKeysize(String str) throws BuildException {
        try {
            this.keysize = Integer.parseInt(str);
        } catch (NumberFormatException unused) {
            throw new BuildException("KeySize attribute should be a integer");
        }
    }

    public void setValidity(String str) throws BuildException {
        try {
            this.validity = Integer.parseInt(str);
        } catch (NumberFormatException unused) {
            throw new BuildException("Validity attribute should be a integer");
        }
    }

    public void setVerbose(boolean z) {
        this.verbose = z;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (this.alias == null) {
            throw new BuildException(SignJar.ERROR_NO_ALIAS);
        }
        if (this.storepass == null) {
            throw new BuildException(SignJar.ERROR_NO_STOREPASS);
        }
        if (this.dname == null && this.expandedDname == null) {
            throw new BuildException("dname must be set");
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("-genkey ");
        if (this.verbose) {
            stringBuffer.append("-v ");
        }
        stringBuffer.append("-alias \"");
        stringBuffer.append(this.alias);
        stringBuffer.append("\" ");
        if (this.dname != null) {
            stringBuffer.append("-dname \"");
            stringBuffer.append(this.dname);
            stringBuffer.append("\" ");
        }
        if (this.expandedDname != null) {
            stringBuffer.append("-dname \"");
            stringBuffer.append(this.expandedDname);
            stringBuffer.append("\" ");
        }
        if (this.keystore != null) {
            stringBuffer.append("-keystore \"");
            stringBuffer.append(this.keystore);
            stringBuffer.append("\" ");
        }
        if (this.storepass != null) {
            stringBuffer.append("-storepass \"");
            stringBuffer.append(this.storepass);
            stringBuffer.append("\" ");
        }
        if (this.storetype != null) {
            stringBuffer.append("-storetype \"");
            stringBuffer.append(this.storetype);
            stringBuffer.append("\" ");
        }
        stringBuffer.append("-keypass \"");
        String str = this.keypass;
        if (str != null) {
            stringBuffer.append(str);
        } else {
            stringBuffer.append(this.storepass);
        }
        stringBuffer.append("\" ");
        if (this.sigalg != null) {
            stringBuffer.append("-sigalg \"");
            stringBuffer.append(this.sigalg);
            stringBuffer.append("\" ");
        }
        if (this.keyalg != null) {
            stringBuffer.append("-keyalg \"");
            stringBuffer.append(this.keyalg);
            stringBuffer.append("\" ");
        }
        if (this.keysize > 0) {
            stringBuffer.append("-keysize \"");
            stringBuffer.append(this.keysize);
            stringBuffer.append("\" ");
        }
        if (this.validity > 0) {
            stringBuffer.append("-validity \"");
            stringBuffer.append(this.validity);
            stringBuffer.append("\" ");
        }
        log("Generating Key for " + this.alias);
        ExecTask execTask = new ExecTask(this);
        execTask.setExecutable(JavaEnvUtils.getJdkExecutable("keytool"));
        execTask.createArg().setLine(stringBuffer.toString());
        execTask.setFailonerror(true);
        execTask.setTaskName(getTaskName());
        execTask.execute();
    }
}
