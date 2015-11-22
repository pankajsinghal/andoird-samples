package com.example.contactseditor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contactseditor.database.DataBaseHelper;
import com.example.contactseditor.database.table.CountryCode;

public class MainActivity extends Activity {

	private static final int SEARCH_TYPE = 0x00;
	static int lastIndex = 0;
	private static final String TAG = "contactsEditor";
	EditText t;
	TextView viewCountryCode;
	private Handler uiMsgHandler;
	Context mContext;
	CountryCode countryCode;
	Button correct;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		t = (EditText) findViewById(R.id.text);
		// t.setKeyListener(null);
		t.setTextIsSelectable(true);
		mContext = this;
		viewCountryCode = (TextView) findViewById(R.id.countrycode);

		correct = (Button) findViewById(R.id.button1);

		DataBaseHelper myDbHelper = new DataBaseHelper(this);

		try {

			myDbHelper.createDataBase();

		} catch (IOException ioe) {

			throw new Error("Unable to create database");

		}

		try {

			myDbHelper.openDataBase();

		} catch (SQLException sqle) {

			throw sqle;

		}
		// List<CountryCode> values = (ArrayList<CountryCode>)
		// myDbHelper.getAllRecords(DataBaseHelper.ID_TABLE_COUNTRY_CODE);
		//
		// for (CountryCode countryCode : values)
		// t.append(countryCode.toString() + "\n");

		// editContacts();
		TelephonyManager tMgr = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		// t.append("Sim operator : "+tMgr.getSimOperator()+"\nSim operator name: "+tMgr.getSimOperatorName()+"\nNetwork operator: "+tMgr.getNetworkOperator()+"\nNetwork operator name: "+tMgr.getNetworkOperatorName()+"\nSim Country Iso: "+tMgr.getSimCountryIso()+"\n");
		final List<CountryCode> values = (ArrayList<CountryCode>) myDbHelper
				.getDialingCode(tMgr.getSimCountryIso());

		if (values.size() < 1)
			return;
		else if (values.size() == 1) {
			countryCode = values.get(0);
			viewCountryCode.setText(countryCode.getDailingCode());
		} else if (values.size() > 1) {
			viewCountryCode.performClick();
		}

		// for (CountryCode countryCode : values)
		// t.append(countryCode.toString() + "\n");

		viewCountryCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MyDialog dlg = new MyDialog(MainActivity.this, R.style.dialog,
						values, uiMsgHandler);
				dlg.show();
			}
		});

		uiMsgHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg != null) {
					// Check for dialog box responses
					countryCode = values.get(msg.what);
					viewCountryCode.setText(countryCode.getDailingCode());
				}
			}
		};

		correct.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editContacts();
			}
		});

	}

	public void editContacts() {
		ContentResolver cr = getContentResolver();
		int count = 0;
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		// Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null,
		// null, null, null);
		// if (cur.getCount() > 0) {
		// while (cur.moveToNext()) {
		// String rawContactsId =
		// cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
		// // String id = cur.getString(cur.getColumnIndex(Data.CONTACT_ID));
		// String name =
		// cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		// if
		// (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))
		// > 0) {
		Cursor pCur = cr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);// ContactsContract.CommonDataKinds.Phone.CONTACT_ID
							// +" = ?",new String[]{rawContactsId}, null);
		while (pCur.moveToNext()) {
			// String rowId =
			// pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
			String Id = pCur
					.getString(pCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
			String num = pCur
					.getString(pCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA1));
			String numType = pCur
					.getString(pCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA2));
			String name = pCur
					.getString(pCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

			updateContact(Id, num, ops);

			final String full = Id + " -> " + name + " -> " + num + " -> "
					+ numType;// +rawContactsId + " -> "+rowId + " -> ";
			// t.append(full + "\n");
			count++;
			Log.d(TAG, count + "-->" + full);
		}
		t.append("Total numbers: " + count + "\n");
		long sec = System.currentTimeMillis();
		applyBatch(this, ops);
		pCur.close();
		long sec1 = System.currentTimeMillis();
		t.append("Time taken: " + (sec1 - sec) + " mili seconds.\n");
		Log.d(TAG, "diff @ " + (sec1 - sec));
		// Log.d(TAG, "time : "+calculateTime(seconds1-seconds));

		// }
		// }
		// }
	}

	public void updateContact(String contactId, String newNumber,
			ArrayList<ContentProviderOperation> ops) {

		// ASSERT: @contactId already has a work phone number
		String selectPhone = Data._ID + "=? AND " + Data.MIMETYPE + "='"
				+ Phone.CONTENT_ITEM_TYPE + "'" + " AND " + Phone.TYPE + "=?";
		String[] phoneArgs = new String[] { contactId,
				String.valueOf(Phone.TYPE_MOBILE) };
		newNumber = updateNumber(newNumber);
		ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
				.withSelection(selectPhone, phoneArgs)
				.withValue(Phone.NUMBER, newNumber).build());
	}

	private void applyBatch(Activity act,
			ArrayList<ContentProviderOperation> ops) {
		try {

			act.getContentResolver()
					.applyBatch(ContactsContract.AUTHORITY, ops);
			Log.d(TAG, "success");

		} catch (RemoteException e) {
			Log.d(TAG, "failed");
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			Log.d(TAG, "failed");
			e.printStackTrace();
		}
	}

	public String calculateTime(long seconds) {
		// int day = (int)TimeUnit.SECONDS.toDays(seconds);
		long hours = TimeUnit.SECONDS.toHours(seconds);// - (day *24);
		long minute = TimeUnit.SECONDS.toMinutes(seconds)
				- (TimeUnit.SECONDS.toHours(seconds) * 60);
		long second = TimeUnit.SECONDS.toSeconds(seconds)
				- (TimeUnit.SECONDS.toMinutes(seconds) * 60);
		String time = hours + " H " + minute + " M " + second + " S ";
		return time;

	}

	private String updateNumber(String num) {
		if (num.length() > 5 && num.length() < 15) {
			if (!num.startsWith(countryCode.getDailingCode())
					&& !num.startsWith("+")
					&& !num.startsWith("00"
							+ countryCode.getDailingCode().substring(1))) {
				if (num.startsWith("0"))
					num = num.substring(1, num.length());
				num = countryCode.getDailingCode() + num;
			}
		}
		return num;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_settings:
			// Single menu item is selected do something
			// Ex: launching new activity/screen or show alert message

			showDialog(2);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		final Dialog dialog = new Dialog(MainActivity.this);

		switch (id) {

		case 2:
			dialog.setContentView(R.layout.input_layout);
			dialog.setTitle("Search");
			dialog.setCancelable(false);

			ImageView searchIcon = (ImageView) dialog
					.findViewById(R.id.input_icon);
			searchIcon.setImageResource(R.drawable.search);

			TextView search_label = (TextView) dialog
					.findViewById(R.id.input_label);
			search_label.setText("Search");
			final EditText search_input = (EditText) dialog
					.findViewById(R.id.input_inputText);

			Button search_button = (Button) dialog
					.findViewById(R.id.input_create_b);
			Button cancel_button = (Button) dialog
					.findViewById(R.id.input_cancel_b);
			search_button.setText("Search");

			search_button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String temp = search_input.getText().toString();

					if (temp.length() > 0)
						new BackgroundWork(SEARCH_TYPE).execute(temp);
					dialog.dismiss();
				}
			});

			cancel_button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			break;
		}
		return dialog;
	}

	private class BackgroundWork extends AsyncTask<String, Void, Integer> {
		private String search_text;
		private ProgressDialog pr_dialog;
		private int type;

		private BackgroundWork(int type) {
			this.type = type;
		}

		/**
		 * This is done on the EDT thread. this is called before doInBackground
		 * is called
		 */
		@Override
		protected void onPreExecute() {

			switch (type) {
			case SEARCH_TYPE:
				pr_dialog = ProgressDialog.show(mContext, "Searching",
						"Searching current file system...", true, true);
				break;

			}
		}

		/**
		 * background thread here
		 */
		@Override
		protected Integer doInBackground(String... params) {

			switch (type) {
			case SEARCH_TYPE:
				search_text = params[0].toLowerCase(Locale.getDefault());
				String text = t.getText().toString()
						.toLowerCase(Locale.getDefault());
				Log.d(TAG, "searching for " + search_text);

				boolean status = text.contains(search_text);
				Log.d(TAG, "status: " + status);
				// Log.d(TAG, "text: "+text);
				lastIndex = t.getSelectionEnd();
				if (status) {

					Log.d(TAG, "yes, it contains " + search_text);
					lastIndex = text.indexOf(search_text, lastIndex);
					Log.d(TAG, "position is: " + lastIndex);
					if (lastIndex == -1) {
						// lastIndex=search_text.length();
						lastIndex = 0;
						lastIndex = text.indexOf(search_text, lastIndex);
					}
					Log.d(TAG, "position is: " + lastIndex);
				} else
					lastIndex = -1;
				return lastIndex;

			}
			return null;
		}

		/**
		 * This is called when the background thread is finished. Like
		 * onPreExecute, anything here will be done on the EDT thread.
		 */
		@Override
		protected void onPostExecute(Integer pos) {
			switch (type) {
			case SEARCH_TYPE:
				if (pos >= 0)
					t.setSelection(lastIndex, lastIndex + search_text.length());
				else {
					Toast.makeText(mContext, "Not found", Toast.LENGTH_SHORT)
							.show();
					t.setSelection(t.getSelectionEnd(), t.getSelectionEnd());
				}
				pr_dialog.dismiss();
				break;

			}
		}

	}

}
