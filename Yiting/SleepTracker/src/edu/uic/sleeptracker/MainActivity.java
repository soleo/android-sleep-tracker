package edu.uic.sleeptracker;

import com.musicg.wave.WaveHeader;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final int DETECT_NONE = 0;
	public static final int DETECT_SNORE = 1;
	public static int selectedDetection = DETECT_NONE;

	// detection parameters
	private DetectorThread detectorThread;
	private RecorderThread recorderThread;
	private Thread detectedTextThread;
	public static int snoreValue = 0;

	// views
	private View mainView, listeningView;
	private Button mSleepRecordBtn, mAlarmBtn, mRecordBtn, mTestBtn;
	private TextView totalSnoreDetectedNumberText, txtAbs;

	// ------------------------
	private Toast mToast;

	private Handler rhandler = new Handler();
	private Handler showhandler = null;
	private Handler alarmhandler = null;

	private Intent intent;
	private PendingIntent pendingIntent;
	private AlarmManager am;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle("UIC SleepTracker Demo");


		mSleepRecordBtn = (Button) this.findViewById(R.id.btnSleepRecord);
		mAlarmBtn = (Button) findViewById(R.id.btnSelectAlarm);
		mRecordBtn = (Button) findViewById(R.id.btnRecordAlarm);
		mTestBtn = (Button) findViewById(R.id.btnAlarmTest);
		txtAbs = (TextView) findViewById(R.id.txtaverageAbsValue);

		intent = new Intent(MainActivity.this, AlarmReceiverActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		pendingIntent = PendingIntent.getActivity(MainActivity.this, 2, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		am = (AlarmManager) getSystemService(ALARM_SERVICE);

		showhandler = new Handler() {
			public void handleMessage(Message msg) {
				txtAbs.setText(msg.obj.toString());
			}
		};

		alarmhandler = new Handler() {
			public void handleMessage(Message msg) {
				int interval = 5;
				int i = msg.arg1;
				setLevel(i);
				AlarmStaticVariables.level = AlarmStaticVariables.level1;
				am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
						+ (interval * 1000), pendingIntent);
			}
		};

		mSleepRecordBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				selectedDetection = DETECT_SNORE;
				// alarmThread = new AlarmThread(pendingIntent, am);
				recorderThread = new RecorderThread(showhandler);
				recorderThread.start();
				detectorThread = new DetectorThread(recorderThread,
						alarmhandler);
				detectorThread.start();
				mToast = Toast.makeText(getApplicationContext(),
						"Recording & Detecting start", Toast.LENGTH_LONG);
				mToast.show();
				// goListeningView();
			}
		});

		mAlarmBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this,
						AlarmSelectActivity.class);
				startActivity(intent);
			}
		});

		mRecordBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				rhandler.removeCallbacks(recordActivity);
				rhandler.postDelayed(recordActivity, 1000);
			}
		});

		mTestBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				int level = 1;
				setLevel(level);
				startOneShoot();
			}
		});
	}

	private Runnable recordActivity = new Runnable() {
		public void run() {
			Intent intent = new Intent(MainActivity.this,
					AlarmRecordActivity.class);
			startActivity(intent);
		}
	};

	public void startOneShoot() {
		int i = 5;
		am.set(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + (i * 1000), pendingIntent);
	}

	public void setLevel(int l) {
		switch (l) {
		case 0:
			AlarmStaticVariables.level = AlarmStaticVariables.level0;
			break;
		case 1:
			AlarmStaticVariables.level = AlarmStaticVariables.level1;
			break;
		case 2:
			AlarmStaticVariables.level = AlarmStaticVariables.level2;
			break;
		case 3:
			AlarmStaticVariables.level = AlarmStaticVariables.level3;
			break;
		default:
			AlarmStaticVariables.level = AlarmStaticVariables.level1;
			break;
		}
	}

	private void goHomeView() {
		setContentView(mainView);
		if (recorderThread != null) {
			recorderThread.stopRecording();
			recorderThread = null;
		}
		if (detectorThread != null) {
			detectorThread.stopDetection();
			detectorThread = null;
		}
		selectedDetection = DETECT_NONE;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Quit demo");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			am.cancel(pendingIntent);
			finish();
			break;
		default:
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			goHomeView();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void onDestroy() {
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
