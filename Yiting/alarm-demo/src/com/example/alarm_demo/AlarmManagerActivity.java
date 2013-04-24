package com.example.alarm_demo;

import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AlarmManagerActivity extends Activity {

	private Button mStartBtn1, mStartBtn2, mStopBtn, mAlarmBtn, mRecordBtn;
	private EditText mTxtSeconds;
	private Toast mToast;

	private static double voice = 1000;// input

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		AlarmStaticVariables.level = AlarmStaticVariables.level1;
		AlarmStaticVariables.partten = AlarmStaticVariables.pattern1;
		mStartBtn1 = (Button) findViewById(R.id.btnSetAlarm1);
		mStartBtn2 = (Button) findViewById(R.id.btnSetAlarm2);
		mStopBtn = (Button) findViewById(R.id.btnStopAlarm);
		mAlarmBtn = (Button) findViewById(R.id.btnSelectAlarm);
		mRecordBtn = (Button) findViewById(R.id.btnRecordAlarm);
		mTxtSeconds = (EditText) findViewById(R.id.txtSeconds);

		mStartBtn1.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {

				try {
					int i = Integer.parseInt(mTxtSeconds.getText().toString());
					Intent intent = new Intent(AlarmManagerActivity.this,
							AlarmReceiverActivity.class);
					PendingIntent pendingIntent = PendingIntent.getActivity(
							AlarmManagerActivity.this, 2, intent,
							PendingIntent.FLAG_CANCEL_CURRENT);
					AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
					am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
							+ (i * 1000), pendingIntent);

					if (mToast != null) {
						mToast.cancel();
					}
					mToast = Toast.makeText(getApplicationContext(),
							"Alarm for activity is set in:" + i + " seconds",
							Toast.LENGTH_LONG);
					mToast.show();
				} catch (NumberFormatException e) {
					if (mToast != null) {
						mToast.cancel();
					}
					mToast = Toast.makeText(AlarmManagerActivity.this,
							"Please enter a number an try again",
							Toast.LENGTH_LONG);
					mToast.show();
					Log.i("AlarmManagerActivity", "Number Format Exception");
				}
			}
		});

		mStartBtn2.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				try {
					int i = Integer.parseInt(mTxtSeconds.getText().toString());
					Intent intent = new Intent(AlarmManagerActivity.this,
							AlarmReceiverActivity.class);
					PendingIntent pendingIntent = PendingIntent.getActivity(
							AlarmManagerActivity.this, 3, intent,
							PendingIntent.FLAG_CANCEL_CURRENT);
					AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
					am.setRepeating(AlarmManager.RTC_WAKEUP,
							System.currentTimeMillis() + (i * 1000), 15 * 1000,
							pendingIntent);
					if (mToast != null) {
						mToast.cancel();
					}
					mToast = Toast
							.makeText(
									getApplicationContext(),
									"Repeating alarm for activity is set in:"
											+ i
											+ " seconds,"
											+ " and repeat every 15 seconds after that",
									Toast.LENGTH_LONG);
					mToast.show();
				} catch (NumberFormatException e) {
					if (mToast != null) {
						mToast.cancel();
					}
					mToast = Toast.makeText(AlarmManagerActivity.this,
							"Please enter a number an try again",
							Toast.LENGTH_LONG);
					mToast.show();
					Log.i("AlarmManagerActivity", "Number Format Exception");
				}
			}
		});

		mStopBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(AlarmManagerActivity.this,
						AlarmReceiverActivity.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(
						AlarmManagerActivity.this, 3, intent, 0);
				AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
				am.cancel(pendingIntent);
				if (mToast != null) {
					mToast.cancel();
				}
				mToast = Toast.makeText(getApplicationContext(),
						"Repeating alarm has been cancelled!",
						Toast.LENGTH_LONG);
				mToast.show();
			}
		});

		mAlarmBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(AlarmManagerActivity.this,
						AlarmSelectActivity.class);
				startActivity(intent);
			}
		});

		mRecordBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(AlarmManagerActivity.this,
						AlarmRecordActivity.class);
				startActivity(intent);
			}
		});

	}
}
