package org.apache.poi.sl.usermodel;

import java.awt.geom.Path2D;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.TextParagraph;

/* JADX INFO: loaded from: classes.dex */
public interface FreeformShape<S extends Shape<S, P>, P extends TextParagraph<S, P, ?>> extends AutoShape<S, P> {
    Path2D.Double getPath();

    int setPath(Path2D.Double r1);
}
