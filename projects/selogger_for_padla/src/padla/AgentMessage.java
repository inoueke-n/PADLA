package padla;

public class AgentMessage {

	private static AgentMessage agentmessage;
	private final String messageHead = "[AGENT]:";


	public static void initialize() {
		agentmessage = new AgentMessage();
	}

	public static AgentMessage getInstance() {
		return agentmessage;
	}



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
