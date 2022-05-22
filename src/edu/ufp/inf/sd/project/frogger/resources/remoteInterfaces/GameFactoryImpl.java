package edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces;

import edu.ufp.inf.sd.project.frogger.resources.classes.DataBase;
import edu.ufp.inf.sd.project.frogger.server.GameServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class GameFactoryImpl extends UnicastRemoteObject implements GameFactoryRI {

    public static HashMap<String, GameSessionRI> sessions = new HashMap<>();
    public static DataBase bd = new DataBase();

    protected GameFactoryImpl() throws RemoteException {
        super();
    }

    @Override
    public GameSessionRI login(String username, String password) {

        GameSessionImpl session = null;

        if (bd.isValidUserLogin(username, password)) {
            if (sessions.get(username) != null) {
                session = (GameSessionImpl) sessions.get(username);
            } else {
                try {
                    session = new GameSessionImpl();
                    sessions.put(username, session);
                    GameServer.updateUserSessionsTable();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        return session;

    }

    @Override
    public boolean register(String username, String password, String name) {
        if (bd.registerUser(username, password, name)) {
            GameServer.updateUsersTable();
            return true;
        }
        return false;
    }
}
