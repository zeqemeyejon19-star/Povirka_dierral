package com.poverka.httpFileClient.containers;

/* JADX INFO: loaded from: classes2.dex */
public class Day implements Comparable<Day> {
    private String date;
    private int dismissNumber;
    private int doneNumberDispatcher;
    private int doneNumberSolo;
    private int newNumber;

    public Day(String date, int newNumber, int dismissNumber, int doneNumberDispatcher, int doneNumberSolo) {
        this.date = date;
        this.newNumber = newNumber;
        this.dismissNumber = dismissNumber;
        this.doneNumberDispatcher = doneNumberDispatcher;
        this.doneNumberSolo = doneNumberSolo;
    }

    public String getDate() {
        return this.date;
    }

    public int getNewNumber() {
        return this.newNumber;
    }

    public int getDismissNumber() {
        return this.dismissNumber;
    }

    public int getDoneNumberDispatcher() {
        return this.doneNumberDispatcher;
    }

    public int getDoneNumberSolo() {
        return this.doneNumberSolo;
    }

    @Override // java.lang.Comparable
    public int compareTo(Day object) {
        int d1 = this.date.split("\\.").length >= 1 ? Integer.parseInt(this.date.split("\\.")[0]) : 0;
        int m1 = this.date.split("\\.").length >= 2 ? Integer.parseInt(this.date.split("\\.")[1]) : 0;
        int y1 = this.date.split("\\.").length >= 3 ? Integer.parseInt(this.date.split("\\.")[2]) : 0;
        int d2 = object.date.split("\\.").length >= 1 ? Integer.parseInt(object.date.split("\\.")[0]) : 0;
        int m2 = object.date.split("\\.").length >= 2 ? Integer.parseInt(object.date.split("\\.")[1]) : 0;
        int y2 = object.date.split("\\.").length >= 3 ? Integer.parseInt(object.date.split("\\.")[2]) : 0;
        if (y1 > y2) {
            return 1;
        }
        if (y1 < y2) {
            return -1;
        }
        if (m1 > m2) {
            return 1;
        }
        if (m1 < m2) {
            return -1;
        }
        return Integer.compare(d1, d2);
    }
}
