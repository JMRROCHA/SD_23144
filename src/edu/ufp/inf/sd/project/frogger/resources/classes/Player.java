package edu.ufp.inf.sd.project.frogger.resources.classes;

import java.io.Serializable;

public class Player implements Serializable {
    private String username;
    private Integer level;
    private Integer score;
    private Point position;

    public Player(String username) {
        this.username = username;
        this.level = 0;
        this.position = new Point(0, 0);
        this.score = 0;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", level=" + level +
                ", score=" + score +
                ", position=" + position +
                '}';
    }
}
