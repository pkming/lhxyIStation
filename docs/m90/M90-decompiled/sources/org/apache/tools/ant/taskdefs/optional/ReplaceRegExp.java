package org.apache.tools.ant.taskdefs.optional;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSSConstants;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.Substitution;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpUtil;

/* JADX INFO: loaded from: classes3.dex */
public class ReplaceRegExp extends Task {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private Union resources;
    private boolean preserveLastModified = false;
    private String encoding = null;
    private File file = null;
    private String flags = "";
    private boolean byline = false;
    private RegularExpression regex = null;
    private Substitution subs = null;

    public void setFile(File file) {
        this.file = file;
    }

    public void setMatch(String str) {
        if (this.regex != null) {
            throw new BuildException("Only one regular expression is allowed");
        }
        RegularExpression regularExpression = new RegularExpression();
        this.regex = regularExpression;
        regularExpression.setPattern(str);
    }

    public void setReplace(String str) {
        if (this.subs != null) {
            throw new BuildException("Only one substitution expression is allowed");
        }
        Substitution substitution = new Substitution();
        this.subs = substitution;
        substitution.setExpression(str);
    }

    public void setFlags(String str) {
        this.flags = str;
    }

    @Deprecated
    public void setByLine(String str) {
        Boolean boolValueOf = Boolean.valueOf(str);
        if (boolValueOf == null) {
            boolValueOf = Boolean.FALSE;
        }
        this.byline = boolValueOf.booleanValue();
    }

    public void setByLine(boolean z) {
        this.byline = z;
    }

    public void setEncoding(String str) {
        this.encoding = str;
    }

    public void addFileset(FileSet fileSet) {
        addConfigured(fileSet);
    }

    public void addConfigured(ResourceCollection resourceCollection) {
        if (!resourceCollection.isFilesystemOnly()) {
            throw new BuildException("only filesystem resources are supported");
        }
        if (this.resources == null) {
            this.resources = new Union();
        }
        this.resources.add(resourceCollection);
    }

    public RegularExpression createRegexp() {
        if (this.regex != null) {
            throw new BuildException("Only one regular expression is allowed.");
        }
        RegularExpression regularExpression = new RegularExpression();
        this.regex = regularExpression;
        return regularExpression;
    }

    public Substitution createSubstitution() {
        if (this.subs != null) {
            throw new BuildException("Only one substitution expression is allowed");
        }
        Substitution substitution = new Substitution();
        this.subs = substitution;
        return substitution;
    }

    public void setPreserveLastModified(boolean z) {
        this.preserveLastModified = z;
    }

    protected String doReplace(RegularExpression regularExpression, Substitution substitution, String str, int i) {
        Regexp regexp = regularExpression.getRegexp(getProject());
        if (!regexp.matches(str, i)) {
            return str;
        }
        log("Found match; substituting", 4);
        return regexp.substitute(str, substitution.getExpression(getProject()), i);
    }

    protected void doReplace(File file, int i) throws IOException {
        boolean zMultilineReplace;
        int i2;
        File fileCreateTempFile = FILE_UTILS.createTempFile(MSVSSConstants.WRITABLE_REPLACE, ".txt", null, true, true);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                Reader inputStreamReader = this.encoding != null ? new InputStreamReader(fileInputStream, this.encoding) : new InputStreamReader(fileInputStream);
                FileOutputStream fileOutputStream = new FileOutputStream(fileCreateTempFile);
                try {
                    Writer outputStreamWriter = this.encoding != null ? new OutputStreamWriter(fileOutputStream, this.encoding) : new OutputStreamWriter(fileOutputStream);
                    log("Replacing pattern '" + this.regex.getPattern(getProject()) + "' with '" + this.subs.getExpression(getProject()) + "' in '" + file.getPath() + "'" + (this.byline ? " by line" : "") + (this.flags.length() > 0 ? " with flags: '" + this.flags + "'" : "") + ".", 3);
                    if (this.byline) {
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                        StringBuffer stringBuffer = new StringBuffer();
                        boolean z = false;
                        zMultilineReplace = false;
                        do {
                            i2 = bufferedReader.read();
                            if (i2 == 13) {
                                if (z) {
                                    zMultilineReplace |= replaceAndWrite(stringBuffer.toString(), bufferedWriter, i);
                                    bufferedWriter.write(13);
                                    stringBuffer = new StringBuffer();
                                } else {
                                    z = true;
                                }
                            } else if (i2 == 10) {
                                zMultilineReplace |= replaceAndWrite(stringBuffer.toString(), bufferedWriter, i);
                                if (z) {
                                    bufferedWriter.write(13);
                                    z = false;
                                }
                                bufferedWriter.write(10);
                                stringBuffer = new StringBuffer();
                            } else {
                                if (z || i2 < 0) {
                                    zMultilineReplace |= replaceAndWrite(stringBuffer.toString(), bufferedWriter, i);
                                    if (z) {
                                        bufferedWriter.write(13);
                                        z = false;
                                    }
                                    stringBuffer = new StringBuffer();
                                }
                                if (i2 >= 0) {
                                    stringBuffer.append((char) i2);
                                }
                            }
                        } while (i2 >= 0);
                        outputStreamWriter = bufferedWriter;
                        inputStreamReader = bufferedReader;
                    } else {
                        zMultilineReplace = multilineReplace(inputStreamReader, outputStreamWriter, i);
                    }
                    inputStreamReader.close();
                    outputStreamWriter.close();
                    if (zMultilineReplace) {
                        log("File has changed; saving the updated file", 3);
                        try {
                            long jLastModified = file.lastModified();
                            FileUtils fileUtils = FILE_UTILS;
                            fileUtils.rename(fileCreateTempFile, file);
                            if (this.preserveLastModified) {
                                fileUtils.setFileLastModified(file, jLastModified);
                            }
                            fileCreateTempFile = null;
                        } catch (IOException e) {
                            throw new BuildException("Couldn't rename temporary file " + fileCreateTempFile, e, getLocation());
                        }
                    } else {
                        log("No change made", 4);
                    }
                } finally {
                    fileOutputStream.close();
                }
            } finally {
                fileInputStream.close();
            }
        } finally {
            if (fileCreateTempFile != null) {
                fileCreateTempFile.delete();
            }
        }
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (this.regex == null) {
            throw new BuildException("No expression to match.");
        }
        if (this.subs == null) {
            throw new BuildException("Nothing to replace expression with.");
        }
        if (this.file != null && this.resources != null) {
            throw new BuildException("You cannot supply the 'file' attribute and resource collections at the same time.");
        }
        int iAsOptions = RegexpUtil.asOptions(this.flags);
        File file = this.file;
        if (file != null && file.exists()) {
            try {
                doReplace(this.file, iAsOptions);
            } catch (IOException e) {
                log("An error occurred processing file: '" + this.file.getAbsolutePath() + "': " + e.toString(), 0);
            }
        } else if (this.file != null) {
            log("The following file is missing: '" + this.file.getAbsolutePath() + "'", 0);
        }
        Union union = this.resources;
        if (union != null) {
            Iterator<Resource> it = union.iterator();
            while (it.hasNext()) {
                File file2 = ((FileProvider) it.next().as(FileProvider.class)).getFile();
                if (file2.exists()) {
                    try {
                        doReplace(file2, iAsOptions);
                    } catch (Exception e2) {
                        log("An error occurred processing file: '" + file2.getAbsolutePath() + "': " + e2.toString(), 0);
                    }
                } else {
                    log("The following file is missing: '" + file2.getAbsolutePath() + "'", 0);
                }
            }
        }
    }

    private boolean multilineReplace(Reader reader, Writer writer, int i) throws IOException {
        return replaceAndWrite(FileUtils.safeReadFully(reader), writer, i);
    }

    private boolean replaceAndWrite(String str, Writer writer, int i) throws IOException {
        writer.write(doReplace(this.regex, this.subs, str, i));
        return !r5.equals(str);
    }
}
