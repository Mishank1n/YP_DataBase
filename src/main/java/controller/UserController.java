package controller;


import lombok.AllArgsConstructor;
import lombok.Data;
import models.*;
import storage.UserStorage;

@Data
@AllArgsConstructor
public class UserController {
    private final UserStorage storage;
    private final TableController tableController;

    public void create(){
        LoginDialogExample.LoginData loginData = LoginDialogExample.getLoginData(null);
        if (loginData.username == null || loginData.username.split(" ").length>1){
            System.out.println(Colors.ANSI_RED + "!!! Логин не может быть пустым и не может содержать пробелы!!!\n" );
            return;
        }
        if (loginData.password == null){
            System.out.println(Colors.ANSI_RED + "!!! Пароль не может быть пустым !!!\n" );
            return;
        }
        User user = new User(loginData.username, loginData.password);
        storage.add(user);
        tableController.getStorage().getStorage().values().forEach(table -> table.getUsersModes().put(user, Mode.NONE));
    }

    public void delete() {
        LoginDialogExample.LoginData loginData = LoginDialogExample.getLoginData(null);
        User user = storage.getByName(loginData.username);
        if (initialization(loginData)){
            storage.delete(user.getLogin());
            tableController.deleteUserFromTables(user);
        }
    }

    public void start(){
        LoginDialogExample.LoginData loginData = LoginDialogExample.getLoginData(null);
        if (initialization(loginData)){
            User user = storage.getByName(loginData.username);
            System.out.println(Colors.ANSI_BLUE+"Здравствуйте " + user.getLogin()+"!");
            while (true){
                System.out.println(Colors.ANSI_BLUE +
                        "Ваши возможные операции:\n" +
                        "1) Посмотреть таблицу по имени\n" +
                        "2) Добавить в таблицу строку\n" +
                        "3) Создать таблицу\n" +
                        "4) Удалить таблицу\n" +
                        "5) Посмотреть мои доступы к таблицам\n" +
                        "6) Выйти\n" + Colors.ANSI_GREEN +
                        "Введите номер команды: " + Colors.ANSI_RESET);
                int command = ProgramController.scanner.nextInt();
                switch (command){
                    case 1:
                        tableController.watchTable(user);
                        ProgramController.exitBD();
                        break;
                    case 2:
                        tableController.addDataToTable(user);
                        ProgramController.exitBD();
                        break;
                    case 3:
                        tableController.createTable(user);
                        ProgramController.exitBD();
                        break;
                    case 4:
                        tableController.deleteTable(user);
                        ProgramController.exitBD();
                        break;
                    case 5:
                        view(user);
                        break;
                    case 6:
                        System.out.println(Colors.ANSI_GREEN + "До свидания!\n");
                        return;
                    default:
                        System.out.println(Colors.ANSI_RED + "!!! Неверный номер команды, посмотрите внимательнее на список !!!\n");
                        break;
                }
            }
        }
    }

    private void view(User user) {
        for (Table table : tableController.getStorage().getStorage().values()) {
            System.out.println(Colors.ANSI_GREEN+table.getName()+" "+table.getUsersModes().get(user) + "\n");
        }
    }

    private boolean initialization(LoginDialogExample.LoginData loginData){
        if (loginData.username == null){
            System.out.println(Colors.ANSI_RED + "!!! Логин не может быть пустым !!!\n" );
            return false;
        } else if (loginData.password == null){
            System.out.println(Colors.ANSI_RED + "!!! Пароль не может быть пустым !!!\n" );
            return false;
        } else if (storage.getByName(loginData.username) == null ){
            System.out.println(Colors.ANSI_RED + String.format("!!! Пользователь с логином %s не найден !!!\n", loginData.username));
            return false;
        } else if (!storage.getByName(loginData.username).getPassword().equals(loginData.password)){
            System.out.println(Colors.ANSI_RED + "!!! Неверный пароль !!!\n");
            return false;
        } else{
            return true;
        }
    }

    public void findAll(){
        storage.getStorage().keySet().forEach(System.out::println);
    }
}