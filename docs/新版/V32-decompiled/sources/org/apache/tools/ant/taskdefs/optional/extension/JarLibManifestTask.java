package org.apache.tools.ant.taskdefs.optional.extension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Task;

/* JADX INFO: loaded from: classes3.dex */
public final class JarLibManifestTask extends Task {
    private static final String CREATED_BY = "Created-By";
    private static final String MANIFEST_VERSION = "1.0";
    private File destFile;
    private Extension extension;
    private final ArrayList dependencies = new ArrayList();
    private final ArrayList optionals = new ArrayList();
    private final ArrayList extraAttributes = new ArrayList();

    public void setDestfile(File file) {
        this.destFile = file;
    }

    public void addConfiguredExtension(ExtensionAdapter extensionAdapter) throws BuildException {
        if (this.extension != null) {
            throw new BuildException("Can not have multiple extensions defined in one library.");
        }
        this.extension = extensionAdapter.toExtension();
    }

    public void addConfiguredDepends(ExtensionSet extensionSet) {
        this.dependencies.add(extensionSet);
    }

    public void addConfiguredOptions(ExtensionSet extensionSet) {
        this.optionals.add(extensionSet);
    }

    public void addConfiguredAttribute(ExtraAttribute extraAttribute) {
        this.extraAttributes.add(extraAttribute);
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws Throwable {
        validate();
        Manifest manifest = new Manifest();
        Attributes mainAttributes = manifest.getMainAttributes();
        mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        mainAttributes.putValue(CREATED_BY, "Apache Ant " + getProject().getProperty(MagicNames.ANT_VERSION));
        appendExtraAttributes(mainAttributes);
        Extension extension = this.extension;
        if (extension != null) {
            Extension.addExtension(extension, mainAttributes);
        }
        ArrayList extensions = toExtensions(this.dependencies);
        appendExtensionList(mainAttributes, Extension.EXTENSION_LIST, "lib", extensions.size());
        appendLibraryList(mainAttributes, "lib", extensions);
        ArrayList extensions2 = toExtensions(this.optionals);
        appendExtensionList(mainAttributes, Extension.OPTIONAL_EXTENSION_LIST, "opt", extensions2.size());
        appendLibraryList(mainAttributes, "opt", extensions2);
        try {
            log("Generating manifest " + this.destFile.getAbsoluteFile(), 2);
            writeManifest(manifest);
        } catch (IOException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    private void validate() throws BuildException {
        File file = this.destFile;
        if (file == null) {
            throw new BuildException("Destfile attribute not specified.");
        }
        if (file.exists() && !this.destFile.isFile()) {
            throw new BuildException(this.destFile + " is not a file.");
        }
    }

    private void appendExtraAttributes(Attributes attributes) {
        for (ExtraAttribute extraAttribute : this.extraAttributes) {
            attributes.putValue(extraAttribute.getName(), extraAttribute.getValue());
        }
    }

    private void writeManifest(Manifest manifest) throws Throwable {
        FileOutputStream fileOutputStream = null;
        try {
            FileOutputStream fileOutputStream2 = new FileOutputStream(this.destFile);
            try {
                manifest.write(fileOutputStream2);
                fileOutputStream2.flush();
                try {
                    fileOutputStream2.close();
                } catch (IOException unused) {
                }
            } catch (Throwable th) {
                th = th;
                fileOutputStream = fileOutputStream2;
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException unused2) {
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private void appendLibraryList(Attributes attributes, String str, ArrayList arrayList) throws BuildException {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            Extension.addExtension((Extension) arrayList.get(i), str + i + "-", attributes);
        }
    }

    private void appendExtensionList(Attributes attributes, Attributes.Name name, String str, int i) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i2 = 0; i2 < i; i2++) {
            stringBuffer.append(str);
            stringBuffer.append(i2);
            stringBuffer.append(' ');
        }
        attributes.put(name, stringBuffer.toString());
    }

    private ArrayList toExtensions(ArrayList arrayList) throws BuildException {
        ArrayList arrayList2 = new ArrayList();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            for (Extension extension : ((ExtensionSet) arrayList.get(i)).toExtensions(getProject())) {
                arrayList2.add(extension);
            }
        }
        return arrayList2;
    }
}
