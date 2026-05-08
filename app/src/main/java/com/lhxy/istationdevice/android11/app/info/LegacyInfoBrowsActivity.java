package com.lhxy.istationdevice.android11.app.info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.app.common.LegacyBaseActivity;
import com.lhxy.istationdevice.android11.core.LegacyInfoMessageRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 旧版信息浏览页。
 * <p>
 * 对齐 M90，只显示消息仓中的持久消息。
 */
public final class LegacyInfoBrowsActivity extends LegacyBaseActivity {
    private final List<InfoRow> rows = new ArrayList<>();
    private ListView listView;
    private LinearLayout emptyView;
    private InfoRowAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.act_infobrows;
    }

    @Override
    protected int getTitleResId() {
        return R.string.infobrows_title;
    }

    @Override
    protected void onPageReady(Bundle savedInstanceState) {
        listView = findViewById(R.id.lvInfoMessage);
        emptyView = findViewById(R.id.lyNotInfoMessage);
        if (listView != null) {
            adapter = new InfoRowAdapter();
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                private int currentNum = -1;

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (currentNum == -1) {
                        currentNum = position;
                    } else if (currentNum == position) {
                        currentNum = -1;
                    } else {
                        currentNum = position;
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }
        refreshContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_del, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.del || item.getItemId() != R.id.index) {
            return true;
        }
        setResult(RESULT_OK);
        finish();
        return true;
    }

    private void refreshContent() {
        rows.clear();
        rows.addAll(buildRows());
        boolean hasMessages = !rows.isEmpty();
        if (listView != null) {
            listView.setVisibility(hasMessages ? View.VISIBLE : View.GONE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(hasMessages ? View.GONE : View.VISIBLE);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private List<InfoRow> buildRows() {
        List<InfoRow> result = new ArrayList<>();
        List<LegacyInfoMessageRepository.InfoMessage> infoMessages = LegacyInfoMessageRepository.snapshot(this);
        appendInfoMessages(result, infoMessages);
        return result;
    }

    private void appendInfoMessages(List<InfoRow> target, List<LegacyInfoMessageRepository.InfoMessage> messages) {
        for (LegacyInfoMessageRepository.InfoMessage message : messages) {
            target.add(new InfoRow(
                    String.valueOf(message.getNumber()),
                    safe(message.getMessageTime()),
                    safe(message.getContent())
            ));
        }
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private static final class InfoRow {
        private final String number;
        private final String time;
        private final String content;

        private InfoRow(String number, String time, String content) {
            this.number = number;
            this.time = time;
            this.content = content;
        }
    }

    private final class InfoRowAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return rows.size();
        }

        @Override
        public Object getItem(int position) {
            return rows.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_legacy_info_line, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            InfoRow row = rows.get(position);
            holder.numberView.setText(row.number);
            holder.timeView.setText(row.time);
            holder.contentView.setText(row.content);
            return convertView;
        }
    }

    private static final class ViewHolder {
        private final TextView numberView;
        private final TextView timeView;
        private final TextView contentView;

        private ViewHolder(View root) {
            numberView = root.findViewById(R.id.tvInfoMessageNo);
            timeView = root.findViewById(R.id.tvInfoMessageTime);
            contentView = root.findViewById(R.id.tvInfoMessageContent);
        }
    }
}
