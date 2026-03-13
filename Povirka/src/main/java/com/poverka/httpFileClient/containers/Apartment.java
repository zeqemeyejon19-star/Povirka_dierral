package com.poverka.httpFileClient.containers;

/* JADX INFO: loaded from: classes2.dex */
public class Apartment implements Comparable<Apartment> {
    private String address;
    private int apartment;
    private String apartmentB;
    private int building;
    private String buildingB;
    private String buildingK;
    private int cityId;
    private int countClosed;
    private int countTotal;
    private int streetId;
    private String surname;
    private String time;

    public Apartment(int cityId, int streetId, int building, String buildingB, String buildingK, int apartment, String apartmentB) {
        this.cityId = cityId;
        this.streetId = streetId;
        this.building = building;
        this.buildingB = buildingB;
        this.buildingK = buildingK;
        this.apartment = apartment;
        this.apartmentB = apartmentB;
    }

    public Apartment(int cityId, int streetId, int building, String buildingB, String buildingK, int apartment, String apartmentB, String address, String time, String surname, int countTotal, int countClosed) {
        this.cityId = cityId;
        this.streetId = streetId;
        this.building = building;
        this.buildingB = buildingB;
        this.buildingK = buildingK;
        this.apartment = apartment;
        this.apartmentB = apartmentB;
        this.address = address;
        this.time = time;
        this.surname = surname;
        this.countTotal = countTotal;
        this.countClosed = countClosed;
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

    public String getBuildingB() {
        return this.buildingB;
    }

    public String getBuildingK() {
        return this.buildingK;
    }

    public int getApartment() {
        return this.apartment;
    }

    public String getApartmentB() {
        return this.apartmentB;
    }

    public String getAddress() {
        return this.address;
    }

    public String getTime() {
        return this.time;
    }

    public String getSurname() {
        return this.surname;
    }

    public int getCountTotal() {
        return this.countTotal;
    }

    public int getCountClosed() {
        return this.countClosed;
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        Apartment other = (Apartment) obj;
        return this.cityId == other.cityId && this.streetId == other.streetId && this.building == other.building && this.buildingB.equals(other.buildingB) && this.buildingK.equals(other.buildingK) && this.apartment == other.apartment && this.apartmentB.equals(other.apartmentB);
    }

    public int hashCode() {
        return (this.streetId * 1000) + this.building;
    }

    @Override // java.lang.Comparable
    public int compareTo(Apartment object) {
        if (object.time == null) {
            return this.time == null ? 0 : 1;
        }
        int h1 = this.time.split(":").length >= 1 ? Integer.parseInt(this.time.split(":")[0]) : 0;
        int m1 = this.time.split(":").length >= 2 ? Integer.parseInt(this.time.split(":")[1]) : 0;
        int s1 = this.time.split(":").length >= 3 ? Integer.parseInt(this.time.split(":")[2]) : 0;
        int h2 = object.time.split(":").length >= 1 ? Integer.parseInt(object.time.split(":")[0]) : 0;
        int m2 = object.time.split(":").length >= 2 ? Integer.parseInt(object.time.split(":")[1]) : 0;
        int s2 = object.time.split(":").length >= 3 ? Integer.parseInt(object.time.split(":")[2]) : 0;
        if (h1 > h2) {
            return 1;
        }
        if (h1 < h2) {
            return -1;
        }
        if (m1 > m2) {
            return 1;
        }
        if (m1 < m2) {
            return -1;
        }
        return Integer.compare(s1, s2);
    }
}
