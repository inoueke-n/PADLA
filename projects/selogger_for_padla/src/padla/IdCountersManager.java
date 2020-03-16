package padla;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import selogger.logging.EventLogger;
import selogger.logging.io.EventFrequencyLogger.Counter;


public class IdCountersManager extends Thread{
	private static IdCountersManager idcountersmanager;

	private List<ArrayList<Double>> knownPhaseList;
	private int updatecounter = 0;
	private long COUNTERLIMIT = 1000000;
	private double phaseThreshold = 0.5;
//	private List<Long> idlist;
	public Connector connector;
	public Config Config = new Config();
	private boolean success = true;
	private boolean updated = false;
	private boolean isUnknownPhase = false;
	private PrevState ps;

	public synchronized void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public boolean isUpdated() {
		return updated;
	}

	private IdCountersManager() {
//		Timer timer = new Timer();
//		timer.start();

//		idlist = new ArrayList<Long>();
		ps = new PrevState();
		knownPhaseList = new ArrayList<ArrayList<Double>>();
		connector = new Connector();
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
		idcountersmanager = new IdCountersManager();
	}

	public static IdCountersManager getInstance() {
		return idcountersmanager ;
	}

	public int getUpdatecounter() {
		return updatecounter;
	}

	public  void addUpdatecounter() {
		if(updatecounter < COUNTERLIMIT) {
			updatecounter++;
		}else {
			isUnknownPhase = isUnknownPhase();
//			isUnknownPhase = false;
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
	public void listadd() {
		addUpdatecounter();
	}

	public boolean isUnknownPhase() {
//		LogFile.getInstance().write("hoge1");
		ArrayList<Long> clonelist = new ArrayList<>();
		for(Counter c: EventLogger.INSTANCE.getCounters()) {
			if(c == null) {
				clonelist.add((long) 0);
			}else {
				clonelist.add((long) c.getCount());
				c.setCount(0);
			}
		}

		ArrayList<Double> normalizedclone = new ArrayList<>(normalizeList(clonelist));
		boolean result = true;

		if(knownPhaseList.size() <= 0) {
			knownPhaseList.add(normalizedclone);
			return result;
		}

		if(compareWithKnownPhaseList(knownPhaseList, normalizedclone) > phaseThreshold) {
			result = false;
		}else {
			if(AgentOptions.getInstance().isDebug()) DebugLogFile.getInstance().write("[Unknow Phase Detected]");
			if(continuesIn2Intervals(ps, normalizedclone)) {
				knownPhaseList.add(normalizedclone);
				ps.refresh();
			}
		}
		return result;
	}

	private double compareWithKnownPhaseList(List<ArrayList<Double>> phaseList, ArrayList<Double> list) {
		double max = 0;
		double cossim = 0;
		int index = 0;
		int result_index = 0;


		for(ArrayList<Double> knownphase: phaseList) {
			cossim = calcInnerProduct(knownphase, list);
			if(cossim > max) {
				max = cossim;
				result_index = index;
			}
			index++;
		}
		if(AgentOptions.getInstance().isDebug()) DebugLogFile.getInstance().write("phase num: [" + result_index + "]; cossim: [" + max + "]; num of events: [" + list.size() + "]; total phases: [" + phaseList.size() + "];");
		return max;
	}


	public ArrayList<Double> normalizeList(List<Long> list) {
		ArrayList<Double> clone = new ArrayList<>();
		long normOfList = 0;

		double max1 = 0;

		// Calculate norm of the vector
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i)> max1) max1 = list.get(i);
			normOfList += list.get(i) * list.get(i);
		}
//		if(AgentOptions.getInstance().isDebug()) DebugLogFile.getInstance().write("normOfList: " + normOfList);
		double normOfList2 = Math.sqrt(normOfList);

		double max2 = 0;


		for (int i = 0; i < list.size(); i++) {
			if (normOfList2 != 0) {
				if((list.get(i) / normOfList2) > max2) max2 = (list.get(i) / normOfList2);
				clone.add(list.get(i) / normOfList2);
			}
		}

//		if(AgentOptions.getInstance().isDebug()) DebugLogFile.getInstance().write("listsize: " + list.size() + " max1: " + max1 + " max2: " + max2 + " normOfList2: " + normOfList2);
		return clone;
	}

	public double calcInnerProduct(ArrayList<Double> list1, ArrayList<Double> list2) {
		if(list1 == null || list2 == null) return 0;
		double innerProduct = 0;
		int minlistsize = list1.size();
		if(minlistsize > list2.size()) minlistsize = list2.size();

		for (int i = 0; i < minlistsize; i++) innerProduct += list1.get(i) * list2.get(i);
		return innerProduct;
	}

	/**
	 * Judges if the current phase continues in 2 intervals
	 * @param ps
	 * @param current
	 * @return
	 */
	private boolean continuesIn2Intervals(PrevState ps, ArrayList<Double> current) {
		if (calcInnerProduct(ps.get(), current) > AgentOptions.getInstance().getEp()) {
			ps.incCount();
			ps.update(current);
			if (ps.getCount() >= 2) {
				return true;
			}
			return false;
		} else {
			ps.stayCount();
			ps.update(current);
			return false;
		}
	}

	public static class PrevState {
		private ArrayList<Double> prev;
		private int count;

		public PrevState() {
//			prev = new ArrayList<Double>();
//			refresh();
		}

		/**
		 * 同じフェイズで連続する区間のカウントを1にする
		 */
		public void stayCount() {
			count = 1;
		}

		public void refresh() {
			prev = null;
			count = 0;
		}

		public void update(ArrayList<Double> list) {
			prev = new ArrayList<Double>(list);
		}

		public ArrayList<Double> get() {
			return prev;
		}

		public void incCount() {
			count++;
		}

		public int getCount() {
			return count;
		}
	}
}
