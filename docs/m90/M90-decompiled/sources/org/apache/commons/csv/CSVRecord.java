package org.apache.commons.csv;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
public final class CSVRecord implements Serializable, Iterable<String> {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final long serialVersionUID = 1;
    private final long characterPosition;
    private final String comment;
    private final Map<String, Integer> mapping;
    private final long recordNumber;
    private final String[] values;

    CSVRecord(String[] strArr, Map<String, Integer> map, String str, long j, long j2) {
        this.recordNumber = j;
        this.values = strArr == null ? EMPTY_STRING_ARRAY : strArr;
        this.mapping = map;
        this.comment = str;
        this.characterPosition = j2;
    }

    public String get(Enum<?> r1) {
        return get(r1.toString());
    }

    public String get(int i) {
        return this.values[i];
    }

    public String get(String str) {
        Map<String, Integer> map = this.mapping;
        if (map == null) {
            throw new IllegalStateException("No header mapping was specified, the record values can't be accessed by name");
        }
        Integer num = map.get(str);
        if (num == null) {
            throw new IllegalArgumentException(String.format("Mapping for %s not found, expected one of %s", str, this.mapping.keySet()));
        }
        try {
            return this.values[num.intValue()];
        } catch (ArrayIndexOutOfBoundsException unused) {
            throw new IllegalArgumentException(String.format("Index for header '%s' is %d but CSVRecord only has %d values!", str, num, Integer.valueOf(this.values.length)));
        }
    }

    public long getCharacterPosition() {
        return this.characterPosition;
    }

    public String getComment() {
        return this.comment;
    }

    public long getRecordNumber() {
        return this.recordNumber;
    }

    public boolean isConsistent() {
        Map<String, Integer> map = this.mapping;
        return map == null || map.size() == this.values.length;
    }

    public boolean hasComment() {
        return this.comment != null;
    }

    public boolean isMapped(String str) {
        Map<String, Integer> map = this.mapping;
        return map != null && map.containsKey(str);
    }

    public boolean isSet(String str) {
        return isMapped(str) && this.mapping.get(str).intValue() < this.values.length;
    }

    @Override // java.lang.Iterable
    public Iterator<String> iterator() {
        return toList().iterator();
    }

    <M extends Map<String, String>> M putIn(M m) {
        Map<String, Integer> map = this.mapping;
        if (map == null) {
            return m;
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            int iIntValue = entry.getValue().intValue();
            if (iIntValue < this.values.length) {
                m.put(entry.getKey(), this.values[iIntValue]);
            }
        }
        return m;
    }

    public int size() {
        return this.values.length;
    }

    private List<String> toList() {
        return Arrays.asList(this.values);
    }

    public Map<String, String> toMap() {
        return putIn(new HashMap(this.values.length));
    }

    public String toString() {
        return "CSVRecord [comment=" + this.comment + ", mapping=" + this.mapping + ", recordNumber=" + this.recordNumber + ", values=" + Arrays.toString(this.values) + "]";
    }

    String[] values() {
        return this.values;
    }
}
