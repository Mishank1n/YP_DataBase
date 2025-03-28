package models;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.HashMap;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Table {
    String name;
    String title;
    ArrayList<String> data;
    HashMap<User, Mode> usersModes;

    public Table(String name, String title, ArrayList<String> data) {
        this.name = name;
        this.title = title;
        this.data = data;
    }

    public Table() {
    }
}
