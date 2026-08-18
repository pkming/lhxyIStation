package com.tbruyelle.rxpermissions2;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import io.reactivex.subjects.PublishSubject;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class RxPermissionsFragment extends Fragment {
    private static final int PERMISSIONS_REQUEST_CODE = 42;
    private boolean mLogging;
    private Map<String, PublishSubject<Permission>> mSubjects = new HashMap();

    @Override // android.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    void requestPermissions(String[] strArr) {
        requestPermissions(strArr, 42);
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i != 42) {
            return;
        }
        boolean[] zArr = new boolean[strArr.length];
        for (int i2 = 0; i2 < strArr.length; i2++) {
            zArr[i2] = shouldShowRequestPermissionRationale(strArr[i2]);
        }
        onRequestPermissionsResult(strArr, iArr, zArr);
    }

    void onRequestPermissionsResult(String[] strArr, int[] iArr, boolean[] zArr) {
        int length = strArr.length;
        for (int i = 0; i < length; i++) {
            log("onRequestPermissionsResult  " + strArr[i]);
            PublishSubject<Permission> publishSubject = this.mSubjects.get(strArr[i]);
            if (publishSubject == null) {
                Log.e("RxPermissions", "RxPermissions.onRequestPermissionsResult invoked but didn't find the corresponding permission request.");
                return;
            }
            this.mSubjects.remove(strArr[i]);
            publishSubject.onNext(new Permission(strArr[i], iArr[i] == 0, zArr[i]));
            publishSubject.onComplete();
        }
    }

    boolean isGranted(String str) {
        return getActivity().checkSelfPermission(str) == 0;
    }

    boolean isRevoked(String str) {
        return getActivity().getPackageManager().isPermissionRevokedByPolicy(str, getActivity().getPackageName());
    }

    public void setLogging(boolean z) {
        this.mLogging = z;
    }

    public PublishSubject<Permission> getSubjectByPermission(String str) {
        return this.mSubjects.get(str);
    }

    public boolean containsByPermission(String str) {
        return this.mSubjects.containsKey(str);
    }

    public PublishSubject<Permission> setSubjectForPermission(String str, PublishSubject<Permission> publishSubject) {
        return this.mSubjects.put(str, publishSubject);
    }

    void log(String str) {
        if (this.mLogging) {
            Log.d("RxPermissions", str);
        }
    }
}
