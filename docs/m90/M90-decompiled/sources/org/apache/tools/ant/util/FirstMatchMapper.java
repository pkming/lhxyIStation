package org.apache.tools.ant.util;

/* JADX INFO: loaded from: classes3.dex */
public class FirstMatchMapper extends ContainerMapper {
    @Override // org.apache.tools.ant.util.FileNameMapper
    public String[] mapFileName(String str) {
        String[] strArrMapFileName;
        for (FileNameMapper fileNameMapper : getMappers()) {
            if (fileNameMapper != null && (strArrMapFileName = fileNameMapper.mapFileName(str)) != null) {
                return strArrMapFileName;
            }
        }
        return null;
    }
}
