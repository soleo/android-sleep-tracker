package com.example.alarm_demo;

import android.util.Log;
import android.media.MediaRecorder;
import android.media.MediaPlayer;

import java.io.IOException;

public class SoundCapture {
	private static final String LOG_TAG = "AudioRecord";
	private String mFileName = null;

	// private RecordButton mRecordButton = null;
	private MediaRecorder mRecorder = null;

	// private PlayButton mPlayButton = null;
	private MediaPlayer mPlayer = null;

	SoundCapture(String path, String fileName) {
		this.mFileName = path + fileName + ".ogg";
	}

	public void onRecord(boolean start) {
		if (start) {
			startRecording();
		} else {
			stopRecording();
		}
	}

	public void onPlay(boolean start) {
		if (start) {
			startPlaying();
		} else {
			stopPlaying();
		}
	}

	private void startPlaying() {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(mFileName);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.i(LOG_TAG, "prepare() failed");
		}
	}

	private void stopPlaying() {
		mPlayer.release();
		mPlayer = null;
	}

	private void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}

		mRecorder.start();
	}

	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

}
