package com.lianhexinye.m90.ui.fragment.systeminfo;

import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class NetworkInfoFragment_ViewBinding implements Unbinder {
    private NetworkInfoFragment target;

    public NetworkInfoFragment_ViewBinding(NetworkInfoFragment networkInfoFragment, View view) {
        this.target = networkInfoFragment;
        networkInfoFragment.tvGpsState = (TextView) Utils.findRequiredViewAsType(view, R.id.tvGpsState, "field 'tvGpsState'", TextView.class);
        networkInfoFragment.tvLanState = (TextView) Utils.findRequiredViewAsType(view, R.id.tvLanState, "field 'tvLanState'", TextView.class);
        networkInfoFragment.tv4GState = (TextView) Utils.findRequiredViewAsType(view, R.id.tv4GState, "field 'tv4GState'", TextView.class);
        networkInfoFragment.tvWifiIP = (TextView) Utils.findRequiredViewAsType(view, R.id.tvWifiIP, "field 'tvWifiIP'", TextView.class);
        networkInfoFragment.tvWifiState = (TextView) Utils.findRequiredViewAsType(view, R.id.tvWifiState, "field 'tvWifiState'", TextView.class);
        networkInfoFragment.tvServerIP1 = (TextView) Utils.findRequiredViewAsType(view, R.id.tvServerIP1, "field 'tvServerIP1'", TextView.class);
        networkInfoFragment.tvServerState1 = (TextView) Utils.findRequiredViewAsType(view, R.id.tvServerState1, "field 'tvServerState1'", TextView.class);
        networkInfoFragment.tvServerIP2 = (TextView) Utils.findRequiredViewAsType(view, R.id.tvServerIP2, "field 'tvServerIP2'", TextView.class);
        networkInfoFragment.tvServerState2 = (TextView) Utils.findRequiredViewAsType(view, R.id.tvServerState2, "field 'tvServerState2'", TextView.class);
        networkInfoFragment.tvWiredIP = (TextView) Utils.findRequiredViewAsType(view, R.id.tvWiredIP, "field 'tvWiredIP'", TextView.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        NetworkInfoFragment networkInfoFragment = this.target;
        if (networkInfoFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        networkInfoFragment.tvGpsState = null;
        networkInfoFragment.tvLanState = null;
        networkInfoFragment.tv4GState = null;
        networkInfoFragment.tvWifiIP = null;
        networkInfoFragment.tvWifiState = null;
        networkInfoFragment.tvServerIP1 = null;
        networkInfoFragment.tvServerState1 = null;
        networkInfoFragment.tvServerIP2 = null;
        networkInfoFragment.tvServerState2 = null;
        networkInfoFragment.tvWiredIP = null;
    }
}
