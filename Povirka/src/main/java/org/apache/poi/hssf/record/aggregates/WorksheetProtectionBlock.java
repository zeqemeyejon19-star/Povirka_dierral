package org.apache.poi.hssf.record.aggregates;

import org.apache.poi.hssf.model.RecordStream;
import org.apache.poi.hssf.record.ObjectProtectRecord;
import org.apache.poi.hssf.record.PasswordRecord;
import org.apache.poi.hssf.record.ProtectRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.ScenarioProtectRecord;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.util.RecordFormatException;

/* JADX INFO: loaded from: classes.dex */
public final class WorksheetProtectionBlock extends RecordAggregate {
    private ObjectProtectRecord _objectProtectRecord;
    private PasswordRecord _passwordRecord;
    private ProtectRecord _protectRecord;
    private ScenarioProtectRecord _scenarioProtectRecord;

    public static boolean isComponentRecord(int sid) {
        if (sid == 18 || sid == 19 || sid == 99 || sid == 221) {
            return true;
        }
        return false;
    }

    private boolean readARecord(RecordStream rs) {
        int iPeekNextSid = rs.peekNextSid();
        if (iPeekNextSid == 18) {
            checkNotPresent(this._protectRecord);
            this._protectRecord = (ProtectRecord) rs.getNext();
            return true;
        }
        if (iPeekNextSid == 19) {
            checkNotPresent(this._passwordRecord);
            this._passwordRecord = (PasswordRecord) rs.getNext();
            return true;
        }
        if (iPeekNextSid == 99) {
            checkNotPresent(this._objectProtectRecord);
            this._objectProtectRecord = (ObjectProtectRecord) rs.getNext();
            return true;
        }
        if (iPeekNextSid == 221) {
            checkNotPresent(this._scenarioProtectRecord);
            this._scenarioProtectRecord = (ScenarioProtectRecord) rs.getNext();
            return true;
        }
        return false;
    }

    private void checkNotPresent(Record rec) {
        if (rec != null) {
            throw new RecordFormatException("Duplicate PageSettingsBlock record (sid=0x" + Integer.toHexString(rec.getSid()) + ")");
        }
    }

    @Override // org.apache.poi.hssf.record.aggregates.RecordAggregate
    public void visitContainedRecords(RecordAggregate.RecordVisitor rv) {
        visitIfPresent(this._protectRecord, rv);
        visitIfPresent(this._objectProtectRecord, rv);
        visitIfPresent(this._scenarioProtectRecord, rv);
        visitIfPresent(this._passwordRecord, rv);
    }

    private static void visitIfPresent(Record r, RecordAggregate.RecordVisitor rv) {
        if (r != null) {
            rv.visitRecord(r);
        }
    }

    public PasswordRecord getPasswordRecord() {
        return this._passwordRecord;
    }

    public ScenarioProtectRecord getHCenter() {
        return this._scenarioProtectRecord;
    }

    public void addRecords(RecordStream rs) {
        while (readARecord(rs)) {
        }
    }

    private ProtectRecord getProtect() {
        if (this._protectRecord == null) {
            this._protectRecord = new ProtectRecord(false);
        }
        return this._protectRecord;
    }

    private PasswordRecord getPassword() {
        if (this._passwordRecord == null) {
            this._passwordRecord = createPassword();
        }
        return this._passwordRecord;
    }

    public void protectSheet(String password, boolean shouldProtectObjects, boolean shouldProtectScenarios) {
        if (password == null) {
            this._passwordRecord = null;
            this._protectRecord = null;
            this._objectProtectRecord = null;
            this._scenarioProtectRecord = null;
            return;
        }
        ProtectRecord prec = getProtect();
        PasswordRecord pass = getPassword();
        prec.setProtect(true);
        pass.setPassword((short) CryptoFunctions.createXorVerifier1(password));
        if (this._objectProtectRecord == null && shouldProtectObjects) {
            ObjectProtectRecord rec = createObjectProtect();
            rec.setProtect(true);
            this._objectProtectRecord = rec;
        }
        if (this._scenarioProtectRecord == null && shouldProtectScenarios) {
            ScenarioProtectRecord srec = createScenarioProtect();
            srec.setProtect(true);
            this._scenarioProtectRecord = srec;
        }
    }

    public boolean isSheetProtected() {
        ProtectRecord protectRecord = this._protectRecord;
        return protectRecord != null && protectRecord.getProtect();
    }

    public boolean isObjectProtected() {
        ObjectProtectRecord objectProtectRecord = this._objectProtectRecord;
        return objectProtectRecord != null && objectProtectRecord.getProtect();
    }

    public boolean isScenarioProtected() {
        ScenarioProtectRecord scenarioProtectRecord = this._scenarioProtectRecord;
        return scenarioProtectRecord != null && scenarioProtectRecord.getProtect();
    }

    private static ObjectProtectRecord createObjectProtect() {
        ObjectProtectRecord retval = new ObjectProtectRecord();
        retval.setProtect(false);
        return retval;
    }

    private static ScenarioProtectRecord createScenarioProtect() {
        ScenarioProtectRecord retval = new ScenarioProtectRecord();
        retval.setProtect(false);
        return retval;
    }

    private static PasswordRecord createPassword() {
        return new PasswordRecord(0);
    }

    public int getPasswordHash() {
        PasswordRecord passwordRecord = this._passwordRecord;
        if (passwordRecord == null) {
            return 0;
        }
        return passwordRecord.getPassword();
    }
}
