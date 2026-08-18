package android.webkit;

import android.webkit.JsResult;

/* JADX INFO: loaded from: classes.dex */
public class JsPromptResult extends JsResult {
    private String mStringResult;

    public void confirm(String str) {
        this.mStringResult = str;
        confirm();
    }

    public JsPromptResult(JsResult.ResultReceiver resultReceiver) {
        super(resultReceiver);
    }

    public String getStringResult() {
        return this.mStringResult;
    }
}
