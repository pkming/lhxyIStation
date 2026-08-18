package com.amap.api.maps.offlinemap;

import android.content.Context;
import android.media.MediaMetadataEditor;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

/* JADX INFO: loaded from: classes2.dex */
public class DownLoadListView extends ListView {
    public DownLoadListView(Context context) {
        super(context);
    }

    public DownLoadListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(MediaMetadataEditor.KEY_EDITABLE_MASK, Integer.MIN_VALUE));
    }
}
