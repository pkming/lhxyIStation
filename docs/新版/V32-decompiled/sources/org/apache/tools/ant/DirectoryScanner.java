package org.apache.tools.ant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceFactory;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.types.selectors.SelectorScanner;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.types.selectors.TokenizedPath;
import org.apache.tools.ant.types.selectors.TokenizedPattern;
import org.apache.tools.ant.util.CollectionUtils;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.SymbolicLinkUtils;
import org.apache.tools.ant.util.VectorSet;

/* JADX INFO: loaded from: classes3.dex */
public class DirectoryScanner implements FileScanner, SelectorScanner, ResourceFactory {
    public static final String DOES_NOT_EXIST_POSTFIX = " does not exist.";
    public static final int MAX_LEVELS_OF_SYMLINKS = 5;
    protected File basedir;
    protected Vector<String> dirsDeselected;
    protected Vector<String> dirsExcluded;
    protected Vector<String> dirsIncluded;
    protected Vector<String> dirsNotIncluded;
    private TokenizedPattern[] excludePatterns;
    protected String[] excludes;
    protected Vector<String> filesDeselected;
    protected Vector<String> filesExcluded;
    protected Vector<String> filesIncluded;
    protected Vector<String> filesNotIncluded;
    private TokenizedPattern[] includePatterns;
    protected String[] includes;
    private static final boolean ON_VMS = Os.isFamily(Os.FAMILY_VMS);
    protected static final String[] DEFAULTEXCLUDES = {"**/*~", "**/#*#", "**/.#*", "**/%*%", "**/._*", "**/CVS", "**/CVS/**", "**/.cvsignore", "**/SCCS", "**/SCCS/**", "**/vssver.scc", "**/.svn", "**/.svn/**", "**/.git", "**/.git/**", "**/.gitattributes", "**/.gitignore", "**/.gitmodules", "**/.hg", "**/.hg/**", "**/.hgignore", "**/.hgsub", "**/.hgsubstate", "**/.hgtags", "**/.bzr", "**/.bzr/**", "**/.bzrignore", "**/.DS_Store"};
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final SymbolicLinkUtils SYMLINK_UTILS = SymbolicLinkUtils.getSymbolicLinkUtils();
    private static final Set<String> defaultExcludes = new HashSet();
    protected FileSelector[] selectors = null;
    protected boolean haveSlowResults = false;
    protected boolean isCaseSensitive = true;
    protected boolean errorOnMissingDir = true;
    private boolean followSymlinks = true;
    protected boolean everythingIncluded = true;
    private Set<String> scannedDirs = new HashSet();
    private Map<String, TokenizedPath> includeNonPatterns = new HashMap();
    private Map<String, TokenizedPath> excludeNonPatterns = new HashMap();
    private boolean areNonPatternSetsReady = false;
    private boolean scanning = false;
    private Object scanLock = new Object();
    private boolean slowScanning = false;
    private Object slowScanLock = new Object();
    private IllegalStateException illegal = null;
    private int maxLevelsOfSymlinks = 5;
    private Set<String> notFollowedSymlinks = new HashSet();

    static {
        resetDefaultExcludes();
    }

    protected static boolean matchPatternStart(String str, String str2) {
        return SelectorUtils.matchPatternStart(str, str2);
    }

    protected static boolean matchPatternStart(String str, String str2, boolean z) {
        return SelectorUtils.matchPatternStart(str, str2, z);
    }

    protected static boolean matchPath(String str, String str2) {
        return SelectorUtils.matchPath(str, str2);
    }

    protected static boolean matchPath(String str, String str2, boolean z) {
        return SelectorUtils.matchPath(str, str2, z);
    }

    public static boolean match(String str, String str2) {
        return SelectorUtils.match(str, str2);
    }

    protected static boolean match(String str, String str2, boolean z) {
        return SelectorUtils.match(str, str2, z);
    }

    public static String[] getDefaultExcludes() {
        String[] strArr;
        Set<String> set = defaultExcludes;
        synchronized (set) {
            strArr = (String[]) set.toArray(new String[set.size()]);
        }
        return strArr;
    }

    public static boolean addDefaultExclude(String str) {
        boolean zAdd;
        Set<String> set = defaultExcludes;
        synchronized (set) {
            zAdd = set.add(str);
        }
        return zAdd;
    }

    public static boolean removeDefaultExclude(String str) {
        boolean zRemove;
        Set<String> set = defaultExcludes;
        synchronized (set) {
            zRemove = set.remove(str);
        }
        return zRemove;
    }

    public static void resetDefaultExcludes() {
        Set<String> set = defaultExcludes;
        synchronized (set) {
            set.clear();
            int i = 0;
            while (true) {
                String[] strArr = DEFAULTEXCLUDES;
                if (i < strArr.length) {
                    defaultExcludes.add(strArr[i]);
                    i++;
                }
            }
        }
    }

    @Override // org.apache.tools.ant.FileScanner
    public void setBasedir(String str) {
        setBasedir(str == null ? (File) null : new File(str.replace('/', File.separatorChar).replace('\\', File.separatorChar)));
    }

    @Override // org.apache.tools.ant.FileScanner
    public synchronized void setBasedir(File file) {
        this.basedir = file;
    }

    @Override // org.apache.tools.ant.FileScanner
    public synchronized File getBasedir() {
        return this.basedir;
    }

    public synchronized boolean isCaseSensitive() {
        return this.isCaseSensitive;
    }

    @Override // org.apache.tools.ant.FileScanner
    public synchronized void setCaseSensitive(boolean z) {
        this.isCaseSensitive = z;
    }

    public void setErrorOnMissingDir(boolean z) {
        this.errorOnMissingDir = z;
    }

    public synchronized boolean isFollowSymlinks() {
        return this.followSymlinks;
    }

    public synchronized void setFollowSymlinks(boolean z) {
        this.followSymlinks = z;
    }

    public void setMaxLevelsOfSymlinks(int i) {
        this.maxLevelsOfSymlinks = i;
    }

    @Override // org.apache.tools.ant.FileScanner
    public synchronized void setIncludes(String[] strArr) {
        if (strArr == null) {
            this.includes = null;
        } else {
            this.includes = new String[strArr.length];
            for (int i = 0; i < strArr.length; i++) {
                this.includes[i] = normalizePattern(strArr[i]);
            }
        }
    }

    @Override // org.apache.tools.ant.FileScanner
    public synchronized void setExcludes(String[] strArr) {
        if (strArr == null) {
            this.excludes = null;
        } else {
            this.excludes = new String[strArr.length];
            for (int i = 0; i < strArr.length; i++) {
                this.excludes[i] = normalizePattern(strArr[i]);
            }
        }
    }

    public synchronized void addExcludes(String[] strArr) {
        if (strArr != null) {
            if (strArr.length > 0) {
                String[] strArr2 = this.excludes;
                if (strArr2 != null && strArr2.length > 0) {
                    String[] strArr3 = new String[strArr.length + strArr2.length];
                    System.arraycopy(strArr2, 0, strArr3, 0, strArr2.length);
                    for (int i = 0; i < strArr.length; i++) {
                        strArr3[this.excludes.length + i] = normalizePattern(strArr[i]);
                    }
                    this.excludes = strArr3;
                } else {
                    setExcludes(strArr);
                }
            }
        }
    }

    private static String normalizePattern(String str) {
        String strReplace = str.replace('/', File.separatorChar).replace('\\', File.separatorChar);
        return strReplace.endsWith(File.separator) ? strReplace + SelectorUtils.DEEP_TREE_MATCH : strReplace;
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorScanner
    public synchronized void setSelectors(FileSelector[] fileSelectorArr) {
        this.selectors = fileSelectorArr;
    }

    public synchronized boolean isEverythingIncluded() {
        return this.everythingIncluded;
    }

    @Override // org.apache.tools.ant.FileScanner
    public void scan() throws IllegalStateException {
        synchronized (this.scanLock) {
            if (this.scanning) {
                while (this.scanning) {
                    try {
                        this.scanLock.wait();
                    } catch (InterruptedException unused) {
                    }
                }
                IllegalStateException illegalStateException = this.illegal;
                if (illegalStateException != null) {
                    throw illegalStateException;
                }
                return;
            }
            boolean z = true;
            this.scanning = true;
            File file = this.basedir;
            try {
                try {
                    synchronized (this) {
                        String[] strArr = null;
                        this.illegal = null;
                        clearResults();
                        String[] strArr2 = this.includes;
                        boolean z2 = strArr2 == null;
                        if (z2) {
                            strArr2 = new String[]{SelectorUtils.DEEP_TREE_MATCH};
                        }
                        this.includes = strArr2;
                        String[] strArr3 = this.excludes;
                        if (strArr3 != null) {
                            z = false;
                        }
                        if (z) {
                            strArr3 = new String[0];
                        }
                        this.excludes = strArr3;
                        File file2 = this.basedir;
                        if (file2 != null && !this.followSymlinks && SYMLINK_UTILS.isSymbolicLink(file2)) {
                            this.notFollowedSymlinks.add(this.basedir.getAbsolutePath());
                            this.basedir = null;
                        }
                        File file3 = this.basedir;
                        if (file3 != null) {
                            if (file3.exists()) {
                                if (!this.basedir.isDirectory()) {
                                    this.illegal = new IllegalStateException("basedir " + this.basedir + " is not a directory.");
                                }
                            } else {
                                if (!this.errorOnMissingDir) {
                                    this.basedir = file;
                                    synchronized (this.scanLock) {
                                        this.scanning = false;
                                        this.scanLock.notifyAll();
                                    }
                                    return;
                                }
                                this.illegal = new IllegalStateException("basedir " + this.basedir + DOES_NOT_EXIST_POSTFIX);
                            }
                            IllegalStateException illegalStateException2 = this.illegal;
                            if (illegalStateException2 != null) {
                                throw illegalStateException2;
                            }
                        } else if (z2) {
                            this.basedir = file;
                            synchronized (this.scanLock) {
                                this.scanning = false;
                                this.scanLock.notifyAll();
                            }
                            return;
                        }
                        if (!isIncluded(TokenizedPath.EMPTY_PATH)) {
                            this.dirsNotIncluded.addElement("");
                        } else if (isExcluded(TokenizedPath.EMPTY_PATH)) {
                            this.dirsExcluded.addElement("");
                        } else if (isSelected("", this.basedir)) {
                            this.dirsIncluded.addElement("");
                        } else {
                            this.dirsDeselected.addElement("");
                        }
                        checkIncludePatterns();
                        clearCaches();
                        this.includes = z2 ? null : this.includes;
                        if (!z) {
                            strArr = this.excludes;
                        }
                        this.excludes = strArr;
                        this.basedir = file;
                        synchronized (this.scanLock) {
                            this.scanning = false;
                            this.scanLock.notifyAll();
                        }
                    }
                } catch (IOException e) {
                    throw new BuildException(e);
                }
            } catch (Throwable th) {
                this.basedir = file;
                synchronized (this.scanLock) {
                    this.scanning = false;
                    this.scanLock.notifyAll();
                    throw th;
                }
            }
        }
    }

    private void checkIncludePatterns() {
        File file;
        File fileFindFile;
        File file2;
        ensureNonPatternSetsReady();
        HashMap map = new HashMap();
        int i = 0;
        while (true) {
            TokenizedPattern[] tokenizedPatternArr = this.includePatterns;
            if (i >= tokenizedPatternArr.length) {
                break;
            }
            String string = tokenizedPatternArr[i].toString();
            if (!shouldSkipPattern(string)) {
                map.put(this.includePatterns[i].rtrimWildcardTokens(), string);
            }
            i++;
        }
        for (Map.Entry<String, TokenizedPath> entry : this.includeNonPatterns.entrySet()) {
            String key = entry.getKey();
            if (!shouldSkipPattern(key)) {
                map.put(entry.getValue(), key);
            }
        }
        if (map.containsKey(TokenizedPath.EMPTY_PATH) && (file2 = this.basedir) != null) {
            scandir(file2, "", true);
            return;
        }
        File canonicalFile = null;
        File file3 = this.basedir;
        if (file3 != null) {
            try {
                canonicalFile = file3.getCanonicalFile();
            } catch (IOException e) {
                throw new BuildException(e);
            }
        }
        for (Map.Entry entry2 : map.entrySet()) {
            TokenizedPath tokenizedPath = (TokenizedPath) entry2.getKey();
            String string2 = tokenizedPath.toString();
            if (this.basedir != null || FileUtils.isAbsolutePath(string2)) {
                File file4 = new File(this.basedir, string2);
                if (file4.exists()) {
                    try {
                        if ((!(this.basedir == null ? file4.getCanonicalPath() : FILE_UTILS.removeLeadingPath(canonicalFile, file4.getCanonicalFile())).equals(string2) || ON_VMS) && (file4 = tokenizedPath.findFile(this.basedir, true)) != null && (file = this.basedir) != null) {
                            string2 = FILE_UTILS.removeLeadingPath(file, file4);
                            if (!tokenizedPath.toString().equals(string2)) {
                                tokenizedPath = new TokenizedPath(string2);
                            }
                        }
                    } catch (IOException e2) {
                        throw new BuildException(e2);
                    }
                }
                if ((file4 == null || !file4.exists()) && !isCaseSensitive() && (fileFindFile = tokenizedPath.findFile(this.basedir, false)) != null && fileFindFile.exists()) {
                    File file5 = this.basedir;
                    string2 = file5 == null ? fileFindFile.getAbsolutePath() : FILE_UTILS.removeLeadingPath(file5, fileFindFile);
                    tokenizedPath = new TokenizedPath(string2);
                    file4 = fileFindFile;
                }
                if (file4 != null && file4.exists()) {
                    if (!this.followSymlinks && tokenizedPath.isSymlink(this.basedir)) {
                        accountForNotFollowedSymlink(tokenizedPath, file4);
                    } else if (file4.isDirectory()) {
                        if (isIncluded(tokenizedPath) && string2.length() > 0) {
                            accountForIncludedDir(tokenizedPath, file4, true);
                        } else {
                            scandir(file4, tokenizedPath, true);
                        }
                    } else if (file4.isFile()) {
                        String str = (String) entry2.getValue();
                        if (isCaseSensitive() ? str.equals(string2) : str.equalsIgnoreCase(string2)) {
                            accountForIncludedFile(tokenizedPath, file4);
                        }
                    }
                }
            }
        }
    }

    private boolean shouldSkipPattern(String str) {
        if (!FileUtils.isAbsolutePath(str)) {
            return this.basedir == null;
        }
        File file = this.basedir;
        return (file == null || SelectorUtils.matchPatternStart(str, file.getAbsolutePath(), isCaseSensitive())) ? false : true;
    }

    protected synchronized void clearResults() {
        this.filesIncluded = new VectorSet();
        this.filesNotIncluded = new VectorSet();
        this.filesExcluded = new VectorSet();
        this.filesDeselected = new VectorSet();
        this.dirsIncluded = new VectorSet();
        this.dirsNotIncluded = new VectorSet();
        this.dirsExcluded = new VectorSet();
        this.dirsDeselected = new VectorSet();
        this.everythingIncluded = this.basedir != null;
        this.scannedDirs.clear();
        this.notFollowedSymlinks.clear();
    }

    protected void slowScan() {
        synchronized (this.slowScanLock) {
            if (this.haveSlowResults) {
                return;
            }
            if (this.slowScanning) {
                while (this.slowScanning) {
                    try {
                        this.slowScanLock.wait();
                    } catch (InterruptedException unused) {
                    }
                }
                return;
            }
            this.slowScanning = true;
            try {
                synchronized (this) {
                    String[] strArr = this.includes;
                    boolean z = strArr == null;
                    if (z) {
                        strArr = new String[]{SelectorUtils.DEEP_TREE_MATCH};
                    }
                    this.includes = strArr;
                    String[] strArr2 = this.excludes;
                    boolean z2 = strArr2 == null;
                    if (z2) {
                        strArr2 = new String[0];
                    }
                    this.excludes = strArr2;
                    String[] strArr3 = new String[this.dirsExcluded.size()];
                    this.dirsExcluded.copyInto(strArr3);
                    String[] strArr4 = new String[this.dirsNotIncluded.size()];
                    this.dirsNotIncluded.copyInto(strArr4);
                    ensureNonPatternSetsReady();
                    processSlowScan(strArr3);
                    processSlowScan(strArr4);
                    clearCaches();
                    String[] strArr5 = null;
                    this.includes = z ? null : this.includes;
                    if (!z2) {
                        strArr5 = this.excludes;
                    }
                    this.excludes = strArr5;
                }
                synchronized (this.slowScanLock) {
                    this.haveSlowResults = true;
                    this.slowScanning = false;
                    this.slowScanLock.notifyAll();
                }
            } catch (Throwable th) {
                synchronized (this.slowScanLock) {
                    this.haveSlowResults = true;
                    this.slowScanning = false;
                    this.slowScanLock.notifyAll();
                    throw th;
                }
            }
        }
    }

    private void processSlowScan(String[] strArr) {
        for (int i = 0; i < strArr.length; i++) {
            TokenizedPath tokenizedPath = new TokenizedPath(strArr[i]);
            if (!couldHoldIncluded(tokenizedPath) || contentsExcluded(tokenizedPath)) {
                scandir(new File(this.basedir, strArr[i]), tokenizedPath, false);
            }
        }
    }

    protected void scandir(File file, String str, boolean z) {
        scandir(file, new TokenizedPath(str), z);
    }

    private void scandir(File file, TokenizedPath tokenizedPath, boolean z) {
        if (file == null) {
            throw new BuildException("dir must not be null.");
        }
        String[] list = file.list();
        if (list == null) {
            if (!file.exists()) {
                throw new BuildException(file + DOES_NOT_EXIST_POSTFIX);
            }
            if (!file.isDirectory()) {
                throw new BuildException(file + " is not a directory.");
            }
            throw new BuildException("IO error scanning directory '" + file.getAbsolutePath() + "'");
        }
        scandir(file, tokenizedPath, z, list, new LinkedList<>());
    }

    private void scandir(File file, TokenizedPath tokenizedPath, boolean z, String[] strArr, LinkedList<String> linkedList) {
        String[] strArr2;
        String[] strArr3 = strArr;
        String string = tokenizedPath.toString();
        if (string.length() > 0 && !string.endsWith(File.separator)) {
            string = string + File.separator;
        }
        String str = string;
        if (z && hasBeenScanned(str)) {
            return;
        }
        if (!this.followSymlinks) {
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < strArr3.length; i++) {
                try {
                    if (SYMLINK_UTILS.isSymbolicLink(file, strArr3[i])) {
                        String str2 = str + strArr3[i];
                        File file2 = new File(file, strArr3[i]);
                        if (file2.isDirectory()) {
                            this.dirsExcluded.addElement(str2);
                        } else if (file2.isFile()) {
                            this.filesExcluded.addElement(str2);
                        }
                        accountForNotFollowedSymlink(str2, file2);
                    } else {
                        arrayList.add(strArr3[i]);
                    }
                } catch (IOException unused) {
                    System.err.println("IOException caught while checking for links, couldn't get canonical path!");
                    arrayList.add(strArr3[i]);
                }
            }
            strArr3 = (String[]) arrayList.toArray(new String[arrayList.size()]);
        } else {
            linkedList.addFirst(file.getName());
        }
        String[] strArr4 = strArr3;
        for (int i2 = 0; i2 < strArr4.length; i2++) {
            String str3 = str + strArr4[i2];
            TokenizedPath tokenizedPath2 = new TokenizedPath(tokenizedPath, strArr4[i2]);
            File file3 = new File(file, strArr4[i2]);
            String[] list = file3.list();
            if (list == null || (list.length == 0 && file3.isFile())) {
                if (isIncluded(tokenizedPath2)) {
                    accountForIncludedFile(tokenizedPath2, file3);
                } else {
                    this.everythingIncluded = false;
                    this.filesNotIncluded.addElement(str3);
                }
            } else if (file3.isDirectory()) {
                if (this.followSymlinks && causesIllegalSymlinkLoop(strArr4[i2], file, linkedList)) {
                    System.err.println("skipping symbolic link " + file3.getAbsolutePath() + " -- too many levels of symbolic links.");
                    this.notFollowedSymlinks.add(file3.getAbsolutePath());
                } else {
                    if (isIncluded(tokenizedPath2)) {
                        strArr2 = list;
                        accountForIncludedDir(tokenizedPath2, file3, z, list, linkedList);
                    } else {
                        strArr2 = list;
                        this.everythingIncluded = false;
                        this.dirsNotIncluded.addElement(str3);
                        if (z && couldHoldIncluded(tokenizedPath2) && !contentsExcluded(tokenizedPath2)) {
                            scandir(file3, tokenizedPath2, z, strArr2, linkedList);
                        }
                    }
                    if (!z) {
                        scandir(file3, tokenizedPath2, z, strArr2, linkedList);
                    }
                }
            }
        }
        if (this.followSymlinks) {
            linkedList.removeFirst();
        }
    }

    private void accountForIncludedFile(TokenizedPath tokenizedPath, File file) {
        processIncluded(tokenizedPath, file, this.filesIncluded, this.filesExcluded, this.filesDeselected);
    }

    private void accountForIncludedDir(TokenizedPath tokenizedPath, File file, boolean z) {
        processIncluded(tokenizedPath, file, this.dirsIncluded, this.dirsExcluded, this.dirsDeselected);
        if (z && couldHoldIncluded(tokenizedPath) && !contentsExcluded(tokenizedPath)) {
            scandir(file, tokenizedPath, z);
        }
    }

    private void accountForIncludedDir(TokenizedPath tokenizedPath, File file, boolean z, String[] strArr, LinkedList<String> linkedList) {
        processIncluded(tokenizedPath, file, this.dirsIncluded, this.dirsExcluded, this.dirsDeselected);
        if (z && couldHoldIncluded(tokenizedPath) && !contentsExcluded(tokenizedPath)) {
            scandir(file, tokenizedPath, z, strArr, linkedList);
        }
    }

    private void accountForNotFollowedSymlink(String str, File file) {
        accountForNotFollowedSymlink(new TokenizedPath(str), file);
    }

    private void accountForNotFollowedSymlink(TokenizedPath tokenizedPath, File file) {
        if (isExcluded(tokenizedPath)) {
            return;
        }
        if (isIncluded(tokenizedPath) || (file.isDirectory() && couldHoldIncluded(tokenizedPath) && !contentsExcluded(tokenizedPath))) {
            this.notFollowedSymlinks.add(file.getAbsolutePath());
        }
    }

    private void processIncluded(TokenizedPath tokenizedPath, File file, Vector<String> vector, Vector<String> vector2, Vector<String> vector3) {
        String string = tokenizedPath.toString();
        if (vector.contains(string) || vector2.contains(string) || vector3.contains(string)) {
            return;
        }
        boolean z = false;
        if (isExcluded(tokenizedPath)) {
            vector2.add(string);
        } else if (isSelected(string, file)) {
            z = true;
            vector.add(string);
        } else {
            vector3.add(string);
        }
        this.everythingIncluded &= z;
    }

    protected boolean isIncluded(String str) {
        return isIncluded(new TokenizedPath(str));
    }

    private boolean isIncluded(TokenizedPath tokenizedPath) {
        ensureNonPatternSetsReady();
        if (!isCaseSensitive() ? !this.includeNonPatterns.containsKey(tokenizedPath.toString().toUpperCase()) : !this.includeNonPatterns.containsKey(tokenizedPath.toString())) {
            return true;
        }
        int i = 0;
        while (true) {
            TokenizedPattern[] tokenizedPatternArr = this.includePatterns;
            if (i >= tokenizedPatternArr.length) {
                return false;
            }
            if (tokenizedPatternArr[i].matchPath(tokenizedPath, isCaseSensitive())) {
                return true;
            }
            i++;
        }
    }

    protected boolean couldHoldIncluded(String str) {
        return couldHoldIncluded(new TokenizedPath(str));
    }

    private boolean couldHoldIncluded(TokenizedPath tokenizedPath) {
        int i = 0;
        while (true) {
            TokenizedPattern[] tokenizedPatternArr = this.includePatterns;
            if (i < tokenizedPatternArr.length) {
                if (couldHoldIncluded(tokenizedPath, tokenizedPatternArr[i])) {
                    return true;
                }
                i++;
            } else {
                Iterator<TokenizedPath> it = this.includeNonPatterns.values().iterator();
                while (it.hasNext()) {
                    if (couldHoldIncluded(tokenizedPath, it.next().toPattern())) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    private boolean couldHoldIncluded(TokenizedPath tokenizedPath, TokenizedPattern tokenizedPattern) {
        return tokenizedPattern.matchStartOf(tokenizedPath, isCaseSensitive()) && isMorePowerfulThanExcludes(tokenizedPath.toString()) && isDeeper(tokenizedPattern, tokenizedPath);
    }

    private boolean isDeeper(TokenizedPattern tokenizedPattern, TokenizedPath tokenizedPath) {
        return tokenizedPattern.containsPattern(SelectorUtils.DEEP_TREE_MATCH) || tokenizedPattern.depth() > tokenizedPath.depth();
    }

    private boolean isMorePowerfulThanExcludes(String str) {
        String str2 = str + File.separatorChar + SelectorUtils.DEEP_TREE_MATCH;
        int i = 0;
        while (true) {
            TokenizedPattern[] tokenizedPatternArr = this.excludePatterns;
            if (i >= tokenizedPatternArr.length) {
                return true;
            }
            if (tokenizedPatternArr[i].toString().equals(str2)) {
                return false;
            }
            i++;
        }
    }

    boolean contentsExcluded(TokenizedPath tokenizedPath) {
        int i = 0;
        while (true) {
            TokenizedPattern[] tokenizedPatternArr = this.excludePatterns;
            if (i >= tokenizedPatternArr.length) {
                return false;
            }
            if (tokenizedPatternArr[i].endsWith(SelectorUtils.DEEP_TREE_MATCH) && this.excludePatterns[i].withoutLastToken().matchPath(tokenizedPath, isCaseSensitive())) {
                return true;
            }
            i++;
        }
    }

    protected boolean isExcluded(String str) {
        return isExcluded(new TokenizedPath(str));
    }

    private boolean isExcluded(TokenizedPath tokenizedPath) {
        ensureNonPatternSetsReady();
        if (!isCaseSensitive() ? !this.excludeNonPatterns.containsKey(tokenizedPath.toString().toUpperCase()) : !this.excludeNonPatterns.containsKey(tokenizedPath.toString())) {
            return true;
        }
        int i = 0;
        while (true) {
            TokenizedPattern[] tokenizedPatternArr = this.excludePatterns;
            if (i >= tokenizedPatternArr.length) {
                return false;
            }
            if (tokenizedPatternArr[i].matchPath(tokenizedPath, isCaseSensitive())) {
                return true;
            }
            i++;
        }
    }

    protected boolean isSelected(String str, File file) {
        if (this.selectors == null) {
            return true;
        }
        int i = 0;
        while (true) {
            FileSelector[] fileSelectorArr = this.selectors;
            if (i >= fileSelectorArr.length) {
                return true;
            }
            if (!fileSelectorArr[i].isSelected(this.basedir, str, file)) {
                return false;
            }
            i++;
        }
    }

    @Override // org.apache.tools.ant.FileScanner
    public String[] getIncludedFiles() {
        String[] strArr;
        synchronized (this) {
            Vector<String> vector = this.filesIncluded;
            if (vector == null) {
                throw new IllegalStateException("Must call scan() first");
            }
            strArr = new String[vector.size()];
            this.filesIncluded.copyInto(strArr);
        }
        Arrays.sort(strArr);
        return strArr;
    }

    public synchronized int getIncludedFilesCount() {
        Vector<String> vector;
        vector = this.filesIncluded;
        if (vector == null) {
            throw new IllegalStateException("Must call scan() first");
        }
        return vector.size();
    }

    @Override // org.apache.tools.ant.FileScanner
    public synchronized String[] getNotIncludedFiles() {
        String[] strArr;
        slowScan();
        strArr = new String[this.filesNotIncluded.size()];
        this.filesNotIncluded.copyInto(strArr);
        return strArr;
    }

    @Override // org.apache.tools.ant.FileScanner
    public synchronized String[] getExcludedFiles() {
        String[] strArr;
        slowScan();
        strArr = new String[this.filesExcluded.size()];
        this.filesExcluded.copyInto(strArr);
        return strArr;
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorScanner
    public synchronized String[] getDeselectedFiles() {
        String[] strArr;
        slowScan();
        strArr = new String[this.filesDeselected.size()];
        this.filesDeselected.copyInto(strArr);
        return strArr;
    }

    @Override // org.apache.tools.ant.FileScanner
    public String[] getIncludedDirectories() {
        String[] strArr;
        synchronized (this) {
            Vector<String> vector = this.dirsIncluded;
            if (vector == null) {
                throw new IllegalStateException("Must call scan() first");
            }
            strArr = new String[vector.size()];
            this.dirsIncluded.copyInto(strArr);
        }
        Arrays.sort(strArr);
        return strArr;
    }

    public synchronized int getIncludedDirsCount() {
        Vector<String> vector;
        vector = this.dirsIncluded;
        if (vector == null) {
            throw new IllegalStateException("Must call scan() first");
        }
        return vector.size();
    }

    @Override // org.apache.tools.ant.FileScanner
    public synchronized String[] getNotIncludedDirectories() {
        String[] strArr;
        slowScan();
        strArr = new String[this.dirsNotIncluded.size()];
        this.dirsNotIncluded.copyInto(strArr);
        return strArr;
    }

    @Override // org.apache.tools.ant.FileScanner
    public synchronized String[] getExcludedDirectories() {
        String[] strArr;
        slowScan();
        strArr = new String[this.dirsExcluded.size()];
        this.dirsExcluded.copyInto(strArr);
        return strArr;
    }

    @Override // org.apache.tools.ant.types.selectors.SelectorScanner
    public synchronized String[] getDeselectedDirectories() {
        String[] strArr;
        slowScan();
        strArr = new String[this.dirsDeselected.size()];
        this.dirsDeselected.copyInto(strArr);
        return strArr;
    }

    public synchronized String[] getNotFollowedSymlinks() {
        String[] strArr;
        synchronized (this) {
            Set<String> set = this.notFollowedSymlinks;
            strArr = (String[]) set.toArray(new String[set.size()]);
        }
        return strArr;
        Arrays.sort(strArr);
        return strArr;
    }

    @Override // org.apache.tools.ant.FileScanner
    public synchronized void addDefaultExcludes() {
        String[] strArr = this.excludes;
        int length = strArr == null ? 0 : strArr.length;
        String[] defaultExcludes2 = getDefaultExcludes();
        String[] strArr2 = new String[defaultExcludes2.length + length];
        if (length > 0) {
            System.arraycopy(this.excludes, 0, strArr2, 0, length);
        }
        for (int i = 0; i < defaultExcludes2.length; i++) {
            strArr2[i + length] = defaultExcludes2[i].replace('/', File.separatorChar).replace('\\', File.separatorChar);
        }
        this.excludes = strArr2;
    }

    @Override // org.apache.tools.ant.types.ResourceFactory
    public synchronized Resource getResource(String str) {
        return new FileResource(this.basedir, str);
    }

    private boolean hasBeenScanned(String str) {
        return !this.scannedDirs.add(str);
    }

    Set<String> getScannedDirs() {
        return this.scannedDirs;
    }

    private synchronized void clearCaches() {
        this.includeNonPatterns.clear();
        this.excludeNonPatterns.clear();
        this.includePatterns = null;
        this.excludePatterns = null;
        this.areNonPatternSetsReady = false;
    }

    synchronized void ensureNonPatternSetsReady() {
        if (!this.areNonPatternSetsReady) {
            this.includePatterns = fillNonPatternSet(this.includeNonPatterns, this.includes);
            this.excludePatterns = fillNonPatternSet(this.excludeNonPatterns, this.excludes);
            this.areNonPatternSetsReady = true;
        }
    }

    private TokenizedPattern[] fillNonPatternSet(Map<String, TokenizedPath> map, String[] strArr) {
        ArrayList arrayList = new ArrayList(strArr.length);
        for (int i = 0; i < strArr.length; i++) {
            if (!SelectorUtils.hasWildcards(strArr[i])) {
                String upperCase = isCaseSensitive() ? strArr[i] : strArr[i].toUpperCase();
                map.put(upperCase, new TokenizedPath(upperCase));
            } else {
                arrayList.add(new TokenizedPattern(strArr[i]));
            }
        }
        return (TokenizedPattern[]) arrayList.toArray(new TokenizedPattern[arrayList.size()]);
    }

    private boolean causesIllegalSymlinkLoop(String str, File file, LinkedList<String> linkedList) {
        try {
            if (linkedList.size() < this.maxLevelsOfSymlinks || CollectionUtils.frequency(linkedList, str) < this.maxLevelsOfSymlinks || !SYMLINK_UTILS.isSymbolicLink(file, str)) {
                return false;
            }
            ArrayList arrayList = new ArrayList();
            String canonicalPath = FILE_UTILS.resolveFile(file, str).getCanonicalPath();
            arrayList.add(canonicalPath);
            String str2 = "";
            for (String str3 : linkedList) {
                str2 = str2 + "../";
                if (str.equals(str3)) {
                    arrayList.add(FILE_UTILS.resolveFile(file, str2 + str3).getCanonicalPath());
                    if (arrayList.size() > this.maxLevelsOfSymlinks && CollectionUtils.frequency(arrayList, canonicalPath) > this.maxLevelsOfSymlinks) {
                        return true;
                    }
                }
            }
            return false;
        } catch (IOException e) {
            throw new BuildException("Caught error while checking for symbolic links", e);
        }
    }
}
