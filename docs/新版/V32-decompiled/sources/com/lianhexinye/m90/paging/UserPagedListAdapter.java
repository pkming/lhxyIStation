package com.lianhexinye.m90.paging;

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

/* JADX INFO: loaded from: classes2.dex */
public class UserPagedListAdapter extends PagedListAdapter<LineNameModel, UserViewHolder> {
    private static final DiffUtil.ItemCallback<LineNameModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<LineNameModel>() { // from class: com.lianhexinye.m90.paging.UserPagedListAdapter.1
        @Override // androidx.recyclerview.widget.DiffUtil.ItemCallback
        public boolean areItemsTheSame(LineNameModel lineNameModel, LineNameModel lineNameModel2) {
            return lineNameModel.getId().equals(lineNameModel2.getId());
        }

        @Override // androidx.recyclerview.widget.DiffUtil.ItemCallback
        public boolean areContentsTheSame(LineNameModel lineNameModel, LineNameModel lineNameModel2) {
            return lineNameModel.getBusName().equals(lineNameModel2.getBusName()) && lineNameModel.isChecked() == lineNameModel2.isChecked();
        }
    };
    private OnItemSelectedListener onItemSelectedListener;
    private String selectedUserId;

    public interface OnItemSelectedListener {
        void onItemSelected(LineNameModel lineNameModel);
    }

    public UserPagedListAdapter() {
        super(DIFF_CALLBACK);
        this.selectedUserId = null;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new UserViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_line_choice, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(UserViewHolder userViewHolder, final int i) {
        final LineNameModel item = getItem(i);
        if (item != null) {
            userViewHolder.cbLine.setText(item.getBusName());
            userViewHolder.tvLineNumber.setText(item.getLineNumber());
            userViewHolder.tvUpStartName.setText(item.getUpStartName());
            userViewHolder.tvUpEndName.setText(item.getUpEndName());
            userViewHolder.tvDownStartName.setText(item.getDownStartName());
            userViewHolder.tvDownEndName.setText(item.getDownEndName());
            int attribute = item.getAttribute();
            if (attribute == 1) {
                userViewHolder.tvLineAttribute.setText(R.string.basic_newspaper_attribute_open);
            } else if (attribute == 2) {
                userViewHolder.tvLineAttribute.setText(R.string.basic_newspaper_attribute_loop);
            } else if (attribute == 3) {
                userViewHolder.tvLineAttribute.setText(R.string.basic_newspaper_attribute_anti);
            }
            userViewHolder.cbLine.setChecked(item.getId().equals(this.selectedUserId));
            userViewHolder.cbLine.setOnClickListener(new View.OnClickListener() { // from class: com.lianhexinye.m90.paging.-$$Lambda$UserPagedListAdapter$74_lMHDjDTw1uTtreP86D9l8cik
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$onBindViewHolder$0$UserPagedListAdapter(item, i, view);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onBindViewHolder$0$UserPagedListAdapter(LineNameModel lineNameModel, int i, View view) {
        int itemPositionById;
        String str = this.selectedUserId;
        this.selectedUserId = lineNameModel.getId();
        if (str != null && (itemPositionById = getItemPositionById(str)) != -1) {
            notifyItemChanged(itemPositionById);
        }
        notifyItemChanged(i);
        OnItemSelectedListener onItemSelectedListener = this.onItemSelectedListener;
        if (onItemSelectedListener != null) {
            onItemSelectedListener.onItemSelected(lineNameModel);
        }
    }

    private LineNameModel findItemById(int i) {
        for (int i2 = 0; i2 < getItemCount(); i2++) {
            LineNameModel item = getItem(i2);
            if (item != null && Integer.parseInt(item.getId()) == i) {
                return item;
            }
        }
        return null;
    }

    private int getItemPositionById(String str) {
        for (int i = 0; i < getItemCount(); i++) {
            LineNameModel item = getItem(i);
            if (item != null && item.getId().equals(str)) {
                return i;
            }
        }
        return -1;
    }

    public void setSelectedUserId(String str) {
        int itemPositionById;
        int itemPositionById2;
        String str2 = this.selectedUserId;
        this.selectedUserId = str;
        if (str2 != null && (itemPositionById2 = getItemPositionById(str2)) != -1) {
            notifyItemChanged(itemPositionById2);
        }
        if (str == null || (itemPositionById = getItemPositionById(str)) == -1) {
            return;
        }
        notifyItemChanged(itemPositionById);
    }

    public LineNameModel getSelectedUser() {
        int itemPositionById;
        String str = this.selectedUserId;
        if (str == null || (itemPositionById = getItemPositionById(str)) == -1) {
            return null;
        }
        return getItem(itemPositionById);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private CheckBox cbLine;
        private TextView tvDownEndName;
        private TextView tvDownStartName;
        private TextView tvLineAttribute;
        private TextView tvLineNumber;
        private TextView tvUpEndName;
        private TextView tvUpStartName;

        public UserViewHolder(View view) {
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
