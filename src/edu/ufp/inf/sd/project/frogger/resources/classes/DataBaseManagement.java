package edu.ufp.inf.sd.project.frogger.resources.classes;

import java.util.ArrayList;

public class DataBaseManagement {

    private final ArrayList<User> users;

    public DataBaseManagement() {

        users = new ArrayList<>();
        users.add(new User("1", "1", "1"));
        users.add(new User("2", "2", "2"));
        users.add(new User("3", "3", "3"));
    }


    public boolean registerUser(String username, String password, String name) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return false;
            }
        }
        users.add(new User(username, password, name));
        return true;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public boolean isValidUserLogin(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }
}
