package com.autonavi.base.ae.gmap.glanimation;

import com.amap.api.maps.AMap;
import com.autonavi.base.ae.gmap.GLMapState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class AdglMapAnimationMgr {
    private List<AbstractAdglAnimation> list = Collections.synchronizedList(new ArrayList());
    private AMap.CancelableCallback mCancelCallback;
    private MapAnimationListener mMapAnimationListener;

    public interface MapAnimationListener {
        void onMapAnimationFinish(AMap.CancelableCallback cancelableCallback);
    }

    public void setMapCoreListener() {
    }

    public synchronized void clearAnimations() {
        this.list.clear();
    }

    public synchronized int getAnimationsCount() {
        return this.list.size();
    }

    public synchronized void doAnimations(GLMapState gLMapState) {
        if (gLMapState == null) {
            return;
        }
        if (this.list.size() <= 0) {
            return;
        }
        AbstractAdglAnimation abstractAdglAnimation = this.list.get(0);
        if (abstractAdglAnimation == null) {
            return;
        }
        if (abstractAdglAnimation.isOver()) {
            MapAnimationListener mapAnimationListener = this.mMapAnimationListener;
            if (mapAnimationListener != null) {
                mapAnimationListener.onMapAnimationFinish(this.mCancelCallback);
            }
            this.list.remove(abstractAdglAnimation);
            return;
        }
        abstractAdglAnimation.doAnimation(gLMapState);
    }

    public void addAnimation(AbstractAdglAnimation abstractAdglAnimation, AMap.CancelableCallback cancelableCallback) {
        if (abstractAdglAnimation == null) {
            return;
        }
        synchronized (this.list) {
            if (!abstractAdglAnimation.isOver() && this.list.size() > 0) {
                AbstractAdglAnimation abstractAdglAnimation2 = this.list.get(r1.size() - 1);
                if (abstractAdglAnimation2 != null && (abstractAdglAnimation instanceof AdglMapAnimGroup) && (abstractAdglAnimation2 instanceof AdglMapAnimGroup) && ((AdglMapAnimGroup) abstractAdglAnimation).typeEqueal((AdglMapAnimGroup) abstractAdglAnimation2) && !((AdglMapAnimGroup) abstractAdglAnimation).needMove) {
                    this.list.remove(abstractAdglAnimation2);
                }
            }
            this.list.add(abstractAdglAnimation);
            this.mCancelCallback = cancelableCallback;
        }
    }

    public AMap.CancelableCallback getCancelCallback() {
        return this.mCancelCallback;
    }

    public void setMapAnimationListener(MapAnimationListener mapAnimationListener) {
        synchronized (this) {
            this.mMapAnimationListener = mapAnimationListener;
        }
    }
}
