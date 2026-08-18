package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes3.dex */
public class EjbJar extends MatchingTask {
    private File destDir;
    private Config config = new Config();
    private String genericJarSuffix = "-generic.jar";
    private String cmpVersion = "1.0";
    private ArrayList deploymentTools = new ArrayList();

    public static class DTDLocation extends org.apache.tools.ant.types.DTDLocation {
    }

    static class Config {
        public String analyzer;
        public String baseJarName;
        public Path classpath;
        public File descriptorDir;
        public File manifest;
        public NamingScheme namingScheme;
        public File srcDir;
        public String baseNameTerminator = "-";
        public boolean flatDestDir = false;
        public List supportFileSets = new ArrayList();
        public ArrayList dtdLocations = new ArrayList();

        Config() {
        }
    }

    public static class NamingScheme extends EnumeratedAttribute {
        public static final String BASEJARNAME = "basejarname";
        public static final String DESCRIPTOR = "descriptor";
        public static final String DIRECTORY = "directory";
        public static final String EJB_NAME = "ejb-name";

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{EJB_NAME, "directory", DESCRIPTOR, BASEJARNAME};
        }
    }

    public static class CMPVersion extends EnumeratedAttribute {
        public static final String CMP1_0 = "1.0";
        public static final String CMP2_0 = "2.0";

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"1.0", CMP2_0};
        }
    }

    protected void addDeploymentTool(EJBDeploymentTool eJBDeploymentTool) {
        eJBDeploymentTool.setTask(this);
        this.deploymentTools.add(eJBDeploymentTool);
    }

    public WeblogicDeploymentTool createWeblogic() {
        WeblogicDeploymentTool weblogicDeploymentTool = new WeblogicDeploymentTool();
        addDeploymentTool(weblogicDeploymentTool);
        return weblogicDeploymentTool;
    }

    public WebsphereDeploymentTool createWebsphere() {
        WebsphereDeploymentTool websphereDeploymentTool = new WebsphereDeploymentTool();
        addDeploymentTool(websphereDeploymentTool);
        return websphereDeploymentTool;
    }

    public BorlandDeploymentTool createBorland() {
        log("Borland deployment tools", 3);
        BorlandDeploymentTool borlandDeploymentTool = new BorlandDeploymentTool();
        borlandDeploymentTool.setTask(this);
        this.deploymentTools.add(borlandDeploymentTool);
        return borlandDeploymentTool;
    }

    public IPlanetDeploymentTool createIplanet() {
        log("iPlanet Application Server deployment tools", 3);
        IPlanetDeploymentTool iPlanetDeploymentTool = new IPlanetDeploymentTool();
        addDeploymentTool(iPlanetDeploymentTool);
        return iPlanetDeploymentTool;
    }

    public JbossDeploymentTool createJboss() {
        JbossDeploymentTool jbossDeploymentTool = new JbossDeploymentTool();
        addDeploymentTool(jbossDeploymentTool);
        return jbossDeploymentTool;
    }

    public JonasDeploymentTool createJonas() {
        log("JOnAS deployment tools", 3);
        JonasDeploymentTool jonasDeploymentTool = new JonasDeploymentTool();
        addDeploymentTool(jonasDeploymentTool);
        return jonasDeploymentTool;
    }

    public WeblogicTOPLinkDeploymentTool createWeblogictoplink() {
        log("The <weblogictoplink> element is no longer required. Please use the <weblogic> element and set newCMP=\"true\"", 2);
        WeblogicTOPLinkDeploymentTool weblogicTOPLinkDeploymentTool = new WeblogicTOPLinkDeploymentTool();
        addDeploymentTool(weblogicTOPLinkDeploymentTool);
        return weblogicTOPLinkDeploymentTool;
    }

    public Path createClasspath() {
        if (this.config.classpath == null) {
            this.config.classpath = new Path(getProject());
        }
        return this.config.classpath.createPath();
    }

    public DTDLocation createDTD() {
        DTDLocation dTDLocation = new DTDLocation();
        this.config.dtdLocations.add(dTDLocation);
        return dTDLocation;
    }

    public FileSet createSupport() {
        FileSet fileSet = new FileSet();
        this.config.supportFileSets.add(fileSet);
        return fileSet;
    }

    public void setManifest(File file) {
        this.config.manifest = file;
    }

    public void setSrcdir(File file) {
        this.config.srcDir = file;
    }

    public void setDescriptordir(File file) {
        this.config.descriptorDir = file;
    }

    public void setDependency(String str) {
        this.config.analyzer = str;
    }

    public void setBasejarname(String str) {
        this.config.baseJarName = str;
        if (this.config.namingScheme == null) {
            this.config.namingScheme = new NamingScheme();
            this.config.namingScheme.setValue(NamingScheme.BASEJARNAME);
        } else if (!this.config.namingScheme.getValue().equals(NamingScheme.BASEJARNAME)) {
            throw new BuildException("The basejarname attribute is not compatible with the " + this.config.namingScheme.getValue() + " naming scheme");
        }
    }

    public void setNaming(NamingScheme namingScheme) {
        this.config.namingScheme = namingScheme;
        if (!this.config.namingScheme.getValue().equals(NamingScheme.BASEJARNAME) && this.config.baseJarName != null) {
            throw new BuildException("The basejarname attribute is not compatible with the " + this.config.namingScheme.getValue() + " naming scheme");
        }
    }

    public File getDestdir() {
        return this.destDir;
    }

    public void setDestdir(File file) {
        this.destDir = file;
    }

    public String getCmpversion() {
        return this.cmpVersion;
    }

    public void setCmpversion(CMPVersion cMPVersion) {
        this.cmpVersion = cMPVersion.getValue();
    }

    public void setClasspath(Path path) {
        this.config.classpath = path;
    }

    public void setFlatdestdir(boolean z) {
        this.config.flatDestDir = z;
    }

    public void setGenericjarsuffix(String str) {
        this.genericJarSuffix = str;
    }

    public void setBasenameterminator(String str) {
        this.config.baseNameTerminator = str;
    }

    private void validateConfig() throws BuildException {
        if (this.config.srcDir == null) {
            throw new BuildException("The srcDir attribute must be specified");
        }
        if (this.config.descriptorDir == null) {
            Config config = this.config;
            config.descriptorDir = config.srcDir;
        }
        if (this.config.namingScheme == null) {
            this.config.namingScheme = new NamingScheme();
            this.config.namingScheme.setValue(NamingScheme.DESCRIPTOR);
        } else if (this.config.namingScheme.getValue().equals(NamingScheme.BASEJARNAME) && this.config.baseJarName == null) {
            throw new BuildException("The basejarname attribute must be specified with the basejarname naming scheme");
        }
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        validateConfig();
        if (this.deploymentTools.size() == 0) {
            GenericDeploymentTool genericDeploymentTool = new GenericDeploymentTool();
            genericDeploymentTool.setTask(this);
            genericDeploymentTool.setDestdir(this.destDir);
            genericDeploymentTool.setGenericJarSuffix(this.genericJarSuffix);
            this.deploymentTools.add(genericDeploymentTool);
        }
        for (EJBDeploymentTool eJBDeploymentTool : this.deploymentTools) {
            eJBDeploymentTool.configure(this.config);
            eJBDeploymentTool.validateConfigured();
        }
        try {
            SAXParserFactory sAXParserFactoryNewInstance = SAXParserFactory.newInstance();
            sAXParserFactoryNewInstance.setValidating(true);
            SAXParser sAXParserNewSAXParser = sAXParserFactoryNewInstance.newSAXParser();
            DirectoryScanner directoryScanner = getDirectoryScanner(this.config.descriptorDir);
            directoryScanner.scan();
            String[] includedFiles = directoryScanner.getIncludedFiles();
            log(includedFiles.length + " deployment descriptors located.", 3);
            for (String str : includedFiles) {
                Iterator it = this.deploymentTools.iterator();
                while (it.hasNext()) {
                    ((EJBDeploymentTool) it.next()).processDescriptor(str, sAXParserNewSAXParser);
                }
            }
        } catch (ParserConfigurationException e) {
            throw new BuildException("ParserConfigurationException while creating parser. Details: " + e.getMessage(), e);
        } catch (SAXException e2) {
            throw new BuildException("SAXException while creating parser.  Details: " + e2.getMessage(), e2);
        }
    }
}
