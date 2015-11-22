package com.example.contactseditor.database;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.contactseditor.database.table.CountryCode;

public class DataBaseHelper extends SQLiteOpenHelper {

	// The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/com.example.contactseditor/databases/";

	private static String DB_NAME = "contactsEditor";

	private SQLiteDatabase myDataBase;

	private final Context myContext;

	public static final String TABLE_COUNTRY_CODE = "countrycode";
	public static final String COL_ROWID = "_id";
	public static final String COL_ISO_2 = "iso_2";
	public static final String COL_ISO_3 = "iso_3";
	public static final String COL_COUNTRY_NAME = "name";
	public static final String COL_DIALING_CODE = "calling_code";

	public static final String[] all_columns = { COL_ROWID, COL_ISO_2,
			COL_ISO_3, COL_COUNTRY_NAME, COL_DIALING_CODE };

	public static final int ID_TABLE_COUNTRY_CODE = 0;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DataBaseHelper(Context context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();

		if (dbExist) {
			// do nothing - database already exist
		} else {

			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();

			try {

				copyDataBase();

			} catch (IOException e) {

				throw new Error("Error copying database");

			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {

		SQLiteDatabase checkDB = null;

		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {

			// database does't exist yet.

		}

		if (checkDB != null) {

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {

		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public void openDataBase() throws SQLException {

		// Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY);

	}

	public Object getAllRecords(int tableId) {

		switch (tableId) {
		case ID_TABLE_COUNTRY_CODE: return getAllRecordsFromCountryCode();
		}
		return null;
	}

	private List<CountryCode> getAllRecordsFromCountryCode() {
		List<CountryCode> result = new ArrayList<CountryCode>();

		Cursor cursor = myDataBase.query(TABLE_COUNTRY_CODE, all_columns, null,
				null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			CountryCode countryCode = (CountryCode) cursorToObject(ID_TABLE_COUNTRY_CODE,cursor);
			result.add(countryCode);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return result;
	}

	public List<CountryCode> getDialingCode(String iso_2) {
		List<CountryCode> result = new ArrayList<CountryCode>();

//		Cursor cursor = myDataBase.query(TABLE_COUNTRY_CODE, all_columns,COL_ISO_2 + "=?", new String[] { iso_2 }, null, null, null);
		Cursor cursor = myDataBase.rawQuery("SELECT * FROM "+TABLE_COUNTRY_CODE+" WHERE "+COL_ISO_2 + " = ? COLLATE NOCASE", new String[] { iso_2 });//query(TABLE_COUNTRY_CODE, all_columns,COL_ISO_2 + "=?", new String[] { iso_2 }, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			CountryCode countryCode = (CountryCode) cursorToObject(ID_TABLE_COUNTRY_CODE,cursor);
			result.add(countryCode);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return result;
	}

	private Object cursorToObject(int id,Cursor cursor) {

		switch (id) {
		case ID_TABLE_COUNTRY_CODE: return objectCountryCode(cursor);
		}
		return null;
	}

	private CountryCode objectCountryCode(Cursor cursor) {
		CountryCode countryCode = new CountryCode();
		countryCode.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ROWID)));
		countryCode.setIso2(cursor.getString(cursor.getColumnIndexOrThrow(COL_ISO_2)));
		countryCode.setIso3(cursor.getString(cursor.getColumnIndexOrThrow(COL_ISO_3)));
		countryCode.setCountryName(cursor.getString(cursor.getColumnIndexOrThrow(COL_COUNTRY_NAME)));
		countryCode.setDailingCode(cursor.getString(cursor.getColumnIndexOrThrow(COL_DIALING_CODE)));
		return countryCode;
	}

	@Override
	public synchronized void close() {

		if (myDataBase != null)
			myDataBase.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	// Add your public helper methods to access and get content from the
	// database.
	// You could return cursors by doing "return myDataBase.query(....)" so it'd
	// be easy
	// to you to create adapters for your views.

}