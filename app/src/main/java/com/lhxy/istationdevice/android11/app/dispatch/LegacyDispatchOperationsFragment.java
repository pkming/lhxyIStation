package com.lhxy.istationdevice.android11.app.dispatch;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
 * 先把旧四个动作入口接到新的调度状态机。
 */
public final class LegacyDispatchOperationsFragment extends Fragment {
    private TextView tvSummary;
    private View itemJoinOperation;
    private View itemConfirmDispatch;
    private View itemAckNotice;
    private View itemRequestCharge;
    private View itemVehicleFailure;
    private View itemStartBus;
    private View itemLeaveOperation;
    private final Handler refreshHandler = new Handler(Looper.getMainLooper());
    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            renderSummary();
            refreshHandler.postDelayed(this, 1000L);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f_dispatch, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ensureSummaryView(view);
        itemJoinOperation = bindAction(view, R.id.itemDispatchJoinOperation, "join_operation");
        itemConfirmDispatch = bindAction(view, R.id.itemDispatchConfirmDispatch, "confirm_dispatch");
        itemAckNotice = bindAction(view, R.id.itemDispatchAckNotice, "ack_notice");
        itemRequestCharge = bindAction(view, R.id.itemDispatchRequestCharge, "request_charge");
        itemVehicleFailure = bindAction(view, R.id.itemDispatchVehicleFailure, "vehicle_failure");
        itemStartBus = bindAction(view, R.id.itemDispatchStartBus, "start_bus");
        itemLeaveOperation = bindAction(view, R.id.itemDispatchLeaveOperation, "leave_operation");
        renderSummary();
    }

    @Override
    public void onResume() {
        super.onResume();
        renderSummary();
        refreshHandler.post(refreshRunnable);
    }

    @Override
    public void onPause() {
        refreshHandler.removeCallbacks(refreshRunnable);
        super.onPause();
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
            renderSummary();
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

    private void renderSummary() {
        if (tvSummary == null) {
            return;
        }
        DispatchState state = requireDispatchModule().getDispatchState();
        tvSummary.setText(
                "协议: " + state.getActiveProtocol()
                        + "\n班次: " + state.getScheduleNo() + " / 趟次 " + state.getTimesNo()
                        + "\n计划: " + state.getPlannedDepartureTime() + " / 到站 " + state.getPlannedArrivalTime()
                        + "\n运营: " + yesNo(state.isJoinedOperation())
                        + " / 调度确认: " + yesNo(state.isDispatchedConfirmed())
                        + " / 发车: " + yesNo(state.isStartedBus())
                        + "\n公告: " + state.getPendingNoticeMessage() + " / 已确认 " + yesNo(state.isPendingNoticeAcked())
                        + "\n状态: " + state.getDispatchMessage()
        );
        updateActionState(itemJoinOperation, !state.isJoinedOperation());
        updateActionState(itemConfirmDispatch, state.isJoinedOperation() && !state.isDispatchedConfirmed());
        updateActionState(itemAckNotice, !state.isPendingNoticeAcked());
        updateActionState(itemRequestCharge, true);
        updateActionState(itemVehicleFailure, true);
        updateActionState(itemStartBus, state.isDispatchedConfirmed() && !state.isStartedBus());
        updateActionState(itemLeaveOperation, state.isJoinedOperation());
    }

    private void ensureSummaryView(@NonNull View root) {
        if (!(root instanceof LinearLayout) || getContext() == null) {
            return;
        }
        LinearLayout container = (LinearLayout) root;
        TextView summary = new TextView(getContext());
        summary.setTextColor(ContextCompat.getColor(requireContext(), R.color.c_fafafa));
        summary.setTextSize(16f);
        summary.setPadding(dp(16), dp(8), dp(16), dp(12));
        container.addView(summary, 0);
        tvSummary = summary;
    }

    private int dp(int value) {
        return Math.round(value * requireContext().getResources().getDisplayMetrics().density);
    }

    private void updateActionState(@Nullable View target, boolean enabled) {
        if (target == null) {
            return;
        }
        target.setEnabled(enabled);
        target.setAlpha(enabled ? 1f : 0.45f);
    }

    private String yesNo(boolean value) {
        return value ? "是" : "否";
    }
}
