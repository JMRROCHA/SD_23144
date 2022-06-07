package edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces;

import edu.ufp.inf.sd.project.frogger.resources.classes.GameSessionManagement;
import edu.ufp.inf.sd.project.frogger.resources.classes.Player;
import edu.ufp.inf.sd.project.frogger.server.Log;
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
                        Log.write(SessionImpl.class.getSimpleName(), "User Logout:" + username);
                        Server.updateTables();
                    }
                }
        );
    }

    @Override
    public HashMap<String, GameSessionManagement> listGameSessions() throws RemoteException {
        Log.write(SessionImpl.class.getSimpleName(), "User listGameSessions(): " + username);
        return gameSessions;
    }

    @Override
    public void attachToGameSession(String roomName) throws RemoteException {
        removeUserInGameSessions(this.username);
        GameSessionManagement gameSession = gameSessions.get(roomName);
        gameSession.addPlayer(this.username);
        Log.write(SessionImpl.class.getSimpleName(), "User attachToGameSession(): " + roomName);
        Server.updateTables();
    }

    @Override
    public void detachFromGameSession(String roomName) throws RemoteException {
        GameSessionManagement gameSession = gameSessions.get(roomName);
        gameSession.removePlayer(this.username);

        if (gameSession.getPlayers().size() < 1) {
            gameSessions.remove(roomName);
        }

        Log.write(SessionImpl.class.getSimpleName(), "User detachFromGameSession(): " + roomName);
        Server.updateTables();
    }

    @Override
    public void newGameSession() throws RemoteException {
        GameSessionManagement gameSession = new GameSessionManagement();
        gameSessions.put(this.username, gameSession);
        attachToGameSession(this.username);

        Log.write(SessionImpl.class.getSimpleName(), "User newGameSession(): " + this.username);
        Server.consumeRMQ(this.username);
    }

    private void removeUserInGameSessions(String username) {
        gameSessions.forEach(
                (key, value) -> {
                    for (Player player : value.getPlayers()) {
                        if (player.getUsername().equals(username)) {
                            try {
                                detachFromGameSession(key);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );

    }


}
