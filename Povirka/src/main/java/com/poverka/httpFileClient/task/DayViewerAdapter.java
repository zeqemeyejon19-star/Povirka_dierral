package com.poverka.httpFileClient.task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.poverka.httpFileClient.R;
import com.poverka.httpFileClient.containers.Day;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class DayViewerAdapter extends ArrayAdapter<Day> {
    private final Context mContext;
    private final int resourceLayout;

    public DayViewerAdapter(Context context, int resource, List<Day> items) {
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
        Day day = getItem(position);
        if (day != null) {
            TextView tt1 = (TextView) view.findViewById(R.id.date);
            TextView tt2 = (TextView) view.findViewById(R.id.new_count);
            TextView tt3 = (TextView) view.findViewById(R.id.cancel_count);
            TextView tt4 = (TextView) view.findViewById(R.id.done_count_disp);
            TextView tt5 = (TextView) view.findViewById(R.id.done_count_solo);
            if (tt1 != null) {
                tt1.setText(day.getDate());
            }
            if (tt2 != null) {
                tt2.setText(String.format(Locale.ROOT, "%d", Integer.valueOf(day.getNewNumber())));
            }
            if (tt3 != null) {
                tt3.setText(String.format(Locale.ROOT, "%d", Integer.valueOf(day.getDismissNumber())));
            }
            if (tt4 != null) {
                tt4.setText(String.format(Locale.ROOT, "%d", Integer.valueOf(day.getDoneNumberDispatcher())));
            }
            if (tt5 != null) {
                tt5.setText(String.format(Locale.ROOT, "%d", Integer.valueOf(day.getDoneNumberSolo())));
            }
            if (day.getNewNumber() == 0) {
                view.setBackgroundColor(-3355444);
            } else {
                view.setBackgroundColor(-1);
            }
        }
        return view;
    }
}
