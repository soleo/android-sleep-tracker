package com.example.alarm_demo;

import android.app.Activity;
import android.os.Bundle;

public class AlarmRecordActivity extends Activity {
	SoundCapture soundCapture;
	String storagePath = "/system/media/audio/ui";
	String storageFilename = "CallNameSound";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		soundCapture = new SoundCapture(storagePath, storageFilename);
	}
}
