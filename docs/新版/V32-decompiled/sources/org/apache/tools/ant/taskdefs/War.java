package org.apache.tools.ant.taskdefs;

import com.unisound.common.r;
import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.zip.ZipOutputStream;

/* JADX INFO: loaded from: classes3.dex */
public class War extends Jar {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final String XML_DESCRIPTOR_PATH = "WEB-INF/web.xml";
    private File addedWebXmlFile;
    private File deploymentDescriptor;
    private boolean needxmlfile = true;

    public War() {
        this.archiveType = "war";
        this.emptyBehavior = r.s;
    }

    @Deprecated
    public void setWarfile(File file) {
        setDestFile(file);
    }

    public void setWebxml(File file) {
        this.deploymentDescriptor = file;
        if (!file.exists()) {
            throw new BuildException("Deployment descriptor: " + this.deploymentDescriptor + DirectoryScanner.DOES_NOT_EXIST_POSTFIX);
        }
        ZipFileSet zipFileSet = new ZipFileSet();
        zipFileSet.setFile(this.deploymentDescriptor);
        zipFileSet.setFullpath(XML_DESCRIPTOR_PATH);
        super.addFileset(zipFileSet);
    }

    public void setNeedxmlfile(boolean z) {
        this.needxmlfile = z;
    }

    public void addLib(ZipFileSet zipFileSet) {
        zipFileSet.setPrefix("WEB-INF/lib/");
        super.addFileset(zipFileSet);
    }

    public void addClasses(ZipFileSet zipFileSet) {
        zipFileSet.setPrefix("WEB-INF/classes/");
        super.addFileset(zipFileSet);
    }

    public void addWebinf(ZipFileSet zipFileSet) {
        zipFileSet.setPrefix("WEB-INF/");
        super.addFileset(zipFileSet);
    }

    @Override // org.apache.tools.ant.taskdefs.Jar, org.apache.tools.ant.taskdefs.Zip
    protected void initZipOutputStream(ZipOutputStream zipOutputStream) throws IOException, BuildException {
        super.initZipOutputStream(zipOutputStream);
    }

    @Override // org.apache.tools.ant.taskdefs.Zip
    protected void zipFile(File file, ZipOutputStream zipOutputStream, String str, int i) throws IOException {
        boolean z = true;
        if (XML_DESCRIPTOR_PATH.equalsIgnoreCase(str)) {
            File file2 = this.addedWebXmlFile;
            if (file2 != null) {
                if (!FILE_UTILS.fileNameEquals(file2, file)) {
                    logWhenWriting("Warning: selected " + this.archiveType + " files include a second " + XML_DESCRIPTOR_PATH + " which will be ignored.\nThe duplicate entry is at " + file + "\nThe file that will be used is " + this.addedWebXmlFile, 1);
                }
                z = false;
            } else {
                this.addedWebXmlFile = file;
                this.deploymentDescriptor = file;
            }
        }
        if (z) {
            super.zipFile(file, zipOutputStream, str, i);
        }
    }

    @Override // org.apache.tools.ant.taskdefs.Jar, org.apache.tools.ant.taskdefs.Zip
    protected void cleanUp() {
        if (this.addedWebXmlFile == null && this.deploymentDescriptor == null && this.needxmlfile && !isInUpdateMode() && hasUpdatedFile()) {
            throw new BuildException("No WEB-INF/web.xml file was added.\nIf this is your intent, set needxmlfile='false' ");
        }
        this.addedWebXmlFile = null;
        super.cleanUp();
    }
}
