package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Touchable;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Touch extends Task {
    public static final DateFormatFactory DEFAULT_DF_FACTORY = new DateFormatFactory() { // from class: org.apache.tools.ant.taskdefs.Touch.1
        @Override // org.apache.tools.ant.taskdefs.Touch.DateFormatFactory
        public DateFormat getPrimaryFormat() {
            return DateFormat.getDateTimeInstance(3, 3, Locale.US);
        }

        @Override // org.apache.tools.ant.taskdefs.Touch.DateFormatFactory
        public DateFormat getFallbackFormat() {
            return DateFormat.getDateTimeInstance(3, 2, Locale.US);
        }
    };
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private String dateTime;
    private boolean dateTimeConfigured;
    private File file;
    private boolean mkdirs;
    private Union resources;
    private long millis = -1;
    private Vector filesets = new Vector();
    private boolean verbose = true;
    private FileNameMapper fileNameMapper = null;
    private DateFormatFactory dfFactory = DEFAULT_DF_FACTORY;

    public interface DateFormatFactory {
        DateFormat getFallbackFormat();

        DateFormat getPrimaryFormat();
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setMillis(long j) {
        this.millis = j;
    }

    public void setDatetime(String str) {
        if (this.dateTime != null) {
            log("Resetting datetime attribute to " + str, 3);
        }
        this.dateTime = str;
        this.dateTimeConfigured = false;
    }

    public void setMkdirs(boolean z) {
        this.mkdirs = z;
    }

    public void setVerbose(boolean z) {
        this.verbose = z;
    }

    public void setPattern(final String str) {
        this.dfFactory = new DateFormatFactory() { // from class: org.apache.tools.ant.taskdefs.Touch.2
            @Override // org.apache.tools.ant.taskdefs.Touch.DateFormatFactory
            public DateFormat getFallbackFormat() {
                return null;
            }

            @Override // org.apache.tools.ant.taskdefs.Touch.DateFormatFactory
            public DateFormat getPrimaryFormat() {
                return new SimpleDateFormat(str);
            }
        };
    }

    public void addConfiguredMapper(Mapper mapper) {
        add(mapper.getImplementation());
    }

    public void add(FileNameMapper fileNameMapper) throws BuildException {
        if (this.fileNameMapper != null) {
            throw new BuildException("Only one mapper may be added to the " + getTaskName() + " task.");
        }
        this.fileNameMapper = fileNameMapper;
    }

    public void addFileset(FileSet fileSet) {
        this.filesets.add(fileSet);
        add(fileSet);
    }

    public void addFilelist(FileList fileList) {
        add(fileList);
    }

    public synchronized void add(ResourceCollection resourceCollection) {
        Union union = this.resources;
        if (union == null) {
            union = new Union();
        }
        this.resources = union;
        union.add(resourceCollection);
    }

    protected synchronized void checkConfiguration() throws BuildException {
        long jCurrentTimeMillis;
        File file = this.file;
        if (file == null && this.resources == null) {
            throw new BuildException("Specify at least one source--a file or resource collection.");
        }
        if (file != null && file.exists() && this.file.isDirectory()) {
            throw new BuildException("Use a resource collection to touch directories.");
        }
        String str = this.dateTime;
        if (str != null && !this.dateTimeConfigured) {
            long time = this.millis;
            if ("now".equalsIgnoreCase(str)) {
                jCurrentTimeMillis = System.currentTimeMillis();
            } else {
                ParseException parseException = null;
                try {
                    time = this.dfFactory.getPrimaryFormat().parse(this.dateTime).getTime();
                } catch (ParseException e) {
                    e = e;
                    DateFormat fallbackFormat = this.dfFactory.getFallbackFormat();
                    if (fallbackFormat == null) {
                        parseException = e;
                    } else {
                        try {
                            time = fallbackFormat.parse(this.dateTime).getTime();
                        } catch (ParseException e2) {
                            e = e2;
                            parseException = e;
                        }
                    }
                }
                if (parseException != null) {
                    throw new BuildException(parseException.getMessage(), parseException, getLocation());
                }
                if (time < 0) {
                    throw new BuildException("Date of " + this.dateTime + " results in negative milliseconds value relative to epoch (January 1, 1970, 00:00:00 GMT).");
                }
                jCurrentTimeMillis = time;
            }
            log("Setting millis to " + jCurrentTimeMillis + " from datetime attribute", this.millis < 0 ? 4 : 3);
            setMillis(jCurrentTimeMillis);
            this.dateTimeConfigured = true;
        }
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        checkConfiguration();
        touch();
    }

    protected void touch() throws BuildException {
        long timestamp = getTimestamp();
        if (this.file != null) {
            touch(new FileResource(this.file.getParentFile(), this.file.getName()), timestamp);
        }
        Union union = this.resources;
        if (union == null) {
            return;
        }
        for (Resource resource : union) {
            if (((Touchable) resource.as(Touchable.class)) == null) {
                throw new BuildException("Can't touch " + resource);
            }
            touch(resource, timestamp);
        }
        int size = this.filesets.size();
        for (int i = 0; i < size; i++) {
            FileSet fileSet = (FileSet) this.filesets.elementAt(i);
            DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(getProject());
            File dir = fileSet.getDir(getProject());
            for (String str : directoryScanner.getIncludedDirectories()) {
                touch(new FileResource(dir, str), timestamp);
            }
        }
    }

    protected void touch(File file) {
        touch(file, getTimestamp());
    }

    private long getTimestamp() {
        long j = this.millis;
        return j < 0 ? System.currentTimeMillis() : j;
    }

    private void touch(Resource resource, long j) {
        FileNameMapper fileNameMapper = this.fileNameMapper;
        if (fileNameMapper == null) {
            FileProvider fileProvider = (FileProvider) resource.as(FileProvider.class);
            if (fileProvider != null) {
                touch(fileProvider.getFile(), j);
                return;
            } else {
                ((Touchable) resource.as(Touchable.class)).touch(j);
                return;
            }
        }
        String[] strArrMapFileName = fileNameMapper.mapFileName(resource.getName());
        if (strArrMapFileName == null || strArrMapFileName.length <= 0) {
            return;
        }
        if (this.millis < 0 && resource.isExists()) {
            j = resource.getLastModified();
        }
        for (String str : strArrMapFileName) {
            touch(getProject().resolveFile(str), j);
        }
    }

    private void touch(File file, long j) {
        if (!file.exists()) {
            log("Creating " + file, this.verbose ? 2 : 3);
            try {
                FILE_UTILS.createNewFile(file, this.mkdirs);
            } catch (IOException e) {
                throw new BuildException("Could not create " + file, e, getLocation());
            }
        }
        if (!file.canWrite()) {
            throw new BuildException("Can not change modification date of read-only file " + file);
        }
        FILE_UTILS.setFileLastModified(file, j);
    }
}
