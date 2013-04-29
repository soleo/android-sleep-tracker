package edu.uic.sleeptracker;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class AlarmRecordActivity extends Activity {

	// private String storagePath = "/system/media/audio/ui";
	// private String storageFilename = "CallNameSound";
	// private SoundCapture soundCapture = new SoundCapture(storagePath,
	// storageFilename);
	// private boolean recording;
	private Toast rToast;

	private MediaRecorder mediaRecorder = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// recording = true;
		// startTime = System.currentTimeMillis();
		// soundCapture.onRecord(recording);

		String fileName = "record.amr";
		try {
			File SDCardpath = Environment.getExternalStorageDirectory();
			File myDataPath = new File(SDCardpath.getAbsolutePath() + "/media");
			if (!myDataPath.exists())
				myDataPath.mkdirs();
			File recodeFile = new File(SDCardpath.getAbsolutePath() + "/media/"
					+ fileName);

			mediaRecorder = new MediaRecorder();
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mediaRecorder.setOutputFile(recodeFile.getAbsolutePath());
			mediaRecorder.prepare();
			mediaRecorder.start();

			rToast = Toast.makeText(getApplicationContext(), "Start recording",
					Toast.LENGTH_LONG);
			rToast.show();

			setContentView(R.layout.recording);
			Button stopRecording = (Button) findViewById(R.id.stopRecordBtn);
			stopRecording.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mediaRecorder != null) {
						rToast = Toast
								.makeText(getApplicationContext(),
										"Stop recording, file saved",
										Toast.LENGTH_LONG);
						rToast.show();
						mediaRecorder.stop();
						mediaRecorder.release();
						mediaRecorder = null;
					}
					finish();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
