package com.example.persistencedemo;

import java.util.List;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MyPreferenceActivity extends PreferenceActivity {

	public static final String PREF_CHECK_BOX = "PREF_CHECK_BOX";
	public static final String PREF_VERBOSE_CHECK_BOX = "PREF_VERBOSE_CHECK_BOX";
	public static final String PREF_EDIT_TEXT = "PREF_EDIT_TEXT";
	public static final String PREF_EVEN_LIST_TEXT = "PREF_EVEN_LIST_TEXT";
	public static final String PREF_ODD_LIST_TEXT = "PREF_ODD_LIST_TEXT";
	public static final String PREF_NO_VAL_LIST_TEXT = "PREF_NO_VAL_LIST_TEXT";
	public static final String PREF_CAT3 = "PREF_CAT3";
	public static final String PREF_MULTI_SEL_LIST = "PREF_MULTI_SEL_LIST";
	public static final String PREF_SWITCH = "PREF_SWITCH";
	public static final String PREF_CAT4 = "PREF_MULTI_SEL_LIST";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
		{
			addPreferencesFromResource( R.xml.preferences );
		}
	}

	@TargetApi(11)
	@Override
	public void onBuildHeaders(List<Header> target)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			loadHeadersFromResource( R.xml.preference_headers, target);
		}
	}
}
