package padla;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class IdArrayManager extends Thread{
	private static IdArrayManager idarraymanager;

	private List<double[]> knownPhaseList;
	private int updatecounter = 0;
	private long COUNTERLIMIT = 1000000;
	private double phaseThreshold = 0.5;
	private List<Long> idlist;
	private long idArray[];
	public Connector connector;
	public Config Config = new Config();
	private boolean success = true;
	private boolean updated = false;
	private boolean isUnknownPhase = false;
	private CalcVectors calcvectors;
	private int numOfId = 200000;

//	public synchronized void setUpdated(boolean updated) {
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public boolean isUpdated() {
		return updated;
	}

	private IdArrayManager() {

		idlist = new ArrayList<Long>();
		idArray = new long[numOfId];
		knownPhaseList = new ArrayList<double[]>();
		connector = new Connector();
		calcvectors = new CalcVectors();
		phaseThreshold = AgentOptions.getInstance().getEp();
		COUNTERLIMIT = AgentOptions.getInstance().getCounterLimit();
		System.out.println("threshold: " + phaseThreshold);
		System.out.println("limit: " + COUNTERLIMIT);
	}

	public void run() {
		establishConnection();
		if(success) firstSend();
		while(success) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(isUpdated()) {
				Message message = new Message();
				message.setISFIRSTLEVEL(isUnknownPhase);
//				message.setISFIRSTLEVEL(false);
				socketWrite(message);
				setUpdated(false);
				if(AgentOptions.getInstance().isDebug()) {
					if(isUnknownPhase) {
						DebugLogFile.getInstance().write("IsUnknownPhase: yes");
					}else {
						DebugLogFile.getInstance().write("IsUnknownPhase: no");
					}
				}
			}
		}
	}


	private void establishConnection() {
		Config.load();
		try {
			Thread.sleep(AgentOptions.getInstance().getAgentWaitingTime());
		} catch (InterruptedException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		try {
			AgentMessage.getInstance().printerr("connector: " + connector + "Config: " + Config);
			connector.connect(getInstance().Config.Host, getInstance().Config.Port);
			AgentMessage.getInstance().printerr("Connection is established");
		} catch (Exception e) {
			AgentMessage.getInstance().printerr("Failed to connect ");
			e.printStackTrace();
			success = false;
		}
	}


	private void firstSend() {
		Message message = new Message();
		message.setBUFFEROUTPUT(AgentOptions.getInstance().getBufferoutput());
		message.setCACHESIZE(AgentOptions.getInstance().getCacheSize());
		message.setNUMOFMETHODS(1);
		try {
			getInstance().connector.write(message);
		} catch (IOException e) {
			AgentMessage.getInstance().print("Connection is closed");
		}
	}

	public static void initialize() {
		idarraymanager = new IdArrayManager();
	}

	public static IdArrayManager getInstance() {
		return idarraymanager;
	}

	public int getUpdatecounter() {
		return updatecounter;
	}

//	public synchronized void addUpdatecounter() {
	public void addUpdatecounter() {
		if(updatecounter < COUNTERLIMIT) {
			updatecounter++;
		}else {
			double[] normalizedvector = calcvectors.normalizeVector(idArray, numOfId);
			calcvectors.initArray(idArray);
			isUnknownPhase = calcvectors.isUnknownPhase(normalizedvector,knownPhaseList,numOfId,phaseThreshold).isUnknownPhase();
			if(isUnknownPhase)knownPhaseList.add(normalizedvector);
			setUpdated(true);
			resetUodatecounter();
		}
	}

	private void socketWrite(Message message) {
			try {
				getInstance().connector.write(message);
			} catch (IOException e) {
				System.err.println("Connection is closed");
				success = false;
			}
	}

	public void resetUodatecounter() {
		updatecounter = 0;
	}


	/**
	 * Increment number the number of "dataId" execution.
	 * @param dataId
	 */
//	public synchronized void listadd(int dataId) {
	public void listadd(int dataId) {
		idArray[dataId % numOfId]++;

		addUpdatecounter();

	}


}
