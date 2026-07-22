package android.webkit;

/* JADX INFO: loaded from: classes.dex */
class MustOverrideException extends RuntimeException {
    MustOverrideException() {
        super("abstract function called: must be overriden!");
    }
}
