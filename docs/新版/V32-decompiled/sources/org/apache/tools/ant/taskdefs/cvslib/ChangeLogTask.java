package org.apache.tools.ant.taskdefs.cvslib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.AbstractCvsTask;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSSConstants;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ChangeLogTask extends AbstractCvsTask {
    private File destFile;
    private Date endDate;
    private String endTag;
    private File inputDir;
    private Date startDate;
    private String startTag;
    private File usersFile;
    private Vector cvsUsers = new Vector();
    private boolean remote = false;
    private final Vector filesets = new Vector();

    public void setDir(File file) {
        this.inputDir = file;
    }

    public void setDestfile(File file) {
        this.destFile = file;
    }

    public void setUsersfile(File file) {
        this.usersFile = file;
    }

    public void addUser(CvsUser cvsUser) {
        this.cvsUsers.addElement(cvsUser);
    }

    public void setStart(Date date) {
        this.startDate = date;
    }

    public void setEnd(Date date) {
        this.endDate = date;
    }

    public void setDaysinpast(int i) {
        setStart(new Date(System.currentTimeMillis() - ((((((long) i) * 24) * 60) * 60) * 1000)));
    }

    public void setRemote(boolean z) {
        this.remote = z;
    }

    public void setStartTag(String str) {
        this.startTag = str;
    }

    public void setEndTag(String str) {
        this.endTag = str;
    }

    public void addFileset(FileSet fileSet) {
        this.filesets.addElement(fileSet);
    }

    @Override // org.apache.tools.ant.taskdefs.AbstractCvsTask, org.apache.tools.ant.Task
    public void execute() throws BuildException {
        File file = this.inputDir;
        try {
            validate();
            Properties properties = new Properties();
            loadUserlist(properties);
            int size = this.cvsUsers.size();
            for (int i = 0; i < size; i++) {
                CvsUser cvsUser = (CvsUser) this.cvsUsers.get(i);
                cvsUser.validate();
                properties.put(cvsUser.getUserID(), cvsUser.getDisplayname());
            }
            String str = "";
            if (!this.remote) {
                setCommand("log");
                if (getTag() != null) {
                    CvsVersion cvsVersion = new CvsVersion();
                    cvsVersion.setProject(getProject());
                    cvsVersion.setTaskName("cvsversion");
                    cvsVersion.setCvsRoot(getCvsRoot());
                    cvsVersion.setCvsRsh(getCvsRsh());
                    cvsVersion.setPassfile(getPassFile());
                    cvsVersion.setDest(this.inputDir);
                    cvsVersion.execute();
                    if (cvsVersion.supportsCvsLogWithSOption()) {
                        addCommandArgument("-S");
                    }
                }
            } else {
                setCommand("");
                addCommandArgument("rlog");
                addCommandArgument("-S");
                addCommandArgument(MSVSSConstants.VALUE_NO);
            }
            String str2 = this.startTag;
            if (str2 != null || this.endTag != null) {
                if (str2 == null) {
                    str2 = "";
                }
                String str3 = this.endTag;
                if (str3 != null) {
                    str = str3;
                }
                addCommandArgument("-r" + str2 + "::" + str);
            } else if (this.startDate != null) {
                String str4 = ">=" + new SimpleDateFormat("yyyy-MM-dd").format(this.startDate);
                addCommandArgument("-d");
                addCommandArgument(str4);
            }
            if (!this.filesets.isEmpty()) {
                Enumeration enumerationElements = this.filesets.elements();
                while (enumerationElements.hasMoreElements()) {
                    for (String str5 : ((FileSet) enumerationElements.nextElement()).getDirectoryScanner(getProject()).getIncludedFiles()) {
                        addCommandArgument(str5);
                    }
                }
            }
            ChangeLogParser changeLogParser = new ChangeLogParser(this.remote, getPackage(), getModules());
            RedirectingStreamHandler redirectingStreamHandler = new RedirectingStreamHandler(changeLogParser);
            log(getCommand(), 3);
            setDest(this.inputDir);
            setExecuteStreamHandler(redirectingStreamHandler);
            try {
                super.execute();
                CVSEntry[] cVSEntryArrFilterEntrySet = filterEntrySet(changeLogParser.getEntrySetAsArray());
                replaceAuthorIdWithName(properties, cVSEntryArrFilterEntrySet);
                writeChangeLog(cVSEntryArrFilterEntrySet);
            } finally {
                String errors = redirectingStreamHandler.getErrors();
                if (errors != null) {
                    log(errors, 0);
                }
            }
        } finally {
            this.inputDir = file;
        }
    }

    private void validate() throws BuildException {
        if (this.inputDir == null) {
            this.inputDir = getProject().getBaseDir();
        }
        if (this.destFile == null) {
            throw new BuildException("Destfile must be set.");
        }
        if (!this.inputDir.exists()) {
            throw new BuildException("Cannot find base dir " + this.inputDir.getAbsolutePath());
        }
        File file = this.usersFile;
        if (file != null && !file.exists()) {
            throw new BuildException("Cannot find user lookup list " + this.usersFile.getAbsolutePath());
        }
        if (this.startTag == null && this.endTag == null) {
            return;
        }
        if (this.startDate != null || this.endDate != null) {
            throw new BuildException("Specify either a tag or date range, not both");
        }
    }

    private void loadUserlist(Properties properties) throws BuildException {
        if (this.usersFile != null) {
            try {
                properties.load(new FileInputStream(this.usersFile));
            } catch (IOException e) {
                throw new BuildException(e.toString(), e);
            }
        }
    }

    private CVSEntry[] filterEntrySet(CVSEntry[] cVSEntryArr) {
        Date date;
        Date date2;
        Vector vector = new Vector();
        for (CVSEntry cVSEntry : cVSEntryArr) {
            Date date3 = cVSEntry.getDate();
            if (date3 != null && (((date = this.startDate) == null || !date.after(date3)) && ((date2 = this.endDate) == null || !date2.before(date3)))) {
                vector.addElement(cVSEntry);
            }
        }
        CVSEntry[] cVSEntryArr2 = new CVSEntry[vector.size()];
        vector.copyInto(cVSEntryArr2);
        return cVSEntryArr2;
    }

    private void replaceAuthorIdWithName(Properties properties, CVSEntry[] cVSEntryArr) {
        for (CVSEntry cVSEntry : cVSEntryArr) {
            if (properties.containsKey(cVSEntry.getAuthor())) {
                cVSEntry.setAuthor(properties.getProperty(cVSEntry.getAuthor()));
            }
        }
    }

    private void writeChangeLog(CVSEntry[] cVSEntryArr) throws Throwable {
        FileOutputStream fileOutputStream;
        FileOutputStream fileOutputStream2 = null;
        try {
            try {
                fileOutputStream = new FileOutputStream(this.destFile);
            } catch (Throwable th) {
                th = th;
            }
        } catch (UnsupportedEncodingException e) {
            e = e;
        } catch (IOException e2) {
            e = e2;
        }
        try {
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));
            new ChangeLogWriter().printChangeLog(printWriter, cVSEntryArr);
            if (printWriter.checkError()) {
                throw new IOException("Encountered an error writing changelog");
            }
            FileUtils.close(fileOutputStream);
        } catch (UnsupportedEncodingException e3) {
            e = e3;
            fileOutputStream2 = fileOutputStream;
            getProject().log(e.toString(), 0);
            FileUtils.close(fileOutputStream2);
        } catch (IOException e4) {
            e = e4;
            throw new BuildException(e.toString(), e);
        } catch (Throwable th2) {
            th = th2;
            fileOutputStream2 = fileOutputStream;
            FileUtils.close(fileOutputStream2);
            throw th;
        }
    }
}
