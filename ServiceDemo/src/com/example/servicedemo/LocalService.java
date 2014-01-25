package com.example.servicedemo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class LocalService extends Service {

	public static final String ACTION_ITEM_ADDED = "action_item_added";
	
	private static final String TAG = "LocalService";
	
	private ArrayList<String> mList = new ArrayList<String>();
	private final IBinder mBinder = new LocalBinder();
	
	class LocalBinder extends Binder
	{
		public LocalService getService()
		{
			return LocalService.this;
		}
	};
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.i(TAG, "onCreate");
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		Log.i(TAG, "onBind");
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent)
	{
		Log.i(TAG, "onUnBind");
		return super.onUnbind(intent);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.i(TAG, "onDestroy");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		super.onStartCommand(intent, flags, startId);
		Log.i(TAG, "onStartCommend(" + intent + ",  " + flags + ", " + startId + ")");
	
		mList.add(mList.size() + "] Service started at : " + new Date().toString() );
		
		sendBroadcast( new Intent(ACTION_ITEM_ADDED ));
		
		return Service.START_STICKY;
	}
	
	public List<String> getList()
	{
		return mList;
	}

}
