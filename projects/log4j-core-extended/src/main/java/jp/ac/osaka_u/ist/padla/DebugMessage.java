package jp.ac.osaka_u.ist.padla;

public class DebugMessage {
	private final String messageHead = "[LOG4JCORE-EXTENDED]:";
	public String getMessagehead() {
		return messageHead;
	}

	boolean ISDEBUG = true;


	public void setISDEBUG(boolean iSDEBUG) {
		ISDEBUG = iSDEBUG;
	}

	/**
	 * Print messages with messageHead
	 * @param args
	 */
	public void print(String arg) {
		if(ISDEBUG) {
			System.err.print(messageHead);
			System.err.println(arg);
			System.err.flush();
		}
	}

}

