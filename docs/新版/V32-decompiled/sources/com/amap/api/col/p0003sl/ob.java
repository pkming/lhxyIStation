package com.amap.api.col.p0003sl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.Arrays;

/* JADX INFO: compiled from: FlatBufferBuilder.java */
/* JADX INFO: loaded from: classes2.dex */
public class ob {
    static final Charset c = Charset.forName("UTF-8");
    static final /* synthetic */ boolean p = true;
    ByteBuffer a;
    int b;
    int d;
    int[] e;
    int f;
    boolean g;
    boolean h;
    int i;
    int[] j;
    int k;
    int l;
    boolean m;
    CharsetEncoder n;
    ByteBuffer o;

    private ob() {
        this.d = 1;
        this.e = null;
        this.f = 0;
        this.g = false;
        this.h = false;
        this.j = new int[16];
        this.k = 0;
        this.l = 0;
        this.m = false;
        this.n = c.newEncoder();
        this.b = 1024;
        this.a = d(1024);
    }

    public ob(ByteBuffer byteBuffer) {
        this.d = 1;
        this.e = null;
        this.f = 0;
        this.g = false;
        this.h = false;
        this.j = new int[16];
        this.k = 0;
        this.l = 0;
        this.m = false;
        this.n = c.newEncoder();
        a(byteBuffer);
    }

    public final ob a(ByteBuffer byteBuffer) {
        this.a = byteBuffer;
        byteBuffer.clear();
        this.a.order(ByteOrder.LITTLE_ENDIAN);
        this.d = 1;
        this.b = this.a.capacity();
        this.f = 0;
        this.g = false;
        this.h = false;
        this.i = 0;
        this.k = 0;
        this.l = 0;
        return this;
    }

    private static ByteBuffer d(int i) {
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(i);
        byteBufferAllocate.order(ByteOrder.LITTLE_ENDIAN);
        return byteBufferAllocate;
    }

    private static ByteBuffer b(ByteBuffer byteBuffer) {
        int iCapacity = byteBuffer.capacity();
        if (((-1073741824) & iCapacity) != 0) {
            throw new AssertionError("FlatBuffers: cannot grow buffer beyond 2 gigabytes.");
        }
        int i = iCapacity << 1;
        byteBuffer.position(0);
        ByteBuffer byteBufferD = d(i);
        byteBufferD.position(i - iCapacity);
        byteBufferD.put(byteBuffer);
        return byteBufferD;
    }

    private int d() {
        return this.a.capacity() - this.b;
    }

    private void e(int i) {
        for (int i2 = 0; i2 < i; i2++) {
            ByteBuffer byteBuffer = this.a;
            int i3 = this.b - 1;
            this.b = i3;
            byteBuffer.put(i3, (byte) 0);
        }
    }

    private void c(int i, int i2) {
        if (i > this.d) {
            this.d = i;
        }
        int i3 = ((~((this.a.capacity() - this.b) + i2)) + 1) & (i - 1);
        while (this.b < i3 + i + i2) {
            int iCapacity = this.a.capacity();
            ByteBuffer byteBufferB = b(this.a);
            this.a = byteBufferB;
            this.b += byteBufferB.capacity() - iCapacity;
        }
        e(i3);
    }

    private void b(boolean z) {
        ByteBuffer byteBuffer = this.a;
        int i = this.b - 1;
        this.b = i;
        byteBuffer.put(i, z ? (byte) 1 : (byte) 0);
    }

    private void b(byte b) {
        ByteBuffer byteBuffer = this.a;
        int i = this.b - 1;
        this.b = i;
        byteBuffer.put(i, b);
    }

    private void a(short s) {
        ByteBuffer byteBuffer = this.a;
        int i = this.b - 2;
        this.b = i;
        byteBuffer.putShort(i, s);
    }

    private void f(int i) {
        ByteBuffer byteBuffer = this.a;
        int i2 = this.b - 4;
        this.b = i2;
        byteBuffer.putInt(i2, i);
    }

    private void a(long j) {
        ByteBuffer byteBuffer = this.a;
        int i = this.b - 8;
        this.b = i;
        byteBuffer.putLong(i, j);
    }

    private void c(boolean z) {
        c(1, 0);
        b(z);
    }

    public final void a(byte b) {
        c(1, 0);
        b(b);
    }

    private void b(short s) {
        c(2, 0);
        a(s);
    }

    private void g(int i) {
        c(4, 0);
        f(i);
    }

    private void b(long j) {
        c(8, 0);
        a(j);
    }

    public final void a(int i) {
        c(4, 0);
        if (!p && i > d()) {
            throw new AssertionError();
        }
        f((d() - i) + 4);
    }

    public final void a(int i, int i2, int i3) {
        f();
        this.l = i2;
        int i4 = i * i2;
        c(4, i4);
        c(i3, i4);
        this.g = true;
    }

    public final int a() {
        if (!this.g) {
            throw new AssertionError("FlatBuffers: endVector called without startVector");
        }
        this.g = false;
        f(this.l);
        return d();
    }

    public int a(CharSequence charSequence) {
        int length = (int) (charSequence.length() * this.n.maxBytesPerChar());
        ByteBuffer byteBuffer = this.o;
        if (byteBuffer == null || byteBuffer.capacity() < length) {
            this.o = ByteBuffer.allocate(Math.max(128, length));
        }
        this.o.clear();
        CoderResult coderResultEncode = this.n.encode(charSequence instanceof CharBuffer ? (CharBuffer) charSequence : CharBuffer.wrap(charSequence), this.o, true);
        if (coderResultEncode.isError()) {
            try {
                coderResultEncode.throwException();
            } catch (CharacterCodingException e) {
                throw new Error(e);
            }
        }
        this.o.flip();
        return c(this.o);
    }

    private int c(ByteBuffer byteBuffer) {
        int iRemaining = byteBuffer.remaining();
        a((byte) 0);
        a(1, iRemaining, 1);
        ByteBuffer byteBuffer2 = this.a;
        int i = this.b - iRemaining;
        this.b = i;
        byteBuffer2.position(i);
        this.a.put(byteBuffer);
        return a();
    }

    private void e() {
        if (!this.h) {
            throw new AssertionError("FlatBuffers: you can only access the serialized buffer after it has been finished by FlatBufferBuilder.finish().");
        }
    }

    private void f() {
        if (this.g) {
            throw new AssertionError("FlatBuffers: object serialization must not be nested.");
        }
    }

    public final void b(int i) {
        f();
        int[] iArr = this.e;
        if (iArr == null || iArr.length < i) {
            this.e = new int[i];
        }
        this.f = i;
        Arrays.fill(this.e, 0, i, 0);
        this.g = true;
        this.i = d();
    }

    public final void a(boolean z) {
        if (this.m || z) {
            c(z);
            h(0);
        }
    }

    public final void a(int i, byte b) {
        if (this.m || b != 0) {
            a(b);
            h(i);
        }
    }

    public final void a(int i, short s) {
        if (this.m || s != 0) {
            b(s);
            h(i);
        }
    }

    public final void a(int i, int i2) {
        if (this.m || i2 != 0) {
            g(i2);
            h(i);
        }
    }

    public final void a(int i, long j) {
        if (this.m || j != 0) {
            b(j);
            h(i);
        }
    }

    public final void b(int i, int i2) {
        if (this.m || i2 != 0) {
            a(i2);
            h(i);
        }
    }

    private void h(int i) {
        this.e[i] = d();
    }

    public final int b() {
        int i;
        int i2;
        if (this.e == null || !this.g) {
            throw new AssertionError("FlatBuffers: endObject called without startObject");
        }
        g(0);
        int iD = d();
        for (int i3 = this.f - 1; i3 >= 0; i3--) {
            int[] iArr = this.e;
            b((short) (iArr[i3] != 0 ? iD - iArr[i3] : 0));
        }
        b((short) (iD - this.i));
        b((short) ((this.f + 2) * 2));
        int i4 = 0;
        loop1: while (true) {
            if (i4 >= this.k) {
                i = 0;
                break;
            }
            int iCapacity = this.a.capacity() - this.j[i4];
            int i5 = this.b;
            short s = this.a.getShort(iCapacity);
            if (s == this.a.getShort(i5)) {
                for (2; i2 < s; i2 + 2) {
                    i2 = this.a.getShort(iCapacity + i2) == this.a.getShort(i5 + i2) ? i2 + 2 : 2;
                }
                i = this.j[i4];
                break loop1;
            }
            i4++;
        }
        if (i != 0) {
            int iCapacity2 = this.a.capacity() - iD;
            this.b = iCapacity2;
            this.a.putInt(iCapacity2, i - iD);
        } else {
            int i6 = this.k;
            int[] iArr2 = this.j;
            if (i6 == iArr2.length) {
                this.j = Arrays.copyOf(iArr2, i6 * 2);
            }
            int[] iArr3 = this.j;
            int i7 = this.k;
            this.k = i7 + 1;
            iArr3[i7] = d();
            ByteBuffer byteBuffer = this.a;
            byteBuffer.putInt(byteBuffer.capacity() - iD, d() - iD);
        }
        this.g = false;
        return iD;
    }

    public final void c(int i) {
        c(this.d, 4);
        a(i);
        this.a.position(this.b);
        this.h = true;
    }

    private byte[] d(int i, int i2) {
        e();
        byte[] bArr = new byte[i2];
        this.a.position(i);
        this.a.get(bArr);
        return bArr;
    }

    public final byte[] c() {
        return d(this.b, this.a.capacity() - this.b);
    }
}
