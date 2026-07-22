package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.XMLCatalog;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes3.dex */
public class XmlProperty extends Task {
    private static final String ID = "id";
    private static final String LOCATION = "location";
    private static final String VALUE = "value";
    private Resource src;
    private static final String REF_ID = "refid";
    private static final String PATH = "path";
    private static final String PATHID = "pathid";
    private static final String[] ATTRIBUTES = {"id", REF_ID, "location", "value", PATH, PATHID};
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private String prefix = "";
    private boolean keepRoot = true;
    private boolean validate = false;
    private boolean collapseAttributes = false;
    private boolean semanticAttributes = false;
    private boolean includeSemanticAttribute = false;
    private File rootDirectory = null;
    private Hashtable addedAttributes = new Hashtable();
    private XMLCatalog xmlCatalog = new XMLCatalog();
    private String delimiter = ",";

    @Override // org.apache.tools.ant.Task
    public void init() {
        super.init();
        this.xmlCatalog.setProject(getProject());
    }

    protected EntityResolver getEntityResolver() {
        return this.xmlCatalog;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        Document document;
        Resource resource = getResource();
        if (resource == null) {
            throw new BuildException("XmlProperty task requires a source resource");
        }
        try {
            log("Loading " + this.src, 3);
            if (resource.isExists()) {
                DocumentBuilderFactory documentBuilderFactoryNewInstance = DocumentBuilderFactory.newInstance();
                documentBuilderFactoryNewInstance.setValidating(this.validate);
                documentBuilderFactoryNewInstance.setNamespaceAware(false);
                DocumentBuilder documentBuilderNewDocumentBuilder = documentBuilderFactoryNewInstance.newDocumentBuilder();
                documentBuilderNewDocumentBuilder.setEntityResolver(getEntityResolver());
                FileProvider fileProvider = (FileProvider) this.src.as(FileProvider.class);
                if (fileProvider != null) {
                    document = documentBuilderNewDocumentBuilder.parse(fileProvider.getFile());
                } else {
                    document = documentBuilderNewDocumentBuilder.parse(this.src.getInputStream());
                }
                Element documentElement = document.getDocumentElement();
                this.addedAttributes = new Hashtable();
                if (this.keepRoot) {
                    addNodeRecursively(documentElement, this.prefix, null);
                    return;
                }
                NodeList childNodes = documentElement.getChildNodes();
                int length = childNodes.getLength();
                for (int i = 0; i < length; i++) {
                    addNodeRecursively(childNodes.item(i), this.prefix, null);
                }
                return;
            }
            log("Unable to find property resource: " + resource, 3);
        } catch (IOException e) {
            throw new BuildException("Failed to load " + this.src, e);
        } catch (ParserConfigurationException e2) {
            throw new BuildException(e2);
        } catch (SAXException e3) {
            Exception exception = e3.getException();
            SAXException exception2 = e3;
            if (exception != null) {
                exception2 = e3.getException();
            }
            throw new BuildException("Failed to load " + this.src, exception2);
        }
    }

    private void addNodeRecursively(Node node, String str, Object obj) {
        if (node.getNodeType() != 3) {
            if (str.trim().length() > 0) {
                str = str + ".";
            }
            str = str + node.getNodeName();
        }
        Object objProcessNode = processNode(node, str, obj);
        if (node.hasChildNodes()) {
            NodeList childNodes = node.getChildNodes();
            int length = childNodes.getLength();
            for (int i = 0; i < length; i++) {
                addNodeRecursively(childNodes.item(i), str, objProcessNode);
            }
        }
    }

    void addNodeRecursively(Node node, String str) {
        addNodeRecursively(node, str, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:109:0x01c9  */
    /* JADX WARN: Removed duplicated region for block: B:118:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:97:0x01aa  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.lang.Object processNode(org.w3c.dom.Node r19, java.lang.String r20, java.lang.Object r21) {
        /*
            Method dump skipped, instruction units count: 459
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.taskdefs.XmlProperty.processNode(org.w3c.dom.Node, java.lang.String, java.lang.Object):java.lang.Object");
    }

    private void addProperty(String str, String str2, String str3) {
        String str4 = str + ":" + str2;
        if (str3 != null) {
            str4 = str4 + "(id=" + str3 + ")";
        }
        log(str4, 4);
        if (this.addedAttributes.containsKey(str)) {
            str2 = ((String) this.addedAttributes.get(str)) + getDelimiter() + str2;
            getProject().setProperty(str, str2);
            this.addedAttributes.put(str, str2);
        } else if (getProject().getProperty(str) == null) {
            getProject().setNewProperty(str, str2);
            this.addedAttributes.put(str, str2);
        } else {
            log("Override ignored for property " + str, 3);
        }
        if (str3 != null) {
            getProject().addReference(str3, str2);
        }
    }

    private String getAttributeName(Node node) {
        String nodeName = node.getNodeName();
        if (this.semanticAttributes) {
            return nodeName.equals(REF_ID) ? "" : (!isSemanticAttribute(nodeName) || this.includeSemanticAttribute) ? "." + nodeName : "";
        }
        return (this.collapseAttributes ? new StringBuilder().append(".").append(nodeName) : new StringBuilder().append("(").append(nodeName).append(")")).toString();
    }

    private static boolean isSemanticAttribute(String str) {
        int i = 0;
        while (true) {
            String[] strArr = ATTRIBUTES;
            if (i >= strArr.length) {
                return false;
            }
            if (str.equals(strArr[i])) {
                return true;
            }
            i++;
        }
    }

    private String getAttributeValue(Node node) {
        Object reference;
        String strTrim = node.getNodeValue().trim();
        if (this.semanticAttributes) {
            String nodeName = node.getNodeName();
            strTrim = getProject().replaceProperties(strTrim);
            if (nodeName.equals("location")) {
                return resolveFile(strTrim).getPath();
            }
            if (nodeName.equals(REF_ID) && (reference = getProject().getReference(strTrim)) != null) {
                return reference.toString();
            }
        }
        return strTrim;
    }

    public void setFile(File file) {
        setSrcResource(new FileResource(file));
    }

    public void setSrcResource(Resource resource) {
        if (resource.isDirectory()) {
            throw new BuildException("the source can't be a directory");
        }
        if (resource.as(FileProvider.class) != null || supportsNonFileResources()) {
            this.src = resource;
            return;
        }
        throw new BuildException("Only FileSystem resources are supported.");
    }

    public void addConfigured(ResourceCollection resourceCollection) {
        if (resourceCollection.size() != 1) {
            throw new BuildException("only single argument resource collections are supported as archives");
        }
        setSrcResource(resourceCollection.iterator().next());
    }

    public void setPrefix(String str) {
        this.prefix = str.trim();
    }

    public void setKeeproot(boolean z) {
        this.keepRoot = z;
    }

    public void setValidate(boolean z) {
        this.validate = z;
    }

    public void setCollapseAttributes(boolean z) {
        this.collapseAttributes = z;
    }

    public void setSemanticAttributes(boolean z) {
        this.semanticAttributes = z;
    }

    public void setRootDirectory(File file) {
        this.rootDirectory = file;
    }

    public void setIncludeSemanticAttribute(boolean z) {
        this.includeSemanticAttribute = z;
    }

    public void addConfiguredXMLCatalog(XMLCatalog xMLCatalog) {
        this.xmlCatalog.addConfiguredXMLCatalog(xMLCatalog);
    }

    protected File getFile() {
        FileProvider fileProvider = (FileProvider) this.src.as(FileProvider.class);
        if (fileProvider != null) {
            return fileProvider.getFile();
        }
        return null;
    }

    protected Resource getResource() {
        File file = getFile();
        FileProvider fileProvider = (FileProvider) this.src.as(FileProvider.class);
        return (file != null && (fileProvider == null || !fileProvider.getFile().equals(file))) ? new FileResource(file) : this.src;
    }

    protected String getPrefix() {
        return this.prefix;
    }

    protected boolean getKeeproot() {
        return this.keepRoot;
    }

    protected boolean getValidate() {
        return this.validate;
    }

    protected boolean getCollapseAttributes() {
        return this.collapseAttributes;
    }

    protected boolean getSemanticAttributes() {
        return this.semanticAttributes;
    }

    protected File getRootDirectory() {
        return this.rootDirectory;
    }

    protected boolean getIncludeSementicAttribute() {
        return this.includeSemanticAttribute;
    }

    private File resolveFile(String str) {
        FileUtils fileUtils = FILE_UTILS;
        File baseDir = this.rootDirectory;
        if (baseDir == null) {
            baseDir = getProject().getBaseDir();
        }
        return fileUtils.resolveFile(baseDir, str);
    }

    protected boolean supportsNonFileResources() {
        return getClass().equals(XmlProperty.class);
    }

    public String getDelimiter() {
        return this.delimiter;
    }

    public void setDelimiter(String str) {
        this.delimiter = str;
    }
}
