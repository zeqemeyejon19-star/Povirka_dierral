package org.apache.poi.poifs.filesystem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.poifs.dev.POIFSViewable;
import org.apache.poi.poifs.property.DocumentProperty;

/* JADX INFO: loaded from: classes.dex */
public class DocumentNode extends EntryNode implements DocumentEntry, POIFSViewable {
    private OPOIFSDocument _document;

    DocumentNode(DocumentProperty property, DirectoryNode parent) {
        super(property, parent);
        this._document = property.getDocument();
    }

    OPOIFSDocument getDocument() {
        return this._document;
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentEntry
    public int getSize() {
        return getProperty().getSize();
    }

    @Override // org.apache.poi.poifs.filesystem.EntryNode, org.apache.poi.poifs.filesystem.Entry
    public boolean isDocumentEntry() {
        return true;
    }

    @Override // org.apache.poi.poifs.filesystem.EntryNode
    protected boolean isDeleteOK() {
        return true;
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public Object[] getViewableArray() {
        return new Object[0];
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public Iterator<Object> getViewableIterator() {
        List<Object> components = new ArrayList<>();
        components.add(getProperty());
        Object obj = this._document;
        if (obj != null) {
            components.add(obj);
        }
        return components.iterator();
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public boolean preferArray() {
        return false;
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public String getShortDescription() {
        return getName();
    }
}
