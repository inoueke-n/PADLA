package padla;

import java.util.ArrayList;
import java.util.List;


public class IdListManagerSub extends Thread{
	private static IdListManagerSub idlistmanagersub;

	private List<ArrayList<Double>> knownPhaseList;
	private int updatecounter = 0;
	private long COUNTERLIMIT = 1000000;
	private double phaseThreshold = 0.5;
	private List<Long> idlist;
	public Connector connector;
	public Config Config = new Config();
	private boolean updated = false;
	public boolean isUnknownPhase = false;

	public synchronized void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public boolean isUpdated() {
		return updated;
	}

	private IdListManagerSub() {
		idlist = new ArrayList<Long>();
		knownPhaseList = new ArrayList<ArrayList<Double>>();
		connector = new Connector();
		phaseThreshold = AgentOptions.getInstance().getEp();
		COUNTERLIMIT = AgentOptions.getInstance().getCounterLimit();
	}


	public static void initialize() {
		idlistmanagersub = new IdListManagerSub();
	}

	public static IdListManagerSub getInstance() {
		return idlistmanagersub ;
	}

	public int getUpdatecounter() {
		return updatecounter;
	}

	public synchronized void addUpdatecounter() {
		if(updatecounter < COUNTERLIMIT) {
			updatecounter++;
		}else {
			isUnknownPhase = isUnknownPhase();
			resetUodatecounter();
		}
	}

	public void resetUodatecounter() {
		updatecounter = 0;
	}


	/**
	 * Increment number the number of "dataId" execution.
	 * @param dataId
	 */
	public synchronized void listadd(int dataId) {
		int idlistsize = idlist.size();
		if(idlist.size() <= dataId) {
			for(int i = 0; i < (dataId - idlistsize + 1); i++) idlist.add((long) 0);
		}
		idlist.set(dataId, idlist.get(dataId) + 1);
	}

	public boolean isUnknownPhase() {
		ArrayList<Long> clonelist = new ArrayList<>(idlist);
		ArrayList<Double> normalizedclone = new ArrayList<>(normalizeList(clonelist));
		//initialize list
		for(int i = 0; i < idlist.size(); i++) idlist.set(i, (long) 0);
		boolean result = true;

		if(knownPhaseList.size() <= 0) {
			knownPhaseList.add(normalizedclone);
			return result;
		}

		if(compareWithKnownPhaseList(knownPhaseList, normalizedclone) > phaseThreshold) {
			result = false;
		}else {
			knownPhaseList.add(normalizedclone);
		}

		return result;
	}

	private double compareWithKnownPhaseList(List<ArrayList<Double>> phaseList, ArrayList<Double> list) {
		double max = 0;
		double cossim = 0;

		for(ArrayList<Double> knownphase: phaseList) {
			cossim = calcInnerProduct(knownphase, list);
			if(cossim > max) max = cossim;
		}
//		DebugLogFile.getInstance().write("cossim: " + max);
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
//		DebugLogFile.getInstance().write("normOfList: " + normOfList);
		double normOfList2 = Math.sqrt(normOfList);

		double max2 = 0;


		for (int i = 0; i < list.size(); i++) {
			if (normOfList2 != 0) {
				if((list.get(i) / normOfList2) > max2) max2 = (list.get(i) / normOfList2);
				clone.add(list.get(i) / normOfList2);
			}
		}

//		DebugLogFile.getInstance().write("listsize: " + list.size() + " max1: " + max1 + " max2: " + max2 + " normOfList2: " + normOfList2);
		return clone;
	}

	public double calcInnerProduct(ArrayList<Double> list1, ArrayList<Double> list2) {
		double innerProduct = 0;
		int minlistsize = list1.size();
		if(minlistsize > list2.size()) minlistsize = list2.size();

		for (int i = 0; i < minlistsize; i++) innerProduct += list1.get(i) * list2.get(i);
		return innerProduct;
	}
}
