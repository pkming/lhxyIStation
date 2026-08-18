package com.amap.api.maps.offlinemap;

import android.content.Context;
import android.media.MediaMetadataEditor;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

/* JADX INFO: loaded from: classes2.dex */
public class CityExpandView extends ExpandableListView {
    public CityExpandView(Context context) {
        this(context, null);
    }

    public CityExpandView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, -1);
        setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.view.View
    protected void onMeasure(int i, int i2) {
        System.currentTimeMillis();
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(MediaMetadataEditor.KEY_EDITABLE_MASK, Integer.MIN_VALUE));
        System.currentTimeMillis();
    }
}
