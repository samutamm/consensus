package database;

public interface Database {
    public String get(String key);
    public String put(String key, String value);
    public String delete(String key);
}
