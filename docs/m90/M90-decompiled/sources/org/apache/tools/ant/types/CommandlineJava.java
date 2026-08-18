package org.apache.tools.ant.types;

import com.unisound.common.r;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Definer;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSSConstants;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.util.JavaEnvUtils;

/* JADX INFO: loaded from: classes3.dex */
public class CommandlineJava implements Cloneable {
    private String vmVersion;
    private Commandline vmCommand = new Commandline();
    private Commandline javaCommand = new Commandline();
    private SysProperties sysProperties = new SysProperties();
    private Path classpath = null;
    private Path bootclasspath = null;
    private String maxMemory = null;
    private Assertions assertions = null;
    private boolean executeJar = false;
    private boolean cloneVm = false;

    public static class SysProperties extends Environment implements Cloneable {
        Properties sys = null;
        private Vector<PropertySet> propertySets = new Vector<>();

        @Override // org.apache.tools.ant.types.Environment
        public String[] getVariables() throws BuildException {
            LinkedList linkedList = new LinkedList();
            addDefinitionsToList(linkedList.listIterator());
            if (linkedList.size() == 0) {
                return null;
            }
            return (String[]) linkedList.toArray(new String[linkedList.size()]);
        }

        public void addDefinitionsToList(ListIterator<String> listIterator) {
            String[] variables = super.getVariables();
            if (variables != null) {
                for (String str : variables) {
                    listIterator.add(MSVSSConstants.FLAG_CODEDIFF + str);
                }
            }
            Properties propertiesMergePropertySets = mergePropertySets();
            Enumeration enumerationKeys = propertiesMergePropertySets.keys();
            while (enumerationKeys.hasMoreElements()) {
                String str2 = (String) enumerationKeys.nextElement();
                listIterator.add(MSVSSConstants.FLAG_CODEDIFF + str2 + "=" + propertiesMergePropertySets.getProperty(str2));
            }
        }

        public int size() {
            return this.variables.size() + mergePropertySets().size();
        }

        public void setSystem() throws BuildException {
            try {
                this.sys = System.getProperties();
                Properties properties = new Properties();
                Enumeration<?> enumerationPropertyNames = this.sys.propertyNames();
                while (enumerationPropertyNames.hasMoreElements()) {
                    String str = (String) enumerationPropertyNames.nextElement();
                    String property = this.sys.getProperty(str);
                    if (str != null && property != null) {
                        properties.put(str, property);
                    }
                }
                properties.putAll(mergePropertySets());
                for (Environment.Variable variable : this.variables) {
                    variable.validate();
                    properties.put(variable.getKey(), variable.getValue());
                }
                System.setProperties(properties);
            } catch (SecurityException e) {
                throw new BuildException("Cannot modify system properties", e);
            }
        }

        public void restoreSystem() throws BuildException {
            Properties properties = this.sys;
            if (properties == null) {
                throw new BuildException("Unbalanced nesting of SysProperties");
            }
            try {
                System.setProperties(properties);
                this.sys = null;
            } catch (SecurityException e) {
                throw new BuildException("Cannot modify system properties", e);
            }
        }

        public Object clone() throws CloneNotSupportedException {
            try {
                SysProperties sysProperties = (SysProperties) super.clone();
                sysProperties.variables = (Vector) this.variables.clone();
                sysProperties.propertySets = (Vector) this.propertySets.clone();
                return sysProperties;
            } catch (CloneNotSupportedException unused) {
                return null;
            }
        }

        public void addSyspropertyset(PropertySet propertySet) {
            this.propertySets.addElement(propertySet);
        }

        public void addSysproperties(SysProperties sysProperties) {
            this.variables.addAll(sysProperties.variables);
            this.propertySets.addAll(sysProperties.propertySets);
        }

        private Properties mergePropertySets() {
            Properties properties = new Properties();
            Iterator<PropertySet> it = this.propertySets.iterator();
            while (it.hasNext()) {
                properties.putAll(it.next().getProperties());
            }
            return properties;
        }
    }

    public CommandlineJava() {
        setVm(JavaEnvUtils.getJreExecutable("java"));
        setVmversion(JavaEnvUtils.getJavaVersion());
    }

    public Commandline.Argument createArgument() {
        return this.javaCommand.createArgument();
    }

    public Commandline.Argument createVmArgument() {
        return this.vmCommand.createArgument();
    }

    public void addSysproperty(Environment.Variable variable) {
        this.sysProperties.addVariable(variable);
    }

    public void addSyspropertyset(PropertySet propertySet) {
        this.sysProperties.addSyspropertyset(propertySet);
    }

    public void addSysproperties(SysProperties sysProperties) {
        this.sysProperties.addSysproperties(sysProperties);
    }

    public void setVm(String str) {
        this.vmCommand.setExecutable(str);
    }

    public void setVmversion(String str) {
        this.vmVersion = str;
    }

    public void setCloneVm(boolean z) {
        this.cloneVm = z;
    }

    public Assertions getAssertions() {
        return this.assertions;
    }

    public void setAssertions(Assertions assertions) {
        this.assertions = assertions;
    }

    public void setJar(String str) {
        this.javaCommand.setExecutable(str);
        this.executeJar = true;
    }

    public String getJar() {
        if (this.executeJar) {
            return this.javaCommand.getExecutable();
        }
        return null;
    }

    public void setClassname(String str) {
        this.javaCommand.setExecutable(str);
        this.executeJar = false;
    }

    public String getClassname() {
        if (this.executeJar) {
            return null;
        }
        return this.javaCommand.getExecutable();
    }

    public Path createClasspath(Project project) {
        if (this.classpath == null) {
            this.classpath = new Path(project);
        }
        return this.classpath;
    }

    public Path createBootclasspath(Project project) {
        if (this.bootclasspath == null) {
            this.bootclasspath = new Path(project);
        }
        return this.bootclasspath;
    }

    public String getVmversion() {
        return this.vmVersion;
    }

    public String[] getCommandline() {
        LinkedList linkedList = new LinkedList();
        addCommandsToList(linkedList.listIterator());
        return (String[]) linkedList.toArray(new String[linkedList.size()]);
    }

    private void addCommandsToList(ListIterator<String> listIterator) {
        getActualVMCommand().addCommandToList(listIterator);
        this.sysProperties.addDefinitionsToList(listIterator);
        if (isCloneVm()) {
            SysProperties sysProperties = new SysProperties();
            PropertySet propertySet = new PropertySet();
            PropertySet.BuiltinPropertySetName builtinPropertySetName = new PropertySet.BuiltinPropertySetName();
            builtinPropertySetName.setValue("system");
            propertySet.appendBuiltin(builtinPropertySetName);
            sysProperties.addSyspropertyset(propertySet);
            sysProperties.addDefinitionsToList(listIterator);
        }
        Path pathCalculateBootclasspath = calculateBootclasspath(true);
        if (pathCalculateBootclasspath.size() > 0) {
            listIterator.add("-Xbootclasspath:" + pathCalculateBootclasspath.toString());
        }
        if (haveClasspath()) {
            listIterator.add("-classpath");
            listIterator.add(this.classpath.concatSystemClasspath(Definer.OnError.POLICY_IGNORE).toString());
        }
        if (getAssertions() != null) {
            getAssertions().applyAssertions(listIterator);
        }
        if (this.executeJar) {
            listIterator.add("-jar");
        }
        this.javaCommand.addCommandToList(listIterator);
    }

    public void setMaxmemory(String str) {
        this.maxMemory = str;
    }

    public String toString() {
        return Commandline.toString(getCommandline());
    }

    public String describeCommand() {
        return Commandline.describeCommand(getCommandline());
    }

    public String describeJavaCommand() {
        return Commandline.describeCommand(getJavaCommand());
    }

    protected Commandline getActualVMCommand() {
        Commandline commandline = (Commandline) this.vmCommand.clone();
        if (this.maxMemory != null) {
            if (this.vmVersion.startsWith("1.1")) {
                commandline.createArgument().setValue("-mx" + this.maxMemory);
            } else {
                commandline.createArgument().setValue("-Xmx" + this.maxMemory);
            }
        }
        return commandline;
    }

    public int size() {
        int size = getActualVMCommand().size() + this.javaCommand.size() + this.sysProperties.size();
        if (isCloneVm()) {
            size += System.getProperties().size();
        }
        if (haveClasspath()) {
            size += 2;
        }
        if (calculateBootclasspath(true).size() > 0) {
            size++;
        }
        if (this.executeJar) {
            size++;
        }
        return getAssertions() != null ? size + getAssertions().size() : size;
    }

    public Commandline getJavaCommand() {
        return this.javaCommand;
    }

    public Commandline getVmCommand() {
        return getActualVMCommand();
    }

    public Path getClasspath() {
        return this.classpath;
    }

    public Path getBootclasspath() {
        return this.bootclasspath;
    }

    public void setSystemProperties() throws BuildException {
        this.sysProperties.setSystem();
    }

    public void restoreSystemProperties() throws BuildException {
        this.sysProperties.restoreSystem();
    }

    public SysProperties getSystemProperties() {
        return this.sysProperties;
    }

    public Object clone() throws CloneNotSupportedException {
        try {
            CommandlineJava commandlineJava = (CommandlineJava) super.clone();
            commandlineJava.vmCommand = (Commandline) this.vmCommand.clone();
            commandlineJava.javaCommand = (Commandline) this.javaCommand.clone();
            commandlineJava.sysProperties = (SysProperties) this.sysProperties.clone();
            Path path = this.classpath;
            if (path != null) {
                commandlineJava.classpath = (Path) path.clone();
            }
            Path path2 = this.bootclasspath;
            if (path2 != null) {
                commandlineJava.bootclasspath = (Path) path2.clone();
            }
            Assertions assertions = this.assertions;
            if (assertions != null) {
                commandlineJava.assertions = (Assertions) assertions.clone();
            }
            return commandlineJava;
        } catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }

    public void clearJavaArgs() {
        this.javaCommand.clearArgs();
    }

    public boolean haveClasspath() {
        Path path = this.classpath;
        Path pathConcatSystemClasspath = path != null ? path.concatSystemClasspath(Definer.OnError.POLICY_IGNORE) : null;
        return pathConcatSystemClasspath != null && pathConcatSystemClasspath.toString().trim().length() > 0;
    }

    protected boolean haveBootclasspath(boolean z) {
        return calculateBootclasspath(z).size() > 0;
    }

    private Path calculateBootclasspath(boolean z) {
        if (this.vmVersion.startsWith("1.1")) {
            Path path = this.bootclasspath;
            if (path != null && z) {
                path.log("Ignoring bootclasspath as the target VM doesn't support it.");
            }
            return new Path(null);
        }
        Path path2 = this.bootclasspath;
        if (path2 == null) {
            path2 = new Path(null);
        }
        return path2.concatSystemBootClasspath(isCloneVm() ? r.E : Definer.OnError.POLICY_IGNORE);
    }

    private boolean isCloneVm() {
        return this.cloneVm || "true".equals(System.getProperty("ant.build.clonevm"));
    }
}
