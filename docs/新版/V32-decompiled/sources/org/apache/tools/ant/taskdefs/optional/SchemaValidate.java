package org.apache.tools.ant.taskdefs.optional;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.XmlConstants;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/* JADX INFO: loaded from: classes3.dex */
public class SchemaValidate extends XMLValidateTask {
    public static final String ERROR_DUPLICATE_SCHEMA = "Duplicate declaration of schema ";
    public static final String ERROR_NO_XSD_SUPPORT = "Parser does not support Xerces or JAXP schema features";
    public static final String ERROR_PARSER_CREATION_FAILURE = "Could not create parser";
    public static final String ERROR_SAX_1 = "SAX1 parsers are not supported";
    public static final String ERROR_TOO_MANY_DEFAULT_SCHEMAS = "Only one of defaultSchemaFile and defaultSchemaURL allowed";
    public static final String MESSAGE_ADDING_SCHEMA = "Adding schema ";
    private SchemaLocation anonymousSchema;
    private HashMap schemaLocations = new HashMap();
    private boolean fullChecking = true;
    private boolean disableDTD = false;

    @Override // org.apache.tools.ant.taskdefs.optional.XMLValidateTask, org.apache.tools.ant.Task
    public void init() throws BuildException {
        super.init();
        setLenient(false);
    }

    public boolean enableXercesSchemaValidation() {
        try {
            setFeature(XmlConstants.FEATURE_XSD, true);
            setNoNamespaceSchemaProperty(XmlConstants.PROPERTY_NO_NAMESPACE_SCHEMA_LOCATION);
            return true;
        } catch (BuildException e) {
            log(e.toString(), 3);
            return false;
        }
    }

    private void setNoNamespaceSchemaProperty(String str) {
        String noNamespaceSchemaURL = getNoNamespaceSchemaURL();
        if (noNamespaceSchemaURL != null) {
            setProperty(str, noNamespaceSchemaURL);
        }
    }

    public boolean enableJAXP12SchemaValidation() {
        try {
            setProperty(XmlConstants.FEATURE_JAXP12_SCHEMA_LANGUAGE, XmlConstants.URI_XSD);
            setNoNamespaceSchemaProperty(XmlConstants.FEATURE_JAXP12_SCHEMA_SOURCE);
            return true;
        } catch (BuildException e) {
            log(e.toString(), 3);
            return false;
        }
    }

    public void addConfiguredSchema(SchemaLocation schemaLocation) {
        log("adding schema " + schemaLocation, 4);
        schemaLocation.validateNamespace();
        SchemaLocation schemaLocation2 = (SchemaLocation) this.schemaLocations.get(schemaLocation.getNamespace());
        if (schemaLocation2 != null && !schemaLocation2.equals(schemaLocation)) {
            throw new BuildException(ERROR_DUPLICATE_SCHEMA + schemaLocation);
        }
        this.schemaLocations.put(schemaLocation.getNamespace(), schemaLocation);
    }

    public void setFullChecking(boolean z) {
        this.fullChecking = z;
    }

    protected void createAnonymousSchema() {
        if (this.anonymousSchema == null) {
            this.anonymousSchema = new SchemaLocation();
        }
        this.anonymousSchema.setNamespace("(no namespace)");
    }

    public void setNoNamespaceURL(String str) {
        createAnonymousSchema();
        this.anonymousSchema.setUrl(str);
    }

    public void setNoNamespaceFile(File file) {
        createAnonymousSchema();
        this.anonymousSchema.setFile(file);
    }

    public void setDisableDTD(boolean z) {
        this.disableDTD = z;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.XMLValidateTask
    protected void initValidator() {
        super.initValidator();
        if (isSax1Parser()) {
            throw new BuildException(ERROR_SAX_1);
        }
        setFeature(XmlConstants.FEATURE_NAMESPACES, true);
        if (!enableXercesSchemaValidation() && !enableJAXP12SchemaValidation()) {
            throw new BuildException(ERROR_NO_XSD_SUPPORT);
        }
        setFeature(XmlConstants.FEATURE_XSD_FULL_VALIDATION, this.fullChecking);
        setFeatureIfSupported(XmlConstants.FEATURE_DISALLOW_DTD, this.disableDTD);
        addSchemaLocations();
    }

    @Override // org.apache.tools.ant.taskdefs.optional.XMLValidateTask
    protected XMLReader createDefaultReader() {
        SAXParserFactory sAXParserFactoryNewInstance = SAXParserFactory.newInstance();
        sAXParserFactoryNewInstance.setValidating(true);
        sAXParserFactoryNewInstance.setNamespaceAware(true);
        try {
            return sAXParserFactoryNewInstance.newSAXParser().getXMLReader();
        } catch (ParserConfigurationException e) {
            throw new BuildException(ERROR_PARSER_CREATION_FAILURE, e);
        } catch (SAXException e2) {
            throw new BuildException(ERROR_PARSER_CREATION_FAILURE, e2);
        }
    }

    protected void addSchemaLocations() {
        Iterator it = this.schemaLocations.values().iterator();
        StringBuffer stringBuffer = new StringBuffer();
        int i = 0;
        while (it.hasNext()) {
            if (i > 0) {
                stringBuffer.append(' ');
            }
            String uRIandLocation = ((SchemaLocation) it.next()).getURIandLocation();
            stringBuffer.append(uRIandLocation);
            log(MESSAGE_ADDING_SCHEMA + uRIandLocation, 3);
            i++;
        }
        if (i > 0) {
            setProperty(XmlConstants.PROPERTY_SCHEMA_LOCATION, stringBuffer.toString());
        }
    }

    protected String getNoNamespaceSchemaURL() {
        SchemaLocation schemaLocation = this.anonymousSchema;
        if (schemaLocation == null) {
            return null;
        }
        return schemaLocation.getSchemaLocationURL();
    }

    protected void setFeatureIfSupported(String str, boolean z) {
        try {
            getXmlReader().setFeature(str, z);
        } catch (SAXNotRecognizedException unused) {
            log("Not recognizied: " + str, 3);
        } catch (SAXNotSupportedException unused2) {
            log("Not supported: " + str, 3);
        }
    }

    @Override // org.apache.tools.ant.taskdefs.optional.XMLValidateTask
    protected void onSuccessfulValidation(int i) {
        log(i + XMLValidateTask.MESSAGE_FILES_VALIDATED, 3);
    }

    public static class SchemaLocation {
        public static final String ERROR_NO_FILE = "File not found: ";
        public static final String ERROR_NO_LOCATION = "No file or URL supplied for the schema ";
        public static final String ERROR_NO_URI = "No namespace URI";
        public static final String ERROR_NO_URL_REPRESENTATION = "Cannot make a URL of ";
        public static final String ERROR_TWO_LOCATIONS = "Both URL and File were given for schema ";
        private File file;
        private String namespace;
        private String url;

        public String getNamespace() {
            return this.namespace;
        }

        public void setNamespace(String str) {
            this.namespace = str;
        }

        public File getFile() {
            return this.file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String str) {
            this.url = str;
        }

        public String getSchemaLocationURL() {
            boolean z = this.file != null;
            boolean zIsSet = isSet(this.url);
            if (!z && !zIsSet) {
                throw new BuildException(ERROR_NO_LOCATION + this.namespace);
            }
            if (z && zIsSet) {
                throw new BuildException(ERROR_TWO_LOCATIONS + this.namespace);
            }
            String str = this.url;
            if (!z) {
                return str;
            }
            if (!this.file.exists()) {
                throw new BuildException(ERROR_NO_FILE + this.file);
            }
            try {
                return FileUtils.getFileUtils().getFileURL(this.file).toString();
            } catch (MalformedURLException e) {
                throw new BuildException(ERROR_NO_URL_REPRESENTATION + this.file, e);
            }
        }

        public String getURIandLocation() throws BuildException {
            validateNamespace();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(this.namespace);
            stringBuffer.append(' ');
            stringBuffer.append(getSchemaLocationURL());
            return new String(stringBuffer);
        }

        public void validateNamespace() {
            if (!isSet(getNamespace())) {
                throw new BuildException(ERROR_NO_URI);
            }
        }

        private boolean isSet(String str) {
            return (str == null || str.length() == 0) ? false : true;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof SchemaLocation)) {
                return false;
            }
            SchemaLocation schemaLocation = (SchemaLocation) obj;
            File file = this.file;
            if (file == null ? schemaLocation.file != null : !file.equals(schemaLocation.file)) {
                return false;
            }
            String str = this.namespace;
            if (str == null ? schemaLocation.namespace != null : !str.equals(schemaLocation.namespace)) {
                return false;
            }
            String str2 = this.url;
            String str3 = schemaLocation.url;
            return str2 == null ? str3 == null : str2.equals(str3);
        }

        public int hashCode() {
            String str = this.namespace;
            int iHashCode = (str != null ? str.hashCode() : 0) * 29;
            File file = this.file;
            int iHashCode2 = (iHashCode + (file != null ? file.hashCode() : 0)) * 29;
            String str2 = this.url;
            return iHashCode2 + (str2 != null ? str2.hashCode() : 0);
        }

        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            String str = this.namespace;
            if (str == null) {
                str = "(anonymous)";
            }
            stringBuffer.append(str);
            stringBuffer.append(' ');
            stringBuffer.append(this.url != null ? this.url + " " : "");
            File file = this.file;
            stringBuffer.append(file != null ? file.getAbsolutePath() : "");
            return stringBuffer.toString();
        }
    }
}
