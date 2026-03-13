package org.apache.poi.ddf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractEscherOptRecord extends EscherRecord {
    private List<EscherProperty> properties = new ArrayList();

    public void addEscherProperty(EscherProperty prop) {
        this.properties.add(prop);
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesRemaining = readHeader(data, offset);
        short propertiesCount = readInstance(data, offset);
        int pos = offset + 8;
        EscherPropertyFactory f = new EscherPropertyFactory();
        this.properties = f.createProperties(data, pos, propertiesCount);
        return bytesRemaining + 8;
    }

    public List<EscherProperty> getEscherProperties() {
        return this.properties;
    }

    public EscherProperty getEscherProperty(int index) {
        return this.properties.get(index);
    }

    private int getPropertiesSize() {
        int totalSize = 0;
        for (EscherProperty property : this.properties) {
            totalSize += property.getPropertySize();
        }
        return totalSize;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int getRecordSize() {
        return getPropertiesSize() + 8;
    }

    public <T extends EscherProperty> T lookup(int propId) {
        Iterator<EscherProperty> it = this.properties.iterator();
        while (it.hasNext()) {
            T t = (T) it.next();
            if (t.getPropertyNumber() == propId) {
                return t;
            }
        }
        return null;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);
        LittleEndian.putShort(data, offset, getOptions());
        LittleEndian.putShort(data, offset + 2, getRecordId());
        LittleEndian.putInt(data, offset + 4, getPropertiesSize());
        int pos = offset + 8;
        for (EscherProperty property : this.properties) {
            pos += property.serializeSimplePart(data, pos);
        }
        for (EscherProperty property2 : this.properties) {
            pos += property2.serializeComplexPart(data, pos);
        }
        listener.afterRecordSerialize(pos, getRecordId(), pos - offset, this);
        return pos - offset;
    }

    public void sortProperties() {
        Collections.sort(this.properties, new Comparator<EscherProperty>() { // from class: org.apache.poi.ddf.AbstractEscherOptRecord.1
            @Override // java.util.Comparator
            public int compare(EscherProperty p1, EscherProperty p2) {
                short s1 = p1.getPropertyNumber();
                short s2 = p2.getPropertyNumber();
                if (s1 < s2) {
                    return -1;
                }
                return s1 == s2 ? 0 : 1;
            }
        });
    }

    public void setEscherProperty(EscherProperty value) {
        Iterator<EscherProperty> iterator = this.properties.iterator();
        while (iterator.hasNext()) {
            EscherProperty prop = iterator.next();
            if (prop.getId() == value.getId()) {
                iterator.remove();
            }
        }
        this.properties.add(value);
        sortProperties();
    }

    public void removeEscherProperty(int num) {
        Iterator<EscherProperty> iterator = getEscherProperties().iterator();
        while (iterator.hasNext()) {
            EscherProperty prop = iterator.next();
            if (prop.getPropertyNumber() == num) {
                iterator.remove();
            }
        }
    }

    @Override // org.apache.poi.ddf.EscherRecord
    protected Object[][] getAttributeMap() {
        List<Object> attrList = new ArrayList<>((this.properties.size() * 2) + 2);
        attrList.add("properties");
        attrList.add(Integer.valueOf(this.properties.size()));
        for (EscherProperty property : this.properties) {
            attrList.add(property.getName());
            attrList.add(property);
        }
        return new Object[][]{new Object[]{"isContainer", Boolean.valueOf(isContainerRecord())}, new Object[]{"numchildren", Integer.valueOf(getChildRecords().size())}, attrList.toArray()};
    }
}
