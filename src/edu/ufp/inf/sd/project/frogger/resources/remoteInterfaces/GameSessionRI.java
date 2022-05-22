package edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameSessionRI extends Remote {
    void logout() throws RemoteException;

    void searchGameSessions() throws RemoteException;

    void attachToGameSession() throws RemoteException;

    void dettachFromGameSession() throws RemoteException;
}
