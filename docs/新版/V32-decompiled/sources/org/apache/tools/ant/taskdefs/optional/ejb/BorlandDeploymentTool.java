package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.optional.ejb.EjbJar;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;

/* JADX INFO: loaded from: classes3.dex */
public class BorlandDeploymentTool extends GenericDeploymentTool implements ExecuteStreamHandler {
    static final int BAS = 4;
    protected static final String BAS_DD = "ejb-inprise.xml";
    static final int BES = 5;
    protected static final String BES_DD = "ejb-borland.xml";
    protected static final String DEFAULT_BAS45_EJB11_DTD_LOCATION = "/com/inprise/j2ee/xml/dtds/ejb-jar.dtd";
    protected static final String DEFAULT_BAS_DTD_LOCATION = "/com/inprise/j2ee/xml/dtds/ejb-inprise.dtd";
    protected static final String JAVA2IIOP = "java2iiop";
    public static final String PUBLICID_BORLAND_EJB = "-//Inprise Corporation//DTD Enterprise JavaBeans 1.1//EN";
    protected static final String VERIFY = "com.inprise.ejb.util.Verify";
    private String borlandDTD;
    private String jarSuffix = "-ejb.jar";
    private boolean java2iiopdebug = false;
    private String java2iioparams = null;
    private boolean generateclient = false;
    private int version = 4;
    private boolean verify = true;
    private String verifyArgs = "";
    private Hashtable genfiles = new Hashtable();

    @Override // org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void setProcessInputStream(OutputStream outputStream) throws IOException {
    }

    @Override // org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void start() throws IOException {
    }

    @Override // org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void stop() {
    }

    public void setDebug(boolean z) {
        this.java2iiopdebug = z;
    }

    public void setVerify(boolean z) {
        this.verify = z;
    }

    public void setSuffix(String str) {
        this.jarSuffix = str;
    }

    public void setVerifyArgs(String str) {
        this.verifyArgs = str;
    }

    public void setBASdtd(String str) {
        this.borlandDTD = str;
    }

    public void setGenerateclient(boolean z) {
        this.generateclient = z;
    }

    public void setVersion(int i) {
        this.version = i;
    }

    public void setJava2iiopParams(String str) {
        this.java2iioparams = str;
    }

    protected DescriptorHandler getBorlandDescriptorHandler(final File file) {
        DescriptorHandler descriptorHandler = new DescriptorHandler(getTask(), file) { // from class: org.apache.tools.ant.taskdefs.optional.ejb.BorlandDeploymentTool.1
            @Override // org.apache.tools.ant.taskdefs.optional.ejb.DescriptorHandler
            protected void processElement() {
                if (this.currentElement.equals("type-storage")) {
                    String str = this.currentText;
                    this.ejbFiles.put(str, new File(file, str.substring(9, str.length())));
                }
            }
        };
        String str = this.borlandDTD;
        if (str == null) {
            str = DEFAULT_BAS_DTD_LOCATION;
        }
        descriptorHandler.registerDTD(PUBLICID_BORLAND_EJB, str);
        for (EjbJar.DTDLocation dTDLocation : getConfig().dtdLocations) {
            descriptorHandler.registerDTD(dTDLocation.getPublicId(), dTDLocation.getLocation());
        }
        return descriptorHandler;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool
    protected void addVendorFiles(Hashtable hashtable, String str) {
        int i = this.version;
        if (i != 5 && i != 4) {
            throw new BuildException("version " + this.version + " is not supported");
        }
        String str2 = i == 5 ? BES_DD : BAS_DD;
        log("vendor file : " + str + str2, 4);
        File file = new File(getConfig().descriptorDir, str + str2);
        if (file.exists()) {
            log("Borland specific file found " + file, 3);
            hashtable.put("META-INF/" + str2, file);
        } else {
            log("Unable to locate borland deployment descriptor. It was expected to be in " + file.getPath(), 1);
        }
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool
    File getVendorOutputJarFile(String str) {
        return new File(getDestDir(), str + this.jarSuffix);
    }

    private void verifyBorlandJar(File file) {
        int i = this.version;
        if (i == 4) {
            verifyBorlandJarV4(file);
        } else if (i == 5) {
            verifyBorlandJarV5(file);
        } else {
            log("verify jar skipped because the version is invalid [" + this.version + "]", 1);
        }
    }

    private void verifyBorlandJarV5(File file) {
        log("verify BES " + file, 2);
        try {
            ExecTask execTask = new ExecTask(getTask());
            execTask.setDir(new File("."));
            execTask.setExecutable("iastool");
            if (getCombinedClasspath() != null) {
                execTask.createArg().setValue("-VBJclasspath");
                execTask.createArg().setValue(getCombinedClasspath().toString());
            }
            if (this.java2iiopdebug) {
                execTask.createArg().setValue("-debug");
            }
            execTask.createArg().setValue("-verify");
            execTask.createArg().setValue("-src");
            execTask.createArg().setValue(file.getPath());
            log("Calling iastool", 3);
            execTask.execute();
        } catch (Exception e) {
            throw new BuildException("Exception while calling generateclient Details: " + e.toString(), e);
        }
    }

    private void verifyBorlandJarV4(File file) {
        log("verify BAS " + file, 2);
        try {
            String str = this.verifyArgs + " " + file.getPath();
            Java java = new Java(getTask());
            java.setTaskName("verify");
            java.setClassname(VERIFY);
            java.createArg().setLine(str);
            Path combinedClasspath = getCombinedClasspath();
            if (combinedClasspath != null) {
                java.setClasspath(combinedClasspath);
                java.setFork(true);
            }
            log("Calling com.inprise.ejb.util.Verify for " + file.toString(), 3);
            java.execute();
        } catch (Exception e) {
            throw new BuildException("Exception while calling com.inprise.ejb.util.Verify Details: " + e.toString(), e);
        }
    }

    private void generateClient(File file) {
        getTask().getProject().addTaskDefinition("internal_bas_generateclient", BorlandGenerateClient.class);
        log("generate client for " + file, 2);
        try {
            BorlandGenerateClient borlandGenerateClient = (BorlandGenerateClient) getTask().getProject().createTask("internal_bas_generateclient");
            borlandGenerateClient.setEjbjar(file);
            borlandGenerateClient.setDebug(this.java2iiopdebug);
            Path combinedClasspath = getCombinedClasspath();
            if (combinedClasspath != null) {
                borlandGenerateClient.setClasspath(combinedClasspath);
            }
            borlandGenerateClient.setVersion(this.version);
            borlandGenerateClient.setTaskName("generate client");
            borlandGenerateClient.execute();
        } catch (Exception e) {
            throw new BuildException("Exception while calling com.inprise.ejb.util.Verify Details: " + e.toString(), e);
        }
    }

    private void buildBorlandStubs(Iterator it) {
        Execute execute = new Execute(this);
        Project project = getTask().getProject();
        execute.setAntRun(project);
        execute.setWorkingDirectory(project.getBaseDir());
        Commandline commandline = new Commandline();
        commandline.setExecutable(JAVA2IIOP);
        if (this.java2iiopdebug) {
            commandline.createArgument().setValue("-VBJdebug");
        }
        commandline.createArgument().setValue("-VBJclasspath");
        commandline.createArgument().setPath(getCombinedClasspath());
        commandline.createArgument().setValue("-list_files");
        commandline.createArgument().setValue("-no_tie");
        if (this.java2iioparams != null) {
            log("additional  " + this.java2iioparams + " to java2iiop ", 0);
            commandline.createArgument().setLine(this.java2iioparams);
        }
        commandline.createArgument().setValue("-root_dir");
        commandline.createArgument().setValue(getConfig().srcDir.getAbsolutePath());
        commandline.createArgument().setValue("-compile");
        while (it.hasNext()) {
            commandline.createArgument().setValue(it.next().toString());
        }
        try {
            log("Calling java2iiop", 3);
            log(commandline.describeCommand(), 4);
            execute.setCommandline(commandline.getCommandline());
            int iExecute = execute.execute();
            if (Execute.isFailure(iExecute)) {
                throw new BuildException("Failed executing java2iiop (ret code is " + iExecute + ")", getTask().getLocation());
            }
        } catch (IOException e) {
            log("java2iiop exception :" + e.getMessage(), 0);
            throw new BuildException(e, getTask().getLocation());
        }
    }

    @Override // org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool
    protected void writeJar(String str, File file, Hashtable hashtable, String str2) throws BuildException {
        Vector vector = new Vector();
        for (String str3 : hashtable.keySet()) {
            if (str3.endsWith("Home.class")) {
                String str4 = toClass(str3);
                vector.add(str4);
                log(" Home " + str4, 3);
            }
        }
        buildBorlandStubs(vector.iterator());
        hashtable.putAll(this.genfiles);
        super.writeJar(str, file, hashtable, str2);
        if (this.verify) {
            verifyBorlandJar(file);
        }
        if (this.generateclient) {
            generateClient(file);
        }
        this.genfiles.clear();
    }

    private String toClass(String str) {
        return str.substring(0, str.lastIndexOf(".class")).replace('\\', '.');
    }

    private String toClassFile(String str) {
        return str.substring(0, str.lastIndexOf(".java")) + ".class";
    }

    @Override // org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void setProcessOutputStream(InputStream inputStream) throws IOException {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
                String line = bufferedReader.readLine();
                if (line != null) {
                    if (line.endsWith(".java")) {
                        String classFile = toClassFile(line);
                        this.genfiles.put(classFile.substring(getConfig().srcDir.getAbsolutePath().length() + 1), new File(classFile));
                    }
                } else {
                    bufferedReader.close();
                    return;
                }
            }
        } catch (Exception e) {
            throw new BuildException("Exception while parsing  java2iiop output. Details: " + e.toString(), e);
        }
    }

    @Override // org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void setProcessErrorStream(InputStream inputStream) throws IOException {
        String line = new BufferedReader(new InputStreamReader(inputStream)).readLine();
        if (line != null) {
            log("[java2iiop] " + line, 0);
        }
    }
}
