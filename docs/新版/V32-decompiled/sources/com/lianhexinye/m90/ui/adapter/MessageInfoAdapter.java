package com.lianhexinye.m90.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.greendao.gen.MessageModel;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class MessageInfoAdapter extends BaseAdapter {
    private Context context;
    private List<MessageModel> data;
    private LayoutInflater inflater;

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public MessageInfoAdapter(Context context, List<MessageModel> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = list;
    }

    public void replaceAll(List<MessageModel> list) {
        List<MessageModel> list2 = this.data;
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
        MessageModel messageModel = this.data.get(i);
        if (view == null) {
            viewHolder = new ViewHolder();
            viewInflate = this.inflater.inflate(R.layout.item_message_view, viewGroup, false);
            viewHolder.tvInfoMessageNo = (TextView) viewInflate.findViewById(R.id.tvInfoMessageNo);
            viewHolder.tvInfoMessageTime = (TextView) viewInflate.findViewById(R.id.tvInfoMessageTime);
            viewHolder.tvInfoMessageContent = (TextView) viewInflate.findViewById(R.id.tvInfoMessageContent);
            viewInflate.setTag(viewHolder);
        } else {
            viewInflate = view;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tvInfoMessageNo.setText("" + messageModel.getMessageNo());
        viewHolder.tvInfoMessageTime.setText(messageModel.getMessageTime());
        viewHolder.tvInfoMessageContent.setText(messageModel.getMessageContent());
        return viewInflate;
    }

    class ViewHolder {
        private TextView tvInfoMessageContent;
        private TextView tvInfoMessageNo;
        private TextView tvInfoMessageTime;

        ViewHolder() {
        }
    }
}
