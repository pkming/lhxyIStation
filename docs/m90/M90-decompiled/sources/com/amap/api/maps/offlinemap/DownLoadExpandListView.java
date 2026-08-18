package com.amap.api.maps.offlinemap;

import android.content.Context;
import android.media.MediaMetadataEditor;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

/* JADX INFO: loaded from: classes2.dex */
public class DownLoadExpandListView extends ExpandableListView {
    public DownLoadExpandListView(Context context) {
        super(context);
    }

    public DownLoadExpandListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(MediaMetadataEditor.KEY_EDITABLE_MASK, Integer.MIN_VALUE));
    }
}
