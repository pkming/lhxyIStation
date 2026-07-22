package de.innosystec.unrar.unpack.decode;

/* JADX INFO: loaded from: classes2.dex */
public enum FilterType {
    FILTER_NONE,
    FILTER_PPM,
    FILTER_E8,
    FILTER_E8E9,
    FILTER_UPCASETOLOW,
    FILTER_AUDIO,
    FILTER_RGB,
    FILTER_DELTA,
    FILTER_ITANIUM,
    FILTER_E8E9V2;

    /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
    public static FilterType[] valuesCustom() {
        FilterType[] filterTypeArrValuesCustom = values();
        int length = filterTypeArrValuesCustom.length;
        FilterType[] filterTypeArr = new FilterType[length];
        System.arraycopy(filterTypeArrValuesCustom, 0, filterTypeArr, 0, length);
        return filterTypeArr;
    }
}
