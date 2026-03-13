package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.util.CellAddress;

/* JADX INFO: loaded from: classes.dex */
public interface Comment {
    CellAddress getAddress();

    String getAuthor();

    ClientAnchor getClientAnchor();

    int getColumn();

    int getRow();

    RichTextString getString();

    boolean isVisible();

    void setAddress(int i, int i2);

    void setAddress(CellAddress cellAddress);

    void setAuthor(String str);

    void setColumn(int i);

    void setRow(int i);

    void setString(RichTextString richTextString);

    void setVisible(boolean z);
}
