package org.apache.poi.hssf.usermodel;

import java.util.List;
import org.apache.poi.ss.usermodel.ShapeContainer;

/* JADX INFO: loaded from: classes.dex */
public interface HSSFShapeContainer extends ShapeContainer<HSSFShape> {
    void addShape(HSSFShape hSSFShape);

    void clear();

    List<HSSFShape> getChildren();

    int getX1();

    int getX2();

    int getY1();

    int getY2();

    boolean removeShape(HSSFShape hSSFShape);

    void setCoordinates(int i, int i2, int i3, int i4);
}
