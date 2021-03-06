package kjk.hiddenmagic.common;

import java.util.HashMap;
import java.util.function.Supplier;

public class DefaultMap<K, T> extends HashMap<K, T> {

    private Supplier<T> getDefault;

    public DefaultMap(Supplier<T> getDefault) {
        super();
        this.getDefault = getDefault;
    }

    public T getOrCreate(Object key) {
        try {
            if (!containsKey(key))
                put((K) key, getDefault.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return get(key);
    }
}
