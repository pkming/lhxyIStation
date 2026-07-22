package com.autonavi.base.ae.gmap.maploader;

import com.autonavi.base.ae.gmap.maploader.Pools;

/* JADX INFO: loaded from: classes2.dex */
public class ProcessingTile {
    private static final Pools.SynchronizedPool<ProcessingTile> M_POOL = new Pools.SynchronizedPool<>(30);
    public long mCreateTime = 0;
    public String mKeyName;

    public static ProcessingTile obtain(String str) {
        ProcessingTile processingTileAcquire = M_POOL.acquire();
        if (processingTileAcquire != null) {
            processingTileAcquire.setParams(str);
            return processingTileAcquire;
        }
        return new ProcessingTile(str);
    }

    public void recycle() {
        this.mKeyName = null;
        this.mCreateTime = 0L;
        M_POOL.release(this);
    }

    public ProcessingTile(String str) {
        setParams(str);
    }

    private void setParams(String str) {
        this.mKeyName = str;
        this.mCreateTime = System.currentTimeMillis() / 1000;
    }
}
