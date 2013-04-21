package edu.uic.sleeptracker;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class RecorderThread extends Thread {
	private AudioRecord audioRecord;
	private boolean isRecording;
	private int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	private int sampleRate = 44100;
	private int frameByteSize = 2048; // for 1024 fft size (16bit sample size)
	byte[] buffer;

	public RecorderThread(){
		int recBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfiguration, audioEncoding); // need to be larger than size of a frame
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfiguration, audioEncoding, recBufSize);
		buffer = new byte[frameByteSize];
	}

	public AudioRecord getAudioRecord(){
		return audioRecord;
	}

	public boolean isRecording(){
		return this.isAlive() && isRecording;
	}

	public void startRecording(){
		try{
			audioRecord.startRecording();
			isRecording = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopRecording(){
		try{
			audioRecord.stop();
			isRecording = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte[] getFrameBytes(){
		audioRecord.read(buffer, 0, frameByteSize);

		// analyze sound
		int totalAbsValue = 0;
        short sample = 0; 
        float averageAbsValue = 0.0f;
        
        for (int i = 0; i < frameByteSize; i += 2) {
            sample = (short)((buffer[i]) | buffer[i + 1] << 8);
            totalAbsValue += Math.abs(sample);
        }
        averageAbsValue = totalAbsValue / frameByteSize / 2;

        //System.out.println(averageAbsValue);
        
        // no input
        if (averageAbsValue < 30){
        	return null;
        }
        
		return buffer;
	}

	public void run() {
		startRecording();
	}
}
