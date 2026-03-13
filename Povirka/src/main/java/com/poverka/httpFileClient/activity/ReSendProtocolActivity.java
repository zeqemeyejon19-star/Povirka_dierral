package com.poverka.httpFileClient.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
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

/* JADX INFO: loaded from: classes2.dex */
public class ReSendProtocolActivity extends AppCompatActivity {
    private static final float BUTTON_HEIGHT_RATIO = 0.13f;
    private static final float BUTTON_WIDTH_RATIO = 0.45f;
    private static final float MARGIN_HEIGHT_RATIO = 0.03f;
    private static final float MARGIN_WIDTH_RATIO = 0.03f;
    private static final String TAG = "ReSendProtocolActivity";
    private static final float TEXT_L_HEIGHT_RATIO = 0.055f;
    private static final float TEXT_S_HEIGHT_RATIO = 0.045f;
    private int fileNotFoundCounter;
    private HttpFileClient mHttpFileClient;
    public HttpFileClient.OnMessageReceived mHttpReceived = new HttpFileClient.OnMessageReceived() { // from class: com.poverka.httpFileClient.activity.ReSendProtocolActivity.1
        @Override // com.poverka.httpFileClient.util.HttpFileClient.OnMessageReceived
        public void messageReceived(HttpFileClient.Type type, String fileName, InputStream stream) {
            Message msg = ReSendProtocolActivity.this.mUiHandler.obtainMessage(0);
            Bundle bundle = new Bundle();
            try {
                if (fileName.contains("archive") && type == HttpFileClient.Type.UPLOAD && stream != null) {
                    Log.d(ReSendProtocolActivity.TAG, "успешно");
                    bundle.putString("alert", ReSendProtocolActivity.this.getString(R.string.success));
                    msg.setData(bundle);
                    ReSendProtocolActivity.this.mUiHandler.sendMessage(msg);
                    return;
                }
                if (!fileName.contains("archive") || type != HttpFileClient.Type.DELETE || stream == null) {
                    if (fileName.contains("archive") && type == HttpFileClient.Type.UPLOAD && stream == null) {
                        Log.d(ReSendProtocolActivity.TAG, "протокол не найден");
                        bundle.putString("alert", ReSendProtocolActivity.this.getString(R.string.protocol_not_found));
                        msg.setData(bundle);
                        ReSendProtocolActivity.this.mUiHandler.sendMessage(msg);
                        return;
                    }
                    if (fileName.contains("archive") && type == HttpFileClient.Type.DELETE && stream == null) {
                        ReSendProtocolActivity.access$208(ReSendProtocolActivity.this);
                        if (ReSendProtocolActivity.this.fileNotFoundCounter == 2) {
                            Log.d(ReSendProtocolActivity.TAG, "протокол не найден");
                            bundle.putString("alert", ReSendProtocolActivity.this.getString(R.string.protocol_not_found));
                            msg.setData(bundle);
                            ReSendProtocolActivity.this.mUiHandler.sendMessage(msg);
                            return;
                        }
                        return;
                    }
                    return;
                }
                String input = HttpFileClient.inputStreamToString(stream);
                if (input.contains("<tr><td>")) {
                    Log.d(ReSendProtocolActivity.TAG, "успешно");
                    bundle.putString("alert", ReSendProtocolActivity.this.getString(R.string.success));
                    msg.setData(bundle);
                    ReSendProtocolActivity.this.mUiHandler.sendMessage(msg);
                }
            } catch (IOException e) {
                Log.e(ReSendProtocolActivity.TAG, "Error in HTTP receiver", e);
                Log.d(ReSendProtocolActivity.TAG, "ошибка");
                bundle.putString("alert", String.format(Locale.ROOT, ReSendProtocolActivity.this.getString(R.string.error_message), ""));
                msg.setData(bundle);
                ReSendProtocolActivity.this.mUiHandler.sendMessage(msg);
            }
        }
    };
    private UiHandler mUiHandler;

    static /* synthetic */ int access$208(ReSendProtocolActivity x0) {
        int i = x0.fileNotFoundCounter;
        x0.fileNotFoundCounter = i + 1;
        return i;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_send_protocol);
        this.mHttpFileClient = new HttpFileClient(this.mHttpReceived);
        this.mUiHandler = new UiHandler();
        initViews();
    }

    public void ReSendClicked(View v) {
        repeatSend();
    }

    public void ManualClicked(View v) {
        showManual();
    }

    public void SkipClicked(View view) {
        skip();
    }

    private void repeatSend() {
        EditText editRepeatDate = (EditText) findViewById(R.id.editRepeatDate);
        EditText editRepeatNumber = (EditText) findViewById(R.id.editRepeatNumber);
        if (editRepeatDate.getText().toString().length() == 0 || editRepeatNumber.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.select_date_and_number_of_protocol), 1).show();
            return;
        }
        String date = editRepeatDate.getText().toString().replace(".", "");
        try {
            Integer.parseInt(date);
            if (date.length() != 8) {
                throw new NumberFormatException();
            }
            try {
                int number = Integer.parseInt(editRepeatNumber.getText().toString());
                if (number < 1) {
                    Resources res = getResources();
                    Configuration conf = res.getConfiguration();
                    Log.i("123", conf.locale.toString());
                    Log.i("123", getString(R.string.protocol_number_has_to_be_grater_0));
                    Toast.makeText(getApplicationContext(), getString(R.string.protocol_number_has_to_be_grater_0), 1).show();
                    return;
                }
                this.fileNotFoundCounter = 0;
                String path = String.format(Locale.ROOT, "1/archive/%s/%d/ControlFile.txt", date, Integer.valueOf(number));
                this.mHttpFileClient.delete(path);
                String path2 = String.format(Locale.ROOT, "1/archive/%s/%d/Control.txt", date, Integer.valueOf(number));
                this.mHttpFileClient.delete(path2);
            } catch (NumberFormatException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.protocol_number_has_to_be_numeric), 1).show();
            }
        } catch (NumberFormatException e2) {
            Toast.makeText(getApplicationContext(), getString(R.string.wrong_date_format), 1).show();
        }
    }

    private void showManual() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.manual_resend_protocol_title);
        builder.setMessage(R.string.manual_resend_protocol_message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.ReSendProtocolActivity.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void skip() {
        EditText editRepeatDate = (EditText) findViewById(R.id.editRepeatDate);
        EditText editRepeatNumber = (EditText) findViewById(R.id.editRepeatNumber);
        if (editRepeatDate.getText().toString().length() == 0 || editRepeatNumber.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.select_date_and_number_of_protocol), 1).show();
            return;
        }
        String date = editRepeatDate.getText().toString().replace(".", "");
        try {
            Integer.parseInt(date);
            if (date.length() != 8) {
                throw new NumberFormatException();
            }
            try {
                int number = Integer.parseInt(editRepeatNumber.getText().toString());
                if (number < 1) {
                    Resources res = getResources();
                    Configuration conf = res.getConfiguration();
                    Log.i("123", conf.locale.toString());
                    Log.i("123", getString(R.string.protocol_number_has_to_be_grater_0));
                    Toast.makeText(getApplicationContext(), getString(R.string.protocol_number_has_to_be_grater_0), 1).show();
                    return;
                }
                String path = String.format(Locale.ROOT, "1/archive/%s/%d/ControlFile.txt", date, Integer.valueOf(number));
                this.mHttpFileClient.upload(path, "{system.json;}");
            } catch (NumberFormatException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.protocol_number_has_to_be_numeric), 1).show();
            }
        } catch (NumberFormatException e2) {
            Toast.makeText(getApplicationContext(), getString(R.string.wrong_date_format), 1).show();
        }
    }

    private void initViews() {
        TextView textReSend = (TextView) findViewById(R.id.textReSend);
        TextView textDate = (TextView) findViewById(R.id.textViewDate);
        TextView textNumber = (TextView) findViewById(R.id.textViewNumber);
        EditText editDate = (EditText) findViewById(R.id.editRepeatDate);
        EditText editNumber = (EditText) findViewById(R.id.editRepeatNumber);
        Button buttonSend = (Button) findViewById(R.id.buttonReSend);
        Button buttonManual = (Button) findViewById(R.id.buttonManual);
        Button buttonSkip = (Button) findViewById(R.id.buttonSkip);
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
        ConstraintLayout.LayoutParams paramsC5 = (ConstraintLayout.LayoutParams) buttonSend.getLayoutParams();
        paramsC5.topMargin = marginHeight;
        paramsC5.width = buttonWidth;
        paramsC5.height = buttonHeight;
        buttonSend.requestLayout();
        ConstraintLayout.LayoutParams paramsC6 = (ConstraintLayout.LayoutParams) buttonManual.getLayoutParams();
        paramsC6.topMargin = marginHeight;
        paramsC6.width = buttonWidth;
        paramsC6.height = buttonHeight;
        buttonManual.requestLayout();
        ConstraintLayout.LayoutParams paramsC7 = (ConstraintLayout.LayoutParams) buttonSkip.getLayoutParams();
        paramsC7.topMargin = marginHeight;
        paramsC7.width = buttonWidth;
        paramsC7.height = buttonHeight;
        buttonSkip.requestLayout();
        textReSend.setTextSize(0, textLHeight);
        textDate.setTextSize(0, textSHeight);
        textNumber.setTextSize(0, textSHeight);
        editDate.setTextSize(0, textSHeight);
        editNumber.setTextSize(0, textSHeight);
        buttonSend.setTextSize(0, textLHeight);
        buttonManual.setTextSize(0, textLHeight);
        buttonSkip.setTextSize(0, textLHeight);
        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener dateProd = new DatePickerDialog.OnDateSetListener() { // from class: com.poverka.httpFileClient.activity.ReSendProtocolActivity.3
            @Override // android.app.DatePickerDialog.OnDateSetListener
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(1, year);
                myCalendar.set(2, monthOfYear);
                myCalendar.set(5, dayOfMonth);
                EditText edittext = (EditText) ReSendProtocolActivity.this.findViewById(R.id.editRepeatDate);
                edittext.setText(sdf.format(myCalendar.getTime()));
            }
        };
        final EditText editRepeatDate = (EditText) findViewById(R.id.editRepeatDate);
        editRepeatDate.setText(sdf.format(myCalendar.getTime()));
        editRepeatDate.setOnClickListener(new View.OnClickListener() { // from class: com.poverka.httpFileClient.activity.ReSendProtocolActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ROOT);
                    calendar.setTime(sdf2.parse(String.format(Locale.ROOT, "%s %s", editRepeatDate.getText().toString(), "12:00:00")));
                    new DatePickerDialog(ReSendProtocolActivity.this, dateProd, calendar.get(1), calendar.get(2), calendar.get(5)).show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        editNumber.setText("1");
    }

    private static class UiHandler extends Handler {
        private static final int ALERT = 0;
        private ReSendProtocolActivity activity;

        private UiHandler(ReSendProtocolActivity activity) {
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
