package edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces;

import edu.ufp.inf.sd.project.frogger.resources.classes.GameSessionManagement;
import edu.ufp.inf.sd.project.frogger.server.Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.HashMap;

public class SessionImpl extends UnicastRemoteObject implements SessionRI {

    HashMap<String, SessionRI> sessions = FactoryImpl.sessions;
    HashMap<String, GameSessionManagement> gameSessions = FactoryImpl.gameSessions;
    private String token = null;
    private String username = null;
    private Date date = null;

    public SessionImpl() throws RemoteException {
        super();
        Server.updateTables();
    }

    @Override
    public Date getDate() throws RemoteException {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String getToken() throws RemoteException {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String getUsername() throws RemoteException {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void logout() throws RemoteException {
        sessions.forEach(
                (key, value) -> {
                    if (value == this) {
                        sessions.remove(key);
                        Server.updateTables();
                    }
                }
        );

    }

    @Override
    public HashMap<String, GameSessionManagement> listGameSessions() throws RemoteException {
        return gameSessions;
    }

    @Override
    public void attachToGameSession(String username, String gameSessionQueue) throws RemoteException {
        GameSessionManagement gameSession = gameSessions.get(gameSessionQueue);
        gameSession.addPlayer(username);

        Server.updateTables();
    }

    @Override
    public void detachFromGameSession(String username, String gameSessionQueue) throws RemoteException {
        GameSessionManagement gameSession = gameSessions.get(gameSessionQueue);
        gameSession.removePlayer(username);

        Server.updateTables();
    }

    @Override
    public void newGameSession(String username) throws RemoteException {
        GameSessionManagement gameSession = new GameSessionManagement();
        gameSession.addPlayer(username);
        gameSessions.put(username, gameSession);

        Server.updateTables();
        Server.newExchangeRoom(username);
    }
}
