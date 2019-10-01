package jp.ac.osaka_u.ist.padla;

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
		System.err.print(messageHead);
		System.err.println(arg);
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

