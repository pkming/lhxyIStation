package com.lianhexinye.canport;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/* JADX INFO: loaded from: classes2.dex */
public class SetDataDialog extends Dialog {
    private final String TAG;
    Activity context;
    private CanData dCanData;
    private View.OnClickListener dClickListener;
    private final View.OnFocusChangeListener focusChangeListener;

    public /* synthetic */ void lambda$new$0$SetDataDialog(View view, boolean z) {
        int i;
        EditText editText = (EditText) view;
        String string = editText.getText().toString();
        if (string.equals("")) {
            string = "0";
        }
        if (string.matches("\\d+")) {
            i = Integer.parseInt(string);
            Log.d("CanDevice", "丢失焦点后获取的数值: " + i);
        } else {
            i = 0;
        }
        if (i < 0 || i > 255) {
            editText.setText("");
            Toast.makeText(this.context, "超过允许发送的范围:0~255, 请重新输入", 0).show();
        }
    }

    public SetDataDialog(Activity activity) {
        super(activity);
        this.dClickListener = null;
        this.TAG = "CanDevice";
        this.focusChangeListener = new View.OnFocusChangeListener() { // from class: com.lianhexinye.canport.-$$Lambda$SetDataDialog$moMVpJrqE83cnlSfdjdfiY1mLfg
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View view, boolean z) {
                this.f$0.lambda$new$0$SetDataDialog(view, z);
            }
        };
        this.context = activity;
    }

    public SetDataDialog(Activity activity, CanData canData) {
        super(activity);
        this.dClickListener = null;
        this.TAG = "CanDevice";
        this.focusChangeListener = new View.OnFocusChangeListener() { // from class: com.lianhexinye.canport.-$$Lambda$SetDataDialog$moMVpJrqE83cnlSfdjdfiY1mLfg
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View view, boolean z) {
                this.f$0.lambda$new$0$SetDataDialog(view, z);
            }
        };
        this.context = activity;
        this.dCanData = canData;
    }

    public SetDataDialog(Activity activity, View.OnClickListener onClickListener) {
        super(activity);
        this.dClickListener = null;
        this.TAG = "CanDevice";
        this.focusChangeListener = new View.OnFocusChangeListener() { // from class: com.lianhexinye.canport.-$$Lambda$SetDataDialog$moMVpJrqE83cnlSfdjdfiY1mLfg
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View view, boolean z) {
                this.f$0.lambda$new$0$SetDataDialog(view, z);
            }
        };
        this.context = activity;
        this.dClickListener = onClickListener;
    }
}
