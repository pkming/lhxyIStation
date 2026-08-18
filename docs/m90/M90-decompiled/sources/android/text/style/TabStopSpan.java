package android.text.style;

/* JADX INFO: loaded from: classes.dex */
public interface TabStopSpan extends ParagraphStyle {
    int getTabStop();

    public static class Standard implements TabStopSpan {
        private int mTab;

        public Standard(int i) {
            this.mTab = i;
        }

        @Override // android.text.style.TabStopSpan
        public int getTabStop() {
            return this.mTab;
        }
    }
}
