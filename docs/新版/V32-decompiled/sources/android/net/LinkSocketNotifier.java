package android.net;

/* JADX INFO: loaded from: classes.dex */
public interface LinkSocketNotifier {
    boolean onBetterLinkAvailable(LinkSocket linkSocket, LinkSocket linkSocket2);

    void onCapabilitiesChanged(LinkSocket linkSocket, LinkCapabilities linkCapabilities);

    void onLinkLost(LinkSocket linkSocket);

    void onNewLinkUnavailable(LinkSocket linkSocket);
}
