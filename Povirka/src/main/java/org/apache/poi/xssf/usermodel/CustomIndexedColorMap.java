package org.apache.poi.xssf.usermodel;

import java.lang.reflect.Array;
import java.util.List;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColors;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRgbColor;

/* JADX INFO: loaded from: classes.dex */
public class CustomIndexedColorMap implements IndexedColorMap {
    private final byte[][] colorIndex;

    private CustomIndexedColorMap(byte[][] colors) {
        this.colorIndex = colors;
    }

    @Override // org.apache.poi.xssf.usermodel.IndexedColorMap
    public byte[] getRGB(int index) {
        byte[][] bArr = this.colorIndex;
        if (bArr == null || index < 0 || index >= bArr.length) {
            return null;
        }
        return bArr[index];
    }

    public static CustomIndexedColorMap fromColors(CTColors colors) {
        if (colors == null || !colors.isSetIndexedColors()) {
            return null;
        }
        List<CTRgbColor> rgbColorList = colors.getIndexedColors().getRgbColorList();
        byte[][] customColorIndex = (byte[][]) Array.newInstance((Class<?>) byte.class, rgbColorList.size(), 3);
        for (int i = 0; i < rgbColorList.size(); i++) {
            customColorIndex[i] = rgbColorList.get(i).getRgb();
        }
        return new CustomIndexedColorMap(customColorIndex);
    }
}
