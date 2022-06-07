package edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import edu.ufp.inf.sd.project.frogger.resources.classes.DataBaseManagement;
import edu.ufp.inf.sd.project.frogger.resources.classes.GameSessionManagement;
import edu.ufp.inf.sd.project.frogger.server.Log;
import edu.ufp.inf.sd.project.frogger.server.Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.HashMap;

public class FactoryImpl extends UnicastRemoteObject implements FactoryRI {

    public static HashMap<String, SessionRI> sessions = new HashMap<>();
    public static HashMap<String, GameSessionManagement> gameSessions = new HashMap<>();
    public static DataBaseManagement bd = new DataBaseManagement();

    protected FactoryImpl() throws RemoteException {
        super();
        Server.updateTables();
    }

    @Override
    public SessionRI login(String username, String password) {
        SessionImpl session = null;

        if (bd.isValidUserLogin(username, password)) {
            if (sessions.get(username) != null) {
                Server.updateTables();
                return sessions.get(username);
            } else {
                try {
                    String token = null;

                    try {
                        Algorithm algorithm = Algorithm.HMAC256(username + password);
                        token = JWT.create()
                                .withIssuer("auth0")
                                .sign(algorithm);
                    } catch (JWTCreationException exception) {
                        //Invalid Signing configuration / Couldn't convert Claims.
                    }

                    session = new SessionImpl();
                    session.setToken(token);
                    session.setUsername(username);
                    session.setDate(new Date());
                    sessions.put(username, session);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        Server.updateTables();
        Log.write(FactoryImpl.class.getSimpleName(), "User LogIn:" + username);
        return session;
    }

    @Override
    public boolean register(String username, String password, String name) {
        if (bd.registerUser(username, password, name)) {
            Server.updateTables();
            Log.write(FactoryImpl.class.getSimpleName(), "User Register:" + username);
            return true;
        }
        return false;
    }


}
