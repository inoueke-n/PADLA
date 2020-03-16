package padla;

public class MethodCallCounter extends Thread{
	public MethodCallCounter() {
	}

	public void run() {
		while(true){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if(IdListManager.getInstance().isUnknownPhase()) {
				DebugLogFile.getInstance().write("IsUnknownPhase: yes");
			}else {
				DebugLogFile.getInstance().write("IsUnknownPhase: no");

			}

		}
	}

}
