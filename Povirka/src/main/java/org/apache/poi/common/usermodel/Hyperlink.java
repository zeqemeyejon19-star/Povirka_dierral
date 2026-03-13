package org.apache.poi.common.usermodel;

/* JADX INFO: loaded from: classes.dex */
public interface Hyperlink {
    String getAddress();

    String getLabel();

    int getType();

    HyperlinkType getTypeEnum();

    void setAddress(String str);

    void setLabel(String str);
}
