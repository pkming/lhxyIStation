package com.lianhexinye.m90.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.mvp.busset.LineNameModel;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class LineChoiceAdapter extends PagedListAdapter<LineNameModel, ViewHolderLine> {
    private Context context;
    private List<LineNameModel> data;
    private LayoutInflater inflater;

    public LineChoiceAdapter() {
        super(new DiffUtil.ItemCallback<LineNameModel>() { // from class: com.lianhexinye.m90.ui.adapter.LineChoiceAdapter.1
            @Override // androidx.recyclerview.widget.DiffUtil.ItemCallback
            public boolean areItemsTheSame(LineNameModel lineNameModel, LineNameModel lineNameModel2) {
                return lineNameModel.getLineNumber().equals(lineNameModel2.getLineNumber());
            }

            @Override // androidx.recyclerview.widget.DiffUtil.ItemCallback
            public boolean areContentsTheSame(LineNameModel lineNameModel, LineNameModel lineNameModel2) {
                return lineNameModel.getBusName().equals(lineNameModel2.getBusName());
            }
        });
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolderLine onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolderLine(this.inflater.inflate(R.layout.item_line_choice, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ViewHolderLine viewHolderLine, int i) {
        LineNameModel item = getItem(i);
        if (item == null) {
            viewHolderLine.cbLine.setText(item.getBusName());
            viewHolderLine.tvLineNumber.setText(item.getLineNumber());
            viewHolderLine.tvUpStartName.setText(item.getUpStartName());
            viewHolderLine.tvUpEndName.setText(item.getUpEndName());
            viewHolderLine.tvDownStartName.setText(item.getDownStartName());
            viewHolderLine.tvDownEndName.setText(item.getDownEndName());
            int attribute = item.getAttribute();
            if (attribute == 1) {
                viewHolderLine.tvLineAttribute.setText(R.string.basic_newspaper_attribute_open);
            } else if (attribute == 2) {
                viewHolderLine.tvLineAttribute.setText(R.string.basic_newspaper_attribute_loop);
            } else if (attribute == 3) {
                viewHolderLine.tvLineAttribute.setText(R.string.basic_newspaper_attribute_anti);
            }
            if (item.isChecked()) {
                viewHolderLine.cbLine.setChecked(true);
            } else {
                viewHolderLine.cbLine.setChecked(false);
            }
        }
    }

    public class ViewHolderLine extends RecyclerView.ViewHolder {
        private CheckBox cbLine;
        private TextView tvDownEndName;
        private TextView tvDownStartName;
        private TextView tvLineAttribute;
        private TextView tvLineNumber;
        private TextView tvUpEndName;
        private TextView tvUpStartName;

        public ViewHolderLine(View view) {
            super(view);
            this.cbLine = (CheckBox) view.findViewById(R.id.cbLine);
            this.tvLineNumber = (TextView) view.findViewById(R.id.tvLineNumber);
            this.tvUpStartName = (TextView) view.findViewById(R.id.tvUpStartName);
            this.tvUpEndName = (TextView) view.findViewById(R.id.tvUpEndName);
            this.tvDownStartName = (TextView) view.findViewById(R.id.tvDownStartName);
            this.tvDownEndName = (TextView) view.findViewById(R.id.tvDownEndName);
            this.tvLineAttribute = (TextView) view.findViewById(R.id.tvLineAttribute);
        }
    }
}
