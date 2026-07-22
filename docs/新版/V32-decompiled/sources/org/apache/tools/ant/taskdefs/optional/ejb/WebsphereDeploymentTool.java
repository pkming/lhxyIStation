package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.optional.ejb.EjbJar;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class WebsphereDeploymentTool extends GenericDeploymentTool {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    public static final String PUBLICID_EJB11 = "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN";
    public static final String PUBLICID_EJB20 = "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 2.0//EN";
    protected static final String SCHEMA_DIR = "Schema/";
    protected static final String WAS_BND = "ibm-ejb-jar-bnd.xmi";
    protected static final String WAS_CMP_MAP = "Map.mapxmi";
    protected static final String WAS_CMP_SCHEMA = "Schema.dbxmi";
    protected static final String WAS_EXT = "ibm-ejb-jar-ext.xmi";
    private boolean codegen;
    private String dbName;
    private String dbSchema;
    private String dbVendor;
    private String ejb11DTD;
    private boolean noinform;
    private boolean novalidate;
    private boolean nowarn;
    private String rmicOptions;
    private boolean trace;
    private boolean use35MappingRules;
    private File websphereHome;
    private String jarSuffix = ".jar";
    private boolean keepGeneric = false;
    private boolean alwaysRebuild = true;
    private boolean ejbdeploy = true;
    private boolean newCMP = false;
    private Path wasClasspath = null;
    private boolean quiet = true;
    private String tempdir = "_ejbdeploy_temp";

    public Path createWASClasspath() {
        if (this.wasClasspath == null) {
            this.wasClasspath = new Path(getTask().getProject());
        }
        return this.wasClasspath.createPath();
    }

    public void setWASClasspath(Path path) {
        this.wasClasspath = path;
    }

    public void setDbvendor(String str) {
        this.dbVendor = str;
    }

    public void setDbname(String str) {
        this.dbName = str;
    }

    public void setDbschema(String str) {
        this.dbSchema = str;
    }

    public void setCodegen(boolean z) {
        this.codegen = z;
    }

    public void setQuiet(boolean z) {
        this.quiet = z;
    }

    public void setNovalidate(boolean z) {
        this.novalidate = z;
    }

    public void setNowarn(boolean z) {
        this.nowarn = z;
    }

    public void setNoinform(boolean z) {
        this.noinform = z;
    }

    public void setTrace(boolean z) {
        this.trace = z;
    }

    public void setRmicoptions(String str) {
        this.rmicOptions = str;
    }

    public void setUse35(boolean z) {
        this.use35MappingRules = z;
    }

    public void setRebuild(boolean z) {
        this.alwaysRebuild = z;
    }

    public void setSuffix(String str) {
        this.jarSuffix = str;
    }

    public void setKeepgeneric(boolean z) {
        this.keepGeneric = z;
    }

    public void setEjbdeploy(boolean z) {
        this.ejbdeploy = z;
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

    public void setTempdir(String str) {
        this.tempdir = str;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool
    protected DescriptorHandler getDescriptorHandler(File file) {
        DescriptorHandler descriptorHandler = new DescriptorHandler(getTask(), file);
        descriptorHandler.registerDTD("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN", this.ejb11DTD);
        for (EjbJar.DTDLocation dTDLocation : getConfig().dtdLocations) {
            descriptorHandler.registerDTD(dTDLocation.getPublicId(), dTDLocation.getLocation());
        }
        return descriptorHandler;
    }

    protected DescriptorHandler getWebsphereDescriptorHandler(File file) {
        DescriptorHandler descriptorHandler = new DescriptorHandler(getTask(), file) { // from class: org.apache.tools.ant.taskdefs.optional.ejb.WebsphereDeploymentTool.1
            @Override // org.apache.tools.ant.taskdefs.optional.ejb.DescriptorHandler
            protected void processElement() {
            }
        };
        for (EjbJar.DTDLocation dTDLocation : getConfig().dtdLocations) {
            descriptorHandler.registerDTD(dTDLocation.getPublicId(), dTDLocation.getLocation());
        }
        return descriptorHandler;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool
    protected void addVendorFiles(Hashtable hashtable, String str) {
        if (usingBaseJarName()) {
            str = "";
        }
        String str2 = this.dbVendor != null ? this.dbVendor + "-" : "";
        File file = new File(getConfig().descriptorDir, str + WAS_EXT);
        if (file.exists()) {
            hashtable.put("META-INF/ibm-ejb-jar-ext.xmi", file);
        } else {
            log("Unable to locate websphere extensions. It was expected to be in " + file.getPath(), 3);
        }
        File file2 = new File(getConfig().descriptorDir, str + WAS_BND);
        if (file2.exists()) {
            hashtable.put("META-INF/ibm-ejb-jar-bnd.xmi", file2);
        } else {
            log("Unable to locate websphere bindings. It was expected to be in " + file2.getPath(), 3);
        }
        if (!this.newCMP) {
            log("The old method for locating CMP files has been DEPRECATED.", 3);
            log("Please adjust your websphere descriptor and set newCMP=\"true\" to use the new CMP descriptor inclusion mechanism. ", 3);
            return;
        }
        try {
            File file3 = new File(getConfig().descriptorDir, str + str2 + WAS_CMP_MAP);
            if (file3.exists()) {
                hashtable.put("META-INF/Map.mapxmi", file3);
            } else {
                log("Unable to locate the websphere Map: " + file3.getPath(), 3);
            }
            File file4 = new File(getConfig().descriptorDir, str + str2 + WAS_CMP_SCHEMA);
            if (file4.exists()) {
                hashtable.put("META-INF/Schema/Schema.dbxmi", file4);
            } else {
                log("Unable to locate the websphere Schema: " + file4.getPath(), 3);
            }
        } catch (Exception e) {
            throw new BuildException("Exception while adding Vendor specific files: " + e.toString(), e);
        }
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool
    File getVendorOutputJarFile(String str) {
        return new File(getDestDir(), str + this.jarSuffix);
    }

    protected String getOptions() {
        StringBuffer stringBuffer = new StringBuffer();
        if (this.dbVendor != null) {
            stringBuffer.append(" -dbvendor ").append(this.dbVendor);
        }
        if (this.dbName != null) {
            stringBuffer.append(" -dbname \"").append(this.dbName).append("\"");
        }
        if (this.dbSchema != null) {
            stringBuffer.append(" -dbschema \"").append(this.dbSchema).append("\"");
        }
        if (this.codegen) {
            stringBuffer.append(" -codegen");
        }
        if (this.quiet) {
            stringBuffer.append(" -quiet");
        }
        if (this.novalidate) {
            stringBuffer.append(" -novalidate");
        }
        if (this.nowarn) {
            stringBuffer.append(" -nowarn");
        }
        if (this.noinform) {
            stringBuffer.append(" -noinform");
        }
        if (this.trace) {
            stringBuffer.append(" -trace");
        }
        if (this.use35MappingRules) {
            stringBuffer.append(" -35");
        }
        if (this.rmicOptions != null) {
            stringBuffer.append(" -rmic \"").append(this.rmicOptions).append("\"");
        }
        return stringBuffer.toString();
    }

    private void buildWebsphereJar(File file, File file2) {
        try {
            if (this.ejbdeploy) {
                Java java = new Java(getTask());
                java.createJvmarg().setValue("-Xms64m");
                java.createJvmarg().setValue("-Xmx128m");
                Environment.Variable variable = new Environment.Variable();
                variable.setKey("websphere.lib.dir");
                variable.setValue(new File(this.websphereHome, "lib").getAbsolutePath());
                java.addSysproperty(variable);
                java.setDir(this.websphereHome);
                java.setTaskName("ejbdeploy");
                java.setClassname("com.ibm.etools.ejbdeploy.EJBDeploy");
                java.createArg().setValue(file.getPath());
                java.createArg().setValue(this.tempdir);
                java.createArg().setValue(file2.getPath());
                java.createArg().setLine(getOptions());
                if (getCombinedClasspath() != null && getCombinedClasspath().toString().length() > 0) {
                    java.createArg().setValue("-cp");
                    java.createArg().setValue(getCombinedClasspath().toString());
                }
                Path combinedClasspath = this.wasClasspath;
                if (combinedClasspath == null) {
                    combinedClasspath = getCombinedClasspath();
                }
                java.setFork(true);
                if (combinedClasspath != null) {
                    java.setClasspath(combinedClasspath);
                }
                log("Calling websphere.ejbdeploy for " + file.toString(), 3);
                java.execute();
            }
        } catch (Exception e) {
            throw new BuildException("Exception while calling ejbdeploy. Details: " + e.toString(), e);
        }
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool
    protected void writeJar(String str, File file, Hashtable hashtable, String str2) throws BuildException {
        if (this.ejbdeploy) {
            File vendorOutputJarFile = super.getVendorOutputJarFile(str);
            super.writeJar(str, vendorOutputJarFile, hashtable, str2);
            if (this.alwaysRebuild || isRebuildRequired(vendorOutputJarFile, file)) {
                buildWebsphereJar(vendorOutputJarFile, file);
            }
            if (this.keepGeneric) {
                return;
            }
            log("deleting generic jar " + vendorOutputJarFile.toString(), 3);
            vendorOutputJarFile.delete();
            return;
        }
        super.writeJar(str, file, hashtable, str2);
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool, org.apache.tools.ant.taskdefs.optional.ejb.EJBDeploymentTool
    public void validateConfigured() throws BuildException {
        super.validateConfigured();
        if (this.ejbdeploy) {
            String property = getTask().getProject().getProperty("websphere.home");
            if (property == null) {
                throw new BuildException("The 'websphere.home' property must be set when 'ejbdeploy=true'");
            }
            this.websphereHome = getTask().getProject().resolveFile(property);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:44:0x0166, code lost:
    
        r0 = false;
     */
    /* JADX WARN: Not initialized variable reg: 16, insn: 0x0319: MOVE (r4 I:??[OBJECT, ARRAY]) = (r16 I:??[OBJECT, ARRAY]), block:B:146:0x0318 */
    /* JADX WARN: Not initialized variable reg: 4, insn: 0x0318: MOVE (r5 I:??[OBJECT, ARRAY]) = (r4 I:??[OBJECT, ARRAY]), block:B:146:0x0318 */
    /* JADX WARN: Not initialized variable reg: 6, insn: 0x031b: MOVE (r16 I:??[OBJECT, ARRAY]) = (r6 I:??[OBJECT, ARRAY]), block:B:146:0x0318 */
    /* JADX WARN: Removed duplicated region for block: B:158:0x033e  */
    /* JADX WARN: Removed duplicated region for block: B:170:0x032a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:174:0x0325 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:178:0x0320 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:205:? A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected boolean isRebuildRequired(java.io.File r23, java.io.File r24) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 840
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.taskdefs.optional.ejb.WebsphereDeploymentTool.isRebuildRequired(java.io.File, java.io.File):boolean");
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
