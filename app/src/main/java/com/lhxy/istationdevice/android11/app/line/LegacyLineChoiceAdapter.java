package com.lhxy.istationdevice.android11.app.line;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lhxy.istationdevice.android11.app.R;

import java.util.List;

final class LegacyLineChoiceAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<LegacyLineCatalog.LineProfile> data;
    private int selectedPosition;

    LegacyLineChoiceAdapter(@NonNull Context context, @NonNull List<LegacyLineCatalog.LineProfile> data, int selectedPosition) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
        this.selectedPosition = selectedPosition;
    }

    void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_legacy_line_choice, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LegacyLineCatalog.LineProfile profile = data.get(position);
        holder.cbLine.setText(profile.getLineName());
        holder.cbLine.setChecked(position == selectedPosition);
        holder.tvUpStartName.setText(profile.getUpstreamStartName());
        holder.tvUpEndName.setText(profile.getUpstreamEndName());
        holder.tvDownStartName.setText(profile.getDownstreamStartName());
        holder.tvDownEndName.setText(profile.getDownstreamEndName());
        holder.tvLineAttribute.setText(profile.getLineAttribute());
        return convertView;
    }

    private static final class ViewHolder {
        private final CheckBox cbLine;
        private final TextView tvUpStartName;
        private final TextView tvUpEndName;
        private final TextView tvDownStartName;
        private final TextView tvDownEndName;
        private final TextView tvLineAttribute;

        private ViewHolder(View root) {
            cbLine = root.findViewById(R.id.cbLine);
            tvUpStartName = root.findViewById(R.id.tvUpStartName);
            tvUpEndName = root.findViewById(R.id.tvUpEndName);
            tvDownStartName = root.findViewById(R.id.tvDownStartName);
            tvDownEndName = root.findViewById(R.id.tvDownEndName);
            tvLineAttribute = root.findViewById(R.id.tvLineAttribute);
        }
    }
}