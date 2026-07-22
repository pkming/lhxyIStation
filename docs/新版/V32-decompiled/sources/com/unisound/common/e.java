package com.unisound.common;

import android.media.AudioRecord;
import com.unisound.client.IAudioSource;
import com.unisound.sdk.BlockingAudioTrack;
import com.unisound.sdk.bk;

/* JADX INFO: loaded from: classes2.dex */
public class e implements IAudioSource {
    public static final int a = 16000;
    protected static int b = 16;
    protected static int c = 2;
    protected static int d = 4;
    protected static int e = 1;
    private static int f = 16000;
    private static int g = 6400;
    private cn.yunzhisheng.asr.a j;
    private bk k;
    private AudioRecord h = null;
    private Object i = new Object();
    private BlockingAudioTrack l = null;

    static {
        int minBufferSize = AudioRecord.getMinBufferSize(16000, 16, 2);
        if (g < minBufferSize) {
            g = minBufferSize;
        }
    }

    public e() {
    }

    public e(cn.yunzhisheng.asr.a aVar) {
        this.j = aVar;
    }

    public e(bk bkVar) {
        this.k = bkVar;
    }

    private int a() {
        synchronized (this.i) {
            BlockingAudioTrack blockingAudioTrack = new BlockingAudioTrack(this.k.z(), this.k.x(), 2, 1);
            this.l = blockingAudioTrack;
            blockingAudioTrack.init();
            this.l.start();
        }
        return 0;
    }

    private int a(byte[] bArr, int i) {
        BlockingAudioTrack blockingAudioTrack = this.l;
        if (blockingAudioTrack != null) {
            return blockingAudioTrack.write(bArr, 0, i);
        }
        return 0;
    }

    private int b(byte[] bArr, int i) {
        AudioRecord audioRecord = this.h;
        if (audioRecord != null) {
            return audioRecord.read(bArr, 0, i);
        }
        return 0;
    }

    private void b() {
        synchronized (this.i) {
            BlockingAudioTrack blockingAudioTrack = this.l;
            if (blockingAudioTrack != null) {
                blockingAudioTrack.stop();
                this.l.waitAndRelease();
                this.l = null;
            }
        }
    }

    private int c() {
        AudioRecord audioRecord = new AudioRecord(this.j.c(), this.j.d(), b, c, g);
        this.h = audioRecord;
        if (audioRecord.getState() != 1) {
            return -1;
        }
        this.h.startRecording();
        return 0;
    }

    private void d() {
        if (this.h != null) {
            r.c("IAudioSource::close audioRecord.stop()");
            if (this.h.getState() == 1) {
                this.h.stop();
            }
            r.c("IAudioSource::close audioRecord.release()");
            this.h.release();
            this.h = null;
            r.c("IAudioSource::close ok");
        }
    }

    @Override // com.unisound.client.IAudioSource
    public void closeAudioIn() {
        d();
    }

    @Override // com.unisound.client.IAudioSource
    public void closeAudioOut() {
        b();
    }

    @Override // com.unisound.client.IAudioSource
    public int openAudioIn() {
        return c();
    }

    @Override // com.unisound.client.IAudioSource
    public int openAudioOut() {
        return a();
    }

    @Override // com.unisound.client.IAudioSource
    public int readData(byte[] bArr, int i) {
        return b(bArr, i);
    }

    @Override // com.unisound.client.IAudioSource
    public int writeData(byte[] bArr, int i) {
        return a(bArr, i);
    }
}
