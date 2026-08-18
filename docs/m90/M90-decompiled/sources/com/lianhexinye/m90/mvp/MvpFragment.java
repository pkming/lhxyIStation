package com.lianhexinye.m90.mvp;

import android.os.Bundle;
import android.widget.Toast;
import com.lianhexinye.m90.mvp.BasePresenter;

/* JADX INFO: loaded from: classes2.dex */
public abstract class MvpFragment<V, P extends BasePresenter> extends BaseFragment {
    private static final String TAG = "MvpFragment";
    protected P mvpPresenter;

    protected abstract P createPresenter();

    @Override // com.lianhexinye.m90.mvp.BaseFragment
    public abstract void hide();

    public abstract void initView();

    @Override // com.lianhexinye.m90.mvp.BaseFragment
    public abstract void show();

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        P p = (P) createPresenter();
        this.mvpPresenter = p;
        p.attachView(this);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        initView();
    }

    public void toastShow(int i) {
        Toast.makeText(getActivity(), i, 0).show();
    }

    public void toastShow(String str) {
        Toast.makeText(getActivity(), str, 0).show();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        P p = this.mvpPresenter;
        if (p != null) {
            p.detachView();
        }
    }
}
