package com.teamkn.activity.contacts;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.Contacts.Phones;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.base.activity.MindpinBaseActivity;

public class ContactsActivity extends MindpinBaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_contacts);
		
		Cursor cursor = getContentResolver().query(Phones.CONTENT_URI, null, null, null, Phones._ID + " asc");
		
		int[] indexes = new int[]{
				cursor.getColumnIndex(Phones._ID),
				cursor.getColumnIndex(Phones.DISPLAY_NAME),
				cursor.getColumnIndex(Phones.NUMBER),
				cursor.getColumnIndex(Phones.LAST_TIME_CONTACTED),
				cursor.getColumnIndex(Phones.LABEL),
				cursor.getColumnIndex(Phones.NOTES)
		};
		
		String re = "";
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
			for(int i : indexes){
				re += cursor.getString(i)+", ";
			}
			re += "\n\n";
		}
		
		((TextView)findViewById(R.id.contacts)).setText(re);
	}
}
