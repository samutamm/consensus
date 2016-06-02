package database;

import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDB implements Database {

    private ConcurrentHashMap<String, String> db;

    public InMemoryDB() {
        db = new ConcurrentHashMap<String, String>();
        db.put("example", "boloutus\n");
    }

    @Override
    public String get(String key) {
        return db.get(key);
    }

    @Override
    public String put(String key, String value) {
        String put = db.put(key, value);
        if (put == null) return "";
        return put;
    }

    @Override
    public String delete(String key) {
        return db.remove(key);
    }
}
