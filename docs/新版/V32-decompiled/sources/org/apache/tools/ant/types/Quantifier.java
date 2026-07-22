package org.apache.tools.ant.types;

import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public class Quantifier extends EnumeratedAttribute {
    private static final Predicate ALL_PRED;
    private static final Predicate ANY_PRED;
    private static final Predicate MAJORITY_PRED;
    private static final Predicate NONE_PRED;
    private static final Predicate ONE_PRED;
    private static final Predicate[] PREDS;
    private static final String[] VALUES;
    public static final Quantifier ALL = new Quantifier("all");
    public static final Quantifier ANY = new Quantifier("any");
    public static final Quantifier ONE = new Quantifier("one");
    public static final Quantifier MAJORITY = new Quantifier("majority");
    public static final Quantifier NONE = new Quantifier("none");

    static {
        String[] strArr = {"all", "each", "every", "any", "some", "one", "majority", "most", "none"};
        VALUES = strArr;
        Predicate predicate = new Predicate() { // from class: org.apache.tools.ant.types.Quantifier.1
            @Override // org.apache.tools.ant.types.Quantifier.Predicate
            boolean eval(int i, int i2) {
                return i2 == 0;
            }
        };
        ALL_PRED = predicate;
        Predicate predicate2 = new Predicate() { // from class: org.apache.tools.ant.types.Quantifier.2
            @Override // org.apache.tools.ant.types.Quantifier.Predicate
            boolean eval(int i, int i2) {
                return i > 0;
            }
        };
        ANY_PRED = predicate2;
        Predicate predicate3 = new Predicate() { // from class: org.apache.tools.ant.types.Quantifier.3
            @Override // org.apache.tools.ant.types.Quantifier.Predicate
            boolean eval(int i, int i2) {
                return i == 1;
            }
        };
        ONE_PRED = predicate3;
        Predicate predicate4 = new Predicate() { // from class: org.apache.tools.ant.types.Quantifier.4
            @Override // org.apache.tools.ant.types.Quantifier.Predicate
            boolean eval(int i, int i2) {
                return i > i2;
            }
        };
        MAJORITY_PRED = predicate4;
        Predicate predicate5 = new Predicate() { // from class: org.apache.tools.ant.types.Quantifier.5
            @Override // org.apache.tools.ant.types.Quantifier.Predicate
            boolean eval(int i, int i2) {
                return i == 0;
            }
        };
        NONE_PRED = predicate5;
        Predicate[] predicateArr = new Predicate[strArr.length];
        PREDS = predicateArr;
        predicateArr[0] = predicate;
        predicateArr[1] = predicate;
        predicateArr[2] = predicate;
        predicateArr[3] = predicate2;
        predicateArr[4] = predicate2;
        predicateArr[5] = predicate3;
        predicateArr[6] = predicate4;
        predicateArr[7] = predicate4;
        predicateArr[8] = predicate5;
    }

    private static abstract class Predicate {
        abstract boolean eval(int i, int i2);

        private Predicate() {
        }
    }

    public Quantifier() {
    }

    public Quantifier(String str) {
        setValue(str);
    }

    @Override // org.apache.tools.ant.types.EnumeratedAttribute
    public String[] getValues() {
        return VALUES;
    }

    public boolean evaluate(boolean[] zArr) {
        int i = 0;
        for (boolean z : zArr) {
            if (z) {
                i++;
            }
        }
        return evaluate(i, zArr.length - i);
    }

    public boolean evaluate(int i, int i2) {
        int index = getIndex();
        if (index == -1) {
            throw new BuildException("Quantifier value not set.");
        }
        return PREDS[index].eval(i, i2);
    }
}
