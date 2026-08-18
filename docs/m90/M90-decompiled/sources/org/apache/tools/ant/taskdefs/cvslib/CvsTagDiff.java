package org.apache.tools.ant.taskdefs.cvslib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.AbstractCvsTask;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSSConstants;
import org.apache.tools.ant.util.CollectionUtils;
import org.apache.tools.ant.util.DOMElementWriter;
import org.apache.tools.ant.util.DOMUtils;
import org.apache.tools.ant.util.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: classes3.dex */
public class CvsTagDiff extends AbstractCvsTask {
    static final String FILE_HAS_CHANGED = " changed from revision ";
    static final String FILE_IS_NEW = " is new;";
    static final String FILE_STRING = "File ";
    static final String FILE_WAS_REMOVED = " is removed";
    static final String REVISION = "revision ";
    static final String TO_STRING = " to ";
    private File mydestfile;
    private String myendDate;
    private String myendTag;
    private String mypackage;
    private String mystartDate;
    private String mystartTag;
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final DOMElementWriter DOM_WRITER = new DOMElementWriter();
    static final int FILE_STRING_LENGTH = 5;
    private boolean ignoreRemoved = false;
    private List packageNames = new ArrayList();
    private String[] packageNamePrefixes = null;
    private int[] packageNamePrefixLengths = null;

    @Override // org.apache.tools.ant.taskdefs.AbstractCvsTask
    public void setPackage(String str) {
        this.mypackage = str;
    }

    public void setStartTag(String str) {
        this.mystartTag = str;
    }

    public void setStartDate(String str) {
        this.mystartDate = str;
    }

    public void setEndTag(String str) {
        this.myendTag = str;
    }

    public void setEndDate(String str) {
        this.myendDate = str;
    }

    public void setDestFile(File file) {
        this.mydestfile = file;
    }

    public void setIgnoreRemoved(boolean z) {
        this.ignoreRemoved = z;
    }

    @Override // org.apache.tools.ant.taskdefs.AbstractCvsTask, org.apache.tools.ant.Task
    public void execute() throws Throwable {
        File fileCreateTempFile;
        validate();
        addCommandArgument("rdiff");
        addCommandArgument("-s");
        if (this.mystartTag != null) {
            addCommandArgument("-r");
            addCommandArgument(this.mystartTag);
        } else {
            addCommandArgument(MSVSSConstants.FLAG_CODEDIFF);
            addCommandArgument(this.mystartDate);
        }
        if (this.myendTag != null) {
            addCommandArgument("-r");
            addCommandArgument(this.myendTag);
        } else {
            addCommandArgument(MSVSSConstants.FLAG_CODEDIFF);
            addCommandArgument(this.myendDate);
        }
        setCommand("");
        try {
            handlePackageNames();
            fileCreateTempFile = FILE_UTILS.createTempFile("cvstagdiff", ".log", null, true, true);
            try {
                setOutput(fileCreateTempFile);
                super.execute();
                writeTagDiff(parseRDiff(fileCreateTempFile));
                this.packageNamePrefixes = null;
                this.packageNamePrefixLengths = null;
                this.packageNames.clear();
                if (fileCreateTempFile != null) {
                    fileCreateTempFile.delete();
                }
            } catch (Throwable th) {
                th = th;
                this.packageNamePrefixes = null;
                this.packageNamePrefixLengths = null;
                this.packageNames.clear();
                if (fileCreateTempFile != null) {
                    fileCreateTempFile.delete();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            fileCreateTempFile = null;
        }
    }

    private CvsTagEntry[] parseRDiff(File file) throws Throwable {
        BufferedReader bufferedReader;
        BufferedReader bufferedReader2 = null;
        try {
            try {
                bufferedReader = new BufferedReader(new FileReader(file));
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException e) {
            e = e;
        }
        try {
            Vector vector = new Vector();
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                String strRemovePackageName = removePackageName(line, this.packageNamePrefixes, this.packageNamePrefixLengths);
                if (strRemovePackageName != null && !doFileIsNew(vector, strRemovePackageName) && !doFileHasChanged(vector, strRemovePackageName)) {
                    doFileWasRemoved(vector, strRemovePackageName);
                }
            }
            CvsTagEntry[] cvsTagEntryArr = new CvsTagEntry[vector.size()];
            vector.copyInto(cvsTagEntryArr);
            try {
                bufferedReader.close();
            } catch (IOException e2) {
                log(e2.toString(), 0);
            }
            return cvsTagEntryArr;
        } catch (IOException e3) {
            e = e3;
            throw new BuildException("Error in parsing", e);
        } catch (Throwable th2) {
            th = th2;
            bufferedReader2 = bufferedReader;
            if (bufferedReader2 != null) {
                try {
                    bufferedReader2.close();
                } catch (IOException e4) {
                    log(e4.toString(), 0);
                }
            }
            throw th;
        }
    }

    private boolean doFileIsNew(Vector vector, String str) {
        int iIndexOf = str.indexOf(FILE_IS_NEW);
        if (iIndexOf == -1) {
            return false;
        }
        String strSubstring = str.substring(0, iIndexOf);
        int iIndexOf2 = str.indexOf(REVISION, iIndexOf);
        CvsTagEntry cvsTagEntry = new CvsTagEntry(strSubstring, iIndexOf2 != -1 ? str.substring(iIndexOf2 + 9) : null);
        vector.addElement(cvsTagEntry);
        log(cvsTagEntry.toString(), 3);
        return true;
    }

    private boolean doFileHasChanged(Vector vector, String str) {
        int iIndexOf = str.indexOf(FILE_HAS_CHANGED);
        if (iIndexOf == -1) {
            return false;
        }
        String strSubstring = str.substring(0, iIndexOf);
        int iIndexOf2 = str.indexOf(TO_STRING, iIndexOf);
        CvsTagEntry cvsTagEntry = new CvsTagEntry(strSubstring, str.substring(iIndexOf2 + 4), str.substring(iIndexOf + 23, iIndexOf2));
        vector.addElement(cvsTagEntry);
        log(cvsTagEntry.toString(), 3);
        return true;
    }

    private boolean doFileWasRemoved(Vector vector, String str) {
        int iIndexOf;
        if (this.ignoreRemoved || (iIndexOf = str.indexOf(FILE_WAS_REMOVED)) == -1) {
            return false;
        }
        String strSubstring = str.substring(0, iIndexOf);
        int iIndexOf2 = str.indexOf(REVISION, iIndexOf);
        CvsTagEntry cvsTagEntry = new CvsTagEntry(strSubstring, null, iIndexOf2 != -1 ? str.substring(iIndexOf2 + 9) : null);
        vector.addElement(cvsTagEntry);
        log(cvsTagEntry.toString(), 3);
        return true;
    }

    private void writeTagDiff(CvsTagEntry[] cvsTagEntryArr) throws Throwable {
        FileOutputStream fileOutputStream;
        PrintWriter printWriter;
        FileOutputStream fileOutputStream2 = null;
        try {
            try {
                try {
                    fileOutputStream = new FileOutputStream(this.mydestfile);
                    try {
                        printWriter = new PrintWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));
                        printWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                        Document documentNewDocument = DOMUtils.newDocument();
                        Element elementCreateElement = documentNewDocument.createElement("tagdiff");
                        String str = this.mystartTag;
                        if (str != null) {
                            elementCreateElement.setAttribute("startTag", str);
                        } else {
                            elementCreateElement.setAttribute("startDate", this.mystartDate);
                        }
                        String str2 = this.myendTag;
                        if (str2 != null) {
                            elementCreateElement.setAttribute("endTag", str2);
                        } else {
                            elementCreateElement.setAttribute("endDate", this.myendDate);
                        }
                        elementCreateElement.setAttribute("cvsroot", getCvsRoot());
                        elementCreateElement.setAttribute("package", CollectionUtils.flattenToString(this.packageNames));
                        DOM_WRITER.openElement(elementCreateElement, printWriter, 0, "\t");
                        printWriter.println();
                        for (CvsTagEntry cvsTagEntry : cvsTagEntryArr) {
                            writeTagEntry(documentNewDocument, printWriter, cvsTagEntry);
                        }
                        DOM_WRITER.closeElement(elementCreateElement, printWriter, 0, "\t", true);
                        printWriter.flush();
                    } catch (UnsupportedEncodingException e) {
                        e = e;
                        fileOutputStream2 = fileOutputStream;
                        log(e.toString(), 0);
                        if (fileOutputStream2 == null) {
                            return;
                        } else {
                            fileOutputStream2.close();
                        }
                    } catch (IOException e2) {
                        e = e2;
                        throw new BuildException(e.toString(), e);
                    } catch (Throwable th) {
                        th = th;
                        fileOutputStream2 = fileOutputStream;
                        if (fileOutputStream2 != null) {
                            try {
                                fileOutputStream2.close();
                            } catch (IOException e3) {
                                log(e3.toString(), 0);
                            }
                        }
                        throw th;
                    }
                } catch (IOException e4) {
                    log(e4.toString(), 0);
                    return;
                }
            } catch (UnsupportedEncodingException e5) {
                e = e5;
            } catch (IOException e6) {
                e = e6;
            }
            if (printWriter.checkError()) {
                throw new IOException("Encountered an error writing tagdiff");
            }
            printWriter.close();
            fileOutputStream.close();
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private void writeTagEntry(Document document, PrintWriter printWriter, CvsTagEntry cvsTagEntry) throws IOException {
        Element elementCreateElement = document.createElement("entry");
        Element elementCreateChildElement = DOMUtils.createChildElement(elementCreateElement, "file");
        DOMUtils.appendCDATAElement(elementCreateChildElement, "name", cvsTagEntry.getFile());
        if (cvsTagEntry.getRevision() != null) {
            DOMUtils.appendTextElement(elementCreateChildElement, "revision", cvsTagEntry.getRevision());
        }
        if (cvsTagEntry.getPreviousRevision() != null) {
            DOMUtils.appendTextElement(elementCreateChildElement, "prevrevision", cvsTagEntry.getPreviousRevision());
        }
        DOM_WRITER.write(elementCreateElement, printWriter, 1, "\t");
    }

    private void validate() throws BuildException {
        if (this.mypackage == null && getModules().size() == 0) {
            throw new BuildException("Package/module must be set.");
        }
        if (this.mydestfile == null) {
            throw new BuildException("Destfile must be set.");
        }
        String str = this.mystartTag;
        if (str == null && this.mystartDate == null) {
            throw new BuildException("Start tag or start date must be set.");
        }
        if (str != null && this.mystartDate != null) {
            throw new BuildException("Only one of start tag and start date must be set.");
        }
        String str2 = this.myendTag;
        if (str2 == null && this.myendDate == null) {
            throw new BuildException("End tag or end date must be set.");
        }
        if (str2 != null && this.myendDate != null) {
            throw new BuildException("Only one of end tag and end date must be set.");
        }
    }

    private void handlePackageNames() {
        if (this.mypackage != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(this.mypackage);
            while (stringTokenizer.hasMoreTokens()) {
                String strNextToken = stringTokenizer.nextToken();
                this.packageNames.add(strNextToken);
                addCommandArgument(strNextToken);
            }
        }
        Iterator<AbstractCvsTask.Module> it = getModules().iterator();
        while (it.hasNext()) {
            this.packageNames.add(it.next().getName());
        }
        this.packageNamePrefixes = new String[this.packageNames.size()];
        this.packageNamePrefixLengths = new int[this.packageNames.size()];
        int i = 0;
        while (true) {
            String[] strArr = this.packageNamePrefixes;
            if (i >= strArr.length) {
                return;
            }
            strArr[i] = FILE_STRING + this.packageNames.get(i) + "/";
            this.packageNamePrefixLengths[i] = this.packageNamePrefixes[i].length();
            i++;
        }
    }

    private static String removePackageName(String str, String[] strArr, int[] iArr) {
        if (str.length() < FILE_STRING_LENGTH) {
            return null;
        }
        boolean z = false;
        int i = 0;
        while (true) {
            if (i >= strArr.length) {
                break;
            }
            if (str.startsWith(strArr[i])) {
                str = str.substring(iArr[i]);
                z = true;
                break;
            }
            i++;
        }
        return !z ? str.substring(FILE_STRING_LENGTH) : str;
    }
}
