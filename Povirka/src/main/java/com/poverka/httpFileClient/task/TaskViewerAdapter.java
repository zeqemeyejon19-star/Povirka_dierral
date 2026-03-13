package com.poverka.httpFileClient.task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.poverka.httpFileClient.R;
import com.poverka.httpFileClient.containers.Task;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class TaskViewerAdapter extends ArrayAdapter<Task> {
    private final Context mContext;
    private final int resourceLayout;

    public TaskViewerAdapter(Context context, int resource, List<Task> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi = LayoutInflater.from(this.mContext);
            view = vi.inflate(this.resourceLayout, (ViewGroup) null);
        }
        Task task = getItem(position);
        if (task != null) {
            TextView tt2 = (TextView) view.findViewById(R.id.serviceType);
            TextView tt3 = (TextView) view.findViewById(R.id.note);
            TextView tt4 = (TextView) view.findViewById(R.id.status);
            if (tt2 != null) {
                if (task.getServiceType() == 1) {
                    tt2.setText(R.string.water_cold);
                } else {
                    tt2.setText(R.string.water_hot);
                }
            }
            if (tt3 != null) {
                tt3.setText(String.valueOf(task.getNote()));
            }
            if (tt4 != null) {
                int status = task.getStatus();
                if (status != 0) {
                    if (status == 1) {
                        tt4.setText(R.string.task_dismissed);
                    } else if (status == 2) {
                        tt4.setText(R.string.task_done);
                    }
                } else {
                    tt4.setText(R.string.task_new);
                }
            }
        }
        return view;
    }
}
