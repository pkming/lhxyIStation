package com.bumptech.glide.manager;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

/* JADX INFO: loaded from: classes2.dex */
public class RequestTracker {
    private boolean isPaused;
    private final Set<Request> requests = Collections.newSetFromMap(new WeakHashMap());
    private final List<Request> pendingRequests = new ArrayList();

    public void runRequest(Request request) {
        this.requests.add(request);
        if (!this.isPaused) {
            request.begin();
        } else {
            this.pendingRequests.add(request);
        }
    }

    void addRequest(Request request) {
        this.requests.add(request);
    }

    public boolean clearRemoveAndRecycle(Request request) {
        return clearRemoveAndMaybeRecycle(request, true);
    }

    private boolean clearRemoveAndMaybeRecycle(Request request, boolean z) {
        boolean z2 = true;
        if (request == null) {
            return true;
        }
        boolean zRemove = this.requests.remove(request);
        if (!this.pendingRequests.remove(request) && !zRemove) {
            z2 = false;
        }
        if (z2) {
            request.clear();
            if (z) {
                request.recycle();
            }
        }
        return z2;
    }

    public boolean isPaused() {
        return this.isPaused;
    }

    public void pauseRequests() {
        this.isPaused = true;
        for (Request request : Util.getSnapshot(this.requests)) {
            if (request.isRunning()) {
                request.pause();
                this.pendingRequests.add(request);
            }
        }
    }

    public void pauseAllRequests() {
        this.isPaused = true;
        for (Request request : Util.getSnapshot(this.requests)) {
            if (request.isRunning() || request.isComplete()) {
                request.pause();
                this.pendingRequests.add(request);
            }
        }
    }

    public void resumeRequests() {
        this.isPaused = false;
        for (Request request : Util.getSnapshot(this.requests)) {
            if (!request.isComplete() && !request.isCancelled() && !request.isRunning()) {
                request.begin();
            }
        }
        this.pendingRequests.clear();
    }

    public void clearRequests() {
        Iterator it = Util.getSnapshot(this.requests).iterator();
        while (it.hasNext()) {
            clearRemoveAndMaybeRecycle((Request) it.next(), false);
        }
        this.pendingRequests.clear();
    }

    public void restartRequests() {
        for (Request request : Util.getSnapshot(this.requests)) {
            if (!request.isComplete() && !request.isCancelled()) {
                request.pause();
                if (!this.isPaused) {
                    request.begin();
                } else {
                    this.pendingRequests.add(request);
                }
            }
        }
    }

    public String toString() {
        return super.toString() + "{numRequests=" + this.requests.size() + ", isPaused=" + this.isPaused + "}";
    }
}
