package padla;

import java.lang.instrument.Instrumentation;

public class Start {
	  public static void premain(String args, Instrumentation inst) {
		  CallRuntimeWeaver crw = new CallRuntimeWeaver(args, inst);
		  crw.start();
	  }
}
