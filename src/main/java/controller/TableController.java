package controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.Colors;
import models.Mode;
import models.Table;
import models.User;
import storage.TableStorage;
import storage.UserStorage;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class TableController {
    private final TableStorage storage;
    private final UserStorage userStorage;

    public void findAll(){
        storage.getStorage().keySet().forEach(System.out::println);
    }

    public void watchTable(User user){
        System.out.println(Colors.ANSI_GREEN + "Введите имя таблицы:");
        String name = ProgramController.scanner.next();
        Table table = storage.getByName(name);
        if (table==null){
            System.out.println(Colors.ANSI_RED + String.format("!!! Таблица с именем = %s не найдена !!!\n", name));
        } else if (table.getUsersModes().get(user).equals(Mode.NONE) || table.getUsersModes().get(user).equals(Mode.CHANGE)){
            System.out.println(Colors.ANSI_RED + "!!! Нет требуемых прав доступа !!!\n");
        } else {
            System.out.println(Colors.ANSI_WHITE + table.getName());
            System.out.println(table.getTitle());
            if (table.getData()!=null){
                table.getData().forEach(System.out::println);
            }
        }
    }

    public void addDataToTable(User user){
        System.out.println(Colors.ANSI_GREEN + "Введите имя таблицы:" + Colors.ANSI_RESET);
        String name = ProgramController.scanner.next();
        Table table = storage.getByName(name);
        if (table==null){
            System.out.println(Colors.ANSI_RED + String.format("!!! Таблица с именем = %s не найдена !!!\n", name));
        } else if (table.getUsersModes().get(user).equals(Mode.NONE) || table.getUsersModes().get(user).equals(Mode.READ)){
            System.out.println(Colors.ANSI_RED + "!!! Нет требуемых прав доступа !!!\n");
        } else {
            System.out.println(Colors.ANSI_GREEN + "Введите данные согласно данному порядку: " + table.getTitle() + Colors.ANSI_RESET);
            String data = "";
            for (int i = 0; i < table.getTitle().split(" ").length;  i++) {
                data+=ProgramController.scanner.next()+" ";
            }
            table.getData().add(data);
            System.out.println(Colors.ANSI_GREEN + "Данные успешно добавлены\n");
        }
    }

    public void createTable(User user) {
        System.out.println(Colors.ANSI_GREEN + "Напишите имя для таблицы: " + Colors.ANSI_RESET);
        String name = ProgramController.scanner.next();
        System.out.println(Colors.ANSI_GREEN + "Вводите категории данных каждую на новой строке, ввод оканчивается вводом символа *: " + Colors.ANSI_RESET);
        String title = "";
        String line = ProgramController.scanner.next();
        while (!line.equals("*")){
            title+=line+" - ";
            line = ProgramController.scanner.next();
        }
        Table table = new Table();
        table.setName(name);
        table.setTitle(title.substring(0,title.length()-2));
        table.setUsersModes(new HashMap<>());
        table.setData(new ArrayList<>());
        userStorage.getStorage().values().forEach(use ->table.getUsersModes().put(use, Mode.NONE));
        table.getUsersModes().put(AdminController.admin, Mode.ALL);
        table.getUsersModes().put(user, Mode.ALL);
        storage.add(table);
    }

    public void deleteTable(User user){
        System.out.println(Colors.ANSI_GREEN + "Введите имя таблицы: " + Colors.ANSI_RESET);
        String name = ProgramController.scanner.next();
        Table table = storage.getByName(name);
        if (table==null){
            System.out.println(Colors.ANSI_RED + String.format("!!! Таблица с именем = %s не найдена !!!\n", name));
        } else if (table.getUsersModes().get(user).equals(Mode.NONE) || table.getUsersModes().get(user).equals(Mode.READ)){
            System.out.println(Colors.ANSI_RED + "!!! Нет требуемых прав доступа !!!\n");
        } else {
            storage.delete(name);
            System.out.println(Colors.ANSI_GREEN+ String.format("Таблица с именем %s была удалена\n", name));
        }
    }

    public void deleteUserFromTables(User user) {
        storage.getStorage().values().stream().filter(table -> table.getUsersModes().containsKey(user)).forEach(table -> table.getUsersModes().remove(user));
    }
}