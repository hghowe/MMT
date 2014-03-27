/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mmt;

import java.awt.Point;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author harlan.howe
 */
public class MMTServer extends TimerTask{

    private int nextAvailableID;
    private ServerSocket mySocket;
    private Map<Integer, MMTServerPlayer> players;
    private static MMTServer theApp;
    private int itID = -1;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        theApp = new MMTServer();
        theApp.go();
        //THIS EDIT CAN BE PUSHED
    }
    
    public MMTServer()
    {
        super();
        nextAvailableID = 0;
        Timer t = new Timer();
        t.scheduleAtFixedRate(this,0,20); // parameters: 0) which TimerTask 
                                            // object's "run" method should I  
                                            // call? 1) how many milliseconds  
                                            // until I call it the first time?
                                            // 2) how many milliseconds
                                            // between subsequent calls?
        players = new HashMap<Integer, MMTServerPlayer>();
        
    }
    
    public void setupNetworking()
    {
        try
        {
            mySocket = new ServerSocket(5000);
            while(true)
            {
                System.out.println("Waiting for Client");
                
                Socket playerSocket = mySocket.accept();
                
                PrintWriter pw = new PrintWriter(playerSocket.getOutputStream());
                ClientReader cr = new ClientReader(playerSocket, pw);
                MMTServerPlayer player = new MMTServerPlayer(new Point(new Random().nextInt(800), new Random().nextInt(800)), this.nextAvailableID, pw, cr.myName);
                
                broadcast(0, new Object[]{
                        nextAvailableID, player.getName()
                });
                
                Set<Integer> keys = players.keySet();
                
                if(keys.size() == 0)
                    itID = nextAvailableID;
                
                for(int key : keys)
                {
                    player.sendMessage(this.getMessageStringFromIntType(0)+"\t"+key+"\t"+players.get(key).getName());
                }
                
                player.sendMessage(this.getMessageStringFromIntType(2)+"\t"+itID);
                
                players.put(nextAvailableID, player);
                
                keys = players.keySet();
                for(int key : keys)
                {
                    player.sendMessage(this.getMessageStringFromIntType(1)+"\t"+key+"\t"+player.getXLoc()+"\t"+player.getYLoc());
                }
                
                System.out.println("Client Found");
                
                this.nextAvailableID++;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public void go()
    {
        System.out.println("Starting Program.");
        setupNetworking();
    }
    
    public void run()
    {
        System.out.println("executing run() method");
        Set<Integer> keys = players.keySet();
        int nextItID = itID;
        for(int key : keys)
        {
            players.get(key).move();
            if(key != itID)
                if(players.get(key).collides(players.get(itID)))
                {
                    nextItID = key;
                    players.get(key).setCantMove();
                    players.get(key).warp();
                }
        }
        if(itID != nextItID)
        {
            itID = nextItID;
            broadcast(2, new Object[]{
                itID
            });
        }
    }
    
    public void disconnectPlayer(int id)
    {
        players.remove(id);
        
        broadcast(3, new Object[]{ id });
    }
    
    public void broadcast(int messageType, Object[] params)
    {
        String message = getMessageStringFromIntType(messageType);
        for(Object obj : params)
        {
            message += "\t"+obj;
        }
        Set<Integer> keys = players.keySet();
        for(Integer key : keys)
        {
            players.get(key).sendMessage(message);
        }
    }
    
    private void handleMessage(String message, int playerID)
    {
        String[] messageComponents = message.split("\t");
        
        System.out.println(message + "From "+players.get(playerID).getName());
        
        if(messageComponents[0].equals("KEY"))
        {
            players.get(playerID).setMovement(Integer.parseInt(messageComponents[1]));
        }
        
    }
    
    private String getMessageStringFromIntType(int type)
    {
        switch(type)
        {
            case 0:
                return "NEW_PLAYER";
            case 1:
                return "LOC_UPDATE";
            case 2:
                return "NEW_IT";
            case 3:
                return "REMOVE_PLAYER";
            case 4:
                return "UPDATE_TIME";
            default:
                return "NO_ACTION_ASSIGNED";
        }
    }
    
    private class ClientReader implements Runnable
    {
        private Socket mySocket;
        private PrintWriter myPrintwriter;
        private Scanner myScanner;
        private String myName;
        private int myID;
        
        public ClientReader(Socket s, PrintWriter pw)
        {
            mySocket = s;
            myPrintwriter = pw;
            try
            {
                myScanner = new Scanner(mySocket.getInputStream());
                myName = myScanner.nextLine();
                myID = nextAvailableID;
                myPrintwriter.println(myID);
                myPrintwriter.flush();
                new Thread(this).start();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
        }

        public String getPlayerName()
        {
            return myName;
        }
        
        @Override
        public void run()
        {
            try
            {
                while(true)
                {
                    handleMessage(myScanner.nextLine(), myID);
                }
            }
            catch(NoSuchElementException nse)
            {
                disconnectPlayer(myID);
            }
        }
    }
    
    public static MMTServer getInstance()
    {
        return theApp;
    }
}
