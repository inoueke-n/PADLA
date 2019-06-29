package jp.ac.osaka_u.padla;

public class AgentMessage {
	private final String messageHead = "[AGENT]:";
	public String getMessagehead() {
		return messageHead;
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
	 * Print error messages with messageHead
	 * @param arg
	 */
	public void printerr(String arg) {
		System.err.print(messageHead);
		System.err.println(arg);
	}
}
