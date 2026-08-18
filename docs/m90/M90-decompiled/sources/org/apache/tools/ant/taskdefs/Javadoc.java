package org.apache.tools.ant.taskdefs;

import android.app.backup.FullBackup;
import android.media.MediaPlayer;
import android.text.format.DateFormat;
import com.unisound.common.r;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Definer;
import org.apache.tools.ant.taskdefs.optional.sos.SOSCmd;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.util.StringUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Javadoc extends Task {
    private static final String LOAD_FRAME = "function loadFrames() {";
    private String noqualifier;
    private static final boolean JAVADOC_5 = !JavaEnvUtils.isJavaVersion("1.4");
    private static final int LOAD_FRAME_LEN = 23;
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    static final String[] SCOPE_ELEMENTS = {"overview", "packages", "types", "constructors", "methods", "fields"};
    private Commandline cmd = new Commandline();
    private boolean failOnError = false;
    private boolean failOnWarning = false;
    private Path sourcePath = null;
    private File destDir = null;
    private Vector<SourceFile> sourceFiles = new Vector<>();
    private Vector<PackageName> packageNames = new Vector<>();
    private Vector<PackageName> excludePackageNames = new Vector<>(1);
    private boolean author = true;
    private boolean version = true;
    private DocletInfo doclet = null;
    private Path classpath = null;
    private Path bootclasspath = null;
    private String group = null;
    private String packageList = null;
    private Vector<LinkArgument> links = new Vector<>();
    private Vector<GroupArgument> groups = new Vector<>();
    private Vector<Object> tags = new Vector<>();
    private boolean useDefaultExcludes = true;
    private Html doctitle = null;
    private Html header = null;
    private Html footer = null;
    private Html bottom = null;
    private boolean useExternalFile = false;
    private String source = null;
    private boolean linksource = false;
    private boolean breakiterator = false;
    private boolean includeNoSourcePackages = false;
    private String executable = null;
    private boolean docFilesSubDirs = false;
    private String excludeDocFilesSubDir = null;
    private String docEncoding = null;
    private boolean postProcessGeneratedJavadocs = true;
    private ResourceCollectionContainer nestedSourceFiles = new ResourceCollectionContainer();
    private Vector<DirSet> packageSets = new Vector<>();

    public class DocletParam {
        private String name;
        private String value;

        public DocletParam() {
        }

        public void setName(String str) {
            this.name = str;
        }

        public String getName() {
            return this.name;
        }

        public void setValue(String str) {
            this.value = str;
        }

        public String getValue() {
            return this.value;
        }
    }

    public static class ExtensionInfo extends ProjectComponent {
        private String name;
        private Path path;

        public void setName(String str) {
            this.name = str;
        }

        public String getName() {
            return this.name;
        }

        public void setPath(Path path) {
            Path path2 = this.path;
            if (path2 == null) {
                this.path = path;
            } else {
                path2.append(path);
            }
        }

        public Path getPath() {
            return this.path;
        }

        public Path createPath() {
            if (this.path == null) {
                this.path = new Path(getProject());
            }
            return this.path.createPath();
        }

        public void setPathRef(Reference reference) {
            createPath().setRefid(reference);
        }
    }

    public class DocletInfo extends ExtensionInfo {
        private Vector<DocletParam> params = new Vector<>();

        public DocletInfo() {
        }

        public DocletParam createParam() {
            DocletParam docletParam = Javadoc.this.new DocletParam();
            this.params.addElement(docletParam);
            return docletParam;
        }

        public Enumeration<DocletParam> getParams() {
            return this.params.elements();
        }
    }

    public static class PackageName {
        private String name;

        public void setName(String str) {
            this.name = str.trim();
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return getName();
        }
    }

    public static class SourceFile {
        private File file;

        public SourceFile() {
        }

        public SourceFile(File file) {
            this.file = file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public File getFile() {
            return this.file;
        }
    }

    public static class Html {
        private StringBuffer text = new StringBuffer();

        public void addText(String str) {
            this.text.append(str);
        }

        public String getText() {
            return this.text.substring(0);
        }
    }

    public static class AccessType extends EnumeratedAttribute {
        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"protected", "public", "package", "private"};
        }
    }

    public class ResourceCollectionContainer {
        private ArrayList<ResourceCollection> rcs = new ArrayList<>();

        public ResourceCollectionContainer() {
        }

        public void add(ResourceCollection resourceCollection) {
            this.rcs.add(resourceCollection);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Iterator<ResourceCollection> iterator() {
            return this.rcs.iterator();
        }
    }

    private void addArgIf(boolean z, String str) {
        if (z) {
            this.cmd.createArgument().setValue(str);
        }
    }

    private void addArgIfNotEmpty(String str, String str2) {
        if (str2 != null && str2.length() != 0) {
            this.cmd.createArgument().setValue(str);
            this.cmd.createArgument().setValue(str2);
        } else {
            log("Warning: Leaving out empty argument '" + str + "'", 1);
        }
    }

    public void setUseExternalFile(boolean z) {
        this.useExternalFile = z;
    }

    public void setDefaultexcludes(boolean z) {
        this.useDefaultExcludes = z;
    }

    public void setMaxmemory(String str) {
        this.cmd.createArgument().setValue("-J-Xmx" + str);
    }

    public void setAdditionalparam(String str) {
        this.cmd.createArgument().setLine(str);
    }

    public Commandline.Argument createArg() {
        return this.cmd.createArgument();
    }

    public void setSourcepath(Path path) {
        Path path2 = this.sourcePath;
        if (path2 == null) {
            this.sourcePath = path;
        } else {
            path2.append(path);
        }
    }

    public Path createSourcepath() {
        if (this.sourcePath == null) {
            this.sourcePath = new Path(getProject());
        }
        return this.sourcePath.createPath();
    }

    public void setSourcepathRef(Reference reference) {
        createSourcepath().setRefid(reference);
    }

    public void setDestdir(File file) {
        this.destDir = file;
        this.cmd.createArgument().setValue("-d");
        this.cmd.createArgument().setFile(this.destDir);
    }

    public void setSourcefiles(String str) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
        while (stringTokenizer.hasMoreTokens()) {
            String strNextToken = stringTokenizer.nextToken();
            SourceFile sourceFile = new SourceFile();
            sourceFile.setFile(getProject().resolveFile(strNextToken.trim()));
            addSource(sourceFile);
        }
    }

    public void addSource(SourceFile sourceFile) {
        this.sourceFiles.addElement(sourceFile);
    }

    public void setPackagenames(String str) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
        while (stringTokenizer.hasMoreTokens()) {
            String strNextToken = stringTokenizer.nextToken();
            PackageName packageName = new PackageName();
            packageName.setName(strNextToken);
            addPackage(packageName);
        }
    }

    public void addPackage(PackageName packageName) {
        this.packageNames.addElement(packageName);
    }

    public void setExcludePackageNames(String str) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
        while (stringTokenizer.hasMoreTokens()) {
            String strNextToken = stringTokenizer.nextToken();
            PackageName packageName = new PackageName();
            packageName.setName(strNextToken);
            addExcludePackage(packageName);
        }
    }

    public void addExcludePackage(PackageName packageName) {
        this.excludePackageNames.addElement(packageName);
    }

    public void setOverview(File file) {
        this.cmd.createArgument().setValue("-overview");
        this.cmd.createArgument().setFile(file);
    }

    public void setPublic(boolean z) {
        addArgIf(z, "-public");
    }

    public void setProtected(boolean z) {
        addArgIf(z, "-protected");
    }

    public void setPackage(boolean z) {
        addArgIf(z, "-package");
    }

    public void setPrivate(boolean z) {
        addArgIf(z, "-private");
    }

    public void setAccess(AccessType accessType) {
        this.cmd.createArgument().setValue("-" + accessType.getValue());
    }

    public void setDoclet(String str) {
        if (this.doclet == null) {
            DocletInfo docletInfo = new DocletInfo();
            this.doclet = docletInfo;
            docletInfo.setProject(getProject());
        }
        this.doclet.setName(str);
    }

    public void setDocletPath(Path path) {
        if (this.doclet == null) {
            DocletInfo docletInfo = new DocletInfo();
            this.doclet = docletInfo;
            docletInfo.setProject(getProject());
        }
        this.doclet.setPath(path);
    }

    public void setDocletPathRef(Reference reference) {
        if (this.doclet == null) {
            DocletInfo docletInfo = new DocletInfo();
            this.doclet = docletInfo;
            docletInfo.setProject(getProject());
        }
        this.doclet.createPath().setRefid(reference);
    }

    public DocletInfo createDoclet() {
        if (this.doclet == null) {
            this.doclet = new DocletInfo();
        }
        return this.doclet;
    }

    public void addTaglet(ExtensionInfo extensionInfo) {
        this.tags.addElement(extensionInfo);
    }

    public void setOld(boolean z) {
        log("Javadoc 1.4 doesn't support the -1.1 switch anymore", 1);
    }

    public void setClasspath(Path path) {
        Path path2 = this.classpath;
        if (path2 == null) {
            this.classpath = path;
        } else {
            path2.append(path);
        }
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(getProject());
        }
        return this.classpath.createPath();
    }

    public void setClasspathRef(Reference reference) {
        createClasspath().setRefid(reference);
    }

    public void setBootclasspath(Path path) {
        Path path2 = this.bootclasspath;
        if (path2 == null) {
            this.bootclasspath = path;
        } else {
            path2.append(path);
        }
    }

    public Path createBootclasspath() {
        if (this.bootclasspath == null) {
            this.bootclasspath = new Path(getProject());
        }
        return this.bootclasspath.createPath();
    }

    public void setBootClasspathRef(Reference reference) {
        createBootclasspath().setRefid(reference);
    }

    public void setExtdirs(String str) {
        this.cmd.createArgument().setValue("-extdirs");
        this.cmd.createArgument().setValue(str);
    }

    public void setExtdirs(Path path) {
        this.cmd.createArgument().setValue("-extdirs");
        this.cmd.createArgument().setPath(path);
    }

    public void setVerbose(boolean z) {
        addArgIf(z, SOSCmd.FLAG_VERBOSE);
    }

    public void setLocale(String str) {
        this.cmd.createArgument(true).setValue(str);
        this.cmd.createArgument(true).setValue("-locale");
    }

    public void setEncoding(String str) {
        this.cmd.createArgument().setValue("-encoding");
        this.cmd.createArgument().setValue(str);
    }

    public void setVersion(boolean z) {
        this.version = z;
    }

    public void setUse(boolean z) {
        addArgIf(z, "-use");
    }

    public void setAuthor(boolean z) {
        this.author = z;
    }

    public void setSplitindex(boolean z) {
        addArgIf(z, "-splitindex");
    }

    public void setWindowtitle(String str) {
        addArgIfNotEmpty("-windowtitle", str);
    }

    public void setDoctitle(String str) {
        Html html = new Html();
        html.addText(str);
        addDoctitle(html);
    }

    public void addDoctitle(Html html) {
        this.doctitle = html;
    }

    public void setHeader(String str) {
        Html html = new Html();
        html.addText(str);
        addHeader(html);
    }

    public void addHeader(Html html) {
        this.header = html;
    }

    public void setFooter(String str) {
        Html html = new Html();
        html.addText(str);
        addFooter(html);
    }

    public void addFooter(Html html) {
        this.footer = html;
    }

    public void setBottom(String str) {
        Html html = new Html();
        html.addText(str);
        addBottom(html);
    }

    public void addBottom(Html html) {
        this.bottom = html;
    }

    public void setLinkoffline(String str) {
        LinkArgument linkArgumentCreateLink = createLink();
        linkArgumentCreateLink.setOffline(true);
        if (str.trim().length() == 0) {
            throw new BuildException("The linkoffline attribute must include a URL and a package-list file location separated by a space");
        }
        StringTokenizer stringTokenizer = new StringTokenizer(str, " ", false);
        linkArgumentCreateLink.setHref(stringTokenizer.nextToken());
        if (!stringTokenizer.hasMoreTokens()) {
            throw new BuildException("The linkoffline attribute must include a URL and a package-list file location separated by a space");
        }
        linkArgumentCreateLink.setPackagelistLoc(getProject().resolveFile(stringTokenizer.nextToken()));
    }

    public void setGroup(String str) {
        this.group = str;
    }

    public void setLink(String str) {
        createLink().setHref(str);
    }

    public void setNodeprecated(boolean z) {
        addArgIf(z, "-nodeprecated");
    }

    public void setNodeprecatedlist(boolean z) {
        addArgIf(z, "-nodeprecatedlist");
    }

    public void setNotree(boolean z) {
        addArgIf(z, "-notree");
    }

    public void setNoindex(boolean z) {
        addArgIf(z, "-noindex");
    }

    public void setNohelp(boolean z) {
        addArgIf(z, "-nohelp");
    }

    public void setNonavbar(boolean z) {
        addArgIf(z, "-nonavbar");
    }

    public void setSerialwarn(boolean z) {
        addArgIf(z, "-serialwarn");
    }

    public void setStylesheetfile(File file) {
        this.cmd.createArgument().setValue("-stylesheetfile");
        this.cmd.createArgument().setFile(file);
    }

    public void setHelpfile(File file) {
        this.cmd.createArgument().setValue("-helpfile");
        this.cmd.createArgument().setFile(file);
    }

    public void setDocencoding(String str) {
        this.cmd.createArgument().setValue("-docencoding");
        this.cmd.createArgument().setValue(str);
        this.docEncoding = str;
    }

    public void setPackageList(String str) {
        this.packageList = str;
    }

    public LinkArgument createLink() {
        LinkArgument linkArgument = new LinkArgument();
        this.links.addElement(linkArgument);
        return linkArgument;
    }

    public class LinkArgument {
        private String href;
        private File packagelistLoc;
        private URL packagelistURL;
        private boolean offline = false;
        private boolean resolveLink = false;

        public LinkArgument() {
        }

        public void setHref(String str) {
            this.href = str;
        }

        public String getHref() {
            return this.href;
        }

        public void setPackagelistLoc(File file) {
            this.packagelistLoc = file;
        }

        public File getPackagelistLoc() {
            return this.packagelistLoc;
        }

        public void setPackagelistURL(URL url) {
            this.packagelistURL = url;
        }

        public URL getPackagelistURL() {
            return this.packagelistURL;
        }

        public void setOffline(boolean z) {
            this.offline = z;
        }

        public boolean isLinkOffline() {
            return this.offline;
        }

        public void setResolveLink(boolean z) {
            this.resolveLink = z;
        }

        public boolean shouldResolveLink() {
            return this.resolveLink;
        }
    }

    public TagArgument createTag() {
        TagArgument tagArgument = new TagArgument();
        this.tags.addElement(tagArgument);
        return tagArgument;
    }

    public class TagArgument extends FileSet {
        private String name = null;
        private boolean enabled = true;
        private String scope = FullBackup.APK_TREE_TOKEN;

        public TagArgument() {
        }

        public void setName(String str) {
            this.name = str;
        }

        public void setScope(String str) throws BuildException {
            String lowerCase = str.toLowerCase(Locale.ENGLISH);
            int length = Javadoc.SCOPE_ELEMENTS.length;
            boolean[] zArr = new boolean[length];
            StringTokenizer stringTokenizer = new StringTokenizer(lowerCase, ",");
            boolean z = false;
            boolean z2 = false;
            while (stringTokenizer.hasMoreTokens()) {
                String strTrim = stringTokenizer.nextToken().trim();
                if (strTrim.equals("all")) {
                    if (z2) {
                        getProject().log("Repeated tag scope element: all", 3);
                    }
                    z2 = true;
                } else {
                    int i = 0;
                    while (i < Javadoc.SCOPE_ELEMENTS.length && !strTrim.equals(Javadoc.SCOPE_ELEMENTS[i])) {
                        i++;
                    }
                    if (i == Javadoc.SCOPE_ELEMENTS.length) {
                        throw new BuildException("Unrecognised scope element: " + strTrim);
                    }
                    if (zArr[i]) {
                        getProject().log("Repeated tag scope element: " + strTrim, 3);
                    }
                    zArr[i] = true;
                    z = true;
                }
            }
            if (z && z2) {
                throw new BuildException("Mixture of \"all\" and other scope elements in tag parameter.");
            }
            if (!z && !z2) {
                throw new BuildException("No scope elements specified in tag parameter.");
            }
            if (z2) {
                this.scope = FullBackup.APK_TREE_TOKEN;
                return;
            }
            StringBuffer stringBuffer = new StringBuffer(length);
            for (int i2 = 0; i2 < length; i2++) {
                if (zArr[i2]) {
                    stringBuffer.append(Javadoc.SCOPE_ELEMENTS[i2].charAt(0));
                }
            }
            this.scope = stringBuffer.toString();
        }

        public void setEnabled(boolean z) {
            this.enabled = z;
        }

        public String getParameter() throws BuildException {
            String str = this.name;
            if (str != null) {
                if (!str.equals("")) {
                    if (getDescription() != null) {
                        return this.name + ":" + (this.enabled ? "" : "X") + this.scope + ":" + getDescription();
                    }
                    if (this.enabled && FullBackup.APK_TREE_TOKEN.equals(this.scope)) {
                        return this.name;
                    }
                    return this.name + ":" + (this.enabled ? "" : "X") + this.scope;
                }
            }
            throw new BuildException("No name specified for custom tag.");
        }
    }

    public GroupArgument createGroup() {
        GroupArgument groupArgument = new GroupArgument();
        this.groups.addElement(groupArgument);
        return groupArgument;
    }

    public class GroupArgument {
        private Vector<PackageName> packages = new Vector<>();
        private Html title;

        public GroupArgument() {
        }

        public void setTitle(String str) {
            Html html = new Html();
            html.addText(str);
            addTitle(html);
        }

        public void addTitle(Html html) {
            this.title = html;
        }

        public String getTitle() {
            Html html = this.title;
            if (html != null) {
                return html.getText();
            }
            return null;
        }

        public void setPackages(String str) {
            StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
            while (stringTokenizer.hasMoreTokens()) {
                String strNextToken = stringTokenizer.nextToken();
                PackageName packageName = new PackageName();
                packageName.setName(strNextToken);
                addPackage(packageName);
            }
        }

        public void addPackage(PackageName packageName) {
            this.packages.addElement(packageName);
        }

        public String getPackages() {
            StringBuffer stringBuffer = new StringBuffer();
            int size = this.packages.size();
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    stringBuffer.append(":");
                }
                stringBuffer.append(this.packages.elementAt(i).toString());
            }
            return stringBuffer.toString();
        }
    }

    public void setCharset(String str) {
        addArgIfNotEmpty("-charset", str);
    }

    public void setFailonerror(boolean z) {
        this.failOnError = z;
    }

    public void setFailonwarning(boolean z) {
        this.failOnWarning = z;
    }

    public void setSource(String str) {
        this.source = str;
    }

    public void setExecutable(String str) {
        this.executable = str;
    }

    public void addPackageset(DirSet dirSet) {
        this.packageSets.addElement(dirSet);
    }

    public void addFileset(FileSet fileSet) {
        createSourceFiles().add(fileSet);
    }

    public ResourceCollectionContainer createSourceFiles() {
        return this.nestedSourceFiles;
    }

    public void setLinksource(boolean z) {
        this.linksource = z;
    }

    public void setBreakiterator(boolean z) {
        this.breakiterator = z;
    }

    public void setNoqualifier(String str) {
        this.noqualifier = str;
    }

    public void setIncludeNoSourcePackages(boolean z) {
        this.includeNoSourcePackages = z;
    }

    public void setDocFilesSubDirs(boolean z) {
        this.docFilesSubDirs = z;
    }

    public void setExcludeDocFilesSubDir(String str) {
        this.excludeDocFilesSubDir = str;
    }

    public void setPostProcessGeneratedJavadocs(boolean z) {
        this.postProcessGeneratedJavadocs = z;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws Throwable {
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        File file;
        FileWriter fileWriter2;
        checkTaskName();
        Vector<String> vector = new Vector<>();
        Path path = new Path(getProject());
        checkPackageAndSourcePath();
        Path path2 = this.sourcePath;
        if (path2 != null) {
            path.addExisting(path2);
        }
        parsePackages(vector, path);
        checkPackages(vector, path);
        Vector<SourceFile> vector2 = (Vector) this.sourceFiles.clone();
        addSourceFiles(vector2);
        checkPackagesToDoc(vector, vector2);
        log("Generating Javadoc", 2);
        Commandline commandline = (Commandline) this.cmd.clone();
        String str = this.executable;
        if (str != null) {
            commandline.setExecutable(str);
        } else {
            commandline.setExecutable(JavaEnvUtils.getJdkExecutable("javadoc"));
        }
        generalJavadocArguments(commandline);
        doSourcePath(commandline, path);
        doDoclet(commandline);
        doBootPath(commandline);
        doLinks(commandline);
        doGroup(commandline);
        doGroups(commandline);
        doDocFilesSubDirs(commandline);
        doJava14(commandline);
        if (this.breakiterator && (this.doclet == null || JAVADOC_5)) {
            commandline.createArgument().setValue("-breakiterator");
        }
        if (this.useExternalFile) {
            writeExternalArgs(commandline);
        }
        File file2 = null;
        FileWriter fileWriter3 = null;
        try {
            try {
                if (this.useExternalFile) {
                    File fileCreateTempFile = FILE_UTILS.createTempFile("javadoc", "", null, true, true);
                    try {
                        commandline.createArgument().setValue("@" + fileCreateTempFile.getAbsolutePath());
                        fileWriter2 = new FileWriter(fileCreateTempFile.getAbsolutePath(), true);
                    } catch (IOException e) {
                        e = e;
                        fileWriter = null;
                        file2 = fileCreateTempFile;
                    }
                    try {
                        bufferedWriter = new BufferedWriter(fileWriter2);
                        file = fileCreateTempFile;
                        fileWriter = fileWriter2;
                    } catch (IOException e2) {
                        e = e2;
                        file2 = fileCreateTempFile;
                        fileWriter = fileWriter2;
                        file2.delete();
                        throw new BuildException("Error creating temporary file", e, getLocation());
                    } catch (Throwable th) {
                        th = th;
                        fileWriter3 = fileWriter2;
                        FileUtils.close(fileWriter3);
                        throw th;
                    }
                } else {
                    bufferedWriter = null;
                    file = null;
                    fileWriter = null;
                }
            } catch (Throwable th2) {
                th = th2;
            }
            try {
                try {
                    doSourceAndPackageNames(commandline, vector, vector2, this.useExternalFile, file, bufferedWriter);
                    if (this.useExternalFile) {
                        bufferedWriter.flush();
                    }
                    FileUtils.close(fileWriter);
                    if (this.packageList != null) {
                        commandline.createArgument().setValue("@" + this.packageList);
                    }
                    log(commandline.describeCommand(), 3);
                    log("Javadoc execution", 2);
                    JavadocOutputStream javadocOutputStream = new JavadocOutputStream(2);
                    JavadocOutputStream javadocOutputStream2 = new JavadocOutputStream(1);
                    Execute execute = new Execute(new PumpStreamHandler(javadocOutputStream, javadocOutputStream2));
                    execute.setAntRun(getProject());
                    execute.setWorkingDirectory(null);
                    try {
                        try {
                            execute.setCommandline(commandline.getCommandline());
                            int iExecute = execute.execute();
                            if (iExecute != 0 && this.failOnError) {
                                throw new BuildException("Javadoc returned " + iExecute, getLocation());
                            }
                            if (javadocOutputStream.sawWarnings() && this.failOnWarning) {
                                throw new BuildException("Javadoc issued warnings.", getLocation());
                            }
                            postProcessGeneratedJavadocs();
                            try {
                                javadocOutputStream.close();
                                javadocOutputStream2.close();
                            } catch (IOException unused) {
                            }
                        } catch (IOException e3) {
                            throw new BuildException("Javadoc failed: " + e3, e3, getLocation());
                        }
                    } finally {
                        if (file != null) {
                            file.delete();
                        }
                        javadocOutputStream.logFlush();
                        javadocOutputStream2.logFlush();
                        try {
                            javadocOutputStream.close();
                            javadocOutputStream2.close();
                        } catch (IOException unused2) {
                        }
                    }
                } catch (IOException e4) {
                    e = e4;
                    file2 = file;
                    file2.delete();
                    throw new BuildException("Error creating temporary file", e, getLocation());
                }
            } catch (Throwable th3) {
                th = th3;
                fileWriter3 = fileWriter;
                FileUtils.close(fileWriter3);
                throw th;
            }
        } catch (IOException e5) {
            e = e5;
            fileWriter = null;
        }
    }

    private void checkTaskName() {
        if ("javadoc2".equals(getTaskType())) {
            log("Warning: the task name <javadoc2> is deprecated. Use <javadoc> instead.", 1);
        }
    }

    private void checkPackageAndSourcePath() {
        if (this.packageList != null && this.sourcePath == null) {
            throw new BuildException("sourcePath attribute must be set when specifying packagelist.");
        }
    }

    private void checkPackages(Vector<String> vector, Path path) {
        if (vector.size() != 0 && path.size() == 0) {
            throw new BuildException("sourcePath attribute must be set when specifying package names.");
        }
    }

    private void checkPackagesToDoc(Vector<String> vector, Vector<SourceFile> vector2) {
        if (this.packageList == null && vector.size() == 0 && vector2.size() == 0) {
            throw new BuildException("No source files and no packages have been specified.");
        }
    }

    private void doSourcePath(Commandline commandline, Path path) {
        if (path.size() > 0) {
            commandline.createArgument().setValue("-sourcepath");
            commandline.createArgument().setPath(path);
        }
    }

    private void generalJavadocArguments(Commandline commandline) {
        if (this.doctitle != null) {
            commandline.createArgument().setValue("-doctitle");
            commandline.createArgument().setValue(expand(this.doctitle.getText()));
        }
        if (this.header != null) {
            commandline.createArgument().setValue("-header");
            commandline.createArgument().setValue(expand(this.header.getText()));
        }
        if (this.footer != null) {
            commandline.createArgument().setValue("-footer");
            commandline.createArgument().setValue(expand(this.footer.getText()));
        }
        if (this.bottom != null) {
            commandline.createArgument().setValue("-bottom");
            commandline.createArgument().setValue(expand(this.bottom.getText()));
        }
        Path path = this.classpath;
        if (path == null) {
            this.classpath = new Path(getProject()).concatSystemClasspath(r.E);
        } else {
            this.classpath = path.concatSystemClasspath(Definer.OnError.POLICY_IGNORE);
        }
        if (this.classpath.size() > 0) {
            commandline.createArgument().setValue("-classpath");
            commandline.createArgument().setPath(this.classpath);
        }
        if (this.version && this.doclet == null) {
            commandline.createArgument().setValue("-version");
        }
        if (this.author && this.doclet == null) {
            commandline.createArgument().setValue("-author");
        }
        if (this.doclet == null && this.destDir == null) {
            throw new BuildException("destdir attribute must be set!");
        }
    }

    private void doDoclet(Commandline commandline) {
        DocletInfo docletInfo = this.doclet;
        if (docletInfo != null) {
            if (docletInfo.getName() == null) {
                throw new BuildException("The doclet name must be specified.", getLocation());
            }
            commandline.createArgument().setValue("-doclet");
            commandline.createArgument().setValue(this.doclet.getName());
            if (this.doclet.getPath() != null) {
                Path pathConcatSystemClasspath = this.doclet.getPath().concatSystemClasspath(Definer.OnError.POLICY_IGNORE);
                if (pathConcatSystemClasspath.size() != 0) {
                    commandline.createArgument().setValue("-docletpath");
                    commandline.createArgument().setPath(pathConcatSystemClasspath);
                }
            }
            Enumeration<DocletParam> params = this.doclet.getParams();
            while (params.hasMoreElements()) {
                DocletParam docletParamNextElement = params.nextElement();
                if (docletParamNextElement.getName() == null) {
                    throw new BuildException("Doclet parameters must have a name");
                }
                commandline.createArgument().setValue(docletParamNextElement.getName());
                if (docletParamNextElement.getValue() != null) {
                    commandline.createArgument().setValue(docletParamNextElement.getValue());
                }
            }
        }
    }

    /* JADX WARN: Not initialized variable reg: 3, insn: 0x0099: MOVE (r0 I:??[OBJECT, ARRAY]) = (r3 I:??[OBJECT, ARRAY]), block:B:33:0x0099 */
    private void writeExternalArgs(Commandline commandline) throws Throwable {
        Writer writer;
        File file = null;
        Writer writer2 = null;
        try {
            try {
                try {
                    File fileCreateTempFile = FILE_UTILS.createTempFile("javadocOptions", "", null, true, true);
                    try {
                        String[] arguments = commandline.getArguments();
                        commandline.clearArgs();
                        commandline.createArgument().setValue("@" + fileCreateTempFile.getAbsolutePath());
                        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileCreateTempFile.getAbsolutePath(), true));
                        for (String str : arguments) {
                            try {
                                if (str.startsWith("-J-")) {
                                    commandline.createArgument().setValue(str);
                                } else if (str.startsWith("-")) {
                                    bufferedWriter.write(str);
                                    bufferedWriter.write(" ");
                                } else {
                                    bufferedWriter.write(quoteString(str));
                                    bufferedWriter.newLine();
                                }
                            } catch (IOException e) {
                                e = e;
                                file = fileCreateTempFile;
                                if (file != null) {
                                    file.delete();
                                }
                                throw new BuildException("Error creating or writing temporary file for javadoc options", e, getLocation());
                            }
                        }
                        bufferedWriter.close();
                        FileUtils.close(bufferedWriter);
                    } catch (IOException e2) {
                        e = e2;
                    }
                } catch (Throwable th) {
                    th = th;
                    FileUtils.close(writer2);
                    throw th;
                }
            } catch (IOException e3) {
                e = e3;
            }
        } catch (Throwable th2) {
            th = th2;
            writer2 = writer;
        }
    }

    private void doBootPath(Commandline commandline) {
        Path path = new Path(getProject());
        Path path2 = this.bootclasspath;
        if (path2 != null) {
            path.append(path2);
        }
        Path pathConcatSystemBootClasspath = path.concatSystemBootClasspath(Definer.OnError.POLICY_IGNORE);
        if (pathConcatSystemBootClasspath.size() > 0) {
            commandline.createArgument().setValue("-bootclasspath");
            commandline.createArgument().setPath(pathConcatSystemBootClasspath);
        }
    }

    private void doLinks(Commandline commandline) {
        if (this.links.size() != 0) {
            Enumeration<LinkArgument> enumerationElements = this.links.elements();
            while (enumerationElements.hasMoreElements()) {
                LinkArgument linkArgumentNextElement = enumerationElements.nextElement();
                if (linkArgumentNextElement.getHref() == null || linkArgumentNextElement.getHref().length() == 0) {
                    log("No href was given for the link - skipping", 3);
                } else {
                    String href = null;
                    if (linkArgumentNextElement.shouldResolveLink()) {
                        File fileResolveFile = getProject().resolveFile(linkArgumentNextElement.getHref());
                        if (fileResolveFile.exists()) {
                            try {
                                href = FILE_UTILS.getFileURL(fileResolveFile).toExternalForm();
                            } catch (MalformedURLException unused) {
                                log("Warning: link location was invalid " + fileResolveFile, 1);
                            }
                        }
                    }
                    if (href == null) {
                        try {
                            new URL(new URL("file://."), linkArgumentNextElement.getHref());
                            href = linkArgumentNextElement.getHref();
                        } catch (MalformedURLException unused2) {
                            log("Link href \"" + linkArgumentNextElement.getHref() + "\" is not a valid url - skipping link", 1);
                        }
                    }
                    if (linkArgumentNextElement.isLinkOffline()) {
                        File packagelistLoc = linkArgumentNextElement.getPackagelistLoc();
                        URL packagelistURL = linkArgumentNextElement.getPackagelistURL();
                        if (packagelistLoc == null && packagelistURL == null) {
                            throw new BuildException("The package list location for link " + linkArgumentNextElement.getHref() + " must be provided because the link is offline");
                        }
                        if (packagelistLoc != null) {
                            if (new File(packagelistLoc, "package-list").exists()) {
                                try {
                                    packagelistURL = FILE_UTILS.getFileURL(packagelistLoc);
                                } catch (MalformedURLException unused3) {
                                    log("Warning: Package list location was invalid " + packagelistLoc, 1);
                                }
                            } else {
                                log("Warning: No package list was found at " + packagelistLoc, 3);
                            }
                        }
                        if (packagelistURL != null) {
                            commandline.createArgument().setValue("-linkoffline");
                            commandline.createArgument().setValue(href);
                            commandline.createArgument().setValue(packagelistURL.toExternalForm());
                        }
                    } else {
                        commandline.createArgument().setValue("-link");
                        commandline.createArgument().setValue(href);
                    }
                }
            }
        }
    }

    private void doGroup(Commandline commandline) {
        if (this.group != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(this.group, ",", false);
            while (stringTokenizer.hasMoreTokens()) {
                String strTrim = stringTokenizer.nextToken().trim();
                int iIndexOf = strTrim.indexOf(" ");
                if (iIndexOf > 0) {
                    String strSubstring = strTrim.substring(0, iIndexOf);
                    String strSubstring2 = strTrim.substring(iIndexOf + 1);
                    commandline.createArgument().setValue("-group");
                    commandline.createArgument().setValue(strSubstring);
                    commandline.createArgument().setValue(strSubstring2);
                }
            }
        }
    }

    private void doGroups(Commandline commandline) {
        if (this.groups.size() != 0) {
            Enumeration<GroupArgument> enumerationElements = this.groups.elements();
            while (enumerationElements.hasMoreElements()) {
                GroupArgument groupArgumentNextElement = enumerationElements.nextElement();
                String title = groupArgumentNextElement.getTitle();
                String packages = groupArgumentNextElement.getPackages();
                if (title == null || packages == null) {
                    throw new BuildException("The title and packages must be specified for group elements.");
                }
                commandline.createArgument().setValue("-group");
                commandline.createArgument().setValue(expand(title));
                commandline.createArgument().setValue(packages);
            }
        }
    }

    private void doJava14(Commandline commandline) {
        Enumeration<Object> enumerationElements = this.tags.elements();
        while (enumerationElements.hasMoreElements()) {
            Object objNextElement = enumerationElements.nextElement();
            if (objNextElement instanceof TagArgument) {
                TagArgument tagArgument = (TagArgument) objNextElement;
                File dir = tagArgument.getDir(getProject());
                if (dir == null) {
                    commandline.createArgument().setValue("-tag");
                    commandline.createArgument().setValue(tagArgument.getParameter());
                } else {
                    for (String str : tagArgument.getDirectoryScanner(getProject()).getIncludedFiles()) {
                        File file = new File(dir, str);
                        try {
                            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                            while (true) {
                                String line = bufferedReader.readLine();
                                if (line == null) {
                                    break;
                                }
                                commandline.createArgument().setValue("-tag");
                                commandline.createArgument().setValue(line);
                            }
                            bufferedReader.close();
                        } catch (IOException e) {
                            throw new BuildException("Couldn't read  tag file from " + file.getAbsolutePath(), e);
                        }
                    }
                }
            } else {
                ExtensionInfo extensionInfo = (ExtensionInfo) objNextElement;
                commandline.createArgument().setValue("-taglet");
                commandline.createArgument().setValue(extensionInfo.getName());
                if (extensionInfo.getPath() != null) {
                    Path pathConcatSystemClasspath = extensionInfo.getPath().concatSystemClasspath(Definer.OnError.POLICY_IGNORE);
                    if (pathConcatSystemClasspath.size() != 0) {
                        commandline.createArgument().setValue("-tagletpath");
                        commandline.createArgument().setPath(pathConcatSystemClasspath);
                    }
                }
            }
        }
        String property = this.source;
        if (property == null) {
            property = getProject().getProperty(MagicNames.BUILD_JAVAC_SOURCE);
        }
        if (property != null) {
            commandline.createArgument().setValue("-source");
            commandline.createArgument().setValue(property);
        }
        if (this.linksource && this.doclet == null) {
            commandline.createArgument().setValue("-linksource");
        }
        if (this.noqualifier == null || this.doclet != null) {
            return;
        }
        commandline.createArgument().setValue("-noqualifier");
        commandline.createArgument().setValue(this.noqualifier);
    }

    private void doDocFilesSubDirs(Commandline commandline) {
        if (this.docFilesSubDirs) {
            commandline.createArgument().setValue("-docfilessubdirs");
            String str = this.excludeDocFilesSubDir;
            if (str == null || str.trim().length() <= 0) {
                return;
            }
            commandline.createArgument().setValue("-excludedocfilessubdir");
            commandline.createArgument().setValue(this.excludeDocFilesSubDir);
        }
    }

    private void doSourceAndPackageNames(Commandline commandline, Vector<String> vector, Vector<SourceFile> vector2, boolean z, File file, BufferedWriter bufferedWriter) throws IOException {
        for (String str : vector) {
            if (z) {
                bufferedWriter.write(str);
                bufferedWriter.newLine();
            } else {
                commandline.createArgument().setValue(str);
            }
        }
        Iterator<SourceFile> it = vector2.iterator();
        while (it.hasNext()) {
            String absolutePath = it.next().getFile().getAbsolutePath();
            if (z) {
                if (absolutePath.indexOf(" ") > -1) {
                    if (File.separatorChar == '\\') {
                        absolutePath = absolutePath.replace(File.separatorChar, '/');
                    }
                    bufferedWriter.write("\"" + absolutePath + "\"");
                } else {
                    bufferedWriter.write(absolutePath);
                }
                bufferedWriter.newLine();
            } else {
                commandline.createArgument().setValue(absolutePath);
            }
        }
    }

    private String quoteString(String str) {
        if (!containsWhitespace(str) && str.indexOf(39) == -1 && str.indexOf(34) == -1) {
            return str;
        }
        if (str.indexOf(39) == -1) {
            return quoteString(str, DateFormat.QUOTE);
        }
        return quoteString(str, '\"');
    }

    private boolean containsWhitespace(String str) {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private String quoteString(String str, char c) {
        StringBuffer stringBuffer = new StringBuffer(str.length() * 2);
        stringBuffer.append(c);
        int length = str.length();
        boolean z = false;
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            if (cCharAt == c) {
                stringBuffer.append('\\').append(cCharAt);
            } else if (cCharAt != '\n') {
                if (cCharAt == '\r') {
                    stringBuffer.append("\\\r");
                    z = true;
                } else if (cCharAt == '\\') {
                    stringBuffer.append("\\\\");
                } else {
                    stringBuffer.append(cCharAt);
                }
            } else if (!z) {
                stringBuffer.append("\\\n");
            } else {
                stringBuffer.append("\n");
            }
            z = false;
        }
        stringBuffer.append(c);
        return stringBuffer.toString();
    }

    private void addSourceFiles(Vector<SourceFile> vector) {
        Iterator it = this.nestedSourceFiles.iterator();
        while (it.hasNext()) {
            ResourceCollection resourceCollection = (ResourceCollection) it.next();
            if (!resourceCollection.isFilesystemOnly()) {
                throw new BuildException("only file system based resources are supported by javadoc");
            }
            boolean z = resourceCollection instanceof FileSet;
            Iterable iterable = resourceCollection;
            if (z) {
                FileSet fileSet = (FileSet) resourceCollection;
                iterable = resourceCollection;
                if (!fileSet.hasPatterns()) {
                    iterable = resourceCollection;
                    if (!fileSet.hasSelectors()) {
                        FileSet fileSet2 = (FileSet) fileSet.clone();
                        fileSet2.createInclude().setName("**/*.java");
                        iterable = fileSet2;
                        if (this.includeNoSourcePackages) {
                            fileSet2.createInclude().setName("**/package.html");
                            iterable = fileSet2;
                        }
                    }
                }
            }
            Iterator<Resource> it2 = iterable.iterator();
            while (it2.hasNext()) {
                vector.addElement(new SourceFile(((FileProvider) it2.next().as(FileProvider.class)).getFile()));
            }
        }
    }

    private void parsePackages(Vector<String> vector, Path path) throws Throwable {
        HashSet hashSet = new HashSet();
        Vector vector2 = (Vector) this.packageSets.clone();
        if (this.sourcePath != null) {
            PatternSet patternSet = new PatternSet();
            patternSet.setProject(getProject());
            if (this.packageNames.size() > 0) {
                Enumeration<PackageName> enumerationElements = this.packageNames.elements();
                while (enumerationElements.hasMoreElements()) {
                    String strReplace = enumerationElements.nextElement().getName().replace('.', '/');
                    if (strReplace.endsWith("*")) {
                        strReplace = strReplace + "*";
                    }
                    patternSet.createInclude().setName(strReplace);
                }
            } else {
                patternSet.createInclude().setName(SelectorUtils.DEEP_TREE_MATCH);
            }
            Enumeration<PackageName> enumerationElements2 = this.excludePackageNames.elements();
            while (enumerationElements2.hasMoreElements()) {
                String strReplace2 = enumerationElements2.nextElement().getName().replace('.', '/');
                if (strReplace2.endsWith("*")) {
                    strReplace2 = strReplace2 + "*";
                }
                patternSet.createExclude().setName(strReplace2);
            }
            String[] list = this.sourcePath.list();
            for (int i = 0; i < list.length; i++) {
                File file = new File(list[i]);
                if (file.isDirectory()) {
                    DirSet dirSet = new DirSet();
                    dirSet.setProject(getProject());
                    dirSet.setDefaultexcludes(this.useDefaultExcludes);
                    dirSet.setDir(file);
                    dirSet.createPatternSet().addConfiguredPatternset(patternSet);
                    vector2.addElement(dirSet);
                } else {
                    log("Skipping " + list[i] + " since it is no directory.", 1);
                }
            }
        }
        Enumeration enumerationElements3 = vector2.elements();
        while (enumerationElements3.hasMoreElements()) {
            DirSet dirSet2 = (DirSet) enumerationElements3.nextElement();
            File dir = dirSet2.getDir(getProject());
            log("scanning " + dir + " for packages.", 4);
            String[] includedDirectories = dirSet2.getDirectoryScanner(getProject()).getIncludedDirectories();
            boolean z = false;
            for (int i2 = 0; i2 < includedDirectories.length; i2++) {
                if (new File(dir, includedDirectories[i2]).list(new FilenameFilter() { // from class: org.apache.tools.ant.taskdefs.Javadoc.1
                    @Override // java.io.FilenameFilter
                    public boolean accept(File file2, String str) {
                        return str.endsWith(".java") || (Javadoc.this.includeNoSourcePackages && str.equals("package.html"));
                    }
                }).length > 0) {
                    if ("".equals(includedDirectories[i2])) {
                        log(dir + " contains source files in the default package, you must specify them as source files not packages.", 1);
                    } else {
                        String strReplace3 = includedDirectories[i2].replace(File.separatorChar, '.');
                        if (!hashSet.contains(strReplace3)) {
                            hashSet.add(strReplace3);
                            vector.addElement(strReplace3);
                        }
                        z = true;
                    }
                }
            }
            if (z) {
                path.createPathElement().setLocation(dir);
            } else {
                log(dir + " doesn't contain any packages, dropping it.", 3);
            }
        }
    }

    private void postProcessGeneratedJavadocs() throws IOException {
        if (this.postProcessGeneratedJavadocs) {
            File file = this.destDir;
            if (file != null && !file.isDirectory()) {
                log("No javadoc created, no need to post-process anything", 3);
                return;
            }
            InputStream resourceAsStream = Javadoc.class.getResourceAsStream("javadoc-frame-injections-fix.txt");
            if (resourceAsStream == null) {
                throw new FileNotFoundException("Missing resource 'javadoc-frame-injections-fix.txt' in classpath.");
            }
            try {
                String strTrim = fixLineFeeds(FileUtils.readFully(new InputStreamReader(resourceAsStream, MediaPlayer.CHARSET_US_ASCII))).trim();
                FileUtils.close(resourceAsStream);
                DirectoryScanner directoryScanner = new DirectoryScanner();
                directoryScanner.setBasedir(this.destDir);
                directoryScanner.setCaseSensitive(false);
                directoryScanner.setIncludes(new String[]{"**/index.html", "**/index.htm", "**/toc.html", "**/toc.htm"});
                directoryScanner.addDefaultExcludes();
                directoryScanner.scan();
                int iPostProcess = 0;
                for (String str : directoryScanner.getIncludedFiles()) {
                    iPostProcess += postProcess(new File(this.destDir, str), strTrim);
                }
                if (iPostProcess > 0) {
                    log("Patched " + iPostProcess + " link injection vulnerable javadocs", 2);
                }
            } catch (Throwable th) {
                FileUtils.close(resourceAsStream);
                throw th;
            }
        }
    }

    private int postProcess(File file, String str) throws IOException {
        String defaultEncoding = this.docEncoding;
        if (defaultEncoding == null) {
            defaultEncoding = FILE_UTILS.getDefaultEncoding();
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            String strFixLineFeeds = fixLineFeeds(FileUtils.safeReadFully(new InputStreamReader(fileInputStream, defaultEncoding)));
            FileUtils.close(fileInputStream);
            if (strFixLineFeeds.indexOf("function validURL(url) {") >= 0) {
                return 0;
            }
            String strPatchContent = patchContent(strFixLineFeeds, str);
            if (strPatchContent.equals(strFixLineFeeds)) {
                return 0;
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, defaultEncoding);
                outputStreamWriter.write(strPatchContent);
                outputStreamWriter.close();
                return 1;
            } finally {
                FileUtils.close(fileOutputStream);
            }
        } catch (Throwable th) {
            FileUtils.close(fileInputStream);
            throw th;
        }
    }

    private String fixLineFeeds(String str) {
        return str.replace("\r\n", "\n").replace("\n", StringUtils.LINE_SEP);
    }

    private String patchContent(String str, String str2) {
        int iIndexOf = str.indexOf(LOAD_FRAME);
        return iIndexOf >= 0 ? str.substring(0, iIndexOf) + str2 + str.substring(iIndexOf + LOAD_FRAME_LEN) : str;
    }

    private class JavadocOutputStream extends LogOutputStream {
        private String queuedLine;
        private boolean sawWarnings;

        JavadocOutputStream(int i) {
            super((Task) Javadoc.this, i);
            this.queuedLine = null;
            this.sawWarnings = false;
        }

        @Override // org.apache.tools.ant.taskdefs.LogOutputStream
        protected void processLine(String str, int i) {
            if (str.contains("warning")) {
                this.sawWarnings = true;
            }
            if (i == 2 && str.startsWith("Generating ")) {
                String str2 = this.queuedLine;
                if (str2 != null) {
                    super.processLine(str2, 3);
                }
                this.queuedLine = str;
                return;
            }
            if (this.queuedLine != null) {
                if (str.startsWith("Building ")) {
                    super.processLine(this.queuedLine, 3);
                } else {
                    super.processLine(this.queuedLine, 2);
                }
                this.queuedLine = null;
            }
            super.processLine(str, i);
        }

        protected void logFlush() {
            String str = this.queuedLine;
            if (str != null) {
                super.processLine(str, 3);
                this.queuedLine = null;
            }
        }

        public boolean sawWarnings() {
            return this.sawWarnings;
        }
    }

    protected String expand(String str) {
        return getProject().replaceProperties(str);
    }
}
