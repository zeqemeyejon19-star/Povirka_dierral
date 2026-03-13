package org.apache.poi.hssf.record;

import androidx.core.view.InputDeviceCompat;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public final class SupBookRecord extends StandardRecord {
    protected static final char CH_ALT_STARTUP_DIR = 7;
    protected static final char CH_DOWN_DIR = 3;
    protected static final char CH_LIB_DIR = '\b';
    protected static final char CH_LONG_VOLUME = 5;
    protected static final char CH_SAME_VOLUME = 2;
    protected static final char CH_STARTUP_DIR = 6;
    protected static final char CH_UP_DIR = 4;
    protected static final char CH_VOLUME = 1;
    private static final short SMALL_RECORD_SIZE = 4;
    private static final short TAG_ADD_IN_FUNCTIONS = 14849;
    private static final short TAG_INTERNAL_REFERENCES = 1025;
    public static final short sid = 430;
    private boolean _isAddInFunctions;
    private short field_1_number_of_sheets;
    private String field_2_encoded_url;
    private String[] field_3_sheet_names;
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) SupBookRecord.class);
    protected static final String PATH_SEPERATOR = System.getProperty("file.separator");

    public static SupBookRecord createInternalReferences(short numberOfSheets) {
        return new SupBookRecord(false, numberOfSheets);
    }

    public static SupBookRecord createAddInFunctions() {
        return new SupBookRecord(true, (short) 1);
    }

    public static SupBookRecord createExternalReferences(String url, String[] sheetNames) {
        return new SupBookRecord(url, sheetNames);
    }

    private SupBookRecord(boolean isAddInFuncs, short numberOfSheets) {
        this.field_1_number_of_sheets = numberOfSheets;
        this.field_2_encoded_url = null;
        this.field_3_sheet_names = null;
        this._isAddInFunctions = isAddInFuncs;
    }

    public SupBookRecord(String url, String[] sheetNames) {
        this.field_1_number_of_sheets = (short) sheetNames.length;
        this.field_2_encoded_url = url;
        this.field_3_sheet_names = sheetNames;
        this._isAddInFunctions = false;
    }

    public boolean isExternalReferences() {
        return this.field_3_sheet_names != null;
    }

    public boolean isInternalReferences() {
        return this.field_3_sheet_names == null && !this._isAddInFunctions;
    }

    public boolean isAddInFunctions() {
        return this.field_3_sheet_names == null && this._isAddInFunctions;
    }

    public SupBookRecord(RecordInputStream in) {
        int recLen = in.remaining();
        this.field_1_number_of_sheets = in.readShort();
        if (recLen > 4) {
            this._isAddInFunctions = false;
            this.field_2_encoded_url = in.readString();
            String[] sheetNames = new String[this.field_1_number_of_sheets];
            for (int i = 0; i < sheetNames.length; i++) {
                sheetNames[i] = in.readString();
            }
            this.field_3_sheet_names = sheetNames;
            return;
        }
        this.field_2_encoded_url = null;
        this.field_3_sheet_names = null;
        short nextShort = in.readShort();
        if (nextShort == 1025) {
            this._isAddInFunctions = false;
        } else {
            if (nextShort == 14849) {
                this._isAddInFunctions = true;
                if (this.field_1_number_of_sheets != 1) {
                    throw new RuntimeException("Expected 0x0001 for number of sheets field in 'Add-In Functions' but got (" + ((int) this.field_1_number_of_sheets) + ")");
                }
                return;
            }
            throw new RuntimeException("invalid EXTERNALBOOK code (" + Integer.toHexString(nextShort) + ")");
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[SUPBOOK ");
        if (isExternalReferences()) {
            sb.append("External References]\n");
            sb.append(" .url     = ").append(this.field_2_encoded_url).append("\n");
            sb.append(" .nSheets = ").append((int) this.field_1_number_of_sheets).append("\n");
            String[] arr$ = this.field_3_sheet_names;
            for (String sheetname : arr$) {
                sb.append("    .name = ").append(sheetname).append("\n");
            }
            sb.append("[/SUPBOOK");
        } else if (this._isAddInFunctions) {
            sb.append("Add-In Functions");
        } else {
            sb.append("Internal References");
            sb.append(" nSheets=").append((int) this.field_1_number_of_sheets);
        }
        sb.append("]");
        return sb.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        if (!isExternalReferences()) {
            return 4;
        }
        int sum = 2 + StringUtil.getEncodedSize(this.field_2_encoded_url);
        int i = 0;
        while (true) {
            String[] strArr = this.field_3_sheet_names;
            if (i < strArr.length) {
                sum += StringUtil.getEncodedSize(strArr[i]);
                i++;
            } else {
                return sum;
            }
        }
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_number_of_sheets);
        if (isExternalReferences()) {
            StringUtil.writeUnicodeString(out, this.field_2_encoded_url);
            int i = 0;
            while (true) {
                String[] strArr = this.field_3_sheet_names;
                if (i < strArr.length) {
                    StringUtil.writeUnicodeString(out, strArr[i]);
                    i++;
                } else {
                    return;
                }
            }
        } else {
            int field2val = this._isAddInFunctions ? 14849 : InputDeviceCompat.SOURCE_GAMEPAD;
            out.writeShort(field2val);
        }
    }

    public void setNumberOfSheets(short number) {
        this.field_1_number_of_sheets = number;
    }

    public short getNumberOfSheets() {
        return this.field_1_number_of_sheets;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    public String getURL() {
        String encodedUrl = this.field_2_encoded_url;
        char cCharAt = encodedUrl.charAt(0);
        if (cCharAt == 0) {
            return encodedUrl.substring(1);
        }
        if (cCharAt == 1) {
            return decodeFileName(encodedUrl);
        }
        if (cCharAt == 2) {
            return encodedUrl.substring(1);
        }
        return encodedUrl;
    }

    private static String decodeFileName(String encodedUrl) {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        while (i < encodedUrl.length()) {
            char c = encodedUrl.charAt(i);
            switch (c) {
                case 1:
                    i++;
                    char driveLetter = encodedUrl.charAt(i);
                    if (driveLetter == '@') {
                        sb.append("\\\\");
                    } else {
                        sb.append(driveLetter).append(":");
                    }
                    break;
                case 2:
                    sb.append(PATH_SEPERATOR);
                    break;
                case 3:
                    sb.append(PATH_SEPERATOR);
                    break;
                case 4:
                    sb.append("..").append(PATH_SEPERATOR);
                    break;
                case 5:
                    logger.log(5, "Found unexpected key: ChLongVolume - IGNORING");
                    break;
                case 6:
                case 7:
                case '\b':
                    logger.log(5, "EXCEL.EXE path unkown - using this directoy instead: .");
                    sb.append(".").append(PATH_SEPERATOR);
                    break;
                default:
                    sb.append(c);
                    break;
            }
            i++;
        }
        return sb.toString();
    }

    public String[] getSheetNames() {
        return (String[]) this.field_3_sheet_names.clone();
    }

    public void setURL(String pUrl) {
        this.field_2_encoded_url = this.field_2_encoded_url.substring(0, 1) + pUrl;
    }
}
