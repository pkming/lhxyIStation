package android.view.accessibility;

/* JADX INFO: loaded from: classes.dex */
public interface AccessibilityEventSource {
    void sendAccessibilityEvent(int i);

    void sendAccessibilityEventUnchecked(AccessibilityEvent accessibilityEvent);
}
