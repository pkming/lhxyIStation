package com.lhxy.istationdevice.android11.app;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

/**
 * 旧版系统信息-版本信息页。
 */
public final class LegacySystemVersionFragment extends Fragment {
    private TextView tvSVersionCode;
    private TextView tvVersionCode;
    private TextView tvVersionName;
    private TextView tvDataVersionCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f_versioninfo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvSVersionCode = view.findViewById(R.id.tvSVersionCode);
        tvVersionCode = view.findViewById(R.id.tvVersionCode);
        tvVersionName = view.findViewById(R.id.tvVersionName);
        tvDataVersionCode = view.findViewById(R.id.tvDataVersionCode);
        render();
    }

    @Override
    public void onResume() {
        super.onResume();
        render();
    }

    private void render() {
        if (getContext() == null) {
            return;
        }
        ShellConfig shellConfig = ShellRuntime.get().getActiveConfig();
        PackageInfo packageInfo = loadPackageInfo();
        String hardwareVersion = shellConfig == null ? Build.MODEL : shellConfig.getDeviceProfile();
        String softwareCode = packageInfo == null ? "-" : String.valueOf(resolveVersionCode(packageInfo));
        String softwareName = packageInfo == null ? "-" : safe(packageInfo.versionName);
        String dataVersion = shellConfig == null ? "-" : safe(shellConfig.getConfigVersion());

        if (tvSVersionCode != null) {
            tvSVersionCode.setText(getString(R.string.version_hardware, hardwareVersion));
        }
        if (tvVersionCode != null) {
            tvVersionCode.setText(getString(R.string.version_software_number, softwareCode));
        }
        if (tvVersionName != null) {
            tvVersionName.setText(getString(R.string.version_software_name, softwareName));
        }
        if (tvDataVersionCode != null) {
            tvDataVersionCode.setText(getString(R.string.version_data_number, dataVersion));
        }
    }

    @Nullable
    private PackageInfo loadPackageInfo() {
        if (getContext() == null) {
            return null;
        }
        PackageManager packageManager = getContext().getPackageManager();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return packageManager.getPackageInfo(getContext().getPackageName(), PackageManager.PackageInfoFlags.of(0));
            }
            return packageManager.getPackageInfo(getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private long resolveVersionCode(PackageInfo packageInfo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return packageInfo.getLongVersionCode();
        }
        return packageInfo.versionCode;
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}
