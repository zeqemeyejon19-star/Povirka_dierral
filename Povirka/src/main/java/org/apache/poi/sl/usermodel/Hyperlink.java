package org.apache.poi.sl.usermodel;

import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.TextParagraph;

/* JADX INFO: loaded from: classes.dex */
public interface Hyperlink<S extends Shape<S, P>, P extends TextParagraph<S, P, ?>> extends org.apache.poi.common.usermodel.Hyperlink {
    void linkToEmail(String str);

    void linkToFirstSlide();

    void linkToLastSlide();

    void linkToNextSlide();

    void linkToPreviousSlide();

    void linkToSlide(Slide<S, P> slide);

    void linkToUrl(String str);
}
