package org.apache.poi.ss.extractor;

/* JADX INFO: loaded from: classes.dex */
public interface ExcelExtractor {
    String getText();

    void setFormulasNotResults(boolean z);

    void setIncludeCellComments(boolean z);

    void setIncludeHeadersFooters(boolean z);

    void setIncludeSheetNames(boolean z);
}
