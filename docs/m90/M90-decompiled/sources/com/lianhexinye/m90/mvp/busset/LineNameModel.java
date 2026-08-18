package com.lianhexinye.m90.mvp.busset;

import com.lianhexinye.m90.mvp.BaseModel;

/* JADX INFO: loaded from: classes2.dex */
public class LineNameModel extends BaseModel {
    private int attribute;
    private String busName;
    private int direction;
    private String downEndName;
    private String downStartName;
    private String id;
    private boolean isChecked;
    private String lineNumber;
    private String upEndName;
    private String upStartName;

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public String getUpStartName() {
        return this.upStartName;
    }

    public void setUpStartName(String str) {
        this.upStartName = str;
    }

    public String getUpEndName() {
        return this.upEndName;
    }

    public void setUpEndName(String str) {
        this.upEndName = str;
    }

    public String getDownStartName() {
        return this.downStartName;
    }

    public void setDownStartName(String str) {
        this.downStartName = str;
    }

    public String getDownEndName() {
        return this.downEndName;
    }

    public void setDownEndName(String str) {
        this.downEndName = str;
    }

    public int getDirection() {
        return this.direction;
    }

    public void setDirection(int i) {
        this.direction = i;
    }

    public String getBusName() {
        return this.busName;
    }

    public void setBusName(String str) {
        this.busName = str;
    }

    public int getAttribute() {
        return this.attribute;
    }

    public void setAttribute(int i) {
        this.attribute = i;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked(boolean z) {
        this.isChecked = z;
    }

    public String getLineNumber() {
        return this.lineNumber;
    }

    public void setLineNumber(String str) {
        this.lineNumber = str;
    }
}
