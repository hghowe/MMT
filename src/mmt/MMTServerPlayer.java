package mmt;

import java.awt.Point;
import java.io.PrintWriter;
import java.util.Random;


public class MMTServerPlayer {
    
    private Point myLoc;
    private int myID;
    public int movementType;
    private PrintWriter myWriter;
    private String myName;
    private int paralysisTimer = 0;
    
    public MMTServerPlayer(Point location, int ID, PrintWriter pw, String name)
    {
        this.myLoc = location;
        this.myID = ID;
        this.myWriter = pw;
        this.myName = name;
        movementType = 0;
        MMTServer.getInstance().broadcast(0, new Object[]{
                myID, myName
        });
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
        if(paralysisTimer > 0)
        {
            paralysisTimer--;
            return;
        }
        boolean update = false;
        if((movementType & 1) == 1)
        {
            if(myLoc.y-2 >= 0)
            {
                myLoc.y-=2;
                update = true;
            }
        }
        else if((movementType & 4) == 4)
            if(myLoc.y+12 < 800)
            {
                myLoc.y+=2;
                update = true;
            }
        if((movementType & 2) == 2)
        {
            if(myLoc.x+12 < 800)
            {
                myLoc.x+=2;
                update = true;
            }
        }
        else if((movementType & 8) == 8)
            if(myLoc.x-2 >= 0)
            {
                myLoc.x-=2;
                update = true;
            }
        if(update)
            MMTServer.getInstance().broadcast(1, new Object[]{
                myID, myLoc.x, myLoc.y
            });
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
    
    public String getName()
    {
        return myName;
    }
    
    public void sendMessage(String message)
    {
        myWriter.println(message);
        myWriter.flush();
    }

    public boolean collides(MMTServerPlayer mmtServerPlayer)
    {
        if(mmtServerPlayer == null)
            return false;
        if((mmtServerPlayer.getXLoc() >= myLoc.x && mmtServerPlayer.getXLoc() <= myLoc.x + 10) || (mmtServerPlayer.getXLoc()+10 >= myLoc.x && mmtServerPlayer.getXLoc()+10 <= myLoc.x + 10))
            if((mmtServerPlayer.getYLoc() >= myLoc.y && mmtServerPlayer.getYLoc() <= myLoc.y + 10) || (mmtServerPlayer.getYLoc()+10 >= myLoc.y && mmtServerPlayer.getYLoc()+10 <= myLoc.y + 10))
                return true;
        return false;
    }

    public void setCantMove()
    {
        paralysisTimer = 10;
    }

    public void warp()
    {
        myLoc = new Point(new Random().nextInt(800), new Random().nextInt(800));
        MMTServer.getInstance().broadcast(1, new Object[]{
                myID, myLoc.x, myLoc.y
            });
    }
}
