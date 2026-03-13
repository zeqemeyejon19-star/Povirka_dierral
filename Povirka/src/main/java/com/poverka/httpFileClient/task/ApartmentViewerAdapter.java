package com.poverka.httpFileClient.task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.poverka.httpFileClient.R;
import com.poverka.httpFileClient.containers.Apartment;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class ApartmentViewerAdapter extends ArrayAdapter<Apartment> {
    private final Context mContext;
    private final int resourceLayout;

    public ApartmentViewerAdapter(Context context, int resource, List<Apartment> items) {
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
        Apartment apartment = getItem(position);
        if (apartment != null) {
            TextView tt1 = (TextView) view.findViewById(R.id.time);
            TextView tt2 = (TextView) view.findViewById(R.id.address);
            TextView tt3 = (TextView) view.findViewById(R.id.name);
            TextView tt4 = (TextView) view.findViewById(R.id.count);
            if (tt1 != null) {
                tt1.setText(apartment.getTime().substring(0, 5));
            }
            if (tt2 != null) {
                tt2.setText(apartment.getAddress());
            }
            if (tt3 != null) {
                tt3.setText(apartment.getSurname());
            }
            if (tt4 != null) {
                tt4.setText(String.format(Locale.ROOT, "%d / %d", Integer.valueOf(apartment.getCountTotal()), Integer.valueOf(apartment.getCountClosed())));
            }
            if (apartment.getCountClosed() >= apartment.getCountTotal()) {
                view.setBackgroundColor(-3355444);
            } else {
                view.setBackgroundColor(-1);
            }
        }
        return view;
    }
}
