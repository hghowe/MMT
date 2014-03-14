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
import java.util.Scanner;
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
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MMTServer theApp = new MMTServer();
        theApp.go();
        //THIS EDIT CAN BE PUSHED
    }
    
    public MMTServer()
    {
        super();
        nextAvailableID = 0;
        Timer t = new Timer();
        t.scheduleAtFixedRate(this,0,2000); // parameters: 0) which TimerTask 
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
                Socket playerSocket = mySocket.accept();
                PrintWriter pw = new PrintWriter(playerSocket.getOutputStream());
                ClientReader cr = new ClientReader(playerSocket, pw);
                MMTServerPlayer player = new MMTServerPlayer(new Point(400, 400), this.nextAvailableID, pw, cr.myName);
                
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
    }
    
    public void run()
    {
        System.out.println("executing run() method");
    }
    
    public void disconnectPlayer(int id)
    {
        
    }
    
    public void broadcast(int messageType, Object[] params)
    {
        String message = getMessageStringFromIntType(messageType);
        
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
                return "";
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
                    
                }
            }
            catch(NoSuchElementException nse)
            {
                disconnectPlayer(myID);
            }
        }
    }
}
