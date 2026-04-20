package com.lhxy.istationdevice.android11.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.gps.GpsSerialMonitor;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * 旧版系统信息-网络信息页。
 */
public final class LegacySystemNetworkFragment extends Fragment {
    private TextView tvGpsState;
    private TextView tvLanState;
    private TextView tv4GState;
    private TextView tvWifiIp;
    private TextView tvWifiState;
    private TextView tvServerIp1;
    private TextView tvServerState1;
    private TextView tvServerIp2;
    private TextView tvServerState2;
    private TextView tvWiredIp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f_networkinfo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvGpsState = view.findViewById(R.id.tvGpsState);
        tvLanState = view.findViewById(R.id.tvLanState);
        tv4GState = view.findViewById(R.id.tv4GState);
        tvWifiIp = view.findViewById(R.id.tvWifiIP);
        tvWifiState = view.findViewById(R.id.tvWifiState);
        tvServerIp1 = view.findViewById(R.id.tvServerIP1);
        tvServerState1 = view.findViewById(R.id.tvServerState1);
        tvServerIp2 = view.findViewById(R.id.tvServerIP2);
        tvServerState2 = view.findViewById(R.id.tvServerState2);
        tvWiredIp = view.findViewById(R.id.tvWiredIP);
        render();
    }

    @Override
    public void onResume() {
        super.onResume();
        render();
    }

    private void render() {
        Context context = getContext();
        if (context == null) {
            return;
        }
        ShellRuntime runtime = ShellRuntime.get();
        ShellConfig shellConfig = runtime.getActiveConfig();
        SocketClientAdapter socketClientAdapter = runtime.getSocketClientAdapter();
        GpsSerialMonitor gpsSerialMonitor = runtime.getGpsSerialMonitor();

        String wiredIp = findIpAddress("eth");
        String wifiIp = findIpAddress("wlan");
        boolean wifiConnected = hasTransport(context, NetworkCapabilities.TRANSPORT_WIFI);
        boolean mobileConnected = hasTransport(context, NetworkCapabilities.TRANSPORT_CELLULAR);

        bindText(tvGpsState, gpsSerialMonitor.isAttached()
                ? getString(R.string.connected)
                : getString(R.string.notopened));
        bindText(tvWiredIp, wiredIp);
        bindText(tvLanState, hasText(wiredIp)
                ? getString(R.string.connected)
                : getString(R.string.unconnected));
        bindText(tvWifiIp, wifiIp);
        bindText(tvWifiState, wifiConnected
                ? getString(R.string.connected)
                : getString(R.string.unconnected));
        bindText(tv4GState, mobileConnected
                ? getString(R.string.connected)
                : getString(R.string.unconnected));

        Iterator<Map.Entry<String, ShellConfig.SocketChannel>> iterator = shellConfig == null
                ? Collections.<Map.Entry<String, ShellConfig.SocketChannel>>emptyList().iterator()
                : shellConfig.getSocketChannels().entrySet().iterator();
        bindSocket(iterator.hasNext() ? iterator.next().getValue() : null, tvServerIp1, tvServerState1, socketClientAdapter);
        bindSocket(iterator.hasNext() ? iterator.next().getValue() : null, tvServerIp2, tvServerState2, socketClientAdapter);
    }

    private void bindSocket(
            @Nullable ShellConfig.SocketChannel channel,
            @Nullable TextView ipView,
            @Nullable TextView stateView,
            SocketClientAdapter socketClientAdapter
    ) {
        if (channel == null) {
            bindText(ipView, "-");
            bindText(stateView, getString(R.string.unconnected));
            return;
        }
        bindText(ipView, channel.getHost());
        bindText(stateView, socketClientAdapter.isConnected(channel.getChannelName())
                ? getString(R.string.connected)
                : getString(R.string.unconnected));
    }

    private boolean hasTransport(Context context, int transportType) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) {
            return false;
        }
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        return capabilities != null && capabilities.hasTransport(transportType);
    }

    private String findIpAddress(String interfacePrefix) {
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface == null || !networkInterface.isUp()) {
                    continue;
                }
                String name = networkInterface.getName();
                if (name == null || !name.startsWith(interfacePrefix)) {
                    continue;
                }
                for (InetAddress address : Collections.list(networkInterface.getInetAddresses())) {
                    if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Exception ignored) {
            // ignore
        }
        return "";
    }

    private void bindText(@Nullable TextView textView, String value) {
        if (textView == null) {
            return;
        }
        textView.setText(hasText(value) ? value : "-");
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
