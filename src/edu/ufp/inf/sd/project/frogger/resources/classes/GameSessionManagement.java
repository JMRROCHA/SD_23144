package edu.ufp.inf.sd.project.frogger.resources.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class GameSessionManagement implements Serializable {

    Date date;
    ArrayList<Player> players = new ArrayList<>();

    public GameSessionManagement() {
        this.date = new Date();
    }

    public Date getDate() {
        return date;
    }

    public void addPlayer(String username) {
        Player player = new Player(username);
        players.add(player);
    }

    public void removePlayer(String username) {
        for (Player player : players) {
            if (player.getUsername().equals(username)) {
                players.remove(player);
            }
        }

    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

}




