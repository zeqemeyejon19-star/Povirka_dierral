package com.poverka.httpFileClient.measurement;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class MeasurementSettings implements Parcelable {
    public static final Parcelable.Creator<MeasurementSettings> CREATOR = new Parcelable.Creator<MeasurementSettings>() { // from class: com.poverka.httpFileClient.measurement.MeasurementSettings.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MeasurementSettings createFromParcel(Parcel in) {
            return new MeasurementSettings(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MeasurementSettings[] newArray(int size) {
            return new MeasurementSettings[size];
        }
    };
    private int consumptionImp;
    private int consumptionLit;
    private int error;
    private int highLimit;
    private int id;
    private int lowLimit;
    private int number;
    private int testName;
    private int volumeImp;
    private int volumeLit;

    MeasurementSettings(int id, int testName, int number, int consumptionLit, int volumeLit, int highLimit, int lowLimit, int error, int impLiter) {
        this.id = id;
        this.testName = testName;
        this.number = number;
        this.consumptionLit = consumptionLit;
        this.consumptionImp = (consumptionLit * impLiter) / 3600;
        this.volumeLit = volumeLit;
        this.volumeImp = volumeLit * impLiter;
        this.highLimit = highLimit;
        this.lowLimit = lowLimit;
        this.error = error;
    }

    public MeasurementSettings(JSONObject setting, int impLiter, int multiplier) {
        try {
            this.id = setting.getInt("meas_id");
            this.testName = setting.getInt("test_name");
            this.number = setting.getInt("meas_numb");
            int i = setting.getInt("con");
            this.consumptionLit = i;
            this.consumptionImp = (i * impLiter) / 3600;
            int i2 = setting.getInt("volume") * multiplier;
            this.volumeLit = i2;
            this.volumeImp = i2 * impLiter;
            this.highLimit = setting.getInt("h_lim");
            this.lowLimit = setting.getInt("l_lim");
            this.error = setting.getInt("error");
        } catch (JSONException e) {
            Log.e("MeasurementSettings", "error in JSON constructor");
        }
    }

    public int getNumber() {
        return this.number;
    }

    int getConsumptionImp() {
        return this.consumptionImp;
    }

    int getConsumptionLit() {
        return this.consumptionLit;
    }

    int getVolumeImp() {
        return this.volumeImp;
    }

    int getVolumeLit() {
        return this.volumeLit;
    }

    int getHighLimit() {
        return this.highLimit;
    }

    int getLowLimit() {
        return this.lowLimit;
    }

    int getError() {
        return this.error;
    }

    void setError(int error) {
        this.error = error;
    }

    public String toString() {
        return "MeasSettings{id=" + this.id + ", testName=" + this.testName + ", number=" + this.number + ", consumptionLit=" + this.consumptionLit + ", consumptionImp=" + this.consumptionImp + ", volumeLit=" + this.volumeLit + ", volumeImp=" + this.volumeImp + ", highLimit=" + this.highLimit + ", lowLimit=" + this.lowLimit + ", error=" + this.error + '}';
    }

    JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("meas_id", this.id);
            json.put("meas_numb", this.number);
            json.put("test_name", this.testName);
            json.put("con", this.consumptionLit);
            json.put("h_lim", this.highLimit);
            json.put("l_lim", this.lowLimit);
            json.put("volume", this.volumeLit);
            json.put("error", this.error);
            return json;
        } catch (JSONException e) {
            return null;
        }
    }

    private MeasurementSettings(Parcel in) {
        this.id = in.readInt();
        this.testName = in.readInt();
        this.number = in.readInt();
        this.consumptionLit = in.readInt();
        this.consumptionImp = in.readInt();
        this.volumeLit = in.readInt();
        this.volumeImp = in.readInt();
        this.highLimit = in.readInt();
        this.lowLimit = in.readInt();
        this.error = in.readInt();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.testName);
        dest.writeInt(this.number);
        dest.writeInt(this.consumptionLit);
        dest.writeInt(this.consumptionImp);
        dest.writeInt(this.volumeLit);
        dest.writeInt(this.volumeImp);
        dest.writeInt(this.highLimit);
        dest.writeInt(this.lowLimit);
        dest.writeInt(this.error);
    }
}
