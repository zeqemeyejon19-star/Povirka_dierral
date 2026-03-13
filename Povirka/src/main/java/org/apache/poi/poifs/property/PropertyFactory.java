package org.apache.poi.poifs.property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.poifs.storage.ListManagedBlock;

/* JADX INFO: loaded from: classes.dex */
class PropertyFactory {
    private PropertyFactory() {
    }

    static List<Property> convertToProperties(ListManagedBlock[] blocks) throws IOException {
        List<Property> properties = new ArrayList<>();
        for (ListManagedBlock block : blocks) {
            byte[] data = block.getData();
            convertToProperties(data, properties);
        }
        return properties;
    }

    static void convertToProperties(byte[] data, List<Property> properties) throws IOException {
        int property_count = data.length / 128;
        int offset = 0;
        for (int k = 0; k < property_count; k++) {
            byte b = data[offset + 66];
            if (b == 1) {
                properties.add(new DirectoryProperty(properties.size(), data, offset));
            } else if (b == 2) {
                properties.add(new DocumentProperty(properties.size(), data, offset));
            } else if (b == 5) {
                properties.add(new RootProperty(properties.size(), data, offset));
            } else {
                properties.add(null);
            }
            offset += 128;
        }
    }
}
