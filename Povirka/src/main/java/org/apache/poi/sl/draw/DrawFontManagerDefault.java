package org.apache.poi.sl.draw;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Map;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import org.apache.poi.sl.draw.Drawable;

/* JADX INFO: loaded from: classes.dex */
public class DrawFontManagerDefault implements DrawFontManager {
    @Override // org.apache.poi.sl.draw.DrawFontManager
    public FontInfo getMappedFont(Graphics2D graphics, FontInfo fontInfo) {
        return getFontWithFallback(graphics, Drawable.FONT_MAP, fontInfo);
    }

    @Override // org.apache.poi.sl.draw.DrawFontManager
    public FontInfo getFallbackFont(Graphics2D graphics, FontInfo fontInfo) {
        FontInfo fi = getFontWithFallback(graphics, Drawable.FONT_FALLBACK, fontInfo);
        if (fi == null) {
            return new DrawFontInfo("SansSerif");
        }
        return fi;
    }

    @Override // org.apache.poi.sl.draw.DrawFontManager
    public String mapFontCharset(Graphics2D graphics, FontInfo fontInfo, String text) {
        if (fontInfo == null || !"Wingdings".equalsIgnoreCase(fontInfo.getTypeface())) {
            return text;
        }
        boolean changed = false;
        char[] chrs = text.toCharArray();
        for (int i = 0; i < chrs.length; i++) {
            if ((' ' <= chrs[i] && chrs[i] <= 127) || (160 <= chrs[i] && chrs[i] <= 255)) {
                chrs[i] = (char) (chrs[i] | 61440);
                changed = true;
            }
        }
        if (!changed) {
            return text;
        }
        String attStr = new String(chrs);
        return attStr;
    }

    @Override // org.apache.poi.sl.draw.DrawFontManager
    public Font createAWTFont(Graphics2D graphics2D, FontInfo fontInfo, double d, boolean z, boolean z2) {
        int i = (z2 ? 2 : 0) | (z ? 1 : 0);
        Font font = new Font(fontInfo.getTypeface(), i, 12);
        if ("Dialog".equals(font.getFamily())) {
            font = new Font("SansSerif", i, 12);
        }
        return font.deriveFont((float) d);
    }

    private FontInfo getFontWithFallback(Graphics2D graphics, Drawable.DrawableHint hint, FontInfo fontInfo) {
        Map<String, String> fontMap = (Map) graphics.getRenderingHint(hint);
        if (fontMap == null) {
            return fontInfo;
        }
        String f = fontInfo != null ? fontInfo.getTypeface() : null;
        String mappedTypeface = null;
        if (fontMap.containsKey(f)) {
            String mappedTypeface2 = fontMap.get(f);
            mappedTypeface = mappedTypeface2;
        } else if (fontMap.containsKey("*")) {
            String mappedTypeface3 = fontMap.get("*");
            mappedTypeface = mappedTypeface3;
        }
        return mappedTypeface != null ? new DrawFontInfo(mappedTypeface) : fontInfo;
    }
}
