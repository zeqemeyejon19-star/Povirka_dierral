package org.apache.poi.xslf.usermodel;

import org.apache.poi.util.Removal;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextLineBreak;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;

/* JADX INFO: loaded from: classes.dex */
@Removal(version = "3.18")
public class DrawingParagraph {
    private final CTTextParagraph p;

    public DrawingParagraph(CTTextParagraph p) {
        this.p = p;
    }

    public CharSequence getText() {
        StringBuilder text = new StringBuilder();
        XmlCursor c = this.p.newCursor();
        c.selectPath("./*");
        while (c.toNextSelection()) {
            CTRegularTextRun object = c.getObject();
            if (object instanceof CTRegularTextRun) {
                CTRegularTextRun txrun = object;
                text.append(txrun.getT());
            } else if (object instanceof CTTextLineBreak) {
                text.append('\n');
            }
        }
        c.dispose();
        return text;
    }
}
