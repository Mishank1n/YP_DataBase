package controller;

import lombok.AllArgsConstructor;
import models.*;
import storage.TableStorage;
import storage.UserStorage;

import java.util.HashMap;

import static models.LoginDialogExample.getLoginData;

@AllArgsConstructor
public class AdminController {

    private UserStorage userStorage;
    private TableStorage tableStorage;
    public static final  User admin = new User("Admin", "MIREA_Admin_BD");

    public void admin() {
        LoginDialogExample.LoginData data = getLoginData(null);
        if (data!=null && data.username.equals(admin.getLogin()) && data.password.equals(admin.getPassword()) ) {
            System.out.println(Colors.ANSI_RED + "Добрый день правитель БД)!\n" + Colors.ANSI_RESET);
            while (true) {
                System.out.println(Colors.ANSI_RED + "Возможные действия с БД:\n" +
                        "1) Удалить любого пользователя\n" +
                        "2) Удалить любую таблицу\n" +
                        "3) Удалить всех пользователей\n" +
                        "4) Удалить все таблицы\n" +
                        "5) Удалить всю БД полностью\n" +
                        "6) Присвоить права доступа пользователю\n" +
                        "7) Выйти с администрирования\n" +
                        "Введите номер команды: "+Colors.ANSI_WHITE);
                int command = ProgramController.scanner.nextInt();
                switch (command){
                    case 1:
                        deleteUserByAdmin();
                        break;
                    case 2:
                        deleteTableByAdmin();
                        break;
                    case 3:
                        deleteAllUsersByAdmin();
                        break;
                    case 4:
                        deleteAllTablesByAdmin();
                        break;
                    case 5:
                        deleteBD();
                        break;
                    case 6:
                        changeModeOfUser();
                        break;
                    case 7:
                        System.out.println(Colors.ANSI_RED + "Прощайте правитель БД :( \n" + Colors.ANSI_RESET);
                        return;
                    default:
                        System.out.println(Colors.ANSI_RED + "!!! Неверный номер команды, посмотрите внимательнее на список !!!\n");
                        break;
                }
            }
        } else {
            System.out.println(Colors.ANSI_YELLOW + "Введен неправильный логин или пароль!\n" + Colors.ANSI_RESET);
        }
    }

    private void changeModeOfUser(){
        System.out.println(Colors.ANSI_RED + "Введите логин пользователя для которого меняем доступ: " + Colors.ANSI_RESET);
        String login = ProgramController.scanner.next();
        User user = userStorage.getByName(login);
        if (user==null){
            System.out.println(Colors.ANSI_YELLOW + String.format("!!! Пользователь с логином %s не найден !!!\n", login));
            return;
        }
        System.out.println(Colors.ANSI_RED + "Введите имя таблицы для которой меняем доступ: " + Colors.ANSI_RESET);
        String nameOfTable = ProgramController.scanner.next();
        Table table = tableStorage.getByName(nameOfTable);
        if (table == null) {
            System.out.println(Colors.ANSI_YELLOW + String.format("!!! Таблица с именем = %s не найдена !!!\n", nameOfTable));
            return;
        }
        System.out.println(Colors.ANSI_RED + "Введите новый режим доступа из списка [READ, CHANGE, ALL, NONE] " + Colors.ANSI_RESET);
        String mode = ProgramController.scanner.next();
        switch (mode){
            case "READ":
                table.getUsersModes().put(user, Mode.READ);
                break;
            case "CHANGE":
                table.getUsersModes().put(user, Mode.CHANGE);
                break;
            case "ALL":
                table.getUsersModes().put(user, Mode.ALL);
                break;
            case "NONE":
                table.getUsersModes().put(user, Mode.NONE);
                break;
            default:
                System.out.println(Colors.ANSI_YELLOW + "!!! Режим не распознан !!!\n" + Colors.ANSI_RESET);
                break;
        }
    }

    private void deleteUserByAdmin(){
        System.out.println(Colors.ANSI_RED + "Введите логин пользователя которого удаляем: " + Colors.ANSI_RESET);
        String login = ProgramController.scanner.next();
        User user = userStorage.getByName(login);
        if (user==null){
            System.out.println(Colors.ANSI_YELLOW + String.format("!!! Пользователь с логином %s не найден !!!\n", login));
            return;
        }
        userStorage.delete(login);
        tableStorage.getStorage().values().stream().filter(table -> table.getUsersModes().containsKey(user)).forEach(table -> table.getUsersModes().remove(user));
    }

    private void deleteTableByAdmin(){
        System.out.println(Colors.ANSI_RED + "Введите имя таблицы которую удаляем: " + Colors.ANSI_RESET);
        String nameOfTable = ProgramController.scanner.next();
        Table table = tableStorage.getByName(nameOfTable);
        if (table == null) {
            System.out.println(Colors.ANSI_YELLOW + String.format("!!! Таблица с именем = %s не найдена !!!\n", nameOfTable));
            return;
        }
        tableStorage.delete(nameOfTable);
    }

    private void deleteAllUsersByAdmin(){
        userStorage.getStorage().clear();
        tableStorage.getStorage().values().stream().map(Table::getUsersModes).forEach(HashMap::clear);
        System.out.println(Colors.ANSI_RED + "Все пользователи удалены\n");
    }

    private void deleteAllTablesByAdmin(){
        tableStorage.getStorage().clear();
        System.out.println(Colors.ANSI_RED + "Все таблицы были удалены\n");
    }

    private void deleteBD(){
        userStorage.getStorage().clear();
        tableStorage.getStorage().clear();
        System.out.println(Colors.ANSI_RED + "Вся база данных была удалена\n");
    }
}