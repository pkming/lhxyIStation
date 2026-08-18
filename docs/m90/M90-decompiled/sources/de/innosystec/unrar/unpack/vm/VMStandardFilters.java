package de.innosystec.unrar.unpack.vm;

/* JADX INFO: loaded from: classes2.dex */
public enum VMStandardFilters {
    VMSF_NONE(0),
    VMSF_E8(1),
    VMSF_E8E9(2),
    VMSF_ITANIUM(3),
    VMSF_RGB(4),
    VMSF_AUDIO(5),
    VMSF_DELTA(6),
    VMSF_UPCASE(7);

    private int filter;

    /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
    public static VMStandardFilters[] valuesCustom() {
        VMStandardFilters[] vMStandardFiltersArrValuesCustom = values();
        int length = vMStandardFiltersArrValuesCustom.length;
        VMStandardFilters[] vMStandardFiltersArr = new VMStandardFilters[length];
        System.arraycopy(vMStandardFiltersArrValuesCustom, 0, vMStandardFiltersArr, 0, length);
        return vMStandardFiltersArr;
    }

    VMStandardFilters(int i) {
        this.filter = i;
    }

    public int getFilter() {
        return this.filter;
    }

    public boolean equals(int i) {
        return this.filter == i;
    }

    public static VMStandardFilters findFilter(int i) {
        VMStandardFilters vMStandardFilters = VMSF_NONE;
        if (vMStandardFilters.equals(i)) {
            return vMStandardFilters;
        }
        VMStandardFilters vMStandardFilters2 = VMSF_E8;
        if (vMStandardFilters2.equals(i)) {
            return vMStandardFilters2;
        }
        VMStandardFilters vMStandardFilters3 = VMSF_E8E9;
        if (vMStandardFilters3.equals(i)) {
            return vMStandardFilters3;
        }
        VMStandardFilters vMStandardFilters4 = VMSF_ITANIUM;
        if (vMStandardFilters4.equals(i)) {
            return vMStandardFilters4;
        }
        VMStandardFilters vMStandardFilters5 = VMSF_RGB;
        if (vMStandardFilters5.equals(i)) {
            return vMStandardFilters5;
        }
        VMStandardFilters vMStandardFilters6 = VMSF_AUDIO;
        if (vMStandardFilters6.equals(i)) {
            return vMStandardFilters6;
        }
        VMStandardFilters vMStandardFilters7 = VMSF_DELTA;
        if (vMStandardFilters7.equals(i)) {
            return vMStandardFilters7;
        }
        return null;
    }
}
