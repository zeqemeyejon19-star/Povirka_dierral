package com.poverka.httpFileClient.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidmads.library.qrgenearator.BuildConfig;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.core.internal.view.SupportMenu;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.poverka.httpFileClient.R;
import com.poverka.httpFileClient.activity.ImageActivity;
import com.poverka.httpFileClient.measurement.CounterVerification;
import com.poverka.httpFileClient.measurement.MeasurementResults;
import com.poverka.httpFileClient.util.HttpFileClient;
import com.poverka.httpFileClient.util.MyDeviceTypeHelper;
import com.poverka.httpFileClient.util.MyFileReader;
import com.poverka.httpFileClient.util.MyJSON;
import com.poverka.httpFileClient.util.MyModeQueue;
import com.poverka.httpFileClient.util.MySender;
import com.poverka.httpFileClient.util.RestorationHelper;
import com.poverka.httpFileClient.util.TcpClient;
import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class StateActivity extends AppCompatActivity {
    private static final float BUTTON_HEIGHT_RATIO = 0.15f;
    private static final float MARGIN_HEIGHT_RATIO = 0.03f;
    private static final String TAG = "StateActivity";
    private static final float TEXT_L_HEIGHT_RATIO = 0.062f;
    private static final float TEXT_S_HEIGHT_RATIO = 0.05f;
    private static ProgressDialog mProgressDialog;
    private static RestorationHelper restoration;
    private Timer freezeCheckerTimer;
    private int freezeSeconds;
    private boolean local;
    private HttpFileClient mHttpFileClient;
    private MyModeQueue mQueue;
    private MySender mSender;
    private TcpClient mTcpClient;
    private UIhandler mUIhandler;
    private CounterVerification mVerification;
    private int photoType;
    private int selectedId;
    private int selectedMechanismIndex;
    private int selectedQNameIndex;
    private int selectedQValueIndex;
    private int selectedRClassValueIndex;
    private Thread senderThread;
    private int threadDelay;
    private int writeLog;
    private final MutableLiveData<MyModeQueue.RequestMode> requestMode = new MutableLiveData<>();
    public TcpClient.OnMessageReceived mTcpReceived = new TcpClient.OnMessageReceived() { // from class: com.poverka.httpFileClient.activity.StateActivity.1
        @Override // com.poverka.httpFileClient.util.TcpClient.OnMessageReceived
        public void messageReceived(String message) throws Throwable {
            StateActivity.Log(message);
            if (StateActivity.this.requestMode.getValue() != MyModeQueue.RequestMode.NONE) {
                StateActivity.this.freezeSeconds = 0;
                try {
                    switch (AnonymousClass10.$SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[((MyModeQueue.RequestMode) Objects.requireNonNull(StateActivity.this.requestMode.getValue())).ordinal()]) {
                        case 1:
                            StateActivity.this.readDateTimeMode(new JSONObject(message));
                            break;
                        case 2:
                            StateActivity.this.testPhotoMode(new JSONObject(message));
                            break;
                        case 3:
                            StateActivity.this.readActionMode(new JSONObject(message));
                            break;
                        case 4:
                            StateActivity.this.stateMode(new JSONObject(message));
                            break;
                        case 5:
                            StateActivity.this.testNameMode(new JSONObject(message));
                            break;
                        case 6:
                            StateActivity.this.prepareMeasurementMode(new JSONObject(message));
                            break;
                        case 7:
                            StateActivity.this.finishMode(new JSONObject(message));
                            break;
                        case 8:
                            StateActivity.this.firmwareUpdateMode(new JSONObject(message));
                            break;
                        case 9:
                            StateActivity.this.restoreSocketMode(new JSONObject(message));
                            break;
                        case 10:
                            StateActivity.this.restoreActionMode(new JSONObject(message));
                            break;
                    }
                    return;
                } catch (JSONException e) {
                    MyFileReader.appendLog(StateActivity.this.getApplicationContext(), "State TCP", e.toString(), e.getStackTrace());
                    Log.e(StateActivity.TAG, "JSON error in TCP receiver", e);
                    StateActivity.this.stopActivity();
                    return;
                }
            }
            Log.e(StateActivity.TAG, "Пришла строка, а программа не в режиме.. ");
            Log.e(StateActivity.TAG, "Message => " + message);
        }
    };
    public HttpFileClient.OnMessageReceived mHttpReceived = new HttpFileClient.OnMessageReceived() { // from class: com.poverka.httpFileClient.activity.StateActivity.2
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:35:0x00eb A[Catch: JSONException -> 0x038f, IOException | JSONException -> 0x0391, TRY_ENTER, TRY_LEAVE, TryCatch #18 {IOException | JSONException -> 0x0391, blocks: (B:12:0x006e, B:35:0x00eb), top: B:259:0x006e }] */
        /* JADX WARN: Removed duplicated region for block: B:62:0x01d4  */
        /* JADX WARN: Type inference failed for: r4v10 */
        /* JADX WARN: Type inference failed for: r4v101 */
        /* JADX WARN: Type inference failed for: r4v102 */
        /* JADX WARN: Type inference failed for: r4v103 */
        /* JADX WARN: Type inference failed for: r4v104 */
        /* JADX WARN: Type inference failed for: r4v105 */
        /* JADX WARN: Type inference failed for: r4v106 */
        /* JADX WARN: Type inference failed for: r4v107 */
        /* JADX WARN: Type inference failed for: r4v15, types: [boolean] */
        /* JADX WARN: Type inference failed for: r4v16 */
        /* JADX WARN: Type inference failed for: r4v18 */
        /* JADX WARN: Type inference failed for: r4v20 */
        /* JADX WARN: Type inference failed for: r4v23 */
        /* JADX WARN: Type inference failed for: r4v38 */
        /* JADX WARN: Type inference failed for: r4v40 */
        /* JADX WARN: Type inference failed for: r4v42 */
        /* JADX WARN: Type inference failed for: r4v9 */
        /* JADX WARN: Type inference failed for: r5v100 */
        /* JADX WARN: Type inference failed for: r5v101 */
        /* JADX WARN: Type inference failed for: r5v27, types: [android.content.pm.PackageInfo] */
        /* JADX WARN: Type inference failed for: r5v28 */
        /* JADX WARN: Type inference failed for: r5v33 */
        /* JADX WARN: Type inference failed for: r9v18 */
        /* JADX WARN: Type inference failed for: r9v19 */
        /* JADX WARN: Type inference failed for: r9v20 */
        /* JADX WARN: Type inference failed for: r9v28 */
        /* JADX WARN: Type inference failed for: r9v8 */
        @Override // com.poverka.httpFileClient.util.HttpFileClient.OnMessageReceived
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void messageReceived(com.poverka.httpFileClient.util.HttpFileClient.Type r32, java.lang.String r33, java.io.InputStream r34) throws java.lang.Throwable {
            /*
                Method dump skipped, instruction units count: 1934
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.poverka.httpFileClient.activity.StateActivity.AnonymousClass2.messageReceived(com.poverka.httpFileClient.util.HttpFileClient$Type, java.lang.String, java.io.InputStream):void");
        }
    };
    private final Runnable senderRunnable = new Runnable() { // from class: com.poverka.httpFileClient.activity.StateActivity.3
        @Override // java.lang.Runnable
        public void run() {
            try {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(StateActivity.this.threadDelay);
                    if (StateActivity.this.mTcpClient.isServerRunning()) {
                        if (StateActivity.this.requestMode.getValue() != MyModeQueue.RequestMode.NONE && StateActivity.this.requestMode.getValue() != MyModeQueue.RequestMode.RESTORE_FILES) {
                            StateActivity.this.mSender.send();
                        }
                    } else {
                        MyFileReader.appendLog(StateActivity.this.getApplicationContext(), "State", "LOST connection");
                        Thread.currentThread().interrupt();
                        Log.e(StateActivity.TAG, "Подключение потеряно");
                        Message msg = StateActivity.this.mUIhandler.obtainMessage(4);
                        Bundle bundle = new Bundle();
                        bundle.putString("error", StateActivity.this.getString(R.string.lost_connection));
                        msg.setData(bundle);
                        StateActivity.this.mUIhandler.sendMessage(msg);
                        StateActivity.this.mUIhandler.sendEmptyMessage(5);
                    }
                }
            } catch (InterruptedException e) {
                Log.e("Interrupted exception", "senderRunnable is interrupted");
            }
        }
    };
    private ImageActivity.OnImageResult imageResult = new ImageActivity.OnImageResult() { // from class: com.poverka.httpFileClient.activity.StateActivity.8
        @Override // com.poverka.httpFileClient.activity.ImageActivity.OnImageResult
        public void imageResult(Bundle bundle) {
            String counterNumber = bundle.getString("number");
            int volume = bundle.getInt("volume");
            int year = bundle.getInt("year");
            int dnType = bundle.getInt("dnType");
            StateActivity.this.mVerification.setCounterInfo(counterNumber, volume, year, dnType);
            try {
                JSONObject json = new JSONObject();
                json.put(MyJSON.counter_number.toString(), counterNumber);
                json.put(MyJSON.start_volume.toString(), volume);
                json.put(MyJSON.production_year.toString(), year);
                json.put(MyJSON.type_id.toString(), dnType);
                json.put(MyJSON.water_temperature.toString(), StateActivity.this.mVerification.getWaterTemperature() * 1000.0f);
                StateActivity.this.mHttpFileClient.upload("1/current/counter_info.json", json.toString());
            } catch (JSONException e) {
                MyFileReader.appendLog(StateActivity.this.getApplicationContext(), "State imageResult", e.toString(), e.getStackTrace());
                e.printStackTrace();
            }
        }
    };

    static /* synthetic */ int access$308(StateActivity x0) {
        int i = x0.freezeSeconds;
        x0.freezeSeconds = i + 1;
        return i;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws Throwable {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);
        Log("*** Created ***");
        initViews();
        restoration = null;
        this.mUIhandler = new UIhandler();
        this.threadDelay = MainActivity.REQUEST_DELAY;
        Intent intent = getIntent();
        this.local = intent.getBooleanExtra("local", true);
        this.selectedId = intent.getIntExtra("selectedId", -1);
        this.photoType = intent.getIntExtra("photoType", 0);
        this.writeLog = intent.getIntExtra("writeLog", 0);
        connect();
        initRequestObserver();
        mProgressDialog = new ProgressDialog(this);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
        Log("*** Started ***");
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        super.onStop();
        Log("*** Stopped ***");
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() throws Throwable {
        super.onDestroy();
        Log("*** Destroyed ***");
        stopActivity();
    }

    /* JADX INFO: renamed from: com.poverka.httpFileClient.activity.StateActivity$10, reason: invalid class name */
    static /* synthetic */ class AnonymousClass10 {
        static final /* synthetic */ int[] $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode;

        static {
            int[] iArr = new int[MyModeQueue.RequestMode.values().length];
            $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode = iArr;
            try {
                iArr[MyModeQueue.RequestMode.READ_DATE_TIME.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[MyModeQueue.RequestMode.TEST_PHOTO.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[MyModeQueue.RequestMode.READ_ACTION.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[MyModeQueue.RequestMode.STATE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[MyModeQueue.RequestMode.TEST_NAME.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[MyModeQueue.RequestMode.PREPARE_MEASUREMENT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[MyModeQueue.RequestMode.FINISH.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[MyModeQueue.RequestMode.UPDATE_FIRMWARE.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[MyModeQueue.RequestMode.RESTORE_SOCKET.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[MyModeQueue.RequestMode.RESTORE_ACTION.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[MyModeQueue.RequestMode.FLASH.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[MyModeQueue.RequestMode.CHECK_CURRENT_STATE.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[MyModeQueue.RequestMode.UPDATE_TESTS.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[MyModeQueue.RequestMode.RESTORE_FILES.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
        }
    }

    private void initRequestObserver() {
        this.requestMode.observe(this, new Observer<MyModeQueue.RequestMode>() { // from class: com.poverka.httpFileClient.activity.StateActivity.4
            @Override // androidx.lifecycle.Observer
            public void onChanged(MyModeQueue.RequestMode rMode) throws Throwable {
                StateActivity.Log(String.format("RequestMode changed to %s", rMode.toString()));
                switch (AnonymousClass10.$SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[rMode.ordinal()]) {
                    case 1:
                        StateActivity.this.mSender.readDateTime(StateActivity.this.photoType, StateActivity.this.writeLog);
                        break;
                    case 2:
                        StateActivity.this.mSender.startTestPhoto();
                        Message msg = StateActivity.this.mUIhandler.obtainMessage(4);
                        Bundle bundle = new Bundle();
                        bundle.putString("alert", StateActivity.this.getString(R.string.reading_test_photo));
                        msg.setData(bundle);
                        StateActivity.this.mUIhandler.sendMessage(msg);
                        break;
                    case 3:
                        StateActivity.this.mSender.readAction();
                        break;
                    case 4:
                        StateActivity.this.mSender.startReadingState();
                        break;
                    case 5:
                        StateActivity.this.mSender.writeTestName(StateActivity.this.mVerification.getTestName(), StateActivity.this.mVerification.getMultiplier());
                        break;
                    case 6:
                        StateActivity.this.threadDelay = MainActivity.REQUEST_DELAY;
                        MeasurementResults mRes = StateActivity.this.mVerification.newMeasurement();
                        StateActivity.this.mSender.prepareMeasurement(mRes.getMeasurementNumber(), mRes.getReiterationNumber());
                        break;
                    case 7:
                        StateActivity.this.mSender.sendFinish();
                        break;
                    case 8:
                        StateActivity.this.mSender.updateFirmware();
                        break;
                    case 9:
                        StateActivity.this.mSender.getAll();
                        break;
                    case 10:
                        if (StateActivity.this.mVerification.getAction() != 1) {
                            if (StateActivity.this.mVerification.getAction() == 3 || StateActivity.this.mVerification.getAction() == 4) {
                                StateActivity.this.mSender.changeAction(2);
                            } else {
                                StateActivity.this.mSender.readAction();
                            }
                        } else {
                            StateActivity.this.mSender.restoreTestSettings(StateActivity.this.mVerification.getTestName(), StateActivity.this.mVerification.getMultiplier(), StateActivity.this.mVerification.getLastMeasNumber(), StateActivity.this.mVerification.getLastReitNumber(StateActivity.this.mVerification.getLastMeasNumber()));
                        }
                        break;
                    case 11:
                        StateActivity.this.mHttpFileClient.download("1/flash/flash_a.json");
                        break;
                    case 12:
                        StateActivity.this.mHttpFileClient.show("1/current");
                        break;
                    case 13:
                        try {
                            File tests = new File(StateActivity.this.getApplicationContext().getFilesDir(), "tests.json");
                            JSONArray jsonTests = new JSONArray(MyFileReader.readAndroidFile(StateActivity.this.getFilesDir(), tests.getName()));
                            JSONObject jsonTestsStation = new JSONObject();
                            jsonTestsStation.put("tests", jsonTests);
                            StateActivity.this.mHttpFileClient.upload("1/flash/tests.json", jsonTestsStation.toString());
                        } catch (JSONException e) {
                            MyFileReader.appendLog(StateActivity.this.getApplicationContext(), "State UPDATE_TESTS", e.toString(), e.getStackTrace());
                            e.printStackTrace();
                            return;
                        }
                        break;
                    case 14:
                        StateActivity.this.restoreFilesMode();
                        break;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void readDateTimeMode(JSONObject json) throws Throwable {
        if (isJSONok(json, new MyJSON[]{MyJSON.date_time})) {
            int stationDateTime = json.getInt(MyJSON.date_time.toString());
            this.mVerification.setCurrentStationTime(stationDateTime);
            Calendar tabletTime = Calendar.getInstance();
            Calendar stationTime = Calendar.getInstance();
            Date stationDate = new Date(((long) stationDateTime) * 1000);
            stationTime.setTime(stationDate);
            if (json.has(MyJSON.count_file_status.toString())) {
                this.mQueue.endMode();
                return;
            }
            if (json.has(MyJSON.date_from_net.toString())) {
                boolean dateFromNet = json.getBoolean("date_from_net");
                Log.e(TAG, String.format(Locale.ROOT, "Date from net is: %b", Boolean.valueOf(dateFromNet)));
                if (!dateFromNet && (tabletTime.get(6) != stationTime.get(6) || tabletTime.get(1) != stationTime.get(1))) {
                    Log.e(TAG, "wrong day of year!!!");
                    String stationDateString = String.format(Locale.ROOT, "%02d.%02d.%04d", Integer.valueOf(stationTime.get(5)), Integer.valueOf(stationTime.get(2) + 1), Integer.valueOf(stationTime.get(1)));
                    Log("station: " + stationDateString);
                    String tabletDateString = String.format(Locale.ROOT, "%02d.%02d.%04d", Integer.valueOf(tabletTime.get(5)), Integer.valueOf(tabletTime.get(2) + 1), Integer.valueOf(tabletTime.get(1)));
                    Log("tablet: " + tabletDateString);
                    MyFileReader.appendLog(getApplicationContext(), "State date", stationDateString + " " + tabletDateString);
                    Message m = this.mUIhandler.obtainMessage(13);
                    Bundle bundleAlert = new Bundle();
                    bundleAlert.putString("stationDate", stationDateString);
                    bundleAlert.putString("tabletDate", tabletDateString);
                    m.setData(bundleAlert);
                    this.mUIhandler.sendMessage(m);
                    return;
                }
                this.mQueue.endMode();
                return;
            }
            int station = 0;
            try {
                File settings = new File(getApplicationContext().getFilesDir(), "settings.json");
                JSONObject jsonSettings = new JSONObject(MyFileReader.readAndroidFile(getFilesDir(), settings.getName()));
                station = jsonSettings.optInt("station");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            File logFile = MyFileReader.getLogFile(getApplicationContext());
            Date curDate = Calendar.getInstance().getTime();
            SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
            this.mHttpFileClient.headUploadServer(String.format(Locale.ROOT, "logs/upload/1/%03d/%s", Integer.valueOf(station), format.format(curDate)), logFile);
            Message m2 = this.mUIhandler.obtainMessage(11);
            Bundle bundleAlert2 = new Bundle();
            bundleAlert2.putString("tittle", getString(R.string.alert));
            bundleAlert2.putString("message", getString(R.string.alert_restart_station));
            m2.setData(bundleAlert2);
            this.mUIhandler.sendMessage(m2);
            return;
        }
        Log.e(TAG, "answer JSON object HAS NO DATE_TIME!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void testPhotoMode(JSONObject json) throws Throwable {
        if (json.has(MyJSON.test_photo.toString())) {
            if (json.getInt(MyJSON.test_photo.toString()) == 0) {
                this.mSender.createSendString("{\"request\":1}\n");
                return;
            } else {
                if (json.getInt(MyJSON.test_photo.toString()) == 1) {
                    this.mHttpFileClient.download("1/current/photo_001.jpeg");
                    this.mQueue.endMode();
                    return;
                }
                return;
            }
        }
        Log.e(TAG, "answer JSON object HAS NO TEST_PHOTO!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stateMode(JSONObject json) throws Throwable {
        if (isJSONok(json, new MyJSON[]{MyJSON.cur_verifs, MyJSON.charge_batt, MyJSON.temper})) {
            int batteryCharge = json.getInt(MyJSON.charge_batt.toString());
            int waterTemperature = json.getInt(MyJSON.temper.toString());
            this.mVerification.setStateData(batteryCharge, waterTemperature);
            if (json.has("ver_soft")) {
                try {
                    PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
                    Log(String.format(Locale.ROOT, "Ver_android = %s", pInfo.versionName));
                    this.mVerification.setVerAndroid(pInfo.versionName);
                } catch (PackageManager.NameNotFoundException e) {
                    MyFileReader.appendLog(getApplicationContext(), "State ver_soft", e.toString(), e.getStackTrace());
                    e.printStackTrace();
                }
                Log(String.format(Locale.ROOT, "Ver_station = %s", json.getString("ver_soft")));
                this.mVerification.setVerStation(json.getString("ver_soft"));
            }
            if (json.has("number_today")) {
                this.mVerification.setVerificationNumberToday(json.getInt(MyJSON.number_today.toString()));
            }
            if (json.has("temper_dev") && json.has("hum_dev")) {
                float temperatureIn = (float) json.getDouble(MyJSON.temper_dev.toString());
                float humidityIn = (float) json.getDouble(MyJSON.hum_dev.toString());
                Log.e(TAG, String.format(Locale.ROOT, "Temperature inside: %.1f, Humidity inside: %.1f", Float.valueOf(temperatureIn), Float.valueOf(humidityIn)));
                this.mVerification.setInnerInfo(temperatureIn, humidityIn);
            }
            if (json.has("allowNoCalibration") && json.has("ACSECC")) {
                this.mVerification.setWorkInShortMode(json.getInt("allowNoCalibration") == 0 && !json.getBoolean("ACSECC"));
            }
            Log(String.format(Locale.ROOT, "SHORT MODE is %b", Boolean.valueOf(this.mVerification.getWorkInShortMode())));
            Message msg = this.mUIhandler.obtainMessage(6);
            Bundle bundle = new Bundle();
            bundle.putInt("count", this.mVerification.getVerificationNumberNew());
            msg.setData(bundle);
            this.mUIhandler.sendMessage(msg);
            this.mUIhandler.sendEmptyMessage(7);
            this.mQueue.endMode();
            return;
        }
        Log.e(TAG, "answer JSON object IS NULL!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void testNameMode(JSONObject json) throws Throwable {
        if (isJSONok(json, new MyJSON[]{MyJSON.test_name})) {
            if (this.mVerification.getTestName() == json.getInt(MyJSON.test_name.toString())) {
                this.mUIhandler.sendEmptyMessage(2);
                this.mQueue.endMode();
                return;
            } else {
                this.mSender.writeTestName(this.mVerification.getTestName(), this.mVerification.getMultiplier());
                return;
            }
        }
        Log.e(TAG, "answer JSON object IS NULL!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public void checkCurrentStateMode(String str) throws Throwable {
        if (str.contains("photo_102") || str.contains("meast_10") || str.contains("photo_111") || str.contains("photo_112") || str.contains("meast_11") || str.contains("photo_121") || str.contains("photo_122") || str.contains("meast_12") || str.contains("photo_131") || str.contains("photo_132") || str.contains("meast_13") || str.contains("photo_201") || str.contains("photo_202") || str.contains("meast_20") || str.contains("photo_211") || str.contains("photo_212") || str.contains("meast_21") || str.contains("photo_221") || str.contains("photo_222") || str.contains("meast_22") || str.contains("photo_231") || str.contains("photo_232") || str.contains("meast_23") || str.contains("photo_301") || str.contains("photo_302") || str.contains("meast_30") || str.contains("photo_311") || str.contains("photo_312") || str.contains("meast_31") || str.contains("photo_321") || str.contains("photo_322") || str.contains("meast_32") || str.contains("photo_331") || str.contains("photo_332") || str.contains("meast_33")) {
            Log("verification was interrupted");
            RestorationHelper restorationHelper = new RestorationHelper();
            restoration = restorationHelper;
            if (restorationHelper.setFilesToRestore(Arrays.asList(str.split("\n")))) {
                MyFileReader.appendLog(getApplicationContext(), "State", "restoring");
                this.mQueue.add(MyModeQueue.RequestMode.RESTORE_SOCKET);
                this.mUIhandler.sendEmptyMessage(12);
            } else {
                Log.e(TAG, "can't restore, need save");
                MyFileReader.appendLog(getApplicationContext(), "State", "can't restore");
                Message messageObtainMessage = this.mUIhandler.obtainMessage(4);
                Bundle bundle = new Bundle();
                bundle.putString("alert", getString(R.string.saving_previous_verification));
                messageObtainMessage.setData(bundle);
                this.mUIhandler.sendMessage(messageObtainMessage);
                this.mQueue.add(MyModeQueue.RequestMode.FINISH);
            }
            File logFile = MyFileReader.getLogFile(getApplicationContext());
            Date time = Calendar.getInstance().getTime();
            this.mHttpFileClient.headUploadServer(String.format(Locale.ROOT, "logs/upload/1/%03d/%s", Integer.valueOf(this.mVerification.getStationNumber()), new SimpleDateFormat("ddMMyyyy").format(time)), logFile);
        } else {
            this.mVerification.setId(String.valueOf(this.selectedId));
            this.mVerification.setLocal(this.local ? 1 : 0);
            this.mQueue.add(MyModeQueue.RequestMode.READ_ACTION);
        }
        this.mQueue.endMode();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void readActionMode(JSONObject json) throws Throwable {
        if (isJSONok(json, new MyJSON[]{MyJSON.action})) {
            int curAction = json.getInt(MyJSON.action.toString());
            if (curAction == 0) {
                MyFileReader.appendLog(getApplicationContext(), "State", "curAction == 0");
                return;
            }
            if (curAction == 1) {
                this.mVerification.setAction(curAction);
                this.mQueue.add(MyModeQueue.RequestMode.TEST_PHOTO);
                this.mQueue.add(MyModeQueue.RequestMode.STATE);
                this.mQueue.endMode();
                return;
            }
            if (curAction > 1) {
                Log("current action is not 1");
                this.mSender.changeAction(1);
                return;
            }
            return;
        }
        Log.e(TAG, "answer JSON object IS NULL!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void prepareMeasurementMode(JSONObject json) throws Throwable {
        if (isJSONok(json, new MyJSON[]{MyJSON.action})) {
            if (json.has(MyJSON.date_time.toString())) {
                this.mVerification.setCurrentStationTime(json.getInt(MyJSON.date_time.toString()));
            }
            int curAction = json.getInt(MyJSON.action.toString());
            if (curAction != 2) {
                this.mSender.changeAction(2);
                return;
            }
            int i = this.selectedId;
            if (i > 0) {
                try {
                    this.mHttpFileClient.upload("1/current/client.json", getClientInfo(i));
                } catch (NullPointerException | JSONException e) {
                    MyFileReader.appendLog(getApplicationContext(), "State prepareMeas", e.toString(), e.getStackTrace());
                    e.printStackTrace();
                }
            }
            this.mVerification.setAction(curAction);
            Message msg = this.mUIhandler.obtainMessage(3);
            Bundle bundleAction = new Bundle();
            bundleAction.putBoolean("restoring", false);
            msg.setData(bundleAction);
            this.mUIhandler.sendMessage(msg);
            this.mQueue.endMode();
            return;
        }
        Log.e(TAG, "answer JSON object IS NULL!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishMode(JSONObject json) throws Throwable {
        if (isJSONok(json, new MyJSON[]{MyJSON.finish})) {
            int finish = json.getInt(MyJSON.finish.toString());
            if (finish == 1) {
                Log.e(TAG, "FINISH!!!!");
                this.mQueue.endMode();
                this.mUIhandler.sendEmptyMessage(5);
                return;
            } else {
                if (finish > 1) {
                    this.mSender.checkFinish();
                    return;
                }
                Log.e(TAG, String.format(Locale.ROOT, "finish == %d", Integer.valueOf(finish)));
                Message msg = this.mUIhandler.obtainMessage(4);
                Bundle bundleAlert = new Bundle();
                bundleAlert.putString("alert", getString(R.string.error_sd_card));
                msg.setData(bundleAlert);
                this.mUIhandler.sendMessage(msg);
                this.mQueue.endMode();
                this.mUIhandler.sendEmptyMessage(5);
                return;
            }
        }
        Log.e(TAG, "answer JSON object IS NULL!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void firmwareUpdateMode(JSONObject json) throws Throwable {
        if (isJSONok(json, new MyJSON[]{MyJSON.update_soft})) {
            boolean updateOk = json.getBoolean(MyJSON.update_soft.toString());
            if (updateOk) {
                Log.e(TAG, "firmware is updating");
                this.mQueue.endMode();
                checkApkUpdate();
                return;
            }
            this.mSender.updateFirmware();
            return;
        }
        Log.e(TAG, "answer JSON object IS NULL!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreSocketMode(JSONObject json) throws Throwable {
        if (isJSONok(json, new MyJSON[]{MyJSON.meas_numb, MyJSON.reit_numb, MyJSON.action, MyJSON.cur_verifs, MyJSON.charge_batt, MyJSON.finish, MyJSON.date_time, MyJSON.ver_soft, MyJSON.number_today})) {
            int measNumber = json.getInt(MyJSON.meas_numb.toString());
            int reitNumber = json.getInt(MyJSON.reit_numb.toString());
            int action = json.getInt(MyJSON.action.toString());
            int batteryCharge = json.getInt(MyJSON.charge_batt.toString());
            int dateTime = json.getInt(MyJSON.date_time.toString());
            String verSoft = json.getString(MyJSON.ver_soft.toString());
            int todayVerifications = json.getInt(MyJSON.number_today.toString());
            restoration.setMeasurementParams(measNumber, reitNumber);
            this.mVerification.loadAll(action, todayVerifications, batteryCharge, dateTime);
            this.mVerification.setVerStation(verSoft);
            try {
                PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
                Log(String.format(Locale.ROOT, "Ver_android = %s", pInfo.versionName));
                this.mVerification.setVerAndroid(pInfo.versionName);
            } catch (PackageManager.NameNotFoundException e) {
                MyFileReader.appendLog(getApplicationContext(), "State restoreSocket", e.toString(), e.getStackTrace());
                e.printStackTrace();
            }
            this.mQueue.add(MyModeQueue.RequestMode.RESTORE_FILES);
        } else {
            Log.e(TAG, "can't restore, need save");
            Toast.makeText(getApplicationContext(), getString(R.string.saving_previous_verification), 1).show();
            this.mQueue.add(MyModeQueue.RequestMode.FINISH);
        }
        this.mQueue.endMode();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreFilesMode() throws Throwable {
        String path = restoration.getNextFilePath();
        if (path != null) {
            this.mHttpFileClient.download(path);
            return;
        }
        Log.e(TAG, "No files to restore!");
        this.mVerification.removeExtraPhotos();
        Log(this.mVerification.toString());
        this.mQueue.add(MyModeQueue.RequestMode.RESTORE_ACTION);
        this.mQueue.endMode();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreActionMode(JSONObject json) throws Throwable {
        if (!isJSONok(json, new MyJSON[]{MyJSON.test_name, MyJSON.multiplier, MyJSON.meas_numb, MyJSON.reit_numb, MyJSON.action})) {
            if (isJSONok(json, new MyJSON[]{MyJSON.action})) {
                int curAction = json.getInt(MyJSON.action.toString());
                if (curAction == 2) {
                    this.mVerification.setAction(curAction);
                    this.mQueue.endMode();
                    Message msg = this.mUIhandler.obtainMessage(3);
                    Bundle bundleAction = new Bundle();
                    bundleAction.putBoolean("restoring", true);
                    msg.setData(bundleAction);
                    this.mUIhandler.sendMessage(msg);
                    return;
                }
                this.mSender.changeAction(2);
                return;
            }
            Log.e(TAG, "answer JSON object IS NULL!");
            return;
        }
        int curAction2 = json.getInt(MyJSON.action.toString());
        if (curAction2 == 2) {
            this.mVerification.setAction(curAction2);
            this.mQueue.endMode();
            Message msg2 = this.mUIhandler.obtainMessage(3);
            Bundle bundleAction2 = new Bundle();
            bundleAction2.putBoolean("restoring", true);
            msg2.setData(bundleAction2);
            this.mUIhandler.sendMessage(msg2);
            return;
        }
        MySender mySender = this.mSender;
        int testName = this.mVerification.getTestName();
        int multiplier = this.mVerification.getMultiplier();
        int lastMeasNumber = this.mVerification.getLastMeasNumber();
        CounterVerification counterVerification = this.mVerification;
        mySender.restoreTestSettings(testName, multiplier, lastMeasNumber, counterVerification.getLastReitNumber(counterVerification.getLastMeasNumber()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isJSONok(JSONObject json, MyJSON[] params) {
        if (json == null) {
            return false;
        }
        for (MyJSON param : params) {
            if (!json.has(param.toString())) {
                return false;
            }
        }
        return true;
    }

    public void StateClicked(View v) {
        if (this.mVerification == null) {
            return;
        }
        new AlertDialog.Builder(this).setTitle(R.string.station_state_info_title).setMessage(this.mVerification.stateDataToString(getResources())).setNeutralButton(android.R.string.ok, (DialogInterface.OnClickListener) null).setIcon(android.R.drawable.ic_dialog_info).show();
    }

    public void PhotoClicked(View v) {
        setViewsClickable(false);
        this.mQueue.add(MyModeQueue.RequestMode.TEST_PHOTO);
        this.mQueue.add(MyModeQueue.RequestMode.STATE);
    }

    public void TestClicked(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View selectTestView = inflater.inflate(R.layout.activity_select_test, (ViewGroup) null);
        TextView textWaterTemperature = (TextView) selectTestView.findViewById(R.id.textSelectTemp);
        final Spinner spinnerQName = (Spinner) selectTestView.findViewById(R.id.spinnerQName);
        final Spinner spinnerQ = (Spinner) selectTestView.findViewById(R.id.spinnerQ);
        final Spinner spinnerRClass = (Spinner) selectTestView.findViewById(R.id.spinnerRClass);
        final Spinner spinnerMechanismType = (Spinner) selectTestView.findViewById(R.id.spinnerMechanismType);
        if (this.mVerification.getWaterTemperature() <= 0.0f || this.mVerification.getWaterTemperature() > 120.0f) {
            Toast.makeText(getApplicationContext(), getString(R.string.need_to_repeat_test_photo), 0).show();
            return;
        }
        builder.setView(selectTestView);
        builder.setPositiveButton(R.string.select, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.StateActivity.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int id) {
                StateActivity stateActivity;
                int i;
                String[] char1Array = {"4.0", "2.5", "1.6", BuildConfig.VERSION_NAME, "3.5", "2.5", "1.5", BuildConfig.VERSION_NAME};
                String[] char2Array = {"25.0", "40.0", "50.0", "63.0", "80.0", "100.0", "25.0", "50.0"};
                int testNumber = 0;
                int temp = StateActivity.this.mVerification.getWaterTemperatureRound();
                StateActivity.this.mVerification.setWaterTemperature(temp);
                int temperatureType = temp <= 30 ? 1 : 2;
                if (spinnerQName.getSelectedItemPosition() == 0) {
                    testNumber = (temperatureType * 100) + ((spinnerQ.getSelectedItemPosition() + 5) * 10) + spinnerRClass.getSelectedItemPosition() + 7;
                    StateActivity.this.mVerification.setDeviceTypeInfo(char1Array[spinnerQ.getSelectedItemPosition() + 4], char2Array[spinnerRClass.getSelectedItemPosition() + 6]);
                } else if (spinnerQName.getSelectedItemPosition() == 1) {
                    testNumber = (temperatureType * 100) + ((spinnerQ.getSelectedItemPosition() + 1) * 10) + spinnerRClass.getSelectedItemPosition() + 1;
                    StateActivity.this.mVerification.setDeviceTypeInfo(char1Array[spinnerQ.getSelectedItemPosition()], char2Array[spinnerRClass.getSelectedItemPosition()]);
                }
                Log.i(StateActivity.TAG, "Test Number = " + testNumber);
                dialog.dismiss();
                if (StateActivity.this.mVerification.loadMeasurementSettings(StateActivity.this.getApplication(), testNumber, spinnerMechanismType.getSelectedItemPosition() + 1)) {
                    StateActivity.this.selectedQNameIndex = spinnerQName.getSelectedItemPosition();
                    StateActivity.this.selectedMechanismIndex = spinnerMechanismType.getSelectedItemPosition();
                    Locale locale = Locale.ROOT;
                    String string = StateActivity.this.getString(R.string.selected_test_for_water);
                    Object[] objArr = new Object[1];
                    if (StateActivity.this.mVerification.getWaterTemperatureRound() > 30) {
                        stateActivity = StateActivity.this;
                        i = R.string.for_hot_water;
                    } else {
                        stateActivity = StateActivity.this;
                        i = R.string.for_cold_water;
                    }
                    objArr[0] = stateActivity.getString(i);
                    String title = String.format(locale, string, objArr);
                    AlertDialog.Builder builderSmall = new AlertDialog.Builder(StateActivity.this);
                    builderSmall.setTitle(title);
                    StringBuilder strBuilder = new StringBuilder();
                    try {
                        if (!MyDeviceTypeHelper.isTypeExist(StateActivity.this.getApplication(), StateActivity.this.mVerification.getChar1(), StateActivity.this.mVerification.getChar2())) {
                            Log.e(StateActivity.TAG, "no types for selected test");
                            strBuilder.append(StateActivity.this.getString(R.string.no_types_for_selected_test));
                            strBuilder.append(System.lineSeparator());
                            strBuilder.append(System.lineSeparator());
                        }
                    } catch (JSONException e) {
                        MyFileReader.appendLog(StateActivity.this.getApplicationContext(), "State TestClick", e.toString(), e.getStackTrace());
                        e.printStackTrace();
                    }
                    strBuilder.append(StateActivity.this.mVerification.testNameToText(StateActivity.this.getResources()));
                    if (spinnerQName.getSelectedItemPosition() == 0) {
                        strBuilder.append(", ").append(StateActivity.this.getString(R.string.according_to)).append(" ДСТУ 3580");
                    } else if (spinnerQName.getSelectedItemPosition() == 1) {
                        strBuilder.append(", ").append(StateActivity.this.getString(R.string.according_to)).append(" ДСТУ EN ISO 4064");
                    }
                    strBuilder.append(System.lineSeparator());
                    strBuilder.append(System.lineSeparator());
                    strBuilder.append(StateActivity.this.mVerification.printMeasurementSettings(StateActivity.this.getResources()));
                    builderSmall.setMessage(strBuilder.toString());
                    builderSmall.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.StateActivity.5.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog2, int id2) {
                            dialog2.dismiss();
                        }
                    });
                    AlertDialog dialogSmall = builderSmall.create();
                    dialogSmall.show();
                    StateActivity.this.setViewsClickable(false);
                    StateActivity.this.mQueue.add(MyModeQueue.RequestMode.TEST_NAME);
                    return;
                }
                Log.e(StateActivity.TAG, "problems with reading tests");
                Toast.makeText(StateActivity.this.getBaseContext(), StateActivity.this.getString(R.string.selected_test_not_found), 1).show();
            }
        });
        builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.StateActivity.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(-1).setTextSize(17.0f);
        dialog.getButton(-2).setTextSize(17.0f);
        textWaterTemperature.setText(String.format(Locale.ROOT, "%s: %d °C", getString(R.string.select_test_temperature), Integer.valueOf(this.mVerification.getWaterTemperatureRound())));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.Q_array, R.layout.spinner_item);
        spinnerQName.setAdapter((SpinnerAdapter) adapter);
        spinnerQName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.poverka.httpFileClient.activity.StateActivity.7
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != StateActivity.this.selectedQNameIndex) {
                    StateActivity.this.updateSelectTestSpinners(selectTestView, position, 0, 0);
                } else {
                    StateActivity stateActivity = StateActivity.this;
                    stateActivity.updateSelectTestSpinners(selectTestView, position, stateActivity.selectedQValueIndex, StateActivity.this.selectedRClassValueIndex);
                }
                if (StateActivity.this.selectedQNameIndex == -1) {
                    StateActivity.this.selectedQNameIndex = position;
                }
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getBaseContext(), R.array.MechanismType_array, R.layout.spinner_item);
        spinnerMechanismType.setAdapter((SpinnerAdapter) adapter2);
        int testName = this.mVerification.getTestName();
        if (testName == 0) {
            spinnerQName.setSelection(0);
            return;
        }
        int QIndex = String.valueOf(testName).charAt(1) - '0';
        int RClassIndex = String.valueOf(testName).charAt(2) - '0';
        if (QIndex < 5) {
            spinnerQName.setSelection(1);
            this.selectedQValueIndex = QIndex - 1;
            this.selectedRClassValueIndex = RClassIndex - 1;
        } else {
            spinnerQName.setSelection(0);
            this.selectedQValueIndex = QIndex - 5;
            this.selectedRClassValueIndex = RClassIndex - 7;
        }
        spinnerMechanismType.setSelection(this.selectedMechanismIndex);
    }

    public void StartClicked(View v) {
        try {
        } catch (JSONException e) {
            MyFileReader.appendLog(getApplicationContext(), "State StartClick", e.toString(), e.getStackTrace());
            e.printStackTrace();
        }
        if (this.mVerification.getTestName() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.need_to_select_test), 0).show();
            return;
        }
        if (!this.mVerification.isTestTemperatureOk()) {
            Toast.makeText(getApplicationContext(), getString(R.string.check_selected_test_and_temperature), 0).show();
            return;
        }
        if (!MyDeviceTypeHelper.isTypeExist(getApplication(), this.mVerification.getChar1(), this.mVerification.getChar2())) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_types_for_selected_test), 0).show();
            return;
        }
        setViewsClickable(false);
        try {
            JSONObject json = new JSONObject();
            json.put(MyJSON.counter_number.toString(), this.mVerification.getCounterNumber());
            json.put(MyJSON.start_volume.toString(), this.mVerification.getInitialVolume());
            json.put(MyJSON.production_year.toString(), this.mVerification.getProductionYear());
            json.put(MyJSON.type_id.toString(), this.mVerification.getDNtypeNumber());
            json.put(MyJSON.water_temperature.toString(), this.mVerification.getWaterTemperature() * 1000.0f);
            this.mHttpFileClient.upload("1/current/counter_info.json", json.toString());
        } catch (JSONException e2) {
            MyFileReader.appendLog(getApplicationContext(), "State StartClick", e2.toString(), e2.getStackTrace());
            e2.printStackTrace();
        }
        this.mQueue.add(MyModeQueue.RequestMode.PREPARE_MEASUREMENT);
    }

    public void ImageClicked(View v) {
        if (this.mVerification.getTestPhoto() == null) {
            return;
        }
        if (this.mVerification.isDeviceTypeInfoEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.select_test_first), 1).show();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("layoutType", ImageActivity.LayoutType.START);
        bundle.putInt("clicked", 0);
        bundle.putParcelable("image", this.mVerification.getTestPhoto());
        bundle.putString("number", this.mVerification.getCounterNumber());
        bundle.putInt("volume", this.mVerification.getInitialVolume());
        bundle.putInt("year", this.mVerification.getProductionYear());
        bundle.putInt("dnType", this.mVerification.getDNtypeNumber());
        bundle.putString("char1", this.mVerification.getChar1());
        bundle.putString("char2", this.mVerification.getChar2());
        new ImageActivity(this, this.imageResult, bundle);
    }

    private void connect() throws Throwable {
        TcpClient tcpClient = new TcpClient(this.mTcpReceived);
        this.mTcpClient = tcpClient;
        tcpClient.start();
        if (isConnected()) {
            MyFileReader.appendLog(getApplicationContext(), "State", "connected");
            Toast.makeText(getApplicationContext(), getString(R.string.connected), 0).show();
            Log("Connected");
            this.mHttpFileClient = new HttpFileClient(this.mHttpReceived);
            this.mSender = new MySender(this.mTcpClient);
            this.mQueue = new MyModeQueue(this.requestMode);
            this.mVerification = new CounterVerification();
            this.mQueue.add(MyModeQueue.RequestMode.READ_DATE_TIME);
            checkTestsUpdate();
            this.mQueue.add(MyModeQueue.RequestMode.FLASH);
            this.mQueue.add(MyModeQueue.RequestMode.CHECK_CURRENT_STATE);
            Timer timer = new Timer();
            this.freezeCheckerTimer = timer;
            timer.schedule(new FreezeCheckerTask(), 5000L, 1000L);
            Thread thread = new Thread(this.senderRunnable);
            this.senderThread = thread;
            thread.start();
            return;
        }
        MyFileReader.appendLog(getApplicationContext(), "State", "NOT connected");
        Toast.makeText(getApplicationContext(), getString(R.string.not_connected), 0).show();
        Log("Not connected");
        stopActivity();
        Intent intent = new Intent(getApplicationContext(), (Class<?>) MainActivity.class);
        intent.addCategory("android.intent.category.HOME");
        intent.setFlags(67108864);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    private boolean isConnected() {
        for (int i = 0; i < 10; i++) {
            try {
                if (this.mTcpClient.isServerRunning()) {
                    return true;
                }
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                Log.e(TAG, "Sleep", e);
                return false;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopActivity() throws Throwable {
        Log("Останавливаем все процессы");
        MyModeQueue myModeQueue = this.mQueue;
        if (myModeQueue != null) {
            myModeQueue.stopAndClear();
        }
        Timer timer = this.freezeCheckerTimer;
        if (timer != null) {
            timer.cancel();
            this.freezeCheckerTimer.purge();
        }
        Thread thread = this.senderThread;
        if (thread != null) {
            thread.interrupt();
        }
        TcpClient tcpClient = this.mTcpClient;
        if (tcpClient != null) {
            tcpClient.stopClient(false);
        }
        this.requestMode.removeObservers(this);
    }

    private void initViews() {
        TextView textFileName = (TextView) findViewById(R.id.textFileName);
        TextView textTemperature = (TextView) findViewById(R.id.textTemperature);
        Button buttonState = (Button) findViewById(R.id.buttonShowState);
        Button buttonPhoto = (Button) findViewById(R.id.buttonRepeatPhoto);
        Button buttonTest = (Button) findViewById(R.id.buttonSelectTest);
        Button buttonStart = (Button) findViewById(R.id.buttonStateStart);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;
        int buttonHeight = (int) (screenHeight * BUTTON_HEIGHT_RATIO);
        int textLHeight = (int) (screenHeight * TEXT_L_HEIGHT_RATIO);
        int textSHeight = (int) (screenHeight * TEXT_S_HEIGHT_RATIO);
        int marginHeight = (int) (screenHeight * MARGIN_HEIGHT_RATIO);
        Log(screenHeight + " " + buttonHeight + " " + textLHeight + " " + marginHeight);
        ((LinearLayout.LayoutParams) textTemperature.getLayoutParams()).bottomMargin = marginHeight;
        textTemperature.requestLayout();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) buttonState.getLayoutParams();
        params.height = buttonHeight;
        params.bottomMargin = marginHeight;
        buttonState.requestLayout();
        LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) buttonPhoto.getLayoutParams();
        params2.height = buttonHeight;
        params2.bottomMargin = marginHeight;
        buttonPhoto.requestLayout();
        LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) buttonTest.getLayoutParams();
        params3.height = buttonHeight;
        params3.bottomMargin = marginHeight;
        buttonTest.requestLayout();
        LinearLayout.LayoutParams params4 = (LinearLayout.LayoutParams) buttonStart.getLayoutParams();
        params4.height = buttonHeight;
        params4.bottomMargin = marginHeight;
        buttonStart.requestLayout();
        textFileName.setTextSize(0, textLHeight);
        textTemperature.setTextSize(0, textLHeight);
        buttonState.setTextSize(0, textSHeight);
        buttonPhoto.setTextSize(0, textSHeight);
        buttonTest.setTextSize(0, textSHeight);
        buttonStart.setTextSize(0, textSHeight);
        setViewsClickable(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setViewsClickable(boolean value) {
        ImageView image = (ImageView) findViewById(R.id.imageStateView);
        Button buttonPhoto = (Button) findViewById(R.id.buttonRepeatPhoto);
        Button buttonTest = (Button) findViewById(R.id.buttonSelectTest);
        Button buttonStart = (Button) findViewById(R.id.buttonStateStart);
        image.setClickable(value);
        buttonPhoto.setEnabled(value);
        buttonTest.setEnabled(value);
        buttonStart.setEnabled(value);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSelectTestSpinners(View view, int pos, int QIndex, int RClassIndex) {
        Spinner spinnerQ = (Spinner) view.findViewById(R.id.spinnerQ);
        Spinner spinnerRClass = (Spinner) view.findViewById(R.id.spinnerRClass);
        if (pos == 0) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.Qn_array, R.layout.spinner_item);
            spinnerQ.setAdapter((SpinnerAdapter) adapter);
            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getBaseContext(), R.array.Class_array, R.layout.spinner_item);
            spinnerRClass.setAdapter((SpinnerAdapter) adapter2);
        } else if (pos == 1) {
            ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getBaseContext(), R.array.Q3_array, R.layout.spinner_item);
            spinnerQ.setAdapter((SpinnerAdapter) adapter3);
            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(getBaseContext(), R.array.R_array, R.layout.spinner_item);
            spinnerRClass.setAdapter((SpinnerAdapter) adapter4);
        }
        spinnerQ.setSelection(QIndex);
        spinnerRClass.setSelection(RClassIndex);
    }

    private class FreezeCheckerTask extends TimerTask {
        private FreezeCheckerTask() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            if (StateActivity.this.requestMode.getValue() != MyModeQueue.RequestMode.NONE && StateActivity.this.requestMode.getValue() != MyModeQueue.RequestMode.READ_DATE_TIME) {
                StateActivity.access$308(StateActivity.this);
                if (StateActivity.this.freezeSeconds == 5) {
                    StateActivity.this.freezeSeconds = 0;
                    Log.e(StateActivity.TAG, "REPEATING LAST MESSAGE");
                    StateActivity.this.mSender.repeatLastMessage();
                }
            }
        }
    }

    private static class UIhandler extends Handler {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private static final int ACTION = 3;
        private static final int ALERT = 4;
        private static final int ALERT_DIALOG = 11;
        private static final int ALERT_RESTORING = 12;
        private static final int ALERT_WRONG_DATE = 13;
        private static final int APK = 10;
        private static final int FILE_NAME = 6;
        private static final int MOBILE_NETWORK = 7;
        private static final int PROMPT_APK_UPDATE = 9;
        private static final int PROMPT_FIRMWARE_UPDATE = 8;
        private static final int STATE = 1;
        private static final int TEST_NAME = 2;
        private static final int TEST_PHOTO_DONE = 0;
        private static final int VERIFICATION_DONE = 5;
        private final StateActivity activity;

        private UIhandler(StateActivity activity) {
            this.activity = activity;
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) throws Throwable {
            Intent intentUpdate;
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case 0:
                    ImageView image = (ImageView) this.activity.findViewById(R.id.imageStateView);
                    image.setImageBitmap(this.activity.mVerification.getTestPhoto());
                    this.activity.setViewsClickable(true);
                    return;
                case 1:
                    Log.d(StateActivity.TAG, "1 " + this.activity.mVerification.stateDataToString(this.activity.getResources()));
                    return;
                case 2:
                    Log.d(StateActivity.TAG, "1 " + this.activity.mVerification.getTestName());
                    this.activity.setViewsClickable(true);
                    int daysToCalibration = this.activity.mVerification.getDaysToCalibration();
                    if (daysToCalibration < 0) {
                        Message m = this.activity.mUIhandler.obtainMessage(11);
                        Bundle bundleAlert = new Bundle();
                        bundleAlert.putString("tittle", this.activity.getString(R.string.alert));
                        if (this.activity.mVerification.getWorkInShortMode()) {
                            bundleAlert.putString("message", this.activity.getString(R.string.station_needs_calibration_short_mode));
                        } else {
                            bundleAlert.putString("message", this.activity.getString(R.string.station_needs_calibration));
                        }
                        m.setData(bundleAlert);
                        this.activity.mUIhandler.sendMessage(m);
                        return;
                    }
                    if (daysToCalibration < 10) {
                        Message m2 = this.activity.mUIhandler.obtainMessage(11);
                        Bundle bundleAlert2 = new Bundle();
                        bundleAlert2.putString("tittle", this.activity.getString(R.string.alert));
                        bundleAlert2.putString("message", this.activity.getString(R.string.station_needs_calibration_in_10));
                        m2.setData(bundleAlert2);
                        this.activity.mUIhandler.sendMessage(m2);
                        return;
                    }
                    return;
                case 3:
                    this.activity.stopActivity();
                    boolean restoring = bundle.getBoolean("restoring");
                    Intent measurementIntent = new Intent(this.activity, (Class<?>) MeasurementActivity.class);
                    measurementIntent.putExtra("verification", this.activity.mVerification);
                    measurementIntent.putExtra("restoring", restoring);
                    this.activity.startActivity(measurementIntent);
                    return;
                case 4:
                    if (bundle.getString("error") != null) {
                        Toast.makeText(this.activity.getApplicationContext(), bundle.getString("error"), 1).show();
                        this.activity.stopActivity();
                    }
                    if (bundle.getString("alert") != null) {
                        Toast.makeText(this.activity.getApplicationContext(), msg.getData().getString("alert"), 1).show();
                        return;
                    }
                    return;
                case 5:
                    this.activity.stopActivity();
                    Intent intent = new Intent(this.activity.getApplicationContext(), (Class<?>) MainActivity.class);
                    intent.addCategory("android.intent.category.HOME");
                    intent.setFlags(67108864);
                    intent.putExtra("EXIT", true);
                    this.activity.startActivity(intent);
                    return;
                case 6:
                    TextView textFileName = (TextView) this.activity.findViewById(R.id.textFileName);
                    TextView textTemperature = (TextView) this.activity.findViewById(R.id.textTemperature);
                    int count = bundle.getInt("count");
                    if (count < 0) {
                        textFileName.setText(String.format(Locale.ROOT, "%s%09d", this.activity.getResources().getString(R.string.state_view_file_name), 0));
                        Message m3 = this.activity.mUIhandler.obtainMessage(11);
                        Bundle bundleAlert3 = new Bundle();
                        bundleAlert3.putString("tittle", this.activity.getString(R.string.alert));
                        bundleAlert3.putString("message", String.format(Locale.ROOT, this.activity.getString(R.string.error_verification_count_code), Integer.valueOf(count)));
                        m3.setData(bundleAlert3);
                        this.activity.mUIhandler.sendMessage(m3);
                    } else {
                        textFileName.setText(String.format(Locale.ROOT, "%s%09d", this.activity.getResources().getString(R.string.state_view_file_name), Integer.valueOf(this.activity.mVerification.getVerificationNumberNew())));
                    }
                    textTemperature.setText(String.format(Locale.ROOT, "%s %d°C", this.activity.getResources().getString(R.string.state_view_temperature), Integer.valueOf(this.activity.mVerification.getWaterTemperatureRound())));
                    if (this.activity.mVerification.getWaterTemperatureRound() > 30) {
                        textTemperature.setTextColor(SupportMenu.CATEGORY_MASK);
                    } else {
                        textTemperature.setTextColor(-16776961);
                    }
                    this.activity.setViewsClickable(true);
                    return;
                case 7:
                    if (this.activity.mVerification.isOnline() == 1) {
                        this.activity.checkAndAlertMobileNetwork();
                        return;
                    }
                    return;
                case 8:
                    if (bundle.getString("cur_firmware_version") != null && bundle.getString("new_firmware_version") != null) {
                        String versionNameCur = msg.getData().getString("cur_firmware_version");
                        String versionNameNew = msg.getData().getString("new_firmware_version");
                        AlertDialog.Builder builderFirmware = new AlertDialog.Builder(this.activity);
                        builderFirmware.setTitle(R.string.firmware_update);
                        builderFirmware.setMessage(String.format(Locale.ROOT, this.activity.getString(R.string.firmware_update_available), versionNameCur, versionNameNew));
                        builderFirmware.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.StateActivity.UIhandler.1
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builderFirmware.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.poverka.httpFileClient.activity.StateActivity.UIhandler.2
                            @Override // android.content.DialogInterface.OnDismissListener
                            public void onDismiss(DialogInterface dialog) {
                                UIhandler.this.activity.mQueue.add(MyModeQueue.RequestMode.UPDATE_FIRMWARE);
                            }
                        });
                        AlertDialog stateDialog = builderFirmware.create();
                        stateDialog.show();
                        return;
                    }
                    return;
                case 9:
                    if (bundle.getString("apk_info") != null) {
                        try {
                            JSONObject apkInfo = new JSONObject(msg.getData().getString("apk_info"));
                            final int id = apkInfo.getInt("id");
                            final String apkName = apkInfo.getString("fileName");
                            String versionNameCur2 = msg.getData().getString("cur_apk");
                            String versionNameNew2 = apkInfo.getString("version").split(":")[1];
                            AlertDialog.Builder builderApk = new AlertDialog.Builder(this.activity);
                            builderApk.setTitle(R.string.tab_update);
                            builderApk.setMessage(String.format(Locale.ROOT, this.activity.getString(R.string.tab_update_available_straight), versionNameCur2, versionNameNew2));
                            builderApk.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.StateActivity.UIhandler.3
                                @Override // android.content.DialogInterface.OnClickListener
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builderApk.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.poverka.httpFileClient.activity.StateActivity.UIhandler.4
                                @Override // android.content.DialogInterface.OnDismissListener
                                public void onDismiss(DialogInterface dialog) {
                                    UIhandler.this.activity.mHttpFileClient.headDownloadApk(id, apkName);
                                }
                            });
                            AlertDialog stateDialog2 = builderApk.create();
                            stateDialog2.show();
                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                    return;
                case 10:
                    if (bundle.getString(NotificationCompat.CATEGORY_PROGRESS) != null) {
                        String progressString = msg.getData().getString(NotificationCompat.CATEGORY_PROGRESS);
                        try {
                            if (progressString == null) {
                                throw new AssertionError();
                            }
                            int progress = Integer.parseInt(progressString);
                            StateActivity.Log("Str " + progressString + " int " + progress);
                            if (progress != -1) {
                                StateActivity.mProgressDialog.setIndeterminate(false);
                                StateActivity.mProgressDialog.setMax(100);
                                StateActivity.mProgressDialog.setProgress(progress);
                            } else {
                                StateActivity.mProgressDialog.setMessage(this.activity.getString(R.string.downloading_update));
                                StateActivity.mProgressDialog.setIndeterminate(true);
                                StateActivity.mProgressDialog.setProgressStyle(1);
                                StateActivity.mProgressDialog.setCancelable(false);
                                StateActivity.mProgressDialog.show();
                            }
                            return;
                        } catch (NumberFormatException e2) {
                            StateActivity.mProgressDialog.dismiss();
                            try {
                                File toInstall = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), progressString);
                                if (Build.VERSION.SDK_INT >= 24) {
                                    Uri apkUri = FileProvider.getUriForFile(this.activity, "com.poverka.httpFileClient.fileprovider", toInstall);
                                    intentUpdate = new Intent("android.intent.action.INSTALL_PACKAGE");
                                    intentUpdate.setData(apkUri);
                                    intentUpdate.setFlags(1);
                                } else {
                                    Uri apkUri2 = Uri.fromFile(toInstall);
                                    intentUpdate = new Intent("android.intent.action.VIEW");
                                    intentUpdate.setDataAndType(apkUri2, "application/vnd.android.package-archive");
                                    intentUpdate.setFlags(268435456);
                                }
                                this.activity.startActivity(intentUpdate);
                                return;
                            } catch (Exception e3) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
                                builder.setTitle(this.activity.getString(R.string.tab_update));
                                builder.setMessage(String.format(Locale.ROOT, this.activity.getString(R.string.update_successfully_downloaded), progressString));
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.StateActivity.UIhandler.5
                                    @Override // android.content.DialogInterface.OnClickListener
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog stateDialog3 = builder.create();
                                stateDialog3.show();
                                return;
                            }
                        }
                    }
                    return;
                case 11:
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(this.activity);
                    builder2.setTitle(msg.getData().getString("tittle"));
                    builder2.setMessage(msg.getData().getString("message"));
                    builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.StateActivity.UIhandler.6
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder2.setCancelable(false);
                    AlertDialog stateDialog4 = builder2.create();
                    stateDialog4.show();
                    return;
                case 12:
                    StateActivity stateActivity = this.activity;
                    ProgressDialog.show(stateActivity, stateActivity.getString(R.string.alert), this.activity.getString(R.string.restoring_verification), true);
                    return;
                case 13:
                    String stationDate = msg.getData().getString("stationDate");
                    String tabletDate = msg.getData().getString("tabletDate");
                    AlertDialog.Builder builderWrongDate = new AlertDialog.Builder(this.activity);
                    builderWrongDate.setTitle(this.activity.getString(R.string.alert));
                    builderWrongDate.setMessage(String.format(Locale.ROOT, this.activity.getString(R.string.alert_wrong_date), stationDate, tabletDate));
                    builderWrongDate.setPositiveButton(stationDate, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.StateActivity.UIhandler.7
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) throws Throwable {
                            UIhandler.this.activity.mQueue.endMode();
                            dialog.dismiss();
                        }
                    });
                    builderWrongDate.setNeutralButton(tabletDate, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.StateActivity.UIhandler.8
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            UIhandler.this.activity.mSender.setDateTime();
                            dialog.dismiss();
                        }
                    });
                    builderWrongDate.setCancelable(false);
                    builderWrongDate.create().show();
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkAndAlertMobileNetwork() {
        if (!checkMobileDataEnabled(getApplicationContext())) {
            AlertDialog.Builder builderSmall = new AlertDialog.Builder(this);
            builderSmall.setTitle(R.string.alert);
            builderSmall.setMessage(R.string.need_to_turn_on_mobile_data);
            builderSmall.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.StateActivity.9
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialogSmall = builderSmall.create();
            dialogSmall.show();
            return;
        }
        try {
            File settings = new File(getApplicationContext().getFilesDir(), "settings.json");
            String text = MyFileReader.readAndroidFile(getFilesDir(), settings.getName());
            JSONObject jsonSettings = new JSONObject(text);
            Calendar calendar = Calendar.getInstance();
            int dayToday = calendar.get(6);
            if (jsonSettings.has("day_number")) {
                int dayRecorded = jsonSettings.getInt("day_number");
                if (dayRecorded != dayToday) {
                    Log("NOT EQUAL");
                    jsonSettings.put("day_number", dayToday);
                    jsonSettings.put("station", this.mVerification.getStationNumber());
                    MyFileReader.writeInternalFile(getApplicationContext(), settings.getName(), jsonSettings.toString());
                    checkDisabledUpdate();
                } else {
                    Log("EQUAL");
                    if (jsonSettings.has("check_firmware_update") && jsonSettings.getBoolean("check_firmware_update")) {
                        jsonSettings.put("check_firmware_update", false);
                        MyFileReader.writeInternalFile(getApplicationContext(), settings.getName(), jsonSettings.toString());
                        checkDisabledUpdate();
                    }
                }
                return;
            }
            Log("'DAY' DOESN'T EXIST");
            jsonSettings.put("day_number", dayToday);
            jsonSettings.put("station", this.mVerification.getStationNumber());
            MyFileReader.writeInternalFile(getApplicationContext(), settings.getName(), jsonSettings.toString());
            checkDisabledUpdate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkMobileDataEnabled(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
        try {
            Method method = Class.forName(cm.getClass().getName()).getDeclaredMethod("getMobileDataEnabled", new Class[0]);
            method.setAccessible(true);
            boolean mobileDataEnabled = ((Boolean) method.invoke(cm, new Object[0])).booleanValue();
            return mobileDataEnabled;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void checkDisabledUpdate() {
        String path = String.format(Locale.ROOT, "stations/state/1/%03d?ver=2", Integer.valueOf(this.mVerification.getStationNumber()));
        this.mHttpFileClient.headDownloadServer(path);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkFirmwareUpdate() {
        String path = String.format(Locale.ROOT, "firmware-version/1/Mob_ust_v2.bin?serial=%03d", Integer.valueOf(this.mVerification.getStationNumber()));
        this.mHttpFileClient.headDownloadServer(path);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkApkUpdate() {
        String apkLast = String.format(Locale.ROOT, "apk/last?equipment-type=1&serial=%03d", Integer.valueOf(this.mVerification.getStationNumber()));
        this.mHttpFileClient.headDownloadServer(apkLast);
    }

    private void checkTestsUpdate() {
        try {
            File settings = new File(getApplicationContext().getFilesDir(), "settings.json");
            JSONObject jsonSettings = new JSONObject(MyFileReader.readAndroidFile(getFilesDir(), settings.getName()));
            if (jsonSettings.has("update_tests") && jsonSettings.getBoolean("update_tests")) {
                jsonSettings.put("update_tests", false);
                MyFileReader.writeInternalFile(getApplicationContext(), settings.getName(), jsonSettings.toString());
                this.mQueue.add(MyModeQueue.RequestMode.UPDATE_TESTS);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getClientInfo(int id) throws JSONException, NullPointerException {
        String taskString = MyFileReader.readAndroidFile(getFilesDir(), "localTasks.json");
        JSONArray array = new JSONArray(taskString);
        for (int i = 0; i < array.length(); i++) {
            JSONObject currentClient = array.getJSONObject(i);
            if (currentClient.getInt("ID") == id) {
                JSONObject clientInfo = new JSONObject();
                clientInfo.put("ID", id);
                clientInfo.put("INTERNAL_ID", Integer.parseInt(this.mVerification.getProtocolNumber().split("-")[1]));
                clientInfo.put("PROTOCOL_NUMBER", this.mVerification.getProtocolNumber());
                clientInfo.put("SURNAME", currentClient.getString("SURNAME"));
                clientInfo.put("CITY", currentClient.getInt("CITY"));
                clientInfo.put("STREET", currentClient.getInt("STREET"));
                clientInfo.put("BUILDING", currentClient.optInt("BUILDING"));
                clientInfo.put("BUILDING_BUKVA", currentClient.getString("BUILDING_BUKVA").equals("null") ? JSONObject.NULL : currentClient.getString("BUILDING_BUKVA"));
                clientInfo.put("BUILDING_KORPUS", currentClient.getString("BUILDING_KORPUS").equals("null") ? JSONObject.NULL : currentClient.getString("BUILDING_KORPUS"));
                clientInfo.put("APARTMENT", currentClient.optInt("APARTMENT"));
                clientInfo.put("APARTMENT_BUKVA", currentClient.getString("APARTMENT_BUKVA").equals("null") ? JSONObject.NULL : currentClient.getString("APARTMENT_BUKVA"));
                clientInfo.put("FULL_ADDRESS", currentClient.getString("FULL_ADDRESS"));
                clientInfo.put("SEAL_NUMBER", JSONObject.NULL);
                clientInfo.put("PHONE_NUMBER", currentClient.getString("PHONE_NUMBER").equals("null") ? JSONObject.NULL : currentClient.getString("PHONE_NUMBER"));
                clientInfo.put("PHONE_NUMBER_DOP", currentClient.getString("PHONE_NUMBER_DOP").equals("null") ? JSONObject.NULL : currentClient.getString("PHONE_NUMBER_DOP"));
                clientInfo.put("PHONE_NUMBER_DOP2", currentClient.getString("PHONE_NUMBER_DOP2").equals("null") ? JSONObject.NULL : currentClient.getString("PHONE_NUMBER_DOP2"));
                clientInfo.put("E_MAIL", JSONObject.NULL);
                clientInfo.put("PROTOCOL_DATE", this.mVerification.getCurrentStationTimeString().split(" ")[0]);
                clientInfo.put("PROTOCOL_TIME", this.mVerification.getCurrentStationTimeString().split(" ")[1]);
                clientInfo.put("COUNTER_NUMBER", this.mVerification.getCounterNumber().isEmpty() ? JSONObject.NULL : this.mVerification.getCounterNumber());
                clientInfo.put("SERVICE_TYPE", this.mVerification.getWaterTemperatureRound() > 30 ? 2 : 1);
                clientInfo.put("NOTE", "");
                clientInfo.put("Z_NOMER", this.mVerification.getStationNumber());
                return clientInfo.toString();
            }
        }
        return "";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void Log(String message) {
        Log.d(TAG, message);
    }
}
