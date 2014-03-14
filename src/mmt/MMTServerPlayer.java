package mmt;

import java.awt.Point;
import java.io.PrintWriter;


public class MMTServerPlayer {
    
    private Point myLoc;
    private int myID;
    public int movementType;
    private PrintWriter myWriter;
    private String myName;
    
    public MMTServerPlayer(Point location, int ID, PrintWriter pw, String name)
    {
        this.myLoc = location;
        this.myID = ID;
        this.myWriter = pw;
        this.myName = name;
        movementType = 0;
    }
    
    public void setMovement(int movementAllowances)
    {
        this.movementType = movementAllowances;
    }
    
    /**
     * A method that handles the processing of movementType to its binary components and adding onto the location of the player
     *      Note: The character will go up if both NORTH and SOUTH components are active. 
     *            Similarly it will go right if both EAST and WEST are active.
     */
    public void move()
    {
        if((movementType & 1) == 1)
            if(myLoc.y-2 >= 0)
                myLoc.y-=2;
        else if((movementType & 4) == 4)
            if(myLoc.y+12 < 800)
                myLoc.y+=2;
        if((movementType & 2) == 2)
            if(myLoc.x+12 < 800)
                myLoc.x+=2;
        else if((movementType & 8) == 8)
            if(myLoc.x-2 >= 0)
                myLoc.x-=2;
    }
    
    public int getXLoc()
    {
        return myLoc.x;
    }
    
    public int getYLoc()
    {
        return myLoc.y;
    }
    
    public int getID()
    {
        return myID;
    }
    
    public void sendMessage(String message)
    {
        myWriter.println(message);
        myWriter.flush();
    }
}
