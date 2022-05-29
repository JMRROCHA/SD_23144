package edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces;

import edu.ufp.inf.sd.project.frogger.resources.classes.GameSessionManagement;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;

public interface SessionRI extends Remote {

    void logout() throws RemoteException;

    HashMap<String, GameSessionManagement> listGameSessions() throws RemoteException;

    void attachToGameSession(String roomName) throws RemoteException;

    void detachFromGameSession(String roomName) throws RemoteException;

    void newGameSession() throws RemoteException;

    String getUsername() throws RemoteException;

    String getToken() throws RemoteException;

    Date getDate() throws RemoteException;
}
