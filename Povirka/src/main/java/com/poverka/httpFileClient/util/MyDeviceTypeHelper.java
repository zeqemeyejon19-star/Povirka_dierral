package com.poverka.httpFileClient.util;

import android.content.ContextWrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class MyDeviceTypeHelper {
    public static boolean isTypeExist(ContextWrapper context, String char1, String char2) throws JSONException {
        JSONArray jsonArray = readTypes(context);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            if (item.getString("char1").equals(char1) && item.getString("char2").equals(char2)) {
                return true;
            }
        }
        return false;
    }

    public static String[] getNamesByChars(ContextWrapper context, String char1, String char2) throws JSONException {
        JSONArray jsonArray = readTypes(context);
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            if (item.getString("char1").equals(char1) && item.getString("char2").equals(char2)) {
                names.add(item.getString("name"));
            }
        }
        return (String[]) new HashSet(names).toArray(new String[0]);
    }

    public static String[] getDNsByNameAndChars(ContextWrapper context, String name, String char1, String char2) throws JSONException {
        JSONArray jsonArray = readTypes(context);
        ArrayList<String> DNs = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            if (item.getString("name").equals(name) && item.getString("char1").equals(char1) && item.getString("char2").equals(char2)) {
                DNs.add(item.getString("dn"));
            }
        }
        return (String[]) new HashSet(DNs).toArray(new String[0]);
    }

    public static Integer[] getVendorIdsByAll(ContextWrapper context, String dn, String name, String char1, String char2) throws JSONException {
        JSONArray jsonArray = readTypes(context);
        ArrayList<Integer> vendorIDs = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            if (item.getString("dn").equals(dn) && item.getString("name").equals(name) && item.getString("char1").equals(char1) && item.getString("char2").equals(char2)) {
                vendorIDs.add(Integer.valueOf(item.getInt("vendorId")));
            }
        }
        int i2 = vendorIDs.size();
        if (i2 == 0) {
            return new Integer[]{0};
        }
        return (Integer[]) vendorIDs.toArray(new Integer[1]);
    }

    public static String[] getVendorsByIDs(ContextWrapper context, Integer[] ids) throws JSONException {
        JSONArray jsonArray = readVendors(context);
        ArrayList<String> vendors = new ArrayList<>();
        for (Integer num : ids) {
            int id = num.intValue();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                if (item.getInt("id") == id) {
                    vendors.add(item.getString("name"));
                }
            }
        }
        if (vendors.size() == 0) {
            return new String[]{""};
        }
        return (String[]) vendors.toArray(new String[1]);
    }

    public static String getVendorById(ContextWrapper context, int id) throws JSONException {
        JSONArray jsonArray = readVendors(context);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            if (item.getInt("id") == id) {
                return item.getString("name");
            }
        }
        return "";
    }

    public static int getVendorIdByName(ContextWrapper context, String name) {
        try {
            JSONArray jsonArray = readVendors(context);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                if (item.getString("name").equals(name)) {
                    return item.getInt("id");
                }
            }
            return 0;
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static JSONObject getJsonById(ContextWrapper context, int id) {
        try {
            JSONArray jsonArray = readTypes(context);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                if (item.getInt("id") == id) {
                    return item;
                }
            }
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int typeToID(ContextWrapper context, String dn, String name, String char1, String char2, int vendorId) {
        try {
            JSONArray jsonArray = readTypes(context);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                if (item.getString("dn").equals(dn) && item.getString("name").equals(name) && item.getString("char1").equals(char1) && item.getString("char2").equals(char2) && item.getInt("vendorId") == vendorId) {
                    return item.getInt("id");
                }
            }
            return 0;
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static JSONArray readTypes(ContextWrapper context) throws JSONException {
        String text = MyFileReader.readAndroidFile(context.getFilesDir(), "types.json");
        JSONObject json = new JSONObject(text);
        return json.getJSONArray("deviceTypes");
    }

    public static JSONArray readVendors(ContextWrapper context) throws JSONException {
        String text = MyFileReader.readAndroidFile(context.getFilesDir(), "types.json");
        JSONObject json = new JSONObject(text);
        return json.getJSONArray("vendors");
    }

    private static void sortDNs(ArrayList<String> list) {
        Collections.sort(list, new Comparator<String>() { // from class: com.poverka.httpFileClient.util.MyDeviceTypeHelper.1
            @Override // java.util.Comparator
            public int compare(String o1, String o2) {
                if (o1.compareTo(o2) < 0) {
                    return -1;
                }
                if (o1.compareTo(o2) > 0) {
                    return 1;
                }
                return doSecondaryOrderSort(o1, o2);
            }

            int doSecondaryOrderSort(String o1, String o2) {
                return Integer.compare(o1.compareTo(o2), 0);
            }
        });
    }
}
