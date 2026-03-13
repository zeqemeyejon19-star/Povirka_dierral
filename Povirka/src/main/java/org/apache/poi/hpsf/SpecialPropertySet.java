package org.apache.poi.hpsf;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.util.Removal;

/* JADX INFO: loaded from: classes.dex */
@Removal(version = "3.18")
public class SpecialPropertySet extends MutablePropertySet {
    public SpecialPropertySet() {
    }

    public SpecialPropertySet(PropertySet ps) throws UnexpectedPropertySetTypeException {
        super(ps);
    }

    SpecialPropertySet(InputStream stream) throws MarkUnsupportedException, NoPropertySetStreamException, IOException {
        super(stream);
    }
}
