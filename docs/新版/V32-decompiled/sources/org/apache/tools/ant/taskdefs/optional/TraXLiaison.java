package org.apache.tools.ant.taskdefs.optional;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.XSLTLiaison4;
import org.apache.tools.ant.taskdefs.XSLTLogger;
import org.apache.tools.ant.taskdefs.XSLTLoggerAware;
import org.apache.tools.ant.taskdefs.XSLTProcess;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.XMLCatalog;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.URLProvider;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JAXPUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/* JADX INFO: loaded from: classes3.dex */
public class TraXLiaison implements XSLTLiaison4, ErrorListener, XSLTLoggerAware {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private EntityResolver entityResolver;
    private XSLTLogger logger;
    private Project project;
    private Resource stylesheet;
    private Templates templates;
    private long templatesModTime;
    private Transformer transformer;
    private URIResolver uriResolver;
    private String factoryName = null;
    private TransformerFactory tfactory = null;
    private Vector outputProperties = new Vector();
    private Hashtable<String, Object> params = new Hashtable<>();
    private Vector attributes = new Vector();
    private boolean suppressWarnings = false;
    private XSLTProcess.TraceConfiguration traceConfiguration = null;

    @Override // org.apache.tools.ant.taskdefs.XSLTLiaison
    public void setStylesheet(File file) throws Exception {
        FileResource fileResource = new FileResource();
        fileResource.setProject(this.project);
        fileResource.setFile(file);
        setStylesheet(fileResource);
    }

    @Override // org.apache.tools.ant.taskdefs.XSLTLiaison3
    public void setStylesheet(Resource resource) throws Exception {
        Resource resource2 = this.stylesheet;
        if (resource2 != null) {
            this.transformer = null;
            if (!resource2.equals(resource) || resource.getLastModified() != this.templatesModTime) {
                this.templates = null;
            }
        }
        this.stylesheet = resource;
    }

    @Override // org.apache.tools.ant.taskdefs.XSLTLiaison
    public void transform(File file, File file2) throws Exception {
        BufferedOutputStream bufferedOutputStream;
        if (this.transformer == null) {
            createTransformer();
        }
        BufferedInputStream bufferedInputStream = null;
        try {
            BufferedInputStream bufferedInputStream2 = new BufferedInputStream(new FileInputStream(file));
            try {
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file2));
                try {
                    StreamResult streamResult = new StreamResult(bufferedOutputStream);
                    streamResult.setSystemId(JAXPUtils.getSystemId(file2));
                    Source source = getSource(bufferedInputStream2, file);
                    setTransformationParameters();
                    this.transformer.transform(source, streamResult);
                    FileUtils.close(bufferedInputStream2);
                    FileUtils.close(bufferedOutputStream);
                } catch (Throwable th) {
                    th = th;
                    bufferedInputStream = bufferedInputStream2;
                    FileUtils.close(bufferedInputStream);
                    FileUtils.close(bufferedOutputStream);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                bufferedOutputStream = null;
            }
        } catch (Throwable th3) {
            th = th3;
            bufferedOutputStream = null;
        }
    }

    private Source getSource(InputStream inputStream, File file) throws ParserConfigurationException, SAXException {
        Source streamSource;
        if (this.entityResolver != null) {
            if (getFactory().getFeature("http://javax.xml.transform.sax.SAXSource/feature")) {
                SAXParserFactory sAXParserFactoryNewInstance = SAXParserFactory.newInstance();
                sAXParserFactoryNewInstance.setNamespaceAware(true);
                XMLReader xMLReader = sAXParserFactoryNewInstance.newSAXParser().getXMLReader();
                xMLReader.setEntityResolver(this.entityResolver);
                streamSource = new SAXSource(xMLReader, new InputSource(inputStream));
            } else {
                throw new IllegalStateException("xcatalog specified, but parser doesn't support SAX");
            }
        } else {
            streamSource = new StreamSource(inputStream);
        }
        streamSource.setSystemId(JAXPUtils.getSystemId(file));
        return streamSource;
    }

    private Source getSource(InputStream inputStream, Resource resource) throws ParserConfigurationException, SAXException {
        Source streamSource;
        if (this.entityResolver != null) {
            if (getFactory().getFeature("http://javax.xml.transform.sax.SAXSource/feature")) {
                SAXParserFactory sAXParserFactoryNewInstance = SAXParserFactory.newInstance();
                sAXParserFactoryNewInstance.setNamespaceAware(true);
                XMLReader xMLReader = sAXParserFactoryNewInstance.newSAXParser().getXMLReader();
                xMLReader.setEntityResolver(this.entityResolver);
                streamSource = new SAXSource(xMLReader, new InputSource(inputStream));
            } else {
                throw new IllegalStateException("xcatalog specified, but parser doesn't support SAX");
            }
        } else {
            streamSource = new StreamSource(inputStream);
        }
        streamSource.setSystemId(resourceToURI(resource));
        return streamSource;
    }

    private String resourceToURI(Resource resource) {
        FileProvider fileProvider = (FileProvider) resource.as(FileProvider.class);
        if (fileProvider != null) {
            return FILE_UTILS.toURI(fileProvider.getFile().getAbsolutePath());
        }
        URLProvider uRLProvider = (URLProvider) resource.as(URLProvider.class);
        if (uRLProvider != null) {
            return String.valueOf(uRLProvider.getURL());
        }
        return resource.getName();
    }

    private void readTemplates() throws Throwable {
        BufferedInputStream bufferedInputStream;
        Throwable th;
        try {
            bufferedInputStream = new BufferedInputStream(this.stylesheet.getInputStream());
            try {
                this.templatesModTime = this.stylesheet.getLastModified();
                this.templates = getFactory().newTemplates(getSource(bufferedInputStream, this.stylesheet));
                bufferedInputStream.close();
            } catch (Throwable th2) {
                th = th2;
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            bufferedInputStream = null;
            th = th3;
        }
    }

    private void createTransformer() throws Exception {
        if (this.templates == null) {
            readTemplates();
        }
        Transformer transformerNewTransformer = this.templates.newTransformer();
        this.transformer = transformerNewTransformer;
        transformerNewTransformer.setErrorListener(this);
        URIResolver uRIResolver = this.uriResolver;
        if (uRIResolver != null) {
            this.transformer.setURIResolver(uRIResolver);
        }
        int size = this.outputProperties.size();
        for (int i = 0; i < size; i++) {
            String[] strArr = (String[]) this.outputProperties.elementAt(i);
            this.transformer.setOutputProperty(strArr[0], strArr[1]);
        }
        if (this.traceConfiguration != null) {
            if ("org.apache.xalan.transformer.TransformerImpl".equals(this.transformer.getClass().getName())) {
                try {
                    ((XSLTTraceSupport) Class.forName("org.apache.tools.ant.taskdefs.optional.Xalan2TraceSupport", true, Thread.currentThread().getContextClassLoader()).newInstance()).configureTrace(this.transformer, this.traceConfiguration);
                    return;
                } catch (Exception e) {
                    String str = "Failed to enable tracing because of " + e;
                    Project project = this.project;
                    if (project != null) {
                        project.log(str, 1);
                        return;
                    } else {
                        System.err.println(str);
                        return;
                    }
                }
            }
            String str2 = "Not enabling trace support for transformer implementation" + this.transformer.getClass().getName();
            Project project2 = this.project;
            if (project2 != null) {
                project2.log(str2, 1);
            } else {
                System.err.println(str2);
            }
        }
    }

    private void setTransformationParameters() {
        Enumeration<String> enumerationKeys = this.params.keys();
        while (enumerationKeys.hasMoreElements()) {
            String strNextElement = enumerationKeys.nextElement();
            this.transformer.setParameter(strNextElement, this.params.get(strNextElement));
        }
    }

    private TransformerFactory getFactory() throws BuildException {
        TransformerFactory transformerFactory = this.tfactory;
        if (transformerFactory != null) {
            return transformerFactory;
        }
        String str = this.factoryName;
        if (str == null) {
            this.tfactory = TransformerFactory.newInstance();
        } else {
            Class<?> cls = null;
            try {
                try {
                    cls = Class.forName(str, true, Thread.currentThread().getContextClassLoader());
                } catch (Exception e) {
                    throw new BuildException(e);
                }
            } catch (ClassNotFoundException unused) {
                String str2 = "Failed to load " + this.factoryName + " via the configured classpath, will try Ant's classpath instead.";
                XSLTLogger xSLTLogger = this.logger;
                if (xSLTLogger != null) {
                    xSLTLogger.log(str2);
                } else {
                    Project project = this.project;
                    if (project != null) {
                        project.log(str2, 1);
                    } else {
                        System.err.println(str2);
                    }
                }
            }
            if (cls == null) {
                cls = Class.forName(this.factoryName);
            }
            this.tfactory = (TransformerFactory) cls.newInstance();
        }
        try {
            Field declaredField = this.tfactory.getClass().getDeclaredField("_isNotSecureProcessing");
            declaredField.setAccessible(true);
            declaredField.set(this.tfactory, Boolean.TRUE);
        } catch (Exception e2) {
            Project project2 = this.project;
            if (project2 != null) {
                project2.log(e2.toString(), 4);
            }
        }
        this.tfactory.setErrorListener(this);
        int size = this.attributes.size();
        for (int i = 0; i < size; i++) {
            Object[] objArr = (Object[]) this.attributes.elementAt(i);
            this.tfactory.setAttribute((String) objArr[0], objArr[1]);
        }
        URIResolver uRIResolver = this.uriResolver;
        if (uRIResolver != null) {
            this.tfactory.setURIResolver(uRIResolver);
        }
        return this.tfactory;
    }

    public void setFactory(String str) {
        this.factoryName = str;
    }

    public void setAttribute(String str, Object obj) {
        this.attributes.addElement(new Object[]{str, obj});
    }

    public void setOutputProperty(String str, String str2) {
        this.outputProperties.addElement(new String[]{str, str2});
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    public void setURIResolver(URIResolver uRIResolver) {
        this.uriResolver = uRIResolver;
    }

    @Override // org.apache.tools.ant.taskdefs.XSLTLiaison
    public void addParam(String str, String str2) {
        this.params.put(str, str2);
    }

    @Override // org.apache.tools.ant.taskdefs.XSLTLiaison4
    public void addParam(String str, Object obj) {
        this.params.put(str, obj);
    }

    @Override // org.apache.tools.ant.taskdefs.XSLTLoggerAware
    public void setLogger(XSLTLogger xSLTLogger) {
        this.logger = xSLTLogger;
    }

    @Override // javax.xml.transform.ErrorListener
    public void error(TransformerException transformerException) {
        logError(transformerException, "Error");
    }

    @Override // javax.xml.transform.ErrorListener
    public void fatalError(TransformerException transformerException) {
        logError(transformerException, "Fatal Error");
        throw new BuildException("Fatal error during transformation using " + this.stylesheet + ": " + transformerException.getMessageAndLocation(), transformerException);
    }

    @Override // javax.xml.transform.ErrorListener
    public void warning(TransformerException transformerException) {
        if (this.suppressWarnings) {
            return;
        }
        logError(transformerException, "Warning");
    }

    private void logError(TransformerException transformerException, String str) {
        if (this.logger == null) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        SourceLocator locator = transformerException.getLocator();
        if (locator != null) {
            String systemId = locator.getSystemId();
            if (systemId != null) {
                if (systemId.startsWith("file:")) {
                    systemId = FileUtils.getFileUtils().fromURI(systemId);
                }
                stringBuffer.append(systemId);
            } else {
                stringBuffer.append("Unknown file");
            }
            int lineNumber = locator.getLineNumber();
            if (lineNumber != -1) {
                stringBuffer.append(":");
                stringBuffer.append(lineNumber);
                int columnNumber = locator.getColumnNumber();
                if (columnNumber != -1) {
                    stringBuffer.append(":");
                    stringBuffer.append(columnNumber);
                }
            }
        }
        stringBuffer.append(": ");
        stringBuffer.append(str);
        stringBuffer.append("! ");
        stringBuffer.append(transformerException.getMessage());
        if (transformerException.getCause() != null) {
            stringBuffer.append(" Cause: ");
            stringBuffer.append(transformerException.getCause());
        }
        this.logger.log(stringBuffer.toString());
    }

    protected String getSystemId(File file) {
        return JAXPUtils.getSystemId(file);
    }

    @Override // org.apache.tools.ant.taskdefs.XSLTLiaison2
    public void configure(XSLTProcess xSLTProcess) {
        this.project = xSLTProcess.getProject();
        XSLTProcess.Factory factory = xSLTProcess.getFactory();
        if (factory != null) {
            setFactory(factory.getName());
            Enumeration attributes = factory.getAttributes();
            while (attributes.hasMoreElements()) {
                XSLTProcess.Factory.Attribute attribute = (XSLTProcess.Factory.Attribute) attributes.nextElement();
                setAttribute(attribute.getName(), attribute.getValue());
            }
        }
        XMLCatalog xMLCatalog = xSLTProcess.getXMLCatalog();
        if (xMLCatalog != null) {
            setEntityResolver(xMLCatalog);
            setURIResolver(xMLCatalog);
        }
        Enumeration outputProperties = xSLTProcess.getOutputProperties();
        while (outputProperties.hasMoreElements()) {
            XSLTProcess.OutputProperty outputProperty = (XSLTProcess.OutputProperty) outputProperties.nextElement();
            setOutputProperty(outputProperty.getName(), outputProperty.getValue());
        }
        this.suppressWarnings = xSLTProcess.getSuppressWarnings();
        this.traceConfiguration = xSLTProcess.getTraceConfiguration();
    }
}
