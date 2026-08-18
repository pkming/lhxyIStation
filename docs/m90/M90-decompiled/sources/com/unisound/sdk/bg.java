package com.unisound.sdk;

import com.unisound.client.SpeechConstants;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
class bg {
    private int a = 0;
    private int b = 1;
    private boolean c = true;
    private boolean d = false;
    private int e = 500;
    private String f;
    private String g;
    private String h;
    private boolean i;
    private String j;
    private String k;
    private String l;

    protected bg() {
    }

    protected bg(String str) {
        com.unisound.common.o.a(str, m());
    }

    public String a() {
        return this.k;
    }

    public void a(int i) {
        this.e = i;
    }

    public void a(String str) {
        this.k = str;
    }

    public void a(boolean z) {
        this.i = z;
    }

    public String b() {
        return this.j;
    }

    protected void b(int i) {
        this.a = i;
    }

    public void b(String str) {
        this.j = str;
    }

    public void b(boolean z) {
        this.c = z;
    }

    public void c(int i) {
        this.b = i;
    }

    public void c(String str) {
        this.g = str;
    }

    public void c(boolean z) {
        this.d = z;
    }

    public boolean c() {
        return this.i;
    }

    public String d() {
        return this.g;
    }

    public void d(String str) {
        this.f = str;
    }

    public String e() {
        return this.f;
    }

    protected String e(String str) {
        return "";
    }

    public void f(String str) {
        this.l = str;
    }

    public boolean f() {
        return this.c;
    }

    public void g(String str) {
        this.h = str;
    }

    public boolean g() {
        return this.d;
    }

    public int h() {
        return this.e;
    }

    protected int i() {
        return this.a;
    }

    public int j() {
        return this.b;
    }

    public String k() {
        return this.l;
    }

    public String l() {
        return this.h;
    }

    public Map<String, Integer> m() {
        HashMap map = new HashMap();
        map.put(SpeechConstants.ASR_SERVICE_MODE_JSONKEY, 1001);
        map.put(SpeechConstants.ASR_VOICE_FIELD_JSONKEY, 1003);
        map.put(SpeechConstants.ASR_LANGUAGE_JSONKEY, 1004);
        map.put(SpeechConstants.ASR_DOMAIN_JSONKEY, 1008);
        map.put(SpeechConstants.ASR_SERVER_ADDR_JSONKEY, 1009);
        map.put(SpeechConstants.ASR_VAD_TIMEOUT_FRONTSIL_JSONKEY, 1010);
        map.put(SpeechConstants.ASR_VAD_TIMEOUT_BACKSIL_JSONKEY, 1011);
        map.put(SpeechConstants.ASR_WAKEUP_WORD_JSONKEY, 1013);
        map.put(SpeechConstants.ASR_NET_TIMEOUT_JSONKEY, 1014);
        map.put(SpeechConstants.NLU_ENABLE_JSONKEY, 1020);
        map.put(SpeechConstants.NLU_SCENARIO_JSONKEY, 1021);
        map.put(SpeechConstants.NLU_SERVER_ADDR_JSONKEY, 1022);
        map.put(SpeechConstants.GENERAL_HISTORY_JSONKEY, 1030);
        map.put(SpeechConstants.GENERAL_CITY_JSONKEY, Integer.valueOf(SpeechConstants.GENERAL_CITY));
        map.put(SpeechConstants.GENERAL_VOICEID_JSONKEY, 1032);
        map.put(SpeechConstants.GENERAL_GPS_JSONKEY, Integer.valueOf(SpeechConstants.GENERAL_GPS));
        map.put(SpeechConstants.ASR_SAMPLING_RATE_JSONKEY, 1044);
        map.put(SpeechConstants.ASR_OPT_ENGINE_TAG_JSONKEY, Integer.valueOf(SpeechConstants.ASR_OPT_ENGINE_TAG));
        map.put(SpeechConstants.ASR_OPT_RESULT_FILTER_JSONKEY, Integer.valueOf(SpeechConstants.ASR_OPT_RESULT_FILTER));
        map.put(SpeechConstants.ASR_OPT_RECORDING_ENABLED_JSONKEY, Integer.valueOf(SpeechConstants.ASR_OPT_RECORDING_ENABLED));
        map.put(SpeechConstants.ASR_OPT_PRINT_LOG_JSONKEY, Integer.valueOf(SpeechConstants.ASR_OPT_PRINT_LOG));
        map.put(SpeechConstants.ASR_OPT_FIX_ASR_CONTINUOUS_JSONKEY, Integer.valueOf(SpeechConstants.ASR_OPT_FIX_ASR_CONTINUOUS));
        map.put(SpeechConstants.ASR_OPT_VAD_ENABLED_JSONKEY, Integer.valueOf(SpeechConstants.ASR_OPT_VAD_ENABLED));
        map.put(SpeechConstants.ASR_OPT_FRONT_VAD_ENABLED_JSONKEY, 1056);
        map.put(SpeechConstants.ASR_OPT_SAVE_RECORDING_DATA_JSONKEY, Integer.valueOf(SpeechConstants.ASR_OPT_SAVE_RECORDING_DATA));
        Integer numValueOf = Integer.valueOf(SpeechConstants.ASR_OPT_RESULT_JSON);
        map.put(SpeechConstants.ASR_OPT_RESULT_JSON_JSONKEY, numValueOf);
        map.put(SpeechConstants.ASR_OPT_FRONT_CACHE_TIME_JSONKEY, 1060);
        map.put(SpeechConstants.ASR_OPT_PRINT_ENGINE_LOG_JSONKEY, Integer.valueOf(SpeechConstants.ASR_OPT_PRINT_ENGINE_LOG));
        map.put(SpeechConstants.ASR_OPT_PRINT_TIME_LOG_JSONKEY, Integer.valueOf(SpeechConstants.ASR_OPT_PRINT_TIME_LOG));
        map.put(SpeechConstants.ASR_OPT_FRONT_RESET_CACHE_BYTE_TIME_JSONKEY, Integer.valueOf(SpeechConstants.ASR_OPT_FRONT_RESET_CACHE_BYTE_TIME));
        map.put(SpeechConstants.ASR_OPT_DEBUG_SAVELOG_JSONKEY, Integer.valueOf(SpeechConstants.ASR_OPT_DEBUG_SAVELOG));
        map.put(SpeechConstants.ASR_OPT_DEBUG_POSTLOG_JSONKEY, 1072);
        map.put(SpeechConstants.ASR_OPT_USE_HANDLERTHREAD_JSONKEY, Integer.valueOf(SpeechConstants.ASR_OPT_USE_HANDLERTHREAD));
        map.put(SpeechConstants.ASR_OPT_SAVE_AFTERVAD_RECORDING_DATA_JSONKEY, Integer.valueOf(SpeechConstants.ASR_OPT_SAVE_AFTERVAD_RECORDING_DATA));
        map.put(SpeechConstants.ASR_OPT_MARK_VAD_JSONKEY, Integer.valueOf(SpeechConstants.ASR_OPT_MARK_VAD));
        map.put(SpeechConstants.ASR_OPT_RESULT_JSON_JSONKEY, numValueOf);
        map.put(SpeechConstants.ASR_OPT_RESULT_JSON_JSONKEY, numValueOf);
        map.put(SpeechConstants.ASR_OPT_RESULT_JSON_JSONKEY, numValueOf);
        map.put(SpeechConstants.WAKEUP_OPT_THRESHOLD_VALUE_JSONKEY, Integer.valueOf(SpeechConstants.WAKEUP_OPT_THRESHOLD_VALUE));
        return map;
    }
}
