package org.apache.tools.ant.taskdefs.optional.vss;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.GregorianCalendar;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public abstract class MSVSS extends Task implements MSVSSConstants {
    private String ssDir = null;
    private String vssLogin = null;
    private String vssPath = null;
    private String serverPath = null;
    private String version = null;
    private String date = null;
    private String label = null;
    private String autoResponse = null;
    private String localPath = null;
    private String comment = null;
    private String fromLabel = null;
    private String toLabel = null;
    private String outputFileName = null;
    private String user = null;
    private String fromDate = null;
    private String toDate = null;
    private String style = null;
    private boolean quiet = false;
    private boolean recursive = false;
    private boolean writable = false;
    private boolean failOnError = true;
    private boolean getLocalCopy = true;
    private int numDays = Integer.MIN_VALUE;
    private DateFormat dateFormat = DateFormat.getDateInstance(3);
    private CurrentModUpdated timestamp = null;
    private WritableFiles writableFiles = null;

    abstract Commandline buildCmdLine();

    public final void setSsdir(String str) {
        this.ssDir = FileUtils.translatePath(str);
    }

    public final void setLogin(String str) {
        this.vssLogin = str;
    }

    public final void setVsspath(String str) {
        if (str.startsWith("vss://")) {
            str = str.substring(5);
        }
        if (str.startsWith("$")) {
            this.vssPath = str;
        } else {
            this.vssPath = "$" + str;
        }
    }

    public final void setServerpath(String str) {
        this.serverPath = str;
    }

    public final void setFailOnError(boolean z) {
        this.failOnError = z;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        Commandline commandlineBuildCmdLine = buildCmdLine();
        int iRun = run(commandlineBuildCmdLine);
        if (Execute.isFailure(iRun) && getFailOnError()) {
            throw new BuildException("Failed executing: " + formatCommandLine(commandlineBuildCmdLine) + " With a return code of " + iRun, getLocation());
        }
    }

    protected void setInternalComment(String str) {
        this.comment = str;
    }

    protected void setInternalAutoResponse(String str) {
        this.autoResponse = str;
    }

    protected void setInternalDate(String str) {
        this.date = str;
    }

    protected void setInternalDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    protected void setInternalFailOnError(boolean z) {
        this.failOnError = z;
    }

    protected void setInternalFromDate(String str) {
        this.fromDate = str;
    }

    protected void setInternalFromLabel(String str) {
        this.fromLabel = str;
    }

    protected void setInternalLabel(String str) {
        this.label = str;
    }

    protected void setInternalLocalPath(String str) {
        this.localPath = str;
    }

    protected void setInternalNumDays(int i) {
        this.numDays = i;
    }

    protected void setInternalOutputFilename(String str) {
        this.outputFileName = str;
    }

    protected void setInternalQuiet(boolean z) {
        this.quiet = z;
    }

    protected void setInternalRecursive(boolean z) {
        this.recursive = z;
    }

    protected void setInternalStyle(String str) {
        this.style = str;
    }

    protected void setInternalToDate(String str) {
        this.toDate = str;
    }

    protected void setInternalToLabel(String str) {
        this.toLabel = str;
    }

    protected void setInternalUser(String str) {
        this.user = str;
    }

    protected void setInternalVersion(String str) {
        this.version = str;
    }

    protected void setInternalWritable(boolean z) {
        this.writable = z;
    }

    protected void setInternalFileTimeStamp(CurrentModUpdated currentModUpdated) {
        this.timestamp = currentModUpdated;
    }

    protected void setInternalWritableFiles(WritableFiles writableFiles) {
        this.writableFiles = writableFiles;
    }

    protected void setInternalGetLocalCopy(boolean z) {
        this.getLocalCopy = z;
    }

    protected String getSSCommand() {
        StringBuilder sbAppend;
        String str;
        String str2 = this.ssDir;
        if (str2 == null) {
            return MSVSSConstants.SS_EXE;
        }
        if (str2.endsWith(File.separator)) {
            sbAppend = new StringBuilder();
            str = this.ssDir;
        } else {
            sbAppend = new StringBuilder().append(this.ssDir);
            str = File.separator;
        }
        return sbAppend.append(str).append(MSVSSConstants.SS_EXE).toString();
    }

    protected String getVsspath() {
        return this.vssPath;
    }

    protected String getQuiet() {
        return this.quiet ? MSVSSConstants.FLAG_QUIET : "";
    }

    protected String getRecursive() {
        return this.recursive ? MSVSSConstants.FLAG_RECURSION : "";
    }

    protected String getWritable() {
        return this.writable ? MSVSSConstants.FLAG_WRITABLE : "";
    }

    protected String getLabel() {
        String str = this.label;
        return (str == null || str.length() <= 0) ? "" : MSVSSConstants.FLAG_LABEL + getShortLabel();
    }

    private String getShortLabel() {
        String str = this.label;
        if (str != null && str.length() > 31) {
            String strSubstring = this.label.substring(0, 30);
            log("Label is longer than 31 characters, truncated to: " + strSubstring, 1);
            return strSubstring;
        }
        return this.label;
    }

    protected String getStyle() {
        String str = this.style;
        return str != null ? str : "";
    }

    protected String getVersionDateLabel() {
        if (this.version != null) {
            return MSVSSConstants.FLAG_VERSION + this.version;
        }
        if (this.date != null) {
            return MSVSSConstants.FLAG_VERSION_DATE + this.date;
        }
        String shortLabel = getShortLabel();
        return (shortLabel == null || shortLabel.equals("")) ? "" : MSVSSConstants.FLAG_VERSION_LABEL + shortLabel;
    }

    protected String getVersion() {
        return this.version != null ? MSVSSConstants.FLAG_VERSION + this.version : "";
    }

    protected String getLocalpath() {
        if (this.localPath == null) {
            return "";
        }
        File fileResolveFile = getProject().resolveFile(this.localPath);
        if (!fileResolveFile.exists()) {
            if (!fileResolveFile.mkdirs()) {
                throw new BuildException("Directory " + this.localPath + " creation was not successful for an unknown reason", getLocation());
            }
            getProject().log("Created dir: " + fileResolveFile.getAbsolutePath());
        }
        return MSVSSConstants.FLAG_OVERRIDE_WORKING_DIR + this.localPath;
    }

    protected String getComment() {
        return this.comment != null ? MSVSSConstants.FLAG_COMMENT + this.comment : "-C-";
    }

    protected String getAutoresponse() {
        String str = this.autoResponse;
        return str == null ? MSVSSConstants.FLAG_AUTORESPONSE_DEF : str.equalsIgnoreCase("Y") ? MSVSSConstants.FLAG_AUTORESPONSE_YES : this.autoResponse.equalsIgnoreCase("N") ? MSVSSConstants.FLAG_AUTORESPONSE_NO : MSVSSConstants.FLAG_AUTORESPONSE_DEF;
    }

    protected String getLogin() {
        return this.vssLogin != null ? "-Y" + this.vssLogin : "";
    }

    protected String getOutput() {
        return this.outputFileName != null ? MSVSSConstants.FLAG_OUTPUT + this.outputFileName : "";
    }

    protected String getUser() {
        return this.user != null ? MSVSSConstants.FLAG_USER + this.user : "";
    }

    protected String getVersionLabel() {
        String str = this.fromLabel;
        if (str == null && this.toLabel == null) {
            return "";
        }
        if (str != null && this.toLabel != null) {
            if (str.length() > 31) {
                this.fromLabel = this.fromLabel.substring(0, 30);
                log("FromLabel is longer than 31 characters, truncated to: " + this.fromLabel, 1);
            }
            if (this.toLabel.length() > 31) {
                this.toLabel = this.toLabel.substring(0, 30);
                log("ToLabel is longer than 31 characters, truncated to: " + this.toLabel, 1);
            }
            return MSVSSConstants.FLAG_VERSION_LABEL + this.toLabel + MSVSSConstants.VALUE_FROMLABEL + this.fromLabel;
        }
        if (str != null) {
            if (str.length() > 31) {
                this.fromLabel = this.fromLabel.substring(0, 30);
                log("FromLabel is longer than 31 characters, truncated to: " + this.fromLabel, 1);
            }
            return "-V~L" + this.fromLabel;
        }
        if (this.toLabel.length() > 31) {
            this.toLabel = this.toLabel.substring(0, 30);
            log("ToLabel is longer than 31 characters, truncated to: " + this.toLabel, 1);
        }
        return MSVSSConstants.FLAG_VERSION_LABEL + this.toLabel;
    }

    protected String getVersionDate() throws BuildException {
        StringBuilder sbAppend;
        String str;
        String str2 = this.fromDate;
        if (str2 == null && this.toDate == null && this.numDays == Integer.MIN_VALUE) {
            return "";
        }
        if (str2 != null && this.toDate != null) {
            return MSVSSConstants.FLAG_VERSION_DATE + this.toDate + MSVSSConstants.VALUE_FROMDATE + this.fromDate;
        }
        if (this.toDate != null && this.numDays != Integer.MIN_VALUE) {
            try {
                return MSVSSConstants.FLAG_VERSION_DATE + this.toDate + MSVSSConstants.VALUE_FROMDATE + calcDate(this.toDate, this.numDays);
            } catch (ParseException unused) {
                throw new BuildException("Error parsing date: " + this.toDate, getLocation());
            }
        }
        if (str2 != null && this.numDays != Integer.MIN_VALUE) {
            try {
                return MSVSSConstants.FLAG_VERSION_DATE + calcDate(this.fromDate, this.numDays) + MSVSSConstants.VALUE_FROMDATE + this.fromDate;
            } catch (ParseException unused2) {
                throw new BuildException("Error parsing date: " + this.fromDate, getLocation());
            }
        }
        if (str2 != null) {
            sbAppend = new StringBuilder().append("-V~d");
            str = this.fromDate;
        } else {
            sbAppend = new StringBuilder().append(MSVSSConstants.FLAG_VERSION_DATE);
            str = this.toDate;
        }
        return sbAppend.append(str).toString();
    }

    protected String getGetLocalCopy() {
        return !this.getLocalCopy ? MSVSSConstants.FLAG_NO_GET : "";
    }

    private boolean getFailOnError() {
        if (getWritableFiles().equals(MSVSSConstants.WRITABLE_SKIP)) {
            return false;
        }
        return this.failOnError;
    }

    public String getFileTimeStamp() {
        CurrentModUpdated currentModUpdated = this.timestamp;
        return currentModUpdated == null ? "" : currentModUpdated.getValue().equals("modified") ? MSVSSConstants.FLAG_FILETIME_MODIFIED : this.timestamp.getValue().equals(MSVSSConstants.TIME_UPDATED) ? MSVSSConstants.FLAG_FILETIME_UPDATED : MSVSSConstants.FLAG_FILETIME_DEF;
    }

    public String getWritableFiles() {
        WritableFiles writableFiles = this.writableFiles;
        if (writableFiles == null) {
            return "";
        }
        if (writableFiles.getValue().equals(MSVSSConstants.WRITABLE_REPLACE)) {
            return MSVSSConstants.FLAG_REPLACE_WRITABLE;
        }
        if (!this.writableFiles.getValue().equals(MSVSSConstants.WRITABLE_SKIP)) {
            return "";
        }
        this.failOnError = false;
        return MSVSSConstants.FLAG_SKIP_WRITABLE;
    }

    private int run(Commandline commandline) {
        try {
            Execute execute = new Execute(new LogStreamHandler((Task) this, 2, 1));
            if (this.serverPath != null) {
                String[] environment = execute.getEnvironment();
                if (environment == null) {
                    environment = new String[0];
                }
                String[] strArr = new String[environment.length + 1];
                System.arraycopy(environment, 0, strArr, 0, environment.length);
                strArr[environment.length] = "SSDIR=" + this.serverPath;
                execute.setEnvironment(strArr);
            }
            execute.setAntRun(getProject());
            execute.setWorkingDirectory(getProject().getBaseDir());
            execute.setCommandline(commandline.getCommandline());
            execute.setVMLauncher(false);
            return execute.execute();
        } catch (IOException e) {
            throw new BuildException(e, getLocation());
        }
    }

    private String calcDate(String str, int i) throws ParseException {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(this.dateFormat.parse(str));
        gregorianCalendar.add(5, i);
        return this.dateFormat.format(gregorianCalendar.getTime());
    }

    private String formatCommandLine(Commandline commandline) {
        StringBuffer stringBuffer = new StringBuffer(commandline.toString());
        int iIndexOf = stringBuffer.substring(0).indexOf("-Y");
        if (iIndexOf > 0) {
            int iIndexOf2 = stringBuffer.substring(0).indexOf(",", iIndexOf);
            int iIndexOf3 = stringBuffer.substring(0).indexOf(" ", iIndexOf2);
            while (true) {
                iIndexOf2++;
                if (iIndexOf2 >= iIndexOf3) {
                    break;
                }
                stringBuffer.setCharAt(iIndexOf2, '*');
            }
        }
        return stringBuffer.toString();
    }

    public static class CurrentModUpdated extends EnumeratedAttribute {
        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{MSVSSConstants.TIME_CURRENT, "modified", MSVSSConstants.TIME_UPDATED};
        }
    }

    public static class WritableFiles extends EnumeratedAttribute {
        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{MSVSSConstants.WRITABLE_REPLACE, MSVSSConstants.WRITABLE_SKIP, "fail"};
        }
    }
}
