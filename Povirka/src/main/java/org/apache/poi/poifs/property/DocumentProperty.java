package org.apache.poi.poifs.property;

import org.apache.poi.poifs.filesystem.OPOIFSDocument;

/* JADX INFO: loaded from: classes.dex */
public class DocumentProperty extends Property {
    private OPOIFSDocument _document;

    public DocumentProperty(String name, int size) {
        this._document = null;
        setName(name);
        setSize(size);
        setNodeColor((byte) 1);
        setPropertyType((byte) 2);
    }

    protected DocumentProperty(int index, byte[] array, int offset) {
        super(index, array, offset);
        this._document = null;
    }

    public void setDocument(OPOIFSDocument doc) {
        this._document = doc;
    }

    public OPOIFSDocument getDocument() {
        return this._document;
    }

    @Override // org.apache.poi.poifs.property.Property
    public boolean shouldUseSmallBlocks() {
        return super.shouldUseSmallBlocks();
    }

    @Override // org.apache.poi.poifs.property.Property
    public boolean isDirectory() {
        return false;
    }

    @Override // org.apache.poi.poifs.property.Property
    protected void preWrite() {
    }

    public void updateSize(int size) {
        setSize(size);
    }
}
