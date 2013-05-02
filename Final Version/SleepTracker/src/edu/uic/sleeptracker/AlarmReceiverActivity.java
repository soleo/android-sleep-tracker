package edu.uic.sleeptracker;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class AlarmReceiverActivity extends Activity {

	private MediaPlayer mMediaPlayer;
	private PowerManager.WakeLock mWakeLock;
	private Long startTime;
	private Handler handler = new Handler();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Wake Log");
		mWakeLock.acquire();
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		startTime = System.currentTimeMillis();
		if (AlarmStaticVariables.level == 0) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(AlarmStaticVariables.partten, -1);
			// AlarmStaticVariables.inProcess = false;
			AlarmReceiverActivity.this.finish();
		} else {
			playSound(this, getAlarmUri());
			handler.removeCallbacks(updateTimer);
			handler.postDelayed(updateTimer, 1000);
		}
	}

	private Runnable updateTimer = new Runnable() {
		public void run() {
			long currentTime;
			while (true) {
				currentTime = System.currentTimeMillis();
				if (currentTime - startTime > AlarmStaticVariables.level)
					break;
			}
			mMediaPlayer.stop();
			AlarmStaticVariables.inProcess = false;
			AlarmReceiverActivity.this.finish();
		}
	};

	private void playSound(Context context, Uri alert) {
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(context, alert);
			final AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mMediaPlayer.setVolume(AlarmStaticVariables.lvolumn,
						AlarmStaticVariables.rvolumn);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
			}
		} catch (IOException e) {
			Log.i("AlarmReceiver", "No audio files are found!");
		}
	}

	private Uri getAlarmUri() {
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);// alarm_first
		if (alert == null) {
			alert = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if (alert == null) {
				RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}
		return alert;
	}

	protected void onStop() {
		super.onStop();
		mWakeLock.release();
	}
}
