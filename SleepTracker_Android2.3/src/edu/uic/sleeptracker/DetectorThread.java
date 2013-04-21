package edu.uic.sleeptracker;

import android.media.AudioFormat;
import android.media.AudioRecord;

public class DetectorThread extends Thread {

	private RecorderThread recorder;
	private volatile Thread _thread;
	
	public DetectorThread(RecorderThread recorderThread) {
		// TODO Auto-generated constructor stub
		this.recorder = recorder;
		AudioRecord audioRecord = recorder.getAudioRecord();

		int bitsPerSample = 0;
		if (audioRecord.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT){
			bitsPerSample = 16;
		}
		else if (audioRecord.getAudioFormat() == AudioFormat.ENCODING_PCM_8BIT){
			bitsPerSample = 8;
		}

		int channel = 0;
		// whistle detection only supports mono channel
		if (audioRecord.getChannelConfiguration() == AudioFormat.CHANNEL_CONFIGURATION_MONO){
			channel = 1;
		}
		
		// TODO: added detection init
	}

	public void stopDetection() {
		// TODO Auto-generated method stub
		_thread = null;
	}

	public int getTotalSnoreDetected() {
		// TODO Auto-generated method stub
		return 0;
	}
	public void start() {
		_thread = new Thread(this);
        _thread.start();
    }

	
	public void run() {
		//@TODO: added run method content
	}

}
