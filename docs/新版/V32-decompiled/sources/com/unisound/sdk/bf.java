package com.unisound.sdk;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.unisound.client.ErrorCode;
import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechUnderstanderListener;

/* JADX INFO: loaded from: classes2.dex */
class bf extends Handler {
    final /* synthetic */ bb a;

    public bf(bb bbVar) {
        this.a = bbVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public bf(bb bbVar, Looper looper) {
        super(looper);
        this.a = bbVar;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        SpeechUnderstanderListener speechUnderstanderListener;
        int i;
        SpeechUnderstanderListener speechUnderstanderListener2;
        int i2;
        int i3 = message.what;
        if (i3 != 1) {
            if (i3 != 20) {
                int i4 = SpeechConstants.ASR_RESULT_RECOGNITION;
                if (i3 != 1210) {
                    i4 = SpeechConstants.WAKEUP_RESULT;
                    if (i3 != 3201) {
                        if (i3 == 5) {
                            if (this.a.A != null && !this.a.al) {
                                this.a.A.onEvent(SpeechConstants.ASR_EVENT_RECOGNITION_END, (int) System.currentTimeMillis());
                            }
                            this.a.v.clear();
                            this.a.w.clear();
                            this.a.x.clear();
                            this.a.am = false;
                            return;
                        }
                        if (i3 == 6) {
                            if (this.a.A == null || this.a.al) {
                                return;
                            }
                            speechUnderstanderListener = this.a.A;
                            i = SpeechConstants.ASR_EVENT_LOCAL_END;
                        } else if (i3 == 7) {
                            if (this.a.A == null || this.a.al) {
                                return;
                            }
                            speechUnderstanderListener = this.a.A;
                            i = SpeechConstants.ASR_EVENT_NET_END;
                        } else if (i3 != 8) {
                            i4 = 1201;
                            if (i3 != 1201) {
                                i4 = 1202;
                                if (i3 != 1202) {
                                    switch (i3) {
                                        case 11:
                                            if (this.a.A == null || this.a.al) {
                                                return;
                                            }
                                            speechUnderstanderListener = this.a.A;
                                            i = 1101;
                                            break;
                                        case 12:
                                            if (this.a.A == null || this.a.al) {
                                                return;
                                            }
                                            this.a.A.onEvent(1102, (int) System.currentTimeMillis());
                                            this.a.an = true;
                                            if (this.a.am) {
                                                this.a.A.onEvent(SpeechConstants.ASR_EVENT_RECOGNITION_END, (int) System.currentTimeMillis());
                                                return;
                                            }
                                            return;
                                        case 13:
                                            if (this.a.A == null || this.a.al) {
                                                return;
                                            }
                                            speechUnderstanderListener = this.a.A;
                                            i = SpeechConstants.ASR_EVENT_SPEECH_DETECTED;
                                            break;
                                        case 14:
                                            if (this.a.A == null || this.a.al) {
                                                return;
                                            }
                                            speechUnderstanderListener = this.a.A;
                                            i = SpeechConstants.ASR_EVENT_RECORDING_PREPARED;
                                            break;
                                        case 15:
                                            if (this.a.A == null) {
                                                return;
                                            }
                                            speechUnderstanderListener = this.a.A;
                                            i = SpeechConstants.ASR_EVENT_CANCEL;
                                            break;
                                        case 16:
                                            if (this.a.A == null) {
                                                return;
                                            }
                                            speechUnderstanderListener = this.a.A;
                                            i = SpeechConstants.ASR_EVENT_USERDATA_UPLOADED;
                                            break;
                                        case 17:
                                            if (this.a.A == null || this.a.al) {
                                                return;
                                            }
                                            speechUnderstanderListener = this.a.A;
                                            i = SpeechConstants.ASR_EVENT_VOLUMECHANGE;
                                            break;
                                        case 18:
                                            if (this.a.A == null || this.a.al) {
                                                return;
                                            }
                                            speechUnderstanderListener = this.a.A;
                                            i = 1103;
                                            break;
                                        default:
                                            switch (i3) {
                                                case 53:
                                                    if (this.a.A == null) {
                                                        return;
                                                    }
                                                    speechUnderstanderListener2 = this.a.A;
                                                    i2 = SpeechConstants.WAKEUP_ERROR;
                                                    break;
                                                case 54:
                                                    if (this.a.A == null) {
                                                        return;
                                                    }
                                                    speechUnderstanderListener2 = this.a.A;
                                                    i2 = SpeechConstants.ASR_ERROR_INSERTVOCAB_EXT_FAIL;
                                                    break;
                                                case 55:
                                                    if (this.a.A == null) {
                                                        return;
                                                    }
                                                    speechUnderstanderListener2 = this.a.A;
                                                    i2 = SpeechConstants.ASR_ERROR;
                                                    break;
                                                default:
                                                    if (this.a.A != null) {
                                                        this.a.A.onEvent(message.what, (int) System.currentTimeMillis());
                                                        return;
                                                    }
                                                    return;
                                            }
                                            speechUnderstanderListener2.onError(i2, ErrorCode.toJsonMessage(((Integer) message.obj).intValue()));
                                            return;
                                    }
                                } else if (this.a.A == null || this.a.al) {
                                    return;
                                }
                            } else if (this.a.A == null || this.a.al) {
                                return;
                            }
                        } else {
                            if (this.a.A == null) {
                                return;
                            }
                            speechUnderstanderListener = this.a.A;
                            i = SpeechConstants.ASR_EVENT_COMPILE_DONE;
                        }
                    } else if (this.a.A == null || this.a.al) {
                        return;
                    }
                } else if (this.a.A == null || this.a.al) {
                    return;
                }
                this.a.A.onResult(i4, (String) message.obj);
                return;
            }
            if (this.a.A == null || this.a.al) {
                return;
            }
            speechUnderstanderListener = this.a.A;
            i = SpeechConstants.ASR_EVENT_SPEECH_END;
        } else {
            if (this.a.A == null || this.a.al) {
                return;
            }
            speechUnderstanderListener = this.a.A;
            i = SpeechConstants.WAKEUP_EVENT_RECOGNITION_SUCCESS;
        }
        speechUnderstanderListener.onEvent(i, (int) System.currentTimeMillis());
    }
}
