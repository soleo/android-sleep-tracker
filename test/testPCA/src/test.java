import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;

import pca_transform.PCA;
import Jama.Matrix;


public class test {
	
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
	
	
	
	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("PCA Testing start..... ");
		double[] numbers = getComplexNumbers();
		double[] reals = new double[numbers.length/2];
		// use just real part
		int increament = 0;
		for(int index = 1; index <numbers.length; index+=2){
			reals[increament] = numbers[index];
			increament++;
		}
		// reformate data
		double[][] data = new double[7][643];
		for(int i = 0; i < 7; i ++){
			for(int j = 0; j < 643; j++){
				data[i][j] = numbers[i*643+j];
			}
		}
		
//		double[][] data = new double[][] {
//				{1, 2, 3, 4, 5, 6},
//				{6, 5, 4, 3, 2, 1},
//				{2, 2, 2, 2, 2, 2}};
		System.out.println(Arrays.toString(data));
		System.out.println("Running a demonstrational program on some sample data ...");
//		Matrix trainingData = new Matrix(new double[][] {
//			{1, 2, 3, 4, 5, 6},
//			{6, 5, 4, 3, 2, 1},
//			{2, 2, 2, 2, 2, 2}});
		Matrix datatobetran = new Matrix(data);
		Matrix trainingData = datatobetran.transpose();
		PCA pca = new PCA(trainingData);
		
		//eigen vectors
		Matrix eigenVectors = pca.getEigenvectorsMatrix();
		
		System.out.println("num eigen vectors: " + eigenVectors.getColumnDimension());
		 
		 for(int i = 0; i < eigenVectors.getColumnDimension(); i++) {
			 System.out.println("eigenvalue for eigenVector " + i + ": " + pca.getEigenvalue(i) );   
		 }
		 
		 eigenVectors.print(10,2);
		//
		Matrix testData = new Matrix(new double[][] {
				{1, 2, 3, 4, 5, 6, 7},
				{1, 2, 1, 2, 1, 2, 7}});
		Matrix transformedData =
			pca.transform(testData, PCA.TransformationType.WHITENING);
		System.out.println("Transformed data:");
		for(int r = 0; r < transformedData.getRowDimension(); r++){
			for(int c = 0; c < transformedData.getColumnDimension(); c++){
				System.out.print(transformedData.get(r, c));
				if (c == transformedData.getColumnDimension()-1) continue;
				System.out.print(", ");
			}
			System.out.println("");
		}
		System.out.println("PCA Testing end..... ");
	}

}
