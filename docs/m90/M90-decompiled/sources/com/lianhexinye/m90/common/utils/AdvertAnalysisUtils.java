package com.lianhexinye.m90.common.utils;

import com.lianhexinye.m90.common.utils.greendao.GreenDaoUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import com.lianhexinye.m90.greendao.gen.BusMediaModel;
import com.lianhexinye.m90.greendao.gen.BusMediaModelDao;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class AdvertAnalysisUtils {
    public static boolean analyzeChannel(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            BusMediaModelDao busMediaModelDao = GreenDaoUtils.getSingleTon().getmDaoSession().getBusMediaModelDao();
            busMediaModelDao.deleteAll();
            JSONArray jSONArray = new JSONObject(getValueByName(jSONObject, "channel")).getJSONArray("playlist");
            if (jSONArray != null && jSONArray.length() > 0) {
                for (int i = 0; i < jSONArray.length(); i++) {
                    JSONArray jSONArray2 = ((JSONObject) jSONArray.opt(i)).getJSONArray("programs");
                    if (jSONArray2 != null && jSONArray2.length() > 0) {
                        for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                            JSONArray jSONArray3 = ((JSONObject) jSONArray2.opt(i2)).getJSONArray("regions");
                            if (jSONArray3 != null && jSONArray3.length() > 0) {
                                for (int i3 = 0; i3 < jSONArray3.length(); i3++) {
                                    JSONObject jSONObject2 = new JSONObject(((JSONObject) jSONArray3.opt(i3)).getString("content"));
                                    int i4 = Integer.parseInt(getValueByName(jSONObject2, "contentType"));
                                    if (i4 == 14) {
                                        BusMediaModel busMediaModel = new BusMediaModel();
                                        JSONArray jSONArray4 = jSONObject2.getJSONArray("images");
                                        String str2 = "";
                                        if (jSONArray4 != null && jSONArray4.length() > 0) {
                                            for (int i5 = 0; i5 < jSONArray4.length(); i5++) {
                                                LogUtils.d("AdvertPlatformService", "urlArray.getString(y):" + jSONArray4.getString(i5));
                                                str2 = str2 + jSONArray4.getString(i5).substring(0, jSONArray4.getString(i5).contains("?") ? jSONArray4.getString(i5).lastIndexOf(63) : jSONArray4.getString(i5).length()) + ",";
                                            }
                                        }
                                        LogUtils.d("AdvertPlatformService", "dUrl:" + str2);
                                        busMediaModel.setDownloadUrls(str2);
                                        busMediaModel.setContentType(i4);
                                        busMediaModel.setDownloadState(0);
                                        busMediaModel.setFileResult(0);
                                        busMediaModel.setDataState(0);
                                        busMediaModelDao.insert(busMediaModel);
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String getValueByName(JSONObject jSONObject, String str) throws JSONException {
        if (jSONObject.has(str)) {
            return jSONObject.getString(str);
        }
        return null;
    }
}
