package com.lhxy.istationdevice.android11.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lhxy.istationdevice.android11.domain.module.DispatchBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.ModuleRunResult;
import com.lhxy.istationdevice.android11.domain.module.TerminalBusinessModule;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

/**
 * 旧版调度中心-调度动作页。
 * <p>
 * 先把旧四个动作入口接到新的调度状态机。
 */
public final class LegacyDispatchOperationsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f_dispatch, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindAction(view, R.id.itemDispatchJoinOperation, "join_operation");
        bindAction(view, R.id.itemDispatchRequestCharge, "request_charge");
        bindAction(view, R.id.itemDispatchVehicleFailure, "vehicle_failure");
        bindAction(view, R.id.itemDispatchLeaveOperation, "leave_operation");
    }

    private void bindAction(View root, int viewId, String actionKey) {
        View target = root.findViewById(viewId);
        if (target == null) {
            return;
        }
        target.setOnClickListener(v -> {
            ModuleRunResult result = ShellRuntime.get().getModuleHub()
                    .runAction(requireDispatchModule().getKey(), actionKey, buildTraceId(actionKey));
            if (getContext() != null) {
                Toast.makeText(getContext(), result.describeInline(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private DispatchBusinessModule requireDispatchModule() {
        TerminalBusinessModule module = ShellRuntime.get().getModuleHub().findModule("dispatch");
        if (module instanceof DispatchBusinessModule) {
            return (DispatchBusinessModule) module;
        }
        throw new IllegalStateException("调度模块未就绪");
    }

    private String buildTraceId(String actionKey) {
        return "legacy-dispatch-" + actionKey + "-" + System.currentTimeMillis();
    }
}
