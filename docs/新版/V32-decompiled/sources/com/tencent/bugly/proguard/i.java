package com.tencent.bugly.proguard;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/* JADX INFO: compiled from: BUGLY */
/* JADX INFO: loaded from: classes2.dex */
public final class i {
    private StringBuilder a;
    private int b;

    private void a(String str) {
        for (int i = 0; i < this.b; i++) {
            this.a.append('\t');
        }
        if (str != null) {
            this.a.append(str).append(": ");
        }
    }

    public i(StringBuilder sb, int i) {
        this.b = 0;
        this.a = sb;
        this.b = i;
    }

    public final i a(boolean z, String str) {
        a(str);
        this.a.append(z ? 'T' : 'F').append('\n');
        return this;
    }

    public final i a(byte b, String str) {
        a(str);
        this.a.append((int) b).append('\n');
        return this;
    }

    private i a(char c, String str) {
        a(str);
        this.a.append(c).append('\n');
        return this;
    }

    public final i a(short s, String str) {
        a(str);
        this.a.append((int) s).append('\n');
        return this;
    }

    public final i a(int i, String str) {
        a(str);
        this.a.append(i).append('\n');
        return this;
    }

    public final i a(long j, String str) {
        a(str);
        this.a.append(j).append('\n');
        return this;
    }

    private i a(float f, String str) {
        a(str);
        this.a.append(f).append('\n');
        return this;
    }

    private i a(double d, String str) {
        a(str);
        this.a.append(d).append('\n');
        return this;
    }

    public final i a(String str, String str2) {
        a(str2);
        if (str == null) {
            this.a.append("null\n");
        } else {
            this.a.append(str).append('\n');
        }
        return this;
    }

    public final i a(byte[] bArr, String str) {
        a(str);
        if (bArr == null) {
            this.a.append("null\n");
            return this;
        }
        if (bArr.length == 0) {
            this.a.append(bArr.length).append(", []\n");
            return this;
        }
        this.a.append(bArr.length).append(", [\n");
        i iVar = new i(this.a, this.b + 1);
        for (byte b : bArr) {
            iVar.a(b, (String) null);
        }
        a(']', (String) null);
        return this;
    }

    private i a(short[] sArr, String str) {
        a(str);
        if (sArr == null) {
            this.a.append("null\n");
            return this;
        }
        if (sArr.length == 0) {
            this.a.append(sArr.length).append(", []\n");
            return this;
        }
        this.a.append(sArr.length).append(", [\n");
        i iVar = new i(this.a, this.b + 1);
        for (short s : sArr) {
            iVar.a(s, (String) null);
        }
        a(']', (String) null);
        return this;
    }

    private i a(int[] iArr, String str) {
        a(str);
        if (iArr == null) {
            this.a.append("null\n");
            return this;
        }
        if (iArr.length == 0) {
            this.a.append(iArr.length).append(", []\n");
            return this;
        }
        this.a.append(iArr.length).append(", [\n");
        i iVar = new i(this.a, this.b + 1);
        for (int i : iArr) {
            iVar.a(i, (String) null);
        }
        a(']', (String) null);
        return this;
    }

    private i a(long[] jArr, String str) {
        a(str);
        if (jArr == null) {
            this.a.append("null\n");
            return this;
        }
        if (jArr.length == 0) {
            this.a.append(jArr.length).append(", []\n");
            return this;
        }
        this.a.append(jArr.length).append(", [\n");
        i iVar = new i(this.a, this.b + 1);
        for (long j : jArr) {
            iVar.a(j, (String) null);
        }
        a(']', (String) null);
        return this;
    }

    private i a(float[] fArr, String str) {
        a(str);
        if (fArr == null) {
            this.a.append("null\n");
            return this;
        }
        if (fArr.length == 0) {
            this.a.append(fArr.length).append(", []\n");
            return this;
        }
        this.a.append(fArr.length).append(", [\n");
        i iVar = new i(this.a, this.b + 1);
        for (float f : fArr) {
            iVar.a(f, (String) null);
        }
        a(']', (String) null);
        return this;
    }

    private i a(double[] dArr, String str) {
        a(str);
        if (dArr == null) {
            this.a.append("null\n");
            return this;
        }
        if (dArr.length == 0) {
            this.a.append(dArr.length).append(", []\n");
            return this;
        }
        this.a.append(dArr.length).append(", [\n");
        i iVar = new i(this.a, this.b + 1);
        for (double d : dArr) {
            iVar.a(d, (String) null);
        }
        a(']', (String) null);
        return this;
    }

    public final <K, V> i a(Map<K, V> map, String str) {
        a(str);
        if (map == null) {
            this.a.append("null\n");
            return this;
        }
        if (map.isEmpty()) {
            this.a.append(map.size()).append(", {}\n");
            return this;
        }
        this.a.append(map.size()).append(", {\n");
        i iVar = new i(this.a, this.b + 1);
        i iVar2 = new i(this.a, this.b + 2);
        for (Map.Entry<K, V> entry : map.entrySet()) {
            iVar.a('(', (String) null);
            iVar2.a(entry.getKey(), (String) null);
            iVar2.a(entry.getValue(), (String) null);
            iVar.a(')', (String) null);
        }
        a('}', (String) null);
        return this;
    }

    private <T> i a(T[] tArr, String str) {
        a(str);
        if (tArr == null) {
            this.a.append("null\n");
            return this;
        }
        if (tArr.length == 0) {
            this.a.append(tArr.length).append(", []\n");
            return this;
        }
        this.a.append(tArr.length).append(", [\n");
        i iVar = new i(this.a, this.b + 1);
        for (T t : tArr) {
            iVar.a(t, (String) null);
        }
        a(']', (String) null);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <T> i a(Collection<T> collection, String str) {
        if (collection == null) {
            a(str);
            this.a.append("null\t");
            return this;
        }
        return a(collection.toArray(), str);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <T> i a(T t, String str) {
        if (t == 0) {
            this.a.append("null\n");
        } else if (t instanceof Byte) {
            a(((Byte) t).byteValue(), str);
        } else if (t instanceof Boolean) {
            a(((Boolean) t).booleanValue(), str);
        } else if (t instanceof Short) {
            a(((Short) t).shortValue(), str);
        } else if (t instanceof Integer) {
            a(((Integer) t).intValue(), str);
        } else if (t instanceof Long) {
            a(((Long) t).longValue(), str);
        } else if (t instanceof Float) {
            a(((Float) t).floatValue(), str);
        } else if (t instanceof Double) {
            a(((Double) t).doubleValue(), str);
        } else if (t instanceof String) {
            a((String) t, str);
        } else if (t instanceof Map) {
            a((Map) t, str);
        } else if (t instanceof List) {
            a((Collection) t, str);
        } else if (t instanceof m) {
            a((m) t, str);
        } else if (t instanceof byte[]) {
            a((byte[]) t, str);
        } else if (t instanceof boolean[]) {
            a((boolean[]) t, str);
        } else if (t instanceof short[]) {
            a((short[]) t, str);
        } else if (t instanceof int[]) {
            a((int[]) t, str);
        } else if (t instanceof long[]) {
            a((long[]) t, str);
        } else if (t instanceof float[]) {
            a((float[]) t, str);
        } else if (t instanceof double[]) {
            a((double[]) t, str);
        } else if (t.getClass().isArray()) {
            a((Object[]) t, str);
        } else {
            throw new j("write object error: unsupport type.");
        }
        return this;
    }

    public final i a(m mVar, String str) {
        a('{', str);
        if (mVar == null) {
            this.a.append('\t').append("null");
        } else {
            mVar.a(this.a, this.b + 1);
        }
        a('}', (String) null);
        return this;
    }
}
