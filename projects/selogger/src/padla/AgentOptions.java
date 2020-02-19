package padla;

import java.io.IOException;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AgentOptions {

	private static AgentOptions options;



	public static void initialize() {
		options = new AgentOptions();
	}

	public static AgentOptions getInstance() {
		return options;
	}

	private String target = null;
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	private String learningData = null;
	public String getLearningData() {
		return learningData;
	}

	public void setLearningData(String learningData) {
		this.learningData = learningData;
	}

	private String bufferoutput = null;
	public String getBufferoutput() {
		return bufferoutput;
	}

	public void setBufferoutput(String bufferoutput) {
		this.bufferoutput = bufferoutput;
	}

	private String phaseoutput = null;
	public String getPhaseoutput() {
		return phaseoutput;
	}

	public void setPhaseoutput(String phaseoutput) {
		this.phaseoutput = phaseoutput;
	}

	private int cachesize = 0;
	public int getCacheSize() {
		return cachesize;
	}

	public void setCacheSize(int buffer) {
		this.cachesize = buffer;
	}

	private int interval = 0;
	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	private double ep = 0;
	public double getEp() {
		return ep;
	}

	public void setEp(double ep) {
		this.ep = ep;
	}

	private boolean isDebug = false;
	public boolean isDebug() {
		return isDebug;
	}

	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	private int agentWaitingTime = 0;
	public int getAgentWaitingTime() {
		return agentWaitingTime;
	}

	public void setAgentWaitingTime(int agentWaitingTime) {
		this.agentWaitingTime = agentWaitingTime;
	}

	private String debugLogOutput = null;
	public String getDebugLogOutput() {
		return debugLogOutput;
	}

	public void setDebugLogOutput(String debugLogOutput) {
		this.debugLogOutput = debugLogOutput;
	}

	private String mode = "Normal";
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}

	private int SampleInterval = 10;
	public int getSampleInterval() {
		return SampleInterval;
	}

	public void setSampleInterval(int sampleInterval) {
		SampleInterval = sampleInterval;
	}

	public int getUpdateInterval() {
		return UpdateInterval;
	}

	public void setUpdateInterval(int updateInterval) {
		UpdateInterval = updateInterval;
	}

	public int bufferedInterval = 2;


	public int getBufferedInterval() {
		return bufferedInterval;
	}

	public void setBufferedInterval(int bufferedInterval) {
		this.bufferedInterval = bufferedInterval;
	}

	private int UpdateInterval = 500;

	private String Seloggeroutput = null;

//	private AgentMessage.getInstance() AgentMessage.getInstance() = new AgentMessage.getInstance()();

	public String getSeloggeroutput() {
		return Seloggeroutput;
	}

	public void setSeloggeroutput(String seloggeroutput) {
		Seloggeroutput = seloggeroutput;
	}

	private int phaseDetectionType = 0;


	public int getPhaseDetectionType() {
		return phaseDetectionType;
	}

	public void setPhaseDetectionType(String Type) {
		if(Type.equals("time")) {
			this.phaseDetectionType = 1;
		}else if(Type.equals("number")) {
			this.phaseDetectionType = 2;
		}else if(Type.equals("object")) {
			this.phaseDetectionType = 3;
		}else if(Type.equals("number_fast")){
			this.phaseDetectionType = 4;
		}else {
			AgentMessage.getInstance().printerr("Error: Invalid phase detection type " + Type);
		}
	}

	private long counterLimit = 1000000;


	public long getCounterLimit() {
		return counterLimit;
	}

	public void setCounterLimit(long counterLimit) {
		this.counterLimit = counterLimit;
	}

	public void setOptions(String optionfile) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document document = null;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(Paths.get(optionfile).toFile());
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

		Element root = document.getDocumentElement();
		NodeList options = root.getElementsByTagName("Option");

		for (int i = 0; i < options.getLength(); i++) {
			Element option = (Element) options.item(i);

			switch(option.getAttribute("type")) {
			case "target":
				this.setTarget(option.getAttribute("value"));
				break;
			case "learningData":
				this.setLearningData(option.getAttribute("value"));
				break;
			case "bufferOutput":
				this.setBufferoutput(option.getAttribute("value"));
				break;
			case "phaseOutput":
				this.setPhaseoutput(option.getAttribute("value"));
				break;
			case "buffer":
				this.setCacheSize(Integer.valueOf(option.getAttribute("value")));
				break;
			case "interval":
				this.setInterval(Integer.valueOf(option.getAttribute("value")));
				break;
			case "threshold":
				this.setEp(Double.valueOf(option.getAttribute("value")));
				break;
			case "isDebug":
				if(Integer.valueOf(option.getAttribute("value")) == 1) {
					this.setDebug(true);
					AgentMessage.getInstance().setDebugflag(true);
				}
				break;
			case "agentWaitingTime":
				this.setAgentWaitingTime(Integer.valueOf(option.getAttribute("value")));
				break;
			case "debugLogOutput":
				this.setDebugLogOutput(option.getAttribute("value"));
				break;
			case "mode":
				this.setMode(option.getAttribute("value"));
				break;
			case "sampleInterval":
				this.setSampleInterval(Integer.valueOf(option.getAttribute("value")));
				break;
			case "updateInterval":
				this.setUpdateInterval(Integer.valueOf(option.getAttribute("value")));
				break;
			case "bufferedInterval":
				this.setBufferedInterval(Integer.valueOf(option.getAttribute("value")));
				break;
			case "counterLimit":
				this.setCounterLimit(Long.valueOf(option.getAttribute("value")));
				break;
			default:
				AgentMessage.getInstance().print("ERROR Invalid argument:" + option.getAttribute("type"));
			}
		}
	}

	public void printOptions() {
		AgentMessage.getInstance().print("---options---");
		AgentMessage.getInstance().print("target = " + this.getTarget());
		AgentMessage.getInstance().print("learningData = " + this.getLearningData());
		AgentMessage.getInstance().print("bufferoutput = " + this.getBufferoutput());
		AgentMessage.getInstance().print("phaseoutput = " + this.getPhaseoutput());
		AgentMessage.getInstance().print("buffer = " + this.getCacheSize());
		AgentMessage.getInstance().print("interval = " + this.getInterval());
		AgentMessage.getInstance().print("threshold = " + this.getEp());
		AgentMessage.getInstance().print("mode = " + this.getMode());
		AgentMessage.getInstance().print("sampleInterval = " + this.getSampleInterval());
		AgentMessage.getInstance().print("updateInterval = " + this.getUpdateInterval());
		AgentMessage.getInstance().print("bufferedInterval = " + this.getBufferedInterval());
		if(this.isDebug()) {
			AgentMessage.getInstance().print("isDebug = true");
		}else {
			AgentMessage.getInstance().print("isDebug = false");
		}
		AgentMessage.getInstance().print("agentWaitingTime = " + this.getAgentWaitingTime());
		AgentMessage.getInstance().print("debugLogOutput = " + this.getDebugLogOutput());
		AgentMessage.getInstance().print("Seloggeroutput = " + this.getSeloggeroutput());
		AgentMessage.getInstance().print("counterLimit = " + this.getCounterLimit());
		AgentMessage.getInstance().print("---options---\n");

	}
}
