package org.apache.tools.ant.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class FilterSetCollection {
    private List<FilterSet> filterSets = new ArrayList();

    public FilterSetCollection() {
    }

    public FilterSetCollection(FilterSet filterSet) {
        addFilterSet(filterSet);
    }

    public void addFilterSet(FilterSet filterSet) {
        this.filterSets.add(filterSet);
    }

    public String replaceTokens(String str) {
        Iterator<FilterSet> it = this.filterSets.iterator();
        while (it.hasNext()) {
            str = it.next().replaceTokens(str);
        }
        return str;
    }

    public boolean hasFilters() {
        Iterator<FilterSet> it = this.filterSets.iterator();
        while (it.hasNext()) {
            if (it.next().hasFilters()) {
                return true;
            }
        }
        return false;
    }
}
