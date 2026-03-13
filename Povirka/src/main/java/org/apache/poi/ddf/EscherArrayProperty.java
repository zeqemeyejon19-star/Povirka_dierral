package org.apache.poi.ddf;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes.dex */
public final class EscherArrayProperty extends EscherComplexProperty implements Iterable<byte[]> {
    private static final int FIXED_SIZE = 6;
    private boolean emptyComplexPart;
    private boolean sizeIncludesHeaderSize;

    public EscherArrayProperty(short id, byte[] complexData) {
        super(id, checkComplexData(complexData));
        boolean z = true;
        this.sizeIncludesHeaderSize = true;
        if (complexData != null && complexData.length != 0) {
            z = false;
        }
        this.emptyComplexPart = z;
    }

    public EscherArrayProperty(short propertyNumber, boolean isBlipId, byte[] complexData) {
        super(propertyNumber, isBlipId, checkComplexData(complexData));
        this.sizeIncludesHeaderSize = true;
    }

    private static byte[] checkComplexData(byte[] complexData) {
        if (complexData == null || complexData.length == 0) {
            return new byte[6];
        }
        return complexData;
    }

    public int getNumberOfElementsInArray() {
        if (this.emptyComplexPart) {
            return 0;
        }
        return LittleEndian.getUShort(getComplexData(), 0);
    }

    public void setNumberOfElementsInArray(int numberOfElements) {
        int expectedArraySize = (getActualSizeOfElements(getSizeOfElements()) * numberOfElements) + 6;
        if (expectedArraySize != getComplexData().length) {
            byte[] newArray = new byte[expectedArraySize];
            System.arraycopy(getComplexData(), 0, newArray, 0, getComplexData().length);
            setComplexData(newArray);
        }
        LittleEndian.putShort(getComplexData(), 0, (short) numberOfElements);
    }

    public int getNumberOfElementsInMemory() {
        if (this.emptyComplexPart) {
            return 0;
        }
        return LittleEndian.getUShort(getComplexData(), 2);
    }

    public void setNumberOfElementsInMemory(int numberOfElements) {
        int expectedArraySize = (getActualSizeOfElements(getSizeOfElements()) * numberOfElements) + 6;
        if (expectedArraySize != getComplexData().length) {
            byte[] newArray = new byte[expectedArraySize];
            System.arraycopy(getComplexData(), 0, newArray, 0, expectedArraySize);
            setComplexData(newArray);
        }
        LittleEndian.putShort(getComplexData(), 2, (short) numberOfElements);
    }

    public short getSizeOfElements() {
        if (this.emptyComplexPart) {
            return (short) 0;
        }
        return LittleEndian.getShort(getComplexData(), 4);
    }

    public void setSizeOfElements(int sizeOfElements) {
        LittleEndian.putShort(getComplexData(), 4, (short) sizeOfElements);
        int expectedArraySize = (getNumberOfElementsInArray() * getActualSizeOfElements(getSizeOfElements())) + 6;
        if (expectedArraySize != getComplexData().length) {
            byte[] newArray = new byte[expectedArraySize];
            System.arraycopy(getComplexData(), 0, newArray, 0, 6);
            setComplexData(newArray);
        }
    }

    public byte[] getElement(int index) {
        int actualSize = getActualSizeOfElements(getSizeOfElements());
        byte[] result = new byte[actualSize];
        System.arraycopy(getComplexData(), (index * actualSize) + 6, result, 0, result.length);
        return result;
    }

    public void setElement(int index, byte[] element) {
        int actualSize = getActualSizeOfElements(getSizeOfElements());
        System.arraycopy(element, 0, getComplexData(), (index * actualSize) + 6, actualSize);
    }

    @Override // org.apache.poi.ddf.EscherComplexProperty, org.apache.poi.ddf.EscherProperty
    public String toString() {
        StringBuilder results = new StringBuilder();
        results.append("propNum: ").append((int) getPropertyNumber());
        results.append(", propName: ").append(EscherProperties.getPropertyName(getPropertyNumber()));
        results.append(", complex: ").append(isComplex());
        results.append(", blipId: ").append(isBlipId());
        results.append(", data: \n");
        results.append("    {EscherArrayProperty:\n");
        results.append("     Num Elements: ").append(getNumberOfElementsInArray()).append('\n');
        results.append("     Num Elements In Memory: ").append(getNumberOfElementsInMemory()).append('\n');
        results.append("     Size of elements: ").append((int) getSizeOfElements()).append('\n');
        for (int i = 0; i < getNumberOfElementsInArray(); i++) {
            results.append("     Element ").append(i).append(": ").append(HexDump.toHex(getElement(i))).append('\n');
        }
        results.append("}\n");
        return results.toString();
    }

    @Override // org.apache.poi.ddf.EscherComplexProperty, org.apache.poi.ddf.EscherProperty
    public String toXml(String tab) {
        StringBuilder builder = new StringBuilder();
        builder.append(tab).append("<").append(getClass().getSimpleName()).append(" id=\"0x").append(HexDump.toHex(getId())).append("\" name=\"").append(getName()).append("\" blipId=\"").append(isBlipId()).append("\">\n");
        for (int i = 0; i < getNumberOfElementsInArray(); i++) {
            builder.append("\t").append(tab).append("<Element>").append(HexDump.toHex(getElement(i))).append("</Element>\n");
        }
        builder.append(tab).append("</").append(getClass().getSimpleName()).append(">");
        return builder.toString();
    }

    public int setArrayData(byte[] data, int offset) {
        if (this.emptyComplexPart) {
            setComplexData(new byte[0]);
        } else {
            short numElements = LittleEndian.getShort(data, offset);
            short sizeOfElements = LittleEndian.getShort(data, offset + 4);
            int arraySize = getActualSizeOfElements(sizeOfElements) * numElements;
            if (arraySize == getComplexData().length) {
                setComplexData(new byte[arraySize + 6]);
                this.sizeIncludesHeaderSize = false;
            }
            System.arraycopy(data, offset, getComplexData(), 0, getComplexData().length);
        }
        return getComplexData().length;
    }

    @Override // org.apache.poi.ddf.EscherComplexProperty, org.apache.poi.ddf.EscherProperty
    public int serializeSimplePart(byte[] data, int pos) {
        LittleEndian.putShort(data, pos, getId());
        int recordSize = getComplexData().length;
        if (!this.sizeIncludesHeaderSize) {
            recordSize -= 6;
        }
        LittleEndian.putInt(data, pos + 2, recordSize);
        return 6;
    }

    private static int getActualSizeOfElements(short sizeOfElements) {
        if (sizeOfElements < 0) {
            return (short) ((-sizeOfElements) >> 2);
        }
        return sizeOfElements;
    }

    @Override // java.lang.Iterable
    public Iterator<byte[]> iterator() {
        return new Iterator<byte[]>() { // from class: org.apache.poi.ddf.EscherArrayProperty.1
            int idx;

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.idx < EscherArrayProperty.this.getNumberOfElementsInArray();
            }

            @Override // java.util.Iterator
            public byte[] next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                EscherArrayProperty escherArrayProperty = EscherArrayProperty.this;
                int i = this.idx;
                this.idx = i + 1;
                return escherArrayProperty.getElement(i);
            }

            @Override // java.util.Iterator
            public void remove() {
                throw new UnsupportedOperationException("not yet implemented");
            }
        };
    }
}
