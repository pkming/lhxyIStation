package com.lhxy.istationdevice.android11.app.sysinfo;

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

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.gps.GpsSerialMonitor;
import com.lhxy.istationdevice.android11.domain.module.DispatchBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.TerminalBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.state.DispatchState;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Collections;

/**
 * 旧版系统信息-网络信息页。
 */
public final class LegacySystemNetworkFragment extends Fragment {
    private static final int SERVER_PROBE_TIMEOUT_MILLIS = 1500;

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
    private int serverProbeVersion;

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        serverProbeVersion++;
        tvGpsState = null;
        tvLanState = null;
        tv4GState = null;
        tvWifiIp = null;
        tvWifiState = null;
        tvServerIp1 = null;
        tvServerState1 = null;
        tvServerIp2 = null;
        tvServerState2 = null;
        tvWiredIp = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        serverProbeVersion++;
        if (!hidden) {
            render();
        }
    }

    private void render() {
        Context context = getContext();
        if (context == null || isHidden()) {
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

        serverProbeVersion++;
        bindDispatchServer(resolvePrimaryServer(shellConfig), tvServerIp1, tvServerState1, socketClientAdapter, runtime);
        bindUpdateServer(resolveSecondaryServer(shellConfig), tvServerIp2, tvServerState2, context);
    }

    @Nullable
    private ShellConfig.SocketChannel resolvePrimaryServer(@Nullable ShellConfig shellConfig) {
        return resolveSocketChannel(shellConfig, shellConfig == null ? null : shellConfig.getDebugReplay().getJt808SocketKey(), true);
    }

    @Nullable
    private ShellConfig.SocketChannel resolveSecondaryServer(@Nullable ShellConfig shellConfig) {
        return resolveSocketChannel(shellConfig, shellConfig == null ? null : shellConfig.getDebugReplay().getAl808SocketKey(), false);
    }

    @Nullable
    private ShellConfig.SocketChannel resolveSocketChannel(
            @Nullable ShellConfig shellConfig,
            @Nullable String preferredKey,
            boolean primary
    ) {
        if (shellConfig == null || shellConfig.getSocketChannels().isEmpty()) {
            return null;
        }
        if (preferredKey != null && !preferredKey.trim().isEmpty()) {
            ShellConfig.SocketChannel preferred = shellConfig.getSocketChannels().get(preferredKey.trim());
            if (preferred != null) {
                return preferred;
            }
        }
        if (primary) {
            return shellConfig.getSocketChannels().values().iterator().next();
        }
        int index = 0;
        for (ShellConfig.SocketChannel channel : shellConfig.getSocketChannels().values()) {
            if (index == 1) {
                return channel;
            }
            index++;
        }
        return null;
    }

    private void bindDispatchServer(
            @Nullable ShellConfig.SocketChannel channel,
            @Nullable TextView ipView,
            @Nullable TextView stateView,
            @NonNull SocketClientAdapter socketClientAdapter,
            @NonNull ShellRuntime runtime
    ) {
        if (channel == null) {
            bindText(ipView, "-");
            bindText(stateView, getString(R.string.unconnected));
            return;
        }
        bindText(ipView, channel.getHost());
        boolean connected = isDispatchBusinessActive(runtime);
        if (!connected) {
            connected = socketClientAdapter.isConnected(channel.getChannelName());
        }
        bindText(stateView, connected ? getString(R.string.connected) : getString(R.string.unconnected));
    }

    private void bindUpdateServer(
            @Nullable ShellConfig.SocketChannel channel,
            @Nullable TextView ipView,
            @Nullable TextView stateView,
            @NonNull Context context
    ) {
        if (channel == null) {
            bindText(ipView, "-");
            bindText(stateView, getString(R.string.unconnected));
            return;
        }
        bindText(ipView, channel.getHost());
        bindText(stateView, getString(R.string.unconnected));
        if (!hasActiveNetwork(context) || !hasText(channel.getHost()) || channel.getPort() <= 0) {
            return;
        }
        int probeVersion = serverProbeVersion;
        new Thread(() -> {
            boolean reachable = probeServer(channel.getHost(), channel.getPort());
            if (!isAdded() || probeVersion != serverProbeVersion || stateView == null) {
                return;
            }
            stateView.post(() -> {
                if (!isAdded() || probeVersion != serverProbeVersion) {
                    return;
                }
                bindText(stateView, getString(reachable ? R.string.connected : R.string.unconnected));
            });
        }, "legacy-update-server-probe").start();
    }

    private boolean hasActiveNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) {
            return false;
        }
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    private boolean probeServer(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), SERVER_PROBE_TIMEOUT_MILLIS);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean isDispatchBusinessActive(@NonNull ShellRuntime runtime) {
        TerminalBusinessModule module = runtime.getModuleHub().findModule("dispatch");
        if (!(module instanceof DispatchBusinessModule)) {
            return false;
        }
        DispatchState dispatchState = ((DispatchBusinessModule) module).getDispatchState();
        if (dispatchState == null) {
            return false;
        }
        return dispatchState.isStartedBus()
                || dispatchState.isDispatchedConfirmed()
                || dispatchState.isJoinedOperation()
                || dispatchState.getLastUpdateTimeMillis() > 0L;
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
