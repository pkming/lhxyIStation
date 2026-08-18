package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Enumeration;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSSConstants;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ManifestTask extends Task {
    public static final String VALID_ATTRIBUTE_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
    private String encoding;
    private File manifestFile;
    private Mode mode;
    private Manifest nestedManifest = new Manifest();
    private boolean mergeClassPaths = false;
    private boolean flattenClassPaths = false;

    public static class Mode extends EnumeratedAttribute {
        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"update", MSVSSConstants.WRITABLE_REPLACE};
        }
    }

    public ManifestTask() {
        Mode mode = new Mode();
        this.mode = mode;
        mode.setValue(MSVSSConstants.WRITABLE_REPLACE);
    }

    public void addConfiguredSection(Manifest.Section section) throws ManifestException {
        Enumeration<String> attributeKeys = section.getAttributeKeys();
        while (attributeKeys.hasMoreElements()) {
            checkAttribute(section.getAttribute(attributeKeys.nextElement()));
        }
        this.nestedManifest.addConfiguredSection(section);
    }

    public void addConfiguredAttribute(Manifest.Attribute attribute) throws ManifestException {
        checkAttribute(attribute);
        this.nestedManifest.addConfiguredAttribute(attribute);
    }

    private void checkAttribute(Manifest.Attribute attribute) throws BuildException {
        String name = attribute.getName();
        char cCharAt = name.charAt(0);
        if (cCharAt == '-' || cCharAt == '_') {
            throw new BuildException("Manifest attribute names must not start with '" + cCharAt + "'.");
        }
        for (int i = 0; i < name.length(); i++) {
            char cCharAt2 = name.charAt(i);
            if (VALID_ATTRIBUTE_CHARS.indexOf(cCharAt2) < 0) {
                throw new BuildException("Manifest attribute names must not contain '" + cCharAt2 + "'");
            }
        }
    }

    public void setFile(File file) {
        this.manifestFile = file;
    }

    public void setEncoding(String str) {
        this.encoding = str;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setMergeClassPathAttributes(boolean z) {
        this.mergeClassPaths = z;
    }

    public void setFlattenAttributes(boolean z) {
        this.flattenClassPaths = z;
    }

    /* JADX WARN: Not initialized variable reg: 4, insn: 0x0088: MOVE (r3 I:??[OBJECT, ARRAY]) = (r4 I:??[OBJECT, ARRAY]), block:B:27:0x0088 */
    @Override // org.apache.tools.ant.Task
    public void execute() throws Throwable {
        Reader reader;
        InputStreamReader inputStreamReader;
        BuildException buildException;
        Manifest manifest;
        PrintWriter printWriter;
        if (this.manifestFile == null) {
            throw new BuildException("the file attribute is required");
        }
        Manifest defaultManifest = Manifest.getDefaultManifest();
        Reader reader2 = null;
        printWriter = null;
        PrintWriter printWriter2 = null;
        try {
            if (this.manifestFile.exists()) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(this.manifestFile);
                    if (this.encoding == null) {
                        inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
                    } else {
                        inputStreamReader = new InputStreamReader(fileInputStream, this.encoding);
                    }
                    try {
                        manifest = new Manifest(inputStreamReader);
                        FileUtils.close(inputStreamReader);
                        buildException = null;
                    } catch (IOException e) {
                        e = e;
                        buildException = new BuildException("Failed to read " + this.manifestFile, e, getLocation());
                        FileUtils.close(inputStreamReader);
                        manifest = null;
                    } catch (ManifestException e2) {
                        e = e2;
                        buildException = new BuildException("Existing manifest " + this.manifestFile + " is invalid", e, getLocation());
                        FileUtils.close(inputStreamReader);
                        manifest = null;
                    }
                } catch (IOException e3) {
                    e = e3;
                    inputStreamReader = null;
                } catch (ManifestException e4) {
                    e = e4;
                    inputStreamReader = null;
                } catch (Throwable th) {
                    th = th;
                    FileUtils.close(reader2);
                    throw th;
                }
            } else {
                manifest = null;
                buildException = null;
            }
            Enumeration<String> warnings = this.nestedManifest.getWarnings();
            while (warnings.hasMoreElements()) {
                log("Manifest warning: " + warnings.nextElement(), 1);
            }
            try {
                if (this.mode.getValue().equals("update") && this.manifestFile.exists()) {
                    if (manifest != null) {
                        defaultManifest.merge(manifest, false, this.mergeClassPaths);
                    } else if (buildException != null) {
                        throw buildException;
                    }
                }
                defaultManifest.merge(this.nestedManifest, false, this.mergeClassPaths);
                if (defaultManifest.equals(manifest)) {
                    log("Manifest has not changed, do not recreate", 3);
                    return;
                }
                try {
                    try {
                        printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.manifestFile), "UTF-8"));
                    } catch (IOException e5) {
                        e = e5;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
                try {
                    defaultManifest.write(printWriter, this.flattenClassPaths);
                    if (printWriter.checkError()) {
                        throw new IOException("Encountered an error writing manifest");
                    }
                    FileUtils.close(printWriter);
                } catch (IOException e6) {
                    e = e6;
                    printWriter2 = printWriter;
                    throw new BuildException("Failed to write " + this.manifestFile, e, getLocation());
                } catch (Throwable th3) {
                    th = th3;
                    printWriter2 = printWriter;
                    FileUtils.close(printWriter2);
                    throw th;
                }
            } catch (ManifestException e7) {
                throw new BuildException("Manifest is invalid", e7, getLocation());
            }
        } catch (Throwable th4) {
            th = th4;
            reader2 = reader;
        }
    }
}
