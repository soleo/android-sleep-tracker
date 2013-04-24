package com.example.alarm_demo;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

public class AlarmSelectActivity extends Activity {

	private static final int RingtongButton = 0;
	private static final int AlarmButton = 1;
	private static final int NotificationButton = 2;
	File SDCardpath = Environment.getExternalStorageDirectory();
	private String strAlarmFolder = SDCardpath.getAbsolutePath() + "/media";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFolder(strAlarmFolder)) {
			Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
					RingtoneManager.TYPE_ALARM);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,
					"Select Alarm");
			startActivityForResult(intent, AlarmButton);
		}
		// AlarmSelectActivity.this.finish();// ?
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case RingtongButton:
			try {
				Uri pickedUri = data
						.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				if (pickedUri != null) {
					RingtoneManager.setActualDefaultRingtoneUri(
							AlarmSelectActivity.this,
							RingtoneManager.TYPE_RINGTONE, pickedUri);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case AlarmButton:
			try {
				Uri pickedUri = data
						.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				if (pickedUri != null) {
					RingtoneManager.setActualDefaultRingtoneUri(
							AlarmSelectActivity.this,
							RingtoneManager.TYPE_ALARM, pickedUri);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case NotificationButton:
			try {
				Uri pickedUri = data
						.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				if (pickedUri != null) {
					RingtoneManager.setActualDefaultRingtoneUri(
							AlarmSelectActivity.this,
							RingtoneManager.TYPE_NOTIFICATION, pickedUri);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}

	private boolean isFolder(String strFolder) {
		boolean tmp = false;
		File f1 = new File(strFolder);
		if (!f1.exists()) {
			if (f1.mkdirs()) {
				tmp = true;
			} else {
				tmp = false;
			}
		} else {
			tmp = true;
		}
		return tmp;
	}
}
