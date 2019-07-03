package jp.ac.osaka_u.ist.padla;

import jp.naist.ogami.message.Message;

public class AgentOptions {
	public AgentOptions(Message message) {
		super();
		LEARNINGDATA = message.LEARNINGDATA;
		BUFFEROUTPUT = message.BUFFEROUTPUT;
		BUFFER = message.BUFFER;
		INTERVAL = message.INTERVAL;
		EP = message.EP;
		ISDEBUG = message.ISDEBUG;
		PHASEOUTPUT = message.PHASEOUTPUT;
		DEBUGLOGOUTPUT = message.DEBUGLOGOUTPUT;
	}
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
	public String getBUFFEROUTPUT() {
		return BUFFEROUTPUT;
	}
	public int getBUFFER() {
		return BUFFER;
	}
	public int getINTERVAL() {
		return INTERVAL;
	}
	public double getEP() {
		return EP;
	}
	public boolean isISDEBUG() {
		return ISDEBUG;
	}
	public String getPHASEOUTPUT() {
		return PHASEOUTPUT;
	}
	public String getDEBUGLOGOUTPUT() {
		return DEBUGLOGOUTPUT;
	}

}