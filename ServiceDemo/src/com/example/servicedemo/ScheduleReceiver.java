package com.example.servicedemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScheduleReceiver extends BroadcastReceiver {
	
	private static final String TAG = "ScheduleReceiver";
	
	public static final String ACTION_SCHEDULE = "action.reschedule";
	public static final String ACTION_CANCEL = "action.cancel";
	
	public static boolean isAlarmOn(Context ctx)
	{
		PendingIntent pi = PendingIntent.getService(ctx, 0, new Intent(ctx, LocalService.class), PendingIntent.FLAG_NO_CREATE);
		Log.i(TAG, "isAlarmOn " + (pi != null));  
		return pi != null;
	}	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "onReceive " + intent.getAction());
		
		AlarmManager am = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
		
		Intent i = new Intent(context, LocalService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
		if (intent.getAction() == ACTION_SCHEDULE)
		{
			am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 2000, pi);
		}
		else
		{
			context.stopService(i);
			am.cancel(pi);
			pi.cancel();
		}
	}

}
