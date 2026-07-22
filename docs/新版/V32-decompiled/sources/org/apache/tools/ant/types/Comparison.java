package org.apache.tools.ant.types;

import java.util.Arrays;
import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public class Comparison extends EnumeratedAttribute {
    private static final String[] VALUES = {"equal", "greater", "less", "ne", "ge", "le", "eq", "gt", "lt", "more"};
    public static final Comparison EQUAL = new Comparison("equal");
    public static final Comparison NOT_EQUAL = new Comparison("ne");
    public static final Comparison GREATER = new Comparison("greater");
    public static final Comparison LESS = new Comparison("less");
    public static final Comparison GREATER_EQUAL = new Comparison("ge");
    public static final Comparison LESS_EQUAL = new Comparison("le");
    private static final int[] EQUAL_INDEX = {0, 4, 5, 6};
    private static final int[] LESS_INDEX = {2, 3, 5, 8};
    private static final int[] GREATER_INDEX = {1, 3, 4, 7, 9};

    public Comparison() {
    }

    public Comparison(String str) {
        setValue(str);
    }

    @Override // org.apache.tools.ant.types.EnumeratedAttribute
    public String[] getValues() {
        return VALUES;
    }

    public boolean evaluate(int i) {
        int[] iArr;
        if (getIndex() == -1) {
            throw new BuildException("Comparison value not set.");
        }
        if (i < 0) {
            iArr = LESS_INDEX;
        } else {
            iArr = i > 0 ? GREATER_INDEX : EQUAL_INDEX;
        }
        return Arrays.binarySearch(iArr, getIndex()) >= 0;
    }
}
