package models;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginDialogExample {

    // Класс для хранения данных логина
    public static class LoginData {
        public final String username;
        public final String password;

        public LoginData(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    // Метод для показа диалога и получения данных
    public static LoginData getLoginData(JFrame parent) {
        JDialog dialog = new JDialog(parent, "Авторизация", true);

        dialog.setLayout(new GridLayout(3, 2));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton okButton = new JButton("OK");
        dialog.getRootPane().setDefaultButton(okButton);

        final LoginData[] result = new LoginData[1]; // Для хранения результата

        okButton.addActionListener((ActionEvent e) -> {
            // Получаем введенные данные
            if (usernameField.getText()!=null && passwordField.getPassword()!=null){
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                result[0] = new LoginData(username, password);
                dialog.dispose();
            } else {
                System.out.println(Colors.ANSI_RED+"!!! Поля не могут быть пустыми !!!"+Colors.ANSI_RESET);
            }
        });

        dialog.add(new JLabel("Логин:"));
        dialog.add(usernameField);
        dialog.add(new JLabel("Пароль:"));
        dialog.add(passwordField);
        dialog.add(new JLabel()); // Пустая ячейка
        dialog.add(okButton);


        if (parent == null) {
            dialog.setLocationRelativeTo(null); // Центрируем относительно экрана
        } else {
            dialog.setLocationRelativeTo(parent); // Центрируем относительно родителя
        }

        dialog.setAlwaysOnTop(true);


        dialog.pack();
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true); // Блокирует выполнение до закрытия диалога

        return result[0]; // Возвращаем результат после закрытия диалога
    }
}