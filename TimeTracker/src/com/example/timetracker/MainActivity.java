package com.example.timetracker;


import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnClickListener, OnItemLongClickListener {

	private static final String TAG = "MainActivity";

	public static final String ACTION_TIME_UPDATE = "MainActivity.action.ACTION_TIME_UPDATE";
	public static final String ACTION_TIMER_FINISHED = "MainActivity.action.ACTION_TIMER_FINISHED";
	public static final String KEY_INTENT_EXTRA_TIME = "time";
	
	
	private static final String KEY_TIME_VALUES = "time_values";
	private static final String KEY_FIRST_POS = "first_pos";
	private static final String KEY_COUNTER = "counter";
	private static final String KEY_CURRENT_TIME = "current_time";
	private long mTime = 0;
	
	private static final String TAG_CONFIRM_DELETE_ALL_DIALOG = "confirm_delete_all_dialog";
	
	Button mStartButton = null;
	private TimeListAdapter mTimeListAdapter = null;
	
	private BroadcastReceiver mTimeReceiver = new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			//Log.d(TAG, "broadcastIntent received");
			long time = intent.getLongExtra(KEY_INTENT_EXTRA_TIME, 0);
			if (intent.getAction() == ACTION_TIME_UPDATE)
			{
				//Log.d(TAG, "update timer");
				TextView counter = (TextView) MainActivity.this.findViewById( R.id.counter );
				counter.setText(DateUtils.formatElapsedTime(time / 1000));
			}
			else if (intent.getAction() == ACTION_TIMER_FINISHED)
			{ 
				Log.d(TAG, "timerFinished, time " + time + " mTimeListAdapter is null: " + (mTimeListAdapter==null));
				if (mTimeListAdapter != null && time > 0)
				{
					Log.d(TAG, "timerFinished mTimerListAdapter is null or time not > 0");
					mTimeListAdapter.add(time / 1000);
				}
			}
		}
	};
	
	
	private TimerService mBoundService = null;
	private boolean mServiceIsBound = false;
	
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "onServiceDisconnected");
			mBoundService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "onServiceConnected");
			mBoundService = ((TimerService.LocalBinder) service).getService();
			updateButtons();
		}
	};
	
	private void doBindService()
	{
		Log.d(TAG, "doBindService");
		if (!mServiceIsBound)
		{
			Log.d(TAG, "binding..");
			Intent intent = new Intent(this, TimerService.class);
			
			intent.putExtra(MainActivity.KEY_INTENT_EXTRA_TIME, mTime);
			
			int flags = Context.BIND_AUTO_CREATE;
			mServiceIsBound = bindService(intent , mConnection, flags);
		}
	}
	
	private void doUnbindService()
	{
		Log.d(TAG, "doUnbindService");
		if (mServiceIsBound)
		{
			Log.d(TAG, "unbinding..");
			mTime = mBoundService.getTime();
			unbindService( mConnection );
			mServiceIsBound = false;
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		TextView counter = (TextView) findViewById(R.id.counter);
		counter.setText(DateUtils.formatElapsedTime(0));
		counter.setOnCreateContextMenuListener( this );
		
		mStartButton = (Button) findViewById(R.id.start_stop);
		mStartButton.setOnClickListener(this);
		
		Button resetButton = (Button) findViewById(R.id.reset);
		resetButton.setOnClickListener(this);

		List<Long> values = new ArrayList<Long>();
		if (savedInstanceState != null)
		{
			long[] arr = savedInstanceState.getLongArray(KEY_TIME_VALUES);
			for (long l : arr)
			{
				values.add(l);
			}
			
			CharSequence seq = savedInstanceState.getCharSequence(KEY_COUNTER);
			if (seq != null)
			{
				counter.setText(seq);
			}
			
			mTime = savedInstanceState.getLong(KEY_CURRENT_TIME, 0);
		}

		ListView lv = (ListView) findViewById(R.id.time_list);

		// TODO : check why resource is 0
		mTimeListAdapter = new TimeListAdapter(this,  0, values);
		lv.setAdapter(mTimeListAdapter);

		//lv.setOnLongClickListener(this);
		lv.setOnItemLongClickListener(this);		
	}
	
	private void updateButtons()
	{
		if (mBoundService != null && !mBoundService.isStopped())
		{
			mStartButton.setText( R.string.stop );
		}
		else
		{
			mStartButton.setText( R.string.start );
		}
	}
	
	@Override 
	public void onResume()
	{
		super.onResume();
		Log.d(TAG, "onResume, register Receiver");
		// Receive status updates from TimerService
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_TIME_UPDATE);
		filter.addAction(ACTION_TIMER_FINISHED);
		registerReceiver( mTimeReceiver, filter);
		
		doBindService();
		
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		Log.d(TAG, "onPause, unregister Receiver");
		unregisterReceiver( mTimeReceiver );

		// TODO: move unbind service in onDestroy
		doUnbindService();
	}
	
	@Override
	public void onClick(View v)
	{
		//TextView ssButton = (TextView) findViewById(R.id.start_stop);
		
		if (v.getId() == R.id.start_stop)
		{
			if (mBoundService == null)
			{
				Log.d(TAG, "mBoundService is null - shouldn't happen");
				mStartButton.setText( R.string.stop );
				//Intent intent = new Intent(this, TimerService.class);
				//startService( intent );
			}
			else if ( mBoundService.isStopped() )
			{
				Log.d(TAG, "mBoundService is not null and stopped");
				mStartButton.setText( R.string.stop );
				Intent intent = new Intent(this, TimerService.class);
				startService( intent );

				mBoundService.startTimer( );
			}
			else
			{
				Log.d(TAG, "mBoundService is not null and started");
				mStartButton.setText( R.string.start );
				mBoundService.stopTimer();
			}
		}
		else if (v.getId() == R.id.reset)
		{
			if (mBoundService != null)
			{
				Log.d(TAG, "mBoundService is not null");
				mBoundService.resetTimer();
			}

			TextView counter = (TextView) findViewById(R.id.counter);
			counter.setText( DateUtils.formatElapsedTime( 0 ));
			mStartButton.setText(R.string.start);
		}
		
	}
	
	// TODO: Add onRestoreInstanceState and onPause/onResume
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		if (mTimeListAdapter != null)
		{
			int count = mTimeListAdapter.getCount();
			long[] arr = new long[count];
			for (int i = 0; i < count; i++)
			{
				arr[i] = mTimeListAdapter.getItem(i);
			}
			outState.putLongArray(KEY_TIME_VALUES, arr);
		}
		
		if (mBoundService != null)
			outState.putLong(KEY_CURRENT_TIME, mBoundService.getTime());
		
		TextView counter = (TextView) findViewById(R.id.counter);
		if (counter != null)
			outState.putCharSequence(KEY_COUNTER, counter.getText());
		
		ListView lv = (ListView) findViewById(R.id.time_list);
		if (lv != null)
			outState.putInt(KEY_FIRST_POS, lv.getFirstVisiblePosition());
		
		// TODO: check why the call to super is at the end
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.clear_all :
			createClearAllDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.item_1:
			Toast.makeText( this, "Selected ContextMenu Item One", Toast.LENGTH_LONG).show();
			return true;
		case R.id.item_2:
			Toast.makeText( this, "Selected ContextMenu Item Two", Toast.LENGTH_LONG).show();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	private void createClearAllDialog()
	{
		FragmentManager fm = getSupportFragmentManager();
		if ( fm.findFragmentByTag( TAG_CONFIRM_DELETE_ALL_DIALOG ) == null)
		{
			// Static method is a common pattern to create Fragments (they need a public default constructor)
			ConfirmClearDialogFragment dialogFrag = ConfirmClearDialogFragment.newInstance(mTimeListAdapter);
			// You should use show(FragmentManager, String) or show(FragmentTransaction, String) 
			// to add an instance of DialogFragment to your UI, as these keep track of how 
			// DialogFragment should remove itself when the dialog is dismissed. (API reference)
			dialogFrag.show(fm, TAG_CONFIRM_DELETE_ALL_DIALOG);
		}		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) 
	{
		String message = "LongPressed Item at position " + position + 1;
		Toast.makeText( this, message, Toast.LENGTH_LONG).show();
		return true;
	}

}
