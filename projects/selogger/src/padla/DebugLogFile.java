package padla;

import java.io.FileWriter;
import java.io.IOException;

public class DebugLogFile {

	private static DebugLogFile debuglogfile;
	private FileWriter file = null;

	private DebugLogFile() {
		try {
			file = new FileWriter(AgentOptions.getInstance().getSeloggeroutput()+ "/debuglog.txt");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public static void initialize() {
		debuglogfile = new DebugLogFile();
	}

	public static DebugLogFile getInstance() {
		return debuglogfile;
	}

	public void write(String str) {
		try {
			file.write(str + "\n");
			file.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

}
