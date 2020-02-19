package selogger.logging;

import java.util.ArrayList;

import selogger.logging.io.EventFrequencyLogger.Counter;

public interface IEventLogger {

	public void close();
	public void recordEvent(int dataId, Object value);
	public void recordEvent(int dataId, int value);
	public void recordEvent(int dataId, long value);
	public void recordEvent(int dataId, byte value);
	public void recordEvent(int dataId, short value);
	public void recordEvent(int dataId, char value);
	public void recordEvent(int dataId, boolean value);
	public void recordEvent(int dataId, double value);
	public void recordEvent(int dataId, float value);
	public ArrayList<Counter> getCounters();

}
