package com.example.autocompletetextview;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
public class ItemsDbAdapter {
 
    public static final String KEY_ROWID = "_id";
    public static final String KEY_ITEM = "itemNumber";
    public static final String KEY_DESC = "description";
 
    private static final String TAG = "ItemsDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
 
    private static final String DATABASE_NAME = "ItemData";
    private static final String SQLITE_TABLE = "Items";
    private static final int DATABASE_VERSION = 1;
 
    private final Context mCtx;
 
    private static final String DATABASE_CREATE =
        "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
        KEY_ROWID + " integer PRIMARY KEY autoincrement," +
        KEY_ITEM + "," +
        KEY_DESC + "," +
        " UNIQUE (" + KEY_ITEM +"));";
 
    private static class DatabaseHelper extends SQLiteOpenHelper {
 
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
 
 
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }
 
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
            onCreate(db);
        }
    }
 
    public ItemsDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
 
    public ItemsDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
 
    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }
 
    public long createItem(String item, String description) {
 
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ITEM, item);
        initialValues.put(KEY_DESC, description);
 
        return mDb.insert(SQLITE_TABLE, null, initialValues);
    }
 
    public boolean deleteAllItems() {
 
        int doneDelete = 0;
        doneDelete = mDb.delete(SQLITE_TABLE, null , null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;
 
    }
 
    public Cursor fetchItemsByDesc(String inputText) throws SQLException {
        Log.w(TAG, inputText);
        Cursor mCursor = mDb.query(true, SQLITE_TABLE, new String[] {KEY_ROWID,
                KEY_ITEM, KEY_DESC}, KEY_DESC + " like '%" + inputText + "%'", null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
 
    }
 
}