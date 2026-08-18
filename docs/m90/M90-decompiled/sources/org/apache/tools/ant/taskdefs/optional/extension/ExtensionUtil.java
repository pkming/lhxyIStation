package org.apache.tools.ant.taskdefs.optional.extension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

/* JADX INFO: loaded from: classes3.dex */
public final class ExtensionUtil {
    private ExtensionUtil() {
    }

    static ArrayList toExtensions(List list) throws BuildException {
        ArrayList arrayList = new ArrayList();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            arrayList.add(((ExtensionAdapter) list.get(i)).toExtension());
        }
        return arrayList;
    }

    static void extractExtensions(Project project, List list, List list2) throws BuildException {
        if (list2.isEmpty()) {
            return;
        }
        Extension[] extensions = getExtensions(project, list2);
        for (Extension extension : extensions) {
            list.add(extension);
        }
    }

    private static Extension[] getExtensions(Project project, List list) throws BuildException {
        boolean zIsIncludeURL;
        ArrayList arrayList = new ArrayList();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            FileSet fileSet = (FileSet) it.next();
            boolean zIsIncludeImpl = true;
            if (fileSet instanceof LibFileSet) {
                LibFileSet libFileSet = (LibFileSet) fileSet;
                zIsIncludeImpl = libFileSet.isIncludeImpl();
                zIsIncludeURL = libFileSet.isIncludeURL();
            } else {
                zIsIncludeURL = true;
            }
            DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(project);
            File basedir = directoryScanner.getBasedir();
            for (String str : directoryScanner.getIncludedFiles()) {
                loadExtensions(new File(basedir, str), arrayList, zIsIncludeImpl, zIsIncludeURL);
            }
        }
        return (Extension[]) arrayList.toArray(new Extension[arrayList.size()]);
    }

    private static void loadExtensions(File file, List list, boolean z, boolean z2) throws BuildException {
        try {
            for (Extension extension : Extension.getAvailable(new JarFile(file).getManifest())) {
                addExtension(list, extension, z, z2);
            }
        } catch (Exception e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    private static void addExtension(List list, Extension extension, boolean z, boolean z2) {
        if (!z2 && extension.getImplementationURL() != null) {
            extension = new Extension(extension.getExtensionName(), extension.getSpecificationVersion().toString(), extension.getSpecificationVendor(), extension.getImplementationVersion().toString(), extension.getImplementationVendor(), extension.getImplementationVendorID(), null);
        }
        boolean z3 = (extension.getImplementationURL() == null && extension.getImplementationVersion() == null && extension.getImplementationVendorID() == null && extension.getImplementationVendor() == null) ? false : true;
        if (!z && z3) {
            extension = new Extension(extension.getExtensionName(), extension.getSpecificationVersion().toString(), extension.getSpecificationVendor(), null, null, null, extension.getImplementationURL());
        }
        list.add(extension);
    }

    static Manifest getManifest(File file) throws BuildException {
        try {
            Manifest manifest = new JarFile(file).getManifest();
            if (manifest != null) {
                return manifest;
            }
            throw new BuildException(file + " doesn't have a MANIFEST");
        } catch (IOException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }
}
