package jp.ac.osaka_u.padla;

import java.io.IOException;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Options {
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

	private int buffer = 0;
	public int getBuffer() {
		return buffer;
	}

	public void setBuffer(int buffer) {
		this.buffer = buffer;
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

	private AgentMessage agentmessage = new AgentMessage();
	
	public Options(String optionfile) {
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
				this.setBuffer(Integer.valueOf(option.getAttribute("value")));
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
				}
				break;
			case "agentWaitingTime":
				this.setAgentWaitingTime(Integer.valueOf(option.getAttribute("value")));
				break;
			case "debugLogOutput":
				this.setDebugLogOutput(option.getAttribute("value"));
				break;
			default:
				agentmessage.print("ERROR Invalid argument:" + option.getAttribute("type"));
			}
		}
		
		System.out.println();
		agentmessage.print("---options---");
		agentmessage.print("target = " + this.getTarget());
		agentmessage.print("learningData = " + this.getLearningData());
		agentmessage.print("bufferoutput = " + this.getBufferoutput());
		agentmessage.print("phaseoutput = " + this.getPhaseoutput());
		agentmessage.print("buffer = " + this.getBuffer());
		agentmessage.print("interval = " + this.getInterval());
		agentmessage.print("threshold = " + this.getEp());
		if(this.isDebug()) {
			agentmessage.print("isDebug = true");
		}else {
			agentmessage.print("isDebug = false");
		}
		agentmessage.print("agentWaitingTime = " + this.getAgentWaitingTime());
		agentmessage.print("debugLogOutput = " + this.getDebugLogOutput());
		agentmessage.print("---options---\n");

	}
}
