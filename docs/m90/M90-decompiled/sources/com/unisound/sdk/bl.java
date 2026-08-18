package com.unisound.sdk;

import android.text.TextUtils;
import com.unisound.client.ErrorCode;
import com.unisound.client.IAudioSource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes2.dex */
public class bl extends bh {
    public static final int c = 50;
    private BlockingQueue<byte[]> g;
    private bm h;
    private boolean i;
    private volatile int j;
    private BlockingAudioTrack k;
    private boolean l;
    private boolean m;
    private Object n;
    private Boolean o;
    private Boolean p;
    private IAudioSource q;
    private bk r;
    private static final SimpleDateFormat e = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
    private static int f = 50;
    public static boolean d = false;

    public bl(bk bkVar) {
        super(bkVar.q().booleanValue(), bkVar.l());
        this.g = new LinkedBlockingQueue();
        this.i = false;
        this.j = 0;
        this.k = null;
        this.l = false;
        this.m = false;
        this.n = new Object();
        this.o = true;
        this.p = true;
        this.r = bkVar;
    }

    public static byte[] a(short[] sArr) {
        if (sArr == null) {
            return null;
        }
        byte[] bArr = new byte[sArr.length * 2];
        for (int i = 0; i < sArr.length; i++) {
            short s = sArr[i];
            int i2 = i * 2;
            bArr[i2] = (byte) (s & 255);
            bArr[i2 + 1] = (byte) ((s & 65280) >> 8);
        }
        return bArr;
    }

    public static void b(boolean z) {
        d = z;
    }

    private void c(boolean z) {
        bm bmVar = this.h;
        if (bmVar != null) {
            bmVar.a(z);
        }
    }

    private void d(int i) {
        bm bmVar = this.h;
        if (bmVar != null) {
            bmVar.b(i);
        }
        this.m = false;
    }

    public static boolean l() {
        return d;
    }

    private boolean m() {
        return this.i;
    }

    private void n() {
        bm bmVar = this.h;
        if (bmVar != null) {
            bmVar.a();
        }
    }

    private void o() {
        bm bmVar = this.h;
        if (bmVar != null) {
            bmVar.b();
        }
    }

    private void p() {
        bm bmVar = this.h;
        if (bmVar != null) {
            bmVar.c();
        }
    }

    private void q() {
        this.m = false;
        bm bmVar = this.h;
        if (bmVar != null) {
            bmVar.e();
        }
    }

    private void r() {
        bm bmVar = this.h;
        if (bmVar != null) {
            bmVar.f();
        }
    }

    private void s() {
        bm bmVar = this.h;
        if (bmVar != null) {
            bmVar.g();
        }
        this.m = false;
    }

    private String t() {
        return e.format(new Date(System.currentTimeMillis())) + ".pcm";
    }

    private boolean u() {
        return this.j > 0;
    }

    private void v() {
        synchronized (this.n) {
            if (this.l) {
                this.l = false;
                com.unisound.common.r.c("lockObject notify..");
                this.n.notify();
                r();
            }
        }
    }

    private void w() {
        this.l = true;
    }

    public void a(IAudioSource iAudioSource) {
        this.q = iAudioSource;
        if (iAudioSource == null) {
            this.q = new com.unisound.common.e(this.r);
        }
    }

    public void a(bm bmVar) {
        this.h = bmVar;
    }

    public void a(Boolean bool) {
        this.o = bool;
    }

    public void a(byte[] bArr) {
        this.j += bArr.length;
        this.g.add(bArr);
        if (this.p.booleanValue()) {
            n();
            this.p = false;
        }
    }

    @Override // com.unisound.sdk.bh
    public void b() {
        super.b();
        if (this.q == null) {
            BlockingAudioTrack blockingAudioTrack = this.k;
            if (blockingAudioTrack != null) {
                v();
                blockingAudioTrack.stop();
                return;
            }
            return;
        }
        v();
        synchronized (this.n) {
            IAudioSource iAudioSource = this.q;
            if (iAudioSource != null) {
                iAudioSource.closeAudioOut();
                this.q = null;
            }
        }
    }

    public void b(int i) {
        bm bmVar = this.h;
        if (bmVar != null) {
            bmVar.a(i);
        }
    }

    public void c(int i) {
        if (isAlive()) {
            j();
            try {
                super.join(i);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
    }

    @Override // com.unisound.sdk.bh
    public void d() {
        super.d();
        w();
    }

    @Override // com.unisound.sdk.bh
    public void f() {
        super.f();
        v();
    }

    public bm g() {
        return this.h;
    }

    public void h() {
        this.i = true;
    }

    public boolean i() {
        return this.h == null;
    }

    public void j() {
        this.h = null;
        this.o = true;
        this.i = true;
    }

    public boolean k() {
        return this.m;
    }

    @Override // com.unisound.common.f, java.lang.Thread, java.lang.Runnable
    public void run() {
        BufferedOutputStream bufferedOutputStream;
        super.run();
        com.unisound.common.r.b("TTSPlayThread run(): play start");
        if (!c() || TextUtils.isEmpty(this.r.r())) {
            bufferedOutputStream = null;
        } else {
            File file = new File(this.r.r(), t());
            File parentFile = file.getParentFile();
            if (parentFile != null) {
                parentFile.mkdirs();
            }
            try {
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file, true));
            } catch (FileNotFoundException e2) {
                e2.printStackTrace();
                bufferedOutputStream = null;
            }
        }
        IAudioSource iAudioSource = this.q;
        if (iAudioSource == null) {
            BlockingAudioTrack blockingAudioTrack = new BlockingAudioTrack(this.r.z(), this.r.x(), 2, 1);
            this.k = blockingAudioTrack;
            blockingAudioTrack.init();
            this.k.start();
        } else {
            if (iAudioSource.openAudioOut() != 0) {
                d(ErrorCode.TTS_ERROR_AUDIOSOURCE_OPEN);
                return;
            }
            b(true);
        }
        int iW = ((this.r.w() * 16000) * 2) / 1000;
        while (!this.o.booleanValue()) {
            try {
                try {
                    try {
                        Thread.sleep(50L);
                    } catch (Exception e3) {
                        e3.printStackTrace();
                        d(ErrorCode.TTS_ERROR_PLAYING_EXCEPTION);
                        if (this.q != null) {
                            synchronized (this.n) {
                                IAudioSource iAudioSource2 = this.q;
                                if (iAudioSource2 != null) {
                                    iAudioSource2.closeAudioOut();
                                    this.q = null;
                                }
                            }
                        } else {
                            BlockingAudioTrack blockingAudioTrack2 = this.k;
                            if (blockingAudioTrack2 != null) {
                                blockingAudioTrack2.stop();
                                this.k.waitAndRelease();
                                this.k = null;
                            }
                        }
                        this.l = false;
                        if (bufferedOutputStream != null) {
                            bufferedOutputStream.flush();
                            bufferedOutputStream.close();
                        }
                    }
                } catch (Throwable th) {
                    if (this.q != null) {
                        synchronized (this.n) {
                            IAudioSource iAudioSource3 = this.q;
                            if (iAudioSource3 != null) {
                                iAudioSource3.closeAudioOut();
                                this.q = null;
                            }
                        }
                    } else {
                        BlockingAudioTrack blockingAudioTrack3 = this.k;
                        if (blockingAudioTrack3 != null) {
                            blockingAudioTrack3.stop();
                            this.k.waitAndRelease();
                            this.k = null;
                        }
                    }
                    this.l = false;
                    if (bufferedOutputStream != null) {
                        try {
                            bufferedOutputStream.flush();
                            bufferedOutputStream.close();
                        } catch (IOException e4) {
                            e4.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (IOException e5) {
                e5.printStackTrace();
            }
        }
        while (!a() && !m() && this.j < iW) {
            Thread.sleep(50L);
        }
        o();
        if (u()) {
            p();
            while (!a()) {
                byte[] bArrPoll = this.g.poll(f, TimeUnit.MILLISECONDS);
                if (bArrPoll != null) {
                    int length = bArrPoll.length;
                    if (this.j >= iW) {
                        c(true);
                    } else {
                        c(false);
                    }
                    this.j -= length;
                    int i = 0;
                    while (length > 0 && !a()) {
                        synchronized (this.n) {
                            if (this.l) {
                                com.unisound.common.r.b("TTSPlayThread run(): lockObject wait...");
                                q();
                                this.n.wait();
                            } else {
                                this.m = true;
                            }
                        }
                        int i2 = 1600 > length ? length : 1600;
                        if (this.q != null) {
                            byte[] bArr = new byte[i2];
                            System.arraycopy(bArrPoll, i, bArr, 0, i2);
                            com.unisound.common.r.f("TTSPlayThread run : before writeData ");
                            this.q.writeData(bArr, i2);
                            com.unisound.common.r.f("TTSPlayThread run : after writeData ");
                        } else {
                            this.k.write(bArrPoll, i, i2);
                        }
                        i += i2;
                        length -= i2;
                    }
                    if (bufferedOutputStream != null && c()) {
                        bufferedOutputStream.write(bArrPoll, 0, bArrPoll.length);
                    }
                } else if (m()) {
                    break;
                }
            }
            s();
        }
        if (this.q != null) {
            synchronized (this.n) {
                IAudioSource iAudioSource4 = this.q;
                if (iAudioSource4 != null) {
                    iAudioSource4.closeAudioOut();
                    this.q = null;
                }
            }
        } else {
            BlockingAudioTrack blockingAudioTrack4 = this.k;
            if (blockingAudioTrack4 != null) {
                blockingAudioTrack4.stop();
                this.k.waitAndRelease();
                this.k = null;
            }
        }
        this.l = false;
        if (bufferedOutputStream != null) {
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        }
        b(false);
        b(0);
        this.j = 0;
        com.unisound.common.r.b("TTSPlayThread run(): play end");
    }
}
