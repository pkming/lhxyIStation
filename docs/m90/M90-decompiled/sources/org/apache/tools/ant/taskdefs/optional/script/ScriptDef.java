package org.apache.tools.ant.taskdefs.optional.script;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.taskdefs.DefBase;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.ScriptRunnerBase;
import org.apache.tools.ant.util.ScriptRunnerHelper;

/* JADX INFO: loaded from: classes3.dex */
public class ScriptDef extends DefBase {
    private Set attributeSet;
    private String name;
    private Map nestedElementMap;
    private ScriptRunnerHelper helper = new ScriptRunnerHelper();
    private List attributes = new ArrayList();
    private List nestedElements = new ArrayList();

    @Override // org.apache.tools.ant.ProjectComponent
    public void setProject(Project project) {
        super.setProject(project);
        this.helper.setProjectComponent(this);
        this.helper.setSetBeans(false);
    }

    public void setName(String str) {
        this.name = str;
    }

    public boolean isAttributeSupported(String str) {
        return this.attributeSet.contains(str);
    }

    public static class Attribute {
        private String name;

        public void setName(String str) {
            this.name = str.toLowerCase(Locale.ENGLISH);
        }
    }

    public void addAttribute(Attribute attribute) {
        this.attributes.add(attribute);
    }

    public static class NestedElement {
        private String className;
        private String name;
        private String type;

        public void setName(String str) {
            this.name = str.toLowerCase(Locale.ENGLISH);
        }

        public void setType(String str) {
            this.type = str;
        }

        public void setClassName(String str) {
            this.className = str;
        }
    }

    public void addElement(NestedElement nestedElement) {
        this.nestedElements.add(nestedElement);
    }

    @Override // org.apache.tools.ant.Task
    public void execute() {
        if (this.name == null) {
            throw new BuildException("scriptdef requires a name attribute to name the script");
        }
        if (this.helper.getLanguage() == null) {
            throw new BuildException("<scriptdef> requires a language attribute to specify the script language");
        }
        if (getAntlibClassLoader() != null || hasCpDelegate()) {
            this.helper.setClassLoader(createLoader());
        }
        this.attributeSet = new HashSet();
        for (Attribute attribute : this.attributes) {
            if (attribute.name != null) {
                if (!this.attributeSet.contains(attribute.name)) {
                    this.attributeSet.add(attribute.name);
                } else {
                    throw new BuildException("scriptdef <" + this.name + "> declares the " + attribute.name + " attribute more than once");
                }
            } else {
                throw new BuildException("scriptdef <attribute> elements must specify an attribute name");
            }
        }
        this.nestedElementMap = new HashMap();
        for (NestedElement nestedElement : this.nestedElements) {
            if (nestedElement.name != null) {
                if (!this.nestedElementMap.containsKey(nestedElement.name)) {
                    if (nestedElement.className != null || nestedElement.type != null) {
                        if (nestedElement.className == null || nestedElement.type == null) {
                            this.nestedElementMap.put(nestedElement.name, nestedElement);
                        } else {
                            throw new BuildException("scriptdef <element> elements must specify only one of the classname and type attributes");
                        }
                    } else {
                        throw new BuildException("scriptdef <element> elements must specify either a classname or type attribute");
                    }
                } else {
                    throw new BuildException("scriptdef <" + this.name + "> declares the " + nestedElement.name + " nested element more than once");
                }
            } else {
                throw new BuildException("scriptdef <element> elements must specify an element name");
            }
        }
        Map mapLookupScriptRepository = lookupScriptRepository();
        String strGenComponentName = ProjectHelper.genComponentName(getURI(), this.name);
        this.name = strGenComponentName;
        mapLookupScriptRepository.put(strGenComponentName, this);
        AntTypeDefinition antTypeDefinition = new AntTypeDefinition();
        antTypeDefinition.setName(this.name);
        antTypeDefinition.setClass(ScriptDefBase.class);
        ComponentHelper.getComponentHelper(getProject()).addDataTypeDefinition(antTypeDefinition);
    }

    private Map lookupScriptRepository() {
        Map map;
        Project project = getProject();
        synchronized (project) {
            map = (Map) project.getReference(MagicNames.SCRIPT_REPOSITORY);
            if (map == null) {
                map = new HashMap();
                project.addReference(MagicNames.SCRIPT_REPOSITORY, map);
            }
        }
        return map;
    }

    public Object createNestedElement(String str) {
        Object objNewInstance;
        Object objCreateTask;
        NestedElement nestedElement = (NestedElement) this.nestedElementMap.get(str);
        if (nestedElement != null) {
            String str2 = nestedElement.className;
            if (str2 == null) {
                objCreateTask = getProject().createTask(nestedElement.type);
                if (objCreateTask == null) {
                    objCreateTask = getProject().createDataType(nestedElement.type);
                }
            } else {
                try {
                    objNewInstance = ClasspathUtils.newInstance(str2, createLoader());
                } catch (BuildException unused) {
                    objNewInstance = ClasspathUtils.newInstance(str2, ScriptDef.class.getClassLoader());
                }
                objCreateTask = objNewInstance;
                getProject().setProjectReference(objCreateTask);
            }
            if (objCreateTask != null) {
                return objCreateTask;
            }
            throw new BuildException("<" + this.name + "> is unable to create the <" + str + "> nested element");
        }
        throw new BuildException("<" + this.name + "> does not support the <" + str + "> nested element");
    }

    public void executeScript(Map map, Map map2) {
        executeScript(map, map2, null);
    }

    public void executeScript(Map map, Map map2, ScriptDefBase scriptDefBase) {
        ScriptRunnerBase scriptRunner = this.helper.getScriptRunner();
        scriptRunner.addBean("attributes", map);
        scriptRunner.addBean("elements", map2);
        scriptRunner.addBean("project", getProject());
        if (scriptDefBase != null) {
            scriptRunner.addBean("self", scriptDefBase);
        }
        scriptRunner.executeScript("scriptdef_" + this.name);
    }

    public void setManager(String str) {
        this.helper.setManager(str);
    }

    public void setLanguage(String str) {
        this.helper.setLanguage(str);
    }

    public void setSrc(File file) {
        this.helper.setSrc(file);
    }

    public void addText(String str) {
        this.helper.addText(str);
    }

    public void add(ResourceCollection resourceCollection) {
        this.helper.add(resourceCollection);
    }
}
