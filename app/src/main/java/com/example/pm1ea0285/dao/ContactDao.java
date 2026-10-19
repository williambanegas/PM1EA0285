package com.example.pm1ea0285.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pm1ea0285.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactDao {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public ContactDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertContact(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, contact.getName());
        values.put(DatabaseHelper.COLUMN_COUNTRY_CODE, contact.getCountryCode());
        values.put(DatabaseHelper.COLUMN_PHONE, contact.getPhone());
        values.put(DatabaseHelper.COLUMN_NOTE, contact.getNote());
        values.put(DatabaseHelper.COLUMN_PHOTO_PATH, contact.getPhotoPath());
        return database.insert(DatabaseHelper.TABLE_CONTACTS, null, values);
    }

    public int updateContact(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, contact.getName());
        values.put(DatabaseHelper.COLUMN_COUNTRY_CODE, contact.getCountryCode());
        values.put(DatabaseHelper.COLUMN_PHONE, contact.getPhone());
        values.put(DatabaseHelper.COLUMN_NOTE, contact.getNote());
        values.put(DatabaseHelper.COLUMN_PHOTO_PATH, contact.getPhotoPath());
        return database.update(
                DatabaseHelper.TABLE_CONTACTS,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(contact.getId())}
        );
    }

    public void deleteContact(int id) {
        database.delete(
                DatabaseHelper.TABLE_CONTACTS,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_CONTACTS,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_NAME + " ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                contacts.add(cursorToContact(cursor));
            }
            cursor.close();
        }
        return contacts;
    }

    public List<Contact> searchContacts(String query) {
        List<Contact> contacts = new ArrayList<>();
        String selection = DatabaseHelper.COLUMN_NAME + " LIKE ? OR " + DatabaseHelper.COLUMN_PHONE + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%", "%" + query + "%"};

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_CONTACTS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                DatabaseHelper.COLUMN_NAME + " ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                contacts.add(cursorToContact(cursor));
            }
            cursor.close();
        }
        return contacts;
    }

    public Contact getContactById(int id) {
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_CONTACTS,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        Contact contact = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contact = cursorToContact(cursor);
            }
            cursor.close();
        }
        return contact;
    }

    public List<String> getAllCountries() {
        List<String> countries = new ArrayList<>();
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_COUNTRIES,
                new String[]{DatabaseHelper.COLUMN_COUNTRY_NAME, DatabaseHelper.COLUMN_CODE},
                null, null, null, null,
                DatabaseHelper.COLUMN_COUNTRY_NAME + " ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COUNTRY_NAME));
                String code = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CODE));
                countries.add(name + " (" + code + ")");
            }
            cursor.close();
        }
        return countries;
    }

    public long insertCountry(String name, String code) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_COUNTRY_NAME, name);
        values.put(DatabaseHelper.COLUMN_CODE, code);
        return database.insert(DatabaseHelper.TABLE_COUNTRIES, null, values);
    }

    private Contact cursorToContact(Cursor cursor) {
        Contact contact = new Contact();
        contact.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
        contact.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)));
        contact.setCountryCode(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COUNTRY_CODE)));
        contact.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE)));
        contact.setNote(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE)));
        contact.setPhotoPath(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHOTO_PATH)));
        return contact;
    }
}
