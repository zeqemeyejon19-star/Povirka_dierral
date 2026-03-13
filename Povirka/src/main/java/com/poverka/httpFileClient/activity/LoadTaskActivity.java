package com.poverka.httpFileClient.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import com.poverka.httpFileClient.R;
import com.poverka.httpFileClient.containers.Task;
import com.poverka.httpFileClient.util.HttpFileClient;
import com.poverka.httpFileClient.util.MyFileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class LoadTaskActivity extends AppCompatActivity {
    private static final String TAG = "LoadTaskActivity";
    private Activity activity;
    public HttpFileClient.OnMessageReceived mHttpReceived = new HttpFileClient.OnMessageReceived() { // from class: com.poverka.httpFileClient.activity.LoadTaskActivity.1
        @Override // com.poverka.httpFileClient.util.HttpFileClient.OnMessageReceived
        public void messageReceived(HttpFileClient.Type type, String fileName, InputStream stream) {
            Bundle bundle = new Bundle();
            try {
                if (fileName.contains("getmarshrut") && type == HttpFileClient.Type.SERVER && stream != null) {
                    String taskString = HttpFileClient.inputStreamToString(stream);
                    JSONObject taskInfo = new JSONObject(taskString);
                    String status = taskInfo.getString(NotificationCompat.CATEGORY_STATUS);
                    if (!status.equals("OK")) {
                        Message msg = LoadTaskActivity.this.myHandler.obtainMessage(2);
                        String error = taskInfo.getString("error");
                        bundle.putString("tittle", LoadTaskActivity.this.getString(R.string.tasks_not_loaded));
                        bundle.putString("message", error);
                        msg.setData(bundle);
                        LoadTaskActivity.this.myHandler.sendMessage(msg);
                        return;
                    }
                    File localTasks = new File(LoadTaskActivity.this.activity.getFilesDir(), "localTasks.json");
                    JSONArray serverTasksJSON = taskInfo.getJSONArray("result");
                    JSONArray localTasksJSON = new JSONArray(MyFileReader.readAndroidFile(LoadTaskActivity.this.activity.getFilesDir(), localTasks.getName()));
                    int loadCount = serverTasksJSON.length();
                    int serverInd = 0;
                    while (true) {
                        String taskString2 = taskString;
                        if (serverInd >= serverTasksJSON.length()) {
                            break;
                        }
                        int localInd = 0;
                        while (localInd < localTasksJSON.length()) {
                            String status2 = status;
                            if (serverTasksJSON.getJSONObject(serverInd).getInt("ID") == localTasksJSON.getJSONObject(localInd).getInt("ID")) {
                                if (!localTasksJSON.getJSONObject(localInd).has("STATUS_ID") || localTasksJSON.getJSONObject(localInd).getInt("STATUS_ID") == 0) {
                                    localTasksJSON.remove(localInd);
                                } else {
                                    serverTasksJSON.remove(serverInd);
                                    serverInd--;
                                }
                                localInd = localTasksJSON.length();
                            }
                            localInd++;
                            status = status2;
                        }
                        serverInd++;
                        taskString = taskString2;
                    }
                    int emptyCount = 0;
                    int saveCount = 0;
                    int serverInd2 = 0;
                    while (serverInd2 < serverTasksJSON.length()) {
                        if (serverTasksJSON.getJSONObject(serverInd2).getString("ZAJAVKA_TIME").equals("null")) {
                            emptyCount++;
                            serverTasksJSON.remove(serverInd2);
                            serverInd2--;
                        } else {
                            localTasksJSON.put(serverTasksJSON.getJSONObject(serverInd2));
                            saveCount++;
                        }
                        serverInd2++;
                    }
                    MyFileReader.writeInternalFile(LoadTaskActivity.this.activity.getApplicationContext(), localTasks.getName(), localTasksJSON.toString());
                    Pattern date = Pattern.compile("date=(.*?)$");
                    Matcher m = date.matcher(fileName);
                    if (m.find()) {
                        LoadTaskActivity.this.saveTasksToFile(m.group(1), serverTasksJSON);
                    }
                    Message msg2 = LoadTaskActivity.this.myHandler.obtainMessage(2);
                    bundle.putString("tittle", LoadTaskActivity.this.getString(R.string.result));
                    bundle.putString("message", String.format(Locale.ROOT, LoadTaskActivity.this.getString(R.string.loaded_tasks_info), Integer.valueOf(loadCount), Integer.valueOf(saveCount), Integer.valueOf(emptyCount)));
                    msg2.setData(bundle);
                    LoadTaskActivity.this.myHandler.sendMessage(msg2);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private MyHandler myHandler;

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_task);
        this.activity = this;
        this.myHandler = new MyHandler(this);
        Intent intent = getIntent();
        initEditStation(intent.getIntExtra("station", 0));
        initEditDate();
        try {
            File localTasks = new File(this.activity.getFilesDir(), "localTasks.json");
            if (!localTasks.exists()) {
                if (localTasks.createNewFile()) {
                    FileWriter writer = new FileWriter(localTasks);
                    writer.write("[]");
                    writer.flush();
                    writer.close();
                } else {
                    Toast.makeText(getApplicationContext(), String.format(Locale.ROOT, this.activity.getString(R.string.error_file_creation), 301), 1).show();
                }
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), String.format(Locale.ROOT, this.activity.getString(R.string.error_file_creation), 303), 1).show();
        }
    }

    public void LoadClicked(View view) {
        EditText editStation = (EditText) findViewById(R.id.editStation);
        EditText editDate = (EditText) findViewById(R.id.editDate);
        String station = editStation.getText().toString();
        String date = editDate.getText().toString();
        String auth = "station_head1:I8X_V..oe%OX";
        byte[] encodedAuth = Base64.encode(auth.getBytes(StandardCharsets.UTF_8), 0);
        String authHeaderValue = "Basic " + new String(encodedAuth);
        String path = String.format(Locale.ROOT, "getmarshrut?equipmentType=1&serial=%s&date=%s", station, date);
        HttpFileClient mHttpFileClient = new HttpFileClient(this.mHttpReceived);
        mHttpFileClient.downloadServer(path, authHeaderValue);
    }

    private static class MyHandler extends Handler {
        private static final int ALERT = 1;
        private static final int ALERT_DIALOG = 2;
        private Activity activity;

        private MyHandler(Activity activity) {
            this.activity = activity;
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
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
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.LoadTaskActivity.MyHandler.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.create().show();
            }
        }
    }

    private void initEditStation(int station) {
        EditText editStation = (EditText) findViewById(R.id.editStation);
        editStation.setText(String.format(Locale.ROOT, "%03d", Integer.valueOf(station)));
        editStation.setEnabled(false);
    }

    private void initEditDate() {
        Calendar today = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener dateSend = new DatePickerDialog.OnDateSetListener() { // from class: com.poverka.httpFileClient.activity.LoadTaskActivity.2
            @Override // android.app.DatePickerDialog.OnDateSetListener
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(1, year);
                myCalendar.set(2, monthOfYear);
                myCalendar.set(5, dayOfMonth);
                LoadTaskActivity.this.updateDateLabel(myCalendar);
            }
        };
        final EditText editSendDate = (EditText) findViewById(R.id.editDate);
        editSendDate.setText(String.format(Locale.ROOT, "%02d.%02d.%04d", Integer.valueOf(today.get(5)), Integer.valueOf(today.get(2) + 1), Integer.valueOf(today.get(1))));
        editSendDate.setOnClickListener(new View.OnClickListener() { // from class: com.poverka.httpFileClient.activity.LoadTaskActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (editSendDate.getText().length() == 0) {
                    return;
                }
                try {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ROOT);
                    calendar.setTime(sdf.parse(String.format(Locale.ROOT, "%s %s", editSendDate.getText().toString(), "12:00:00")));
                    new DatePickerDialog(LoadTaskActivity.this.activity, dateSend, calendar.get(1), calendar.get(2), calendar.get(5)).show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDateLabel(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
        EditText edittext = (EditText) this.activity.findViewById(R.id.editDate);
        edittext.setText(sdf.format(calendar.getTime()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveTasksToFile(String date, JSONArray tasksJSON) {
        MyFileReader.createPoverkaFolder();
        File dayFolder = MyFileReader.createFolderInPoverkaFolder(date);
        try {
            StringBuilder strBuilder = new StringBuilder();
            List<Task> list = Task.jsonStringToList(tasksJSON.toString());
            for (Task task : list) {
                strBuilder.append(task.toString()).append("\n");
            }
            File taskFile = new File(dayFolder, "Заявки.txt");
            taskFile.createNewFile();
            FileWriter writer = new FileWriter(taskFile);
            writer.write(strBuilder.toString());
            writer.flush();
            writer.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private static void Log(String message) {
        Log.d(TAG, message);
    }
}
