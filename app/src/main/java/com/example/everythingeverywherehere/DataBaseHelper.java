package com.example.everythingeverywherehere;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String SEARCHED_PRODUCT_TABLE = "SEARCHED_PRODUCT_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_JSON_OBJECT = "JSON_OBJECT";
    public static final String COLUMN_KEYWORD = "KEYWORD";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "searchedProducts.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + SEARCHED_PRODUCT_TABLE + " (" + COLUMN_ID + " TEXT, " + COLUMN_JSON_OBJECT + " TEXT)";
        db.execSQL(createTableStatement);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addProduct(String keyWord, String product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID, keyWord);
        cv.put(COLUMN_JSON_OBJECT, product);
        String queryString = "SELECT * FROM " + SEARCHED_PRODUCT_TABLE;
        Cursor cursor = db.query(SEARCHED_PRODUCT_TABLE, new String[]{COLUMN_ID, COLUMN_JSON_OBJECT}, COLUMN_ID + " LIKE ?", new String[]{keyWord}, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToNext();
            Log.i("HELPER", "" + cursor.getString(0));
            int rowAffected = db.update(SEARCHED_PRODUCT_TABLE, cv, COLUMN_ID + " LIKE ?", new String[]{keyWord});
            Log.i("HELPER", "" + rowAffected);
        } else {
            long insert = db.insert(SEARCHED_PRODUCT_TABLE, null, cv);
            if (insert == -1) {
                return false;
            }
        }
        return true;
    }

    public List<String> getkeyWord() {
        List<String> keyWordList = new ArrayList<>();
        String queryString = "SELECT * FROM " + SEARCHED_PRODUCT_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            do {
                String keyWord = cursor.getString(0);
                keyWordList.add(keyWord);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return keyWordList;
    }

    public String getDataById(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(SEARCHED_PRODUCT_TABLE, new String[]{COLUMN_ID, COLUMN_JSON_OBJECT}, COLUMN_ID + " LIKE ?", new String[]{id}, null, null, null, null);
        cursor.moveToNext();
        return cursor.getString(1);

    }

}

