package android.widget;

/* JADX INFO: loaded from: classes.dex */
public interface SectionIndexer {
    int getPositionForSection(int i);

    int getSectionForPosition(int i);

    Object[] getSections();
}
