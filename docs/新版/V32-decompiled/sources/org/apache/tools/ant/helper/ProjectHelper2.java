package org.apache.tools.ant.helper;

import android.app.Instrumentation;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ExtensionPoint;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.URLProvider;
import org.apache.tools.ant.util.FileUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/* JADX INFO: loaded from: classes3.dex */
public class ProjectHelper2 extends ProjectHelper {
    private static final String REFID_CONTEXT = "ant.parsing.context";
    public static final String REFID_TARGETS = "ant.targets";
    private static AntHandler elementHandler = new ElementHandler();
    private static AntHandler targetHandler = new TargetHandler();
    private static AntHandler mainHandler = new MainHandler();
    private static AntHandler projectHandler = new ProjectHandler();
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();

    @Override // org.apache.tools.ant.ProjectHelper
    public boolean canParseAntlibDescriptor(Resource resource) {
        return true;
    }

    @Override // org.apache.tools.ant.ProjectHelper
    public UnknownElement parseAntlibDescriptor(Project project, Resource resource) {
        URLProvider uRLProvider = (URLProvider) resource.as(URLProvider.class);
        if (uRLProvider == null) {
            throw new BuildException("Unsupported resource type: " + resource);
        }
        return parseUnknownElement(project, uRLProvider.getURL());
    }

    public UnknownElement parseUnknownElement(Project project, URL url) throws Throwable {
        Target target = new Target();
        target.setProject(project);
        AntXMLContext antXMLContext = new AntXMLContext(project);
        antXMLContext.addTarget(target);
        antXMLContext.setImplicitTarget(target);
        parse(antXMLContext.getProject(), url, new RootHandler(antXMLContext, elementHandler));
        Task[] tasks = target.getTasks();
        if (tasks.length != 1) {
            throw new BuildException("No tasks defined");
        }
        return (UnknownElement) tasks[0];
    }

    @Override // org.apache.tools.ant.ProjectHelper
    public void parse(Project project, Object obj) throws Throwable {
        getImportStack().addElement(obj);
        AntXMLContext antXMLContext = (AntXMLContext) project.getReference(REFID_CONTEXT);
        if (antXMLContext == null) {
            antXMLContext = new AntXMLContext(project);
            project.addReference(REFID_CONTEXT, antXMLContext);
            project.addReference(REFID_TARGETS, antXMLContext.getTargets());
        }
        if (getImportStack().size() > 1) {
            antXMLContext.setIgnoreProjectTag(true);
            Target currentTarget = antXMLContext.getCurrentTarget();
            Target implicitTarget = antXMLContext.getImplicitTarget();
            Map<String, Target> currentTargets = antXMLContext.getCurrentTargets();
            try {
                Target target = new Target();
                target.setProject(project);
                target.setName("");
                antXMLContext.setCurrentTarget(target);
                antXMLContext.setCurrentTargets(new HashMap());
                antXMLContext.setImplicitTarget(target);
                parse(project, obj, new RootHandler(antXMLContext, mainHandler));
                target.execute();
                return;
            } finally {
                antXMLContext.setCurrentTarget(currentTarget);
                antXMLContext.setImplicitTarget(implicitTarget);
                antXMLContext.setCurrentTargets(currentTargets);
            }
        }
        antXMLContext.setCurrentTargets(new HashMap());
        parse(project, obj, new RootHandler(antXMLContext, mainHandler));
        antXMLContext.getImplicitTarget().execute();
        resolveExtensionOfAttributes(project);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0041  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0053  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0068 A[Catch: all -> 0x0110, IOException -> 0x0114, UnsupportedEncodingException -> 0x013d, FileNotFoundException -> 0x015e, SAXException -> 0x0166, SAXParseException -> 0x0181, TryCatch #5 {FileNotFoundException -> 0x015e, UnsupportedEncodingException -> 0x013d, IOException -> 0x0114, SAXParseException -> 0x0181, SAXException -> 0x0166, all -> 0x0110, blocks: (B:24:0x0062, B:26:0x0068, B:27:0x007b, B:29:0x0087, B:31:0x0090, B:35:0x00aa), top: B:107:0x0062 }] */
    /* JADX WARN: Removed duplicated region for block: B:27:0x007b A[Catch: all -> 0x0110, IOException -> 0x0114, UnsupportedEncodingException -> 0x013d, FileNotFoundException -> 0x015e, SAXException -> 0x0166, SAXParseException -> 0x0181, TryCatch #5 {FileNotFoundException -> 0x015e, UnsupportedEncodingException -> 0x013d, IOException -> 0x0114, SAXParseException -> 0x0181, SAXException -> 0x0166, all -> 0x0110, blocks: (B:24:0x0062, B:26:0x0068, B:27:0x007b, B:29:0x0087, B:31:0x0090, B:35:0x00aa), top: B:107:0x0062 }] */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00c0 A[Catch: IOException -> 0x0105, UnsupportedEncodingException -> 0x0107, FileNotFoundException -> 0x0109, SAXException -> 0x010b, SAXParseException -> 0x010d, all -> 0x01b8, TryCatch #8 {all -> 0x01b8, blocks: (B:37:0x00b9, B:39:0x00c0, B:40:0x00c3, B:44:0x00e3, B:33:0x009d, B:61:0x0116, B:62:0x013c, B:65:0x013f, B:66:0x015d, B:69:0x0160, B:70:0x0165, B:73:0x0168, B:75:0x0170, B:79:0x017a, B:80:0x017d, B:81:0x017e, B:82:0x0180, B:85:0x0183, B:87:0x019c, B:89:0x01a6, B:90:0x01a9, B:91:0x01aa, B:95:0x01b4, B:96:0x01b7), top: B:107:0x0062 }] */
    /* JADX WARN: Removed duplicated region for block: B:42:0x00de  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00e1  */
    /* JADX WARN: Type inference failed for: r3v22 */
    /* JADX WARN: Type inference failed for: r3v6, types: [java.net.URL] */
    /* JADX WARN: Type inference failed for: r3v7 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void parse(org.apache.tools.ant.Project r9, java.lang.Object r10, org.apache.tools.ant.helper.ProjectHelper2.RootHandler r11) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 494
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.helper.ProjectHelper2.parse(org.apache.tools.ant.Project, java.lang.Object, org.apache.tools.ant.helper.ProjectHelper2$RootHandler):void");
    }

    protected static AntHandler getMainHandler() {
        return mainHandler;
    }

    protected static void setMainHandler(AntHandler antHandler) {
        mainHandler = antHandler;
    }

    protected static AntHandler getProjectHandler() {
        return projectHandler;
    }

    protected static void setProjectHandler(AntHandler antHandler) {
        projectHandler = antHandler;
    }

    protected static AntHandler getTargetHandler() {
        return targetHandler;
    }

    protected static void setTargetHandler(AntHandler antHandler) {
        targetHandler = antHandler;
    }

    protected static AntHandler getElementHandler() {
        return elementHandler;
    }

    protected static void setElementHandler(AntHandler antHandler) {
        elementHandler = antHandler;
    }

    public static class AntHandler {
        protected void checkNamespace(String str) {
        }

        public void onEndChild(String str, String str2, String str3, AntXMLContext antXMLContext) throws SAXParseException {
        }

        public void onEndElement(String str, String str2, AntXMLContext antXMLContext) {
        }

        public void onStartElement(String str, String str2, String str3, Attributes attributes, AntXMLContext antXMLContext) throws SAXParseException {
        }

        public AntHandler onStartChild(String str, String str2, String str3, Attributes attributes, AntXMLContext antXMLContext) throws SAXParseException {
            throw new SAXParseException("Unexpected element \"" + str3 + " \"", antXMLContext.getLocator());
        }

        public void characters(char[] cArr, int i, int i2, AntXMLContext antXMLContext) throws SAXParseException {
            String strTrim = new String(cArr, i, i2).trim();
            if (strTrim.length() > 0) {
                throw new SAXParseException("Unexpected text \"" + strTrim + "\"", antXMLContext.getLocator());
            }
        }
    }

    public static class RootHandler extends DefaultHandler {
        private Stack<AntHandler> antHandlers;
        private AntXMLContext context;
        private AntHandler currentHandler;

        public RootHandler(AntXMLContext antXMLContext, AntHandler antHandler) {
            Stack<AntHandler> stack = new Stack<>();
            this.antHandlers = stack;
            this.currentHandler = null;
            this.currentHandler = antHandler;
            stack.push(antHandler);
            this.context = antXMLContext;
        }

        public AntHandler getCurrentAntHandler() {
            return this.currentHandler;
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.EntityResolver
        public InputSource resolveEntity(String str, String str2) {
            this.context.getProject().log("resolving systemId: " + str2, 3);
            if (str2.startsWith("file:")) {
                String strFromURI = ProjectHelper2.FILE_UTILS.fromURI(str2);
                File file = new File(strFromURI);
                if (!file.isAbsolute()) {
                    file = ProjectHelper2.FILE_UTILS.resolveFile(this.context.getBuildFileParent(), strFromURI);
                    this.context.getProject().log("Warning: '" + str2 + "' in " + this.context.getBuildFile() + " should be expressed simply as '" + strFromURI.replace('\\', '/') + "' for compliance with other XML tools", 1);
                }
                this.context.getProject().log("file=" + file, 4);
                try {
                    InputSource inputSource = new InputSource(new FileInputStream(file));
                    inputSource.setSystemId(ProjectHelper2.FILE_UTILS.toURI(file.getAbsolutePath()));
                    return inputSource;
                } catch (FileNotFoundException unused) {
                    this.context.getProject().log(file.getAbsolutePath() + " could not be found", 1);
                }
            }
            this.context.getProject().log("could not resolve systemId", 4);
            return null;
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void startElement(String str, String str2, String str3, Attributes attributes) throws SAXParseException {
            AntHandler antHandlerOnStartChild = this.currentHandler.onStartChild(str, str2, str3, attributes, this.context);
            this.antHandlers.push(this.currentHandler);
            this.currentHandler = antHandlerOnStartChild;
            antHandlerOnStartChild.onStartElement(str, str2, str3, attributes, this.context);
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void setDocumentLocator(Locator locator) {
            this.context.setLocator(locator);
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void endElement(String str, String str2, String str3) throws SAXException {
            this.currentHandler.onEndElement(str, str2, this.context);
            AntHandler antHandlerPop = this.antHandlers.pop();
            this.currentHandler = antHandlerPop;
            if (antHandlerPop != null) {
                antHandlerPop.onEndChild(str, str2, str3, this.context);
            }
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void characters(char[] cArr, int i, int i2) throws SAXParseException {
            this.currentHandler.characters(cArr, i, i2, this.context);
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void startPrefixMapping(String str, String str2) {
            this.context.startPrefixMapping(str, str2);
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void endPrefixMapping(String str) {
            this.context.endPrefixMapping(str);
        }
    }

    public static class MainHandler extends AntHandler {
        @Override // org.apache.tools.ant.helper.ProjectHelper2.AntHandler
        public AntHandler onStartChild(String str, String str2, String str3, Attributes attributes, AntXMLContext antXMLContext) throws SAXParseException {
            if (str2.equals("project") && (str.equals("") || str.equals(ProjectHelper.ANT_CORE_URI))) {
                return ProjectHelper2.projectHandler;
            }
            if (str2.equals(str3)) {
                throw new SAXParseException("Unexpected element \"{" + str + "}" + str2 + "\" {" + ProjectHelper.ANT_CORE_URI + "}" + str2, antXMLContext.getLocator());
            }
            throw new SAXParseException("Unexpected element \"" + str3 + "\" " + str2, antXMLContext.getLocator());
        }
    }

    public static class ProjectHandler extends AntHandler {
        @Override // org.apache.tools.ant.helper.ProjectHelper2.AntHandler
        public void onStartElement(String str, String str2, String str3, Attributes attributes, AntXMLContext antXMLContext) throws SAXParseException {
            Object url;
            Object buildFileURL;
            Project project = antXMLContext.getProject();
            antXMLContext.getImplicitTarget().setLocation(new Location(antXMLContext.getLocator()));
            String str4 = null;
            boolean z = false;
            for (int i = 0; i < attributes.getLength(); i++) {
                String uri = attributes.getURI(i);
                if (uri == null || uri.equals("") || uri.equals(str)) {
                    String localName = attributes.getLocalName(i);
                    String value = attributes.getValue(i);
                    if (localName.equals("default")) {
                        if (value != null && !value.equals("") && !antXMLContext.isIgnoringProjectTag()) {
                            project.setDefault(value);
                        }
                    } else if (localName.equals("name")) {
                        if (value != null) {
                            antXMLContext.setCurrentProjectName(value);
                            if (!antXMLContext.isIgnoringProjectTag()) {
                                project.setName(value);
                                project.addReference(value, project);
                            } else if (ProjectHelper.isInIncludeMode() && !"".equals(value) && ProjectHelper.getCurrentTargetPrefix() != null && ProjectHelper.getCurrentTargetPrefix().endsWith(ProjectHelper.USE_PROJECT_NAME_AS_TARGET_PREFIX)) {
                                ProjectHelper.setCurrentTargetPrefix(ProjectHelper.getCurrentTargetPrefix().replace(ProjectHelper.USE_PROJECT_NAME_AS_TARGET_PREFIX, value));
                            }
                            z = true;
                        }
                    } else if (localName.equals(Instrumentation.REPORT_KEY_IDENTIFIER)) {
                        if (value != null && !antXMLContext.isIgnoringProjectTag()) {
                            project.addReference(value, project);
                        }
                    } else if (localName.equals(MagicNames.PROJECT_BASEDIR)) {
                        if (!antXMLContext.isIgnoringProjectTag()) {
                            str4 = value;
                        }
                    } else {
                        throw new SAXParseException("Unexpected attribute \"" + attributes.getQName(i) + "\"", antXMLContext.getLocator());
                    }
                }
            }
            String str5 = "ant.file." + antXMLContext.getCurrentProjectName();
            String property = project.getProperty(str5);
            String str6 = "ant.file.type." + antXMLContext.getCurrentProjectName();
            String property2 = project.getProperty(str6);
            if (property != null && z) {
                if ("url".equals(property2)) {
                    try {
                        url = new URL(property);
                        buildFileURL = antXMLContext.getBuildFileURL();
                    } catch (MalformedURLException e) {
                        throw new BuildException("failed to parse " + property + " as URL while looking at a duplicate project name.", e);
                    }
                } else {
                    url = new File(property);
                    buildFileURL = antXMLContext.getBuildFile();
                }
                if (antXMLContext.isIgnoringProjectTag() && !url.equals(buildFileURL)) {
                    project.log("Duplicated project name in import. Project " + antXMLContext.getCurrentProjectName() + " defined first in " + property + " and again in " + buildFileURL, 1);
                }
            }
            if (z) {
                if (antXMLContext.getBuildFile() != null) {
                    project.setUserProperty(str5, antXMLContext.getBuildFile().toString());
                    project.setUserProperty(str6, "file");
                } else if (antXMLContext.getBuildFileURL() != null) {
                    project.setUserProperty(str5, antXMLContext.getBuildFileURL().toString());
                    project.setUserProperty(str6, "url");
                }
            }
            if (antXMLContext.isIgnoringProjectTag()) {
                return;
            }
            if (project.getProperty(MagicNames.PROJECT_BASEDIR) != null) {
                project.setBasedir(project.getProperty(MagicNames.PROJECT_BASEDIR));
            } else if (str4 == null) {
                project.setBasedir(antXMLContext.getBuildFileParent().getAbsolutePath());
            } else if (!new File(str4).isAbsolute()) {
                project.setBaseDir(ProjectHelper2.FILE_UTILS.resolveFile(antXMLContext.getBuildFileParent(), str4));
            } else {
                project.setBasedir(str4);
            }
            project.addTarget("", antXMLContext.getImplicitTarget());
            antXMLContext.setCurrentTarget(antXMLContext.getImplicitTarget());
        }

        @Override // org.apache.tools.ant.helper.ProjectHelper2.AntHandler
        public AntHandler onStartChild(String str, String str2, String str3, Attributes attributes, AntXMLContext antXMLContext) throws SAXParseException {
            return ((str2.equals("target") || str2.equals("extension-point")) && (str.equals("") || str.equals(ProjectHelper.ANT_CORE_URI))) ? ProjectHelper2.targetHandler : ProjectHelper2.elementHandler;
        }
    }

    public static class TargetHandler extends AntHandler {
        @Override // org.apache.tools.ant.helper.ProjectHelper2.AntHandler
        public void onStartElement(String str, String str2, String str3, Attributes attributes, AntXMLContext antXMLContext) throws SAXParseException {
            String targetPrefix;
            boolean z;
            Target target;
            String str4;
            Attributes attributes2 = attributes;
            Project project = antXMLContext.getProject();
            Target target2 = "target".equals(str2) ? new Target() : new ExtensionPoint();
            target2.setProject(project);
            target2.setLocation(new Location(antXMLContext.getLocator()));
            antXMLContext.addTarget(target2);
            String str5 = "";
            int i = 0;
            String str6 = null;
            String value = null;
            ProjectHelper.OnMissingExtensionPoint onMissingExtensionPointValueOf = null;
            while (i < attributes.getLength()) {
                String uri = attributes2.getURI(i);
                if (uri == null || uri.equals("")) {
                    str4 = value;
                } else {
                    str4 = value;
                    if (!uri.equals(str)) {
                        value = str4;
                    }
                    i++;
                    attributes2 = attributes;
                }
                String localName = attributes2.getLocalName(i);
                value = attributes2.getValue(i);
                if (localName.equals("name")) {
                    if ("".equals(value)) {
                        throw new BuildException("name attribute must not be empty");
                    }
                    str6 = value;
                } else if (localName.equals("depends")) {
                    str5 = value;
                } else if (localName.equals("if")) {
                    target2.setIf(value);
                } else if (localName.equals("unless")) {
                    target2.setUnless(value);
                } else if (localName.equals(Instrumentation.REPORT_KEY_IDENTIFIER)) {
                    if (value != null && !value.equals("")) {
                        antXMLContext.getProject().addReference(value, target2);
                    }
                } else if (localName.equals("description")) {
                    target2.setDescription(value);
                } else if (localName.equals("extensionOf")) {
                    continue;
                    i++;
                    attributes2 = attributes;
                } else if (localName.equals("onMissingExtensionPoint")) {
                    try {
                        onMissingExtensionPointValueOf = ProjectHelper.OnMissingExtensionPoint.valueOf(value);
                    } catch (IllegalArgumentException unused) {
                        throw new BuildException("Invalid onMissingExtensionPoint " + value);
                    }
                } else {
                    throw new SAXParseException("Unexpected attribute \"" + localName + "\"", antXMLContext.getLocator());
                }
                value = str4;
                i++;
                attributes2 = attributes;
            }
            String str7 = value;
            if (str6 == null) {
                throw new SAXParseException("target element appears without a name attribute", antXMLContext.getLocator());
            }
            boolean z2 = antXMLContext.isIgnoringProjectTag() && ProjectHelper.isInIncludeMode();
            String currentPrefixSeparator = ProjectHelper.getCurrentPrefixSeparator();
            if (z2) {
                targetPrefix = getTargetPrefix(antXMLContext);
                if (targetPrefix == null) {
                    throw new BuildException("can't include build file " + antXMLContext.getBuildFileURL() + ", no as attribute has been given and the project tag doesn't specify a name attribute");
                }
                str6 = targetPrefix + currentPrefixSeparator + str6;
            } else {
                targetPrefix = null;
            }
            if (antXMLContext.getCurrentTargets().get(str6) != null) {
                throw new BuildException("Duplicate target '" + str6 + "'", target2.getLocation());
            }
            if (project.getTargets().containsKey(str6)) {
                project.log("Already defined in main or a previous import, ignore " + str6, 3);
                z = false;
            } else {
                target2.setName(str6);
                antXMLContext.getCurrentTargets().put(str6, target2);
                project.addOrReplaceTarget(str6, target2);
                z = true;
            }
            if (str5.length() > 0) {
                if (!z2) {
                    target2.setDepends(str5);
                } else {
                    Iterator<String> it = Target.parseDepends(str5, str6, "depends").iterator();
                    while (it.hasNext()) {
                        target2.addDependency(targetPrefix + currentPrefixSeparator + it.next());
                    }
                }
            }
            if (!z2 && antXMLContext.isIgnoringProjectTag() && (targetPrefix = getTargetPrefix(antXMLContext)) != null) {
                String str8 = targetPrefix + currentPrefixSeparator + str6;
                if (z) {
                    target = "target".equals(str2) ? new Target(target2) : new ExtensionPoint(target2);
                } else {
                    target = target2;
                }
                target.setName(str8);
                antXMLContext.getCurrentTargets().put(str8, target);
                project.addOrReplaceTarget(str8, target);
            }
            if (onMissingExtensionPointValueOf != null && str7 == null) {
                throw new BuildException("onMissingExtensionPoint attribute cannot be specified unless extensionOf is specified", target2.getLocation());
            }
            if (str7 != null) {
                ProjectHelper projectHelper = (ProjectHelper) antXMLContext.getProject().getReference("ant.projectHelper");
                for (String str9 : Target.parseDepends(str7, str6, "extensionOf")) {
                    if (onMissingExtensionPointValueOf == null) {
                        onMissingExtensionPointValueOf = ProjectHelper.OnMissingExtensionPoint.FAIL;
                    }
                    if (ProjectHelper.isInIncludeMode()) {
                        projectHelper.getExtensionStack().add(new String[]{str9, target2.getName(), onMissingExtensionPointValueOf.name(), targetPrefix + currentPrefixSeparator});
                    } else {
                        projectHelper.getExtensionStack().add(new String[]{str9, target2.getName(), onMissingExtensionPointValueOf.name()});
                    }
                }
            }
        }

        private String getTargetPrefix(AntXMLContext antXMLContext) {
            String currentTargetPrefix = ProjectHelper.getCurrentTargetPrefix();
            if (currentTargetPrefix != null && currentTargetPrefix.length() == 0) {
                currentTargetPrefix = null;
            }
            if (currentTargetPrefix != null) {
                return currentTargetPrefix;
            }
            String currentProjectName = antXMLContext.getCurrentProjectName();
            if ("".equals(currentProjectName)) {
                return null;
            }
            return currentProjectName;
        }

        @Override // org.apache.tools.ant.helper.ProjectHelper2.AntHandler
        public AntHandler onStartChild(String str, String str2, String str3, Attributes attributes, AntXMLContext antXMLContext) throws SAXParseException {
            return ProjectHelper2.elementHandler;
        }

        @Override // org.apache.tools.ant.helper.ProjectHelper2.AntHandler
        public void onEndElement(String str, String str2, AntXMLContext antXMLContext) {
            antXMLContext.setCurrentTarget(antXMLContext.getImplicitTarget());
        }
    }

    public static class ElementHandler extends AntHandler {
        @Override // org.apache.tools.ant.helper.ProjectHelper2.AntHandler
        public void onStartElement(String str, String str2, String str3, Attributes attributes, AntXMLContext antXMLContext) throws SAXParseException {
            RuntimeConfigurable runtimeConfigurableCurrentWrapper = antXMLContext.currentWrapper();
            Object proxy = runtimeConfigurableCurrentWrapper != null ? runtimeConfigurableCurrentWrapper.getProxy() : null;
            UnknownElement unknownElement = new UnknownElement(str2);
            unknownElement.setProject(antXMLContext.getProject());
            unknownElement.setNamespace(str);
            unknownElement.setQName(str3);
            unknownElement.setTaskType(ProjectHelper.genComponentName(unknownElement.getNamespace(), str2));
            unknownElement.setTaskName(str3);
            unknownElement.setLocation(new Location(antXMLContext.getLocator().getSystemId(), antXMLContext.getLocator().getLineNumber(), antXMLContext.getLocator().getColumnNumber()));
            unknownElement.setOwningTarget(antXMLContext.getCurrentTarget());
            if (proxy != null) {
                ((UnknownElement) proxy).addChild(unknownElement);
            } else {
                antXMLContext.getCurrentTarget().addTask(unknownElement);
            }
            antXMLContext.configureId(unknownElement, attributes);
            RuntimeConfigurable runtimeConfigurable = new RuntimeConfigurable(unknownElement, unknownElement.getTaskName());
            for (int i = 0; i < attributes.getLength(); i++) {
                String localName = attributes.getLocalName(i);
                String uri = attributes.getURI(i);
                if (uri != null && !uri.equals("") && !uri.equals(str)) {
                    localName = uri + ":" + attributes.getQName(i);
                }
                String value = attributes.getValue(i);
                if (ProjectHelper.ANT_TYPE.equals(localName) || (ProjectHelper.ANT_CORE_URI.equals(uri) && ProjectHelper.ANT_TYPE.equals(attributes.getLocalName(i)))) {
                    int iIndexOf = value.indexOf(":");
                    if (iIndexOf >= 0) {
                        String strSubstring = value.substring(0, iIndexOf);
                        String prefixMapping = antXMLContext.getPrefixMapping(strSubstring);
                        if (prefixMapping == null) {
                            throw new BuildException("Unable to find XML NS prefix \"" + strSubstring + "\"");
                        }
                        value = ProjectHelper.genComponentName(prefixMapping, value.substring(iIndexOf + 1));
                    }
                    localName = ProjectHelper.ANT_TYPE;
                }
                runtimeConfigurable.setAttribute(localName, value);
            }
            if (runtimeConfigurableCurrentWrapper != null) {
                runtimeConfigurableCurrentWrapper.addChild(runtimeConfigurable);
            }
            antXMLContext.pushWrapper(runtimeConfigurable);
        }

        @Override // org.apache.tools.ant.helper.ProjectHelper2.AntHandler
        public void characters(char[] cArr, int i, int i2, AntXMLContext antXMLContext) throws SAXParseException {
            antXMLContext.currentWrapper().addText(cArr, i, i2);
        }

        @Override // org.apache.tools.ant.helper.ProjectHelper2.AntHandler
        public AntHandler onStartChild(String str, String str2, String str3, Attributes attributes, AntXMLContext antXMLContext) throws SAXParseException {
            return ProjectHelper2.elementHandler;
        }

        @Override // org.apache.tools.ant.helper.ProjectHelper2.AntHandler
        public void onEndElement(String str, String str2, AntXMLContext antXMLContext) {
            antXMLContext.popWrapper();
        }
    }
}
