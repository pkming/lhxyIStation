package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.PrintStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.Comparison;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.PropertyOutputStream;

/* JADX INFO: loaded from: classes3.dex */
public class Length extends Task implements Condition {
    private static final String ALL = "all";
    private static final String EACH = "each";
    private static final String LENGTH_REQUIRED = "Use of the Length condition requires that the length attribute be set.";
    private static final String STRING = "string";
    private Long length;
    private String property;
    private Resources resources;
    private String string;
    private Boolean trim;
    private String mode = "all";
    private Comparison when = Comparison.EQUAL;

    public static class When extends Comparison {
    }

    public synchronized void setProperty(String str) {
        this.property = str;
    }

    public synchronized void setResource(Resource resource) {
        add(resource);
    }

    public synchronized void setFile(File file) {
        add(new FileResource(file));
    }

    public synchronized void add(FileSet fileSet) {
        add((ResourceCollection) fileSet);
    }

    public synchronized void add(ResourceCollection resourceCollection) {
        if (resourceCollection == null) {
            return;
        }
        Resources resources = this.resources;
        if (resources == null) {
            resources = new Resources();
        }
        this.resources = resources;
        resources.add(resourceCollection);
    }

    public synchronized void setLength(long j) {
        this.length = new Long(j);
    }

    public synchronized void setWhen(When when) {
        setWhen((Comparison) when);
    }

    public synchronized void setWhen(Comparison comparison) {
        this.when = comparison;
    }

    public synchronized void setMode(FileMode fileMode) {
        this.mode = fileMode.getValue();
    }

    public synchronized void setString(String str) {
        this.string = str;
        this.mode = STRING;
    }

    public synchronized void setTrim(boolean z) {
        this.trim = z ? Boolean.TRUE : Boolean.FALSE;
    }

    public boolean getTrim() {
        Boolean bool = this.trim;
        return bool != null && bool.booleanValue();
    }

    @Override // org.apache.tools.ant.Task
    public void execute() {
        validate();
        PrintStream printStream = new PrintStream(this.property != null ? new PropertyOutputStream(getProject(), this.property) : new LogOutputStream((Task) this, 2));
        if (STRING.equals(this.mode)) {
            printStream.print(getLength(this.string, getTrim()));
            printStream.close();
        } else if (EACH.equals(this.mode)) {
            handleResources(new EachHandler(printStream));
        } else if ("all".equals(this.mode)) {
            handleResources(new AllHandler(printStream));
        }
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() {
        Long l;
        validate();
        if (this.length == null) {
            throw new BuildException(LENGTH_REQUIRED);
        }
        if (STRING.equals(this.mode)) {
            l = new Long(getLength(this.string, getTrim()));
        } else {
            AccumHandler accumHandler = new AccumHandler();
            handleResources(accumHandler);
            l = new Long(accumHandler.getAccum());
        }
        return this.when.evaluate(l.compareTo(this.length));
    }

    private void validate() {
        if (this.string != null) {
            if (this.resources != null) {
                throw new BuildException("the string length function is incompatible with the file/resource length function");
            }
            if (!STRING.equals(this.mode)) {
                throw new BuildException("the mode attribute is for use with the file/resource length function");
            }
            return;
        }
        if (this.resources != null) {
            if (!EACH.equals(this.mode) && !"all".equals(this.mode)) {
                throw new BuildException("invalid mode setting for file/resource length function: \"" + this.mode + "\"");
            }
            if (this.trim != null) {
                throw new BuildException("the trim attribute is for use with the string length function only");
            }
            return;
        }
        throw new BuildException("you must set either the string attribute or specify one or more files using the file attribute or nested resource collections");
    }

    private void handleResources(Handler handler) {
        for (Resource resource : this.resources) {
            if (!resource.isExists()) {
                log(resource + " does not exist", 1);
            }
            if (resource.isDirectory()) {
                log(resource + " is a directory; length may not be meaningful", 1);
            }
            handler.handle(resource);
        }
        handler.complete();
    }

    private static long getLength(String str, boolean z) {
        if (z) {
            str = str.trim();
        }
        return str.length();
    }

    public static class FileMode extends EnumeratedAttribute {
        static final String[] MODES = {Length.EACH, "all"};

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return MODES;
        }
    }

    private abstract class Handler {
        private PrintStream ps;

        protected abstract void handle(Resource resource);

        Handler(PrintStream printStream) {
            this.ps = printStream;
        }

        protected PrintStream getPs() {
            return this.ps;
        }

        void complete() {
            FileUtils.close(this.ps);
        }
    }

    private class EachHandler extends Handler {
        EachHandler(PrintStream printStream) {
            super(printStream);
        }

        @Override // org.apache.tools.ant.taskdefs.Length.Handler
        protected void handle(Resource resource) {
            getPs().print(resource.toString());
            getPs().print(" : ");
            long size = resource.getSize();
            if (size == -1) {
                getPs().println("unknown");
            } else {
                getPs().println(size);
            }
        }
    }

    private class AccumHandler extends Handler {
        private long accum;

        AccumHandler() {
            super(null);
            this.accum = 0L;
        }

        protected AccumHandler(PrintStream printStream) {
            super(printStream);
            this.accum = 0L;
        }

        protected long getAccum() {
            return this.accum;
        }

        @Override // org.apache.tools.ant.taskdefs.Length.Handler
        protected synchronized void handle(Resource resource) {
            long size = resource.getSize();
            if (size == -1) {
                Length.this.log("Size unknown for " + resource.toString(), 1);
            } else {
                this.accum += size;
            }
        }
    }

    private class AllHandler extends AccumHandler {
        AllHandler(PrintStream printStream) {
            super(printStream);
        }

        @Override // org.apache.tools.ant.taskdefs.Length.Handler
        void complete() {
            getPs().print(getAccum());
            super.complete();
        }
    }
}
