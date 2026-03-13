package org.apache.poi.hssf.extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.POIDocument;
import org.apache.poi.POIOLE2TextExtractor;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/* JADX INFO: loaded from: classes.dex */
public class EventBasedExcelExtractor extends POIOLE2TextExtractor implements org.apache.poi.ss.extractor.ExcelExtractor {
    private DirectoryNode _dir;
    boolean _formulasNotResults;
    boolean _includeSheetNames;

    public EventBasedExcelExtractor(DirectoryNode dir) {
        super((POIDocument) null);
        this._includeSheetNames = true;
        this._formulasNotResults = false;
        this._dir = dir;
    }

    public EventBasedExcelExtractor(POIFSFileSystem fs) {
        this(fs.getRoot());
        super.setFilesystem(fs);
    }

    @Override // org.apache.poi.POIOLE2TextExtractor
    public DocumentSummaryInformation getDocSummaryInformation() {
        throw new IllegalStateException("Metadata extraction not supported in streaming mode, please use ExcelExtractor");
    }

    @Override // org.apache.poi.POIOLE2TextExtractor
    public SummaryInformation getSummaryInformation() {
        throw new IllegalStateException("Metadata extraction not supported in streaming mode, please use ExcelExtractor");
    }

    @Override // org.apache.poi.ss.extractor.ExcelExtractor
    public void setIncludeCellComments(boolean includeComments) {
        throw new IllegalStateException("Comment extraction not supported in streaming mode, please use ExcelExtractor");
    }

    @Override // org.apache.poi.ss.extractor.ExcelExtractor
    public void setIncludeHeadersFooters(boolean includeHeadersFooters) {
        throw new IllegalStateException("Header/Footer extraction not supported in streaming mode, please use ExcelExtractor");
    }

    @Override // org.apache.poi.ss.extractor.ExcelExtractor
    public void setIncludeSheetNames(boolean includeSheetNames) {
        this._includeSheetNames = includeSheetNames;
    }

    @Override // org.apache.poi.ss.extractor.ExcelExtractor
    public void setFormulasNotResults(boolean formulasNotResults) {
        this._formulasNotResults = formulasNotResults;
    }

    @Override // org.apache.poi.POITextExtractor
    public String getText() {
        try {
            TextListener tl = triggerExtraction();
            String text = tl._text.toString();
            return !text.endsWith("\n") ? text + "\n" : text;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TextListener triggerExtraction() throws IOException {
        TextListener tl = new TextListener();
        FormatTrackingHSSFListener ft = new FormatTrackingHSSFListener(tl);
        tl._ft = ft;
        HSSFEventFactory factory = new HSSFEventFactory();
        HSSFRequest request = new HSSFRequest();
        request.addListenerForAllRecords(ft);
        factory.processWorkbookEvents(request, this._dir);
        return tl;
    }

    private class TextListener implements HSSFListener {
        FormatTrackingHSSFListener _ft;
        private int rowNum;
        private SSTRecord sstRecord;
        final StringBuffer _text = new StringBuffer();
        private int sheetNum = -1;
        private boolean outputNextStringValue = false;
        private int nextRow = -1;
        private final List<String> sheetNames = new ArrayList();

        public TextListener() {
        }

        @Override // org.apache.poi.hssf.eventusermodel.HSSFListener
        public void processRecord(Record record) {
            String thisText = null;
            int thisRow = -1;
            short sid = record.getSid();
            if (sid == 6) {
                FormulaRecord frec = (FormulaRecord) record;
                thisRow = frec.getRow();
                if (EventBasedExcelExtractor.this._formulasNotResults) {
                    thisText = HSSFFormulaParser.toFormulaString((HSSFWorkbook) null, frec.getParsedExpression());
                } else if (frec.hasCachedResultString()) {
                    this.outputNextStringValue = true;
                    this.nextRow = frec.getRow();
                } else {
                    thisText = this._ft.formatNumberDateCell(frec);
                }
            } else if (sid == 28) {
                NoteRecord nrec = (NoteRecord) record;
                thisRow = nrec.getRow();
            } else if (sid == 133) {
                BoundSheetRecord sr = (BoundSheetRecord) record;
                this.sheetNames.add(sr.getSheetname());
            } else if (sid != 519) {
                if (sid == 2057) {
                    BOFRecord bof = (BOFRecord) record;
                    if (bof.getType() == 16) {
                        this.sheetNum++;
                        this.rowNum = -1;
                        if (EventBasedExcelExtractor.this._includeSheetNames) {
                            if (this._text.length() > 0) {
                                this._text.append("\n");
                            }
                            this._text.append(this.sheetNames.get(this.sheetNum));
                        }
                    }
                } else if (sid == 252) {
                    this.sstRecord = (SSTRecord) record;
                } else if (sid == 253) {
                    LabelSSTRecord lsrec = (LabelSSTRecord) record;
                    thisRow = lsrec.getRow();
                    SSTRecord sSTRecord = this.sstRecord;
                    if (sSTRecord == null) {
                        throw new IllegalStateException("No SST record found");
                    }
                    thisText = sSTRecord.getString(lsrec.getSSTIndex()).toString();
                } else if (sid == 515) {
                    NumberRecord numrec = (NumberRecord) record;
                    thisRow = numrec.getRow();
                    thisText = this._ft.formatNumberDateCell(numrec);
                } else if (sid == 516) {
                    LabelRecord lrec = (LabelRecord) record;
                    thisRow = lrec.getRow();
                    thisText = lrec.getValue();
                }
            } else if (this.outputNextStringValue) {
                StringRecord srec = (StringRecord) record;
                thisText = srec.getString();
                thisRow = this.nextRow;
                this.outputNextStringValue = false;
            }
            if (thisText != null) {
                if (thisRow != this.rowNum) {
                    this.rowNum = thisRow;
                    if (this._text.length() > 0) {
                        this._text.append("\n");
                    }
                } else {
                    this._text.append("\t");
                }
                this._text.append(thisText);
            }
        }
    }
}
