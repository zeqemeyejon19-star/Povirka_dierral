package com.poverka.httpFileClient.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import com.poverka.httpFileClient.R;
import com.poverka.httpFileClient.util.HttpFileClient;
import com.poverka.httpFileClient.util.MyFileReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Locale;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class MainActivity extends AppCompatActivity implements HttpFileClient.OnMessageReceived {
    private static final float BUTTON_HEIGHT_RATIO = 0.17f;
    private static final float BUTTON_WIDTH_RATIO = 0.4f;
    public static final boolean D = true;
    private static final float MARGIN_HEIGHT_RATIO = 0.03f;
    private static final float MARGIN_WIDTH_RATIO = 0.03f;
    public static final int MARKER_DISPATCHER = 0;
    public static final int MARKER_LOCAL = 1;
    public static final int REQUEST_DELAY = 500;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String TAG = "MainActivity";
    private static final float TEXT_L_HEIGHT_RATIO = 0.065f;
    private static final float TEXT_S_HEIGHT_RATIO = 0.03f;
    private int station;
    public static String IP = "192.168.43.140";
    public static int MARKER = 0;
    private static final String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws JSONException {
        super.onCreate(savedInstanceState);
        setLanguage();
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        View backgroundImage = findViewById(R.id.main);
        backgroundImage.getBackground().setAlpha(110);
        initViews();
        showChanges();
        checkStationCity();
        Button b = (Button) findViewById(R.id.button);
        b.setVisibility(8);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            updateTextViews();
        }
    }

    public void SettingsClicked(View v) {
        new SettingsActivity(this);
    }

    public void StartClicked(View v) {
        File settings = new File(getApplicationContext().getFilesDir(), "settings.json");
        File tests = new File(getApplicationContext().getFilesDir(), "tests.json");
        File types = new File(getApplicationContext().getFilesDir(), "types.json");
        if (settings.exists()) {
            if (!tests.exists()) {
                Toast.makeText(getApplicationContext(), getString(R.string.tests_not_found), 1).show();
                return;
            }
            if (!types.exists()) {
                Toast.makeText(getApplicationContext(), getString(R.string.types_not_found), 1).show();
                return;
            }
            try {
                MyFileReader.appendLog(getApplicationContext(), "Main", "Start click");
                String text = MyFileReader.readAndroidFile(getFilesDir(), settings.getName());
                JSONObject jsonSettings = new JSONObject(text);
                IP = jsonSettings.getString("ip");
                int photoType = jsonSettings.optBoolean("photoType") ? 1 : 0;
                int writeLog = jsonSettings.optBoolean("writeLog") ? 1 : 0;
                Intent myIntent = new Intent(getApplicationContext(), (Class<?>) StateActivity.class);
                myIntent.putExtra("local", true);
                myIntent.putExtra("selectedId", -1);
                myIntent.putExtra("photoType", photoType);
                myIntent.putExtra("writeLog", writeLog);
                startActivity(myIntent);
                return;
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
        new SettingsActivity(this);
    }

    public void TasksClicked(View v) {
        try {
            File settings = new File(getApplicationContext().getFilesDir(), "settings.json");
            String text = MyFileReader.readAndroidFile(getFilesDir(), settings.getName());
            JSONObject jsonSettings = new JSONObject(text);
            IP = jsonSettings.getString("ip");
            int writeLog = 1;
            int photoType = jsonSettings.optBoolean("photoType") ? 1 : 0;
            if (!jsonSettings.optBoolean("writeLog")) {
                writeLog = 0;
            }
            if (!jsonSettings.has("station")) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.alert);
                alertDialogBuilder.setMessage(R.string.need_to_take_test_photo);
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.MainActivity.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.create().show();
            } else {
                Intent taskViewerActivity = new Intent(this, (Class<?>) TaskViewerActivity.class);
                taskViewerActivity.putExtra("photoType", photoType);
                taskViewerActivity.putExtra("writeLog", writeLog);
                startActivity(taskViewerActivity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void TempButtonClicked(View v) {
    }

    @Override // com.poverka.httpFileClient.util.HttpFileClient.OnMessageReceived
    public void messageReceived(HttpFileClient.Type type, String fileName, InputStream stream) {
        try {
            if (fileName.contains("stations/city") && type == HttpFileClient.Type.SERVER && stream != null) {
                String serverString = HttpFileClient.inputStreamToString(stream);
                String stationCity = "";
                if (!TextUtils.isEmpty(serverString)) {
                    JSONObject jsonObject = new JSONObject(serverString);
                    stationCity = jsonObject.optString("name");
                }
                Log(stationCity);
                File settingsFile = new File(getFilesDir(), "settings.json");
                String text = MyFileReader.readAndroidFile(getFilesDir(), settingsFile.getName());
                JSONObject jsonSettings = new JSONObject(text);
                jsonSettings.put("stationCity", stationCity);
                MyFileReader.writeInternalFile(getApplicationContext(), settingsFile.getName(), jsonSettings.toString());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void setLanguage() throws JSONException {
        String language = "en";
        File settingsFile = new File(getFilesDir(), "settings.json");
        if (settingsFile.exists()) {
            try {
                JSONObject settingsJSON = new JSONObject(MyFileReader.readAndroidFile(getFilesDir(), settingsFile.getName()));
                if (settingsJSON.has("language")) {
                    language = settingsJSON.getString("language");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Locale locale = new Locale(language);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
    }

    private void initViews() {
        ImageButton buttonSettings = (ImageButton) findViewById(R.id.buttonSettings);
        Button buttonStart = (Button) findViewById(R.id.buttonMainStart);
        Button buttonTasks = (Button) findViewById(R.id.buttonMainTasks);
        TextView textDispatcher = (TextView) findViewById(R.id.textDispatcher);
        TextView textLoadedCity = (TextView) findViewById(R.id.textLoadedCity);
        TextView textEnvironment = (TextView) findViewById(R.id.textEnvironmentSettings);
        TextView textVersion = (TextView) findViewById(R.id.textVersion);
        TextView textStationCity = (TextView) findViewById(R.id.textStationCity);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;
        int buttonHeight = (int) (screenHeight * BUTTON_HEIGHT_RATIO);
        int buttonWidth = (int) (screenWidth * BUTTON_WIDTH_RATIO);
        int textLHeight = (int) (screenHeight * TEXT_L_HEIGHT_RATIO);
        int textSHeight = (int) (screenHeight * 0.03f);
        int screenHeight2 = (int) (screenHeight * 0.03f);
        int screenWidth2 = (int) (screenWidth * 0.03f);
        ConstraintLayout.LayoutParams paramsC = (ConstraintLayout.LayoutParams) buttonStart.getLayoutParams();
        paramsC.topMargin = screenHeight2;
        paramsC.rightMargin = screenWidth2;
        paramsC.height = buttonHeight;
        paramsC.width = buttonWidth;
        buttonStart.requestLayout();
        ConstraintLayout.LayoutParams paramsC2 = (ConstraintLayout.LayoutParams) buttonTasks.getLayoutParams();
        paramsC2.topMargin = screenHeight2;
        paramsC2.leftMargin = screenWidth2;
        paramsC2.height = buttonHeight;
        paramsC2.width = buttonWidth;
        buttonTasks.requestLayout();
        ConstraintLayout.LayoutParams paramsC3 = (ConstraintLayout.LayoutParams) buttonSettings.getLayoutParams();
        paramsC3.height = buttonHeight;
        paramsC3.width = buttonHeight;
        buttonSettings.requestLayout();
        textDispatcher.setTextSize(0, textSHeight);
        textLoadedCity.setTextSize(0, textSHeight);
        textEnvironment.setTextSize(0, textSHeight);
        textVersion.setTextSize(0, textSHeight);
        textStationCity.setTextSize(0, textLHeight);
        buttonStart.setTextSize(0, textLHeight);
        buttonTasks.setTextSize(0, textLHeight);
        if (Build.VERSION.SDK_INT >= 23) {
            textStationCity.setTextColor(getColor(R.color.colorPrimaryDark));
        }
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            textVersion.setText(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showChanges() {
        File settings;
        try {
            settings = new File(getApplicationContext().getFilesDir(), "settings.json");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (settings.exists()) {
            String text = MyFileReader.readAndroidFile(getFilesDir(), settings.getName());
            JSONObject jsonSettings = new JSONObject(text);
            try {
                this.station = jsonSettings.optInt("station");
                PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
                String curVersion = pInfo.versionName;
                String writtenVersion = jsonSettings.optString("version", "");
                if (!curVersion.equals(writtenVersion)) {
                    AlertDialog.Builder builderSmall = new AlertDialog.Builder(this);
                    builderSmall.setTitle(String.format(Locale.ROOT, getResources().getString(R.string.version_description_header), pInfo.versionName, getResources().getString(R.string.lowest_firmware)));
                    builderSmall.setMessage(R.string.whats_new_in_update);
                    builderSmall.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.MainActivity.2
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialogSmall = builderSmall.create();
                    dialogSmall.show();
                    jsonSettings.put("version", curVersion);
                    MyFileReader.writeInternalFile(getApplicationContext(), settings.getName(), jsonSettings.toString());
                    return;
                }
                return;
            } catch (PackageManager.NameNotFoundException e2) {
                e2.printStackTrace();
                return;
            }
            e.printStackTrace();
        }
    }

    public void updateTextViews() {
        try {
            Button buttonStart = (Button) findViewById(R.id.buttonMainStart);
            Button buttonTasks = (Button) findViewById(R.id.buttonMainTasks);
            TextView textDispatcher = (TextView) findViewById(R.id.textDispatcher);
            TextView textLoadedCity = (TextView) findViewById(R.id.textLoadedCity);
            TextView textEnvironment = (TextView) findViewById(R.id.textEnvironmentSettings);
            TextView textStationCity = (TextView) findViewById(R.id.textStationCity);
            File settingsFile = new File(getFilesDir(), "settings.json");
            File addressFile = new File(getFilesDir(), "address.json");
            if (settingsFile.exists()) {
                JSONObject settingsJSON = new JSONObject(MyFileReader.readAndroidFile(getFilesDir(), settingsFile.getName()));
                if (settingsJSON.optInt("local", 1) == 1) {
                    textDispatcher.setText("");
                    MARKER = 1;
                    buttonStart.setText(getResources().getString(R.string.main_button_start_local));
                    buttonTasks.setEnabled(false);
                } else {
                    textDispatcher.setText(getResources().getString(R.string.main_view_dispatcher));
                    MARKER = 0;
                    buttonStart.setText(getResources().getString(R.string.main_button_start_dispatcher));
                    buttonTasks.setEnabled(true);
                }
                int temperature = settingsJSON.optInt("environmentT", 22);
                int humidity = settingsJSON.optInt("environmentH", 60);
                textEnvironment.setText(String.format(Locale.ROOT, "%s %d°C, %d%%", getResources().getString(R.string.main_view_environment_settings), Integer.valueOf(temperature), Integer.valueOf(humidity)));
                textStationCity.setText(settingsJSON.optString("stationCity"));
            } else {
                textDispatcher.setText("");
                MARKER = 1;
                buttonStart.setText(getResources().getString(R.string.main_button_start_local));
                buttonTasks.setEnabled(false);
                textEnvironment.setText(String.format(Locale.ROOT, "%s %d°C, %d%%", getResources().getString(R.string.main_view_environment_settings), 22, 60));
            }
            if (addressFile.exists()) {
                JSONObject addressJSON = new JSONObject(MyFileReader.readAndroidFile(getFilesDir(), addressFile.getName()));
                textLoadedCity.setText(String.format(Locale.ROOT, "%s %s", getResources().getString(R.string.main_view_loaded_city), addressJSON.getString("city_name")));
            } else {
                textLoadedCity.setText(String.format(Locale.ROOT, "%s %s", getResources().getString(R.string.main_view_loaded_city), getResources().getString(R.string.text_no)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkStationCity() {
        if (this.station > 0) {
            String path = String.format(Locale.ROOT, "stations/city/1/%03d", Integer.valueOf(this.station));
            HttpFileClient httpFileClient = new HttpFileClient(this);
            httpFileClient.headDownloadServer(path);
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != 0) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, 1);
        }
    }

    private static void disableCertificateVerification() {
        TrustManager[] trustAllCerts = {new X509TrustManager() { // from class: com.poverka.httpFileClient.activity.MainActivity.3
            @Override // javax.net.ssl.X509TrustManager
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override // javax.net.ssl.X509TrustManager
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override // javax.net.ssl.X509TrustManager
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier allHostsValid = new HostnameVerifier() { // from class: com.poverka.httpFileClient.activity.MainActivity.4
                @Override // javax.net.ssl.HostnameVerifier
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void Log(String message) {
        Log.d(TAG, message);
    }
}
