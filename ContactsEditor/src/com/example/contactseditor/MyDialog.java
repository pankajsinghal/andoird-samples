package com.example.contactseditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.contactseditor.database.table.CountryCode;

public class MyDialog extends Dialog {
	private Context context;
	private ListView dialog_list = null;
	private List<CountryCode> values;

	private Handler uiMsgHandler;

	public MyDialog(Context context) {
		super(context);
		this.context = context;
	}

	public MyDialog(Context context, int theme, List<CountryCode> values,
			Handler uiMsgHandler) {
		super(context, theme);
		this.context = context;
		this.values = values;
		this.uiMsgHandler = uiMsgHandler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog);
		dialog_list = (ListView) findViewById(R.id.dialog_list);

		// ListView
		SimpleAdapter adapter = new SimpleAdapter(context, getPriorityList(),
				R.layout.row_view, new String[] { "image", "text" }, new int[] {
						R.id.row_image, R.id.row_text });
		dialog_list.setAdapter(adapter);
		// ListView
		dialog_list
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						uiMsgHandler.sendEmptyMessage(position);
						dismiss();
					}
				});
	}

	private List<HashMap<String, Object>> getPriorityList() {
		List<HashMap<String, Object>> priorityList = new ArrayList<HashMap<String, Object>>();
		for (CountryCode c : values) {
			HashMap<String, Object> map1 = new HashMap<String, Object>();
			map1.put("image", R.drawable.color);
			map1.put("text", c.toString());
			priorityList.add(map1);
		}

		return priorityList;
	}
}