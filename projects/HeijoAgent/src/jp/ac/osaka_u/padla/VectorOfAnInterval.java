package jp.ac.osaka_u.padla;

public class VectorOfAnInterval {
	private int numOfMethods;
	private int countSample;
	private double[] sumOfVectors;
	private double[] normalizedVector;

	public VectorOfAnInterval(int numOfMethods) {
		this.numOfMethods = numOfMethods;
		sumOfVectors = new double[this.numOfMethods];
		normalizedVector= new double[this.numOfMethods];
	}

	public int getNumOfMethods() {
		return numOfMethods;
	}

	public double[] getSumOfVectors() {
		return sumOfVectors;
	}
	public void setSumOfVectors(double[] sumOfVectors) {
		this.sumOfVectors = sumOfVectors;
	}

	public double[] getNormalizedVector() {
		return normalizedVector;
	}
	public void setNormalizedVector(double[] normalizedVector) {
		this.normalizedVector = normalizedVector;
	}
	public int getCountSample() {
		return countSample;
	}
	public void resetCountSample() {
		this.countSample = 0;
	}

	public void incCountSamaple() {
		this.countSample++;
	}

}
