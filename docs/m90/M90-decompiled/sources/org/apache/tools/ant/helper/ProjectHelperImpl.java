package org.apache.tools.ant.helper;

import android.app.Instrumentation;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Locale;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.IntrospectionHelper;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.TypeAdapter;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.util.FileUtils;
import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: classes3.dex */
public class ProjectHelperImpl extends ProjectHelper {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private File buildFile;
    private File buildFileParent;
    private Target implicitTarget;
    private Locator locator;
    private Parser parser;
    private Project project;

    public ProjectHelperImpl() {
        Target target = new Target();
        this.implicitTarget = target;
        target.setName("");
    }

    /* JADX WARN: Removed duplicated region for block: B:44:0x00e5 A[Catch: all -> 0x0028, TryCatch #3 {all -> 0x0028, blocks: (B:5:0x0021, B:20:0x0045, B:36:0x00b2, B:37:0x00ce, B:38:0x00cf, B:39:0x00d6, B:40:0x00d7, B:41:0x00dc, B:42:0x00dd, B:44:0x00e5, B:45:0x00e7, B:46:0x00e8, B:47:0x00f1, B:48:0x00f2, B:50:0x010b, B:52:0x0115, B:53:0x0118, B:54:0x0119, B:55:0x0122, B:19:0x003a), top: B:62:0x0021 }] */
    /* JADX WARN: Removed duplicated region for block: B:46:0x00e8 A[Catch: all -> 0x0028, TryCatch #3 {all -> 0x0028, blocks: (B:5:0x0021, B:20:0x0045, B:36:0x00b2, B:37:0x00ce, B:38:0x00cf, B:39:0x00d6, B:40:0x00d7, B:41:0x00dc, B:42:0x00dd, B:44:0x00e5, B:45:0x00e7, B:46:0x00e8, B:47:0x00f1, B:48:0x00f2, B:50:0x010b, B:52:0x0115, B:53:0x0118, B:54:0x0119, B:55:0x0122, B:19:0x003a), top: B:62:0x0021 }] */
    /* JADX WARN: Removed duplicated region for block: B:50:0x010b A[Catch: all -> 0x0028, TryCatch #3 {all -> 0x0028, blocks: (B:5:0x0021, B:20:0x0045, B:36:0x00b2, B:37:0x00ce, B:38:0x00cf, B:39:0x00d6, B:40:0x00d7, B:41:0x00dc, B:42:0x00dd, B:44:0x00e5, B:45:0x00e7, B:46:0x00e8, B:47:0x00f1, B:48:0x00f2, B:50:0x010b, B:52:0x0115, B:53:0x0118, B:54:0x0119, B:55:0x0122, B:19:0x003a), top: B:62:0x0021 }] */
    /* JADX WARN: Removed duplicated region for block: B:54:0x0119 A[Catch: all -> 0x0028, TryCatch #3 {all -> 0x0028, blocks: (B:5:0x0021, B:20:0x0045, B:36:0x00b2, B:37:0x00ce, B:38:0x00cf, B:39:0x00d6, B:40:0x00d7, B:41:0x00dc, B:42:0x00dd, B:44:0x00e5, B:45:0x00e7, B:46:0x00e8, B:47:0x00f1, B:48:0x00f2, B:50:0x010b, B:52:0x0115, B:53:0x0118, B:54:0x0119, B:55:0x0122, B:19:0x003a), top: B:62:0x0021 }] */
    @Override // org.apache.tools.ant.ProjectHelper
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void parse(org.apache.tools.ant.Project r6, java.lang.Object r7) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 303
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.helper.ProjectHelperImpl.parse(org.apache.tools.ant.Project, java.lang.Object):void");
    }

    static class AbstractHandler extends HandlerBase {
        ProjectHelperImpl helperImpl;
        protected DocumentHandler parentHandler;

        public AbstractHandler(ProjectHelperImpl projectHelperImpl, DocumentHandler documentHandler) {
            this.parentHandler = documentHandler;
            this.helperImpl = projectHelperImpl;
            projectHelperImpl.parser.setDocumentHandler(this);
        }

        @Override // org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
        public void startElement(String str, AttributeList attributeList) throws SAXParseException {
            throw new SAXParseException("Unexpected element \"" + str + "\"", this.helperImpl.locator);
        }

        @Override // org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
        public void characters(char[] cArr, int i, int i2) throws SAXParseException {
            String strTrim = new String(cArr, i, i2).trim();
            if (strTrim.length() > 0) {
                throw new SAXParseException("Unexpected text \"" + strTrim + "\"", this.helperImpl.locator);
            }
        }

        @Override // org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
        public void endElement(String str) throws SAXException {
            this.helperImpl.parser.setDocumentHandler(this.parentHandler);
        }
    }

    static class RootHandler extends HandlerBase {
        ProjectHelperImpl helperImpl;

        public RootHandler(ProjectHelperImpl projectHelperImpl) {
            this.helperImpl = projectHelperImpl;
        }

        @Override // org.xml.sax.HandlerBase, org.xml.sax.EntityResolver
        public InputSource resolveEntity(String str, String str2) {
            this.helperImpl.project.log("resolving systemId: " + str2, 3);
            if (!str2.startsWith("file:")) {
                return null;
            }
            String strFromURI = ProjectHelperImpl.FILE_UTILS.fromURI(str2);
            File file = new File(strFromURI);
            if (!file.isAbsolute()) {
                file = ProjectHelperImpl.FILE_UTILS.resolveFile(this.helperImpl.buildFileParent, strFromURI);
                this.helperImpl.project.log("Warning: '" + str2 + "' in " + this.helperImpl.buildFile + " should be expressed simply as '" + strFromURI.replace('\\', '/') + "' for compliance with other XML tools", 1);
            }
            try {
                InputSource inputSource = new InputSource(new FileInputStream(file));
                inputSource.setSystemId(ProjectHelperImpl.FILE_UTILS.toURI(file.getAbsolutePath()));
                return inputSource;
            } catch (FileNotFoundException unused) {
                this.helperImpl.project.log(file.getAbsolutePath() + " could not be found", 1);
                return null;
            }
        }

        @Override // org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
        public void startElement(String str, AttributeList attributeList) throws SAXParseException {
            if (str.equals("project")) {
                new ProjectHandler(this.helperImpl, this).init(str, attributeList);
                return;
            }
            throw new SAXParseException("Config file is not of expected XML type", this.helperImpl.locator);
        }

        @Override // org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
        public void setDocumentLocator(Locator locator) {
            this.helperImpl.locator = locator;
        }
    }

    static class ProjectHandler extends AbstractHandler {
        public ProjectHandler(ProjectHelperImpl projectHelperImpl, DocumentHandler documentHandler) {
            super(projectHelperImpl, documentHandler);
        }

        public void init(String str, AttributeList attributeList) throws SAXParseException {
            String str2 = null;
            String str3 = null;
            String str4 = null;
            String str5 = null;
            for (int i = 0; i < attributeList.getLength(); i++) {
                String name = attributeList.getName(i);
                String value = attributeList.getValue(i);
                if (name.equals("default")) {
                    str2 = value;
                } else if (name.equals("name")) {
                    str5 = value;
                } else if (name.equals(Instrumentation.REPORT_KEY_IDENTIFIER)) {
                    str3 = value;
                } else {
                    if (!name.equals(MagicNames.PROJECT_BASEDIR)) {
                        throw new SAXParseException("Unexpected attribute \"" + attributeList.getName(i) + "\"", this.helperImpl.locator);
                    }
                    str4 = value;
                }
            }
            if (str2 != null && !str2.equals("")) {
                this.helperImpl.project.setDefault(str2);
                if (str5 != null) {
                    this.helperImpl.project.setName(str5);
                    this.helperImpl.project.addReference(str5, this.helperImpl.project);
                }
                if (str3 != null) {
                    this.helperImpl.project.addReference(str3, this.helperImpl.project);
                }
                if (this.helperImpl.project.getProperty(MagicNames.PROJECT_BASEDIR) != null) {
                    this.helperImpl.project.setBasedir(this.helperImpl.project.getProperty(MagicNames.PROJECT_BASEDIR));
                } else if (str4 == null) {
                    this.helperImpl.project.setBasedir(this.helperImpl.buildFileParent.getAbsolutePath());
                } else if (new File(str4).isAbsolute()) {
                    this.helperImpl.project.setBasedir(str4);
                } else {
                    this.helperImpl.project.setBaseDir(ProjectHelperImpl.FILE_UTILS.resolveFile(this.helperImpl.buildFileParent, str4));
                }
                this.helperImpl.project.addTarget("", this.helperImpl.implicitTarget);
                return;
            }
            throw new BuildException("The default attribute is required");
        }

        @Override // org.apache.tools.ant.helper.ProjectHelperImpl.AbstractHandler, org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
        public void startElement(String str, AttributeList attributeList) throws SAXParseException {
            if (!str.equals("target")) {
                ProjectHelperImpl.handleElement(this.helperImpl, this, this.helperImpl.implicitTarget, str, attributeList);
            } else {
                handleTarget(str, attributeList);
            }
        }

        private void handleTarget(String str, AttributeList attributeList) throws SAXParseException {
            new TargetHandler(this.helperImpl, this).init(str, attributeList);
        }
    }

    static class TargetHandler extends AbstractHandler {
        private Target target;

        public TargetHandler(ProjectHelperImpl projectHelperImpl, DocumentHandler documentHandler) {
            super(projectHelperImpl, documentHandler);
        }

        public void init(String str, AttributeList attributeList) throws SAXParseException {
            String str2 = null;
            String str3 = "";
            String str4 = null;
            String str5 = null;
            String str6 = null;
            String str7 = null;
            for (int i = 0; i < attributeList.getLength(); i++) {
                String name = attributeList.getName(i);
                String value = attributeList.getValue(i);
                if (name.equals("name")) {
                    if (value.equals("")) {
                        throw new BuildException("name attribute must not be empty", new Location(this.helperImpl.locator));
                    }
                    str2 = value;
                } else if (name.equals("depends")) {
                    str3 = value;
                } else if (name.equals("if")) {
                    str7 = value;
                } else if (name.equals("unless")) {
                    str4 = value;
                } else if (name.equals(Instrumentation.REPORT_KEY_IDENTIFIER)) {
                    str6 = value;
                } else {
                    if (!name.equals("description")) {
                        throw new SAXParseException("Unexpected attribute \"" + name + "\"", this.helperImpl.locator);
                    }
                    str5 = value;
                }
            }
            if (str2 == null) {
                throw new SAXParseException("target element appears without a name attribute", this.helperImpl.locator);
            }
            Target target = new Target();
            this.target = target;
            target.addDependency("");
            this.target.setName(str2);
            this.target.setIf(str7);
            this.target.setUnless(str4);
            this.target.setDescription(str5);
            this.helperImpl.project.addTarget(str2, this.target);
            if (str6 != null && !str6.equals("")) {
                this.helperImpl.project.addReference(str6, this.target);
            }
            if (str3.length() > 0) {
                this.target.setDepends(str3);
            }
        }

        @Override // org.apache.tools.ant.helper.ProjectHelperImpl.AbstractHandler, org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
        public void startElement(String str, AttributeList attributeList) throws SAXParseException {
            ProjectHelperImpl.handleElement(this.helperImpl, this, this.target, str, attributeList);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void handleElement(ProjectHelperImpl projectHelperImpl, DocumentHandler documentHandler, Target target, String str, AttributeList attributeList) throws SAXParseException {
        if (str.equals("description")) {
            new DescriptionHandler(projectHelperImpl, documentHandler);
        } else if (projectHelperImpl.project.getDataTypeDefinitions().get(str) != null) {
            new DataTypeHandler(projectHelperImpl, documentHandler, target).init(str, attributeList);
        } else {
            new TaskHandler(projectHelperImpl, documentHandler, target, null, target).init(str, attributeList);
        }
    }

    static class DescriptionHandler extends AbstractHandler {
        public DescriptionHandler(ProjectHelperImpl projectHelperImpl, DocumentHandler documentHandler) {
            super(projectHelperImpl, documentHandler);
        }

        @Override // org.apache.tools.ant.helper.ProjectHelperImpl.AbstractHandler, org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
        public void characters(char[] cArr, int i, int i2) {
            String str = new String(cArr, i, i2);
            String description = this.helperImpl.project.getDescription();
            if (description == null) {
                this.helperImpl.project.setDescription(str);
            } else {
                this.helperImpl.project.setDescription(description + str);
            }
        }
    }

    static class TaskHandler extends AbstractHandler {
        private TaskContainer container;
        private RuntimeConfigurable parentWrapper;
        private Target target;
        private Task task;
        private RuntimeConfigurable wrapper;

        public TaskHandler(ProjectHelperImpl projectHelperImpl, DocumentHandler documentHandler, TaskContainer taskContainer, RuntimeConfigurable runtimeConfigurable, Target target) {
            super(projectHelperImpl, documentHandler);
            this.wrapper = null;
            this.container = taskContainer;
            this.parentWrapper = runtimeConfigurable;
            this.target = target;
        }

        public void init(String str, AttributeList attributeList) throws SAXParseException {
            try {
                this.task = this.helperImpl.project.createTask(str);
            } catch (BuildException unused) {
            }
            if (this.task == null) {
                UnknownElement unknownElement = new UnknownElement(str);
                this.task = unknownElement;
                unknownElement.setProject(this.helperImpl.project);
                this.task.setTaskName(str);
            }
            this.task.setLocation(new Location(this.helperImpl.locator));
            this.helperImpl.configureId(this.task, attributeList);
            this.task.setOwningTarget(this.target);
            this.container.addTask(this.task);
            this.task.init();
            RuntimeConfigurable runtimeConfigurableWrapper = this.task.getRuntimeConfigurableWrapper();
            this.wrapper = runtimeConfigurableWrapper;
            runtimeConfigurableWrapper.setAttributes(attributeList);
            RuntimeConfigurable runtimeConfigurable = this.parentWrapper;
            if (runtimeConfigurable != null) {
                runtimeConfigurable.addChild(this.wrapper);
            }
        }

        @Override // org.apache.tools.ant.helper.ProjectHelperImpl.AbstractHandler, org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
        public void characters(char[] cArr, int i, int i2) {
            this.wrapper.addText(cArr, i, i2);
        }

        @Override // org.apache.tools.ant.helper.ProjectHelperImpl.AbstractHandler, org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
        public void startElement(String str, AttributeList attributeList) throws SAXParseException {
            if (this.task instanceof TaskContainer) {
                new TaskHandler(this.helperImpl, this, (TaskContainer) this.task, this.wrapper, this.target).init(str, attributeList);
            } else {
                new NestedElementHandler(this.helperImpl, this, this.task, this.wrapper, this.target).init(str, attributeList);
            }
        }
    }

    static class NestedElementHandler extends AbstractHandler {
        private Object child;
        private RuntimeConfigurable childWrapper;
        private Object parent;
        private RuntimeConfigurable parentWrapper;
        private Target target;

        public NestedElementHandler(ProjectHelperImpl projectHelperImpl, DocumentHandler documentHandler, Object obj, RuntimeConfigurable runtimeConfigurable, Target target) {
            super(projectHelperImpl, documentHandler);
            this.childWrapper = null;
            if (obj instanceof TypeAdapter) {
                this.parent = ((TypeAdapter) obj).getProxy();
            } else {
                this.parent = obj;
            }
            this.parentWrapper = runtimeConfigurable;
            this.target = target;
        }

        public void init(String str, AttributeList attributeList) throws SAXParseException {
            IntrospectionHelper helper = IntrospectionHelper.getHelper(this.helperImpl.project, this.parent.getClass());
            try {
                String lowerCase = str.toLowerCase(Locale.ENGLISH);
                if (!(this.parent instanceof UnknownElement)) {
                    this.child = helper.createElement(this.helperImpl.project, this.parent, lowerCase);
                } else {
                    UnknownElement unknownElement = new UnknownElement(lowerCase);
                    unknownElement.setProject(this.helperImpl.project);
                    ((UnknownElement) this.parent).addChild(unknownElement);
                    this.child = unknownElement;
                }
                this.helperImpl.configureId(this.child, attributeList);
                RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(this.child, str);
                this.childWrapper = runtimeConfigurable;
                runtimeConfigurable.setAttributes(attributeList);
                this.parentWrapper.addChild(this.childWrapper);
            } catch (BuildException e) {
                throw new SAXParseException(e.getMessage(), this.helperImpl.locator, e);
            }
        }

        @Override // org.apache.tools.ant.helper.ProjectHelperImpl.AbstractHandler, org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
        public void characters(char[] cArr, int i, int i2) {
            this.childWrapper.addText(cArr, i, i2);
        }

        @Override // org.apache.tools.ant.helper.ProjectHelperImpl.AbstractHandler, org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
        public void startElement(String str, AttributeList attributeList) throws SAXParseException {
            if (this.child instanceof TaskContainer) {
                new TaskHandler(this.helperImpl, this, (TaskContainer) this.child, this.childWrapper, this.target).init(str, attributeList);
            } else {
                new NestedElementHandler(this.helperImpl, this, this.child, this.childWrapper, this.target).init(str, attributeList);
            }
        }
    }

    static class DataTypeHandler extends AbstractHandler {
        private Object element;
        private Target target;
        private RuntimeConfigurable wrapper;

        public DataTypeHandler(ProjectHelperImpl projectHelperImpl, DocumentHandler documentHandler, Target target) {
            super(projectHelperImpl, documentHandler);
            this.wrapper = null;
            this.target = target;
        }

        public void init(String str, AttributeList attributeList) throws SAXParseException {
            try {
                Object objCreateDataType = this.helperImpl.project.createDataType(str);
                this.element = objCreateDataType;
                if (objCreateDataType == null) {
                    throw new BuildException("Unknown data type " + str);
                }
                RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(this.element, str);
                this.wrapper = runtimeConfigurable;
                runtimeConfigurable.setAttributes(attributeList);
                this.target.addDataType(this.wrapper);
            } catch (BuildException e) {
                throw new SAXParseException(e.getMessage(), this.helperImpl.locator, e);
            }
        }

        @Override // org.apache.tools.ant.helper.ProjectHelperImpl.AbstractHandler, org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
        public void characters(char[] cArr, int i, int i2) {
            this.wrapper.addText(cArr, i, i2);
        }

        @Override // org.apache.tools.ant.helper.ProjectHelperImpl.AbstractHandler, org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
        public void startElement(String str, AttributeList attributeList) throws SAXParseException {
            new NestedElementHandler(this.helperImpl, this, this.element, this.wrapper, this.target).init(str, attributeList);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void configureId(Object obj, AttributeList attributeList) {
        String value = attributeList.getValue(Instrumentation.REPORT_KEY_IDENTIFIER);
        if (value != null) {
            this.project.addReference(value, obj);
        }
    }
}
