package org.apache.tools.ant.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PathTokenizer;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.launch.Locator;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.types.resources.FileResource;

/* JADX INFO: loaded from: classes3.dex */
public class FileUtils {
    static final int BUF_SIZE = 8192;
    private static final int DELETE_RETRY_SLEEP_MILLIS = 10;
    private static final int EXPAND_SPACE = 50;
    public static final long FAT_FILE_TIMESTAMP_GRANULARITY = 2000;
    public static final long NTFS_FILE_TIMESTAMP_GRANULARITY = 1;
    private static final String NULL_PLACEHOLDER = "null";
    public static final long UNIX_FILE_TIMESTAMP_GRANULARITY = 1000;
    private Object cacheFromUriLock = new Object();
    private String cacheFromUriRequest = null;
    private String cacheFromUriResponse = null;
    private static final FileUtils PRIMARY_INSTANCE = new FileUtils();
    private static Random rand = new Random(System.currentTimeMillis() + Runtime.getRuntime().freeMemory());
    private static final boolean ON_NETWARE = Os.isFamily(Os.FAMILY_NETWARE);
    private static final boolean ON_DOS = Os.isFamily(Os.FAMILY_DOS);
    private static final boolean ON_WIN9X = Os.isFamily(Os.FAMILY_9X);
    private static final boolean ON_WINDOWS = Os.isFamily(Os.FAMILY_WINDOWS);

    public boolean isUpToDate(long j, long j2, long j3) {
        return j2 != -1 && j2 >= j + j3;
    }

    public static FileUtils newFileUtils() {
        return new FileUtils();
    }

    public static FileUtils getFileUtils() {
        return PRIMARY_INSTANCE;
    }

    protected FileUtils() {
    }

    public URL getFileURL(File file) throws MalformedURLException {
        return new URL(file.toURI().toASCIIString());
    }

    public void copyFile(String str, String str2) throws IOException {
        copyFile(new File(str), new File(str2), (FilterSetCollection) null, false, false);
    }

    public void copyFile(String str, String str2, FilterSetCollection filterSetCollection) throws IOException {
        copyFile(new File(str), new File(str2), filterSetCollection, false, false);
    }

    public void copyFile(String str, String str2, FilterSetCollection filterSetCollection, boolean z) throws IOException {
        copyFile(new File(str), new File(str2), filterSetCollection, z, false);
    }

    public void copyFile(String str, String str2, FilterSetCollection filterSetCollection, boolean z, boolean z2) throws IOException {
        copyFile(new File(str), new File(str2), filterSetCollection, z, z2);
    }

    public void copyFile(String str, String str2, FilterSetCollection filterSetCollection, boolean z, boolean z2, String str3) throws IOException {
        copyFile(new File(str), new File(str2), filterSetCollection, z, z2, str3);
    }

    public void copyFile(String str, String str2, FilterSetCollection filterSetCollection, Vector vector, boolean z, boolean z2, String str3, Project project) throws IOException {
        copyFile(new File(str), new File(str2), filterSetCollection, vector, z, z2, str3, project);
    }

    public void copyFile(String str, String str2, FilterSetCollection filterSetCollection, Vector vector, boolean z, boolean z2, String str3, String str4, Project project) throws IOException {
        copyFile(new File(str), new File(str2), filterSetCollection, vector, z, z2, str3, str4, project);
    }

    public void copyFile(File file, File file2) throws IOException {
        copyFile(file, file2, (FilterSetCollection) null, false, false);
    }

    public void copyFile(File file, File file2, FilterSetCollection filterSetCollection) throws IOException {
        copyFile(file, file2, filterSetCollection, false, false);
    }

    public void copyFile(File file, File file2, FilterSetCollection filterSetCollection, boolean z) throws IOException {
        copyFile(file, file2, filterSetCollection, z, false);
    }

    public void copyFile(File file, File file2, FilterSetCollection filterSetCollection, boolean z, boolean z2) throws IOException {
        copyFile(file, file2, filterSetCollection, z, z2, (String) null);
    }

    public void copyFile(File file, File file2, FilterSetCollection filterSetCollection, boolean z, boolean z2, String str) throws IOException {
        copyFile(file, file2, filterSetCollection, (Vector) null, z, z2, str, (Project) null);
    }

    public void copyFile(File file, File file2, FilterSetCollection filterSetCollection, Vector vector, boolean z, boolean z2, String str, Project project) throws IOException {
        copyFile(file, file2, filterSetCollection, vector, z, z2, str, str, project);
    }

    public void copyFile(File file, File file2, FilterSetCollection filterSetCollection, Vector vector, boolean z, boolean z2, String str, String str2, Project project) throws IOException {
        copyFile(file, file2, filterSetCollection, vector, z, z2, false, str, str2, project);
    }

    public void copyFile(File file, File file2, FilterSetCollection filterSetCollection, Vector vector, boolean z, boolean z2, boolean z3, String str, String str2, Project project) throws IOException {
        copyFile(file, file2, filterSetCollection, vector, z, z2, z3, str, str2, project, false);
    }

    public void copyFile(File file, File file2, FilterSetCollection filterSetCollection, Vector vector, boolean z, boolean z2, boolean z3, String str, String str2, Project project, boolean z4) throws IOException {
        ResourceUtils.copyResource(new FileResource(file), new FileResource(file2), filterSetCollection, vector, z, z2, z3, str, str2, project, z4);
    }

    public void setFileLastModified(File file, long j) {
        ResourceUtils.setLastModified(new FileResource(file), j);
    }

    public File resolveFile(File file, String str) {
        if (!isAbsolutePath(str)) {
            char c = File.separatorChar;
            String strReplace = str.replace('/', c).replace('\\', c);
            if (isContextRelativePath(strReplace)) {
                file = null;
                String property = System.getProperty("user.dir");
                if (strReplace.charAt(0) == c && property.charAt(0) == c) {
                    strReplace = dissect(property)[0] + strReplace.substring(1);
                }
            }
            str = new File(file, strReplace).getAbsolutePath();
        }
        return normalize(str);
    }

    public static boolean isContextRelativePath(String str) {
        if ((!ON_DOS && !ON_NETWARE) || str.length() == 0) {
            return false;
        }
        char c = File.separatorChar;
        String strReplace = str.replace('/', c).replace('\\', c);
        char cCharAt = strReplace.charAt(0);
        int length = strReplace.length();
        if (cCharAt != c || (length != 1 && strReplace.charAt(1) == c)) {
            if (!Character.isLetter(cCharAt) || length <= 1 || strReplace.charAt(1) != ':') {
                return false;
            }
            if (length != 2 && strReplace.charAt(2) == c) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAbsolutePath(String str) {
        int iIndexOf;
        int length = str.length();
        if (length == 0) {
            return false;
        }
        char c = File.separatorChar;
        String strReplace = str.replace('/', c).replace('\\', c);
        char cCharAt = strReplace.charAt(0);
        boolean z = ON_DOS;
        if (!z && !ON_NETWARE) {
            return cCharAt == c;
        }
        if (cCharAt == c) {
            return z && length > 4 && strReplace.charAt(1) == c && (iIndexOf = strReplace.indexOf(c, 2)) > 2 && iIndexOf + 1 < length;
        }
        int iIndexOf2 = strReplace.indexOf(58);
        return (Character.isLetter(cCharAt) && iIndexOf2 == 1 && strReplace.length() > 2 && strReplace.charAt(2) == c) || (ON_NETWARE && iIndexOf2 > 0);
    }

    public static String translatePath(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer(str.length() + 50);
        PathTokenizer pathTokenizer = new PathTokenizer(str);
        while (pathTokenizer.hasMoreTokens()) {
            String strReplace = pathTokenizer.nextToken().replace('/', File.separatorChar).replace('\\', File.separatorChar);
            if (stringBuffer.length() != 0) {
                stringBuffer.append(File.pathSeparatorChar);
            }
            stringBuffer.append(strReplace);
        }
        return stringBuffer.toString();
    }

    public File normalize(String str) {
        Stack stack = new Stack();
        String[] strArrDissect = dissect(str);
        stack.push(strArrDissect[0]);
        java.util.StringTokenizer stringTokenizer = new java.util.StringTokenizer(strArrDissect[1], File.separator);
        while (stringTokenizer.hasMoreTokens()) {
            String strNextToken = stringTokenizer.nextToken();
            if (!".".equals(strNextToken)) {
                if ("..".equals(strNextToken)) {
                    if (stack.size() < 2) {
                        return new File(str);
                    }
                    stack.pop();
                } else {
                    stack.push(strNextToken);
                }
            }
        }
        StringBuffer stringBuffer = new StringBuffer();
        int size = stack.size();
        for (int i = 0; i < size; i++) {
            if (i > 1) {
                stringBuffer.append(File.separatorChar);
            }
            stringBuffer.append(stack.elementAt(i));
        }
        return new File(stringBuffer.toString());
    }

    public String[] dissect(String str) {
        String strSubstring;
        String strSubstring2;
        char c = File.separatorChar;
        String strReplace = str.replace('/', c).replace('\\', c);
        if (!isAbsolutePath(strReplace)) {
            throw new BuildException(strReplace + " is not an absolute path");
        }
        int iIndexOf = strReplace.indexOf(58);
        if (iIndexOf > 0 && (ON_DOS || ON_NETWARE)) {
            int i = iIndexOf + 1;
            String strSubstring3 = strReplace.substring(0, i);
            char[] charArray = strReplace.toCharArray();
            strSubstring = strSubstring3 + c;
            if (charArray[i] == c) {
                i++;
            }
            StringBuffer stringBuffer = new StringBuffer();
            while (i < charArray.length) {
                if (charArray[i] != c || charArray[i - 1] != c) {
                    stringBuffer.append(charArray[i]);
                }
                i++;
            }
            strSubstring2 = stringBuffer.toString();
        } else if (strReplace.length() > 1 && strReplace.charAt(1) == c) {
            int iIndexOf2 = strReplace.indexOf(c, strReplace.indexOf(c, 2) + 1);
            strSubstring = iIndexOf2 > 2 ? strReplace.substring(0, iIndexOf2 + 1) : strReplace;
            strSubstring2 = strReplace.substring(strSubstring.length());
        } else {
            strSubstring = File.separator;
            strSubstring2 = strReplace.substring(1);
        }
        return new String[]{strSubstring, strSubstring2};
    }

    /* JADX WARN: Multi-variable type inference failed */
    public String toVMSPath(File file) {
        String strSubstring;
        int i;
        String strSubstring2;
        String path = normalize(file.getAbsolutePath()).getPath();
        String name = file.getName();
        Object[] objArr = path.charAt(0) == File.separatorChar;
        Object[] objArr2 = file.isDirectory() && !name.regionMatches(true, name.length() + (-4), ".DIR", 0, 4);
        StringBuffer stringBuffer = null;
        if (objArr == true) {
            int iIndexOf = path.indexOf(File.separatorChar, 1);
            if (iIndexOf == -1) {
                return path.substring(1) + ":[000000]";
            }
            i = iIndexOf + 1;
            strSubstring = path.substring(1, iIndexOf);
        } else {
            strSubstring = null;
            i = 0;
        }
        if (objArr2 != false) {
            stringBuffer = new StringBuffer(path.substring(i).replace(File.separatorChar, '.'));
            strSubstring2 = null;
        } else {
            int iLastIndexOf = path.lastIndexOf(File.separatorChar, path.length());
            if (iLastIndexOf == -1 || iLastIndexOf < i) {
                strSubstring2 = path.substring(i);
            } else {
                StringBuffer stringBuffer2 = new StringBuffer(path.substring(i, iLastIndexOf).replace(File.separatorChar, '.'));
                int i2 = iLastIndexOf + 1;
                strSubstring2 = path.length() > i2 ? path.substring(i2) : null;
                stringBuffer = stringBuffer2;
            }
        }
        if (objArr == false && stringBuffer != null) {
            stringBuffer.insert(0, '.');
        }
        StringBuilder sbAppend = new StringBuilder().append(strSubstring != null ? strSubstring + ":" : "").append(stringBuffer != null ? "[" + ((Object) stringBuffer) + "]" : "");
        if (strSubstring2 == null) {
            strSubstring2 = "";
        }
        return sbAppend.append(strSubstring2).toString();
    }

    public File createTempFile(String str, String str2, File file) {
        return createTempFile(str, str2, file, false, false);
    }

    public File createTempFile(String str, String str2, File file, boolean z, boolean z2) {
        File fileCreateTempFile;
        File file2;
        String property = file == null ? System.getProperty("java.io.tmpdir") : file.getPath();
        if (str == null) {
            str = NULL_PLACEHOLDER;
        }
        if (str2 == null) {
            str2 = NULL_PLACEHOLDER;
        }
        if (z2) {
            try {
                fileCreateTempFile = File.createTempFile(str, str2, new File(property));
            } catch (IOException e) {
                throw new BuildException("Could not create tempfile in " + property, e);
            }
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#####");
            synchronized (rand) {
                do {
                    file2 = new File(property, str + decimalFormat.format(rand.nextInt(Integer.MAX_VALUE)) + str2);
                } while (file2.exists());
            }
            fileCreateTempFile = file2;
        }
        if (z) {
            fileCreateTempFile.deleteOnExit();
        }
        return fileCreateTempFile;
    }

    public File createTempFile(String str, String str2, File file, boolean z) {
        return createTempFile(str, str2, file, z, false);
    }

    public boolean contentEquals(File file, File file2) throws IOException {
        return contentEquals(file, file2, false);
    }

    public boolean contentEquals(File file, File file2, boolean z) throws IOException {
        return ResourceUtils.contentEquals(new FileResource(file), new FileResource(file2), z);
    }

    public File getParentFile(File file) {
        if (file == null) {
            return null;
        }
        return file.getParentFile();
    }

    public static String readFully(Reader reader) throws IOException {
        return readFully(reader, 8192);
    }

    public static String readFully(Reader reader, int i) throws IOException {
        if (i <= 0) {
            throw new IllegalArgumentException("Buffer size must be greater than 0");
        }
        char[] cArr = new char[i];
        StringBuffer stringBuffer = null;
        int i2 = 0;
        while (i2 != -1) {
            i2 = reader.read(cArr);
            if (i2 > 0) {
                if (stringBuffer == null) {
                    stringBuffer = new StringBuffer();
                }
                stringBuffer.append(new String(cArr, 0, i2));
            }
        }
        if (stringBuffer == null) {
            return null;
        }
        return stringBuffer.toString();
    }

    public static String safeReadFully(Reader reader) throws IOException {
        String fully = readFully(reader);
        return fully == null ? "" : fully;
    }

    public boolean createNewFile(File file) throws IOException {
        return file.createNewFile();
    }

    public boolean createNewFile(File file, boolean z) throws IOException {
        File parentFile = file.getParentFile();
        if (z && !parentFile.exists()) {
            parentFile.mkdirs();
        }
        return file.createNewFile();
    }

    public boolean isSymbolicLink(File file, String str) throws IOException {
        SymbolicLinkUtils symbolicLinkUtils = SymbolicLinkUtils.getSymbolicLinkUtils();
        if (file == null) {
            return symbolicLinkUtils.isSymbolicLink(str);
        }
        return symbolicLinkUtils.isSymbolicLink(file, str);
    }

    public String removeLeadingPath(File file, File file2) {
        String absolutePath = normalize(file.getAbsolutePath()).getAbsolutePath();
        String absolutePath2 = normalize(file2.getAbsolutePath()).getAbsolutePath();
        if (absolutePath.equals(absolutePath2)) {
            return "";
        }
        if (!absolutePath.endsWith(File.separator)) {
            absolutePath = absolutePath + File.separator;
        }
        return absolutePath2.startsWith(absolutePath) ? absolutePath2.substring(absolutePath.length()) : absolutePath2;
    }

    public boolean isLeadingPath(File file, File file2) {
        String absolutePath = normalize(file.getAbsolutePath()).getAbsolutePath();
        String absolutePath2 = normalize(file2.getAbsolutePath()).getAbsolutePath();
        if (absolutePath.equals(absolutePath2)) {
            return true;
        }
        if (!absolutePath.endsWith(File.separator)) {
            absolutePath = absolutePath + File.separator;
        }
        return absolutePath2.startsWith(absolutePath);
    }

    public String toURI(String str) {
        return new File(str).toURI().toASCIIString();
    }

    public String fromURI(String str) {
        synchronized (this.cacheFromUriLock) {
            if (str.equals(this.cacheFromUriRequest)) {
                return this.cacheFromUriResponse;
            }
            String strFromURI = Locator.fromURI(str);
            if (isAbsolutePath(strFromURI)) {
                strFromURI = normalize(strFromURI).getAbsolutePath();
            }
            this.cacheFromUriRequest = str;
            this.cacheFromUriResponse = strFromURI;
            return strFromURI;
        }
    }

    public boolean fileNameEquals(File file, File file2) {
        return normalize(file.getAbsolutePath()).getAbsolutePath().equals(normalize(file2.getAbsolutePath()).getAbsolutePath());
    }

    public boolean areSame(File file, File file2) throws IOException {
        if (file == null && file2 == null) {
            return true;
        }
        if (file == null || file2 == null) {
            return false;
        }
        File fileNormalize = normalize(file.getAbsolutePath());
        File fileNormalize2 = normalize(file2.getAbsolutePath());
        return fileNormalize.equals(fileNormalize2) || fileNormalize.getCanonicalFile().equals(fileNormalize2.getCanonicalFile());
    }

    public void rename(File file, File file2) throws IOException {
        File canonicalFile = normalize(file.getAbsolutePath()).getCanonicalFile();
        File fileNormalize = normalize(file2.getAbsolutePath());
        if (!canonicalFile.exists()) {
            System.err.println("Cannot rename nonexistent file " + canonicalFile);
            return;
        }
        if (canonicalFile.getAbsolutePath().equals(fileNormalize.getAbsolutePath())) {
            System.err.println("Rename of " + canonicalFile + " to " + fileNormalize + " is a no-op.");
            return;
        }
        if (fileNormalize.exists() && !areSame(canonicalFile, fileNormalize) && !tryHardToDelete(fileNormalize)) {
            throw new IOException("Failed to delete " + fileNormalize + " while trying to rename " + canonicalFile);
        }
        File parentFile = fileNormalize.getParentFile();
        if (parentFile != null && !parentFile.isDirectory() && !parentFile.mkdirs() && !parentFile.isDirectory()) {
            throw new IOException("Failed to create directory " + parentFile + " while trying to rename " + canonicalFile);
        }
        if (canonicalFile.renameTo(fileNormalize)) {
            return;
        }
        copyFile(canonicalFile, fileNormalize);
        if (!tryHardToDelete(canonicalFile)) {
            throw new IOException("Failed to delete " + canonicalFile + " while trying to rename it.");
        }
    }

    public long getFileTimestampGranularity() {
        if (ON_WIN9X) {
            return FAT_FILE_TIMESTAMP_GRANULARITY;
        }
        if (ON_WINDOWS) {
            return 1L;
        }
        if (ON_DOS) {
            return FAT_FILE_TIMESTAMP_GRANULARITY;
        }
        return 1000L;
    }

    public boolean hasErrorInCase(File file) {
        File fileNormalize = normalize(file.getAbsolutePath());
        if (!fileNormalize.exists()) {
            return false;
        }
        final String name = fileNormalize.getName();
        String[] list = fileNormalize.getParentFile().list(new FilenameFilter() { // from class: org.apache.tools.ant.util.FileUtils.1
            @Override // java.io.FilenameFilter
            public boolean accept(File file2, String str) {
                return str.equalsIgnoreCase(name) && !str.equals(name);
            }
        });
        return list != null && list.length == 1;
    }

    public boolean isUpToDate(File file, File file2, long j) {
        if (file2.exists()) {
            return isUpToDate(file.lastModified(), file2.lastModified(), j);
        }
        return false;
    }

    public boolean isUpToDate(File file, File file2) {
        return isUpToDate(file, file2, getFileTimestampGranularity());
    }

    public boolean isUpToDate(long j, long j2) {
        return isUpToDate(j, j2, getFileTimestampGranularity());
    }

    public static void close(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException unused) {
            }
        }
    }

    public static void close(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException unused) {
            }
        }
    }

    public static void close(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException unused) {
            }
        }
    }

    public static void close(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException unused) {
            }
        }
    }

    public static void close(Channel channel) {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException unused) {
            }
        }
    }

    public static void close(URLConnection uRLConnection) {
        if (uRLConnection != null) {
            try {
                if (uRLConnection instanceof JarURLConnection) {
                    ((JarURLConnection) uRLConnection).getJarFile().close();
                } else if (uRLConnection instanceof HttpURLConnection) {
                    ((HttpURLConnection) uRLConnection).disconnect();
                }
            } catch (IOException unused) {
            }
        }
    }

    public static void delete(File file) {
        if (file != null) {
            file.delete();
        }
    }

    public boolean tryHardToDelete(File file) {
        return tryHardToDelete(file, ON_WINDOWS);
    }

    public boolean tryHardToDelete(File file, boolean z) {
        if (file.delete()) {
            return true;
        }
        if (z) {
            System.gc();
        }
        try {
            Thread.sleep(10L);
        } catch (InterruptedException unused) {
        }
        return file.delete();
    }

    public static String getRelativePath(File file, File file2) throws Exception {
        String canonicalPath = file.getCanonicalPath();
        String canonicalPath2 = file2.getCanonicalPath();
        String[] pathStack = getPathStack(canonicalPath);
        String[] pathStack2 = getPathStack(canonicalPath2);
        if (pathStack2.length > 0 && pathStack.length > 0) {
            if (!pathStack[0].equals(pathStack2[0])) {
                return getPath(Arrays.asList(pathStack2));
            }
            int iMin = Math.min(pathStack.length, pathStack2.length);
            int i = 1;
            while (i < iMin && pathStack[i].equals(pathStack2[i])) {
                i++;
            }
            ArrayList arrayList = new ArrayList();
            for (int i2 = i; i2 < pathStack.length; i2++) {
                arrayList.add("..");
            }
            while (i < pathStack2.length) {
                arrayList.add(pathStack2[i]);
                i++;
            }
            return getPath(arrayList);
        }
        return getPath(Arrays.asList(pathStack2));
    }

    public static String[] getPathStack(String str) {
        return str.replace(File.separatorChar, '/').split("/");
    }

    public static String getPath(List list) {
        return getPath(list, '/');
    }

    public static String getPath(List list, char c) {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator it = list.iterator();
        if (it.hasNext()) {
            stringBuffer.append(it.next());
        }
        while (it.hasNext()) {
            stringBuffer.append(c);
            stringBuffer.append(it.next());
        }
        return stringBuffer.toString();
    }

    public String getDefaultEncoding() {
        InputStreamReader inputStreamReader = new InputStreamReader(new InputStream() { // from class: org.apache.tools.ant.util.FileUtils.2
            @Override // java.io.InputStream
            public int read() {
                return -1;
            }
        });
        try {
            return inputStreamReader.getEncoding();
        } finally {
            close(inputStreamReader);
        }
    }
}
