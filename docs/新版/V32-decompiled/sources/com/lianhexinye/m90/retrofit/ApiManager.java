package com.lianhexinye.m90.retrofit;

import android.util.Log;
import com.lianhexinye.m90.AppApplication;
import com.lianhexinye.m90.common.utils.SPUserInfoUtils;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/* JADX INFO: loaded from: classes2.dex */
public class ApiManager {
    public static final String SERVER_PRODUCT;
    private static final String TAG = "ApiManager";
    public static final String apiKey = "ofcourseistillloveyou";
    public static ApiManagerService apiManager;
    private static Cache cache;
    private static File cacheDirectory;
    private static OkHttpClient client;
    private static final Retrofit sRetrofit;

    static {
        String str = (String) SPUserInfoUtils.get(AppApplication.getContext(), "netAdvertIP", "39.104.66.194");
        String str2 = (String) SPUserInfoUtils.get(AppApplication.getContext(), "netAdvertPort", "8081");
        String str3 = "http://" + String.valueOf(str.trim().length() > 0 ? str : "39.104.66.194") + ":" + String.valueOf(str2.trim().length() > 0 ? str2 : "8081");
        SERVER_PRODUCT = str3;
        cacheDirectory = new File(AppApplication.getInstance().getApplicationContext().getCacheDir().getAbsolutePath(), "MyCache");
        cache = new Cache(cacheDirectory, 10485760L);
        client = new OkHttpClient.Builder().addInterceptor(new LogInterceptor()).cache(cache).retryOnConnectionFailure(true).connectTimeout(30L, TimeUnit.SECONDS).writeTimeout(30L, TimeUnit.SECONDS).readTimeout(30L, TimeUnit.SECONDS).build();
        Retrofit retrofitBuild = new Retrofit.Builder().baseUrl(str3).client(client).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
        sRetrofit = retrofitBuild;
        apiManager = (ApiManagerService) retrofitBuild.create(ApiManagerService.class);
    }

    private static class LogInterceptor implements Interceptor {
        private LogInterceptor() {
        }

        @Override // okhttp3.Interceptor
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Log.e(ApiManager.TAG, "okhttp3:" + chain.request().toString());
            System.nanoTime();
            Response responseProceed = chain.proceed(chain.request());
            System.nanoTime();
            MediaType mediaTypeContentType = responseProceed.body().contentType();
            String strString = responseProceed.body().string();
            Log.e(ApiManager.TAG, "Url:" + responseProceed.request().url() + "; response body:" + strString);
            return responseProceed.newBuilder().body(ResponseBody.create(mediaTypeContentType, strString)).build();
        }
    }
}
