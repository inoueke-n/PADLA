package jp.ac.osaka_u.padla;

public class AgentMessage {
	private final String messageHead = "[AGENT]:";
	public String getMessagehead() {
		return messageHead;
	}
	private boolean debugflag = false;


	public void setDebugflag(boolean debugflag) {
		this.debugflag = debugflag;
	}

	/**
	 * Print messages with messageHead
	 * @param args
	 */
	public void print(String arg) {
		if(debugflag) {
			System.err.print(messageHead);
			System.err.println(arg);
		}
	}

	/**
	 * Print error messages with messageHead
	 * @param arg
	 */
	public void printerr(String arg) {
		if(debugflag) {
			System.err.print(messageHead);
			System.err.println(arg);
		}
	}
}
