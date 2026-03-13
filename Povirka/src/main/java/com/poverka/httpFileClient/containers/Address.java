package com.poverka.httpFileClient.containers;

/* JADX INFO: loaded from: classes2.dex */
public class Address implements Comparable<Address> {
    private int id;
    private String name;

    public Address(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    @Override // java.lang.Comparable
    public int compareTo(Address address) {
        return this.name.compareTo(address.name);
    }
}
