package org.apache.tools.ant.util;

import java.util.LinkedHashSet;

/* JADX INFO: loaded from: classes3.dex */
public class CompositeMapper extends ContainerMapper {
    @Override // org.apache.tools.ant.util.FileNameMapper
    public String[] mapFileName(String str) {
        String[] strArrMapFileName;
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (FileNameMapper fileNameMapper : getMappers()) {
            if (fileNameMapper != null && (strArrMapFileName = fileNameMapper.mapFileName(str)) != null) {
                for (String str2 : strArrMapFileName) {
                    linkedHashSet.add(str2);
                }
            }
        }
        if (linkedHashSet.size() == 0) {
            return null;
        }
        return (String[]) linkedHashSet.toArray(new String[linkedHashSet.size()]);
    }
}
