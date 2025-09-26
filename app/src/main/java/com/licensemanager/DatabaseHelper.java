package com.licensemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "license_manager.db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    public static final String TABLE_LICENSES = "licenses";

    // Column names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_EXPIRY_DATE = "expiry_date";
    public static final String COLUMN_DESCRIPTION = "description";

    // Create table SQL
    private static final String CREATE_TABLE_LICENSES = 
        "CREATE TABLE " + TABLE_LICENSES + "(" +
        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        COLUMN_NAME + " TEXT NOT NULL," +
        COLUMN_TYPE + " TEXT NOT NULL," +
        COLUMN_EXPIRY_DATE + " TEXT NOT NULL," +
        COLUMN_DESCRIPTION + " TEXT" +
        ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LICENSES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LICENSES);
        onCreate(db);
    }

    // CRUD Operations

    // Create - Insert a new license
    public long insertLicense(License license) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_NAME, license.getName());
        values.put(COLUMN_TYPE, license.getType());
        values.put(COLUMN_EXPIRY_DATE, license.getExpiryDate());
        values.put(COLUMN_DESCRIPTION, license.getDescription());
        
        long id = db.insert(TABLE_LICENSES, null, values);
        db.close();
        return id;
    }

    // Read - Get a single license by ID
    public License getLicense(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_LICENSES,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_TYPE, COLUMN_EXPIRY_DATE, COLUMN_DESCRIPTION},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);
        
        License license = null;
        if (cursor != null && cursor.moveToFirst()) {
            license = new License(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)
            );
            cursor.close();
        }
        
        db.close();
        return license;
    }

    // Read - Get all licenses
    public List<License> getAllLicenses() {
        List<License> licenseList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_LICENSES + " ORDER BY " + COLUMN_EXPIRY_DATE + " ASC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                License license = new License(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
                licenseList.add(license);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return licenseList;
    }

    // Update - Update an existing license
    public int updateLicense(License license) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, license.getName());
        values.put(COLUMN_TYPE, license.getType());
        values.put(COLUMN_EXPIRY_DATE, license.getExpiryDate());
        values.put(COLUMN_DESCRIPTION, license.getDescription());
        
        int rowsUpdated = db.update(TABLE_LICENSES, values,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(license.getId())});
        
        db.close();
        return rowsUpdated;
    }

    // Delete - Delete a license
    public void deleteLicense(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LICENSES,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // Search licenses by name or type
    public List<License> searchLicenses(String query) {
        List<License> licenseList = new ArrayList<>();
        
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_LICENSES + 
                " WHERE " + COLUMN_NAME + " LIKE ? OR " + COLUMN_TYPE + " LIKE ?" +
                " ORDER BY " + COLUMN_EXPIRY_DATE + " ASC";
        
        String searchQuery = "%" + query + "%";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{searchQuery, searchQuery});
        
        if (cursor.moveToFirst()) {
            do {
                License license = new License(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
                licenseList.add(license);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return licenseList;
    }

    // Get count of licenses by status
    public int getLicenseCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_LICENSES, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
}