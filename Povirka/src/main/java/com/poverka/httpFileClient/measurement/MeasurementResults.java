package com.poverka.httpFileClient.measurement;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class MeasurementResults implements Parcelable {
    public static final Parcelable.Creator<MeasurementResults> CREATOR = new Parcelable.Creator<MeasurementResults>() { // from class: com.poverka.httpFileClient.measurement.MeasurementResults.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MeasurementResults createFromParcel(Parcel in) {
            return new MeasurementResults(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MeasurementResults[] newArray(int size) {
            return new MeasurementResults[size];
        }
    };
    private static final int MEASUREMENT_RESULT_INVALID = 2;
    private static final int MEASUREMENT_RESULT_UNKNOWN = 0;
    private static final int MEASUREMENT_RESULT_VALID = 1;
    private int consumptionAvrgImp;
    private float consumptionAvrgLit;
    private int consumptionCurImp;
    private float consumptionCurLit;
    private int duration;
    private int error;
    private final int impLiter;
    private final MeasurementSettings measSettings;
    private final int measurementNumber;
    private byte[] photoEnd;
    private byte[] photoStart;
    private final int reiterationNumber;
    private int result;
    private int valEnd;
    private int valStart;
    private int volumeCurImp;
    private float volumeCurLit;
    private float waterTemperature;

    MeasurementResults(int measurementNumber, int reiterationNumber, MeasurementSettings measSettings, int impLiter) {
        this.measurementNumber = measurementNumber;
        this.reiterationNumber = reiterationNumber;
        this.measSettings = measSettings;
        this.impLiter = impLiter;
        initValues();
    }

    private void initValues() {
        this.consumptionCurImp = 0;
        this.consumptionCurLit = 0.0f;
        this.consumptionAvrgImp = 0;
        this.consumptionAvrgLit = 0.0f;
        this.volumeCurImp = 0;
        this.volumeCurLit = 0.0f;
        this.duration = 0;
        this.error = 0;
        this.valStart = 0;
        this.valEnd = 0;
        this.result = 0;
    }

    public int getMeasurementNumber() {
        return this.measurementNumber;
    }

    public int getReiterationNumber() {
        return this.reiterationNumber;
    }

    public float getConsumptionCurLit() {
        return this.consumptionCurLit;
    }

    public int getConsumptionAvrgImp() {
        return this.consumptionAvrgImp;
    }

    public float getConsumptionAvrgLit() {
        return this.consumptionAvrgLit;
    }

    public int getVolumeCurImp() {
        return this.volumeCurImp;
    }

    public int getRequiredConsumptionLit() {
        return this.measSettings.getConsumptionLit();
    }

    public int getRequiredVolumeLit() {
        return this.measSettings.getVolumeLit();
    }

    public int getRequiredVolumeImp() {
        return this.measSettings.getVolumeImp();
    }

    public float getRequiredLowLimit() {
        return this.measSettings.getConsumptionLit() - ((this.measSettings.getConsumptionLit() * this.measSettings.getLowLimit()) / 100.0f);
    }

    public float getRequiredHighLimit() {
        return this.measSettings.getConsumptionLit() + ((this.measSettings.getConsumptionLit() * this.measSettings.getHighLimit()) / 100.0f);
    }

    public int getRequiredError() {
        return this.measSettings.getError();
    }

    public void setRequiredError(int error) {
        this.measSettings.setError(error);
    }

    public void updateValues(int consumptionCurImp, int consumptionAvrgImp, int volumeCurImp) {
        this.consumptionCurImp = consumptionCurImp;
        this.consumptionAvrgImp = consumptionAvrgImp;
        this.volumeCurImp = volumeCurImp;
        int i = this.impLiter;
        this.consumptionCurLit = (consumptionCurImp * 3.6f) / i;
        this.consumptionAvrgLit = (consumptionAvrgImp * 3.6f) / i;
        this.volumeCurLit = (volumeCurImp * 1.0f) / i;
    }

    public void setAverageConsumption(int consumptionAvrgImp) {
        this.consumptionAvrgImp = consumptionAvrgImp;
        this.consumptionAvrgLit = (consumptionAvrgImp * 3.6f) / this.impLiter;
    }

    public String getTimeLeft() {
        int timeLeft;
        return (this.consumptionCurLit != 0.0f && (timeLeft = (int) ((((((float) this.measSettings.getVolumeLit()) - this.volumeCurLit) / this.consumptionCurLit) / 1000.0f) * 3600.0f)) <= 999) ? timeLeft < 1 ? "0" : String.valueOf(timeLeft) : ">999";
    }

    public boolean hasPhotoStart() {
        return this.photoStart != null;
    }

    public boolean hasPhotoEnd() {
        return this.photoEnd != null;
    }

    public String getPhotoName(int numb) {
        return String.format(Locale.ROOT, "1/current/photo_%d%d%d.jpeg", Integer.valueOf(this.measurementNumber), Integer.valueOf(this.reiterationNumber), Integer.valueOf(numb));
    }

    public void setPhoto(byte[] photo, int photoNumber) {
        if (photoNumber == 1) {
            this.photoStart = photo;
        } else if (photoNumber == 2) {
            this.photoEnd = photo;
        }
    }

    public void clearData() {
        this.photoStart = null;
        this.photoEnd = null;
        this.valStart = 0;
        this.valEnd = 0;
        this.result = 0;
        this.error = 0;
    }

    public Bitmap getBitmap(int numb) {
        if (numb == 1) {
            byte[] bArr = this.photoStart;
            Bitmap bitmap = BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
            return bitmap;
        }
        if (numb != 2) {
            return null;
        }
        byte[] bArr2 = this.photoEnd;
        Bitmap bitmap2 = BitmapFactory.decodeByteArray(bArr2, 0, bArr2.length);
        return bitmap2;
    }

    public boolean calculate() {
        int i;
        int i2 = this.valStart;
        if (i2 > 0 && (i = this.valEnd) > 0) {
            if (i >= i2) {
                this.error = ((i - i2) - (this.measSettings.getVolumeLit() * 1000)) / this.measSettings.getVolumeLit();
                if (this.measSettings.getError() * 10 >= Math.abs(this.error)) {
                    this.result = 1;
                } else {
                    this.result = 2;
                }
            } else {
                this.error = 0;
                this.result = 0;
                return false;
            }
        } else {
            this.error = 0;
            this.result = 0;
        }
        return true;
    }

    public int curConRange() {
        if (this.consumptionCurLit * 1000.0f >= getRequiredLowLimit() && this.consumptionCurLit * 1000.0f <= getRequiredHighLimit()) {
            return 0;
        }
        if (this.consumptionCurLit * 1000.0f < getRequiredLowLimit()) {
            return -1;
        }
        if (this.consumptionCurLit * 1000.0f <= getRequiredHighLimit()) {
            return 0;
        }
        return 1;
    }

    public int avrgConRange() {
        if (this.consumptionAvrgLit * 1000.0f >= getRequiredLowLimit() && this.consumptionAvrgLit * 1000.0f <= getRequiredHighLimit()) {
            return 0;
        }
        if (this.consumptionAvrgLit * 1000.0f < getRequiredLowLimit()) {
            return -1;
        }
        if (this.consumptionAvrgLit * 1000.0f <= getRequiredHighLimit()) {
            return 0;
        }
        return 1;
    }

    public int getValStart() {
        return this.valStart;
    }

    public void setValStart(int valStart) {
        this.valStart = valStart;
    }

    public int getValEnd() {
        return this.valEnd;
    }

    public void setValEnd(int valEnd) {
        this.valEnd = valEnd;
    }

    public int getError() {
        return this.error;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getResult() {
        return this.result;
    }

    public float getWaterTemperature() {
        return this.waterTemperature;
    }

    public void setWaterTemperature(int waterTemperature) {
        this.waterTemperature = waterTemperature / 1000.0f;
    }

    public String measurementData() {
        return "Measurement Data{ consumptionCurImp=" + this.consumptionCurImp + ", consumptionCurLit=" + this.consumptionCurLit + ", consumptionAvrgImp=" + this.consumptionAvrgImp + ", consumptionAvrgLit=" + this.consumptionAvrgLit + ", volumeCurImp=" + this.volumeCurImp + ", volumeCurLit=" + this.volumeCurLit + '}';
    }

    public String toString() {
        return "MeasurementResults{measurementNumber=" + this.measurementNumber + ", reiterationNumber=" + this.reiterationNumber + ", measurementSetting=" + this.measSettings.toString() + ", impLiter=" + this.impLiter + ", consumptionCurImp=" + this.consumptionCurImp + ", consumptionCurLit=" + this.consumptionCurLit + ", consumptionAvrgImp=" + this.consumptionAvrgImp + ", consumptionAvrgLit=" + this.consumptionAvrgLit + ", volumeCurImp=" + this.volumeCurImp + ", volumeCurLit=" + this.volumeCurLit + ", duration=" + this.duration + ", error=" + this.error + ", valStart=" + this.valStart + ", valEnd=" + this.valEnd + ", result=" + this.result + ", waterTemperature=" + this.waterTemperature + ", photoStart=" + (this.photoStart != null) + ", photoEnd=" + (this.photoEnd != null) + '}';
    }

    private MeasurementResults(Parcel in) {
        this.measurementNumber = in.readInt();
        this.reiterationNumber = in.readInt();
        this.measSettings = (MeasurementSettings) in.readValue(MeasurementSettings.class.getClassLoader());
        this.impLiter = in.readInt();
        this.consumptionCurImp = in.readInt();
        this.consumptionCurLit = in.readFloat();
        this.consumptionAvrgImp = in.readInt();
        this.consumptionAvrgLit = in.readFloat();
        this.volumeCurImp = in.readInt();
        this.volumeCurLit = in.readFloat();
        if (in.readInt() == 1) {
            byte[] bArr = new byte[in.readInt()];
            this.photoStart = bArr;
            in.readByteArray(bArr);
        }
        if (in.readInt() == 1) {
            byte[] bArr2 = new byte[in.readInt()];
            this.photoEnd = bArr2;
            in.readByteArray(bArr2);
        }
        this.duration = in.readInt();
        this.error = in.readInt();
        this.valStart = in.readInt();
        this.valEnd = in.readInt();
        this.result = in.readInt();
        this.waterTemperature = in.readFloat();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.measurementNumber);
        dest.writeInt(this.reiterationNumber);
        dest.writeValue(this.measSettings);
        dest.writeInt(this.impLiter);
        dest.writeInt(this.consumptionCurImp);
        dest.writeFloat(this.consumptionCurLit);
        dest.writeInt(this.consumptionAvrgImp);
        dest.writeFloat(this.consumptionAvrgLit);
        dest.writeInt(this.volumeCurImp);
        dest.writeFloat(this.volumeCurLit);
        if (this.photoStart == null) {
            dest.writeInt(0);
        } else {
            dest.writeInt(1);
            dest.writeInt(this.photoStart.length);
            dest.writeByteArray(this.photoStart);
        }
        if (this.photoEnd == null) {
            dest.writeInt(0);
        } else {
            dest.writeInt(1);
            dest.writeInt(this.photoEnd.length);
            dest.writeByteArray(this.photoEnd);
        }
        dest.writeInt(this.duration);
        dest.writeInt(this.error);
        dest.writeInt(this.valStart);
        dest.writeInt(this.valEnd);
        dest.writeInt(this.result);
        dest.writeFloat(this.waterTemperature);
    }
}
