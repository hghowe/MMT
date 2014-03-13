/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mmt;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author harlan.howe
 */
public class MMTServer extends TimerTask{

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
        Timer t = new Timer();
        t.scheduleAtFixedRate(this,0,2000); // parameters: 0) which TimerTask 
                                            // object's "run" method should I  
                                            // call? 1) how many milliseconds  
                                            // until I call it the first time?
                                            // 2) how many milliseconds
                                            // between subsequent calls?
        
    }
    
    public void go()
    {
        System.out.println("Starting Program.");
    }
    
    public void run()
    {
        System.out.println("executing run() method");
    }
}
