package edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameFactoryRI extends Remote {

    boolean register(String userername, String password, String name) throws RemoteException;

    GameSessionRI login(String username, String password) throws RemoteException;
}
