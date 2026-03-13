package com.poverka.httpFileClient.measurement;

import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.poverka.httpFileClient.R;
import com.poverka.httpFileClient.util.MyFileReader;
import com.poverka.httpFileClient.util.MyJSON;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class CounterVerification implements Parcelable {
    private static final int BATTERY_THRESHOLD_PERCENT = 10;
    public static final Parcelable.Creator<CounterVerification> CREATOR = new Parcelable.Creator<CounterVerification>() { // from class: com.poverka.httpFileClient.measurement.CounterVerification.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CounterVerification createFromParcel(Parcel in) {
            return new CounterVerification(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CounterVerification[] newArray(int size) {
            return new CounterVerification[size];
        }
    };
    private static final String TAG = "CounterVerification";
    private int DNtypeNumber;
    private int action;
    private float batteryCharge;
    private String char1;
    private String char2;
    private String counterNumber;
    private int currentStationTime;
    private int dueDate;
    private int environmentH;
    private int environmentP;
    private int environmentT;
    private String id;
    private int impulsesPerLiter;
    private int initialVolume;
    private float innerHumidity;
    private float innerTemperature;
    private int isOnline;
    private double latitude;
    private int local;
    private double longitude;
    private ArrayList<MeasurementResults> measResults;
    private MeasurementSettings[] measSettings;
    private int multiplier;
    private int paymentAccount;
    private String personalAccount;
    private int productionDate;
    private int productionYear;
    private int serviceType;
    private int stationNumber;
    private int testName;
    private byte[] testPhoto;
    private String verAndroid;
    private String verStation;
    private int verificationDate;
    private int verificationNumberNew;
    private int verificationNumberToday;
    private float waterTemperature;
    private boolean workInShortMode;

    public CounterVerification() {
        initValues();
    }

    private void initValues() {
        this.id = "0";
        this.personalAccount = "";
        this.measResults = new ArrayList<>();
        this.counterNumber = "";
        this.serviceType = 1;
        this.char1 = "";
        this.char2 = "";
        this.verStation = "-";
        this.verAndroid = "-";
        this.workInShortMode = false;
    }

    public MeasurementResults newMeasurement() {
        int meas = getLastMeasNumber();
        return createMeasurement(meas + 1, 0);
    }

    public MeasurementResults repeatMeasurement(int meas) {
        int reit = getLastReitNumber(meas);
        return createMeasurement(meas, reit + 1);
    }

    public int getLastMeasNumber() {
        int last = 0;
        for (MeasurementResults mRes : this.measResults) {
            if (mRes.getMeasurementNumber() > last) {
                last = mRes.getMeasurementNumber();
            }
        }
        return last;
    }

    public int getLastReitNumber(int meas) {
        int last = 0;
        for (MeasurementResults mRes : this.measResults) {
            if (mRes.getMeasurementNumber() == meas && mRes.getReiterationNumber() > last) {
                last = mRes.getReiterationNumber();
            }
        }
        return last;
    }

    public int getTotalReitNumber() {
        int count = 0;
        for (MeasurementResults mRes : this.measResults) {
            if (mRes.getReiterationNumber() > 0) {
                count++;
            }
        }
        return count;
    }

    public MeasurementResults createMeasurement(int meas, int reit) {
        MeasurementResults mRes = new MeasurementResults(meas, reit, this.measSettings[meas - 1], this.impulsesPerLiter);
        this.measResults.add(mRes);
        return mRes;
    }

    public MeasurementResults getMeasurement(int measNumb, int reitNumb) {
        for (MeasurementResults res : this.measResults) {
            if (res.getMeasurementNumber() == measNumb && res.getReiterationNumber() == reitNumb) {
                return res;
            }
        }
        return null;
    }

    public MeasurementResults getRequiredMeasurement(int measNumber) {
        return getMeasurement(measNumber, getLastReitNumber(measNumber));
    }

    public boolean loadMeasurementSettings(ContextWrapper context, int testName, int multiplier) {
        this.testName = testName;
        this.multiplier = multiplier;
        MeasurementSettings[] settingsForTest = getSettingsForTest(context, testName, multiplier);
        this.measSettings = settingsForTest;
        return settingsForTest != null;
    }

    private MeasurementSettings[] getSettingsForTest(ContextWrapper context, int testName, int multiplier) {
        String testsString = MyFileReader.readAndroidFile(context.getFilesDir(), "tests.json");
        MeasurementSettings[] measSettings = new MeasurementSettings[3];
        if (testsString != null) {
            try {
                JSONArray testsArray = new JSONArray(testsString);
                for (int i = 0; i < testsArray.length(); i++) {
                    JSONObject setting = testsArray.getJSONObject(i);
                    if (setting.getInt(MyJSON.test_name.toString()) == testName) {
                        MeasurementSettings measurementSettings = new MeasurementSettings(setting, this.impulsesPerLiter, multiplier);
                        measSettings[measurementSettings.getNumber() - 1] = measurementSettings;
                    }
                }
                if (measSettings[0] == null) {
                    return null;
                }
                return measSettings;
            } catch (JSONException e) {
                return null;
            }
        }
        return null;
    }

    public void setFlashData(int impulsesPerLiter, int stationNumber) {
        this.impulsesPerLiter = impulsesPerLiter;
        this.stationNumber = stationNumber;
    }

    public void setStateData(int batteryCharge, int waterTemperature) {
        this.batteryCharge = batteryCharge / 100.0f;
        this.waterTemperature = waterTemperature / 1000.0f;
    }

    public String stateDataToString(Resources resources) {
        StringBuilder sbAppend = new StringBuilder().append(String.format(Locale.ROOT, resources.getString(R.string.station_number), Integer.valueOf(this.stationNumber)));
        Locale locale = Locale.ROOT;
        String string = resources.getString(R.string.mode_local_online);
        Object[] objArr = new Object[1];
        objArr[0] = resources.getString(this.isOnline == 0 ? R.string.local : R.string.online);
        StringBuilder sbAppend2 = sbAppend.append(String.format(locale, string, objArr)).append(String.format(Locale.ROOT, resources.getString(R.string.battery_charge), Integer.valueOf(Math.round(this.batteryCharge)))).append(String.format(Locale.ROOT, resources.getString(R.string.temperature_1), Float.valueOf(this.waterTemperature))).append(String.format(Locale.ROOT, resources.getString(R.string.protocols_today), Integer.valueOf(this.verificationNumberToday)));
        Locale locale2 = Locale.ROOT;
        Object[] objArr2 = new Object[2];
        objArr2[0] = resources.getString(getDaysToCalibration() >= 0 ? R.string.till_calibration : R.string.past_calibration);
        objArr2[1] = Integer.valueOf(getDaysToCalibration());
        return sbAppend2.append(String.format(locale2, "%s: %d \n", objArr2)).append(String.format(Locale.ROOT, resources.getString(R.string.calibration_date), getVerificationDate())).append(String.format(Locale.ROOT, resources.getString(R.string.production_date), getProductionDate())).toString();
    }

    public int getDaysToCalibration() {
        try {
            Calendar calibrationDate = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ROOT);
            calibrationDate.setTime(sdf.parse(String.format(Locale.ROOT, "%s %s", getVerificationDate(), "12:00:00")));
            calibrationDate.add(6, this.dueDate);
            return (int) (((calibrationDate.getTimeInMillis() / 1000) - ((long) this.currentStationTime)) / 86400);
        } catch (ParseException e) {
            return 0;
        }
    }

    public String printMeasurementSettings(Resources resources) {
        StringBuilder builder = new StringBuilder();
        int index = 1;
        MeasurementSettings[] measurementSettingsArr = this.measSettings;
        int length = measurementSettingsArr.length;
        int i = 0;
        while (i < length) {
            MeasurementSettings settings = measurementSettingsArr[i];
            Log.d(TAG, settings.toString());
            builder.append(index + ". Q = " + (settings.getConsumptionLit() / 1000.0f) + " " + resources.getString(R.string.cubic_meters) + ", V = " + settings.getVolumeLit() + " л;\n");
            i++;
            index++;
        }
        return builder.toString();
    }

    public String testNameToText(Resources res) {
        int Q = Integer.toString(this.testName).toCharArray()[1] - '0';
        int RClass = Integer.toString(this.testName).toCharArray()[2] - '0';
        if (Q <= 4) {
            String[] QArray = res.getStringArray(R.array.Q3_array);
            String[] RClassArray = res.getStringArray(R.array.R_array);
            return String.format(Locale.ROOT, "Q3 %s %s", QArray[Q - 1], RClassArray[RClass - 1]);
        }
        String[] QArray2 = res.getStringArray(R.array.Qn_array);
        String[] RClassArray2 = res.getStringArray(R.array.Class_array);
        return String.format(Locale.ROOT, "Qn %s %s", QArray2[Q - 5], RClassArray2[RClass - 7]);
    }

    public int getRequiredConsumption(int measNumb) {
        return this.measSettings[measNumb].getConsumptionLit();
    }

    public int getAction() {
        return this.action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public Bitmap getTestPhoto() {
        byte[] bArr = this.testPhoto;
        return BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
    }

    public void setTestPhoto(byte[] testPhoto) {
        this.testPhoto = testPhoto;
    }

    public boolean hasStartPhoto(int measNumber) {
        int lastReiterationNumber = getLastReitNumber(measNumber);
        MeasurementResults measurement = getMeasurement(measNumber, lastReiterationNumber);
        return measurement.hasPhotoStart();
    }

    public boolean hasEndPhoto(int measNumber) {
        int lastReiterationNumber = getLastReitNumber(measNumber);
        MeasurementResults measurement = getMeasurement(measNumber, lastReiterationNumber);
        return measurement.hasPhotoEnd();
    }

    public Bitmap getBitmapByNumber(int number) {
        int selectedMeasurementNumber = (number + 1) / 2;
        int selectedReiterationNumber = getLastReitNumber(selectedMeasurementNumber);
        MeasurementResults measurement = getMeasurement(selectedMeasurementNumber, selectedReiterationNumber);
        return measurement.getBitmap(((number - 1) % 2) + 1);
    }

    private MeasurementResults getMeasurementByNumber(int number) {
        int selectedMeasurementNumber = (number + 1) / 2;
        int selectedReiterationNumber = getLastReitNumber(selectedMeasurementNumber);
        return getMeasurement(selectedMeasurementNumber, selectedReiterationNumber);
    }

    public String getFileNameByNumber(int number) {
        MeasurementResults measurement = getMeasurementByNumber(number);
        return String.format(Locale.ROOT, "1/current/meast_%d%d.json", Integer.valueOf(measurement.getMeasurementNumber()), Integer.valueOf(measurement.getReiterationNumber()));
    }

    public int getValueByNumber(int number) {
        MeasurementResults measurement = getMeasurementByNumber(number);
        if ((number - 1) % 2 == 0) {
            return measurement.getValStart();
        }
        return measurement.getValEnd();
    }

    public boolean setValueByNumber(int number, int value) {
        MeasurementResults measurement = getMeasurementByNumber(number);
        if ((number - 1) % 2 == 0) {
            measurement.setValStart(value);
        } else {
            measurement.setValEnd(value);
        }
        return measurement.calculate();
    }

    public float getAvrgConsumption(int number) {
        return getMeasurement(number, getLastReitNumber(number)).getConsumptionAvrgLit();
    }

    public float getTemperatureByNumber(int number) {
        return getMeasurement(number, getLastReitNumber(number)).getWaterTemperature();
    }

    public int getRequiredVolume(int number) {
        return getMeasurement(number, getLastReitNumber(number)).getRequiredVolumeLit();
    }

    public int getError(int number) {
        return getMeasurement(number, getLastReitNumber(number)).getError();
    }

    public String getResultString(Resources resources, int number) {
        String[] results = resources.getStringArray(R.array.result_status);
        return results[getMeasurement(number, getLastReitNumber(number)).getResult()];
    }

    public int getResultInt(int number) {
        return getMeasurement(number, getLastReitNumber(number)).getResult();
    }

    public boolean isResultDefined() {
        if (getLastMeasNumber() == 3) {
            boolean resultDefined = true;
            for (int meas = 1; meas <= 3; meas++) {
                if (getMeasurement(meas, getLastReitNumber(meas)).getResult() == 0 || !getMeasurement(meas, getLastReitNumber(meas)).hasPhotoEnd()) {
                    resultDefined = false;
                }
            }
            return resultDefined;
        }
        return false;
    }

    public int getAvrgConRange(int number) {
        return getMeasurement(number, getLastReitNumber(number)).avrgConRange();
    }

    public String getCounterNumber() {
        return this.counterNumber;
    }

    public int getInitialVolume() {
        return this.initialVolume;
    }

    public int getProductionYear() {
        return this.productionYear;
    }

    public int getDNtypeNumber() {
        return this.DNtypeNumber;
    }

    public void setCounterInfo(String counterNumber, int initialVolume, int productionYear, int DNtypeNumber) {
        this.counterNumber = counterNumber;
        this.initialVolume = initialVolume;
        this.productionYear = productionYear;
        this.DNtypeNumber = DNtypeNumber;
    }

    public boolean isCounterInfoOK() {
        return (this.counterNumber.length() == 0 || this.productionYear == 0 || this.DNtypeNumber == 0) ? false : true;
    }

    public boolean isGlobalTemperatureOK() {
        boolean waterHot = false;
        if (getWaterTemperatureRound() > 30.0f) {
            waterHot = true;
        }
        int meas = 1;
        while (true) {
            if (meas > getLastMeasNumber()) {
                return true;
            }
            if (getRequiredMeasurement(meas).getWaterTemperature() > 0.0f) {
                if ((getRequiredMeasurement(meas).getWaterTemperature() > 30.0f) != waterHot) {
                    return false;
                }
            }
            meas++;
        }
    }

    public boolean isDeltaTemperatureOK() {
        float[] array = new float[3];
        for (int meas = 1; meas <= getLastMeasNumber(); meas++) {
            array[meas - 1] = getRequiredMeasurement(meas).getWaterTemperature();
        }
        if (array[0] <= 0.0f || array[1] <= 0.0f || Math.abs(array[0] - array[1]) <= 5.0f) {
            return array[1] <= 0.0f || array[2] <= 0.0f || Math.abs(array[1] - array[2]) <= 5.0f;
        }
        return false;
    }

    public void calculateAverageTemperature() {
        float buff = 0.0f;
        int count = 0;
        for (int meas = 1; meas <= getLastMeasNumber(); meas++) {
            if (getRequiredMeasurement(meas).getWaterTemperature() > 0.0f) {
                buff += getRequiredMeasurement(meas).getWaterTemperature();
                count++;
            }
        }
        if (count > 0) {
            setWaterTemperature(Math.round(buff / count));
        }
    }

    public void updateResults(ContextWrapper context) {
        int testName;
        boolean updateToCold;
        int testName2 = this.testName;
        if (testName2 > 200) {
            testName = testName2 - 100;
            updateToCold = true;
        } else {
            testName = testName2 + 100;
            updateToCold = false;
        }
        loadMeasurementSettings(context, testName, this.multiplier);
        int newRequiredError = updateToCold ? 4 : 6;
        for (MeasurementResults res : this.measResults) {
            if (res.getMeasurementNumber() != 3) {
                res.setRequiredError(newRequiredError);
                res.calculate();
            }
        }
    }

    public int getTestName() {
        return this.testName;
    }

    public boolean isTestTemperatureOk() {
        if (String.valueOf(this.testName).charAt(0) != '1' || Math.round(this.waterTemperature) > 30) {
            return String.valueOf(this.testName).charAt(0) == '2' && Math.round(this.waterTemperature) > 30;
        }
        return true;
    }

    public int getMultiplier() {
        return this.multiplier;
    }

    public int getImpLiter() {
        return this.impulsesPerLiter;
    }

    public void setWaterTemperature(float waterTemperature) {
        this.waterTemperature = waterTemperature;
    }

    public float getWaterTemperature() {
        return this.waterTemperature;
    }

    public int getWaterTemperatureRound() {
        return Math.round(this.waterTemperature);
    }

    boolean isBatteryOK() {
        return this.batteryCharge >= 10.0f;
    }

    public int getVerificationNumberToday() {
        return this.verificationNumberToday;
    }

    public void setVerificationNumberToday(int verificationNumberToday) {
        this.verificationNumberToday = verificationNumberToday;
        setVerificationNumberNew();
    }

    public int getVerificationNumberNew() {
        return this.verificationNumberNew;
    }

    private void setVerificationNumberNew() {
        Date curDate = new Date(((long) this.currentStationTime) * 1000);
        this.verificationNumberNew = Integer.parseInt(String.format(Locale.ROOT, "%s%03d", new SimpleDateFormat("yyMMdd", Locale.ROOT).format(curDate), Integer.valueOf(this.verificationNumberToday + 1)));
    }

    public int getPaymentAccount() {
        return this.paymentAccount;
    }

    public void setPaymentAccount(int paymentAccount) {
        this.paymentAccount = paymentAccount;
    }

    public int getStationNumber() {
        return this.stationNumber;
    }

    public String getProtocolNumber() {
        return String.format(Locale.ROOT, "%03d-%09d", Integer.valueOf(getStationNumber()), Integer.valueOf(getVerificationNumberNew()));
    }

    public int getVerificationDateUnix() {
        return this.verificationDate;
    }

    public String getVerificationDate() {
        Calendar calendar = Calendar.getInstance();
        Date prodDate = new Date(((long) this.verificationDate) * 1000);
        calendar.setTime(prodDate);
        return String.format(Locale.ROOT, "%02d.%02d.%02d", Integer.valueOf(calendar.get(5)), Integer.valueOf(calendar.get(2) + 1), Integer.valueOf(calendar.get(1)));
    }

    public void setVerificationDate(int verificationDate) {
        this.verificationDate = verificationDate;
    }

    public int getProductionDateUnix() {
        return this.productionDate;
    }

    public String getProductionDate() {
        Calendar calendar = Calendar.getInstance();
        Date prodDate = new Date(((long) this.productionDate) * 1000);
        calendar.setTime(prodDate);
        return String.format(Locale.ROOT, "%02d.%02d.%02d", Integer.valueOf(calendar.get(5)), Integer.valueOf(calendar.get(2) + 1), Integer.valueOf(calendar.get(1)));
    }

    public void setProductionDate(int productionDate) {
        this.productionDate = productionDate;
    }

    public void setDueDate(int dueDate) {
        this.dueDate = dueDate;
    }

    public String getCurrentStationTimeString() {
        Date curDate = new Date(((long) this.currentStationTime) * 1000);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return format.format(curDate);
    }

    public int getCurrentStationTime() {
        return this.currentStationTime;
    }

    public void setCurrentStationTime(int currentStationTime) {
        this.currentStationTime = currentStationTime;
    }

    public int isOnline() {
        return this.isOnline;
    }

    public void setOnline(int online) {
        this.isOnline = online;
    }

    public int getServiceType() {
        return this.serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public String getVerStation() {
        return this.verStation;
    }

    public void setVerStation(String verStation) {
        this.verStation = verStation;
    }

    public String getVerAndroid() {
        return this.verAndroid;
    }

    public void setVerAndroid(String verAndroid) {
        this.verAndroid = verAndroid;
    }

    public boolean getWorkInShortMode() {
        return this.workInShortMode;
    }

    public void setWorkInShortMode(boolean value) {
        this.workInShortMode = value;
    }

    public boolean isDeviceTypeInfoEmpty() {
        return this.char1.isEmpty() || this.char2.isEmpty();
    }

    public String getChar1() {
        return this.char1;
    }

    public String getChar2() {
        return this.char2;
    }

    public void setDeviceTypeInfo(String char1, String char2) {
        this.char1 = char1;
        this.char2 = char2;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonalAccount() {
        return this.personalAccount;
    }

    public void setPersonalAccount(String personalAccount) {
        this.personalAccount = personalAccount;
    }

    public int getLocal() {
        return this.local;
    }

    public void setLocal(int local) {
        this.local = local;
    }

    public void loadAll(int action, int todayVerifications, int batteryCharge, int dateTime) {
        this.action = action;
        this.verificationNumberToday = todayVerifications;
        this.batteryCharge = batteryCharge / 100.0f;
        this.currentStationTime = dateTime;
        setVerificationNumberNew();
    }

    public void removeExtraPhotos() {
        for (int meas = 1; meas <= getLastMeasNumber(); meas++) {
            int lastReit = getLastReitNumber(meas);
            if (lastReit > 0) {
                for (MeasurementResults res : this.measResults) {
                    if (res.getMeasurementNumber() == meas && res.getReiterationNumber() != lastReit) {
                        res.setPhoto(null, 1);
                        res.setPhoto(null, 2);
                    }
                }
            }
        }
    }

    public int getEnvironmentT() {
        return this.environmentT;
    }

    public int getEnvironmentH() {
        return this.environmentH;
    }

    public void setEnvironment(int environmentT, int environmentH) {
        this.environmentT = environmentT;
        this.environmentH = environmentH;
    }

    public int measureEnvironment(int fromVal, int toVal, int oldVal, int gap) {
        Random random = new Random();
        int n = toVal - fromVal;
        int newVal = 0;
        if (oldVal < fromVal || oldVal >= toVal) {
            return random.nextInt(n) + fromVal;
        }
        while (Math.abs(oldVal - newVal) > gap) {
            newVal = random.nextInt(n) + fromVal;
        }
        return newVal;
    }

    public float getInnerTemperature() {
        return this.innerTemperature;
    }

    public float getInnerHumidity() {
        return this.innerHumidity;
    }

    public void setInnerInfo(float innerT, float innerH) {
        this.innerTemperature = innerT;
        this.innerHumidity = innerH;
    }

    public String toString() {
        return "CounterVerification{measSettings=" + Arrays.toString(this.measSettings) + ", measResults=" + this.measResults + ", id='" + this.id + "', local=" + this.local + ", verificationNumberToday=" + this.verificationNumberToday + ", verificationNumberNew=" + this.verificationNumberNew + ", action=" + this.action + ", impulsesPerLiter=" + this.impulsesPerLiter + ", stationNumber=" + this.stationNumber + ", batteryCharge=" + this.batteryCharge + ", testName=" + this.testName + ", multiplier=" + this.multiplier + ", verificationDate=" + this.verificationDate + ", currentStationTime=" + this.currentStationTime + ", isOnline=" + this.isOnline + ", serviceType=" + this.serviceType + ", verStation='" + this.verStation + "', verAndroid='" + this.verAndroid + "', char1='" + this.char1 + "', char2='" + this.char2 + "', counterNumber='" + this.counterNumber + "', initialVolume=" + this.initialVolume + ", productionYear=" + this.productionYear + ", DNtypeNumber=" + this.DNtypeNumber + ", waterTemperature=" + this.waterTemperature + ", testPhoto=" + (this.testPhoto != null) + '}';
    }

    private CounterVerification(Parcel in) {
        this.measSettings = new MeasurementSettings[3];
        for (int i = 0; i < 3; i++) {
            this.measSettings[i] = (MeasurementSettings) in.readValue(MeasurementSettings.class.getClassLoader());
        }
        ArrayList<MeasurementResults> arrayList = new ArrayList<>();
        this.measResults = arrayList;
        in.readList(arrayList, MeasurementResults.class.getClassLoader());
        this.verificationNumberToday = in.readInt();
        this.verificationNumberNew = in.readInt();
        this.paymentAccount = in.readInt();
        this.action = in.readInt();
        this.impulsesPerLiter = in.readInt();
        this.stationNumber = in.readInt();
        this.batteryCharge = in.readFloat();
        this.testName = in.readInt();
        this.multiplier = in.readInt();
        this.verificationDate = in.readInt();
        this.productionDate = in.readInt();
        this.dueDate = in.readInt();
        this.currentStationTime = in.readInt();
        this.isOnline = in.readInt();
        this.serviceType = in.readInt();
        this.char1 = in.readString();
        this.char2 = in.readString();
        byte[] bArr = new byte[in.readInt()];
        this.testPhoto = bArr;
        in.readByteArray(bArr);
        this.counterNumber = in.readString();
        this.initialVolume = in.readInt();
        this.productionYear = in.readInt();
        this.DNtypeNumber = in.readInt();
        this.waterTemperature = in.readFloat();
        this.verStation = in.readString();
        this.verAndroid = in.readString();
        this.workInShortMode = in.readInt() == 1;
        this.id = in.readString();
        this.personalAccount = in.readString();
        this.local = in.readInt();
        this.environmentT = in.readInt();
        this.environmentH = in.readInt();
        this.environmentP = in.readInt();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.innerTemperature = in.readFloat();
        this.innerHumidity = in.readFloat();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        for (MeasurementSettings measurementSettings : this.measSettings) {
            parcel.writeValue(measurementSettings);
        }
        parcel.writeList(this.measResults);
        parcel.writeInt(this.verificationNumberToday);
        parcel.writeInt(this.verificationNumberNew);
        parcel.writeInt(this.paymentAccount);
        parcel.writeInt(this.action);
        parcel.writeInt(this.impulsesPerLiter);
        parcel.writeInt(this.stationNumber);
        parcel.writeFloat(this.batteryCharge);
        parcel.writeInt(this.testName);
        parcel.writeInt(this.multiplier);
        parcel.writeInt(this.verificationDate);
        parcel.writeInt(this.productionDate);
        parcel.writeInt(this.dueDate);
        parcel.writeInt(this.currentStationTime);
        parcel.writeInt(this.isOnline);
        parcel.writeInt(this.serviceType);
        parcel.writeString(this.char1);
        parcel.writeString(this.char2);
        parcel.writeInt(this.testPhoto.length);
        parcel.writeByteArray(this.testPhoto);
        parcel.writeString(this.counterNumber);
        parcel.writeInt(this.initialVolume);
        parcel.writeInt(this.productionYear);
        parcel.writeInt(this.DNtypeNumber);
        parcel.writeFloat(this.waterTemperature);
        parcel.writeString(this.verStation);
        parcel.writeString(this.verAndroid);
        parcel.writeInt(this.workInShortMode ? 1 : 0);
        parcel.writeString(this.id);
        parcel.writeString(this.personalAccount);
        parcel.writeInt(this.local);
        parcel.writeInt(this.environmentT);
        parcel.writeInt(this.environmentH);
        parcel.writeInt(this.environmentP);
        parcel.writeDouble(this.latitude);
        parcel.writeDouble(this.longitude);
        parcel.writeFloat(this.innerTemperature);
        parcel.writeFloat(this.innerHumidity);
    }
}
