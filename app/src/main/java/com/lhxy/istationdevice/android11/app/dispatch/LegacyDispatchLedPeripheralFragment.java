package com.lhxy.istationdevice.android11.app.dispatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.core.LegacyInfoMessageRepository;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.station.LegacyStationDisplayUseCase;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LegacyDispatchLedPeripheralFragment extends Fragment {
    private final List<MaintenanceItem> items = new ArrayList<>();
    private final LegacyStationDisplayUseCase stationDisplayUseCase = new LegacyStationDisplayUseCase(ShellRuntime.get().getSerialPortAdapter());
    private int selectedIndex = -1;
    private MaintenanceAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f_led_peripheral, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        seedItems();
        ListView listView = view.findViewById(R.id.lvInfoLedPeripheral);
        Button sendButton = view.findViewById(R.id.butSendLedPeripheral);
        adapter = new MaintenanceAdapter();
        if (listView != null) {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, itemView, position, id) -> {
                selectedIndex = selectedIndex == position ? -1 : position;
                adapter.notifyDataSetChanged();
            });
        }
        if (sendButton != null) {
            sendButton.setOnClickListener(v -> handleSend());
        }
    }

    private void seedItems() {
        if (!items.isEmpty()) {
            return;
        }
        items.add(new MaintenanceItem(0, "本趟加油"));
        items.add(new MaintenanceItem(1, "回场充电"));
        items.add(new MaintenanceItem(2, "停止运营"));
        items.add(new MaintenanceItem(3, "事故结束"));
        items.add(new MaintenanceItem(4, "事故开始"));
        items.add(new MaintenanceItem(5, "紧急告警"));
        items.add(new MaintenanceItem(6, "包车开始"));
        items.add(new MaintenanceItem(7, "包车结束"));
        items.add(new MaintenanceItem(8, "交通事故"));
        items.add(new MaintenanceItem(9, "司机吃饭"));
        items.add(new MaintenanceItem(10, "前方路堵"));
        items.add(new MaintenanceItem(11, "本趟加气"));
        items.add(new MaintenanceItem(12, "车辆保养"));
    }

    private void handleSend() {
        if (selectedIndex < 0 || selectedIndex >= items.size()) {
            toast(getString(R.string.dispatch_center_led_peripheral_pick_first));
            return;
        }
        MaintenanceItem selected = items.get(selectedIndex);
        ShellConfig shellConfig = ShellRuntime.get().getActiveConfig();
        boolean sent = shellConfig != null && stationDisplayUseCase.sendLedAdvertisement(
                shellConfig,
                Collections.singletonList(selected.content),
                "dispatch-led-peripheral-" + System.currentTimeMillis()
        );
        if (getContext() != null) {
            LegacyInfoMessageRepository.append(requireContext(), selected.content);
        }
        if (!sent) {
            toast(getString(R.string.dispatch_center_led_peripheral_send_failed));
            return;
        }
        toast(getString(R.string.dispatch_center_led_peripheral_sent, selected.content));
    }

    private void toast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private final class MaintenanceAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_maintenance_choice, parent, false);
            }
            TextView indexView = view.findViewById(R.id.tvMaintenanceIndex);
            TextView contentView = view.findViewById(R.id.tvMaintenanceContent);
            CheckBox checkBox = view.findViewById(R.id.cbMaintenanceSelected);
            MaintenanceItem item = items.get(position);
            indexView.setText(String.valueOf(item.index));
            contentView.setText(item.content);
            checkBox.setChecked(position == selectedIndex);
            return view;
        }
    }

    private static final class MaintenanceItem {
        private final int index;
        private final String content;

        private MaintenanceItem(int index, String content) {
            this.index = index;
            this.content = content;
        }
    }
}