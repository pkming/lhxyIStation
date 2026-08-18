package android.speech.tts;

import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public final class SynthesisRequest {
    private int mCallerUid;
    private String mCountry;
    private String mLanguage;
    private final Bundle mParams;
    private int mPitch;
    private int mSpeechRate;
    private final String mText;
    private String mVariant;

    public SynthesisRequest(String str, Bundle bundle) {
        this.mText = str;
        this.mParams = new Bundle(bundle);
    }

    public String getText() {
        return this.mText;
    }

    public String getLanguage() {
        return this.mLanguage;
    }

    public String getCountry() {
        return this.mCountry;
    }

    public String getVariant() {
        return this.mVariant;
    }

    public int getSpeechRate() {
        return this.mSpeechRate;
    }

    public int getPitch() {
        return this.mPitch;
    }

    public Bundle getParams() {
        return this.mParams;
    }

    public int getCallerUid() {
        return this.mCallerUid;
    }

    void setLanguage(String str, String str2, String str3) {
        this.mLanguage = str;
        this.mCountry = str2;
        this.mVariant = str3;
    }

    void setSpeechRate(int i) {
        this.mSpeechRate = i;
    }

    void setPitch(int i) {
        this.mPitch = i;
    }

    void setCallerUid(int i) {
        this.mCallerUid = i;
    }
}
