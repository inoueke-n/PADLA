package jp.ac.osaka_u.padla;

public class DebugMessage {
	private final String messageHead = "[LOG4JCORE-EXTENDED]:";
	public String getMessagehead() {
		return messageHead;
	}

	static boolean ISDEBUG = false;
	
	public void setISDEBUG(boolean iSDEBUG) {
		ISDEBUG = iSDEBUG;
	}

	public DebugMessage() {
	}

	/**
	 * Print messages with messageHead
	 * @param args
	 */
	public void print(String arg) {
		System.out.print(messageHead);
		System.out.println(arg);
	}
	
	/**
	 * Print messages when debug mode
	 * @param args
	 */
	public void printOnDebug(String arg) {
		if(ISDEBUG) {
			this.print(arg);
		}
	}
}

