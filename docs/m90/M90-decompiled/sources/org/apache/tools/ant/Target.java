package org.apache.tools.ant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.tools.ant.property.LocalProperties;
import org.apache.tools.ant.taskdefs.condition.And;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.Or;

/* JADX INFO: loaded from: classes3.dex */
public class Target implements TaskContainer {
    private List<Object> children;
    private List<String> dependencies;
    private String description;
    private Condition ifCondition;
    private String ifString;
    private Location location;
    private String name;
    private Project project;
    private Condition unlessCondition;
    private String unlessString;

    public Target() {
        this.ifString = "";
        this.unlessString = "";
        this.dependencies = null;
        this.children = new ArrayList();
        this.location = Location.UNKNOWN_LOCATION;
        this.description = null;
    }

    public Target(Target target) {
        this.ifString = "";
        this.unlessString = "";
        this.dependencies = null;
        this.children = new ArrayList();
        this.location = Location.UNKNOWN_LOCATION;
        this.description = null;
        this.name = target.name;
        this.ifString = target.ifString;
        this.unlessString = target.unlessString;
        this.ifCondition = target.ifCondition;
        this.unlessCondition = target.unlessCondition;
        this.dependencies = target.dependencies;
        this.location = target.location;
        this.project = target.project;
        this.description = target.description;
        this.children = target.children;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return this.project;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setDepends(String str) {
        Iterator<String> it = parseDepends(str, getName(), "depends").iterator();
        while (it.hasNext()) {
            addDependency(it.next());
        }
    }

    public static List<String> parseDepends(String str, String str2, String str3) {
        ArrayList arrayList = new ArrayList();
        if (str.length() > 0) {
            StringTokenizer stringTokenizer = new StringTokenizer(str, ",", true);
            while (stringTokenizer.hasMoreTokens()) {
                String strTrim = stringTokenizer.nextToken().trim();
                if ("".equals(strTrim) || ",".equals(strTrim)) {
                    throw new BuildException("Syntax Error: " + str3 + " attribute of target \"" + str2 + "\" contains an empty string.");
                }
                arrayList.add(strTrim);
                if (stringTokenizer.hasMoreTokens()) {
                    String strNextToken = stringTokenizer.nextToken();
                    if (!stringTokenizer.hasMoreTokens() || !",".equals(strNextToken)) {
                        throw new BuildException("Syntax Error: " + str3 + " attribute for target \"" + str2 + "\" ends with a \",\" character");
                    }
                }
            }
        }
        return arrayList;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getName() {
        return this.name;
    }

    @Override // org.apache.tools.ant.TaskContainer
    public void addTask(Task task) {
        this.children.add(task);
    }

    public void addDataType(RuntimeConfigurable runtimeConfigurable) {
        this.children.add(runtimeConfigurable);
    }

    public Task[] getTasks() {
        ArrayList arrayList = new ArrayList(this.children.size());
        for (Object obj : this.children) {
            if (obj instanceof Task) {
                arrayList.add((Task) obj);
            }
        }
        return (Task[]) arrayList.toArray(new Task[arrayList.size()]);
    }

    public void addDependency(String str) {
        if (this.dependencies == null) {
            this.dependencies = new ArrayList(2);
        }
        this.dependencies.add(str);
    }

    public Enumeration<String> getDependencies() {
        List<String> listEmptyList = this.dependencies;
        if (listEmptyList == null) {
            listEmptyList = Collections.emptyList();
        }
        return Collections.enumeration(listEmptyList);
    }

    public boolean dependsOn(String str) {
        Project project = getProject();
        Hashtable<String, Target> targets = project == null ? null : project.getTargets();
        return project != null && project.topoSort(getName(), targets, false).contains(targets.get(str));
    }

    public void setIf(String str) {
        if (str == null) {
            str = "";
        }
        this.ifString = str;
        setIf(new IfStringCondition(this.ifString));
    }

    public String getIf() {
        if ("".equals(this.ifString)) {
            return null;
        }
        return this.ifString;
    }

    public void setIf(Condition condition) {
        if (this.ifCondition == null) {
            this.ifCondition = condition;
            return;
        }
        And and = new And();
        and.setProject(getProject());
        and.setLocation(getLocation());
        and.add(this.ifCondition);
        and.add(condition);
        this.ifCondition = and;
    }

    public void setUnless(String str) {
        if (str == null) {
            str = "";
        }
        this.unlessString = str;
        setUnless(new UnlessStringCondition(this.unlessString));
    }

    public String getUnless() {
        if ("".equals(this.unlessString)) {
            return null;
        }
        return this.unlessString;
    }

    public void setUnless(Condition condition) {
        if (this.unlessCondition == null) {
            this.unlessCondition = condition;
            return;
        }
        Or or = new Or();
        or.setProject(getProject());
        or.setLocation(getLocation());
        or.add(this.unlessCondition);
        or.add(condition);
        this.unlessCondition = or;
    }

    public void setDescription(String str) {
        this.description = str;
    }

    public String getDescription() {
        return this.description;
    }

    public String toString() {
        return this.name;
    }

    public void execute() throws BuildException {
        Condition condition = this.ifCondition;
        if (condition != null && !condition.eval()) {
            this.project.log(this, "Skipped because property '" + this.project.replaceProperties(this.ifString) + "' not set.", 3);
            return;
        }
        Condition condition2 = this.unlessCondition;
        if (condition2 != null && condition2.eval()) {
            this.project.log(this, "Skipped because property '" + this.project.replaceProperties(this.unlessString) + "' set.", 3);
            return;
        }
        LocalProperties localProperties = LocalProperties.get(getProject());
        localProperties.enterScope();
        for (int i = 0; i < this.children.size(); i++) {
            try {
                Object obj = this.children.get(i);
                if (obj instanceof Task) {
                    ((Task) obj).perform();
                } else {
                    ((RuntimeConfigurable) obj).maybeConfigure(this.project);
                }
            } finally {
                localProperties.exitScope();
            }
        }
    }

    public final void performTasks() {
        this.project.fireTargetStarted(this);
        RuntimeException e = null;
        try {
            try {
                execute();
            } catch (RuntimeException e2) {
                e = e2;
                throw e;
            }
        } finally {
            this.project.fireTargetFinished(this, e);
        }
    }

    void replaceChild(Task task, RuntimeConfigurable runtimeConfigurable) {
        while (true) {
            int iIndexOf = this.children.indexOf(task);
            if (iIndexOf < 0) {
                return;
            } else {
                this.children.set(iIndexOf, runtimeConfigurable);
            }
        }
    }

    void replaceChild(Task task, Task task2) {
        while (true) {
            int iIndexOf = this.children.indexOf(task);
            if (iIndexOf < 0) {
                return;
            } else {
                this.children.set(iIndexOf, task2);
            }
        }
    }

    private class IfStringCondition implements Condition {
        private String condition;

        public IfStringCondition(String str) {
            this.condition = str;
        }

        @Override // org.apache.tools.ant.taskdefs.condition.Condition
        public boolean eval() throws BuildException {
            PropertyHelper propertyHelper = PropertyHelper.getPropertyHelper(Target.this.getProject());
            return propertyHelper.testIfCondition(propertyHelper.parseProperties(this.condition));
        }
    }

    private class UnlessStringCondition implements Condition {
        private String condition;

        public UnlessStringCondition(String str) {
            this.condition = str;
        }

        @Override // org.apache.tools.ant.taskdefs.condition.Condition
        public boolean eval() throws BuildException {
            PropertyHelper propertyHelper = PropertyHelper.getPropertyHelper(Target.this.getProject());
            return !propertyHelper.testUnlessCondition(propertyHelper.parseProperties(this.condition));
        }
    }
}
