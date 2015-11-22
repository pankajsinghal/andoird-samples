package com.example.autocompletetextview;
 
 
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;
 
public class AndroidAutocompleteTextViewActivity extends Activity {
 
    private AutoCompleteTextView itemDescriptionView;
    private ItemsDbAdapter dbHelper;
    private EditText itemView;
    private TextView descView;
    
    static final String[] COUNTRIES = new String[] {
          "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
          "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina",
          "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan",
          "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium",
          "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia",
          "Bosnia and Herzegovina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory",
          "British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso", "Burundi",
          "Cote d'Ivoire", "Cambodia", "Cameroon", "Canada", "Cape Verde",
          "Cayman Islands", "Central African Republic", "Chad", "Chile", "China",
          "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo",
          "Cook Islands", "Costa Rica", "Croatia", "Cuba", "Cyprus", "Czech Republic",
          "Democratic Republic of the Congo", "Denmark", "Djibouti", "Dominica", "Dominican Republic",
          "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea",
          "Estonia", "Ethiopia", "Faeroe Islands", "Falkland Islands", "Fiji", "Finland",
          "Former Yugoslav Republic of Macedonia", "France", "French Guiana", "French Polynesia",
          "French Southern Territories", "Gabon", "Georgia", "Germany", "Ghana", "Gibraltar",
          "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau",
          "Guyana", "Haiti", "Heard Island and McDonald Islands", "Honduras", "Hong Kong", "Hungary",
          "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica",
          "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan", "Laos",
          "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg",
          "Macau", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands",
          "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia", "Moldova",
          "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia",
          "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand",
          "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "North Korea", "Northern Marianas",
          "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru",
          "Philippines", "Pitcairn Islands", "Poland", "Portugal", "Puerto Rico", "Qatar",
          "Reunion", "Romania", "Russia", "Rwanda", "Sqo Tome and Principe", "Saint Helena",
          "Saint Kitts and Nevis", "Saint Lucia", "Saint Pierre and Miquelon",
          "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Saudi Arabia", "Senegal",
          "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands",
          "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "South Korea",
          "Spain", "Sri Lanka", "Sudan", "Suriname", "Svalbard and Jan Mayen", "Swaziland", "Sweden",
          "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "The Bahamas",
          "The Gambia", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey",
          "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Virgin Islands", "Uganda",
          "Ukraine", "United Arab Emirates", "United Kingdom",
          "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan",
          "Vanuatu", "Vatican City", "Venezuela", "Vietnam", "Wallis and Futuna", "Western Sahara",
          "Yemen", "Yugoslavia", "Zambia", "Zimbabwe"
        };
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
 
        dbHelper = new ItemsDbAdapter(this);
        dbHelper.open();
 
        itemView = (EditText) findViewById(R.id.item);
        descView = (TextView) findViewById(R.id.itemDesc);
        itemDescriptionView = (AutoCompleteTextView) findViewById(R.id.autocomplete_desc);
 
 
        // Create an ItemAutoTextAdapter for the Item description field,
        // and set it as the OnItemClickListener for that field.
        ItemAutoTextAdapter adapter = this.new ItemAutoTextAdapter(dbHelper);
        itemDescriptionView.setAdapter(adapter);
        itemDescriptionView.setOnItemClickListener(adapter);
        
        // Create an ItemAutoTextAdapter for Country name
        AutoCompleteTextView countryView = (AutoCompleteTextView) findViewById(R.id.autocomplete_country);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.country_list, COUNTRIES);
        countryView.setAdapter(arrayAdapter);
 
        //Clean all Items
        dbHelper.deleteAllItems();
        //Add some Item data as a sample
        dbHelper.createItem("206-569-761", "Display Calculator");
        dbHelper.createItem("206-577-145", "Adjustable Shelf Storage Cab");
        dbHelper.createItem("206-577-813", "Ink Ribbon, Sharp SH5507");
        dbHelper.createItem("206-579-130", "Paper Clips - OIC");
        dbHelper.createItem("206-580-886", "Partition Triple Tray");
        dbHelper.createItem("206-580-902", "Partition Binder Rack");
        dbHelper.createItem("206-580-894", "Partition Hanging Folder File");
        dbHelper.createItem("206-580-111", "Partition Triple Tray - Black");
        dbHelper.createItem("206-580-222", "Partition Triple Tray - Blue");
        dbHelper.createItem("206-580-333", "Partition Triple Tray - Red");
        dbHelper.createItem("206-581-111", "Partition Binder Rack - Black");
        dbHelper.createItem("206-581-222", "Partition Binder Rack - Blue");
        dbHelper.createItem("206-581-333", "Partition Binder Rack - Red");
        
    }
 
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper  != null) {
            dbHelper.close();
        }
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
            super(AndroidAutocompleteTextViewActivity.this, null);
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
 
            Cursor cursor = mDbHelper.fetchItemsByDesc(
                    (constraint != null ? constraint.toString() : "@@@@"));
 
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
            final int columnIndex = cursor.getColumnIndexOrThrow("description");
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
            final int itemColumnIndex = cursor.getColumnIndexOrThrow("itemNumber");
            final int descColumnIndex = cursor.getColumnIndexOrThrow("description");
            TextView text1 = (TextView) view.findViewById(R.id.text1);
            text1.setText(cursor.getString(itemColumnIndex));
            TextView text2 = (TextView) view.findViewById(R.id.text2);
            text2.setText(cursor.getString(descColumnIndex));
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
            String itemNumber = cursor.getString(cursor.getColumnIndexOrThrow("itemNumber"));
 
            // Update the parent class's TextView
            itemView.setText(itemNumber);
            descView.setText(itemDescriptionView.getText());
            Log.w("Quantity:", String.valueOf(descView.getText().length()));
            itemDescriptionView.setText("");
        }
    }
 
}