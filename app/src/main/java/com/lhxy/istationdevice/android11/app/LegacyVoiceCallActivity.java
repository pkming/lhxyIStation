package com.lhxy.istationdevice.android11.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 旧版语音通话页骨架。
 */
public final class LegacyVoiceCallActivity extends LegacyBaseActivity {
    private boolean connected;

    @Override
    protected int getLayoutId() {
        return R.layout.act_voice_call;
    }

    @Override
    protected int getTitleResId() {
        return R.string.voicecall_title;
    }

    @Override
    protected void onPageReady(Bundle savedInstanceState) {
        TextView stateView = findViewById(R.id.etVoiceState);
        EditText ipInput = findViewById(R.id.etVoiceIP);
        Button actionButton = findViewById(R.id.butCollectionOperation);
        if (stateView != null) {
            stateView.setText(R.string.voice_call_disconnect_txt);
        }
        if (actionButton != null) {
            actionButton.setOnClickListener(v -> {
                connected = !connected;
                if (stateView != null) {
                    stateView.setText(connected ? R.string.legacy_voice_connected : R.string.voice_call_disconnect_txt);
                }
                if (ipInput != null && connected && ipInput.getText() != null && ipInput.getText().length() == 0) {
                    ipInput.setText(R.string.legacy_voice_default_ip);
                }
                actionButton.setText(connected ? R.string.legacy_voice_disconnect_button : R.string.voice_call_connect_but);
            });
        }
    }
}
