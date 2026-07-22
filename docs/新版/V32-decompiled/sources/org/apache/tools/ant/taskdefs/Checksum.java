package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.Restrict;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.types.resources.selectors.Type;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.StringUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Checksum extends MatchingTask implements Condition {
    private static final int BUFFER_SIZE = 8192;
    private static final int BYTE_MASK = 255;
    private static final int NIBBLE = 4;
    private static final int WORD = 16;
    private String fileext;
    private boolean forceOverwrite;
    private boolean isCondition;
    private MessageDigest messageDigest;
    private String property;
    private File todir;
    private String totalproperty;
    private String verifyProperty;
    private File file = null;
    private String algorithm = "MD5";
    private String provider = null;
    private Map<File, byte[]> allDigests = new HashMap();
    private Map<File, String> relativeFilePaths = new HashMap();
    private FileUnion resources = null;
    private Hashtable<File, Object> includeFileMap = new Hashtable<>();
    private int readBufferSize = 8192;
    private MessageFormat format = FormatElement.getDefault().getFormat();

    private static class FileUnion extends Restrict {
        private Union u;

        FileUnion() {
            Union union = new Union();
            this.u = union;
            super.add(union);
            super.add(Type.FILE);
        }

        @Override // org.apache.tools.ant.types.resources.Restrict
        public void add(ResourceCollection resourceCollection) {
            this.u.add(resourceCollection);
        }
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setTodir(File file) {
        this.todir = file;
    }

    public void setAlgorithm(String str) {
        this.algorithm = str;
    }

    public void setProvider(String str) {
        this.provider = str;
    }

    public void setFileext(String str) {
        this.fileext = str;
    }

    public void setProperty(String str) {
        this.property = str;
    }

    public void setTotalproperty(String str) {
        this.totalproperty = str;
    }

    public void setVerifyproperty(String str) {
        this.verifyProperty = str;
    }

    public void setForceOverwrite(boolean z) {
        this.forceOverwrite = z;
    }

    public void setReadBufferSize(int i) {
        this.readBufferSize = i;
    }

    public void setFormat(FormatElement formatElement) {
        this.format = formatElement.getFormat();
    }

    public void setPattern(String str) {
        this.format = new MessageFormat(str);
    }

    public void addFileset(FileSet fileSet) {
        add(fileSet);
    }

    public void add(ResourceCollection resourceCollection) {
        if (resourceCollection == null) {
            return;
        }
        FileUnion fileUnion = this.resources;
        if (fileUnion == null) {
            fileUnion = new FileUnion();
        }
        this.resources = fileUnion;
        fileUnion.add(resourceCollection);
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        this.isCondition = false;
        boolean zValidateAndExecute = validateAndExecute();
        if (this.verifyProperty != null) {
            getProject().setNewProperty(this.verifyProperty, (zValidateAndExecute ? Boolean.TRUE : Boolean.FALSE).toString());
        }
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() throws BuildException {
        this.isCondition = true;
        return validateAndExecute();
    }

    private boolean validateAndExecute() throws BuildException {
        FileUnion fileUnion;
        String str = this.fileext;
        if (this.file == null && ((fileUnion = this.resources) == null || fileUnion.size() == 0)) {
            throw new BuildException("Specify at least one source - a file or a resource collection.");
        }
        FileUnion fileUnion2 = this.resources;
        if (fileUnion2 != null && !fileUnion2.isFilesystemOnly()) {
            throw new BuildException("Can only calculate checksums for file-based resources.");
        }
        File file = this.file;
        if (file != null && file.exists() && this.file.isDirectory()) {
            throw new BuildException("Checksum cannot be generated for directories");
        }
        if (this.file != null && this.totalproperty != null) {
            throw new BuildException("File and Totalproperty cannot co-exist.");
        }
        String str2 = this.property;
        if (str2 != null && this.fileext != null) {
            throw new BuildException("Property and FileExt cannot co-exist.");
        }
        if (str2 != null) {
            if (this.forceOverwrite) {
                throw new BuildException("ForceOverwrite cannot be used when Property is specified");
            }
            FileUnion fileUnion3 = this.resources;
            int size = fileUnion3 != null ? 0 + fileUnion3.size() : 0;
            if (this.file != null) {
                size++;
            }
            if (size > 1) {
                throw new BuildException("Multiple files cannot be used when Property is specified");
            }
        }
        String str3 = this.verifyProperty;
        if (str3 != null) {
            this.isCondition = true;
        }
        if (str3 != null && this.forceOverwrite) {
            throw new BuildException("VerifyProperty and ForceOverwrite cannot co-exist.");
        }
        if (this.isCondition && this.forceOverwrite) {
            throw new BuildException("ForceOverwrite cannot be used when conditions are being used.");
        }
        this.messageDigest = null;
        String str4 = this.provider;
        if (str4 != null) {
            try {
                this.messageDigest = MessageDigest.getInstance(this.algorithm, str4);
            } catch (NoSuchAlgorithmException e) {
                throw new BuildException(e, getLocation());
            } catch (NoSuchProviderException e2) {
                throw new BuildException(e2, getLocation());
            }
        } else {
            try {
                this.messageDigest = MessageDigest.getInstance(this.algorithm);
            } catch (NoSuchAlgorithmException e3) {
                throw new BuildException(e3, getLocation());
            }
        }
        if (this.messageDigest == null) {
            throw new BuildException("Unable to create Message Digest", getLocation());
        }
        String str5 = this.fileext;
        if (str5 == null) {
            this.fileext = "." + this.algorithm;
        } else if (str5.trim().length() == 0) {
            throw new BuildException("File extension when specified must not be an empty string");
        }
        try {
            FileUnion fileUnion4 = this.resources;
            if (fileUnion4 != null) {
                for (Resource resource : fileUnion4) {
                    File file2 = ((FileProvider) resource.as(FileProvider.class)).getFile();
                    if (this.totalproperty != null || this.todir != null) {
                        this.relativeFilePaths.put(file2, resource.getName().replace(File.separatorChar, '/'));
                    }
                    addToIncludeFileMap(file2);
                }
            }
            File file3 = this.file;
            if (file3 != null) {
                if (this.totalproperty != null || this.todir != null) {
                    this.relativeFilePaths.put(file3, file3.getName().replace(File.separatorChar, '/'));
                }
                addToIncludeFileMap(this.file);
            }
            return generateChecksums();
        } finally {
            this.fileext = str;
            this.includeFileMap.clear();
        }
    }

    private void addToIncludeFileMap(File file) throws BuildException {
        if (file.exists()) {
            String str = this.property;
            if (str == null) {
                File checksumFile = getChecksumFile(file);
                if (this.forceOverwrite || this.isCondition || file.lastModified() > checksumFile.lastModified()) {
                    this.includeFileMap.put(file, checksumFile);
                    return;
                }
                log(file + " omitted as " + checksumFile + " is up to date.", 3);
                if (this.totalproperty != null) {
                    this.allDigests.put(file, decodeHex(readChecksum(checksumFile).toCharArray()));
                    return;
                }
                return;
            }
            this.includeFileMap.put(file, str);
            return;
        }
        String str2 = "Could not find file " + file.getAbsolutePath() + " to generate checksum for.";
        log(str2);
        throw new BuildException(str2, getLocation());
    }

    private File getChecksumFile(File file) {
        File parentFile;
        if (this.todir != null) {
            parentFile = new File(this.todir, getRelativeFilePath(file)).getParentFile();
            parentFile.mkdirs();
        } else {
            parentFile = file.getParentFile();
        }
        return new File(parentFile, file.getName() + this.fileext);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean generateChecksums() throws Throwable {
        FileOutputStream fileOutputStream;
        Iterator<Map.Entry<File, Object>> it;
        boolean z;
        int i;
        byte[] bArr = new byte[this.readBufferSize];
        FileInputStream fileInputStream = null;
        try {
            it = this.includeFileMap.entrySet().iterator();
        } catch (Exception e) {
            e = e;
            fileOutputStream = null;
        } catch (Throwable th) {
            th = th;
            fileOutputStream = null;
        }
        loop0: while (true) {
            z = 1;
            while (true) {
                i = 0;
                if (!it.hasNext()) {
                    break loop0;
                }
                Map.Entry<File, Object> next = it.next();
                this.messageDigest.reset();
                File key = next.getKey();
                if (!this.isCondition) {
                    log("Calculating " + this.algorithm + " checksum for " + key, 3);
                }
                FileInputStream fileInputStream2 = new FileInputStream(key);
                try {
                    DigestInputStream digestInputStream = new DigestInputStream(fileInputStream2, this.messageDigest);
                    do {
                    } while (digestInputStream.read(bArr, 0, this.readBufferSize) != -1);
                    digestInputStream.close();
                    fileInputStream2.close();
                    byte[] bArrDigest = this.messageDigest.digest();
                    if (this.totalproperty != null) {
                        this.allDigests.put(key, bArrDigest);
                    }
                    String strCreateDigestString = createDigestString(bArrDigest);
                    Object value = next.getValue();
                    if (value instanceof String) {
                        String str = (String) value;
                        if (this.isCondition) {
                            if (z != 0 && strCreateDigestString.equals(this.property)) {
                                break;
                            }
                            z = i;
                        } else {
                            getProject().setNewProperty(str, strCreateDigestString);
                        }
                    } else if (!(value instanceof File)) {
                        continue;
                    } else if (this.isCondition) {
                        File file = (File) value;
                        if (file.exists()) {
                            try {
                                String checksum = readChecksum(file);
                                if (z != 0 && strCreateDigestString.equals(checksum)) {
                                    i = 1;
                                }
                            } catch (BuildException unused) {
                            }
                        }
                        z = i;
                    } else {
                        File file2 = (File) value;
                        fileOutputStream = new FileOutputStream(file2);
                        try {
                            try {
                                fileOutputStream.write(this.format.format(new Object[]{strCreateDigestString, key.getName(), FileUtils.getRelativePath(file2.getParentFile(), key), FileUtils.getRelativePath(getProject().getBaseDir(), key), key.getAbsolutePath()}).getBytes());
                                fileOutputStream.write(StringUtils.LINE_SEP.getBytes());
                                fileOutputStream.close();
                            } catch (Exception e2) {
                                e = e2;
                                throw new BuildException(e, getLocation());
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            FileUtils.close(fileInputStream);
                            FileUtils.close(fileOutputStream);
                            throw th;
                        }
                    }
                } catch (Exception e3) {
                    e = e3;
                    fileOutputStream = null;
                    fileInputStream = fileInputStream2;
                } catch (Throwable th3) {
                    th = th3;
                    fileOutputStream = null;
                    fileInputStream = fileInputStream2;
                    FileUtils.close(fileInputStream);
                    FileUtils.close(fileOutputStream);
                    throw th;
                }
                throw new BuildException(e, getLocation());
            }
        }
        if (this.totalproperty != null) {
            File[] fileArr = (File[]) this.allDigests.keySet().toArray(new File[this.allDigests.size()]);
            Arrays.sort(fileArr, new Comparator<File>() { // from class: org.apache.tools.ant.taskdefs.Checksum.1
                @Override // java.util.Comparator
                public int compare(File file3, File file4) {
                    if (file3 == null) {
                        return file4 == null ? 0 : -1;
                    }
                    if (file4 == null) {
                        return 1;
                    }
                    return Checksum.this.getRelativeFilePath(file3).compareTo(Checksum.this.getRelativeFilePath(file4));
                }
            });
            this.messageDigest.reset();
            int length = fileArr.length;
            while (i < length) {
                File file3 = fileArr[i];
                this.messageDigest.update(this.allDigests.get(file3));
                this.messageDigest.update(getRelativeFilePath(file3).getBytes());
                i++;
            }
            getProject().setNewProperty(this.totalproperty, createDigestString(this.messageDigest.digest()));
        }
        FileUtils.close((InputStream) null);
        FileUtils.close((OutputStream) null);
        return z;
    }

    private String createDigestString(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() < 2) {
                stringBuffer.append("0");
            }
            stringBuffer.append(hexString);
        }
        return stringBuffer.toString();
    }

    public static byte[] decodeHex(char[] cArr) throws BuildException {
        int length = cArr.length;
        if ((length & 1) != 0) {
            throw new BuildException("odd number of characters.");
        }
        byte[] bArr = new byte[length >> 1];
        int i = 0;
        int i2 = 0;
        while (i < length) {
            int i3 = i + 1;
            bArr[i2] = (byte) (((Character.digit(cArr[i], 16) << 4) | Character.digit(cArr[i3], 16)) & 255);
            i2++;
            i = i3 + 1;
        }
        return bArr;
    }

    /* JADX WARN: Not initialized variable reg: 2, insn: 0x006e: MOVE (r1 I:??[OBJECT, ARRAY]) = (r2 I:??[OBJECT, ARRAY]), block:B:30:0x006e */
    private String readChecksum(File file) throws Throwable {
        ParseException e;
        IOException e2;
        Reader reader;
        Reader reader2 = null;
        try {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                try {
                    Object[] objArr = this.format.parse(bufferedReader.readLine());
                    if (objArr == null || objArr.length == 0 || objArr[0] == null) {
                        throw new BuildException("failed to find a checksum");
                    }
                    String str = (String) objArr[0];
                    FileUtils.close(bufferedReader);
                    return str;
                } catch (IOException e3) {
                    e2 = e3;
                    throw new BuildException("Couldn't read checksum file " + file, e2);
                } catch (ParseException e4) {
                    e = e4;
                    throw new BuildException("Couldn't read checksum file " + file, e);
                }
            } catch (Throwable th) {
                th = th;
                reader2 = reader;
                FileUtils.close(reader2);
                throw th;
            }
        } catch (IOException e5) {
            e2 = e5;
        } catch (ParseException e6) {
            e = e6;
        } catch (Throwable th2) {
            th = th2;
            FileUtils.close(reader2);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getRelativeFilePath(File file) {
        String str = this.relativeFilePaths.get(file);
        if (str != null) {
            return str;
        }
        throw new BuildException("Internal error: relativeFilePaths could not match file " + file + "\nplease file a bug report on this");
    }

    public static class FormatElement extends EnumeratedAttribute {
        private static final String CHECKSUM = "CHECKSUM";
        private static final String MD5SUM = "MD5SUM";
        private static final String SVF = "SVF";
        private static HashMap<String, MessageFormat> formatMap;

        static {
            HashMap<String, MessageFormat> map = new HashMap<>();
            formatMap = map;
            map.put(CHECKSUM, new MessageFormat("{0}"));
            formatMap.put(MD5SUM, new MessageFormat("{0} *{1}"));
            formatMap.put(SVF, new MessageFormat("MD5 ({1}) = {0}"));
        }

        public static FormatElement getDefault() {
            FormatElement formatElement = new FormatElement();
            formatElement.setValue(CHECKSUM);
            return formatElement;
        }

        public MessageFormat getFormat() {
            return formatMap.get(getValue());
        }

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{CHECKSUM, MD5SUM, SVF};
        }
    }
}
