package org.apache.poi.ss.usermodel;

/* JADX INFO: loaded from: classes.dex */
public interface PictureData {
    byte[] getData();

    String getMimeType();

    int getPictureType();

    String suggestFileExtension();
}
