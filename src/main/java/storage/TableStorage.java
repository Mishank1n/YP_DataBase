package storage;

import lombok.AllArgsConstructor;
import models.Colors;
import models.Table;

import java.io.File;
import java.util.HashMap;

@AllArgsConstructor
public class TableStorage implements Storage<Table> {
    private HashMap<String, Table> storage;

    @Override
    public void add(Table table) {
        if (storage.containsKey(table.getName())) {
            System.out.printf(Colors.ANSI_RED + "!!! Таблица с именем %s уже есть в базе данных !!!\n", table.getName());
        } else {
            storage.put(table.getName(), table);
            System.out.printf(Colors.ANSI_GREEN + "Таблица с именем %s была добавлена\n", table.getName());
        }
    }

    @Override
    public Table getByName(String name) {
        return storage.getOrDefault(name, null);
    }

    @Override
    public void delete(String name) {
        if (storage.containsKey(name)) {
            storage.remove(name);
            new File("src/main/java/data/"+name+".txt").delete();
            System.out.printf(Colors.ANSI_GREEN + "Таблица с именем %s была удалена\n", name);
        } else {
            System.out.printf(Colors.ANSI_RED + "!!! Таблица с именем %s нет в базе данных !!!\n", name);
        }
    }

    @Override
    public HashMap<String, Table> getStorage() {
        return storage;
    }
}
