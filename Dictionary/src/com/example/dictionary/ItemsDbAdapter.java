package com.example.dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ProgressBar;
 
public class ItemsDbAdapter {
 
    public static final String COL_ROWID = "_id";
    public static final String COL_WORD = "WORD";
    public static final String COL_DEFINITION = "DEFINITION";
 
    private static final String TAG = "DictionaryDatabase";
    private DatabaseHelper mDbHelper;
    static SQLiteDatabase mDb;
 
    private static final String DATABASE_NAME = "DICTIONARY";
    private static final String FTS_VIRTUAL_TABLE = "FTS";
    private static final int DATABASE_VERSION = 1;
 
    private final Context mCtx;
 
    private static final String DATABASE_CREATE =
            "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
            " USING fts3 (" +
            COL_ROWID + " integer PRIMARY KEY autoincrement,"+
            COL_WORD + ", " +
            COL_DEFINITION + ", "+
            " UNIQUE (" + COL_WORD +")"+")";
    /*        "CREATE TABLE if not exists " + FTS_VIRTUAL_TABLE + " (" +
    COL_ROWID + " integer PRIMARY KEY autoincrement," +
    COL_WORD + "," +
    COL_DEFINITION + "," +
    " UNIQUE (" + COL_WORD +"));";*/
 
    private static class DatabaseHelper extends SQLiteOpenHelper {
 
    	private final Context mHelperContext;
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mHelperContext = context;
        }
 
 
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
            loadDictionary();
        }
 
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
            onCreate(db);
        }
        
        
        private void loadDictionary() {
            new Thread(new Runnable() {
                public void run() {
                    try {
                    	Log.d(TAG, "loading dictionary");
                        MainActivity.p.setVisibility(ProgressBar.VISIBLE);
                        loadWords();
                        Log.d(TAG, "dictionary loaded");
                        MainActivity.p.setVisibility(ProgressBar.GONE);
                        
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }

        private void loadWords() throws IOException {
            final Resources resources = mHelperContext.getResources();
            InputStream inputStream = resources.openRawResource(R.raw.definitions);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            try {
                String line;
                String def="";
                while ((line = reader.readLine()) != null) {
                    String[] strings = TextUtils.split(line, " ");
                    if (strings.length < 2) continue;
                    def = "";
                    for(int i=1;i<strings.length;i++)
                    	def+=" "+strings[i];
                    long id = addWord(strings[0].trim(), def.trim());
                    if (id < 0) {
                        Log.d(TAG, "unable to add word: " + strings[0].trim());
                    }
                }
            } finally {
                reader.close();
            }
        }

        public long addWord(String word, String definition) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(COL_WORD, word);
            initialValues.put(COL_DEFINITION, definition);

            return mDb.insert(FTS_VIRTUAL_TABLE, null, initialValues);
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
// 
//    public long createItem(String item, String description) {
// 
//        ContentValues initialValues = new ContentValues();
//        initialValues.put(COL_WORD, item);
//        initialValues.put(COL_DEFINITION, description);
//        
//        return mDb.insert(FTS_VIRTUAL_TABLE, null, initialValues);
//    }
// 
//    public boolean deleteAllItems() {
// 
//        int doneDelete = 0;
//        doneDelete = mDb.delete(FTS_VIRTUAL_TABLE, null , null);
//        Log.w(TAG, Integer.toString(doneDelete));
//        return doneDelete > 0;
// 
//    }
 
    public Cursor fetchItemsByDesc(String inputText) throws SQLException {
        Log.w(TAG, inputText);
        Cursor mCursor = mDb.query(true, FTS_VIRTUAL_TABLE, new String[] {COL_ROWID,COL_WORD, COL_DEFINITION}, COL_WORD + " like '%" + inputText + "%'", null,
                null, null, null, null);
        if (mCursor != null) {
        	Log.w(TAG, "cursor not null");
            mCursor.moveToFirst();
        }
        else{
        	Log.w(TAG, "cursor null");
        }
        return mCursor;
 
    }
    

    
 
}