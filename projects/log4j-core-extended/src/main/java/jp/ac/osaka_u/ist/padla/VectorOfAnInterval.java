package jp.ac.osaka_u.ist.padla;

public class VectorOfAnInterval {
	private int numOfMethods = 0;
	private int countSample = 0;
	public int getNumOfMethods() {
		return numOfMethods;
	}
	public void setNumOfMethods(int numOfMethods) {
		this.numOfMethods = numOfMethods;
		sumOfVectors = new double[this.numOfMethods];
		normalizedVector= new double[this.numOfMethods];
	}
	private double[] sumOfVectors = null;
	public double[] getSumOfVectors() {
		return sumOfVectors;
	}
	public void setSumOfVectors(double[] sumOfVectors) {
		this.sumOfVectors = sumOfVectors;
	}
	
	private double[] normalizedVector = null;
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
