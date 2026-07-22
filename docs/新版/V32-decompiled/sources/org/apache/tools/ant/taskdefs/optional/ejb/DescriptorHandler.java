package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import org.apache.tools.ant.Task;
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes3.dex */
public class DescriptorHandler extends HandlerBase {
    private static final String BEAN_CLASS = "ejb-class";
    private static final int DEFAULT_HASH_TABLE_SIZE = 10;
    private static final String EJB_JAR = "ejb-jar";
    private static final String EJB_LOCAL_REF = "ejb-local-ref";
    private static final String EJB_NAME = "ejb-name";
    private static final String EJB_REF = "ejb-ref";
    private static final String ENTERPRISE_BEANS = "enterprise-beans";
    private static final String ENTITY_BEAN = "entity";
    private static final String HOME_INTERFACE = "home";
    private static final String LOCAL_HOME_INTERFACE = "local-home";
    private static final String LOCAL_INTERFACE = "local";
    private static final String MESSAGE_BEAN = "message-driven";
    private static final String PK_CLASS = "prim-key-class";
    private static final String REMOTE_INTERFACE = "remote";
    private static final String SESSION_BEAN = "session";
    private static final int STATE_IN_BEANS = 3;
    private static final int STATE_IN_EJBJAR = 2;
    private static final int STATE_IN_ENTITY = 5;
    private static final int STATE_IN_MESSAGE = 6;
    private static final int STATE_IN_SESSION = 4;
    private static final int STATE_LOOKING_EJBJAR = 1;
    private Task owningTask;
    private File srcDir;
    private String publicId = null;
    private int parseState = 1;
    protected String currentElement = null;
    protected String currentText = null;
    protected Hashtable ejbFiles = null;
    protected String ejbName = null;
    private Hashtable fileDTDs = new Hashtable();
    private Hashtable resourceDTDs = new Hashtable();
    private boolean inEJBRef = false;
    private Hashtable urlDTDs = new Hashtable();

    public DescriptorHandler(Task task, File file) {
        this.owningTask = task;
        this.srcDir = file;
    }

    public void registerDTD(String str, String str2) {
        if (str2 == null) {
            return;
        }
        File file = new File(str2);
        if (!file.exists()) {
            file = this.owningTask.getProject().resolveFile(str2);
        }
        if (file.exists()) {
            if (str != null) {
                this.fileDTDs.put(str, file);
                this.owningTask.log("Mapped publicId " + str + " to file " + file, 3);
                return;
            }
            return;
        }
        if (getClass().getResource(str2) != null && str != null) {
            this.resourceDTDs.put(str, str2);
            this.owningTask.log("Mapped publicId " + str + " to resource " + str2, 3);
        }
        if (str != null) {
            try {
                this.urlDTDs.put(str, new URL(str2));
            } catch (MalformedURLException unused) {
            }
        }
    }

    @Override // org.xml.sax.HandlerBase, org.xml.sax.EntityResolver
    public InputSource resolveEntity(String str, String str2) throws SAXException {
        InputStream resourceAsStream;
        this.publicId = str;
        File file = (File) this.fileDTDs.get(str);
        if (file != null) {
            try {
                this.owningTask.log("Resolved " + str + " to local file " + file, 3);
                return new InputSource(new FileInputStream(file));
            } catch (FileNotFoundException unused) {
            }
        }
        String str3 = (String) this.resourceDTDs.get(str);
        if (str3 != null && (resourceAsStream = getClass().getResourceAsStream(str3)) != null) {
            this.owningTask.log("Resolved " + str + " to local resource " + str3, 3);
            return new InputSource(resourceAsStream);
        }
        URL url = (URL) this.urlDTDs.get(str);
        if (url != null) {
            try {
                InputStream inputStreamOpenStream = url.openStream();
                this.owningTask.log("Resolved " + str + " to url " + url, 3);
                return new InputSource(inputStreamOpenStream);
            } catch (IOException unused2) {
            }
        }
        this.owningTask.log("Could not resolve ( publicId: " + str + ", systemId: " + str2 + ") to a local entity", 2);
        return null;
    }

    public Hashtable getFiles() {
        Hashtable hashtable = this.ejbFiles;
        return hashtable == null ? new Hashtable() : hashtable;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public String getEjbName() {
        return this.ejbName;
    }

    @Override // org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
    public void startDocument() throws SAXException {
        this.ejbFiles = new Hashtable(10, 1.0f);
        this.currentElement = null;
        this.inEJBRef = false;
    }

    @Override // org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
    public void startElement(String str, AttributeList attributeList) throws SAXException {
        this.currentElement = str;
        this.currentText = "";
        if (str.equals(EJB_REF) || str.equals(EJB_LOCAL_REF)) {
            this.inEJBRef = true;
            return;
        }
        if (this.parseState == 1 && str.equals(EJB_JAR)) {
            this.parseState = 2;
            return;
        }
        if (this.parseState == 2 && str.equals(ENTERPRISE_BEANS)) {
            this.parseState = 3;
            return;
        }
        if (this.parseState == 3 && str.equals(SESSION_BEAN)) {
            this.parseState = 4;
            return;
        }
        if (this.parseState == 3 && str.equals("entity")) {
            this.parseState = 5;
        } else if (this.parseState == 3 && str.equals(MESSAGE_BEAN)) {
            this.parseState = 6;
        }
    }

    @Override // org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
    public void endElement(String str) throws SAXException {
        processElement();
        this.currentText = "";
        this.currentElement = "";
        if (str.equals(EJB_REF) || str.equals(EJB_LOCAL_REF)) {
            this.inEJBRef = false;
            return;
        }
        if (this.parseState == 5 && str.equals("entity")) {
            this.parseState = 3;
            return;
        }
        if (this.parseState == 4 && str.equals(SESSION_BEAN)) {
            this.parseState = 3;
            return;
        }
        if (this.parseState == 6 && str.equals(MESSAGE_BEAN)) {
            this.parseState = 3;
            return;
        }
        if (this.parseState == 3 && str.equals(ENTERPRISE_BEANS)) {
            this.parseState = 2;
        } else if (this.parseState == 2 && str.equals(EJB_JAR)) {
            this.parseState = 1;
        }
    }

    @Override // org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
    public void characters(char[] cArr, int i, int i2) throws SAXException {
        this.currentText += new String(cArr, i, i2);
    }

    protected void processElement() {
        if (this.inEJBRef) {
            return;
        }
        int i = this.parseState;
        if (i == 5 || i == 4 || i == 6) {
            if (this.currentElement.equals("home") || this.currentElement.equals(REMOTE_INTERFACE) || this.currentElement.equals("local") || this.currentElement.equals(LOCAL_HOME_INTERFACE) || this.currentElement.equals(BEAN_CLASS) || this.currentElement.equals(PK_CLASS)) {
                String strTrim = this.currentText.trim();
                if (!strTrim.startsWith("java.") && !strTrim.startsWith("javax.")) {
                    String str = strTrim.replace('.', File.separatorChar) + ".class";
                    this.ejbFiles.put(str, new File(this.srcDir, str));
                }
            }
            if (this.currentElement.equals("ejb-name") && this.ejbName == null) {
                this.ejbName = this.currentText.trim();
            }
        }
    }
}
