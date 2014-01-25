package com.example.timetracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;
import android.util.Log;

public class TimerService extends Service {

	private static final String TAG = "TimerService";
	public static int TIMER_NOTIFICATION = 0;
	
	private NotificationManager mNM = null;
	private Notification mNotification = null;
	private long mStart = 0;
	private long mTime = 0;
	private static final int ID_TRIGGER_TIMER = 0;
	private static final long TIMER_DELAY = 250; 
	
	
	public class LocalBinder extends Binder
	{
		TimerService getService()
		{
			return TimerService.this;
		}
	}
	
	private final IBinder mBinder = new LocalBinder();
	
	private Handler mHandler = new Handler()
	{
		// This method will update the counter TextView each TIMER_DELAY
		// while mHandler contains messages ID_TRIGGER_TIMER
		public void handleMessage(Message msg)
		{
			if (msg.what == ID_TRIGGER_TIMER)
			{
				long current = System.currentTimeMillis();
			
				mTime += current - mStart;
				mStart = current;
			
				updateTime(mTime);

				mHandler.sendEmptyMessageDelayed(ID_TRIGGER_TIMER, TIMER_DELAY);
			}
		}
	};	
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		mTime = intent.getLongExtra( MainActivity.KEY_INTENT_EXTRA_TIME, 0);
		return mBinder;
	}
	
	@Override
	public void onCreate()
	{
		Log.d(TAG, "onCreate");
		super.onCreate();
		mNM = (NotificationManager) getSystemService( NOTIFICATION_SERVICE );
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.d(TAG, "onStartCommand " + startId);
		super.onStartCommand(intent, flags, startId);
		
		// Keep restarting until we stop the service
		return START_STICKY;
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		// Cancel the ongoing notification
		mNM.cancel( TIMER_NOTIFICATION );
		mHandler.removeMessages( ID_TRIGGER_TIMER );
	}	
	
	public long getTime() { return mTime; }
	
	public void stopTimer()
	{
		mHandler.removeMessages( ID_TRIGGER_TIMER);
		stopSelf();
		mNM.cancel( TIMER_NOTIFICATION );
	}
	
	public void startTimer()
	{
		// Show notification when we start the timer
		showNotification();

		mStart = System.currentTimeMillis();

		// Only a single message type, 0
		mHandler.removeMessages( ID_TRIGGER_TIMER );
		mHandler.sendEmptyMessage( ID_TRIGGER_TIMER );
	}
	
	public void resetTimer()
	{
		stopTimer();
		timerStopped(mTime);
		mTime = 0;
	}
	
	public boolean isStopped()
	{
		return !mHandler.hasMessages( ID_TRIGGER_TIMER );
	}
	
	private void updateTime(long time)
	{
		// Broadcast update time
		Log.d(TAG, "updateTime, send ACTION_TIME_UPDATE");
		Intent intent = new Intent(MainActivity.ACTION_TIME_UPDATE );
		intent.putExtra(MainActivity.KEY_INTENT_EXTRA_TIME, time);
		sendBroadcast(intent);
		
		updateNotification(time);
	}

	private void timerStopped(long time)
	{
		Log.d(TAG, "timerStopped, send ACTION_TIMER_FINISHED");	
		// Broadcast timer stopped
		Intent intent = new Intent(MainActivity.ACTION_TIMER_FINISHED );
		intent.putExtra(MainActivity.KEY_INTENT_EXTRA_TIME, time);
		sendBroadcast(intent);
		
		// Stop the notification
		stopForeground(true);
	}
	
	private void showNotification()
	{
		mNotification = new NotificationCompat.Builder( this ).setSmallIcon( R.drawable.ic_launcher ).build();
		mNotification.flags = Notification.FLAG_ONGOING_EVENT;
		
		 // Use start foreground as user would notice if timer was stopped
		startForeground( TIMER_NOTIFICATION, mNotification);
	}
	
	private void updateNotification(long time)
	{
		String title = getResources().getString(R.string.running_timer_notification_title);
		String message = DateUtils.formatElapsedTime( time / 1000 );
		
		Context ctx = this; //getApplicationContext();
		
		Intent intent = new Intent(ctx, MainActivity.class);
		
		int requestCode = 0;
		int flags = 0;
		PendingIntent pi = PendingIntent.getActivity(ctx, requestCode, intent, flags);
		
		// Deprecated, but Notification.Builder is available only since API 11
		mNotification.setLatestEventInfo( ctx, title, message, pi);

		/*
		mNotification = new NotificationCompat.Builder(ctx)
							.setContentTitle(title)
							.setContentText(message)
							//.setContentIntent(pi)
							.setSmallIcon(R.drawable.ic_launcher)
							.build();
		*/
		mNM.notify(TIMER_NOTIFICATION, mNotification);
	}
	
}
