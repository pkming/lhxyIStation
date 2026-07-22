package org.apache.tools.ant.types.optional;

import java.util.ArrayList;
import org.apache.tools.ant.util.FileNameMapper;

/* JADX INFO: loaded from: classes3.dex */
public class ScriptMapper extends AbstractScriptComponent implements FileNameMapper {
    private ArrayList<String> files;

    @Override // org.apache.tools.ant.util.FileNameMapper
    public void setFrom(String str) {
    }

    @Override // org.apache.tools.ant.util.FileNameMapper
    public void setTo(String str) {
    }

    public void clear() {
        this.files = new ArrayList<>(1);
    }

    public void addMappedName(String str) {
        this.files.add(str);
    }

    @Override // org.apache.tools.ant.util.FileNameMapper
    public String[] mapFileName(String str) {
        initScriptRunner();
        getRunner().addBean("source", str);
        clear();
        executeScript("ant_mapper");
        if (this.files.size() == 0) {
            return null;
        }
        ArrayList<String> arrayList = this.files;
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }
}
