package org.apache.poi.ss.usermodel;

import org.apache.poi.util.Removal;

/* JADX INFO: loaded from: classes.dex */
@Removal(version = "3.18")
@Deprecated
public interface Textbox {
    public static final short OBJECT_TYPE_TEXT = 6;

    int getMarginBottom();

    int getMarginLeft();

    int getMarginRight();

    int getMarginTop();

    RichTextString getString();

    void setMarginBottom(int i);

    void setMarginLeft(int i);

    void setMarginRight(int i);

    void setMarginTop(int i);

    void setString(RichTextString richTextString);
}
