package org.apache.poi.poifs.property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.apache.poi.poifs.filesystem.BATManaged;
import org.apache.poi.poifs.storage.HeaderBlock;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public abstract class PropertyTableBase implements BATManaged {
    private static final POILogger _logger = POILogFactory.getLogger((Class<?>) PropertyTableBase.class);
    private final HeaderBlock _header_block;
    protected final List<Property> _properties;

    public PropertyTableBase(HeaderBlock header_block) {
        this._header_block = header_block;
        this._properties = new ArrayList();
        addProperty(new RootProperty());
    }

    public PropertyTableBase(HeaderBlock header_block, List<Property> properties) throws IOException {
        this._header_block = header_block;
        this._properties = properties;
        populatePropertyTree((DirectoryProperty) properties.get(0));
    }

    public void addProperty(Property property) {
        this._properties.add(property);
    }

    public void removeProperty(Property property) {
        this._properties.remove(property);
    }

    public RootProperty getRoot() {
        return (RootProperty) this._properties.get(0);
    }

    private void populatePropertyTree(DirectoryProperty root) throws IOException {
        int index = root.getChildIndex();
        if (!Property.isValidIndex(index)) {
            return;
        }
        Stack<Property> children = new Stack<>();
        children.push(this._properties.get(index));
        while (!children.empty()) {
            Property property = children.pop();
            if (property != null) {
                root.addChild(property);
                if (property.isDirectory()) {
                    populatePropertyTree((DirectoryProperty) property);
                }
                int index2 = property.getPreviousChildIndex();
                if (isValidIndex(index2)) {
                    children.push(this._properties.get(index2));
                }
                int index3 = property.getNextChildIndex();
                if (isValidIndex(index3)) {
                    children.push(this._properties.get(index3));
                }
            }
        }
    }

    protected boolean isValidIndex(int index) {
        if (!Property.isValidIndex(index)) {
            return false;
        }
        if (index < 0 || index >= this._properties.size()) {
            _logger.log(5, "Property index " + index + "outside the valid range 0.." + this._properties.size());
            return false;
        }
        return true;
    }

    public int getStartBlock() {
        return this._header_block.getPropertyStart();
    }

    @Override // org.apache.poi.poifs.filesystem.BATManaged
    public void setStartBlock(int index) {
        this._header_block.setPropertyStart(index);
    }
}
