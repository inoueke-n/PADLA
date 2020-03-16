package selogger.logging;

import java.util.LinkedList;

import padla.AgentOptions;
import padla.CacheUpdateCounter;
import padla.IdListManager;
import padla.IdListManagerSub;



public class Logging {


	public static void recordEvent(Object value, int dataId) {
		if(AgentOptions.getInstance().getPhaseDetectionType() == 2) {
			IdListManager.getInstance().listadd(dataId);
//			IdArrayManager.getInstance().listadd(dataId);
		}else if(AgentOptions.getInstance().getPhaseDetectionType() == 3) {
			IdListManagerSub.getInstance().listadd(dataId);
			CacheUpdateCounter.getInstance().lruadd(dataId, System.identityHashCode(value));
			CacheUpdateCounter.getInstance().addUpdatecounter();
		}
		EventLogger.INSTANCE.recordEvent(dataId, value);
	}

	/**
	 * This method is defined for type checking
	 * @param value
	 * @param dataId
	 */
	public static void recordEvent(Throwable value, int dataId) {
		EventLogger.INSTANCE.recordEvent(dataId, value);
	}

	public static void recordEvent(boolean value, int dataId) {
		EventLogger.INSTANCE.recordEvent(dataId, value);
	}

	public static void recordEvent(byte value, int dataId) {
		EventLogger.INSTANCE.recordEvent(dataId, value);
	}

	public static void recordEvent(char value, int dataId) {
		EventLogger.INSTANCE.recordEvent(dataId, value);
	}

	public static void recordEvent(short value, int dataId) {
		EventLogger.INSTANCE.recordEvent(dataId, value);
	}

	public static void recordEvent(int value, int dataId) {
		EventLogger.INSTANCE.recordEvent(dataId, value);
	}

	public static void recordEvent(long value, int dataId) {
		EventLogger.INSTANCE.recordEvent(dataId, value);
	}

	public static void recordEvent(float value, int dataId) {
		EventLogger.INSTANCE.recordEvent(dataId, value);
	}

	public static void recordEvent(double value, int dataId) {
		EventLogger.INSTANCE.recordEvent(dataId, value);
	}

	public static void recordEvent(int dataId) {
		if(AgentOptions.getInstance().getPhaseDetectionType() == 2) {
			IdListManager.getInstance().listadd(dataId);
//			IdArrayManager.getInstance().listadd(dataId);
		}else if(AgentOptions.getInstance().getPhaseDetectionType() == 3) {
			IdListManagerSub.getInstance().listadd(dataId);
			CacheUpdateCounter.getInstance().lruadd(dataId, 0);
			CacheUpdateCounter.getInstance().addUpdatecounter();
		}
		EventLogger.INSTANCE.recordEvent(dataId, 0);
	}

	/**
	 * Method for byte[] and boolean[]
	 */
	public static void recordArrayLoad(Object array, int index, int dataId) {
		EventLogger.INSTANCE.recordEvent(dataId, array);
		EventLogger.INSTANCE.recordEvent(dataId+1, index);
	}

	public static void recordArrayStore(Object array, int index, byte value, int dataId) {
		EventLogger.INSTANCE.recordEvent(dataId, array);
		EventLogger.INSTANCE.recordEvent(dataId+1, index);
		EventLogger.INSTANCE.recordEvent(dataId+2, value);
	}

	public static void recordArrayStore(Object array, int index, char value, int dataId) {
		EventLogger.INSTANCE.recordEvent(dataId, array);
		EventLogger.INSTANCE.recordEvent(dataId+1, index);
		EventLogger.INSTANCE.recordEvent(dataId+2, value);
	}

	public static void recordArrayStore(Object array, int index, double value, int dataId) {
		EventLogger.INSTANCE.recordEvent(dataId, array);
		EventLogger.INSTANCE.recordEvent(dataId+1, index);
		EventLogger.INSTANCE.recordEvent(dataId+2, value);
	}

	public static void recordArrayStore(Object array, int index, float value, int dataId) {
		EventLogger.INSTANCE.recordEvent(dataId, array);
		EventLogger.INSTANCE.recordEvent(dataId+1, index);
		EventLogger.INSTANCE.recordEvent(dataId+2, value);
	}

	public static void recordArrayStore(Object array, int index, int value, int dataId) {
		EventLogger.INSTANCE.recordEvent(dataId, array);
		EventLogger.INSTANCE.recordEvent(dataId+1, index);
		EventLogger.INSTANCE.recordEvent(dataId+2, value);
	}

	public static void recordArrayStore(Object array, int index, long value, int dataId) {
		EventLogger.INSTANCE.recordEvent(dataId, array);
		EventLogger.INSTANCE.recordEvent(dataId+1, index);
		EventLogger.INSTANCE.recordEvent(dataId+2, value);
	}

	public static void recordArrayStore(Object array, int index, short value, int dataId) {
		EventLogger.INSTANCE.recordEvent(dataId, array);
		EventLogger.INSTANCE.recordEvent(dataId+1, index);
		EventLogger.INSTANCE.recordEvent(dataId+2, value);
	}

	public static void recordArrayStore(Object array, int index, Object value, int dataId) {
		EventLogger.INSTANCE.recordEvent(dataId, array);
		EventLogger.INSTANCE.recordEvent(dataId+1, index);
		EventLogger.INSTANCE.recordEvent(dataId+2, value);
	}

	public static void recordMultiNewArray(Object array, int dataId) {
		EventLogger.INSTANCE.recordEvent(dataId, array);
		recordMultiNewArrayContents((Object[])array, dataId);
	}

	private static void recordMultiNewArrayContents(Object[] array, int dataId) {
		LinkedList<Object[]> arrays = new LinkedList<Object[]>();
		arrays.addFirst(array);
		while (!arrays.isEmpty()) {
			Object[] asArray = arrays.removeFirst();
			EventLogger.INSTANCE.recordEvent(dataId+1, asArray);
			for (int index=0; index<asArray.length; ++index) {
				Object element = asArray[index];
				Class<?> elementType = element.getClass();
				if (element != null && elementType.isArray()) {
					EventLogger.INSTANCE.recordEvent(dataId+2, element);
					if (elementType.getComponentType().isArray()) {
						arrays.addLast((Object[])element);
					}
				}
			}
		}
	}
}
