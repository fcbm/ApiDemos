package com.example.servicedemo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

public class FakeDownloader<Token> extends HandlerThread {

	private static final String TAG = "FakeDownloader";
	private static final int FAKE_DOWNLOAD = 10;
	
	private Handler mHandler;
	private Handler mResponseHandler;
	private List<Token> mQueue = Collections.synchronizedList( new ArrayList<Token>() );
	
	public FakeDownloader(Handler responseHandler)
	{
		super(TAG);
		mResponseHandler = responseHandler;
	}
	
	@Override
	public void onLooperPrepared()
	{
		Log.i(TAG, "onLooperPrepared");
		mHandler = new Handler ()
		{
			@Override
			public void handleMessage(Message msg)
			{
				if (msg.what == FAKE_DOWNLOAD)
				{
					Token t = (Token) msg.obj;
					Log.i(TAG+"s", "startLongTask..");
					doLongTask();
					Log.i(TAG+"s", "..doneLongTask");
					mResponseHandler.obtainMessage(MainActivity.FAKE_DOWNLOAD_DONE, t).sendToTarget();
				}
			}
		};
	}
	
	public void pushRequest(Token t)
	{
		Log.i(TAG+"s", "Got new msg!");
		//mQueue.add(t);
		mHandler.obtainMessage(FAKE_DOWNLOAD, t).sendToTarget();
	}
	
	public void clearList()
	{
		//mQueue.clear();
		mHandler.removeMessages(FAKE_DOWNLOAD);
	}
	
	private void doLongTask()
	{
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
