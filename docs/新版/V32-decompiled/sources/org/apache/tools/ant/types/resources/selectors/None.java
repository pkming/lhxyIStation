package org.apache.tools.ant.types.resources.selectors;

import java.util.Iterator;
import org.apache.tools.ant.types.Resource;

/* JADX INFO: loaded from: classes3.dex */
public class None extends ResourceSelectorContainer implements ResourceSelector {
    public None() {
    }

    public None(ResourceSelector[] resourceSelectorArr) {
        super(resourceSelectorArr);
    }

    @Override // org.apache.tools.ant.types.resources.selectors.ResourceSelector
    public boolean isSelected(Resource resource) {
        Iterator<ResourceSelector> selectors = getSelectors();
        while (selectors.hasNext()) {
            if (selectors.next().isSelected(resource)) {
                return false;
            }
        }
        return true;
    }
}
