package com.lianhexinye.canport;

/* JADX INFO: loaded from: classes2.dex */
public class CanData {
    String canData1;
    String canData2;
    String canData3;
    String canData4;
    String canData5;
    String canData6;
    String canData7;
    String canData8;
    String canId;

    public String getCanId() {
        return this.canId;
    }

    public void setCanId(String str) {
        this.canId = str;
    }

    public String getCanData1() {
        return this.canData1;
    }

    public void setCanData1(String str) {
        this.canData1 = str;
    }

    public String getCanData2() {
        return this.canData2;
    }

    public void setCanData2(String str) {
        this.canData2 = str;
    }

    public String getCanData3() {
        return this.canData3;
    }

    public void setCanData3(String str) {
        this.canData3 = str;
    }

    public String getCanData4() {
        return this.canData4;
    }

    public void setCanData4(String str) {
        this.canData4 = str;
    }

    public String getCanData5() {
        return this.canData5;
    }

    public void setCanData5(String str) {
        this.canData5 = str;
    }

    public String getCanData6() {
        return this.canData6;
    }

    public void setCanData6(String str) {
        this.canData6 = str;
    }

    public String getCanData7() {
        return this.canData7;
    }

    public void setCanData7(String str) {
        this.canData7 = str;
    }

    public String getCanData8() {
        return this.canData8;
    }

    public void setCanData8(String str) {
        this.canData8 = str;
    }

    public String toString() {
        return "CanData{ " + this.canId + '#' + this.canData1 + '#' + this.canData2 + '#' + this.canData3 + '#' + this.canData4 + '#' + this.canData5 + '#' + this.canData6 + '#' + this.canData7 + '#' + this.canData8 + " }";
    }

    public int[] toIntArray() {
        return new int[]{Integer.parseInt(this.canId), Integer.parseInt(this.canData1), Integer.parseInt(this.canData2), Integer.parseInt(this.canData3), Integer.parseInt(this.canData4), Integer.parseInt(this.canData5), Integer.parseInt(this.canData6), Integer.parseInt(this.canData7), Integer.parseInt(this.canData8)};
    }
}
