package com.lhxy.istationdevice.android11.app.line;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lhxy.istationdevice.android11.app.R;

import java.util.List;

final class LegacyLineChoiceAdapter extends RecyclerView.Adapter<LegacyLineChoiceAdapter.ViewHolder> {
    interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    private final LayoutInflater inflater;
    private final List<LegacyLineCatalog.LineProfile> data;
    private final OnItemSelectedListener onItemSelectedListener;
    private int selectedPosition;

    LegacyLineChoiceAdapter(
            @NonNull Context context,
            @NonNull List<LegacyLineCatalog.LineProfile> data,
            int selectedPosition,
            @NonNull OnItemSelectedListener onItemSelectedListener
    ) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
        this.selectedPosition = selectedPosition;
        this.onItemSelectedListener = onItemSelectedListener;
    }

    void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = inflater.inflate(R.layout.item_legacy_line_choice, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LegacyLineCatalog.LineProfile profile = data.get(position);
        holder.cbLine.setText(profile.getLineName());
        holder.cbLine.setChecked(position == selectedPosition);
        holder.tvLineNumber.setText(profile.getLineNumber());
        holder.tvUpStartName.setText(profile.getUpstreamStartName());
        holder.tvUpEndName.setText(profile.getUpstreamEndName());
        holder.tvDownStartName.setText(profile.getDownstreamStartName());
        holder.tvDownEndName.setText(profile.getDownstreamEndName());
        holder.tvLineAttribute.setText(profile.getLineAttribute());
        holder.cbLine.setOnClickListener(v -> {
            setSelectedPosition(position);
            onItemSelectedListener.onItemSelected(position);
        });
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox cbLine;
        private final TextView tvLineNumber;
        private final TextView tvUpStartName;
        private final TextView tvUpEndName;
        private final TextView tvDownStartName;
        private final TextView tvDownEndName;
        private final TextView tvLineAttribute;

        private ViewHolder(@NonNull View root) {
            super(root);
            cbLine = root.findViewById(R.id.cbLine);
            tvLineNumber = root.findViewById(R.id.tvLineNumber);
            tvUpStartName = root.findViewById(R.id.tvUpStartName);
            tvUpEndName = root.findViewById(R.id.tvUpEndName);
            tvDownStartName = root.findViewById(R.id.tvDownStartName);
            tvDownEndName = root.findViewById(R.id.tvDownEndName);
            tvLineAttribute = root.findViewById(R.id.tvLineAttribute);
        }
    }
}