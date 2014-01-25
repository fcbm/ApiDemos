package com.example.servicedemo;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.servicedemo.LocalService.LocalBinder;

public class MainActivity extends ListActivity {

	private static final String TAG = "MainActivity";
	public static final int FAKE_DOWNLOAD_DONE = 11;
	
	private LocalService mService;
	private Handler mUiHandler;
	private FakeDownloader<TextView> mFakeDownloader;
	
	private ServiceConnection mConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "onServiceConnected");
			mService = ((LocalBinder)service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "onServiceDisconnected");
			mService = null;
		}
	};
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "onReceive");
			if (intent.getAction() == LocalService.ACTION_ITEM_ADDED && mService != null)
			{
				Log.d(TAG, "onReceive ACTION_ITEM_ADDED");
				mList.clear();
				mList.addAll( mService.getList() );
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	private ArrayList<String> mList = new ArrayList<String>();
	private TextAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mAdapter = new TextAdapter(mList);
		
		final Button btn = (Button) findViewById( R.id.btnStartStop );
		btn.setText( ScheduleReceiver.isAlarmOn(MainActivity.this) ? R.string.stop : R.string.start);
		btn.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!ScheduleReceiver.isAlarmOn(MainActivity.this))
				{
					Intent i = new Intent(ScheduleReceiver.ACTION_SCHEDULE);
					btn.setText( R.string.stop );
					sendBroadcast(i);
				}
				else
				{
					Intent i = new Intent(ScheduleReceiver.ACTION_CANCEL);
					if(mFakeDownloader != null) { mFakeDownloader.clearList(); }
					btn.setText( R.string.start );
					sendBroadcast(i);
				}
			}
		});
		
		final Button btnClear = (Button) findViewById( R.id.btnClearItems );
		btnClear.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mService != null)
				{
					mService.getList().clear();
				}
				mList.clear();
				mAdapter.notifyDataSetChanged();
			}
		});
		
		mUiHandler = new Handler ()
		{
			public void handleMessage(Message msg)
			{
				if (msg.what == FAKE_DOWNLOAD_DONE)
				{
					Log.d(TAG+"s", "Got new msg!");
					TextView tv = (TextView) msg.obj;
					tv.setText( tv.getText() +  " - done!");
				}
			}
		};
		
		mFakeDownloader = new FakeDownloader<TextView>( mUiHandler );
		Log.d(TAG, "Starting FakeDownloader..");
		mFakeDownloader.start();
		Log.d(TAG, "Started FakeDownloader..getLooper..");
		mFakeDownloader.getLooper();
		Log.d(TAG, "FakeDownloader got Looper..");
		
		setListAdapter(mAdapter);
	}
	
	private void doBindService()
	{
		Intent i = new Intent(this, LocalService.class);
		bindService(i, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	private void doUnbindService()
	{
		Log.d(TAG, "doUnbind");
		unbindService( mConnection );
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		Log.d(TAG, "onResume");
		registerReceiver( mReceiver, new IntentFilter( LocalService.ACTION_ITEM_ADDED ));
		doBindService();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		Log.d(TAG, "onPause");
		doUnbindService();
		unregisterReceiver( mReceiver );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public class TextAdapter extends ArrayAdapter<String>
	{
		public TextAdapter(ArrayList<String> data) {
			super(MainActivity.this, android.R.layout.simple_list_item_1, data);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if (convertView == null)
			{
				convertView = super.getView(position, convertView, parent);
			}
			TextView tv = (TextView) convertView;
			if (tv.getText().length() > 0 && tv.getText().toString().startsWith(""+position) && !tv.getText().toString().endsWith("!"))
			{
				mFakeDownloader.pushRequest( tv );
				Log.d(TAG, "call View for pos " + position + " " + tv.getText().toString() + "|"+tv.getText().toString().endsWith("!"));
			}
			return convertView;
		}
	}

}
