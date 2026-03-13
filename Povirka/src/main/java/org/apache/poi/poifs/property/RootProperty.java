package org.apache.poi.poifs.property;

import org.apache.poi.poifs.storage.SmallDocumentBlock;

/* JADX INFO: loaded from: classes.dex */
public final class RootProperty extends DirectoryProperty {
    private static final String NAME = "Root Entry";

    RootProperty() {
        super(NAME);
        setNodeColor((byte) 1);
        setPropertyType((byte) 5);
        setStartBlock(-2);
    }

    protected RootProperty(int index, byte[] array, int offset) {
        super(index, array, offset);
    }

    @Override // org.apache.poi.poifs.property.Property
    public void setSize(int size) {
        super.setSize(SmallDocumentBlock.calcSize(size));
    }

    @Override // org.apache.poi.poifs.property.Property
    public String getName() {
        return NAME;
    }
}
