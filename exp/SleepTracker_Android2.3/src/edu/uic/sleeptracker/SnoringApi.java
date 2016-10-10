package edu.uic.sleeptracker;

//import com.musicg.*;
import com.musicg.api.DetectionApi;
import com.musicg.wave.WaveHeader;
public class SnoringApi extends DetectionApi{
	
	public SnoringApi(WaveHeader waveHeader) {
		super(waveHeader);
	}

	protected void init(){
		// settings for detecting a whistle
		minFrequency = 600.0f;
		maxFrequency = Double.MAX_VALUE;
		
		minIntensity = 100.0f;
		maxIntensity = 100000.0f;
		
		minStandardDeviation = 0.1f;
		maxStandardDeviation = 1.0f;
		
		highPass = 100;
		lowPass = 10000;
		
		minNumZeroCross = 50;
		maxNumZeroCross = 200;
		
		numRobust = 10;
	}
		
	public boolean isSnoring(byte[] audioBytes){
		return isSpecificSound(audioBytes);
	}
}
