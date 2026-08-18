package com.lianhexinye.m90.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class VoiceCallActivity_ViewBinding implements Unbinder {
    private VoiceCallActivity target;

    public VoiceCallActivity_ViewBinding(VoiceCallActivity voiceCallActivity) {
        this(voiceCallActivity, voiceCallActivity.getWindow().getDecorView());
    }

    public VoiceCallActivity_ViewBinding(VoiceCallActivity voiceCallActivity, View view) {
        this.target = voiceCallActivity;
        voiceCallActivity.toolbarTitle = (TextView) Utils.findRequiredViewAsType(view, R.id.toolbar_title, "field 'toolbarTitle'", TextView.class);
        voiceCallActivity.toolbar = (Toolbar) Utils.findRequiredViewAsType(view, R.id.toolbar, "field 'toolbar'", Toolbar.class);
        voiceCallActivity.etVoiceIP = (EditText) Utils.findRequiredViewAsType(view, R.id.etVoiceIP, "field 'etVoiceIP'", EditText.class);
        voiceCallActivity.etVoiceState = (TextView) Utils.findRequiredViewAsType(view, R.id.etVoiceState, "field 'etVoiceState'", TextView.class);
        voiceCallActivity.butCollectionOperation = (Button) Utils.findRequiredViewAsType(view, R.id.butCollectionOperation, "field 'butCollectionOperation'", Button.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        VoiceCallActivity voiceCallActivity = this.target;
        if (voiceCallActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        voiceCallActivity.toolbarTitle = null;
        voiceCallActivity.toolbar = null;
        voiceCallActivity.etVoiceIP = null;
        voiceCallActivity.etVoiceState = null;
        voiceCallActivity.butCollectionOperation = null;
    }
}
