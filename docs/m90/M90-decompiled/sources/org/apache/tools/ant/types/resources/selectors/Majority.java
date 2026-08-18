package org.apache.tools.ant.types.resources.selectors;

import java.util.Iterator;
import org.apache.tools.ant.types.Resource;

/* JADX INFO: loaded from: classes3.dex */
public class Majority extends ResourceSelectorContainer implements ResourceSelector {
    private boolean tie;

    public Majority() {
        this.tie = true;
    }

    public Majority(ResourceSelector[] resourceSelectorArr) {
        super(resourceSelectorArr);
        this.tie = true;
    }

    public synchronized void setAllowtie(boolean z) {
        this.tie = z;
    }

    @Override // org.apache.tools.ant.types.resources.selectors.ResourceSelector
    public synchronized boolean isSelected(Resource resource) {
        int iSelectorCount = selectorCount();
        boolean z = iSelectorCount % 2 == 0;
        int i = iSelectorCount / 2;
        Iterator<ResourceSelector> selectors = getSelectors();
        int i2 = 0;
        int i3 = 0;
        while (selectors.hasNext()) {
            if (selectors.next().isSelected(resource)) {
                i3++;
                if (i3 > i || (z && this.tie && i3 == i)) {
                    return true;
                }
            } else {
                i2++;
                if (i2 > i || (z && !this.tie && i2 == i)) {
                    return false;
                }
            }
        }
        return false;
    }
}
