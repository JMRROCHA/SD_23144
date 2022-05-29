package edu.ufp.inf.sd.project.frogger.resources.classes;

import java.io.Serializable;

public class Point implements Serializable {
    private Integer x;
    private Integer Y;

    public Point(Integer x, Integer y) {
        this.x = x;
        this.Y = y;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return Y;
    }

    public void setY(Integer y) {
        Y = y;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", Y=" + Y +
                '}';
    }
}
