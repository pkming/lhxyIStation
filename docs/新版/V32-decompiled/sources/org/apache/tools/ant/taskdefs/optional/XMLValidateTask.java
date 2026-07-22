package org.apache.tools.ant.taskdefs.optional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DTDLocation;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.XMLCatalog;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JAXPUtils;
import org.apache.tools.ant.util.XmlConstants;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.ParserAdapter;

/* JADX INFO: loaded from: classes3.dex */
public class XMLValidateTask extends Task {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    protected static final String INIT_FAILED_MSG = "Could not start xml validation: ";
    public static final String MESSAGE_FILES_VALIDATED = " file(s) have been successfully validated.";
    protected Path classpath;
    protected boolean failOnError = true;
    protected boolean warn = true;
    protected boolean lenient = false;
    protected String readerClassName = null;
    protected File file = null;
    protected Vector filesets = new Vector();
    protected XMLReader xmlReader = null;
    protected ValidatorErrorHandler errorHandler = new ValidatorErrorHandler();
    private Vector attributeList = new Vector();
    private final Vector propertyList = new Vector();
    private XMLCatalog xmlCatalog = new XMLCatalog();
    private AntClassLoader readerLoader = null;

    public void setFailOnError(boolean z) {
        this.failOnError = z;
    }

    public void setWarn(boolean z) {
        this.warn = z;
    }

    public void setLenient(boolean z) {
        this.lenient = z;
    }

    public void setClassName(String str) {
        this.readerClassName = str;
    }

    public void setClasspath(Path path) {
        Path path2 = this.classpath;
        if (path2 == null) {
            this.classpath = path;
        } else {
            path2.append(path);
        }
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

    public void setFile(File file) {
        this.file = file;
    }

    public void addConfiguredXMLCatalog(XMLCatalog xMLCatalog) {
        this.xmlCatalog.addConfiguredXMLCatalog(xMLCatalog);
    }

    public void addFileset(FileSet fileSet) {
        this.filesets.addElement(fileSet);
    }

    public Attribute createAttribute() {
        Attribute attribute = new Attribute();
        this.attributeList.addElement(attribute);
        return attribute;
    }

    public Property createProperty() {
        Property property = new Property();
        this.propertyList.addElement(property);
        return property;
    }

    @Override // org.apache.tools.ant.Task
    public void init() throws BuildException {
        super.init();
        this.xmlCatalog.setProject(getProject());
    }

    public DTDLocation createDTD() {
        DTDLocation dTDLocation = new DTDLocation();
        this.xmlCatalog.addDTD(dTDLocation);
        return dTDLocation;
    }

    protected EntityResolver getEntityResolver() {
        return this.xmlCatalog;
    }

    protected XMLReader getXmlReader() {
        return this.xmlReader;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        int i;
        try {
            if (this.file == null && this.filesets.size() == 0) {
                throw new BuildException("Specify at least one source - a file or a fileset.");
            }
            File file = this.file;
            if (file == null) {
                i = 0;
            } else if (file.exists() && this.file.canRead() && this.file.isFile()) {
                doValidate(this.file);
                i = 1;
            } else {
                String str = "File " + this.file + " cannot be read";
                if (this.failOnError) {
                    throw new BuildException(str);
                }
                log(str, 0);
                i = 0;
            }
            int size = this.filesets.size();
            for (int i2 = 0; i2 < size; i2++) {
                FileSet fileSet = (FileSet) this.filesets.elementAt(i2);
                for (String str2 : fileSet.getDirectoryScanner(getProject()).getIncludedFiles()) {
                    doValidate(new File(fileSet.getDir(getProject()), str2));
                    i++;
                }
            }
            onSuccessfulValidation(i);
        } finally {
            cleanup();
        }
    }

    protected void onSuccessfulValidation(int i) {
        log(i + MESSAGE_FILES_VALIDATED);
    }

    protected void initValidator() {
        XMLReader xMLReaderCreateXmlReader = createXmlReader();
        this.xmlReader = xMLReaderCreateXmlReader;
        xMLReaderCreateXmlReader.setEntityResolver(getEntityResolver());
        this.xmlReader.setErrorHandler(this.errorHandler);
        if (isSax1Parser()) {
            return;
        }
        if (!this.lenient) {
            setFeature(XmlConstants.FEATURE_VALIDATION, true);
        }
        int size = this.attributeList.size();
        for (int i = 0; i < size; i++) {
            Attribute attribute = (Attribute) this.attributeList.elementAt(i);
            setFeature(attribute.getName(), attribute.getValue());
        }
        int size2 = this.propertyList.size();
        for (int i2 = 0; i2 < size2; i2++) {
            Property property = (Property) this.propertyList.elementAt(i2);
            setProperty(property.getName(), property.getValue());
        }
    }

    protected boolean isSax1Parser() {
        return this.xmlReader instanceof ParserAdapter;
    }

    protected XMLReader createXmlReader() {
        Class<?> cls;
        Object objNewInstance;
        String str = this.readerClassName;
        if (str == null) {
            objNewInstance = createDefaultReaderOrParser();
        } else {
            try {
                if (this.classpath != null) {
                    AntClassLoader antClassLoaderCreateClassLoader = getProject().createClassLoader(this.classpath);
                    this.readerLoader = antClassLoaderCreateClassLoader;
                    cls = Class.forName(this.readerClassName, true, antClassLoaderCreateClassLoader);
                } else {
                    cls = Class.forName(str);
                }
                objNewInstance = cls.newInstance();
            } catch (ClassNotFoundException e) {
                throw new BuildException(INIT_FAILED_MSG + this.readerClassName, e);
            } catch (IllegalAccessException e2) {
                throw new BuildException(INIT_FAILED_MSG + this.readerClassName, e2);
            } catch (InstantiationException e3) {
                throw new BuildException(INIT_FAILED_MSG + this.readerClassName, e3);
            }
        }
        if (objNewInstance instanceof XMLReader) {
            XMLReader xMLReader = (XMLReader) objNewInstance;
            log("Using SAX2 reader " + objNewInstance.getClass().getName(), 3);
            return xMLReader;
        }
        if (objNewInstance instanceof Parser) {
            ParserAdapter parserAdapter = new ParserAdapter((Parser) objNewInstance);
            log("Using SAX1 parser " + objNewInstance.getClass().getName(), 3);
            return parserAdapter;
        }
        throw new BuildException(INIT_FAILED_MSG + objNewInstance.getClass().getName() + " implements nor SAX1 Parser nor SAX2 XMLReader.");
    }

    protected void cleanup() {
        AntClassLoader antClassLoader = this.readerLoader;
        if (antClassLoader != null) {
            antClassLoader.cleanup();
            this.readerLoader = null;
        }
    }

    private Object createDefaultReaderOrParser() {
        try {
            return createDefaultReader();
        } catch (BuildException unused) {
            return JAXPUtils.getParser();
        }
    }

    protected XMLReader createDefaultReader() {
        return JAXPUtils.getXMLReader();
    }

    protected void setFeature(String str, boolean z) throws BuildException {
        log("Setting feature " + str + "=" + z, 4);
        try {
            this.xmlReader.setFeature(str, z);
        } catch (SAXNotRecognizedException e) {
            throw new BuildException("Parser " + this.xmlReader.getClass().getName() + " doesn't recognize feature " + str, e, getLocation());
        } catch (SAXNotSupportedException e2) {
            throw new BuildException("Parser " + this.xmlReader.getClass().getName() + " doesn't support feature " + str, e2, getLocation());
        }
    }

    protected void setProperty(String str, String str2) throws BuildException {
        if (str == null || str2 == null) {
            throw new BuildException("Property name and value must be specified.");
        }
        try {
            this.xmlReader.setProperty(str, str2);
        } catch (SAXNotRecognizedException e) {
            throw new BuildException("Parser " + this.xmlReader.getClass().getName() + " doesn't recognize property " + str, e, getLocation());
        } catch (SAXNotSupportedException e2) {
            throw new BuildException("Parser " + this.xmlReader.getClass().getName() + " doesn't support property " + str, e2, getLocation());
        }
    }

    protected boolean doValidate(File file) {
        boolean z;
        initValidator();
        try {
            log("Validating " + file.getName() + "... ", 3);
            this.errorHandler.init(file);
            InputSource inputSource = new InputSource(new FileInputStream(file));
            inputSource.setSystemId(FILE_UTILS.toURI(file.getAbsolutePath()));
            this.xmlReader.parse(inputSource);
            z = true;
        } catch (IOException e) {
            throw new BuildException("Could not validate document " + file, e);
        } catch (SAXException e2) {
            log("Caught when validating: " + e2.toString(), 4);
            if (this.failOnError) {
                throw new BuildException("Could not validate document " + file);
            }
            log("Could not validate document " + file + ": " + e2.toString());
            z = false;
        }
        if (!this.errorHandler.getFailure()) {
            return z;
        }
        if (this.failOnError) {
            throw new BuildException(file + " is not a valid XML document.");
        }
        log(file + " is not a valid XML document", 0);
        return false;
    }

    protected class ValidatorErrorHandler implements ErrorHandler {
        protected File currentFile = null;
        protected String lastErrorMessage = null;
        protected boolean failed = false;

        protected ValidatorErrorHandler() {
        }

        public void init(File file) {
            this.currentFile = file;
            this.failed = false;
        }

        public boolean getFailure() {
            return this.failed;
        }

        @Override // org.xml.sax.ErrorHandler
        public void fatalError(SAXParseException sAXParseException) {
            this.failed = true;
            doLog(sAXParseException, 0);
        }

        @Override // org.xml.sax.ErrorHandler
        public void error(SAXParseException sAXParseException) {
            this.failed = true;
            doLog(sAXParseException, 0);
        }

        @Override // org.xml.sax.ErrorHandler
        public void warning(SAXParseException sAXParseException) {
            if (XMLValidateTask.this.warn) {
                doLog(sAXParseException, 1);
            }
        }

        private void doLog(SAXParseException sAXParseException, int i) {
            XMLValidateTask.this.log(getMessage(sAXParseException), i);
        }

        private String getMessage(SAXParseException sAXParseException) {
            String str;
            String systemId = sAXParseException.getSystemId();
            if (systemId != null) {
                if (systemId.startsWith("file:")) {
                    try {
                        systemId = XMLValidateTask.FILE_UTILS.fromURI(systemId);
                    } catch (Exception unused) {
                    }
                }
                int lineNumber = sAXParseException.getLineNumber();
                int columnNumber = sAXParseException.getColumnNumber();
                StringBuilder sbAppend = new StringBuilder().append(systemId);
                if (lineNumber != -1) {
                    str = ":" + lineNumber + (columnNumber != -1 ? ":" + columnNumber : "");
                }
                return sbAppend.append(str).append(": ").append(sAXParseException.getMessage()).toString();
            }
            return sAXParseException.getMessage();
        }
    }

    public static class Attribute {
        private String attributeName = null;
        private boolean attributeValue;

        public void setName(String str) {
            this.attributeName = str;
        }

        public void setValue(boolean z) {
            this.attributeValue = z;
        }

        public String getName() {
            return this.attributeName;
        }

        public boolean getValue() {
            return this.attributeValue;
        }
    }

    public static final class Property {
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
}
