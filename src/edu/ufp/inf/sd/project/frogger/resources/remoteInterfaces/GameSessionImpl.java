package edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces;

import edu.ufp.inf.sd.project.frogger.server.GameServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class GameSessionImpl extends UnicastRemoteObject implements GameSessionRI {

    HashMap<String, GameSessionRI> sessions = GameFactoryImpl.sessions;
    String token = null;

    public GameSessionImpl() throws RemoteException {
        super();
    }

    @Override
    public void logout() throws RemoteException {
        sessions.forEach(
                (key, value) -> {
                    if (value == this) {
                        sessions.remove(key);
                        GameServer.updateUserSessionsTable();
                    }
                }
        );
    }

    @Override
    public void searchGameSessions() throws RemoteException {

    }

    @Override
    public void attachToGameSession() throws RemoteException {

    }

    @Override
    public void dettachFromGameSession() throws RemoteException {

    }
}
