package android.text;

/* JADX INFO: loaded from: classes.dex */
public interface InputFilter {
    CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4);

    public static class AllCaps implements InputFilter {
        @Override // android.text.InputFilter
        public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
            for (int i5 = i; i5 < i2; i5++) {
                if (Character.isLowerCase(charSequence.charAt(i5))) {
                    char[] cArr = new char[i2 - i];
                    TextUtils.getChars(charSequence, i, i2, cArr, 0);
                    String upperCase = new String(cArr).toUpperCase();
                    if (!(charSequence instanceof Spanned)) {
                        return upperCase;
                    }
                    SpannableString spannableString = new SpannableString(upperCase);
                    TextUtils.copySpansFrom((Spanned) charSequence, i, i2, null, spannableString, 0);
                    return spannableString;
                }
            }
            return null;
        }
    }

    public static class LengthFilter implements InputFilter {
        private int mMax;

        public LengthFilter(int i) {
            this.mMax = i;
        }

        @Override // android.text.InputFilter
        public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
            int length = this.mMax - (spanned.length() - (i4 - i3));
            if (length <= 0) {
                return "";
            }
            if (length >= i2 - i) {
                return null;
            }
            int i5 = length + i;
            return (Character.isHighSurrogate(charSequence.charAt(i5 + (-1))) && (i5 = i5 + (-1)) == i) ? "" : charSequence.subSequence(i, i5);
        }
    }
}
