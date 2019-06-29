package jp.ac.osaka_u.ist.padla;

public class ResultOfPhaseDetection {
	private double maxSimilarity = 0;
	public double getMaxSimilarity() {
		return maxSimilarity;
	}
	public void setMaxSimilarity(double maxSimilarity) {
		this.maxSimilarity = maxSimilarity;
	}
	public boolean isUnknownPhase() {
		return isUnknownPhase;
	}
	public void setUnknownPhase(boolean isUnknownPhase) {
		this.isUnknownPhase = isUnknownPhase;
	}
	public int getPhaseNum() {
		return phaseNum;
	}
	public void setPhaseNum(int phaseNum) {
		this.phaseNum = phaseNum;
	}
	private boolean isUnknownPhase = false;
	private int phaseNum = 0;
}
