package com.poverka.httpFileClient.activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import com.poverka.httpFileClient.R;
import com.poverka.httpFileClient.containers.Apartment;
import com.poverka.httpFileClient.containers.Day;
import com.poverka.httpFileClient.containers.Task;
import com.poverka.httpFileClient.task.ApartmentViewerAdapter;
import com.poverka.httpFileClient.task.DayViewerAdapter;
import com.poverka.httpFileClient.task.TaskHelper;
import com.poverka.httpFileClient.task.TaskViewerAdapter;
import com.poverka.httpFileClient.util.HttpFileClient;
import com.poverka.httpFileClient.util.MyFileReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes2.dex */
public class TaskViewerActivity extends AppCompatActivity implements Observer {
    private static final int APARTMENTS = 2;
    private static final int COUNTERS = 3;
    private static final int DAYS = 1;
    private static final int NONE = 0;
    private static final String TAG = "TaskViewerActivity";
    private MyLists currentList;
    public HttpFileClient.OnMessageReceived mHttpReceived = new HttpFileClient.OnMessageReceived() { // from class: com.poverka.httpFileClient.activity.TaskViewerActivity.2
        @Override // com.poverka.httpFileClient.util.HttpFileClient.OnMessageReceived
        public void messageReceived(HttpFileClient.Type type, String fileName, InputStream stream) {
            Message msg = TaskViewerActivity.this.myHandler.obtainMessage(1);
            Bundle bundle = new Bundle();
            try {
                if (fileName.contains("cancelpribor") && type == HttpFileClient.Type.SERVER && stream != null) {
                    String dismissString = HttpFileClient.inputStreamToString(stream);
                    Log.d(TaskViewerActivity.TAG, dismissString);
                    JSONObject dismissInfo = new JSONObject(dismissString);
                    String status = dismissInfo.getString(NotificationCompat.CATEGORY_STATUS);
                    if (!status.equals("OK")) {
                        Message msg2 = TaskViewerActivity.this.myHandler.obtainMessage(3);
                        Pattern photo = Pattern.compile("cancelpribor?protocolId=(.*?)", 32);
                        Matcher m = photo.matcher(fileName);
                        if (m.find()) {
                            int id = Integer.parseInt(m.group(1));
                            bundle.putInt("id", id);
                        } else {
                            bundle.putInt("id", -2);
                        }
                        String error = dismissInfo.getString("error");
                        bundle.putString("alert", error);
                        msg2.setData(bundle);
                        TaskViewerActivity.this.myHandler.sendMessage(msg2);
                    } else {
                        String result = dismissInfo.getString("result");
                        Log.d(TaskViewerActivity.TAG, result);
                        bundle.putString("alert", TaskViewerActivity.this.getString(R.string.success));
                        msg.setData(bundle);
                        TaskViewerActivity.this.myHandler.sendMessage(msg);
                    }
                } else if (fileName.contains("cancelpribor") && type == HttpFileClient.Type.SERVER && stream == null) {
                    bundle.putString("alert", TaskViewerActivity.this.getString(R.string.error_occurred_task_cancellation));
                    msg.setData(bundle);
                    TaskViewerActivity.this.myHandler.sendMessage(msg);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private MyHandler myHandler;
    private int photoType;
    private int writeLog;

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_viewer);
        this.myHandler = new MyHandler(this, null);
        Intent intent = getIntent();
        this.photoType = intent.getIntExtra("photoType", 0);
        this.writeLog = intent.getIntExtra("writeLog", 0);
        ListView myListView = (ListView) findViewById(R.id.taskList);
        myListView.setOnItemClickListener(new AnonymousClass1());
    }

    /* JADX INFO: renamed from: com.poverka.httpFileClient.activity.TaskViewerActivity$1, reason: invalid class name */
    class AnonymousClass1 implements AdapterView.OnItemClickListener {
        AnonymousClass1() {
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int currentList = TaskViewerActivity.this.currentList.getCurrentList();
            if (currentList == 1) {
                Day day = (Day) parent.getAdapter().getItem(position);
                TaskViewerActivity.this.currentList.toApartments(day.getDate());
                return;
            }
            if (currentList == 2) {
                Apartment apartment = (Apartment) parent.getAdapter().getItem(position);
                TaskViewerActivity.this.currentList.toCounters(apartment.getCityId(), apartment.getStreetId(), apartment.getBuilding(), apartment.getBuildingB(), apartment.getBuildingK(), apartment.getApartment(), apartment.getApartmentB());
                return;
            }
            if (currentList == 3) {
                final Task task = (Task) parent.getAdapter().getItem(position);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TaskViewerActivity.this);
                String title = "";
                String message = "";
                int status = task.getStatus();
                if (status == 0) {
                    title = TaskViewerActivity.this.getString(R.string.new_task);
                    message = String.format(Locale.ROOT, TaskViewerActivity.this.getString(R.string.task_info), Integer.valueOf(task.getId()), task.getSurname(), task.getFullAddress(), task.getFullApartment(), Integer.valueOf(task.getPodezd()), Integer.valueOf(task.getEtag()), task.getPhones(), task.getNote());
                    alertDialogBuilder.setPositiveButton(R.string.select, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.TaskViewerActivity.1.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            File addressFile = new File(TaskViewerActivity.this.getFilesDir(), "address.json");
                            AlertDialog.Builder alertAddress = new AlertDialog.Builder(TaskViewerActivity.this);
                            alertAddress.setTitle(R.string.alert);
                            if (addressFile.exists()) {
                                try {
                                    JSONObject jsonAddress = new JSONObject(MyFileReader.readAndroidFile(TaskViewerActivity.this.getFilesDir(), addressFile.getName()));
                                    if (jsonAddress.getInt("city_id") == task.getCityId()) {
                                        Intent myIntent = new Intent(TaskViewerActivity.this.getApplicationContext(), (Class<?>) StateActivity.class);
                                        myIntent.putExtra("local", false);
                                        myIntent.putExtra("selectedId", task.getId());
                                        myIntent.putExtra("photoType", TaskViewerActivity.this.photoType);
                                        myIntent.putExtra("writeLog", TaskViewerActivity.this.writeLog);
                                        TaskViewerActivity.this.startActivity(myIntent);
                                    } else {
                                        alertAddress.setMessage(R.string.wrong_addresses);
                                        alertAddress.setPositiveButton(R.string.select, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.TaskViewerActivity.1.1.1
                                            @Override // android.content.DialogInterface.OnClickListener
                                            public void onClick(DialogInterface dialog2, int which2) {
                                                Intent myIntent2 = new Intent(TaskViewerActivity.this.getApplicationContext(), (Class<?>) StateActivity.class);
                                                myIntent2.putExtra("local", false);
                                                myIntent2.putExtra("selectedId", task.getId());
                                                myIntent2.putExtra("photoType", TaskViewerActivity.this.photoType);
                                                myIntent2.putExtra("writeLog", TaskViewerActivity.this.writeLog);
                                                TaskViewerActivity.this.startActivity(myIntent2);
                                            }
                                        });
                                        alertAddress.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.TaskViewerActivity.1.1.2
                                            @Override // android.content.DialogInterface.OnClickListener
                                            public void onClick(DialogInterface dialog2, int which2) {
                                                dialog2.dismiss();
                                            }
                                        });
                                        alertAddress.create().show();
                                    }
                                    return;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    return;
                                }
                            }
                            alertAddress.setMessage(R.string.need_to_load_address);
                            alertAddress.setPositiveButton("ОК", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.TaskViewerActivity.1.1.3
                                @Override // android.content.DialogInterface.OnClickListener
                                public void onClick(DialogInterface dialog2, int which2) {
                                    dialog2.dismiss();
                                }
                            });
                            alertAddress.create().show();
                        }
                    });
                    alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.TaskViewerActivity.1.2
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            if (!TaskViewerActivity.this.checkMobileDataEnabled(TaskViewerActivity.this.getApplicationContext())) {
                                AlertDialog.Builder builderSmall = new AlertDialog.Builder(TaskViewerActivity.this);
                                builderSmall.setTitle(R.string.alert);
                                builderSmall.setMessage(R.string.need_to_turn_on_mobile_data);
                                builderSmall.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.TaskViewerActivity.1.2.1
                                    @Override // android.content.DialogInterface.OnClickListener
                                    public void onClick(DialogInterface dialog2, int id2) {
                                        dialog2.dismiss();
                                    }
                                });
                                AlertDialog dialogSmall = builderSmall.create();
                                dialogSmall.show();
                                return;
                            }
                            AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(TaskViewerActivity.this);
                            alertDialogBuilder2.setTitle(R.string.task_cancellation);
                            final EditText editComment = new EditText(alertDialogBuilder2.getContext());
                            editComment.setImeOptions(33554432);
                            editComment.setHint(R.string.set_reason_to_cancel_task);
                            alertDialogBuilder2.setView(editComment);
                            alertDialogBuilder2.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.TaskViewerActivity.1.2.2
                                @Override // android.content.DialogInterface.OnClickListener
                                public void onClick(DialogInterface dialog2, int which2) {
                                    TaskViewerActivity.this.cancelPribor(task.getId(), editComment.getText().toString());
                                    TaskViewerActivity.this.updateList(2);
                                }
                            });
                            alertDialogBuilder2.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.TaskViewerActivity.1.2.3
                                @Override // android.content.DialogInterface.OnClickListener
                                public void onClick(DialogInterface dialog2, int which2) {
                                    dialog2.dismiss();
                                }
                            });
                            alertDialogBuilder2.create().show();
                        }
                    });
                    alertDialogBuilder.setNeutralButton(R.string.dismiss, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.TaskViewerActivity.1.3
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                } else if (status == 1) {
                    title = TaskViewerActivity.this.getString(R.string.task_canceled);
                    message = String.format(Locale.ROOT, TaskViewerActivity.this.getString(R.string.cancel_task_info), Integer.valueOf(task.getId()), task.getStatusDate(), task.getDismissNote());
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.TaskViewerActivity.1.4
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                } else if (status == 2) {
                    title = TaskViewerActivity.this.getString(R.string.task_has_been_done);
                    message = String.format(Locale.ROOT, TaskViewerActivity.this.getString(R.string.done_task_info), Integer.valueOf(task.getId()), task.getCounterNumber(), task.getStatusDate(), task.getProtocolNumber());
                    alertDialogBuilder.setPositiveButton(R.string.open_protocol, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.TaskViewerActivity.1.5
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) throws XmlPullParserException, IOException {
                            File folderPoverka = new File(Environment.getExternalStorageDirectory().toString(), "Poverka");
                            File folderDay = new File(folderPoverka, task.getStatusDate().replace('.', '_'));
                            File pdfFile = new File(folderDay, String.format(Locale.ROOT, "%s.pdf", task.getProtocolNumber()));
                            if (pdfFile.exists()) {
                                Intent intent = new Intent("android.intent.action.VIEW");
                                if (Build.VERSION.SDK_INT >= 24) {
                                    Uri pdfUri = FileProvider.getUriForFile(TaskViewerActivity.this, "com.poverka.httpFileClient.fileprovider", pdfFile);
                                    intent.setDataAndType(pdfUri, "application/pdf");
                                } else {
                                    intent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
                                }
                                intent.setFlags(1);
                                try {
                                    TaskViewerActivity.this.startActivity(intent);
                                    return;
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(TaskViewerActivity.this, TaskViewerActivity.this.getString(R.string.no_pdf_reader), 0).show();
                                    return;
                                }
                            }
                            Toast.makeText(TaskViewerActivity.this, TaskViewerActivity.this.getString(R.string.protocol_is_missing), 0).show();
                        }
                    });
                    alertDialogBuilder.setNeutralButton(R.string.dismiss, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.TaskViewerActivity.1.6
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
                alertDialogBuilder.setTitle(title);
                alertDialogBuilder.setMessage(message);
                alertDialogBuilder.create().show();
            }
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
        findViewById(R.id.taskHeaderDays).setVisibility(8);
        findViewById(R.id.taskHeaderApartments).setVisibility(8);
        findViewById(R.id.taskHeaderCounters).setVisibility(8);
        findViewById(R.id.buttonCleanGrey).setVisibility(8);
        MyLists myLists = new MyLists(initListView());
        this.currentList = myLists;
        myLists.addObserver(this);
        this.currentList.setCurrentList(1);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        this.currentList.toPreviousList();
    }

    @Override // java.util.Observer
    public void update(Observable observable, Object arg) {
        ListView myListView = (ListView) findViewById(R.id.taskList);
        View viewDays = findViewById(R.id.taskHeaderDays);
        View viewApartments = findViewById(R.id.taskHeaderApartments);
        View viewCounters = findViewById(R.id.taskHeaderCounters);
        Button buttonClose = (Button) findViewById(R.id.buttonCleanGrey);
        MyLists mList = (MyLists) observable;
        int currentList = mList.getCurrentList();
        if (currentList == 0) {
            super.onBackPressed();
            return;
        }
        if (currentList == 1) {
            viewDays.setVisibility(0);
            viewApartments.setVisibility(8);
            viewCounters.setVisibility(8);
            buttonClose.setVisibility(0);
            buttonClose.setText(getResources().getString(R.string.task_viewer_button_clean));
            DayViewerAdapter dayAdapter = new DayViewerAdapter(this, R.layout.task_list_day_row, mList.getTasksByDay());
            myListView.setAdapter((ListAdapter) dayAdapter);
            return;
        }
        if (currentList == 2) {
            viewDays.setVisibility(8);
            viewApartments.setVisibility(0);
            viewCounters.setVisibility(8);
            buttonClose.setVisibility(8);
            ApartmentViewerAdapter apartmentAdapter = new ApartmentViewerAdapter(this, R.layout.task_list_apartment_row, mList.getTasksOfDay());
            myListView.setAdapter((ListAdapter) apartmentAdapter);
            return;
        }
        if (currentList == 3) {
            viewDays.setVisibility(8);
            viewApartments.setVisibility(8);
            viewCounters.setVisibility(0);
            buttonClose.setVisibility(0);
            buttonClose.setText(getResources().getString(R.string.task_viewer_button_dismiss));
            TaskViewerAdapter taskAdapter = new TaskViewerAdapter(this, R.layout.task_list_counter_row, mList.getTasksOfApartment());
            myListView.setAdapter((ListAdapter) taskAdapter);
        }
    }

    private static class MyHandler extends Handler {
        private static final int ALERT = 1;
        private static final int ALERT_DIALOG = 2;
        private static final int DISMISS_ERROR = 3;
        private TaskViewerActivity activity;

        /* synthetic */ MyHandler(TaskViewerActivity x0, AnonymousClass1 x1) {
            this(x0);
        }

        private MyHandler(TaskViewerActivity activity) {
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
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.TaskViewerActivity.MyHandler.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.create().show();
                return;
            }
            if (i == 3) {
                int id = msg.getData().getInt("id");
                if (id != -2) {
                    try {
                        File localTasks = new File(this.activity.getFilesDir(), "localTasks.json");
                        JSONArray localTasksJSON = new JSONArray(MyFileReader.readAndroidFile(this.activity.getFilesDir(), localTasks.getName()));
                        Task task = this.activity.takeTaskById(id, localTasksJSON);
                        task.setNew();
                        localTasksJSON.put(task.toJSON());
                        MyFileReader.writeInternalFile(this.activity, localTasks.getName(), localTasksJSON.toString());
                        this.activity.updateList(1);
                        Toast.makeText(this.activity.getApplicationContext(), msg.getData().getString("alert"), 1).show();
                        return;
                    } catch (JSONException e) {
                        Toast.makeText(this.activity.getApplicationContext(), String.format(Locale.ROOT, "id: -3 \n%s", msg.getData().getString("alert")), 1).show();
                        return;
                    }
                }
                Toast.makeText(this.activity.getApplicationContext(), String.format(Locale.ROOT, "id: -2 \n%s", msg.getData().getString("alert")), 1).show();
            }
        }
    }

    private List<Task> initListView() {
        try {
            File localTasks = new File(getFilesDir(), "localTasks.json");
            return Task.jsonStringToList(MyFileReader.readAndroidFile(getFilesDir(), localTasks.getName()));
        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
            return new ArrayList();
        }
    }

    public void CleanGreyClicked(View v) {
        Button buttonClose = (Button) findViewById(R.id.buttonCleanGrey);
        if (buttonClose.getText().toString().equals(getResources().getString(R.string.task_viewer_button_clean))) {
            final List<String> datesStr = new ArrayList<>();
            StringBuilder dates = new StringBuilder();
            for (Day d : this.currentList.getTasksByDay()) {
                if (d.getNewNumber() == 0) {
                    datesStr.add(d.getDate());
                    dates.append(d.getDate()).append("\n");
                }
            }
            if (datesStr.size() == 0) {
                Toast.makeText(this, getString(R.string.completed_days_not_found), 1).show();
                return;
            }
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.alert);
            alertDialogBuilder.setMessage(String.format(Locale.ROOT, getString(R.string.want_to_clean_completed_days), dates.toString()));
            alertDialogBuilder.setPositiveButton(R.string.clean, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.TaskViewerActivity.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        File localTasks = new File(TaskViewerActivity.this.getFilesDir(), "localTasks.json");
                        JSONArray localTasksJSON = new JSONArray(MyFileReader.readAndroidFile(TaskViewerActivity.this.getFilesDir(), localTasks.getName()));
                        int counter = 0;
                        for (String day : datesStr) {
                            int localInd = 0;
                            while (localInd < localTasksJSON.length()) {
                                if (localTasksJSON.getJSONObject(localInd).getString("ZAJAVKA_DATE").equals(day)) {
                                    localTasksJSON.remove(localInd);
                                    localInd--;
                                    counter++;
                                }
                                localInd++;
                            }
                        }
                        MyFileReader.writeInternalFile(TaskViewerActivity.this, localTasks.getName(), localTasksJSON.toString());
                        TaskViewerActivity.this.updateList(1);
                        Toast.makeText(TaskViewerActivity.this, String.format(Locale.ROOT, TaskViewerActivity.this.getString(R.string.deleted_tasks_number), Integer.valueOf(counter)), 1).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            alertDialogBuilder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.TaskViewerActivity.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialogBuilder.create().show();
            return;
        }
        if (buttonClose.getText().toString().equals(getResources().getString(R.string.task_viewer_button_dismiss))) {
            final List<Task> list = this.currentList.getTasksOfApartment();
            int counter = 0;
            for (Task t : list) {
                if (t.getStatus() == 0) {
                    counter++;
                }
            }
            if (!checkMobileDataEnabled(getApplicationContext())) {
                AlertDialog.Builder builderSmall = new AlertDialog.Builder(this);
                builderSmall.setTitle(R.string.alert);
                builderSmall.setMessage(R.string.need_to_turn_on_mobile_data);
                builderSmall.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.TaskViewerActivity.5
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialogSmall = builderSmall.create();
                dialogSmall.show();
                return;
            }
            if (counter == 0) {
                Toast.makeText(this, getString(R.string.all_tasks_are_done), 1).show();
                return;
            }
            AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(this);
            alertDialogBuilder2.setTitle(String.format(Locale.ROOT, getString(R.string.number_of_tasks_to_cancel), Integer.valueOf(counter)));
            final EditText editComment = new EditText(alertDialogBuilder2.getContext());
            editComment.setImeOptions(33554432);
            editComment.setHint(R.string.set_reason_to_cancel_tasks);
            alertDialogBuilder2.setView(editComment);
            alertDialogBuilder2.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.TaskViewerActivity.6
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    for (Task t2 : list) {
                        if (t2.getStatus() == 0) {
                            TaskViewerActivity.this.cancelPribor(t2.getId(), editComment.getText().toString());
                        }
                    }
                    TaskViewerActivity.this.updateList(2);
                }
            });
            alertDialogBuilder2.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.TaskViewerActivity.7
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialogBuilder2.create().show();
        }
    }

    public void LoadTasksClicked(View v) {
        try {
            File settings = new File(getApplicationContext().getFilesDir(), "settings.json");
            String text = MyFileReader.readAndroidFile(getFilesDir(), settings.getName());
            JSONObject jsonSettings = new JSONObject(text);
            Intent loadTaskIntent = new Intent(this, (Class<?>) LoadTaskActivity.class);
            loadTaskIntent.putExtra("station", jsonSettings.getInt("station"));
            startActivity(loadTaskIntent);
        } catch (JSONException e) {
            Toast.makeText(this, String.format(Locale.ROOT, getString(R.string.error_message), e.getMessage()), 1).show();
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelPribor(int id, String comment) {
        try {
            String auth = "station_head1:I8X_V..oe%OX";
            byte[] encodedAuth = Base64.encode(auth.getBytes(StandardCharsets.UTF_8), 0);
            String authHeaderValue = "Basic " + new String(encodedAuth);
            Date date = Calendar.getInstance().getTime();
            String path = String.format(Locale.ROOT, "cancelpribor?protocolId=%d", Integer.valueOf(id));
            JSONObject dismissJSON = new JSONObject();
            dismissJSON.put("ID", id);
            dismissJSON.put("COMMENT", comment);
            dismissJSON.put("DATETIME", String.format(Locale.ROOT, "%tFT%<tTZ", date));
            String data = dismissJSON.toString();
            HttpFileClient mHttpFileClient = new HttpFileClient(this.mHttpReceived);
            mHttpFileClient.uploadServer(path, data, authHeaderValue);
            File localTasks = new File(getFilesDir(), "localTasks.json");
            File filesDir = getFilesDir();
            String user = localTasks.getName();
            JSONArray localTasksJSON = new JSONArray(MyFileReader.readAndroidFile(filesDir, user));
            Task task = takeTaskById(id, localTasksJSON);
            task.setDismiss(String.format(Locale.ROOT, "%1$td.%1$tm.%1$tY", date), comment);
            localTasksJSON.put(task.toJSON());
            MyFileReader.writeInternalFile(this, localTasks.getName(), localTasksJSON.toString());
        } catch (JSONException e) {
            Toast.makeText(this, String.format(Locale.ROOT, getString(R.string.error_message), e.getMessage()), 1).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateList(int list) {
        String date = this.currentList.getDate();
        int cityId = this.currentList.getCityId();
        int streetId = this.currentList.getStreetId();
        int building = this.currentList.getBuilding();
        String buildingB = this.currentList.getBuildingB();
        String buildingK = this.currentList.getBuildingK();
        int apartment = this.currentList.getApartment();
        String apartmentB = this.currentList.getApartmentB();
        MyLists myLists = new MyLists(initListView());
        this.currentList = myLists;
        myLists.setData(date, cityId, streetId, building, buildingB, buildingK, apartment, apartmentB);
        this.currentList.addObserver(this);
        this.currentList.setCurrentList(list);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Task takeTaskById(int id, JSONArray localTasksJSON) throws JSONException {
        for (int localInd = 0; localInd < localTasksJSON.length(); localInd++) {
            if (localTasksJSON.getJSONObject(localInd).getInt("ID") == id) {
                Task task = new Task(localTasksJSON.getJSONObject(localInd));
                localTasksJSON.remove(localInd);
                return task;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkMobileDataEnabled(Context context) {
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

    private static class MyLists extends Observable {
        private int apartment;
        private String apartmentB;
        private int building;
        private String buildingB;
        private String buildingK;
        private int cityId;
        private int currentList;
        private String date;
        private int streetId;
        private final List<Task> taskList;

        public MyLists(List<Task> taskList) {
            this.taskList = taskList;
        }

        public int getCurrentList() {
            return this.currentList;
        }

        public String getDate() {
            return this.date;
        }

        public int getCityId() {
            return this.cityId;
        }

        public int getStreetId() {
            return this.streetId;
        }

        public int getBuilding() {
            return this.building;
        }

        public String getBuildingB() {
            return this.buildingB;
        }

        public String getBuildingK() {
            return this.buildingK;
        }

        public int getApartment() {
            return this.apartment;
        }

        public String getApartmentB() {
            return this.apartmentB;
        }

        public void setData(String date, int cityId, int streetId, int building, String buildingB, String buildingK, int apartment, String apartmentB) {
            this.date = date;
            this.cityId = cityId;
            this.streetId = streetId;
            this.building = building;
            this.buildingB = buildingB;
            this.buildingK = buildingK;
            this.apartment = apartment;
            this.apartmentB = apartmentB;
        }

        public void setCurrentList(int currentList) {
            this.currentList = currentList;
            setChanged();
            notifyObservers();
        }

        public void toPreviousList() {
            this.currentList--;
            setChanged();
            notifyObservers();
        }

        public void toApartments(String date) {
            this.currentList = 2;
            this.date = date;
            setChanged();
            notifyObservers();
        }

        public void toCounters(int cityId, int streetId, int building, String buildingB, String buildingK, int apartment, String apartmentB) {
            this.currentList = 3;
            this.cityId = cityId;
            this.streetId = streetId;
            this.building = building;
            this.buildingB = buildingB;
            this.buildingK = buildingK;
            this.apartment = apartment;
            this.apartmentB = apartmentB;
            setChanged();
            notifyObservers();
        }

        public List<Day> getTasksByDay() {
            TaskHelper taskHelper = new TaskHelper(this.taskList);
            return taskHelper.getTasksByDay();
        }

        public List<Apartment> getTasksOfDay() {
            TaskHelper taskHelper = new TaskHelper(this.taskList);
            return taskHelper.getTasksOfDay(this.date);
        }

        public List<Task> getTasksOfApartment() {
            TaskHelper taskHelper = new TaskHelper(this.taskList);
            return taskHelper.getTasksOfApartment(this.date, this.cityId, this.streetId, this.building, this.buildingB, this.buildingK, this.apartment, this.apartmentB);
        }
    }
}
