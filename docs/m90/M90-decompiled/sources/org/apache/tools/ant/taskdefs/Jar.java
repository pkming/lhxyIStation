package org.apache.tools.ant.taskdefs;

import com.unisound.common.r;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.Definer;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSSConstants;
import org.apache.tools.ant.types.ArchiveFileSet;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.ant.types.spi.Service;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.zip.JarMarker;
import org.apache.tools.zip.ZipExtraField;
import org.apache.tools.zip.ZipOutputStream;

/* JADX INFO: loaded from: classes3.dex */
public class Jar extends Zip {
    private static final String INDEX_NAME = "META-INF/INDEX.LIST";
    private static final ZipExtraField[] JAR_MARKER = {JarMarker.getInstance()};
    private static final String MANIFEST_NAME = "META-INF/MANIFEST.MF";
    private Manifest configuredManifest;
    private Manifest filesetManifest;
    private FilesetManifestConfig filesetManifestConfig;
    private Path indexJars;
    private Manifest manifest;
    private String manifestEncoding;
    private File manifestFile;
    private Manifest originalManifest;
    private Vector<String> rootEntries;
    private Manifest savedConfiguredManifest;
    private List<Service> serviceList = new ArrayList();
    private boolean mergeManifestsMain = true;
    private boolean index = false;
    private boolean indexMetaInf = false;
    private boolean createEmpty = false;
    private StrictMode strict = new StrictMode(Definer.OnError.POLICY_IGNORE);
    private boolean mergeClassPaths = false;
    private boolean flattenClassPaths = false;

    public Jar() {
        this.archiveType = "jar";
        this.emptyBehavior = r.s;
        setEncoding("UTF8");
        setZip64Mode(Zip.Zip64ModeAttribute.NEVER);
        this.rootEntries = new Vector<>();
    }

    @Override // org.apache.tools.ant.taskdefs.Zip
    public void setWhenempty(Zip.WhenEmpty whenEmpty) {
        log("JARs are never empty, they contain at least a manifest file", 1);
    }

    public void setWhenmanifestonly(Zip.WhenEmpty whenEmpty) {
        this.emptyBehavior = whenEmpty.getValue();
    }

    public void setStrict(StrictMode strictMode) {
        this.strict = strictMode;
    }

    public void setJarfile(File file) {
        setDestFile(file);
    }

    public void setIndex(boolean z) {
        this.index = z;
    }

    public void setIndexMetaInf(boolean z) {
        this.indexMetaInf = z;
    }

    public void setManifestEncoding(String str) {
        this.manifestEncoding = str;
    }

    public void addConfiguredManifest(Manifest manifest) throws ManifestException {
        Manifest manifest2 = this.configuredManifest;
        if (manifest2 == null) {
            this.configuredManifest = manifest;
        } else {
            manifest2.merge(manifest, false, this.mergeClassPaths);
        }
        this.savedConfiguredManifest = this.configuredManifest;
    }

    public void setManifest(File file) {
        if (!file.exists()) {
            throw new BuildException("Manifest file: " + file + DirectoryScanner.DOES_NOT_EXIST_POSTFIX, getLocation());
        }
        this.manifestFile = file;
    }

    private Manifest getManifest(File file) {
        InputStreamReader inputStreamReader;
        InputStreamReader inputStreamReader2 = null;
        try {
            try {
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    if (this.manifestEncoding == null) {
                        inputStreamReader = new InputStreamReader(fileInputStream);
                    } else {
                        inputStreamReader = new InputStreamReader(fileInputStream, this.manifestEncoding);
                    }
                    inputStreamReader2 = inputStreamReader;
                    return getManifest(inputStreamReader2);
                } catch (UnsupportedEncodingException e) {
                    throw new BuildException("Unsupported encoding while reading manifest: " + e.getMessage(), e);
                }
            } catch (IOException e2) {
                throw new BuildException("Unable to read manifest file: " + file + " (" + e2.getMessage() + ")", e2);
            }
        } finally {
            FileUtils.close(inputStreamReader2);
        }
    }

    private Manifest getManifestFromJar(File file) throws Throwable {
        ZipFile zipFile = null;
        try {
            ZipFile zipFile2 = new ZipFile(file);
            try {
                Enumeration<? extends ZipEntry> enumerationEntries = zipFile2.entries();
                while (enumerationEntries.hasMoreElements()) {
                    ZipEntry zipEntryNextElement = enumerationEntries.nextElement();
                    if (zipEntryNextElement.getName().equalsIgnoreCase(MANIFEST_NAME)) {
                        Manifest manifest = getManifest(new InputStreamReader(zipFile2.getInputStream(zipEntryNextElement), "UTF-8"));
                        try {
                            zipFile2.close();
                        } catch (IOException unused) {
                        }
                        return manifest;
                    }
                }
                try {
                    zipFile2.close();
                } catch (IOException unused2) {
                }
                return null;
            } catch (Throwable th) {
                th = th;
                zipFile = zipFile2;
                if (zipFile != null) {
                    try {
                        zipFile.close();
                    } catch (IOException unused3) {
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private Manifest getManifest(Reader reader) {
        try {
            return new Manifest(reader);
        } catch (IOException e) {
            throw new BuildException("Unable to read manifest file (" + e.getMessage() + ")", e);
        } catch (ManifestException e2) {
            log("Manifest is invalid: " + e2.getMessage(), 0);
            throw new BuildException("Invalid Manifest: " + this.manifestFile, e2, getLocation());
        }
    }

    private boolean jarHasIndex(File file) throws Throwable {
        ZipFile zipFile = null;
        try {
            ZipFile zipFile2 = new ZipFile(file);
            try {
                Enumeration<? extends ZipEntry> enumerationEntries = zipFile2.entries();
                while (enumerationEntries.hasMoreElements()) {
                    if (enumerationEntries.nextElement().getName().equalsIgnoreCase(INDEX_NAME)) {
                        try {
                            zipFile2.close();
                        } catch (IOException unused) {
                        }
                        return true;
                    }
                }
                try {
                    zipFile2.close();
                } catch (IOException unused2) {
                }
                return false;
            } catch (Throwable th) {
                th = th;
                zipFile = zipFile2;
                if (zipFile != null) {
                    try {
                        zipFile.close();
                    } catch (IOException unused3) {
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void setFilesetmanifest(FilesetManifestConfig filesetManifestConfig) {
        this.filesetManifestConfig = filesetManifestConfig;
        this.mergeManifestsMain = "merge".equals(filesetManifestConfig.getValue());
        FilesetManifestConfig filesetManifestConfig2 = this.filesetManifestConfig;
        if (filesetManifestConfig2 == null || filesetManifestConfig2.getValue().equals(MSVSSConstants.WRITABLE_SKIP)) {
            return;
        }
        this.doubleFilePass = true;
    }

    public void addMetainf(ZipFileSet zipFileSet) {
        zipFileSet.setPrefix("META-INF/");
        super.addFileset(zipFileSet);
    }

    public void addConfiguredIndexJars(Path path) {
        if (this.indexJars == null) {
            this.indexJars = new Path(getProject());
        }
        this.indexJars.append(path);
    }

    public void addConfiguredService(Service service) {
        service.check();
        this.serviceList.add(service);
    }

    private void writeServices(ZipOutputStream zipOutputStream) throws IOException {
        for (Service service : this.serviceList) {
            InputStream asStream = null;
            try {
                asStream = service.getAsStream();
                super.zipFile(asStream, zipOutputStream, "META-INF/services/" + service.getType(), System.currentTimeMillis(), null, 33188);
            } finally {
                FileUtils.close(asStream);
            }
        }
    }

    public void setMergeClassPathAttributes(boolean z) {
        this.mergeClassPaths = z;
    }

    public void setFlattenAttributes(boolean z) {
        this.flattenClassPaths = z;
    }

    @Override // org.apache.tools.ant.taskdefs.Zip
    protected void initZipOutputStream(ZipOutputStream zipOutputStream) throws IOException, BuildException {
        if (this.skipWriting) {
            return;
        }
        writeManifest(zipOutputStream, createManifest());
        writeServices(zipOutputStream);
    }

    private Manifest createManifest() throws Throwable {
        Manifest defaultManifest;
        File file;
        try {
            if (this.manifest == null && (file = this.manifestFile) != null) {
                this.manifest = getManifest(file);
            }
            boolean z = true;
            boolean z2 = !this.mergeManifestsMain && this.filesetManifest != null && this.configuredManifest == null && this.manifest == null;
            if (z2) {
                defaultManifest = new Manifest();
                defaultManifest.merge(this.filesetManifest, false, this.mergeClassPaths);
                defaultManifest.merge(Manifest.getDefaultManifest(), true, this.mergeClassPaths);
            } else {
                defaultManifest = Manifest.getDefaultManifest();
            }
            if (isInUpdateMode()) {
                defaultManifest.merge(this.originalManifest, false, this.mergeClassPaths);
            }
            if (!z2) {
                defaultManifest.merge(this.filesetManifest, false, this.mergeClassPaths);
            }
            defaultManifest.merge(this.configuredManifest, !this.mergeManifestsMain, this.mergeClassPaths);
            Manifest manifest = this.manifest;
            if (this.mergeManifestsMain) {
                z = false;
            }
            defaultManifest.merge(manifest, z, this.mergeClassPaths);
            return defaultManifest;
        } catch (ManifestException e) {
            log("Manifest is invalid: " + e.getMessage(), 0);
            throw new BuildException("Invalid Manifest", e, getLocation());
        }
    }

    private void writeManifest(ZipOutputStream zipOutputStream, Manifest manifest) throws IOException {
        Enumeration<String> warnings = manifest.getWarnings();
        while (warnings.hasMoreElements()) {
            log("Manifest warning: " + warnings.nextElement(), 1);
        }
        zipDir((Resource) null, zipOutputStream, "META-INF/", 16877, JAR_MARKER);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(byteArrayOutputStream, "UTF-8"));
        manifest.write(printWriter, this.flattenClassPaths);
        if (printWriter.checkError()) {
            throw new IOException("Encountered an error writing the manifest");
        }
        printWriter.close();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        try {
            super.zipFile(byteArrayInputStream, zipOutputStream, MANIFEST_NAME, System.currentTimeMillis(), null, 33188);
            FileUtils.close(byteArrayInputStream);
            super.initZipOutputStream(zipOutputStream);
        } catch (Throwable th) {
            FileUtils.close(byteArrayInputStream);
            throw th;
        }
    }

    @Override // org.apache.tools.ant.taskdefs.Zip
    protected void finalizeZipOutputStream(ZipOutputStream zipOutputStream) throws Throwable {
        if (this.index) {
            createIndexList(zipOutputStream);
        }
    }

    private void createIndexList(ZipOutputStream zipOutputStream) throws Throwable {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(byteArrayOutputStream, "UTF8"));
        printWriter.println("JarIndex-Version: 1.0");
        printWriter.println();
        printWriter.println(this.zipFile.getName());
        writeIndexLikeList(new ArrayList<>(this.addedDirs.keySet()), this.rootEntries, printWriter);
        printWriter.println();
        if (this.indexJars != null) {
            Manifest.Attribute attribute = createManifest().getMainSection().getAttribute(Manifest.ATTRIBUTE_CLASSPATH);
            String[] strArr = null;
            if (attribute != null && attribute.getValue() != null) {
                StringTokenizer stringTokenizer = new StringTokenizer(attribute.getValue(), " ");
                String[] strArr2 = new String[stringTokenizer.countTokens()];
                int i = 0;
                while (stringTokenizer.hasMoreTokens()) {
                    strArr2[i] = stringTokenizer.nextToken();
                    i++;
                }
                strArr = strArr2;
            }
            String[] list = this.indexJars.list();
            for (int i2 = 0; i2 < list.length; i2++) {
                String strFindJarName = findJarName(list[i2], strArr);
                if (strFindJarName != null) {
                    ArrayList arrayList = new ArrayList();
                    ArrayList arrayList2 = new ArrayList();
                    grabFilesAndDirs(list[i2], arrayList, arrayList2);
                    if (arrayList.size() + arrayList2.size() > 0) {
                        printWriter.println(strFindJarName);
                        writeIndexLikeList(arrayList, arrayList2, printWriter);
                        printWriter.println();
                    }
                }
            }
        }
        if (printWriter.checkError()) {
            throw new IOException("Encountered an error writing jar index");
        }
        printWriter.close();
        InputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        try {
            super.zipFile(byteArrayInputStream, zipOutputStream, INDEX_NAME, System.currentTimeMillis(), null, 33188);
        } finally {
            FileUtils.close(byteArrayInputStream);
        }
    }

    @Override // org.apache.tools.ant.taskdefs.Zip
    protected void zipFile(InputStream inputStream, ZipOutputStream zipOutputStream, String str, long j, File file, int i) throws IOException {
        if (MANIFEST_NAME.equalsIgnoreCase(str)) {
            if (isFirstPass()) {
                filesetManifest(file, inputStream);
            }
        } else {
            if (INDEX_NAME.equalsIgnoreCase(str) && this.index) {
                logWhenWriting("Warning: selected " + this.archiveType + " files include a " + INDEX_NAME + " which will be replaced by a newly generated one.", 1);
                return;
            }
            if (this.index && str.indexOf("/") == -1) {
                this.rootEntries.addElement(str);
            }
            super.zipFile(inputStream, zipOutputStream, str, j, file, i);
        }
    }

    private void filesetManifest(File file, InputStream inputStream) throws IOException {
        Manifest manifest;
        InputStreamReader inputStreamReader;
        InputStreamReader inputStreamReader2;
        File file2 = this.manifestFile;
        if (file2 != null && file2.equals(file)) {
            log("Found manifest " + file, 3);
            try {
                if (inputStream != null) {
                    if (this.manifestEncoding == null) {
                        inputStreamReader2 = new InputStreamReader(inputStream);
                    } else {
                        inputStreamReader2 = new InputStreamReader(inputStream, this.manifestEncoding);
                    }
                    this.manifest = getManifest(inputStreamReader2);
                    return;
                }
                this.manifest = getManifest(file);
                return;
            } catch (UnsupportedEncodingException e) {
                throw new BuildException("Unsupported encoding while reading manifest: " + e.getMessage(), e);
            }
        }
        FilesetManifestConfig filesetManifestConfig = this.filesetManifestConfig;
        if (filesetManifestConfig == null || filesetManifestConfig.getValue().equals(MSVSSConstants.WRITABLE_SKIP)) {
            return;
        }
        logWhenWriting("Found manifest to merge in file " + file, 3);
        try {
            if (inputStream != null) {
                if (this.manifestEncoding == null) {
                    inputStreamReader = new InputStreamReader(inputStream);
                } else {
                    inputStreamReader = new InputStreamReader(inputStream, this.manifestEncoding);
                }
                manifest = getManifest(inputStreamReader);
            } else {
                manifest = getManifest(file);
            }
            Manifest manifest2 = this.filesetManifest;
            if (manifest2 == null) {
                this.filesetManifest = manifest;
            } else {
                manifest2.merge(manifest, false, this.mergeClassPaths);
            }
        } catch (UnsupportedEncodingException e2) {
            throw new BuildException("Unsupported encoding while reading manifest: " + e2.getMessage(), e2);
        } catch (ManifestException e3) {
            log("Manifest in file " + file + " is invalid: " + e3.getMessage(), 0);
            throw new BuildException("Invalid Manifest", e3, getLocation());
        }
    }

    @Override // org.apache.tools.ant.taskdefs.Zip
    protected Zip.ArchiveState getResourcesToAdd(ResourceCollection[] resourceCollectionArr, File file, boolean z) throws BuildException {
        boolean zJarHasIndex = true;
        if (this.skipWriting) {
            Resource[][] resourceArrGrabManifests = grabManifests(resourceCollectionArr);
            int length = 0;
            for (Resource[] resourceArr : resourceArrGrabManifests) {
                length += resourceArr.length;
            }
            log("found a total of " + length + " manifests in " + resourceArrGrabManifests.length + " resource collections", 3);
            return new Zip.ArchiveState(true, resourceArrGrabManifests);
        }
        if (file.exists()) {
            try {
                Manifest manifestFromJar = getManifestFromJar(file);
                this.originalManifest = manifestFromJar;
                if (manifestFromJar == null) {
                    log("Updating jar since the current jar has no manifest", 3);
                } else if (!createManifest().equals(this.originalManifest)) {
                    log("Updating jar since jar manifest has changed", 3);
                }
            } catch (Throwable th) {
                log("error while reading original manifest in file: " + file.toString() + " due to " + th.getMessage(), 1);
            }
            z = true;
        } else {
            z = true;
        }
        this.createEmpty = z;
        if (z || !this.index) {
            zJarHasIndex = z;
        } else {
            try {
                zJarHasIndex = true ^ jarHasIndex(file);
            } catch (IOException unused) {
            }
        }
        return super.getResourcesToAdd(resourceCollectionArr, file, zJarHasIndex);
    }

    @Override // org.apache.tools.ant.taskdefs.Zip
    protected boolean createEmptyZip(File file) throws Throwable {
        ZipOutputStream zipOutputStream;
        if (!this.createEmpty) {
            return true;
        }
        if (this.emptyBehavior.equals(MSVSSConstants.WRITABLE_SKIP)) {
            if (!this.skipWriting) {
                log("Warning: skipping " + this.archiveType + " archive " + file + " because no files were included.", 1);
            }
            return true;
        }
        if (this.emptyBehavior.equals("fail")) {
            throw new BuildException("Cannot create " + this.archiveType + " archive " + file + ": no files were included.", getLocation());
        }
        ZipOutputStream zipOutputStream2 = null;
        try {
            try {
                if (!this.skipWriting) {
                    log("Building MANIFEST-only jar: " + getDestFile().getAbsolutePath());
                }
                zipOutputStream = new ZipOutputStream(getDestFile());
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException e) {
            e = e;
        }
        try {
            zipOutputStream.setEncoding(getEncoding());
            if (isCompress()) {
                zipOutputStream.setMethod(8);
            } else {
                zipOutputStream.setMethod(0);
            }
            initZipOutputStream(zipOutputStream);
            finalizeZipOutputStream(zipOutputStream);
            FileUtils.close(zipOutputStream);
            this.createEmpty = false;
            return true;
        } catch (IOException e2) {
            e = e2;
            throw new BuildException("Could not create almost empty JAR archive (" + e.getMessage() + ")", e, getLocation());
        } catch (Throwable th2) {
            th = th2;
            zipOutputStream2 = zipOutputStream;
            FileUtils.close(zipOutputStream2);
            this.createEmpty = false;
            throw th;
        }
    }

    @Override // org.apache.tools.ant.taskdefs.Zip
    protected void cleanUp() {
        super.cleanUp();
        checkJarSpec();
        if (!this.doubleFilePass || !this.skipWriting) {
            this.manifest = null;
            this.configuredManifest = this.savedConfiguredManifest;
            this.filesetManifest = null;
            this.originalManifest = null;
        }
        this.rootEntries.removeAllElements();
    }

    private void checkJarSpec() {
        String property = System.getProperty("line.separator");
        StringBuffer stringBuffer = new StringBuffer();
        Manifest manifest = this.configuredManifest;
        Manifest.Section mainSection = manifest == null ? null : manifest.getMainSection();
        if (mainSection == null) {
            stringBuffer.append("No Implementation-Title set.");
            stringBuffer.append("No Implementation-Version set.");
            stringBuffer.append("No Implementation-Vendor set.");
        } else {
            if (mainSection.getAttribute("Implementation-Title") == null) {
                stringBuffer.append("No Implementation-Title set.");
            }
            if (mainSection.getAttribute("Implementation-Version") == null) {
                stringBuffer.append("No Implementation-Version set.");
            }
            if (mainSection.getAttribute("Implementation-Vendor") == null) {
                stringBuffer.append("No Implementation-Vendor set.");
            }
        }
        if (stringBuffer.length() > 0) {
            stringBuffer.append(property);
            stringBuffer.append("Location: ").append(getLocation());
            stringBuffer.append(property);
            if (this.strict.getValue().equalsIgnoreCase("fail")) {
                throw new BuildException(stringBuffer.toString(), getLocation());
            }
            logWhenWriting(stringBuffer.toString(), this.strict.getLogLevel());
        }
    }

    @Override // org.apache.tools.ant.taskdefs.Zip
    public void reset() {
        super.reset();
        this.emptyBehavior = r.s;
        this.configuredManifest = null;
        this.filesetManifestConfig = null;
        this.mergeManifestsMain = false;
        this.manifestFile = null;
        this.index = false;
    }

    public static class FilesetManifestConfig extends EnumeratedAttribute {
        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{MSVSSConstants.WRITABLE_SKIP, "merge", "mergewithoutmain"};
        }
    }

    protected final void writeIndexLikeList(List<String> list, List<String> list2, PrintWriter printWriter) throws IOException {
        Collections.sort(list);
        Collections.sort(list2);
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String strReplace = it.next().replace('\\', '/');
            if (strReplace.startsWith("./")) {
                strReplace = strReplace.substring(2);
            }
            while (strReplace.startsWith("/")) {
                strReplace = strReplace.substring(1);
            }
            int iLastIndexOf = strReplace.lastIndexOf(47);
            if (iLastIndexOf != -1) {
                strReplace = strReplace.substring(0, iLastIndexOf);
            }
            if (this.indexMetaInf || !strReplace.startsWith("META-INF")) {
                printWriter.println(strReplace);
            }
        }
        Iterator<String> it2 = list2.iterator();
        while (it2.hasNext()) {
            printWriter.println(it2.next());
        }
    }

    protected static String findJarName(String str, String[] strArr) {
        if (strArr == null) {
            return new File(str).getName();
        }
        String strReplace = str.replace(File.separatorChar, '/');
        TreeMap treeMap = new TreeMap(new Comparator<Object>() { // from class: org.apache.tools.ant.taskdefs.Jar.1
            @Override // java.util.Comparator
            public int compare(Object obj, Object obj2) {
                if ((obj instanceof String) && (obj2 instanceof String)) {
                    return ((String) obj2).length() - ((String) obj).length();
                }
                return 0;
            }
        });
        for (int i = 0; i < strArr.length; i++) {
            if (strReplace.endsWith(strArr[i])) {
                treeMap.put(strArr[i], strArr[i]);
            } else {
                int iIndexOf = strArr[i].indexOf("/");
                String strSubstring = strArr[i];
                while (true) {
                    if (iIndexOf > -1) {
                        strSubstring = strSubstring.substring(iIndexOf + 1);
                        if (strReplace.endsWith(strSubstring)) {
                            treeMap.put(strSubstring, strArr[i]);
                            break;
                        }
                        iIndexOf = strSubstring.indexOf("/");
                    }
                }
            }
        }
        if (treeMap.size() == 0) {
            return null;
        }
        return (String) treeMap.get(treeMap.firstKey());
    }

    protected static void grabFilesAndDirs(String str, List<String> list, List<String> list2) throws Throwable {
        org.apache.tools.zip.ZipFile zipFile;
        org.apache.tools.zip.ZipFile zipFile2 = null;
        try {
            zipFile = new org.apache.tools.zip.ZipFile(str, "utf-8");
        } catch (Throwable th) {
            th = th;
        }
        try {
            Enumeration<org.apache.tools.zip.ZipEntry> entries = zipFile.getEntries();
            HashSet hashSet = new HashSet();
            while (entries.hasMoreElements()) {
                org.apache.tools.zip.ZipEntry zipEntryNextElement = entries.nextElement();
                String name = zipEntryNextElement.getName();
                if (zipEntryNextElement.isDirectory()) {
                    hashSet.add(name);
                } else if (name.indexOf("/") == -1) {
                    list2.add(name);
                } else {
                    hashSet.add(name.substring(0, name.lastIndexOf("/") + 1));
                }
            }
            list.addAll(hashSet);
            zipFile.close();
        } catch (Throwable th2) {
            th = th2;
            zipFile2 = zipFile;
            if (zipFile2 != null) {
                zipFile2.close();
            }
            throw th;
        }
    }

    private Resource[][] grabManifests(ResourceCollection[] resourceCollectionArr) {
        Resource[][] resourceArr = new Resource[resourceCollectionArr.length][];
        for (int i = 0; i < resourceCollectionArr.length; i++) {
            Resource[][] resourceArrGrabResources = resourceCollectionArr[i] instanceof FileSet ? grabResources(new FileSet[]{(FileSet) resourceCollectionArr[i]}) : grabNonFileSetResources(new ResourceCollection[]{resourceCollectionArr[i]});
            int i2 = 0;
            while (true) {
                if (i2 >= resourceArrGrabResources[0].length) {
                    break;
                }
                String strReplace = resourceArrGrabResources[0][i2].getName().replace('\\', '/');
                if (resourceCollectionArr[i] instanceof ArchiveFileSet) {
                    ArchiveFileSet archiveFileSet = (ArchiveFileSet) resourceCollectionArr[i];
                    if (!"".equals(archiveFileSet.getFullpath(getProject()))) {
                        strReplace = archiveFileSet.getFullpath(getProject());
                    } else if (!"".equals(archiveFileSet.getPrefix(getProject()))) {
                        String prefix = archiveFileSet.getPrefix(getProject());
                        if (!prefix.endsWith("/") && !prefix.endsWith("\\")) {
                            prefix = prefix + "/";
                        }
                        strReplace = prefix + strReplace;
                    }
                }
                if (strReplace.equalsIgnoreCase(MANIFEST_NAME)) {
                    resourceArr[i] = new Resource[]{resourceArrGrabResources[0][i2]};
                    break;
                }
                i2++;
            }
            if (resourceArr[i] == null) {
                resourceArr[i] = new Resource[0];
            }
        }
        return resourceArr;
    }

    public static class StrictMode extends EnumeratedAttribute {
        public StrictMode() {
        }

        public StrictMode(String str) {
            setValue(str);
        }

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"fail", "warn", Definer.OnError.POLICY_IGNORE};
        }

        public int getLogLevel() {
            return getValue().equals(Definer.OnError.POLICY_IGNORE) ? 3 : 1;
        }
    }
}
