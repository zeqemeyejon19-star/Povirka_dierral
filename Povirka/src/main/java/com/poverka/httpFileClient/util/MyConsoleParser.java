package com.poverka.httpFileClient.util;

import java.util.ArrayList;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class MyConsoleParser {
    private static final boolean D = true;
    private static final String TAG = "PARSER";
    private JSONObject answerJSON;
    private String consoleHeader;
    private String firstLineString;
    private JSONObject requestJSON;
    private String receivedString = "";
    private ArrayList<String> receivedList = new ArrayList<>();
    private String currentDirectory = "";
    private String sentString = "";
    private String stringToRepeat = "";
    private String receivedCommand = "";
    private String receivedParams = "";
    private String requestedFile = "";
    private boolean isFirstLine = false;
    private boolean isLastLine = true;
    private int photoSize = 0;

    public MyConsoleParser(String header) {
        this.consoleHeader = header;
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x006f  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x011a  */
    /* JADX WARN: Removed duplicated region for block: B:79:0x0189  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void parse(java.lang.String r17) {
        /*
            Method dump skipped, instruction units count: 486
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.poverka.httpFileClient.util.MyConsoleParser.parse(java.lang.String):void");
    }

    public String getReceivedString() {
        return this.receivedString;
    }

    public JSONObject getRequestJSON() {
        return this.requestJSON;
    }

    public JSONObject getAnswerJSON() {
        return this.answerJSON;
    }

    public ArrayList<String> getReceivedList() {
        return this.receivedList;
    }

    public String getConsoleHeader() {
        return this.consoleHeader;
    }

    private void readConsoleHeader(String str) {
        if (str.startsWith("root") && str.indexOf(58) != -1) {
            this.consoleHeader = str.substring(0, str.indexOf(58));
        }
    }

    String getStringToRepeat() {
        return this.stringToRepeat;
    }

    void setSentString(String sentString) {
        if (sentString == null || sentString.length() == 0) {
            sentString = "";
        } else if (sentString.charAt(sentString.length() - 1) == '\n') {
            sentString = sentString.substring(0, sentString.length() - 1);
        }
        this.sentString = sentString;
        if (!sentString.equals("cat /home/1/current/s_to_a.json")) {
            this.stringToRepeat = sentString;
        }
    }

    public int getPhotoSize() {
        return this.photoSize;
    }

    public String getReceivedCommand() {
        return this.receivedCommand;
    }

    public String getRequestedFile() {
        return this.requestedFile;
    }

    public boolean isFirstLine() {
        return this.isFirstLine;
    }

    public boolean isLastLine() {
        return this.isLastLine;
    }

    void clearParser() {
        this.receivedString = "";
        this.receivedList.clear();
        this.currentDirectory = "";
        this.receivedCommand = "";
        this.receivedParams = "";
        this.requestedFile = "";
        this.firstLineString = "";
        this.isFirstLine = true;
        this.isLastLine = false;
        this.photoSize = 0;
        this.requestJSON = null;
        this.answerJSON = null;
    }

    public String getParserStuff() {
        Object[] objArr = new Object[5];
        objArr[0] = this.consoleHeader;
        objArr[1] = this.currentDirectory;
        objArr[2] = this.receivedCommand;
        objArr[3] = this.requestedFile;
        objArr[4] = this.isLastLine ? "Yes" : "No";
        return String.format("Header: %s, Dir: %s, Command: %s, File: %s, LastLine?: %s", objArr);
    }

    private int tryParse(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
