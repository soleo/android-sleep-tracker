import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.musicg.wave.Wave;
import com.musicg.wave.WaveFileManager;
import com.musicg.wave.WaveHeader;
import com.musicg.wave.WaveTypeDetector;

public class Read {
	private WaveHeader waveHear;
	private byte[] data;
	private int channels;
	private int sampleRate;
	private int byteRate;
	private int bytePerSample;
	private float threshold_E;
	private float threshold_Z;
	private float[] E = null;
	/*
	 * Constructor
	 * @param filename
	 * 			wave file
	 */
	public Read(String filename){
		Wave wave = new Wave(filename);
		this.waveHear = wave.getWaveHeader();
		this.data = wave.getBytes();
		this.channels = this.waveHear.getChannels();
		this.byteRate = this.waveHear.getByteRate();
		this.sampleRate = this.waveHear.getSampleRate();
		this.bytePerSample = this.waveHear.getBitsPerSample()/8;
	}
	
	public void cal_threshold(){
		int length_time = 100; //ms
		int overlap_time = 50;//ms
		
		int length = 0; //byte
		int overlap = 0; //byte
		
		
		int count_e = 0;
		float max_E = 0;
		float min_E = 0;
		length = this.byteRate/1000*length_time;
		overlap = this.byteRate/1000*overlap_time;
		int num_E = (this.data.length+(length-overlap)-overlap)/(length-overlap)+1;
		this.E = new float[num_E];
		float sum_E = 0;
		for (int i = 0; i < this.data.length-length; i += length-overlap){
			int tmp_sum = 0;
			for(int j=0; j < length-this.bytePerSample; j += this.bytePerSample){
				for(int k=0; k<this.bytePerSample; k++){
					tmp_sum += this.data[i+j+k]*this.data[i+j+k];
				}
			}
			if(tmp_sum==0)
				continue;
			if(count_e == 0){
				max_E = tmp_sum;
				min_E = tmp_sum;
			}
			else{
				if (tmp_sum>max_E)
					max_E = tmp_sum;
				if(tmp_sum<min_E)
					min_E = tmp_sum;
			}
			E[count_e] = tmp_sum;
			sum_E += tmp_sum;
			count_e++;
		}
		float a = (float) 0.59;
		float b = (float) 2.0;
		float c = (float) 0.5;
		float I_1 = a*(max_E-min_E)+min_E;
		float I_2 = b*min_E;
		if(I_1<I_2)
			this.threshold_E = I_1;
		else
			this.threshold_E = I_2;
		this.threshold_Z = c*sum_E/count_e;
	/*	System.out.println(max_E);
		System.out.println(min_E);
		System.out.println(I_1);
		System.out.println(I_2);
		System.out.println(this.threshold_E);
		System.out.println(this.threshold_Z);*/
	//	for (int i = 0; i < E.length; i++)
		//	System.out.println(E[i]);
	}
	
	public float[] getSnoring(){
		ArrayList<Float> snoring_time = new ArrayList<Float>();//s
		boolean flag = false;
		for(int i = 0; i<this.E.length; i++){
			if (E[i]>this.threshold_E){
				if(flag == false){
					snoring_time.add((float) (i/20.0));
					flag = true;
				}
			}
			else{
				if(flag == true)
					flag = false;
			}
		}
		float[] res = new float[snoring_time.size()];
		for(int i=0;i<snoring_time.size();i++)
			res[i] = snoring_time.get(i);
		return res;
	}
	
	public static void main(String[] args) throws IOException{
		String filename = "/Users/edmond/test03.wav";
		//String outFolder = "/Users/edmond";

		Read readWave = new Read(filename);
		readWave.cal_threshold();
		float[] res = readWave.getSnoring();
		
		for(int i = 0; i<res.length; i++)
			System.out.println(res[i]);
	}
}
