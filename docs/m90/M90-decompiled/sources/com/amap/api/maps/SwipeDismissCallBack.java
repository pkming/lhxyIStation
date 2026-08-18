package com.amap.api.maps;

import android.view.View;
import com.amap.api.maps.SwipeDismissTouchListener;

/* JADX INFO: loaded from: classes2.dex */
public class SwipeDismissCallBack implements SwipeDismissTouchListener.DismissCallbacks {
    SwipeDismissView a;

    @Override // com.amap.api.maps.SwipeDismissTouchListener.DismissCallbacks
    public boolean canDismiss(Object obj) {
        return true;
    }

    public SwipeDismissCallBack(SwipeDismissView swipeDismissView) {
        this.a = swipeDismissView;
    }

    @Override // com.amap.api.maps.SwipeDismissTouchListener.DismissCallbacks
    public void onDismiss(View view, Object obj) {
        if (this.a.onDismissCallback != null) {
            this.a.onDismissCallback.onDismiss();
        }
    }

    @Override // com.amap.api.maps.SwipeDismissTouchListener.DismissCallbacks
    public void onNotifySwipe() {
        if (this.a.onDismissCallback != null) {
            this.a.onDismissCallback.onNotifySwipe();
        }
    }
}
