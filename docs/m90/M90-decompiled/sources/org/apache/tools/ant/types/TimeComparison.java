package org.apache.tools.ant.types;

import android.text.style.SuggestionSpan;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class TimeComparison extends EnumeratedAttribute {
    private static final String[] VALUES = {SuggestionSpan.SUGGESTION_SPAN_PICKED_BEFORE, SuggestionSpan.SUGGESTION_SPAN_PICKED_AFTER, "equal"};
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    public static final TimeComparison BEFORE = new TimeComparison(SuggestionSpan.SUGGESTION_SPAN_PICKED_BEFORE);
    public static final TimeComparison AFTER = new TimeComparison(SuggestionSpan.SUGGESTION_SPAN_PICKED_AFTER);
    public static final TimeComparison EQUAL = new TimeComparison("equal");

    public TimeComparison() {
    }

    public TimeComparison(String str) {
        setValue(str);
    }

    @Override // org.apache.tools.ant.types.EnumeratedAttribute
    public String[] getValues() {
        return VALUES;
    }

    public boolean evaluate(long j, long j2) {
        return evaluate(j, j2, FILE_UTILS.getFileTimestampGranularity());
    }

    public boolean evaluate(long j, long j2, long j3) {
        int index = getIndex();
        if (index != -1) {
            return index == 0 ? j - j3 < j2 : index == 1 ? j + j3 > j2 : Math.abs(j - j2) <= j3;
        }
        throw new BuildException("TimeComparison value not set.");
    }

    public static int compare(long j, long j2) {
        return compare(j, j2, FILE_UTILS.getFileTimestampGranularity());
    }

    public static int compare(long j, long j2, long j3) {
        long j4 = j - j2;
        long jAbs = Math.abs(j4);
        if (jAbs > Math.abs(j3)) {
            return (int) (j4 / jAbs);
        }
        return 0;
    }
}
