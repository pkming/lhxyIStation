package com.lianhexinye.m90.greendao.gen;

import com.lianhexinye.m90.mvp.BaseModel;

/* JADX INFO: loaded from: classes2.dex */
public class ConfigInfoModel extends BaseModel {
    private Long _id;
    private String configExplain;
    private String configItem;
    private String configValue;
    private String filePath;

    public ConfigInfoModel(Long l, String str, String str2, String str3, String str4) {
        this._id = l;
        this.configItem = str;
        this.configValue = str2;
        this.configExplain = str3;
        this.filePath = str4;
    }

    public ConfigInfoModel() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long l) {
        this._id = l;
    }

    public String getConfigItem() {
        return this.configItem;
    }

    public void setConfigItem(String str) {
        this.configItem = str;
    }

    public String getConfigValue() {
        return this.configValue;
    }

    public void setConfigValue(String str) {
        this.configValue = str;
    }

    public String getConfigExplain() {
        return this.configExplain;
    }

    public void setConfigExplain(String str) {
        this.configExplain = str;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String str) {
        this.filePath = str;
    }
}
