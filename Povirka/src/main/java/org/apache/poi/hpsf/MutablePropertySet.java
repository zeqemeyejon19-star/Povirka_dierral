package org.apache.poi.hpsf;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.util.Removal;

/* JADX INFO: loaded from: classes.dex */
@Removal(version = "3.18")
public class MutablePropertySet extends PropertySet {
    public MutablePropertySet() {
    }

    public MutablePropertySet(PropertySet ps) {
        super(ps);
    }

    MutablePropertySet(InputStream stream) throws MarkUnsupportedException, NoPropertySetStreamException, IOException {
        super(stream);
    }
}
