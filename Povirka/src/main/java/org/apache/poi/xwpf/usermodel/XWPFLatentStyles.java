package org.apache.poi.xwpf.usermodel;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLatentStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLsdException;

/* JADX INFO: loaded from: classes.dex */
public class XWPFLatentStyles {
    private CTLatentStyles latentStyles;
    protected XWPFStyles styles;

    protected XWPFLatentStyles() {
    }

    protected XWPFLatentStyles(CTLatentStyles latentStyles) {
        this(latentStyles, null);
    }

    protected XWPFLatentStyles(CTLatentStyles latentStyles, XWPFStyles styles) {
        this.latentStyles = latentStyles;
        this.styles = styles;
    }

    public int getNumberOfStyles() {
        return this.latentStyles.sizeOfLsdExceptionArray();
    }

    protected boolean isLatentStyle(String latentStyleID) {
        CTLsdException[] arr$ = this.latentStyles.getLsdExceptionArray();
        for (CTLsdException lsd : arr$) {
            if (lsd.getName().equals(latentStyleID)) {
                return true;
            }
        }
        return false;
    }
}
