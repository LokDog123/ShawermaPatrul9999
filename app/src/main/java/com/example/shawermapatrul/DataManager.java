package com.example.shawermapatrul;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static List<Shawerma> records = new ArrayList<>();
    private static SharedPreferences prefs;
    private static Gson gson = new Gson();

    public static void init(Context context) {
        prefs = context.getSharedPreferences("ShawarmaPrefs", Context.MODE_PRIVATE);
        loadData();
    }

    public static List<Shawerma> getAllRecords() {
        return new ArrayList<>(records);
    }

    public static void addRecord(Shawerma record) {
        int maxId = 0;
        for (Shawerma r : records) {
            if (r.getId() > maxId) maxId = r.getId();
        }
        record.setId(maxId + 1);
        records.add(record);
        saveData();
    }

    public static void updateRecord(Shawerma updatedRecord) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getId() == updatedRecord.getId()) {
                records.set(i, updatedRecord);
                break;
            }
        }
        saveData();
    }

    public static void deleteRecord(int id) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getId() == id) {
                records.remove(i);
                break;
            }
        }
        saveData();
    }

    // ИЗМЕНЯЕМ private НА public - ЭТО ГЛАВНОЕ!
    public static void saveData() {
        if (prefs == null) return;

        String json = gson.toJson(records);
        prefs.edit().putString("shawarma_list", json).apply();

        android.util.Log.d("DataManager", "✅ Сохранено " + records.size() + " записей");
    }

    private static void loadData() {
        if (prefs == null) return;

        String json = prefs.getString("shawarma_list", "");

        if (json.isEmpty()) {
            records = new ArrayList<>();
            android.util.Log.d("DataManager", "Нет сохраненных данных");
        } else {
            Type type = new TypeToken<List<Shawerma>>(){}.getType();
            records = gson.fromJson(json, type);
            android.util.Log.d("DataManager", "✅ Загружено " + records.size() + " записей");
        }
    }

    public static void clearAll() {
        records.clear();
        saveData();
    }
}