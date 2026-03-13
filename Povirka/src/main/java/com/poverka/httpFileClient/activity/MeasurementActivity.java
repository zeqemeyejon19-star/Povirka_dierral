package com.poverka.httpFileClient.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.poverka.httpFileClient.R;
import com.poverka.httpFileClient.activity.ClientActivity;
import com.poverka.httpFileClient.activity.ImageActivity;
import com.poverka.httpFileClient.measurement.CounterVerification;
import com.poverka.httpFileClient.measurement.MeasurementResults;
import com.poverka.httpFileClient.util.HttpFileClient;
import com.poverka.httpFileClient.util.MyFileReader;
import com.poverka.httpFileClient.util.MyJSON;
import com.poverka.httpFileClient.util.MyModeQueue;
import com.poverka.httpFileClient.util.MyPager;
import com.poverka.httpFileClient.util.MyPagerAdapter;
import com.poverka.httpFileClient.util.MySender;
import com.poverka.httpFileClient.util.TcpClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class MeasurementActivity extends AppCompatActivity {
    static final int ACTION_MEASUREMENT = 4;
    static final int ACTION_MEASUREMENT_DONE = 5;
    static final int ACTION_PARAMS = 2;
    static final int ACTION_PRE_MEASUREMENT = 3;
    static final int ACTION_START = 1;
    private static final float BUTTON_HEIGHT_RATIO = 0.14f;
    private static final float MARGIN_HEIGHT_RATIO = 0.02f;
    static final int SERVICE_TYPE_COLD = 1;
    static final int SERVICE_TYPE_HOT = 2;
    private static final String TAG = "MeasurementActivity";
    private static final float TEXT_L_HEIGHT_RATIO = 0.07f;
    private static final float TEXT_S_HEIGHT_RATIO = 0.05f;
    private static AlertDialog mProgressDialog;
    private static int progressDialogTimer;
    private MeasurementResults curMeasurementResult;
    private int finishTimer;
    private Timer freezeCheckerTimer;
    private int freezeRepeats;
    private int freezeSeconds;
    private boolean isRepeating;
    private boolean isRestoring;
    private HttpFileClient mHttpFileClient;
    private MyModeQueue mQueue;
    private MySender mSender;
    private TcpClient mTcpClient;
    private UIhandler mUIhandler;
    private CounterVerification mVerification;
    private MyPager pager;
    private MyPagerAdapter pagerAdapter;
    private int report;
    private int screenHeight;
    private Thread senderThread;
    private int threadDelay;
    private final MutableLiveData<MyModeQueue.RequestMode> requestMode = new MutableLiveData<>();
    private final MutableLiveData<String> resetListener = new MutableLiveData<>();
    public TcpClient.OnMessageReceived mTcpReceived = new TcpClient.OnMessageReceived() { // from class: com.poverka.httpFileClient.activity.MeasurementActivity.2
        @Override // com.poverka.httpFileClient.util.TcpClient.OnMessageReceived
        public void messageReceived(String message) throws Throwable {
            if (MeasurementActivity.this.requestMode.getValue() != MyModeQueue.RequestMode.NONE) {
                MeasurementActivity.this.freezeSeconds = 0;
                MeasurementActivity.this.freezeRepeats = 0;
                try {
                    int i = AnonymousClass16.$SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[((MyModeQueue.RequestMode) Objects.requireNonNull(MeasurementActivity.this.requestMode.getValue())).ordinal()];
                    if (i == 1) {
                        MeasurementActivity.this.flowRateSelectionMode(new JSONObject(message));
                    } else if (i == 2) {
                        MeasurementActivity.this.measurementMode(new JSONObject(message));
                    } else if (i == 3) {
                        MeasurementActivity.this.prepareMeasurementMode(new JSONObject(message));
                    } else if (i == 4) {
                        MeasurementActivity.this.finishMode(new JSONObject(message));
                    } else if (i == 5) {
                        MeasurementActivity.this.sleepTimerMode(new JSONObject(message));
                    }
                    return;
                } catch (JSONException e) {
                    Log.e(MeasurementActivity.TAG, "JSON error in receiver", e);
                    MeasurementActivity.this.stopActivity();
                    return;
                }
            }
            Log.e(MeasurementActivity.TAG, "Пришла строка, а программа не в режиме.. ");
            Log.e(MeasurementActivity.TAG, "Message => " + message);
        }
    };
    public HttpFileClient.OnMessageReceived mHttpReceived = new HttpFileClient.OnMessageReceived() { // from class: com.poverka.httpFileClient.activity.MeasurementActivity.3
        @Override // com.poverka.httpFileClient.util.HttpFileClient.OnMessageReceived
        public void messageReceived(HttpFileClient.Type type, String fileName, InputStream stream) throws Throwable {
            int i = 0;
            if (stream == null) {
                MyFileReader.appendLog(MeasurementActivity.this.getApplicationContext(), "Meas HTTP R", String.format(Locale.ROOT, "type is %d, %s", Integer.valueOf(type.ordinal()), fileName));
            }
            if (MeasurementActivity.this.requestMode.getValue() == MyModeQueue.RequestMode.UPDATE_RESULTS) {
                MeasurementActivity.this.freezeSeconds = 0;
                MeasurementActivity.this.freezeRepeats = 0;
                int fileNumber = -1;
                if (fileName.contains("counter_info")) {
                    fileNumber = 1;
                } else if (fileName.contains("meast_1")) {
                    fileNumber = 2;
                } else if (fileName.contains("meast_2")) {
                    fileNumber = 3;
                }
                MeasurementActivity.this.updateResultsMode(fileNumber);
                return;
            }
            if (fileName.contains("meast_") && type == HttpFileClient.Type.DOWNLOAD && stream != null) {
                try {
                    JSONObject json = new JSONObject(HttpFileClient.inputStreamToString(stream));
                    int measNumb = Integer.parseInt(String.valueOf(fileName.charAt(fileName.indexOf(".json") - 2)));
                    int reitNumb = Integer.parseInt(String.valueOf(fileName.charAt(fileName.indexOf(".json") - 1)));
                    MeasurementResults measurement = MeasurementActivity.this.mVerification.getMeasurement(measNumb, reitNumb);
                    json.put(MyJSON.first_val.toString(), measurement.getValStart());
                    json.put(MyJSON.last_val.toString(), measurement.getValEnd());
                    json.put(MyJSON.calc_error.toString(), measurement.getError());
                    json.put(MyJSON.result.toString(), measurement.getResult());
                    if (json.has(MyJSON.duration.toString()) && measurement.getDuration() == 0) {
                        measurement.setDuration(json.getInt(MyJSON.duration.toString()));
                    }
                    if (json.has(MyJSON.temper.toString()) && measurement.getWaterTemperature() == 0.0f) {
                        measurement.setWaterTemperature(json.getInt(MyJSON.temper.toString()));
                        Message msg = MeasurementActivity.this.mUIhandler.obtainMessage(7);
                        Bundle bundleAlert = new Bundle();
                        bundleAlert.putInt("meas_number", measurement.getMeasurementNumber());
                        msg.setData(bundleAlert);
                        MeasurementActivity.this.mUIhandler.sendMessage(msg);
                    }
                    MeasurementActivity.this.mHttpFileClient.upload(fileName, json.toString());
                    return;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
            if (fileName.contains("photo_") && type == HttpFileClient.Type.DOWNLOAD && stream != null) {
                try {
                    Pattern photo = Pattern.compile("photo_(.*?).jpeg");
                    Matcher m = photo.matcher(fileName);
                    if (m.find()) {
                        MeasurementActivity.this.curMeasurementResult.setPhoto(HttpFileClient.inputStreamToByteArray(stream), m.group(1).charAt(2) - '0');
                        MeasurementActivity.this.mUIhandler.sendEmptyMessage(4);
                    }
                    if (MeasurementActivity.this.mVerification.getAction() == 5) {
                        MeasurementActivity.this.threadDelay = MainActivity.REQUEST_DELAY;
                        MeasurementActivity.this.mUIhandler.sendEmptyMessage(3);
                        MeasurementActivity.this.mQueue.endMode();
                        return;
                    }
                    return;
                } catch (IOException e2) {
                    Log.e(MeasurementActivity.TAG, "Error in HTTP receiver", e2);
                    MeasurementActivity.this.stopActivity();
                    return;
                }
            }
            if (fileName.contains("client.json") && type == HttpFileClient.Type.UPLOAD && stream != null) {
                MyFileReader.appendLog(MeasurementActivity.this.getApplicationContext(), "Meas HTTP R", "client up");
                try {
                    JSONObject geoMetDate = new JSONObject();
                    geoMetDate.put("temperature", MeasurementActivity.this.mVerification.getEnvironmentT());
                    geoMetDate.put("humidity", MeasurementActivity.this.mVerification.getEnvironmentH());
                    geoMetDate.put("temperature_in", ((double) Math.round(MeasurementActivity.this.mVerification.getInnerTemperature() * 10.0f)) / 10.0d);
                    geoMetDate.put("humidity_in", ((double) Math.round(MeasurementActivity.this.mVerification.getInnerHumidity() * 10.0f)) / 10.0d);
                    MeasurementActivity.this.mHttpFileClient.upload("1/current/geo_met_date.json", geoMetDate.toString());
                    return;
                } catch (JSONException e3) {
                    e3.printStackTrace();
                    return;
                }
            }
            if (fileName.contains("geo_met_date.json") && type == HttpFileClient.Type.UPLOAD && stream != null) {
                MyFileReader.appendLog(MeasurementActivity.this.getApplicationContext(), "Meas HTTP R", "geo up");
                MeasurementActivity.this.mHttpFileClient.show("1/current");
                return;
            }
            if (fileName.contains("current") && type == HttpFileClient.Type.SHOW && stream != null) {
                MyFileReader.appendLog(MeasurementActivity.this.getApplicationContext(), "Meas HTTP R", "current shown");
                try {
                    String files = HttpFileClient.inputStreamToString(stream);
                    int fileCount = files.split("\n").length;
                    try {
                        JSONObject systemJson = new JSONObject();
                        systemJson.put("id", MeasurementActivity.this.mVerification.getId());
                        systemJson.put("personal_account", MeasurementActivity.this.mVerification.getPersonalAccount());
                        systemJson.put("local", MainActivity.MARKER);
                        systemJson.put("verif_number", MeasurementActivity.this.mVerification.getPaymentAccount());
                        systemJson.put("verif_today", MeasurementActivity.this.mVerification.getVerificationNumberToday() + 1);
                        systemJson.put("calibration_date", MeasurementActivity.this.mVerification.getVerificationDateUnix());
                        systemJson.put("date", MeasurementActivity.this.mVerification.getCurrentStationTime());
                        systemJson.put("test_name", MeasurementActivity.this.mVerification.getTestName());
                        systemJson.put("multiplier", MeasurementActivity.this.mVerification.getMultiplier());
                        systemJson.put("imp_liter", MeasurementActivity.this.mVerification.getImpLiter());
                        systemJson.put("report", MeasurementActivity.this.report);
                        systemJson.put("service_type", MeasurementActivity.this.mVerification.getServiceType());
                        systemJson.put("ver_soft", MeasurementActivity.this.mVerification.getVerStation());
                        systemJson.put("ver_android", MeasurementActivity.this.mVerification.getVerAndroid());
                        systemJson.put("file_count", fileCount);
                        if (MeasurementActivity.this.mVerification.isOnline() != 1) {
                            i = 1;
                        }
                        systemJson.put("status_server", i);
                        MeasurementActivity.this.mHttpFileClient.upload("1/current/system.json", systemJson.toString());
                    } catch (JSONException e4) {
                        e4.printStackTrace();
                    }
                    return;
                } catch (IOException e5) {
                    e5.printStackTrace();
                    return;
                }
            }
            if (fileName.contains("system.json") && type == HttpFileClient.Type.UPLOAD && stream != null) {
                Button buttonStart = (Button) MeasurementActivity.this.findViewById(R.id.buttonTestStart);
                if (buttonStart.getText().toString().contains(MeasurementActivity.this.getString(R.string.ready))) {
                    MyFileReader.appendLog(MeasurementActivity.this.getApplicationContext(), "Meas HTTP R", "system up");
                    MeasurementActivity.this.mQueue.add(MyModeQueue.RequestMode.FINISH);
                }
            }
        }
    };
    private final Runnable senderRunnable = new Runnable() { // from class: com.poverka.httpFileClient.activity.MeasurementActivity.4
        @Override // java.lang.Runnable
        public void run() {
            try {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(MeasurementActivity.this.threadDelay);
                    if (MeasurementActivity.this.mTcpClient.isServerRunning()) {
                        if (MeasurementActivity.this.requestMode.getValue() != MyModeQueue.RequestMode.NONE && MeasurementActivity.this.requestMode.getValue() != MyModeQueue.RequestMode.UPDATE_RESULTS) {
                            MeasurementActivity.this.mSender.send();
                        }
                    } else if (MeasurementActivity.this.freezeRepeats < 2) {
                        MyFileReader.appendLog(MeasurementActivity.this.getApplicationContext(), "Meas", "LOST connection");
                        Thread.currentThread().interrupt();
                        Log.e(MeasurementActivity.TAG, "Подключение потеряно");
                        Message msg = MeasurementActivity.this.mUIhandler.obtainMessage(0);
                        Bundle bundle = new Bundle();
                        bundle.putString("error", MeasurementActivity.this.getString(R.string.lost_connection));
                        msg.setData(bundle);
                        MeasurementActivity.this.mUIhandler.sendMessage(msg);
                        MeasurementActivity.this.mUIhandler.sendEmptyMessage(5);
                    }
                }
            } catch (InterruptedException e) {
                Log.e("Interrupted exception", "senderRunnable is interrupted");
            }
        }
    };
    private ImageActivity.OnImageResult imageResult = new ImageActivity.OnImageResult() { // from class: com.poverka.httpFileClient.activity.MeasurementActivity.14
        @Override // com.poverka.httpFileClient.activity.ImageActivity.OnImageResult
        public void imageResult(Bundle bundle) {
            int clicked = bundle.getInt("clicked");
            if (bundle.getSerializable("layoutType") == ImageActivity.LayoutType.START) {
                String counterNumber = bundle.getString("number");
                int volume = bundle.getInt("volume");
                int year = bundle.getInt("year");
                int dnType = bundle.getInt("dnType");
                MeasurementActivity.this.mVerification.setCounterInfo(counterNumber, volume, year, dnType);
                TextView page = (TextView) MeasurementActivity.this.pagerAdapter.getView(clicked);
                page.setText(MeasurementActivity.this.textOnImage(clicked));
                try {
                    JSONObject json = new JSONObject();
                    json.put(MyJSON.counter_number.toString(), counterNumber);
                    json.put(MyJSON.start_volume.toString(), String.format(Locale.ROOT, "%05d", Integer.valueOf(volume)));
                    json.put(MyJSON.production_year.toString(), year);
                    json.put(MyJSON.type_id.toString(), dnType);
                    json.put(MyJSON.water_temperature.toString(), MeasurementActivity.this.mVerification.getWaterTemperature() * 1000.0f);
                    MeasurementActivity.this.mHttpFileClient.upload("1/current/counter_info.json", json.toString());
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
            int value = bundle.getInt("value");
            if (!MeasurementActivity.this.mVerification.setValueByNumber(clicked, value)) {
                Message msg = MeasurementActivity.this.mUIhandler.obtainMessage(6);
                Bundle bundleAlert = new Bundle();
                bundleAlert.putString("tittle", MeasurementActivity.this.getString(R.string.alert));
                bundleAlert.putString("message", MeasurementActivity.this.getString(R.string.start_greater_than_finish));
                msg.setData(bundleAlert);
                MeasurementActivity.this.mUIhandler.sendMessage(msg);
            }
            TextView page2 = (TextView) MeasurementActivity.this.pagerAdapter.getView(clicked);
            page2.setText(MeasurementActivity.this.textOnImage(clicked));
            if ((clicked - 1) % 2 == 0 && MeasurementActivity.this.pagerAdapter.getCount() > clicked + 1) {
                TextView page3 = (TextView) MeasurementActivity.this.pagerAdapter.getView(clicked + 1);
                page3.setText(MeasurementActivity.this.textOnImage(clicked + 1));
            }
            if (MeasurementActivity.this.mVerification.hasEndPhoto((clicked + 1) / 2)) {
                MeasurementActivity.this.mHttpFileClient.download(MeasurementActivity.this.mVerification.getFileNameByNumber(clicked));
            }
        }
    };
    private final ClientActivity.OnClientResult clientResult = new ClientActivity.OnClientResult() { // from class: com.poverka.httpFileClient.activity.MeasurementActivity.15
        @Override // com.poverka.httpFileClient.activity.ClientActivity.OnClientResult
        public void clientResult(Bundle bundle) {
            File settings;
            JSONObject jsonSettings;
            int T_OLD;
            boolean delaySleep = bundle.getBoolean("delay_sleep");
            if (delaySleep) {
                if (bundle.getBoolean("sleeping")) {
                    MeasurementActivity.this.mUIhandler.sendEmptyMessage(5);
                    return;
                } else {
                    MeasurementActivity.this.mQueue.add(MyModeQueue.RequestMode.SLEEP_TIMER);
                    return;
                }
            }
            final String clientInfo = bundle.getString("clientInfo");
            MeasurementActivity.this.mVerification.setId(bundle.getString("id"));
            int paymentAccount = bundle.getInt("payment_account");
            MeasurementActivity.this.mVerification.setPersonalAccount(bundle.getString("personal_account"));
            MeasurementActivity.this.mVerification.setServiceType(bundle.getInt("service_type"));
            MeasurementActivity.Log(clientInfo);
            try {
                settings = new File(MeasurementActivity.this.getFilesDir(), "settings.json");
                String text = MyFileReader.readAndroidFile(MeasurementActivity.this.getFilesDir(), settings.getName());
                jsonSettings = new JSONObject(text);
                T_OLD = jsonSettings.optInt("environmentT", 22);
            } catch (JSONException e) {
                e = e;
            }
            try {
                int H_OLD = jsonSettings.optInt("environmentH", 60);
                MeasurementActivity.this.mVerification.setEnvironment(T_OLD, H_OLD);
                int newT = MeasurementActivity.this.mVerification.measureEnvironment(18, 24, T_OLD, 2);
                int newH = MeasurementActivity.this.mVerification.measureEnvironment(40, 71, H_OLD, 7);
                jsonSettings.put("environmentT", newT);
                jsonSettings.put("environmentH", newH);
                if (paymentAccount == 0) {
                    MeasurementActivity.this.mVerification.setPaymentAccount(0);
                    jsonSettings.put("paymentAccountCurrent", 0);
                    jsonSettings.put("paymentAccountGenerate", false);
                } else {
                    MeasurementActivity.this.mVerification.setPaymentAccount(paymentAccount);
                    boolean paymentAccountGenerate = jsonSettings.optBoolean("paymentAccountGenerate");
                    int paymentAccountNext = paymentAccount;
                    if (paymentAccountGenerate && paymentAccount < 99999) {
                        paymentAccountNext++;
                    }
                    jsonSettings.put("paymentAccountCurrent", paymentAccountNext);
                }
                MyFileReader.writeInternalFile(MeasurementActivity.this.getApplicationContext(), settings.getName(), jsonSettings.toString());
            } catch (JSONException e2) {
                e = e2;
                e.printStackTrace();
            }
            MeasurementActivity.this.mHttpFileClient.upload("1/current/client.json", clientInfo);
            Runnable myRunnable = new Runnable() { // from class: com.poverka.httpFileClient.activity.MeasurementActivity.15.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        MeasurementActivity.this.savePdfAndExcel(new JSONObject(clientInfo), MeasurementActivity.this.report);
                    } catch (JSONException e3) {
                        e3.printStackTrace();
                    }
                }
            };
            myRunnable.run();
        }
    };

    public enum Pages {
        FIRST,
        START,
        FINISH
    }

    static /* synthetic */ int access$3108() {
        int i = progressDialogTimer;
        progressDialogTimer = i + 1;
        return i;
    }

    static /* synthetic */ int access$408(MeasurementActivity x0) {
        int i = x0.freezeRepeats;
        x0.freezeRepeats = i + 1;
        return i;
    }

    static /* synthetic */ int access$808(MeasurementActivity x0) {
        int i = x0.freezeSeconds;
        x0.freezeSeconds = i + 1;
        return i;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws Throwable {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        Log("*** Created ***");
        initViews();
        progressDialogTimer = 0;
        this.isRepeating = false;
        this.mUIhandler = new UIhandler(this);
        this.threadDelay = MainActivity.REQUEST_DELAY;
        this.finishTimer = 0;
        Intent intent = getIntent();
        this.mVerification = (CounterVerification) intent.getParcelableExtra("verification");
        this.isRestoring = intent.getBooleanExtra("restoring", false);
        Log.d(TAG, this.mVerification.toString());
        LinearLayout statusLayout = (LinearLayout) findViewById(R.id.status_layout);
        initPager();
        for (int i = 0; i < 3; i++) {
            TextView view = (TextView) statusLayout.findViewById(i + 1);
            view.setText(String.format(Locale.ROOT, "Q=%.3f", Float.valueOf(this.mVerification.getRequiredConsumption(i) / 1000.0f)));
        }
        if (!this.isRestoring) {
            this.curMeasurementResult = this.mVerification.getMeasurement(1, 0);
        } else {
            for (int meas = 1; meas <= this.mVerification.getLastMeasNumber(); meas++) {
                MeasurementResults requiredMeasurement = this.mVerification.getRequiredMeasurement(meas);
                this.curMeasurementResult = requiredMeasurement;
                if (requiredMeasurement != null) {
                    insertPage(Pages.START, this.curMeasurementResult.getMeasurementNumber());
                    insertPage(Pages.FINISH, this.curMeasurementResult.getMeasurementNumber());
                }
            }
            if (this.mVerification.getLastMeasNumber() == 0) {
                this.curMeasurementResult = this.mVerification.newMeasurement();
            }
        }
        prepareViews();
        connect();
        initRequestObserver();
        this.resetListener.observe(this, new Observer<String>() { // from class: com.poverka.httpFileClient.activity.MeasurementActivity.1
            @Override // androidx.lifecycle.Observer
            public void onChanged(String val) throws Throwable {
                MeasurementActivity.this.mTcpClient = new TcpClient(MeasurementActivity.this.mTcpReceived);
                MeasurementActivity.this.mTcpClient.start();
                if (MeasurementActivity.this.isConnected()) {
                    MyFileReader.appendLog(MeasurementActivity.this.getApplicationContext(), "Reset", "connected");
                    MeasurementActivity.Log("Connected after reset");
                    MeasurementActivity.this.freezeRepeats = 0;
                    MeasurementActivity.this.mSender = new MySender(MeasurementActivity.this.mTcpClient);
                    MeasurementActivity.this.mSender.createSendString(val);
                    return;
                }
                Toast.makeText(MeasurementActivity.this.getApplicationContext(), MeasurementActivity.this.getString(R.string.not_connected), 0).show();
                MyFileReader.appendLog(MeasurementActivity.this.getApplicationContext(), "Reset", "NOT connected");
                MeasurementActivity.Log("Not connected after reset");
                MeasurementActivity.this.stopActivity();
                Intent intent2 = new Intent(MeasurementActivity.this.getApplicationContext(), (Class<?>) MainActivity.class);
                intent2.addCategory("android.intent.category.HOME");
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent2.putExtra("EXIT", true);
                MeasurementActivity.this.startActivity(intent2);
            }
        });
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

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), getString(R.string.need_to_finish_verification), 1).show();
    }

    private void initRequestObserver() {
        this.requestMode.observe(this, new Observer<MyModeQueue.RequestMode>() { // from class: com.poverka.httpFileClient.activity.MeasurementActivity.5
            @Override // androidx.lifecycle.Observer
            public void onChanged(MyModeQueue.RequestMode rMode) throws Throwable {
                MeasurementActivity.Log(String.format("RequestMode changed to %s", rMode.toString()));
                switch (AnonymousClass16.$SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[rMode.ordinal()]) {
                    case 1:
                        MeasurementActivity.this.mSender.changeAction(3);
                        break;
                    case 2:
                        MeasurementActivity.this.threadDelay = 1000;
                        MeasurementActivity.this.mSender.readMeasuringData();
                        break;
                    case 3:
                        MeasurementActivity.this.threadDelay = MainActivity.REQUEST_DELAY;
                        MeasurementActivity.this.mSender.prepareMeasurement(MeasurementActivity.this.curMeasurementResult.getMeasurementNumber(), MeasurementActivity.this.curMeasurementResult.getReiterationNumber());
                        break;
                    case 4:
                        MeasurementActivity.this.mSender.sendFinish();
                        break;
                    case 5:
                        MeasurementActivity.this.mSender.setSleepTimer(600);
                        break;
                    case 6:
                        MeasurementActivity.this.updateResultsMode(0);
                        break;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void flowRateSelectionMode(JSONObject json) throws Throwable {
        if (isJSONok(json, new MyJSON[]{MyJSON.action})) {
            int curAction = json.getInt(MyJSON.action.toString());
            if (curAction != 3) {
                this.mSender.changeAction(3);
                return;
            } else {
                this.mVerification.setAction(curAction);
                this.mQueue.endMode();
                return;
            }
        }
        Log.e(TAG, "answer JSON object IS NULL!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void measurementMode(JSONObject json) throws JSONException {
        if (isJSONok(json, new MyJSON[]{MyJSON.temper_dev, MyJSON.press_dev, MyJSON.hum_dev})) {
            int temperature = json.getInt(MyJSON.temper_dev.toString());
            int pressure = json.getInt(MyJSON.press_dev.toString());
            int humidity = json.getInt(MyJSON.hum_dev.toString());
            System.out.println(String.format(Locale.ROOT, "temperature: %.2f pressure: %.2f humidity: %.2f", Float.valueOf(temperature / 10.0f), Float.valueOf(pressure / 10.0f), Float.valueOf(humidity / 10.0f)));
        }
        if (isJSONok(json, new MyJSON[]{MyJSON.action, MyJSON.cur_con, MyJSON.average_con, MyJSON.cur_volume, MyJSON.test_photo})) {
            int curAction = json.getInt(MyJSON.action.toString());
            int curConsumption = json.getInt(MyJSON.cur_con.toString());
            int averageConsumption = json.getInt(MyJSON.average_con.toString());
            int curVolume = json.getInt(MyJSON.cur_volume.toString());
            boolean photoReady = json.getInt(MyJSON.test_photo.toString()) > 0;
            this.mVerification.setAction(curAction);
            this.curMeasurementResult.updateValues(curConsumption, averageConsumption, curVolume);
            this.mUIhandler.sendEmptyMessage(2);
            if (curAction == 4 && photoReady) {
                this.mHttpFileClient.download(this.curMeasurementResult.getPhotoName(1));
            } else if (curAction == 5 && photoReady) {
                this.mHttpFileClient.download(this.curMeasurementResult.getPhotoName(2));
            }
            this.mSender.readMeasuringData();
            return;
        }
        Log.e(TAG, "answer JSON object IS NULL!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void prepareMeasurementMode(JSONObject json) throws Throwable {
        if (isJSONok(json, new MyJSON[]{MyJSON.meas_numb, MyJSON.reit_numb, MyJSON.action})) {
            int curAction = json.getInt(MyJSON.action.toString());
            int meas = json.getInt(MyJSON.meas_numb.toString());
            int reit = json.getInt(MyJSON.reit_numb.toString());
            if (curAction == 2) {
                if (this.curMeasurementResult.getMeasurementNumber() == meas && this.curMeasurementResult.getReiterationNumber() == reit) {
                    this.mVerification.setAction(curAction);
                    this.mUIhandler.sendEmptyMessage(1);
                    this.mQueue.endMode();
                } else {
                    this.mSender.prepareMeasurement(this.curMeasurementResult.getMeasurementNumber(), this.curMeasurementResult.getReiterationNumber());
                }
            } else {
                this.mSender.changeAction(2);
            }
            if (json.has(MyJSON.date_time.toString())) {
                this.mVerification.setCurrentStationTime(json.getInt(MyJSON.date_time.toString()));
                return;
            }
            return;
        }
        Log.e(TAG, "answer JSON object IS NULL!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishMode(JSONObject json) throws Throwable {
        if (isJSONok(json, new MyJSON[]{MyJSON.finish})) {
            int finish = json.getInt(MyJSON.finish.toString());
            if (finish == 1) {
                MyFileReader.removeLog(getApplicationContext());
                Log.e(TAG, "FINISH!!!!");
                this.mQueue.endMode();
                this.mUIhandler.sendEmptyMessage(5);
                return;
            }
            if (finish == 3) {
                MyFileReader.appendLog(getApplicationContext(), "Meas", "finish is 3");
                Log.e(TAG, "FINISH NO PDF!!!!");
                this.mQueue.endMode();
                this.mUIhandler.sendEmptyMessage(5);
                return;
            }
            if (finish > 1) {
                int i = this.finishTimer + 1;
                this.finishTimer = i;
                if (i % 5 == 0) {
                    Log.d(TAG, String.format(Locale.ROOT, "finish == %d, timer == %d", Integer.valueOf(finish), Integer.valueOf(this.finishTimer)));
                    MyFileReader.appendLog(getApplicationContext(), "Meas", String.format(Locale.ROOT, "finish == %d, timer == %d", Integer.valueOf(finish), Integer.valueOf(this.finishTimer)));
                }
                if (this.finishTimer >= 15) {
                    this.mQueue.endMode();
                    this.mUIhandler.sendEmptyMessage(5);
                    return;
                } else {
                    this.mSender.checkFinish();
                    return;
                }
            }
            MyFileReader.appendLog(getApplicationContext(), "Meas", String.format(Locale.ROOT, "finish == %d", Integer.valueOf(finish)));
            Log.e(TAG, String.format(Locale.ROOT, "finish == %d", Integer.valueOf(finish)));
            Message msg = this.mUIhandler.obtainMessage(0);
            Bundle bundleAlert = new Bundle();
            bundleAlert.putString("alert", getString(R.string.error_sd_card));
            msg.setData(bundleAlert);
            this.mUIhandler.sendMessage(msg);
            this.mQueue.endMode();
            this.mUIhandler.sendEmptyMessage(5);
            return;
        }
        Log.e(TAG, "answer JSON object IS NULL!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sleepTimerMode(JSONObject json) throws Throwable {
        if (isJSONok(json, new MyJSON[]{MyJSON.powerOnOff})) {
            Log(String.valueOf(json.getInt(MyJSON.powerOnOff.toString())));
            this.mQueue.endMode();
        } else {
            Log.e(TAG, "answer JSON object IS NULL!");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateResultsMode(int fileNumber) throws Throwable {
        String path;
        String data;
        JSONObject json = new JSONObject();
        try {
            if (fileNumber != 0) {
                if (fileNumber == 1) {
                    MeasurementResults results = this.mVerification.getRequiredMeasurement(fileNumber);
                    if (results.getDuration() != 0 && results.getResult() != 0) {
                        json.put(MyJSON.average_con.toString(), results.getConsumptionAvrgImp());
                        json.put(MyJSON.duration.toString(), results.getDuration());
                        json.put(MyJSON.temper.toString(), (int) (results.getWaterTemperature() * 1000.0f));
                        json.put(MyJSON.first_val.toString(), results.getValStart());
                        json.put(MyJSON.last_val.toString(), results.getValEnd());
                        json.put(MyJSON.calc_error.toString(), results.getError());
                        json.put(MyJSON.result.toString(), results.getResult());
                        path = this.mVerification.getFileNameByNumber(2);
                        data = json.toString();
                    }
                    updateResultsMode(2);
                    return;
                }
                if (fileNumber == 2) {
                    MeasurementResults results2 = this.mVerification.getRequiredMeasurement(fileNumber);
                    if (results2.getDuration() != 0 && results2.getResult() != 0) {
                        json.put(MyJSON.average_con.toString(), results2.getConsumptionAvrgImp());
                        json.put(MyJSON.duration.toString(), results2.getDuration());
                        json.put(MyJSON.temper.toString(), (int) (results2.getWaterTemperature() * 1000.0f));
                        json.put(MyJSON.first_val.toString(), results2.getValStart());
                        json.put(MyJSON.last_val.toString(), results2.getValEnd());
                        json.put(MyJSON.calc_error.toString(), results2.getError());
                        json.put(MyJSON.result.toString(), results2.getResult());
                        path = this.mVerification.getFileNameByNumber(4);
                        data = json.toString();
                    }
                    updateResultsMode(3);
                    return;
                }
                this.mUIhandler.sendEmptyMessage(8);
                this.mQueue.endMode();
                return;
            }
            json.put(MyJSON.counter_number.toString(), this.mVerification.getCounterNumber());
            json.put(MyJSON.start_volume.toString(), this.mVerification.getInitialVolume());
            json.put(MyJSON.production_year.toString(), this.mVerification.getProductionYear());
            json.put(MyJSON.type_id.toString(), this.mVerification.getDNtypeNumber());
            json.put(MyJSON.water_temperature.toString(), this.mVerification.getWaterTemperature() * 1000.0f);
            path = "1/current/counter_info.json";
            data = json.toString();
            this.mHttpFileClient.upload(path, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isJSONok(JSONObject json, MyJSON[] params) {
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

    public void StartClicked(View v) throws Throwable {
        Button buttonStart = (Button) findViewById(R.id.buttonTestStart);
        if (buttonStart.getText().toString().contains(getString(R.string.start))) {
            setViewsClickable(false);
            buttonStart.setText(String.format(Locale.ROOT, "%s %d", getString(R.string.stop), Integer.valueOf(this.curMeasurementResult.getMeasurementNumber())));
            this.mQueue.add(MyModeQueue.RequestMode.FLOW_RATE_SELECTION);
            this.mQueue.add(MyModeQueue.RequestMode.MEASUREMENT);
            return;
        }
        if (buttonStart.getText().toString().contains(getString(R.string.stop))) {
            setViewsClickable(false);
            buttonStart.setText(String.format(Locale.ROOT, "%s %d", getString(R.string.start), Integer.valueOf(this.curMeasurementResult.getMeasurementNumber())));
            this.mQueue.stop();
            removePagesOfMeasurement(this.curMeasurementResult.getMeasurementNumber());
            this.curMeasurementResult.clearData();
            prepareViews();
            this.mQueue.add(MyModeQueue.RequestMode.PREPARE_MEASUREMENT);
            return;
        }
        if (buttonStart.getText().toString().contains(getString(R.string.ready))) {
            MyFileReader.appendLog(getApplicationContext(), "Meas buttonStart", "END");
            if (this.mVerification.getWorkInShortMode()) {
                MyFileReader.appendLog(getApplicationContext(), "Meas", "END in SHORT_MODE");
                this.mQueue.add(MyModeQueue.RequestMode.FINISH);
                return;
            }
            this.mVerification.calculateAverageTemperature();
            if (!this.mVerification.isTestTemperatureOk()) {
                this.mVerification.updateResults(getApplication());
                this.mQueue.add(MyModeQueue.RequestMode.UPDATE_RESULTS);
                setViewsClickable(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.alert);
                builder.setMessage(R.string.temperature_has_changed);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.MeasurementActivity.6
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog stateDialog = builder.create();
                stateDialog.setCancelable(false);
                stateDialog.show();
                return;
            }
            if (this.mVerification.isCounterInfoOK()) {
                this.mQueue.add(MyModeQueue.RequestMode.SLEEP_TIMER);
                this.report = 1;
                Bundle bundle = new Bundle();
                bundle.putString("id", this.mVerification.getId());
                bundle.putString("protocolNumber", this.mVerification.getProtocolNumber());
                bundle.putInt("todayVerificationNumber", this.mVerification.getVerificationNumberToday() + 1);
                bundle.putString("counterNumber", this.mVerification.getCounterNumber());
                bundle.putString("dateTime", this.mVerification.getCurrentStationTimeString());
                bundle.putFloat("waterTemperature", this.mVerification.getWaterTemperature());
                new ClientActivity(this, this.clientResult, bundle);
                setViewsClickable(false);
                return;
            }
            Toast.makeText(getApplicationContext(), getString(R.string.need_to_enter_counter_info), 1).show();
        }
    }

    public void RepeatClicked(View v) {
        int id = this.curMeasurementResult.getMeasurementNumber();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.repeat_measurement_title);
        builder.setMessage(String.format(Locale.ROOT, getString(R.string.sure_want_repeat), Integer.valueOf(id)));
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.MeasurementActivity.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                MeasurementActivity.this.setViewsClickable(false);
                MeasurementActivity measurementActivity = MeasurementActivity.this;
                measurementActivity.removePagesOfMeasurement(measurementActivity.curMeasurementResult.getMeasurementNumber());
                MeasurementActivity measurementActivity2 = MeasurementActivity.this;
                measurementActivity2.curMeasurementResult = measurementActivity2.mVerification.repeatMeasurement(MeasurementActivity.this.curMeasurementResult.getMeasurementNumber());
                MeasurementActivity.this.prepareViews();
                MeasurementActivity.this.mQueue.add(MyModeQueue.RequestMode.PREPARE_MEASUREMENT);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.MeasurementActivity.8
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog stateDialog = builder.create();
        stateDialog.show();
    }

    public void NextClicked(View v) {
        if (this.mVerification.getLastMeasNumber() < 3) {
            setViewsClickable(false);
            this.curMeasurementResult = this.mVerification.newMeasurement();
            prepareViews();
            this.mQueue.add(MyModeQueue.RequestMode.PREPARE_MEASUREMENT);
            return;
        }
        Button buttonStart = (Button) findViewById(R.id.buttonTestStart);
        LinearLayout layoutButton = (LinearLayout) findViewById(R.id.layoutTestButtons);
        buttonStart.setText(R.string.ready);
        buttonStart.setVisibility(0);
        layoutButton.setVisibility(8);
        this.isRepeating = true;
        for (int i = 0; i < 3; i++) {
            RepeatAfterMeasurements((TextView) findViewById(i + 1));
        }
    }

    private void RepeatAfterMeasurements(TextView view) {
        view.setOnClickListener(new View.OnClickListener() { // from class: com.poverka.httpFileClient.activity.MeasurementActivity.9
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Button buttonStart = (Button) MeasurementActivity.this.findViewById(R.id.buttonTestStart);
                if (MeasurementActivity.this.requestMode.getValue() == MyModeQueue.RequestMode.NONE && buttonStart.getText().toString().contains(MeasurementActivity.this.getString(R.string.ready))) {
                    int id = v.getId();
                    MeasurementActivity measurementActivity = MeasurementActivity.this;
                    measurementActivity.curMeasurementResult = measurementActivity.mVerification.getMeasurement(id, MeasurementActivity.this.mVerification.getLastReitNumber(id));
                    MeasurementActivity.this.RepeatClicked(null);
                }
            }
        });
    }


    /* JADX INFO: Access modifiers changed from: private */
    public void fabClicked() {
        if (this.mVerification.getWorkInShortMode()) {
            Toast.makeText(getApplicationContext(), getString(R.string.verification_cancel_blocked), 1).show();
            return;
        }
        if (this.mVerification.isResultDefined()) {
            Toast.makeText(getApplicationContext(), getString(R.string.verification_with_result_cancel_blocked), 1).show();
            return;
        }
        if (this.requestMode.getValue() != MyModeQueue.RequestMode.NONE || !this.mVerification.isCounterInfoOK()) {
            if (!this.mVerification.isCounterInfoOK()) {
                Toast.makeText(getApplicationContext(), getString(R.string.need_to_enter_counter_info), 1).show();
                return;
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.stop_measurement_first), 1).show();
                return;
            }
        }
        AlertDialog.Builder builderConfirm = new AlertDialog.Builder(this);
        builderConfirm.setTitle(R.string.confirm_your_choise);
        builderConfirm.setMessage(R.string.sure_want_finish_verification);
        builderConfirm.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.MeasurementActivity.10
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builderReport = new AlertDialog.Builder(this);
                builderReport.setTitle(R.string.set_report);
                builderReport.setItems(MeasurementActivity.this.getResources().getStringArray(R.array.reports_to_choose), new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.MeasurementActivity.10.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog2, int which2) {
                        if (which2 == 0) {
                            MeasurementActivity.this.report = 7;
                        } else if (which2 == 1) {
                            MeasurementActivity.this.report = 8;
                            MeasurementActivity.this.mVerification.setId("-1");
                            MeasurementActivity.this.mVerification.setLocal(1);
                            MainActivity.MARKER = 1;
                        }
                        MeasurementActivity.this.mQueue.add(MyModeQueue.RequestMode.SLEEP_TIMER);
                        Button buttonStart = (Button) MeasurementActivity.this.findViewById(R.id.buttonTestStart);
                        buttonStart.setText(R.string.ready);
                        Bundle bundle = new Bundle();
                        bundle.putString("id", MeasurementActivity.this.mVerification.getId());
                        bundle.putString("protocolNumber", MeasurementActivity.this.mVerification.getProtocolNumber());
                        bundle.putInt("todayVerificationNumber", MeasurementActivity.this.mVerification.getVerificationNumberToday() + 1);
                        bundle.putString("counterNumber", MeasurementActivity.this.mVerification.getCounterNumber());
                        bundle.putString("dateTime", MeasurementActivity.this.mVerification.getCurrentStationTimeString());
                        bundle.putFloat("waterTemperature", MeasurementActivity.this.mVerification.getWaterTemperature());
                        new ClientActivity(this, MeasurementActivity.this.clientResult, bundle);
                        MeasurementActivity.this.setViewsClickable(false);
                        dialog2.dismiss();
                    }
                });
                builderReport.show();
            }
        });
        builderConfirm.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.MeasurementActivity.11
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderConfirm.create().show();
    }

    private void connect() throws Throwable {
        TcpClient tcpClient = new TcpClient(this.mTcpReceived);
        this.mTcpClient = tcpClient;
        tcpClient.start();
        int i = 0;
        if (isConnected()) {
            MyFileReader.appendLog(getApplicationContext(), "Meas", "connected");
            Toast.makeText(getApplicationContext(), getString(R.string.connected), 0).show();
            Log("Connected");
            this.mHttpFileClient = new HttpFileClient(this.mHttpReceived);
            this.mSender = new MySender(this.mTcpClient);
            this.mQueue = new MyModeQueue(this.requestMode);
            Timer timer = new Timer();
            this.freezeCheckerTimer = timer;
            timer.schedule(new FreezeCheckerTask(), 5000L, 1000L);
            Thread thread = new Thread(this.senderRunnable);
            this.senderThread = thread;
            thread.start();
            if (this.pagerAdapter.getCount() == 1) {
                this.mUIhandler.sendEmptyMessage(1);
            } else {
                this.mUIhandler.sendEmptyMessage(3);
            }
            if (!this.isRestoring) {
                try {
                    JSONObject systemJson = new JSONObject();
                    systemJson.put("id", this.mVerification.getId());
                    systemJson.put("personal_account", this.mVerification.getPersonalAccount());
                    systemJson.put("local", MainActivity.MARKER);
                    systemJson.put("verif_number", this.mVerification.getPaymentAccount());
                    systemJson.put("verif_today", this.mVerification.getVerificationNumberToday() + 1);
                    systemJson.put("calibration_date", this.mVerification.getVerificationDateUnix());
                    systemJson.put("date", this.mVerification.getCurrentStationTime());
                    systemJson.put("test_name", this.mVerification.getTestName());
                    systemJson.put("multiplier", this.mVerification.getMultiplier());
                    systemJson.put("imp_liter", this.mVerification.getImpLiter());
                    systemJson.put("report", 2);
                    systemJson.put("service_type", 1);
                    systemJson.put("ver_soft", this.mVerification.getVerStation());
                    systemJson.put("ver_android", this.mVerification.getVerAndroid());
                    systemJson.put("file_count", -1);
                    if (this.mVerification.isOnline() != 1) {
                        i = 1;
                    }
                    systemJson.put("status_server", i);
                    this.mHttpFileClient.upload("1/current/system.json", systemJson.toString());
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
            return;
        }
        MyFileReader.appendLog(getApplicationContext(), "Meas", "NOT connected");
        Toast.makeText(getApplicationContext(), getString(R.string.not_connected), 0).show();
        Log("Not connected");
        stopActivity();
        Intent intent = new Intent(getApplicationContext(), (Class<?>) MainActivity.class);
        intent.addCategory("android.intent.category.HOME");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isConnected() {
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

    private void initViews() {
        TextView textTestName = (TextView) findViewById(R.id.textTestName);
        TextView textTestSettings = (TextView) findViewById(R.id.textTestSettings);
        Button buttonStart = (Button) findViewById(R.id.buttonTestStart);
        ImageButton buttonRepeat = (ImageButton) findViewById(R.id.buttonTestRepeat);
        ImageButton buttonNext = (ImageButton) findViewById(R.id.buttonTestNext);
        TextView textLow = (TextView) findViewById(R.id.textLow);
        TextView textHigh = (TextView) findViewById(R.id.textHigh);
        TextView textCurCon = (TextView) findViewById(R.id.textCurCon);
        TextView textAvrgCon = (TextView) findViewById(R.id.textAvrgCon);
        TextView textTimeLeft = (TextView) findViewById(R.id.textTimeLeft);
        ProgressBar pBar = (ProgressBar) findViewById(R.id.progressBar);
        LinearLayout temperatureLayout = (LinearLayout) findViewById(R.id.temperature_layout);
        LinearLayout statusLayout = (LinearLayout) findViewById(R.id.status_layout);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int i = metrics.heightPixels;
        this.screenHeight = i;
        int buttonHeight = (int) (i * BUTTON_HEIGHT_RATIO);
        int textLHeight = (int) (i * TEXT_L_HEIGHT_RATIO);
        int textSHeight = (int) (i * TEXT_S_HEIGHT_RATIO);
        int marginHeight = (int) (i * MARGIN_HEIGHT_RATIO);
        textTestName.requestLayout();
        ((LinearLayout.LayoutParams) textTestSettings.getLayoutParams()).bottomMargin = marginHeight;
        textTestSettings.requestLayout();
        LinearLayout.LayoutParams paramsL = (LinearLayout.LayoutParams) buttonStart.getLayoutParams();
        paramsL.height = buttonHeight;
        paramsL.bottomMargin = marginHeight;
        buttonStart.requestLayout();
        LinearLayout.LayoutParams paramsL2 = (LinearLayout.LayoutParams) buttonRepeat.getLayoutParams();
        paramsL2.height = buttonHeight;
        paramsL2.bottomMargin = marginHeight;
        buttonRepeat.requestLayout();
        LinearLayout.LayoutParams paramsL3 = (LinearLayout.LayoutParams) buttonNext.getLayoutParams();
        paramsL3.height = buttonHeight;
        paramsL3.bottomMargin = marginHeight;
        buttonNext.requestLayout();
        ConstraintLayout.LayoutParams paramsC = (ConstraintLayout.LayoutParams) textAvrgCon.getLayoutParams();
        paramsC.topMargin = marginHeight / 2;
        textAvrgCon.requestLayout();
        ConstraintLayout.LayoutParams paramsC2 = (ConstraintLayout.LayoutParams) pBar.getLayoutParams();
        paramsC2.topMargin = marginHeight;
        pBar.requestLayout();
        ((LinearLayout.LayoutParams) temperatureLayout.getLayoutParams()).topMargin = marginHeight;
        temperatureLayout.requestLayout();
        ((LinearLayout.LayoutParams) statusLayout.getLayoutParams()).topMargin = marginHeight;
        statusLayout.requestLayout();
        textTestName.setTextSize(0, textLHeight);
        textTestSettings.setTextSize(0, textLHeight);
        buttonStart.setTextSize(0, textSHeight);
        buttonRepeat.setPadding(0, marginHeight, 0, marginHeight);
        buttonNext.setPadding(0, marginHeight, 0, marginHeight);
        textCurCon.setTextSize(0, textLHeight);
        textAvrgCon.setTextSize(0, textLHeight);
        textLow.setTextSize(0, textSHeight);
        textHigh.setTextSize(0, textSHeight);
        textTimeLeft.setTextSize(0, textSHeight);
        temperatureLayout.addView(temperatureBox());
        statusLayout.addView(circleBox(1));
        statusLayout.addView(circleBox(2));
        statusLayout.addView(circleBox(3));
        fab.setOnClickListener(new View.OnClickListener() { // from class: com.poverka.httpFileClient.activity.MeasurementActivity.12
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                MeasurementActivity.this.fabClicked();
            }
        });
    }

    private void initPager() {
        this.pager = (MyPager) findViewById(R.id.view_pager);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter();
        this.pagerAdapter = myPagerAdapter;
        this.pager.setAdapter(myPagerAdapter);
        if (!this.mVerification.getWorkInShortMode()) {
            final GestureDetector tapGestureDetector = new GestureDetector(this, new TapGestureListener(this));
            this.pager.setOnTouchListener(new View.OnTouchListener() { // from class: com.poverka.httpFileClient.activity.MeasurementActivity.13
                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View v, MotionEvent event) {
                    tapGestureDetector.onTouchEvent(event);
                    return false;
                }
            });
        }
        insertPage(Pages.FIRST, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void insertPage(Pages page, int measNumber) {
        int textLHeight = (int) (this.screenHeight * TEXT_L_HEIGHT_RATIO);
        TextView view = new TextView(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        view.setTextSize(0, textLHeight);
        view.setTextColor(ContextCompat.getColor(this, R.color.colorTextPrimary));
        view.setShadowLayer(15.0f, 0.0f, 0.0f, -1);
        try {
            int i = AnonymousClass16.$SwitchMap$com$poverka$httpFileClient$activity$MeasurementActivity$Pages[page.ordinal()];
            if (i == 1) {
                view.setText(textOnImage(measNumber));
                view.setBackground(new BitmapDrawable(getResources(), this.mVerification.getTestPhoto()));
                this.pagerAdapter.addView(this.pager, view, measNumber);
            } else if (i == 2) {
                view.setText(textOnImage((measNumber * 2) - 1));
                view.setBackground(new BitmapDrawable(getResources(), this.curMeasurementResult.getBitmap(1)));
                this.pagerAdapter.addView(this.pager, view, (measNumber * 2) - 1);
            } else if (i == 3) {
                view.setText(textOnImage(measNumber * 2));
                view.setBackground(new BitmapDrawable(getResources(), this.curMeasurementResult.getBitmap(2)));
                this.pagerAdapter.addView(this.pager, view, measNumber * 2);
            }
            int index = this.pagerAdapter.getItemPosition(view);
            Log(String.format(Locale.ROOT, "New page! it is %s, measNumber is %d, total is %d", page, Integer.valueOf(measNumber), Integer.valueOf(this.pagerAdapter.getCount())));
            this.pager.setCurrentItem(index, true);
        } catch (IndexOutOfBoundsException e) {
            MyFileReader.appendLog(getApplicationContext(), "Meas insertPage", e.toString(), e.getStackTrace());
            Message msg = this.mUIhandler.obtainMessage(6);
            Bundle bundle = new Bundle();
            bundle.putString("tittle", getString(R.string.alert));
            bundle.putString("message", getString(R.string.error_page_inserter));
            msg.setData(bundle);
            this.mUIhandler.sendMessage(msg);
        }
    }

    /* JADX INFO: renamed from: com.poverka.httpFileClient.activity.MeasurementActivity$16, reason: invalid class name */
    static /* synthetic */ class AnonymousClass16 {
        static final /* synthetic */ int[] $SwitchMap$com$poverka$httpFileClient$activity$MeasurementActivity$Pages;
        static final /* synthetic */ int[] $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode;

        static {
            int[] iArr = new int[Pages.values().length];
            $SwitchMap$com$poverka$httpFileClient$activity$MeasurementActivity$Pages = iArr;
            try {
                iArr[Pages.FIRST.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$poverka$httpFileClient$activity$MeasurementActivity$Pages[Pages.START.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$poverka$httpFileClient$activity$MeasurementActivity$Pages[Pages.FINISH.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            int[] iArr2 = new int[MyModeQueue.RequestMode.values().length];
            $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode = iArr2;
            try {
                iArr2[MyModeQueue.RequestMode.FLOW_RATE_SELECTION.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[MyModeQueue.RequestMode.MEASUREMENT.ordinal()] = 2;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[MyModeQueue.RequestMode.PREPARE_MEASUREMENT.ordinal()] = 3;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[MyModeQueue.RequestMode.FINISH.ordinal()] = 4;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[MyModeQueue.RequestMode.SLEEP_TIMER.ordinal()] = 5;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$poverka$httpFileClient$util$MyModeQueue$RequestMode[MyModeQueue.RequestMode.UPDATE_RESULTS.ordinal()] = 6;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePage(Pages page, int measNumber, boolean toClean) {
        int i = AnonymousClass16.$SwitchMap$com$poverka$httpFileClient$activity$MeasurementActivity$Pages[page.ordinal()];
        if (i == 1) {
            TextView view = (TextView) this.pagerAdapter.getView(measNumber);
            view.setText(textOnImage(measNumber));
            view.setBackground(new BitmapDrawable(getResources(), this.mVerification.getTestPhoto()));
        } else if (i == 2) {
            TextView view2 = (TextView) this.pagerAdapter.getView((measNumber * 2) - 1);
            if (toClean) {
                view2.setText(String.format(Locale.ROOT, "%s %d \nVc 0.00 л", getString(R.string.start), Integer.valueOf(measNumber)));
                view2.setBackground(null);
            } else {
                view2.setText(textOnImage((measNumber * 2) - 1));
                view2.setBackground(new BitmapDrawable(getResources(), this.mVerification.getBitmapByNumber((measNumber * 2) - 1)));
            }
        } else if (i == 3) {
            TextView view3 = (TextView) this.pagerAdapter.getView(measNumber * 2);
            if (toClean) {
                view3.setText(String.format(Locale.ROOT, "%s %d \nQ 0.000 %s\nt 0 °C \nVе 0 л\nVc 0.00 л \nΔ 0.00 %% \n%s", getString(R.string.finish), Integer.valueOf(measNumber), getString(R.string.cubic_meters), getResources().getStringArray(R.array.result_status)[0]));
                view3.setBackground(null);
            } else {
                view3.setText(textOnImage(measNumber * 2));
                view3.setBackground(new BitmapDrawable(getResources(), this.mVerification.getBitmapByNumber(measNumber * 2)));
            }
        }
        Log(String.format(Locale.ROOT, "Updating page! it is %s, measNumber is %d, total is %d", page.toString(), Integer.valueOf(measNumber), Integer.valueOf(this.pagerAdapter.getCount())));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removePagesOfMeasurement(int measNumber) {
        if (this.isRepeating) {
            updatePage(Pages.START, measNumber, true);
            updatePage(Pages.FINISH, measNumber, true);
            return;
        }
        if (this.mVerification.hasEndPhoto(measNumber)) {
            this.pagerAdapter.removeView(this.pager, measNumber * 2);
        }
        if (this.mVerification.hasStartPhoto(measNumber)) {
            this.pagerAdapter.removeView(this.pager, (measNumber * 2) - 1);
        }
        int index = this.pagerAdapter.getCount() - 1;
        this.pager.setCurrentItem(index, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void prepareViews() {
        TextView textTestName = (TextView) findViewById(R.id.textTestName);
        View underline = findViewById(R.id.underline);
        TextView textTestSettings = (TextView) findViewById(R.id.textTestSettings);
        TextView textLow = (TextView) findViewById(R.id.textLow);
        TextView textHigh = (TextView) findViewById(R.id.textHigh);
        TextView textCurCon = (TextView) findViewById(R.id.textCurCon);
        TextView textAverageCon = (TextView) findViewById(R.id.textAvrgCon);
        ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);
        TextView textTimeLeft = (TextView) findViewById(R.id.textTimeLeft);
        textTestName.setText(this.mVerification.testNameToText(getResources()));
        if (String.valueOf(this.mVerification.getTestName()).charAt(0) == '1') {
            underline.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccentCold));
        } else {
            underline.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccentHot));
        }
        textTestSettings.setText(String.format(Locale.ROOT, "Q = %.3f %s \nV = %d л", Float.valueOf(this.curMeasurementResult.getRequiredConsumptionLit() / 1000.0f), getString(R.string.cubic_meters), Integer.valueOf(this.curMeasurementResult.getRequiredVolumeLit())));
        textLow.setText(String.format(Locale.ROOT, "%.3f", Float.valueOf(this.curMeasurementResult.getRequiredLowLimit() / 1000.0f)));
        textHigh.setText(String.format(Locale.ROOT, "%.3f", Float.valueOf(this.curMeasurementResult.getRequiredHighLimit() / 1000.0f)));
        textCurCon.setText(String.format(Locale.ROOT, "Qт %.3f", Float.valueOf(0.0f)));
        textCurCon.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRangeNone));
        textCurCon.setTextColor(ContextCompat.getColor(this, R.color.colorTextPrimary));
        textAverageCon.setText(String.format(Locale.ROOT, "Qс %.3f", Float.valueOf(0.0f)));
        textAverageCon.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRangeNone));
        textAverageCon.setTextColor(ContextCompat.getColor(this, R.color.colorTextPrimary));
        progress.setMax(this.curMeasurementResult.getRequiredVolumeImp());
        progress.setProgress(0);
        textTimeLeft.setText(String.format(Locale.ROOT, "%s с.", "-"));
        int curMeasurementNumber = this.curMeasurementResult.getMeasurementNumber();
        changeToInit((TextView) findViewById(curMeasurementNumber), String.format(Locale.ROOT, "Q=%.3f", Float.valueOf(this.mVerification.getRequiredConsumption(curMeasurementNumber - 1) / 1000.0f)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setViewsClickable(boolean value) {
        Button buttonStart = (Button) findViewById(R.id.buttonTestStart);
        ImageButton buttonRepeat = (ImageButton) findViewById(R.id.buttonTestRepeat);
        ImageButton buttonNext = (ImageButton) findViewById(R.id.buttonTestNext);
        buttonStart.setEnabled(value);
        buttonRepeat.setEnabled(value);
        buttonNext.setEnabled(value);
    }

    private TextView temperatureBox() {
        int i = this.screenHeight;
        int viewHeight = (int) (((double) (i * BUTTON_HEIGHT_RATIO)) * 0.4d);
        int textSize = (int) (((double) (i * TEXT_S_HEIGHT_RATIO)) * 0.7d);
        TextView v = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, viewHeight);
        lp.weight = 1.0f;
        v.setLayoutParams(lp);
        v.setBackgroundResource(R.drawable.circle_green);
        v.setId(10);
        v.setTextSize(0, textSize);
        v.setText("Температура");
        v.setGravity(17);
        return v;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeTemperature(boolean temperatureOk) {
        TextView v = (TextView) findViewById(10);
        if (temperatureOk) {
            v.setBackgroundResource(R.drawable.circle_green);
            v.setTextColor(ContextCompat.getColor(this, R.color.colorOnAccent));
        } else {
            v.setBackgroundResource(R.drawable.circle_red);
            v.setTextColor(ContextCompat.getColor(this, R.color.colorOnAccent));
        }
    }

    private TextView circleBox(int id) {
        int i = this.screenHeight;
        int viewHeight = (int) (((double) (i * BUTTON_HEIGHT_RATIO)) * 0.8d);
        int textSize = (int) (i * TEXT_S_HEIGHT_RATIO);
        int margin = (int) (i * MARGIN_HEIGHT_RATIO);
        TextView v = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, viewHeight);
        lp.weight = 1.0f;
        if (id == 3) {
            lp.setMargins(0, 0, 0, 0);
        } else {
            lp.setMargins(0, 0, margin, 0);
        }
        v.setLayoutParams(lp);
        v.setBackgroundResource(R.drawable.circle_dark);
        v.setId(id);
        v.setTextSize(0, textSize - 6);
        v.setText("Q=0");
        v.setGravity(17);
        return v;
    }

    private void changeToInit(TextView v, String text) {
        int textSize = (int) (this.screenHeight * TEXT_S_HEIGHT_RATIO);
        v.setBackgroundResource(R.drawable.circle_dark);
        v.setText(text);
        v.setTextSize(0, textSize - 6);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeToValid(TextView v, boolean statusOK) {
        int textSize = (int) (this.screenHeight * 0.025f);
        if (statusOK) {
            v.setBackgroundResource(R.drawable.circle_green);
        } else {
            v.setBackgroundResource(R.drawable.circle_red);
        }
        v.setText(getResources().getStringArray(R.array.result_status)[1]);
        v.setTextSize(0, textSize);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeToInvalid(TextView v) {
        int textSize = (int) (this.screenHeight * 0.025f);
        v.setBackgroundResource(R.drawable.circle_red);
        v.setText(getResources().getStringArray(R.array.result_status)[2]);
        v.setTextSize(0, textSize);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeToDoing(TextView v) {
        int textSize = (int) (this.screenHeight * 0.025f);
        Drawable circleLightDrawable = ContextCompat.getDrawable(this, R.drawable.circle_light);
        if (circleLightDrawable != null && circleLightDrawable.getConstantState() != null
                && v.getBackground() != null && v.getBackground().getConstantState() != null
                && v.getBackground().getConstantState().equals(circleLightDrawable.getConstantState())) {
            v.setBackgroundResource(R.drawable.circle_dark);
        } else {
            v.setBackgroundResource(R.drawable.circle_light);
        }
        v.setText(R.string.measuring);
        v.setTextSize(0, textSize);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeToDone(TextView v, boolean statusOK) {
        int textSize = (int) (this.screenHeight * 0.035f);
        if (statusOK) {
            v.setBackgroundResource(R.drawable.circle_yellow);
        } else {
            v.setBackgroundResource(R.drawable.circle_red);
        }
        v.setText(R.string.measured);
        v.setTextSize(0, textSize);
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

    private class FreezeCheckerTask extends TimerTask {
        private FreezeCheckerTask() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() throws Throwable {
            if (MeasurementActivity.this.requestMode.getValue() != MyModeQueue.RequestMode.NONE) {
                MeasurementActivity.access$808(MeasurementActivity.this);
                if (MeasurementActivity.this.freezeSeconds == 5) {
                    MeasurementActivity.this.freezeSeconds = 0;
                    MeasurementActivity.access$408(MeasurementActivity.this);
                    if (MeasurementActivity.this.freezeRepeats < 2) {
                        Log.e(MeasurementActivity.TAG, "REPEATING LAST MESSAGE");
                        MeasurementActivity.this.mSender.repeatLastMessage();
                        return;
                    }
                    Log.e(MeasurementActivity.TAG, "RESETTING TCP CLIENT");
                    try {
                        try {
                            MeasurementActivity.this.mTcpClient.stopClient(true);
                            MeasurementActivity.this.mTcpClient.interrupt();
                            Thread.sleep(1000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } finally {
                        MeasurementActivity.this.resetListener.postValue(MeasurementActivity.this.mSender.getLastMessage());
                    }
                }
            }
        }
    }

    private static class UIhandler extends Handler {
        private static final int ALERT = 0;
        private static final int ALERT_DIALOG = 6;
        private static final int MEASUREMENT = 2;
        private static final int MEASUREMENT_DONE = 3;
        private static final int MEASUREMENT_READY = 1;
        private static final int PHOTO = 4;
        private static final int UPDATE_PHOTO_FOR_TEMPERATURE = 7;
        private static final int UPDATE_RESULTS = 8;
        private static final int VERIFICATION_DONE = 5;
        private MeasurementActivity activity;

        private UIhandler(MeasurementActivity activity) {
            super(Looper.getMainLooper());
            this.activity = activity;
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) throws Throwable {
            MeasurementActivity measurementActivity;
            int i;
            boolean z;
            LinearLayout layoutButton = (LinearLayout) this.activity.findViewById(R.id.layoutTestButtons);
            Button buttonStart = (Button) this.activity.findViewById(R.id.buttonTestStart);
            ProgressBar progress = (ProgressBar) this.activity.findViewById(R.id.progressBar);
            TextView textTimeLeft = (TextView) this.activity.findViewById(R.id.textTimeLeft);
            switch (msg.what) {
                case 0:
                    Bundle bundle = msg.getData();
                    if (bundle.getString("error") != null) {
                        Toast.makeText(this.activity.getApplicationContext(), bundle.getString("error"), 1).show();
                        this.activity.stopActivity();
                    }
                    if (bundle.getString("alert") != null) {
                        Toast.makeText(this.activity.getApplicationContext(), msg.getData().getString("alert"), 0).show();
                    }
                    break;
                case 1:
                    this.activity.setViewsClickable(true);
                    buttonStart.setVisibility(0);
                    layoutButton.setVisibility(8);
                    buttonStart.setText(String.format(Locale.ROOT, this.activity.getString(R.string.start_number), Integer.valueOf(this.activity.curMeasurementResult.getMeasurementNumber())));
                    int unused = MeasurementActivity.progressDialogTimer = 0;
                    break;
                case 2:
                    if (this.activity.mVerification.getAction() == 5) {
                        this.activity.setViewsClickable(false);
                        if ((MeasurementActivity.mProgressDialog == null || !MeasurementActivity.mProgressDialog.isShowing()) && MeasurementActivity.progressDialogTimer == 0) {
                            LinearLayout progressLayout = new LinearLayout(this.activity);
                            progressLayout.setOrientation(LinearLayout.HORIZONTAL);
                            int pad = (int) (12 * this.activity.getResources().getDisplayMetrics().density);
                            progressLayout.setPadding(pad, pad, pad, pad);
                            ProgressBar progressSpinner = new ProgressBar(this.activity);
                            progressSpinner.setIndeterminate(true);
                            progressLayout.addView(progressSpinner);
                            TextView progressMsg = new TextView(this.activity);
                            progressMsg.setPadding(pad, 0, 0, 0);
                            progressMsg.setText(this.activity.getString(R.string.measuring_temperature));
                            progressMsg.setGravity(17);
                            progressLayout.addView(progressMsg);
                            AlertDialog.Builder progressBuilder = new AlertDialog.Builder(this.activity);
                            progressBuilder.setView(progressLayout);
                            progressBuilder.setCancelable(false);
                            MeasurementActivity.mProgressDialog = progressBuilder.create();
                            MeasurementActivity.mProgressDialog.show();
                        } else {
                            MeasurementActivity.access$3108();
                            if (MeasurementActivity.progressDialogTimer == 25) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
                                builder.setTitle(this.activity.getString(R.string.alert));
                                builder.setMessage(R.string.error_no_finish_photo);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.MeasurementActivity.UIhandler.1
                                    @Override // android.content.DialogInterface.OnClickListener
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog stateDialog = builder.create();
                                stateDialog.show();
                            }
                        }
                    } else {
                        this.activity.setViewsClickable(true);
                    }
                    TextView textCurCon = (TextView) this.activity.findViewById(R.id.textCurCon);
                    TextView textAverageCon = (TextView) this.activity.findViewById(R.id.textAvrgCon);
                    textCurCon.setText(String.format(Locale.ROOT, "Qт %.3f", Float.valueOf(this.activity.curMeasurementResult.getConsumptionCurLit())));
                    textAverageCon.setText(String.format(Locale.ROOT, "Qс %.3f", Float.valueOf(this.activity.curMeasurementResult.getConsumptionAvrgLit())));
                    if (this.activity.mVerification.getAction() == 4) {
                        progress.setProgress(this.activity.curMeasurementResult.getVolumeCurImp());
                        textTimeLeft.setText(String.format(Locale.ROOT, "%s с.", this.activity.curMeasurementResult.getTimeLeft()));
                    }
                    int currentConsumptionRange = this.activity.curMeasurementResult.curConRange();
                    int averageConsumptionRange = this.activity.curMeasurementResult.avrgConRange();
                    if (currentConsumptionRange == -1) {
                        textCurCon.setBackgroundColor(ContextCompat.getColor(this.activity, R.color.colorRangeLow));
                        textCurCon.setTextColor(ContextCompat.getColor(this.activity, R.color.colorOnAccent));
                    } else if (currentConsumptionRange == 0) {
                        textCurCon.setBackgroundColor(ContextCompat.getColor(this.activity, R.color.colorRangeOk));
                        textCurCon.setTextColor(ContextCompat.getColor(this.activity, R.color.colorOnAccent));
                    } else if (currentConsumptionRange == 1) {
                        textCurCon.setBackgroundColor(ContextCompat.getColor(this.activity, R.color.colorRangeHigh));
                        textCurCon.setTextColor(ContextCompat.getColor(this.activity, R.color.colorOnAccent));
                    }
                    if (averageConsumptionRange == -1) {
                        textAverageCon.setBackgroundColor(ContextCompat.getColor(this.activity, R.color.colorRangeLow));
                        textAverageCon.setTextColor(ContextCompat.getColor(this.activity, R.color.colorOnAccent));
                    } else if (averageConsumptionRange == 0) {
                        textAverageCon.setBackgroundColor(ContextCompat.getColor(this.activity, R.color.colorRangeOk));
                        textAverageCon.setTextColor(ContextCompat.getColor(this.activity, R.color.colorOnAccent));
                    } else if (averageConsumptionRange == 1) {
                        textAverageCon.setBackgroundColor(ContextCompat.getColor(this.activity, R.color.colorRangeHigh));
                        textAverageCon.setTextColor(ContextCompat.getColor(this.activity, R.color.colorOnAccent));
                    }
                    MeasurementActivity measurementActivity2 = this.activity;
                    measurementActivity2.changeToDoing((TextView) measurementActivity2.findViewById(measurementActivity2.curMeasurementResult.getMeasurementNumber()));
                    break;
                case 3:
                    if (MeasurementActivity.mProgressDialog != null && MeasurementActivity.mProgressDialog.isShowing()) {
                        MeasurementActivity.mProgressDialog.dismiss();
                    }
                    this.activity.setViewsClickable(true);
                    if (this.activity.isRepeating) {
                        buttonStart.setText(this.activity.getString(R.string.ready));
                        buttonStart.setVisibility(0);
                        layoutButton.setVisibility(8);
                    } else {
                        buttonStart.setVisibility(8);
                        layoutButton.setVisibility(0);
                    }
                    int value = this.activity.curMeasurementResult.avrgConRange();
                    if (value != 0) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(this.activity);
                        builder2.setTitle(this.activity.getString(R.string.alert));
                        Locale locale = Locale.ROOT;
                        String string = this.activity.getString(R.string.average_consumption_problem);
                        Object[] objArr = new Object[1];
                        if (value >= 0) {
                            measurementActivity = this.activity;
                            i = R.string.more;
                        } else {
                            measurementActivity = this.activity;
                            i = R.string.less;
                        }
                        objArr[0] = measurementActivity.getString(i);
                        builder2.setMessage(String.format(locale, string, objArr));
                        builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.MeasurementActivity.UIhandler.2
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog stateDialog2 = builder2.create();
                        stateDialog2.show();
                    }
                    progress.setProgress(this.activity.curMeasurementResult.getRequiredVolumeImp());
                    textTimeLeft.setText(String.format(Locale.ROOT, "%s с.", "0"));
                    if (this.activity.curMeasurementResult.getResult() != 0) {
                        if (this.activity.curMeasurementResult.getResult() != 1) {
                            if (this.activity.curMeasurementResult.getResult() == 2) {
                                MeasurementActivity measurementActivity3 = this.activity;
                                measurementActivity3.changeToInvalid((TextView) measurementActivity3.findViewById(measurementActivity3.curMeasurementResult.getMeasurementNumber()));
                            }
                        } else {
                            MeasurementActivity measurementActivity4 = this.activity;
                            measurementActivity4.changeToValid((TextView) measurementActivity4.findViewById(measurementActivity4.curMeasurementResult.getMeasurementNumber()), value == 0);
                        }
                    } else {
                        MeasurementActivity measurementActivity5 = this.activity;
                        measurementActivity5.changeToDone((TextView) measurementActivity5.findViewById(measurementActivity5.curMeasurementResult.getMeasurementNumber()), value == 0);
                    }
                    break;
                case 4:
                    if (this.activity.mVerification.getAction() == 4) {
                        if (this.activity.isRepeating) {
                            this.activity.updatePage(Pages.START, this.activity.curMeasurementResult.getMeasurementNumber(), false);
                        } else {
                            this.activity.insertPage(Pages.START, this.activity.curMeasurementResult.getMeasurementNumber());
                        }
                    } else if (this.activity.mVerification.getAction() == 5) {
                        if (this.activity.isRepeating) {
                            this.activity.updatePage(Pages.FINISH, this.activity.curMeasurementResult.getMeasurementNumber(), false);
                        } else {
                            this.activity.insertPage(Pages.FINISH, this.activity.curMeasurementResult.getMeasurementNumber());
                        }
                    }
                    break;
                case 5:
                    this.activity.stopActivity();
                    Intent intent = new Intent(this.activity.getApplicationContext(), (Class<?>) MainActivity.class);
                    intent.addCategory("android.intent.category.HOME");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
                    this.activity.startActivity(intent);
                    break;
                case 6:
                    AlertDialog.Builder builder3 = new AlertDialog.Builder(this.activity);
                    builder3.setTitle(msg.getData().getString("tittle"));
                    builder3.setMessage(msg.getData().getString("message"));
                    builder3.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.MeasurementActivity.UIhandler.3
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog stateDialog3 = builder3.create();
                    stateDialog3.show();
                    break;
                case 7:
                    this.activity.updatePage(Pages.FINISH, msg.getData().getInt("meas_number"), false);
                    if (!this.activity.mVerification.isGlobalTemperatureOK()) {
                        MeasurementActivity.Log("global temperature fail");
                        this.activity.changeTemperature(false);
                        Message m = this.activity.mUIhandler.obtainMessage(6);
                        Bundle bundleAlert = new Bundle();
                        bundleAlert.putString("tittle", this.activity.getString(R.string.alert));
                        bundleAlert.putString("message", this.activity.getString(R.string.global_temperature_fail));
                        m.setData(bundleAlert);
                        this.activity.mUIhandler.sendMessage(m);
                    } else if (!this.activity.mVerification.isDeltaTemperatureOK()) {
                        MeasurementActivity.Log("delta temperature fail");
                        this.activity.changeTemperature(false);
                        Message m2 = this.activity.mUIhandler.obtainMessage(6);
                        Bundle bundleAlert2 = new Bundle();
                        bundleAlert2.putString("tittle", this.activity.getString(R.string.alert));
                        bundleAlert2.putString("message", this.activity.getString(R.string.delta_temperature_fail));
                        m2.setData(bundleAlert2);
                        this.activity.mUIhandler.sendMessage(m2);
                    } else {
                        MeasurementActivity.Log("temperature analysis is OK");
                        this.activity.changeTemperature(true);
                    }
                    break;
                case 8:
                    View underline = this.activity.findViewById(R.id.underline);
                    if (String.valueOf(this.activity.mVerification.getTestName()).charAt(0) == '1') {
                        underline.setBackgroundColor(ContextCompat.getColor(this.activity, R.color.colorAccentCold));
                    } else {
                        underline.setBackgroundColor(ContextCompat.getColor(this.activity, R.color.colorAccentHot));
                    }
                    this.activity.updatePage(Pages.FIRST, 0, false);
                    for (int meas = 1; meas <= 2; meas++) {
                        this.activity.updatePage(Pages.FINISH, meas, false);
                        if (this.activity.mVerification.getResultInt(meas) != 1) {
                            if (this.activity.mVerification.getResultInt(meas) == 2) {
                                MeasurementActivity measurementActivity6 = this.activity;
                                measurementActivity6.changeToInvalid((TextView) measurementActivity6.findViewById(meas));
                            }
                        } else {
                            MeasurementActivity measurementActivity7 = this.activity;
                            measurementActivity7.changeToValid((TextView) measurementActivity7.findViewById(meas), this.activity.mVerification.getAvrgConRange(meas) == 0);
                        }
                    }
                    if (!this.activity.mVerification.isGlobalTemperatureOK()) {
                        MeasurementActivity.Log("global temperature fail");
                        this.activity.changeTemperature(false);
                        z = true;
                    } else if (!this.activity.mVerification.isDeltaTemperatureOK()) {
                        MeasurementActivity.Log("delta temperature fail");
                        this.activity.changeTemperature(false);
                        z = true;
                    } else {
                        MeasurementActivity.Log("temperature analysis is OK");
                        z = true;
                        this.activity.changeTemperature(true);
                    }
                    this.activity.setViewsClickable(z);
                    break;
            }
        }
    }

    private class TapGestureListener extends GestureDetector.SimpleOnGestureListener {
        private final Activity activity;

        private TapGestureListener(Activity activity) {
            this.activity = activity;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Button buttonStart = (Button) MeasurementActivity.this.findViewById(R.id.buttonTestStart);
            TextView view = (TextView) MeasurementActivity.this.pagerAdapter.getView(MeasurementActivity.this.pager.getCurrentItem());
            if (view.getBackground() == null || !buttonStart.isEnabled()) {
                return false;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("clicked", MeasurementActivity.this.pager.getCurrentItem());
            if (MeasurementActivity.this.pager.getCurrentItem() == 0) {
                bundle.putSerializable("layoutType", ImageActivity.LayoutType.START);
                bundle.putParcelable("image", MeasurementActivity.this.mVerification.getTestPhoto());
                bundle.putString("number", MeasurementActivity.this.mVerification.getCounterNumber());
                bundle.putInt("volume", MeasurementActivity.this.mVerification.getInitialVolume());
                bundle.putInt("year", MeasurementActivity.this.mVerification.getProductionYear());
                bundle.putInt("dnType", MeasurementActivity.this.mVerification.getDNtypeNumber());
                bundle.putString("char1", MeasurementActivity.this.mVerification.getChar1());
                bundle.putString("char2", MeasurementActivity.this.mVerification.getChar2());
            } else {
                bundle.putSerializable("layoutType", ImageActivity.LayoutType.MEASUREMENT);
                bundle.putParcelable("image", MeasurementActivity.this.mVerification.getBitmapByNumber(MeasurementActivity.this.pager.getCurrentItem()));
                bundle.putInt("value", MeasurementActivity.this.mVerification.getValueByNumber(MeasurementActivity.this.pager.getCurrentItem()));
            }
            new ImageActivity(this.activity, MeasurementActivity.this.imageResult, bundle);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String textOnImage(int number) throws JSONException {
        String dnTypeFull;
        int meas = ((number - 1) / 2) + 1;
        if (number == 0) {
            try {
                dnTypeFull = getFullTypeById(this.mVerification.getDNtypeNumber());
            } catch (JSONException e) {
                e.printStackTrace();
                dnTypeFull = "-";
            }
            return String.format(Locale.ROOT, getString(R.string.start_photo) + "\n№ %s \nt %d°C \nVc %05d м³ \n%s %d \nТип %s ", this.mVerification.getCounterNumber(), Integer.valueOf(this.mVerification.getWaterTemperatureRound()), Integer.valueOf(this.mVerification.getInitialVolume()), getString(R.string.year), Integer.valueOf(this.mVerification.getProductionYear()), dnTypeFull);
        }
        if ((number - 1) % 2 == 0) {
            return String.format(Locale.ROOT, getString(R.string.start_number) + "\nVc %.2f л", Integer.valueOf(meas), Float.valueOf(this.mVerification.getValueByNumber(number) / 1000.0f));
        }
        String textOnImage = String.format(Locale.ROOT, getString(R.string.finish_number) + "\nQ %.3f %s \nt %d °C \nVе %d л \nVc %.2f л \nΔ %.2f %% \n%s", Integer.valueOf(meas), Float.valueOf(this.mVerification.getAvrgConsumption(meas)), getString(R.string.cubic_meters), Integer.valueOf(Math.round(this.mVerification.getTemperatureByNumber(meas))), Integer.valueOf(this.mVerification.getRequiredVolume(meas)), Float.valueOf(this.mVerification.getValueByNumber(number) / 1000.0f), Float.valueOf(this.mVerification.getError(meas) / 10.0f), this.mVerification.getResultString(getResources(), meas));
        if (this.mVerification.getResultInt(meas) == 0) {
            changeToDone((TextView) findViewById(meas), this.mVerification.getAvrgConRange(meas) == 0);
            return textOnImage;
        }
        if (this.mVerification.getResultInt(meas) == 1) {
            changeToValid((TextView) findViewById(meas), this.mVerification.getAvrgConRange(meas) == 0);
            return textOnImage;
        }
        if (this.mVerification.getResultInt(meas) == 2) {
            changeToInvalid((TextView) findViewById(meas));
            return textOnImage;
        }
        return textOnImage;
    }

    private String getFullTypeById(int id) throws JSONException {
        JSONArray jsonArray = readTypes();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            if (item.getInt("id") == id) {
                return "DN " + item.getString("dn") + " " + item.getString("name");
            }
        }
        throw new JSONException("no id");
    }

    private JSONArray readTypes() throws JSONException {
        String text = MyFileReader.readAndroidFile(getFilesDir(), "types.json");
        JSONObject json = new JSONObject(text);
        return json.getJSONArray("deviceTypes");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void savePdfAndExcel(JSONObject json, int success) throws JSONException {
        String date = json.getString("PROTOCOL_DATE");
        String time = json.getString("PROTOCOL_TIME");
        MyFileReader.createPoverkaFolder();
        File dayFolder = MyFileReader.createFolderInPoverkaFolder(date);
        ArrayList<String> list = MyFileReader.getDaysFromPoverka();
        MyFileReader.cleanOldest(list);
        savePdf(dayFolder, this.mVerification.getProtocolNumber(), date, time, success);
        saveExcel(dayFolder, date.replace('.', '_'), json);
    }

    /* JADX WARN: Unreachable blocks removed: 2, instructions: 4 */
    private void savePdf(File path, String fileName, String date, String time, int success) {
        String dnTypeFull;
        String str;
        String dnTypeFull2;
        String str2;
        String str3;
        boolean consumptionValid;
        int resultValid;
        int row;
        String str4;
        JSONException e;
        int col;
        try {
            try {
                dnTypeFull = getFullTypeById(this.mVerification.getDNtypeNumber());
            } catch (IOException e2) {
                e2.printStackTrace();
                return;
            }
        } catch (JSONException e3) {
            e3.printStackTrace();
            dnTypeFull = "-";
        }
        File file = new File(path, String.format(Locale.ROOT, "%s-%02d.pdf", fileName, Integer.valueOf(this.mVerification.getVerificationNumberToday() + 1)));
        file.createNewFile();
        FileOutputStream fOut = new FileOutputStream(file);
        int measCounter = 0;
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(842, 595, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setTextSize(12.0f);
        paint.setTypeface(Typeface.DEFAULT);
        canvas.drawLine(15.0f, 30.0f, 830.0f, 30.0f, paint);
        canvas.drawLine(15.0f, 290.0f, 215.0f, 290.0f, paint);
        canvas.drawLine(15.0f, 355.0f, 215.0f, 355.0f, paint);
        canvas.drawText(String.format(Locale.ROOT, getString(R.string.pdf_header), Integer.valueOf(this.mVerification.getVerificationNumberToday() + 1), fileName, date, time, Integer.valueOf(this.mVerification.getStationNumber()), this.mVerification.getVerificationDate()), 20.0f, 20.0f, paint);
        paint.setTextSize(9.0f);
        int row2 = 4 + 1;
        canvas.drawText(getString(R.string.pdf_required_consumption), 20, 4 * 16, paint);
        canvas.drawText(getString(R.string.pdf_allowable_error), 20, row2 * 16, paint);
        canvas.drawText(getString(R.string.pdf_required_volume), 20, r11 * 16, paint);
        canvas.drawText(getString(R.string.pdf_start_value), 20, r14 * 16, paint);
        canvas.drawText(getString(R.string.pdf_end_value), 20, r11 * 16, paint);
        canvas.drawText(getString(R.string.pdf_measured_volume), 20, r14 * 16, paint);
        canvas.drawText(getString(R.string.pdf_duration), 20, r11 * 16, paint);
        canvas.drawText(getString(R.string.pdf_average_consumption), 20, r14 * 16, paint);
        int row3 = row2 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1;
        canvas.drawText(getString(R.string.pdf_consumption_result), 20, r11 * 16, paint);
        canvas.drawText(getString(R.string.pdf_measured_error), 20, row3 * 16, paint);
        canvas.drawText(getString(R.string.pdf_measurement_result), 20, (row3 + 1) * 16, paint);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        MeasurementResults measurement = this.mVerification.getRequiredMeasurement(1);
        int resultValid2 = 1;
        if (measurement == null) {
            str = "-";
            dnTypeFull2 = dnTypeFull;
            str2 = "";
            str3 = "%.1f";
            consumptionValid = true;
        } else {
            str = "-";
            dnTypeFull2 = dnTypeFull;
            int row4 = 3 + 1;
            int measCounter2 = 3 * 16;
            canvas.drawText("Тест 1", 240, measCounter2, paint);
            str2 = "";
            canvas.drawText(String.format(Locale.ROOT, "%.3f", Float.valueOf(measurement.getRequiredConsumptionLit() / 1000.0f)), 240, row4 * 16, paint);
            canvas.drawText(String.format(Locale.ROOT, "%d", Integer.valueOf(measurement.getRequiredError())), 240, r13 * 16, paint);
            canvas.drawText(String.format(Locale.ROOT, "%d", Integer.valueOf(measurement.getRequiredVolumeLit())), 240, r8 * 16, paint);
            canvas.drawText(String.format(Locale.ROOT, "%.2f", Float.valueOf(measurement.getValStart() / 1000.0f)), 240, r13 * 16, paint);
            canvas.drawText(String.format(Locale.ROOT, "%.2f", Float.valueOf(measurement.getValEnd() / 1000.0f)), 240, r8 * 16, paint);
            canvas.drawText(String.format(Locale.ROOT, "%.2f", Float.valueOf((measurement.getValEnd() - measurement.getValStart()) / 1000.0f)), 240, r13 * 16, paint);
            canvas.drawText(String.format(Locale.ROOT, "%.2f", Float.valueOf(measurement.getDuration() / 1000.0f)), 240, r8 * 16, paint);
            int row5 = row4 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1;
            canvas.drawText(String.format(Locale.ROOT, "%.3f", Float.valueOf(measurement.getConsumptionAvrgLit())), 240, r13 * 16, paint);
            Locale locale = Locale.ROOT;
            Object[] objArr = new Object[1];
            objArr[0] = measurement.avrgConRange() == 0 ? getString(R.string.consumption_valid) : getString(R.string.consumption_invalid);
            canvas.drawText(String.format(locale, "%s", objArr), 240, row5 * 16, paint);
            int row6 = row5 + 1 + 1;
            canvas.drawText(String.format(Locale.ROOT, "%.1f", Float.valueOf(measurement.getError() / 10.0f)), 240, r13 * 16, paint);
            Locale locale2 = Locale.ROOT;
            Object[] objArr2 = new Object[1];
            str3 = "%.1f";
            objArr2[0] = measurement.getResult() == 1 ? getResources().getStringArray(R.array.result_status)[1] : measurement.getResult() == 2 ? getResources().getStringArray(R.array.result_status)[2] : str;
            canvas.drawText(String.format(locale2, "%s", objArr2), 240, row6 * 16, paint);
            paint.setTypeface(Typeface.DEFAULT);
            String reiteration = str2;
            if (measurement.getReiterationNumber() > 0) {
                reiteration = String.format(Locale.ROOT, ", повтор %d", Integer.valueOf(measurement.getReiterationNumber()));
            }
            if (measurement.hasPhotoStart()) {
                canvas.drawText(String.format(Locale.ROOT, "%s 1%s", getString(R.string.pdf_photo_start), reiteration), 225.0f, 250.0f, paint);
                Bitmap image = measurement.getBitmap(1);
                Matrix matrix = new Matrix();
                matrix.preTranslate(225.0f, 260.0f);
                matrix.preScale(0.3f, 0.3f);
                canvas.drawBitmap(image, matrix, null);
            }
            if (!measurement.hasPhotoEnd()) {
                consumptionValid = true;
                measCounter = 0;
            } else {
                canvas.drawText(String.format(Locale.ROOT, "%s 1%s", getString(R.string.pdf_photo_finish), reiteration), 225.0f, 420.0f, paint);
                Bitmap image2 = measurement.getBitmap(2);
                Matrix matrix2 = new Matrix();
                matrix2.preTranslate(225.0f, 430.0f);
                matrix2.preScale(0.3f, 0.3f);
                canvas.drawBitmap(image2, matrix2, null);
                measCounter = 0 + 1;
                if (measurement.avrgConRange() == 0) {
                    consumptionValid = true;
                } else {
                    consumptionValid = false;
                }
                if (measurement.getResult() != 1) {
                    resultValid2 = measurement.getResult();
                }
            }
        }
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        MeasurementResults measurement2 = this.mVerification.getRequiredMeasurement(2);
        if (measurement2 != null) {
            int row7 = 3 + 1;
            canvas.drawText("Тест 2", 445, 3 * 16, paint);
            boolean consumptionValid2 = consumptionValid;
            canvas.drawText(String.format(Locale.ROOT, "%.3f", Float.valueOf(measurement2.getRequiredConsumptionLit() / 1000.0f)), 445, row7 * 16, paint);
            canvas.drawText(String.format(Locale.ROOT, "%d", Integer.valueOf(measurement2.getRequiredError())), 445, r8 * 16, paint);
            canvas.drawText(String.format(Locale.ROOT, "%d", Integer.valueOf(measurement2.getRequiredVolumeLit())), 445, r14 * 16, paint);
            canvas.drawText(String.format(Locale.ROOT, "%.2f", Float.valueOf(measurement2.getValStart() / 1000.0f)), 445, r8 * 16, paint);
            canvas.drawText(String.format(Locale.ROOT, "%.2f", Float.valueOf(measurement2.getValEnd() / 1000.0f)), 445, r14 * 16, paint);
            canvas.drawText(String.format(Locale.ROOT, "%.2f", Float.valueOf((measurement2.getValEnd() - measurement2.getValStart()) / 1000.0f)), 445, r8 * 16, paint);
            canvas.drawText(String.format(Locale.ROOT, "%.2f", Float.valueOf(measurement2.getDuration() / 1000.0f)), 445, r14 * 16, paint);
            int row8 = row7 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1;
            canvas.drawText(String.format(Locale.ROOT, "%.3f", Float.valueOf(measurement2.getConsumptionAvrgLit())), 445, r8 * 16, paint);
            Locale locale3 = Locale.ROOT;
            Object[] objArr3 = new Object[1];
            objArr3[0] = measurement2.avrgConRange() == 0 ? getString(R.string.consumption_valid) : getString(R.string.consumption_invalid);
            canvas.drawText(String.format(locale3, "%s", objArr3), 445, row8 * 16, paint);
            String str5 = str3;
            int row9 = row8 + 1 + 1;
            canvas.drawText(String.format(Locale.ROOT, str5, Float.valueOf(measurement2.getError() / 10.0f)), 445, r8 * 16, paint);
            Locale locale4 = Locale.ROOT;
            Object[] objArr4 = new Object[1];
            str3 = str5;
            objArr4[0] = measurement2.getResult() == 1 ? getResources().getStringArray(R.array.result_status)[1] : measurement2.getResult() == 2 ? getResources().getStringArray(R.array.result_status)[2] : str;
            canvas.drawText(String.format(locale4, "%s", objArr4), 445, row9 * 16, paint);
            paint.setTypeface(Typeface.DEFAULT);
            String reiteration2 = str2;
            if (measurement2.getReiterationNumber() > 0) {
                reiteration2 = String.format(Locale.ROOT, ", повтор %d", Integer.valueOf(measurement2.getReiterationNumber()));
            }
            if (measurement2.hasPhotoStart()) {
                col = 445;
                canvas.drawText(String.format(Locale.ROOT, "%s 2%s", getString(R.string.pdf_photo_start), reiteration2), 430.0f, 250.0f, paint);
                Bitmap image3 = measurement2.getBitmap(1);
                Matrix matrix3 = new Matrix();
                matrix3.preTranslate(430.0f, 260.0f);
                matrix3.preScale(0.3f, 0.3f);
                canvas.drawBitmap(image3, matrix3, null);
            } else {
                col = 445;
            }
            if (measurement2.hasPhotoEnd()) {
                canvas.drawText(String.format(Locale.ROOT, "%s 2%s", getString(R.string.pdf_photo_finish), reiteration2), 430.0f, 420.0f, paint);
                Bitmap image4 = measurement2.getBitmap(2);
                Matrix matrix4 = new Matrix();
                matrix4.preTranslate(430.0f, 430.0f);
                matrix4.preScale(0.3f, 0.3f);
                canvas.drawBitmap(image4, matrix4, null);
                measCounter++;
                if (measurement2.avrgConRange() == 0) {
                    consumptionValid = consumptionValid2;
                } else {
                    consumptionValid = false;
                }
                if (measurement2.getResult() != 1 && resultValid2 != 0) {
                    resultValid2 = measurement2.getResult();
                }
            } else {
                consumptionValid = consumptionValid2;
            }
        }
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        MeasurementResults measurement3 = this.mVerification.getRequiredMeasurement(3);
        if (measurement3 != null) {
            int row10 = 3 + 1;
            canvas.drawText("Тест 3", 650, 3 * 16, paint);
            canvas.drawText(String.format(Locale.ROOT, "%.3f", Float.valueOf(measurement3.getRequiredConsumptionLit() / 1000.0f)), 650, row10 * 16, paint);
            canvas.drawText(String.format(Locale.ROOT, "%d", Integer.valueOf(measurement3.getRequiredError())), 650, r8 * 16, paint);
            canvas.drawText(String.format(Locale.ROOT, "%d", Integer.valueOf(measurement3.getRequiredVolumeLit())), 650, r14 * 16, paint);
            canvas.drawText(String.format(Locale.ROOT, "%.2f", Float.valueOf(measurement3.getValStart() / 1000.0f)), 650, r8 * 16, paint);
            canvas.drawText(String.format(Locale.ROOT, "%.2f", Float.valueOf(measurement3.getValEnd() / 1000.0f)), 650, r14 * 16, paint);
            canvas.drawText(String.format(Locale.ROOT, "%.2f", Float.valueOf((measurement3.getValEnd() - measurement3.getValStart()) / 1000.0f)), 650, r8 * 16, paint);
            canvas.drawText(String.format(Locale.ROOT, "%.2f", Float.valueOf(measurement3.getDuration() / 1000.0f)), 650, r14 * 16, paint);
            int row11 = row10 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1;
            canvas.drawText(String.format(Locale.ROOT, "%.3f", Float.valueOf(measurement3.getConsumptionAvrgLit())), 650, r8 * 16, paint);
            Locale locale5 = Locale.ROOT;
            Object[] objArr5 = new Object[1];
            objArr5[0] = measurement3.avrgConRange() == 0 ? getString(R.string.consumption_valid) : getString(R.string.consumption_invalid);
            canvas.drawText(String.format(locale5, "%s", objArr5), 650, row11 * 16, paint);
            int row12 = row11 + 1 + 1;
            canvas.drawText(String.format(Locale.ROOT, str3, Float.valueOf(measurement3.getError() / 10.0f)), 650, r8 * 16, paint);
            Locale locale6 = Locale.ROOT;
            Object[] objArr6 = new Object[1];
            objArr6[0] = measurement3.getResult() == 1 ? getResources().getStringArray(R.array.result_status)[1] : measurement3.getResult() == 2 ? getResources().getStringArray(R.array.result_status)[2] : str;
            canvas.drawText(String.format(locale6, "%s", objArr6), 650, row12 * 16, paint);
            paint.setTypeface(Typeface.DEFAULT);
            String reiteration3 = str2;
            if (measurement3.getReiterationNumber() > 0) {
                reiteration3 = String.format(Locale.ROOT, ", повтор %d", Integer.valueOf(measurement3.getReiterationNumber()));
            }
            if (measurement3.hasPhotoStart()) {
                canvas.drawText(String.format(Locale.ROOT, "%s 3%s", getString(R.string.pdf_photo_start), reiteration3), 635.0f, 250.0f, paint);
                Bitmap image5 = measurement3.getBitmap(1);
                Matrix matrix5 = new Matrix();
                matrix5.preTranslate(635.0f, 260.0f);
                matrix5.preScale(0.3f, 0.3f);
                canvas.drawBitmap(image5, matrix5, null);
            }
            if (measurement3.hasPhotoEnd()) {
                canvas.drawText(String.format(Locale.ROOT, "%s 3%s", getString(R.string.pdf_photo_finish), reiteration3), 635.0f, 420.0f, paint);
                Bitmap image6 = measurement3.getBitmap(2);
                Matrix matrix6 = new Matrix();
                matrix6.preTranslate(635.0f, 430.0f);
                matrix6.preScale(0.3f, 0.3f);
                canvas.drawBitmap(image6, matrix6, null);
                measCounter++;
                if (measurement3.avrgConRange() != 0) {
                }
                if (measurement3.getResult() != 1 && resultValid2 != 0) {
                    resultValid = measurement3.getResult();
                } else {
                    resultValid = resultValid2;
                }
            } else {
                resultValid = resultValid2;
            }
        } else {
            resultValid = resultValid2;
        }
        paint.setTypeface(Typeface.DEFAULT);
        int row13 = 17 + 1;
        canvas.drawText(getString(R.string.pdf_counter_info), 20, 17 * 16, paint);
        canvas.drawText(getString(R.string.pdf_according_to), 20, row13 * 16, paint);
        canvas.drawText(getString(R.string.pdf_counter_number), 20, r9 * 16, paint);
        canvas.drawText(getString(R.string.pdf_counter_type), 20, r12 * 16, paint);
        canvas.drawText(getString(R.string.pdf_production_year), 20, r9 * 16, paint);
        canvas.drawText(getString(R.string.pdf_volume), 20, r12 * 16, paint);
        canvas.drawText(getString(R.string.pdf_water_temperature), 20, r9 * 16, paint);
        int row14 = row13 + 1 + 1 + 1 + 1 + 1 + 1 + 1;
        canvas.drawText(getString(R.string.pdf_air_temperature), 20, r12 * 16, paint);
        canvas.drawText(getString(R.string.pdf_air_humidity), 20, row14 * 16, paint);
        canvas.drawText(getString(R.string.pdf_verification_result), 20, (row14 + 1) * 16, paint);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        int row15 = 17 + 1;
        canvas.drawText(this.mVerification.testNameToText(getResources()), 115, 17 * 16, paint);
        if (String.valueOf(this.mVerification.getTestName()).toCharArray()[1] >= '1' && String.valueOf(this.mVerification.getTestName()).toCharArray()[1] <= '4') {
            row = row15 + 1;
            canvas.drawText("ДСТУ EN ISO 4064", 115, row15 * 16, paint);
        } else {
            row = row15 + 1;
            canvas.drawText("ДСТУ 3580", 115, row15 * 16, paint);
        }
        canvas.drawText(String.format(Locale.ROOT, "%s", this.mVerification.getCounterNumber()), 115, row * 16, paint);
        canvas.drawText(String.format(Locale.ROOT, "%s", dnTypeFull2), 115, r12 * 16, paint);
        canvas.drawText(String.format(Locale.ROOT, "%d", Integer.valueOf(this.mVerification.getProductionYear())), 115, r9 * 16, paint);
        canvas.drawText(String.format(Locale.ROOT, "%05d", Integer.valueOf(this.mVerification.getInitialVolume())), 115, r12 * 16, paint);
        canvas.drawText(String.format(Locale.ROOT, "%d", Integer.valueOf(this.mVerification.getWaterTemperatureRound())), 115, r8 * 16, paint);
        canvas.drawText(String.format(Locale.ROOT, "%d", Integer.valueOf(this.mVerification.getEnvironmentT())), 115, r9 * 16, paint);
        int row16 = row + 1 + 1 + 1 + 1 + 1 + 1 + 1;
        canvas.drawText(String.format(Locale.ROOT, "%d", Integer.valueOf(this.mVerification.getEnvironmentH())), 115, r8 * 16, paint);
        if (measCounter != 3) {
            canvas.drawText(str, 115, row16 * 16, paint);
        } else {
            Locale locale7 = Locale.ROOT;
            Object[] objArr7 = new Object[1];
            objArr7[0] = resultValid == 0 ? str : resultValid == 1 ? getResources().getStringArray(R.array.result_status)[1] : getResources().getStringArray(R.array.result_status)[2];
            canvas.drawText(String.format(locale7, "%s", objArr7), 115, row16 * 16, paint);
        }
        Bitmap image7 = this.mVerification.getTestPhoto();
        Matrix matrix7 = new Matrix();
        matrix7.preTranslate(20.0f, 430.0f);
        matrix7.preScale(0.3f, 0.3f);
        canvas.drawBitmap(image7, matrix7, null);
        String metrologist = str2;
        try {
            File settings = new File(getFilesDir(), "settings.json");
            String text = MyFileReader.readAndroidFile(getFilesDir(), settings.getName());
            JSONObject jsonSettings = new JSONObject(text);
            str4 = str2;
            try {
                metrologist = jsonSettings.optString("metrologist", str4);
            } catch (JSONException e4) {
                e = e4;
                e.printStackTrace();
            }
        } catch (JSONException e5) {
            str4 = str2;
            e = e5;
        }
        if (!metrologist.trim().equals(str4)) {
            canvas.drawText(String.format(Locale.ROOT, getString(R.string.pdf_verification_metrologist), metrologist), 20.0f, 585.0f, paint);
        }
        document.finishPage(page);
        document.writeTo(fOut);
        document.close();
        Log("pdf saved");
    }

    private void createExcel(File path, String fileName) {
        String[] columns;
        try {
            Locale current = getResources().getConfiguration().locale;
            if (current.toString().equals("ru")) {
                columns = new String[]{"id", "Номер глобальный", "№ протокола", "ФИО", "Город", "Улица", "Дом", "Буква", "Корпус", "Квартира", "Буква", "Адрес", "№ пломбы", "Телефон осн.", "Телефон доп.", "Телефон доп2.", "E-mail", "Дата", "Время", "№ счетчика", "Тип услуги", "Комментарий", "Станция"};
            } else {
                columns = new String[]{"id", "Номер глобальний", "№ протоколу", "ПІБ", "Місто", "Вулиця", "Будинок", "Літера", "Корпус", "Квартира", "Літера", "Адреса", "№ пломби", "Телефон осн.", "Телефон дод.", "Телефон дод2.", "E-mail", "Дата", "Час", "№ лічильника", "Тип послуги", "Коментар", "Станція"};
            }
            try {
                File excelFile = new File(path, fileName + ".xls");
                Workbook workbook = new HSSFWorkbook();
                Sheet sheet = workbook.createSheet(fileName);
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns[i]);
                }
                FileOutputStream fileOut = new FileOutputStream(excelFile);
                workbook.write(fileOut);
                fileOut.close();
                workbook.close();
                Log("excel created");
            } catch (IOException e) {
                ex = e;
                ex.printStackTrace();
            } catch (EncryptedDocumentException e2) {
                ex = e2;
                ex.printStackTrace();
            }
        } catch (IOException | EncryptedDocumentException e3) {
            ex = e3;
        }
    }

    private void saveExcel(File path, String fileName, JSONObject json) {
        File excelFile = new File(path, fileName + ".xls");
        if (!excelFile.exists()) {
            createExcel(path, fileName);
        }
        try {
            FileInputStream inputStream = new FileInputStream(excelFile);
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getLastRowNum();
            Row row = sheet.createRow(rowCount + 1);
            int columnCount = 0;
            Iterator<String> itKeys = json.keys();
            ArrayList<Object> list = new ArrayList<>();
            while (itKeys.hasNext()) {
                String key = itKeys.next();
                try {
                    list.add(json.get(key));
                } catch (IOException e) {
                    ex = e;
                    ex.printStackTrace();
                    return;
                } catch (EncryptedDocumentException e2) {
                    ex = e2;
                    ex.printStackTrace();
                    return;
                } catch (InvalidFormatException e3) {
                    ex = e3;
                    ex.printStackTrace();
                    return;
                } catch (JSONException e4) {
                    ex = e4;
                    ex.printStackTrace();
                    return;
                }
            }
            for (Object field : list) {
                int columnCount2 = columnCount + 1;
                Cell cell = row.createCell(columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue(((Integer) field).intValue());
                }
                columnCount = columnCount2;
            }
            inputStream.close();
            FileOutputStream outputStream = new FileOutputStream(excelFile);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            Log("excel saved");
        } catch (IOException | EncryptedDocumentException | InvalidFormatException | JSONException e5) {
            ex = e5;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void Log(String message) {
        Log.d(TAG, message);
    }
}
