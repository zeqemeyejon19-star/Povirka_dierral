package com.poverka.httpFileClient.util;

import java.util.Calendar;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class MySender {
    private static final boolean D = true;
    private static final String TAG = "SENDER";
    private final TcpClient tcpClient;
    private String stringToSend = "";
    private String sentString = "";

    public MySender(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    public void startTestPhoto() {
        this.stringToSend = "{\"test_photo\":1,\"request\":1}\n";
    }

    public void readDateTime(int photoType, int writeLog) {
        this.stringToSend = String.format(Locale.ROOT, "{\"phototype\":%d,\"writelog\":%d,\"request\":%d}\n", Integer.valueOf(photoType), Integer.valueOf(writeLog), 8912896);
    }

    public void setDateTime() {
        Calendar cal = Calendar.getInstance();
        int currentDateUnix = (int) (cal.getTime().getTime() / 1000);
        this.stringToSend = String.format(Locale.ROOT, "{\"date_time\":%d,\"request\":%d}\n", Integer.valueOf(currentDateUnix), 524288);
    }

    public void startReadingState() {
        this.stringToSend = String.format(Locale.ROOT, "{\"request\":%d}\n", 22080256);
    }

    public void writeTestName(int testName, int multiplier) {
        this.stringToSend = String.format(Locale.ROOT, "{\"test_name\":%d,\"multiplier\":%d,\"request\":%d}\n", Integer.valueOf(testName), Integer.valueOf(multiplier), 16777218);
    }

    public void readAction() {
        this.stringToSend = "{\"request\":16}\n";
    }

    public void changeAction(int action) {
        this.stringToSend = String.format(Locale.ROOT, "{\"action\":%d,\"request\":16}\n", Integer.valueOf(action));
    }

    public void prepareMeasurement(int measNumb, int reitNumb) {
        this.stringToSend = String.format(Locale.ROOT, "{\"meas_numb\":%d,\"reit_numb\":%d,\"action\":2,\"request\":524316}\n", Integer.valueOf(measNumb), Integer.valueOf(reitNumb));
    }

    public void readMeasuringData() {
        this.stringToSend = "{\"request\":241}\n";
    }

    public void sendFinish() {
        this.stringToSend = "{\"finish\":1,\"request\":1024}\n";
    }

    public void checkFinish() {
        this.stringToSend = "{\"request\":1024}\n";
    }

    public void updateFirmware() {
        this.stringToSend = String.format(Locale.ROOT, "{\"update_soft\":true,\"request\":%d}\n", 2097152);
    }

    public void setSleepTimer(int time) {
        this.stringToSend = String.format(Locale.ROOT, "{\"powerOnOff\":%d,\"request\":%d}\n", Integer.valueOf(time), 4096);
    }

    public void getAll() {
        this.stringToSend = String.format(Locale.ROOT, "{\"request\":%d}\n", 5826332);
    }

    public void restoreTestSettings(int testName, int multiplier, int meas, int reit) {
        Locale locale = Locale.ROOT;
        Object[] objArr = new Object[5];
        objArr[0] = Integer.valueOf(testName);
        objArr[1] = Integer.valueOf(multiplier);
        objArr[2] = Integer.valueOf(meas > 0 ? meas : 1);
        objArr[3] = Integer.valueOf(reit);
        objArr[4] = 65566;
        this.stringToSend = String.format(locale, "{\"test_name\":%d,\"multiplier\":%d,\"meas_numb\":%d,\"reit_numb\":%d,\"action\":2,\"request\":%d}\n", objArr);
    }

    public void createSendString(String str) {
        this.stringToSend = str;
    }

    public void repeatLastMessage() {
        this.stringToSend = this.sentString;
    }

    public String getLastMessage() {
        return this.sentString;
    }

    public void send() {
        if (!this.stringToSend.isEmpty()) {
            String str = this.stringToSend;
            this.sentString = str;
            this.tcpClient.sendMessage(str);
            this.stringToSend = "";
        }
    }
}
