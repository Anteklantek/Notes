package com.example.antek.notes10clouds.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.antek.notes10clouds.models.Note;

import java.util.Calendar;
import java.util.Date;


public class DatabaseAdapter {
    private static final String DEBUG_TAG = "SqLiteDatabaseManager";

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "database.db";
    public static final String DB_NOTES_TABLE = "notes";

    public static final String KEY_ID = "_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int ID_COLUMN = 0;
    public static final String KEY_BODY = "body";
    public static final String BODY_OPTIONS = "TEXT NOT NULL";
    public static final int BODY_COLUMN = 1;
    public static final String KEY_DATE = "date";
    public static final String DATE_OPTIONS = "LONG DEFAULT 0";
    public static final int DATE_COLUMN = 2;

    private static final String DB_CREATE_TODO_TABLE =
            "CREATE TABLE " + DB_NOTES_TABLE + "( " +
                    KEY_ID + " " + ID_OPTIONS + ", " +
                    KEY_BODY + " " + BODY_OPTIONS + ", " +
                    KEY_DATE + " " + DATE_OPTIONS +
                    ");";
    private static final String DROP_TODO_TABLE =
            "DROP TABLE IF EXISTS " + DB_NOTES_TABLE;

    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper dbHelper;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_TODO_TABLE);

            Log.d(DEBUG_TAG, "Database creating...");
            Log.d(DEBUG_TAG, "Table " + DB_NOTES_TABLE + " ver." + DB_VERSION + " created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_TODO_TABLE);

            Log.d(DEBUG_TAG, "Database updating...");
            Log.d(DEBUG_TAG, "Table " + DB_NOTES_TABLE + " updated from ver." + oldVersion + " to ver." + newVersion);
            Log.d(DEBUG_TAG, "All data is lost.");

            onCreate(db);
        }
    }

    public DatabaseAdapter(Context context) {
        this.context = context;
    }

    public DatabaseAdapter open(){
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long insertNote(String body) {
        ContentValues newNoteValues = new ContentValues();
        newNoteValues.put(KEY_BODY, body);
        Calendar calendar = Calendar.getInstance();
        newNoteValues.put(KEY_DATE, calendar.getTimeInMillis());
        return db.insert(DB_NOTES_TABLE, null, newNoteValues);
    }

    public boolean updateNote(Note note) {
        long id = note.getId();
        String body = note.getBody();
        Date date = note.getDate();
        return updateNote(id, body, date);
    }

    public boolean updateNote(long id, String body, Date date) {
        String where = KEY_ID + "=" + id;
        ContentValues updateNoteValues = new ContentValues();
        updateNoteValues.put(KEY_BODY, body);
        updateNoteValues.put(KEY_DATE, date.getTime());
        return db.update(DB_NOTES_TABLE, updateNoteValues, where, null) > 0;
    }

    public boolean deleteNote(long id){
        String where = KEY_ID + "=" + id;
        return db.delete(DB_NOTES_TABLE, where, null) > 0;
    }

    public int deleteAllNotes(){
        return db.delete(DB_NOTES_TABLE, null, null);
    }

    public Cursor getAllNotes() {
        String[] columns = {KEY_ID, KEY_BODY, KEY_DATE};
        return db.query(DB_NOTES_TABLE, columns, null, null, null, null, null);
    }

    public Note getNote(long id) {
        String[] columns = {KEY_ID, KEY_BODY, KEY_DATE};
        String where = KEY_ID + "=" + id;
        Cursor cursor = db.query(DB_NOTES_TABLE, columns, where, null, null, null, null);
        Note note = null;
        if(cursor != null && cursor.moveToFirst()) {
            String description = cursor.getString(BODY_COLUMN);
            long completed = cursor.getLong(DATE_COLUMN);
            Date date = new Date(completed);
            note = new Note(id, description, date);
        }
        return note;
    }
}
