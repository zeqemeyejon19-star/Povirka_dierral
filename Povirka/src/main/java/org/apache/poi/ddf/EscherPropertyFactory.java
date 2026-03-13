package org.apache.poi.ddf;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes.dex */
public final class EscherPropertyFactory {
    public List<EscherProperty> createProperties(byte[] data, int offset, short numProperties) {
        EscherProperty ep;
        List<EscherProperty> results = new ArrayList<>();
        int pos = offset;
        int i = 0;
        while (true) {
            if (i >= numProperties) {
                break;
            }
            short propId = LittleEndian.getShort(data, pos);
            int propData = LittleEndian.getInt(data, pos + 2);
            short propNumber = (short) (propId & 16383);
            boolean isComplex = (propId & Short.MIN_VALUE) != 0;
            byte propertyType = EscherProperties.getPropertyType(propNumber);
            if (propertyType == 1) {
                ep = new EscherBoolProperty(propId, propData);
            } else if (propertyType == 2) {
                ep = new EscherRGBProperty(propId, propData);
            } else if (propertyType == 3) {
                ep = new EscherShapePathProperty(propId, propData);
            } else if (!isComplex) {
                ep = new EscherSimpleProperty(propId, propData);
            } else if (propertyType == 5) {
                ep = new EscherArrayProperty(propId, new byte[propData]);
            } else {
                ep = new EscherComplexProperty(propId, new byte[propData]);
            }
            results.add(ep);
            pos += 6;
            i++;
        }
        for (EscherProperty p : results) {
            if (p instanceof EscherComplexProperty) {
                if (p instanceof EscherArrayProperty) {
                    pos += ((EscherArrayProperty) p).setArrayData(data, pos);
                } else {
                    byte[] complexData = ((EscherComplexProperty) p).getComplexData();
                    int leftover = data.length - pos;
                    if (leftover < complexData.length) {
                        throw new IllegalStateException("Could not read complex escher property, length was " + complexData.length + ", but had only " + leftover + " bytes left");
                    }
                    System.arraycopy(data, pos, complexData, 0, complexData.length);
                    pos += complexData.length;
                }
            }
        }
        return results;
    }
}
