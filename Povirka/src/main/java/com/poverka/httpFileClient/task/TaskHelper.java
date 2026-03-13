package com.poverka.httpFileClient.task;

import com.poverka.httpFileClient.containers.Apartment;
import com.poverka.httpFileClient.containers.Day;
import com.poverka.httpFileClient.containers.Task;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/* JADX INFO: loaded from: classes2.dex */
public class TaskHelper {
    private final List<Task> tasks;

    public TaskHelper(List<Task> list) {
        this.tasks = list;
    }

    public List<Day> getTasksByDay() {
        TaskHelper taskHelper = this;
        Set<String> uniqueDates = new HashSet<>();
        Iterator<Task> it = taskHelper.tasks.iterator();
        while (it.hasNext()) {
            uniqueDates.add(it.next().getDate());
        }
        int i = 0;
        String[] dates = (String[]) uniqueDates.toArray(new String[0]);
        List<Day> result = new ArrayList<>();
        int length = dates.length;
        while (i < length) {
            String date = dates[i];
            int countNew = 0;
            int countDismiss = 0;
            int countDoneDisp = 0;
            int countDoneSolo = 0;
            for (Task t : taskHelper.tasks) {
                if (t.getDate().equals(date)) {
                    int status = t.getStatus();
                    if (status == 0) {
                        countNew++;
                    } else if (status == 1) {
                        countDismiss++;
                    } else if (status == 2) {
                        if (t.getId() == -1) {
                            countDoneSolo++;
                        } else {
                            countDoneDisp++;
                        }
                    }
                }
            }
            result.add(new Day(date, countNew, countDismiss, countDoneDisp, countDoneSolo));
            i++;
            taskHelper = this;
        }
        Collections.sort(result);
        return result;
    }

    public List<Apartment> getTasksOfDay(String date) {
        TaskHelper taskHelper = this;
        String str = date;
        Set<Apartment> uniqueApartments = new HashSet<>();
        for (Task t : taskHelper.tasks) {
            if (t.getDate().equals(str)) {
                uniqueApartments.add(new Apartment(t.getCityId(), t.getStreetId(), t.getBuilding(), t.getBuildingBuk(), t.getBuildingCorp(), t.getApartment(), t.getApartmentBuk()));
            }
        }
        Apartment[] apartments = (Apartment[]) uniqueApartments.toArray(new Apartment[0]);
        List<Apartment> result = new ArrayList<>();
        int length = apartments.length;
        int i = 0;
        while (i < length) {
            Apartment apartment = apartments[i];
            String address = "";
            String time = "";
            String surname = "";
            int countTotal = 0;
            int countClosed = 0;
            for (Task t2 : taskHelper.tasks) {
                if (t2.getCityId() == apartment.getCityId() && t2.getStreetId() == apartment.getStreetId() && t2.getBuilding() == apartment.getBuilding() && t2.getBuildingBuk().equals(apartment.getBuildingB())) {
                    if (t2.getBuildingCorp().equals(apartment.getBuildingK()) && t2.getApartment() == apartment.getApartment() && t2.getApartmentBuk().equals(apartment.getApartmentB()) && t2.getDate().equals(str)) {
                        String address2 = String.format(Locale.ROOT, "%s, кв. %s", t2.getFullAddress(), t2.getFullApartment());
                        String time2 = t2.getTime();
                        String surname2 = t2.getSurname();
                        if (t2.getId() != -1) {
                            countTotal++;
                        }
                        if (t2.getStatus() == 0) {
                            address = address2;
                            time = time2;
                            surname = surname2;
                        } else {
                            countClosed++;
                            address = address2;
                            time = time2;
                            surname = surname2;
                        }
                    }
                }
                str = date;
            }
            result.add(new Apartment(apartment.getCityId(), apartment.getStreetId(), apartment.getBuilding(), apartment.getBuildingB(), apartment.getBuildingK(), apartment.getApartment(), apartment.getApartmentB(), address, time, surname, countTotal, countClosed));
            i++;
            taskHelper = this;
            str = date;
        }
        Collections.sort(result);
        return result;
    }

    public List<Task> getTasksOfApartment(String date, int cityId, int streetId, int building, String buildingB, String buildingK, int apartment, String apartmentB) {
        List<Task> result = new ArrayList<>();
        for (Task t : this.tasks) {
            if (t.getCityId() == cityId && t.getStreetId() == streetId && t.getBuilding() == building && t.getBuildingBuk().equals(buildingB) && t.getBuildingCorp().equals(buildingK) && t.getApartment() == apartment && t.getApartmentBuk().equals(apartmentB) && t.getDate().equals(date)) {
                result.add(t);
            }
        }
        return result;
    }
}
