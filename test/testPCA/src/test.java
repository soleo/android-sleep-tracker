import pca_transform.PCA;
import Jama.Matrix;


public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("PCA Testing start..... ");
		System.out.println("Running a demonstrational program on some sample data ...");
		Matrix trainingData = new Matrix(new double[][] {
			{1, 2, 3, 4, 5, 6},
			{6, 5, 4, 3, 2, 1},
			{2, 2, 2, 2, 2, 2}});
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
				{1, 2, 3, 4, 5, 6},
				{1, 2, 1, 2, 1, 2}});
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
