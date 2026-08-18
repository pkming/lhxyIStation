package com.amap.api.col.p0003sl;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.lianhexinye.m90.R;
import java.util.List;

/* JADX INFO: compiled from: OfflineListAdapter.java */
/* JADX INFO: loaded from: classes2.dex */
public final class et extends BaseExpandableListAdapter implements ExpandableListView.OnGroupCollapseListener, ExpandableListView.OnGroupExpandListener {
    private boolean[] a;
    private int b = -1;
    private List<OfflineMapProvince> c;
    private OfflineMapManager d;
    private Context e;

    @Override // android.widget.ExpandableListAdapter
    public final Object getChild(int i, int i2) {
        return null;
    }

    @Override // android.widget.ExpandableListAdapter
    public final long getChildId(int i, int i2) {
        return i2;
    }

    @Override // android.widget.ExpandableListAdapter
    public final long getGroupId(int i) {
        return i;
    }

    @Override // android.widget.ExpandableListAdapter
    public final boolean hasStableIds() {
        return true;
    }

    @Override // android.widget.ExpandableListAdapter
    public final boolean isChildSelectable(int i, int i2) {
        return true;
    }

    public et(List<OfflineMapProvince> list, OfflineMapManager offlineMapManager, Context context) {
        this.c = null;
        this.c = list;
        this.d = offlineMapManager;
        this.e = context;
        this.a = new boolean[list.size()];
    }

    @Override // android.widget.ExpandableListAdapter
    public final int getGroupCount() {
        int i = this.b;
        return i == -1 ? this.c.size() : i;
    }

    @Override // android.widget.ExpandableListAdapter
    public final Object getGroup(int i) {
        return this.c.get(i).getProvinceName();
    }

    @Override // android.widget.ExpandableListAdapter
    public final int getChildrenCount(int i) {
        if (a(i)) {
            return this.c.get(i).getCityList().size();
        }
        return this.c.get(i).getCityList().size();
    }

    @Override // android.widget.ExpandableListAdapter
    public final View getGroupView(int i, boolean z, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = (RelativeLayout) fb.a(this.e, R.array.arrayport);
        }
        TextView textView = (TextView) view.findViewById(R.dimen.abc_action_button_min_height_material);
        ImageView imageView = (ImageView) view.findViewById(R.dimen.abc_action_button_min_width_material);
        textView.setText(this.c.get(i).getProvinceName());
        if (this.a[i]) {
            imageView.setImageDrawable(fb.a().getDrawable(R.animator.mtrl_card_state_list_anim));
        } else {
            imageView.setImageDrawable(fb.a().getDrawable(R.animator.mtrl_chip_state_list_anim));
        }
        return view;
    }

    @Override // android.widget.ExpandableListAdapter
    public final View getChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup) {
        a aVar;
        if (view != null) {
            aVar = (a) view.getTag();
        } else {
            aVar = new a();
            ex exVar = new ex(this.e, this.d);
            exVar.a(1);
            View viewA = exVar.a();
            aVar.a = exVar;
            viewA.setTag(aVar);
            view = viewA;
        }
        aVar.a.a(this.c.get(i).getCityList().get(i2));
        return view;
    }

    private boolean a(int i) {
        return (i == 0 || i == getGroupCount() - 1) ? false : true;
    }

    public final void a() {
        this.b = -1;
        notifyDataSetChanged();
    }

    public final void b() {
        this.b = 0;
        notifyDataSetChanged();
    }

    /* JADX INFO: compiled from: OfflineListAdapter.java */
    public final class a {
        public ex a;

        public a() {
        }
    }

    @Override // android.widget.ExpandableListView.OnGroupCollapseListener
    public final void onGroupCollapse(int i) {
        this.a[i] = false;
    }

    @Override // android.widget.ExpandableListView.OnGroupExpandListener
    public final void onGroupExpand(int i) {
        this.a[i] = true;
    }
}
