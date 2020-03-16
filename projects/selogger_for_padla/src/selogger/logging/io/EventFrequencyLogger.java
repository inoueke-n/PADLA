package selogger.logging.io;

import java.io.File;
import java.util.ArrayList;

import padla.IdCountersManager;
import selogger.logging.IEventLogger;

/**
 * A logger to record only the numbers of each data items without data contents.
 */
public class EventFrequencyLogger implements IEventLogger {

	public static class Counter {
		private int count = 0;
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		public void increment() {
			count++;
		}
	}

	private ArrayList<Counter> counters;
	@Override
	public ArrayList<Counter> getCounters() {
		return counters;
	}

	private File outputDir;

	public EventFrequencyLogger(File outputDir) {
		this.outputDir = outputDir;
		counters = new ArrayList<>();
	}

	@Override
	public void recordEvent(int dataId, boolean value) {
		record(dataId);
	}

	@Override
	public void recordEvent(int dataId, byte value) {
		record(dataId);
	}

	@Override
	public void recordEvent(int dataId, char value) {
		record(dataId);
	}

	@Override
	public void recordEvent(int dataId, double value) {
		record(dataId);
	}

	@Override
	public void recordEvent(int dataId, float value) {
		record(dataId);
	}

	@Override
	public void recordEvent(int dataId, int value) {
		record(dataId);
	}

	@Override
	public void recordEvent(int dataId, long value) {
		record(dataId);
	}

	@Override
	public void recordEvent(int dataId, Object value) {
		record(dataId);
	}

	@Override
	public void recordEvent(int dataId, short value) {
		record(dataId);
	}

	private synchronized void record(int dataId) {
		while (counters.size() <= dataId) {
			counters.add(null);
		}
		Counter c = counters.get(dataId);
		if (c == null) {
			c = new Counter();
			counters.set(dataId, c);
		}
		c.increment();
		IdCountersManager.getInstance().addUpdatecounter();
	}

	@Override
	public synchronized void close() {
//		try (PrintWriter w = new PrintWriter(new FileWriter(new File(outputDir, "eventfreq.txt")))) {
//			for (int i=0; i<counters.size(); i++) {
//				Counter c = counters.get(i);
//				int count = c != null? c.count: 0;
//				w.println(i + "," + count);
//			}
//		} catch (IOException e) {
//		}
	}


}
