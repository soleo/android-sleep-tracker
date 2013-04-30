
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import com.musicg.wave.Wave;
import com.musicg.wave.WaveHeader;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Read {
	private WaveHeader waveHear;
	private byte[] data;
	private short[] amplitudes;
	private double threshold_E;
	private double threshold_ZCR;
	private double[] E = null;
	private double[] ZCR = null;
	private double MAX_ZCR;
	private double MIN_ZCR;
	private double AVER_ZCR;
	private double MAX_E;
	private double MIN_E;
	private double AVER_E;
	/*
	 * Constructor
	 * @param filename
	 * 			wave file
	 */
	public Read(String filename){
		Wave wave = new Wave(filename);
		this.waveHear = wave.getWaveHeader();
		this.data = wave.getBytes();
		this.amplitudes = wave.getSampleAmplitudes();
		
	}
	public void setThreshold_E(double value){
		this.threshold_E = value;
	}	
	public double getThreshold_E(){
		return this.threshold_E;
	}
	public void setThreshold_ZCR(double value){
		this.threshold_ZCR = value;
	}
	public double getThreshold_ZCR(){
		return this.threshold_ZCR;
	}
	
 	public void setMIN_ZCR(double value){
		this.MIN_ZCR = value;
	}
	public void setMAX_ZCR(double value){
		this.MAX_ZCR = value;
	}	
	public void setAVER_ZCR(){
		double sum = 0;
		for (int i=0; i<this.ZCR.length; i++)
			sum+=this.ZCR[i];
		this.AVER_ZCR = sum/this.ZCR.length;
	}
	public double getMIN_ZCR(){
		return this.MIN_ZCR;
	}
	public double getMAX_ZCR(){
		return this.MAX_ZCR;
	}	
	public double getAVER_ZCR(){
		return this.AVER_ZCR;
	}

	public void setMAX_E(double value){
		this.MAX_E = value;
	}
	public void setMIN_E(double value){
		this.MIN_E = value;
	}	
	public void setAVER_E(){
		double sum = 0;
		for (int i =0; i<this.E.length; i++)
			sum += this.E[i];
		this.AVER_E = sum/this.E.length;
	}
	public double getMAX_E(){
		return this.MAX_E;
	}	
	public double getMIN_E(){
		return this.MIN_E;
	}
	public double getAVER_E(){
		return this.AVER_E;
	}
	
	public void setE_ZCRArray(int length_time, int overlap_time){//ms
		int length = this.waveHear.getSampleRate()/1000*length_time;
		int overlap = this.waveHear.getSampleRate()/1000*overlap_time;
		int count_e = 0;
		int num_E = (this.data.length+(length-overlap)-overlap)/(length-overlap)+1;
		double tmp_energy[] = new double[num_E];
		double tmp_ZCR[] = new double[num_E];
		for(int i=4; i<this.amplitudes.length-length; i+=length-overlap){
			double sum_slice = 0;
			double sum_ZCR = 0;
			for(int j=0; j<length; j++){
				sum_slice += java.lang.Math.pow(this.amplitudes[i+j],2);
				if((this.amplitudes[i+j]>0)!=(this.amplitudes[i+j+1]>0))
					sum_ZCR++;
			}
			if(sum_slice==0 && sum_ZCR==0)
				continue;
			if(count_e == 0){
				this.setMAX_E(sum_slice);
				this.setMIN_E(sum_slice);
				this.setMAX_ZCR(sum_ZCR);
				this.setMIN_E(sum_ZCR);
			}else{
				if(sum_slice>this.getMAX_E())
					this.setMAX_E(sum_slice);
				if(sum_slice<this.getMIN_E())
					this.setMIN_E(sum_slice);
				if(sum_ZCR>this.getMAX_ZCR())
					this.setMAX_ZCR(sum_ZCR);
				if(sum_ZCR<this.getMIN_ZCR())
					this.setMIN_ZCR(sum_ZCR);
			}
			tmp_energy[count_e] = sum_slice;
			tmp_ZCR[count_e] = sum_ZCR;
			count_e++;
		}
		this.E = new double[count_e];
		this.ZCR = new double [count_e];
		for(int i=0; i<count_e;i++){
			this.E[i] = tmp_energy[i];
			this.ZCR[i] = tmp_ZCR[i];
		}
		this.setAVER_E();
		this.setAVER_ZCR();
		//for(int i=0; i< this.E.length; i++)
			//System.out.println(this.E[i]);
	}
	
	public void cal_threshold(){	

		float a = (float) 0.02;
		float b = (float) 8000;
		float c = (float) 1;
		double I_1 = a*(this.getMAX_E()-this.getMIN_E())+this.getMIN_E();
		double I_2 = b*this.getMIN_E();
		/*if(I_1<I_2)
			this.threshold_E = I_1;
		else
			this.threshold_E = I_2;
		*/
		this.setThreshold_E(I_1);
		this.setThreshold_ZCR(c*this.getAVER_ZCR());

		//for(int i=0; i<this.ZCR.length; i++)
			//System.out.println(this.ZCR[i]);
		//System.out.println(this.getThreshold_E());
		//System.out.println(this.getThreshold_ZCR());
	}
	
	public float[] getSnoring(){
		ArrayList<Float> snoring_time = new ArrayList<Float>();//s
		boolean flag = false;
		for(int i = 0; i<this.E.length; i++){
			if (this.E[i]>this.getThreshold_E() && this.ZCR[i]<this.getThreshold_ZCR()){
				if(flag == false){
					snoring_time.add((float) (i/20.0));
					flag = true;
					//System.out.println(i);
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
	
	public void print_to_file(String filename) throws IOException{
		FileOutputStream fos = new FileOutputStream(filename);
		OutputStreamWriter osw = new OutputStreamWriter(fos);
		BufferedWriter bw = new BufferedWriter(osw);
		float[] res = this.getSnoring();
		for (int i=0; i<res.length; i++){
			bw.write(Float.toString(res[i]));
			if(i!=res.length-1)
				bw.write(",");
		}
		bw.close();
	}
	
/*	public static void main(String[] args) throws IOException{
		String filename = "/Users/edmond/test03.wav";
		String out_filename = "/Users/edmond/res.txt";

		Read readWave = new Read(filename);
		readWave.setE_ZCRArray(100, 50);
		readWave.cal_threshold();
		//float[] res = readWave.getSnoring();
		readWave.print_to_file(out_filename);
		//for(int i = 0; i<res.length; i++)
			//System.out.println(res[i]);
	}*/
}
