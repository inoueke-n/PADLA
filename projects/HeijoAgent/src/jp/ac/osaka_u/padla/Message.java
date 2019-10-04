package jp.ac.osaka_u.padla;

@org.msgpack.annotation.Message
public class Message {
	public Message() {

	}
	@org.msgpack.annotation.Index(0)
	private boolean ISFIRSTLEVEL = false;

	public boolean isISFIRSTLEVEL() {
		return ISFIRSTLEVEL;
	}

	public void setISFIRSTLEVEL(boolean iSFIRSTLEVEL) {
		ISFIRSTLEVEL = iSFIRSTLEVEL;
	}

	@org.msgpack.annotation.Index(1)
	private String BUFFEROUTPUT = null;

	public String getBUFFEROUTPUT() {
		return BUFFEROUTPUT;
	}

	public void setBUFFEROUTPUT(String bUFFEROUTPUT) {
		BUFFEROUTPUT = bUFFEROUTPUT;
	}

	@org.msgpack.annotation.Index(2)
	private int CACHESIZE = 0;

	public int getCACHESIZE() {
		return CACHESIZE;
	}

	public void setCACHESIZE(int cACHESIZE) {
		CACHESIZE = cACHESIZE;
	}

	@org.msgpack.annotation.Index(3)
	private int NUMOFMETHODS = -1;

	public int getNUMOFMETHODS() {
		return NUMOFMETHODS;
	}

	public void setNUMOFMETHODS(int numOfMethods) {
		this.NUMOFMETHODS = numOfMethods;
	}

}
