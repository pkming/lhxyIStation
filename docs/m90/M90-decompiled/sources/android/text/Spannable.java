package android.text;

/* JADX INFO: loaded from: classes.dex */
public interface Spannable extends Spanned {
    void removeSpan(Object obj);

    void setSpan(Object obj, int i, int i2, int i3);

    public static class Factory {
        private static Factory sInstance = new Factory();

        public static Factory getInstance() {
            return sInstance;
        }

        public Spannable newSpannable(CharSequence charSequence) {
            return new SpannableString(charSequence);
        }
    }
}
