package com.poverka.httpFileClient.activity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.poverka.httpFileClient.measurement.CounterVerification;

/* JADX INFO: loaded from: classes2.dex */
public class ExitService extends Service {
    CounterVerification mVerification;

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onTaskRemoved(Intent rootIntent) {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! onTaskRemoved called !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }
}
