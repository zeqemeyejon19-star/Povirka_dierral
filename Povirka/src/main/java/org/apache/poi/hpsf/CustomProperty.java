package org.apache.poi.hpsf;

/* JADX INFO: loaded from: classes.dex */
public class CustomProperty extends MutableProperty {
    private String name;

    public CustomProperty() {
        this.name = null;
    }

    public CustomProperty(Property property) {
        this(property, null);
    }

    public CustomProperty(Property property, String name) {
        super(property);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equalsContents(Object o) {
        boolean equalNames;
        CustomProperty c = (CustomProperty) o;
        String name1 = c.getName();
        String name2 = getName();
        if (name1 == null) {
            equalNames = name2 == null;
        } else {
            equalNames = name1.equals(name2);
        }
        return equalNames && c.getID() == getID() && c.getType() == getType() && c.getValue().equals(getValue());
    }

    @Override // org.apache.poi.hpsf.Property
    public int hashCode() {
        return (int) getID();
    }

    @Override // org.apache.poi.hpsf.Property
    public boolean equals(Object o) {
        if (o instanceof CustomProperty) {
            return equalsContents(o);
        }
        return false;
    }
}
