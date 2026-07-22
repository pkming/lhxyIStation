package com.rengwuxian.materialedittext.validation;

/* JADX INFO: loaded from: classes2.dex */
public abstract class METValidator {
    protected String errorMessage;

    public abstract boolean isValid(CharSequence charSequence, boolean z);

    public METValidator(String str) {
        this.errorMessage = str;
    }

    public void setErrorMessage(String str) {
        this.errorMessage = str;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
