package com.lianhexinye.m90.retrofit;

import com.lianhexinye.m90.common.service.advert.NotifyResult;
import com.lianhexinye.m90.retrofit.request.AuthorizReqBody;
import com.lianhexinye.m90.retrofit.request.RegisterReqBody;
import com.lianhexinye.m90.retrofit.request.RequestBody;
import com.lianhexinye.m90.retrofit.request.UploadDownloadReqBody;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;

/* JADX INFO: loaded from: classes2.dex */
public interface ApiManagerService {
    @GET("/api/content/{contentId}")
    Call<NotifyResult> getChannelContent(@Path("contentId") String str);

    @GET("/api/notify/{macAddress}")
    Call<NotifyResult> getNotify(@Path("macAddress") String str);

    @GET("/api/Register/update/{userName}")
    Call<NotifyResult> getUpVersionInfo(@Path("userName") String str);

    @PUT("/api/MyInformation/{macAddress}")
    Observable<Result> myInformation(@Path("macAddress") String str, @Body AuthorizReqBody authorizReqBody);

    @GET("/api/Myinformation/{macAddress}")
    Call<NotifyResult> myInformation(@Path("macAddress") String str);

    @POST
    Call<NotifyResult> postNotify(@Url String str);

    @POST("/api/register")
    Observable<Result> register(@Body RegisterReqBody registerReqBody);

    @POST("/pos/Index.ashx")
    Observable<Result> sendSMS(@Body RequestBody requestBody);

    @POST("/api/Myinformation")
    Call<NotifyResult> uploadDownloadInfo(@Body UploadDownloadReqBody uploadDownloadReqBody);
}
