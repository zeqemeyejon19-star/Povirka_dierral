package com.poverka.httpFileClient.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.text.method.DigitsKeyListener;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import com.poverka.httpFileClient.R;
import com.poverka.httpFileClient.containers.Address;
import com.poverka.httpFileClient.containers.Town;
import com.poverka.httpFileClient.searchableSpinner.SearchableAdapter;
import com.poverka.httpFileClient.searchableSpinner.SearchableSpinner;
import com.poverka.httpFileClient.util.HttpFileClient;
import com.poverka.httpFileClient.util.MyFileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
class SettingsActivity {
    private static final float BUTTON_HEIGHT_RATIO = 0.11f;
    private static final float BUTTON_WIDTH_RATIO = 0.4f;
    private static final float MARGIN_HEIGHT_RATIO = 0.02f;
    private static final String TAG = "SettingsActivity";
    private static final float TEXT_L_HEIGHT_RATIO = 0.06f;
    private static final float TEXT_S_HEIGHT_RATIO = 0.035f;
    private static HttpFileClient httpFileClient;
    private static ProgressDialog mProgressDialog;
    private static int townID;
    private static String townName;
    private final Activity activity;
    private final Dialog dialog;
    private final HttpFileClient.OnMessageReceived mHttpReceived;
    private final MyHandler myHandler;
    private final View view;

    SettingsActivity(Activity activity) {
        HttpFileClient.OnMessageReceived onMessageReceived = new HttpFileClient.OnMessageReceived() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.25
            /* JADX WARN: Can't wrap try/catch for region: R(9:0|2|(3:(12:296|3|298|(5:299|5|(0)(4:8|9|10|11)|283|348)|14|305|15|16|(5:18|19|(0)(6:22|319|23|(2:25|26)|31|32)|283|348)(1:35)|36|37|315)|321|(3:125|(12:133|134|(3:310|136|(0)(9:318|139|316|140|(6:312|142|308|143|144|357)(9:151|152|341|153|154|339|155|156|356)|162|301|170|355))|179|328|180|(6:182|330|183|(0)(5:186|327|187|(1:189)(3:190|(1:192)(1:193)|194)|199)|283|348)(1:202)|203|325|(5:213|214|(2:225|(2:231|(2:237|(2:243|(2:249|(2:255|(2:261|(2:267|(2:275|(2:277|(2:280|281)))(2:272|273))(1:266))(1:260))(1:254))(1:248))(1:242))(1:236))(1:230))(3:303|219|224)|283|348)(2:208|209)|324|295)(3:130|131|354)|289)(11:76|336|77|332|(3:79|80|81)(1:(9:92|334|93|94|345|95|(4:97|98|343|99)(1:107)|108|353)(3:89|90|91))|331|345|95|(0)(0)|108|353))|(5:307|39|(0)(6:42|314|43|(1:45)(2:46|(2:51|(5:53|337|54|55|351))(1:50))|61|350)|294|347)|322|71|294|347|(1:(0))) */
            /* JADX WARN: Code restructure failed: missing block: B:287:0x07e8, code lost:
            
                r0 = e;
             */
            /* JADX WARN: Code restructure failed: missing block: B:288:0x07e9, code lost:
            
                r18 = com.poverka.httpFileClient.activity.SettingsActivity.TAG;
                r15 = "alert";
                r5 = r5;
             */
            /* JADX WARN: Multi-variable type inference failed */
            /* JADX WARN: Removed duplicated region for block: B:107:0x0381  */
            /* JADX WARN: Removed duplicated region for block: B:97:0x0339 A[Catch: IOException -> 0x0388, NameNotFoundException -> 0x0392, JSONException -> 0x0394, TRY_LEAVE, TryCatch #30 {NameNotFoundException -> 0x0392, IOException -> 0x0388, JSONException -> 0x0394, blocks: (B:95:0x0320, B:97:0x0339), top: B:345:0x0320 }] */
            /* JADX WARN: Type inference failed for: r10v5, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r10v7, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r11v32, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r12v26, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r18v20 */
            /* JADX WARN: Type inference failed for: r18v22 */
            /* JADX WARN: Type inference failed for: r18v23 */
            /* JADX WARN: Type inference failed for: r18v24 */
            /* JADX WARN: Type inference failed for: r18v25 */
            /* JADX WARN: Type inference failed for: r18v29 */
            /* JADX WARN: Type inference failed for: r26v0, types: [java.lang.String] */
            /* JADX WARN: Type inference failed for: r4v23, types: [android.os.Message] */
            /* JADX WARN: Type inference failed for: r4v31, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r4v36, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r4v41, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r4v54, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r4v65 */
            /* JADX WARN: Type inference failed for: r4v66 */
            /* JADX WARN: Type inference failed for: r4v67 */
            /* JADX WARN: Type inference failed for: r4v68 */
            /* JADX WARN: Type inference failed for: r4v69 */
            /* JADX WARN: Type inference failed for: r4v7 */
            /* JADX WARN: Type inference failed for: r4v70 */
            /* JADX WARN: Type inference failed for: r4v71 */
            /* JADX WARN: Type inference failed for: r4v72 */
            /* JADX WARN: Type inference failed for: r4v73 */
            /* JADX WARN: Type inference failed for: r4v74 */
            /* JADX WARN: Type inference failed for: r4v75 */
            /* JADX WARN: Type inference failed for: r4v76 */
            /* JADX WARN: Type inference failed for: r4v77 */
            /* JADX WARN: Type inference failed for: r4v8 */
            /* JADX WARN: Type inference failed for: r5v104 */
            /* JADX WARN: Type inference failed for: r5v107, types: [android.os.Message] */
            /* JADX WARN: Type inference failed for: r5v108 */
            /* JADX WARN: Type inference failed for: r5v11 */
            /* JADX WARN: Type inference failed for: r5v111 */
            /* JADX WARN: Type inference failed for: r5v114, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r5v12 */
            /* JADX WARN: Type inference failed for: r5v123 */
            /* JADX WARN: Type inference failed for: r5v124 */
            /* JADX WARN: Type inference failed for: r5v125 */
            /* JADX WARN: Type inference failed for: r5v126 */
            /* JADX WARN: Type inference failed for: r5v127 */
            /* JADX WARN: Type inference failed for: r5v128 */
            /* JADX WARN: Type inference failed for: r5v129 */
            /* JADX WARN: Type inference failed for: r5v13 */
            /* JADX WARN: Type inference failed for: r5v130 */
            /* JADX WARN: Type inference failed for: r5v131 */
            /* JADX WARN: Type inference failed for: r5v132 */
            /* JADX WARN: Type inference failed for: r5v133 */
            /* JADX WARN: Type inference failed for: r5v134 */
            /* JADX WARN: Type inference failed for: r5v135 */
            /* JADX WARN: Type inference failed for: r5v136 */
            /* JADX WARN: Type inference failed for: r5v137 */
            /* JADX WARN: Type inference failed for: r5v14 */
            /* JADX WARN: Type inference failed for: r5v15 */
            /* JADX WARN: Type inference failed for: r5v17 */
            /* JADX WARN: Type inference failed for: r5v19 */
            /* JADX WARN: Type inference failed for: r5v21 */
            /* JADX WARN: Type inference failed for: r5v22 */
            /* JADX WARN: Type inference failed for: r5v24 */
            /* JADX WARN: Type inference failed for: r5v25 */
            /* JADX WARN: Type inference failed for: r5v26, types: [java.lang.CharSequence] */
            /* JADX WARN: Type inference failed for: r5v27 */
            /* JADX WARN: Type inference failed for: r5v3, types: [android.os.Message] */
            /* JADX WARN: Type inference failed for: r5v33, types: [android.os.Message] */
            /* JADX WARN: Type inference failed for: r5v37, types: [android.os.Message] */
            /* JADX WARN: Type inference failed for: r5v4, types: [android.os.Message] */
            /* JADX WARN: Type inference failed for: r5v42, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r5v47, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r5v5 */
            /* JADX WARN: Type inference failed for: r5v52, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r5v57, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r5v62, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r5v67, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r5v7 */
            /* JADX WARN: Type inference failed for: r5v72, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r5v75 */
            /* JADX WARN: Type inference failed for: r5v8 */
            /* JADX WARN: Type inference failed for: r5v80, types: [android.os.Message] */
            /* JADX WARN: Type inference failed for: r5v86, types: [android.os.Message] */
            /* JADX WARN: Type inference failed for: r5v9 */
            /* JADX WARN: Type inference failed for: r5v92 */
            /* JADX WARN: Type inference failed for: r5v95, types: [android.os.Message] */
            /* JADX WARN: Type inference failed for: r5v96 */
            /* JADX WARN: Type inference failed for: r5v97 */
            /* JADX WARN: Type inference failed for: r5v99 */
            /* JADX WARN: Type inference failed for: r6v14, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r6v16 */
            /* JADX WARN: Type inference failed for: r6v17 */
            /* JADX WARN: Type inference failed for: r6v18 */
            /* JADX WARN: Type inference failed for: r6v19 */
            /* JADX WARN: Type inference failed for: r6v25, types: [android.os.Message] */
            /* JADX WARN: Type inference failed for: r6v27 */
            /* JADX WARN: Type inference failed for: r6v28 */
            /* JADX WARN: Type inference failed for: r6v5, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r6v54, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r6v72, types: [android.os.Message] */
            /* JADX WARN: Type inference failed for: r6v77, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r6v90, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            /* JADX WARN: Type inference failed for: r7v1 */
            /* JADX WARN: Type inference failed for: r7v10, types: [android.os.Bundle] */
            /* JADX WARN: Type inference failed for: r7v11 */
            /* JADX WARN: Type inference failed for: r7v12 */
            /* JADX WARN: Type inference failed for: r7v13 */
            /* JADX WARN: Type inference failed for: r7v14 */
            /* JADX WARN: Type inference failed for: r7v15 */
            /* JADX WARN: Type inference failed for: r7v16 */
            /* JADX WARN: Type inference failed for: r7v17 */
            /* JADX WARN: Type inference failed for: r7v18 */
            /* JADX WARN: Type inference failed for: r7v19 */
            /* JADX WARN: Type inference failed for: r7v2 */
            /* JADX WARN: Type inference failed for: r7v20 */
            /* JADX WARN: Type inference failed for: r7v21 */
            /* JADX WARN: Type inference failed for: r7v22 */
            /* JADX WARN: Type inference failed for: r7v23 */
            /* JADX WARN: Type inference failed for: r7v24 */
            /* JADX WARN: Type inference failed for: r7v25 */
            /* JADX WARN: Type inference failed for: r7v3, types: [android.os.Bundle] */
            /* JADX WARN: Type inference failed for: r7v4 */
            /* JADX WARN: Type inference failed for: r7v5 */
            /* JADX WARN: Type inference failed for: r8v16, types: [android.os.Message] */
            /* JADX WARN: Type inference failed for: r8v27, types: [com.poverka.httpFileClient.activity.SettingsActivity$MyHandler] */
            @Override // com.poverka.httpFileClient.util.HttpFileClient.OnMessageReceived
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public void messageReceived(com.poverka.httpFileClient.util.HttpFileClient.Type r25, java.lang.String r26, java.io.InputStream r27) {
                /*
                    Method dump skipped, instruction units count: 2085
                    To view this dump change 'Code comments level' option to 'DEBUG'
                */
                throw new UnsupportedOperationException("Method not decompiled: com.poverka.httpFileClient.activity.SettingsActivity.AnonymousClass25.messageReceived(com.poverka.httpFileClient.util.HttpFileClient$Type, java.lang.String, java.io.InputStream):void");
            }
        };
        this.mHttpReceived = onMessageReceived;
        this.activity = activity;
        View viewInflate = activity.getLayoutInflater().inflate(R.layout.activity_settings, (ViewGroup) null);
        this.view = viewInflate;
        Dialog dialog = new Dialog(activity, android.R.style.Theme.DeviceDefault.Light.NoActionBar);
        this.dialog = dialog;
        this.myHandler = new MyHandler(activity);
        httpFileClient = new HttpFileClient(onMessageReceived);
        mProgressDialog = new ProgressDialog(activity);
        initViews();
        dialog.setContentView(viewInflate);
        dialog.show();
    }

    private void initViews() {
        Switch switchDispatcher = (Switch) this.view.findViewById(R.id.switchDispatcher);
        Button buttonSetIP = (Button) this.view.findViewById(R.id.buttonSetIP);
        Button buttonSetEnvironment = (Button) this.view.findViewById(R.id.buttonSetEnvironment);
        Button buttonLanguage = (Button) this.view.findViewById(R.id.buttonSettingsLanguage);
        Button buttonLoadLocal = (Button) this.view.findViewById(R.id.buttonSettingsLoadLocal);
        Button buttonLoadServer = (Button) this.view.findViewById(R.id.buttonSettingsLoadServer);
        Button buttonLoadAddress = (Button) this.view.findViewById(R.id.buttonSettingsLoadAddress);
        Button buttonCheckApkUpdate = (Button) this.view.findViewById(R.id.buttonSettingsCheckApkUpdate);
        Button buttonSupport = (Button) this.view.findViewById(R.id.buttonSettingsSupport);
        Button buttonVersionInfo = (Button) this.view.findViewById(R.id.buttonSettingsVersionInfo);
        Button buttonMetrologistName = (Button) this.view.findViewById(R.id.buttonSettingsMetrologistName);
        Button buttonReSendProtocol = (Button) this.view.findViewById(R.id.buttonSettingsReSendProtocol);
        Button buttonPaymentAccount = (Button) this.view.findViewById(R.id.buttonSettingsPaymentAccount);
        DisplayMetrics metrics = new DisplayMetrics();
        this.activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;
        int buttonHeight = (int) (screenHeight * BUTTON_HEIGHT_RATIO);
        int buttonWidth = (int) (screenWidth * BUTTON_WIDTH_RATIO);
        int textSHeight = (int) (screenHeight * TEXT_S_HEIGHT_RATIO);
        int marginHeight = (int) (screenHeight * MARGIN_HEIGHT_RATIO);
        ConstraintLayout.LayoutParams paramsC = (ConstraintLayout.LayoutParams) switchDispatcher.getLayoutParams();
        paramsC.width = buttonWidth - 20;
        paramsC.topMargin = marginHeight * 2;
        switchDispatcher.requestLayout();
        ConstraintLayout.LayoutParams paramsC2 = (ConstraintLayout.LayoutParams) buttonLoadAddress.getLayoutParams();
        paramsC2.width = buttonWidth;
        paramsC2.height = buttonHeight;
        paramsC2.topMargin = marginHeight * 2;
        buttonLoadAddress.requestLayout();
        ConstraintLayout.LayoutParams paramsC3 = (ConstraintLayout.LayoutParams) buttonLoadLocal.getLayoutParams();
        paramsC3.width = buttonWidth;
        paramsC3.height = buttonHeight;
        paramsC3.topMargin = marginHeight;
        buttonLoadLocal.requestLayout();
        ConstraintLayout.LayoutParams paramsC4 = (ConstraintLayout.LayoutParams) buttonLoadServer.getLayoutParams();
        paramsC4.width = buttonWidth;
        paramsC4.height = buttonHeight;
        paramsC4.topMargin = marginHeight;
        buttonLoadServer.requestLayout();
        ConstraintLayout.LayoutParams paramsC5 = (ConstraintLayout.LayoutParams) buttonCheckApkUpdate.getLayoutParams();
        paramsC5.width = buttonWidth;
        paramsC5.height = buttonHeight;
        paramsC5.topMargin = marginHeight;
        buttonCheckApkUpdate.requestLayout();
        ConstraintLayout.LayoutParams paramsC6 = (ConstraintLayout.LayoutParams) buttonSupport.getLayoutParams();
        paramsC6.width = buttonWidth;
        paramsC6.height = buttonHeight;
        paramsC6.topMargin = marginHeight;
        buttonSupport.requestLayout();
        ConstraintLayout.LayoutParams paramsC7 = (ConstraintLayout.LayoutParams) buttonVersionInfo.getLayoutParams();
        paramsC7.width = buttonWidth;
        paramsC7.height = buttonHeight;
        paramsC7.topMargin = marginHeight;
        buttonVersionInfo.requestLayout();
        ConstraintLayout.LayoutParams paramsC8 = (ConstraintLayout.LayoutParams) buttonMetrologistName.getLayoutParams();
        paramsC8.width = buttonWidth;
        paramsC8.height = buttonHeight;
        paramsC8.topMargin = marginHeight;
        buttonMetrologistName.requestLayout();
        ConstraintLayout.LayoutParams paramsC9 = (ConstraintLayout.LayoutParams) buttonSetIP.getLayoutParams();
        paramsC9.width = buttonWidth;
        paramsC9.height = buttonHeight;
        paramsC9.topMargin = marginHeight;
        buttonSetIP.requestLayout();
        ConstraintLayout.LayoutParams paramsC10 = (ConstraintLayout.LayoutParams) buttonSetEnvironment.getLayoutParams();
        paramsC10.width = buttonWidth;
        paramsC10.height = buttonHeight;
        paramsC10.topMargin = marginHeight;
        buttonSetEnvironment.requestLayout();
        ConstraintLayout.LayoutParams paramsC11 = (ConstraintLayout.LayoutParams) buttonReSendProtocol.getLayoutParams();
        paramsC11.width = buttonWidth;
        paramsC11.height = buttonHeight;
        paramsC11.topMargin = marginHeight;
        buttonReSendProtocol.requestLayout();
        ConstraintLayout.LayoutParams paramsC12 = (ConstraintLayout.LayoutParams) buttonPaymentAccount.getLayoutParams();
        paramsC12.width = buttonWidth;
        paramsC12.height = buttonHeight;
        paramsC12.topMargin = marginHeight;
        buttonPaymentAccount.requestLayout();
        LoadSettings();
        buttonLoadLocal.setEnabled(false);
        switchDispatcher.setTextSize(0, textSHeight);
        buttonSetIP.setTextSize(0, textSHeight);
        buttonSetEnvironment.setTextSize(0, textSHeight);
        buttonLanguage.setTextSize(0, textSHeight);
        buttonLoadLocal.setTextSize(0, textSHeight);
        buttonLoadServer.setTextSize(0, textSHeight);
        buttonLoadAddress.setTextSize(0, textSHeight);
        buttonCheckApkUpdate.setTextSize(0, textSHeight);
        buttonSupport.setTextSize(0, textSHeight);
        buttonVersionInfo.setTextSize(0, textSHeight);
        buttonMetrologistName.setTextSize(0, textSHeight);
        buttonReSendProtocol.setTextSize(0, textSHeight);
        buttonPaymentAccount.setTextSize(0, textSHeight);
        switchDispatcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsActivity.this.DispatcherSwitched(isChecked);
            }
        });
        buttonSetIP.setOnClickListener(new View.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SettingsActivity.this.SetIP();
            }
        });
        buttonSetEnvironment.setOnClickListener(new View.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SettingsActivity.this.SetEnvironment();
            }
        });
        buttonLanguage.setOnClickListener(new View.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SettingsActivity.this.SelectLanguage();
            }
        });
        buttonLoadLocal.setOnClickListener(new View.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SettingsActivity.this.LoadFiles();
            }
        });
        buttonLoadServer.setOnClickListener(new View.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SettingsActivity.this.LoadServer();
            }
        });
        buttonLoadAddress.setOnClickListener(new View.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SettingsActivity.this.LoadAddress();
            }
        });
        buttonCheckApkUpdate.setOnClickListener(new View.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SettingsActivity.this.CheckApkUpdates();
            }
        });
        buttonSupport.setOnClickListener(new View.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.9
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SettingsActivity.this.Support();
            }
        });
        buttonVersionInfo.setOnClickListener(new View.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.10
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SettingsActivity.this.VersionInfoClicked();
            }
        });
        buttonMetrologistName.setOnClickListener(new View.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.11
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SettingsActivity.this.MetrologistNameClicked();
            }
        });
        buttonReSendProtocol.setOnClickListener(new View.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.12
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SettingsActivity.this.ReSendProtocol();
            }
        });
        buttonPaymentAccount.setOnClickListener(new View.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.13
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SettingsActivity.this.PaymentAccount();
            }
        });
        File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String[] list = downloads.list();
        int length = list.length;
        int i = 0;
        while (i < length) {
            int i2 = length;
            String file = list[i];
            String[] strArr = list;
            Button buttonSetEnvironment2 = buttonSetEnvironment;
            if (file.length() <= 6 || (!file.equals("types.json") && !file.equals("tests.json"))) {
                i++;
                length = i2;
                list = strArr;
                buttonSetEnvironment = buttonSetEnvironment2;
            } else {
                buttonLoadLocal.setEnabled(true);
                break;
            }
        }
        this.dialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.14
            @Override // android.content.DialogInterface.OnKeyListener
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                JSONObject jsonSettings;
                if (keyCode == 4 && event.getAction() == 1) {
                    File settings = new File(SettingsActivity.this.activity.getApplicationContext().getFilesDir(), "settings.json");
                    File address = new File(SettingsActivity.this.activity.getApplicationContext().getFilesDir(), "address.json");
                    Switch switchDispatcher2 = (Switch) SettingsActivity.this.view.findViewById(R.id.switchDispatcher);
                    if (switchDispatcher2.isChecked() && !address.exists()) {
                        Bundle bundle = new Bundle();
                        Message msg = SettingsActivity.this.myHandler.obtainMessage(2);
                        bundle.putString("tittle", SettingsActivity.this.activity.getString(R.string.alert));
                        bundle.putString("message", SettingsActivity.this.activity.getString(R.string.need_to_load_streets));
                        msg.setData(bundle);
                        SettingsActivity.this.myHandler.sendMessage(msg);
                    } else {
                        try {
                            if (settings.exists()) {
                                jsonSettings = new JSONObject(MyFileReader.readAndroidFile(SettingsActivity.this.activity.getFilesDir(), settings.getName()));
                            } else {
                                jsonSettings = new JSONObject();
                            }
                            int i3 = 0;
                            jsonSettings.put("local", switchDispatcher2.isChecked() ? 0 : 1);
                            MyFileReader.writeInternalFile(SettingsActivity.this.activity.getApplicationContext(), settings.getName(), jsonSettings.toString());
                            if (!switchDispatcher2.isChecked()) {
                                i3 = 1;
                            }
                            MainActivity.MARKER = i3;
                            SettingsActivity.this.dialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return true;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void DispatcherSwitched(boolean isChecked) {
        Button buttonLoadAddress = (Button) this.view.findViewById(R.id.buttonSettingsLoadAddress);
        if (isChecked) {
            buttonLoadAddress.setVisibility(0);
            try {
                File localTasks = new File(this.activity.getFilesDir(), "localTasks.json");
                if (!localTasks.exists()) {
                    if (localTasks.createNewFile()) {
                        FileWriter writer = new FileWriter(localTasks);
                        writer.write("[]");
                        writer.flush();
                        writer.close();
                    } else {
                        Toast.makeText(this.activity.getApplicationContext(), String.format(Locale.ROOT, this.activity.getString(R.string.error_file_creation), 301), 1).show();
                    }
                }
                return;
            } catch (IOException e) {
                Toast.makeText(this.activity.getApplicationContext(), String.format(Locale.ROOT, this.activity.getString(R.string.error_file_creation), 303), 1).show();
                return;
            }
        }
        buttonLoadAddress.setVisibility(8);
        MyFileReader.removeInternalFile(this.activity.getApplicationContext(), "address.json");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void Support() {
        try {
            File logFile = MyFileReader.getLogFile(this.activity);
            File settings = new File(this.activity.getFilesDir(), "settings.json");
            if (logFile.exists()) {
                if (!settings.exists()) {
                    Activity activity = this.activity;
                    Toast.makeText(activity, activity.getString(R.string.no_settings_file), 1).show();
                    return;
                } else {
                    Date curDate = Calendar.getInstance().getTime();
                    SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
                    String text = MyFileReader.readAndroidFile(this.activity.getFilesDir(), settings.getName());
                    JSONObject jsonSettings = new JSONObject(text);
                    httpFileClient.headUploadServer(String.format(Locale.ROOT, "logs/upload/1/%03d/%s", Integer.valueOf(jsonSettings.optInt("station")), format.format(curDate)), logFile);
                }
            } else {
                Log.d(TAG, "no log");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setTitle(this.activity.getResources().getString(R.string.support_title));
        builder.setItems(this.activity.getResources().getStringArray(R.array.support_items), new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.15
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builderItem = new AlertDialog.Builder(SettingsActivity.this.activity);
                if (which == 0) {
                    builderItem.setMessage(SettingsActivity.this.activity.getResources().getString(R.string.support_text_tel));
                } else if (which == 1) {
                    builderItem.setMessage(SettingsActivity.this.activity.getResources().getString(R.string.support_text_shipment));
                } else if (which == 2) {
                    builderItem.setView(R.layout.qr_youtube_dialog);
                }
                AlertDialog dialogItem = builderItem.create();
                dialogItem.show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SetIP() {
        boolean cameraChecked;
        boolean loggingChecked;
        final File settings = new File(this.activity.getApplicationContext().getFilesDir(), "settings.json");
        final Button buttonSetIP = (Button) this.view.findViewById(R.id.buttonSetIP);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setTitle(R.string.enter_ip);
        LinearLayout linearLayout = new LinearLayout(builder.getContext());
        final EditText editIP = new EditText(builder.getContext());
        final SwitchCompat switchCamera = new SwitchCompat(builder.getContext());
        final SwitchCompat switchLogging = new SwitchCompat(builder.getContext());
        boolean cameraChecked2 = false;
        boolean loggingChecked2 = false;
        try {
            if (settings.exists()) {
                JSONObject jsonSettings = new JSONObject(MyFileReader.readAndroidFile(this.activity.getFilesDir(), settings.getName()));
                cameraChecked2 = jsonSettings.optBoolean("photoType");
                loggingChecked2 = jsonSettings.optBoolean("writeLog");
            }
            cameraChecked = cameraChecked2;
            loggingChecked = loggingChecked2;
        } catch (JSONException e) {
            e.printStackTrace();
            cameraChecked = cameraChecked2;
            loggingChecked = false;
        }
        linearLayout.setGravity(1);
        linearLayout.setOrientation(1);
        editIP.setImeOptions(33554432);
        editIP.setHint(this.activity.getResources().getString(R.string.settings_button_default_ip));
        editIP.setInputType(2);
        editIP.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        editIP.setTextAlignment(4);
        editIP.setText(buttonSetIP.getText().toString());
        switchCamera.setText(R.string.switch_camera);
        switchLogging.setText(R.string.switch_logging);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
        lp.setMargins(25, 10, 25, 10);
        switchCamera.setLayoutParams(lp);
        switchCamera.setChecked(cameraChecked);
        switchLogging.setLayoutParams(lp);
        switchLogging.setChecked(loggingChecked);
        linearLayout.addView(editIP);
        linearLayout.addView(switchCamera);
        linearLayout.addView(switchLogging);
        builder.setView(linearLayout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.16
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                JSONObject jsonSettings2;
                String text = editIP.getText().toString();
                Pattern p = Pattern.compile("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
                Matcher m = p.matcher(text);
                if (!m.find()) {
                    Toast.makeText(SettingsActivity.this.activity.getApplicationContext(), SettingsActivity.this.activity.getString(R.string.wrong_ip_format), 0).show();
                    return;
                }
                try {
                    if (settings.exists()) {
                        jsonSettings2 = new JSONObject(MyFileReader.readAndroidFile(SettingsActivity.this.activity.getFilesDir(), settings.getName()));
                    } else {
                        jsonSettings2 = new JSONObject();
                    }
                    jsonSettings2.put("ip", text);
                    jsonSettings2.put("photoType", switchCamera.isChecked());
                    jsonSettings2.put("writeLog", switchLogging.isChecked());
                    MyFileReader.writeInternalFile(SettingsActivity.this.activity.getApplicationContext(), settings.getName(), jsonSettings2.toString());
                    MainActivity.IP = text;
                    buttonSetIP.setText(text);
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
            }
        });
        builder.create().show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SetEnvironment() {
        final File settings = new File(this.activity.getApplicationContext().getFilesDir(), "settings.json");
        try {
            if (settings.exists()) {
                final JSONObject jsonSettings = new JSONObject(MyFileReader.readAndroidFile(this.activity.getFilesDir(), settings.getName()));
                AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
                builder.setTitle(R.string.environment_measurement);
                LinearLayout linearLayout = new LinearLayout(builder.getContext());
                TextView textT = new TextView(builder.getContext());
                TextView textH = new TextView(builder.getContext());
                final EditText editT = new EditText(builder.getContext());
                final EditText editH = new EditText(builder.getContext());
                linearLayout.setGravity(1);
                textT.setText(R.string.temperature);
                textH.setText(R.string.humidity);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
                lp.setMargins(10, 0, 10, 0);
                editT.setWidth(100);
                editT.setLayoutParams(lp);
                editT.setImeOptions(33554432);
                editT.setInputType(2);
                editT.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                editT.setTextAlignment(4);
                editT.setText(String.valueOf(jsonSettings.optInt("environmentT", 22)));
                editH.setWidth(100);
                editT.setLayoutParams(lp);
                editH.setImeOptions(33554432);
                editH.setInputType(2);
                editH.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                editH.setTextAlignment(4);
                editH.setText(String.valueOf(jsonSettings.optInt("environmentH", 60)));
                linearLayout.addView(textT);
                linearLayout.addView(editT);
                linearLayout.addView(textH);
                linearLayout.addView(editH);
                builder.setView(linearLayout);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.17
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            jsonSettings.put("environmentT", editT.getText().toString());
                            jsonSettings.put("environmentH", editH.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        MyFileReader.writeInternalFile(SettingsActivity.this.activity.getApplicationContext(), settings.getName(), jsonSettings.toString());
                    }
                });
                builder.create().show();
            } else {
                Activity activity = this.activity;
                Toast.makeText(activity, activity.getString(R.string.need_to_set_ip), 1).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void LoadSettings() {
        Button buttonLoadAddress = (Button) this.view.findViewById(R.id.buttonSettingsLoadAddress);
        Button buttonSetIP = (Button) this.view.findViewById(R.id.buttonSetIP);
        Switch switchDispatcher = (Switch) this.view.findViewById(R.id.switchDispatcher);
        File settings = new File(this.activity.getApplicationContext().getFilesDir(), "settings.json");
        if (settings.exists()) {
            try {
                String text = MyFileReader.readAndroidFile(this.activity.getFilesDir(), settings.getName());
                JSONObject jsonSettings = new JSONObject(text);
                buttonSetIP.setText(jsonSettings.getString("ip"));
                boolean z = true;
                if (jsonSettings.optInt("local", 1) == 1) {
                    z = false;
                }
                switchDispatcher.setChecked(z);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            buttonSetIP.setText(MainActivity.IP);
            switchDispatcher.setChecked(false);
        }
        if (switchDispatcher.isChecked()) {
            buttonLoadAddress.setVisibility(0);
        } else {
            buttonLoadAddress.setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SelectLanguage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setTitle(this.activity.getString(R.string.settings_language_title));
        String[] languages = {this.activity.getString(R.string.settings_language_ua), this.activity.getString(R.string.settings_language_ru)};
        builder.setItems(languages, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.18
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                if (which == 1) {
                    SettingsActivity.this.setLocale("ru");
                } else {
                    SettingsActivity.this.setLocale("en");
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setLocale(String language) {
        File settings = new File(this.activity.getApplicationContext().getFilesDir(), "settings.json");
        try {
            if (settings.exists()) {
                JSONObject jsonSettings = new JSONObject(MyFileReader.readAndroidFile(this.activity.getFilesDir(), settings.getName()));
                jsonSettings.put("language", language);
                MyFileReader.writeInternalFile(this.activity.getApplicationContext(), settings.getName(), jsonSettings.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent refresh = new Intent(this.activity, (Class<?>) MainActivity.class);
        this.dialog.dismiss();
        this.activity.finish();
        this.activity.startActivity(refresh);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void LoadFiles() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File type = new File(dir, "types.json");
        File test = new File(dir, "tests.json");
        if (type.exists()) {
            String typeString = MyFileReader.readAndroidFile(dir, type.getName());
            MyFileReader.writeInternalFile(this.activity.getApplicationContext(), type.getName(), typeString);
            if (!type.delete()) {
                Log.e(TAG, String.format(Locale.ROOT, "can not delete file '%s' from 'Downloads'", type.getName()));
                Log.e(TAG, String.format(Locale.ROOT, "absolute path is '%s'", type.getAbsolutePath()));
            }
            new MyFileReader.UpdateStorage(this.activity, type);
            Toast.makeText(this.activity.getApplicationContext(), String.format(Locale.ROOT, this.activity.getString(R.string.file_loaded), type.getName()), 1).show();
        }
        if (test.exists()) {
            String testString = MyFileReader.readAndroidFile(dir, test.getName());
            MyFileReader.writeInternalFile(this.activity.getApplicationContext(), test.getName(), testString);
            if (!test.delete()) {
                Log.e(TAG, String.format(Locale.ROOT, "can not delete file '%s' from 'Downloads'", test.getName()));
                Log.e(TAG, String.format(Locale.ROOT, "absolute path is '%s'", test.getAbsolutePath()));
            }
            new MyFileReader.UpdateStorage(this.activity, test);
            Toast.makeText(this.activity.getApplicationContext(), String.format(Locale.ROOT, this.activity.getString(R.string.file_loaded), test.getName()), 1).show();
        }
        Button buttonLoadLocal = (Button) this.view.findViewById(R.id.buttonSettingsLoadLocal);
        buttonLoadLocal.setEnabled(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void LoadServer() {
        String auth = "station_head1:I8X_V..oe%OX";
        byte[] encodedAuth = Base64.encode(auth.getBytes(StandardCharsets.UTF_8), 0);
        String authHeaderValue = "Basic " + new String(encodedAuth);
        httpFileClient.downloadServer("device-types/download-simple?device-class=8", authHeaderValue);
        httpFileClient.downloadServer("ver-tests/download?equipment-type=1", authHeaderValue);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void LoadAddress() {
        try {
            File settings = new File(this.activity.getFilesDir(), "settings.json");
            if (!settings.exists()) {
                Activity activity = this.activity;
                Toast.makeText(activity, activity.getString(R.string.no_settings_file), 1).show();
                return;
            }
            String text = MyFileReader.readAndroidFile(this.activity.getFilesDir(), settings.getName());
            JSONObject jsonSettings = new JSONObject(text);
            if (!jsonSettings.has("station")) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.activity);
                alertDialogBuilder.setTitle(R.string.alert);
                alertDialogBuilder.setMessage(R.string.need_to_take_test_photo);
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.19
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.create().show();
                return;
            }
            String auth = "station_head1:I8X_V..oe%OX";
            byte[] encodedAuth = Base64.encode(auth.getBytes(StandardCharsets.UTF_8), 0);
            String authHeaderValue = "Basic " + new String(encodedAuth);
            String addressPath = String.format(Locale.ROOT, "getsprtown?equipmentType=1&serial=%03d", Integer.valueOf(jsonSettings.optInt("station")));
            httpFileClient.downloadServer(addressPath, authHeaderValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void LoadStreets(int id, int station) {
        String auth = "station_head1:I8X_V..oe%OX";
        byte[] encodedAuth = Base64.encode(auth.getBytes(StandardCharsets.UTF_8), 0);
        String authHeaderValue = "Basic " + new String(encodedAuth);
        String streetPath = String.format(Locale.ROOT, "getsprstreet?townId=%d&equipmentType=1&serial=%03d", Integer.valueOf(id), Integer.valueOf(station));
        httpFileClient.downloadServer(streetPath, authHeaderValue);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void CheckApkUpdates() {
        try {
            File settings = new File(this.activity.getFilesDir(), "settings.json");
            if (!settings.exists()) {
                Activity activity = this.activity;
                Toast.makeText(activity, activity.getString(R.string.no_settings_file), 1).show();
                return;
            }
            String text = MyFileReader.readAndroidFile(this.activity.getFilesDir(), settings.getName());
            JSONObject jsonSettings = new JSONObject(text);
            if (!jsonSettings.has("station")) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.activity);
                alertDialogBuilder.setTitle(R.string.alert);
                alertDialogBuilder.setMessage(R.string.need_to_take_test_photo);
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.20
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.create().show();
                return;
            }
            checkDisabledUpdate(jsonSettings.optInt("station"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void VersionInfoClicked() {
        try {
            PackageInfo pInfo = this.activity.getPackageManager().getPackageInfo(this.activity.getPackageName(), 0);
            AlertDialog.Builder builderSmall = new AlertDialog.Builder(this.activity);
            builderSmall.setTitle(String.format(Locale.ROOT, this.activity.getResources().getString(R.string.version_description_header), pInfo.versionName, this.activity.getResources().getString(R.string.lowest_firmware)));
            builderSmall.setMessage(R.string.whats_new_in_update);
            builderSmall.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.21
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialogSmall = builderSmall.create();
            dialogSmall.show();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void MetrologistNameClicked() {
        final File settings = new File(this.activity.getApplicationContext().getFilesDir(), "settings.json");
        if (!settings.exists()) {
            Activity activity = this.activity;
            Toast.makeText(activity, activity.getString(R.string.no_settings_file), 1).show();
            return;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.activity);
        alertDialogBuilder.setTitle(R.string.enter_metrologist_name);
        final EditText editName = new EditText(alertDialogBuilder.getContext());
        editName.setImeOptions(33554432);
        editName.setInputType(1);
        editName.setTextAlignment(4);
        try {
            if (settings.exists()) {
                JSONObject jsonSettings = new JSONObject(MyFileReader.readAndroidFile(this.activity.getFilesDir(), settings.getName()));
                if (jsonSettings.has("metrologist")) {
                    editName.setText(jsonSettings.getString("metrologist"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        alertDialogBuilder.setView(editName);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.22
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                JSONObject jsonSettings2;
                String text = editName.getText().toString();
                try {
                    if (settings.exists()) {
                        jsonSettings2 = new JSONObject(MyFileReader.readAndroidFile(SettingsActivity.this.activity.getFilesDir(), settings.getName()));
                    } else {
                        jsonSettings2 = new JSONObject();
                    }
                    jsonSettings2.put("metrologist", text);
                    MyFileReader.writeInternalFile(SettingsActivity.this.activity.getApplicationContext(), settings.getName(), jsonSettings2.toString());
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
            }
        });
        alertDialogBuilder.create().show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ReSendProtocol() {
        if (!checkMobileDataEnabled(this.activity)) {
            AlertDialog.Builder builderSmall = new AlertDialog.Builder(this.activity);
            builderSmall.setTitle(R.string.alert);
            builderSmall.setMessage(R.string.need_to_turn_on_mobile_data);
            builderSmall.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.23
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialogSmall = builderSmall.create();
            dialogSmall.show();
            return;
        }
        Button buttonSetIP = (Button) this.view.findViewById(R.id.buttonSetIP);
        MainActivity.IP = buttonSetIP.getText().toString();
        Intent reSendIntent = new Intent(this.activity, (Class<?>) ReSendProtocolActivity.class);
        this.activity.startActivity(reSendIntent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void PaymentAccount() {
        final File settings = new File(this.activity.getFilesDir(), "settings.json");
        if (!settings.exists()) {
            Activity activity = this.activity;
            Toast.makeText(activity, activity.getString(R.string.need_to_set_ip), 1).show();
            return;
        }
        View paymentView = this.activity.getLayoutInflater().inflate(R.layout.payment_account_dialog, (ViewGroup) null);
        TextView textStation = (TextView) paymentView.findViewById(R.id.textStation);
        final EditText editPaymentAccountCurrent = (EditText) paymentView.findViewById(R.id.editPaymentAccountCurrent);
        final Switch switchPaymentAccountGenerate = (Switch) paymentView.findViewById(R.id.switchPaymentAccountGenerate);
        try {
            final JSONObject jsonSettings = new JSONObject(MyFileReader.readAndroidFile(this.activity.getFilesDir(), settings.getName()));
            textStation.setText(String.format(Locale.ROOT, "%03d", Integer.valueOf(jsonSettings.optInt("station"))));
            int paymentAccountCurrent = jsonSettings.optInt("paymentAccountCurrent");
            if (paymentAccountCurrent != 0) {
                editPaymentAccountCurrent.setText(String.format(Locale.ROOT, "%05d", Integer.valueOf(paymentAccountCurrent)));
                switchPaymentAccountGenerate.setChecked(jsonSettings.optBoolean("paymentAccountGenerate"));
            }
            final AlertDialog dialogNew = new AlertDialog.Builder(this.activity).setView(paymentView).setTitle(R.string.settings_payment_account).setPositiveButton(android.R.string.ok, (DialogInterface.OnClickListener) null).setNegativeButton(R.string.dismiss, (DialogInterface.OnClickListener) null).create();
            dialogNew.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.24
                @Override // android.content.DialogInterface.OnShowListener
                public void onShow(DialogInterface dialogInterface) {
                    Button button = dialogNew.getButton(-1);
                    button.setOnClickListener(new View.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.24.1
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view) {
                            String paymentAccountCurrent2 = editPaymentAccountCurrent.getText().toString();
                            try {
                                if (paymentAccountCurrent2.length() == 0) {
                                    jsonSettings.put("paymentAccountCurrent", 0);
                                    jsonSettings.put("paymentAccountGenerate", false);
                                    MyFileReader.writeInternalFile(SettingsActivity.this.activity.getApplicationContext(), settings.getName(), jsonSettings.toString());
                                    dialogNew.dismiss();
                                } else {
                                    int number = Integer.parseInt(paymentAccountCurrent2);
                                    if (paymentAccountCurrent2.length() != 5) {
                                        Toast.makeText(SettingsActivity.this.activity, SettingsActivity.this.activity.getString(R.string.has_to_be_5_digit), 1).show();
                                    } else if (number < 1 || number > 99999) {
                                        Toast.makeText(SettingsActivity.this.activity, SettingsActivity.this.activity.getString(R.string.has_to_be_00001_99999), 1).show();
                                    } else {
                                        jsonSettings.put("paymentAccountCurrent", paymentAccountCurrent2);
                                        jsonSettings.put("paymentAccountGenerate", switchPaymentAccountGenerate.isChecked());
                                        MyFileReader.writeInternalFile(SettingsActivity.this.activity.getApplicationContext(), settings.getName(), jsonSettings.toString());
                                        dialogNew.dismiss();
                                    }
                                }
                            } catch (NumberFormatException e) {
                                Toast.makeText(SettingsActivity.this.activity, SettingsActivity.this.activity.getString(R.string.has_to_be_number), 1).show();
                            } catch (JSONException e2) {
                                e2.printStackTrace();
                                Toast.makeText(SettingsActivity.this.activity, SettingsActivity.this.activity.getString(R.string.error_message), 1).show();
                            }
                        }
                    });
                }
            });
            dialogNew.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static class MyHandler extends Handler {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private static final int ALERT = 1;
        private static final int ALERT_DIALOG = 2;
        private static final int APK = 4;
        private static final int PROMPT_APK_UPDATE = 3;
        private static final int SELECT_TOWN = 5;
        private final Activity activity;

        private MyHandler(Activity activity) {
            this.activity = activity;
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            Intent intent;
            Bundle bundle = msg.getData();
            int i = msg.what;
            if (i == 1) {
                if (bundle.getString("error") != null) {
                    Toast.makeText(this.activity.getApplicationContext(), bundle.getString("error"), 1).show();
                }
                if (bundle.getString("alert") != null) {
                    Toast.makeText(this.activity.getApplicationContext(), msg.getData().getString("alert"), 1).show();
                    return;
                }
                return;
            }
            if (i == 2) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.activity);
                alertDialogBuilder.setTitle(msg.getData().getString("tittle"));
                alertDialogBuilder.setMessage(msg.getData().getString("message"));
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.MyHandler.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.create().show();
                return;
            }
            if (i == 3) {
                if (bundle.getString("apk_info") != null) {
                    try {
                        JSONObject apkInfo = new JSONObject(msg.getData().getString("apk_info"));
                        final int id = apkInfo.getInt("id");
                        final String apkName = apkInfo.getString("fileName");
                        String versionNameNew = apkInfo.getString("version").split(":")[1];
                        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
                        builder.setTitle(R.string.tab_update);
                        builder.setMessage(String.format(Locale.ROOT, this.activity.getString(R.string.tab_update_available), versionNameNew));
                        builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.MyHandler.2
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog, int which) {
                                SettingsActivity.httpFileClient.headDownloadApk(id, apkName);
                            }
                        });
                        builder.setNegativeButton(R.string.postpone, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.MyHandler.3
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog stateDialog = builder.create();
                        stateDialog.show();
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                return;
            }
            if (i != 4) {
                if (i == 5) {
                    try {
                        DisplayMetrics metrics = new DisplayMetrics();
                        this.activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        int screenHeight = metrics.heightPixels;
                        final int textSHeight = (int) (screenHeight * SettingsActivity.TEXT_S_HEIGHT_RATIO);
                        final int textLHeight = (int) (screenHeight * SettingsActivity.TEXT_L_HEIGHT_RATIO);
                        final int marginHeight = (int) (screenHeight * SettingsActivity.MARGIN_HEIGHT_RATIO);
                        String NO_TOWN = this.activity.getString(R.string.text_no);
                        JSONArray townsJSON = new JSONArray(msg.getData().getString("townsJSON"));
                        ArrayList<Address> townList = new ArrayList<>();
                        for (int i2 = 0; i2 < townsJSON.length(); i2++) {
                            townList.add(new Address(townsJSON.getJSONObject(i2).getInt("ID"), townsJSON.getJSONObject(i2).getString("TOWN_NAME")));
                        }
                        Collections.sort(townList);
                        townList.add(0, new Town(-1, NO_TOWN));
                        SearchableAdapter adapterTown = new SearchableAdapter(this.activity, R.layout.spinner_item, townList) { // from class: com.poverka.httpFileClient.activity.SettingsActivity.MyHandler.5
                            @Override // android.widget.ArrayAdapter, android.widget.Adapter
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View v = super.getView(position, convertView, parent);
                                ((TextView) v).setTextSize(0, textSHeight);
                                ((TextView) v).setGravity(GravityCompat.START);
                                return v;
                            }

                            @Override // android.widget.ArrayAdapter, android.widget.BaseAdapter, android.widget.SpinnerAdapter
                            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                View v = super.getDropDownView(position, convertView, parent);
                                ((TextView) v).setTextSize(0, textLHeight);
                                int i3 = marginHeight;
                                v.setPadding(i3, i3, 0, 0);
                                ((TextView) v).setGravity(GravityCompat.START);
                                return v;
                            }
                        };
                        AlertDialog.Builder builderTownSelector = new AlertDialog.Builder(this.activity);
                        builderTownSelector.setTitle(R.string.select_city_address);
                        final SearchableSpinner spinnerTown = new SearchableSpinner(builderTownSelector.getContext());
                        spinnerTown.setPadding(20, 20, 0, 0);
                        spinnerTown.setTitle(this.activity.getString(R.string.select_city_address));
                        spinnerTown.setPositiveButton("");
                        spinnerTown.setAdapter((SpinnerAdapter) adapterTown);
                        builderTownSelector.setView(spinnerTown);
                        builderTownSelector.setPositiveButton(R.string.select, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.MyHandler.6
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog, int which) {
                                int unused = SettingsActivity.townID = ((Address) spinnerTown.getSelectedItem()).getId();
                                String unused2 = SettingsActivity.townName = ((Address) spinnerTown.getSelectedItem()).getName();
                                if (SettingsActivity.townID == -1) {
                                    MyFileReader.removeInternalFile(MyHandler.this.activity.getApplicationContext(), "address.json");
                                    return;
                                }
                                try {
                                    File settings = new File(MyHandler.this.activity.getFilesDir(), "settings.json");
                                    String text = MyFileReader.readAndroidFile(MyHandler.this.activity.getFilesDir(), settings.getName());
                                    JSONObject jsonSettings = new JSONObject(text);
                                    int station = jsonSettings.optInt("station");
                                    if (jsonSettings.has("station")) {
                                        SettingsActivity.LoadStreets(SettingsActivity.townID, station);
                                    } else {
                                        Toast.makeText(MyHandler.this.activity, MyHandler.this.activity.getString(R.string.unknown_station), 1).show();
                                    }
                                } catch (JSONException e2) {
                                    Toast.makeText(MyHandler.this.activity, String.format(Locale.ROOT, MyHandler.this.activity.getString(R.string.error_message), e2.getMessage()), 1).show();
                                    e2.printStackTrace();
                                }
                            }
                        });
                        builderTownSelector.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.MyHandler.7
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builderTownSelector.show();
                        return;
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                        return;
                    }
                }
                return;
            }
            if (bundle.getString(NotificationCompat.CATEGORY_PROGRESS) != null) {
                String progressString = msg.getData().getString(NotificationCompat.CATEGORY_PROGRESS);
                try {
                    if (progressString == null) {
                        throw new AssertionError();
                    }
                    int progress = Integer.parseInt(progressString);
                    SettingsActivity.Log("Str " + progressString + " int " + progress);
                    if (progress != -1) {
                        SettingsActivity.mProgressDialog.setIndeterminate(false);
                        SettingsActivity.mProgressDialog.setMax(100);
                        SettingsActivity.mProgressDialog.setProgress(progress);
                    } else {
                        SettingsActivity.mProgressDialog.setMessage(this.activity.getString(R.string.downloading_update));
                        SettingsActivity.mProgressDialog.setIndeterminate(true);
                        SettingsActivity.mProgressDialog.setProgressStyle(1);
                        SettingsActivity.mProgressDialog.setCancelable(false);
                        SettingsActivity.mProgressDialog.show();
                    }
                } catch (NumberFormatException e3) {
                    SettingsActivity.mProgressDialog.dismiss();
                    try {
                        File toInstall = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), progressString);
                        if (Build.VERSION.SDK_INT >= 24) {
                            Uri apkUri = FileProvider.getUriForFile(this.activity, "com.poverka.httpFileClient.fileprovider", toInstall);
                            intent = new Intent("android.intent.action.INSTALL_PACKAGE");
                            intent.setData(apkUri);
                            intent.setFlags(1);
                        } else {
                            Uri apkUri2 = Uri.fromFile(toInstall);
                            intent = new Intent("android.intent.action.VIEW");
                            intent.setDataAndType(apkUri2, "application/vnd.android.package-archive");
                            intent.setFlags(268435456);
                        }
                        this.activity.startActivity(intent);
                    } catch (Exception e4) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(this.activity);
                        builder2.setTitle(R.string.tab_update);
                        builder2.setMessage(String.format(Locale.ROOT, this.activity.getString(R.string.update_successfully_downloaded), progressString));
                        builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.SettingsActivity.MyHandler.4
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog stateDialog2 = builder2.create();
                        stateDialog2.show();
                    }
                }
            }
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

    private void checkDisabledUpdate(int station) {
        String path = String.format(Locale.ROOT, "stations/state/1/%03d?ver=2", Integer.valueOf(station));
        httpFileClient.headDownloadServer(path);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkApkUpdate() {
        try {
            File settings = new File(this.activity.getFilesDir(), "settings.json");
            String text = MyFileReader.readAndroidFile(this.activity.getFilesDir(), settings.getName());
            JSONObject jsonSettings = new JSONObject(text);
            if (jsonSettings.has("station")) {
                String apkLast = String.format(Locale.ROOT, "apk/last?equipment-type=1&serial=%03d", Integer.valueOf(jsonSettings.optInt("station")));
                httpFileClient.headDownloadServer(apkLast);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void Log(String message) {
        Log.d(TAG, message);
    }
}
