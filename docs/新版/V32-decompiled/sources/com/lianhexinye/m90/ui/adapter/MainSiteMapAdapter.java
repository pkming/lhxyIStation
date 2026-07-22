package com.lianhexinye.m90.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.greendao.gen.BusLineModel;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class MainSiteMapAdapter extends BaseAdapter {
    private Context context;
    private int currentIndex;
    private List<BusLineModel> data;
    private LayoutInflater inflater;
    private boolean isNextStation;
    private String[] priceArrays;

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public MainSiteMapAdapter(Context context, List<BusLineModel> list, String[] strArr, int i, boolean z) {
        this.priceArrays = null;
        this.currentIndex = 0;
        this.isNextStation = false;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = list;
        this.priceArrays = strArr;
        this.currentIndex = i;
        this.isNextStation = z;
    }

    public void setPriceArrays(String[] strArr) {
        this.priceArrays = strArr;
    }

    public void setCurrentIndex(int i) {
        this.currentIndex = i;
    }

    public void setISNextStation(boolean z) {
        this.isNextStation = z;
    }

    public void cleanList() {
        this.data.clear();
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.data.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return this.data.get(i);
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        View viewInflate;
        ViewHolder viewHolder;
        BusLineModel busLineModel = this.data.get(i);
        if (view == null) {
            viewHolder = new ViewHolder();
            viewInflate = this.inflater.inflate(R.layout.item_site_map, viewGroup, false);
            viewHolder.tvPriceBusName = (TextView) viewInflate.findViewById(R.id.tvPriceBusName);
            viewInflate.setTag(viewHolder);
        } else {
            viewInflate = view;
            viewHolder = (ViewHolder) view.getTag();
        }
        if (i < this.priceArrays.length) {
            viewHolder.tvPriceBusName.setText(this.priceArrays[i] + "-" + busLineModel.getBusName());
        } else {
            viewHolder.tvPriceBusName.setText(busLineModel.getBusName());
        }
        return viewInflate;
    }

    class ViewHolder {
        private TextView tvBusNumber;
        private TextView tvPriceBusName;

        ViewHolder() {
        }
    }
}
