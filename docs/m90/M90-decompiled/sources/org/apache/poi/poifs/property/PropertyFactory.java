package org.apache.poi.poifs.property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.poifs.storage.ListManagedBlock;

/* JADX INFO: loaded from: classes3.dex */
class PropertyFactory {
    private PropertyFactory() {
    }

    static List convertToProperties(ListManagedBlock[] listManagedBlockArr) throws IOException {
        ArrayList arrayList = new ArrayList();
        for (ListManagedBlock listManagedBlock : listManagedBlockArr) {
            byte[] data = listManagedBlock.getData();
            int length = data.length / 128;
            int i = 0;
            for (int i2 = 0; i2 < length; i2++) {
                byte b = data[i + 66];
                if (b == 1) {
                    arrayList.add(new DirectoryProperty(arrayList.size(), data, i));
                } else if (b == 2) {
                    arrayList.add(new DocumentProperty(arrayList.size(), data, i));
                } else if (b == 5) {
                    arrayList.add(new RootProperty(arrayList.size(), data, i));
                } else {
                    arrayList.add(null);
                }
                i += 128;
            }
        }
        return arrayList;
    }
}
