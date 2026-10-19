package com.example.pm1ea0285.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "contacts.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_CONTACTS = "contacts";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COUNTRY_CODE = "country_code";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_PHOTO_PATH = "photo_path";

    public static final String TABLE_COUNTRIES = "countries";
    public static final String COLUMN_COUNTRY_ID = "id";
    public static final String COLUMN_COUNTRY_NAME = "name";
    public static final String COLUMN_CODE = "code";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_CONTACTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_COUNTRY_CODE + " TEXT NOT NULL, " +
                    COLUMN_PHONE + " TEXT NOT NULL, " +
                    COLUMN_NOTE + " TEXT NOT NULL, " +
                    COLUMN_PHOTO_PATH + " TEXT" +
                    ");";

    private static final String TABLE_COUNTRIES_CREATE =
            "CREATE TABLE " + TABLE_COUNTRIES + " (" +
                    COLUMN_COUNTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_COUNTRY_NAME + " TEXT NOT NULL, " +
                    COLUMN_CODE + " TEXT NOT NULL" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        db.execSQL(TABLE_COUNTRIES_CREATE);

        // Pre-populate default countries
        db.execSQL("INSERT INTO " + TABLE_COUNTRIES + " (" + COLUMN_COUNTRY_NAME + ", " + COLUMN_CODE + ") VALUES ('Honduras', '+504');");
        db.execSQL("INSERT INTO " + TABLE_COUNTRIES + " (" + COLUMN_COUNTRY_NAME + ", " + COLUMN_CODE + ") VALUES ('Costa Rica', '+506');");
        db.execSQL("INSERT INTO " + TABLE_COUNTRIES + " (" + COLUMN_COUNTRY_NAME + ", " + COLUMN_CODE + ") VALUES ('El Salvador', '+503');");
        db.execSQL("INSERT INTO " + TABLE_COUNTRIES + " (" + COLUMN_COUNTRY_NAME + ", " + COLUMN_CODE + ") VALUES ('Guatemala', '+502');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUNTRIES);
        onCreate(db);
    }
}
