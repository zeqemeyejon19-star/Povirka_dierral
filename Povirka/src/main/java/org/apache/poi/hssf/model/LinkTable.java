package org.apache.poi.hssf.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.record.CRNCountRecord;
import org.apache.poi.hssf.record.CRNRecord;
import org.apache.poi.hssf.record.ExternSheetRecord;
import org.apache.poi.hssf.record.ExternalNameRecord;
import org.apache.poi.hssf.record.NameCommentRecord;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SupBookRecord;
import org.apache.poi.ss.formula.SheetNameFormatter;
import org.apache.poi.ss.formula.ptg.ErrPtg;
import org.apache.poi.ss.formula.ptg.NameXPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.Workbook;

/* JADX INFO: loaded from: classes.dex */
final class LinkTable {
    private final List<NameRecord> _definedNames;
    private final ExternSheetRecord _externSheetRecord;
    private ExternalBookBlock[] _externalBookBlocks;
    private final int _recordCount;
    private final WorkbookRecordList _workbookRecordList;

    private static final class CRNBlock {
        private final CRNCountRecord _countRecord;
        private final CRNRecord[] _crns;

        public CRNBlock(RecordStream rs) {
            CRNCountRecord cRNCountRecord = (CRNCountRecord) rs.getNext();
            this._countRecord = cRNCountRecord;
            int nCRNs = cRNCountRecord.getNumberOfCRNs();
            CRNRecord[] crns = new CRNRecord[nCRNs];
            for (int i = 0; i < crns.length; i++) {
                crns[i] = (CRNRecord) rs.getNext();
            }
            this._crns = crns;
        }

        public CRNRecord[] getCrns() {
            return (CRNRecord[]) this._crns.clone();
        }
    }

    private static final class ExternalBookBlock {
        private final CRNBlock[] _crnBlocks;
        private final SupBookRecord _externalBookRecord;
        private ExternalNameRecord[] _externalNameRecords;

        public ExternalBookBlock(RecordStream rs) {
            this._externalBookRecord = (SupBookRecord) rs.getNext();
            List<Object> temp = new ArrayList<>();
            while (rs.peekNextClass() == ExternalNameRecord.class) {
                temp.add(rs.getNext());
            }
            ExternalNameRecord[] externalNameRecordArr = new ExternalNameRecord[temp.size()];
            this._externalNameRecords = externalNameRecordArr;
            temp.toArray(externalNameRecordArr);
            temp.clear();
            while (rs.peekNextClass() == CRNCountRecord.class) {
                temp.add(new CRNBlock(rs));
            }
            CRNBlock[] cRNBlockArr = new CRNBlock[temp.size()];
            this._crnBlocks = cRNBlockArr;
            temp.toArray(cRNBlockArr);
        }

        public ExternalBookBlock(String url, String[] sheetNames) {
            this._externalBookRecord = SupBookRecord.createExternalReferences(url, sheetNames);
            this._crnBlocks = new CRNBlock[0];
        }

        public ExternalBookBlock(int numberOfSheets) {
            this._externalBookRecord = SupBookRecord.createInternalReferences((short) numberOfSheets);
            this._externalNameRecords = new ExternalNameRecord[0];
            this._crnBlocks = new CRNBlock[0];
        }

        public ExternalBookBlock() {
            this._externalBookRecord = SupBookRecord.createAddInFunctions();
            this._externalNameRecords = new ExternalNameRecord[0];
            this._crnBlocks = new CRNBlock[0];
        }

        public SupBookRecord getExternalBookRecord() {
            return this._externalBookRecord;
        }

        public String getNameText(int definedNameIndex) {
            return this._externalNameRecords[definedNameIndex].getText();
        }

        public int getNameIx(int definedNameIndex) {
            return this._externalNameRecords[definedNameIndex].getIx();
        }

        public int getIndexOfName(String name) {
            int i = 0;
            while (true) {
                ExternalNameRecord[] externalNameRecordArr = this._externalNameRecords;
                if (i < externalNameRecordArr.length) {
                    if (!externalNameRecordArr[i].getText().equalsIgnoreCase(name)) {
                        i++;
                    } else {
                        return i;
                    }
                } else {
                    return -1;
                }
            }
        }

        public int getNumberOfNames() {
            return this._externalNameRecords.length;
        }

        public int addExternalName(ExternalNameRecord rec) {
            ExternalNameRecord[] externalNameRecordArr = this._externalNameRecords;
            ExternalNameRecord[] tmp = new ExternalNameRecord[externalNameRecordArr.length + 1];
            System.arraycopy(externalNameRecordArr, 0, tmp, 0, externalNameRecordArr.length);
            tmp[tmp.length - 1] = rec;
            this._externalNameRecords = tmp;
            return tmp.length - 1;
        }
    }

    public LinkTable(List<Record> inputList, int startIndex, WorkbookRecordList workbookRecordList, Map<String, NameCommentRecord> commentRecords) {
        this._workbookRecordList = workbookRecordList;
        RecordStream rs = new RecordStream(inputList, startIndex);
        List<ExternalBookBlock> temp = new ArrayList<>();
        while (rs.peekNextClass() == SupBookRecord.class) {
            temp.add(new ExternalBookBlock(rs));
        }
        ExternalBookBlock[] externalBookBlockArr = new ExternalBookBlock[temp.size()];
        this._externalBookBlocks = externalBookBlockArr;
        temp.toArray(externalBookBlockArr);
        temp.clear();
        if (this._externalBookBlocks.length <= 0 || rs.peekNextClass() != ExternSheetRecord.class) {
            this._externSheetRecord = null;
        } else {
            this._externSheetRecord = readExtSheetRecord(rs);
        }
        this._definedNames = new ArrayList();
        while (true) {
            Class<? extends Record> nextClass = rs.peekNextClass();
            if (nextClass == NameRecord.class) {
                NameRecord nr = (NameRecord) rs.getNext();
                this._definedNames.add(nr);
            } else if (nextClass == NameCommentRecord.class) {
                NameCommentRecord ncr = (NameCommentRecord) rs.getNext();
                commentRecords.put(ncr.getNameText(), ncr);
            } else {
                int countRead = rs.getCountRead();
                this._recordCount = countRead;
                this._workbookRecordList.getRecords().addAll(inputList.subList(startIndex, countRead + startIndex));
                return;
            }
        }
    }

    private static ExternSheetRecord readExtSheetRecord(RecordStream rs) {
        List<ExternSheetRecord> temp = new ArrayList<>(2);
        while (rs.peekNextClass() == ExternSheetRecord.class) {
            temp.add((ExternSheetRecord) rs.getNext());
        }
        int nItems = temp.size();
        if (nItems < 1) {
            throw new RuntimeException("Expected an EXTERNSHEET record but got (" + rs.peekNextClass().getName() + ")");
        }
        if (nItems == 1) {
            return temp.get(0);
        }
        ExternSheetRecord[] esrs = new ExternSheetRecord[nItems];
        temp.toArray(esrs);
        return ExternSheetRecord.combine(esrs);
    }

    public LinkTable(int numberOfSheets, WorkbookRecordList workbookRecordList) {
        this._workbookRecordList = workbookRecordList;
        this._definedNames = new ArrayList();
        this._externalBookBlocks = new ExternalBookBlock[]{new ExternalBookBlock(numberOfSheets)};
        ExternSheetRecord externSheetRecord = new ExternSheetRecord();
        this._externSheetRecord = externSheetRecord;
        this._recordCount = 2;
        SupBookRecord supbook = this._externalBookBlocks[0].getExternalBookRecord();
        int idx = findFirstRecordLocBySid((short) 140);
        if (idx < 0) {
            throw new RuntimeException("CountryRecord not found");
        }
        workbookRecordList.add(idx + 1, externSheetRecord);
        workbookRecordList.add(idx + 1, supbook);
    }

    public int getRecordCount() {
        return this._recordCount;
    }

    public NameRecord getSpecificBuiltinRecord(byte builtInCode, int sheetNumber) {
        for (NameRecord record : this._definedNames) {
            if (record.getBuiltInName() == builtInCode && record.getSheetNumber() == sheetNumber) {
                return record;
            }
        }
        return null;
    }

    public void removeBuiltinRecord(byte name, int sheetIndex) {
        NameRecord record = getSpecificBuiltinRecord(name, sheetIndex);
        if (record != null) {
            this._definedNames.remove(record);
        }
    }

    public int getNumNames() {
        return this._definedNames.size();
    }

    public NameRecord getNameRecord(int index) {
        return this._definedNames.get(index);
    }

    public void addName(NameRecord name) {
        this._definedNames.add(name);
        int idx = findFirstRecordLocBySid((short) 23);
        if (idx == -1) {
            idx = findFirstRecordLocBySid(SupBookRecord.sid);
        }
        if (idx == -1) {
            idx = findFirstRecordLocBySid((short) 140);
        }
        int countNames = this._definedNames.size();
        this._workbookRecordList.add(idx + countNames, name);
    }

    public void removeName(int namenum) {
        this._definedNames.remove(namenum);
    }

    public boolean nameAlreadyExists(NameRecord name) {
        for (int i = getNumNames() - 1; i >= 0; i--) {
            NameRecord rec = getNameRecord(i);
            if (rec != name && isDuplicatedNames(name, rec)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isDuplicatedNames(NameRecord firstName, NameRecord lastName) {
        return lastName.getNameText().equalsIgnoreCase(firstName.getNameText()) && isSameSheetNames(firstName, lastName);
    }

    private static boolean isSameSheetNames(NameRecord firstName, NameRecord lastName) {
        return lastName.getSheetNumber() == firstName.getSheetNumber();
    }

    public String[] getExternalBookAndSheetName(int extRefIndex) {
        int ebIx = this._externSheetRecord.getExtbookIndexFromRefIndex(extRefIndex);
        SupBookRecord ebr = this._externalBookBlocks[ebIx].getExternalBookRecord();
        if (!ebr.isExternalReferences()) {
            return null;
        }
        int shIx1 = this._externSheetRecord.getFirstSheetIndexFromRefIndex(extRefIndex);
        int shIx2 = this._externSheetRecord.getLastSheetIndexFromRefIndex(extRefIndex);
        String firstSheetName = null;
        String lastSheetName = null;
        if (shIx1 >= 0) {
            firstSheetName = ebr.getSheetNames()[shIx1];
        }
        if (shIx2 >= 0) {
            lastSheetName = ebr.getSheetNames()[shIx2];
        }
        if (shIx1 == shIx2) {
            return new String[]{ebr.getURL(), firstSheetName};
        }
        return new String[]{ebr.getURL(), firstSheetName, lastSheetName};
    }

    private int getExternalWorkbookIndex(String workbookName) {
        int i = 0;
        while (true) {
            ExternalBookBlock[] externalBookBlockArr = this._externalBookBlocks;
            if (i < externalBookBlockArr.length) {
                SupBookRecord ebr = externalBookBlockArr[i].getExternalBookRecord();
                if (!ebr.isExternalReferences() || !workbookName.equals(ebr.getURL())) {
                    i++;
                } else {
                    return i;
                }
            } else {
                return -1;
            }
        }
    }

    public int linkExternalWorkbook(String name, Workbook externalWorkbook) {
        int extBookIndex = getExternalWorkbookIndex(name);
        if (extBookIndex != -1) {
            return extBookIndex;
        }
        String[] sheetNames = new String[externalWorkbook.getNumberOfSheets()];
        for (int sn = 0; sn < sheetNames.length; sn++) {
            sheetNames[sn] = externalWorkbook.getSheetName(sn);
        }
        String url = "\u0000" + name;
        ExternalBookBlock block = new ExternalBookBlock(url, sheetNames);
        int extBookIndex2 = extendExternalBookBlocks(block);
        int idx = findFirstRecordLocBySid((short) 23);
        if (idx == -1) {
            idx = this._workbookRecordList.size();
        }
        this._workbookRecordList.add(idx, block.getExternalBookRecord());
        for (int sn2 = 0; sn2 < sheetNames.length; sn2++) {
            this._externSheetRecord.addRef(extBookIndex2, sn2, sn2);
        }
        return extBookIndex2;
    }

    public int getExternalSheetIndex(String workbookName, String firstSheetName, String lastSheetName) {
        int externalBookIndex = getExternalWorkbookIndex(workbookName);
        if (externalBookIndex == -1) {
            throw new RuntimeException("No external workbook with name '" + workbookName + "'");
        }
        SupBookRecord ebrTarget = this._externalBookBlocks[externalBookIndex].getExternalBookRecord();
        int firstSheetIndex = getSheetIndex(ebrTarget.getSheetNames(), firstSheetName);
        int lastSheetIndex = getSheetIndex(ebrTarget.getSheetNames(), lastSheetName);
        int result = this._externSheetRecord.getRefIxForSheet(externalBookIndex, firstSheetIndex, lastSheetIndex);
        if (result < 0) {
            return this._externSheetRecord.addRef(externalBookIndex, firstSheetIndex, lastSheetIndex);
        }
        return result;
    }

    private static int getSheetIndex(String[] sheetNames, String sheetName) {
        for (int i = 0; i < sheetNames.length; i++) {
            if (sheetNames[i].equals(sheetName)) {
                return i;
            }
        }
        throw new RuntimeException("External workbook does not contain sheet '" + sheetName + "'");
    }

    public int getFirstInternalSheetIndexForExtIndex(int extRefIndex) {
        if (extRefIndex >= this._externSheetRecord.getNumOfRefs() || extRefIndex < 0) {
            return -1;
        }
        return this._externSheetRecord.getFirstSheetIndexFromRefIndex(extRefIndex);
    }

    public int getLastInternalSheetIndexForExtIndex(int extRefIndex) {
        if (extRefIndex >= this._externSheetRecord.getNumOfRefs() || extRefIndex < 0) {
            return -1;
        }
        return this._externSheetRecord.getLastSheetIndexFromRefIndex(extRefIndex);
    }

    public void removeSheet(int sheetIdx) {
        this._externSheetRecord.removeSheet(sheetIdx);
    }

    public int checkExternSheet(int sheetIndex) {
        return checkExternSheet(sheetIndex, sheetIndex);
    }

    public int checkExternSheet(int firstSheetIndex, int lastSheetIndex) {
        int thisWbIndex = -1;
        int i = 0;
        while (true) {
            ExternalBookBlock[] externalBookBlockArr = this._externalBookBlocks;
            if (i >= externalBookBlockArr.length) {
                break;
            }
            SupBookRecord ebr = externalBookBlockArr[i].getExternalBookRecord();
            if (!ebr.isInternalReferences()) {
                i++;
            } else {
                thisWbIndex = i;
                break;
            }
        }
        if (thisWbIndex < 0) {
            throw new RuntimeException("Could not find 'internal references' EXTERNALBOOK");
        }
        int i2 = this._externSheetRecord.getRefIxForSheet(thisWbIndex, firstSheetIndex, lastSheetIndex);
        if (i2 >= 0) {
            return i2;
        }
        return this._externSheetRecord.addRef(thisWbIndex, firstSheetIndex, lastSheetIndex);
    }

    private int findFirstRecordLocBySid(short sid) {
        int index = 0;
        for (Record record : this._workbookRecordList.getRecords()) {
            if (record.getSid() == sid) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public String resolveNameXText(int refIndex, int definedNameIndex, InternalWorkbook workbook) {
        int extBookIndex = this._externSheetRecord.getExtbookIndexFromRefIndex(refIndex);
        int firstTabIndex = this._externSheetRecord.getFirstSheetIndexFromRefIndex(refIndex);
        if (firstTabIndex == -1) {
            throw new RuntimeException("Referenced sheet could not be found");
        }
        ExternalBookBlock externalBook = this._externalBookBlocks[extBookIndex];
        if (externalBook._externalNameRecords.length > definedNameIndex) {
            return this._externalBookBlocks[extBookIndex].getNameText(definedNameIndex);
        }
        if (firstTabIndex == -2) {
            NameRecord nr = getNameRecord(definedNameIndex);
            int sheetNumber = nr.getSheetNumber();
            StringBuffer text = new StringBuffer();
            if (sheetNumber > 0) {
                String sheetName = workbook.getSheetName(sheetNumber - 1);
                SheetNameFormatter.appendFormat(text, sheetName);
                text.append("!");
            }
            String sheetName2 = nr.getNameText();
            text.append(sheetName2);
            return text.toString();
        }
        throw new ArrayIndexOutOfBoundsException("Ext Book Index relative but beyond the supported length, was " + extBookIndex + " but maximum is " + this._externalBookBlocks.length);
    }

    public int resolveNameXIx(int refIndex, int definedNameIndex) {
        int extBookIndex = this._externSheetRecord.getExtbookIndexFromRefIndex(refIndex);
        return this._externalBookBlocks[extBookIndex].getNameIx(definedNameIndex);
    }

    public NameXPtg getNameXPtg(String name, int sheetRefIndex) {
        int definedNameIndex;
        int thisSheetRefIndex;
        int i = 0;
        while (true) {
            ExternalBookBlock[] externalBookBlockArr = this._externalBookBlocks;
            if (i < externalBookBlockArr.length) {
                definedNameIndex = externalBookBlockArr[i].getIndexOfName(name);
                if (definedNameIndex >= 0 && (thisSheetRefIndex = findRefIndexFromExtBookIndex(i)) >= 0 && (sheetRefIndex == -1 || thisSheetRefIndex == sheetRefIndex)) {
                    break;
                }
                i++;
            } else {
                return null;
            }
        }
        return new NameXPtg(thisSheetRefIndex, definedNameIndex);
    }

    public NameXPtg addNameXPtg(String name) {
        int extBlockIndex = -1;
        ExternalBookBlock extBlock = null;
        int i = 0;
        while (true) {
            ExternalBookBlock[] externalBookBlockArr = this._externalBookBlocks;
            if (i >= externalBookBlockArr.length) {
                break;
            }
            SupBookRecord ebr = externalBookBlockArr[i].getExternalBookRecord();
            if (!ebr.isAddInFunctions()) {
                i++;
            } else {
                extBlock = this._externalBookBlocks[i];
                extBlockIndex = i;
                break;
            }
        }
        if (extBlock == null) {
            extBlock = new ExternalBookBlock();
            extBlockIndex = extendExternalBookBlocks(extBlock);
            int idx = findFirstRecordLocBySid((short) 23);
            this._workbookRecordList.add(idx, extBlock.getExternalBookRecord());
            this._externSheetRecord.addRef(this._externalBookBlocks.length - 1, -2, -2);
        }
        ExternalNameRecord extNameRecord = new ExternalNameRecord();
        extNameRecord.setText(name);
        extNameRecord.setParsedExpression(new Ptg[]{ErrPtg.REF_INVALID});
        int nameIndex = extBlock.addExternalName(extNameRecord);
        int supLinkIndex = 0;
        for (Record record : this._workbookRecordList.getRecords()) {
            if ((record instanceof SupBookRecord) && ((SupBookRecord) record).isAddInFunctions()) {
                break;
            }
            supLinkIndex++;
        }
        int numberOfNames = extBlock.getNumberOfNames();
        this._workbookRecordList.add(supLinkIndex + numberOfNames, extNameRecord);
        int ix = this._externSheetRecord.getRefIxForSheet(extBlockIndex, -2, -2);
        return new NameXPtg(ix, nameIndex);
    }

    private int extendExternalBookBlocks(ExternalBookBlock newBlock) {
        ExternalBookBlock[] externalBookBlockArr = this._externalBookBlocks;
        ExternalBookBlock[] tmp = new ExternalBookBlock[externalBookBlockArr.length + 1];
        System.arraycopy(externalBookBlockArr, 0, tmp, 0, externalBookBlockArr.length);
        tmp[tmp.length - 1] = newBlock;
        this._externalBookBlocks = tmp;
        return tmp.length - 1;
    }

    private int findRefIndexFromExtBookIndex(int extBookIndex) {
        return this._externSheetRecord.findRefIndexFromExtBookIndex(extBookIndex);
    }

    public boolean changeExternalReference(String oldUrl, String newUrl) {
        ExternalBookBlock[] arr$ = this._externalBookBlocks;
        for (ExternalBookBlock ex : arr$) {
            SupBookRecord externalRecord = ex.getExternalBookRecord();
            if (externalRecord.isExternalReferences() && externalRecord.getURL().equals(oldUrl)) {
                externalRecord.setURL(newUrl);
                return true;
            }
        }
        return false;
    }
}
