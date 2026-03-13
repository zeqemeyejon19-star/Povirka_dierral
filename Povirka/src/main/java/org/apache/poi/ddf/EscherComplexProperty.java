package org.apache.poi.ddf;

import java.util.Arrays;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes.dex */
public class EscherComplexProperty extends EscherProperty {
    private byte[] _complexData;

    public EscherComplexProperty(short id, byte[] complexData) {
        super(id);
        if (complexData == null) {
            throw new IllegalArgumentException("complexData can't be null");
        }
        this._complexData = (byte[]) complexData.clone();
    }

    public EscherComplexProperty(short propertyNumber, boolean isBlipId, byte[] complexData) {
        super(propertyNumber, true, isBlipId);
        if (complexData == null) {
            throw new IllegalArgumentException("complexData can't be null");
        }
        this._complexData = (byte[]) complexData.clone();
    }

    @Override // org.apache.poi.ddf.EscherProperty
    public int serializeSimplePart(byte[] data, int pos) {
        LittleEndian.putShort(data, pos, getId());
        LittleEndian.putInt(data, pos + 2, this._complexData.length);
        return 6;
    }

    @Override // org.apache.poi.ddf.EscherProperty
    public int serializeComplexPart(byte[] data, int pos) {
        byte[] bArr = this._complexData;
        System.arraycopy(bArr, 0, data, pos, bArr.length);
        return this._complexData.length;
    }

    public byte[] getComplexData() {
        return this._complexData;
    }

    protected void setComplexData(byte[] _complexData) {
        this._complexData = _complexData;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof EscherComplexProperty)) {
            return false;
        }
        EscherComplexProperty escherComplexProperty = (EscherComplexProperty) o;
        return Arrays.equals(this._complexData, escherComplexProperty._complexData);
    }

    @Override // org.apache.poi.ddf.EscherProperty
    public int getPropertySize() {
        return this._complexData.length + 6;
    }

    public int hashCode() {
        return getId() * 11;
    }

    @Override // org.apache.poi.ddf.EscherProperty
    public String toString() {
        String dataStr = HexDump.toHex(this._complexData, 32);
        return "propNum: " + ((int) getPropertyNumber()) + ", propName: " + EscherProperties.getPropertyName(getPropertyNumber()) + ", complex: " + isComplex() + ", blipId: " + isBlipId() + ", data: " + System.getProperty("line.separator") + dataStr;
    }

    @Override // org.apache.poi.ddf.EscherProperty
    public String toXml(String tab) {
        return tab + "<" + getClass().getSimpleName() + " id=\"0x" + HexDump.toHex(getId()) + "\" name=\"" + getName() + "\" blipId=\"" + isBlipId() + "\">\n" + tab + "</" + getClass().getSimpleName() + ">";
    }
}
