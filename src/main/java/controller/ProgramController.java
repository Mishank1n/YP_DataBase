package controller;


import crypto.Aes;
import crypto.KeyFromString;
import models.Colors;
import models.Mode;
import models.Table;
import models.User;
import storage.TableStorage;
import storage.UserStorage;

import javax.crypto.SecretKey;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ProgramController {
    public static final Scanner scanner = new Scanner(System.in);
    private UserController userController;
    private TableController tableController;
    private AdminController adminController;
    private final SecretKey key = KeyFromString.createKeyFromString("Mirea");


    public void start() {
        if (init()) {
            work();
        } else {
            System.out.println(Colors.ANSI_RED + "Программа не смогла проинициализировать данные с файлов!" + Colors.ANSI_RESET);
        }
    }

    public boolean init() {
        boolean flag = true;
        HashMap<String, User> userHashMap = new HashMap<>();
        try (Reader reader = new FileReader("src/main/java/data/Users.txt"); BufferedReader bufferedReader = new BufferedReader(reader)){
            while (bufferedReader.ready()){
                String login = Aes.decrypt(bufferedReader.readLine(), key);
                String password = Aes.decrypt(bufferedReader.readLine(), key);
                userHashMap.put(login, new User(login, password));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            flag = false;
        }
        HashMap<String, Table> tableHashMap = new HashMap<>();
        try (Reader reader = new FileReader("src/main/java/data/Tables.txt"); BufferedReader bufferedReader = new BufferedReader(reader)){
            while (bufferedReader.ready()){
                String file = "src/main/java/data/"+bufferedReader.readLine();
                try (Reader reader1 = new FileReader(file); BufferedReader bufferedReader1 = new BufferedReader(reader1)){
                    String name = bufferedReader1.readLine();
                    String title = Aes.decrypt(bufferedReader1.readLine(), key);
                    ArrayList<String> data = new ArrayList<>();
                    while (bufferedReader1.ready()){
                        data.add(Aes.decrypt(bufferedReader1.readLine(), key));
                    }
                    tableHashMap.put(name, new Table(name, title, data));
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        try (Reader reader = new FileReader("src/main/java/data/Modes.txt"); BufferedReader bufferedReader = new BufferedReader(reader)){
            while (bufferedReader.ready()){
                String line = bufferedReader.readLine();
                if (!line.equals("-") && line.split(" ")[0].equals("*")){
                    Table table = tableHashMap.get(line.split(" ")[1]);
                    table.setUsersModes(new HashMap<>());
                    for (int i = 0; i < userHashMap.size(); i++) {
                        String[] userMode = Aes.decrypt(bufferedReader.readLine(), key).split(" ");
                        Mode mode;
                        if (userMode[1].equals("All")){
                            mode = Mode.ALL;
                        } else if (userMode[1].equals("Read")){
                            mode = Mode.READ;
                        } else if (userMode[1].equals("Change")){
                            mode = Mode.CHANGE;
                        } else {
                            mode = Mode.NONE;
                        }
                        table.getUsersModes().put(userHashMap.get(userMode[0]), mode);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        TableStorage tableStorage = new TableStorage(tableHashMap);
        UserStorage userStorage = new UserStorage(userHashMap);
        tableController = new TableController(tableStorage, userStorage);
        userController = new UserController(userStorage, tableController);
        adminController = new AdminController(userStorage, tableStorage);
        return flag;
    }

    public void work() {
        while (true) {
            System.out.println(Colors.ANSI_BLUE +
                    "Список возможных действий: \n" +
                    "1) Зайти используя логин и пароль\n" +
                    "2) Посмотреть имена всех пользователей\n" +
                    "3) Посмотреть имена всех таблиц\n" +
                    "4) Зарегистрировать пользователя в БД\n" +
                    "5) Удалить себя\n" +
                    "6) Администрирование БД\n" +
                    "7) Выход из БД\n" + Colors.ANSI_GREEN +
                    "Введите номер команды:" + Colors.ANSI_RESET);
            int command = scanner.nextInt();
            switch (command) {
                case 1:
                    userController.start();
                    break;
                case 2:
                    userController.findAll();
                    break;
                case 3:
                    tableController.findAll();
                    break;
                case 4:
                    userController.create();
                    break;
                case 5:
                    userController.delete();
                    break;
                case 6:
                    adminController.admin();
                    break;
                case 7:
                    exitBD();
                    System.out.println(Colors.ANSI_GREEN + "Работа с БД завершена. До свидания!\n"+Colors.ANSI_RESET);
                    return;
                default:
                    System.out.println(Colors.ANSI_RED + "!!! Неверный номер команды, посмотрите внимательнее на список !!!\n"+Colors.ANSI_RESET);
                    break;
            }
        }
    }

    private void exitBD() {
        try (Writer writer = new FileWriter("src/main/java/data/Users.txt")){
            for(User user : userController.getStorage().getStorage().values()){
                writer.write(Aes.encrypt(user.getLogin(),key) + "\n");
                writer.write(Aes.encrypt(user.getPassword(), key) + "\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        try (Writer writer = new FileWriter("src/main/java/data/Tables.txt")){
            for (Table table : tableController.getStorage().getStorage().values()){
                writer.write(table.getName()+".txt\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        for (Table table : tableController.getStorage().getStorage().values()){
            try (Writer writer = new FileWriter("src/main/java/data/"+table.getName()+".txt")){
                writer.write(table.getName()+"\n");
                writer.write(Aes.encrypt(table.getTitle(), key)+"\n");
                for (String data : table.getData()) {
                    writer.write(Aes.encrypt(data, key)+"\n");
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        try (Writer writer = new FileWriter("src/main/java/data/Modes.txt")){
            for (Table table : tableController.getStorage().getStorage().values()) {
                writer.write("* "+table.getName()+"\n");
                for (Map.Entry<User, Mode> userMode : table.getUsersModes().entrySet()) {
                    String string = userMode.getKey().getLogin()+" ";
                    if (userMode.getValue().equals(Mode.READ)){
                        string+="Read";
                    } else if (userMode.getValue().equals(Mode.CHANGE)){
                        string+="Change";
                    } else if (userMode.getValue().equals(Mode.ALL)){
                        string+="All";
                    } else {
                        string+="None";
                    }
                    writer.write(Aes.encrypt(string, key)+"\n");
                }
                writer.write("-\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}