package com.lianhexinye.m90.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.greendao.gen.MaintenanceModel;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class MaintenanceChoiceAdapter extends BaseAdapter {
    private Context context;
    private List<MaintenanceModel> data;
    private LayoutInflater inflater;

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public MaintenanceChoiceAdapter(Context context, List<MaintenanceModel> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = list;
    }

    public void replaceAll(List<MaintenanceModel> list) {
        List<MaintenanceModel> list2 = this.data;
        if (list2 == null || list2.isEmpty()) {
            return;
        }
        this.data.clear();
        this.data.addAll(list);
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
        MaintenanceModel maintenanceModel = this.data.get(i);
        if (view == null) {
            viewHolder = new ViewHolder();
            viewInflate = this.inflater.inflate(R.layout.item_maintenance_choice, viewGroup, false);
            viewHolder.cbInfo = (CheckBox) viewInflate.findViewById(R.id.cbInfo);
            viewHolder.tvInfoMNo = (TextView) viewInflate.findViewById(R.id.tvInfoMNo);
            viewHolder.tvInfoMContent = (TextView) viewInflate.findViewById(R.id.tvInfoMContent);
            viewInflate.setTag(viewHolder);
        } else {
            viewInflate = view;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tvInfoMNo.setText("" + maintenanceModel.getMId());
        viewHolder.tvInfoMContent.setText(maintenanceModel.getContent());
        if (maintenanceModel.isChecked()) {
            viewHolder.cbInfo.setChecked(true);
        } else {
            viewHolder.cbInfo.setChecked(false);
        }
        return viewInflate;
    }

    class ViewHolder {
        private CheckBox cbInfo;
        private TextView tvInfoMContent;
        private TextView tvInfoMNo;

        ViewHolder() {
        }
    }
}
