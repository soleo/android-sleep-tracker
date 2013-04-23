package com.example.alarm_demo;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class AlarmReceiverActivity extends Activity {
	private MediaPlayer mMediaPlayer;
	private PowerManager.WakeLock mWakeLock;

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
		setContentView(R.layout.alarm);

		Button stopAlarm = (Button) findViewById(R.id.btnStopAlarm);
		stopAlarm.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				mMediaPlayer.stop();
				finish();
			}
		});
		playSound(this,getAlarmUri());
	}

	private void playSound(Context context,Uri alert){
		mMediaPlayer = new MediaPlayer();
		try{
			mMediaPlayer.setDataSource(context,alert);
			final AudioManager audioManager = 
					(AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			if(audioManager.getStreamVolume(AudioManager.STREAM_ALARM)!=0){
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
			}
		}catch(IOException e){
			Log.i("AlarmReceiver", "No audio files are found!");
		}
	}
	
	private Uri getAlarmUri(){
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);//alarm first
		if(alert==null){
			alert=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if(alert==null){
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
















