package org.apache.tools.ant.taskdefs.condition;

import android.text.style.SuggestionSpan;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.Touch;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Resource;

/* JADX INFO: loaded from: classes3.dex */
public class IsLastModified extends ProjectComponent implements Condition {
    private Resource resource;
    private long millis = -1;
    private String dateTime = null;
    private Touch.DateFormatFactory dfFactory = Touch.DEFAULT_DF_FACTORY;
    private CompareMode mode = CompareMode.EQUALS;

    public void setMillis(long j) {
        this.millis = j;
    }

    public void setDatetime(String str) {
        this.dateTime = str;
    }

    public void setPattern(final String str) {
        this.dfFactory = new Touch.DateFormatFactory() { // from class: org.apache.tools.ant.taskdefs.condition.IsLastModified.1
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

    public void add(Resource resource) {
        if (this.resource != null) {
            throw new BuildException("only one resource can be tested");
        }
        this.resource = resource;
    }

    public void setMode(CompareMode compareMode) {
        this.mode = compareMode;
    }

    protected void validate() throws BuildException {
        long j = this.millis;
        if (j >= 0 && this.dateTime != null) {
            throw new BuildException("Only one of dateTime and millis can be set");
        }
        if (j < 0 && this.dateTime == null) {
            throw new BuildException("millis or dateTime is required");
        }
        if (this.resource == null) {
            throw new BuildException("resource is required");
        }
    }

    protected long getMillis() throws BuildException {
        long j = this.millis;
        if (j >= 0) {
            return j;
        }
        if ("now".equalsIgnoreCase(this.dateTime)) {
            return System.currentTimeMillis();
        }
        try {
            return this.dfFactory.getPrimaryFormat().parse(this.dateTime).getTime();
        } catch (ParseException e) {
            e = e;
            DateFormat fallbackFormat = this.dfFactory.getFallbackFormat();
            if (fallbackFormat != null) {
                try {
                    return fallbackFormat.parse(this.dateTime).getTime();
                } catch (ParseException e2) {
                    e = e2;
                    throw new BuildException(e.getMessage(), e, getLocation());
                }
            }
            throw new BuildException(e.getMessage(), e, getLocation());
        }
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() throws BuildException {
        validate();
        long millis = getMillis();
        long lastModified = this.resource.getLastModified();
        log("expected timestamp: " + millis + " (" + new Date(millis) + "), actual timestamp: " + lastModified + " (" + new Date(lastModified) + ")", 3);
        if ("equals".equals(this.mode.getValue())) {
            return millis == lastModified;
        }
        if (SuggestionSpan.SUGGESTION_SPAN_PICKED_BEFORE.equals(this.mode.getValue())) {
            return millis > lastModified;
        }
        if ("not-before".equals(this.mode.getValue())) {
            return millis <= lastModified;
        }
        if (SuggestionSpan.SUGGESTION_SPAN_PICKED_AFTER.equals(this.mode.getValue())) {
            return millis < lastModified;
        }
        if ("not-after".equals(this.mode.getValue())) {
            return millis >= lastModified;
        }
        throw new BuildException("Unknown mode " + this.mode.getValue());
    }

    public static class CompareMode extends EnumeratedAttribute {
        private static final String AFTER_TEXT = "after";
        private static final String BEFORE_TEXT = "before";
        private static final String NOT_AFTER_TEXT = "not-after";
        private static final String NOT_BEFORE_TEXT = "not-before";
        private static final String EQUALS_TEXT = "equals";
        private static final CompareMode EQUALS = new CompareMode(EQUALS_TEXT);

        public CompareMode() {
            this(EQUALS_TEXT);
        }

        public CompareMode(String str) {
            setValue(str);
        }

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{EQUALS_TEXT, "before", "after", NOT_BEFORE_TEXT, NOT_AFTER_TEXT};
        }
    }
}
