package org.apache.poi.poifs.filesystem;

import org.apache.poi.poifs.property.Property;

/* JADX INFO: loaded from: classes.dex */
public abstract class EntryNode implements Entry {
    private DirectoryNode _parent;
    private Property _property;

    protected abstract boolean isDeleteOK();

    protected EntryNode(Property property, DirectoryNode parent) {
        this._property = property;
        this._parent = parent;
    }

    protected Property getProperty() {
        return this._property;
    }

    protected boolean isRoot() {
        return this._parent == null;
    }

    @Override // org.apache.poi.poifs.filesystem.Entry
    public String getName() {
        return this._property.getName();
    }

    @Override // org.apache.poi.poifs.filesystem.Entry
    public boolean isDirectoryEntry() {
        return false;
    }

    @Override // org.apache.poi.poifs.filesystem.Entry
    public boolean isDocumentEntry() {
        return false;
    }

    @Override // org.apache.poi.poifs.filesystem.Entry
    public DirectoryEntry getParent() {
        return this._parent;
    }

    @Override // org.apache.poi.poifs.filesystem.Entry
    public boolean delete() {
        if (isRoot() || !isDeleteOK()) {
            return false;
        }
        boolean rval = this._parent.deleteEntry(this);
        return rval;
    }

    @Override // org.apache.poi.poifs.filesystem.Entry
    public boolean renameTo(String newName) {
        if (isRoot()) {
            return false;
        }
        boolean rval = this._parent.changeName(getName(), newName);
        return rval;
    }
}
