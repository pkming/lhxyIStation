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
public class Ear extends Jar {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final String XML_DESCRIPTOR_PATH = "META-INF/application.xml";
    private File deploymentDescriptor;
    private boolean descriptorAdded;

    public Ear() {
        this.archiveType = "ear";
        this.emptyBehavior = r.s;
    }

    public void setEarfile(File file) {
        setDestFile(file);
    }

    public void setAppxml(File file) {
        this.deploymentDescriptor = file;
        if (!file.exists()) {
            throw new BuildException("Deployment descriptor: " + this.deploymentDescriptor + DirectoryScanner.DOES_NOT_EXIST_POSTFIX);
        }
        ZipFileSet zipFileSet = new ZipFileSet();
        zipFileSet.setFile(this.deploymentDescriptor);
        zipFileSet.setFullpath(XML_DESCRIPTOR_PATH);
        super.addFileset(zipFileSet);
    }

    public void addArchives(ZipFileSet zipFileSet) {
        zipFileSet.setPrefix("/");
        super.addFileset(zipFileSet);
    }

    @Override // org.apache.tools.ant.taskdefs.Jar, org.apache.tools.ant.taskdefs.Zip
    protected void initZipOutputStream(ZipOutputStream zipOutputStream) throws IOException, BuildException {
        if (this.deploymentDescriptor == null && !isInUpdateMode()) {
            throw new BuildException("appxml attribute is required", getLocation());
        }
        super.initZipOutputStream(zipOutputStream);
    }

    @Override // org.apache.tools.ant.taskdefs.Zip
    protected void zipFile(File file, ZipOutputStream zipOutputStream, String str, int i) throws IOException {
        if (XML_DESCRIPTOR_PATH.equalsIgnoreCase(str)) {
            File file2 = this.deploymentDescriptor;
            if (file2 == null || !FILE_UTILS.fileNameEquals(file2, file) || this.descriptorAdded) {
                logWhenWriting("Warning: selected " + this.archiveType + " files include a " + XML_DESCRIPTOR_PATH + " which will be ignored (please use appxml attribute to " + this.archiveType + " task)", 1);
                return;
            } else {
                super.zipFile(file, zipOutputStream, str, i);
                this.descriptorAdded = true;
                return;
            }
        }
        super.zipFile(file, zipOutputStream, str, i);
    }

    @Override // org.apache.tools.ant.taskdefs.Jar, org.apache.tools.ant.taskdefs.Zip
    protected void cleanUp() {
        this.descriptorAdded = false;
        super.cleanUp();
    }
}
