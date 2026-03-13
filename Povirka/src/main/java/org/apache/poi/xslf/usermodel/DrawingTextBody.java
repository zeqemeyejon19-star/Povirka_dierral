package org.apache.poi.xslf.usermodel;

import org.apache.poi.util.Removal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;

/* JADX INFO: loaded from: classes.dex */
@Removal(version = "3.18")
public class DrawingTextBody {
    private final CTTextBody textBody;

    public DrawingTextBody(CTTextBody textBody) {
        this.textBody = textBody;
    }

    public DrawingParagraph[] getParagraphs() {
        CTTextParagraph[] paragraphs = this.textBody.getPArray();
        DrawingParagraph[] o = new DrawingParagraph[paragraphs.length];
        for (int i = 0; i < o.length; i++) {
            o[i] = new DrawingParagraph(paragraphs[i]);
        }
        return o;
    }
}
