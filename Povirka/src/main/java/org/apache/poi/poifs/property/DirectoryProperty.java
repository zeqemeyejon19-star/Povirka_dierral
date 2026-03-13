package org.apache.poi.poifs.property;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class DirectoryProperty extends Property implements Parent, Iterable<Property> {
    private List<Property> _children;
    private Set<String> _children_names;

    public DirectoryProperty(String name) {
        this._children = new ArrayList();
        this._children_names = new HashSet();
        setName(name);
        setSize(0);
        setPropertyType((byte) 1);
        setStartBlock(0);
        setNodeColor((byte) 1);
    }

    protected DirectoryProperty(int index, byte[] array, int offset) {
        super(index, array, offset);
        this._children = new ArrayList();
        this._children_names = new HashSet();
    }

    public boolean changeName(Property property, String newName) {
        String oldName = property.getName();
        property.setName(newName);
        String cleanNewName = property.getName();
        if (this._children_names.contains(cleanNewName)) {
            property.setName(oldName);
            return false;
        }
        this._children_names.add(cleanNewName);
        this._children_names.remove(oldName);
        return true;
    }

    public boolean deleteChild(Property property) {
        boolean result = this._children.remove(property);
        if (result) {
            this._children_names.remove(property.getName());
        }
        return result;
    }

    public static class PropertyComparator implements Comparator<Property>, Serializable {
        @Override // java.util.Comparator
        public int compare(Property o1, Property o2) {
            String name1 = o1.getName();
            String name2 = o2.getName();
            int result = name1.length() - name2.length();
            if (result == 0) {
                if (name1.compareTo("_VBA_PROJECT") == 0) {
                    return 1;
                }
                if (name2.compareTo("_VBA_PROJECT") == 0) {
                    return -1;
                }
                if (name1.startsWith("__") && name2.startsWith("__")) {
                    return name1.compareToIgnoreCase(name2);
                }
                if (name1.startsWith("__")) {
                    return 1;
                }
                if (name2.startsWith("__")) {
                    return -1;
                }
                return name1.compareToIgnoreCase(name2);
            }
            return result;
        }
    }

    @Override // org.apache.poi.poifs.property.Property
    public boolean isDirectory() {
        return true;
    }

    @Override // org.apache.poi.poifs.property.Property
    protected void preWrite() {
        if (this._children.size() > 0) {
            Property[] children = (Property[]) this._children.toArray(new Property[0]);
            Arrays.sort(children, new PropertyComparator());
            int midpoint = children.length / 2;
            setChildProperty(children[midpoint].getIndex());
            children[0].setPreviousChild(null);
            children[0].setNextChild(null);
            for (int j = 1; j < midpoint; j++) {
                children[j].setPreviousChild(children[j - 1]);
                children[j].setNextChild(null);
            }
            if (midpoint != 0) {
                children[midpoint].setPreviousChild(children[midpoint - 1]);
            }
            if (midpoint != children.length - 1) {
                children[midpoint].setNextChild(children[midpoint + 1]);
                for (int j2 = midpoint + 1; j2 < children.length - 1; j2++) {
                    children[j2].setPreviousChild(null);
                    children[j2].setNextChild(children[j2 + 1]);
                }
                int j3 = children.length;
                children[j3 - 1].setPreviousChild(null);
                children[children.length - 1].setNextChild(null);
                return;
            }
            children[midpoint].setNextChild(null);
        }
    }

    @Override // org.apache.poi.poifs.property.Parent
    public Iterator<Property> getChildren() {
        return this._children.iterator();
    }

    @Override // java.lang.Iterable
    public Iterator<Property> iterator() {
        return getChildren();
    }

    @Override // org.apache.poi.poifs.property.Parent
    public void addChild(Property property) throws IOException {
        String name = property.getName();
        if (this._children_names.contains(name)) {
            throw new IOException("Duplicate name \"" + name + "\"");
        }
        this._children_names.add(name);
        this._children.add(property);
    }
}
