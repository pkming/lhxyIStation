package com.lhxy.istationdevice.android11.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lhxy.istationdevice.android11.domain.module.ModuleRunResult;
import com.lhxy.istationdevice.android11.domain.module.SignInBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.TerminalBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.state.SignInState;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

/**
 * 旧版调度中心-司机考勤页。
 * <p>
 * 保留旧布局，直接把签到模块状态和动作挂进来。
 */
public final class LegacyDispatchAttendanceFragment extends Fragment {
    private TextView tvDriverId;
    private TextView tvDriverName;
    private RadioButton rbGoToSignIn;
    private RadioButton rbGoOffSignIn;
    private Button butAffirmOperation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f_attendance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvDriverId = view.findViewById(R.id.tvDriverID);
        tvDriverName = view.findViewById(R.id.tvDriverName);
        rbGoToSignIn = view.findViewById(R.id.rb_radio_goto_signin);
        rbGoOffSignIn = view.findViewById(R.id.rb_radio_gooff_signin);
        butAffirmOperation = view.findViewById(R.id.butAffirmOperation);
        if (butAffirmOperation != null) {
            butAffirmOperation.setOnClickListener(v -> handleConfirm());
        }
        render();
    }

    @Override
    public void onResume() {
        super.onResume();
        render();
    }

    private void handleConfirm() {
        SignInBusinessModule module = requireSignInModule();
        String actionKey = rbGoOffSignIn != null && rbGoOffSignIn.isChecked() ? "manual_sign_out" : "read_card";
        ModuleRunResult result = ShellRuntime.get().getModuleHub()
                .runAction(module.getKey(), actionKey, buildTraceId(actionKey));
        render();
        if (getContext() != null) {
            Toast.makeText(getContext(), result.describeInline(), Toast.LENGTH_SHORT).show();
        }
    }

    private void render() {
        SignInState state = requireSignInModule().getSignInState();
        if (tvDriverId != null) {
            tvDriverId.setText(state.getCardNo());
        }
        if (tvDriverName != null) {
            tvDriverName.setText(state.getDriverName());
        }
        if (rbGoToSignIn != null && rbGoOffSignIn != null) {
            if (state.isSignedIn()) {
                rbGoOffSignIn.setChecked(true);
            } else {
                rbGoToSignIn.setChecked(true);
            }
        }
        if (butAffirmOperation != null) {
            butAffirmOperation.setText(state.isSignedIn()
                    ? getString(R.string.dispatch_center_gooff_signin)
                    : getString(R.string.dispatch_center_goto_signin));
        }
    }

    private SignInBusinessModule requireSignInModule() {
        TerminalBusinessModule module = ShellRuntime.get().getModuleHub().findModule("signin");
        if (module instanceof SignInBusinessModule) {
            return (SignInBusinessModule) module;
        }
        throw new IllegalStateException("签到模块未就绪");
    }

    private String buildTraceId(String actionKey) {
        return "legacy-attendance-" + actionKey + "-" + System.currentTimeMillis();
    }
}
