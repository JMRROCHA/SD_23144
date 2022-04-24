package server.classes;

import java.util.ArrayList;

public class DataBase {

    private final ArrayList<User> users;
    private final ArrayList<UserSession> userSessions;
    private final ArrayList<GameSession> gameSessions;


    public DataBase(ArrayList<User> users, ArrayList<UserSession> userSessions, ArrayList<GameSession> gameSessions) {
        this.users = users;
        this.userSessions = userSessions;
        this.gameSessions = gameSessions;
    }

    public boolean registerUser(){
        return false;
    }

    public boolean loginUser(){
        return false;
    }

    public void logoutUser(){

    }

    public boolean attachUserToGameSession(){
        return false;
    }

    public boolean detachUserFromSession(){
        return false;
    }

    public void newGameSession(){

    }


}
