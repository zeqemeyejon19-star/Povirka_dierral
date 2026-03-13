package com.poverka.httpFileClient.containers;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class TaskResult {
    private String date;
    private int dismissId;
    private String dismissNote;
    private int id;
    private String protocolNumber;
    private int status;

    public TaskResult(int id) {
        this.id = id;
        this.status = 0;
    }

    public TaskResult(JSONObject json) {
        try {
            this.id = json.optInt("ID", 0);
            this.status = json.optInt("STATUS", 0);
            this.date = json.optString("DATE", "01.01.1970");
            this.protocolNumber = json.getString("PROTOCOL");
            this.dismissId = json.optInt("DISMISS_ID", 0);
            this.dismissNote = json.getString("DISMISS_NOTE");
        } catch (JSONException e) {
            Log.e("TaskResult", "error in JSON constructor");
        }
    }

    public static List<TaskResult> jsonStringToList(String str) throws JSONException {
        List<TaskResult> result = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(str);
        for (int index = 0; index < jsonArray.length(); index++) {
            result.add(new TaskResult(jsonArray.getJSONObject(index)));
        }
        return result;
    }

    public int getId() {
        return this.id;
    }

    public int getStatus() {
        return this.status;
    }

    public String getDate() {
        return this.date;
    }

    public String getProtocolNumber() {
        return this.protocolNumber;
    }

    public int getDismissId() {
        return this.dismissId;
    }

    public String getDismissNote() {
        return this.dismissNote;
    }

    public void setDismiss(String date, int dismissReason, String note) {
        this.status = 1;
        this.date = date;
        this.dismissId = dismissReason;
        this.dismissNote = note;
    }

    public void setDone(String date, String protocolNumber) {
        this.status = 2;
        this.date = date;
        this.protocolNumber = protocolNumber;
    }
}
