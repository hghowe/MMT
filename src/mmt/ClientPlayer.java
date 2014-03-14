/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mmt;

/**
 *
 * @author harlan.howe
 */
public class ClientPlayer {
    private int x,y,id;
    private String name;

    public ClientPlayer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ClientPlayer{" + "x=" + x + ", y=" + y + ", id=" + id + ", name=" + name + '}';
    }

    
}
