package edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FactoryRI extends Remote {
    boolean register(String username, String password, String name) throws RemoteException;

    SessionRI login(String username, String password) throws RemoteException;
}
