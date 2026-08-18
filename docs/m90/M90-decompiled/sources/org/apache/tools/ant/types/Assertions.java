package org.apache.tools.ant.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public class Assertions extends DataType implements Cloneable {
    private ArrayList<BaseAssertion> assertionList = new ArrayList<>();
    private Boolean enableSystemAssertions;

    public static class DisabledAssertion extends BaseAssertion {
        @Override // org.apache.tools.ant.types.Assertions.BaseAssertion
        public String getCommandPrefix() {
            return "-da";
        }
    }

    public static class EnabledAssertion extends BaseAssertion {
        @Override // org.apache.tools.ant.types.Assertions.BaseAssertion
        public String getCommandPrefix() {
            return "-ea";
        }
    }

    public void addEnable(EnabledAssertion enabledAssertion) {
        checkChildrenAllowed();
        this.assertionList.add(enabledAssertion);
    }

    public void addDisable(DisabledAssertion disabledAssertion) {
        checkChildrenAllowed();
        this.assertionList.add(disabledAssertion);
    }

    public void setEnableSystemAssertions(Boolean bool) {
        checkAttributesAllowed();
        this.enableSystemAssertions = bool;
    }

    @Override // org.apache.tools.ant.types.DataType
    public void setRefid(Reference reference) {
        if (this.assertionList.size() > 0 || this.enableSystemAssertions != null) {
            throw tooManyAttributes();
        }
        super.setRefid(reference);
    }

    private Assertions getFinalReference() {
        if (getRefid() == null) {
            return this;
        }
        Object referencedObject = getRefid().getReferencedObject(getProject());
        if (!(referencedObject instanceof Assertions)) {
            throw new BuildException("reference is of wrong type");
        }
        return (Assertions) referencedObject;
    }

    public int size() {
        return getFinalReference().getFinalSize();
    }

    private int getFinalSize() {
        return this.assertionList.size() + (this.enableSystemAssertions != null ? 1 : 0);
    }

    public void applyAssertions(List<String> list) {
        getProject().log("Applying assertions", 4);
        Assertions finalReference = getFinalReference();
        if (Boolean.TRUE.equals(finalReference.enableSystemAssertions)) {
            getProject().log("Enabling system assertions", 4);
            list.add("-enablesystemassertions");
        } else if (Boolean.FALSE.equals(finalReference.enableSystemAssertions)) {
            getProject().log("disabling system assertions", 4);
            list.add("-disablesystemassertions");
        }
        Iterator<BaseAssertion> it = finalReference.assertionList.iterator();
        while (it.hasNext()) {
            String command = it.next().toCommand();
            getProject().log("adding assertion " + command, 4);
            list.add(command);
        }
    }

    public void applyAssertions(CommandlineJava commandlineJava) {
        Assertions finalReference = getFinalReference();
        if (Boolean.TRUE.equals(finalReference.enableSystemAssertions)) {
            addVmArgument(commandlineJava, "-enablesystemassertions");
        } else if (Boolean.FALSE.equals(finalReference.enableSystemAssertions)) {
            addVmArgument(commandlineJava, "-disablesystemassertions");
        }
        Iterator<BaseAssertion> it = finalReference.assertionList.iterator();
        while (it.hasNext()) {
            addVmArgument(commandlineJava, it.next().toCommand());
        }
    }

    public void applyAssertions(ListIterator<String> listIterator) {
        getProject().log("Applying assertions", 4);
        Assertions finalReference = getFinalReference();
        if (Boolean.TRUE.equals(finalReference.enableSystemAssertions)) {
            getProject().log("Enabling system assertions", 4);
            listIterator.add("-enablesystemassertions");
        } else if (Boolean.FALSE.equals(finalReference.enableSystemAssertions)) {
            getProject().log("disabling system assertions", 4);
            listIterator.add("-disablesystemassertions");
        }
        Iterator<BaseAssertion> it = finalReference.assertionList.iterator();
        while (it.hasNext()) {
            String command = it.next().toCommand();
            getProject().log("adding assertion " + command, 4);
            listIterator.add(command);
        }
    }

    private static void addVmArgument(CommandlineJava commandlineJava, String str) {
        commandlineJava.createVmArgument().setValue(str);
    }

    @Override // org.apache.tools.ant.types.DataType, org.apache.tools.ant.ProjectComponent
    public Object clone() throws CloneNotSupportedException {
        Assertions assertions = (Assertions) super.clone();
        assertions.assertionList = new ArrayList<>(this.assertionList);
        return assertions;
    }

    public static abstract class BaseAssertion {
        private String className;
        private String packageName;

        public abstract String getCommandPrefix();

        public void setClass(String str) {
            this.className = str;
        }

        public void setPackage(String str) {
            this.packageName = str;
        }

        protected String getClassName() {
            return this.className;
        }

        protected String getPackageName() {
            return this.packageName;
        }

        public String toCommand() {
            if (getPackageName() != null && getClassName() != null) {
                throw new BuildException("Both package and class have been set");
            }
            StringBuffer stringBuffer = new StringBuffer(getCommandPrefix());
            if (getPackageName() != null) {
                stringBuffer.append(':');
                stringBuffer.append(getPackageName());
                if (!stringBuffer.toString().endsWith("...")) {
                    stringBuffer.append("...");
                }
            } else if (getClassName() != null) {
                stringBuffer.append(':');
                stringBuffer.append(getClassName());
            }
            return stringBuffer.toString();
        }
    }
}
