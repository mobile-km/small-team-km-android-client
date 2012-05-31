package com.teamkn.activity.base;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.teamkn.R;

public class MindpinSettingActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
}
