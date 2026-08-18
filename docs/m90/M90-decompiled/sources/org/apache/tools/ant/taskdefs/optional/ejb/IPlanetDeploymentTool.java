package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import javax.xml.parsers.SAXParser;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.ejb.EjbJar;
import org.apache.tools.ant.taskdefs.optional.ejb.IPlanetEjbc;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes3.dex */
public class IPlanetDeploymentTool extends GenericDeploymentTool {
    private static final String IAS_DD = "ias-ejb-jar.xml";
    private String descriptorName;
    private String displayName;
    private String iasDescriptorName;
    private File iashome;
    private String jarSuffix = ".jar";
    private boolean keepgenerated = false;
    private boolean debug = false;

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool
    protected String getPublicId() {
        return null;
    }

    public void setIashome(File file) {
        this.iashome = file;
    }

    public void setKeepgenerated(boolean z) {
        this.keepgenerated = z;
    }

    public void setDebug(boolean z) {
        this.debug = z;
    }

    public void setSuffix(String str) {
        this.jarSuffix = str;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool
    public void setGenericJarSuffix(String str) {
        log("Since a generic JAR file is not created during processing, the iPlanet Deployment Tool does not support the \"genericjarsuffix\" attribute.  It will be ignored.", 1);
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool, org.apache.tools.ant.taskdefs.optional.ejb.EJBDeploymentTool
    public void processDescriptor(String str, SAXParser sAXParser) throws Throwable {
        this.descriptorName = str;
        this.iasDescriptorName = null;
        log("iPlanet Deployment Tool processing: " + str + " (and " + getIasDescriptorName() + ")", 3);
        super.processDescriptor(str, sAXParser);
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool
    protected void checkConfiguration(String str, SAXParser sAXParser) throws BuildException {
        if (str.substring(str.lastIndexOf(File.separatorChar) + 1).equals("ejb-jar.xml") && getConfig().baseJarName == null) {
            throw new BuildException("No name specified for the completed JAR file.  The EJB descriptor should be prepended with the JAR name or it should be specified using the attribute \"basejarname\" in the \"ejbjar\" task.", getLocation());
        }
        File file = new File(getConfig().descriptorDir, getIasDescriptorName());
        if (!file.exists() || !file.isFile()) {
            throw new BuildException("The iAS-specific EJB descriptor (" + file + ") was not found.", getLocation());
        }
        File file2 = this.iashome;
        if (file2 != null && !file2.isDirectory()) {
            throw new BuildException("If \"iashome\" is specified, it must be a valid directory (it was set to " + this.iashome + ").", getLocation());
        }
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool
    protected Hashtable parseEjbFiles(String str, SAXParser sAXParser) throws SAXException, IOException {
        IPlanetEjbc iPlanetEjbc = new IPlanetEjbc(new File(getConfig().descriptorDir, str), new File(getConfig().descriptorDir, getIasDescriptorName()), getConfig().srcDir, getCombinedClasspath().toString(), sAXParser);
        iPlanetEjbc.setRetainSource(this.keepgenerated);
        iPlanetEjbc.setDebugOutput(this.debug);
        File file = this.iashome;
        if (file != null) {
            iPlanetEjbc.setIasHomeDir(file);
        }
        if (getConfig().dtdLocations != null) {
            for (EjbJar.DTDLocation dTDLocation : getConfig().dtdLocations) {
                iPlanetEjbc.registerDTD(dTDLocation.getPublicId(), dTDLocation.getLocation());
            }
        }
        try {
            iPlanetEjbc.execute();
            this.displayName = iPlanetEjbc.getDisplayName();
            Hashtable ejbFiles = iPlanetEjbc.getEjbFiles();
            String[] cmpDescriptors = iPlanetEjbc.getCmpDescriptors();
            if (cmpDescriptors.length > 0) {
                File file2 = getConfig().descriptorDir;
                String strSubstring = str.substring(0, str.lastIndexOf(File.separator) + 1);
                for (int i = 0; i < cmpDescriptors.length; i++) {
                    File file3 = new File(file2, strSubstring + cmpDescriptors[i].substring(cmpDescriptors[i].lastIndexOf(47) + 1));
                    if (!file3.exists()) {
                        throw new BuildException("The CMP descriptor file (" + file3 + ") could not be found.", getLocation());
                    }
                    ejbFiles.put(cmpDescriptors[i], file3);
                }
            }
            return ejbFiles;
        } catch (IPlanetEjbc.EjbcException e) {
            throw new BuildException("An error has occurred while trying to execute the iAS ejbc utility", e, getLocation());
        }
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool
    protected void addVendorFiles(Hashtable hashtable, String str) {
        hashtable.put("META-INF/ias-ejb-jar.xml", new File(getConfig().descriptorDir, getIasDescriptorName()));
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool
    File getVendorOutputJarFile(String str) {
        File file = new File(getDestDir(), str + this.jarSuffix);
        log("JAR file name: " + file.toString(), 3);
        return file;
    }

    private String getIasDescriptorName() {
        String str = this.iasDescriptorName;
        if (str != null) {
            return str;
        }
        int iLastIndexOf = this.descriptorName.lastIndexOf(File.separatorChar);
        String strSubstring = "";
        String strSubstring2 = iLastIndexOf != -1 ? this.descriptorName.substring(0, iLastIndexOf + 1) : "";
        int i = iLastIndexOf + 1;
        String strSubstring3 = "ejb-jar.xml";
        if (!this.descriptorName.substring(i).equals("ejb-jar.xml")) {
            int iIndexOf = this.descriptorName.indexOf(getConfig().baseNameTerminator, iLastIndexOf);
            if (iIndexOf < 0 && this.descriptorName.lastIndexOf(46) - 1 < 0) {
                iIndexOf = this.descriptorName.length() - 1;
            }
            int i2 = iIndexOf + 1;
            strSubstring = this.descriptorName.substring(i, i2);
            strSubstring3 = this.descriptorName.substring(i2);
        }
        String str2 = strSubstring2 + strSubstring + "ias-" + strSubstring3;
        this.iasDescriptorName = str2;
        return str2;
    }
}
