package org.apache.poi.common.usermodel.fonts;

/* JADX INFO: loaded from: classes.dex */
public interface FontInfo {
    FontCharset getCharset();

    FontFamily getFamily();

    Integer getIndex();

    FontPitch getPitch();

    String getTypeface();

    void setCharset(FontCharset fontCharset);

    void setFamily(FontFamily fontFamily);

    void setIndex(int i);

    void setPitch(FontPitch fontPitch);

    void setTypeface(String str);
}
