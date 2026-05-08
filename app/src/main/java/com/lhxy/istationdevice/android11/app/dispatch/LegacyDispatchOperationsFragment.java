package com.lhxy.istationdevice.android11.app.dispatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.domain.module.DispatchBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.ModuleRunResult;
import com.lhxy.istationdevice.android11.domain.module.TerminalBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.state.DispatchState;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

/**
 * 旧版调度中心-调度动作页。
 * <p>
 * 按 M90 的 11 个职业请求入口直接挂到调度模块。
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
        bindAction(view, R.id.butRequestSchedule, "request_schedule");
        bindAction(view, R.id.butRequestHandover, "request_handover");
        bindAction(view, R.id.butRequestOil, "request_oil");
        bindAction(view, R.id.butRequestAerate, "request_aerate");
        bindAction(view, R.id.butRequestCharge, "request_charge");
        bindAction(view, R.id.butExitOperation, "leave_operation");
        bindAction(view, R.id.butManualStart, "manual_start");
        bindAction(view, R.id.butManualEnd, "manual_end");
        bindAction(view, R.id.butRequestCharter, "request_charter");
        bindAction(view, R.id.butRequestRepair, "request_repair");
        bindAction(view, R.id.butOtherRequests, "other_requests");
        bindAction(view, R.id.butIntercom, "intercom");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    private View bindAction(View root, int viewId, String actionKey) {
        View target = root.findViewById(viewId);
        if (target == null) {
            return null;
        }
        target.setOnClickListener(v -> {
            ModuleRunResult result = ShellRuntime.get().getModuleHub()
                    .runAction(requireDispatchModule().getKey(), actionKey, buildTraceId(actionKey));
            if (getContext() != null) {
                Toast.makeText(getContext(), result.describeInline(), Toast.LENGTH_SHORT).show();
            }
        });
        return target;
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
