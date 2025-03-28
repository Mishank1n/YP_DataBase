package storage;


import lombok.AllArgsConstructor;
import models.Colors;
import models.User;

import java.util.HashMap;

@AllArgsConstructor
public class UserStorage implements Storage<User> {
    private HashMap<String, User> storage;

    @Override
    public void add(User user) {
        if (storage.containsKey(user.getLogin())){
            System.out.printf(Colors.ANSI_RED + "!!! Пользователь с логином %s уже есть в базе данных !!!\n", user.getLogin());
        } else {
            storage.put(user.getLogin(), user);
            System.out.printf(Colors.ANSI_GREEN + "Пользователь с логином %s был добавлен\n", user.getLogin());
        }
    }

    @Override
    public User getByName(String login) {
        return storage.getOrDefault(login, null);
    }

    @Override
    public void delete(String login) {
        if (storage.containsKey(login)){
            storage.remove(login);
            System.out.printf(Colors.ANSI_GREEN+"Пользователь с логином %s был удален\n", login);
        } else {
            System.out.printf(Colors.ANSI_RED + "!!! Пользователя с именем %s нет в базе данных !!!\n", login);
        }
    }

    @Override
    public HashMap<String, User> getStorage() {
        return storage;
    }
}
