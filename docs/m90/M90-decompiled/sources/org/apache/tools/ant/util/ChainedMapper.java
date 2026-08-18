package org.apache.tools.ant.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/* JADX INFO: loaded from: classes3.dex */
public class ChainedMapper extends ContainerMapper {
    @Override // org.apache.tools.ant.util.FileNameMapper
    public String[] mapFileName(String str) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(str);
        for (FileNameMapper fileNameMapper : getMappers()) {
            if (fileNameMapper != null) {
                arrayList.clear();
                arrayList.addAll(arrayList2);
                arrayList2.clear();
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    String[] strArrMapFileName = fileNameMapper.mapFileName((String) it.next());
                    if (strArrMapFileName != null) {
                        arrayList2.addAll(Arrays.asList(strArrMapFileName));
                    }
                }
            }
        }
        if (arrayList2.size() == 0) {
            return null;
        }
        return (String[]) arrayList2.toArray(new String[arrayList2.size()]);
    }
}
