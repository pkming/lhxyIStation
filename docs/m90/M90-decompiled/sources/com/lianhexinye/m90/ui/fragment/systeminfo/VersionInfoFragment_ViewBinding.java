package com.lianhexinye.m90.ui.fragment.systeminfo;

import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class VersionInfoFragment_ViewBinding implements Unbinder {
    private VersionInfoFragment target;

    public VersionInfoFragment_ViewBinding(VersionInfoFragment versionInfoFragment, View view) {
        this.target = versionInfoFragment;
        versionInfoFragment.tvVersionCode = (TextView) Utils.findRequiredViewAsType(view, R.id.tvVersionCode, "field 'tvVersionCode'", TextView.class);
        versionInfoFragment.tvVersionName = (TextView) Utils.findRequiredViewAsType(view, R.id.tvVersionName, "field 'tvVersionName'", TextView.class);
        versionInfoFragment.tvSVersionCode = (TextView) Utils.findRequiredViewAsType(view, R.id.tvSVersionCode, "field 'tvSVersionCode'", TextView.class);
        versionInfoFragment.tvDataVersionCode = (TextView) Utils.findRequiredViewAsType(view, R.id.tvDataVersionCode, "field 'tvDataVersionCode'", TextView.class);
        versionInfoFragment.tvSourceVersionTime = (TextView) Utils.findRequiredViewAsType(view, R.id.tvSourceVersionTime, "field 'tvSourceVersionTime'", TextView.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        VersionInfoFragment versionInfoFragment = this.target;
        if (versionInfoFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        versionInfoFragment.tvVersionCode = null;
        versionInfoFragment.tvVersionName = null;
        versionInfoFragment.tvSVersionCode = null;
        versionInfoFragment.tvDataVersionCode = null;
        versionInfoFragment.tvSourceVersionTime = null;
    }
}
