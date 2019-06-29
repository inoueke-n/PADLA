package jp.ac.osaka_u.ist.padla;

import java.util.Arrays;
import java.util.List;

public class CalcVectors {
	


	/**
	 * Compare vec with learningData and return false if the similarity is above threshold, otherwise return true
	 * If vec and learningData are both zero vector, the similarity is 1
	 * @param vec
	 * @param prevVectors
	 * @param numOfMethods
	 * @param EP
	 * @return
	 */
	public boolean isUnknownPhase(double[] vec, List<double[]> prevVectors, int numOfMethods, double EP) {
		double maxSimilarity = 0;
		double innerProduct = 0;

		for (int i = 0; i < prevVectors.size(); i++) {
			innerProduct = calcInnerProduct(vec, prevVectors.get(i), numOfMethods);
			if (innerProduct == 0.0) {
				double sum = 0.0;
				for (double v : vec) {
					sum += v;
				}
				for (int j = 0; j < vec.length; j++) {
					sum += prevVectors.get(i)[j];
				}
				if (sum == 0.0)
					innerProduct = 1;
			}
			if (innerProduct > maxSimilarity) {
				maxSimilarity = innerProduct;
			}
		}
		if (maxSimilarity > EP) {
			return false;
		}
		return true;
	}
	
	/**
	 * Return inner product of two vectors(array1 and array2)
	 * @param array1
	 * @param array2
	 * @param numOfMethods
	 * @return
	 */
	public double calcInnerProduct(double[] array1, double[] array2, int numOfMethods) {
		double innerProduct = 0;

		for (int i = 0; i < numOfMethods; i++) {
			innerProduct += array1[i] * array2[i];
		}

		return innerProduct;
	}
	
	/**
	 * Return normalized vector
	 * @param vector
	 * @return
	 */
	public double[] normalizeVector(double[] vector, int numOfMethods) {
		double[] normalizedVector = new double[numOfMethods];
		double normOfVector = 0;

		initArray(normalizedVector);

		// calculate norm of the vector
		for (int i = 0; i < numOfMethods; i++) {
			normOfVector += vector[i] * vector[i];
		}
		normOfVector = Math.sqrt(normOfVector);

		for (int i = 0; i < numOfMethods; i++) {
			if (normOfVector != 0) {
				normalizedVector[i] = vector[i] / normOfVector;
			}
		}

		return normalizedVector;
	}
	
	/**
	 * Initialize array with 0
	 * @param array
	 */
	public void initArray(double[] array) {
		Arrays.fill(array, 0.0);
	}

}
