package jp.ac.osaka_u.ist.padla;

public class AgentOptions {
	private String LEARNINGDATA = null;
	private String BUFFEROUTPUT = null;
	private int BUFFER = 0;
	private int INTERVAL = 0;
	private double EP = 0;
	private boolean ISDEBUG = false;
	private String PHASEOUTPUT = null;
	private String DEBUGLOGOUTPUT = null;
	
	public String getLEARNINGDATA() {
		return LEARNINGDATA;
	}
	public void setLEARNINGDATA(String fILENAME) {
		LEARNINGDATA = fILENAME;
	}
	public String getBUFFEROUTPUT() {
		return BUFFEROUTPUT;
	}
	public void setBUFFEROUTPUT(String bUFFEROUTPUT) {
		BUFFEROUTPUT = bUFFEROUTPUT;
	}
	public int getBUFFER() {
		return BUFFER;
	}
	public void setBUFFER(int cACHESIZE) {
		BUFFER = cACHESIZE;
	}
	public int getINTERVAL() {
		return INTERVAL;
	}
	public void setINTERVAL(int iNTERVAL) {
		INTERVAL = iNTERVAL;
	}
	public double getEP() {
		return EP;
	}
	public void setEP(double eP) {
		EP = eP;
	}
	public boolean isISDEBUG() {
		return ISDEBUG;
	}
	public void setISDEBUG(boolean iSDEBUG) {
		ISDEBUG = iSDEBUG;
	}
	public String getPHASEOUTPUT() {
		return PHASEOUTPUT;
	}
	public void setPHASEOUTPUT(String oUTPUTFILENAME) {
		PHASEOUTPUT = oUTPUTFILENAME;
	}
	public String getDEBUGLOGOUTPUT() {
		return DEBUGLOGOUTPUT;
	}
	public void setDEBUGLOGOUTPUT(String dEBUGLOGOUTPUT) {
		DEBUGLOGOUTPUT = dEBUGLOGOUTPUT;
	}

}