package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathVariableResolver;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.DynamicConfigurator;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.taskdefs.optional.TraXLiaison;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.XMLCatalog;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.ResourceUtils;

/* JADX INFO: loaded from: classes3.dex */
public class XSLTProcess extends MatchingTask implements XSLTLogger {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    public static final String PROCESSOR_TRAX = "trax";
    private XSLTLiaison liaison;
    private String processor;
    private TraceConfiguration traceConfiguration;
    private XPath xpath;
    private XPathFactory xpathFactory;
    private File destDir = null;
    private File baseDir = null;
    private String xslFile = null;
    private Resource xslResource = null;
    private String targetExtension = ".html";
    private String fileNameParameter = null;
    private String fileDirParameter = null;
    private List<Param> params = new ArrayList();
    private File inFile = null;
    private File outFile = null;
    private Path classpath = null;
    private boolean stylesheetLoaded = false;
    private boolean force = false;
    private Vector outputProperties = new Vector();
    private XMLCatalog xmlCatalog = new XMLCatalog();
    private boolean performDirectoryScan = true;
    private Factory factory = null;
    private boolean reuseLoadedStylesheet = true;
    private AntClassLoader loader = null;
    private Mapper mapperElement = null;
    private Union resources = new Union();
    private boolean useImplicitFileset = true;
    private boolean suppressWarnings = false;
    private boolean failOnTransformationError = true;
    private boolean failOnError = true;
    private boolean failOnNoResources = true;
    private CommandlineJava.SysProperties sysProperties = new CommandlineJava.SysProperties();

    public void setScanIncludedDirectories(boolean z) {
        this.performDirectoryScan = z;
    }

    public void setReloadStylesheet(boolean z) {
        this.reuseLoadedStylesheet = !z;
    }

    public void addMapper(Mapper mapper) {
        if (this.mapperElement != null) {
            handleError(Expand.ERROR_MULTIPLE_MAPPERS);
        } else {
            this.mapperElement = mapper;
        }
    }

    public void add(ResourceCollection resourceCollection) {
        this.resources.add(resourceCollection);
    }

    public void addConfiguredStyle(Resources resources) {
        if (resources.size() != 1) {
            handleError("The style element must be specified with exactly one nested resource.");
        } else {
            setXslResource(resources.iterator().next());
        }
    }

    public void setXslResource(Resource resource) {
        this.xslResource = resource;
    }

    public void add(FileNameMapper fileNameMapper) throws BuildException {
        Mapper mapper = new Mapper(getProject());
        mapper.add(fileNameMapper);
        addMapper(mapper);
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        Resource resource;
        File file;
        if ("style".equals(getTaskType())) {
            log("Warning: the task name <style> is deprecated. Use <xslt> instead.", 1);
        }
        File file2 = this.baseDir;
        Resource resource2 = this.xslResource;
        if (resource2 == null && this.xslFile == null) {
            handleError("specify the stylesheet either as a filename in style attribute or as a nested resource");
            return;
        }
        if (resource2 != null && this.xslFile != null) {
            handleError("specify the stylesheet either as a filename in style attribute or as a nested resource but not as both");
            return;
        }
        File file3 = this.inFile;
        if (file3 != null && !file3.exists()) {
            handleError("input file " + this.inFile + " does not exist");
            return;
        }
        try {
            setupLoader();
            if (this.sysProperties.size() > 0) {
                this.sysProperties.setSystem();
            }
            if (this.baseDir == null) {
                this.baseDir = getProject().getBaseDir();
            }
            XSLTLiaison liaison = getLiaison();
            this.liaison = liaison;
            if (liaison instanceof XSLTLoggerAware) {
                ((XSLTLoggerAware) liaison).setLogger(this);
            }
            log("Using " + this.liaison.getClass().toString(), 3);
            if (this.xslFile != null) {
                File fileResolveFile = getProject().resolveFile(this.xslFile);
                if (!fileResolveFile.exists()) {
                    File fileResolveFile2 = FILE_UTILS.resolveFile(this.baseDir, this.xslFile);
                    if (fileResolveFile2.exists()) {
                        log("DEPRECATED - the 'style' attribute should be relative to the project's");
                        log("             basedir, not the tasks's basedir.");
                        fileResolveFile = fileResolveFile2;
                    }
                }
                FileResource fileResource = new FileResource();
                fileResource.setProject(getProject());
                fileResource.setFile(fileResolveFile);
                resource = fileResource;
            } else {
                resource = this.xslResource;
            }
            if (!resource.isExists()) {
                handleError("stylesheet " + resource + " doesn't exist.");
                AntClassLoader antClassLoader = this.loader;
                if (antClassLoader != null) {
                    antClassLoader.resetThreadContextLoader();
                    this.loader.cleanup();
                    this.loader = null;
                }
                if (this.sysProperties.size() > 0) {
                    this.sysProperties.restoreSystem();
                }
                this.liaison = null;
                this.stylesheetLoaded = false;
                this.baseDir = file2;
                return;
            }
            File file4 = this.inFile;
            if (file4 != null && (file = this.outFile) != null) {
                process(file4, file, resource);
                AntClassLoader antClassLoader2 = this.loader;
                if (antClassLoader2 != null) {
                    antClassLoader2.resetThreadContextLoader();
                    this.loader.cleanup();
                    this.loader = null;
                }
                if (this.sysProperties.size() > 0) {
                    this.sysProperties.restoreSystem();
                }
                this.liaison = null;
                this.stylesheetLoaded = false;
                this.baseDir = file2;
                return;
            }
            checkDest();
            if (this.useImplicitFileset) {
                DirectoryScanner directoryScanner = getDirectoryScanner(this.baseDir);
                log("Transforming into " + this.destDir, 2);
                for (String str : directoryScanner.getIncludedFiles()) {
                    process(this.baseDir, str, this.destDir, resource);
                }
                if (this.performDirectoryScan) {
                    String[] includedDirectories = directoryScanner.getIncludedDirectories();
                    for (int i = 0; i < includedDirectories.length; i++) {
                        for (String str2 : new File(this.baseDir, includedDirectories[i]).list()) {
                            process(this.baseDir, includedDirectories[i] + File.separator + str2, this.destDir, resource);
                        }
                    }
                }
            } else if (this.resources.size() == 0) {
                if (this.failOnNoResources) {
                    handleError("no resources specified");
                }
                AntClassLoader antClassLoader3 = this.loader;
                if (antClassLoader3 != null) {
                    antClassLoader3.resetThreadContextLoader();
                    this.loader.cleanup();
                    this.loader = null;
                }
                if (this.sysProperties.size() > 0) {
                    this.sysProperties.restoreSystem();
                }
                this.liaison = null;
                this.stylesheetLoaded = false;
                this.baseDir = file2;
                return;
            }
            processResources(resource);
            AntClassLoader antClassLoader4 = this.loader;
            if (antClassLoader4 != null) {
                antClassLoader4.resetThreadContextLoader();
                this.loader.cleanup();
                this.loader = null;
            }
            if (this.sysProperties.size() > 0) {
                this.sysProperties.restoreSystem();
            }
            this.liaison = null;
            this.stylesheetLoaded = false;
            this.baseDir = file2;
        } catch (Throwable th) {
            AntClassLoader antClassLoader5 = this.loader;
            if (antClassLoader5 != null) {
                antClassLoader5.resetThreadContextLoader();
                this.loader.cleanup();
                this.loader = null;
            }
            if (this.sysProperties.size() > 0) {
                this.sysProperties.restoreSystem();
            }
            this.liaison = null;
            this.stylesheetLoaded = false;
            this.baseDir = file2;
            throw th;
        }
    }

    public void setForce(boolean z) {
        this.force = z;
    }

    public void setBasedir(File file) {
        this.baseDir = file;
    }

    public void setDestdir(File file) {
        this.destDir = file;
    }

    public void setExtension(String str) {
        this.targetExtension = str;
    }

    public void setStyle(String str) {
        this.xslFile = str;
    }

    public void setClasspath(Path path) {
        createClasspath().append(path);
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(getProject());
        }
        return this.classpath.createPath();
    }

    public void setClasspathRef(Reference reference) {
        createClasspath().setRefid(reference);
    }

    public void setProcessor(String str) {
        this.processor = str;
    }

    public void setUseImplicitFileset(boolean z) {
        this.useImplicitFileset = z;
    }

    public void addConfiguredXMLCatalog(XMLCatalog xMLCatalog) {
        this.xmlCatalog.addConfiguredXMLCatalog(xMLCatalog);
    }

    public void setFileNameParameter(String str) {
        this.fileNameParameter = str;
    }

    public void setFileDirParameter(String str) {
        this.fileDirParameter = str;
    }

    public void setSuppressWarnings(boolean z) {
        this.suppressWarnings = z;
    }

    public boolean getSuppressWarnings() {
        return this.suppressWarnings;
    }

    public void setFailOnTransformationError(boolean z) {
        this.failOnTransformationError = z;
    }

    public void setFailOnError(boolean z) {
        this.failOnError = z;
    }

    public void setFailOnNoResources(boolean z) {
        this.failOnNoResources = z;
    }

    public void addSysproperty(Environment.Variable variable) {
        this.sysProperties.addVariable(variable);
    }

    public void addSyspropertyset(PropertySet propertySet) {
        this.sysProperties.addSyspropertyset(propertySet);
    }

    public TraceConfiguration createTrace() {
        if (this.traceConfiguration != null) {
            throw new BuildException("can't have more than one trace configuration");
        }
        TraceConfiguration traceConfiguration = new TraceConfiguration();
        this.traceConfiguration = traceConfiguration;
        return traceConfiguration;
    }

    public TraceConfiguration getTraceConfiguration() {
        return this.traceConfiguration;
    }

    private void resolveProcessor(String str) throws Exception {
        if (str.equals(PROCESSOR_TRAX)) {
            this.liaison = new TraXLiaison();
        } else {
            this.liaison = (XSLTLiaison) loadClass(str).newInstance();
        }
    }

    private Class loadClass(String str) throws Exception {
        setupLoader();
        AntClassLoader antClassLoader = this.loader;
        if (antClassLoader == null) {
            return Class.forName(str);
        }
        return Class.forName(str, true, antClassLoader);
    }

    private void setupLoader() {
        if (this.classpath == null || this.loader != null) {
            return;
        }
        AntClassLoader antClassLoaderCreateClassLoader = getProject().createClassLoader(this.classpath);
        this.loader = antClassLoaderCreateClassLoader;
        antClassLoaderCreateClassLoader.setThreadContextLoader();
    }

    public void setOut(File file) {
        this.outFile = file;
    }

    public void setIn(File file) {
        this.inFile = file;
    }

    private void checkDest() {
        if (this.destDir == null) {
            handleError("destdir attributes must be set!");
        }
    }

    private void processResources(Resource resource) {
        FileResource fileResourceAsFileResource;
        for (Resource resource2 : this.resources) {
            if (resource2.isExists()) {
                File baseDir = this.baseDir;
                String name = resource2.getName();
                FileProvider fileProvider = (FileProvider) resource2.as(FileProvider.class);
                if (fileProvider != null && (baseDir = (fileResourceAsFileResource = ResourceUtils.asFileResource(fileProvider)).getBaseDir()) == null) {
                    name = fileResourceAsFileResource.getFile().getAbsolutePath();
                }
                process(baseDir, name, this.destDir, resource);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void process(File file, String str, File file2, Resource resource) throws BuildException {
        FileNameMapper styleMapper;
        File file3 = null;
        Object[] objArr = 0;
        try {
            long lastModified = resource.getLastModified();
            File file4 = new File(file, str);
            if (file4.isDirectory()) {
                log("Skipping " + file4 + " it is a directory.", 3);
                return;
            }
            Mapper mapper = this.mapperElement;
            if (mapper != null) {
                styleMapper = mapper.getImplementation();
            } else {
                styleMapper = new StyleMapper();
            }
            String[] strArrMapFileName = styleMapper.mapFileName(str);
            if (strArrMapFileName != null && strArrMapFileName.length != 0) {
                if (strArrMapFileName != null && strArrMapFileName.length <= 1) {
                    File file5 = new File(file2, strArrMapFileName[0]);
                    try {
                        if (this.force || file4.lastModified() > file5.lastModified() || lastModified > file5.lastModified()) {
                            ensureDirectoryFor(file5);
                            log("Processing " + file4 + " to " + file5);
                            configureLiaison(resource);
                            setLiaisonDynamicFileParameters(this.liaison, file4);
                            this.liaison.transform(file4, file5);
                            return;
                        }
                        return;
                    } catch (Exception e) {
                        e = e;
                        file3 = file5;
                        log("Failed to process " + this.inFile, 2);
                        if (file3 != null) {
                            file3.delete();
                        }
                        handleTransformationError(e);
                        return;
                    }
                }
                log("Skipping " + this.inFile + " its mapping is ambiguos.", 3);
                return;
            }
            log("Skipping " + this.inFile + " it cannot get mapped to output.", 3);
        } catch (Exception e2) {
            e = e2;
        }
    }

    private void process(File file, File file2, Resource resource) throws BuildException {
        try {
            long lastModified = resource.getLastModified();
            log("In file " + file + " time: " + file.lastModified(), 4);
            log("Out file " + file2 + " time: " + file2.lastModified(), 4);
            log("Style file " + this.xslFile + " time: " + lastModified, 4);
            if (this.force || file.lastModified() >= file2.lastModified() || lastModified >= file2.lastModified()) {
                ensureDirectoryFor(file2);
                log("Processing " + file + " to " + file2, 2);
                configureLiaison(resource);
                setLiaisonDynamicFileParameters(this.liaison, file);
                this.liaison.transform(file, file2);
            } else {
                log("Skipping input file " + file + " because it is older than output file " + file2 + " and so is the stylesheet " + resource, 4);
            }
        } catch (Exception e) {
            log("Failed to process " + file, 2);
            if (file2 != null) {
                file2.delete();
            }
            handleTransformationError(e);
        }
    }

    private void ensureDirectoryFor(File file) throws BuildException {
        File parentFile = file.getParentFile();
        if (parentFile.exists() || parentFile.mkdirs() || parentFile.isDirectory()) {
            return;
        }
        handleError("Unable to create directory: " + parentFile.getAbsolutePath());
    }

    public Factory getFactory() {
        return this.factory;
    }

    public XMLCatalog getXMLCatalog() {
        this.xmlCatalog.setProject(getProject());
        return this.xmlCatalog;
    }

    public Enumeration getOutputProperties() {
        return this.outputProperties.elements();
    }

    protected XSLTLiaison getLiaison() {
        if (this.liaison == null) {
            String str = this.processor;
            if (str != null) {
                try {
                    resolveProcessor(str);
                } catch (Exception e) {
                    handleError(e);
                }
            } else {
                try {
                    resolveProcessor(PROCESSOR_TRAX);
                } catch (Throwable th) {
                    th.printStackTrace();
                    handleError(th);
                }
            }
        }
        return this.liaison;
    }

    public Param createParam() {
        Param param = new Param();
        this.params.add(param);
        return param;
    }

    public static class Param {
        private Object ifCond;
        private Project project;
        private String type;
        private Object unlessCond;
        private String name = null;
        private String expression = null;

        public void setProject(Project project) {
            this.project = project;
        }

        public void setName(String str) {
            this.name = str;
        }

        public void setExpression(String str) {
            this.expression = str;
        }

        public void setType(String str) {
            this.type = str;
        }

        public String getName() throws BuildException {
            String str = this.name;
            if (str != null) {
                return str;
            }
            throw new BuildException("Name attribute is missing.");
        }

        public String getExpression() throws BuildException {
            String str = this.expression;
            if (str != null) {
                return str;
            }
            throw new BuildException("Expression attribute is missing.");
        }

        public String getType() {
            return this.type;
        }

        public void setIf(Object obj) {
            this.ifCond = obj;
        }

        public void setIf(String str) {
            setIf((Object) str);
        }

        public void setUnless(Object obj) {
            this.unlessCond = obj;
        }

        public void setUnless(String str) {
            setUnless((Object) str);
        }

        public boolean shouldUse() {
            PropertyHelper propertyHelper = PropertyHelper.getPropertyHelper(this.project);
            return propertyHelper.testIfCondition(this.ifCond) && propertyHelper.testUnlessCondition(this.unlessCond);
        }
    }

    public enum ParamType {
        STRING,
        BOOLEAN,
        INT,
        LONG,
        DOUBLE,
        XPATH_STRING,
        XPATH_BOOLEAN,
        XPATH_NUMBER,
        XPATH_NODE,
        XPATH_NODESET;

        public static final Map<ParamType, QName> XPATH_TYPES;

        static {
            ParamType paramType = XPATH_STRING;
            ParamType paramType2 = XPATH_BOOLEAN;
            ParamType paramType3 = XPATH_NUMBER;
            ParamType paramType4 = XPATH_NODE;
            ParamType paramType5 = XPATH_NODESET;
            EnumMap enumMap = new EnumMap(ParamType.class);
            enumMap.put(paramType, XPathConstants.STRING);
            enumMap.put(paramType2, XPathConstants.BOOLEAN);
            enumMap.put(paramType3, XPathConstants.NUMBER);
            enumMap.put(paramType4, XPathConstants.NODE);
            enumMap.put(paramType5, XPathConstants.NODESET);
            XPATH_TYPES = Collections.unmodifiableMap(enumMap);
        }
    }

    public OutputProperty createOutputProperty() {
        OutputProperty outputProperty = new OutputProperty();
        this.outputProperties.addElement(outputProperty);
        return outputProperty;
    }

    public static class OutputProperty {
        private String name;
        private String value;

        public String getName() {
            return this.name;
        }

        public void setName(String str) {
            this.name = str;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String str) {
            this.value = str;
        }
    }

    @Override // org.apache.tools.ant.Task
    public void init() throws BuildException {
        super.init();
        this.xmlCatalog.setProject(getProject());
        XPathFactory xPathFactoryNewInstance = XPathFactory.newInstance();
        this.xpathFactory = xPathFactoryNewInstance;
        XPath xPathNewXPath = xPathFactoryNewInstance.newXPath();
        this.xpath = xPathNewXPath;
        xPathNewXPath.setXPathVariableResolver(new XPathVariableResolver() { // from class: org.apache.tools.ant.taskdefs.XSLTProcess.1
            @Override // javax.xml.xpath.XPathVariableResolver
            public Object resolveVariable(QName qName) {
                return XSLTProcess.this.getProject().getProperty(qName.toString());
            }
        });
    }

    protected void configureLiaison(File file) throws BuildException {
        FileResource fileResource = new FileResource();
        fileResource.setProject(getProject());
        fileResource.setFile(file);
        configureLiaison(fileResource);
    }

    protected void configureLiaison(Resource resource) throws BuildException {
        if (this.stylesheetLoaded && this.reuseLoadedStylesheet) {
            return;
        }
        this.stylesheetLoaded = true;
        try {
            log("Loading stylesheet " + resource, 2);
            XSLTLiaison xSLTLiaison = this.liaison;
            if (xSLTLiaison instanceof XSLTLiaison2) {
                ((XSLTLiaison2) xSLTLiaison).configure(this);
            }
            XSLTLiaison xSLTLiaison2 = this.liaison;
            if (xSLTLiaison2 instanceof XSLTLiaison3) {
                ((XSLTLiaison3) xSLTLiaison2).setStylesheet(resource);
            } else {
                FileProvider fileProvider = (FileProvider) resource.as(FileProvider.class);
                if (fileProvider != null) {
                    this.liaison.setStylesheet(fileProvider.getFile());
                } else {
                    handleError(this.liaison.getClass().toString() + " accepts the stylesheet only as a file");
                    return;
                }
            }
            for (Param param : this.params) {
                if (param.shouldUse()) {
                    Object objEvaluateParam = evaluateParam(param);
                    XSLTLiaison xSLTLiaison3 = this.liaison;
                    if (xSLTLiaison3 instanceof XSLTLiaison4) {
                        ((XSLTLiaison4) xSLTLiaison3).addParam(param.getName(), objEvaluateParam);
                    } else if (objEvaluateParam == null || (objEvaluateParam instanceof String)) {
                        xSLTLiaison3.addParam(param.getName(), (String) objEvaluateParam);
                    } else {
                        log("XSLTLiaison '" + this.liaison.getClass().getName() + "' supports only String parameters. Converting parameter '" + param.getName() + "' to its String value '" + objEvaluateParam, 1);
                        this.liaison.addParam(param.getName(), String.valueOf(objEvaluateParam));
                    }
                }
            }
        } catch (Exception e) {
            log("Failed to transform using stylesheet " + resource, 2);
            handleTransformationError(e);
        }
    }

    private Object evaluateParam(Param param) throws XPathExpressionException {
        ParamType paramTypeValueOf;
        String type = param.getType();
        String expression = param.getExpression();
        if (type == null || "".equals(type)) {
            paramTypeValueOf = ParamType.STRING;
        } else {
            try {
                paramTypeValueOf = ParamType.valueOf(type);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid XSLT parameter type: " + type, e);
            }
        }
        int i = AnonymousClass2.$SwitchMap$org$apache$tools$ant$taskdefs$XSLTProcess$ParamType[paramTypeValueOf.ordinal()];
        if (i == 1) {
            return expression;
        }
        if (i == 2) {
            return Boolean.valueOf(Boolean.parseBoolean(expression));
        }
        if (i == 3) {
            return Double.valueOf(Double.parseDouble(expression));
        }
        if (i == 4) {
            return Integer.valueOf(Integer.parseInt(expression));
        }
        if (i == 5) {
            return Long.valueOf(Long.parseLong(expression));
        }
        QName qName = ParamType.XPATH_TYPES.get(paramTypeValueOf);
        if (qName == null) {
            throw new IllegalArgumentException("Invalid XSLT parameter type: " + type);
        }
        return this.xpath.compile(expression).evaluate((Object) null, qName);
    }

    /* JADX INFO: renamed from: org.apache.tools.ant.taskdefs.XSLTProcess$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$tools$ant$taskdefs$XSLTProcess$ParamType;

        static {
            int[] iArr = new int[ParamType.values().length];
            $SwitchMap$org$apache$tools$ant$taskdefs$XSLTProcess$ParamType = iArr;
            try {
                iArr[ParamType.STRING.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$org$apache$tools$ant$taskdefs$XSLTProcess$ParamType[ParamType.BOOLEAN.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$org$apache$tools$ant$taskdefs$XSLTProcess$ParamType[ParamType.DOUBLE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$org$apache$tools$ant$taskdefs$XSLTProcess$ParamType[ParamType.INT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$org$apache$tools$ant$taskdefs$XSLTProcess$ParamType[ParamType.LONG.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    private void setLiaisonDynamicFileParameters(XSLTLiaison xSLTLiaison, File file) throws Exception {
        String str = this.fileNameParameter;
        if (str != null) {
            xSLTLiaison.addParam(str, file.getName());
        }
        if (this.fileDirParameter != null) {
            File file2 = new File(FileUtils.getRelativePath(this.baseDir, file));
            xSLTLiaison.addParam(this.fileDirParameter, file2.getParent() != null ? file2.getParent().replace('\\', '/') : ".");
        }
    }

    public Factory createFactory() throws BuildException {
        if (this.factory != null) {
            handleError("'factory' element must be unique");
        } else {
            this.factory = new Factory();
        }
        return this.factory;
    }

    protected void handleError(String str) {
        if (this.failOnError) {
            throw new BuildException(str, getLocation());
        }
        log(str, 1);
    }

    protected void handleError(Throwable th) {
        if (this.failOnError) {
            throw new BuildException(th);
        }
        log("Caught an exception: " + th, 1);
    }

    protected void handleTransformationError(Exception exc) {
        if (this.failOnError && this.failOnTransformationError) {
            throw new BuildException(exc);
        }
        log("Caught an error during transformation: " + exc, 1);
    }

    public static class Factory {
        private Vector attributes = new Vector();
        private String name;

        public String getName() {
            return this.name;
        }

        public void setName(String str) {
            this.name = str;
        }

        public void addAttribute(Attribute attribute) {
            this.attributes.addElement(attribute);
        }

        public Enumeration getAttributes() {
            return this.attributes.elements();
        }

        public static class Attribute implements DynamicConfigurator {
            private String name;
            private Object value;

            @Override // org.apache.tools.ant.DynamicElement
            public Object createDynamicElement(String str) throws BuildException {
                return null;
            }

            public String getName() {
                return this.name;
            }

            public Object getValue() {
                return this.value;
            }

            @Override // org.apache.tools.ant.DynamicAttribute
            public void setDynamicAttribute(String str, String str2) throws BuildException {
                if ("name".equalsIgnoreCase(str)) {
                    this.name = str2;
                    return;
                }
                if ("value".equalsIgnoreCase(str)) {
                    if ("true".equalsIgnoreCase(str2)) {
                        this.value = Boolean.TRUE;
                        return;
                    } else {
                        if ("false".equalsIgnoreCase(str2)) {
                            this.value = Boolean.FALSE;
                            return;
                        }
                        try {
                            this.value = new Integer(str2);
                            return;
                        } catch (NumberFormatException unused) {
                            this.value = str2;
                            return;
                        }
                    }
                }
                throw new BuildException("Unsupported attribute: " + str);
            }
        }
    }

    private class StyleMapper implements FileNameMapper {
        @Override // org.apache.tools.ant.util.FileNameMapper
        public void setFrom(String str) {
        }

        @Override // org.apache.tools.ant.util.FileNameMapper
        public void setTo(String str) {
        }

        private StyleMapper() {
        }

        @Override // org.apache.tools.ant.util.FileNameMapper
        public String[] mapFileName(String str) {
            int iLastIndexOf = str.lastIndexOf(46);
            if (iLastIndexOf > 0) {
                str = str.substring(0, iLastIndexOf);
            }
            return new String[]{str + XSLTProcess.this.targetExtension};
        }
    }

    public final class TraceConfiguration {
        private boolean elements;
        private boolean extension;
        private boolean generation;
        private boolean selection;
        private boolean templates;

        public TraceConfiguration() {
        }

        public void setElements(boolean z) {
            this.elements = z;
        }

        public boolean getElements() {
            return this.elements;
        }

        public void setExtension(boolean z) {
            this.extension = z;
        }

        public boolean getExtension() {
            return this.extension;
        }

        public void setGeneration(boolean z) {
            this.generation = z;
        }

        public boolean getGeneration() {
            return this.generation;
        }

        public void setSelection(boolean z) {
            this.selection = z;
        }

        public boolean getSelection() {
            return this.selection;
        }

        public void setTemplates(boolean z) {
            this.templates = z;
        }

        public boolean getTemplates() {
            return this.templates;
        }

        public OutputStream getOutputStream() {
            return new LogOutputStream(XSLTProcess.this);
        }
    }
}
