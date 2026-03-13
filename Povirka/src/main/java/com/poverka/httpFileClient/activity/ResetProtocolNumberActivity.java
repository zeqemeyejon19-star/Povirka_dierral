package com.poverka.httpFileClient.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.poverka.httpFileClient.R;
import com.poverka.httpFileClient.util.HttpFileClient;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes2.dex */
public class ResetProtocolNumberActivity extends AppCompatActivity {
    private static final float BUTTON_HEIGHT_RATIO = 0.15f;
    private static final float BUTTON_WIDTH_RATIO = 0.45f;
    private static final float MARGIN_HEIGHT_RATIO = 0.03f;
    private static final float MARGIN_WIDTH_RATIO = 0.03f;
    private static final String TAG = "ReSendProtocolActivity";
    private static final float TEXT_L_HEIGHT_RATIO = 0.065f;
    private static final float TEXT_S_HEIGHT_RATIO = 0.05f;
    private HttpFileClient mHttpFileClient;
    public HttpFileClient.OnMessageReceived mHttpReceived = new HttpFileClient.OnMessageReceived() { // from class: com.poverka.httpFileClient.activity.ResetProtocolNumberActivity.1
        @Override // com.poverka.httpFileClient.util.HttpFileClient.OnMessageReceived
        public void messageReceived(HttpFileClient.Type type, String fileName, InputStream stream) {
            String input;
            Message msg = ResetProtocolNumberActivity.this.mUiHandler.obtainMessage(0);
            Bundle bundle = new Bundle();
            try {
                if (fileName.contains("archive") && type == HttpFileClient.Type.DELETE && stream != null) {
                    if (HttpFileClient.inputStreamToString(stream).contains("<tr><td>")) {
                        Log.d(ResetProtocolNumberActivity.TAG, "успешно");
                        bundle.putString("alert", ResetProtocolNumberActivity.this.getString(R.string.success));
                        msg.setData(bundle);
                        ResetProtocolNumberActivity.this.mUiHandler.sendMessage(msg);
                    }
                    return;
                }
                if (fileName.contains("archive") && type == HttpFileClient.Type.DELETE && stream == null) {
                    Log.d(ResetProtocolNumberActivity.TAG, "протокол не найден");
                    bundle.putString("alert", ResetProtocolNumberActivity.this.getString(R.string.protocol_not_found));
                    msg.setData(bundle);
                    ResetProtocolNumberActivity.this.mUiHandler.sendMessage(msg);
                    return;
                }
                if (fileName.contains("archive") && type == HttpFileClient.Type.SHOW && stream != null) {
                    String input2 = HttpFileClient.inputStreamToString(stream);
                    boolean match = false;
                    String[] list = input2.split("\n");
                    Pattern p = Pattern.compile("\\d.cmt");
                    int length = list.length;
                    int i = 0;
                    while (i < length) {
                        String file = list[i];
                        if (!p.matcher(file).find()) {
                            input = input2;
                        } else {
                            input = input2;
                            String path = String.format(Locale.ROOT, "%s/%s", fileName, file);
                            Log.d(ResetProtocolNumberActivity.TAG, String.format(Locale.ROOT, "Deleting: %s", path));
                            ResetProtocolNumberActivity.this.mHttpFileClient.delete(path);
                            match = true;
                        }
                        i++;
                        input2 = input;
                    }
                    if (!match) {
                        Log.d(ResetProtocolNumberActivity.TAG, "счетчик сброшен");
                        bundle.putString("alert", ResetProtocolNumberActivity.this.getString(R.string.success));
                        msg.setData(bundle);
                        ResetProtocolNumberActivity.this.mUiHandler.sendMessage(msg);
                        return;
                    }
                    return;
                }
                if (fileName.contains("archive") && type == HttpFileClient.Type.SHOW && stream == null) {
                    Log.d(ResetProtocolNumberActivity.TAG, "протокол не найден");
                    bundle.putString("alert", ResetProtocolNumberActivity.this.getString(R.string.protocol_not_found));
                    msg.setData(bundle);
                    ResetProtocolNumberActivity.this.mUiHandler.sendMessage(msg);
                }
            } catch (IOException e) {
                Log.e(ResetProtocolNumberActivity.TAG, "Error in HTTP receiver", e);
                Log.d(ResetProtocolNumberActivity.TAG, "ошибка");
                bundle.putString("alert", String.format(Locale.ROOT, ResetProtocolNumberActivity.this.getString(R.string.error_message), ""));
                msg.setData(bundle);
                ResetProtocolNumberActivity.this.mUiHandler.sendMessage(msg);
            }
        }
    };
    private UiHandler mUiHandler;

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_protocol_number);
        this.mHttpFileClient = new HttpFileClient(this.mHttpReceived);
        this.mUiHandler = new UiHandler(this);
        initViews();
    }

    public void ResetCounterClicked(View v) {
        resetCounter();
    }

    public void ManualClicked(View v) {
        showManual();
    }

    private void resetCounter() {
        EditText editResetDate = (EditText) findViewById(R.id.editResetDate);
        EditText editResetNumber = (EditText) findViewById(R.id.editResetNumber);
        if (editResetDate.getText().toString().length() == 0 || editResetNumber.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.select_date_and_number_of_protocol), 1).show();
            return;
        }
        String date = editResetDate.getText().toString().replace(".", "");
        try {
            Integer.parseInt(date);
            if (date.length() != 8) {
                throw new NumberFormatException();
            }
            try {
                int number = Integer.parseInt(editResetNumber.getText().toString());
                if (number < 1) {
                    Toast.makeText(getApplicationContext(), getString(R.string.protocol_number_has_to_be_grater_0), 1).show();
                } else {
                    String path = String.format(Locale.ROOT, "1/archive/%s/%d", date, Integer.valueOf(number));
                    this.mHttpFileClient.show(path);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.protocol_number_has_to_be_numeric), 1).show();
            }
        } catch (NumberFormatException e2) {
            Toast.makeText(getApplicationContext(), getString(R.string.wrong_date_format), 1).show();
        }
    }

    private void showManual() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.manual_reset_protocol_title);
        builder.setMessage(R.string.manual_reset_protocol_message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.ResetProtocolNumberActivity.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void initViews() {
        TextView textReset = (TextView) findViewById(R.id.textReset);
        TextView textDate = (TextView) findViewById(R.id.textViewDate);
        TextView textNumber = (TextView) findViewById(R.id.textViewNumber);
        EditText editDate = (EditText) findViewById(R.id.editResetDate);
        EditText editNumber = (EditText) findViewById(R.id.editResetNumber);
        Button buttonSetCounter = (Button) findViewById(R.id.buttonResetCounter);
        Button buttonManual = (Button) findViewById(R.id.buttonManual);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;
        int buttonHeight = (int) (screenHeight * BUTTON_HEIGHT_RATIO);
        int buttonWidth = (int) (screenWidth * BUTTON_WIDTH_RATIO);
        int textLHeight = (int) (screenHeight * TEXT_L_HEIGHT_RATIO);
        int textSHeight = (int) (screenHeight * TEXT_S_HEIGHT_RATIO);
        int marginHeight = (int) (screenHeight * 0.03f);
        int marginWidth = (int) (screenWidth * 0.03f);
        ConstraintLayout.LayoutParams paramsC = (ConstraintLayout.LayoutParams) editDate.getLayoutParams();
        paramsC.topMargin = marginHeight * 5;
        paramsC.rightMargin = marginWidth;
        paramsC.width = buttonWidth / 2;
        editDate.requestLayout();
        ConstraintLayout.LayoutParams paramsC2 = (ConstraintLayout.LayoutParams) editNumber.getLayoutParams();
        paramsC2.topMargin = marginHeight * 5;
        paramsC2.leftMargin = marginWidth;
        paramsC2.width = buttonWidth / 2;
        editNumber.requestLayout();
        ConstraintLayout.LayoutParams paramsC3 = (ConstraintLayout.LayoutParams) textDate.getLayoutParams();
        paramsC3.bottomMargin = marginHeight;
        paramsC3.rightMargin = marginWidth;
        textDate.requestLayout();
        ConstraintLayout.LayoutParams paramsC4 = (ConstraintLayout.LayoutParams) textNumber.getLayoutParams();
        paramsC4.bottomMargin = marginHeight;
        paramsC4.leftMargin = marginWidth;
        textNumber.requestLayout();
        ConstraintLayout.LayoutParams paramsC5 = (ConstraintLayout.LayoutParams) buttonSetCounter.getLayoutParams();
        paramsC5.topMargin = marginHeight;
        paramsC5.width = buttonWidth;
        paramsC5.height = buttonHeight;
        buttonSetCounter.requestLayout();
        ConstraintLayout.LayoutParams paramsC6 = (ConstraintLayout.LayoutParams) buttonManual.getLayoutParams();
        paramsC6.topMargin = marginHeight;
        paramsC6.width = buttonWidth;
        paramsC6.height = buttonHeight;
        buttonManual.requestLayout();
        textReset.setTextSize(0, textLHeight);
        textDate.setTextSize(0, textSHeight);
        textNumber.setTextSize(0, textSHeight);
        editDate.setTextSize(0, textSHeight);
        editNumber.setTextSize(0, textSHeight);
        buttonSetCounter.setTextSize(0, textLHeight);
        buttonManual.setTextSize(0, textLHeight);
        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener dateProd = new DatePickerDialog.OnDateSetListener() { // from class: com.poverka.httpFileClient.activity.ResetProtocolNumberActivity.3
            @Override // android.app.DatePickerDialog.OnDateSetListener
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(1, year);
                myCalendar.set(2, monthOfYear);
                myCalendar.set(5, dayOfMonth);
                EditText edittext = (EditText) ResetProtocolNumberActivity.this.findViewById(R.id.editResetDate);
                edittext.setText(sdf.format(myCalendar.getTime()));
            }
        };
        final EditText editResetDate = (EditText) findViewById(R.id.editResetDate);
        editResetDate.setText(sdf.format(myCalendar.getTime()));
        editResetDate.setOnClickListener(new View.OnClickListener() { // from class: com.poverka.httpFileClient.activity.ResetProtocolNumberActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ROOT);
                    calendar.setTime(sdf2.parse(String.format(Locale.ROOT, "%s %s", editResetDate.getText().toString(), "12:00:00")));
                    new DatePickerDialog(ResetProtocolNumberActivity.this, dateProd, calendar.get(1), calendar.get(2), calendar.get(5)).show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        editNumber.setText("1");
    }

    private static class UiHandler extends Handler {
        private static final int ALERT = 0;
        private ResetProtocolNumberActivity activity;

        private UiHandler(ResetProtocolNumberActivity activity) {
            super(Looper.getMainLooper());
            this.activity = activity;
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Bundle bundle = msg.getData();
                if (bundle.getString("alert") != null) {
                    Toast.makeText(this.activity.getApplicationContext(), msg.getData().getString("alert"), 1).show();
                }
            }
        }
    }
}
