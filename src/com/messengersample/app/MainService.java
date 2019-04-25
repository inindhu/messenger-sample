package com.messengersample.app;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class MainService extends Service {

	private static final String TAG = "MainService";
	Messenger mServiceMessenger = new Messenger(new IncomingHandler());
	Messenger mActivityMessenger;
	
	private class IncomingHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			String str = (String)msg.obj;
			Toast.makeText(getApplicationContext(), 
					"From Activity -> " + str, Toast.LENGTH_LONG).show();
			Message lMsg = new Message();
			lMsg.obj="Hello Activity";
			try {
				mActivityMessenger.send(lMsg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		mServiceMessenger = new Messenger(new IncomingHandler());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		mActivityMessenger = intent.getParcelableExtra("Messenger");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return mServiceMessenger.getBinder();
	}
	

}
