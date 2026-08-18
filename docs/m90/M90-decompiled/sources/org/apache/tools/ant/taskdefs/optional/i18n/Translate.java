package org.apache.tools.ant.taskdefs.optional.i18n;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.LineTokenizer;

/* JADX INFO: loaded from: classes3.dex */
public class Translate extends MatchingTask {
    private static final int BUNDLE_DEFAULT_LANGUAGE = 6;
    private static final int BUNDLE_DEFAULT_LANGUAGE_COUNTRY = 5;
    private static final int BUNDLE_DEFAULT_LANGUAGE_COUNTRY_VARIANT = 4;
    private static final int BUNDLE_MAX_ALTERNATIVES = 7;
    private static final int BUNDLE_NOMATCH = 3;
    private static final int BUNDLE_SPECIFIED_LANGUAGE = 2;
    private static final int BUNDLE_SPECIFIED_LANGUAGE_COUNTRY = 1;
    private static final int BUNDLE_SPECIFIED_LANGUAGE_COUNTRY_VARIANT = 0;
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private String bundle;
    private String bundleCountry;
    private String bundleEncoding;
    private String bundleLanguage;
    private String bundleVariant;
    private String destEncoding;
    private long destLastModified;
    private String endToken;
    private boolean forceOverwrite;
    private String srcEncoding;
    private long srcLastModified;
    private String startToken;
    private File toDir;
    private Vector filesets = new Vector();
    private Hashtable resourceMap = new Hashtable();
    private long[] bundleLastModified = new long[7];
    private boolean loaded = false;

    public void setBundle(String str) {
        this.bundle = str;
    }

    public void setBundleLanguage(String str) {
        this.bundleLanguage = str;
    }

    public void setBundleCountry(String str) {
        this.bundleCountry = str;
    }

    public void setBundleVariant(String str) {
        this.bundleVariant = str;
    }

    public void setToDir(File file) {
        this.toDir = file;
    }

    public void setStartToken(String str) {
        this.startToken = str;
    }

    public void setEndToken(String str) {
        this.endToken = str;
    }

    public void setSrcEncoding(String str) {
        this.srcEncoding = str;
    }

    public void setDestEncoding(String str) {
        this.destEncoding = str;
    }

    public void setBundleEncoding(String str) {
        this.bundleEncoding = str;
    }

    public void setForceOverwrite(boolean z) {
        this.forceOverwrite = z;
    }

    public void addFileset(FileSet fileSet) {
        this.filesets.addElement(fileSet);
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws Throwable {
        if (this.bundle == null) {
            throw new BuildException("The bundle attribute must be set.", getLocation());
        }
        if (this.startToken == null) {
            throw new BuildException("The starttoken attribute must be set.", getLocation());
        }
        if (this.endToken == null) {
            throw new BuildException("The endtoken attribute must be set.", getLocation());
        }
        if (this.bundleLanguage == null) {
            this.bundleLanguage = Locale.getDefault().getLanguage();
        }
        if (this.bundleCountry == null) {
            this.bundleCountry = Locale.getDefault().getCountry();
        }
        if (this.bundleVariant == null) {
            this.bundleVariant = new Locale(this.bundleLanguage, this.bundleCountry).getVariant();
        }
        File file = this.toDir;
        if (file == null) {
            throw new BuildException("The todir attribute must be set.", getLocation());
        }
        if (!file.exists()) {
            this.toDir.mkdirs();
        } else if (this.toDir.isFile()) {
            throw new BuildException(this.toDir + " is not a directory");
        }
        if (this.srcEncoding == null) {
            this.srcEncoding = System.getProperty("file.encoding");
        }
        if (this.destEncoding == null) {
            this.destEncoding = this.srcEncoding;
        }
        if (this.bundleEncoding == null) {
            this.bundleEncoding = this.srcEncoding;
        }
        loadResourceMaps();
        translate();
    }

    private void loadResourceMaps() throws BuildException {
        Locale locale = new Locale(this.bundleLanguage, this.bundleCountry, this.bundleVariant);
        String str = locale.getLanguage().length() > 0 ? "_" + locale.getLanguage() : "";
        String str2 = locale.getCountry().length() > 0 ? "_" + locale.getCountry() : "";
        processBundle(this.bundle + str + str2 + (locale.getVariant().length() > 0 ? "_" + locale.getVariant() : ""), 0, false);
        processBundle(this.bundle + str + str2, 1, false);
        processBundle(this.bundle + str, 2, false);
        processBundle(this.bundle, 3, false);
        Locale locale2 = Locale.getDefault();
        String str3 = locale2.getLanguage().length() > 0 ? "_" + locale2.getLanguage() : "";
        String str4 = locale2.getCountry().length() > 0 ? "_" + locale2.getCountry() : "";
        String str5 = locale2.getVariant().length() > 0 ? "_" + locale2.getVariant() : "";
        this.bundleEncoding = System.getProperty("file.encoding");
        processBundle(this.bundle + str3 + str4 + str5, 4, false);
        processBundle(this.bundle + str3 + str4, 5, false);
        processBundle(this.bundle + str3, 6, true);
    }

    private void processBundle(String str, int i, boolean z) throws BuildException {
        File fileResolveFile = getProject().resolveFile(str + ".properties");
        try {
            FileInputStream fileInputStream = new FileInputStream(fileResolveFile);
            this.loaded = true;
            this.bundleLastModified[i] = fileResolveFile.lastModified();
            log("Using " + fileResolveFile, 4);
            loadResourceMap(fileInputStream);
        } catch (IOException e) {
            log(fileResolveFile + " not found.", 4);
            if (!this.loaded && z) {
                throw new BuildException(e.getMessage(), getLocation());
            }
        }
    }

    private void loadResourceMap(FileInputStream fileInputStream) throws BuildException {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, this.bundleEncoding));
            while (true) {
                String line = bufferedReader.readLine();
                if (line != null) {
                    if (line.trim().length() > 1 && '#' != line.charAt(0) && '!' != line.charAt(0)) {
                        int iIndexOf = line.indexOf(61);
                        if (-1 == iIndexOf) {
                            iIndexOf = line.indexOf(58);
                        }
                        if (-1 == iIndexOf) {
                            int i = 0;
                            while (true) {
                                if (i >= line.length()) {
                                    break;
                                }
                                if (Character.isSpaceChar(line.charAt(i))) {
                                    iIndexOf = i;
                                    break;
                                }
                                i++;
                            }
                        }
                        if (-1 != iIndexOf) {
                            String strTrim = line.substring(0, iIndexOf).trim();
                            String strTrim2 = line.substring(iIndexOf + 1).trim();
                            while (strTrim2.endsWith("\\")) {
                                strTrim2 = strTrim2.substring(0, strTrim2.length() - 1);
                                String line2 = bufferedReader.readLine();
                                if (line2 == null) {
                                    break;
                                } else {
                                    strTrim2 = strTrim2 + line2.trim();
                                }
                            }
                            if (strTrim.length() > 0 && this.resourceMap.get(strTrim) == null) {
                                this.resourceMap.put(strTrim, strTrim2);
                            }
                        }
                    }
                } else {
                    bufferedReader.close();
                    return;
                }
            }
        } catch (IOException e) {
            throw new BuildException(e.getMessage(), getLocation());
        }
    }

    private void translate() throws Throwable {
        int size = this.filesets.size();
        int i = 0;
        int i2 = 0;
        while (i2 < size) {
            DirectoryScanner directoryScanner = ((FileSet) this.filesets.elementAt(i2)).getDirectoryScanner(getProject());
            String[] includedFiles = directoryScanner.getIncludedFiles();
            int i3 = i;
            for (int i4 = 0; i4 < includedFiles.length; i4++) {
                try {
                    File fileResolveFile = FILE_UTILS.resolveFile(this.toDir, includedFiles[i4]);
                    try {
                        File file = new File(fileResolveFile.getParent());
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                    } catch (Exception e) {
                        log("Exception occurred while trying to check/create  parent directory.  " + e.getMessage(), 4);
                    }
                    this.destLastModified = fileResolveFile.lastModified();
                    File fileResolveFile2 = FILE_UTILS.resolveFile(directoryScanner.getBasedir(), includedFiles[i4]);
                    long jLastModified = fileResolveFile2.lastModified();
                    this.srcLastModified = jLastModified;
                    boolean z = this.forceOverwrite || this.destLastModified < jLastModified;
                    if (!z) {
                        for (int i5 = 0; i5 < 7; i5++) {
                            z = this.destLastModified < this.bundleLastModified[i5];
                            if (z) {
                                break;
                            }
                        }
                    }
                    if (z) {
                        log("Processing " + includedFiles[i4], 4);
                        translateOneFile(fileResolveFile2, fileResolveFile);
                        i3++;
                    } else {
                        log("Skipping " + includedFiles[i4] + " as destination file is up to date", 3);
                    }
                } catch (IOException e2) {
                    throw new BuildException(e2.getMessage(), getLocation());
                }
            }
            i2++;
            i = i3;
        }
        log("Translation performed on " + i + " file(s).", 4);
    }

    private void translateOneFile(File file, File file2) throws Throwable {
        BufferedWriter bufferedWriter;
        Throwable th;
        BufferedReader bufferedReader;
        String str;
        int length;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2), this.destEncoding));
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), this.srcEncoding));
                try {
                    LineTokenizer lineTokenizer = new LineTokenizer();
                    lineTokenizer.setIncludeDelims(true);
                    String token = lineTokenizer.getToken(bufferedReader);
                    while (token != null) {
                        int iIndexOf = token.indexOf(this.startToken);
                        while (iIndexOf >= 0 && this.startToken.length() + iIndexOf <= token.length()) {
                            int iIndexOf2 = token.indexOf(this.endToken, this.startToken.length() + iIndexOf);
                            if (iIndexOf2 < 0) {
                                length = iIndexOf + 1;
                            } else {
                                String strSubstring = token.substring(this.startToken.length() + iIndexOf, iIndexOf2);
                                boolean z = true;
                                for (int i = 0; i < strSubstring.length() && z; i++) {
                                    char cCharAt = strSubstring.charAt(i);
                                    if (cCharAt == ':' || cCharAt == '=' || Character.isSpaceChar(cCharAt)) {
                                        z = false;
                                    }
                                }
                                if (z) {
                                    if (this.resourceMap.containsKey(strSubstring)) {
                                        str = (String) this.resourceMap.get(strSubstring);
                                    } else {
                                        log("Replacement string missing for: " + strSubstring, 3);
                                        str = this.startToken + strSubstring + this.endToken;
                                    }
                                    token = token.substring(0, iIndexOf) + str + token.substring(iIndexOf2 + this.endToken.length());
                                    length = iIndexOf + str.length();
                                } else {
                                    length = iIndexOf + 1;
                                }
                            }
                            iIndexOf = token.indexOf(this.startToken, length);
                        }
                        bufferedWriter.write(token);
                        token = lineTokenizer.getToken(bufferedReader);
                    }
                    FileUtils.close(bufferedReader);
                    FileUtils.close(bufferedWriter);
                } catch (Throwable th2) {
                    th = th2;
                    FileUtils.close(bufferedReader);
                    FileUtils.close(bufferedWriter);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                bufferedReader = null;
            }
        } catch (Throwable th4) {
            bufferedWriter = null;
            th = th4;
            bufferedReader = null;
        }
    }
}
