package padla;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    protected int limit = 1000;

    public LRUCache(int limit) {
        super(16, 0.75F, true);
        this.limit = limit;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    	boolean sizeexceedlimit = (size() > limit);
    	//Count up if new key is added
    	if (sizeexceedlimit) {
    		CacheUpdateCounter.getInstance().addUpdatecounter();
    	}
        return sizeexceedlimit;
    }
}