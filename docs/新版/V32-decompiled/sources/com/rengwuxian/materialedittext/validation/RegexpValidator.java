package com.rengwuxian.materialedittext.validation;

import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes2.dex */
public class RegexpValidator extends METValidator {
    private Pattern pattern;

    public RegexpValidator(String str, String str2) {
        super(str);
        this.pattern = Pattern.compile(str2);
    }

    @Override // com.rengwuxian.materialedittext.validation.METValidator
    public boolean isValid(CharSequence charSequence, boolean z) {
        return this.pattern.matcher(charSequence).matches();
    }
}
