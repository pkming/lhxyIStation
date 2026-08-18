package com.lianhexinye.m90.ui.activity;

import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class VideoMonitorActivity_ViewBinding implements Unbinder {
    private VideoMonitorActivity target;
    private View view7f090215;

    public VideoMonitorActivity_ViewBinding(VideoMonitorActivity videoMonitorActivity) {
        this(videoMonitorActivity, videoMonitorActivity.getWindow().getDecorView());
    }

    public VideoMonitorActivity_ViewBinding(final VideoMonitorActivity videoMonitorActivity, View view) {
        this.target = videoMonitorActivity;
        videoMonitorActivity.coreSurface = (SurfaceView) Utils.findRequiredViewAsType(view, R.id.core_surface, "field 'coreSurface'", SurfaceView.class);
        videoMonitorActivity.lyDvrCameraOpen = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.lyDvrCameraOpen, "field 'lyDvrCameraOpen'", LinearLayout.class);
        videoMonitorActivity.lyDvrSurfaceView = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.lyDvrSurfaceView, "field 'lyDvrSurfaceView'", LinearLayout.class);
        View viewFindRequiredView = Utils.findRequiredView(view, R.id.tvBack, "field 'tvBack' and method 'onViewClicked'");
        videoMonitorActivity.tvBack = (TextView) Utils.castView(viewFindRequiredView, R.id.tvBack, "field 'tvBack'", TextView.class);
        this.view7f090215 = viewFindRequiredView;
        viewFindRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.VideoMonitorActivity_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                videoMonitorActivity.onViewClicked(view2);
            }
        });
        videoMonitorActivity.lyBut = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.lyBut, "field 'lyBut'", LinearLayout.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        VideoMonitorActivity videoMonitorActivity = this.target;
        if (videoMonitorActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        videoMonitorActivity.coreSurface = null;
        videoMonitorActivity.lyDvrCameraOpen = null;
        videoMonitorActivity.lyDvrSurfaceView = null;
        videoMonitorActivity.tvBack = null;
        videoMonitorActivity.lyBut = null;
        this.view7f090215.setOnClickListener(null);
        this.view7f090215 = null;
    }
}
