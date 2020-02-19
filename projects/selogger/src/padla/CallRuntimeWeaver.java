package padla;

import java.lang.instrument.Instrumentation;

public class CallRuntimeWeaver extends Thread {
	private String args;
	private Instrumentation inst;
	public String getArgs() {
		return args;
	}
	public void setArgs(String args) {
		this.args = args;
	}
	public Instrumentation getInst() {
		return inst;
	}
	public void setInst(Instrumentation inst) {
		this.inst = inst;
	}

	public CallRuntimeWeaver (String args, Instrumentation inst) {
		setArgs(args);
		setInst(inst);
	}

	public void run() {
//		RuntimeWeaver.start(getArgs(), getInst());
	}

}
