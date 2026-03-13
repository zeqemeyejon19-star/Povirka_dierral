package com.poverka.httpFileClient.containers;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class Task {
    public static final int DISMISSED = 1;
    public static final int DONE = 2;
    public static final int NEW = 0;
    private int apartment;
    private String apartmentBuk;
    private int building;
    private String buildingBuk;
    private String buildingCorp;
    private int cityId;
    private String counterNumber;
    private String date;
    private String dismissNote;
    private int etag;
    private String fullAddress;
    private float geoX;
    private float geoY;
    private int groupId;
    private int id;
    private int marshrutOrder;
    private String middleName;
    private String name;
    private String note;
    private String phone2;
    private String phone3;
    private String phoneMain;
    private int podezd;
    private String protocolNumber;
    private int serviceType;
    private int station;
    private String statusDate;
    private int statusId;
    private int streetId;
    private String surname;
    private String time;

    public Task(JSONObject json) {
        try {
            this.id = json.getInt("ID");
            this.groupId = json.optInt("ZAJAVKA_ID");
            this.surname = json.getString("SURNAME");
            this.name = json.optString("NAME");
            this.middleName = json.optString("MIDDLE_NAME");
            this.cityId = json.getInt("CITY");
            this.streetId = json.getInt("STREET");
            this.building = json.optInt("BUILDING");
            this.buildingBuk = json.getString("BUILDING_BUKVA");
            this.buildingCorp = json.getString("BUILDING_KORPUS");
            this.apartment = json.optInt("APARTMENT");
            this.apartmentBuk = json.getString("APARTMENT_BUKVA");
            this.fullAddress = json.getString("FULL_ADDRESS");
            this.podezd = json.optInt("PODEZD");
            this.etag = json.optInt("ETAG");
            this.marshrutOrder = json.optInt("MARSHRUT_ORDER");
            this.phoneMain = json.getString("PHONE_NUMBER");
            this.phone2 = json.getString("PHONE_NUMBER_DOP");
            this.phone3 = json.getString("PHONE_NUMBER_DOP2");
            if (json.has("ZAJAVKA_DATE")) {
                this.date = json.getString("ZAJAVKA_DATE");
                this.time = json.getString("ZAJAVKA_TIME");
            } else if (json.has("PROTOCOL_DATE")) {
                this.date = json.getString("PROTOCOL_DATE");
                this.time = json.getString("PROTOCOL_TIME");
            }
            this.counterNumber = json.getString("COUNTER_NUMBER");
            this.serviceType = json.optInt("SERVICE_TYPE", 1);
            this.note = json.getString("NOTE");
            this.geoX = (float) json.optDouble("GEO_XX", 50.45466d);
            this.geoY = (float) json.optDouble("GEO_YY", 30.5238d);
            this.station = json.getInt("Z_NOMER");
            this.statusId = json.optInt("STATUS_ID", 0);
            this.statusDate = json.optString("STATUS_DATE", "01.01.1970");
            this.protocolNumber = json.optString("PROTOCOL", null);
            this.dismissNote = json.optString("DISMISS_NOTE", null);
        } catch (JSONException e) {
            Log.e("Task", "error in JSON constructor");
        }
    }

    public static List<Task> jsonStringToList(String str) throws JSONException {
        List<Task> result = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(str);
        for (int index = 0; index < jsonArray.length(); index++) {
            result.add(new Task(jsonArray.getJSONObject(index)));
        }
        return result;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("ID", this.id);
        json.put("ZAJAVKA_ID", this.groupId);
        json.put("SURNAME", this.surname);
        json.put("NAME", this.name);
        json.put("MIDDLE_NAME", this.middleName);
        json.put("CITY", this.cityId);
        json.put("STREET", this.streetId);
        json.put("BUILDING", this.building);
        json.put("BUILDING_BUKVA", this.buildingBuk);
        json.put("BUILDING_KORPUS", this.buildingCorp);
        json.put("APARTMENT", this.apartment);
        json.put("APARTMENT_BUKVA", this.apartmentBuk);
        json.put("FULL_ADDRESS", this.fullAddress);
        json.put("PODEZD", this.podezd);
        json.put("ETAG", this.etag);
        json.put("MARSHRUT_ORDER", this.marshrutOrder);
        json.put("PHONE_NUMBER", this.phoneMain);
        json.put("PHONE_NUMBER_DOP", this.phone2);
        json.put("PHONE_NUMBER_DOP2", this.phone3);
        json.put("ZAJAVKA_DATE", this.date);
        json.put("ZAJAVKA_TIME", this.time);
        json.put("COUNTER_NUMBER", this.counterNumber);
        json.put("SERVICE_TYPE", this.serviceType);
        json.put("NOTE", this.note);
        json.put("GEO_XX", this.geoX);
        json.put("GEO_YY", this.geoY);
        json.put("Z_NOMER", this.station);
        json.put("STATUS_ID", this.statusId);
        json.put("STATUS_DATE", this.statusDate);
        json.put("PROTOCOL", this.protocolNumber);
        json.put("DISMISS_NOTE", this.dismissNote);
        return json;
    }

    public String toString() {
        return "№" + this.id + ", ПІБ = " + this.surname + ", адреса = " + this.fullAddress + ", кв. = " + getFullApartment() + ", тел = " + getPhones() + ", час = " + this.time + ", тип послуги = " + (this.serviceType == 1 ? "ХВ" : "ГВ") + ", коментар = " + this.note;
    }

    public int getId() {
        return this.id;
    }

    public int getGroupId() {
        return this.groupId;
    }

    public String getSurname() {
        return this.surname;
    }

    public String getName() {
        return this.name;
    }

    public String getMiddleName() {
        return this.middleName;
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

    public String getBuildingBuk() {
        return this.buildingBuk;
    }

    public String getBuildingCorp() {
        return this.buildingCorp;
    }

    public int getApartment() {
        return this.apartment;
    }

    public String getApartmentBuk() {
        return this.apartmentBuk;
    }

    public String getFullApartment() {
        Locale locale = Locale.ROOT;
        Object[] objArr = new Object[2];
        objArr[0] = Integer.valueOf(this.apartment);
        objArr[1] = this.apartmentBuk.equals("null") ? "" : this.apartmentBuk;
        return String.format(locale, "%d %s", objArr);
    }

    public String getFullAddress() {
        return this.fullAddress;
    }

    public int getPodezd() {
        return this.podezd;
    }

    public int getEtag() {
        return this.etag;
    }

    public int getMarshrutOrder() {
        return this.marshrutOrder;
    }

    public String getPhones() {
        StringBuilder sb = new StringBuilder();
        if (!this.phoneMain.equals("null")) {
            sb.append(this.phoneMain);
        }
        if (!this.phone2.equals("null")) {
            sb.append(", ").append(this.phone2);
        }
        if (!this.phone3.equals("null")) {
            sb.append(", ").append(this.phone3);
        }
        return sb.toString();
    }

    public String getPhoneMain() {
        return this.phoneMain;
    }

    public String getPhone2() {
        return this.phone2;
    }

    public String getPhone3() {
        return this.phone3;
    }

    public String getDate() {
        return this.date;
    }

    public String getTime() {
        return this.time;
    }

    public String getCounterNumber() {
        return this.counterNumber;
    }

    public int getServiceType() {
        return this.serviceType;
    }

    public String getNote() {
        return this.note;
    }

    public float getGeoX() {
        return this.geoX;
    }

    public float getGeoY() {
        return this.geoY;
    }

    public int getStation() {
        return this.station;
    }

    public int getStatus() {
        return this.statusId;
    }

    public String getStatusDate() {
        return this.statusDate;
    }

    public String getProtocolNumber() {
        return this.protocolNumber;
    }

    public String getDismissNote() {
        return this.dismissNote;
    }

    public void setProtocol(String date, String protocolNumber, String counterNumber) {
        this.statusId = 2;
        this.statusDate = date;
        this.protocolNumber = protocolNumber;
        this.counterNumber = counterNumber;
    }

    public void setDismiss(String date, String dismissNote) {
        this.statusId = 1;
        this.statusDate = date;
        this.dismissNote = dismissNote;
    }

    public void setNew() {
        this.statusId = 0;
        this.statusDate = "01.01.1970";
        this.protocolNumber = null;
        this.dismissNote = null;
    }
}
