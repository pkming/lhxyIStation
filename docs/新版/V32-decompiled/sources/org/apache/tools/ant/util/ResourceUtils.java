package org.apache.tools.ant.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.ResourceFactory;
import org.apache.tools.ant.types.TimeComparison;
import org.apache.tools.ant.types.resources.Appendable;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.Restrict;
import org.apache.tools.ant.types.resources.StringResource;
import org.apache.tools.ant.types.resources.Touchable;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.types.resources.selectors.Date;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.types.selectors.SelectorUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ResourceUtils {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    public static final String ISO_8859_1 = "ISO-8859-1";
    private static final long MAX_IO_CHUNK_SIZE = 16777216;

    public interface ResourceSelectorProvider {
        ResourceSelector getTargetSelectorForSource(Resource resource);
    }

    public static Resource[] selectOutOfDateSources(ProjectComponent projectComponent, Resource[] resourceArr, FileNameMapper fileNameMapper, ResourceFactory resourceFactory) {
        return selectOutOfDateSources(projectComponent, resourceArr, fileNameMapper, resourceFactory, FILE_UTILS.getFileTimestampGranularity());
    }

    public static Resource[] selectOutOfDateSources(ProjectComponent projectComponent, Resource[] resourceArr, FileNameMapper fileNameMapper, ResourceFactory resourceFactory, long j) {
        Union union = new Union();
        union.addAll(Arrays.asList(resourceArr));
        ResourceCollection resourceCollectionSelectOutOfDateSources = selectOutOfDateSources(projectComponent, union, fileNameMapper, resourceFactory, j);
        return resourceCollectionSelectOutOfDateSources.size() == 0 ? new Resource[0] : ((Union) resourceCollectionSelectOutOfDateSources).listResources();
    }

    public static ResourceCollection selectOutOfDateSources(ProjectComponent projectComponent, ResourceCollection resourceCollection, FileNameMapper fileNameMapper, ResourceFactory resourceFactory, final long j) {
        logFuture(projectComponent, resourceCollection, j);
        return selectSources(projectComponent, resourceCollection, fileNameMapper, resourceFactory, new ResourceSelectorProvider() { // from class: org.apache.tools.ant.util.ResourceUtils.1
            @Override // org.apache.tools.ant.util.ResourceUtils.ResourceSelectorProvider
            public ResourceSelector getTargetSelectorForSource(final Resource resource) {
                return new ResourceSelector() { // from class: org.apache.tools.ant.util.ResourceUtils.1.1
                    @Override // org.apache.tools.ant.types.resources.selectors.ResourceSelector
                    public boolean isSelected(Resource resource2) {
                        return SelectorUtils.isOutOfDate(resource, resource2, j);
                    }
                };
            }
        });
    }

    public static ResourceCollection selectSources(ProjectComponent projectComponent, ResourceCollection resourceCollection, FileNameMapper fileNameMapper, ResourceFactory resourceFactory, ResourceSelectorProvider resourceSelectorProvider) {
        if (resourceCollection.size() == 0) {
            projectComponent.log("No sources found.", 3);
            return Resources.NONE;
        }
        Union union = Union.getInstance(resourceCollection);
        Union union2 = new Union();
        for (Resource resource : union) {
            String name = resource.getName();
            if (name != null) {
                name = name.replace('/', File.separatorChar);
            }
            String[] strArrMapFileName = null;
            try {
                strArrMapFileName = fileNameMapper.mapFileName(name);
            } catch (Exception e) {
                projectComponent.log("Caught " + e + " mapping resource " + resource, 3);
            }
            if (strArrMapFileName == null || strArrMapFileName.length == 0) {
                projectComponent.log(resource + " skipped - don't know how to handle it", 3);
            } else {
                for (int i = 0; i < strArrMapFileName.length; i++) {
                    if (strArrMapFileName[i] == null) {
                        strArrMapFileName[i] = "(no name)";
                    }
                }
                Union union3 = new Union();
                for (String str : strArrMapFileName) {
                    union3.add(resourceFactory.getResource(str.replace(File.separatorChar, '/')));
                }
                Restrict restrict = new Restrict();
                restrict.add(resourceSelectorProvider.getTargetSelectorForSource(resource));
                restrict.add(union3);
                if (restrict.size() > 0) {
                    union2.add(resource);
                    Resource next = restrict.iterator().next();
                    projectComponent.log(resource.getName() + " added as " + next.getName() + (next.isExists() ? " is outdated." : " doesn't exist."), 3);
                } else {
                    projectComponent.log(resource.getName() + " omitted as " + union3.toString() + (union3.size() == 1 ? " is" : " are ") + " up to date.", 3);
                }
            }
        }
        return union2;
    }

    public static void copyResource(Resource resource, Resource resource2) throws IOException {
        copyResource(resource, resource2, null);
    }

    public static void copyResource(Resource resource, Resource resource2, Project project) throws IOException {
        copyResource(resource, resource2, null, null, false, false, null, null, project);
    }

    public static void copyResource(Resource resource, Resource resource2, FilterSetCollection filterSetCollection, Vector vector, boolean z, boolean z2, String str, String str2, Project project) throws IOException {
        copyResource(resource, resource2, filterSetCollection, vector, z, z2, false, str, str2, project);
    }

    public static void copyResource(Resource resource, Resource resource2, FilterSetCollection filterSetCollection, Vector vector, boolean z, boolean z2, boolean z3, String str, String str2, Project project) throws IOException {
        copyResource(resource, resource2, filterSetCollection, vector, z, z2, z3, str, str2, project, false);
    }

    public static void copyResource(Resource resource, Resource resource2, FilterSetCollection filterSetCollection, Vector vector, boolean z, boolean z2, boolean z3, String str, String str2, Project project, boolean z4) throws IOException {
        Touchable touchable;
        if (z || SelectorUtils.isOutOfDate(resource, resource2, FileUtils.getFileUtils().getFileTimestampGranularity())) {
            boolean z5 = false;
            boolean z6 = filterSetCollection != null && filterSetCollection.hasFilters();
            boolean z7 = vector != null && vector.size() > 0;
            String encoding = resource instanceof StringResource ? ((StringResource) resource).getEncoding() : str;
            File file = resource2.as(FileProvider.class) != null ? ((FileProvider) resource2.as(FileProvider.class)).getFile() : null;
            if (file != null && file.isFile() && !file.canWrite()) {
                if (!z4) {
                    throw new ReadOnlyTargetFileException(file);
                }
                if (!FILE_UTILS.tryHardToDelete(file)) {
                    throw new IOException("failed to delete read-only destination file " + file);
                }
            }
            if (z6) {
                copyWithFilterSets(resource, resource2, filterSetCollection, vector, z7, z3, encoding, str2, project);
            } else if (z7 || ((encoding != null && !encoding.equals(str2)) || (encoding == null && str2 != null))) {
                copyWithFilterChainsOrTranscoding(resource, resource2, vector, z7, z3, encoding, str2, project);
            } else {
                if (resource.as(FileProvider.class) != null && file != null && !z3) {
                    File file2 = ((FileProvider) resource.as(FileProvider.class)).getFile();
                    try {
                        copyUsingFileChannels(file2, file);
                        z5 = true;
                    } catch (IOException e) {
                        project.log("Attempt to copy " + file2 + " to " + file + " using NIO Channels failed due to '" + e.getMessage() + "'.  Falling back to streams.", 1);
                    }
                }
                if (!z5) {
                    copyUsingStreams(resource, resource2, z3, project);
                }
            }
            if (!z2 || (touchable = (Touchable) resource2.as(Touchable.class)) == null) {
                return;
            }
            setLastModified(touchable, resource.getLastModified());
        }
    }

    public static void setLastModified(Touchable touchable, long j) {
        if (j < 0) {
            j = System.currentTimeMillis();
        }
        touchable.touch(j);
    }

    public static boolean contentEquals(Resource resource, Resource resource2, boolean z) throws IOException {
        if (resource.isExists() != resource2.isExists()) {
            return false;
        }
        if (!resource.isExists()) {
            return true;
        }
        if (resource.isDirectory() || resource2.isDirectory()) {
            return false;
        }
        if (resource.equals(resource2)) {
            return true;
        }
        if (!z) {
            long size = resource.getSize();
            long size2 = resource2.getSize();
            if (size != -1 && size2 != -1 && size != size2) {
                return false;
            }
        }
        return compareContent(resource, resource2, z) == 0;
    }

    public static int compareContent(Resource resource, Resource resource2, boolean z) throws IOException {
        if (resource.equals(resource2)) {
            return 0;
        }
        boolean zIsExists = resource.isExists();
        boolean zIsExists2 = resource2.isExists();
        if (!zIsExists && !zIsExists2) {
            return 0;
        }
        if (zIsExists != zIsExists2) {
            return zIsExists ? 1 : -1;
        }
        boolean zIsDirectory = resource.isDirectory();
        boolean zIsDirectory2 = resource2.isDirectory();
        if (zIsDirectory && zIsDirectory2) {
            return 0;
        }
        return (zIsDirectory || zIsDirectory2) ? zIsDirectory ? -1 : 1 : z ? textCompare(resource, resource2) : binaryCompare(resource, resource2);
    }

    public static FileResource asFileResource(FileProvider fileProvider) {
        if ((fileProvider instanceof FileResource) || fileProvider == null) {
            return (FileResource) fileProvider;
        }
        FileResource fileResource = new FileResource(fileProvider.getFile());
        fileResource.setProject(Project.getProject(fileProvider));
        return fileResource;
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x002e, code lost:
    
        if (r3.read() != (-1)) goto L11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0030, code lost:
    
        r0 = 0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static int binaryCompare(org.apache.tools.ant.types.Resource r3, org.apache.tools.ant.types.Resource r4) throws java.lang.Throwable {
        /*
            r0 = 0
            java.io.BufferedInputStream r1 = new java.io.BufferedInputStream     // Catch: java.lang.Throwable -> L38
            java.io.InputStream r3 = r3.getInputStream()     // Catch: java.lang.Throwable -> L38
            r1.<init>(r3)     // Catch: java.lang.Throwable -> L38
            java.io.BufferedInputStream r3 = new java.io.BufferedInputStream     // Catch: java.lang.Throwable -> L34
            java.io.InputStream r4 = r4.getInputStream()     // Catch: java.lang.Throwable -> L34
            r3.<init>(r4)     // Catch: java.lang.Throwable -> L34
        L13:
            int r4 = r1.read()     // Catch: java.lang.Throwable -> L32
            r0 = -1
            if (r4 == r0) goto L2a
            int r2 = r3.read()     // Catch: java.lang.Throwable -> L32
            if (r4 == r2) goto L13
            if (r4 <= r2) goto L23
            r0 = 1
        L23:
            org.apache.tools.ant.util.FileUtils.close(r1)
            org.apache.tools.ant.util.FileUtils.close(r3)
            return r0
        L2a:
            int r4 = r3.read()     // Catch: java.lang.Throwable -> L32
            if (r4 != r0) goto L23
            r0 = 0
            goto L23
        L32:
            r4 = move-exception
            goto L36
        L34:
            r4 = move-exception
            r3 = r0
        L36:
            r0 = r1
            goto L3a
        L38:
            r4 = move-exception
            r3 = r0
        L3a:
            org.apache.tools.ant.util.FileUtils.close(r0)
            org.apache.tools.ant.util.FileUtils.close(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.util.ResourceUtils.binaryCompare(org.apache.tools.ant.types.Resource, org.apache.tools.ant.types.Resource):int");
    }

    private static int textCompare(Resource resource, Resource resource2) throws Throwable {
        BufferedReader bufferedReader;
        int iCompareTo;
        BufferedReader bufferedReader2 = null;
        try {
            BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(resource2.getInputStream()));
                try {
                    String line = bufferedReader3.readLine();
                    while (true) {
                        if (line != null) {
                            String line2 = bufferedReader.readLine();
                            if (!line.equals(line2)) {
                                iCompareTo = line2 == null ? 1 : line.compareTo(line2);
                            } else {
                                line = bufferedReader3.readLine();
                            }
                        } else {
                            iCompareTo = bufferedReader.readLine() == null ? 0 : -1;
                        }
                    }
                    FileUtils.close(bufferedReader3);
                    FileUtils.close(bufferedReader);
                    return iCompareTo;
                } catch (Throwable th) {
                    th = th;
                    bufferedReader2 = bufferedReader3;
                    FileUtils.close(bufferedReader2);
                    FileUtils.close(bufferedReader);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                bufferedReader = null;
            }
        } catch (Throwable th3) {
            th = th3;
            bufferedReader = null;
        }
    }

    private static void logFuture(ProjectComponent projectComponent, ResourceCollection resourceCollection, long j) {
        long jCurrentTimeMillis = System.currentTimeMillis() + j;
        Date date = new Date();
        date.setMillis(jCurrentTimeMillis);
        date.setWhen(TimeComparison.AFTER);
        Restrict restrict = new Restrict();
        restrict.add(date);
        restrict.add(resourceCollection);
        Iterator<Resource> it = restrict.iterator();
        while (it.hasNext()) {
            projectComponent.log("Warning: " + it.next().getName() + " modified in the future.", 1);
        }
    }

    private static void copyWithFilterSets(Resource resource, Resource resource2, FilterSetCollection filterSetCollection, Vector vector, boolean z, boolean z2, String str, String str2, Project project) throws Throwable {
        BufferedReader bufferedReader;
        InputStreamReader inputStreamReader;
        OutputStreamWriter outputStreamWriter;
        BufferedReader bufferedReader2;
        BufferedWriter bufferedWriter = null;
        try {
            if (str == null) {
                inputStreamReader = new InputStreamReader(resource.getInputStream());
            } else {
                inputStreamReader = new InputStreamReader(resource.getInputStream(), str);
            }
            BufferedReader bufferedReader3 = new BufferedReader(inputStreamReader);
            try {
                OutputStream outputStream = getOutputStream(resource2, z2, project);
                if (str2 == null) {
                    outputStreamWriter = new OutputStreamWriter(outputStream);
                } else {
                    outputStreamWriter = new OutputStreamWriter(outputStream, str2);
                }
                BufferedWriter bufferedWriter2 = new BufferedWriter(outputStreamWriter);
                if (z) {
                    try {
                        ChainReaderHelper chainReaderHelper = new ChainReaderHelper();
                        chainReaderHelper.setBufferSize(8192);
                        chainReaderHelper.setPrimaryReader(bufferedReader3);
                        chainReaderHelper.setFilterChains(vector);
                        chainReaderHelper.setProject(project);
                        bufferedReader2 = new BufferedReader(chainReaderHelper.getAssembledReader());
                    } catch (Throwable th) {
                        bufferedWriter = bufferedWriter2;
                        bufferedReader = bufferedReader3;
                        th = th;
                        FileUtils.close(bufferedWriter);
                        FileUtils.close(bufferedReader);
                        throw th;
                    }
                } else {
                    bufferedReader2 = bufferedReader3;
                }
                try {
                    LineTokenizer lineTokenizer = new LineTokenizer();
                    lineTokenizer.setIncludeDelims(true);
                    for (String token = lineTokenizer.getToken(bufferedReader2); token != null; token = lineTokenizer.getToken(bufferedReader2)) {
                        if (token.length() == 0) {
                            bufferedWriter2.newLine();
                        } else {
                            bufferedWriter2.write(filterSetCollection.replaceTokens(token));
                        }
                    }
                    FileUtils.close(bufferedWriter2);
                    FileUtils.close(bufferedReader2);
                } catch (Throwable th2) {
                    th = th2;
                    BufferedReader bufferedReader4 = bufferedReader2;
                    bufferedWriter = bufferedWriter2;
                    bufferedReader = bufferedReader4;
                    FileUtils.close(bufferedWriter);
                    FileUtils.close(bufferedReader);
                    throw th;
                }
            } catch (Throwable th3) {
                bufferedReader = bufferedReader3;
                th = th3;
            }
        } catch (Throwable th4) {
            th = th4;
            bufferedReader = null;
        }
    }

    private static void copyWithFilterChainsOrTranscoding(Resource resource, Resource resource2, Vector vector, boolean z, boolean z2, String str, String str2, Project project) throws Throwable {
        BufferedReader bufferedReader;
        InputStreamReader inputStreamReader;
        OutputStreamWriter outputStreamWriter;
        BufferedWriter bufferedWriter;
        BufferedReader bufferedReader2;
        BufferedWriter bufferedWriter2 = null;
        try {
            if (str == null) {
                inputStreamReader = new InputStreamReader(resource.getInputStream());
            } else {
                inputStreamReader = new InputStreamReader(resource.getInputStream(), str);
            }
            BufferedReader bufferedReader3 = new BufferedReader(inputStreamReader);
            try {
                OutputStream outputStream = getOutputStream(resource2, z2, project);
                if (str2 == null) {
                    outputStreamWriter = new OutputStreamWriter(outputStream);
                } else {
                    outputStreamWriter = new OutputStreamWriter(outputStream, str2);
                }
                bufferedWriter = new BufferedWriter(outputStreamWriter);
                if (z) {
                    try {
                        ChainReaderHelper chainReaderHelper = new ChainReaderHelper();
                        chainReaderHelper.setBufferSize(8192);
                        chainReaderHelper.setPrimaryReader(bufferedReader3);
                        chainReaderHelper.setFilterChains(vector);
                        chainReaderHelper.setProject(project);
                        bufferedReader2 = new BufferedReader(chainReaderHelper.getAssembledReader());
                    } catch (Throwable th) {
                        bufferedWriter2 = bufferedWriter;
                        bufferedReader = bufferedReader3;
                        th = th;
                        FileUtils.close(bufferedWriter2);
                        FileUtils.close(bufferedReader);
                        throw th;
                    }
                } else {
                    bufferedReader2 = bufferedReader3;
                }
            } catch (Throwable th2) {
                bufferedReader = bufferedReader3;
                th = th2;
            }
        } catch (Throwable th3) {
            th = th3;
            bufferedReader = null;
        }
        try {
            char[] cArr = new char[8192];
            while (true) {
                int i = bufferedReader2.read(cArr, 0, 8192);
                if (i != -1) {
                    bufferedWriter.write(cArr, 0, i);
                } else {
                    FileUtils.close(bufferedWriter);
                    FileUtils.close(bufferedReader2);
                    return;
                }
            }
        } catch (Throwable th4) {
            th = th4;
            BufferedReader bufferedReader4 = bufferedReader2;
            bufferedWriter2 = bufferedWriter;
            bufferedReader = bufferedReader4;
            FileUtils.close(bufferedWriter2);
            FileUtils.close(bufferedReader);
            throw th;
        }
    }

    private static void copyUsingFileChannels(File file, File file2) throws Throwable {
        FileChannel fileChannel;
        FileOutputStream fileOutputStream;
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream2;
        FileChannel channel;
        File parentFile = file2.getParentFile();
        if (parentFile != null && !parentFile.isDirectory() && !parentFile.mkdirs() && !parentFile.isDirectory()) {
            throw new IOException("failed to create the parent directory for " + file2);
        }
        FileChannel channel2 = null;
        try {
            fileInputStream = new FileInputStream(file);
            try {
                fileOutputStream2 = new FileOutputStream(file2);
                try {
                    channel = fileInputStream.getChannel();
                } catch (Throwable th) {
                    th = th;
                    fileOutputStream = fileOutputStream2;
                    fileChannel = null;
                }
            } catch (Throwable th2) {
                th = th2;
                fileChannel = null;
                fileOutputStream = null;
            }
        } catch (Throwable th3) {
            th = th3;
            fileChannel = null;
            fileOutputStream = null;
            fileInputStream = null;
        }
        try {
            channel2 = fileOutputStream2.getChannel();
            long size = channel.size();
            for (long jTransferFrom = 0; jTransferFrom < size; jTransferFrom += channel2.transferFrom(channel, jTransferFrom, Math.min(MAX_IO_CHUNK_SIZE, size - jTransferFrom))) {
            }
            FileUtils.close(channel);
            FileUtils.close(channel2);
            FileUtils.close(fileOutputStream2);
            FileUtils.close(fileInputStream);
        } catch (Throwable th4) {
            th = th4;
            fileOutputStream = fileOutputStream2;
            fileChannel = channel2;
            channel2 = channel;
            FileUtils.close(channel2);
            FileUtils.close(fileChannel);
            FileUtils.close(fileOutputStream);
            FileUtils.close(fileInputStream);
            throw th;
        }
    }

    private static void copyUsingStreams(Resource resource, Resource resource2, boolean z, Project project) throws Throwable {
        InputStream inputStream;
        OutputStream outputStream = null;
        try {
            inputStream = resource.getInputStream();
            try {
                outputStream = getOutputStream(resource2, z, project);
                byte[] bArr = new byte[8192];
                int i = 0;
                do {
                    outputStream.write(bArr, 0, i);
                    i = inputStream.read(bArr, 0, 8192);
                } while (i != -1);
                FileUtils.close(outputStream);
                FileUtils.close(inputStream);
            } catch (Throwable th) {
                th = th;
                FileUtils.close(outputStream);
                FileUtils.close(inputStream);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            inputStream = null;
        }
    }

    private static OutputStream getOutputStream(Resource resource, boolean z, Project project) throws IOException {
        if (z) {
            Appendable appendable = (Appendable) resource.as(Appendable.class);
            if (appendable != null) {
                return appendable.getAppendOutputStream();
            }
            project.log("Appendable OutputStream not available for non-appendable resource " + resource + "; using plain OutputStream", 3);
        }
        return resource.getOutputStream();
    }

    public static class ReadOnlyTargetFileException extends IOException {
        public ReadOnlyTargetFileException(File file) {
            super("can't write to read-only destination file " + file);
        }
    }
}
