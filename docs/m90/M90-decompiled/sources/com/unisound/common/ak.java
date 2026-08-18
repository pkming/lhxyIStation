package com.unisound.common;

import android.content.Context;
import android.media.MediaPlayer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes2.dex */
public class ak {
    private MediaPlayer a;
    private Context b;
    private MediaPlayer.OnCompletionListener c;
    private int d;
    private String e;

    public ak(Context context) {
        this.b = context;
    }

    private void g() throws IOException {
        if (this.e == null) {
            if (b() != 0) {
                this.a = MediaPlayer.create(this.b, b());
            }
        } else {
            MediaPlayer mediaPlayer = new MediaPlayer();
            this.a = mediaPlayer;
            mediaPlayer.setDataSource(new FileInputStream(new File(this.e)).getFD());
            this.a.prepare();
        }
    }

    public String a() {
        return this.e;
    }

    public void a(int i) {
        this.e = null;
        this.d = i;
    }

    public void a(MediaPlayer.OnCompletionListener onCompletionListener) {
        this.c = onCompletionListener;
    }

    public void a(String str) {
        this.d = 0;
        this.e = str;
    }

    public int b() {
        return this.d;
    }

    public MediaPlayer.OnCompletionListener c() {
        return this.c;
    }

    public void d() {
        e();
        try {
            g();
            this.a.setOnCompletionListener(c());
            this.a.start();
            this.a.setLooping(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void e() {
        MediaPlayer mediaPlayer = this.a;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.a.release();
            this.a = null;
        }
    }

    public boolean f() {
        MediaPlayer mediaPlayer = this.a;
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }
}
