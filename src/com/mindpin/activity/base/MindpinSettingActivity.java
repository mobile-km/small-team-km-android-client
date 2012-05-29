package com.mindpin.activity.base;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.mindpin.R;

public class MindpinSettingActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
}
