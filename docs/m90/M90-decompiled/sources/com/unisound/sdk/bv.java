package com.unisound.sdk;

import android.os.AsyncTask;

/* JADX INFO: loaded from: classes2.dex */
class bv extends AsyncTask<Object, Object, Object> {
    bu a;
    final /* synthetic */ bt b;

    public bv(bt btVar, bu buVar) {
        this.b = btVar;
        this.a = buVar;
    }

    public void a() {
        this.a = null;
    }

    @Override // android.os.AsyncTask
    protected Object doInBackground(Object... objArr) {
        com.unisound.common.r.b("USCAsyncTask doInBackground: " + objArr[0]);
        return this.a.a(objArr);
    }

    @Override // android.os.AsyncTask
    protected void onPostExecute(Object obj) {
        super.onPostExecute(obj);
        this.b.d = false;
        this.a.a(obj);
    }

    @Override // android.os.AsyncTask
    protected void onPreExecute() {
        this.b.d = true;
        super.onPreExecute();
    }
}
