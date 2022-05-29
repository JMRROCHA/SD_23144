package edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import edu.ufp.inf.sd.project.frogger.resources.classes.DataBaseManagement;
import edu.ufp.inf.sd.project.frogger.resources.classes.GameSessionManagement;
import edu.ufp.inf.sd.project.frogger.server.Server;
import org.json.JSONArray;
import org.json.JSONObject;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
        //Algorithm algorithm = Algorithm.HMAC256(username+password);

        if (bd.isValidUserLogin(username, password)) {
            if (sessions.get(username) != null) {
                Server.updateTables();
                return sessions.get(username);
            } else {
                try {

                    Algorithm algorithm = Algorithm.HMAC256("username"+"password");
                    String token = JWT.create()
                            .withIssuer("auth0")
                            .sign(algorithm);

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
        return session;

    }

    @Override
    public boolean register(String username, String password, String name) {
        if (bd.registerUser(username, password, name)) {
            Server.updateTables();
            return true;
        }
        return false;
    }


}
