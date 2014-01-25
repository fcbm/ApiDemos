package com.example.persistencedemo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.TreeSet;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends Activity implements OnSharedPreferenceChangeListener {

	private final static String TAG = "MainActivitySP";
	
	private final static String SP_FILE_NAME = "SP_FILE_NAME";
	private final static String KEY_BOOLEAN = "KEY_BOOLEAN";
	private final static String KEY_INT = "KEY_INT";
	private final static String KEY_STRING = "KEY_STRING";
	private final static String KEY_STRING_SET = "KEY_STRING_SET";
	
	private final static int SHOW_PREFERENCES = 80;
	
	private RadioGroup mRadioGroup;
	
	@TargetApi(11)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		SharedPreferences spGlobalNamed = getSharedPreferences(SP_FILE_NAME, Activity.MODE_PRIVATE);
		
		SharedPreferences.Editor editor = spGlobalNamed.edit();
		
		editor.putBoolean(KEY_BOOLEAN, false);
		editor.putInt(KEY_INT, 18);
		editor.putString(KEY_STRING, "this is a string");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			Set<String> stringSet = new TreeSet<String>();
			stringSet.add("First");
			stringSet.add("Second");
			stringSet.add("Third");
			editor.putStringSet(KEY_STRING_SET,  stringSet);
		}
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
		{
			editor.apply();
		}
		else
		{
			editor.commit();
		}
		
		SharedPreferences spGlobalUnnamed = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
		spGlobalUnnamed.registerOnSharedPreferenceChangeListener( this );
		
		SharedPreferences spLocalUnnamed = getPreferences( MODE_PRIVATE );
		mRadioGroup = (RadioGroup) findViewById( R.id.rgChoice );
		int idCheck = spLocalUnnamed.getInt( KEY_INT, R.id.rbThree);
		mRadioGroup.check( idCheck );

		Log.i(TAG, "onCreate got localSP " + spLocalUnnamed.getInt( KEY_INT, 1000) + " rb= " + mRadioGroup.getCheckedRadioButtonId());
		readGlobalNamedSharedPreferences();
		readGlobalUnnamedPreferences();
		
		InputStream is = getResources().openRawResource( R.raw.filename );
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		readFromInputStream(is, baos);
		
		TextView tvRaw = (TextView) findViewById( R.id.tvRawFile );
		tvRaw.setText( baos.toString() );
	}
	
	static public void readFromInputStream(InputStream is, ByteArrayOutputStream baos)
	{
		byte[] buffer=new byte[1024];
		
		try {
			int len = 0;
			while ((len = is.read(buffer)) > 0)
			{
				Log.d(TAG, "Read " + len); 
				baos.write(buffer, 0, len);
			}
		} catch (IOException e) { Log.d(TAG, "Read failed " + e.toString()); }		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		Log.i(TAG, "onSaveInstanceState");
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		SharedPreferences sp = getPreferences( MODE_PRIVATE );
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(KEY_INT,  mRadioGroup.getCheckedRadioButtonId());
		editor.commit();
		Log.i(TAG, "onPause save localSP " + sp.getInt( KEY_INT, 1001));
	}

	private void readGlobalNamedSharedPreferences()
	{
		SharedPreferences sp = getSharedPreferences(SP_FILE_NAME, Activity.MODE_PRIVATE);
		
		TextView tv = (TextView) findViewById( R.id.tvCtxSharedPref );
		StringBuilder sb = new StringBuilder();
		sb.append("Boolean : " + sp.getBoolean(KEY_BOOLEAN, false) + "\n");
		sb.append("Int : " + sp.getInt(KEY_INT, 10) + "\n");
		sb.append("String : " + sp.getString(KEY_STRING, "noString") + "\n");
		tv.setText( sb.toString() );
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@TargetApi(11)
	private void readGlobalUnnamedPreferences()
	{
		Context ctx = getApplicationContext();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		
		StringBuilder sb = new StringBuilder();
		sb.append(MyPreferenceActivity.PREF_CHECK_BOX + ":" + sp.getBoolean(MyPreferenceActivity.PREF_CHECK_BOX, true) + "\n");
		sb.append(MyPreferenceActivity.PREF_VERBOSE_CHECK_BOX + ":" + sp.getBoolean(MyPreferenceActivity.PREF_VERBOSE_CHECK_BOX, false)+ "\n");
		sb.append(MyPreferenceActivity.PREF_EDIT_TEXT + ":" + sp.getString(MyPreferenceActivity.PREF_EDIT_TEXT, "No Text")+ "\n");
		sb.append(MyPreferenceActivity.PREF_EVEN_LIST_TEXT + ":" + sp.getString(MyPreferenceActivity.PREF_EVEN_LIST_TEXT, "boh even")+ "\n");
		sb.append(MyPreferenceActivity.PREF_ODD_LIST_TEXT + ":" + sp.getString(MyPreferenceActivity.PREF_ODD_LIST_TEXT, "boh odd")+ "\n");
		sb.append(MyPreferenceActivity.PREF_NO_VAL_LIST_TEXT + ":" + sp.getString(MyPreferenceActivity.PREF_NO_VAL_LIST_TEXT, "not used!")+ "\n");
		sb.append(MyPreferenceActivity.PREF_CAT3 + ":" + sp.getString(MyPreferenceActivity.PREF_CAT3, "cat3!")+ "\n");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			sb.append(MyPreferenceActivity.PREF_MULTI_SEL_LIST + ":" + (sp.getStringSet(MyPreferenceActivity.PREF_MULTI_SEL_LIST, new TreeSet<String>())).toString()+ "\n");
		}
		else
		{
			sb.append(MyPreferenceActivity.PREF_MULTI_SEL_LIST + ": Not Supported"+ "\n");
		}
		sb.append(MyPreferenceActivity.PREF_SWITCH + ":" + sp.getBoolean(MyPreferenceActivity.PREF_SWITCH, false)+ "\n");
		//sb.append(MyPreferenceActivity.PREF_CAT4 + ":" + sp.getString(MyPreferenceActivity.PREF_CAT4, "none")+ "\n");
		
		TextView tv = (TextView) findViewById( R.id.tvPrefMgrSharedPref );
		tv.setText( sb.toString() );
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == SHOW_PREFERENCES)
		{
			readGlobalUnnamedPreferences();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.action_settings:
		{
			Intent i = new Intent(this, MyPreferenceActivity.class);
			startActivityForResult(i, SHOW_PREFERENCES);
			return true;
		}
		case R.id.action_fsactivity:
		{
			Intent i = new Intent(this, FileSystemDemoActivity.class);
			startActivity(i);
			return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.i(TAG, "Key " + key + " : changed" );
	}

}
