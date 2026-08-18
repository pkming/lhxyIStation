package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.optional.ejb.EjbJar;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;
import org.xml.sax.InputSource;

/* JADX INFO: loaded from: classes3.dex */
public class WeblogicDeploymentTool extends GenericDeploymentTool {
    protected static final String COMPILER_EJB11 = "weblogic.ejbc";
    protected static final String COMPILER_EJB20 = "weblogic.ejbc20";
    protected static final String DEFAULT_COMPILER = "default";
    protected static final String DEFAULT_WL51_DTD_LOCATION = "/weblogic/ejb/deployment/xml/weblogic-ejb-jar.dtd";
    protected static final String DEFAULT_WL51_EJB11_DTD_LOCATION = "/weblogic/ejb/deployment/xml/ejb-jar.dtd";
    protected static final String DEFAULT_WL60_51_DTD_LOCATION = "/weblogic/ejb20/dd/xml/weblogic510-ejb-jar.dtd";
    protected static final String DEFAULT_WL60_DTD_LOCATION = "/weblogic/ejb20/dd/xml/weblogic600-ejb-jar.dtd";
    protected static final String DEFAULT_WL60_EJB11_DTD_LOCATION = "/weblogic/ejb20/dd/xml/ejb11-jar.dtd";
    protected static final String DEFAULT_WL60_EJB20_DTD_LOCATION = "/weblogic/ejb20/dd/xml/ejb20-jar.dtd";
    protected static final String DEFAULT_WL70_DTD_LOCATION = "/weblogic/ejb20/dd/xml/weblogic700-ejb-jar.dtd";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    public static final String PUBLICID_EJB11 = "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN";
    public static final String PUBLICID_EJB20 = "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 2.0//EN";
    public static final String PUBLICID_WEBLOGIC_EJB510 = "-//BEA Systems, Inc.//DTD WebLogic 5.1.0 EJB//EN";
    public static final String PUBLICID_WEBLOGIC_EJB600 = "-//BEA Systems, Inc.//DTD WebLogic 6.0.0 EJB//EN";
    public static final String PUBLICID_WEBLOGIC_EJB700 = "-//BEA Systems, Inc.//DTD WebLogic 7.0.0 EJB//EN";
    protected static final String WL_CMP_DD = "weblogic-cmp-rdbms-jar.xml";
    protected static final String WL_DD = "weblogic-ejb-jar.xml";
    private String ejb11DTD;
    private File outputDir;
    private String weblogicDTD;
    private String jarSuffix = ".jar";
    private boolean keepgenerated = false;
    private String ejbcClass = null;
    private String additionalArgs = "";
    private String additionalJvmArgs = "";
    private boolean keepGeneric = false;
    private String compiler = null;
    private boolean alwaysRebuild = true;
    private boolean noEJBC = false;
    private boolean newCMP = false;
    private Path wlClasspath = null;
    private Vector sysprops = new Vector();
    private Integer jvmDebugLevel = null;

    public void addSysproperty(Environment.Variable variable) {
        this.sysprops.add(variable);
    }

    public Path createWLClasspath() {
        if (this.wlClasspath == null) {
            this.wlClasspath = new Path(getTask().getProject());
        }
        return this.wlClasspath.createPath();
    }

    public void setOutputDir(File file) {
        this.outputDir = file;
    }

    public void setWLClasspath(Path path) {
        this.wlClasspath = path;
    }

    public void setCompiler(String str) {
        this.compiler = str;
    }

    public void setRebuild(boolean z) {
        this.alwaysRebuild = z;
    }

    public void setJvmDebugLevel(Integer num) {
        this.jvmDebugLevel = num;
    }

    public Integer getJvmDebugLevel() {
        return this.jvmDebugLevel;
    }

    public void setSuffix(String str) {
        this.jarSuffix = str;
    }

    public void setKeepgeneric(boolean z) {
        this.keepGeneric = z;
    }

    public void setKeepgenerated(String str) {
        this.keepgenerated = Boolean.valueOf(str).booleanValue();
    }

    public void setArgs(String str) {
        this.additionalArgs = str;
    }

    public void setJvmargs(String str) {
        this.additionalJvmArgs = str;
    }

    public void setEjbcClass(String str) {
        this.ejbcClass = str;
    }

    public String getEjbcClass() {
        return this.ejbcClass;
    }

    public void setWeblogicdtd(String str) {
        setEJBdtd(str);
    }

    public void setWLdtd(String str) {
        this.weblogicDTD = str;
    }

    public void setEJBdtd(String str) {
        this.ejb11DTD = str;
    }

    public void setOldCMP(boolean z) {
        this.newCMP = !z;
    }

    public void setNewCMP(boolean z) {
        this.newCMP = z;
    }

    public void setNoEJBC(boolean z) {
        this.noEJBC = z;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool
    protected void registerKnownDTDs(DescriptorHandler descriptorHandler) {
        descriptorHandler.registerDTD("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN", DEFAULT_WL51_EJB11_DTD_LOCATION);
        descriptorHandler.registerDTD("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN", DEFAULT_WL60_EJB11_DTD_LOCATION);
        descriptorHandler.registerDTD("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN", this.ejb11DTD);
        descriptorHandler.registerDTD("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 2.0//EN", DEFAULT_WL60_EJB20_DTD_LOCATION);
    }

    protected DescriptorHandler getWeblogicDescriptorHandler(final File file) {
        DescriptorHandler descriptorHandler = new DescriptorHandler(getTask(), file) { // from class: org.apache.tools.ant.taskdefs.optional.ejb.WeblogicDeploymentTool.1
            @Override // org.apache.tools.ant.taskdefs.optional.ejb.DescriptorHandler
            protected void processElement() {
                if (this.currentElement.equals("type-storage")) {
                    String str = this.currentText;
                    this.ejbFiles.put(str, new File(file, str.substring(9, str.length())));
                }
            }
        };
        descriptorHandler.registerDTD(PUBLICID_WEBLOGIC_EJB510, DEFAULT_WL51_DTD_LOCATION);
        descriptorHandler.registerDTD(PUBLICID_WEBLOGIC_EJB510, DEFAULT_WL60_51_DTD_LOCATION);
        descriptorHandler.registerDTD(PUBLICID_WEBLOGIC_EJB600, DEFAULT_WL60_DTD_LOCATION);
        descriptorHandler.registerDTD(PUBLICID_WEBLOGIC_EJB700, DEFAULT_WL70_DTD_LOCATION);
        descriptorHandler.registerDTD(PUBLICID_WEBLOGIC_EJB510, this.weblogicDTD);
        descriptorHandler.registerDTD(PUBLICID_WEBLOGIC_EJB600, this.weblogicDTD);
        for (EjbJar.DTDLocation dTDLocation : getConfig().dtdLocations) {
            descriptorHandler.registerDTD(dTDLocation.getPublicId(), dTDLocation.getLocation());
        }
        return descriptorHandler;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool
    protected void addVendorFiles(Hashtable hashtable, String str) {
        File file = new File(getConfig().descriptorDir, str + WL_DD);
        if (file.exists()) {
            hashtable.put("META-INF/weblogic-ejb-jar.xml", file);
            if (!this.newCMP) {
                log("The old method for locating CMP files has been DEPRECATED.", 3);
                log("Please adjust your weblogic descriptor and set newCMP=\"true\" to use the new CMP descriptor inclusion mechanism. ", 3);
                File file2 = new File(getConfig().descriptorDir, str + WL_CMP_DD);
                if (file2.exists()) {
                    hashtable.put("META-INF/weblogic-cmp-rdbms-jar.xml", file2);
                    return;
                }
                return;
            }
            try {
                File file3 = (File) hashtable.get("META-INF/ejb-jar.xml");
                SAXParserFactory sAXParserFactoryNewInstance = SAXParserFactory.newInstance();
                sAXParserFactoryNewInstance.setValidating(true);
                SAXParser sAXParserNewSAXParser = sAXParserFactoryNewInstance.newSAXParser();
                DescriptorHandler weblogicDescriptorHandler = getWeblogicDescriptorHandler(file3.getParentFile());
                sAXParserNewSAXParser.parse(new InputSource(new FileInputStream(file)), weblogicDescriptorHandler);
                Hashtable files = weblogicDescriptorHandler.getFiles();
                Enumeration enumerationKeys = files.keys();
                while (enumerationKeys.hasMoreElements()) {
                    String str2 = (String) enumerationKeys.nextElement();
                    hashtable.put(str2, files.get(str2));
                }
                return;
            } catch (Exception e) {
                throw new BuildException("Exception while adding Vendor specific files: " + e.toString(), e);
            }
        }
        log("Unable to locate weblogic deployment descriptor. It was expected to be in " + file.getPath(), 1);
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool
    File getVendorOutputJarFile(String str) {
        return new File(getDestDir(), str + this.jarSuffix);
    }

    private void buildWeblogicJar(File file, File file2, String str) {
        if (this.noEJBC) {
            try {
                FILE_UTILS.copyFile(file, file2);
                if (this.keepgenerated) {
                    return;
                }
                file.delete();
                return;
            } catch (IOException e) {
                throw new BuildException("Unable to write EJB jar", e);
            }
        }
        String str2 = this.ejbcClass;
        try {
            Java java = new Java(getTask());
            java.setTaskName("ejbc");
            java.createJvmarg().setLine(this.additionalJvmArgs);
            if (!this.sysprops.isEmpty()) {
                Enumeration enumerationElements = this.sysprops.elements();
                while (enumerationElements.hasMoreElements()) {
                    java.addSysproperty((Environment.Variable) enumerationElements.nextElement());
                }
            }
            if (getJvmDebugLevel() != null) {
                java.createJvmarg().setLine(" -Dweblogic.StdoutSeverityLevel=" + this.jvmDebugLevel);
            }
            if (str2 == null) {
                if ("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN".equals(str)) {
                    str2 = COMPILER_EJB11;
                } else if ("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 2.0//EN".equals(str)) {
                    str2 = COMPILER_EJB20;
                } else {
                    log("Unrecognized publicId " + str + " - using EJB 1.1 compiler", 1);
                    str2 = COMPILER_EJB11;
                }
            }
            java.setClassname(str2);
            java.createArg().setLine(this.additionalArgs);
            if (this.keepgenerated) {
                java.createArg().setValue("-keepgenerated");
            }
            String str3 = this.compiler;
            if (str3 == null) {
                String property = getTask().getProject().getProperty("build.compiler");
                if (property != null && property.equals("jikes")) {
                    java.createArg().setValue("-compiler");
                    java.createArg().setValue("jikes");
                }
            } else if (!str3.equals("default")) {
                java.createArg().setValue("-compiler");
                java.createArg().setLine(this.compiler);
            }
            Path combinedClasspath = getCombinedClasspath();
            if (this.wlClasspath != null && combinedClasspath != null && combinedClasspath.toString().trim().length() > 0) {
                java.createArg().setValue("-classpath");
                java.createArg().setPath(combinedClasspath);
            }
            java.createArg().setValue(file.getPath());
            if (this.outputDir == null) {
                java.createArg().setValue(file2.getPath());
            } else {
                java.createArg().setValue(this.outputDir.getPath());
            }
            Path combinedClasspath2 = this.wlClasspath;
            if (combinedClasspath2 == null) {
                combinedClasspath2 = getCombinedClasspath();
            }
            java.setFork(true);
            if (combinedClasspath2 != null) {
                java.setClasspath(combinedClasspath2);
            }
            log("Calling " + str2 + " for " + file.toString(), 3);
            if (java.executeJava() == 0) {
            } else {
                throw new BuildException("Ejbc reported an error");
            }
        } catch (Exception e2) {
            throw new BuildException("Exception while calling " + str2 + ". Details: " + e2.toString(), e2);
        }
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool
    protected void writeJar(String str, File file, Hashtable hashtable, String str2) throws BuildException {
        File vendorOutputJarFile = super.getVendorOutputJarFile(str);
        super.writeJar(str, vendorOutputJarFile, hashtable, str2);
        if (this.alwaysRebuild || isRebuildRequired(vendorOutputJarFile, file)) {
            buildWeblogicJar(vendorOutputJarFile, file, str2);
        }
        if (this.keepGeneric) {
            return;
        }
        log("deleting generic jar " + vendorOutputJarFile.toString(), 3);
        vendorOutputJarFile.delete();
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool, org.apache.tools.ant.taskdefs.optional.ejb.EJBDeploymentTool
    public void validateConfigured() throws BuildException {
        super.validateConfigured();
    }

    /* JADX WARN: Not initialized variable reg: 16, insn: 0x0322: MOVE (r4 I:??[OBJECT, ARRAY]) = (r16 I:??[OBJECT, ARRAY]), block:B:146:0x0321 */
    /* JADX WARN: Not initialized variable reg: 4, insn: 0x0321: MOVE (r5 I:??[OBJECT, ARRAY]) = (r4 I:??[OBJECT, ARRAY]), block:B:146:0x0321 */
    /* JADX WARN: Not initialized variable reg: 6, insn: 0x0324: MOVE (r16 I:??[OBJECT, ARRAY]) = (r6 I:??[OBJECT, ARRAY]), block:B:146:0x0321 */
    /* JADX WARN: Removed duplicated region for block: B:158:0x0347  */
    /* JADX WARN: Removed duplicated region for block: B:171:0x0333 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:173:0x032e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:177:0x0329 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:206:? A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected boolean isRebuildRequired(java.io.File r24, java.io.File r25) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 849
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.taskdefs.optional.ejb.WeblogicDeploymentTool.isRebuildRequired(java.io.File, java.io.File):boolean");
    }

    protected ClassLoader getClassLoaderFromJar(File file) throws IOException {
        Path path = new Path(getTask().getProject());
        path.setLocation(file);
        Path combinedClasspath = getCombinedClasspath();
        if (combinedClasspath != null) {
            path.append(combinedClasspath);
        }
        return getTask().getProject().createClassLoader(path);
    }
}
