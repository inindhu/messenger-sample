package com.messengersample.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	Messenger mActivityMessenger; 
	Messenger mServiceMessenger;
	MyServiceConnection mCon;

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private class IncomingHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			String str = (String)msg.obj;
			Toast.makeText(getApplicationContext(), 
					"From Service -> " + str, Toast.LENGTH_LONG).show();
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.activity_main);

		mActivityMessenger = new Messenger(new IncomingHandler());

		Intent lIntent = new Intent(MainActivity.this, MainService.class);
		lIntent.putExtra("Messenger", mActivityMessenger);
		startService(lIntent);
		
		mCon = new MyServiceConnection();
		
		((Button)findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "onClick");
				Message msg = new Message();
				msg.obj = "Hi service..";
				try {
					mServiceMessenger.send(msg);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		unbindService(mCon);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		Intent lIntent = new Intent(MainActivity.this, MainService.class);
		bindService(lIntent, mCon, 0);
	}

	class MyServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "onServiceConnected");
			mServiceMessenger = new Messenger(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "onServiceDisconnected");
			mServiceMessenger = null;
			unbindService(mCon);
		}

	}
}
