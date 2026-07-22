package com.lianhexinye.m90.mvp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.lianhexinye.m90.mvp.BasePresenter;
import com.unisound.client.SpeechConstants;

/* JADX INFO: loaded from: classes2.dex */
public abstract class MvpActivity<V, P extends BasePresenter> extends BaseActivity {
    private View decorView = null;
    protected P mvpPresenter;

    protected abstract P createPresenter();

    @Override // com.lianhexinye.m90.mvp.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        P p = (P) createPresenter();
        this.mvpPresenter = p;
        p.attachView(this);
        hideBottomUIMenu();
    }

    protected void hideBottomUIMenu() {
        if (Build.VERSION.SDK_INT < 19) {
            View decorView = getWindow().getDecorView();
            this.decorView = decorView;
            decorView.setSystemUiVisibility(8);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView2 = getWindow().getDecorView();
            this.decorView = decorView2;
            decorView2.setSystemUiVisibility(SpeechConstants.VPR_EVENT_RECORDING_STOP);
        }
    }

    @Override // com.lianhexinye.m90.mvp.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        P p = this.mvpPresenter;
        if (p != null) {
            p.detachView();
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        InputMethodManager inputMethodManager;
        if (motionEvent.getAction() == 0) {
            View currentFocus = getCurrentFocus();
            if (isShouldHideInput(currentFocus, motionEvent) && (inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)) != null) {
                inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
            return super.dispatchTouchEvent(motionEvent);
        }
        if (getWindow().superDispatchTouchEvent(motionEvent)) {
            return true;
        }
        return onTouchEvent(motionEvent);
    }

    public void showLoading() {
        showProgressDialog();
    }

    public void hideLoading() {
        dismissProgressDialog();
    }
}
