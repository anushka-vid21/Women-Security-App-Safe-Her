package com.devdroid.cardviewexmp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import android.database.Cursor;
import android.util.Log;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class MyDBHelper extends SQLiteOpenHelper {
    //database info
    private static final String DATABASE_NAME = "ContactsDB";
    private static final int DATABASE_ID = 1;
    //table name
    public static final String TABLE_NAME = "contacts";
    //column name
    public static final String KEY_ID = "id";
    public static final String KEY_PHONE_NO = "phone_no";

    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_ID);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_PHONE_NO + " TEXT)";
        //
        db.execSQL(CREATE_TABLE);
        Log.d("DatabaseHelper", "Database created successfully with table: " + TABLE_NAME);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    public void addContact(String phone_no) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PHONE_NO, phone_no);

        db.insert(TABLE_NAME, null, values);
        db.close();//new added

    }


    // insert vala neeche jo hai vohh yaha thaa
    // Method to retrieve all contacts
    public Cursor getAllContacts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, new String[]{KEY_ID, KEY_PHONE_NO},
                null, null, null, null, null);
    }

    // Method to clear all contacts from the database
    public void clearContacts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
        Log.d("DatabaseHelper", "All contacts have been cleared");
    }

    public boolean insertContacts(String contact1, String contact2, String contact3, String contact4, String contact5) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Clear existing contacts before inserting new ones
        db.delete(TABLE_NAME, null, null);

        boolean success = true; // Track insertion success

        // Insert each contact if it's not empty
        String[] contacts = {contact1, contact2, contact3, contact4, contact5};
        for (String contact : contacts) {
            if (contact != null && !contact.isEmpty()) {
                ContentValues values = new ContentValues();
                values.put(KEY_PHONE_NO, contact);
                long result = db.insert(TABLE_NAME, null, values);
                if (result == -1) {
                    Log.e("DatabaseHelper", "Failed to insert contact: " + contact);
                    success = false;
                } else {
                    Log.d("DatabaseHelper", "Contact inserted successfully: " + contact);
                }
            }
        }

        db.close();
        return success;

    }
//    public List<String> getAllNonEmptyContacts() {
//        List<String> contacts = new ArrayList<>();
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        // Query to select non-empty contacts
//        Cursor cursor = db.query(
//                TABLE_NAME,                      // Table name
//                new String[]{KEY_PHONE_NO},       // Columns to select
//                KEY_PHONE_NO + " IS NOT NULL AND " + KEY_PHONE_NO + " != ?", // Where clause
//                new String[]{""},                 // Where clause value for empty strings
//                null,                             // Group by
//                null,                             // Having
//                null                              // Order by
//        );
//
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                // Check for the existence of the column and retrieve the data
//                int index = cursor.getColumnIndex(KEY_PHONE_NO);
//                if (index != -1) { // Verify column index exists
//                    String contact = cursor.getString(index);
//                    contacts.add(contact);
//                }
//            }
//            cursor.close();
//        }
//
//        db.close();
//        return contacts;
//    }

    public List<String> getAllNonEmptyContacts() {
        // Assuming you fetch contacts here, and return a list
        List<String> contacts = new ArrayList<>();

        // Example database fetching logic
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("contacts", new String[]{"phone_no"},
                "contacts IS NOT NULL AND phone_no IS NOT NULL", null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String contact = cursor.getString(cursor.getColumnIndex("phone_no"));
                    if (contact != null && !contact.isEmpty()) {
                        contacts.add(contact);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        // Add logging to check if we have any non-empty contacts
        Log.d("DBHelper", "getAllNonEmptyContacts size: " + contacts.size());
        for (String contact : contacts) {
            Log.d("DBHelper", "Non-empty contact: " + contact);
        }

        return contacts;
    }



}



//    public boolean insertContact(String contact1, String contact2, String contact3, String contact4, String contact5) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        byte[] contact = new byte[0];
//        contentValues.put(KEY_PHONE_NO, contact);
//
//        // Insert the contact into the database
//        long result = db.insert(TABLE_NAME, null, contentValues);
//        db.close();
//        return result != -1; // Returns true if insert was successful
//
//    }


//    public boolean insertContact(String contact) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        //clear existing contacts before inserting new ones
//        db.delete(TABLE_NAME, null, null);
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(KEY_PHONE_NO, contact);
//
//        // Insert the contact into the database
//        long result = db.insert(TABLE_NAME, null, contentValues);
//        db.close();
//        return result != -1; // Returns true if insert was successful
//    }
//7276084054    7887902246