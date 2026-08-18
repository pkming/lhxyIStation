package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.tools.ant.taskdefs.rmic.DefaultRmicAdapter;
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes3.dex */
public class IPlanetEjbc {
    private static final String ENTITY_BEAN = "entity";
    private static final int MAX_NUM_ARGS = 8;
    private static final int MIN_NUM_ARGS = 2;
    private static final int NUM_CLASSES_WITHOUT_IIOP = 9;
    private static final int NUM_CLASSES_WITH_IIOP = 15;
    private static final String STATEFUL_SESSION = "stateful";
    private static final String STATELESS_SESSION = "stateless";
    private String classpath;
    private String[] classpathElements;
    private File destDirectory;
    private String displayName;
    private File iasDescriptor;
    private File iasHomeDir;
    private SAXParser parser;
    private File stdDescriptor;
    private boolean retainSource = false;
    private boolean debugOutput = false;
    private EjbcHandler handler = new EjbcHandler();
    private Hashtable ejbFiles = new Hashtable();

    public IPlanetEjbc(File file, File file2, File file3, String str, SAXParser sAXParser) {
        this.stdDescriptor = file;
        this.iasDescriptor = file2;
        this.destDirectory = file3;
        this.classpath = str;
        this.parser = sAXParser;
        ArrayList arrayList = new ArrayList();
        if (str != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(str, File.pathSeparator);
            while (stringTokenizer.hasMoreTokens()) {
                arrayList.add(stringTokenizer.nextToken());
            }
            this.classpathElements = (String[]) arrayList.toArray(new String[arrayList.size()]);
        }
    }

    public void setRetainSource(boolean z) {
        this.retainSource = z;
    }

    public void setDebugOutput(boolean z) {
        this.debugOutput = z;
    }

    public void registerDTD(String str, String str2) {
        this.handler.registerDTD(str, str2);
    }

    public void setIasHomeDir(File file) {
        this.iasHomeDir = file;
    }

    public Hashtable getEjbFiles() {
        return this.ejbFiles;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String[] getCmpDescriptors() {
        ArrayList arrayList = new ArrayList();
        for (EjbInfo ejbInfo : this.handler.getEjbs()) {
            arrayList.addAll(ejbInfo.getCmpDescriptors());
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    public static void main(String[] strArr) {
        if (strArr.length < 2 || strArr.length > 8) {
            usage();
            return;
        }
        File file = new File(strArr[strArr.length - 2]);
        File file2 = new File(strArr[strArr.length - 1]);
        String str = null;
        int i = 0;
        boolean z = false;
        boolean z2 = false;
        File file3 = null;
        while (i < strArr.length - 2) {
            if (strArr[i].equals("-classpath")) {
                i++;
                str = strArr[i];
            } else if (strArr[i].equals("-d")) {
                i++;
                file3 = new File(strArr[i]);
            } else if (strArr[i].equals("-debug")) {
                z = true;
            } else {
                if (!strArr[i].equals("-keepsource")) {
                    usage();
                    return;
                }
                z2 = true;
            }
            i++;
        }
        String property = str == null ? System.getProperties().getProperty("java.class.path") : str;
        File file4 = file3 == null ? new File(System.getProperties().getProperty("user.dir")) : file3;
        SAXParserFactory sAXParserFactoryNewInstance = SAXParserFactory.newInstance();
        sAXParserFactoryNewInstance.setValidating(true);
        try {
            IPlanetEjbc iPlanetEjbc = new IPlanetEjbc(file, file2, file4, property, sAXParserFactoryNewInstance.newSAXParser());
            iPlanetEjbc.setDebugOutput(z);
            iPlanetEjbc.setRetainSource(z2);
            try {
                iPlanetEjbc.execute();
            } catch (IOException e) {
                System.out.println("An IOException has occurred while reading the XML descriptors (" + e.getMessage() + ").");
            } catch (EjbcException e2) {
                System.out.println("An error has occurred while executing the ejbc utility (" + e2.getMessage() + ").");
            } catch (SAXException e3) {
                System.out.println("A SAXException has occurred while reading the XML descriptors (" + e3.getMessage() + ").");
            }
        } catch (Exception e4) {
            System.out.println("An exception was generated while trying to ");
            System.out.println("create a new SAXParser.");
            e4.printStackTrace();
        }
    }

    private static void usage() {
        System.out.println("java org.apache.tools.ant.taskdefs.optional.ejb.IPlanetEjbc \\");
        System.out.println("  [OPTIONS] [EJB 1.1 descriptor] [iAS EJB descriptor]");
        System.out.println("");
        System.out.println("Where OPTIONS are:");
        System.out.println("  -debug -- for additional debugging output");
        System.out.println("  -keepsource -- to retain Java source files generated");
        System.out.println("  -classpath [classpath] -- classpath used for compilation");
        System.out.println("  -d [destination directory] -- directory for compiled classes");
        System.out.println("");
        System.out.println("If a classpath is not specified, the system classpath");
        System.out.println("will be used.  If a destination directory is not specified,");
        System.out.println("the current working directory will be used (classes will");
        System.out.println("still be placed in subfolders which correspond to their");
        System.out.println("package name).");
        System.out.println("");
        System.out.println("The EJB home interface, remote interface, and implementation");
        System.out.println("class must be found in the destination directory.  In");
        System.out.println("addition, the destination will look for the stubs and skeletons");
        System.out.println("in the destination directory to ensure they are up to date.");
    }

    public void execute() throws EjbcException, SAXException, IOException {
        checkConfiguration();
        EjbInfo[] ejbs = getEjbs();
        for (EjbInfo ejbInfo : ejbs) {
            log("EJBInfo...");
            log(ejbInfo.toString());
        }
        for (EjbInfo ejbInfo2 : ejbs) {
            ejbInfo2.checkConfiguration(this.destDirectory);
            if (ejbInfo2.mustBeRecompiled(this.destDirectory)) {
                log(ejbInfo2.getName() + " must be recompiled using ejbc.");
                callEjbc(buildArgumentList(ejbInfo2));
            } else {
                log(ejbInfo2.getName() + " is up to date.");
            }
        }
    }

    private void callEjbc(String[] strArr) {
        StringBuffer stringBuffer = new StringBuffer();
        for (String str : strArr) {
            stringBuffer.append(str).append(" ");
        }
        String str2 = (this.iasHomeDir == null ? "" : this.iasHomeDir.toString() + File.separator + "bin" + File.separator) + "ejbc ";
        log(str2 + ((Object) stringBuffer));
        try {
            Process processExec = Runtime.getRuntime().exec(str2 + ((Object) stringBuffer));
            RedirectOutput redirectOutput = new RedirectOutput(processExec.getInputStream());
            RedirectOutput redirectOutput2 = new RedirectOutput(processExec.getErrorStream());
            redirectOutput.start();
            redirectOutput2.start();
            processExec.waitFor();
            processExec.destroy();
        } catch (IOException e) {
            log("An IOException has occurred while trying to execute ejbc.");
            e.printStackTrace();
        } catch (InterruptedException unused) {
        }
    }

    protected void checkConfiguration() throws EjbcException {
        String str = this.stdDescriptor == null ? "A standard XML descriptor file must be specified.  " : "";
        if (this.iasDescriptor == null) {
            str = str + "An iAS-specific XML descriptor file must be specified.  ";
        }
        if (this.classpath == null) {
            str = str + "A classpath must be specified.    ";
        }
        if (this.parser == null) {
            str = str + "An XML parser must be specified.    ";
        }
        File file = this.destDirectory;
        if (file == null) {
            str = str + "A destination directory must be specified.  ";
        } else if (!file.exists()) {
            str = str + "The destination directory specified does not exist.  ";
        } else if (!this.destDirectory.isDirectory()) {
            str = str + "The destination specified is not a directory.  ";
        }
        if (str.length() > 0) {
            throw new EjbcException(str);
        }
    }

    private EjbInfo[] getEjbs() throws SAXException, IOException {
        this.parser.parse(this.stdDescriptor, this.handler);
        this.parser.parse(this.iasDescriptor, this.handler);
        return this.handler.getEjbs();
    }

    private String[] buildArgumentList(EjbInfo ejbInfo) {
        ArrayList arrayList = new ArrayList();
        if (this.debugOutput) {
            arrayList.add("-debug");
        }
        if (ejbInfo.getBeantype().equals(STATELESS_SESSION)) {
            arrayList.add("-sl");
        } else if (ejbInfo.getBeantype().equals(STATEFUL_SESSION)) {
            arrayList.add("-sf");
        }
        if (ejbInfo.getIiop()) {
            arrayList.add("-iiop");
        }
        if (ejbInfo.getCmp()) {
            arrayList.add("-cmp");
        }
        if (this.retainSource) {
            arrayList.add("-gs");
        }
        if (ejbInfo.getHasession()) {
            arrayList.add("-fo");
        }
        arrayList.add("-classpath");
        arrayList.add(this.classpath);
        arrayList.add("-d");
        arrayList.add(this.destDirectory.toString());
        arrayList.add(ejbInfo.getHome().getQualifiedClassName());
        arrayList.add(ejbInfo.getRemote().getQualifiedClassName());
        arrayList.add(ejbInfo.getImplementation().getQualifiedClassName());
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String str) {
        if (this.debugOutput) {
            System.out.println(str);
        }
    }

    public class EjbcException extends Exception {
        public EjbcException(String str) {
            super(str);
        }
    }

    private class EjbcHandler extends HandlerBase {
        private static final String DEFAULT_IAS60_DTD_LOCATION = "IASEjb_jar_1_0.dtd";
        private static final String DEFAULT_IAS60_EJB11_DTD_LOCATION = "ejb-jar_1_1.dtd";
        private static final String PUBLICID_EJB11 = "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN";
        private static final String PUBLICID_IPLANET_EJB_60 = "-//Sun Microsystems, Inc.//DTD iAS Enterprise JavaBeans 1.0//EN";
        private EjbInfo currentEjb;
        private String currentText;
        private String ejbType;
        private Map resourceDtds = new HashMap();
        private Map fileDtds = new HashMap();
        private Map ejbs = new HashMap();
        private boolean iasDescriptor = false;
        private String currentLoc = "";

        public EjbcHandler() {
            registerDTD("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN", DEFAULT_IAS60_EJB11_DTD_LOCATION);
            registerDTD(PUBLICID_IPLANET_EJB_60, DEFAULT_IAS60_DTD_LOCATION);
        }

        public EjbInfo[] getEjbs() {
            return (EjbInfo[]) this.ejbs.values().toArray(new EjbInfo[this.ejbs.size()]);
        }

        public String getDisplayName() {
            return IPlanetEjbc.this.displayName;
        }

        public void registerDTD(String str, String str2) {
            IPlanetEjbc.this.log("Registering: " + str2);
            if (str == null || str2 == null) {
                return;
            }
            if (ClassLoader.getSystemResource(str2) != null) {
                IPlanetEjbc.this.log("Found resource: " + str2);
                this.resourceDtds.put(str, str2);
                return;
            }
            File file = new File(str2);
            if (file.exists() && file.isFile()) {
                IPlanetEjbc.this.log("Found file: " + str2);
                this.fileDtds.put(str, str2);
            }
        }

        @Override // org.xml.sax.HandlerBase, org.xml.sax.EntityResolver
        public InputSource resolveEntity(String str, String str2) throws SAXException {
            InputStream fileInputStream;
            try {
                String str3 = (String) this.resourceDtds.get(str);
                if (str3 != null) {
                    fileInputStream = ClassLoader.getSystemResource(str3).openStream();
                } else {
                    String str4 = (String) this.fileDtds.get(str);
                    fileInputStream = str4 != null ? new FileInputStream(str4) : null;
                }
                if (fileInputStream == null) {
                    return super.resolveEntity(str, str2);
                }
                return new InputSource(fileInputStream);
            } catch (IOException unused) {
                return super.resolveEntity(str, str2);
            }
        }

        @Override // org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
        public void startElement(String str, AttributeList attributeList) throws SAXException {
            String str2 = this.currentLoc + "\\" + str;
            this.currentLoc = str2;
            this.currentText = "";
            if (str2.equals("\\ejb-jar")) {
                this.iasDescriptor = false;
            } else if (this.currentLoc.equals("\\ias-ejb-jar")) {
                this.iasDescriptor = true;
            }
            if (str.equals("session") || str.equals("entity")) {
                this.ejbType = str;
            }
        }

        @Override // org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
        public void characters(char[] cArr, int i, int i2) throws SAXException {
            this.currentText += new String(cArr).substring(i, i2 + i);
        }

        @Override // org.xml.sax.HandlerBase, org.xml.sax.DocumentHandler
        public void endElement(String str) throws SAXException {
            if (this.iasDescriptor) {
                iasCharacters(this.currentText);
            } else {
                stdCharacters(this.currentText);
            }
            int length = str.length() + 1;
            this.currentLoc = this.currentLoc.substring(0, this.currentLoc.length() - length);
        }

        private void stdCharacters(String str) {
            if (this.currentLoc.equals("\\ejb-jar\\display-name")) {
                IPlanetEjbc.this.displayName = str;
                return;
            }
            String str2 = "\\ejb-jar\\enterprise-beans\\" + this.ejbType;
            if (this.currentLoc.equals(str2 + "\\ejb-name")) {
                EjbInfo ejbInfo = (EjbInfo) this.ejbs.get(str);
                this.currentEjb = ejbInfo;
                if (ejbInfo == null) {
                    EjbInfo ejbInfo2 = IPlanetEjbc.this.new EjbInfo(str);
                    this.currentEjb = ejbInfo2;
                    this.ejbs.put(str, ejbInfo2);
                    return;
                }
                return;
            }
            if (this.currentLoc.equals(str2 + "\\home")) {
                this.currentEjb.setHome(str);
                return;
            }
            if (this.currentLoc.equals(str2 + "\\remote")) {
                this.currentEjb.setRemote(str);
                return;
            }
            if (this.currentLoc.equals(str2 + "\\ejb-class")) {
                this.currentEjb.setImplementation(str);
                return;
            }
            if (this.currentLoc.equals(str2 + "\\prim-key-class")) {
                this.currentEjb.setPrimaryKey(str);
            } else if (this.currentLoc.equals(str2 + "\\session-type")) {
                this.currentEjb.setBeantype(str);
            } else if (this.currentLoc.equals(str2 + "\\persistence-type")) {
                this.currentEjb.setCmp(str);
            }
        }

        private void iasCharacters(String str) {
            String str2 = "\\ias-ejb-jar\\enterprise-beans\\" + this.ejbType;
            if (this.currentLoc.equals(str2 + "\\ejb-name")) {
                EjbInfo ejbInfo = (EjbInfo) this.ejbs.get(str);
                this.currentEjb = ejbInfo;
                if (ejbInfo == null) {
                    EjbInfo ejbInfo2 = IPlanetEjbc.this.new EjbInfo(str);
                    this.currentEjb = ejbInfo2;
                    this.ejbs.put(str, ejbInfo2);
                    return;
                }
                return;
            }
            if (this.currentLoc.equals(str2 + "\\iiop")) {
                this.currentEjb.setIiop(str);
            } else if (this.currentLoc.equals(str2 + "\\failover-required")) {
                this.currentEjb.setHasession(str);
            } else if (this.currentLoc.equals(str2 + "\\persistence-manager\\properties-file-location")) {
                this.currentEjb.addCmpDescriptor(str);
            }
        }
    }

    private class EjbInfo {
        private Classname home;
        private Classname implementation;
        private String name;
        private Classname primaryKey;
        private Classname remote;
        private String beantype = "entity";
        private boolean cmp = false;
        private boolean iiop = false;
        private boolean hasession = false;
        private List cmpDescriptors = new ArrayList();

        public EjbInfo(String str) {
            this.name = str;
        }

        public String getName() {
            String str = this.name;
            if (str != null) {
                return str;
            }
            Classname classname = this.implementation;
            return classname == null ? "[unnamed]" : classname.getClassName();
        }

        public void setHome(String str) {
            setHome(new Classname(str));
        }

        public void setHome(Classname classname) {
            this.home = classname;
        }

        public Classname getHome() {
            return this.home;
        }

        public void setRemote(String str) {
            setRemote(new Classname(str));
        }

        public void setRemote(Classname classname) {
            this.remote = classname;
        }

        public Classname getRemote() {
            return this.remote;
        }

        public void setImplementation(String str) {
            setImplementation(new Classname(str));
        }

        public void setImplementation(Classname classname) {
            this.implementation = classname;
        }

        public Classname getImplementation() {
            return this.implementation;
        }

        public void setPrimaryKey(String str) {
            setPrimaryKey(new Classname(str));
        }

        public void setPrimaryKey(Classname classname) {
            this.primaryKey = classname;
        }

        public Classname getPrimaryKey() {
            return this.primaryKey;
        }

        public void setBeantype(String str) {
            this.beantype = str.toLowerCase();
        }

        public String getBeantype() {
            return this.beantype;
        }

        public void setCmp(boolean z) {
            this.cmp = z;
        }

        public void setCmp(String str) {
            setCmp(str.equals("Container"));
        }

        public boolean getCmp() {
            return this.cmp;
        }

        public void setIiop(boolean z) {
            this.iiop = z;
        }

        public void setIiop(String str) {
            setIiop(str.equals("true"));
        }

        public boolean getIiop() {
            return this.iiop;
        }

        public void setHasession(boolean z) {
            this.hasession = z;
        }

        public void setHasession(String str) {
            setHasession(str.equals("true"));
        }

        public boolean getHasession() {
            return this.hasession;
        }

        public void addCmpDescriptor(String str) {
            this.cmpDescriptors.add(str);
        }

        public List getCmpDescriptors() {
            return this.cmpDescriptors;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void checkConfiguration(File file) throws EjbcException {
            if (this.home == null) {
                throw IPlanetEjbc.this.new EjbcException("A home interface was not found for the " + this.name + " EJB.");
            }
            if (this.remote == null) {
                throw IPlanetEjbc.this.new EjbcException("A remote interface was not found for the " + this.name + " EJB.");
            }
            if (this.implementation == null) {
                throw IPlanetEjbc.this.new EjbcException("An EJB implementation class was not found for the " + this.name + " EJB.");
            }
            if (!this.beantype.equals("entity") && !this.beantype.equals(IPlanetEjbc.STATELESS_SESSION) && !this.beantype.equals(IPlanetEjbc.STATEFUL_SESSION)) {
                throw IPlanetEjbc.this.new EjbcException("The beantype found (" + this.beantype + ") isn't valid in the " + this.name + " EJB.");
            }
            if (this.cmp && !this.beantype.equals("entity")) {
                System.out.println("CMP stubs and skeletons may not be generated for a Session Bean -- the \"cmp\" attribute will be ignoredfor the " + this.name + " EJB.");
            }
            if (this.hasession && !this.beantype.equals(IPlanetEjbc.STATEFUL_SESSION)) {
                System.out.println("Highly available stubs and skeletons may only be generated for a Stateful Session Bean -- the \"hasession\" attribute will be ignored for the " + this.name + " EJB.");
            }
            if (!this.remote.getClassFile(file).exists()) {
                throw IPlanetEjbc.this.new EjbcException("The remote interface " + this.remote.getQualifiedClassName() + " could not be found.");
            }
            if (!this.home.getClassFile(file).exists()) {
                throw IPlanetEjbc.this.new EjbcException("The home interface " + this.home.getQualifiedClassName() + " could not be found.");
            }
            if (!this.implementation.getClassFile(file).exists()) {
                throw IPlanetEjbc.this.new EjbcException("The EJB implementation class " + this.implementation.getQualifiedClassName() + " could not be found.");
            }
        }

        public boolean mustBeRecompiled(File file) {
            return destClassesModified(file) < sourceClassesModified(file);
        }

        private long sourceClassesModified(File file) {
            File classFile;
            File classFile2 = this.remote.getClassFile(file);
            long jLastModified = classFile2.lastModified();
            if (jLastModified == -1) {
                System.out.println("The class " + this.remote.getQualifiedClassName() + " couldn't be found on the classpath");
                return -1L;
            }
            File classFile3 = this.home.getClassFile(file);
            long jLastModified2 = classFile3.lastModified();
            if (jLastModified2 == -1) {
                System.out.println("The class " + this.home.getQualifiedClassName() + " couldn't be found on the classpath");
                return -1L;
            }
            long jMax = Math.max(jLastModified, jLastModified2);
            Classname classname = this.primaryKey;
            if (classname != null) {
                classFile = classname.getClassFile(file);
                long jLastModified3 = classFile.lastModified();
                if (jLastModified3 == -1) {
                    System.out.println("The class " + this.primaryKey.getQualifiedClassName() + "couldn't be found on the classpath");
                    return -1L;
                }
                jMax = Math.max(jMax, jLastModified3);
            } else {
                classFile = null;
            }
            File classFile4 = this.implementation.getClassFile(file);
            if (classFile4.lastModified() == -1) {
                System.out.println("The class " + this.implementation.getQualifiedClassName() + " couldn't be found on the classpath");
                return -1L;
            }
            IPlanetEjbc.this.ejbFiles.put(this.remote.getQualifiedClassName().replace('.', File.separatorChar) + ".class", classFile2);
            IPlanetEjbc.this.ejbFiles.put(this.home.getQualifiedClassName().replace('.', File.separatorChar) + ".class", classFile3);
            IPlanetEjbc.this.ejbFiles.put(this.implementation.getQualifiedClassName().replace('.', File.separatorChar) + ".class", classFile4);
            if (classFile != null) {
                IPlanetEjbc.this.ejbFiles.put(this.primaryKey.getQualifiedClassName().replace('.', File.separatorChar) + ".class", classFile);
            }
            return jMax;
        }

        private long destClassesModified(File file) {
            String[] strArrClassesToGenerate = classesToGenerate();
            long time = new Date().getTime();
            boolean z = true;
            for (String str : strArrClassesToGenerate) {
                String str2 = str.replace('.', File.separatorChar) + ".class";
                File file2 = new File(file, str2);
                IPlanetEjbc.this.ejbFiles.put(str2, file2);
                z = z && file2.exists();
                if (z) {
                    time = Math.min(time, file2.lastModified());
                }
            }
            if (z) {
                return time;
            }
            return -1L;
        }

        private String[] classesToGenerate() {
            String[] strArr = this.iiop ? new String[15] : new String[9];
            String str = this.remote.getPackageName() + ".";
            String className = this.remote.getClassName();
            String str2 = this.home.getPackageName() + ".";
            String className2 = this.home.getClassName();
            String str3 = this.implementation.getPackageName() + ".";
            String qualifiedWithUnderscores = this.implementation.getQualifiedWithUnderscores();
            strArr[0] = str3 + "ejb_fac_" + qualifiedWithUnderscores;
            strArr[1] = str3 + "ejb_home_" + qualifiedWithUnderscores;
            strArr[2] = str3 + "ejb_skel_" + qualifiedWithUnderscores;
            strArr[3] = str + "ejb_kcp_skel_" + className;
            strArr[4] = str2 + "ejb_kcp_skel_" + className2;
            strArr[5] = str + "ejb_kcp_stub_" + className;
            strArr[6] = str2 + "ejb_kcp_stub_" + className2;
            strArr[7] = str + "ejb_stub_" + className;
            strArr[8] = str2 + "ejb_stub_" + className2;
            if (!this.iiop) {
                return strArr;
            }
            strArr[9] = "org.omg.stub." + str + "_" + className + DefaultRmicAdapter.RMI_STUB_SUFFIX;
            strArr[10] = "org.omg.stub." + str2 + "_" + className2 + DefaultRmicAdapter.RMI_STUB_SUFFIX;
            strArr[11] = "org.omg.stub." + str + "_ejb_RmiCorbaBridge_" + className + DefaultRmicAdapter.RMI_TIE_SUFFIX;
            strArr[12] = "org.omg.stub." + str2 + "_ejb_RmiCorbaBridge_" + className2 + DefaultRmicAdapter.RMI_TIE_SUFFIX;
            strArr[13] = str + "ejb_RmiCorbaBridge_" + className;
            strArr[14] = str2 + "ejb_RmiCorbaBridge_" + className2;
            return strArr;
        }

        public String toString() {
            String str = "EJB name: " + this.name + "\n\r              home:      " + this.home + "\n\r              remote:    " + this.remote + "\n\r              impl:      " + this.implementation + "\n\r              primaryKey: " + this.primaryKey + "\n\r              beantype:  " + this.beantype + "\n\r              cmp:       " + this.cmp + "\n\r              iiop:      " + this.iiop + "\n\r              hasession: " + this.hasession;
            Iterator it = this.cmpDescriptors.iterator();
            while (it.hasNext()) {
                str = str + "\n\r              CMP Descriptor: " + it.next();
            }
            return str;
        }
    }

    private static class Classname {
        private String className;
        private String packageName;
        private String qualifiedName;

        public Classname(String str) {
            if (str == null) {
                return;
            }
            this.qualifiedName = str;
            int iLastIndexOf = str.lastIndexOf(46);
            if (iLastIndexOf == -1) {
                this.className = str;
                this.packageName = "";
            } else {
                this.packageName = str.substring(0, iLastIndexOf);
                this.className = str.substring(iLastIndexOf + 1);
            }
        }

        public String getQualifiedClassName() {
            return this.qualifiedName;
        }

        public String getPackageName() {
            return this.packageName;
        }

        public String getClassName() {
            return this.className;
        }

        public String getQualifiedWithUnderscores() {
            return this.qualifiedName.replace('.', '_');
        }

        public File getClassFile(File file) {
            return new File(file, this.qualifiedName.replace('.', File.separatorChar) + ".class");
        }

        public String toString() {
            return getQualifiedClassName();
        }
    }

    private static class RedirectOutput extends Thread {
        private InputStream stream;

        public RedirectOutput(InputStream inputStream) {
            this.stream = inputStream;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.stream));
            while (true) {
                try {
                    try {
                        String line = bufferedReader.readLine();
                        if (line != null) {
                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        return;
                    } catch (IOException unused) {
                        return;
                    }
                } finally {
                    try {
                        bufferedReader.close();
                    } catch (IOException unused2) {
                    }
                }
            }
        }
    }
}
