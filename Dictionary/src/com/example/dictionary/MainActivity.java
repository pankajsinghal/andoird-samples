package com.example.dictionary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {

	static ProgressBar p;
	private AutoCompleteTextView autoCompleteView;
    private ItemsDbAdapter dbHelper;
    private TextView t;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		p = (ProgressBar) findViewById(R.id.progressBar1);
        autoCompleteView = (AutoCompleteTextView) findViewById(R.id.AutoCompleteTextView1);
		t = (TextView)findViewById(R.id.textView1);
		dbHelper = new ItemsDbAdapter(this);
        dbHelper.open();
		
		 
        // Create an ItemAutoTextAdapter for the Item description field,
        // and set it as the OnItemClickListener for that field.
        ItemAutoTextAdapter adapter = this.new ItemAutoTextAdapter(dbHelper);
        autoCompleteView.setAdapter(adapter);
        autoCompleteView.setOnItemClickListener(adapter);

		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
//	See if this can be added
//	@Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (dbHelper  != null) {
//            dbHelper.close();
//        }
//    }
//	
	public void errorDialog(String title, String msg)
	{
		new AlertDialog.Builder(this)
	    .setTitle(title)
	    .setMessage(msg)
	    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
		})
//	    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//	        public void onClick(DialogInterface dialog, int which) { 
//	            // continue with delete
//	        }
//	     })
//	    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//	        public void onClick(DialogInterface dialog, int which) { 
//	            // do nothing
//	        }
//	     })
	     .show();
	}
	
	
	
	
	   class ItemAutoTextAdapter extends CursorAdapter
	    implements android.widget.AdapterView.OnItemClickListener {
	 
	        private ItemsDbAdapter mDbHelper;
	 
	        /**
	         * Constructor. Note that no cursor is needed when we create the
	         * adapter. Instead, cursors are created on demand when completions are
	         * needed for the field. (see
	         * {@link ItemAutoTextAdapter#runQueryOnBackgroundThread(CharSequence)}.)
	         *
	         * @param dbHelper
	         *            The AutoCompleteDbAdapter in use by the outer class
	         *            object.
	         */
	        public ItemAutoTextAdapter(ItemsDbAdapter dbHelper) {
	            // Call the CursorAdapter constructor with a null Cursor.
	            super(MainActivity.this, null);
	            mDbHelper = dbHelper;
	        }
	 
	        /**
	         * Invoked by the AutoCompleteTextView field to get completions for the
	         * current input.
	         *
	         * NOTE: If this method either throws an exception or returns null, the
	         * Filter class that invokes it will log an error with the traceback,
	         * but otherwise ignore the problem. No choice list will be displayed.
	         * Watch those error logs!
	         *
	         * @param constraint
	         *            The input entered thus far. The resulting query will
	         *            search for Items whose description begins with this string.
	         * @return A Cursor that is positioned to the first row (if one exists)
	         *         and managed by the activity.
	         */
	        @Override
	        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
	            if (getFilterQueryProvider() != null) {
	                return getFilterQueryProvider().runQuery(constraint);
	            }
	 
	            Cursor cursor = mDbHelper.fetchItemsByDesc((constraint != null ? constraint.toString() : "@@@@"));
	 
	            return cursor;
	        }
	 
	        /**
	         * Called by the AutoCompleteTextView field to get the text that will be
	         * entered in the field after a choice has been made.
	         *
	         * @param Cursor
	         *            The cursor, positioned to a particular row in the list.
	         * @return A String representing the row's text value. (Note that this
	         *         specializes the base class return value for this method,
	         *         which is {@link CharSequence}.)
	         */
	        @Override
	        public String convertToString(Cursor cursor) {
	            final int columnIndex = cursor.getColumnIndexOrThrow(ItemsDbAdapter.COL_DEFINITION);
	            final String str = cursor.getString(columnIndex);
	            return str;
	        }
	 
	        /**
	         * Called by the ListView for the AutoCompleteTextView field to display
	         * the text for a particular choice in the list.
	         *
	         * @param view
	         *            The TextView used by the ListView to display a particular
	         *            choice.
	         * @param context
	         *            The context (Activity) to which this form belongs;
	         * @param cursor
	         *            The cursor for the list of choices, positioned to a
	         *            particular row.
	         */
	        @Override
	        public void bindView(View view, Context context, Cursor cursor) {
	            //final String text = convertToString(cursor);
	            //((TextView) view).setText(text);
	            final int itemColumnIndex = cursor.getColumnIndexOrThrow(ItemsDbAdapter.COL_WORD);
	            TextView text1 = (TextView) view.findViewById(R.id.text1);
	            text1.setText(cursor.getString(itemColumnIndex));
	        }
	 
	        /**
	         * Called by the AutoCompleteTextView field to display the text for a
	         * particular choice in the list.
	         *
	         * @param context
	         *            The context (Activity) to which this form belongs;
	          * @param cursor
	         *            The cursor for the list of choices, positioned to a
	         *            particular row.
	         * @param parent
	         *            The ListView that contains the list of choices.
	         *
	         * @return A new View (really, a TextView) to hold a particular choice.
	         */
	        @Override
	        public View newView(Context context, Cursor cursor, ViewGroup parent) {
	            final LayoutInflater inflater = LayoutInflater.from(context);
	            final View view = inflater.inflate(R.layout.item_list,parent, false);
	            return view;
	        }
	 
	        /**
	         * Called by the AutoCompleteTextView field when a choice has been made
	         * by the user.
	         *
	         * @param listView
	         *            The ListView containing the choices that were displayed to
	         *            the user.
	         * @param view
	         *            The field representing the selected choice
	         * @param position
	         *            The position of the choice within the list (0-based)
	         * @param id
	         *            The id of the row that was chosen (as provided by the _id
	         *            column in the cursor.)
	         */
	        @Override
	        public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
	            // Get the cursor, positioned to the corresponding row in the result set
	            Cursor cursor = (Cursor) listView.getItemAtPosition(position);
	 
	            // Get the Item Number from this row in the database.
	            String itemNumber = cursor.getString(cursor.getColumnIndexOrThrow(ItemsDbAdapter.COL_DEFINITION));
	 
	            // Update the parent class's TextView
//	            itemView.setText(itemNumber);
	            t.setText(itemNumber);
	            Log.w("Quantity:", String.valueOf(itemNumber));
	            autoCompleteView.setText(cursor.getString(cursor.getColumnIndexOrThrow(ItemsDbAdapter.COL_WORD)));
	        }
	    }
	
	
	

}
