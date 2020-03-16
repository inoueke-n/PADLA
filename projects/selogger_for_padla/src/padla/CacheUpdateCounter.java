package padla;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CacheUpdateCounter extends Thread{
	private static CacheUpdateCounter cacheupdatecounter;

	private List<LRUCache<Integer, Integer>> knownPhaseList;
	private int updatecounter = 0;
	private final long COUNTERLIMIT = 1000000;
	private int cachelimit = 1000;
	private LRUCache<Integer, Integer> clone;
	private double phaseThreshold = 0.5;
	private LRUCache<Integer, Integer> lrucache;
	private boolean isPhaseChanged = false;
	public Connector connector;
	public Config Config = new Config();
	private boolean success = true;
	private boolean updated = false;
	private long numberOfNewIds = 0;

	public synchronized void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public boolean isUpdated() {
		return updated;
	}

	private CacheUpdateCounter() {
		lrucache = new LRUCache<>(cachelimit);
		knownPhaseList = new ArrayList<LRUCache<Integer, Integer>>();
		connector = new Connector();
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
				message.setISFIRSTLEVEL(isPhaseChanged);
				socketWrite(message);
				setUpdated(false);
				if(isPhaseChanged) {
					DebugLogFile.getInstance().write("IsUnknownPhase: yes");
				}else {
					DebugLogFile.getInstance().write("IsUnknownPhase: no");
				}
			}
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

	private void establishConnection() {
		Config.load();
		try {
			Thread.sleep(AgentOptions.getInstance().getAgentWaitingTime());
		} catch (InterruptedException e1) {
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
		cacheupdatecounter = new CacheUpdateCounter();
	}

	public static CacheUpdateCounter getInstance() {
		return cacheupdatecounter;
	}

	public int getUpdatecounter() {
		return updatecounter;
	}

	public void resetUodatecounter() {
		updatecounter = 0;
	}

	private void resetNumberOfNewIds() {
		numberOfNewIds = 0;
	}

	/**
	 * Put key and value if the key dose not exist in the map. If exist, it just update recent used data
	 * @param key
	 * @param value
	 */
	public synchronized void lruadd(Integer key, Integer value) {
		if(lrucache.get(key) == null) {
			lrucache.put(key, value);
			numberOfNewIds++;
		}
	}

	public synchronized void addUpdatecounter() {
		if(updatecounter < COUNTERLIMIT) {
			updatecounter++;
		}else {
			isPhaseChanged = isPhaseChanged();
			setUpdated(true);
			resetUodatecounter();
			resetNumberOfNewIds();
		}
	}

	public boolean isPhaseChanged() {
		boolean result = false;
		double div = (double)numberOfNewIds / (double)COUNTERLIMIT;
		if(div > phaseThreshold) {
			result = true;
		}
		DebugLogFile.getInstance().write("numberOfNewIds: " + numberOfNewIds + " COUNTERLIMIT: " + COUNTERLIMIT + " div: " + div);
		if(result) result = IdListManagerSub.getInstance().isUnknownPhase;
		return result;
	}

}
