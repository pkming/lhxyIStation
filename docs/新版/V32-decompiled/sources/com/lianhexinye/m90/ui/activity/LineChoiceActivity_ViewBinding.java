package com.lianhexinye.m90.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class LineChoiceActivity_ViewBinding implements Unbinder {
    private LineChoiceActivity target;
    private View view7f09004a;

    public LineChoiceActivity_ViewBinding(LineChoiceActivity lineChoiceActivity) {
        this(lineChoiceActivity, lineChoiceActivity.getWindow().getDecorView());
    }

    public LineChoiceActivity_ViewBinding(final LineChoiceActivity lineChoiceActivity, View view) {
        this.target = lineChoiceActivity;
        lineChoiceActivity.toolbarTitle = (TextView) Utils.findRequiredViewAsType(view, R.id.toolbar_title, "field 'toolbarTitle'", TextView.class);
        lineChoiceActivity.toolbar = (Toolbar) Utils.findRequiredViewAsType(view, R.id.toolbar, "field 'toolbar'", Toolbar.class);
        lineChoiceActivity.rlToolbar = (RelativeLayout) Utils.findRequiredViewAsType(view, R.id.rlToolbar, "field 'rlToolbar'", RelativeLayout.class);
        lineChoiceActivity.lyLineInfoTitle = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.lyLineInfoTitle, "field 'lyLineInfoTitle'", LinearLayout.class);
        lineChoiceActivity.vLineList = Utils.findRequiredView(view, R.id.vLineList, "field 'vLineList'");
        View viewFindRequiredView = Utils.findRequiredView(view, R.id.butAffirm, "field 'butAffirm' and method 'onViewClicked'");
        lineChoiceActivity.butAffirm = (Button) Utils.castView(viewFindRequiredView, R.id.butAffirm, "field 'butAffirm'", Button.class);
        this.view7f09004a = viewFindRequiredView;
        viewFindRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lianhexinye.m90.ui.activity.LineChoiceActivity_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                lineChoiceActivity.onViewClicked();
            }
        });
        lineChoiceActivity.rlAffirm = (RelativeLayout) Utils.findRequiredViewAsType(view, R.id.rlAffirm, "field 'rlAffirm'", RelativeLayout.class);
        lineChoiceActivity.tvLineTip = (TextView) Utils.findRequiredViewAsType(view, R.id.tvLineTip, "field 'tvLineTip'", TextView.class);
        lineChoiceActivity.lyNotLine = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.lyNotLine, "field 'lyNotLine'", LinearLayout.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        LineChoiceActivity lineChoiceActivity = this.target;
        if (lineChoiceActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        lineChoiceActivity.toolbarTitle = null;
        lineChoiceActivity.toolbar = null;
        lineChoiceActivity.rlToolbar = null;
        lineChoiceActivity.lyLineInfoTitle = null;
        lineChoiceActivity.vLineList = null;
        lineChoiceActivity.butAffirm = null;
        lineChoiceActivity.rlAffirm = null;
        lineChoiceActivity.tvLineTip = null;
        lineChoiceActivity.lyNotLine = null;
        this.view7f09004a.setOnClickListener(null);
        this.view7f09004a = null;
    }
}
