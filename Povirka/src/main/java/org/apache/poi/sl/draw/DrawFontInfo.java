package org.apache.poi.sl.draw;

import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.common.usermodel.fonts.FontFamily;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import org.apache.poi.common.usermodel.fonts.FontPitch;
import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
@Internal
class DrawFontInfo implements FontInfo {
    private final String typeface;

    DrawFontInfo(String typeface) {
        this.typeface = typeface;
    }

    @Override // org.apache.poi.common.usermodel.fonts.FontInfo
    public Integer getIndex() {
        return null;
    }

    @Override // org.apache.poi.common.usermodel.fonts.FontInfo
    public void setIndex(int index) {
        throw new UnsupportedOperationException("DrawFontManagers FontInfo can't be changed.");
    }

    @Override // org.apache.poi.common.usermodel.fonts.FontInfo
    public String getTypeface() {
        return this.typeface;
    }

    @Override // org.apache.poi.common.usermodel.fonts.FontInfo
    public void setTypeface(String typeface) {
        throw new UnsupportedOperationException("DrawFontManagers FontInfo can't be changed.");
    }

    @Override // org.apache.poi.common.usermodel.fonts.FontInfo
    public FontCharset getCharset() {
        return FontCharset.ANSI;
    }

    @Override // org.apache.poi.common.usermodel.fonts.FontInfo
    public void setCharset(FontCharset charset) {
        throw new UnsupportedOperationException("DrawFontManagers FontInfo can't be changed.");
    }

    @Override // org.apache.poi.common.usermodel.fonts.FontInfo
    public FontFamily getFamily() {
        return FontFamily.FF_SWISS;
    }

    @Override // org.apache.poi.common.usermodel.fonts.FontInfo
    public void setFamily(FontFamily family) {
        throw new UnsupportedOperationException("DrawFontManagers FontInfo can't be changed.");
    }

    @Override // org.apache.poi.common.usermodel.fonts.FontInfo
    public FontPitch getPitch() {
        return FontPitch.VARIABLE;
    }

    @Override // org.apache.poi.common.usermodel.fonts.FontInfo
    public void setPitch(FontPitch pitch) {
        throw new UnsupportedOperationException("DrawFontManagers FontInfo can't be changed.");
    }
}
