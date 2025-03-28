package storage;

import java.util.HashMap;
import java.util.List;

public interface Storage<T> {
    public void add(T t);
    public T getByName(String name);
    public void delete(String name);
    public HashMap<String, T> getStorage();
}
