package com.unisound.sdk;

import android.os.Looper;

/* JADX INFO: loaded from: classes2.dex */
class ay extends com.unisound.common.u {
    final /* synthetic */ au a;

    public ay(au auVar) {
        this.a = auVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ay(au auVar, Looper looper) {
        super(looper);
        this.a = auVar;
    }

    /*  JADX ERROR: UnsupportedOperationException in pass: RegionMakerVisitor
        java.lang.UnsupportedOperationException
        	at java.base/java.util.Collections$UnmodifiableCollection.add(Collections.java:1068)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker$1.leaveRegion(SwitchRegionMaker.java:390)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:70)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:23)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.insertBreaksForCase(SwitchRegionMaker.java:370)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.insertBreaks(SwitchRegionMaker.java:85)
        	at jadx.core.dex.visitors.regions.PostProcessRegions.leaveRegion(PostProcessRegions.java:33)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:70)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1093)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1093)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1093)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1093)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:19)
        	at jadx.core.dex.visitors.regions.PostProcessRegions.process(PostProcessRegions.java:23)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:31)
        */
    @Override // com.unisound.common.u
    public boolean a(android.os.Message r6) {
        /*
            r5 = this;
            com.unisound.sdk.au r0 = r5.a
            com.unisound.client.SpeechSynthesizerListener r0 = com.unisound.sdk.au.d(r0)
            r1 = 0
            if (r0 != 0) goto La
            return r1
        La:
            int r2 = r6.what
            r3 = 114(0x72, float:1.6E-43)
            if (r2 == r3) goto L4c
            r3 = 225(0xe1, float:3.15E-43)
            r4 = 2301(0x8fd, float:3.224E-42)
            if (r2 == r3) goto L20
            switch(r2) {
                case 101: goto L46;
                case 102: goto L43;
                case 103: goto L40;
                case 104: goto L3d;
                case 105: goto L3a;
                case 106: goto L37;
                case 107: goto L34;
                case 108: goto L31;
                case 109: goto L2e;
                case 110: goto L4f;
                case 111: goto L2b;
                case 112: goto L28;
                default: goto L19;
            }
        L19:
            switch(r2) {
                case 200: goto L20;
                case 201: goto L20;
                case 202: goto L20;
                default: goto L1c;
            }
        L1c:
            switch(r2) {
                case 210: goto L20;
                case 211: goto L20;
                case 212: goto L20;
                default: goto L1f;
            }
        L1f:
            return r1
        L20:
            java.lang.Object r6 = r6.obj
            java.lang.String r6 = (java.lang.String) r6
            r0.onError(r4, r6)
            goto L4f
        L28:
            r6 = 2112(0x840, float:2.96E-42)
            goto L48
        L2b:
            r6 = 2111(0x83f, float:2.958E-42)
            goto L48
        L2e:
            r6 = 2109(0x83d, float:2.955E-42)
            goto L48
        L31:
            r6 = 2108(0x83c, float:2.954E-42)
            goto L48
        L34:
            r6 = 2107(0x83b, float:2.953E-42)
            goto L48
        L37:
            r6 = 2106(0x83a, float:2.951E-42)
            goto L48
        L3a:
            r6 = 2105(0x839, float:2.95E-42)
            goto L48
        L3d:
            r6 = 2104(0x838, float:2.948E-42)
            goto L48
        L40:
            r6 = 2103(0x837, float:2.947E-42)
            goto L48
        L43:
            r6 = 2102(0x836, float:2.946E-42)
            goto L48
        L46:
            r6 = 2101(0x835, float:2.944E-42)
        L48:
            r0.onEvent(r6)
            goto L4f
        L4c:
            r6 = 2114(0x842, float:2.962E-42)
            goto L48
        L4f:
            r6 = 1
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.unisound.sdk.ay.a(android.os.Message):boolean");
    }

    @Override // com.unisound.common.u
    public void sendMessage(int i) {
        super.sendMessage(i);
    }
}
