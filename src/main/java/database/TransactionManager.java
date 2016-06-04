package database;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Wrapper class for ConcurrentHashMap to log the adds and removes.
 */
public class TransactionManager extends ConcurrentHashMap<String, Function<Database, String>> {
    @Override
    public Function put(String key, Function value) {
        System.out.println("ADDDING TRANSACTION: " + key);
        return super.put(key, value);
    }

    @Override
    public Function remove(Object key) {
        System.out.println("REMOVING TRANSACTION: " + key);
        return super.remove(key);
    }

    @Override
    public Function get(Object key) {
        System.out.println("GET AND COMMIT TRANSACTION: " + key);
        return super.get(key);
    }
}
