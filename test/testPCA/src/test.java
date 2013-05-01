import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;

import com.musicg.wave.Wave;
import com.musicg.wave.WaveHeader;
import com.musicg.wave.extension.Spectrogram;
import com.sun.media.sound.FFT;

import pca_transform.PCA;
import Jama.Matrix;


public class test {
	public static double[] getFFT(){
		String filename = "test03.wav";
		Wave wave = new Wave(filename);
		WaveHeader waveHeader = wave.getWaveHeader();
		double[] spectrum = null;

		// fft size 1024, no overlap
		int fftSampleSize = 4096;
		int fftSignalByteLength = fftSampleSize * waveHeader.getBitsPerSample() / 8;
		byte[] audioBytes = wave.getBytes();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(audioBytes);
		
		int numFrames = inputStream.available() / fftSignalByteLength;
		byte[] bytes = new byte[fftSignalByteLength];
		
		int bytesPerSample = waveHeader.getBitsPerSample() / 8;
		int numSamples = bytes.length / bytesPerSample;
		System.out.println("number of Samples: "+ numSamples);
		// numSamples required to be a power of 2
		if (numSamples > 0 && Integer.bitCount(numSamples) == 1) {
			fftSampleSize = numSamples;
			Wave wave_short = new Wave(waveHeader, bytes);	// audio bytes of this frame
			// spectrum for the clip
			Spectrogram spectrogram = wave_short.getSpectrogram(fftSampleSize, 2);
			double[][] spectrogramData = spectrogram.getAbsoluteSpectrogramData();
			// since fftSampleSize==numSamples, there're only one spectrum which is thisFrameSpectrogramData[0]
			 spectrum = spectrogramData[0];
		}
		
		return spectrum;
		
	}
	public double[] getMagnitudes(double[] amplitudes) {

		int sampleSize = amplitudes.length;
	
		// call the fft and transform the complex numbers
		FFT fft = new FFT(sampleSize / 2, -1);
		fft.transform(amplitudes);
		// end call the fft and transform the complex numbers

		double[] complexNumbers = amplitudes;

		// even indexes (0,2,4,6,...) are real parts
		// odd indexes (1,3,5,7,...) are img parts
		int indexSize = sampleSize / 2;

		// FFT produces a transformed pair of arrays where the first half of the
		// values represent positive frequency components and the second half
		// represents negative frequency components.
		// we omit the negative ones
		int positiveSize = indexSize / 2;

		double[] mag = new double[positiveSize];
		for (int i = 0; i < indexSize; i += 2) {
			mag[i / 2] = Math.sqrt(complexNumbers[i] * complexNumbers[i] + complexNumbers[i + 1] * complexNumbers[i + 1]);
		}

		return mag;
	}
	public static double[][] getData(){
		double[][] data = new double[1614][7];
		File file = new File("test03_for_pca");
		BufferedReader bufRdr = null;
		try {
			bufRdr = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line = null;
		int row = 0;
		int col = 0;
		 
		//read each line of text file
		try {
			while((line = bufRdr.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(line,",");
				col = 0;
				while (st.hasMoreTokens())
				{
					//get next token and store it in the array
					data[row][col] = Double.parseDouble(st.nextToken());
					col++;
				}
				row++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		//close the file
		try {
			bufRdr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	public static double[] getComplexNumbers(){
		double[] complexNumbers = new double[4501*2];
		String[] numbers = new String[4501]; // 7*643
		File file = new File("fft.txt");
		 
		BufferedReader bufRdr = null;
		try {
			bufRdr = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line = null;
		int row = 0;
		int col = 0;
		 
		//read each line of text file
		try {
			while((line = bufRdr.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(line,",");
				while (st.hasMoreTokens())
				{
					//get next token and store it in the array
					numbers[col] = st.nextToken();
					col++;
				}
				row++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		//close the file
		try {
			bufRdr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//parse complex numbers
		
		int index = 0;
		for(String number: numbers){
			if(number.indexOf('+') >= 0){
				int a = number.lastIndexOf("+");
				int d = number.lastIndexOf("i");
				String b = number.substring(0, a); // takes the first part of the string broken at the index of "+"
				String c = number.substring(a, d); // takes the second part of the string broken at the index of "+"
				complexNumbers[index] = Double.parseDouble(b);
				complexNumbers[index+1] = Double.parseDouble(c);
			}else if(number.indexOf('-') >= 0 && number.indexOf('e') <= 0){
				int a = number.lastIndexOf("-");
				int d = number.lastIndexOf("i");
				String b = number.substring(0, a); // takes the first part of the string broken at the index of "+"
				String c = number.substring(a, d); // takes the second part of the string broken at the index of "+"
				complexNumbers[index] = Double.parseDouble(b);
				complexNumbers[index+1] = 0.0 - Double.parseDouble(c);
			}else{
				if(number.indexOf('e') >= 0){
					//System.out.println(number);
				}else{
					complexNumbers[index] = Double.parseDouble(number);
					complexNumbers[index+1] = 0.0;
				}
			}
			
			index = index + 2;
		}
		//System.out.println(Arrays.toString(numbers));
		System.out.println(numbers.length);
		//System.out.println(Arrays.toString(complexNumbers));
		System.out.println(complexNumbers.length);
		return complexNumbers;
		
	}
	
	public static double[][] getFakeData(){
		double[] numbers = getComplexNumbers();
		double[] reals = new double[numbers.length/2];
		double[] imgs = new double[numbers.length/2];
		
		// use just real and img part
		int increament = 0;
		for(int index = 0; index <numbers.length; index+=2){
			reals[increament] = numbers[index];
			imgs[increament] = numbers[index+1];
			increament++;
		}
		
		// reformat data
		double[][] data = new double[7][643];
		for(int i = 0; i < 7; i ++){
			for(int j = 0; j < 643; j++){
				data[i][j] = Math.sqrt(reals[i*643+j]*reals[i*643+j] + imgs[i*643+j]*imgs[i*643+j]);
				//data[i][j] = reals[i*643+j]*reals[i*643+j] + imgs[i*643+j]*imgs[i*643+j];
				//data[i][j] = reals[i*643+j];
			}
			
		}
		return data;
	}
	
	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("PCA Testing start..... ");
		
		
//		double[][] data = new double[][] {
//				{1, 2, 3, 4, 5, 6},
//				{6, 5, 4, 3, 2, 1},
//				{2, 2, 2, 2, 2, 2}};
		
		System.out.println("Running a demonstrational program on some sample data ...");
//		Matrix trainingData = new Matrix(new double[][] {
//			{1, 2, 3, 4, 5, 6},
//			{6, 5, 4, 3, 2, 1},
//			{2, 2, 2, 2, 2, 2}});
//		double[] spectrum = getFFT();
//		System.out.println(spectrum.length);
//		Matrix datatobetran = new Matrix(data);
		double[][] newdata = getData();
		Matrix trainingData = new Matrix(newdata);//datatobetran.transpose();
		//trainingData.print(10, 2);
		PCA pca = new PCA(trainingData);
		
		//eigen vectors
		Matrix eigenVectors = pca.getEigenvectorsMatrix();
	
		System.out.println("num eigen vectors: " + eigenVectors.getColumnDimension());
		 
		 for(int i = 0; i < eigenVectors.getColumnDimension(); i++) {
			 System.out.println("eigenvalue for eigenVector " + i + ": " + pca.getEigenvalue(i) );   
		 }
		 
		 eigenVectors.print(10,2);
		 
		//
//		Matrix testData = new Matrix(new double[][] {
//				{1, 2, 3, 4, 5, 6, 7},
//				{1, 2, 1, 2, 1, 2, 7}});
		Matrix transformedData =
			pca.transform(trainingData, PCA.TransformationType.WHITENING);
		System.out.println("Transformed data:");
		//transformedData.print(10, 2);
//		for(int r = 0; r < transformedData.getRowDimension(); r++){
//			for(int c = 0; c < transformedData.getColumnDimension(); c++){
//				System.out.print(transformedData.get(r, c));
//				if (c == transformedData.getColumnDimension()-1) continue;
//				System.out.print(", ");
//			}
//			System.out.println("");
//		}
		System.out.println("PCA Testing end..... ");
	}

}
