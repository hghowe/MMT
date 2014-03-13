/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mmt;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
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
        
    }
    
    public void setupNetworking()
    {
        
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
