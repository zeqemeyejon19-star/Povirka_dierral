package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.usermodel.Shape;

/* JADX INFO: loaded from: classes.dex */
public interface Drawing<T extends Shape> extends ShapeContainer<T> {
    ClientAnchor createAnchor(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8);

    Comment createCellComment(ClientAnchor clientAnchor);

    Chart createChart(ClientAnchor clientAnchor);

    ObjectData createObjectData(ClientAnchor clientAnchor, int i, int i2);

    Picture createPicture(ClientAnchor clientAnchor, int i);
}
